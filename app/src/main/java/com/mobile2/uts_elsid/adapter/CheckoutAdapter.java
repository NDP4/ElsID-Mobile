package com.mobile2.uts_elsid.adapter;

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
import com.mobile2.uts_elsid.model.CartItem;

import java.util.List;

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.ViewHolder> {
    private List<CartItem> cartItems;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onRemoveClick(int position);
    }

    public CheckoutAdapter(List<CartItem> cartItems, OnItemClickListener listener) {
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = cartItems.get(position);

        holder.productName.setText(item.getName());
        holder.variantName.setText(item.getVariant());
        holder.productPrice.setText(String.format("Rp %,.0f", item.getPrice()));
        holder.quantityText.setText(String.valueOf(item.getQuantity()));

        Glide.with(holder.itemView.getContext())
                .load(item.getImageUrl())
                .into(holder.productImage);

        holder.removeButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemoveClick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, variantName, productPrice, quantityText;
        ImageButton removeButton;

        ViewHolder(View view) {
            super(view);
            productImage = view.findViewById(R.id.productImage);
            productName = view.findViewById(R.id.productName);
            variantName = view.findViewById(R.id.variantName);
            productPrice = view.findViewById(R.id.productPrice);
            quantityText = view.findViewById(R.id.quantityText);
            removeButton = view.findViewById(R.id.removeButton);
        }
    }
}