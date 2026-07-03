/**
 * 商品数据访问对象（DAO层）
 * 负责商品表(product)和分类表(category)的数据库操作
 * 
 * 主要功能: 商品列表查询、搜索、详情、新增、修改、删除等
 * 使用LEFT JOIN关联分类表获取分类名称
 */
/* 成员1: 数据访问层 - 商品数据访问对象 */
package dao;
import com.shop.product.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    
    /** 数据库连接对象 */
    private Connection conn;
    
    /**
     * 构造方法，注入数据库连接
     * @param conn 数据库连接对象
     */
    public ProductDAO(Connection conn) { 
        this.conn = conn; 
    }

    /**
     * 查询商品列表（分页）
     * LEFT JOIN分类表获取分类名称
     * @param page 页码（从1开始）
     * @param size 每页数量
     * @return 商品列表
     * @throws SQLException 数据库异常
     */
    public List<Product> findAll(int page, int size) throws SQLException {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*,c.name as category_name FROM product p LEFT JOIN category c ON p.category_id=c.id ORDER BY p.id DESC LIMIT ?,?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, (page-1)*size); 
        ps.setInt(2, size);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) { 
            list.add(mapProduct(rs)); 
        }
        rs.close(); 
        ps.close(); 
        return list;
    }

    /**
     * 按分类查询商品（分页）
     * @param categoryId 分类ID
     * @param page 页码
     * @param size 每页数量
     * @return 商品列表
     * @throws SQLException 数据库异常
     */
    public List<Product> findByCategory(int categoryId, int page, int size) throws SQLException {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*,c.name as category_name FROM product p LEFT JOIN category c ON p.category_id=c.id WHERE p.category_id=? ORDER BY p.id DESC LIMIT ?,?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, categoryId); 
        ps.setInt(2, (page-1)*size); 
        ps.setInt(3, size);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) { 
            list.add(mapProduct(rs)); 
        }
        rs.close(); 
        ps.close(); 
        return list;
    }

    /**
     * 搜索商品（按名称或描述模糊匹配）
     * @param keyword 搜索关键词
     * @param page 页码
     * @param size 每页数量
     * @return 商品列表
     * @throws SQLException 数据库异常
     */
    public List<Product> search(String keyword, int page, int size) throws SQLException {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*,c.name as category_name FROM product p LEFT JOIN category c ON p.category_id=c.id WHERE p.name LIKE ? OR p.description LIKE ? ORDER BY p.id DESC LIMIT ?,?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, "%"+keyword+"%"); 
        ps.setString(2, "%"+keyword+"%");
        ps.setInt(3, (page-1)*size); 
        ps.setInt(4, size);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) { 
            list.add(mapProduct(rs)); 
        }
        rs.close(); 
        ps.close(); 
        return list;
    }

    /**
     * 根据ID查询商品详情
     * @param id 商品ID
     * @return Product对象，如果不存在返回null
     * @throws SQLException 数据库异常
     */
    public Product findById(int id) throws SQLException {
        String sql = "SELECT p.*,c.name as category_name FROM product p LEFT JOIN category c ON p.category_id=c.id WHERE p.id=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        Product p = rs.next() ? mapProduct(rs) : null;
        rs.close(); 
        ps.close(); 
        return p;
    }

    /**
     * 插入新商品
     * @param p 商品对象
     * @return 新插入商品的ID
     * @throws SQLException 数据库异常
     */
    public int insert(Product p) throws SQLException {
        String sql = "INSERT INTO product (category_id,name,description,price,stock,image) VALUES (?,?,?,?,?,?)";
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, p.getCategoryId()); 
        ps.setString(2, p.getName()); 
        ps.setString(3, p.getDescription());
        ps.setDouble(4, p.getPrice()); 
        ps.setInt(5, p.getStock()); 
        ps.setString(6, p.getImage());
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        int id = rs.next() ? rs.getInt(1) : 0;
        rs.close(); 
        ps.close(); 
        return id;
    }

    /**
     * 更新商品信息
     * @param p 商品对象（包含ID和要更新的字段）
     * @throws SQLException 数据库异常
     */
    public void update(Product p) throws SQLException {
        String sql = "UPDATE product SET category_id=?,name=?,description=?,price=?,stock=?,image=?,status=? WHERE id=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, p.getCategoryId()); 
        ps.setString(2, p.getName()); 
        ps.setString(3, p.getDescription());
        ps.setDouble(4, p.getPrice()); 
        ps.setInt(5, p.getStock()); 
        ps.setString(6, p.getImage());
        ps.setInt(7, p.getStatus()); 
        ps.setInt(8, p.getId());
        ps.executeUpdate(); 
        ps.close();
    }

    /**
     * 删除商品
     * @param id 商品ID
     * @throws SQLException 数据库异常
     */
    public void delete(int id) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("DELETE FROM product WHERE id=?");
        ps.setInt(1, id); 
        ps.executeUpdate(); 
        ps.close();
    }

    /**
     * 查询商品总数
     * @return 商品总数
     * @throws SQLException 数据库异常
     */
    public int count() throws SQLException {
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM product");
        int cnt = rs.next() ? rs.getInt(1) : 0;
        rs.close(); 
        st.close(); 
        return cnt;
    }

    /**
     * 查询某分类下商品数量
     * @param categoryId 分类ID
     * @return 商品数量
     * @throws SQLException 数据库异常
     */
    public int countByCategory(int categoryId) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM product WHERE category_id=?");
        ps.setInt(1, categoryId);
        ResultSet rs = ps.executeQuery();
        int cnt = rs.next() ? rs.getInt(1) : 0;
        rs.close(); 
        ps.close(); 
        return cnt;
    }

    /**
     * 查询搜索结果数量
     * @param keyword 搜索关键词
     * @return 匹配的商品数量
     * @throws SQLException 数据库异常
     */
    public int countSearch(String keyword) throws SQLException {
        String sql = "SELECT COUNT(*) FROM product WHERE name LIKE ? OR description LIKE ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, "%"+keyword+"%"); 
        ps.setString(2, "%"+keyword+"%");
        ResultSet rs = ps.executeQuery();
        int cnt = rs.next() ? rs.getInt(1) : 0;
        rs.close(); 
        ps.close(); 
        return cnt;
    }

    /**
     * 查询最新商品（用于首页展示）
     * @param limit 返回数量
     * @return 最新商品列表（按ID降序）
     * @throws SQLException 数据库异常
     */
    public List<Product> findLatest(int limit) throws SQLException {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*,c.name as category_name FROM product p LEFT JOIN category c ON p.category_id=c.id WHERE p.status=1 ORDER BY p.id DESC LIMIT ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, limit);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) { 
            list.add(mapProduct(rs)); 
        }
        rs.close(); 
        ps.close(); 
        return list;
    }

    /**
     * 将ResultSet映射为Product对象（私有辅助方法）
     * @param rs 查询结果集
     * @return Product对象
     * @throws SQLException 数据库异常
     */
    private Product mapProduct(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getInt("id")); 
        p.setCategoryId(rs.getInt("category_id"));
        p.setName(rs.getString("name")); 
        p.setDescription(rs.getString("description"));
        p.setPrice(rs.getDouble("price")); 
        p.setStock(rs.getInt("stock"));
        p.setImage(rs.getString("image")); 
        p.setStatus(rs.getInt("status"));
        p.setSales(rs.getInt("sales")); 
        p.setCreateTime(rs.getString("create_time"));
        // 分类名称可能为空（LEFT JOIN），需要异常处理
        try { 
            p.setCategoryName(rs.getString("category_name")); 
        } catch (Exception e) {}
        return p;
    }
}