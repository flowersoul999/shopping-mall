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

/**
 * 订单业务逻辑类
 * 处理订单相关的业务操作，包括创建订单、查询订单列表、查询订单详情、更新订单状态
 */
public class OrderService {

    /**
     * 创建订单（核心业务方法）
     * 使用事务保证数据一致性：创建订单→创建订单项→更新订单总价→清空购物车
     * @param userId 用户ID
     * @param addressId 收货地址ID
     * @param remark 订单备注
     * @return Result对象，包含订单信息
     * @throws Exception 数据库操作异常
     */
    public Result createOrder(int userId, int addressId, String remark) throws Exception {
        Connection conn = DBUtil.getConnection();
        conn.setAutoCommit(false);
        try {
            // 获取购物车已选商品
            CartDAO cartDAO = new CartDAO(conn);
            List<CartItem> cartItems = cartDAO.findByUserId(userId);
            if (cartItems.isEmpty()) {
                conn.close();
                return Result.badRequest("购物车为空");
            }

            // 计算总价、生成订单号（格式：yyyyMMddHHmmss+4位随机数）
            double total = 0;
            String orderNo = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())
                    + String.format("%04d", new Random().nextInt(10000));

            // 根据地址ID获取收货信息
            dao.AddressDAO addrDAO = new dao.AddressDAO(conn);
            com.shop.address.Address addr = addrDAO.findById(addressId);
            String receiverName = addr != null ? addr.getName() : "";
            String receiverPhone = addr != null ? addr.getPhone() : "";
            String addrDetail = addr != null ? (addr.getProvince() + addr.getCity() + addr.getDistrict() + addr.getDetail()) : "";

            // 构造订单对象
            Order order = new Order();
            order.setOrderNo(orderNo);
            order.setUserId(userId);
            order.setStatus("pending");
            order.setReceiverName(receiverName);
            order.setReceiverPhone(receiverPhone);
            order.setAddressDetail(addrDetail);
            order.setRemark(remark);

            // 插入订单记录
            OrderDAO orderDAO = new OrderDAO(conn);
            int orderId = orderDAO.insert(order);
            if (orderId <= 0) {
                conn.rollback();
                conn.close();
                return Result.serverError();
            }

            // 创建订单项
            for (CartItem item : cartItems) {
                double sub = item.getPrice() * item.getQuantity();
                total += sub;
                OrderItem oi = new OrderItem();
                oi.setOrderId(orderId);
                oi.setProductId(item.getProductId());
                oi.setProductName(item.getProductName());
                oi.setProductImage(item.getProductImage());
                oi.setPrice(item.getPrice());
                oi.setQuantity(item.getQuantity());
                oi.setSubtotal(sub);
                orderDAO.insertItem(oi);
            }

            // 更新订单总价
            order.setTotalPrice(total);
            java.sql.PreparedStatement ps = conn.prepareStatement("UPDATE orders SET total_price=? WHERE id=?");
            ps.setDouble(1, total);
            ps.setInt(2, orderId);
            ps.executeUpdate();
            ps.close();

            // 清空购物车
            cartDAO.clearByUserId(userId);

            // 提交事务
            conn.commit();
            order.setId(orderId);
            return Result.success("下单成功", order);

        } catch (Exception e) {
            // 事务回滚
            conn.rollback();
            throw e;
        } finally {
            conn.close();
        }
    }

    /**
     * 获取用户订单列表（分页）
     * @param userId 用户ID
     * @param page 页码（从1开始）
     * @param pageSize 每页数量
     * @param status 订单状态筛选（可选）
     * @return Result对象，包含订单列表和分页信息
     * @throws Exception 数据库操作异常
     */
    public Result getList(int userId, int page, int pageSize, String status) throws Exception {
        Connection conn = DBUtil.getConnection();
        OrderDAO dao = new OrderDAO(conn);
        List<Order> list = dao.findByUserId(userId, page, pageSize, status);
        // 为每个订单加载订单项
        for (Order o : list) {
            o.setItems(dao.findItemsByOrderId(o.getId()));
        }
        conn.close();

        // 封装返回数据
        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("page", page);
        data.put("pageSize", pageSize);
        return Result.success(data);
    }

    /**
     * 获取后台订单列表（分页）
     * 管理员查看所有订单，包含统计信息
     * @param page 页码（从1开始）
     * @param pageSize 每页数量
     * @param status 订单状态筛选（可选）
     * @return Result对象，包含订单列表、分页信息和统计数据
     * @throws Exception 数据库操作异常
     */
    public Result getAdminList(int page, int pageSize, String status) throws Exception {
        Connection conn = DBUtil.getConnection();
        OrderDAO dao = new OrderDAO(conn);
        List<Order> list = dao.findAll(page, pageSize, status);
        // 为每个订单加载订单项
        for (Order o : list) {
            o.setItems(dao.findItemsByOrderId(o.getId()));
        }
        // 获取统计信息
        int total = dao.count();
        double totalSales = dao.sumTotalPrice();
        conn.close();

        // 封装返回数据
        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("page", page);
        data.put("pageSize", pageSize);
        data.put("total", total);
        data.put("totalSales", totalSales);
        return Result.success(data);
    }

    /**
     * 获取订单详情
     * @param id 订单ID
     * @return Result对象，包含订单详情和订单项列表
     * @throws Exception 数据库操作异常
     */
    public Result getDetail(int id) throws Exception {
        Connection conn = DBUtil.getConnection();
        OrderDAO dao = new OrderDAO(conn);
        Order order = dao.findById(id);
        if (order != null) {
            // 加载订单项
            order.setItems(dao.findItemsByOrderId(id));
        }
        conn.close();

        if (order == null) {
            return Result.badRequest("订单不存在");
        }
        return Result.success(order);
    }

    /**
     * 更新订单状态
     * @param id 订单ID
     * @param status 新状态
     * @return Result对象，操作结果
     * @throws Exception 数据库操作异常
     */
    public Result updateStatus(int id, String status) throws Exception {
        Connection conn = DBUtil.getConnection();
        OrderDAO dao = new OrderDAO(conn);
        dao.updateStatus(id, status);
        conn.close();
        return Result.success("更新成功");
    }
}