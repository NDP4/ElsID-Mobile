package com.mobile2.uts_elsid.ui.checkout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.mobile2.uts_elsid.adapter.CartAdapter;
import com.mobile2.uts_elsid.databinding.FragmentCheckoutBinding;
import com.mobile2.uts_elsid.model.Cart;
import com.mobile2.uts_elsid.utils.CartManager;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CheckoutFragment extends Fragment {

    private FragmentCheckoutBinding binding;
    private CartAdapter cartAdapter;
    private CartManager cartManager;
    private List<Cart> cartItems;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCheckoutBinding.inflate(inflater, container, false);

        // Initialize CartManager and cart items list
        cartManager = new CartManager(requireContext());
        cartItems = new ArrayList<>();

        // Setup RecyclerView
        binding.cartRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        cartAdapter = new CartAdapter(
                requireContext(),
                cartItems,
                new CartAdapter.CartClickListener() {
                    @Override
                    public void onQuantityChanged(int position, int newQuantity) {
                        Cart item = cartItems.get(position);
                        item.setQuantity(newQuantity);
                        updateCart();
                    }

                    @Override
                    public void onRemoveItem(int position) {
                        cartItems.remove(position);
                        cartManager.saveCart(cartItems);
                        updateCart();
                        cartAdapter.notifyItemRemoved(position);
                    }

                }
        );
        binding.cartRecyclerView.setAdapter(cartAdapter);

        // Load cart items
        updateCart();

        return binding.getRoot();
    }

    private void updateCart() {
        // Get cart items from CartManager
        cartItems = cartManager.getCart();

        // Save updated cart
        cartManager.saveCart(cartItems);

        // Update UI based on cart state
        if (cartItems.isEmpty()) {
            binding.emptyCartText.setVisibility(View.VISIBLE);
            binding.cartRecyclerView.setVisibility(View.GONE);
            binding.checkoutButton.setEnabled(false);
            binding.totalPriceText.setText("Total: Rp 0");
        } else {
            binding.emptyCartText.setVisibility(View.GONE);
            binding.cartRecyclerView.setVisibility(View.VISIBLE);
            binding.checkoutButton.setEnabled(true);

            // Calculate total price
            double total = 0;
            for (Cart item : cartItems) {
                // Include discount in price calculation
                double finalPrice = item.getPrice() * (1 - item.getDiscount() / 100.0);
                total += finalPrice * item.getQuantity();
            }

            // Format total price to Indonesian Rupiah
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
            binding.totalPriceText.setText(formatter.format(total));
        }

        // Update adapter with new items
        cartAdapter.updateItems(cartItems);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}