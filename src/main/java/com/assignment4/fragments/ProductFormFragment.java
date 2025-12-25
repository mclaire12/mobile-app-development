package com.assignment4.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.agniment22.R;
import com.assignment4.models.Category;
import com.assignment4.models.Product;
import com.assignment4.utils.NetworkConfig;
import com.assignment4.utils.VolleySingleton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ProductFormFragment extends Fragment {
    private EditText editTextName;
    private EditText editTextPrice;
    private EditText editTextDescription;
    private EditText editTextImageUrl;
    private Spinner spinnerCategory;
    private Button buttonSubmit;
    private List<Category> categories;
    private OnProductCreatedListener listener;
    private Product productToEdit = null;
    private boolean isEditMode = false;

    public interface OnProductCreatedListener {
        void onProductCreated();
    }

    public void setProductCreatedListener(OnProductCreatedListener listener) {
        this.listener = listener;
    }

    public void setProductToEdit(Product product) {
        this.productToEdit = product;
        this.isEditMode = true;
        if (editTextName != null) {
            populateFormWithProduct();
        }
    }

    public void clearEditMode() {
        this.productToEdit = null;
        this.isEditMode = false;
        if (buttonSubmit != null) {
            buttonSubmit.setText(getString(R.string.submit));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_form, container, false);

        editTextName = view.findViewById(R.id.editTextName);
        editTextPrice = view.findViewById(R.id.editTextPrice);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        editTextImageUrl = view.findViewById(R.id.editTextImageUrl);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        buttonSubmit = view.findViewById(R.id.buttonSubmit);

        categories = new ArrayList<>();
        NetworkConfig.init(requireContext());
        loadCategories();

        buttonSubmit.setOnClickListener(v -> submitProduct());

        // If we're in edit mode, populate the form
        if (isEditMode && productToEdit != null) {
            populateFormWithProduct();
        }

        return view;
    }

    private void populateFormWithProduct() {
        if (productToEdit == null)
            return;

        editTextName.setText(productToEdit.getName());
        editTextPrice.setText(String.valueOf(productToEdit.getPrice()));
        if (productToEdit.getDescription() != null) {
            editTextDescription.setText(productToEdit.getDescription());
        }
        if (productToEdit.getImageUrl() != null) {
            editTextImageUrl.setText(productToEdit.getImageUrl());
        }

        // Set category spinner selection
        if (productToEdit.getCategory() != null && categories != null) {
            for (int i = 0; i < categories.size(); i++) {
                if (categories.get(i).getId() == productToEdit.getCategory().getId()) {
                    spinnerCategory.setSelection(i);
                    break;
                }
            }
        }

        buttonSubmit.setText("Update Product");
    }

    private void loadCategories() {
        String url = NetworkConfig.getCategoriesUrl(requireContext());

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            List<Category> categoryList = new ArrayList<>();

                            // Handle API response: { success: true, data: [...] }
                            if (response.has("success") && response.getBoolean("success")) {
                                if (response.has("data") && response.get("data") instanceof JSONArray) {
                                    JSONArray dataArray = response.getJSONArray("data");
                                    for (int j = 0; j < dataArray.length(); j++) {
                                        JSONObject catObj = dataArray.getJSONObject(j);
                                        Category category = Category.fromJson(catObj);
                                        categoryList.add(category);
                                    }
                                }
                            }

                            categories.clear();
                            categories.addAll(categoryList);

                            ArrayAdapter<Category> adapter = new ArrayAdapter<>(
                                    requireContext(),
                                    android.R.layout.simple_spinner_item,
                                    categories);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerCategory.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(requireContext(),
                                    "Error loading categories: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = "Failed to load categories";
                        if (error.getMessage() != null) {
                            errorMessage = error.getMessage();
                        }
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show();
                    }
                });

        VolleySingleton.getInstance(requireContext()).getRequestQueue().add(request);
    }

    private void submitProduct() {
        String name = editTextName.getText().toString().trim();
        String priceStr = editTextPrice.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();

        if (name.isEmpty()) {
            editTextName.setError(getString(R.string.required_field));
            return;
        }

        if (priceStr.isEmpty()) {
            editTextPrice.setError(getString(R.string.required_field));
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
            if (price < 0) {
                editTextPrice.setError(getString(R.string.invalid_price));
                return;
            }
        } catch (NumberFormatException e) {
            editTextPrice.setError(getString(R.string.invalid_price));
            return;
        }

        if (spinnerCategory.getSelectedItem() == null) {
            Toast.makeText(requireContext(), getString(R.string.select_category), Toast.LENGTH_SHORT).show();
            return;
        }

        Category selectedCategory = (Category) spinnerCategory.getSelectedItem();
        int categoryId = selectedCategory.getId();

        try {
            JSONObject productJson = new JSONObject();
            productJson.put("name", name);
            productJson.put("price", price);
            productJson.put("category_id", categoryId);
            if (!description.isEmpty()) {
                productJson.put("description", description);
            }

            // Add image URL if provided
            String imageUrl = editTextImageUrl.getText().toString().trim();
            if (!imageUrl.isEmpty()) {
                productJson.put("image_url", imageUrl);
            }

            // Determine if we're creating or updating
            int method = isEditMode ? Request.Method.PUT : Request.Method.POST;
            String url = isEditMode ? NetworkConfig.getProductUrl(requireContext(), productToEdit.getId())
                    : NetworkConfig.getProductsUrl(requireContext());
            String successMessage = isEditMode ? "Product updated successfully"
                    : getString(R.string.success_product_created);

            JsonObjectRequest request = new JsonObjectRequest(
                    method,
                    url,
                    productJson,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.has("success") && response.getBoolean("success")) {
                                    Toast.makeText(requireContext(),
                                            successMessage,
                                            Toast.LENGTH_SHORT).show();

                                    // Clear form
                                    editTextName.setText("");
                                    editTextPrice.setText("");
                                    editTextDescription.setText("");
                                    clearEditMode();

                                    // Notify listener to refresh list
                                    if (listener != null) {
                                        listener.onProductCreated();
                                    }
                                } else {
                                    String message = isEditMode ? "Failed to update product"
                                            : "Failed to create product";
                                    if (response.has("message")) {
                                        message = response.getString("message");
                                    }
                                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(requireContext(),
                                        "Error: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            String errorMessage = getString(R.string.error_network);
                            if (error.networkResponse != null) {
                                errorMessage = "Error: " + error.networkResponse.statusCode;
                            } else if (error.getMessage() != null) {
                                errorMessage = error.getMessage();
                            }
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show();
                        }
                    });

            VolleySingleton.getInstance(requireContext()).getRequestQueue().add(request);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Error creating request: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}

