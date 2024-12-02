package com.gokulsundar4545.kpm.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gokulsundar4545.kpm.R;
import com.gokulsundar4545.kpm.model.DeliveryAddress;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    private List<DeliveryAddress> addressList;
    private int selectedPosition = -1; // -1 means no selection by default
    private DatabaseReference userDatabaseRef;

    public AddressAdapter(List<DeliveryAddress> addressList) {
        this.addressList = addressList;
        // Initialize the Firebase reference for the current user's selected address
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userDatabaseRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(currentUserUid).child("selectedAddress");  // Direct reference to "selectedAddress"
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        DeliveryAddress deliveryAddress = addressList.get(position);

        holder.addressTextView.setText(deliveryAddress.getAddress());

        // Update the RadioButton to show the selected state
        holder.radioButton.setChecked(position == selectedPosition);

        // Set up a click listener to update selected position and save to Firebase
        holder.radioButton.setOnClickListener(v -> {
            selectedPosition = position;
            notifyDataSetChanged(); // Refresh to update selection
            saveSelectedAddressToFirebase(); // Update the selected address in Firebase
        });
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    private void saveSelectedAddressToFirebase() {
        if (selectedPosition != -1) {
            DeliveryAddress selectedAddress = addressList.get(selectedPosition);

            // Create a map to hold all address fields
            Map<String, Object> addressData = new HashMap<>();
            addressData.put("address", selectedAddress.getAddress());
            addressData.put("addressType", selectedAddress.getAddressType());
            addressData.put("area", selectedAddress.getArea());
            addressData.put("email", selectedAddress.getEmail());
            addressData.put("landmark", selectedAddress.getLandmark());
            addressData.put("phone", selectedAddress.getPhone());
            addressData.put("pinCode", selectedAddress.getPinCode());
            addressData.put("state", selectedAddress.getState());

            // Update Firebase with the selected address (overwrite if necessary)
            userDatabaseRef.setValue(addressData)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Address updated successfully
                        } else {
                            // Handle failure if needed
                        }
                    });
        }
    }

    public class AddressViewHolder extends RecyclerView.ViewHolder {
        TextView addressTextView;
        RadioButton radioButton;

        public AddressViewHolder(View itemView) {
            super(itemView);
            addressTextView = itemView.findViewById(R.id.addressTextView);
            radioButton = itemView.findViewById(R.id.radioButton);
        }
    }

    // Optional method to get the selected item
    public DeliveryAddress getSelectedAddress() {
        return selectedPosition != -1 ? addressList.get(selectedPosition) : null;
    }
}
