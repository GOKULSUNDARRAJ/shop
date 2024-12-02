package com.gokulsundar4545.kpm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class ProductdetailsDirectBy extends AppCompatActivity {

    private FirebaseUser currentUser;
    private String productId;
    int currentValue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.directproductdetail);

        // Retrieve Firebase authenticated user
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Retrieve product details from the Intent
        Intent intent = getIntent();
        productId = intent.getStringExtra("productId");
        String productName = intent.getStringExtra("productName");
        String productPrice = intent.getStringExtra("productPrice");
        String productQty = intent.getStringExtra("productQty");
        String productImage = intent.getStringExtra("productImage");
        String totalProduct = intent.getStringExtra("totalproduct");
        String productDescription = intent.getStringExtra("totaldescription");

        // Set up views
        TextView nameTextView = findViewById(R.id.textView11);
        TextView priceTextView = findViewById(R.id.prod_price);
        TextView descriptionTextView = findViewById(R.id.textView13);
        TextView prod_qty = findViewById(R.id.prod_qty);
        TextView ml = findViewById(R.id.textView12);
        ImageView imageView = findViewById(R.id.imageView7);

        nameTextView.setText(productName);
        priceTextView.setText(productPrice + "₹");
        descriptionTextView.setText(productDescription);
        prod_qty.setText(totalProduct);
        ml.setText(productQty+" ml");

        Glide.with(this).load(productImage).into(imageView);



        // Handle "Add to Cart" button click
        Button addToCartButton = findViewById(R.id.button2);
        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });






    }




}
