package com.gokulsundar4545.kpm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gokulsundar4545.kpm.adapter.ProductAdapter;
import com.gokulsundar4545.kpm.model.Products;
import com.gokulsundar4545.kpm.model.WishList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ProductAdapter productAdapter;
    private RecyclerView prodItemRecycler;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private TextView tvUserName;
    private Button tvTotalProducts;




    ImageView favimg;
    private List<WishList> favoriteProductsList;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        tvUserName = findViewById(R.id.textView5);
        tvTotalProducts = findViewById(R.id.button);  // Assuming there's a TextView for total products

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            getUserName(userId);
        }

        // Initialize RecyclerView for Products
        prodItemRecycler = findViewById(R.id.product_recycler);
        prodItemRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        loadProductsFromFirebase();

        // Setup Profile Image Click Listener
        ImageView profileImage = findViewById(R.id.imageView3);
        profileImage.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AdminActivity.class)));

        // Setup Cart Button Click Listener
        Button yourCartButton = findViewById(R.id.button);
        yourCartButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, YourCartActivity.class)));

        // Calculate Total Products
        calculateTotalProducts();

        favimg=findViewById(R.id.fav);
        favimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(),FavoritesActivity.class));
            }
        });




        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        favoriteProductsList = new ArrayList<>();


        loadFavoriteProducts();
    }

    private void getUserName(String userId) {
        mDatabase.child("Users").child(userId).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.getValue(String.class);
                    tvUserName.setText("Hello, " + name + "!");
                } else {
                    Toast.makeText(MainActivity.this, "User name not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to retrieve user name", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProductsFromFirebase() {
        mDatabase.child("Products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Products> productsList = new ArrayList<>();
                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    Products product = productSnapshot.getValue(Products.class);
                    if (product != null) {
                        productsList.add(product);
                    }
                }
                productAdapter = new ProductAdapter(MainActivity.this, productsList);
                prodItemRecycler.setAdapter(productAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to load products", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calculateTotalProducts() {
        DatabaseReference userCartRef = mDatabase.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("UserCart");

        userCartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int totalProducts = 0;
                int productCount = 0;
                for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                    String ProductPriceStr = productSnapshot.child("productPrice").getValue(String.class);
                    if (ProductPriceStr != null) {
                        int totalProduct = Integer.parseInt(ProductPriceStr);
                        totalProducts += totalProduct;
                    }

                    productCount++;
                }

                // Update total product count in the UI
                tvTotalProducts.setText("Total Products "+productCount+ " : "+ totalProducts+"₹");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadFavoriteProducts() {
        DatabaseReference favoritesRef = FirebaseDatabase.getInstance().getReference("Favorites")
                .child(firebaseUser.getUid());

        favoritesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                favoriteProductsList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    WishList product = snapshot.getValue(WishList.class);
                    if (product != null) {
                        favoriteProductsList.add(product);
                    }
                }

                // Check if the list is empty and save in SharedPreferences
                boolean isFavoritesEmpty = favoriteProductsList.isEmpty();

                // Optionally, show a message based on the list being empty or not
                if (isFavoritesEmpty) {
                    favimg.setImageResource(R.drawable.baseline_favorite_border_24);  // Set to favorite border
                } else {
                    favimg.setImageResource(R.drawable.baseline_favorite_24);  // Set to favorite border
                }

                // No need to update the adapter, just check if it's empty
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Failed to load favorites", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
