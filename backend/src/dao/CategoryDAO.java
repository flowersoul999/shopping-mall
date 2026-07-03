/**
 * 分类数据访问对象（DAO层）
 * 负责分类表(category)的数据库操作
 * 
 * 主要功能: 查询分类列表、查询分类详情、新增分类、更新分类、删除分类等
 * 分类用于商品的归类，支持排序(order_sort)
 */
/* 成员1: 数据访问层 - 分类数据访问对象 */
package dao;
import com.shop.product.Category;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    
    /** 数据库连接对象 */
    private Connection conn;
    
    /**
     * 构造方法，注入数据库连接
     * @param conn 数据库连接对象
     */
    public CategoryDAO(Connection conn) { 
        this.conn = conn; 
    }

    /**
     * 查询所有分类（按排序字段升序）
     * @return 分类列表
     * @throws SQLException 数据库异常
     */
    public List<Category> findAll() throws SQLException {
        List<Category> list = new ArrayList<>();
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM category ORDER BY sort_order ASC, id ASC");
        while (rs.next()) { 
            Category c = new Category(); 
            c.setId(rs.getInt("id")); 
            c.setName(rs.getString("name")); 
            c.setDescription(rs.getString("description")); 
            c.setSortOrder(rs.getInt("sort_order")); 
            list.add(c); 
        }
        rs.close(); 
        st.close(); 
        return list;
    }

    /**
     * 根据ID查询分类
     * @param id 分类ID
     * @return Category对象，如果不存在返回null
     * @throws SQLException 数据库异常
     */
    public Category findById(int id) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM category WHERE id=?");
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        Category c = rs.next() ? mapCategory(rs) : null;
        rs.close(); 
        ps.close(); 
        return c;
    }

    /**
     * 插入新分类
     * @param c 分类对象
     * @return 新插入分类的ID
     * @throws SQLException 数据库异常
     */
    public int insert(Category c) throws SQLException {
        String sql = "INSERT INTO category (name,description,sort_order) VALUES (?,?,?)";
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, c.getName()); 
        ps.setString(2, c.getDescription()); 
        ps.setInt(3, c.getSortOrder());
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        int id = rs.next() ? rs.getInt(1) : 0;
        rs.close(); 
        ps.close(); 
        return id;
    }

    /**
     * 更新分类信息
     * @param c 分类对象（包含ID和要更新的字段）
     * @throws SQLException 数据库异常
     */
    public void update(Category c) throws SQLException {
        String sql = "UPDATE category SET name=?,description=?,sort_order=? WHERE id=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, c.getName()); 
        ps.setString(2, c.getDescription()); 
        ps.setInt(3, c.getSortOrder()); 
        ps.setInt(4, c.getId());
        ps.executeUpdate(); 
        ps.close();
    }

    /**
     * 删除分类
     * @param id 分类ID
     * @throws SQLException 数据库异常
     */
    public void delete(int id) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("DELETE FROM category WHERE id=?");
        ps.setInt(1, id); 
        ps.executeUpdate(); 
        ps.close();
    }

    /**
     * 将ResultSet映射为Category对象（私有辅助方法）
     * @param rs 查询结果集
     * @return Category对象
     * @throws SQLException 数据库异常
     */
    private Category mapCategory(ResultSet rs) throws SQLException {
        Category c = new Category();
        c.setId(rs.getInt("id")); 
        c.setName(rs.getString("name"));
        c.setDescription(rs.getString("description")); 
        c.setSortOrder(rs.getInt("sort_order"));
        return c;
    }
}