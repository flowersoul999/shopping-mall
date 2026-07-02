/* 成员2: 商品模块 - 分类实体类 */
package com.shop.product;
public class Category {
    private int id; private String name; private String description; private int sortOrder;
    public int getId() { return id; } public void setId(int id) { this.id = id; }
    public String getName() { return name; } public void setName(String name) { this.name = name; }
    public String getDescription() { return description; } public void setDescription(String description) { this.description = description; }
    public int getSortOrder() { return sortOrder; } public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
}
