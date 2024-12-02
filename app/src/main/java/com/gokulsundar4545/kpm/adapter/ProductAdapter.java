package com.gokulsundar4545.kpm.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gokulsundar4545.kpm.Productdetails;
import com.gokulsundar4545.kpm.R;
import com.gokulsundar4545.kpm.model.Products;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gokulsundar4545.kpm.Productdetails;
import com.gokulsundar4545.kpm.R;
import com.gokulsundar4545.kpm.model.Products;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    Context context;
    List<Products> productsList;
    FirebaseUser firebaseUser;

    public ProductAdapter(Context context, List<Products> productsList) {
        this.context = context;
        this.productsList = productsList;
        this.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.products_row_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProductViewHolder holder, int position) {
        Products product = productsList.get(position);
        String imageUrl = product.getImageUrl();

        // Load product image
        Glide.with(context)
                .load(imageUrl)
                .into(holder.prodImage);

        holder.prodName.setText(product.getProductName());
        holder.prodQty.setText(product.getProductQty());
        holder.prodPrice.setText(product.getProductPrice() + "₹");

        // Handle item click to navigate to Productdetails
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, Productdetails.class);
            intent.putExtra("productId", product.getProductid());
            intent.putExtra("productName", product.getProductName());
            intent.putExtra("productPrice", product.getProductPrice());
            intent.putExtra("productQty", product.getProductQty());
            intent.putExtra("productImage", product.getImageUrl2());
            intent.putExtra("totalproduct", product.getTotalProduct());
            intent.putExtra("totaldescription", product.getProductDescription());
            context.startActivity(intent);
        });

        // Check if product is already a favorite
        DatabaseReference favoriteRef = FirebaseDatabase.getInstance().getReference("Favorites")
                .child(firebaseUser.getUid())
                .child(product.getProductid());

        favoriteRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Product is already in favorites, show filled favorite icon
                    holder.favoriteImage.setImageResource(R.drawable.baseline_favorite_24);
                } else {
                    // Product is not in favorites, show outline favorite icon
                    holder.favoriteImage.setImageResource(R.drawable.baseline_favorite_border_24);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle potential errors here
            }
        });

        // Toggle favorite status on favoriteImage click
        holder.favoriteImage.setOnClickListener(view -> {
            favoriteRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Product is in favorites, so remove it
                        favoriteRef.removeValue()
                                .addOnSuccessListener(aVoid -> {
                                    holder.favoriteImage.setImageResource(R.drawable.baseline_favorite_border_24);  // Set to favorite border
                                    Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Failed to remove from favorites", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        // Product is not in favorites, so add it
                        HashMap<String, Object> favoriteData = new HashMap<>();
                        favoriteData.put("productid", product.getProductid());
                        favoriteData.put("productName", product.getProductName());
                        favoriteData.put("productPrice", product.getProductPrice());
                        favoriteData.put("productQty", product.getProductQty());
                        favoriteData.put("productDescription", product.getProductDescription());
                        favoriteData.put("imageUrl", product.getImageUrl());  // Add the first image URL
                        favoriteData.put("imageUrl2", product.getImageUrl2());  // Add the second image URL
                        favoriteData.put("totalProduct", product.getTotalProduct());  // Add the second image URL

                        favoriteRef.setValue(favoriteData)
                                .addOnSuccessListener(aVoid -> {
                                    holder.favoriteImage.setImageResource(R.drawable.baseline_favorite_24);  // Set to filled favorite icon
                                    Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Failed to add to favorites", Toast.LENGTH_SHORT).show();
                                });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle potential errors here (like permission issues or database unavailability)
                    Toast.makeText(context, "Error occurred while checking favorite status", Toast.LENGTH_SHORT).show();
                }
            });
        });

    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    public static final class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView prodImage;
        TextView prodName, prodQty, prodPrice;
        ImageView favoriteImage;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            prodImage = itemView.findViewById(R.id.prod_image);
            prodName = itemView.findViewById(R.id.prod_name);
            prodPrice = itemView.findViewById(R.id.prod_price);
            prodQty = itemView.findViewById(R.id.prod_qty);
            favoriteImage = itemView.findViewById(R.id.imageView6);
        }
    }
}
