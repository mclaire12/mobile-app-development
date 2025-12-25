package com.assignment4.utils;

import android.content.Context;

public class NetworkConfig {
    // Default IP - can be changed by user in settings
    private static final String DEFAULT_BASE_URL = "http://172.20.10.5:3000/api";
    private static PreferenceManager preferenceManager;

    public static void init(Context context) {
        if (preferenceManager == null) {
            preferenceManager = new PreferenceManager(context.getApplicationContext());
        }
    }

    public static String getBaseUrl(Context context) {
        init(context);
        return preferenceManager.getBaseUrl();
    }

    public static String getCategoriesUrl(Context context) {
        return getBaseUrl(context) + "/categories";
    }

    public static String getProductsUrl(Context context) {
        return getBaseUrl(context) + "/products";
    }

    public static String getProductUrl(Context context, int id) {
        return getBaseUrl(context) + "/products/" + id;
    }

    // For backward compatibility
    public static String getBaseUrl() {
        return DEFAULT_BASE_URL;
    }

    public static String getCategoriesUrl() {
        return DEFAULT_BASE_URL + "/categories";
    }

    public static String getProductsUrl() {
        return DEFAULT_BASE_URL + "/products";
    }

    public static String getProductUrl(int id) {
        return DEFAULT_BASE_URL + "/products/" + id;
    }
}


