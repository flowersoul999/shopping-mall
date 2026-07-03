/**
 * 商品实体类（Entity）
 * 对应数据库中的product表
 * 
 * 每个字段对应数据库表的一列，使用getter/setter方法访问
 * categoryName字段用于存储关联的分类名称（非数据库字段，查询时通过LEFT JOIN获取）
 */
/* 成员2: 商品模块 - 商品实体类 */
package com.shop.product;

public class Product {
    
    /** 商品ID（主键，自增） */
    private int id;
    
    /** 分类ID（关联category表） */
    private int categoryId;
    
    /** 商品名称 */
    private String name;
    
    /** 商品描述 */
    private String description;
    
    /** 商品价格 */
    private double price;
    
    /** 库存数量 */
    private int stock;
    
    /** 商品图片路径 */
    private String image;
    
    /** 状态（0下架，1上架） */
    private int status;
    
    /** 销量 */
    private int sales;
    
    /** 创建时间 */
    private String createTime;
    
    /** 分类名称（非数据库字段，查询时通过LEFT JOIN获取） */
    private String categoryName;

    // Getter 和 Setter 方法
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    
    public int getSales() { return sales; }
    public void setSales(int sales) { this.sales = sales; }
    
    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
    
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
}