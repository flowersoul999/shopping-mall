/* 成员3: 购物车模块 - 购物车项实体 */
package com.shop.cart;

/**
 * 购物车项实体类
 * 表示用户购物车中的一个商品条目
 */
public class CartItem {
    /** 购物车项ID，主键自增 */
    private int id;
    /** 用户ID，关联用户表 */
    private int userId;
    /** 商品ID，关联商品表 */
    private int productId;
    /** 商品数量 */
    private int quantity;
    /** 商品名称（冗余存储，方便展示） */
    private String productName;
    /** 商品价格（冗余存储，下单时锁定价格） */
    private double price;
    /** 商品图片路径 */
    private String productImage;
    /** 商品库存 */
    private int stock;
    /** 商品状态（1上架，0下架） */
    private int productStatus;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getProductImage() { return productImage; }
    public void setProductImage(String productImage) { this.productImage = productImage; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public int getProductStatus() { return productStatus; }
    public void setProductStatus(int productStatus) { this.productStatus = productStatus; }
}