package com.gokulsundar4545.kpm;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gokulsundar4545.kpm.adapter.ProductAdapter;
import com.gokulsundar4545.kpm.model.Products;
import com.gokulsundar4545.kpm.model.WishList;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private ProductAdapter productAdapter;
    private RecyclerView prodItemRecycler;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private TextView tvUserName;
    private Button tvTotalProducts;


    ImageView favimg;
    private List<WishList> favoriteProductsList;
    private FirebaseUser firebaseUser;
    TextView favoriteCountTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_home, container, false);


        favoriteCountTextView =view.findViewById(R.id.favcount); // Replace with your actual TextView ID
        // Initialize UI components
        tvUserName =view.findViewById(R.id.textView5);
        tvTotalProducts = view.findViewById(R.id.button);  // Assuming there's a TextView for total products

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            getUserName(userId);
        }

        // Initialize RecyclerView for Products
        prodItemRecycler =view. findViewById(R.id.product_recycler);
        prodItemRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        loadProductsFromFirebase();

        // Setup Profile Image Click Listener
        ImageView profileImage =view. findViewById(R.id.imageView3);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AddressActivity.class));
            }
        });

        TextView textView5=view.findViewById(R.id.textView5);
        textView5.setOnClickListener(v -> startActivity(new Intent(getContext(), AdminActivity.class)));

        // Setup Cart Button Click Listener
        Button yourCartButton =view. findViewById(R.id.button);
        yourCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a new instance of the desired fragment (e.g., FavoritesFragment)
                Fragment favoritesFragment = new YouCartFragment();

                // Begin a fragment transaction to replace the current fragment
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                // Replace the current fragment with the new one
                transaction.replace(R.id.container, favoritesFragment);  // R.id.fragment_container is the container where the fragment is placed

                // Optionally, you can add this transaction to the back stack if you want to allow users to navigate back
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();

                BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
                bottomNavigationView.setSelectedItemId(R.id.nav_cart); // Set the Home icon as selected
            }
        });

        // Calculate Total Products
        calculateTotalProducts();

        favimg=view.findViewById(R.id.fav);
        favimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a new instance of the desired fragment (e.g., FavoritesFragment)
                Fragment favoritesFragment = new WishListFragment();

                // Begin a fragment transaction to replace the current fragment
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                // Replace the current fragment with the new one
                transaction.replace(R.id.container, favoritesFragment);  // R.id.fragment_container is the container where the fragment is placed

                // Optionally, you can add this transaction to the back stack if you want to allow users to navigate back
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();

                BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
                bottomNavigationView.setSelectedItemId(R.id.nav_fav); // Set the Home icon as selected
            }
        });




        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        favoriteProductsList = new ArrayList<>();


        loadFavoriteProducts();





        return view;
    }


    private void getUserName(String userId) {
        mDatabase.child("Users").child(userId).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.getValue(String.class);
                    tvUserName.setText("Hello, " + name + "!");
                } else {
                    Toast.makeText(getContext(), "User name not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to retrieve user name", Toast.LENGTH_SHORT).show();
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
                productAdapter = new ProductAdapter(getContext(), productsList);
                prodItemRecycler.setAdapter(productAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load products", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
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

                // Get the size of the favoriteProductsList
                int size = favoriteProductsList.size();

                // Set the size in a TextView

                favoriteCountTextView.setText(String.valueOf(size));

                // Check if the list is empty and update the favorite image
                boolean isFavoritesEmpty = favoriteProductsList.isEmpty();
                if (isFavoritesEmpty) {
                    favimg.setImageResource(R.drawable.baseline_favorite_border_24);  // Set to favorite border
                } else {
                    favimg.setImageResource(R.drawable.baseline_favorite_24);  // Set to filled favorite
                }

                // Optionally, update the adapter here if needed
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load favorites", Toast.LENGTH_SHORT).show();
            }
        });
    }

}