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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gokulsundar4545.kpm.Productdetails;
import com.gokulsundar4545.kpm.Productdetailscart;
import com.gokulsundar4545.kpm.R;
import com.gokulsundar4545.kpm.model.CartItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<CartItem> cartItems;
    private DatabaseReference userCartReference;

    public CartAdapter(Context context, List<CartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userCartReference = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(currentUserId)
                .child("UserCart");
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        holder.productName.setText(cartItem.getProductName());
        holder.productPrice.setText(cartItem.getProductPrice() + "₹");
        holder.productDescription.setText(cartItem.getProductDescription());
        Glide.with(context).load(cartItem.getProductImage()).into(holder.productImage);

        holder.valueTextView.setText(cartItem.getTotalProduct());

        // Decrease Quantity
        holder.decButton.setOnClickListener(view -> {
            int currentValue = Integer.parseInt(holder.valueTextView.getText().toString());
            if (currentValue > 1) {
                currentValue--;
                holder.valueTextView.setText(String.valueOf(currentValue));
                updateQuantityAndPriceInDatabase(cartItem.getProductId(), currentValue, cartItem.getOriginalPrice()); // Update in Firebase
            }
        });

        // Increase Quantity
        holder.incButton.setOnClickListener(view -> {
            int currentValue = Integer.parseInt(holder.valueTextView.getText().toString());
            if (currentValue < 25) {
                currentValue++;
                holder.valueTextView.setText(String.valueOf(currentValue));
                updateQuantityAndPriceInDatabase(cartItem.getProductId(), currentValue, cartItem.getOriginalPrice()); // Update in Firebase
            } else {
                showToast("Stock not available");
            }
        });

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, Productdetailscart.class);
            intent.putExtra("productId", cartItem.getProductId());
            intent.putExtra("productName", cartItem.getProductName());
            intent.putExtra("productPrice", cartItem.getProductPrice());
            intent.putExtra("productQty", cartItem.getProductQty());
            intent.putExtra("productImage", cartItem.getProductImage());
            intent.putExtra("totalproduct", cartItem.getTotalProduct());
            intent.putExtra("totaldescription", cartItem.getProductDescription());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, productDescription;
        ImageView productImage;
        ImageView decButton, incButton;
        TextView valueTextView;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productNameTextView);
            productPrice = itemView.findViewById(R.id.productPriceTextView);
            productDescription = itemView.findViewById(R.id.productDescriptionTextView);
            productImage = itemView.findViewById(R.id.productImageView);
            decButton = itemView.findViewById(R.id.imageView8);
            incButton = itemView.findViewById(R.id.imageView9);
            valueTextView = itemView.findViewById(R.id.textView14);
        }
    }

    private void updateQuantityAndPriceInDatabase(String productId, int quantity, String unitPrice) {
        try {
            // Remove the ₹ symbol and parse the price as a double
            String priceWithoutCurrency = unitPrice.replace("₹", "");
            int price = Integer.parseInt(priceWithoutCurrency);

            // Calculate the total price
            int totalPrice = price * quantity;

            // Update quantity and total price in Firebase
            userCartReference.child(productId).child("totalProduct").setValue(String.valueOf(quantity))
                    .addOnSuccessListener(aVoid -> {
                        // Update product price in the database
                        userCartReference.child(productId).child("productPrice").setValue(String.valueOf(totalPrice))
                                .addOnSuccessListener(aVoid1 -> {
                                    // Notify the adapter to refresh the item
                                    notifyItemChanged(cartItems.indexOf(getCartItemById(productId)));
                                    showToast("Quantity and price updated");
                                })
                                .addOnFailureListener(e -> showToast("Failed to update price"));
                    })
                    .addOnFailureListener(e -> showToast("Failed to update quantity"));
        } catch (NumberFormatException e) {
            showToast("Invalid price format");
        }
    }

    private CartItem getCartItemById(String productId) {
        for (CartItem cartItem : cartItems) {
            if (cartItem.getProductId().equals(productId)) {
                return cartItem;
            }
        }
        return null; // Return null if not found
    }

    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    // Method to remove item from the list and Firebase
    public void removeItem(int position) {
        CartItem cartItem = cartItems.get(position);
        cartItems.remove(position);  // Remove the item from the list
        notifyItemRemoved(position);  // Notify adapter of item removal

        // Remove the item from Firebase
        userCartReference.child(cartItem.getProductId()).removeValue()
                .addOnSuccessListener(aVoid -> showToast("Item removed from cart"))
                .addOnFailureListener(e -> showToast("Failed to remove item"));
    }
}
