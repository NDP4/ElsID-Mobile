package com.mobile2.uts_elsid.adapter;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mobile2.uts_elsid.R;
import com.mobile2.uts_elsid.model.Product;
import com.mobile2.uts_elsid.utils.WishlistManager;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import es.dmoral.toasty.Toasty;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private Context context;
    private List<Product> products;
    private OnProductClickListener listener;
    private WishlistManager wishlistManager;

    private boolean showUnavailableUI = false;
    // Tambahkan method untuk product unavailable
    public void setShowUnavailableUI(boolean show) {
        this.showUnavailableUI = show;
        notifyDataSetChanged();
    }

    // Constructor
//    public ProductAdapter(Context context, List<Product> products, WishlistManager wishlistManager) {
//        this.context = context;
//        this.products = products;
//        this.wishlistManager = wishlistManager;
//    }


    public interface OnProductClickListener {
        void onProductClick(Product product);
    }
    public void setOnProductClickListener(OnProductClickListener listener) {
        this.listener = listener;
    }
    public ProductAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
        this.wishlistManager = new WishlistManager(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        MaterialCardView cardView = (MaterialCardView) view; // Cast directly since root view is MaterialCardView
        cardView.setStateListAnimator(null);
        cardView.setElevation(0); // Set consistent elevation
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);

        holder.titleText.setText(product.getTitle());
        holder.categoryText.setText(product.getCategory());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductClick(product);
            }
        });

        // Nonaktifkan klik hanya untuk produk tidak tersedia
        if (showUnavailableUI || !"available".equalsIgnoreCase(product.getStatus())) {
            holder.itemView.setClickable(false); // <-- Tambahkan ini
            holder.itemView.setAlpha(0.5f);
            holder.soldOutLabel.setVisibility(View.VISIBLE);
//            holder.addToCartButton.setEnabled(false);
        } else {
            holder.itemView.setClickable(true); // <-- Pastikan klik aktif
            holder.itemView.setAlpha(1f);
            holder.soldOutLabel.setVisibility(View.GONE);
//            holder.addToCartButton.setEnabled(true);
        }

        // Tampilkan UI khusus untuk produk tidak tersedia
        if (showUnavailableUI || !"available".equalsIgnoreCase(product.getStatus())) {
            holder.itemView.setAlpha(0.5f);
            holder.soldOutLabel.setVisibility(View.VISIBLE);
//            holder.addToCartButton.setEnabled(false);
        } else {
            holder.itemView.setAlpha(1f);
            holder.soldOutLabel.setVisibility(View.GONE);
//            holder.addToCartButton.setEnabled(true);
        }

        // Format price with currency
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        double finalPrice = product.getPrice() * (1 - product.getDiscount()/100.0);
        holder.priceText.setText(formatter.format(finalPrice));

        // Handle discount
        if (product.getDiscount() > 0) {
            holder.discountText.setVisibility(View.VISIBLE);
            holder.discountText.setText("-" + (int)product.getDiscount() + "%");

            holder.originalPriceText.setVisibility(View.VISIBLE);
            holder.originalPriceText.setText(formatter.format(product.getPrice()));
            holder.originalPriceText.setPaintFlags(holder.originalPriceText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.discountText.setVisibility(View.GONE);
            holder.originalPriceText.setVisibility(View.GONE);
        }

        // Load first image if available
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            String imageUrl = product.getImages().get(0);
            // Check if the URL is relative and prepend base URL if needed
            if (!imageUrl.startsWith("http")) {
                imageUrl = "https://mobile2.ndp.my.id/" + imageUrl;
            }

            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(holder.productImage);
        }


        // Handle cart button click
//        holder.cartButton.setOnClickListener(v -> {
//            Toasty.success(context, "Added " + product.getTitle() + " to cart", Toasty.LENGTH_SHORT).show();
//            // TODO: Implement cart functionality
//        });
//        holder.cartButton.setOnClickListener(v -> {
//            Log.d("HomeFragment", "Navigating to Checkout");
//            Toasty.success(requireContext(), "Navigating to checkout", Toasty.LENGTH_SHORT).show();
//            NavController navController = Navigation.findNavController((), R.id.nav_host_fragment_activity_home);
//            navController.navigate(R.id.navigation_checkout, null, new NavOptions.Builder()
//                    .setPopUpTo(R.id.navigation_home, true)
//                    .setLaunchSingleTop(true)
//                    .build());
//        });

        boolean isInWishlist = wishlistManager.isInWishlist(product.getId());
        updateWishlistButton(holder.wishlistButton, isInWishlist);

        holder.wishlistButton.setOnClickListener(v -> {
            if (isInWishlist) {
                wishlistManager.removeFromWishlist(product.getId());
                Toasty.success(context, "Removed from wishlist", Toasty.LENGTH_SHORT).show();
            } else {
                wishlistManager.addToWishlist(product.getId());
                Toasty.success(context, "Added to wishlist", Toasty.LENGTH_SHORT).show();
            }
            updateWishlistButton(holder.wishlistButton, !isInWishlist);
        });
    }


    private void updateWishlistButton(FloatingActionButton wishlistButton, boolean isInWishlist) {
        wishlistButton.setImageResource(isInWishlist ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
        wishlistButton.setColorFilter(isInWishlist ? ContextCompat.getColor(context, R.color.wishlist_icon_active) : ContextCompat.getColor(context, R.color.wishlist_icon_inactive));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void updateData(List<Product> newProducts) {
        this.products = newProducts;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView titleText, categoryText, priceText, originalPriceText, discountText;
        MaterialButton cartButton;
        FloatingActionButton wishlistButton;
        TextView soldOutLabel;
//        MaterialButton addToCartButton;


        public ViewHolder(View view) {
            super(view);
            productImage = view.findViewById(R.id.productImage);
            titleText = view.findViewById(R.id.titleText);
            categoryText = view.findViewById(R.id.categoryText);
            priceText = view.findViewById(R.id.priceText);
            originalPriceText = view.findViewById(R.id.originalPriceText);
            discountText = view.findViewById(R.id.discountText);
            cartButton = view.findViewById(R.id.cartButton);
            wishlistButton = view.findViewById(R.id.wishlistButton);
            soldOutLabel = view.findViewById(R.id.soldOutLabel);
//            addToCartButton = view.findViewById(R.id.addToCartButton);
        }
    }
}