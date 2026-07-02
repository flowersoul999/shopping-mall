/* 成员3: 订单模块 - 订单业务逻辑 */
package com.shop.order;
import dao.CartDAO;
import dao.OrderDAO;
import com.shop.cart.CartItem;
import com.shop.common.DBUtil;
import com.shop.common.Result;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.*;
public class OrderService {
    public Result createOrder(int userId, int addressId, String remark) throws Exception {
        Connection conn = DBUtil.getConnection();
        conn.setAutoCommit(false);
        try {
            // 获取购物车已选商品
            CartDAO cartDAO = new CartDAO(conn);
            List<CartItem> cartItems = cartDAO.findByUserId(userId);
            if (cartItems.isEmpty()) { conn.close(); return Result.badRequest("购物车为空"); }
            // 计算总价、生成订单号
            double total = 0;
            String orderNo = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())
                    + String.format("%04d", new Random().nextInt(10000));
            // 根据地址ID获取收货信息
            dao.AddressDAO addrDAO = new dao.AddressDAO(conn);
            com.shop.address.Address addr = addrDAO.findById(addressId);
            String receiverName = addr != null ? addr.getName() : "";
            String receiverPhone = addr != null ? addr.getPhone() : "";
            String addrDetail = addr != null ? (addr.getProvince()+addr.getCity()+addr.getDistrict()+addr.getDetail()) : "";
            // 构造订单
            Order order = new Order();
            order.setOrderNo(orderNo); order.setUserId(userId); order.setStatus("pending");
            order.setReceiverName(receiverName); order.setReceiverPhone(receiverPhone);
            order.setAddressDetail(addrDetail); order.setRemark(remark);
            OrderDAO orderDAO = new OrderDAO(conn);
            int orderId = orderDAO.insert(order);
            if (orderId <= 0) { conn.rollback(); conn.close(); return Result.serverError(); }
            // 创建订单项
            for (CartItem item : cartItems) {
                double sub = item.getPrice() * item.getQuantity();
                total += sub;
                OrderItem oi = new OrderItem();
                oi.setOrderId(orderId); oi.setProductId(item.getProductId());
                oi.setProductName(item.getProductName()); oi.setProductImage(item.getProductImage());
                oi.setPrice(item.getPrice()); oi.setQuantity(item.getQuantity()); oi.setSubtotal(sub);
                orderDAO.insertItem(oi);
            }
            order.setTotalPrice(total);
            // 更新订单总价
            java.sql.PreparedStatement ps = conn.prepareStatement("UPDATE orders SET total_price=? WHERE id=?");
            ps.setDouble(1, total); ps.setInt(2, orderId); ps.executeUpdate(); ps.close();
            // 清空购物车
            cartDAO.clearByUserId(userId);
            conn.commit();
            order.setId(orderId);
            return Result.success("下单成功", order);
        } catch (Exception e) {
            conn.rollback(); throw e;
        } finally { conn.close(); }
    }
    public Result getList(int userId, int page, int pageSize, String status) throws Exception {
        Connection conn = DBUtil.getConnection();
        OrderDAO dao = new OrderDAO(conn);
        List<Order> list = dao.findByUserId(userId, page, pageSize, status);
        // 为每个订单加载订单项
        for (Order o : list) {
            o.setItems(dao.findItemsByOrderId(o.getId()));
        }
        conn.close();
        Map<String, Object> data = new HashMap<>();
        data.put("list", list); data.put("page", page); data.put("pageSize", pageSize);
        return Result.success(data);
    }
    public Result getDetail(int id) throws Exception {
        Connection conn = DBUtil.getConnection();
        OrderDAO dao = new OrderDAO(conn);
        Order order = dao.findById(id);
        if (order != null) {
            order.setItems(dao.findItemsByOrderId(id));
        }
        conn.close();
        if (order == null) return Result.badRequest("订单不存在");
        return Result.success(order);
    }
    public Result updateStatus(int id, String status) throws Exception {
        Connection conn = DBUtil.getConnection();
        OrderDAO dao = new OrderDAO(conn);
        dao.updateStatus(id, status);
        conn.close(); return Result.success("更新成功");
    }
}
