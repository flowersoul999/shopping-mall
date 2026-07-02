/* 成员2: 商品模块 - 商品业务逻辑 */
package com.shop.product;
import dao.ProductDAO;
import com.shop.common.DBUtil;
import com.shop.common.Result;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class ProductService {
    public Result getList(int page, int pageSize, Integer categoryId) throws Exception {
        Connection conn = DBUtil.getConnection();
        ProductDAO dao = new ProductDAO(conn);
        List<Product> list;
        int total;
        if (categoryId != null && categoryId > 0) {
            list = dao.findByCategory(categoryId, page, pageSize);
            total = dao.countByCategory(categoryId);
        } else {
            list = dao.findAll(page, pageSize);
            total = dao.count();
        }
        conn.close();
        Map<String, Object> data = new HashMap<>();
        data.put("list", list); data.put("total", total);
        data.put("page", page); data.put("pageSize", pageSize);
        return Result.success(data);
    }
    public Result search(String keyword, int page, int pageSize) throws Exception {
        Connection conn = DBUtil.getConnection();
        ProductDAO dao = new ProductDAO(conn);
        List<Product> list = dao.search(keyword, page, pageSize);
        int total = dao.countSearch(keyword);
        conn.close();
        Map<String, Object> data = new HashMap<>();
        data.put("list", list); data.put("total", total);
        data.put("page", page); data.put("pageSize", pageSize);
        return Result.success(data);
    }
    public Result getDetail(int id) throws Exception {
        Connection conn = DBUtil.getConnection();
        ProductDAO dao = new ProductDAO(conn);
        Product p = dao.findById(id);
        conn.close();
        if (p == null) return Result.badRequest("商品不存在");
        return Result.success(p);
    }
    public Result getLatest(int limit) throws Exception {
        Connection conn = DBUtil.getConnection();
        ProductDAO dao = new ProductDAO(conn);
        List<Product> list = dao.findLatest(limit);
        conn.close(); return Result.success(list);
    }
}
