package com.mobile2.uts_elsid.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.mobile2.uts_elsid.R;
import com.mobile2.uts_elsid.utils.CategoryIconMapper;

import java.util.List;

// CategoryChipAdapter.java
public class CategoryChipAdapter extends RecyclerView.Adapter<CategoryChipAdapter.ViewHolder> {
    private List<String> categories;
    private OnCategoryClickListener listener;

    public CategoryChipAdapter(List<String> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.item_category_chip, parent, false);
                .inflate(R.layout.item_category_custom, parent, false);
        return new ViewHolder(view);
    }

//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        String category = categories.get(position);
//        holder.chip.setText(category);
//
//        // Set icon
//        holder.chip.setChipIconResource(CategoryIconMapper.getCategoryIcon(category));
//
//        // Handle click
//        holder.chip.setOnClickListener(v -> {
//            if (listener != null) {
//                listener.onCategoryClick(category);
//            }
//        });
//    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String category = categories.get(position);

        // Pendekkan teks jika lebih dari 15 karakter
        String displayText = category.length() > 15 ?
                category.substring(0, 12) + "..." : category;

        holder.label.setText(displayText);

        // Set icon
        holder.icon.setImageResource(CategoryIconMapper.getCategoryIcon(category));

        // Set text
        holder.label.setText(category);

        // Handle selection state
        holder.root.setSelected(isCategorySelected(category));

        // Handle click
        holder.root.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCategoryClick(category);
            }
        });

    }
    private boolean isCategorySelected(String category) {
        // Implement your selection logic here
        return false;
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
//        Chip chip;
        ImageView icon;
        TextView label;
        View root;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
//            chip = itemView.findViewById(R.id.chip);
            root = itemView;
            icon = itemView.findViewById(R.id.icon);
            label = itemView.findViewById(R.id.label);
        }
    }

    public interface OnCategoryClickListener {
        void onCategoryClick(String category);
    }
}