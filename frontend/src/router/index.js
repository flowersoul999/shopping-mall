/**
 * 路由配置文件
 * 定义应用的所有路由规则和导航守卫
 * 使用Vue Router 4实现前端路由管理
 */
import { createRouter, createWebHistory } from 'vue-router' // 引入Vue Router的核心方法
import { useUserStore } from '../store/user'                // 引入用户状态管理，用于权限判断
import { ElMessage } from 'element-plus'                    // 引入Element Plus消息提示组件

/**
 * 路由配置数组
 * 每个路由对象包含：
 * - path: 路由路径
 * - name: 路由名称（用于编程式导航）
 * - component: 路由对应的组件（使用懒加载方式）
 * - meta: 路由元信息（用于权限控制、页面标题等）
 * - children: 子路由配置（用于嵌套路由）
 */
const routes = [
  // 首页路由
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue'),           // 懒加载首页组件
    meta: { title: '首页' }                                 // 页面标题
  },
  // 登录页面路由
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),         // 懒加载登录组件
    meta: { title: '登录', guestOnly: true }                // guestOnly: 仅未登录用户可访问
  },
  // 注册页面路由
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue'),      // 懒加载注册组件
    meta: { title: '注册', guestOnly: true }                // guestOnly: 仅未登录用户可访问
  },
  // 商品列表页面路由
  {
    path: '/products',
    name: 'ProductList',
    component: () => import('../views/ProductList.vue'),   // 懒加载商品列表组件
    meta: { title: '商品列表' }
  },
  // 商品详情页面路由（动态路由参数:id）
  {
    path: '/product/:id',
    name: 'ProductDetail',
    component: () => import('../views/ProductDetail.vue'), // 懒加载商品详情组件
    meta: { title: '商品详情' }
  },
  // 购物车页面路由
  {
    path: '/cart',
    name: 'Cart',
    component: () => import('../views/Cart.vue'),          // 懒加载购物车组件
    meta: { title: '购物车', requiresAuth: true }           // requiresAuth: 需要登录才能访问
  },
  // 结算页面路由
  {
    path: '/checkout',
    name: 'Checkout',
    component: () => import('../views/Checkout.vue'),      // 懒加载结算组件
    meta: { title: '结算', requiresAuth: true }             // requiresAuth: 需要登录才能访问
  },
  // 订单列表页面路由
  {
    path: '/orders',
    name: 'OrderList',
    component: () => import('../views/OrderList.vue'),     // 懒加载订单列表组件
    meta: { title: '我的订单', requiresAuth: true }         // requiresAuth: 需要登录才能访问
  },
  // 订单详情页面路由（动态路由参数:id）
  {
    path: '/order/:id',
    name: 'OrderDetail',
    component: () => import('../views/OrderDetail.vue'),   // 懒加载订单详情组件
    meta: { title: '订单详情', requiresAuth: true }         // requiresAuth: 需要登录才能访问
  },
  // 个人中心页面路由
  {
    path: '/profile',
    name: 'UserProfile',
    component: () => import('../views/UserProfile.vue'),   // 懒加载个人中心组件
    meta: { title: '个人中心', requiresAuth: true }         // requiresAuth: 需要登录才能访问
  },
  // 地址管理页面路由
  {
    path: '/address',
    name: 'AddressManage',
    component: () => import('../views/AddressManage.vue'), // 懒加载地址管理组件
    meta: { title: '地址管理', requiresAuth: true }         // requiresAuth: 需要登录才能访问
  },
  // 管理后台路由（嵌套路由）
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('../views/admin/AdminLayout.vue'), // 懒加载后台布局组件
    meta: { requiresAuth: true, requiresAdmin: true },          // 需要登录且为管理员
    children: [
      // 仪表盘（默认子路由）
      {
        path: '',
        name: 'AdminDashboard',
        component: () => import('../views/admin/Dashboard.vue'),
        meta: { title: '仪表盘' }
      },
      // 商品管理
      {
        path: 'products',
        name: 'AdminProductManage',
        component: () => import('../views/admin/ProductManage.vue'),
        meta: { title: '商品管理' }
      },
      // 订单管理
      {
        path: 'orders',
        name: 'AdminOrderManage',
        component: () => import('../views/admin/OrderManage.vue'),
        meta: { title: '订单管理' }
      },
      // 用户管理
      {
        path: 'users',
        name: 'AdminUserManage',
        component: () => import('../views/admin/UserManage.vue'),
        meta: { title: '用户管理' }
      },
      // 分类管理
      {
        path: 'categories',
        name: 'AdminCategoryManage',
        component: () => import('../views/admin/CategoryManage.vue'),
        meta: { title: '分类管理' }
      }
    ]
  }
]

/**
 * 创建路由实例
 * 使用HTML5 History模式（去除URL中的#）
 */
const router = createRouter({
  history: createWebHistory(),   // 使用HTML5历史记录模式
  routes                         // 注册路由配置
})

/**
 * 全局导航守卫
 * 在每个路由跳转前执行，用于权限控制和页面标题设置
 * @param {Route} to - 目标路由对象
 * @param {Route} from - 当前路由对象
 * @param {Function} next - 继续导航的函数
 */
router.beforeEach((to, from, next) => {
  // 设置页面标题，拼接站点名称
  document.title = to.meta.title ? `${to.meta.title} - 牢大商城` : '牢大商城'

  // 获取用户登录状态和管理员权限
  const userStore = useUserStore()
  const isLoggedIn = userStore.isLoggedIn
  const isAdmin = userStore.isAdmin

  // 检查是否需要登录且用户未登录
  if (to.meta.requiresAuth && !isLoggedIn) {
    ElMessage.warning('请先登录')
    // 重定向到登录页面，并携带原路径作为redirect参数
    next({ path: '/login', query: { redirect: to.fullPath } })
    return
  }

  // 检查是否需要管理员权限且用户不是管理员
  if (to.meta.requiresAdmin && !isAdmin) {
    ElMessage.error('没有管理权限')
    next({ path: '/' })           // 重定向到首页
    return
  }

  // 检查是否仅允许未登录用户访问且用户已登录
  if (to.meta.guestOnly && isLoggedIn) {
    next({ path: '/' })           // 已登录用户访问登录/注册页时重定向到首页
    return
  }

  // 所有检查通过，继续导航到目标路由
  next()
})

// 导出路由实例，供main.js引入
export default router
