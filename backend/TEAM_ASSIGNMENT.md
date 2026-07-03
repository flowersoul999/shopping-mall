# 后端团队职责划分

## 团队成员

| 序号 | 角色 | 负责范围 |
|------|------|----------|
| 1 | 数据访问层开发 | DAO 层全部文件 |
| 2 | 业务逻辑开发 - 用户&商品 | User + Product 模块 |
| 3 | 业务逻辑开发 - 购物车&订单 | Cart + Order 模块 |
| 4 | 业务逻辑开发 - 地址&管理 | Address + Admin + Common 模块 |

---

## 组员1：数据访问层（DAO层）

### 负责文件

```
backend/src/dao/
├── AddressDAO.java     # 地址数据访问
├── CartDAO.java        # 购物车数据访问
├── CategoryDAO.java    # 分类数据访问
├── OrderDAO.java       # 订单数据访问
├── ProductDAO.java     # 商品数据访问
└── UserDAO.java        # 用户数据访问
```

### 职责说明

- 编写 SQL 查询语句
- 实现数据库 CRUD 操作
- 处理数据库连接和资源释放
- 确保 SQL 注入防护

---

## 组员2：业务逻辑 - 用户&商品模块

### 负责文件

```
backend/src/com/shop/user/
├── User.java           # 用户实体类
├── UserService.java    # 用户业务逻辑
└── UserServlet.java    # 用户接口控制器

backend/src/com/shop/product/
├── Category.java       # 分类实体类
├── CategoryService.java # 分类业务逻辑
├── CategoryServlet.java # 分类接口控制器
├── Product.java        # 商品实体类
├── ProductService.java # 商品业务逻辑
└── ProductServlet.java # 商品接口控制器
```

### 职责说明

- 用户注册、登录、信息管理
- 商品列表、详情、搜索
- 商品分类管理

---

## 组员3：业务逻辑 - 购物车&订单模块

### 负责文件

```
backend/src/com/shop/cart/
├── CartItem.java       # 购物车项实体类
├── CartService.java    # 购物车业务逻辑
└── CartServlet.java    # 购物车接口控制器

backend/src/com/shop/order/
├── Order.java          # 订单实体类
├── OrderItem.java      # 订单项实体类
├── OrderService.java   # 订单业务逻辑
└── OrderServlet.java   # 订单接口控制器
```

### 职责说明

- 购物车增删改查
- 订单创建、支付、确认收货
- 订单列表和详情查询

---

## 组员4：业务逻辑 - 地址&管理模块

### 负责文件

```
backend/src/com/shop/address/
├── Address.java        # 地址实体类
├── AddressService.java # 地址业务逻辑
└── AddressServlet.java # 地址接口控制器

backend/src/com/shop/admin/
├── AdminServlet.java   # 管理员接口控制器
└── DashboardServlet.java # 仪表盘接口控制器

backend/src/com/shop/common/
├── CORSFilter.java     # 跨域过滤器
├── DBUtil.java         # 数据库连接工具
├── JsonUtil.java       # JSON 工具类
└── Result.java         # 统一响应封装
```

### 职责说明

- 收货地址管理
- 后台管理功能
- 公共工具类维护
- 跨域配置

---

## 接口文档

### 用户模块（组员2）

| 接口 | 方法 | 文件 |
|------|------|------|
| `/api/user/register` | POST | UserServlet.java |
| `/api/user/login` | POST | UserServlet.java |
| `/api/user/info` | GET | UserServlet.java |
| `/api/user/update` | POST | UserServlet.java |
| `/api/user/changePassword` | POST | UserServlet.java |

### 商品模块（组员2）

| 接口 | 方法 | 文件 |
|------|------|------|
| `/api/product/list` | GET | ProductServlet.java |
| `/api/product/detail` | GET | ProductServlet.java |
| `/api/product/search` | GET | ProductServlet.java |
| `/api/category/list` | GET | CategoryServlet.java |
| `/api/category/detail` | GET | CategoryServlet.java |

### 购物车模块（组员3）

| 接口 | 方法 | 文件 |
|------|------|------|
| `/api/cart/list` | GET | CartServlet.java |
| `/api/cart/add` | POST | CartServlet.java |
| `/api/cart/update` | POST | CartServlet.java |
| `/api/cart/delete` | POST | CartServlet.java |
| `/api/cart/clear` | POST | CartServlet.java |

### 订单模块（组员3）

| 接口 | 方法 | 文件 |
|------|------|------|
| `/api/order/create` | POST | OrderServlet.java |
| `/api/order/list` | GET | OrderServlet.java |
| `/api/order/detail` | GET | OrderServlet.java |
| `/api/order/pay` | POST | OrderServlet.java |
| `/api/order/confirm` | POST | OrderServlet.java |
| `/api/order/admin/list` | GET | OrderServlet.java |
| `/api/order/admin/update` | POST | OrderServlet.java |

### 地址模块（组员4）

| 接口 | 方法 | 文件 |
|------|------|------|
| `/api/address/list` | GET | AddressServlet.java |
| `/api/address/add` | POST | AddressServlet.java |
| `/api/address/update` | POST | AddressServlet.java |
| `/api/address/delete` | POST | AddressServlet.java |

### 管理模块（组员4）

| 接口 | 方法 | 文件 |
|------|------|------|
| `/api/admin/dashboard` | GET | DashboardServlet.java |