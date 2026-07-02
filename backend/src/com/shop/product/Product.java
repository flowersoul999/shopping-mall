/* 成员2: 商品模块 - 商品实体类 */
package com.shop.product;
public class Product {
    private int id; private int categoryId; private String name; private String description;
    private double price; private int stock; private String image; private int status;
    private int sales; private String createTime; private String categoryName;
    public int getId() { return id; } public void setId(int id) { this.id = id; }
    public int getCategoryId() { return categoryId; } public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public String getName() { return name; } public void setName(String name) { this.name = name; }
    public String getDescription() { return description; } public void setDescription(String description) { this.description = description; }
    public double getPrice() { return price; } public void setPrice(double price) { this.price = price; }
    public int getStock() { return stock; } public void setStock(int stock) { this.stock = stock; }
    public String getImage() { return image; } public void setImage(String image) { this.image = image; }
    public int getStatus() { return status; } public void setStatus(int status) { this.status = status; }
    public int getSales() { return sales; } public void setSales(int sales) { this.sales = sales; }
    public String getCreateTime() { return createTime; } public void setCreateTime(String createTime) { this.createTime = createTime; }
    public String getCategoryName() { return categoryName; } public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
}
