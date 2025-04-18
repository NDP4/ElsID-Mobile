package com.mobile2.uts_elsid.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mobile2.uts_elsid.R;
import com.mobile2.uts_elsid.model.Product;
import com.mobile2.uts_elsid.model.ProductVariant;
import com.mobile2.uts_elsid.utils.CurrencyFormatter;

public class VariantSelectionBottomSheet extends BottomSheetDialogFragment {
    private Product product;
    private VariantSelectionListener listener;
    private ProductVariant selectedVariant;

    public interface VariantSelectionListener {
        void onVariantSelected(Product product, ProductVariant variant);
    }

    public VariantSelectionBottomSheet(Product product, VariantSelectionListener listener) {
        this.product = product;
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_variant_selection, container, false);

        TextView titleText = view.findViewById(R.id.titleText);
        RadioGroup variantsGroup = view.findViewById(R.id.variantsGroup);
        TextView selectedPriceText = view.findViewById(R.id.selectedPriceText);
        Button confirmButton = view.findViewById(R.id.confirmButton);

        // Check if product has variants
        if (product.getVariants() == null || product.getVariants().isEmpty()) {
            titleText.setText("No variants available");
            confirmButton.setEnabled(false);  // Disable the confirm button if no variants
            return view;  // Return early if no variants exist
        }

        titleText.setText("Select Variant");

        // Add radio buttons for each variant
        for (ProductVariant variant : product.getVariants()) {
            RadioButton radioButton = new RadioButton(requireContext());
            radioButton.setText(variant.getName());
            radioButton.setTag(variant);
            variantsGroup.addView(radioButton);
        }

        // Automatically select the first variant
        if (!product.getVariants().isEmpty()) {
            RadioButton firstButton = (RadioButton) variantsGroup.getChildAt(0);
            firstButton.setChecked(true); // Auto-select the first variant
            selectedVariant = (ProductVariant) firstButton.getTag(); // Set selected variant
            double finalPrice = selectedVariant.getPrice() * (1 - selectedVariant.getDiscount());
            selectedPriceText.setText(CurrencyFormatter.format(finalPrice));
        }

        // Handle variant selection
        variantsGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedButton = group.findViewById(checkedId);
            if (selectedButton != null) {
                selectedVariant = (ProductVariant) selectedButton.getTag();
                double finalPrice = selectedVariant.getPrice() * (1 - selectedVariant.getDiscount());
                selectedPriceText.setText(CurrencyFormatter.format(finalPrice));
            }
        });

        // Confirm button click listener
        confirmButton.setOnClickListener(v -> {
            if (selectedVariant != null) {
                listener.onVariantSelected(product, selectedVariant);
                dismiss();
            }
        });

        return view;
    }
}
