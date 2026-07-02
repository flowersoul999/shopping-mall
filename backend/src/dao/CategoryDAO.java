/* 成员4: 数据访问层 - 分类数据访问对象 */
package dao;
import com.shop.product.Category;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class CategoryDAO {
    private Connection conn;
    public CategoryDAO(Connection conn) { this.conn = conn; }
    public List<Category> findAll() throws SQLException {
        List<Category> list = new ArrayList<>();
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM category ORDER BY sort_order ASC, id ASC");
        while (rs.next()) { Category c = new Category(); c.setId(rs.getInt("id")); c.setName(rs.getString("name")); c.setDescription(rs.getString("description")); c.setSortOrder(rs.getInt("sort_order")); list.add(c); }
        rs.close(); st.close(); return list;
    }
    public Category findById(int id) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM category WHERE id=?");
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        Category c = rs.next() ? mapCategory(rs) : null;
        rs.close(); ps.close(); return c;
    }
    public int insert(Category c) throws SQLException {
        String sql = "INSERT INTO category (name,description,sort_order) VALUES (?,?,?)";
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, c.getName()); ps.setString(2, c.getDescription()); ps.setInt(3, c.getSortOrder());
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        int id = rs.next() ? rs.getInt(1) : 0;
        rs.close(); ps.close(); return id;
    }
    public void update(Category c) throws SQLException {
        String sql = "UPDATE category SET name=?,description=?,sort_order=? WHERE id=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, c.getName()); ps.setString(2, c.getDescription()); ps.setInt(3, c.getSortOrder()); ps.setInt(4, c.getId());
        ps.executeUpdate(); ps.close();
    }
    public void delete(int id) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("DELETE FROM category WHERE id=?");
        ps.setInt(1, id); ps.executeUpdate(); ps.close();
    }
    private Category mapCategory(ResultSet rs) throws SQLException {
        Category c = new Category();
        c.setId(rs.getInt("id")); c.setName(rs.getString("name"));
        c.setDescription(rs.getString("description")); c.setSortOrder(rs.getInt("sort_order"));
        return c;
    }
}
