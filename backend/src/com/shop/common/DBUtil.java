/**
 * 数据库连接工具类
 * 负责建立和关闭数据库连接
 * 
 * 使用方法:
 * Connection conn = DBUtil.getConnection();
 * // 执行SQL操作
 * DBUtil.close(conn, stmt, rs);
 */
/* 成员4: 数据访问层 - 数据库连接工具类 */
package com.shop.common;
import java.sql.*;

public class DBUtil {
    /** 数据库驱动类名 */
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    
    /** 
     * 数据库连接URL
     * jdbc:mysql://主机:端口/数据库名?参数
     * useSSL=false: 不使用SSL连接
     * characterEncoding=utf-8: 使用UTF-8编码
     * serverTimezone=Asia/Shanghai: 设置时区为上海
     * allowPublicKeyRetrieval=true: 允许公钥检索（解决连接错误）
     */
    private static final String URL = "jdbc:mysql://localhost:3306/shopping_mall?useSSL=false&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
    
    /** 数据库用户名 */
    private static final String USERNAME = "root";
    
    /** 数据库密码 */
    private static final String PASSWORD = "abcfff";

    /**
     * 获取数据库连接
     * @return Connection对象，如果连接失败返回null
     */
    public static Connection getConnection() {
        Connection conn = null;
        try { 
            // 加载JDBC驱动
            Class.forName(DRIVER); 
            // 建立数据库连接
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD); 
        } catch (ClassNotFoundException e) {
            // 驱动类找不到
            System.err.println("数据库驱动加载失败: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            // 数据库连接失败
            System.err.println("数据库连接失败: " + e.getMessage());
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * 关闭数据库资源
     * 按照 ResultSet -> Statement -> Connection 的顺序关闭
     * @param conn 数据库连接对象
     * @param stmt SQL语句执行对象
     * @param rs 查询结果集对象
     */
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        // 关闭ResultSet
        try { 
            if (rs != null) rs.close(); 
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        // 关闭Statement
        try { 
            if (stmt != null) stmt.close(); 
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        // 关闭Connection
        try { 
            if (conn != null) conn.close(); 
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
    }
}