/**
 * 用户接口Servlet
 * 处理前端发起的用户相关HTTP请求
 * 
 * 使用@WebServlet("/api/user/*")注解，所有/api/user/开头的请求都会路由到这里
 * 通过getPathInfo()区分不同操作（/login, /register）
 */
/* 成员2: 用户模块 - 用户接口Servlet */
package com.shop.user;
import com.shop.common.JsonUtil;
import com.shop.common.Result;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

/**
 * 用户接口控制器
 * 提供用户注册、登录、信息查询、信息更新等API接口
 */
@WebServlet("/api/user/*")
public class UserServlet extends HttpServlet {

    /**
     * 处理GET请求
     * 当前用于获取用户信息（需要登录）
     */
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 设置响应内容类型为JSON
        resp.setContentType("application/json;charset=utf-8");
        
        try { 
            // 从请求头获取Authorization（用户ID）
            String userId = req.getHeader("Authorization");
            
            // 未登录，返回401
            if (userId == null) { 
                resp.getWriter().write(JsonUtil.resultToJson(Result.unauthorized())); 
                return; 
            }
            
            // 创建服务对象，调用获取用户信息方法
            UserService svc = new UserService(null);
            resp.getWriter().write(JsonUtil.resultToJson(svc.getUserInfo(Integer.parseInt(userId))));
            
        } catch (Exception e) { 
            // 服务器内部错误，打印日志并返回500
            e.printStackTrace(); 
            resp.getWriter().write(JsonUtil.resultToJson(Result.serverError())); 
        }
    }

    /**
     * 处理POST请求
     * 用于登录和注册操作
     */
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        
        try { 
            // 获取请求路径（如 /login, /register）
            String path = req.getPathInfo(); 
            // 读取请求体并解析为Map
            Map body = JsonUtil.toMap(readBody(req));
            
            UserService svc = new UserService(null); 
            Result result;
            
            // 根据路径分发到不同处理方法
            if ("/login".equals(path)) {
                // 登录：获取用户名和密码
                result = svc.login((String)body.get("username"), (String)body.get("password"));
            } else if ("/register".equals(path)) {
                // 注册：获取用户名和密码
                result = svc.register((String)body.get("username"), (String)body.get("password"));
            } else {
                // 未知操作
                result = Result.badRequest("未知操作");
            }
            
            // 返回JSON响应
            resp.getWriter().write(JsonUtil.resultToJson(result));
            
        } catch (Exception e) { 
            e.printStackTrace(); 
            resp.getWriter().write(JsonUtil.resultToJson(Result.serverError())); 
        }
    }

    /**
     * 处理PUT请求
     * 用于更新用户信息（昵称、手机号）
     */
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        
        try { 
            // 验证登录状态
            String userId = req.getHeader("Authorization");
            if (userId == null) { 
                resp.getWriter().write(JsonUtil.resultToJson(Result.unauthorized())); 
                return; 
            }
            
            // 解析请求体
            Map body = JsonUtil.toMap(readBody(req));
            
            // 创建用户对象，设置ID和要更新的字段
            User u = new User(); 
            u.setId(Integer.parseInt(userId));
            if (body.get("nickname") != null) {
                u.setNickname((String)body.get("nickname"));
            }
            if (body.get("phone") != null) {
                u.setPhone((String)body.get("phone"));
            }
            
            // 更新用户信息
            UserService svc = new UserService(null);
            resp.getWriter().write(JsonUtil.resultToJson(svc.updateUserInfo(u)));
            
        } catch (Exception e) { 
            e.printStackTrace(); 
            resp.getWriter().write(JsonUtil.resultToJson(Result.serverError())); 
        }
    }

    /**
     * 读取HTTP请求体内容（私有辅助方法）
     * @param req HTTP请求对象
     * @return 请求体字符串
     * @throws IOException IO异常
     */
    private String readBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder(); 
        BufferedReader br = req.getReader(); 
        String line;
        // 逐行读取请求体
        while ((line = br.readLine()) != null) { 
            sb.append(line); 
        } 
        return sb.toString();
    }
}