/**
 * 订单数据访问对象（DAO层）
 * 负责订单表(orders)和订单项表(order_item)的数据库操作
 * 
 * 主要功能: 创建订单、查询订单列表、查询订单详情、更新订单状态等
 * 订单和订单项是一对多关系，一个订单包含多个订单项
 */
/* 成员1: 数据访问层 - 订单数据访问对象 */
package dao;
import com.shop.order.Order;
import com.shop.order.OrderItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    
    /** 数据库连接对象 */
    private Connection conn;
    
    /**
     * 构造方法，注入数据库连接
     * @param conn 数据库连接对象
     */
    public OrderDAO(Connection conn) { 
        this.conn = conn; 
    }

    /**
     * 插入新订单
     * @param order 订单对象
     * @return 新插入订单的ID
     * @throws SQLException 数据库异常
     */
    public int insert(Order order) throws SQLException {
        String sql = "INSERT INTO orders (order_no,user_id,total_price,status,receiver_name,receiver_phone,address_detail,remark) VALUES (?,?,?,?,?,?,?,?)";
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, order.getOrderNo()); 
        ps.setInt(2, order.getUserId());
        ps.setDouble(3, order.getTotalPrice()); 
        ps.setString(4, order.getStatus());
        ps.setString(5, order.getReceiverName()); 
        ps.setString(6, order.getReceiverPhone());
        ps.setString(7, order.getAddressDetail()); 
        ps.setString(8, order.getRemark());
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        int id = rs.next() ? rs.getInt(1) : 0;
        rs.close(); 
        ps.close(); 
        return id;
    }

    /**
     * 插入订单项
     * 一个订单可以包含多个订单项
     * @param item 订单项对象
     * @throws SQLException 数据库异常
     */
    public void insertItem(OrderItem item) throws SQLException {
        String sql = "INSERT INTO order_item (order_id,product_id,product_name,product_image,price,quantity,subtotal) VALUES (?,?,?,?,?,?,?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, item.getOrderId()); 
        ps.setInt(2, item.getProductId());
        ps.setString(3, item.getProductName()); 
        ps.setString(4, item.getProductImage());
        ps.setDouble(5, item.getPrice()); 
        ps.setInt(6, item.getQuantity());
        ps.setDouble(7, item.getSubtotal());
        ps.executeUpdate(); 
        ps.close();
    }

    /**
     * 查询用户订单列表（分页）
     * @param userId 用户ID
     * @param page 页码（从1开始）
     * @param size 每页数量
     * @param status 订单状态筛选（可选，all表示全部）
     * @return 订单列表
     * @throws SQLException 数据库异常
     */
    public List<Order> findByUserId(int userId, int page, int size, String status) throws SQLException {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE user_id=?";
        // 如果有状态筛选，添加WHERE条件
        if (status != null && !status.isEmpty() && !"all".equals(status)) { 
            sql += " AND status='"+status+"'"; 
        }
        sql += " ORDER BY create_time DESC LIMIT ?,?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, userId); 
        ps.setInt(2, (page-1)*size); 
        ps.setInt(3, size);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) { 
            list.add(mapOrder(rs)); 
        }
        rs.close(); 
        ps.close(); 
        return list;
    }

    /**
     * 根据ID查询订单详情
     * @param id 订单ID
     * @return Order对象，如果不存在返回null
     * @throws SQLException 数据库异常
     */
    public Order findById(int id) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM orders WHERE id=?");
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        Order o = rs.next() ? mapOrder(rs) : null;
        rs.close(); 
        ps.close(); 
        return o;
    }

    /**
     * 查询订单的所有订单项
     * @param orderId 订单ID
     * @return 订单项列表
     * @throws SQLException 数据库异常
     */
    public List<OrderItem> findItemsByOrderId(int orderId) throws SQLException {
        List<OrderItem> list = new ArrayList<>();
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM order_item WHERE order_id=?");
        ps.setInt(1, orderId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) { 
            OrderItem item = new OrderItem();
            item.setId(rs.getInt("id")); 
            item.setOrderId(rs.getInt("order_id"));
            item.setProductId(rs.getInt("product_id")); 
            item.setProductName(rs.getString("product_name"));
            item.setProductImage(rs.getString("product_image")); 
            item.setPrice(rs.getDouble("price"));
            item.setQuantity(rs.getInt("quantity")); 
            item.setSubtotal(rs.getDouble("subtotal"));
            list.add(item); 
        }
        rs.close(); 
        ps.close(); 
        return list;
    }

    /**
     * 更新订单状态
     * @param id 订单ID
     * @param status 新状态（待付款、待发货、已发货、已完成、已取消）
     * @throws SQLException 数据库异常
     */
    public void updateStatus(int id, String status) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("UPDATE orders SET status=? WHERE id=?");
        ps.setString(1, status); 
        ps.setInt(2, id);
        ps.executeUpdate(); 
        ps.close();
    }

    /**
     * 查询所有订单（后台管理用）- 分页查询
     * @param page 页码
     * @param size 每页数量
     * @param status 订单状态筛选（可选）
     * @return 订单列表
     * @throws SQLException 数据库异常
     */
    public List<Order> findAll(int page, int size, String status) throws SQLException {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM orders";
        if (status != null && !status.isEmpty() && !"all".equals(status)) { 
            sql += " WHERE status='"+status+"'"; 
        }
        sql += " ORDER BY create_time DESC LIMIT ?,?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, (page-1)*size); 
        ps.setInt(2, size);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) { 
            list.add(mapOrder(rs)); 
        }
        rs.close(); 
        ps.close(); 
        return list;
    }

    /**
     * 查询订单总数
     * @return 订单总数
     * @throws SQLException 数据库异常
     */
    public int count() throws SQLException {
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM orders");
        int cnt = rs.next() ? rs.getInt(1) : 0;
        rs.close(); 
        st.close(); 
        return cnt;
    }

    /**
     * 统计订单总金额（排除已取消订单）
     * 使用COALESCE处理NULL值（如果没有订单，返回0）
     * @return 订单总金额
     * @throws SQLException 数据库异常
     */
    public double sumTotalPrice() throws SQLException {
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT COALESCE(SUM(total_price),0) FROM orders WHERE status!='cancelled'");
        double sum = rs.next() ? rs.getDouble(1) : 0;
        rs.close(); 
        st.close(); 
        return sum;
    }

    /**
     * 将ResultSet映射为Order对象（私有辅助方法）
     * @param rs 查询结果集
     * @return Order对象
     * @throws SQLException 数据库异常
     */
    private Order mapOrder(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setId(rs.getInt("id")); 
        o.setOrderNo(rs.getString("order_no"));
        o.setUserId(rs.getInt("user_id")); 
        o.setTotalPrice(rs.getDouble("total_price"));
        o.setStatus(rs.getString("status")); 
        o.setReceiverName(rs.getString("receiver_name"));
        o.setReceiverPhone(rs.getString("receiver_phone"));
        o.setAddressDetail(rs.getString("address_detail"));
        o.setRemark(rs.getString("remark"));
        o.setCreateTime(rs.getString("create_time"));
        return o;
    }
}