<template>
  <!-- 管理后台布局组件 -->
  <div class="admin-layout">
    <!-- 左侧侧边栏 -->
    <aside class="admin-sidebar">
      <!-- 侧边栏头部（标题） -->
      <div class="sidebar-header">
        <h2 class="sidebar-title">管理后台</h2>
      </div>
      <!-- 侧边栏导航菜单 -->
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
    <!-- 右侧主内容区域 -->
    <main class="admin-main">
      <router-view />
    </main>
  </div>
</template>

<script setup>
/**
 * AdminLayout管理后台布局组件
 * 提供管理后台的统一布局框架，包含侧边栏导航和主内容区域
 */
import { Grid, ShoppingBag, Document, User, List } from '@element-plus/icons-vue' // 引入Element Plus图标
</script>

<style scoped>
/**
 * 管理后台布局样式
 */
.admin-layout {
  display: flex;                                            /* flex布局 */
  min-height: calc(100vh - 60px);                           /* 减去顶部导航高度 */
}

/**
 * 侧边栏样式（固定定位）
 */
.admin-sidebar {
  width: 220px;                                             /* 固定宽度220px */
  background: #2a3f5f;                                     /* 深蓝色背景 */
  color: #fff;                                              /* 白色文字 */
  flex-shrink: 0;                                          /* 不缩小 */
  position: fixed;                                          /* 固定定位 */
  height: calc(100vh - 60px);                               /* 减去顶部导航高度 */
  overflow-y: auto;                                         /* 垂直滚动 */
  z-index: 999;                                             /* 高层级 */
}

/**
 * 侧边栏头部样式
 */
.sidebar-header {
  padding: 20px;                                           /* 内边距20px */
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);       /* 底部边框 */
}

/**
 * 侧边栏标题样式
 */
.sidebar-title {
  font-size: 18px;                                          /* 字号18px */
  font-weight: 700;                                         /* 字重700 */
  margin: 0;                                                /* 无外边距 */
  color: #fff;                                              /* 白色文字 */
}

/**
 * 侧边栏导航样式
 */
.sidebar-nav {
  padding: 12px 0;                                          /* 上下内边距12px */
}

/**
 * 导航项样式
 */
.nav-item {
  display: flex;                                            /* flex布局 */
  align-items: center;                                      /* 垂直居中 */
  padding: 12px 20px;                                       /* 内边距12px 20px */
  color: rgba(255, 255, 255, 0.8);                         /* 半透明白色 */
  text-decoration: none;                                     /* 去除下划线 */
  transition: all 0.2s;                                     /* 过渡动画 */
  gap: 10px;                                                /* 间距10px */
}

/**
 * 导航项悬停样式
 */
.nav-item:hover {
  background: rgba(255, 255, 255, 0.1);                    /* 悬停背景 */
  color: #fff;                                              /* 白色文字 */
}

/**
 * 导航项激活样式
 */
.nav-item.active {
  background: #409eff;                                      /* 蓝色背景 */
  color: #fff;                                              /* 白色文字 */
}

/**
 * 导航项图标样式
 */
.nav-item .el-icon {
  font-size: 18px;                                          /* 图标大小18px */
}

/**
 * 主内容区域样式
 */
.admin-main {
  flex: 1;                                                  /* 占满剩余空间 */
  margin-left: 220px;                                       /* 左侧边距220px */
  background: #f0f2f5;                                      /* 浅灰色背景 */
}
</style>