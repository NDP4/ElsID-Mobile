package com.mobile2.uts_elsid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.mobile2.uts_elsid.R;
import java.util.ArrayList;
import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {
    private final Context context;
    private List<String> imageUrls;
    private static final String BASE_URL = "https://mobile2.ndp.my.id/";

    public BannerAdapter(Context context) {
        this.context = context;
        this.imageUrls = new ArrayList<>();
    }

    public void setImageUrls(List<String> imageUrls) {
        if (imageUrls != null) {
            this.imageUrls = new ArrayList<>(imageUrls);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.banner_item, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);
        if (imageUrl != null && !imageUrl.isEmpty()) {
            // Prepend base URL if the image URL is relative
            String fullUrl = imageUrl.startsWith("http") ? imageUrl : BASE_URL + imageUrl;

            Glide.with(context)
                    .load(fullUrl)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(holder.bannerImage);
        }
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView bannerImage;

        BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            bannerImage = itemView.findViewById(R.id.bannerImage);
        }
    }
}