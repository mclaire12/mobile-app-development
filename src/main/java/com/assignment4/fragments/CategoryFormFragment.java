package com.assignment4.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.assignment4.utils.NetworkConfig;
import com.assignment4.utils.VolleySingleton;
import com.google.android.material.button.MaterialButton;
import org.json.JSONException;
import org.json.JSONObject;

public class CategoryFormFragment extends Fragment {
    private EditText editTextName;
    private EditText editTextDescription;
    private MaterialButton buttonSubmit;
    private OnCategoryCreatedListener listener;
    private Category categoryToEdit = null;
    private boolean isEditMode = false;

    public interface OnCategoryCreatedListener {
        void onCategoryCreated();
    }

    public void setCategoryCreatedListener(OnCategoryCreatedListener listener) {
        this.listener = listener;
    }

    public void setCategoryToEdit(Category category) {
        this.categoryToEdit = category;
        this.isEditMode = true;
        if (editTextName != null) {
            populateFormWithCategory();
        }
    }

    public void clearEditMode() {
        this.categoryToEdit = null;
        this.isEditMode = false;
        if (buttonSubmit != null) {
            buttonSubmit.setText("Create Category");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_form, container, false);

        editTextName = view.findViewById(R.id.editTextCategoryName);
        editTextDescription = view.findViewById(R.id.editTextCategoryDescription);
        buttonSubmit = view.findViewById(R.id.buttonSubmitCategory);

        buttonSubmit.setOnClickListener(v -> submitCategory());

        if (isEditMode && categoryToEdit != null) {
            populateFormWithCategory();
        }

        return view;
    }

    private void populateFormWithCategory() {
        if (categoryToEdit == null)
            return;

        editTextName.setText(categoryToEdit.getName());
        if (categoryToEdit.getDescription() != null) {
            editTextDescription.setText(categoryToEdit.getDescription());
        }

        buttonSubmit.setText("Update Category");
    }

    private void submitCategory() {
        String name = editTextName.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();

        if (name.isEmpty()) {
            editTextName.setError("Category name is required");
            return;
        }

        try {
            JSONObject categoryJson = new JSONObject();
            categoryJson.put("name", name);
            if (!description.isEmpty()) {
                categoryJson.put("description", description);
            }

            int method = isEditMode ? Request.Method.PUT : Request.Method.POST;
            String url = isEditMode ? NetworkConfig.getCategoriesUrl(requireContext()) + "/" + categoryToEdit.getId()
                    : NetworkConfig.getCategoriesUrl(requireContext());
            String successMessage = isEditMode ? "Category updated successfully" : "Category created successfully";

            JsonObjectRequest request = new JsonObjectRequest(
                    method,
                    url,
                    categoryJson,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.has("success") && response.getBoolean("success")) {
                                    Toast.makeText(requireContext(), successMessage, Toast.LENGTH_SHORT).show();

                                    editTextName.setText("");
                                    editTextDescription.setText("");
                                    clearEditMode();

                                    if (listener != null) {
                                        listener.onCategoryCreated();
                                    }
                                } else {
                                    String message = isEditMode ? "Failed to update category"
                                            : "Failed to create category";
                                    if (response.has("message")) {
                                        message = response.getString("message");
                                    }
                                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
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
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Error creating request: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}

