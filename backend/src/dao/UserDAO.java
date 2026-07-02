/* 成员4: 数据访问层 - 用户数据访问对象 */
package dao;
import com.shop.user.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class UserDAO {
    private Connection conn;
    public UserDAO(Connection conn) { this.conn = conn; }
    /** 根据用户名查询用户（登录验证用） */
    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM user WHERE username = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();
        User user = null;
        if (rs.next()) { user = mapUser(rs); }
        rs.close(); ps.close();
        return user;
    }
    /** 根据ID查询用户 */
    public User findById(int id) throws SQLException {
        String sql = "SELECT * FROM user WHERE id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        User user = null;
        if (rs.next()) { user = mapUser(rs); }
        rs.close(); ps.close();
        return user;
    }
    /** 插入新用户（注册） */
    public int insert(User user) throws SQLException {
        String sql = "INSERT INTO user (username, password, nickname, role) VALUES (?, ?, ?, 'user')";
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, user.getUsername());
        ps.setString(2, user.getPassword());
        ps.setString(3, user.getNickname() != null ? user.getNickname() : user.getUsername());
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        int id = 0;
        if (rs.next()) { id = rs.getInt(1); }
        rs.close(); ps.close();
        return id;
    }
    /** 更新用户信息 */
    public void update(User user) throws SQLException {
        String sql = "UPDATE user SET nickname=?, phone=?, avatar=? WHERE id=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, user.getNickname());
        ps.setString(2, user.getPhone());
        ps.setString(3, user.getAvatar());
        ps.setInt(4, user.getId());
        ps.executeUpdate();
        ps.close();
    }
    /** 更新密码 */
    public void updatePassword(int userId, String newPassword) throws SQLException {
        String sql = "UPDATE user SET password=? WHERE id=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, newPassword);
        ps.setInt(2, userId);
        ps.executeUpdate();
        ps.close();
    }
    /** 查询所有用户（后台管理用） */
    public List<User> findAll(int page, int size) throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM user ORDER BY create_time DESC LIMIT ?, ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, (page - 1) * size);
        ps.setInt(2, size);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) { list.add(mapUser(rs)); }
        rs.close(); ps.close();
        return list;
    }
    /** 查询用户总数 */
    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM user";
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sql);
        int cnt = 0;
        if (rs.next()) cnt = rs.getInt(1);
        rs.close(); st.close();
        return cnt;
    }
    /** 更新用户状态（禁用/启用） */
    public void updateStatus(int userId, int status) throws SQLException {
        String sql = "UPDATE user SET status=? WHERE id=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, status);
        ps.setInt(2, userId);
        ps.executeUpdate();
        ps.close();
    }
    private User mapUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setNickname(rs.getString("nickname"));
        u.setPhone(rs.getString("phone"));
        u.setRole(rs.getString("role"));
        u.setAvatar(rs.getString("avatar"));
        u.setStatus(rs.getInt("status"));
        u.setCreateTime(rs.getString("create_time"));
        return u;
    }
}
