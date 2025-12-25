package com.assignment4.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    private static final String PREF_NAME = "AppPreferences";
    private static final String KEY_IP_ADDRESS = "ip_address";
    private static final String DEFAULT_IP = "172.20.10.5";
    private static final String KEY_PORT = "port";
    private static final String DEFAULT_PORT = "3000";

    private SharedPreferences preferences;

    public PreferenceManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setIpAddress(String ipAddress) {
        preferences.edit().putString(KEY_IP_ADDRESS, ipAddress).apply();
    }

    public String getIpAddress() {
        return preferences.getString(KEY_IP_ADDRESS, DEFAULT_IP);
    }

    public void setPort(String port) {
        preferences.edit().putString(KEY_PORT, port).apply();
    }

    public String getPort() {
        return preferences.getString(KEY_PORT, DEFAULT_PORT);
    }

    public String getBaseUrl() {
        return "http://" + getIpAddress() + ":" + getPort() + "/api";
    }

    public void resetToDefault() {
        preferences.edit()
                .putString(KEY_IP_ADDRESS, DEFAULT_IP)
                .putString(KEY_PORT, DEFAULT_PORT)
                .apply();
    }
}


