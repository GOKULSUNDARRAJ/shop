package com.gokulsundar4545.kpm;


import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity2 extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // Initialize BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set OnNavigationItemSelectedListener using if-else condition
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            // Use if-else to check which item is selected
            if (item.getItemId() == R.id.nav_fav) {
                selectedFragment = new WishListFragment();
            } else if (item.getItemId() == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (item.getItemId() == R.id.nav_cart) {
                selectedFragment = new YouCartFragment();
            }

            // If a fragment is selected, replace the fragment container
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, selectedFragment)
                        .commit();
            }

            return true;
        });

        // Set default fragment and select home icon on launch (home fragment)
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home); // Set the Home icon as selected
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new HomeFragment())
                    .commit();
        }

        // Retrieve the intent data
        Intent intent = getIntent();
        String fromProductDetailPage = intent.getStringExtra("FromproductDetailPage");

        // Use the data to perform some action, like checking if the data was passed
        if ("YES".equals(fromProductDetailPage)) {
            // Replace the current fragment with YouCartFragment if the flag is "YES"
            bottomNavigationView.setSelectedItemId(R.id.nav_cart); // Set the Home icon as selected
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new YouCartFragment())
                    .commit();
        }
    }

}
