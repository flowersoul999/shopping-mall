<template>
  <!-- 根组件容器，采用flex布局实现页面整体结构 -->
  <div id="app-wrapper">
    <!-- 顶部导航栏组件，固定显示在页面顶部 -->
    <NavBar />
    <!-- 主内容区域，包含路由视图出口 -->
    <div class="main-content">
      <!-- 路由视图，根据当前路由渲染对应的页面组件 -->
      <router-view />
    </div>
    <!-- 底部页脚组件 -->
    <FooterBar />
  </div>
</template>

<script setup>
/**
 * App.vue 根组件
 * 作为整个应用的布局容器，包含导航栏、主内容区和页脚
 * 在应用挂载时自动加载用户信息和购物车数据
 */
import { onMounted } from 'vue'                           // 引入Vue的onMounted生命周期钩子
import NavBar from './components/NavBar.vue'             // 引入导航栏组件
import FooterBar from './components/FooterBar.vue'       // 引入页脚组件
import { useUserStore } from './store/user'              // 引入用户状态管理store
import { useCartStore } from './store/cart'              // 引入购物车状态管理store

// 获取用户store实例
const userStore = useUserStore()
// 获取购物车store实例
const cartStore = useCartStore()

/**
 * 应用挂载完成后执行的初始化逻辑
 * 如果用户已登录，则自动刷新用户信息和购物车列表
 */
onMounted(() => {
  if (userStore.isLoggedIn) {
    userStore.fetchUserInfo()    // 刷新用户信息
    cartStore.fetchCartList()    // 刷新购物车数据
  }
})
</script>

<style>
/**
 * 根容器样式
 * 使用flex布局实现页面的垂直分布
 */
#app-wrapper {
  min-height: 100vh;             /* 最小高度为视口高度，确保铺满屏幕 */
  display: flex;                 /* 使用flex布局 */
  flex-direction: column;        /* 垂直方向排列子元素 */
}

/**
 * 主内容区域样式
 */
.main-content {
  flex: 1;                       /* 占据剩余空间，实现footer固定在底部 */
  padding-top: 60px;             /* 预留导航栏高度，避免内容被遮挡 */
}
</style>
