/**
 * 跨域过滤器
 * 解决前端和后端不同域名/端口之间的请求限制问题
 * 
 * 使用@WebFilter("/*")注解，对所有请求生效
 * 
 * 跨域(CORS)是浏览器的安全策略，默认不允许不同域名的前端访问后端API
 * 此过滤器通过设置HTTP响应头，允许跨域请求
 */
/* 成员4: 数据访问层 - 跨域过滤器 */
package com.shop.common;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * CORS过滤器
 * 允许所有来源的跨域请求
 */
@WebFilter("/*")
public class CORSFilter implements Filter {
    
    /** 过滤器初始化方法 */
    public void init(FilterConfig config) {}

    /**
     * 过滤方法，设置跨域响应头
     * @param req 请求对象
     * @param res 响应对象
     * @param chain 过滤器链
     */
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        // 转换为HTTP请求/响应对象
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        
        // 允许所有来源访问（生产环境应该限制特定域名）
        response.setHeader("Access-Control-Allow-Origin", "*");
        
        // 允许的HTTP方法
        response.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
        
        // 允许的请求头
        response.setHeader("Access-Control-Allow-Headers", "Content-Type,Authorization");
        
        // 设置响应内容类型为JSON
        response.setContentType("application/json;charset=utf-8");
        response.setCharacterEncoding("UTF-8");
        
        // 处理OPTIONS预检请求（浏览器在发送实际请求前会先发一个OPTIONS请求）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK); 
            return;
        }
        
        // 继续执行过滤器链（放行）
        chain.doFilter(request, response);
    }

    /** 过滤器销毁方法 */
    public void destroy() {}
}