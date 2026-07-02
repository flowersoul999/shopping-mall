/* 成员3: 购物车模块 - 购物车业务逻辑 */
package com.shop.cart;
import dao.CartDAO;
import com.shop.common.DBUtil;
import com.shop.common.Result;
import java.sql.Connection;
import java.util.List;
public class CartService {
    public Result getList(int userId) throws Exception {
        Connection conn = DBUtil.getConnection();
        CartDAO dao = new CartDAO(conn);
        List<CartItem> list = dao.findByUserId(userId);
        conn.close(); return Result.success(list);
    }
    public Result add(int userId, int productId, int quantity) throws Exception {
        Connection conn = DBUtil.getConnection();
        CartDAO dao = new CartDAO(conn);
        CartItem existing = dao.findByUserAndProduct(userId, productId);
        if (existing != null) {
            dao.updateQuantity(existing.getId(), existing.getQuantity() + quantity);
        } else {
            CartItem item = new CartItem();
            item.setUserId(userId); item.setProductId(productId); item.setQuantity(quantity);
            dao.insert(item);
        }
        conn.close(); return Result.success("添加成功");
    }
    public Result updateQuantity(int id, int quantity) throws Exception {
        Connection conn = DBUtil.getConnection();
        CartDAO dao = new CartDAO(conn);
        dao.updateQuantity(id, quantity);
        conn.close(); return Result.success("更新成功");
    }
    public Result delete(int id) throws Exception {
        Connection conn = DBUtil.getConnection();
        CartDAO dao = new CartDAO(conn);
        dao.delete(id);
        conn.close(); return Result.success("删除成功");
    }
    public Result clear(int userId) throws Exception {
        Connection conn = DBUtil.getConnection();
        CartDAO dao = new CartDAO(conn);
        dao.clearByUserId(userId);
        conn.close(); return Result.success("已清空");
    }
}
