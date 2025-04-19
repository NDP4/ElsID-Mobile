package com.mobile2.uts_elsid.ui.home;

import static com.mobile2.uts_elsid.utils.CategoryIconMapper.getCategoryIcon;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.chip.Chip;
import com.google.android.material.tabs.TabLayoutMediator;
import com.mobile2.uts_elsid.R;
import com.mobile2.uts_elsid.adapter.BannerAdapter;
import com.mobile2.uts_elsid.adapter.CategoryChipAdapter;
import com.mobile2.uts_elsid.adapter.ProductAdapter;
import com.mobile2.uts_elsid.adapter.SearchSuggestionsAdapter;
import com.mobile2.uts_elsid.api.ApiClient;
import com.mobile2.uts_elsid.api.ApiService;
import com.mobile2.uts_elsid.api.BannerResponse;
import com.mobile2.uts_elsid.api.LoginResponse;
import com.mobile2.uts_elsid.api.ProductResponse;
import com.mobile2.uts_elsid.databinding.FragmentHomeBinding;
import com.mobile2.uts_elsid.model.Cart;
import com.mobile2.uts_elsid.model.Product;
import com.mobile2.uts_elsid.model.ProductVariant;
import com.mobile2.uts_elsid.utils.CartManager;
import com.mobile2.uts_elsid.utils.CategoryIconMapper;
import com.mobile2.uts_elsid.utils.GridSpacingItemDecoration;
import com.mobile2.uts_elsid.utils.SessionManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements VariantSelectionBottomSheet.VariantSelectionListener {
    private CartManager cartManager;
    private TextView cartBadge;
    private ProductAdapter unavailableProductAdapter;
    private List<Product> unavailableProducts = new ArrayList<>();

    private FragmentHomeBinding binding;
    private BannerAdapter bannerAdapter;
    private ProductAdapter productAdapter;
    private List<Product> productList = new ArrayList<>();
    private static final long SLIDE_DELAY = 3000; // 3 seconds
    private final Handler sliderHandler = new Handler(Looper.getMainLooper());
    private String baseUrl = "https://mobile2.ndp.my.id/";



    private final Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            if (binding != null && bannerAdapter != null && bannerAdapter.getItemCount() > 0) {
                int currentItem = binding.bannerViewPager.getCurrentItem();
                int nextItem = (currentItem + 1) % bannerAdapter.getItemCount();
                binding.bannerViewPager.setCurrentItem(nextItem);
                sliderHandler.postDelayed(this, SLIDE_DELAY);
            }
        }
    };


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        cartManager = new CartManager(requireContext());
        cartBadge = binding.cartBadge;

        // Update cart badge count
        updateCartBadge(getCartItemCount());

        // Setup adapter
        productAdapter = new ProductAdapter(requireContext(), productList);
        productAdapter.setOnProductClickListener(new ProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                navigateToProductDetail(product);
            }

//            @Override
            public void onCartClick(Product product) {
                handleAddToCart(product);
            }
        });

        return binding.getRoot(); // FINAL RETURN
    }


    private void updateCartBadge(int count) {
        if (count > 0) {
            cartBadge.setVisibility(View.VISIBLE);
            cartBadge.setText(String.valueOf(count));
        } else {
            cartBadge.setVisibility(View.GONE);
        }
    }
    private int getCartItemCount() {
        return cartManager.getCartItemCount();
    }

