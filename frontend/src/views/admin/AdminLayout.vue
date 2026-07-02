<template>
  <div class="admin-layout">
    <aside class="admin-sidebar">
      <div class="sidebar-header">
        <h2 class="sidebar-title">管理后台</h2>
      </div>
      <nav class="sidebar-nav">
        <router-link to="/admin" class="nav-item" :class="{ active: $route.path === '/admin' }">
          <el-icon><Grid /></el-icon>
          <span>仪表盘</span>
        </router-link>
        <router-link to="/admin/products" class="nav-item" :class="{ active: $route.path.startsWith('/admin/products') }">
          <el-icon><ShoppingBag /></el-icon>
          <span>商品管理</span>
        </router-link>
        <router-link to="/admin/orders" class="nav-item" :class="{ active: $route.path.startsWith('/admin/orders') }">
          <el-icon><Document /></el-icon>
          <span>订单管理</span>
        </router-link>
        <router-link to="/admin/users" class="nav-item" :class="{ active: $route.path.startsWith('/admin/users') }">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </router-link>
        <router-link to="/admin/categories" class="nav-item" :class="{ active: $route.path.startsWith('/admin/categories') }">
          <el-icon><List /></el-icon>
          <span>分类管理</span>
        </router-link>
      </nav>
    </aside>
    <main class="admin-main">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { Grid, ShoppingBag, Document, User, List } from '@element-plus/icons-vue'
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: calc(100vh - 60px);
}

.admin-sidebar {
  width: 220px;
  background: #2a3f5f;
  color: #fff;
  flex-shrink: 0;
  position: fixed;
  height: calc(100vh - 60px);
  overflow-y: auto;
  z-index: 999;
}

.sidebar-header {
  padding: 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.sidebar-title {
  font-size: 18px;
  font-weight: 700;
  margin: 0;
  color: #fff;
}

.sidebar-nav {
  padding: 12px 0;
}

.nav-item {
  display: flex;
  align-items: center;
  padding: 12px 20px;
  color: rgba(255, 255, 255, 0.8);
  text-decoration: none;
  transition: all 0.2s;
  gap: 10px;
}

.nav-item:hover {
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
}

.nav-item.active {
  background: #409eff;
  color: #fff;
}

.nav-item .el-icon {
  font-size: 18px;
}

.admin-main {
  flex: 1;
  margin-left: 220px;
  background: #f0f2f5;
}
</style>