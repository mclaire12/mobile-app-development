package com.assignment4.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Product {
    private int id;
    private String name;
    private double price;
    private String description;
    private String imageUrl;
    private int categoryId;
    private String createdAt;
    private Category category;

    public Product() {
    }

    public Product(int id, String name, double price, String description, String imageUrl, int categoryId, String createdAt) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
        this.categoryId = categoryId;
        this.createdAt = createdAt;
    }

    public static Product fromJson(JSONObject json) throws JSONException {
        Product product = new Product();
        
        // Handle nested data structure from API response
        // If json has "data" key, use that; otherwise use json directly
        JSONObject dataObj;
        if (json.has("data") && json.get("data") instanceof JSONObject) {
            dataObj = json.getJSONObject("data");
        } else {
            dataObj = json;
        }
        
        product.setId(dataObj.getInt("id"));
        product.setName(dataObj.getString("name"));
        product.setPrice(dataObj.getDouble("price"));
        product.setCategoryId(dataObj.getInt("category_id"));
        
        if (dataObj.has("description") && !dataObj.isNull("description")) {
            product.setDescription(dataObj.getString("description"));
        }
        if (dataObj.has("image_url") && !dataObj.isNull("image_url")) {
            product.setImageUrl(dataObj.getString("image_url"));
        }
        if (dataObj.has("created_at") && !dataObj.isNull("created_at")) {
            product.setCreatedAt(dataObj.getString("created_at"));
        }
        
        // Handle category if present
        if (dataObj.has("category") && !dataObj.isNull("category")) {
            JSONObject categoryObj = dataObj.getJSONObject("category");
            product.setCategory(Category.fromJson(categoryObj));
        } else if (dataObj.has("category_name") && !dataObj.isNull("category_name")) {
            // Handle flattened category data
            Category category = new Category();
            category.setId(dataObj.getInt("category_id"));
            category.setName(dataObj.getString("category_name"));
            if (dataObj.has("category_description") && !dataObj.isNull("category_description")) {
                category.setDescription(dataObj.getString("category_description"));
            }
            product.setCategory(category);
        }
        
        return product;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("price", price);
        json.put("category_id", categoryId);
        if (description != null && !description.isEmpty()) {
            json.put("description", description);
        }
        if (imageUrl != null && !imageUrl.isEmpty()) {
            json.put("image_url", imageUrl);
        }
        return json;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}


