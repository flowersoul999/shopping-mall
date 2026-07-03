/**
 * 用户业务逻辑类（Service层）
 * 处理用户相关的业务逻辑，如注册、登录、信息更新等
 * 
 * 调用UserDAO进行数据库操作，不直接操作数据库
 */
/* 成员2: 用户模块 - 用户业务逻辑 */
package com.shop.user;
import dao.UserDAO;
import com.shop.common.DBUtil;
import com.shop.common.Result;
import java.sql.Connection;
import java.security.MessageDigest;

public class UserService {
    
    /** 用户数据访问对象 */
    private UserDAO userDAO;
    
    /**
     * 构造方法，注入数据库连接
     * @param conn 数据库连接对象（可为null，内部会重新获取）
     */
    public UserService(Connection conn) { 
        if (conn != null) {
            this.userDAO = new UserDAO(conn);
        }
    }

    /**
     * MD5加密方法（静态方法）
     * 用于密码加密存储，防止明文存储密码
     * @param str 要加密的字符串
     * @return MD5加密后的十六进制字符串
     */
    public static String md5(String str) {
        try { 
            // 获取MD5消息摘要实例
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 将字符串转换为字节数组并计算摘要
            byte[] digest = md.digest(str.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            // 将字节数组转换为十六进制字符串
            for (byte b : digest) { 
                sb.append(String.format("%02x", b)); 
            }
            return sb.toString();
        } catch (Exception e) { 
            // 加密失败返回原字符串（生产环境应抛出异常）
            return str; 
        }
    }

    /**
     * 用户注册
     * 验证用户名和密码，检查用户名是否已存在，创建新用户
     * @param username 用户名
     * @param password 密码
     * @return Result结果对象
     * @throws Exception 数据库异常
     */
    public Result register(String username, String password) throws Exception {
        // 参数验证：用户名不能为空
        if (username == null || username.trim().isEmpty()) {
            return Result.badRequest("用户名不能为空");
        }
        // 参数验证：密码不能少于6位
        if (password == null || password.length() < 6) {
            return Result.badRequest("密码不能少于6位");
        }
        
        // 获取数据库连接
        Connection conn = DBUtil.getConnection(); 
        UserDAO dao = new UserDAO(conn);
        
        // 检查用户名是否已存在
        if (dao.findByUsername(username.trim()) != null) { 
            conn.close(); 
            return Result.badRequest("用户名已存在"); 
        }
        
        // 创建用户对象，密码使用MD5加密
        User u = new User(); 
        u.setUsername(username.trim()); 
        u.setPassword(md5(password)); 
        u.setNickname(username.trim());
        
        // 插入用户记录
        int id = dao.insert(u); 
        conn.close();
        
        // 返回结果
        return id > 0 ? Result.success("注册成功") : Result.serverError();
    }

    /**
     * 用户登录
     * 验证用户名和密码，返回用户信息（隐藏密码）
     * @param username 用户名
     * @param password 密码
     * @return Result结果对象（包含用户信息）
     * @throws Exception 数据库异常
     */
    public Result login(String username, String password) throws Exception {
        // 获取数据库连接
        Connection conn = DBUtil.getConnection(); 
        UserDAO dao = new UserDAO(conn);
        
        // 根据用户名查询用户
        User user = dao.findByUsername(username); 
        conn.close();
        
        // 用户不存在
        if (user == null) {
            return Result.badRequest("用户不存在");
        }
        
        // 账号已被禁用
        if (user.getStatus() == 0) {
            return Result.error(403, "账号已被禁用");
        }
        
        // 密码验证：将输入密码MD5加密后与数据库中的密码比较
        String encrypted = md5(password);
        if (!user.getPassword().equals(encrypted)) {
            return Result.badRequest("密码错误");
        }
        
        // 返回用户信息时，清除密码字段（安全考虑）
        user.setPassword(null); 
        return Result.success("登录成功", user);
    }

    /**
     * 获取用户信息
     * @param userId 用户ID
     * @return Result结果对象（包含用户信息）
     * @throws Exception 数据库异常
     */
    public Result getUserInfo(int userId) throws Exception {
        Connection conn = DBUtil.getConnection(); 
        UserDAO dao = new UserDAO(conn);
        User user = dao.findById(userId); 
        conn.close();
        
        // 清除密码字段
        user.setPassword(null); 
        return Result.success(user);
    }

    /**
     * 更新用户信息（昵称、手机号）
     * @param input 用户输入的更新信息
     * @return Result结果对象
     * @throws Exception 数据库异常
     */
    public Result updateUserInfo(User input) throws Exception {
        Connection conn = DBUtil.getConnection(); 
        UserDAO dao = new UserDAO(conn);
        
        // 查询现有用户
        User user = dao.findById(input.getId());
        
        // 更新昵称（如果提供）
        if (input.getNickname() != null) {
            user.setNickname(input.getNickname());
        }
        
        // 更新手机号（如果提供）
        if (input.getPhone() != null) {
            user.setPhone(input.getPhone());
        }
        
        // 保存更新
        dao.update(user); 
        conn.close();
        
        // 返回更新后的用户信息
        user.setPassword(null); 
        return Result.success(user);
    }

    /**
     * 修改密码
     * 需要验证原密码，然后更新为新密码
     * @param userId 用户ID
     * @param oldPwd 原密码
     * @param newPwd 新密码
     * @return Result结果对象
     * @throws Exception 数据库异常
     */
    public Result changePassword(int userId, String oldPwd, String newPwd) throws Exception {
        Connection conn = DBUtil.getConnection(); 
        UserDAO dao = new UserDAO(conn);
        
        // 查询用户
        User user = dao.findById(userId);
        
        // 验证原密码
        if (!user.getPassword().equals(md5(oldPwd))) { 
            conn.close(); 
            return Result.badRequest("原密码错误"); 
        }
        
        // 更新密码（使用MD5加密）
        dao.updatePassword(userId, md5(newPwd)); 
        conn.close();
        
        return Result.success("密码修改成功");
    }
}