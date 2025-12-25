package com.assignment4;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.assignment4.fragments.CategoryFragment;
import com.assignment4.fragments.ProductFragment;
import com.assignment4.utils.NetworkConfig;
import com.assignment4.utils.PreferenceManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.example.agniment22.R;

public class ProductManagementActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private FragmentManager fragmentManager;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_management);

        // Set up toolbar first
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        
        // Apply window insets for safe area
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.product_management_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Set toolbar top margin for status bar
            android.view.ViewGroup.MarginLayoutParams toolbarParams = 
                (android.view.ViewGroup.MarginLayoutParams) toolbar.getLayoutParams();
            toolbarParams.topMargin = systemBars.top;
            toolbar.setLayoutParams(toolbarParams);
            // Apply padding to bottom navigation for bottom safe area
            bottomNav.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
        setSupportActionBar(toolbar);

        // Set up action bar with back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Product Management");
        }

        preferenceManager = new PreferenceManager(this);
        NetworkConfig.init(this);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        fragmentManager = getSupportFragmentManager();

        // Set default fragment (Products)
        if (savedInstanceState == null) {
            loadFragment(new ProductFragment());
        }

        // Handle bottom navigation item clicks
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_products) {
                fragment = new ProductFragment();
            } else if (itemId == R.id.navigation_categories) {
                fragment = new CategoryFragment();
            }

            if (fragment != null) {
                loadFragment(fragment);
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Handle back button press
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_settings) {
            showNetworkSettingsDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showNetworkSettingsDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_network_settings);
        dialog.getWindow().setLayout(
                (int) (getResources().getDisplayMetrics().widthPixels * 0.9),
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

        EditText editTextIpAddress = dialog.findViewById(R.id.editTextIpAddress);
        EditText editTextPort = dialog.findViewById(R.id.editTextPort);
        TextView textCurrentUrl = dialog.findViewById(R.id.textCurrentUrl);
        MaterialButton btnSave = dialog.findViewById(R.id.btnSave);
        MaterialButton btnCancel = dialog.findViewById(R.id.btnCancel);
        MaterialButton btnResetDefault = dialog.findViewById(R.id.btnResetDefault);

        // Set current values
        editTextIpAddress.setText(preferenceManager.getIpAddress());
        editTextPort.setText(preferenceManager.getPort());
        textCurrentUrl.setText(preferenceManager.getBaseUrl());

        btnSave.setOnClickListener(v -> {
            String ipAddress = editTextIpAddress.getText().toString().trim();
            String port = editTextPort.getText().toString().trim();

            if (ipAddress.isEmpty()) {
                editTextIpAddress.setError("IP address is required");
                return;
            }

            if (port.isEmpty()) {
                editTextPort.setError("Port is required");
                return;
            }

            preferenceManager.setIpAddress(ipAddress);
            preferenceManager.setPort(port);

            Toast.makeText(this, "Network settings saved!\nNew URL: " + preferenceManager.getBaseUrl(),
                    Toast.LENGTH_LONG).show();
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnResetDefault.setOnClickListener(v -> {
            preferenceManager.resetToDefault();
            editTextIpAddress.setText(preferenceManager.getIpAddress());
            editTextPort.setText(preferenceManager.getPort());
            textCurrentUrl.setText(preferenceManager.getBaseUrl());
            Toast.makeText(this, "Reset to default settings", Toast.LENGTH_SHORT).show();
        });

        dialog.show();
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}

