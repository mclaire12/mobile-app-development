package com.assignment4.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.agniment22.R;
import com.assignment4.adapters.CategoryAdapter;
import com.assignment4.models.Category;
import com.assignment4.utils.NetworkConfig;
import com.assignment4.utils.VolleySingleton;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment implements CategoryFormFragment.OnCategoryCreatedListener {
    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private List<Category> categoryList;
    private MaterialButton btnToggleForm;
    private FloatingActionButton fabAddCategory;
    private CategoryFormFragment formFragment;
    private boolean isFormVisible = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewCategories);
        btnToggleForm = view.findViewById(R.id.btnToggleCategoryForm);
        fabAddCategory = view.findViewById(R.id.fabAddCategory);

        categoryList = new ArrayList<>();
        adapter = new CategoryAdapter(categoryList, new CategoryAdapter.OnCategoryActionListener() {
            @Override
            public void onEditClick(Category category) {
                editCategory(category);
            }

            @Override
            public void onDeleteClick(Category category) {
                confirmDeleteCategory(category);
            }

            @Override
            public void onItemClick(Category category) {
                showCategoryDetails(category);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        btnToggleForm.setOnClickListener(v -> toggleFormFragment());
        fabAddCategory.setOnClickListener(v -> toggleFormFragment());

        FragmentManager fragmentManager = getChildFragmentManager();
        formFragment = (CategoryFormFragment) fragmentManager.findFragmentById(R.id.categoryFragmentContainer);

        if (formFragment == null) {
            formFragment = new CategoryFormFragment();
            formFragment.setCategoryCreatedListener(this);
        }

        loadCategories();

        return view;
    }

    private void toggleFormFragment() {
        FragmentManager fragmentManager = getChildFragmentManager();
        FrameLayout fragmentContainer = getView().findViewById(R.id.categoryFragmentContainer);

        if (!isFormVisible) {
            recyclerView.setVisibility(View.GONE);
            fragmentContainer.setVisibility(View.VISIBLE);

            fragmentManager.beginTransaction()
                    .replace(R.id.categoryFragmentContainer, formFragment)
                    .commit();
            isFormVisible = true;
            btnToggleForm.setText("Cancel");
            fabAddCategory.hide();
        } else {
            fragmentContainer.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            formFragment.clearEditMode();

            fragmentManager.beginTransaction()
                    .remove(formFragment)
                    .commit();
            isFormVisible = false;
            btnToggleForm.setText("Add Category");
            fabAddCategory.show();
        }
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
                        loadCategoriesFromResponse(response);
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

    private void loadCategoriesFromResponse(JSONObject response) {
        try {
            List<Category> categories = new ArrayList<>();

            if (response.has("success") && response.getBoolean("success")) {
                if (response.has("data")) {
                    Object data = response.get("data");
                    if (data instanceof JSONArray) {
                        JSONArray dataArray = (JSONArray) data;
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject categoryObj = dataArray.getJSONObject(i);
                            Category category = Category.fromJson(categoryObj);
                            categories.add(category);
                        }
                    }
                }
            }

            categoryList.clear();
            categoryList.addAll(categories);
            adapter.updateCategories(categoryList);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Error parsing response: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void editCategory(Category category) {
        if (!isFormVisible) {
            toggleFormFragment();
        }
        formFragment.setCategoryToEdit(category);
    }

    private void confirmDeleteCategory(Category category) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Delete Category");
        builder.setMessage("Are you sure you want to delete \"" + category.getName() + "\"?");
        builder.setPositiveButton("Delete", (dialog, which) -> deleteCategory(category));
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void deleteCategory(Category category) {
        String url = NetworkConfig.getCategoriesUrl(requireContext()) + "/" + category.getId();

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.DELETE,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(requireContext(), "Category deleted successfully", Toast.LENGTH_SHORT).show();
                        loadCategories();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = "Failed to delete category";
                        if (error.networkResponse != null) {
                            errorMessage = "Error: " + error.networkResponse.statusCode;
                        }
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show();
                    }
                });

        VolleySingleton.getInstance(requireContext()).getRequestQueue().add(request);
    }

    private void showCategoryDetails(Category category) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Category Details");

        String details = "Name: " + category.getName() + "\n\n";

        if (category.getDescription() != null && !category.getDescription().isEmpty()) {
            details += "Description: " + category.getDescription() + "\n\n";
        }

        if (category.getCreatedAt() != null) {
            details += "Created: " + category.getCreatedAt();
        }

        builder.setMessage(details);
        builder.setPositiveButton("Close", null);
        builder.show();
    }

    @Override
    public void onCategoryCreated() {
        loadCategories();
        if (isFormVisible) {
            toggleFormFragment();
        }
    }
}

