/* 成员3: 购物车模块 - 购物车项实体 */
package com.shop.cart;
public class CartItem {
    private int id; private int userId; private int productId; private int quantity;
    private String productName; private double price; private String productImage;
    private int stock; private int productStatus;
    public int getId() { return id; } public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; } public void setUserId(int userId) { this.userId = userId; }
    public int getProductId() { return productId; } public void setProductId(int productId) { this.productId = productId; }
    public int getQuantity() { return quantity; } public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getProductName() { return productName; } public void setProductName(String productName) { this.productName = productName; }
    public double getPrice() { return price; } public void setPrice(double price) { this.price = price; }
    public String getProductImage() { return productImage; } public void setProductImage(String productImage) { this.productImage = productImage; }
    public int getStock() { return stock; } public void setStock(int stock) { this.stock = stock; }
    public int getProductStatus() { return productStatus; } public void setProductStatus(int productStatus) { this.productStatus = productStatus; }
}
