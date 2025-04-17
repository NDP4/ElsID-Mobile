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
import com.mobile2.uts_elsid.model.Product;

import java.util.ArrayList;
import java.util.List;

public class SearchSuggestionsAdapter extends RecyclerView.Adapter<SearchSuggestionsAdapter.ViewHolder> {
    private List<Product> suggestions = new ArrayList<>();
    private OnSuggestionClickListener listener;
    private Context context;

    public interface OnSuggestionClickListener {
        void onSuggestionClick(Product product);
    }

    public SearchSuggestionsAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_suggestion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = suggestions.get(position);
//        holder.textView.setText(product.getTitle());

        // Set the product title
        holder.titleText.setText(product.getTitle());

        // Load product image using Glide
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            String imageUrl = product.getImages().get(0);
            if (!imageUrl.startsWith("http")) {
                imageUrl = "https://mobile2.ndp.my.id/" + imageUrl;
            }

            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(holder.productImage);
        }

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSuggestionClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return suggestions.size();
    }

    // Method to set click listener
    public void setOnSuggestionClickListener(OnSuggestionClickListener listener) {
        this.listener = listener;
    }


    // Method to update suggestions list
    public void updateSuggestions(List<Product> newSuggestions) {
        this.suggestions = newSuggestions;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;
        ImageView productImage;

        ViewHolder(View view) {
            super(view);
            titleText = view.findViewById(R.id.productTitle);
            productImage = view.findViewById(R.id.productImage);
        }
    }
}