/* 成员3: 订单模块 - 订单实体类 */
package com.shop.order;

import java.util.List;

/**
 * 订单实体类
 * 表示用户提交的一个订单，包含订单基本信息和订单项列表
 */
public class Order {
    /** 订单ID，主键自增 */
    private int id;
    /** 订单编号，格式为yyyyMMddHHmmss+4位随机数 */
    private String orderNo;
    /** 用户ID，关联用户表 */
    private int userId;
    /** 订单总金额 */
    private double totalPrice;
    /** 订单状态：pending(待付款)、paid(已付款)、shipped(已发货)、completed(已完成)、cancelled(已取消) */
    private String status;
    /** 收货人姓名 */
    private String receiverName;
    /** 收货人电话 */
    private String receiverPhone;
    /** 收货地址详情（省市区+详细地址） */
    private String addressDetail;
    /** 订单备注 */
    private String remark;
    /** 创建时间 */
    private String createTime;
    /** 订单项列表 */
    private List<OrderItem> items;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }
    public String getReceiverPhone() { return receiverPhone; }
    public void setReceiverPhone(String receiverPhone) { this.receiverPhone = receiverPhone; }
    public String getAddressDetail() { return addressDetail; }
    public void setAddressDetail(String addressDetail) { this.addressDetail = addressDetail; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
}