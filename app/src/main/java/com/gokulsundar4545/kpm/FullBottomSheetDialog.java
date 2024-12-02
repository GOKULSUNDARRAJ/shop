package com.gokulsundar4545.kpm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gokulsundar4545.kpm.model.Address;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FullBottomSheetDialog extends BottomSheetDialogFragment {

    String string;

    public FullBottomSheetDialog(String string) {
        this.string = string;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_bottom_sheet, container, false);

        EditText address = ((EditText) view.findViewById(R.id.address));
        address.setText(string);


        Button saveButton = view.findViewById(R.id.button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get data from EditTexts
                String state = ((EditText) view.findViewById(R.id.state)).getText().toString();
                String pinCode = ((EditText) view.findViewById(R.id.PinCode)).getText().toString();
                String area = ((EditText) view.findViewById(R.id.area)).getText().toString();
                String landmark = ((EditText) view.findViewById(R.id.landmark)).getText().toString();
                String address = ((EditText) view.findViewById(R.id.address)).getText().toString();
                String email = ((EditText) view.findViewById(R.id.email)).getText().toString();
                String phone = ((EditText) view.findViewById(R.id.phone)).getText().toString();

                // Get the selected address type
                RadioGroup radioGroup = view.findViewById(R.id.radioGroup);
                int selectedId = radioGroup.getCheckedRadioButtonId();
                RadioButton selectedRadioButton = view.findViewById(selectedId);
                String addressType = selectedRadioButton != null ? selectedRadioButton.getText().toString() : "Home";

                // Get current user's UID
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                // Create a reference to the Firebase Realtime Database
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("addresses");

                // Create a unique ID for each address
                String addressId = databaseReference.push().getKey();

                // Create a new address object
                Address addressObject = new Address(state, pinCode, area, landmark, address, email, phone, addressType);

                // Save the address data to Firebase
                if (addressId != null) {
                    databaseReference.child(addressId).setValue(addressObject)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Address Saved", Toast.LENGTH_SHORT).show();
                                    dismiss(); // Close the bottom sheet
                                } else {
                                    Toast.makeText(getContext(), "Failed to save address", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Set Bottom Sheet to full-screen height
        if (getDialog() != null) {
            View bottomSheet = getDialog().findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
                bottomSheet.getLayoutParams().height = WindowManager.LayoutParams.MATCH_PARENT;
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setPeekHeight(WindowManager.LayoutParams.MATCH_PARENT);
            }
        }
    }


}
