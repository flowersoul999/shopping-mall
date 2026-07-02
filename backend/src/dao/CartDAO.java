/* 成员4: 数据访问层 - 购物车数据访问对象 */
package dao;
import com.shop.cart.CartItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class CartDAO {
    private Connection conn;
    public CartDAO(Connection conn) { this.conn = conn; }
    /** 查询用户购物车列表（连带商品信息） */
    public List<CartItem> findByUserId(int userId) throws SQLException {
        List<CartItem> list = new ArrayList<>();
        String sql = "SELECT ci.*,p.name as product_name,p.price,p.image,p.stock,p.status FROM cart_item ci LEFT JOIN product p ON ci.product_id=p.id WHERE ci.user_id=? ORDER BY ci.id DESC";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) { CartItem item = new CartItem();
            item.setId(rs.getInt("id")); item.setUserId(rs.getInt("user_id"));
            item.setProductId(rs.getInt("product_id")); item.setQuantity(rs.getInt("quantity"));
            item.setProductName(rs.getString("product_name"));
            item.setPrice(rs.getDouble("price")); item.setProductImage(rs.getString("image"));
            item.setStock(rs.getInt("stock")); item.setProductStatus(rs.getInt("status"));
            list.add(item); }
        rs.close(); ps.close(); return list;
    }
    /** 查询用户购物车中某商品记录 */
    public CartItem findByUserAndProduct(int userId, int productId) throws SQLException {
        String sql = "SELECT * FROM cart_item WHERE user_id=? AND product_id=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, userId); ps.setInt(2, productId);
        ResultSet rs = ps.executeQuery();
        CartItem item = null;
        if (rs.next()) { item = new CartItem(); item.setId(rs.getInt("id")); item.setUserId(rs.getInt("user_id")); item.setProductId(rs.getInt("product_id")); item.setQuantity(rs.getInt("quantity")); }
        rs.close(); ps.close(); return item;
    }
    /** 添加购物车项 */
    public int insert(CartItem item) throws SQLException {
        String sql = "INSERT INTO cart_item (user_id,product_id,quantity) VALUES (?,?,?)";
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, item.getUserId()); ps.setInt(2, item.getProductId()); ps.setInt(3, item.getQuantity());
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        int id = rs.next() ? rs.getInt(1) : 0;
        rs.close(); ps.close(); return id;
    }
    /** 更新购物车项数量 */
    public void updateQuantity(int id, int quantity) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("UPDATE cart_item SET quantity=? WHERE id=?");
        ps.setInt(1, quantity); ps.setInt(2, id);
        ps.executeUpdate(); ps.close();
    }
    /** 删除购物车项 */
    public void delete(int id) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("DELETE FROM cart_item WHERE id=?");
        ps.setInt(1, id); ps.executeUpdate(); ps.close();
    }
    /** 清空用户购物车 */
    public void clearByUserId(int userId) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("DELETE FROM cart_item WHERE user_id=?");
        ps.setInt(1, userId); ps.executeUpdate(); ps.close();
    }
    /** 获取购物车商品数量 */
    public int countByUserId(int userId) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM cart_item WHERE user_id=?");
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        int cnt = rs.next() ? rs.getInt(1) : 0;
        rs.close(); ps.close(); return cnt;
    }
}
