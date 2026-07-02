/* 成员2: 商品模块 - 分类业务逻辑 */
package com.shop.product;
import dao.CategoryDAO;
import com.shop.common.DBUtil;
import com.shop.common.Result;
import java.sql.Connection;
import java.util.List;
public class CategoryService {
    public Result getList() throws Exception {
        Connection conn = DBUtil.getConnection();
        CategoryDAO dao = new CategoryDAO(conn);
        List<Category> list = dao.findAll();
        conn.close(); return Result.success(list);
    }
}
