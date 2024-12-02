package com.gokulsundar4545.kpm.model;

public class DeliveryAddress {
    private String state, pinCode, area, landmark, address, email, phone, addressType;

    // Constructor
    public DeliveryAddress(String state, String pinCode, String area, String landmark, String address, String email, String phone, String addressType) {
        this.state = state;
        this.pinCode = pinCode;
        this.area = area;
        this.landmark = landmark;
        this.address = address;
        this.email = email;
        this.phone = phone;
        this.addressType = addressType;
    }

    // Default constructor for Firebase deserialization
    public DeliveryAddress() {}

    // Getters and Setters for each field (optional)


    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }
}
