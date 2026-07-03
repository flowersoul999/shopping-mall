/* 成员3: 购物车模块 - 购物车接口Servlet */
package com.shop.cart;

import com.shop.common.JsonUtil;
import com.shop.common.Result;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

/**
 * 购物车接口Servlet
 * 处理购物车相关的HTTP请求，提供RESTful API
 * 接口路径: /api/cart/*
 */
@WebServlet("/api/cart/*")
public class CartServlet extends HttpServlet {

    /**
     * 从请求头Authorization中获取用户ID
     * @param req HTTP请求对象
     * @return 用户ID，失败返回-1
     */
    private int getUserId(HttpServletRequest req) {
        try {
            return Integer.parseInt(req.getHeader("Authorization"));
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * 读取HTTP请求体内容
     * @param req HTTP请求对象
     * @return 请求体字符串
     * @throws IOException IO异常
     */
    private String readBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = req.getReader();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    /**
     * GET请求处理 - 获取购物车列表
     * 请求路径: GET /api/cart/
     * @param req HTTP请求对象
     * @param resp HTTP响应对象
     * @throws IOException IO异常
     */
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        try {
            int uid = getUserId(req);
            // 用户未登录
            if (uid <= 0) {
                resp.getWriter().write(JsonUtil.resultToJson(Result.unauthorized()));
                return;
            }
            // 获取购物车列表
            resp.getWriter().write(JsonUtil.resultToJson(new CartService().getList(uid)));
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write(JsonUtil.resultToJson(Result.serverError()));
        }
    }

    /**
     * POST请求处理 - 添加商品到购物车
     * 请求路径: POST /api/cart/
     * 请求体: {"productId": 商品ID, "quantity": 数量}
     * @param req HTTP请求对象
     * @param resp HTTP响应对象
     * @throws IOException IO异常
     */
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        try {
            int uid = getUserId(req);
            // 用户未登录
            if (uid <= 0) {
                resp.getWriter().write(JsonUtil.resultToJson(Result.unauthorized()));
                return;
            }
            // 解析请求体
            Map body = JsonUtil.toMap(readBody(req));
            int pid = ((Number) body.get("productId")).intValue();
            int qty = body.get("quantity") != null ? ((Number) body.get("quantity")).intValue() : 1;
            // 添加到购物车
            resp.getWriter().write(JsonUtil.resultToJson(new CartService().add(uid, pid, qty)));
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write(JsonUtil.resultToJson(Result.serverError()));
        }
    }

    /**
     * PUT请求处理 - 更新购物车项数量
     * 请求路径: PUT /api/cart/
     * 请求体: {"id": 购物车项ID, "quantity": 新数量}
     * @param req HTTP请求对象
     * @param resp HTTP响应对象
     * @throws IOException IO异常
     */
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        try {
            Map body = JsonUtil.toMap(readBody(req));
            int id = ((Number) body.get("id")).intValue();
            int qty = ((Number) body.get("quantity")).intValue();
            // 更新数量
            resp.getWriter().write(JsonUtil.resultToJson(new CartService().updateQuantity(id, qty)));
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write(JsonUtil.resultToJson(Result.serverError()));
        }
    }

    /**
     * DELETE请求处理 - 删除购物车项或清空购物车
     * 请求路径: DELETE /api/cart/{id} - 删除单个购物车项
     * 请求路径: DELETE /api/cart/clear - 清空购物车
     * @param req HTTP请求对象
     * @param resp HTTP响应对象
     * @throws IOException IO异常
     */
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        try {
            String path = req.getPathInfo();
            if (path != null && path.contains("clear")) {
                // 清空购物车
                resp.getWriter().write(JsonUtil.resultToJson(new CartService().clear(getUserId(req))));
            } else {
                // 删除单个购物车项
                int id = Integer.parseInt(path.replace("/", ""));
                resp.getWriter().write(JsonUtil.resultToJson(new CartService().delete(id)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write(JsonUtil.resultToJson(Result.serverError()));
        }
    }
}