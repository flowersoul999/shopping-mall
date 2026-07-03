/**
 * 地址数据访问对象（DAO层）
 * 负责地址表(address)的数据库操作
 * 
 * 主要功能: 查询地址列表、添加地址、更新地址、删除地址、设置默认地址等
 * 用户可以有多个地址，但只能有一个默认地址
 */
/* 成员1: 数据访问层 - 地址数据访问对象 */
package dao;
import com.shop.address.Address;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AddressDAO {
    
    /** 数据库连接对象 */
    private Connection conn;
    
    /**
     * 构造方法，注入数据库连接
     * @param conn 数据库连接对象
     */
    public AddressDAO(Connection conn) { 
        this.conn = conn; 
    }

    /**
     * 查询用户地址列表
     * 默认地址排在最前面（ORDER BY is_default DESC）
     * @param userId 用户ID
     * @return 地址列表
     * @throws SQLException 数据库异常
     */
    public List<Address> findByUserId(int userId) throws SQLException {
        List<Address> list = new ArrayList<>();
        String sql = "SELECT * FROM address WHERE user_id=? ORDER BY is_default DESC, id DESC";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) { 
            Address a = new Address(); 
            a.setId(rs.getInt("id")); 
            a.setUserId(rs.getInt("user_id")); 
            a.setName(rs.getString("name")); 
            a.setPhone(rs.getString("phone")); 
            a.setProvince(rs.getString("province")); 
            a.setCity(rs.getString("city")); 
            a.setDistrict(rs.getString("district")); 
            a.setDetail(rs.getString("detail")); 
            a.setIsDefault(rs.getInt("is_default")); 
            list.add(a); 
        }
        rs.close(); 
        ps.close(); 
        return list;
    }

    /**
     * 根据ID查询地址
     * @param id 地址ID
     * @return Address对象，如果不存在返回null
     * @throws SQLException 数据库异常
     */
    public Address findById(int id) throws SQLException {
        String sql = "SELECT * FROM address WHERE id=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        Address a = rs.next() ? mapAddress(rs) : null;
        rs.close(); 
        ps.close(); 
        return a;
    }

    /**
     * 插入新地址
     * @param addr 地址对象
     * @return 新插入地址的ID
     * @throws SQLException 数据库异常
     */
    public int insert(Address addr) throws SQLException {
        String sql = "INSERT INTO address (user_id,name,phone,province,city,district,detail,is_default) VALUES (?,?,?,?,?,?,?,?)";
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, addr.getUserId()); 
        ps.setString(2, addr.getName()); 
        ps.setString(3, addr.getPhone());
        ps.setString(4, addr.getProvince()); 
        ps.setString(5, addr.getCity()); 
        ps.setString(6, addr.getDistrict());
        ps.setString(7, addr.getDetail()); 
        ps.setInt(8, addr.getIsDefault());
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        int id = rs.next() ? rs.getInt(1) : 0;
        rs.close(); 
        ps.close(); 
        return id;
    }

    /**
     * 更新地址信息
     * @param addr 地址对象（包含ID和要更新的字段）
     * @throws SQLException 数据库异常
     */
    public void update(Address addr) throws SQLException {
        String sql = "UPDATE address SET name=?,phone=?,province=?,city=?,district=?,detail=?,is_default=? WHERE id=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, addr.getName()); 
        ps.setString(2, addr.getPhone());
        ps.setString(3, addr.getProvince()); 
        ps.setString(4, addr.getCity());
        ps.setString(5, addr.getDistrict()); 
        ps.setString(6, addr.getDetail());
        ps.setInt(7, addr.getIsDefault()); 
        ps.setInt(8, addr.getId());
        ps.executeUpdate(); 
        ps.close();
    }

    /**
     * 删除地址
     * @param id 地址ID
     * @throws SQLException 数据库异常
     */
    public void delete(int id) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("DELETE FROM address WHERE id=?");
        ps.setInt(1, id); 
        ps.executeUpdate(); 
        ps.close();
    }

    /**
     * 清除用户所有地址的默认状态
     * 设置新默认地址前调用此方法
     * @param userId 用户ID
     * @throws SQLException 数据库异常
     */
    public void clearDefault(int userId) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("UPDATE address SET is_default=0 WHERE user_id=?");
        ps.setInt(1, userId); 
        ps.executeUpdate(); 
        ps.close();
    }

    /**
     * 设置某地址为默认地址
     * @param id 地址ID
     * @throws SQLException 数据库异常
     */
    public void setDefault(int id) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("UPDATE address SET is_default=1 WHERE id=?");
        ps.setInt(1, id); 
        ps.executeUpdate(); 
        ps.close();
    }

    /**
     * 将ResultSet映射为Address对象（私有辅助方法）
     * @param rs 查询结果集
     * @return Address对象
     * @throws SQLException 数据库异常
     */
    private Address mapAddress(ResultSet rs) throws SQLException {
        Address a = new Address();
        a.setId(rs.getInt("id")); 
        a.setUserId(rs.getInt("user_id"));
        a.setName(rs.getString("name")); 
        a.setPhone(rs.getString("phone"));
        a.setProvince(rs.getString("province")); 
        a.setCity(rs.getString("city"));
        a.setDistrict(rs.getString("district")); 
        a.setDetail(rs.getString("detail"));
        a.setIsDefault(rs.getInt("is_default")); 
        return a;
    }
}