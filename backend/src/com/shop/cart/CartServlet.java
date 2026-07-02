/* 成员3: 购物车模块 - 购物车接口Servlet */
package com.shop.cart;
import com.shop.common.JsonUtil;
import com.shop.common.Result;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
@WebServlet("/api/cart/*")
public class CartServlet extends HttpServlet {
    private int getUserId(HttpServletRequest req) {
        try { return Integer.parseInt(req.getHeader("Authorization")); } catch (Exception e) { return -1; }
    }
    private String readBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder(); BufferedReader br = req.getReader(); String line;
        while ((line = br.readLine()) != null) sb.append(line); return sb.toString();
    }
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        try { int uid = getUserId(req); if (uid <= 0) { resp.getWriter().write(JsonUtil.resultToJson(Result.unauthorized())); return; }
            resp.getWriter().write(JsonUtil.resultToJson(new CartService().getList(uid)));
        } catch (Exception e) { resp.getWriter().write(JsonUtil.resultToJson(Result.serverError())); }
    }
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        try { int uid = getUserId(req); Map body = JsonUtil.toMap(readBody(req));
            int pid = Double.valueOf(body.get("productId").toString()).intValue();
            int qty = body.get("quantity") != null ? Double.valueOf(body.get("quantity").toString()).intValue() : 1;
            resp.getWriter().write(JsonUtil.resultToJson(new CartService().add(uid, pid, qty)));
        } catch (Exception e) { resp.getWriter().write(JsonUtil.resultToJson(Result.serverError())); }
    }
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        try { Map body = JsonUtil.toMap(readBody(req));
            int id = Double.valueOf(body.get("id").toString()).intValue();
            int qty = Double.valueOf(body.get("quantity").toString()).intValue();
            resp.getWriter().write(JsonUtil.resultToJson(new CartService().updateQuantity(id, qty)));
        } catch (Exception e) { resp.getWriter().write(JsonUtil.resultToJson(Result.serverError())); }
    }
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        try { String path = req.getPathInfo();
            if (path != null && path.contains("clear")) {
                resp.getWriter().write(JsonUtil.resultToJson(new CartService().clear(getUserId(req))));
            } else {
                int id = Integer.parseInt(path.replace("/", ""));
                resp.getWriter().write(JsonUtil.resultToJson(new CartService().delete(id)));
            }
        } catch (Exception e) { resp.getWriter().write(JsonUtil.resultToJson(Result.serverError())); }
    }
}
