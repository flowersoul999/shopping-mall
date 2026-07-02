/* 成员1: 用户模块 - 地址接口Servlet */
package com.shop.address;
import com.shop.common.JsonUtil;
import com.shop.common.Result;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
@WebServlet("/api/address/*")
public class AddressServlet extends HttpServlet {
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
            resp.getWriter().write(JsonUtil.resultToJson(new AddressService().getList(uid)));
        } catch (Exception e) { resp.getWriter().write(JsonUtil.resultToJson(Result.serverError())); }
    }
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        try { int uid = getUserId(req); Map body = JsonUtil.toMap(readBody(req));
            Address a = new Address(); a.setUserId(uid);
            a.setName((String)body.get("name")); a.setPhone((String)body.get("phone"));
            a.setProvince((String)body.get("province")); a.setCity((String)body.get("city"));
            a.setDistrict((String)body.get("district")); a.setDetail((String)body.get("detail"));
            Object def = body.get("isDefault"); a.setIsDefault(def != null && (Boolean)def ? 1 : 0);
            resp.getWriter().write(JsonUtil.resultToJson(new AddressService().add(a)));
        } catch (Exception e) { resp.getWriter().write(JsonUtil.resultToJson(Result.serverError())); }
    }
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        try { Map body = JsonUtil.toMap(readBody(req));
            int id = Double.valueOf(body.get("id").toString()).intValue();
            int uid = getUserId(req);
            Address a = new Address(); a.setId(id); a.setUserId(uid);
            a.setName((String)body.get("name")); a.setPhone((String)body.get("phone"));
            a.setProvince((String)body.get("province")); a.setCity((String)body.get("city"));
            a.setDistrict((String)body.get("district")); a.setDetail((String)body.get("detail"));
            Object def = body.get("isDefault"); a.setIsDefault(def != null && (Boolean)def ? 1 : 0);
            resp.getWriter().write(JsonUtil.resultToJson(new AddressService().update(a)));
        } catch (Exception e) { resp.getWriter().write(JsonUtil.resultToJson(Result.serverError())); }
    }
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        try { String path = req.getPathInfo();
            int id = Integer.parseInt(path.replace("/", ""));
            resp.getWriter().write(JsonUtil.resultToJson(new AddressService().delete(id)));
        } catch (Exception e) { resp.getWriter().write(JsonUtil.resultToJson(Result.serverError())); }
    }
}