//    private void handleAddToCart(Product product) {
//        if (product.getHasVariants() == 1 && product.getVariants() != null && !product.getVariants().isEmpty()) {
//            // Show variant selection bottom sheet
//            VariantSelectionBottomSheet bottomSheet = new VariantSelectionBottomSheet(product, (VariantSelectionBottomSheet.VariantSelectionListener) this);
//            bottomSheet.show(getChildFragmentManager(), "variant_selection");
//        } else {
//            // Add product directly without variant
//            addProductToCart(product, null);
//        }
//    }
    private void handleAddToCart(Product product) {
        // Create new Cart item from Product
        Cart cartItem = new Cart(
                product.getId(),
                product.getTitle(), // Changed from getName() to getTitle()
                product.getImages() != null && !product.getImages().isEmpty()
                        ? product.getImages().get(0)
                        : "", // Get first image URL or empty string
                product.getPrice(),
                1, // Initial quantity
                product.getMainStock(), // Changed from getStock() to getMainStock()
                product.getVariants() != null && !product.getVariants().isEmpty()
                        ? product.getVariants().get(0).getName()
                        : "", // Get first variant name or empty string
                product.getDiscount() // Add discount
        );

        // Add to cart and show success message
        cartManager.addToCart(cartItem);
        Toasty.success(requireContext(), "Added to cart", Toasty.LENGTH_SHORT).show();
    }
    private void addProductToCart(Product product, ProductVariant variant) {
        // Get price and discount based on variant or main product
        double price = variant != null ? variant.getPrice() : product.getPrice();
        double discount = variant != null ? variant.getDiscount() : product.getDiscount();
        int stock = variant != null ? variant.getStock() : product.getMainStock();
        String variantName = variant != null ? variant.getName() : "";

        // Create cart item
        Cart cartItem = new Cart(
                product.getId(),
                product.getTitle(),
                product.getImages() != null && !product.getImages().isEmpty() ? product.getImages().get(0) : "",
                price,
                1, // Initial quantity
                stock,
                variantName,
                discount
        );

        // Add to cart
        cartManager.addToCart(cartItem);

        // Show success message
        Toasty.success(requireContext(),
                "Added " + product.getTitle() + (variant != null ? " (" + variant.getName() + ")" : "") + " to cart",
                Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onVariantSelected(Product product, ProductVariant variant) {
        addProductToCart(product, variant);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get user data from SessionManager
        SessionManager sessionManager = new SessionManager(requireContext());
        LoginResponse userData = sessionManager.getUserData();

        // In HomeFragment.java onViewCreated method, replace the avatar loading code with:
        if (userData != null && userData.getUser() != null) {
            TextView userNameText = binding.userNameText;
            userNameText.setText(userData.getUser().getFullname());

            // Add cart button click listener
//            binding.cartButton.setOnClickListener(v -> {
//                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_home);
//                navController.navigate(R.id.navigation_checkout, null, new NavOptions.Builder()
//                        .setPopUpTo(R.id.navigation_home, true)  // true to remove home from back stack
//                        .setLaunchSingleTop(true)
//                        .build());
//            });
            binding.cartButton.setOnClickListener(v -> {
                Log.d("HomeFragment", "Navigating to Checkout");
                Toasty.success(requireContext(), "Navigating to checkout", Toasty.LENGTH_SHORT).show();
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_home);
                navController.navigate(R.id.navigation_checkout, null, new NavOptions.Builder()
                        .setPopUpTo(R.id.navigation_home, true)
                        .setLaunchSingleTop(true)
                        .build());
            });

        }

        initViews();
        setupSearchView();
        loadData();

        binding.swipeRefresh.setOnRefreshListener(this::loadData);
    }

    private void initViews() {
        // Initialize banner
        bannerAdapter = new BannerAdapter(requireContext());
        binding.bannerViewPager.setAdapter(bannerAdapter);

        // Reduce sensitivity for manual scrolling
        binding.bannerViewPager.setOffscreenPageLimit(3);
        binding.bannerViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, SLIDE_DELAY);
            }
        });

        // Initialize unavailable products
        unavailableProductAdapter = new ProductAdapter(requireContext(), unavailableProducts);
        unavailableProductAdapter.setShowUnavailableUI(true); // metode untuk menampilkan UI tidak tersedia
        binding.unavailableProductsRecyclerView.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        binding.unavailableProductsRecyclerView.setAdapter(unavailableProductAdapter);

        // Setup TabLayout with ViewPager2
        new TabLayoutMediator(binding.bannerIndicator, binding.bannerViewPager,
                (tab, position) -> {
                    // No text needed for indicators
                }).attach();

        // Initialize products
        binding.productsRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.productsRecyclerView.setAdapter(productAdapter);
        binding.productsRecyclerView.setHasFixedSize(true); // Add this
        binding.productsRecyclerView.setNestedScrollingEnabled(false); // Add this
        productAdapter.setOnProductClickListener(product -> {
            // Add debug logging
            Log.d("HomeFragment", "Clicked product: " +
                    "\nID: " + product.getId() +
                    "\nTitle: " + product.getTitle());

            Bundle bundle = new Bundle();
            bundle.putInt("product_id", product.getId());

            // Log the bundle content
            Log.d("HomeFragment", "Bundle product_id: " + bundle.getInt("product_id"));

            NavOptions navOptions = new NavOptions.Builder()
                    .setLaunchSingleTop(true)
//                    .setPopUpTo(R.id.navigation_home, true)  // false to preserve home in back stack
                    .build();
            Navigation.findNavController(requireView())
                    .navigate(R.id.navigation_product_detail, bundle, navOptions);
        });
    }

    // Add this inside HomeFragment.java class, after initViews() method
