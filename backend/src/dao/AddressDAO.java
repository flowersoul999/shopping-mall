/* 成员4: 数据访问层 - 地址数据访问对象 */
package dao;
import com.shop.address.Address;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class AddressDAO {
    private Connection conn;
    public AddressDAO(Connection conn) { this.conn = conn; }
    public List<Address> findByUserId(int userId) throws SQLException {
        List<Address> list = new ArrayList<>();
        String sql = "SELECT * FROM address WHERE user_id=? ORDER BY is_default DESC, id DESC";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) { Address a = new Address(); a.setId(rs.getInt("id")); a.setUserId(rs.getInt("user_id")); a.setName(rs.getString("name")); a.setPhone(rs.getString("phone")); a.setProvince(rs.getString("province")); a.setCity(rs.getString("city")); a.setDistrict(rs.getString("district")); a.setDetail(rs.getString("detail")); a.setIsDefault(rs.getInt("is_default")); list.add(a); }
        rs.close(); ps.close(); return list;
    }
    public Address findById(int id) throws SQLException {
        String sql = "SELECT * FROM address WHERE id=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        Address a = rs.next() ? mapAddress(rs) : null;
        rs.close(); ps.close(); return a;
    }
    public int insert(Address addr) throws SQLException {
        String sql = "INSERT INTO address (user_id,name,phone,province,city,district,detail,is_default) VALUES (?,?,?,?,?,?,?,?)";
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, addr.getUserId()); ps.setString(2, addr.getName()); ps.setString(3, addr.getPhone());
        ps.setString(4, addr.getProvince()); ps.setString(5, addr.getCity()); ps.setString(6, addr.getDistrict());
        ps.setString(7, addr.getDetail()); ps.setInt(8, addr.getIsDefault());
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        int id = rs.next() ? rs.getInt(1) : 0;
        rs.close(); ps.close(); return id;
    }
    public void update(Address addr) throws SQLException {
        String sql = "UPDATE address SET name=?,phone=?,province=?,city=?,district=?,detail=?,is_default=? WHERE id=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, addr.getName()); ps.setString(2, addr.getPhone());
        ps.setString(3, addr.getProvince()); ps.setString(4, addr.getCity());
        ps.setString(5, addr.getDistrict()); ps.setString(6, addr.getDetail());
        ps.setInt(7, addr.getIsDefault()); ps.setInt(8, addr.getId());
        ps.executeUpdate(); ps.close();
    }
    public void delete(int id) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("DELETE FROM address WHERE id=?");
        ps.setInt(1, id); ps.executeUpdate(); ps.close();
    }
    public void clearDefault(int userId) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("UPDATE address SET is_default=0 WHERE user_id=?");
        ps.setInt(1, userId); ps.executeUpdate(); ps.close();
    }
    public void setDefault(int id) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("UPDATE address SET is_default=1 WHERE id=?");
        ps.setInt(1, id); ps.executeUpdate(); ps.close();
    }
    private Address mapAddress(ResultSet rs) throws SQLException {
        Address a = new Address();
        a.setId(rs.getInt("id")); a.setUserId(rs.getInt("user_id"));
        a.setName(rs.getString("name")); a.setPhone(rs.getString("phone"));
        a.setProvince(rs.getString("province")); a.setCity(rs.getString("city"));
        a.setDistrict(rs.getString("district")); a.setDetail(rs.getString("detail"));
        a.setIsDefault(rs.getInt("is_default")); return a;
    }
}
