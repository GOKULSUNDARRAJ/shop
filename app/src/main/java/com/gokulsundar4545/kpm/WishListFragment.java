package com.gokulsundar4545.kpm;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gokulsundar4545.kpm.adapter.WishListAdapter;
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

public class WishListFragment extends Fragment {
    private RecyclerView favoritesRecyclerView;
    private WishListAdapter productAdapter;
    private List<WishList> favoriteProductsList;
    private FirebaseUser firebaseUser;
    private SharedPreferences sharedPreferences;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_wish_list, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        favoritesRecyclerView =view.findViewById(R.id.favorites_recycler_view);
        favoritesRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        favoriteProductsList = new ArrayList<>();
        productAdapter = new WishListAdapter(getContext(), favoriteProductsList);
        favoritesRecyclerView.setAdapter(productAdapter);



        loadFavoriteProducts();

        return view;
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
                Toast.makeText(getContext(), "Failed to load favorites", Toast.LENGTH_SHORT).show();
            }
        });
    }


}