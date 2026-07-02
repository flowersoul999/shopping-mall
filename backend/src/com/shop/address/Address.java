/* 成员1: 用户模块 - 地址实体类 */
package com.shop.address;
public class Address {
    private int id; private int userId; private String name; private String phone;
    private String province; private String city; private String district;
    private String detail; private int isDefault;
    public int getId() { return id; } public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; } public void setUserId(int userId) { this.userId = userId; }
    public String getName() { return name; } public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; } public void setPhone(String phone) { this.phone = phone; }
    public String getProvince() { return province; } public void setProvince(String province) { this.province = province; }
    public String getCity() { return city; } public void setCity(String city) { this.city = city; }
    public String getDistrict() { return district; } public void setDistrict(String district) { this.district = district; }
    public String getDetail() { return detail; } public void setDetail(String detail) { this.detail = detail; }
    public int getIsDefault() { return isDefault; } public void setIsDefault(int isDefault) { this.isDefault = isDefault; }
}
