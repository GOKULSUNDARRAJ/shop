package com.gokulsundar4545.kpm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class BuyDetailActivity extends AppCompatActivity implements PaymentResultListener {


    private TextView selectedAddressTextView;  // TextView to display the selected address
    private DatabaseReference userDatabaseRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_detail);

        // Retrieve the Intent extras
        Intent intent = getIntent();
        String productId = intent.getStringExtra("productId");
        String productName = intent.getStringExtra("productName");
        String productPrice = intent.getStringExtra("productPrice");
        String productQty = intent.getStringExtra("productQty");
        String productImage = intent.getStringExtra("productImage");
        String totalProduct = intent.getStringExtra("totalproduct");
        String productDescription = intent.getStringExtra("productdescription");

        // Find TextView elements in the layout
        TextView productNameTextView = findViewById(R.id.productNameTextView);
        TextView productPriceTextView = findViewById(R.id.productPriceTextView1);
        TextView productqtyTextView = findViewById(R.id.productPriceTextView2);
        TextView productDescriptionTextView = findViewById(R.id.productDescriptionTextView);

        // Set the text for each TextView
        productNameTextView.setText(productName);
        productPriceTextView.setText("Price: " + productPrice); // You can format as needed
        productDescriptionTextView.setText(productDescription);
        productqtyTextView.setText("Qty: " + productQty);

        // Load the product image if needed (e.g., using Glide or Picasso if it's a URL)
        ImageView productImageView = findViewById(R.id.productImageView);
        // For example, if using Glide:
        //
        Glide.with(this).load(productImage).into(productImageView);

        selectedAddressTextView = findViewById(R.id.addressTextView);

        // Get current user's Firebase reference for selectedAddress
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userDatabaseRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(currentUserUid).child("selectedAddress");

        // Retrieve the selected address from Firebase
        retrieveSelectedAddress();

        LinearLayout addaddress=findViewById(R.id.addaddress);
        addaddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(),AddressActivity.class));
            }
        });
        TextView totalprice = findViewById(R.id.notification15);
        totalprice.setText(productPrice+" ₹"); // You can format as needed
        TextView ml = findViewById(R.id.notification157);
        ml.setText(productQty); // You can format as needed
        TextView totalpriceall = findViewById(R.id.notification157825);
        totalpriceall.setText(productPrice+" ₹"); // You can format as needed

        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_kEloGiFoPFtyoJ"); // Use your Razorpay test key
        Button pay=findViewById(R.id.button2);
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPayment();
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        retrieveSelectedAddress();
    }

    private void retrieveSelectedAddress() {
        userDatabaseRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                // Address data exists, retrieve and display
                Map<String, Object> addressData = (Map<String, Object>) task.getResult().getValue();
                if (addressData != null) {
                    // Get the address from the map
                    String address = (String) addressData.get("address");
                    String addressType = (String) addressData.get("addressType");
                    String area = (String) addressData.get("area");
                    String email = (String) addressData.get("email");
                    String landmark = (String) addressData.get("landmark");
                    String phone = (String) addressData.get("phone");
                    String pinCode = (String) addressData.get("pinCode");
                    String state = (String) addressData.get("state");

                    // Combine the address data into one string
                    String fullAddress = "Address: " + address + "\n" +
                            "Address Type: " + addressType + "\n" +
                            "Area: " + area + "\n" +
                            "Landmark: " + landmark + "\n" +
                            "Phone: " + phone + "\n" +
                            "PinCode: " + pinCode + "\n" +
                            "State: " + state;

                    // Set the full address to the TextView
                    selectedAddressTextView.setText(fullAddress);
                }
            } else {
                // Handle case where data does not exist or retrieval failed
                selectedAddressTextView.setText("No selected address found.");
            }
        });
    }


    private void startPayment() {
        final Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_kEloGiFoPFtyoJ");

        try {
            JSONObject options = new JSONObject();

            // Add details to the Razorpay Checkout
            options.put("name", "Your App Name");
            options.put("description", "Test Payment");
            options.put("image", "https://your_logo_url.com"); // Optional
            options.put("currency", "INR");
            options.put("amount", "10"); // Amount in paise (50000 paise = INR 500)

            JSONObject prefill = new JSONObject();
            prefill.put("email", "customer@example.com");
            prefill.put("contact", "9876543210");

            options.put("prefill", prefill);

            checkout.open(this, options);

        } catch (Exception e) {
            Toast.makeText(this, "Error in starting Razorpay Checkout: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
    @Override
    public void onPaymentSuccess(String razorpayPaymentId) {
        // Payment Success Callback
        Toast.makeText(this, "Payment Successful: " + razorpayPaymentId, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPaymentError(int code, String response) {
        // Payment Error Callback
        Toast.makeText(this, "Payment Failed: " + response, Toast.LENGTH_LONG).show();
    }

}
