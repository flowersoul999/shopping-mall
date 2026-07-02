/* 成员1: 用户模块 - 用户接口Servlet */
package com.shop.user;
import com.shop.common.JsonUtil;
import com.shop.common.Result;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
@WebServlet("/api/user/*")
public class UserServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        try { String userId = req.getHeader("Authorization");
            if (userId == null) { resp.getWriter().write(JsonUtil.resultToJson(Result.unauthorized())); return; }
            UserService svc = new UserService(null);
            resp.getWriter().write(JsonUtil.resultToJson(svc.getUserInfo(Integer.parseInt(userId))));
        } catch (Exception e) { e.printStackTrace(); resp.getWriter().write(JsonUtil.resultToJson(Result.serverError())); }
    }
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        try { String path = req.getPathInfo(); Map body = JsonUtil.toMap(readBody(req));
            UserService svc = new UserService(null); Result result;
            if ("/login".equals(path)) result = svc.login((String)body.get("username"), (String)body.get("password"));
            else if ("/register".equals(path)) result = svc.register((String)body.get("username"), (String)body.get("password"));
            else result = Result.badRequest("未知操作");
            resp.getWriter().write(JsonUtil.resultToJson(result));
        } catch (Exception e) { e.printStackTrace(); resp.getWriter().write(JsonUtil.resultToJson(Result.serverError())); }
    }
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        try { String userId = req.getHeader("Authorization");
            if (userId == null) { resp.getWriter().write(JsonUtil.resultToJson(Result.unauthorized())); return; }
            Map body = JsonUtil.toMap(readBody(req));
            User u = new User(); u.setId(Integer.parseInt(userId));
            if (body.get("nickname") != null) u.setNickname((String)body.get("nickname"));
            if (body.get("phone") != null) u.setPhone((String)body.get("phone"));
            UserService svc = new UserService(null);
            resp.getWriter().write(JsonUtil.resultToJson(svc.updateUserInfo(u)));
        } catch (Exception e) { e.printStackTrace(); resp.getWriter().write(JsonUtil.resultToJson(Result.serverError())); }
    }
    private String readBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder(); BufferedReader br = req.getReader(); String line;
        while ((line = br.readLine()) != null) sb.append(line); return sb.toString();
    }
}
