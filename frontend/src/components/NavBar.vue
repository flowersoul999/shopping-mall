<template>
  <div class="navbar">
    <div class="main-width navbar-inner">
      <!-- Logo -->
      <router-link to="/" class="logo">
        <span class="logo-text">乐购商城</span>
      </router-link>

      <!-- Search -->
      <div class="search-bar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索商品"
          :prefix-icon="Search"
          clearable
          @keyup.enter="handleSearch"
        />
      </div>

      <!-- Nav links -->
      <div class="nav-links">
        <template v-if="userStore.isLoggedIn">
          <router-link to="/cart" class="nav-link cart-link">
            <el-badge :value="cartStore.cartCount" :hidden="cartStore.cartCount === 0">
              <el-icon :size="20"><ShoppingCart /></el-icon>
            </el-badge>
            <span>购物车</span>
          </router-link>
          <router-link to="/orders" class="nav-link">我的订单</router-link>
          <router-link to="/profile" class="nav-link">{{ userStore.userInfo?.username || '个人中心' }}</router-link>
          <router-link v-if="userStore.isAdmin" to="/admin" class="nav-link admin-link">后台管理</router-link>
          <a class="nav-link logout-link" @click.prevent="handleLogout">退出</a>
        </template>
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
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Search, ShoppingCart } from '@element-plus/icons-vue'
import { useUserStore } from '../store/user'
import { useCartStore } from '../store/cart'

const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()

const searchKeyword = ref('')

function handleSearch() {
  if (searchKeyword.value.trim()) {
    router.push({ path: '/products', query: { keyword: searchKeyword.value.trim() } })
  }
}

function handleLogout() {
  userStore.logout()
}
</script>

<style scoped>
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

.navbar-inner {
  display: flex;
  align-items: center;
  height: 100%;
  gap: 24px;
}

.logo {
  flex-shrink: 0;
}

.logo-text {
  font-size: 22px;
  font-weight: 700;
  color: var(--primary);
  letter-spacing: 1px;
}

.search-bar {
  flex: 1;
  max-width: 480px;
}

.search-bar .el-input {
  --el-input-border-radius: 20px;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

.nav-link {
  font-size: 14px;
  color: var(--text-color);
  cursor: pointer;
  transition: color 0.2s;
  white-space: nowrap;
}

.nav-link:hover {
  color: var(--primary);
}

.cart-link {
  display: flex;
  align-items: center;
  gap: 4px;
}

.nav-divider {
  color: #dcdfe6;
  font-size: 12px;
}

.logout-link {
  color: var(--text-secondary);
}

.admin-link {
  color: #e6a23c;
}

.admin-link:hover {
  color: #cf9236;
}
</style>
