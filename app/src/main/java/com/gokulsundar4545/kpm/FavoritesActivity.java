package com.gokulsundar4545.kpm;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gokulsundar4545.kpm.adapter.ProductAdapter;
import com.gokulsundar4545.kpm.adapter.WishListAdapter;
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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gokulsundar4545.kpm.adapter.WishListAdapter;
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

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView favoritesRecyclerView;
    private WishListAdapter productAdapter;
    private List<WishList> favoriteProductsList;
    private FirebaseUser firebaseUser;
    private SharedPreferences sharedPreferences;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        favoritesRecyclerView = findViewById(R.id.favorites_recycler_view);
        favoritesRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        favoriteProductsList = new ArrayList<>();
        productAdapter = new WishListAdapter(this, favoriteProductsList);
        favoritesRecyclerView.setAdapter(productAdapter);


        loadFavoriteProducts();
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

                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(FavoritesActivity.this, "Failed to load favorites", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
