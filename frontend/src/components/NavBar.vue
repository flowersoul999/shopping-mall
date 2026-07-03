<template>
  <!-- 导航栏组件 -->
  <!-- 固定在页面顶部，包含Logo、搜索框和导航链接 -->
  <div class="navbar">
    <!-- 导航栏内部容器，限制最大宽度并居中 -->
    <div class="main-width navbar-inner">
      <!-- Logo区域，点击跳转到首页 -->
      <router-link to="/" class="logo">
        <span class="logo-text">牢大商城</span>
      </router-link>

      <!-- 搜索框区域 -->
      <div class="search-bar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索商品"
          :prefix-icon="Search"
          clearable
          @keyup.enter="handleSearch"
        />
      </div>

      <!-- 导航链接区域 -->
      <div class="nav-links">
        <!-- 已登录状态显示的导航项 -->
        <template v-if="userStore.isLoggedIn">
          <!-- 购物车链接，带有数量徽章 -->
          <router-link to="/cart" class="nav-link cart-link">
            <el-badge :value="cartStore.cartCount" :hidden="cartStore.cartCount === 0">
              <el-icon :size="20"><ShoppingCart /></el-icon>
            </el-badge>
            <span>购物车</span>
          </router-link>
          <!-- 我的订单链接 -->
          <router-link to="/orders" class="nav-link">我的订单</router-link>
          <!-- 个人中心链接 -->
          <router-link to="/profile" class="nav-link">个人中心</router-link>
          <!-- 后台管理链接（仅管理员显示） -->
          <router-link v-if="userStore.isAdmin" to="/admin" class="nav-link admin-link">后台管理</router-link>
          <!-- 退出登录链接 -->
          <a class="nav-link logout-link" @click.prevent="handleLogout">退出</a>
        </template>
        <!-- 未登录状态显示的导航项 -->
        <template v-else>
          <router-link to="/login" class="nav-link">登录</router-link>
          <span class="nav-divider">|</span>
          <router-link to="/register" class="nav-link">注册</router-link>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * NavBar导航栏组件
 * 提供全站统一的顶部导航，包含Logo、搜索功能和用户导航链接
 * 根据用户登录状态动态显示不同的导航项
 */
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Search, ShoppingCart } from '@element-plus/icons-vue'
import { useUserStore } from '../store/user'
import { useCartStore } from '../store/cart'

// 获取路由实例
const router = useRouter()
// 获取用户store实例
const userStore = useUserStore()
// 获取购物车store实例
const cartStore = useCartStore()

/**
 * 搜索关键词，响应式变量
 */
const searchKeyword = ref('')

/**
 * 处理搜索功能
 * 当关键词不为空时，跳转到商品列表页并携带搜索参数
 */
function handleSearch() {
  if (searchKeyword.value.trim()) {
    router.push({ path: '/products', query: { keyword: searchKeyword.value.trim() } })
  }
}

/**
 * 处理退出登录
 * 调用用户store的logout方法
 */
function handleLogout() {
  userStore.logout()
}
</script>

<style scoped>
/**
 * 导航栏主样式
 * 固定定位在页面顶部，高度60px
 */
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 60px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  z-index: 1000;
}

/**
 * 导航栏内部容器样式
 */
.navbar-inner {
  display: flex;
  align-items: center;
  height: 100%;
  gap: 24px;
}

/**
 * Logo样式
 */
.logo {
  flex-shrink: 0;
}

/**
 * Logo文字样式
 */
.logo-text {
  font-size: 22px;
  font-weight: 700;
  color: var(--primary);
  letter-spacing: 1px;
}

/**
 * 搜索框区域样式
 */
.search-bar {
  flex: 1;
  max-width: 360px;
  min-width: 200px;
}

/**
 * 搜索框圆角样式覆盖
 */
.search-bar .el-input {
  --el-input-border-radius: 20px;
}

/**
 * 导航链接区域样式
 */
.nav-links {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-shrink: 0;
  order: 3;
}

/**
 * 导航链接通用样式
 */
.nav-link {
  font-size: 14px;
  color: var(--text-color);
  cursor: pointer;
  transition: color 0.2s;
  white-space: nowrap;
}

/**
 * 导航链接悬停样式
 */
.nav-link:hover {
  color: var(--primary);
}

/**
 * 购物车链接样式
 */
.cart-link {
  display: flex;
  align-items: center;
  gap: 4px;
}

/**
 * 分隔线样式
 */
.nav-divider {
  color: #dcdfe6;
  font-size: 12px;
}

/**
 * 退出链接样式
 */
.logout-link {
  color: var(--text-secondary);
}

/**
 * 管理员链接样式
 */
.admin-link {
  color: #e6a23c;
}

/**
 * 管理员链接悬停样式
 */
.admin-link:hover {
  color: #cf9236;
}
</style>