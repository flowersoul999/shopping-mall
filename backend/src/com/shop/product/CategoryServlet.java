/* 成员2: 商品模块 - 分类接口Servlet */
package com.shop.product;
import com.shop.common.JsonUtil;
import com.shop.common.Result;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
@WebServlet("/api/category/*")
public class CategoryServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        try { String path = req.getPathInfo();
            CategoryService svc = new CategoryService();
            Result result;
            if (path == null || "/list".equals(path)) {
                result = svc.getList();
            } else { result = Result.error(404, "接口不存在"); }
            resp.getWriter().write(JsonUtil.resultToJson(result));
        } catch (Exception e) { e.printStackTrace(); resp.getWriter().write(JsonUtil.resultToJson(Result.serverError())); }
    }
}
