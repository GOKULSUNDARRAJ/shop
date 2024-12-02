package com.gokulsundar4545.kpm.model;

public class Products {

    String productid; // Changed to String
    String productName;
    String productQty;
    String productPrice;
    String imageUrl; // Changed to String
    String ProductDescription;
    String TotalProduct;
    String  imageUrl2;

    public Products() {
        // Firebase requires this constructor for deserialization
    }

    public Products(String productid, String productName, String productQty, String productPrice, String imageUrl, String productDescription, String totalProduct, String imageUrl2) {
        this.productid = productid;
        this.productName = productName;
        this.productQty = productQty;
        this.productPrice = productPrice;
        this.imageUrl = imageUrl;
        ProductDescription = productDescription;
        TotalProduct = totalProduct;
        this.imageUrl2 = imageUrl2;
    }

    public String getProductid() {
        return productid;
    }

    public void setProductid(String productid) {
        this.productid = productid;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductQty() {
        return productQty;
    }

    public void setProductQty(String productQty) {
        this.productQty = productQty;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getProductDescription() {
        return ProductDescription;
    }

    public void setProductDescription(String productDescription) {
        ProductDescription = productDescription;
    }

    public String getTotalProduct() {
        return TotalProduct;
    }

    public void setTotalProduct(String totalProduct) {
        TotalProduct = totalProduct;
    }

    public String getImageUrl2() {
        return imageUrl2;
    }

    public void setImageUrl2(String imageUrl2) {
        this.imageUrl2 = imageUrl2;
    }
}
