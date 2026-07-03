/* 成员4: 数据访问层 - 后台概览Servlet */
package com.shop.admin;

import com.shop.common.*;
import dao.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * 后台管理概览接口Servlet
 * 提供后台仪表盘统计数据
 * 接口路径: /api/admin/dashboard
 */
@WebServlet("/api/admin/dashboard")
public class DashboardServlet extends HttpServlet {

    /**
     * GET请求处理 - 获取后台仪表盘统计数据
     * 请求路径: GET /api/admin/dashboard
     * 返回数据包含：用户总数、商品总数、订单总数、总销售额
     * @param req HTTP请求对象
     * @param resp HTTP响应对象
     * @throws IOException IO异常
     */
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        try {
            Connection conn = DBUtil.getConnection();

            // 查询用户总数
            int userCount = new UserDAO(conn).count();
            // 查询商品总数
            int productCount = new ProductDAO(conn).count();
            // 查询订单总数
            int orderCount = new OrderDAO(conn).count();
            // 查询总销售额
            double totalSales = new OrderDAO(conn).sumTotalPrice();

            conn.close();

            // 封装统计数据
            Map<String, Object> data = new HashMap<>();
            data.put("userCount", userCount);
            data.put("productCount", productCount);
            data.put("orderCount", orderCount);
            data.put("totalSales", totalSales);

            // 返回成功结果
            resp.getWriter().write(JsonUtil.resultToJson(Result.success(data)));

        } catch (Exception e) {
            e.printStackTrace();
            try {
                resp.getWriter().write(JsonUtil.resultToJson(Result.serverError()));
            } catch (IOException ex) {}
        }
    }
}