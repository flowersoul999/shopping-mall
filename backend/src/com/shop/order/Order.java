/* 成员3: 订单模块 - 订单实体类 */
package com.shop.order;
import java.util.List;
public class Order {
    private int id; private String orderNo; private int userId; private double totalPrice;
    private String status; private String receiverName; private String receiverPhone;
    private String addressDetail; private String remark; private String createTime;
    private List<OrderItem> items;
    public int getId() { return id; } public void setId(int id) { this.id = id; }
    public String getOrderNo() { return orderNo; } public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public int getUserId() { return userId; } public void setUserId(int userId) { this.userId = userId; }
    public double getTotalPrice() { return totalPrice; } public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public String getStatus() { return status; } public void setStatus(String status) { this.status = status; }
    public String getReceiverName() { return receiverName; } public void setReceiverName(String receiverName) { this.receiverName = receiverName; }
    public String getReceiverPhone() { return receiverPhone; } public void setReceiverPhone(String receiverPhone) { this.receiverPhone = receiverPhone; }
    public String getAddressDetail() { return addressDetail; } public void setAddressDetail(String addressDetail) { this.addressDetail = addressDetail; }
    public String getRemark() { return remark; } public void setRemark(String remark) { this.remark = remark; }
    public String getCreateTime() { return createTime; } public void setCreateTime(String createTime) { this.createTime = createTime; }
    public List<OrderItem> getItems() { return items; } public void setItems(List<OrderItem> items) { this.items = items; }
}
