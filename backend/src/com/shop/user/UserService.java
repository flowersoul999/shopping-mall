/* 成员1: 用户模块 - 用户业务逻辑 */
package com.shop.user;
import dao.UserDAO;
import com.shop.common.DBUtil;
import com.shop.common.Result;
import java.sql.Connection;
import java.security.MessageDigest;
public class UserService {
    private UserDAO userDAO;
    public UserService(Connection conn) { this.userDAO = new UserDAO(conn); }
    public static String md5(String str) {
        try { MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(str.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) { return str; }
    }
    public Result register(String username, String password) throws Exception {
        if (username == null || username.trim().isEmpty()) return Result.badRequest("用户名不能为空");
        if (password == null || password.length() < 6) return Result.badRequest("密码不能少于6位");
        Connection conn = DBUtil.getConnection(); UserDAO dao = new UserDAO(conn);
        if (dao.findByUsername(username.trim()) != null) { conn.close(); return Result.badRequest("用户名已存在"); }
        User u = new User(); u.setUsername(username.trim()); u.setPassword(md5(password)); u.setNickname(username.trim());
        int id = dao.insert(u); conn.close();
        return id > 0 ? Result.success("注册成功") : Result.serverError();
    }
    public Result login(String username, String password) throws Exception {
        Connection conn = DBUtil.getConnection(); UserDAO dao = new UserDAO(conn);
        User user = dao.findByUsername(username); conn.close();
        if (user == null) return Result.badRequest("用户不存在");
        if (user.getStatus() == 0) return Result.error(403, "账号已被禁用");
        String encrypted = md5(password);
        if (!user.getPassword().equals(encrypted)) return Result.badRequest("密码错误");
        user.setPassword(null); return Result.success("登录成功", user);
    }
    public Result getUserInfo(int userId) throws Exception {
        Connection conn = DBUtil.getConnection(); UserDAO dao = new UserDAO(conn);
        User user = dao.findById(userId); conn.close();
        user.setPassword(null); return Result.success(user);
    }
    public Result updateUserInfo(User input) throws Exception {
        Connection conn = DBUtil.getConnection(); UserDAO dao = new UserDAO(conn);
        User user = dao.findById(input.getId());
        if (input.getNickname() != null) user.setNickname(input.getNickname());
        if (input.getPhone() != null) user.setPhone(input.getPhone());
        dao.update(user); conn.close();
        user.setPassword(null); return Result.success(user);
    }
    public Result changePassword(int userId, String oldPwd, String newPwd) throws Exception {
        Connection conn = DBUtil.getConnection(); UserDAO dao = new UserDAO(conn);
        User user = dao.findById(userId);
        if (!user.getPassword().equals(md5(oldPwd))) { conn.close(); return Result.badRequest("原密码错误"); }
        dao.updatePassword(userId, md5(newPwd)); conn.close();
        return Result.success("密码修改成功");
    }
}
