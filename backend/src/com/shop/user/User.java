/**
 * 用户实体类（Entity）
 * 对应数据库中的user表
 * 
 * 每个字段对应数据库表的一列，使用getter/setter方法访问
 */
/* 成员2: 用户模块 - 用户实体类 */
package com.shop.user;

public class User {
    
    /** 用户ID（主键，自增） */
    private int id;
    
    /** 用户名（唯一，登录时使用） */
    private String username;
    
    /** 密码（MD5加密存储） */
    private String password;
    
    /** 昵称（显示用） */
    private String nickname;
    
    /** 手机号 */
    private String phone;
    
    /** 角色（user普通用户，admin管理员） */
    private String role;
    
    /** 头像路径 */
    private String avatar;
    
    /** 状态（0禁用，1启用） */
    private int status;
    
    /** 创建时间 */
    private String createTime;

    // Getter 和 Setter 方法
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    
    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
}