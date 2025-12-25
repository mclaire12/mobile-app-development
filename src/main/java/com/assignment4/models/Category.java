package com.assignment4.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Category {
    private int id;
    private String name;
    private String description;
    private String createdAt;

    public Category() {
    }

    public Category(int id, String name, String description, String createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
    }

    public static Category fromJson(JSONObject json) throws JSONException {
        Category category = new Category();
        category.setId(json.getInt("id"));
        category.setName(json.getString("name"));
        if (json.has("description") && !json.isNull("description")) {
            category.setDescription(json.getString("description"));
        }
        if (json.has("created_at") && !json.isNull("created_at")) {
            category.setCreatedAt(json.getString("created_at"));
        }
        return category;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("name", name);
        if (description != null) {
            json.put("description", description);
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return name;
    }
}


