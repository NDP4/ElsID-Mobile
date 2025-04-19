package com.mobile2.uts_elsid.ui.home;

import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayoutMediator;
import com.mobile2.uts_elsid.R;
import com.mobile2.uts_elsid.adapter.ImageSliderAdapter;
import com.mobile2.uts_elsid.adapter.ProductAdapter;
import com.mobile2.uts_elsid.api.ApiClient;
import com.mobile2.uts_elsid.api.ApiService;
import com.mobile2.uts_elsid.api.ProductDetailResponse;
import com.mobile2.uts_elsid.api.ProductResponse;
import com.mobile2.uts_elsid.databinding.FragmentProductDetailBinding;
import com.mobile2.uts_elsid.model.Cart;
import com.mobile2.uts_elsid.model.Product;
import com.mobile2.uts_elsid.model.ProductVariant;
import com.mobile2.uts_elsid.utils.CartManager;
import com.mobile2.uts_elsid.utils.NotificationHelper;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailFragment extends Fragment {

//    private static final String BASE_URL = "https://mobile2.ndp.my.id/";

    private FragmentProductDetailBinding binding;
    private CartManager cartManager;
    private Product product;
    private int productId;
    private ProductVariant selectedVariant;

//    @Override
    public void onCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set the status bar color to transparent
        WindowCompat.setDecorFitsSystemWindows(requireActivity().getWindow(), false);

        // Set the status bar color to transparent
        requireActivity().getWindow().setStatusBarColor(getResources().getColor(android.R.color.transparent));

        binding = FragmentProductDetailBinding.bind(view);
    }
    private void setupVariants(List<ProductVariant> variants) {
        if (variants == null || variants.isEmpty()) {
            binding.variantsLabel.setVisibility(View.GONE);
            binding.variantsGroup.setVisibility(View.GONE);
            selectedVariant = null;
            return;
        }

        binding.variantsLabel.setVisibility(View.VISIBLE);
        binding.variantsGroup.setVisibility(View.VISIBLE);
        binding.variantsGroup.removeAllViews();

//        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

        NumberFormat formatter = getCurrencyFormatter();
        for (ProductVariant variant : variants) {
            RadioButton radioButton = new RadioButton(requireContext());
            double variantFinalPrice = variant.getPrice() * (1 - variant.getDiscount()/100.0);
            String priceText = formatter.format(variantFinalPrice);
            radioButton.setText(variant.getName() + " - " + priceText);
            radioButton.setTag(variant);
            binding.variantsGroup.addView(radioButton);
        }

        binding.variantsGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedButton = group.findViewById(checkedId);
            if (selectedButton != null) {
                selectedVariant = (ProductVariant) selectedButton.getTag();
            } else {
                selectedVariant = null;
            }
            updateFinalPrice();
        });
    }

    private void updateFinalPrice() {
        if (binding == null || product == null) return;

//        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        NumberFormat formatter = getCurrencyFormatter();
        double priceToUse;
        double discountToUse;

        if (selectedVariant != null) {
            // Use variant price and discount if a variant is selected
            priceToUse = selectedVariant.getPrice();
            discountToUse = selectedVariant.getDiscount();
        } else {
            // Use main product price and discount if no variant is selected
            priceToUse = product.getPrice();
            discountToUse = product.getDiscount();
        }

        double finalPrice = priceToUse * (1 - discountToUse/100.0);

        // Update UI elements
        binding.finalPriceText.setText(formatter.format(finalPrice));

        // Update total price to match the final price
//        binding.totalPriceText.setText(formatter.format(finalPrice));

        // Show/hide original price and discount text
        if (discountToUse > 0) {
            binding.originalPriceText.setVisibility(View.VISIBLE);
            binding.originalPriceText.setText(formatter.format(priceToUse));
            binding.originalPriceText.setPaintFlags(binding.originalPriceText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            binding.discountText.setVisibility(View.VISIBLE);
            binding.discountText.setText("-" + (int)discountToUse + "%");
        } else {
            binding.originalPriceText.setVisibility(View.GONE);
            binding.discountText.setVisibility(View.GONE);
        }
    }



    private double selectedPrice = 0;

    private void updatePriceForVariant(ProductVariant variant) {
        if (variant != null) {
            double variantPrice = variant.getPrice();
            double variantDiscount = variant.getDiscount();

            // Calculate discounted price for variant
            double discountAmount = variantPrice * (variantDiscount / 100);
            double finalPrice = variantPrice - discountAmount;

            // Format prices
            String formattedOriginalPrice = String.format("Rp %,.0f", variantPrice);
            String formattedFinalPrice = String.format("Rp %,.0f", finalPrice);
            String formattedDiscount = String.format("%.0f%%", variantDiscount);

            // Update UI
            if (variantDiscount > 0) {
                binding.originalPriceText.setVisibility(View.VISIBLE);
                binding.originalPriceText.setText(formattedOriginalPrice);
                binding.originalPriceText.setPaintFlags(binding.originalPriceText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                binding.discountText.setVisibility(View.VISIBLE);
                binding.discountText.setText("-" + formattedDiscount);
            } else {
                binding.originalPriceText.setVisibility(View.GONE);
                binding.discountText.setVisibility(View.GONE);
            }

            binding.mainPriceText.setText(formattedFinalPrice);
            binding.finalPriceText.setText(formattedFinalPrice);
        }
    }

    private void setupVariantSelection(List<ProductVariant> variants) {
        RadioGroup variantsGroup = binding.variantsGroup;
        variantsGroup.removeAllViews();

        if (variants != null && !variants.isEmpty()) {
            binding.variantsLabel.setVisibility(View.VISIBLE);
            variantsGroup.setVisibility(View.VISIBLE);

            for (ProductVariant variant : variants) {
                RadioButton radioButton = new RadioButton(requireContext());
                radioButton.setText(variant.getName());
                radioButton.setTag(variant); // Store variant object as tag
                variantsGroup.addView(radioButton);
            }

            // Select first variant by default
            if (variantsGroup.getChildCount() > 0) {
                RadioButton firstVariant = (RadioButton) variantsGroup.getChildAt(0);
                firstVariant.setChecked(true);
                updatePriceForVariant((ProductVariant) firstVariant.getTag());
            }

            // Listen for variant selection changes
            variantsGroup.setOnCheckedChangeListener((group, checkedId) -> {
                RadioButton selectedButton = group.findViewById(checkedId);
                if (selectedButton != null) {
                    ProductVariant selectedVariant = (ProductVariant) selectedButton.getTag();
                    updatePriceForVariant(selectedVariant);
                }
            });
        } else {
            binding.variantsLabel.setVisibility(View.GONE);
            variantsGroup.setVisibility(View.GONE);

            // Show main product price if no variants
            double mainPrice = product.getPrice();
            double mainDiscount = product.getDiscount();
            double finalPrice = mainPrice - (mainPrice * mainDiscount / 100);
            binding.mainPriceText.setText(String.format("Rp %,.0f", finalPrice));
            binding.finalPriceText.setText(String.format("Rp %,.0f", finalPrice));
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProductDetailBinding.inflate(inflater, container, false);
        cartManager = new CartManager(requireContext());

        TextView descriptionText = binding.descriptionText;
        TextView seeMoreText = binding.seeMoreButton;
        final boolean[] isExpanded = {false};

        seeMoreText.setOnClickListener(v -> {
            if (isExpanded[0]) {
                descriptionText.setMaxLines(3);
                seeMoreText.setText("See More");
            } else {
                descriptionText.setMaxLines(Integer.MAX_VALUE);
                seeMoreText.setText("See Less");
            }
            isExpanded[0] = !isExpanded[0];
        });

        // Get product ID from arguments
        if (getArguments() != null) {
            productId = getArguments().getInt("product_id", -1);
            if (productId != -1) {
                loadProductDetails(productId);
            }
        }

        // Set click listener untuk add to cart
        binding.addToCartButton.setOnClickListener(v -> {
            addToCart();
        });

        setupRecommendations();

        return binding.getRoot();
    }



    private void loadProductDetails(int productId) {
        showLoading();
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        // Add debug logging for URL
        String fullUrl = "https://mobile2.ndp.my.id/product_elsid.php?id=" + productId;
        Log.d("ProductDetail", "Making request to: " + fullUrl);
        Call<ProductDetailResponse> call = apiService.getProductDetail(String.valueOf(productId));
//        String url = ApiClient.BASE_URL + "product/" + productId;


        // log
        Log.d("ProductDetail","Loading product with ID: " + productId);
//        Log.d("ProductDetail","URL: " + url);

        call.enqueue(new Callback<ProductDetailResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProductDetailResponse> call, @NonNull Response<ProductDetailResponse> response) {
                Log.d("ProductDetail", "Response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    ProductDetailResponse detailResponse = response.body();
                    Log.d("ProductDetail", "Status: " + detailResponse.getStatus());

                    // Tambahan logging untuk debug
                    if (detailResponse.getProduct() != null) {
                        Product product = detailResponse.getProduct();
                        Log.d("ProductDetail", "Product data: " +
                                "\nTitle: " + product.getTitle() +
                                "\nDescription: " + product.getDescription() +
                                "\nImages: " + (product.getImages() != null ? product.getImages().size() : "null") +
                                "\nPrice: " + product.getPrice()
                        );
                    } else {
                        Log.d("ProductDetail", "Product is null");
                    }

                    if (detailResponse.getStatus() == 1 && detailResponse.getProduct() != null) {
                        Product product = detailResponse.getProduct();
                        updateUI(product);
                    } else {
                        showError("Failed to load product details: Status=" + detailResponse.getStatus());
                        Toasty.error(requireContext(), "Failed to load product details", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e("ProductDetail", "Error response: " + errorBody);
                        showError("Failed to load product details: " + errorBody);
                    } catch (IOException e) {
                        Log.e("ProductDetail", "Error reading error body", e);
                        showError("Failed to load product details: Unknown error");
                    }
                }
            }



            @Override
            public void onFailure(@NonNull Call<ProductDetailResponse> call, @NonNull Throwable t) {
                binding.loadingIndicator.setVisibility(View.GONE);
                showError("Failed to load product details"+ ": " + t.getMessage());
                Log.e("ProductDetail", "Error: " + t.getMessage());
                Toasty.error(requireContext(), "Error loading product details: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
//        setupVariants(product.getVariants());
//        updateFinalPrice();

    }

    private void updateUI(Product product) {
        Log.d("ProductDetail", "Updating UI with product data");
        showContent();

        // simpan reference ke product
        this.product = product;

        if (binding == null) {
            Log.e("ProductDetail", "Binding is null!");
            return;
        }

        try {
            // Set basic info
            binding.titleText.setText(product.getTitle());
            binding.categoryText.setText(product.getCategory());
            binding.descriptionText.setText(product.getDescription());

            setupVariants(product.getVariants());
            updateFinalPrice();

            // Format currency
//            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
            NumberFormat formatter = getCurrencyFormatter();

            // Handle price and discount
            double finalPrice = product.getPrice() * (1 - product.getDiscount()/100.0);
            binding.mainPriceText.setText(formatter.format(finalPrice));

            if (product.getDiscount() > 0) {
                binding.originalPriceText.setVisibility(View.VISIBLE);
                binding.originalPriceText.setText(formatter.format(product.getPrice()));
                binding.originalPriceText.setPaintFlags(binding.originalPriceText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                binding.discountText.setVisibility(View.VISIBLE);
                binding.discountText.setText("-" + (int)product.getDiscount() + "%");
            } else {
                binding.originalPriceText.setVisibility(View.GONE);
                binding.discountText.setVisibility(View.GONE);
            }

            // Show stock
            binding.stockText.setText("Stock: " + product.getMainStock());

            // Handle variants
            if (product.getVariants() != null && !product.getVariants().isEmpty()) {
                binding.variantsLabel.setVisibility(View.VISIBLE);
                binding.variantsGroup.removeAllViews();

                for (ProductVariant variant : product.getVariants()) {
                    RadioButton radioButton = new RadioButton(requireContext());
                    radioButton.setTag(variant); // Store variant object in tag
                    double variantFinalPrice = variant.getPrice() * (1 - variant.getDiscount()/100.0);
                    String variantText = String.format("%s (Stock: %d)",
                            variant.getName(),
                            variant.getStock());
                    radioButton.setText(variantText);
                    binding.variantsGroup.addView(radioButton);
                }

                // Add radio group change listener
                binding.variantsGroup.setOnCheckedChangeListener((group, checkedId) -> {
                    RadioButton selectedButton = group.findViewById(checkedId);
                    if (selectedButton != null) {
                        ProductVariant variant = (ProductVariant) selectedButton.getTag();
                        updatePriceDisplay(variant.getPrice(), variant.getDiscount());
                    } else {
                        // If no variant is selected, show main product price
                        updatePriceDisplay(product.getPrice(), product.getDiscount());
                    }
                });
            } else {
                binding.variantsLabel.setVisibility(View.GONE);
            }

            // Setup image slider
            if (product.getImages() != null && !product.getImages().isEmpty()) {
                ImageSliderAdapter imageAdapter = new ImageSliderAdapter(requireContext());
                List<String> fullUrls = product.getImages().stream()
                        .map(img -> !img.startsWith("http") ? "https://mobile2.ndp.my.id/" + img : img)
                        .collect(Collectors.toList());
                imageAdapter.setImages(fullUrls);

                binding.imageSlider.setAdapter(imageAdapter);
                TabLayoutMediator mediator = new TabLayoutMediator(
                        binding.imageIndicator,
                        binding.imageSlider,
                        (tab, position) -> {}
                );
                mediator.attach();
            }



        } catch (Exception e) {
            Log.e("ProductDetail", "Error updating UI: " + e.getMessage());
            showError("Error displaying product details");
        }
        // Update the final price display
        // Format currency
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        double finalPrice = product.getPrice() * (1 - product.getDiscount()/100.0);
        binding.finalPriceText.setText(formatter.format(finalPrice));
    }

    private void updatePriceDisplay(double price, double discount) {
//        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        NumberFormat formatter = getCurrencyFormatter();

        // Calculate final price
        double finalPrice = price * (1 - discount/100.0);

        if (discount > 0) {
            // Show original price with strikethrough
            binding.originalPriceText.setVisibility(View.VISIBLE);
            binding.originalPriceText.setText(formatter.format(price));
            binding.originalPriceText.setPaintFlags(binding.originalPriceText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            // Show discounted price
            binding.mainPriceText.setText(formatter.format(finalPrice));

            // Show discount percentage
            binding.discountText.setVisibility(View.VISIBLE);
            binding.discountText.setText("-" + (int)discount + "%");
        } else {
            // No discount case
            binding.mainPriceText.setText(formatter.format(price));
            binding.originalPriceText.setVisibility(View.GONE);
            binding.discountText.setVisibility(View.GONE);
        }

        // Always update the final price display
        binding.finalPriceText.setText(formatter.format(finalPrice));
//        binding.totalPriceText.setText(formatter.format(finalPrice));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        WindowCompat.setDecorFitsSystemWindows(requireActivity().getWindow(), true);
        requireActivity().getWindow().setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.primary_dark));
        binding = null;
    }

    // Di ProductDetailFragment.java
    private void showLoading() {
        if (binding != null) {
            binding.loadingIndicator.setVisibility(View.VISIBLE);
            binding.contentLayout.setVisibility(View.GONE);
            binding.errorText.setVisibility(View.GONE);
        }
    }

    private void showContent() {
        if (binding != null) {
            binding.loadingIndicator.setVisibility(View.GONE);
            binding.contentLayout.setVisibility(View.VISIBLE);
            binding.errorText.setVisibility(View.GONE);
        }
    }

    private void showError(String message) {
        if (binding != null) {
            binding.loadingIndicator.setVisibility(View.GONE);
            binding.contentLayout.setVisibility(View.GONE);
            binding.errorText.setVisibility(View.VISIBLE);
            binding.errorText.setText(message);
        }
    }

    private void addToCart() {
        if (product == null) return;

        // Get selected variant if exists
        ProductVariant selectedVariant = null;
        if (binding.variantsGroup.getCheckedRadioButtonId() != -1) {
            RadioButton selectedButton = binding.variantsGroup.findViewById(binding.variantsGroup.getCheckedRadioButtonId());
            selectedVariant = (ProductVariant) selectedButton.getTag();
        }

        // Get price, stock and discount info based on selection
        double price;
        int stock;
        String variantName = "";
        double discount;

        if (selectedVariant != null) {
            // Use variant details
            price = selectedVariant.getPrice();
            stock = selectedVariant.getStock();
            variantName = selectedVariant.getName();
            discount = selectedVariant.getDiscount();
        } else {
            // Use main product details
            price = product.getPrice();
            stock = product.getMainStock();
            discount = product.getDiscount();
        }

        // Get first image URL
        String imageUrl = product.getImages() != null && !product.getImages().isEmpty()
                ? product.getImages().get(0)
                : "";

        // Create cart item with original price and discount
        Cart cartItem = new Cart(
                product.getId(),
                product.getTitle(),
                imageUrl,
                price,    // Pass original price, not final price
                1,       // Initial quantity
                stock,
                variantName,
                discount  // Pass discount percentage
        );

        // Add to cart using CartManager
        cartManager.addToCart(cartItem);

        // Show success message
        Toasty.success(requireContext(),
                "Added " + product.getTitle() + " to cart",
                Toast.LENGTH_SHORT).show();

        // Show notification
        // Request notification permission for Android 13+ (API 33+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        1);
            }
        }
        String productName = product.getTitle();
        String notificationTittle = "Added to Cart";
        String notificationMessage = productName + " has been added to your cart";

        NotificationHelper notificationHelper = new NotificationHelper(requireContext());
        notificationHelper.showCartNotification(notificationTittle, notificationMessage);
    }

    private NumberFormat getCurrencyFormatter() {
//        return NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        formatter.setMinimumFractionDigits(0);
        return formatter;
    }

    // logika untuk enable dan disable button
    private void updateAddToCartButton(int stock) {
        MaterialButton addToCartButton = binding.addToCartButton;

        if (stock > 0) {
            addToCartButton.setEnabled(true);
            addToCartButton.setText("Add to Cart");
            addToCartButton.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), R.color.primary)));
        } else {
            addToCartButton.setEnabled(false);
            addToCartButton.setText("Out of Stock");
            addToCartButton.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), R.color.text_secondary)));
        }
    }

    private void setupRecommendations() {
        // Sembunyikan dulu sampai data siap
        binding.recommendationsSection.setVisibility(View.GONE);

        // Gunakan LinearLayoutManager horizontal
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );
        binding.recommendationsRecyclerView.setLayoutManager(layoutManager);

        ProductAdapter adapter = new ProductAdapter(requireContext(), new ArrayList<>());
        adapter.setOnProductClickListener(product -> {
            // Navigasi ke ProductDetailFragment
            Bundle bundle = new Bundle();
            bundle.putInt("product_id", product.getId());

            NavController navController = Navigation.findNavController(requireActivity(), R.id.navigation_product_detail);
            navController.navigate(R.id.navigation_product_detail, bundle);
        });

        binding.recommendationsRecyclerView.setAdapter(adapter);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getProducts().enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> allProducts = response.body().getProducts();

                    // Filter dan urutkan produk
                    List<Product> recommendations = new ArrayList<>();
                    for (Product product : allProducts) {
                        if (product.getDiscount() > 0) {
                            recommendations.add(product);
                        }
                    }

                    // Urutkan berdasarkan diskon tertinggi
                    Collections.sort(recommendations, (p1, p2) ->
                            Double.compare(p2.getDiscount(), p1.getDiscount()));

                    // Ambil maksimal 3 item
                    if (recommendations.size() > 3) {
                        recommendations = recommendations.subList(0, 3);
                    }

                    if (!recommendations.isEmpty()) {
                        // Tampilkan section rekomendasi
                        binding.recommendationsSection.setVisibility(View.VISIBLE);

                        // Set adapter
                        ProductAdapter adapter = new ProductAdapter(requireContext(), recommendations);
                        binding.recommendationsRecyclerView.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                Log.e("Recommendations", "Error loading recommendations: " + t.getMessage());
                binding.recommendationsSection.setVisibility(View.GONE);
            }
        });
    }

}