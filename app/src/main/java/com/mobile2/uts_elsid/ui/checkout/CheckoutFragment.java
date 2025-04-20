package com.mobile2.uts_elsid.ui.checkout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

//import com.midtrans.sdk.corekit.core.MidtransSDK;
//import com.midtrans.sdk.corekit.core.TransactionRequest;
//import com.midtrans.sdk.corekit.internal.network.model.response.TransactionDetails;
//import com.midtrans.sdk.corekit.models.CustomerDetails;
//import com.midtrans.sdk.corekit.models.snap.TransactionResult;
//import com.midtrans.sdk.uikit.SdkUIFlowBuilder;
//import com.midtrans.sdk.uikit.api.model.ItemDetails;
//import com.midtrans.sdk.corekit.models.ItemDetails;
//import com.mobile2.uts_elsid.PaymentActivity;
import com.mobile2.uts_elsid.adapter.CartAdapter;
import com.mobile2.uts_elsid.adapter.CheckoutAdapter;
import com.mobile2.uts_elsid.databinding.FragmentCheckoutBinding;
import com.mobile2.uts_elsid.model.Cart;
import com.mobile2.uts_elsid.utils.CartManager;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class CheckoutFragment extends Fragment {

    private FragmentCheckoutBinding binding;
    private CartAdapter cartAdapter;
    private CartManager cartManager;
    private CheckoutAdapter checkoutAdapter;
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


        // Setup checkout button listener
        setupCheckoutButton();
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

    private void setupCheckoutButton() {
        binding.checkoutButton.setOnClickListener(v -> {
            if (!cartItems.isEmpty()) {
                processCheckout();
            }
        });
    }

//    private void initMidtransPayment() {
//        if (!isAdded() || getActivity() == null) {
//            Log.e("Midtrans", "Fragment tidak terattach ke activity");
//            return;
//        }
//        // Initialize Midtrans SDK Config
//        SdkUIFlowBuilder.init()
//                .setClientKey("SB-Mid-client-qxA7e0wpu9hUGyhk")
//                .setContext(requireActivity())
//                .setTransactionFinishedCallback(result -> {
//                    // Handle transaction result
//                    if (result.getResponse() != null) {
//                        switch (result.getStatus()) {
//                            case TransactionResult.STATUS_SUCCESS:
//                                handleSuccessTransaction();
//                                break;
//                            case TransactionResult.STATUS_PENDING:
//                                handlePendingTransaction();
//                                break;
//                            case TransactionResult.STATUS_FAILED:
//                                handleFailedTransaction();
//                                break;
//                        }
//                    } else if (result.isTransactionCanceled()) {
//                        handleCancelTransaction();
//                    }
//                })
//                .enableLog(true)
//                .buildSDK();
//
//        // Create transaction request
//        TransactionRequest transactionRequest = new TransactionRequest(
//                "ORDER-" + System.currentTimeMillis(),
//                calculateTotal()
//        );
//
//        // Add customer details
//        CustomerDetails customerDetails = new CustomerDetails();
//        customerDetails.setFirstName("John");
//        customerDetails.setEmail("john@email.com");
//        customerDetails.setPhone("08123456789");
//        transactionRequest.setCustomerDetails(customerDetails);
//
//        // Add item details (FIXED: Gunakan class ItemDetails dari corekit)
//        ArrayList<com.midtrans.sdk.corekit.models.ItemDetails> itemDetails = new ArrayList<>();
//        for (Cart item : cartItems) {
//            com.midtrans.sdk.corekit.models.ItemDetails midtransItem =
//                    new com.midtrans.sdk.corekit.models.ItemDetails(
//                            String.valueOf(item.getProductId()), // Convert int to String
//                            item.getPrice(),
//                            item.getQuantity(),
//                            item.getTitle()
//                    );
//            itemDetails.add(midtransItem);
//        }
//        transactionRequest.setItemDetails(itemDetails);
//
//
//        // Start payment flow
//        MidtransSDK.getInstance().setTransactionRequest(transactionRequest);
//        MidtransSDK.getInstance().startPaymentUiFlow(requireActivity());
//    }

//    private void handleTransactionResult(TransactionResult result) {
//        requireActivity().runOnUiThread(() -> {
//            Log.d("Midtrans", "Status transaksi: " + result.getStatus());
//
//            if (result.isTransactionCanceled()) {
//                Toasty.info(getContext(), "Pembayaran dibatalkan", Toasty.LENGTH_LONG).show();
//                return;
//            }
//
//            switch (result.getStatus()) {
//                case TransactionResult.STATUS_SUCCESS:
//                    handleSuccessTransaction();
//                    break;
//                case TransactionResult.STATUS_PENDING:
//                    handlePendingTransaction();
//                    break;
//                case TransactionResult.STATUS_FAILED:
//                    Log.e("Midtrans", "Gagal: " + result.getResponse().getStatusCode());
//                    handleFailedTransaction();
//                    break;
//                default:
//                    Toasty.warning(getContext(), "Status tidak diketahui", Toasty.LENGTH_LONG).show();
//            }
//        });
//    }
//
//    private void handleSuccessTransaction() {
//        requireActivity().runOnUiThread(() -> {
//            Toasty.success(getContext(), "Payment Success!", Toasty.LENGTH_LONG).show();
//            // Clear cart and navigate back
//            cartItems.clear();
//            cartManager.saveCart(cartItems);
//            requireActivity().onBackPressed();
//        });
//    }
//
//    private void handlePendingTransaction() {
//        requireActivity().runOnUiThread(() -> {
//            Toasty.info(getContext(), "Payment Pending", Toasty.LENGTH_LONG).show();
//        });
//    }
//
//    private void handleFailedTransaction() {
//        requireActivity().runOnUiThread(() -> {
//            Toasty.error(getContext(), "Payment Failed", Toasty.LENGTH_LONG).show();
//        });
//    }
//
//    private void handleCancelTransaction() {
//        requireActivity().runOnUiThread(() -> {
//            Toasty.info(getContext(), "Payment Cancelled", Toasty.LENGTH_LONG).show();
//        });
//    }
    private void processCheckout() {
        // Here you would implement the actual checkout process
        if (cartItems == null) {
            Toasty.error(requireContext(), "Cart is null", Toasty.LENGTH_SHORT).show();
            return;
        }
        // For example: connecting to payment gateway, creating order, etc.
        Toasty.success(requireContext(), "Checkout successful!", Toasty.LENGTH_SHORT).show();

        // Clear cart after successful checkout
        cartItems.clear();
        cartManager.saveCart(cartItems); // Save the empty cart
        cartAdapter.notifyDataSetChanged();
        updateCart();
    }
//    private void processCheckout() {
//        if (cartItems == null || cartItems.isEmpty()) {
//            Toasty.info(requireContext(), "Keranjang kosong", Toasty.LENGTH_SHORT).show();
//            return;
//        }
//
//        // 1. Hitung total harga
//        double totalAmount = 0;
//        for (Cart item : cartItems) {
//            double finalPrice = item.getPrice() * (1 - item.getDiscount() / 100.0);
//            totalAmount += finalPrice * item.getQuantity();
//        }
//
//        // 2. Buat transaksi detail
//        TransactionDetails transactionDetails = new TransactionDetails(
//                "ORDER-" + System.currentTimeMillis(), // ID unik
//                totalAmount
//        );
//
//        // 3. Buat item details
//        ArrayList<ItemDetails> itemDetails = new ArrayList<>();
//        for (Cart item : cartItems) {
//            itemDetails.add(new ItemDetails(
//                    item.getId(), // ID produk
//                    item.getPrice(),
//                    item.getQuantity(),
//                    item.getName()
//            ));
//        }
//
//        // 4. Buat customer detail (opsional)
//        CustomerDetails customerDetails = new CustomerDetails();
//        customerDetails.setFirstName("Nama Pelanggan");
//        customerDetails.setPhone("08123456789");
//        customerDetails.setEmail("customer@example.com");
//
//        // 5. Mulai pembayaran
//        SnapTransaction snapTransaction = new SnapTransaction(transactionDetails)
//                .setCustomerDetails(customerDetails)
//                .setItemDetails(itemDetails);
//
//        MidtransSDK.getInstance().setTransactionRequest(snapTransaction);
//
//        // 6. Buka halaman pembayaran Midtrans
//        startActivity(new Intent(requireContext(), PaymentActivity.class));
//
//        // 7. Kosongkan keranjang setelah pembayaran dimulai
//        cartItems.clear();
//        cartManager.saveCart(cartItems);
//        cartAdapter.notifyDataSetChanged();
//        updateCart();
//    }

//    private void initializeMidtransSdk() {
//        if (MidtransSDK.getInstance().getEnvironment() == null) {
//            SdkUIFlowBuilder.init()
//                    .setClientKey("SB-Mid-client-qxA7e0wpu9hUGyhk")
//                    .setContext(requireActivity())
//                    .setTransactionFinishedCallback(this::handleTransactionResult)
//                    .setMerchantBaseUrl("https://midtrans-sample.herokuapp.com") // Gunakan URL sandbox publik
//                    .enableLog(true)
//                    .buildSDK();
//        }
//    }

    // proses checkout langsung
//    private void processCheckout() {
//        Log.d("Checkout", "Tombol checkout ditekan");
//        if (cartItems.isEmpty()) {
//            Toasty.warning(requireContext(), "Keranjang kosong", Toasty.LENGTH_SHORT).show();
//            return;
//        }
////        initializeMidtransSdk();
//
//        // Pastikan fragment masih aktif
//        if (!isAdded() || getActivity() == null) {
//            Log.e("Checkout", "Fragment tidak aktif");
//            return;
//        }
//
//        try {
//            // 1. Hitung total
//            double totalAmount = calculateTotal();
//            Log.d("Checkout", "Total belanja: " + totalAmount);
//
//            // 2. Inisialisasi SDK (sekali saja)
//            if (MidtransSDK.getInstance().getSemiBoldText() == null) {
//                SdkUIFlowBuilder.init()
//                        .setClientKey("SB-Mid-client-qxA7e0wpu9hUGyhk")
//                        .setContext(requireActivity())
//                        .setTransactionFinishedCallback(this::handleTransactionResult)
//                        .enableLog(true)
//                        .buildSDK();
//            }
//
//            // 3. Siapkan transaksi
//            TransactionRequest request = new TransactionRequest(
//                    "ORDER-" + System.currentTimeMillis(),
//                    totalAmount
//            );
//
//            // 4. Tambahkan item
//            ArrayList<ItemDetails> items = new ArrayList<>();
//            for (Cart item : cartItems) {
//                items.add(new ItemDetails(
//                        String.valueOf(item.getProductId()),
//                        item.getPrice(),
//                        item.getQuantity(),
//                        item.getTitle()
//                ));
//            }
//            request.setItemDetails(items);
//
//            // 5. Tambahkan customer
//            CustomerDetails customer = new CustomerDetails();
//            customer.setFirstName("Pelanggan");
//            customer.setEmail("customer@example.com");
//            request.setCustomerDetails(customer);
//
//            // 6. Mulai pembayaran
//            MidtransSDK.getInstance().setTransactionRequest(request);
//            MidtransSDK.getInstance().startPaymentUiFlow(requireActivity());
//            Log.d("Checkout", "UI Pembayaran dimulai");
//
//        } catch (Exception e) {
//            Log.e("Checkout", "Error: " + e.getMessage(), e);
//            Toasty.error(requireContext(), "Gagal memulai pembayaran").show();
//        }
//    }
    private double calculateTotal() {
        double total = 0;
        for (Cart item : cartItems) {
            double finalPrice = item.getPrice() * (1 - item.getDiscount() / 100.0);
            total += finalPrice * item.getQuantity();
        }
        return total;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}