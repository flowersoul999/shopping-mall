/* 成员1: 用户模块 - 地址接口Servlet */
package com.shop.address;

import com.shop.common.JsonUtil;
import com.shop.common.Result;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

/**
 * 地址接口Servlet
 * 处理收货地址相关的HTTP请求，提供RESTful API
 * 接口路径: /api/address/*
 */
@WebServlet("/api/address/*")
public class AddressServlet extends HttpServlet {

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
     * GET请求处理 - 获取用户地址列表
     * 请求路径: GET /api/address/
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
            // 获取地址列表
            resp.getWriter().write(JsonUtil.resultToJson(new AddressService().getList(uid)));
        } catch (Exception e) {
            resp.getWriter().write(JsonUtil.resultToJson(Result.serverError()));
        }
    }

    /**
     * POST请求处理 - 添加收货地址
     * 请求路径: POST /api/address/
     * 请求体: {"name": "收货人", "phone": "电话", "province": "省", "city": "市", "district": "区", "detail": "详细地址", "isDefault": true/false}
     * @param req HTTP请求对象
     * @param resp HTTP响应对象
     * @throws IOException IO异常
     */
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        try {
            int uid = getUserId(req);
            Map body = JsonUtil.toMap(readBody(req));
            Address a = new Address();
            a.setUserId(uid);
            a.setName((String) body.get("name"));
            a.setPhone((String) body.get("phone"));
            a.setProvince((String) body.get("province"));
            a.setCity((String) body.get("city"));
            a.setDistrict((String) body.get("district"));
            a.setDetail((String) body.get("detail"));
            Object def = body.get("isDefault");
            a.setIsDefault(def != null && (Boolean) def ? 1 : 0);
            // 添加地址
            resp.getWriter().write(JsonUtil.resultToJson(new AddressService().add(a)));
        } catch (Exception e) {
            resp.getWriter().write(JsonUtil.resultToJson(Result.serverError()));
        }
    }

    /**
     * PUT请求处理 - 更新收货地址
     * 请求路径: PUT /api/address/
     * 请求体: {"id": 地址ID, "name": "收货人", "phone": "电话", "province": "省", "city": "市", "district": "区", "detail": "详细地址", "isDefault": true/false}
     * @param req HTTP请求对象
     * @param resp HTTP响应对象
     * @throws IOException IO异常
     */
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        try {
            Map body = JsonUtil.toMap(readBody(req));
            int id = Double.valueOf(body.get("id").toString()).intValue();
            int uid = getUserId(req);
            Address a = new Address();
            a.setId(id);
            a.setUserId(uid);
            a.setName((String) body.get("name"));
            a.setPhone((String) body.get("phone"));
            a.setProvince((String) body.get("province"));
            a.setCity((String) body.get("city"));
            a.setDistrict((String) body.get("district"));
            a.setDetail((String) body.get("detail"));
            Object def = body.get("isDefault");
            a.setIsDefault(def != null && (Boolean) def ? 1 : 0);
            // 更新地址
            resp.getWriter().write(JsonUtil.resultToJson(new AddressService().update(a)));
        } catch (Exception e) {
            resp.getWriter().write(JsonUtil.resultToJson(Result.serverError()));
        }
    }

    /**
     * DELETE请求处理 - 删除收货地址
     * 请求路径: DELETE /api/address/{id}
     * @param req HTTP请求对象
     * @param resp HTTP响应对象
     * @throws IOException IO异常
     */
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        try {
            String path = req.getPathInfo();
            int id = Integer.parseInt(path.replace("/", ""));
            // 删除地址
            resp.getWriter().write(JsonUtil.resultToJson(new AddressService().delete(id)));
        } catch (Exception e) {
            resp.getWriter().write(JsonUtil.resultToJson(Result.serverError()));
        }
    }
}