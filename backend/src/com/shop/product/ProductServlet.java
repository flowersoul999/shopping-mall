/**
 * 商品接口Servlet
 * 处理前端发起的商品相关HTTP请求
 * 
 * 使用@WebServlet("/api/product/*")注解，所有/api/product/开头的请求都会路由到这里
 * 通过getPathInfo()区分不同操作（/list, /search, /detail, /latest）
 */
/* 成员2: 商品模块 - 商品接口Servlet */
package com.shop.product;
import com.shop.common.JsonUtil;
import com.shop.common.Result;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * 商品接口控制器
 * 提供商品列表、搜索、详情、最新商品等API接口
 */
@WebServlet("/api/product/*")
public class ProductServlet extends HttpServlet {

    /**
     * 处理GET请求
     * 根据路径分发到不同处理方法
     */
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 设置响应内容类型为JSON
        resp.setContentType("application/json;charset=utf-8");
        
        try { 
            // 获取请求路径（如 /list, /search）
            String path = req.getPathInfo();
            ProductService svc = new ProductService();
            Result result;
            
            // 根据路径分发
            if (path == null || "/list".equals(path)) {
                // 获取商品列表（支持分页和分类筛选）
                int page = 1, pageSize = 12; 
                Integer categoryId = null;
                
                // 获取查询参数
                if (req.getParameter("page") != null) {
                    page = Integer.parseInt(req.getParameter("page"));
                }
                if (req.getParameter("pageSize") != null) {
                    pageSize = Integer.parseInt(req.getParameter("pageSize"));
                }
                if (req.getParameter("categoryId") != null) {
                    categoryId = Integer.parseInt(req.getParameter("categoryId"));
                }
                
                result = svc.getList(page, pageSize, categoryId);
                
            } else if ("/search".equals(path)) {
                // 搜索商品
                String keyword = req.getParameter("keyword");
                int page = req.getParameter("page") != null ? Integer.parseInt(req.getParameter("page")) : 1;
                int pageSize = req.getParameter("pageSize") != null ? Integer.parseInt(req.getParameter("pageSize")) : 12;
                result = svc.search(keyword != null ? keyword : "", page, pageSize);
                
            } else if ("/detail".equals(path)) {
                // 获取商品详情
                int id = Integer.parseInt(req.getParameter("id"));
                result = svc.getDetail(id);
                
            } else if ("/latest".equals(path)) {
                // 获取最新商品
                int limit = req.getParameter("limit") != null ? Integer.parseInt(req.getParameter("limit")) : 8;
                result = svc.getLatest(limit);
                
            } else {
                // 未知接口
                result = Result.error(404, "接口不存在");
            }
            
            // 返回JSON响应
            resp.getWriter().write(JsonUtil.resultToJson(result));
            
        } catch (Exception e) { 
            // 服务器内部错误
            e.printStackTrace(); 
            resp.getWriter().write(JsonUtil.resultToJson(Result.serverError())); 
        }
    }
}