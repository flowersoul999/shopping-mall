/* 成员1: 用户模块 - 地址业务逻辑 */
package com.shop.address;
import dao.AddressDAO;
import com.shop.common.DBUtil;
import com.shop.common.Result;
import java.sql.Connection;
import java.util.List;
public class AddressService {
    public Result getList(int userId) throws Exception {
        Connection conn = DBUtil.getConnection();
        AddressDAO dao = new AddressDAO(conn);
        List list = dao.findByUserId(userId);
        conn.close(); return Result.success(list);
    }
    public Result add(Address addr) throws Exception {
        Connection conn = DBUtil.getConnection();
        AddressDAO dao = new AddressDAO(conn);
        if (addr.getIsDefault() == 1) dao.clearDefault(addr.getUserId());
        int id = dao.insert(addr); conn.close();
        return id > 0 ? Result.success("添加成功", id) : Result.serverError();
    }
    public Result update(Address addr) throws Exception {
        Connection conn = DBUtil.getConnection();
        AddressDAO dao = new AddressDAO(conn);
        if (addr.getIsDefault() == 1) dao.clearDefault(addr.getUserId());
        dao.update(addr); conn.close(); return Result.success("更新成功");
    }
    public Result delete(int id) throws Exception {
        Connection conn = DBUtil.getConnection();
        AddressDAO dao = new AddressDAO(conn);
        dao.delete(id); conn.close(); return Result.success("删除成功");
    }
    public Result setDefault(int id, int userId) throws Exception {
        Connection conn = DBUtil.getConnection();
        AddressDAO dao = new AddressDAO(conn);
        dao.clearDefault(userId); dao.setDefault(id);
        conn.close(); return Result.success("设置成功");
    }
}
