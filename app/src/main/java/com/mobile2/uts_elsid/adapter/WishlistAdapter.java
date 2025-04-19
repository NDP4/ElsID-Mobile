package com.mobile2.uts_elsid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.mobile2.uts_elsid.R;
import com.mobile2.uts_elsid.api.ProductResponse;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.ViewHolder> {
    private Context context;
    private List<ProductResponse> products;

    public WishlistAdapter(Context context) {
        this.context = context;
        this.products = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        ProductResponse product = products.get(position);
//
//
//
//        // Bind title
//        holder.titleText.setText(product.getName());
//
//        // Bind price
//        holder.priceText.setText(String.valueOf(product.getPrice()));
//        // Load image using Glide
//        Glide.with(context)
//                .load(product.getImage())
//                .into(holder.productImage);
//
//
//        // Set price
//        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
//        String formattedPrice = formatter.format(product.getPrice());
//        holder.priceText.setText(formattedPrice);
//
//        // Load image using Glide
//        if (product.getImage() != null && !product.getImage().isEmpty()) {
//            Glide.with(context)
//                    .load(product.getImage())
//                    .placeholder(R.drawable.placeholder_image)
//                    .into(holder.productImage);
//        }
//
//
//    }

    // Di WishlistAdapter.java
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductResponse product = products.get(position);

        // Gunakan getTitle() bukan getName()
        holder.titleText.setText(product.getTitle());

        // Ambil gambar pertama dari list images
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            String imageUrl = product.getImages().get(0);
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .into(holder.productImage);
        }

        // Format harga
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        String formattedPrice = formatter.format(product.getPrice());
        holder.priceText.setText(formattedPrice);
    }
    // Di WishlistAdapter.java
    public void addAllItems(List<ProductResponse> newProducts) {
        products.addAll(newProducts);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return products.size();
    }

    public void addItem(ProductResponse product) {
        products.add(product);
        notifyItemInserted(products.size() - 1);
    }

    public void clearItems() {
        products.clear();
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView titleText, priceText, categoryText;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            titleText = itemView.findViewById(R.id.titleText);
            priceText = itemView.findViewById(R.id.priceText);
            categoryText = itemView.findViewById(R.id.categoryText);
        }
    }
}