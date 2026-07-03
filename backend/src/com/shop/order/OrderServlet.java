/* 成员3: 订单模块 - 订单接口Servlet */
package com.shop.order;

import com.shop.common.JsonUtil;
import com.shop.common.Result;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

/**
 * 订单接口Servlet
 * 处理订单相关的HTTP请求，提供RESTful API
 * 接口路径: /api/order/*
 */
@WebServlet("/api/order/*")
public class OrderServlet extends HttpServlet {

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
     * GET请求处理 - 获取订单列表或订单详情
     * 请求路径: GET /api/order/list - 用户订单列表（分页）
     * 请求路径: GET /api/order/detail?id={订单ID} - 订单详情
     * 请求路径: GET /api/order/admin/list - 后台订单列表（分页）
     * @param req HTTP请求对象
     * @param resp HTTP响应对象
     * @throws IOException IO异常
     */
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        try {
            String path = req.getPathInfo();
            int uid = getUserId(req);
            OrderService svc = new OrderService();
            Result result;

            // 后台订单列表
            if ("/admin/list".equals(path)) {
                int page = req.getParameter("page") != null ? Integer.parseInt(req.getParameter("page")) : 1;
                int ps = req.getParameter("pageSize") != null ? Integer.parseInt(req.getParameter("pageSize")) : 10;
                String status = req.getParameter("status");
                result = svc.getAdminList(page, ps, status);

            // 用户未登录
            } else if (uid <= 0) {
                resp.getWriter().write(JsonUtil.resultToJson(Result.unauthorized()));
                return;

            // 用户订单列表
            } else if ("/list".equals(path)) {
                int page = req.getParameter("page") != null ? Integer.parseInt(req.getParameter("page")) : 1;
                int ps = req.getParameter("pageSize") != null ? Integer.parseInt(req.getParameter("pageSize")) : 10;
                String status = req.getParameter("status");
                result = svc.getList(uid, page, ps, status);

            // 订单详情
            } else if ("/detail".equals(path)) {
                int id = Integer.parseInt(req.getParameter("id"));
                result = svc.getDetail(id);

            // 接口不存在
            } else {
                result = Result.error(404, "接口不存在");
            }

            resp.getWriter().write(JsonUtil.resultToJson(result));

        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write(JsonUtil.resultToJson(Result.serverError()));
        }
    }

    /**
     * POST请求处理 - 创建订单
     * 请求路径: POST /api/order/
     * 请求体: {"addressId": 地址ID, "remark": 备注}
     * @param req HTTP请求对象
     * @param resp HTTP响应对象
     * @throws IOException IO异常
     */
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        try {
            int uid = getUserId(req);
            Map body = JsonUtil.toMap(readBody(req));
            int addressId = body.get("addressId") != null ? Double.valueOf(body.get("addressId").toString()).intValue() : 0;
            String remark = (String) body.get("remark");
            // 创建订单
            resp.getWriter().write(JsonUtil.resultToJson(new OrderService().createOrder(uid, addressId, remark)));
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write(JsonUtil.resultToJson(Result.serverError()));
        }
    }

    /**
     * PUT请求处理 - 更新订单状态
     * 请求路径: PUT /api/order/
     * 请求体: {"id": 订单ID, "status": 新状态}
     * @param req HTTP请求对象
     * @param resp HTTP响应对象
     * @throws IOException IO异常
     */
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        try {
            Map body = JsonUtil.toMap(readBody(req));
            int id = Double.valueOf(body.get("id").toString()).intValue();
            String status = (String) body.get("status");
            // 更新订单状态
            resp.getWriter().write(JsonUtil.resultToJson(new OrderService().updateStatus(id, status)));
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write(JsonUtil.resultToJson(Result.serverError()));
        }
    }
}