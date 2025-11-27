package com.example.myapplication2;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Dialog;

import com.bumptech.glide.Glide;
import com.example.myapplication2.api.SizeOption;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

public class MenuItemDialogFragment extends DialogFragment {

    private static final String ARG_ID = "id";
    private static final String ARG_NAME = "name";
    private static final String ARG_IMAGE_URL = "image_url";
    private static final String ARG_SIZES = "sizes";

    public static MenuItemDialogFragment newInstance(int id, String name, String imageUrl, List<SizeOption> sizes) {
        MenuItemDialogFragment fragment = new MenuItemDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ID, id);
        args.putString(ARG_NAME, name);
        args.putString(ARG_IMAGE_URL, imageUrl);
        args.putSerializable(ARG_SIZES, new java.util.ArrayList<>(sizes));
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_menu_item, container, false);

        ImageView image = view.findViewById(R.id.itemImage);
        TextView name = view.findViewById(R.id.itemName);
        ChipGroup sizeGroup = view.findViewById(R.id.sizeGroup);
        Button addBtn = view.findViewById(R.id.addButton);
        Button closeBtn = view.findViewById(R.id.closeButton);

        if (getArguments() != null) {
            int id = getArguments().getInt(ARG_ID, -1);
            String itemName = getArguments().getString(ARG_NAME, "UNKNOWN");
            String imageUrl = getArguments().getString(ARG_IMAGE_URL, "");
            List<SizeOption> sizes = (List<SizeOption>) getArguments().getSerializable(ARG_SIZES);

            Log.d("DIALOG_DEBUG", "Dialog opened for item: " + itemName + " (ID=" + id + ")");
            Log.d("DIALOG_DEBUG", "Image URL: " + imageUrl);
            Log.d("DIALOG_DEBUG", "Sizes received: " + (sizes != null ? sizes.size() : 0));

            name.setText(itemName);

            // Load image with Glide
            Glide.with(requireContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(image);

            // Populate size chips dynamically
            if (sizes != null) {
                for (SizeOption s : sizes) {
                    Chip chip = new Chip(requireContext());

                    // Replace "Default" with more readable label
                    String displaySize = s.size.equalsIgnoreCase("Default") ? "Regular" : s.size;

                    chip.setText(displaySize + " - ₱" + String.format("%.2f", s.price));
                    chip.setCheckable(true);
                    chip.setChipBackgroundColorResource(R.color.chip_selector);
                    chip.setTextColor(getResources().getColor(R.color.chip_text));
                    chip.setTag(s);

                    sizeGroup.addView(chip);
                }

                // Optional: select first size by default
                if (sizeGroup.getChildCount() > 0) {
                    ((Chip) sizeGroup.getChildAt(0)).setChecked(true);
                }
            }

            addBtn.setOnClickListener(v -> {
                int checkedId = sizeGroup.getCheckedChipId();
                if (checkedId == View.NO_ID) {
                    Toast.makeText(getContext(), "Please select a size", Toast.LENGTH_SHORT).show();
                    return;
                }

                Chip selectedChip = view.findViewById(checkedId);
                SizeOption chosen = (SizeOption) selectedChip.getTag();

                // ✅ Use context-aware CartManager
                CartManager cartManager = CartManager.getInstance(requireContext());
                cartManager.addItem(new CartItem(
                        chosen.item_id,
                        itemName,
                        chosen.price,
                        1
                ));

                Toast.makeText(getContext(), "Added to cart", Toast.LENGTH_SHORT).show();
                dismiss();
            });

        } else {
            Log.e("DIALOG_DEBUG", "getArguments() is null!");
        }

        closeBtn.setOnClickListener(v -> dismiss());
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
