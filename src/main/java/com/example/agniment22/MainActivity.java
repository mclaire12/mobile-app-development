package com.example.agniment22;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setup Spinners for Date (Mock data)
        Spinner checkInSpinner = findViewById(R.id.spinner_checkin);
        Spinner checkOutSpinner = findViewById(R.id.spinner_checkout);
        String[] dates = { "9/16/2015", "9/17/2015", "9/18/2015" };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dates);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        checkInSpinner.setAdapter(adapter);
        checkOutSpinner.setAdapter(adapter);

        // Wire event to open SecondActivity
        Button bookButton = findViewById(R.id.btn_book);
        bookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                startActivity(intent);
            }
        });

        // Add navigation to Activity3 from bottom navigation (home button - 3rd button,
        // index 2)
        View bottomNav = findViewById(R.id.bottom_nav);
        if (bottomNav instanceof ViewGroup) {
            ViewGroup bottomNavGroup = (ViewGroup) bottomNav;
            // Home button is the 3rd button (index 2)
            if (bottomNavGroup.getChildCount() > 2) {
                View homeBtn = bottomNavGroup.getChildAt(2);
                if (homeBtn instanceof ImageButton) {
                    homeBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MainActivity.this, Activity3.class);
                            startActivity(intent);
                        }
                    });
                }
            }
        }
    }
}
