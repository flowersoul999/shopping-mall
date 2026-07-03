/**
 * 分类业务逻辑类（Service层）
 * 处理分类相关的业务逻辑
 * 
 * 调用CategoryDAO进行数据库操作，不直接操作数据库
 */
/* 成员2: 商品模块 - 分类业务逻辑 */
package com.shop.product;
import dao.CategoryDAO;
import com.shop.common.DBUtil;
import com.shop.common.Result;
import java.sql.Connection;
import java.util.List;

public class CategoryService {

    /**
     * 获取分类列表
     * @return Result结果对象（包含分类列表）
     * @throws Exception 数据库异常
     */
    public Result getList() throws Exception {
        // 获取数据库连接
        Connection conn = DBUtil.getConnection();
        CategoryDAO dao = new CategoryDAO(conn);
        
        // 查询所有分类（按sortOrder升序）
        List<Category> list = dao.findAll();
        
        conn.close(); 
        return Result.success(list);
    }
}