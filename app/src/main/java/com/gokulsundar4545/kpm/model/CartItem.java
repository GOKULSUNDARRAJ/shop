package com.gokulsundar4545.kpm.model;
public class CartItem {
    private String productId;
    private String productName;
    private String productPrice;
    private String productQty;
    private String productImage;
    private String totalProduct;
    private String productDescription;
    private String OriginalPrice;

    public CartItem() {
        // Required empty constructor for Firebase
    }

    public CartItem(String productId, String productName, String productPrice, String productQty, String productImage, String totalProduct, String productDescription, String originalPrice) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productQty = productQty;
        this.productImage = productImage;
        this.totalProduct = totalProduct;
        this.productDescription = productDescription;
        OriginalPrice = originalPrice;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductQty() {
        return productQty;
    }

    public void setProductQty(String productQty) {
        this.productQty = productQty;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getTotalProduct() {
        return totalProduct;
    }

    public void setTotalProduct(String totalProduct) {
        this.totalProduct = totalProduct;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getOriginalPrice() {
        return OriginalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        OriginalPrice = originalPrice;
    }
}
