/* 成员1: 用户模块 - 地址实体类 */
package com.shop.address;

/**
 * 收货地址实体类
 * 表示用户的一个收货地址信息
 */
public class Address {
    /** 地址ID，主键自增 */
    private int id;
    /** 用户ID，关联用户表 */
    private int userId;
    /** 收货人姓名 */
    private String name;
    /** 收货人电话 */
    private String phone;
    /** 省份 */
    private String province;
    /** 城市 */
    private String city;
    /** 区/县 */
    private String district;
    /** 详细地址 */
    private String detail;
    /** 是否默认地址：1是默认，0不是 */
    private int isDefault;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }
    public int getIsDefault() { return isDefault; }
    public void setIsDefault(int isDefault) { this.isDefault = isDefault; }
}