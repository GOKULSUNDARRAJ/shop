package com.gokulsundar4545.kpm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Productdetailscart extends AppCompatActivity {

    private FirebaseUser currentUser;
    private String productId;
    int currentValue;
    String productPrice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_productdetailscart);

        // Retrieve Firebase authenticated user
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Retrieve product details from the Intent
        Intent intent = getIntent();
        productId = intent.getStringExtra("productId");
        String productName = intent.getStringExtra("productName");
        productPrice = intent.getStringExtra("productPrice");
        String productQty = intent.getStringExtra("productQty");
        String productImage = intent.getStringExtra("productImage");
        String totalProduct = intent.getStringExtra("totalproduct");
        String productDescription = intent.getStringExtra("totaldescription");

        // Set up views
        TextView nameTextView = findViewById(R.id.textView11);
        TextView priceTextView = findViewById(R.id.textView12);
        TextView descriptionTextView = findViewById(R.id.textView13);
        ImageView imageView = findViewById(R.id.imageView7);
        TextView totalProductTextView = findViewById(R.id.textView14);

        nameTextView.setText(productName);
        priceTextView.setText(productPrice + "₹");
        descriptionTextView.setText(productDescription);
        totalProductTextView.setText(totalProduct);
        Glide.with(this).load(productImage).into(imageView);

        // Quantity increment and decrement setup
        ImageView decButton = findViewById(R.id.imageView8);
        ImageView incButton = findViewById(R.id.imageView9);
        TextView valueTextView = findViewById(R.id.textView14);
        currentValue = Integer.parseInt(valueTextView.getText().toString());
        decButton.setOnClickListener(view -> {

            if (currentValue > 1) {
                currentValue--;
                valueTextView.setText(String.valueOf(currentValue));
            }
        });

        incButton.setOnClickListener(view -> {
            currentValue = Integer.parseInt(valueTextView.getText().toString());
            if (currentValue < 25) {
                currentValue++;
                valueTextView.setText(String.valueOf(currentValue));
            } else {
                showToast("Stock not available");
            }
        });

        // Handle "Add to Cart" button click
        Button addToCartButton = findViewById(R.id.button2);
        addToCartButton.setOnClickListener(view -> checkIfProductInCart());


        if (currentUser != null && productId != null) {
            DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("Users")
                    .child(currentUser.getUid())
                    .child("UserCart")
                    .child(productId);

            cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Product is already in cart, navigate to new activity
                        Button addToCartButton = findViewById(R.id.button2);
                        addToCartButton.setText("Go to Cart!");
                    } else {

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    showToast("Error checking cart status.");
                }
            });
        } else {
            showToast("User not logged in or product ID missing.");
        }

        Button buyButton = findViewById(R.id.button3);
        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                double totalprice=Double.parseDouble(valueTextView.getText().toString())*Double.parseDouble(productPrice);
                Intent intent = new Intent(v.getContext(), BuyDetailActivity.class);
                intent.putExtra("productId", productId);
                intent.putExtra("productName", productName);
                intent.putExtra("productPrice", String.valueOf(totalprice));
                intent.putExtra("productQty", valueTextView.getText().toString());
                intent.putExtra("productImage", productImage);
                intent.putExtra("totalproduct", totalProduct);
                intent.putExtra("productdescription", productDescription);
                startActivity(intent);
            }
        });


    }

    private void checkIfProductInCart() {
        if (currentUser != null && productId != null) {
            DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("Users")
                    .child(currentUser.getUid())
                    .child("UserCart")
                    .child(productId);

            cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Product is already in cart, navigate to new activity
                        navigateToCartActivity();
                    } else {
                        // Product not in cart, add it
                        addToCart();
                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    showToast("Error checking cart status.");
                }
            });
        } else {
            showToast("User not logged in or product ID missing.");
        }
    }

    private void addToCart() {
        if (currentUser != null && productId != null) {
            DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("Users")
                    .child(currentUser.getUid())
                    .child("UserCart")
                    .child(productId);

            int totalprice = Integer.parseInt(getIntent().getStringExtra("productPrice")) * currentValue;
            // Create a map to hold product details
            Map<String, Object> productDetails = new HashMap<>();
            productDetails.put("productId", productId);
            productDetails.put("productName", getIntent().getStringExtra("productName"));
            productDetails.put("productPrice", String.valueOf(totalprice));
            productDetails.put("OriginalPrice",productPrice);
            productDetails.put("productQty", getIntent().getStringExtra("productQty"));
            productDetails.put("productImage", getIntent().getStringExtra("productImage"));
            productDetails.put("totalProduct", String.valueOf(currentValue));
            productDetails.put("productDescription", getIntent().getStringExtra("totaldescription"));

            // Save product details to the database
            cartRef.setValue(productDetails)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            showToast("Product added to cart with all details!");
                            Button addToCartButton = findViewById(R.id.button2);
                            addToCartButton.setText("Go to Cart!");
                        } else {
                            showToast("Failed to add product details to cart.");
                        }
                    });
        } else {
            showToast("User not logged in or product ID missing.");
        }
    }


    private void navigateToCartActivity() {
        Intent intent = new Intent(Productdetailscart.this, MainActivity2.class);
        intent.putExtra("FromproductDetailPage", "YES");
        startActivity(intent);
    }


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
