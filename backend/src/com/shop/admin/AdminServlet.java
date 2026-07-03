/* 成员4: 数据访问层 - 后台管理Servlet */
package com.shop.admin;

import com.shop.common.*;
import dao.*;
import com.shop.product.*;
import com.shop.user.User;
import com.shop.order.Order;
import com.shop.order.OrderService;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.util.*;

/**
 * 后台管理接口Servlet
 * 处理管理员相关的HTTP请求，提供RESTful API
 * 接口路径: /api/admin/*
 * 包含用户管理、商品管理、订单管理、分类管理等功能
 */
@WebServlet("/api/admin/*")
public class AdminServlet extends HttpServlet {

    /**
     * 检查是否为管理员（当前默认返回true，实际项目中应验证管理员权限）
     * @param req HTTP请求对象
     * @return 是否为管理员
     */
    private boolean isAdmin(HttpServletRequest req) {
        return true;
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
     * GET请求处理 - 获取管理数据列表
     * 请求路径: GET /api/admin/users - 用户列表（分页）
     * 请求路径: GET /api/admin/products - 商品列表（分页）
     * 请求路径: GET /api/admin/orders - 订单列表（分页）
     * 请求路径: GET /api/admin/categories - 分类列表
     * @param req HTTP请求对象
     * @param resp HTTP响应对象
     * @throws IOException IO异常
     */
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        try {
            String path = req.getPathInfo();
            Connection conn = DBUtil.getConnection();
            Result result;

            // 用户列表
            if ("/users".equals(path)) {
                int page = req.getParameter("page") != null ? Integer.parseInt(req.getParameter("page")) : 1;
                int ps = req.getParameter("pageSize") != null ? Integer.parseInt(req.getParameter("pageSize")) : 10;
                UserDAO dao = new UserDAO(conn);
                List<User> list = dao.findAll(page, ps);
                int total = dao.count();
                conn.close();
                Map<String, Object> data = new HashMap<>();
                data.put("list", list);
                data.put("total", total);
                result = Result.success(data);

            // 商品列表
            } else if ("/products".equals(path)) {
                int page = req.getParameter("page") != null ? Integer.parseInt(req.getParameter("page")) : 1;
                int ps = req.getParameter("pageSize") != null ? Integer.parseInt(req.getParameter("pageSize")) : 10;
                ProductDAO dao = new ProductDAO(conn);
                result = Result.success(dao.findAll(page, ps));
                conn.close();

            // 订单列表
            } else if ("/orders".equals(path)) {
                int page = req.getParameter("page") != null ? Integer.parseInt(req.getParameter("page")) : 1;
                int ps = req.getParameter("pageSize") != null ? Integer.parseInt(req.getParameter("pageSize")) : 10;
                OrderDAO dao = new OrderDAO(conn);
                List<Order> orders = dao.findAll(page, ps);
                // 为每个订单加载订单项
                for (Order o : orders) {
                    o.setItems(dao.findItemsByOrderId(o.getId()));
                }
                conn.close();
                result = Result.success(orders);

            // 分类列表
            } else if ("/categories".equals(path)) {
                CategoryDAO dao = new CategoryDAO(conn);
                result = Result.success(dao.findAll());
                conn.close();

            // 接口不存在
            } else {
                result = Result.error(404, "接口不存在");
                conn.close();
            }

            resp.getWriter().write(JsonUtil.resultToJson(result));

        } catch (Exception e) {
            e.printStackTrace();
            try {
                resp.getWriter().write(JsonUtil.resultToJson(Result.serverError()));
            } catch (IOException ex) {}
        }
    }

