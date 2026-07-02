/* 成员4: 数据访问层 - 商品数据访问对象 */
package dao;
import com.shop.product.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class ProductDAO {
    private Connection conn;
    public ProductDAO(Connection conn) { this.conn = conn; }
    public List<Product> findAll(int page, int size) throws SQLException {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*,c.name as category_name FROM product p LEFT JOIN category c ON p.category_id=c.id ORDER BY p.id DESC LIMIT ?,?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, (page-1)*size); ps.setInt(2, size);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) list.add(mapProduct(rs));
        rs.close(); ps.close(); return list;
    }
    public List<Product> findByCategory(int categoryId, int page, int size) throws SQLException {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*,c.name as category_name FROM product p LEFT JOIN category c ON p.category_id=c.id WHERE p.category_id=? ORDER BY p.id DESC LIMIT ?,?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, categoryId); ps.setInt(2, (page-1)*size); ps.setInt(3, size);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) list.add(mapProduct(rs));
        rs.close(); ps.close(); return list;
    }
    public List<Product> search(String keyword, int page, int size) throws SQLException {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*,c.name as category_name FROM product p LEFT JOIN category c ON p.category_id=c.id WHERE p.name LIKE ? OR p.description LIKE ? ORDER BY p.id DESC LIMIT ?,?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, "%"+keyword+"%"); ps.setString(2, "%"+keyword+"%");
        ps.setInt(3, (page-1)*size); ps.setInt(4, size);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) list.add(mapProduct(rs));
        rs.close(); ps.close(); return list;
    }
    public Product findById(int id) throws SQLException {
        String sql = "SELECT p.*,c.name as category_name FROM product p LEFT JOIN category c ON p.category_id=c.id WHERE p.id=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        Product p = rs.next() ? mapProduct(rs) : null;
        rs.close(); ps.close(); return p;
    }
    public int insert(Product p) throws SQLException {
        String sql = "INSERT INTO product (category_id,name,description,price,stock,image) VALUES (?,?,?,?,?,?)";
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, p.getCategoryId()); ps.setString(2, p.getName()); ps.setString(3, p.getDescription());
        ps.setDouble(4, p.getPrice()); ps.setInt(5, p.getStock()); ps.setString(6, p.getImage());
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        int id = rs.next() ? rs.getInt(1) : 0;
        rs.close(); ps.close(); return id;
    }
    public void update(Product p) throws SQLException {
        String sql = "UPDATE product SET category_id=?,name=?,description=?,price=?,stock=?,image=?,status=? WHERE id=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, p.getCategoryId()); ps.setString(2, p.getName()); ps.setString(3, p.getDescription());
        ps.setDouble(4, p.getPrice()); ps.setInt(5, p.getStock()); ps.setString(6, p.getImage());
        ps.setInt(7, p.getStatus()); ps.setInt(8, p.getId());
        ps.executeUpdate(); ps.close();
    }
    public void delete(int id) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("DELETE FROM product WHERE id=?");
        ps.setInt(1, id); ps.executeUpdate(); ps.close();
    }
    public int count() throws SQLException {
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM product");
        int cnt = rs.next() ? rs.getInt(1) : 0;
        rs.close(); st.close(); return cnt;
    }
    public int countByCategory(int categoryId) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM product WHERE category_id=?");
        ps.setInt(1, categoryId);
        ResultSet rs = ps.executeQuery();
        int cnt = rs.next() ? rs.getInt(1) : 0;
        rs.close(); ps.close(); return cnt;
    }
    public int countSearch(String keyword) throws SQLException {
        String sql = "SELECT COUNT(*) FROM product WHERE name LIKE ? OR description LIKE ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, "%"+keyword+"%"); ps.setString(2, "%"+keyword+"%");
        ResultSet rs = ps.executeQuery();
        int cnt = rs.next() ? rs.getInt(1) : 0;
        rs.close(); ps.close(); return cnt;
    }
    public List<Product> findLatest(int limit) throws SQLException {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*,c.name as category_name FROM product p LEFT JOIN category c ON p.category_id=c.id WHERE p.status=1 ORDER BY p.id DESC LIMIT ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, limit);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) list.add(mapProduct(rs));
        rs.close(); ps.close(); return list;
    }
    private Product mapProduct(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getInt("id")); p.setCategoryId(rs.getInt("category_id"));
        p.setName(rs.getString("name")); p.setDescription(rs.getString("description"));
        p.setPrice(rs.getDouble("price")); p.setStock(rs.getInt("stock"));
        p.setImage(rs.getString("image")); p.setStatus(rs.getInt("status"));
        p.setSales(rs.getInt("sales")); p.setCreateTime(rs.getString("create_time"));
        try { p.setCategoryName(rs.getString("category_name")); } catch (Exception e) {}
        return p;
    }
}
