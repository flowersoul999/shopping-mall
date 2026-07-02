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
@WebServlet("/api/admin/dashboard")
public class DashboardServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        try { Connection conn = DBUtil.getConnection();
            int userCount = new UserDAO(conn).count();
            int productCount = new ProductDAO(conn).count();
            int orderCount = new OrderDAO(conn).count();
            double totalSales = new OrderDAO(conn).sumTotalPrice();
            conn.close();
            Map<String, Object> data = new HashMap<>();
            data.put("userCount", userCount); data.put("productCount", productCount);
            data.put("orderCount", orderCount); data.put("totalSales", totalSales);
            resp.getWriter().write(JsonUtil.resultToJson(Result.success(data)));
        } catch (Exception e) { e.printStackTrace();
            try { resp.getWriter().write(JsonUtil.resultToJson(Result.serverError())); }
            catch (IOException ex) {} }
    }
}