    /**
     * POST请求处理 - 新增管理数据
     * 请求路径: POST /api/admin/products - 新增商品
     * 请求路径: POST /api/admin/categories - 新增分类
     * @param req HTTP请求对象
     * @param resp HTTP响应对象
     * @throws IOException IO异常
     */
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        try {
            String path = req.getPathInfo();
            Connection conn = DBUtil.getConnection();
            Map body = JsonUtil.toMap(readBody(req));
            Result result;

            // 新增商品
            if ("/products".equals(path)) {
                ProductDAO dao = new ProductDAO(conn);
                Product p = new Product();
                p.setCategoryId(Integer.parseInt(body.get("categoryId").toString()));
                p.setName((String) body.get("name"));
                p.setDescription((String) body.get("description"));
                p.setPrice(Double.parseDouble(body.get("price").toString()));
                p.setStock(Integer.parseInt(body.get("stock").toString()));
                p.setImage((String) body.get("image"));
                int id = dao.insert(p);
                conn.close();
                result = id > 0 ? Result.success("新增成功", id) : Result.serverError();

            // 新增分类
            } else if ("/categories".equals(path)) {
                CategoryDAO dao = new CategoryDAO(conn);
                Category c = new Category();
                c.setName((String) body.get("name"));
                c.setDescription((String) body.get("description"));
                c.setSortOrder(body.get("sortOrder") != null ? Integer.parseInt(body.get("sortOrder").toString()) : 0);
                int id = dao.insert(c);
                conn.close();
                result = id > 0 ? Result.success("新增成功", id) : Result.serverError();

            // 接口不存在
            } else {
                result = Result.error(404, "接口不存在");
                conn.close();
            }

            resp.getWriter().write(JsonUtil.resultToJson(result));

        } catch (Exception e) {
            resp.getWriter().write(JsonUtil.resultToJson(Result.serverError()));
        }
    }

    /**
     * PUT请求处理 - 更新管理数据
     * 请求路径: PUT /api/admin/products - 更新商品
     * 请求路径: PUT /api/admin/categories - 更新分类
     * 请求路径: PUT /api/admin/users - 更新用户状态
     * 请求路径: PUT /api/admin/orders/status - 更新订单状态
     * @param req HTTP请求对象
     * @param resp HTTP响应对象
     * @throws IOException IO异常
     */
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        try {
            String path = req.getPathInfo();
            Connection conn = DBUtil.getConnection();
            Map body = JsonUtil.toMap(readBody(req));
            Result result;

            // 更新商品
            if ("/products".equals(path)) {
                ProductDAO dao = new ProductDAO(conn);
                Product p = new Product();
                p.setId(Integer.parseInt(body.get("id").toString()));
                p.setCategoryId(Integer.parseInt(body.get("categoryId").toString()));
                p.setName((String) body.get("name"));
                p.setDescription((String) body.get("description"));
                p.setPrice(Double.parseDouble(body.get("price").toString()));
                p.setStock(Integer.parseInt(body.get("stock").toString()));
                p.setImage((String) body.get("image"));
                p.setStatus(body.get("status") != null ? Integer.parseInt(body.get("status").toString()) : 1);
                dao.update(p);
                conn.close();
                result = Result.success("更新成功");

            // 更新分类
            } else if ("/categories".equals(path)) {
                CategoryDAO dao = new CategoryDAO(conn);
                Category c = new Category();
                c.setId(Integer.parseInt(body.get("id").toString()));
                c.setName((String) body.get("name"));
                c.setDescription((String) body.get("description"));
                c.setSortOrder(body.get("sortOrder") != null ? Integer.parseInt(body.get("sortOrder").toString()) : 0);
                dao.update(c);
                conn.close();
                result = Result.success("更新成功");

            // 更新用户状态
            } else if ("/users".equals(path)) {
                UserDAO dao = new UserDAO(conn);
                int uid = Integer.parseInt(body.get("id").toString());
                int statusVal = Integer.parseInt(body.get("status").toString());
                dao.updateStatus(uid, statusVal);
                conn.close();
                result = Result.success("更新成功");

            // 更新订单状态
            } else if ("/orders/status".equals(path)) {
                OrderDAO dao = new OrderDAO(conn);
                int oid = Integer.parseInt(body.get("id").toString());
                String st = (String) body.get("status");
                dao.updateStatus(oid, st);
                conn.close();
                result = Result.success("更新成功");

            // 接口不存在
            } else {
                result = Result.error(404, "接口不存在");
                conn.close();
            }

            resp.getWriter().write(JsonUtil.resultToJson(result));

        } catch (Exception e) {
            resp.getWriter().write(JsonUtil.resultToJson(Result.serverError()));
        }
    }

    /**
     * DELETE请求处理 - 删除管理数据
     * 请求路径: DELETE /api/admin/products/{id} - 删除商品
     * 请求路径: DELETE /api/admin/categories/{id} - 删除分类
     * @param req HTTP请求对象
     * @param resp HTTP响应对象
     * @throws IOException IO异常
     */
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        try {
            String path = req.getPathInfo();
            Connection conn = DBUtil.getConnection();
            Result result;

            String[] parts = path.split("/");
            int id = Integer.parseInt(parts[1]);

            // 删除商品
            if (path.startsWith("/products/")) {
                new ProductDAO(conn).delete(id);
                conn.close();
                result = Result.success("删除成功");

            // 删除分类
            } else if (path.startsWith("/categories/")) {
                new CategoryDAO(conn).delete(id);
                conn.close();
                result = Result.success("删除成功");

            // 接口不存在
            } else {
                result = Result.error(404, "接口不存在");
                conn.close();
            }

            resp.getWriter().write(JsonUtil.resultToJson(result));

        } catch (Exception e) {
            resp.getWriter().write(JsonUtil.resultToJson(Result.serverError()));
        }
    }
}