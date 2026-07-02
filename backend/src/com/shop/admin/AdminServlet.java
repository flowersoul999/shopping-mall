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
@WebServlet("/api/admin/*")
public class AdminServlet extends HttpServlet {
    private boolean isAdmin(HttpServletRequest req) { return true; }
    private String readBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder(); BufferedReader br = req.getReader(); String line;
        while ((line = br.readLine()) != null) sb.append(line); return sb.toString();
    }
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        try { String path = req.getPathInfo(); Connection conn = DBUtil.getConnection();
            Result result;
            if ("/users".equals(path)) {
                int page = req.getParameter("page")!=null?Integer.parseInt(req.getParameter("page")):1;
                int ps = req.getParameter("pageSize")!=null?Integer.parseInt(req.getParameter("pageSize")):10;
                UserDAO dao = new UserDAO(conn);
                List<User> list = dao.findAll(page, ps); int total = dao.count();
                conn.close(); Map<String,Object> data = new HashMap<>();
                data.put("list",list); data.put("total",total); result = Result.success(data);
            } else if ("/products".equals(path)) {
                int page = req.getParameter("page")!=null?Integer.parseInt(req.getParameter("page")):1;
                int ps = req.getParameter("pageSize")!=null?Integer.parseInt(req.getParameter("pageSize")):10;
                ProductDAO dao = new ProductDAO(conn);
                result = Result.success(dao.findAll(page, ps));
                conn.close();
            } else if ("/orders".equals(path)) {
                int page = req.getParameter("page")!=null?Integer.parseInt(req.getParameter("page")):1;
                int ps = req.getParameter("pageSize")!=null?Integer.parseInt(req.getParameter("pageSize")):10;
                OrderDAO dao = new OrderDAO(conn);
                List<Order> orders = dao.findAll(page, ps);
                for (Order o : orders) { o.setItems(dao.findItemsByOrderId(o.getId())); }
                conn.close(); result = Result.success(orders);
            } else if ("/categories".equals(path)) {
                CategoryDAO dao = new CategoryDAO(conn);
                result = Result.success(dao.findAll()); conn.close();
            } else { result = Result.error(404,"接口不存在"); conn.close(); }
            resp.getWriter().write(JsonUtil.resultToJson(result));
        } catch (Exception e) { e.printStackTrace();
            try { resp.getWriter().write(JsonUtil.resultToJson(Result.serverError())); }
            catch (IOException ex) {} }
    }
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        try { String path = req.getPathInfo(); Connection conn = DBUtil.getConnection();
            Map body = JsonUtil.toMap(readBody(req)); Result result;
            if ("/products".equals(path)) {
                ProductDAO dao = new ProductDAO(conn);
                Product p = new Product();
                p.setCategoryId(Integer.parseInt(body.get("categoryId").toString()));
                p.setName((String)body.get("name")); p.setDescription((String)body.get("description"));
                p.setPrice(Double.parseDouble(body.get("price").toString()));
                p.setStock(Integer.parseInt(body.get("stock").toString()));
                p.setImage((String)body.get("image"));
                int id = dao.insert(p); conn.close();
                result = id > 0 ? Result.success("新增成功", id) : Result.serverError();
            } else if ("/categories".equals(path)) {
                CategoryDAO dao = new CategoryDAO(conn);
                Category c = new Category();
                c.setName((String)body.get("name")); c.setDescription((String)body.get("description"));
                c.setSortOrder(body.get("sortOrder")!=null?Integer.parseInt(body.get("sortOrder").toString()):0);
                int id = dao.insert(c); conn.close();
                result = id > 0 ? Result.success("新增成功", id) : Result.serverError();
            } else { result = Result.error(404,"接口不存在"); conn.close(); }
            resp.getWriter().write(JsonUtil.resultToJson(result));
        } catch (Exception e) { resp.getWriter().write(JsonUtil.resultToJson(Result.serverError())); }
    }
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        try { String path = req.getPathInfo(); Connection conn = DBUtil.getConnection();
            Map body = JsonUtil.toMap(readBody(req)); Result result;
            if ("/products".equals(path)) {
                ProductDAO dao = new ProductDAO(conn);
                Product p = new Product(); p.setId(Integer.parseInt(body.get("id").toString()));
                p.setCategoryId(Integer.parseInt(body.get("categoryId").toString()));
                p.setName((String)body.get("name")); p.setDescription((String)body.get("description"));
                p.setPrice(Double.parseDouble(body.get("price").toString()));
                p.setStock(Integer.parseInt(body.get("stock").toString()));
                p.setImage((String)body.get("image"));
                p.setStatus(body.get("status")!=null?Integer.parseInt(body.get("status").toString()):1);
                dao.update(p); conn.close(); result = Result.success("更新成功");
            } else if ("/categories".equals(path)) {
                CategoryDAO dao = new CategoryDAO(conn);
                Category c = new Category(); c.setId(Integer.parseInt(body.get("id").toString()));
                c.setName((String)body.get("name")); c.setDescription((String)body.get("description"));
                c.setSortOrder(body.get("sortOrder")!=null?Integer.parseInt(body.get("sortOrder").toString()):0);
                dao.update(c); conn.close(); result = Result.success("更新成功");
            } else if ("/users".equals(path)) {
                UserDAO dao = new UserDAO(conn);
                int uid = Integer.parseInt(body.get("id").toString());
                int statusVal = Integer.parseInt(body.get("status").toString());
                dao.updateStatus(uid, statusVal); conn.close();
                result = Result.success("更新成功");
            } else if ("/orders/status".equals(path)) {
                OrderDAO dao = new OrderDAO(conn);
                int oid = Integer.parseInt(body.get("id").toString());
                String st = (String)body.get("status");
                dao.updateStatus(oid, st); conn.close();
                result = Result.success("更新成功");
            } else { result = Result.error(404,"接口不存在"); conn.close(); }
            resp.getWriter().write(JsonUtil.resultToJson(result));
        } catch (Exception e) { resp.getWriter().write(JsonUtil.resultToJson(Result.serverError())); }
    }
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        try { String path = req.getPathInfo(); Connection conn = DBUtil.getConnection(); Result result;
            String[] parts = path.split("/"); int id = Integer.parseInt(parts[1]);
            if (path.startsWith("/products/")) {
                new ProductDAO(conn).delete(id); conn.close();
                result = Result.success("删除成功");
            } else if (path.startsWith("/categories/")) {
                new CategoryDAO(conn).delete(id); conn.close();
                result = Result.success("删除成功");
            } else { result = Result.error(404,"接口不存在"); conn.close(); }
            resp.getWriter().write(JsonUtil.resultToJson(result));
        } catch (Exception e) { resp.getWriter().write(JsonUtil.resultToJson(Result.serverError())); }
    }
}
