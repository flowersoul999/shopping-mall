/* 成员3: 订单模块 - 订单接口Servlet */
package com.shop.order;
import com.shop.common.JsonUtil;
import com.shop.common.Result;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
@WebServlet("/api/order/*")
public class OrderServlet extends HttpServlet {
    private int getUserId(HttpServletRequest req) {
        try { return Integer.parseInt(req.getHeader("Authorization")); } catch (Exception e) { return -1; }
    }
    private String readBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder(); BufferedReader br = req.getReader(); String line;
        while ((line = br.readLine()) != null) sb.append(line); return sb.toString();
    }
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        try { String path = req.getPathInfo(); int uid = getUserId(req);
            OrderService svc = new OrderService();
            Result result;
            if ("/admin/list".equals(path)) {
                int page = req.getParameter("page") != null ? Integer.parseInt(req.getParameter("page")) : 1;
                int ps = req.getParameter("pageSize") != null ? Integer.parseInt(req.getParameter("pageSize")) : 10;
                String status = req.getParameter("status");
                result = svc.getAdminList(page, ps, status);
            } else if (uid <= 0) { resp.getWriter().write(JsonUtil.resultToJson(Result.unauthorized())); return; }
            else if ("/list".equals(path)) {
                int page = req.getParameter("page") != null ? Integer.parseInt(req.getParameter("page")) : 1;
                int ps = req.getParameter("pageSize") != null ? Integer.parseInt(req.getParameter("pageSize")) : 10;
                String status = req.getParameter("status");
                result = svc.getList(uid, page, ps, status);
            } else if ("/detail".equals(path)) {
                int id = Integer.parseInt(req.getParameter("id"));
                result = svc.getDetail(id);
            } else { result = Result.error(404, "接口不存在"); }
            resp.getWriter().write(JsonUtil.resultToJson(result));
        } catch (Exception e) { e.printStackTrace(); resp.getWriter().write(JsonUtil.resultToJson(Result.serverError())); }
    }
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        try { int uid = getUserId(req); Map body = JsonUtil.toMap(readBody(req));
            int addressId = body.get("addressId") != null ? Double.valueOf(body.get("addressId").toString()).intValue() : 0;
            String remark = (String) body.get("remark");
            resp.getWriter().write(JsonUtil.resultToJson(new OrderService().createOrder(uid, addressId, remark)));
        } catch (Exception e) { e.printStackTrace(); resp.getWriter().write(JsonUtil.resultToJson(Result.serverError())); }
    }
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        try { Map body = JsonUtil.toMap(readBody(req));
            int id = Double.valueOf(body.get("id").toString()).intValue();
            String status = (String) body.get("status");
            resp.getWriter().write(JsonUtil.resultToJson(new OrderService().updateStatus(id, status)));
        } catch (Exception e) { e.printStackTrace(); resp.getWriter().write(JsonUtil.resultToJson(Result.serverError())); }
    }
}
