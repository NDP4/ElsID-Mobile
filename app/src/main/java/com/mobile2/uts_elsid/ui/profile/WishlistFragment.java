package com.mobile2.uts_elsid.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.mobile2.uts_elsid.R;
import com.mobile2.uts_elsid.adapter.WishlistAdapter;
import com.mobile2.uts_elsid.api.ApiClient;
import com.mobile2.uts_elsid.api.ApiService;
import com.mobile2.uts_elsid.api.ProductResponse;
import com.mobile2.uts_elsid.utils.WishlistManager;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import es.dmoral.toasty.Toasty;

public class WishlistFragment extends Fragment {

    private RecyclerView recyclerView;
    private WishlistAdapter adapter;
    private WishlistManager wishlistManager;
    private View emptyView;
    private View loadingView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wishlist, container, false);

        recyclerView = view.findViewById(R.id.wishlistRecyclerView);
        emptyView = view.findViewById(R.id.emptyWishlistView);
        loadingView = view.findViewById(R.id.loadingView);

        // Initialize WishlistManager
        wishlistManager = new WishlistManager(requireContext());

        // Setup RecyclerView
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new WishlistAdapter(requireContext());
        recyclerView.setAdapter(adapter);

        loadWishlistItems();

        return view;
    }

    private void loadWishlistItems() {
        List<Integer> wishlistIds = wishlistManager.getWishlist();

        if (wishlistIds.isEmpty()) {
            showEmptyState();
            return;
        }

        loadingView.setVisibility(View.VISIBLE);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        // Get details for each product in wishlist
        for (Integer productId : wishlistIds) {
            Call<ProductResponse> call = apiService.getProduct(productId);
            call.enqueue(new Callback<ProductResponse>() {
                @Override
                public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ProductResponse product = response.body();
                        adapter.addItem(product);

                        if (adapter.getItemCount() > 0) {
                            showContent();
                        }
                    }
                    loadingView.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<ProductResponse> call, Throwable t) {
                    Toasty.error(requireContext(), "Failed to load wishlist items", Toasty.LENGTH_SHORT).show();
                    loadingView.setVisibility(View.GONE);
                }
            });
        }
    }
// Di WishlistFragment.java
//private void loadWishlistItems() {
//    List<Integer> wishlistIds = wishlistManager.getWishlist();
//
//    if (wishlistIds.isEmpty()) {
//        showEmptyState();
//        return;
//    }
//
//    loadingView.setVisibility(View.VISIBLE);
//    ApiService apiService = ApiClient.getClient().create(ApiService.class);
//
//    // Gunakan endpoint khusus untuk multiple products
//    Call<List<ProductResponse>> call = apiService.getProductsByIds(wishlistIds);
//
//    call.enqueue(new Callback<List<ProductResponse>>() {
//        @Override
//        public void onResponse(Call<List<ProductResponse>> call, Response<List<ProductResponse>> response) {
//            loadingView.setVisibility(View.GONE);
//            if (response.isSuccessful() && response.body() != null) {
//                adapter.clearItems();
//                adapter.addAllItems(response.body());
//                if (adapter.getItemCount() > 0) {
//                    showContent();
//                } else {
//                    showEmptyState();
//                }
//            }
//        }
//
//        @Override
//        public void onFailure(Call<List<ProductResponse>> call, Throwable t) {
//            loadingView.setVisibility(View.GONE);
//            Toasty.error(requireContext(), "Gagal memuat wishlist", Toasty.LENGTH_SHORT).show();
//        }
//    });
//}

    private void showEmptyState() {
        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
        loadingView.setVisibility(View.GONE);
    }

    private void showContent() {
        recyclerView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        loadingView.setVisibility(View.GONE);
    }
}