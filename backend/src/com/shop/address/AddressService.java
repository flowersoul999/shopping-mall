/* 成员1: 用户模块 - 地址业务逻辑 */
package com.shop.address;

import dao.AddressDAO;
import com.shop.common.DBUtil;
import com.shop.common.Result;
import java.sql.Connection;
import java.util.List;

/**
 * 地址业务逻辑类
 * 处理收货地址相关的业务操作，包括添加、修改、删除、设置默认地址
 */
public class AddressService {

    /**
     * 获取用户地址列表
     * @param userId 用户ID
     * @return Result对象，包含地址列表
     * @throws Exception 数据库操作异常
     */
    public Result getList(int userId) throws Exception {
        Connection conn = DBUtil.getConnection();
        AddressDAO dao = new AddressDAO(conn);
        List list = dao.findByUserId(userId);
        conn.close();
        return Result.success(list);
    }

    /**
     * 添加收货地址
     * 如果设置为默认地址，会先清除该用户其他默认地址
     * @param addr 地址对象
     * @return Result对象，操作结果
     * @throws Exception 数据库操作异常
     */
    public Result add(Address addr) throws Exception {
        Connection conn = DBUtil.getConnection();
        AddressDAO dao = new AddressDAO(conn);
        // 如果设置为默认地址，先清除其他默认地址
        if (addr.getIsDefault() == 1) {
            dao.clearDefault(addr.getUserId());
        }
        int id = dao.insert(addr);
        conn.close();
        return id > 0 ? Result.success("添加成功", id) : Result.serverError();
    }

    /**
     * 更新收货地址
     * 如果设置为默认地址，会先清除该用户其他默认地址
     * @param addr 地址对象
     * @return Result对象，操作结果
     * @throws Exception 数据库操作异常
     */
    public Result update(Address addr) throws Exception {
        Connection conn = DBUtil.getConnection();
        AddressDAO dao = new AddressDAO(conn);
        // 如果设置为默认地址，先清除其他默认地址
        if (addr.getIsDefault() == 1) {
            dao.clearDefault(addr.getUserId());
        }
        dao.update(addr);
        conn.close();
        return Result.success("更新成功");
    }

    /**
     * 删除收货地址
     * @param id 地址ID
     * @return Result对象，操作结果
     * @throws Exception 数据库操作异常
     */
    public Result delete(int id) throws Exception {
        Connection conn = DBUtil.getConnection();
        AddressDAO dao = new AddressDAO(conn);
        dao.delete(id);
        conn.close();
        return Result.success("删除成功");
    }

    /**
     * 设置默认地址
     * 先清除该用户其他默认地址，再设置指定地址为默认
     * @param id 地址ID
     * @param userId 用户ID
     * @return Result对象，操作结果
     * @throws Exception 数据库操作异常
     */
    public Result setDefault(int id, int userId) throws Exception {
        Connection conn = DBUtil.getConnection();
        AddressDAO dao = new AddressDAO(conn);
        // 清除其他默认地址
        dao.clearDefault(userId);
        // 设置指定地址为默认
        dao.setDefault(id);
        conn.close();
        return Result.success("设置成功");
    }
}