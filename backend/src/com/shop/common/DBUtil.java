/* 成员4: 数据访问层 - 数据库连接工具类 */
package com.shop.common;
import java.sql.*;
public class DBUtil {
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/shopping_mall?useSSL=false&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "abcfff";
    public static Connection getConnection() {
        Connection conn = null;
        try { Class.forName(DRIVER); conn = DriverManager.getConnection(URL, USERNAME, PASSWORD); }
        catch (Exception e) { e.printStackTrace(); }
        return conn;
    }
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
    }
}
