package com.assignment4.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.agniment22.R;
import com.assignment4.adapters.ProductAdapter;
import com.assignment4.models.Product;
import com.assignment4.utils.NetworkConfig;
import com.assignment4.utils.VolleySingleton;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ProductFragment extends Fragment implements ProductFormFragment.OnProductCreatedListener {
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> productList;
    private MaterialButton btnToggleForm;
    private FloatingActionButton fabAddProduct;
    private ProductFormFragment formFragment;
    private boolean isFormVisible = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewProducts);
        btnToggleForm = view.findViewById(R.id.btnToggleForm);
        fabAddProduct = view.findViewById(R.id.fabAddProduct);

        recyclerView.setVisibility(View.VISIBLE);

        productList = new ArrayList<>();
        adapter = new ProductAdapter(productList, this::showProductDetails);
        adapter.setOnItemActionListener(new ProductAdapter.OnItemActionListener() {
            @Override
            public void onViewDetailClick(Product product) {
                showProductDetailModal(product);
            }

            @Override
            public void onEditClick(Product product) {
                editProduct(product);
            }

            @Override
            public void onDeleteClick(Product product) {
                confirmDeleteProduct(product);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        btnToggleForm.setOnClickListener(v -> toggleFormFragment());
        fabAddProduct.setOnClickListener(v -> toggleFormFragment());

        FragmentManager fragmentManager = getChildFragmentManager();
        formFragment = (ProductFormFragment) fragmentManager.findFragmentById(R.id.fragmentContainer);

        if (formFragment == null) {
            formFragment = new ProductFormFragment();
            formFragment.setProductCreatedListener(this);
        }

        loadProducts();

        return view;
    }

    private void toggleFormFragment() {
        FragmentManager fragmentManager = getChildFragmentManager();
        FrameLayout fragmentContainer = getView().findViewById(R.id.fragmentContainer);

        if (!isFormVisible) {
            recyclerView.setVisibility(View.GONE);
            fragmentContainer.setVisibility(View.VISIBLE);

            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, formFragment)
                    .commit();
            isFormVisible = true;
            btnToggleForm.setText("Cancel");
            fabAddProduct.hide();
        } else {
            fragmentContainer.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            formFragment.clearEditMode();

            fragmentManager.beginTransaction()
                    .remove(formFragment)
                    .commit();
            isFormVisible = false;
            btnToggleForm.setText("Add Product");
            fabAddProduct.show();
        }
    }

    private void loadProducts() {
        String url = NetworkConfig.getProductsUrl(requireContext());

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loadProductsFromObjectResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = "Network error";
                        if (error.networkResponse != null) {
                            errorMessage = "Error: " + error.networkResponse.statusCode;
                        } else if (error.getMessage() != null) {
                            errorMessage = error.getMessage();
                        }
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show();
                    }
                });

        VolleySingleton.getInstance(requireContext()).getRequestQueue().add(request);
    }

    private void loadProductsFromObjectResponse(JSONObject response) {
        try {
            List<Product> products = new ArrayList<>();

            if (response.has("success") && response.getBoolean("success")) {
                if (response.has("data")) {
                    Object data = response.get("data");
                    if (data instanceof JSONArray) {
                        JSONArray dataArray = (JSONArray) data;
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject productObj = dataArray.getJSONObject(i);
                            Product product = Product.fromJson(productObj);
                            products.add(product);
                        }
                    }
                }
            }

            productList.clear();
            productList.addAll(products);
            adapter.updateProducts(productList);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Error parsing response: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void showProductDetails(Product product) {
        showProductDetailModal(product);
    }

    private void showProductDetailModal(Product product) {
        // Create custom dialog view
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_product_detail, null);

        // Find views
        ImageView imageView = dialogView.findViewById(R.id.dialogImageViewProduct);
        TextView nameTextView = dialogView.findViewById(R.id.dialogTextProductName);
        TextView priceTextView = dialogView.findViewById(R.id.dialogTextProductPrice);
        TextView categoryTextView = dialogView.findViewById(R.id.dialogTextProductCategory);
        TextView descriptionTextView = dialogView.findViewById(R.id.dialogTextProductDescription);
        TextView createdAtTextView = dialogView.findViewById(R.id.dialogTextCreatedAt);

        // Set product data
        nameTextView.setText(product.getName());
        priceTextView.setText(String.format("%,.0f RWF", product.getPrice()));

        if (product.getCategory() != null) {
            categoryTextView.setText(product.getCategory().getName());
        } else {
            categoryTextView.setText("Uncategorized");
        }

        if (product.getDescription() != null && !product.getDescription().isEmpty()) {
            descriptionTextView.setText(product.getDescription());
        } else {
            descriptionTextView.setText("No description available");
        }

        if (product.getCreatedAt() != null && !product.getCreatedAt().isEmpty()) {
            createdAtTextView.setText(product.getCreatedAt());
        } else {
            createdAtTextView.setText("Unknown");
        }

        // Load product image
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Glide.with(requireContext())
                    .load(product.getImageUrl())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(R.color.gray_light)
                    .error(R.color.gray_light)
                    .into(imageView);
        } else {
            imageView.setImageResource(android.R.color.transparent);
            imageView.setBackgroundResource(R.color.gray_light);
        }

        // Create and show dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // Handle close button
        dialogView.findViewById(R.id.dialogBtnClose).setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void editProduct(Product product) {
        if (!isFormVisible) {
            toggleFormFragment();
        }
        formFragment.setProductToEdit(product);
    }

    private void confirmDeleteProduct(Product product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Delete Product");
        builder.setMessage("Are you sure you want to delete \"" + product.getName() + "\"?");
        builder.setPositiveButton("Delete", (dialog, which) -> deleteProduct(product));
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void deleteProduct(Product product) {
        String url = NetworkConfig.getProductUrl(requireContext(), product.getId());

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.DELETE,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(requireContext(), "Product deleted successfully", Toast.LENGTH_SHORT).show();
                        loadProducts();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = "Failed to delete product";
                        if (error.networkResponse != null) {
                            errorMessage = "Error: " + error.networkResponse.statusCode;
                        }
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show();
                    }
                });

        VolleySingleton.getInstance(requireContext()).getRequestQueue().add(request);
    }

    @Override
    public void onProductCreated() {
        loadProducts();
        if (isFormVisible) {
            toggleFormFragment();
        }
    }
}

