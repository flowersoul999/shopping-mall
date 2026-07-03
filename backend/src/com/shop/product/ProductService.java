/**
 * 商品业务逻辑类（Service层）
 * 处理商品相关的业务逻辑
 * 
 * 调用ProductDAO进行数据库操作，不直接操作数据库
 */
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

    /**
     * 获取商品列表（分页）
     * 支持按分类筛选
     * @param page 页码（从1开始）
     * @param pageSize 每页数量
     * @param categoryId 分类ID（可选，null表示全部）
     * @return Result结果对象（包含列表、总数、页码信息）
     * @throws Exception 数据库异常
     */
    public Result getList(int page, int pageSize, Integer categoryId) throws Exception {
        // 获取数据库连接
        Connection conn = DBUtil.getConnection();
        ProductDAO dao = new ProductDAO(conn);
        
        List<Product> list;
        int total;
        
        // 如果指定了分类ID，按分类查询
        if (categoryId != null && categoryId > 0) {
            list = dao.findByCategory(categoryId, page, pageSize);
            total = dao.countByCategory(categoryId);
        } else {
            // 查询全部商品
            list = dao.findAll(page, pageSize);
            total = dao.count();
        }
        
        conn.close();
        
        // 构建分页响应数据
        Map<String, Object> data = new HashMap<>();
        data.put("list", list);    // 商品列表
        data.put("total", total);  // 总数量
        data.put("page", page);    // 当前页码
        data.put("pageSize", pageSize);  // 每页数量
        
        return Result.success(data);
    }

    /**
     * 搜索商品
     * 按商品名称或描述模糊匹配
     * @param keyword 搜索关键词
     * @param page 页码
     * @param pageSize 每页数量
     * @return Result结果对象（包含列表、总数）
     * @throws Exception 数据库异常
     */
    public Result search(String keyword, int page, int pageSize) throws Exception {
        Connection conn = DBUtil.getConnection();
        ProductDAO dao = new ProductDAO(conn);
        
        // 执行搜索
        List<Product> list = dao.search(keyword, page, pageSize);
        int total = dao.countSearch(keyword);
        
        conn.close();
        
        // 构建响应数据
        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("total", total);
        data.put("page", page);
        data.put("pageSize", pageSize);
        
        return Result.success(data);
    }

    /**
     * 获取商品详情
     * @param id 商品ID
     * @return Result结果对象（包含商品信息）
     * @throws Exception 数据库异常
     */
    public Result getDetail(int id) throws Exception {
        Connection conn = DBUtil.getConnection();
        ProductDAO dao = new ProductDAO(conn);
        
        // 根据ID查询商品
        Product p = dao.findById(id);
        
        conn.close();
        
        // 商品不存在返回400
        if (p == null) {
            return Result.badRequest("商品不存在");
        }
        
        return Result.success(p);
    }

    /**
     * 获取最新商品（用于首页展示）
     * @param limit 返回数量
     * @return Result结果对象（包含最新商品列表）
     * @throws Exception 数据库异常
     */
    public Result getLatest(int limit) throws Exception {
        Connection conn = DBUtil.getConnection();
        ProductDAO dao = new ProductDAO(conn);
        
        // 查询最新上架的商品
        List<Product> list = dao.findLatest(limit);
        
        conn.close(); 
        return Result.success(list);
    }
}