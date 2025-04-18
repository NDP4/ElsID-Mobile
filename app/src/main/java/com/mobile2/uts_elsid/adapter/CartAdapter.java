package com.mobile2.uts_elsid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mobile2.uts_elsid.R;
import com.mobile2.uts_elsid.model.Cart;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.widget.ImageButton;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private List<Cart> cartItems;
    private Context context;
    private CartClickListener listener;



    public interface OnCartUpdateListener {
        void onCartUpdated();
    }

    public interface CartClickListener {
        void onQuantityChanged(int position, int newQuantity);
        void onRemoveItem(int position);
    }



    public CartAdapter(Context context, List<Cart> cartItems, CartClickListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cart item = cartItems.get(position);

        holder.productName.setText(item.getTitle());

        // Handle variant name visibility
        if (item.getVariantName() != null && !item.getVariantName().isEmpty()) {
            holder.variantName.setVisibility(View.VISIBLE);
            holder.variantName.setText(item.getVariantName());
        } else {
            holder.variantName.setVisibility(View.GONE);
        }

        // Format price to Indonesian Rupiah
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        double finalPrice = item.getPrice() * (1 - item.getDiscount()/100.0);
        holder.productPrice.setText(formatter.format(finalPrice));

        holder.quantityText.setText(String.valueOf(item.getQuantity()));

        // Load image using Glide
        if (item.getImage() != null && !item.getImage().isEmpty()) {
            String imageUrl = item.getImage().startsWith("http") ?
                    item.getImage() :
                    "https://mobile2.ndp.my.id/" + item.getImage();

            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .into(holder.productImage);
        }

        // Single remove button click listener with confirmation dialog
        holder.removeButton.setOnClickListener(v -> {
            // Get the current adapter position
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                new AlertDialog.Builder(context)
                        .setTitle("Remove Item")
                        .setMessage("Are you sure you want to remove this item from cart?")
                        .setPositiveButton("Remove", (dialog, which) -> {
                            if (listener != null) {
                                listener.onRemoveItem(adapterPosition);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        // Add delete button click listener
//        holder.removeButton.setOnClickListener(v -> {
//            new AlertDialog.Builder(context)
//                    .setTitle("Remove Item")
//                    .setMessage("Are you sure you want to remove this item from cart?")
//                    .setPositiveButton("Remove", (dialog, which) -> {
//                        if (listener != null) {
//                            listener.onRemoveItem(position);
//                        }
//                    })
//                    .setNegativeButton("Cancel", null)
//                    .show();
//        });
//
//        // Second listener (direct removal)
//        // Remove button
//        holder.removeButton.setOnClickListener(v -> {
//            if (listener != null) {
//                listener.onRemoveItem(holder.getAdapterPosition());
//            }
//        });




        // Set click listeners
        holder.decreaseQuantity.setOnClickListener(v -> {
            int currentQty = item.getQuantity();
            if (currentQty > 1) {
                listener.onQuantityChanged(position, currentQty - 1);
            }
        });

        holder.increaseQuantity.setOnClickListener(v -> {
            int currentQty = item.getQuantity();
            if (currentQty < item.getStock()) {
                listener.onQuantityChanged(position, currentQty + 1);
            }
        });

//        holder.removeButton.setOnClickListener(v ->
//                listener.onRemoveItem(position)
//        );
    }


    @Override
    public int getItemCount() {
        return cartItems != null ? cartItems.size() : 0;
    }

    // Add this method to update the cart items
    public void updateItems(List<Cart> newItems) {
        this.cartItems = new ArrayList<>(newItems);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productPrice, quantityText, variantName;
        View decreaseQuantity, increaseQuantity;
        ImageButton removeButton;

        ViewHolder(View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            variantName = itemView.findViewById(R.id.variantName);
            productPrice = itemView.findViewById(R.id.productPrice);
            quantityText = itemView.findViewById(R.id.quantityText);
            decreaseQuantity = itemView.findViewById(R.id.decreaseButton);
            increaseQuantity = itemView.findViewById(R.id.increaseButton);
            removeButton = itemView.findViewById(R.id.removeButton);
        }
    }


}