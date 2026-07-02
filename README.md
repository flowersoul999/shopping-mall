# 牢大商城

一个基于 Vue 3 + Java Servlet 的在线商城项目，包含完整的商品浏览、购物车、订单管理和后台管理功能。

## 🛠 技术栈

### 前端
- **Vue 3** - 渐进式 JavaScript 框架
- **Element Plus** - Vue 3 组件库
- **Vue Router** - Vue 路由管理器
- **Pinia** - Vue 状态管理
- **Axios** - HTTP 客户端
- **Vite** - 前端构建工具

### 后端
- **Java Servlet** - Java Web 服务器端技术
- **MySQL** - 关系型数据库
- **Gson** - JSON 序列化/反序列化

### 开发环境
- **Windows** - 操作系统
- **Tomcat 9** - Web 服务器
- **VS Code** - 代码编辑器

## 📁 项目结构

```
商城购物项目/
├── frontend/                 # 前端项目
│   ├── src/
│   │   ├── api/             # API 请求封装
│   │   ├── components/      # 公共组件
│   │   ├── router/          # 路由配置
│   │   ├── store/           # Pinia 状态管理
│   │   ├── views/           # 页面组件
│   │   │   ├── admin/       # 管理后台页面
│   │   │   └── *.vue        # 用户端页面
│   │   ├── App.vue          # 根组件
│   │   └── main.js          # 入口文件
│   ├── index.html           # HTML 模板
│   ├── package.json         # 前端依赖
│   └── vite.config.js       # Vite 配置
│
├── backend/                  # 后端项目
│   ├── src/
│   │   ├── com/shop/        # 业务逻辑层
│   │   │   ├── user/        # 用户模块
│   │   │   ├── product/     # 商品模块
│   │   │   ├── cart/        # 购物车模块
│   │   │   ├── order/       # 订单模块
│   │   │   ├── address/     # 地址模块
│   │   │   ├── admin/       # 管理模块
│   │   │   └── common/      # 公共工具
│   │   └── dao/             # 数据访问层
│   └── webapp/              # Web 应用资源
│
├── database/                 # 数据库脚本
│   └── init.sql             # 初始化脚本
│
├── build/                   # 后端构建产物
└── README.md                # 项目说明
```

## ✨ 功能特性

### 用户端
- ✅ 用户注册与登录
- ✅ 商品浏览与搜索
- ✅ 商品分类筛选
- ✅ 购物车管理（添加、删除、数量修改）
- ✅ 收货地址管理（添加、编辑、删除、默认地址）
- ✅ 订单结算与提交
- ✅ 订单列表与详情查看
- ✅ 个人中心（信息管理、密码修改）

### 管理后台
- ✅ 仪表盘（数据统计）
- ✅ 商品管理（占位）
- ✅ 订单管理（查看、发货、完成、取消）
- ✅ 用户管理（占位）
- ✅ 分类管理（占位）

## 🚀 快速开始

### 环境要求
- **Node.js** >= 18.0.0
- **Java** >= 1.8
- **MySQL** >= 5.7
- **Tomcat** >= 9.0

### 1. 数据库配置

创建数据库并导入初始化脚本：

```sql
CREATE DATABASE shopping_mall CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE shopping_mall;
source database/init.sql;
```

修改后端数据库连接配置（`backend/src/com/shop/common/DBUtil.java`）：

```java
private static final String URL = "jdbc:mysql://localhost:3306/shopping_mall?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8";
private static final String USER = "your_username";
private static final String PASSWORD = "your_password";
```

### 2. 前端运行

```bash
cd frontend
npm install
npm run dev
```

前端服务启动后访问：http://localhost:3000/

### 3. 后端部署

**方式一：手动部署到 Tomcat**

1. 编译后端代码：
```bash
cd backend
javac -d build-vscode -cp "src;webapp/WEB-INF/lib/*" src/com/shop/**/*.java src/dao/*.java
```

2. 将编译后的 `build-vscode` 目录复制到 Tomcat 的 `webapps/shopping-mall/WEB-INF/classes/`

3. 启动 Tomcat 服务

**方式二：使用项目脚本**

```bash
# 启动服务
.\start

# 停止服务
.\stop
```

后端服务启动后访问：http://localhost:8080/shopping-mall/

## 🔌 API 接口

### 用户模块
| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/user/register` | POST | 用户注册 |
| `/api/user/login` | POST | 用户登录 |
| `/api/user/info` | GET | 获取用户信息 |
| `/api/user/update` | POST | 更新用户信息 |
| `/api/user/changePassword` | POST | 修改密码 |

### 商品模块
| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/product/list` | GET | 获取商品列表 |
| `/api/product/detail` | GET | 获取商品详情 |
| `/api/product/search` | GET | 搜索商品 |

### 购物车模块
| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/cart/list` | GET | 获取购物车列表 |
| `/api/cart/add` | POST | 添加商品到购物车 |
| `/api/cart/update` | POST | 更新购物车数量 |
| `/api/cart/delete` | POST | 删除购物车商品 |
| `/api/cart/clear` | POST | 清空购物车 |

### 订单模块
| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/order/create` | POST | 创建订单 |
| `/api/order/list` | GET | 获取订单列表 |
| `/api/order/detail` | GET | 获取订单详情 |
| `/api/order/pay` | POST | 支付订单 |
| `/api/order/confirm` | POST | 确认收货 |
| `/api/order/admin/list` | GET | 管理员订单列表 |

### 地址模块
| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/address/list` | GET | 获取地址列表 |
| `/api/address/add` | POST | 添加地址 |
| `/api/address/update` | POST | 更新地址 |
| `/api/address/delete` | POST | 删除地址 |

## 🔐 测试账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 普通用户 | test | 123456 |
| 管理员 | admin | 123456 |

## 📝 开发说明

### 前端代理配置

前端通过 Vite 代理转发请求到后端：

```javascript
// vite.config.js
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080/shopping-mall',
      changeOrigin: true
    }
  }
}
```

### 状态管理

使用 Pinia 管理全局状态：

- `user.js` - 用户状态（登录信息、权限）
- `cart.js` - 购物车状态（商品列表、数量、价格）

### 路由守卫

路由配置了权限守卫，未登录用户访问需要登录的页面会自动跳转到登录页。

## 📄 许可证

MIT License

## 📧 联系

如有问题或建议，请提交 Issue。