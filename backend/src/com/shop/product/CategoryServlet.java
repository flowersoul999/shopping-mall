/**
 * 分类接口Servlet
 * 处理前端发起的分类相关HTTP请求
 * 
 * 使用@WebServlet("/api/category/*")注解，所有/api/category/开头的请求都会路由到这里
 */
/* 成员2: 商品模块 - 分类接口Servlet */
package com.shop.product;
import com.shop.common.JsonUtil;
import com.shop.common.Result;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * 分类接口控制器
 * 提供分类列表等API接口
 */
@WebServlet("/api/category/*")
public class CategoryServlet extends HttpServlet {

    /**
     * 处理GET请求
     * 根据路径分发到不同处理方法
     */
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 设置响应内容类型为JSON
        resp.setContentType("application/json;charset=utf-8");
        
        try { 
            // 获取请求路径
            String path = req.getPathInfo();
            CategoryService svc = new CategoryService();
            Result result;
            
            // 根据路径分发
            if (path == null || "/list".equals(path)) {
                // 获取分类列表
                result = svc.getList();
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