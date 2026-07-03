/* 成员3: 订单模块 - 订单项实体类 */
package com.shop.order;

/**
 * 订单项实体类
 * 表示订单中的一个商品条目，记录商品信息和购买数量
 */
public class OrderItem {
    /** 订单项ID，主键自增 */
    private int id;
    /** 订单ID，关联订单表 */
    private int orderId;
    /** 商品ID，关联商品表 */
    private int productId;
    /** 商品名称（下单时锁定，避免后续商品名称变更影响订单） */
    private String productName;
    /** 商品图片路径（下单时锁定） */
    private String productImage;
    /** 商品单价（下单时锁定，避免后续价格变动影响订单） */
    private double price;
    /** 购买数量 */
    private int quantity;
    /** 小计（price * quantity） */
    private double subtotal;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getProductImage() { return productImage; }
    public void setProductImage(String productImage) { this.productImage = productImage; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
}