package com.gokulsundar4545.kpm;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.gokulsundar4545.kpm.model.Products;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private EditText etProductName, etProductDescription, etProductPrice, etProductImageURL;
    private Button btnSaveProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        mDatabase = FirebaseDatabase.getInstance().getReference("Products");

        etProductName = findViewById(R.id.etProductName);
        etProductDescription = findViewById(R.id.etProductDescription);
        etProductPrice = findViewById(R.id.etProductPrice);
        etProductImageURL = findViewById(R.id.etProductImageURL);
        btnSaveProduct = findViewById(R.id.btnSaveProduct);

        btnSaveProduct.setOnClickListener(v -> saveProductToFirebase());
    }

    private void saveProductToFirebase() {
        String productName = etProductName.getText().toString();
        String productoty = etProductDescription.getText().toString();
        String productPrice = etProductPrice.getText().toString();
        String productImageURL = etProductImageURL.getText().toString();

        if (!productName.isEmpty() && !productoty.isEmpty() && !productPrice.isEmpty() && !productImageURL.isEmpty()) {
            String productId = mDatabase.push().getKey(); // Get unique key for the product

            // Pass the productId as the 5th argument
            Products product = new Products(productId,productName, productoty, productPrice, productImageURL,"Shampoo is a hair care product that is used to cleanse the scalp and hair by removing dirt, sebum, and residues of hair care products. It contains surfactants that emulsify oily dirt, allowing it to be washed away easily.","25","https://firebasestorage.googleapis.com/v0/b/course-c3c18.appspot.com/o/prod.png?alt=media&token=e751e949-f6b8-4364-80dc-27d21d169ca8");

            mDatabase.child(productId).setValue(product).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(AdminActivity.this, "Product added successfully", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(AdminActivity.this, "Failed to add product", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        }
    }


}
