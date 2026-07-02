import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../store/user'
import { ElMessage } from 'element-plus'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { title: '登录', guestOnly: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue'),
    meta: { title: '注册', guestOnly: true }
  },
  {
    path: '/products',
    name: 'ProductList',
    component: () => import('../views/ProductList.vue'),
    meta: { title: '商品列表' }
  },
  {
    path: '/product/:id',
    name: 'ProductDetail',
    component: () => import('../views/ProductDetail.vue'),
    meta: { title: '商品详情' }
  },
  {
    path: '/cart',
    name: 'Cart',
    component: () => import('../views/Cart.vue'),
    meta: { title: '购物车', requiresAuth: true }
  },
  {
    path: '/checkout',
    name: 'Checkout',
    component: () => import('../views/Checkout.vue'),
    meta: { title: '结算', requiresAuth: true }
  },
  {
    path: '/orders',
    name: 'OrderList',
    component: () => import('../views/OrderList.vue'),
    meta: { title: '我的订单', requiresAuth: true }
  },
  {
    path: '/order/:id',
    name: 'OrderDetail',
    component: () => import('../views/OrderDetail.vue'),
    meta: { title: '订单详情', requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'UserProfile',
    component: () => import('../views/UserProfile.vue'),
    meta: { title: '个人中心', requiresAuth: true }
  },
  {
    path: '/address',
    name: 'AddressManage',
    component: () => import('../views/AddressManage.vue'),
    meta: { title: '地址管理', requiresAuth: true }
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('../views/admin/AdminLayout.vue'),
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      {
        path: '',
        name: 'AdminDashboard',
        component: () => import('../views/admin/Dashboard.vue'),
        meta: { title: '仪表盘' }
      },
      {
        path: 'products',
        name: 'AdminProductManage',
        component: () => import('../views/admin/ProductManage.vue'),
        meta: { title: '商品管理' }
      },
      {
        path: 'orders',
        name: 'AdminOrderManage',
        component: () => import('../views/admin/OrderManage.vue'),
        meta: { title: '订单管理' }
      },
      {
        path: 'users',
        name: 'AdminUserManage',
        component: () => import('../views/admin/UserManage.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'categories',
        name: 'AdminCategoryManage',
        component: () => import('../views/admin/CategoryManage.vue'),
        meta: { title: '分类管理' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Navigation guard
router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 牢大商城` : '牢大商城'

  const userStore = useUserStore()
  const isLoggedIn = userStore.isLoggedIn
  const isAdmin = userStore.isAdmin

  if (to.meta.requiresAuth && !isLoggedIn) {
    ElMessage.warning('请先登录')
    next({ path: '/login', query: { redirect: to.fullPath } })
    return
  }

  if (to.meta.requiresAdmin && !isAdmin) {
    ElMessage.error('没有管理权限')
    next({ path: '/' })
    return
  }

  if (to.meta.guestOnly && isLoggedIn) {
    next({ path: '/' })
    return
  }

  next()
})

export default router
