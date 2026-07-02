/* 成员4: 数据访问层 - 订单数据访问对象 */
package dao;
import com.shop.order.Order;
import com.shop.order.OrderItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class OrderDAO {
    private Connection conn;
    public OrderDAO(Connection conn) { this.conn = conn; }
    public int insert(Order order) throws SQLException {
        String sql = "INSERT INTO orders (order_no,user_id,total_price,status,receiver_name,receiver_phone,address_detail,remark) VALUES (?,?,?,?,?,?,?,?)";
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, order.getOrderNo()); ps.setInt(2, order.getUserId());
        ps.setDouble(3, order.getTotalPrice()); ps.setString(4, order.getStatus());
        ps.setString(5, order.getReceiverName()); ps.setString(6, order.getReceiverPhone());
        ps.setString(7, order.getAddressDetail()); ps.setString(8, order.getRemark());
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        int id = rs.next() ? rs.getInt(1) : 0;
        rs.close(); ps.close(); return id;
    }
    public void insertItem(OrderItem item) throws SQLException {
        String sql = "INSERT INTO order_item (order_id,product_id,product_name,product_image,price,quantity,subtotal) VALUES (?,?,?,?,?,?,?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, item.getOrderId()); ps.setInt(2, item.getProductId());
        ps.setString(3, item.getProductName()); ps.setString(4, item.getProductImage());
        ps.setDouble(5, item.getPrice()); ps.setInt(6, item.getQuantity());
        ps.setDouble(7, item.getSubtotal());
        ps.executeUpdate(); ps.close();
    }
    public List<Order> findByUserId(int userId, int page, int size, String status) throws SQLException {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE user_id=?";
        if (status != null && !status.isEmpty() && !"all".equals(status)) { sql += " AND status='"+status+"'"; }
        sql += " ORDER BY create_time DESC LIMIT ?,?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, userId); ps.setInt(2, (page-1)*size); ps.setInt(3, size);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) list.add(mapOrder(rs));
        rs.close(); ps.close(); return list;
    }
    public Order findById(int id) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM orders WHERE id=?");
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        Order o = rs.next() ? mapOrder(rs) : null;
        rs.close(); ps.close(); return o;
    }
    public List<OrderItem> findItemsByOrderId(int orderId) throws SQLException {
        List<OrderItem> list = new ArrayList<>();
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM order_item WHERE order_id=?");
        ps.setInt(1, orderId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) { OrderItem item = new OrderItem();
            item.setId(rs.getInt("id")); item.setOrderId(rs.getInt("order_id"));
            item.setProductId(rs.getInt("product_id")); item.setProductName(rs.getString("product_name"));
            item.setProductImage(rs.getString("product_image")); item.setPrice(rs.getDouble("price"));
            item.setQuantity(rs.getInt("quantity")); item.setSubtotal(rs.getDouble("subtotal"));
            list.add(item); }
        rs.close(); ps.close(); return list;
    }
    public void updateStatus(int id, String status) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("UPDATE orders SET status=? WHERE id=?");
        ps.setString(1, status); ps.setInt(2, id);
        ps.executeUpdate(); ps.close();
    }
    public List<Order> findAll(int page, int size) throws SQLException {
        List<Order> list = new ArrayList<>();
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM orders ORDER BY create_time DESC LIMIT ?,?");
        ps.setInt(1, (page-1)*size); ps.setInt(2, size);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) list.add(mapOrder(rs));
        rs.close(); ps.close(); return list;
    }
    public int count() throws SQLException {
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM orders");
        int cnt = rs.next() ? rs.getInt(1) : 0;
        rs.close(); st.close(); return cnt;
    }
    public double sumTotalPrice() throws SQLException {
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT COALESCE(SUM(total_price),0) FROM orders WHERE status!='cancelled'");
        double sum = rs.next() ? rs.getDouble(1) : 0;
        rs.close(); st.close(); return sum;
    }
    private Order mapOrder(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setId(rs.getInt("id")); o.setOrderNo(rs.getString("order_no"));
        o.setUserId(rs.getInt("user_id")); o.setTotalPrice(rs.getDouble("total_price"));
        o.setStatus(rs.getString("status")); o.setReceiverName(rs.getString("receiver_name"));
        o.setReceiverPhone(rs.getString("receiver_phone"));
        o.setAddressDetail(rs.getString("address_detail"));
        o.setRemark(rs.getString("remark"));
        o.setCreateTime(rs.getString("create_time"));
        return o;
    }
}
