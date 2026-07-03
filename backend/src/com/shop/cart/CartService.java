/* 成员3: 购物车模块 - 购物车业务逻辑 */
package com.shop.cart;

import dao.CartDAO;
import com.shop.common.DBUtil;
import com.shop.common.Result;
import java.sql.Connection;
import java.util.List;

/**
 * 购物车业务逻辑类
 * 处理购物车相关的业务操作，包括添加、修改、删除、清空购物车
 */
public class CartService {

    /**
     * 获取用户购物车列表
     * @param userId 用户ID
     * @return Result对象，包含购物车项列表
     * @throws Exception 数据库操作异常
     */
    public Result getList(int userId) throws Exception {
        Connection conn = DBUtil.getConnection();
        CartDAO dao = new CartDAO(conn);
        List<CartItem> list = dao.findByUserId(userId);
        conn.close();
        return Result.success(list);
    }

    /**
     * 添加商品到购物车
     * 如果商品已存在则数量累加，否则新增购物车项
     * @param userId 用户ID
     * @param productId 商品ID
     * @param quantity 数量
     * @return Result对象，操作结果
     * @throws Exception 数据库操作异常
     */
    public Result add(int userId, int productId, int quantity) throws Exception {
        Connection conn = DBUtil.getConnection();
        CartDAO dao = new CartDAO(conn);
        // 检查商品是否已在购物车中
        CartItem existing = dao.findByUserAndProduct(userId, productId);
        if (existing != null) {
            // 已存在则累加数量
            dao.updateQuantity(existing.getId(), existing.getQuantity() + quantity);
        } else {
            // 不存在则新增
            CartItem item = new CartItem();
            item.setUserId(userId);
            item.setProductId(productId);
            item.setQuantity(quantity);
            dao.insert(item);
        }
        conn.close();
        return Result.success("添加成功");
    }

    /**
     * 更新购物车项数量
     * @param id 购物车项ID
     * @param quantity 新数量
     * @return Result对象，操作结果
     * @throws Exception 数据库操作异常
     */
    public Result updateQuantity(int id, int quantity) throws Exception {
        Connection conn = DBUtil.getConnection();
        CartDAO dao = new CartDAO(conn);
        dao.updateQuantity(id, quantity);
        conn.close();
        return Result.success("更新成功");
    }

    /**
     * 删除购物车项
     * @param id 购物车项ID
     * @return Result对象，操作结果
     * @throws Exception 数据库操作异常
     */
    public Result delete(int id) throws Exception {
        Connection conn = DBUtil.getConnection();
        CartDAO dao = new CartDAO(conn);
        dao.delete(id);
        conn.close();
        return Result.success("删除成功");
    }

    /**
     * 清空用户购物车
     * @param userId 用户ID
     * @return Result对象，操作结果
     * @throws Exception 数据库操作异常
     */
    public Result clear(int userId) throws Exception {
        Connection conn = DBUtil.getConnection();
        CartDAO dao = new CartDAO(conn);
        dao.clearByUserId(userId);
        conn.close();
        return Result.success("已清空");
    }
}