//    private void setupSearchView() {
//        SearchView searchView = binding.searchView;
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                searchProducts(query);
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                searchProducts(newText);
//                return true;
//            }
//        });
//    }
    private void setupSearchView() {
        SearchView searchView = binding.searchView;
        RecyclerView suggestionsList = binding.searchSuggestionsList;
        SearchSuggestionsAdapter suggestionsAdapter = new SearchSuggestionsAdapter(requireContext()); // Pass context

        suggestionsList.setLayoutManager(new LinearLayoutManager(requireContext()));
        suggestionsList.setAdapter(suggestionsAdapter);

        suggestionsAdapter.setOnSuggestionClickListener(product -> {
            searchView.setQuery(product.getTitle(), false);
            suggestionsList.setVisibility(View.GONE);
            Bundle bundle = new Bundle();
            bundle.putInt("product_id", product.getId());
            NavOptions navOptions = new NavOptions.Builder()
                    .setLaunchSingleTop(true)
                    .setPopUpTo(R.id.navigation_home, true)
                    .build();
            Navigation.findNavController(requireView())
                    .navigate(R.id.navigation_product_detail, bundle, navOptions);
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchProducts(query);
                binding.searchSuggestionsList.setVisibility(View.GONE);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    binding.searchSuggestionsList.setVisibility(View.GONE);
                } else {
                    updateSearchSuggestions(newText, suggestionsAdapter);
                    binding.searchSuggestionsList.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });

        // Hide suggestions when search view loses focus
        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                binding.searchSuggestionsList.setVisibility(View.GONE);
            }
        });
    }

    private void updateSearchSuggestions(String query, SearchSuggestionsAdapter adapter) {
        if (productList == null) return;

        String lowercaseQuery = query.toLowerCase().trim();
        List<Product> suggestions = new ArrayList<>();

        for (Product product : productList) {
            if (product.getTitle().toLowerCase().contains(lowercaseQuery) ||
                    product.getCategory().toLowerCase().contains(lowercaseQuery)) {
                suggestions.add(product);
                // Removed the limit check
            }
        }

        adapter.updateSuggestions(suggestions);
    }

    private void searchProducts(String query) {
        if (productList == null) return;

        if (query.isEmpty()) {
            // If search query is empty, show all products
            productAdapter.updateData(productList);
            return;
        }

        // Filter products based on search query
        List<Product> filteredList = new ArrayList<>();
        String lowercaseQuery = query.toLowerCase().trim();

        for (Product product : productList) {
            // Search in title and category
            if (product.getTitle().toLowerCase().contains(lowercaseQuery) ||
                    product.getCategory().toLowerCase().contains(lowercaseQuery)) {
                filteredList.add(product);
            }
        }

        productAdapter.updateData(filteredList);

        // Show message if no results found
        if (filteredList.isEmpty()) {
            Toasty.info(requireContext(), "No products found", Toasty.LENGTH_SHORT).show();
        }
    }

    private void loadData() {
        loadBanners();
        loadProducts();
    }

    private void loadBanners() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<BannerResponse> call = apiService.getBanners();

        call.enqueue(new Callback<BannerResponse>() {
            @Override
            public void onResponse(@NonNull Call<BannerResponse> call, @NonNull Response<BannerResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BannerResponse bannerResponse = response.body();
                    if (bannerResponse.getStatus() == 1 && bannerResponse.getBanners() != null) {
                        List<String> bannerUrls = new ArrayList<>();
                        for (BannerResponse.Banner banner : bannerResponse.getBanners()) {
                            if (banner.getImages() != null) {
                                bannerUrls.addAll(banner.getImages());
                            }
                        }
                        bannerAdapter.setImageUrls(bannerUrls);

                        // Start auto-sliding if there are banners
                        if (!bannerUrls.isEmpty()) {
                            sliderHandler.postDelayed(sliderRunnable, SLIDE_DELAY);
                        }
                    }
                }
            }

//            @Override
//            public void onFailure(@NonNull Call<BannerResponse> call, @NonNull Throwable t) {
//                Toasty.error(requireContext(), "Failed to load banners: " + t.getMessage(), Toasty.LENGTH_SHORT).show();
//            }
            @Override
            public void onFailure(@NonNull Call<BannerResponse> call, @NonNull Throwable t) {
                Log.e("Banner Load Failed", "Error: " + t.getMessage());
                Toasty.error(requireContext(), "Failed to load banners", Toasty.LENGTH_SHORT).show();
            }
        });
    }

