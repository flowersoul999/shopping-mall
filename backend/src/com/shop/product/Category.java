/**
 * 分类实体类（Entity）
 * 对应数据库中的category表
 * 
 * 分类用于商品的归类，支持排序（sortOrder）
 */
/* 成员2: 商品模块 - 分类实体类 */
package com.shop.product;

public class Category {
    
    /** 分类ID（主键，自增） */
    private int id;
    
    /** 分类名称 */
    private String name;
    
    /** 分类描述 */
    private String description;
    
    /** 排序字段（数值越小越靠前） */
    private int sortOrder;

    // Getter 和 Setter 方法
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
}