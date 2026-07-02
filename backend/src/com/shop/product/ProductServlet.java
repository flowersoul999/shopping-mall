/* 成员2: 商品模块 - 商品接口Servlet */
package com.shop.product;
import com.shop.common.JsonUtil;
import com.shop.common.Result;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
@WebServlet("/api/product/*")
public class ProductServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        try { String path = req.getPathInfo();
            ProductService svc = new ProductService();
            Result result;
            if (path == null || "/list".equals(path)) {
                int page = 1, pageSize = 12; Integer categoryId = null;
                if (req.getParameter("page") != null) page = Integer.parseInt(req.getParameter("page"));
                if (req.getParameter("pageSize") != null) pageSize = Integer.parseInt(req.getParameter("pageSize"));
                if (req.getParameter("categoryId") != null) categoryId = Integer.parseInt(req.getParameter("categoryId"));
                result = svc.getList(page, pageSize, categoryId);
            } else if ("/search".equals(path)) {
                String keyword = req.getParameter("keyword");
                int page = req.getParameter("page") != null ? Integer.parseInt(req.getParameter("page")) : 1;
                int pageSize = req.getParameter("pageSize") != null ? Integer.parseInt(req.getParameter("pageSize")) : 12;
                result = svc.search(keyword != null ? keyword : "", page, pageSize);
            } else if ("/detail".equals(path)) {
                int id = Integer.parseInt(req.getParameter("id"));
                result = svc.getDetail(id);
            } else if ("/latest".equals(path)) {
                int limit = req.getParameter("limit") != null ? Integer.parseInt(req.getParameter("limit")) : 8;
                result = svc.getLatest(limit);
            } else {
                result = Result.error(404, "接口不存在");
            }
            resp.getWriter().write(JsonUtil.resultToJson(result));
        } catch (Exception e) { e.printStackTrace(); resp.getWriter().write(JsonUtil.resultToJson(Result.serverError())); }
    }
}