//    private void loadProducts() {
//        ApiService apiService = ApiClient.getClient().create(ApiService.class);
//        Call<ProductResponse> call = apiService.getProducts();
//
//        call.enqueue(new Callback<ProductResponse>() {
//            @Override
//            public void onResponse(@NonNull Call<ProductResponse> call, @NonNull Response<ProductResponse> response) {
//                binding.swipeRefresh.setRefreshing(false);
//                if (response.isSuccessful() && response.body() != null) {
//                    productList = response.body().getProducts();
//                    productAdapter.updateData(productList);
//                    updateCategories(productList);
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<ProductResponse> call, @NonNull Throwable t) {
//                binding.swipeRefresh.setRefreshing(false);
//                Toasty.error(requireContext(), "Error loading products: " + t.getMessage(),
//                        Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//private void loadProducts() {
//    ApiService apiService = ApiClient.getClient().create(ApiService.class);
//    Call<ProductResponse> call = apiService.getProducts();
//    List<Product> availableProducts = new ArrayList<>();
//    List<Product> unavailableProducts = new ArrayList<>();
//
//    call.enqueue(new Callback<ProductResponse>() {
//        @Override
//        public void onResponse(@NonNull Call<ProductResponse> call, @NonNull Response<ProductResponse> response) {
//            binding.swipeRefresh.setRefreshing(false);
//            if (response.isSuccessful() && response.body() != null) {
//                // Pisahkan produk tersedia dan tidak tersedia
//                List<Product> allProducts = response.body().getProducts();
//                List<Product> availableProducts = new ArrayList<>();
//                unavailableProducts.clear();
//
//                for (Product product : allProducts) {
//                    if ("available".equalsIgnoreCase(product.getStatus())) {
//                        availableProducts.add(product);
//                    } else {
//                        unavailableProducts.add(product);
//                    }
//                }
//
//                // Separasi produk yang tidak tersedia
//                for (Product product : allProducts) {
//                    if (product.getMainStock()> 0) {
//                        availableProducts.add(product);
//                    }else {
//                        unavailableProducts.add(product);
//                    }
//                }
//
//                // setup available products grid
//                GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2);
//                productAdapter = new ProductAdapter(requireContext(), availableProducts);
//                binding.productsRecyclerView.setLayoutManager(gridLayoutManager);
//                binding.productsRecyclerView.setAdapter(productAdapter);
//
//                productList = availableProducts;
//                productAdapter.updateData(productList);
//                updateCategories(productList);
//
//                // Update unavailable products
//                if (unavailableProductAdapter != null) {
//                    unavailableProductAdapter.updateData(unavailableProducts);
//                    binding.unavailableProductsSection.setVisibility(
//                            unavailableProducts.isEmpty() ? View.GONE : View.VISIBLE
//                    );
//                }
//            }
//        }
//
//        @Override
//        public void onFailure(@NonNull Call<ProductResponse> call, @NonNull Throwable t) {
//            binding.swipeRefresh.setRefreshing(false);
//            Toasty.error(requireContext(), "Error loading products: " + t.getMessage(),
//                    Toast.LENGTH_SHORT).show();
//        }
//    });
//}
    private void loadProducts() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ProductResponse> call = apiService.getProducts();

        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProductResponse> call, @NonNull Response<ProductResponse> response) {
                binding.swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> allProducts = response.body().getProducts();
                    List<Product> availableProducts = new ArrayList<>();
                    unavailableProducts.clear();

                    // Pisahkan produk dengan status "available" DAN stock > 0
                    for (Product product : allProducts) {
                        if ("available".equalsIgnoreCase(product.getStatus()) && product.getMainStock() > 0) {
                            availableProducts.add(product);
                        } else {
                            unavailableProducts.add(product);
                        }
                    }

                    // Update adapter tanpa membuat instance baru
                    productList = availableProducts;
                    productAdapter.updateData(productList); // <-- Gunakan updateData()

                    // Update kategori
                    updateCategories(productList);

                    // Update produk tidak tersedia
                    unavailableProductAdapter.updateData(unavailableProducts);
                    binding.unavailableProductsSection.setVisibility(
                            unavailableProducts.isEmpty() ? View.GONE : View.VISIBLE
                    );
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProductResponse> call, @NonNull Throwable t) {
                binding.swipeRefresh.setRefreshing(false);
                Toasty.error(requireContext(), "Error loading products: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

//    private void updateCategories(List<Product> products) {
//        Set<String> categories = new HashSet<>();
//        for (Product product : products) {
//            categories.add(product.getCategory());
//        }
//
//        binding.categoryChipGroup.removeAllViews();
//
//        // Add "All" category
//        Chip allChip = new Chip(requireContext());
//        allChip.setText("All");
//        allChip.setCheckable(true);
//        allChip.setChecked(true);
//        binding.categoryChipGroup.addView(allChip);
//
//        // Add other categories
//        for (String category : categories) {
//            Chip chip = new Chip(requireContext());
//            chip.setText(category);
//            chip.setCheckable(true);
//            binding.categoryChipGroup.addView(chip);
//        }
//
//        // Handle category selection
//        binding.categoryChipGroup.setOnCheckedChangeListener((group, checkedId) -> {
//            Chip selectedChip = group.findViewById(checkedId);
//            filterProducts(selectedChip != null ? selectedChip.getText().toString() : "All");
//        });
//    }
//    private void updateCategories(List<Product> products) {
//        Set<String> categories = new HashSet<>();
//        for (Product product : products) {
//            categories.add(product.getCategory());
//        }
//
//        binding.categoryChipGroup.removeAllViews();
//
//        // Chip "All"
//        Chip allChip = new Chip(requireContext());
//        allChip.setText("All");
//        allChip.setChipIconResource(R.drawable.ic_all); // Ikon untuk "All"
//        allChip.setChipIconVisible(true);
//        allChip.setCheckable(true);
//        allChip.setChecked(true);
//        binding.categoryChipGroup.addView(allChip);
//
//        // Chip kategori lainnya
//        for (String category : categories) {
//            Chip chip = new Chip(requireContext());
//            chip.setText(category);
//
//            // Debug: Log normalized category name
//            String normalized = category.toLowerCase()
//                    .replaceAll("\\s+", "")
//                    .replace("-", "")
//                    .replace("_", "")
//                    .trim();
//            Log.d("CategoryDebug", "Original: " + category + " | Normalized: " + normalized);
//
//            int iconRes = CategoryIconMapper.getCategoryIcon(category);
//            chip.setChipIconResource(iconRes);
//
//            chip.setCheckable(true);
//            binding.categoryChipGroup.addView(chip);
//        }
//
//        // Handle pemilihan kategori
//        binding.categoryChipGroup.setOnCheckedChangeListener((group, checkedId) -> {
//            Chip selectedChip = group.findViewById(checkedId);
//            filterProducts(selectedChip != null ? selectedChip.getText().toString() : "All");
//        });
//    }
    private void updateCategories(List<Product> products) {
        Set<String> categories = new HashSet<>();
        for (Product product : products) {
            categories.add(product.getCategory());
        }

        List<String> categoryList = new ArrayList<>(categories);

        // Batasi maksimal 8 item untuk 2 baris (4x2)
        if (categoryList.size() > 9) {
            categoryList = categoryList.subList(0, 8);
        }
        categoryList.add(0, "All"); // Add "All" category at the beginning


        // Setup RecyclerView
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 4);
        binding.categoryRecyclerView.addItemDecoration(new GridSpacingItemDecoration(4, 16, true));
        binding.categoryRecyclerView.setLayoutManager(layoutManager);
        CategoryChipAdapter adapter = new CategoryChipAdapter(categoryList, new CategoryChipAdapter.OnCategoryClickListener() {
            @Override
            public void onCategoryClick(String category) {
                filterProducts(category);
            }
        });

        binding.categoryRecyclerView.setAdapter(adapter);
    }

    private void filterProducts(String category) {
        if (category.equals("All")) {
            productAdapter.updateData(productList);
            return;
        }

        List<Product> filteredList = new ArrayList<>();
        for (Product product : productList) {
            if (product.getCategory().equals(category)) {
                filteredList.add(product);
            }
        }
        productAdapter.updateData(filteredList);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (bannerAdapter != null && bannerAdapter.getItemCount() > 0) {
            sliderHandler.postDelayed(sliderRunnable, SLIDE_DELAY);
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        sliderHandler.removeCallbacks(sliderRunnable);
//        binding = null;
//    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        sliderHandler.removeCallbacks(sliderRunnable);
        binding = null;
    }


    private void setupImageLoading() {
        if (!supportsHardwareAcceleration()) {
            // Use software rendering if hardware acceleration isn't supported
            getActivity().getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        }
    }

    private boolean supportsHardwareAcceleration() {
        ActivityManager activityManager = (ActivityManager) requireContext().getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo configInfo = activityManager.getDeviceConfigurationInfo();
        return configInfo.reqGlEsVersion >= 0x20000;
    }

//    public interface VariantSelectionListener {
//        void onVariantSelected(Product product, ProductVariant variant);
//    }


//    private void navigateToProductDetail(Product product) {
//        Bundle bundle = new Bundle();
//        bundle.putInt("product_id", product.getId());
//        NavOptions navOptions = new NavOptions.Builder()
//                .setLaunchSingleTop(true)
//                .setPopUpTo(R.id.navigation_home, false)
//                .build();
//        Navigation.findNavController(requireView())
//                .navigate(R.id.navigation_product_detail, bundle, navOptions);
//    }
    private void navigateToProductDetail(Product product) {
        Bundle bundle = new Bundle();
        bundle.putInt("product_id", product.getId());

        // Update NavOptions - jangan hapus home dari back stack
        NavOptions navOptions = new NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setPopUpTo(R.id.navigation_home, false, true) // false agar home tetap ada di back stack
                .build();

        Navigation.findNavController(requireView())
                .navigate(R.id.navigation_product_detail, bundle, navOptions);
    }
}