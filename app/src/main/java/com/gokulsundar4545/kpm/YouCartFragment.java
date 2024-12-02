package com.gokulsundar4545.kpm;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gokulsundar4545.kpm.adapter.CartAdapter;
import com.gokulsundar4545.kpm.model.CartItem;
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

public class YouCartFragment extends Fragment {

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems;
    private Drawable deleteIcon;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_you_cart, container, false);
        recyclerView =view.findViewById(R.id.recyclerViewCart);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        cartItems = new ArrayList<>();
        cartAdapter = new CartAdapter(getContext(), cartItems);
        recyclerView.setAdapter(cartAdapter);

        loadCartItems();

        deleteIcon = ContextCompat.getDrawable(getContext(), R.drawable.baseline_delete_24);

        // Attach ItemTouchHelper for swipe-to-delete functionality
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false; // No drag-and-drop functionality
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                cartAdapter.removeItem(position); // Call removeItem method when item is swiped
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;
                    float iconMargin = (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                    float iconTop = itemView.getTop() + (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                    float iconBottom = iconTop + deleteIcon.getIntrinsicHeight();
                    float iconLeft = itemView.getRight() - iconMargin - deleteIcon.getIntrinsicWidth();
                    float iconRight = itemView.getRight() - iconMargin;

                    // Set the delete icon color (red) and draw it
                    deleteIcon.setBounds((int) iconLeft, (int) iconTop, (int) iconRight, (int) iconBottom);
                    deleteIcon.setTint(Color.RED);  // Set color to red for the delete icon
                    deleteIcon.draw(c);
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        return view;
    }

    private void loadCartItems() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("Users")
                    .child(currentUser.getUid()).child("UserCart");

            cartRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    cartItems.clear();
                    for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                        CartItem cartItem = itemSnapshot.getValue(CartItem.class);
                        if (cartItem != null) {
                            cartItems.add(cartItem);
                        }
                    }
                    cartAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Failed to load cart items.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


}