/**
 * 用户数据访问对象（DAO层）
 * 负责用户表(user)的数据库操作
 * 
 * 主要功能: 用户登录验证、注册、信息查询、更新、密码修改等
 */
/* 成员1: 数据访问层 - 用户数据访问对象 */
package dao;
import com.shop.user.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    
    /** 数据库连接对象 */
    private Connection conn;
    
    /**
     * 构造方法，注入数据库连接
     * @param conn 数据库连接对象
     */
    public UserDAO(Connection conn) { 
        this.conn = conn; 
    }

    /**
     * 根据用户名查询用户（登录验证用）
     * @param username 用户名
     * @return User对象，如果不存在返回null
     * @throws SQLException 数据库异常
     */
    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM user WHERE username = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();
        User user = null;
        if (rs.next()) { 
            user = mapUser(rs); 
        }
        rs.close(); 
        ps.close();
        return user;
    }

    /**
     * 根据ID查询用户
     * @param id 用户ID
     * @return User对象，如果不存在返回null
     * @throws SQLException 数据库异常
     */
    public User findById(int id) throws SQLException {
        String sql = "SELECT * FROM user WHERE id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        User user = null;
        if (rs.next()) { 
            user = mapUser(rs); 
        }
        rs.close(); 
        ps.close();
        return user;
    }

    /**
     * 插入新用户（注册）
     * 默认角色为'user'（普通用户）
     * @param user 用户对象
     * @return 新插入用户的ID
     * @throws SQLException 数据库异常
     */
    public int insert(User user) throws SQLException {
        String sql = "INSERT INTO user (username, password, nickname, role) VALUES (?, ?, ?, 'user')";
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, user.getUsername());
        ps.setString(2, user.getPassword());
        ps.setString(3, user.getNickname() != null ? user.getNickname() : user.getUsername());
        ps.executeUpdate();
        // 获取自动生成的主键ID
        ResultSet rs = ps.getGeneratedKeys();
        int id = 0;
        if (rs.next()) { 
            id = rs.getInt(1); 
        }
        rs.close(); 
        ps.close();
        return id;
    }

    /**
     * 更新用户信息（昵称、手机号、头像）
     * @param user 用户对象（包含ID和要更新的字段）
     * @throws SQLException 数据库异常
     */
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

    /**
     * 更新密码
     * @param userId 用户ID
     * @param newPassword 新密码（已加密）
     * @throws SQLException 数据库异常
     */
    public void updatePassword(int userId, String newPassword) throws SQLException {
        String sql = "UPDATE user SET password=? WHERE id=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, newPassword);
        ps.setInt(2, userId);
        ps.executeUpdate();
        ps.close();
    }

    /**
     * 查询所有用户（后台管理用）- 分页查询
     * @param page 页码（从1开始）
     * @param size 每页数量
     * @return 用户列表
     * @throws SQLException 数据库异常
     */
    public List<User> findAll(int page, int size) throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM user ORDER BY create_time DESC LIMIT ?, ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, (page - 1) * size);  // 计算偏移量
        ps.setInt(2, size);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) { 
            list.add(mapUser(rs)); 
        }
        rs.close(); 
        ps.close();
        return list;
    }

    /**
     * 查询用户总数（分页用）
     * @return 用户总数
     * @throws SQLException 数据库异常
     */
    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM user";
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sql);
        int cnt = 0;
        if (rs.next()) {
            cnt = rs.getInt(1);
        }
        rs.close(); 
        st.close();
        return cnt;
    }

    /**
     * 更新用户状态（禁用/启用）
     * @param userId 用户ID
     * @param status 状态值（0禁用，1启用）
     * @throws SQLException 数据库异常
     */
    public void updateStatus(int userId, int status) throws SQLException {
        String sql = "UPDATE user SET status=? WHERE id=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, status);
        ps.setInt(2, userId);
        ps.executeUpdate();
        ps.close();
    }

    /**
     * 将ResultSet映射为User对象（私有辅助方法）
     * @param rs 查询结果集
     * @return User对象
     * @throws SQLException 数据库异常
     */
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