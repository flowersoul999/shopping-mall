<template>
  <!-- 管理后台仪表盘页面组件 -->
  <div class="main-content">
    <!-- 页面头部（标题和描述） -->
    <div class="page-header">
      <h2 class="page-title">仪表盘</h2>
      <p class="page-desc">欢迎来到管理后台，查看系统概览</p>
    </div>
    <!-- 统计卡片网格（商品总数、订单总数、用户总数、总销售额） -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon products">
          <el-icon><ShoppingBag /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.products }}</div>
          <div class="stat-label">商品总数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon orders">
          <el-icon><Document /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.orders }}</div>
          <div class="stat-label">订单总数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon users">
          <el-icon><User /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.users }}</div>
          <div class="stat-label">用户总数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon sales">
          <el-icon><TrendCharts /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">&yen;{{ formatPrice(stats.sales) }}</div>
          <div class="stat-label">总销售额</div>
        </div>
      </div>
    </div>
    <!-- 下方区域行（订单状态统计和快捷操作） -->
    <div class="section-row">
      <!-- 订单状态统计卡片 -->
      <div class="section-card">
        <h3 class="section-title">订单状态统计</h3>
        <div class="status-list">
          <div class="status-item">
            <span class="status-dot pending"></span>
            <span class="status-name">待付款</span>
            <span class="status-count">{{ orderStatus.pending }}</span>
          </div>
          <div class="status-item">
            <span class="status-dot paid"></span>
            <span class="status-name">待发货</span>
            <span class="status-count">{{ orderStatus.paid }}</span>
          </div>
          <div class="status-item">
            <span class="status-dot shipped"></span>
            <span class="status-name">待收货</span>
            <span class="status-count">{{ orderStatus.shipped }}</span>
          </div>
          <div class="status-item">
            <span class="status-dot completed"></span>
            <span class="status-name">已完成</span>
            <span class="status-count">{{ orderStatus.completed }}</span>
          </div>
        </div>
      </div>
      <!-- 快捷操作卡片 -->
      <div class="section-card">
        <h3 class="section-title">快捷操作</h3>
        <div class="quick-actions">
          <router-link to="/admin/products" class="action-btn">
            <el-icon><Plus /></el-icon>
            <span>添加商品</span>
          </router-link>
          <router-link to="/admin/orders" class="action-btn">
            <el-icon><ShoppingCart /></el-icon>
            <span>处理订单</span>
          </router-link>
          <router-link to="/admin/categories" class="action-btn">
            <el-icon><List /></el-icon>
            <span>管理分类</span>
          </router-link>
          <router-link to="/admin/users" class="action-btn">
            <el-icon><User /></el-icon>
            <span>管理用户</span>
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * Dashboard仪表盘页面组件
 * 展示管理后台的数据概览，包括商品总数、订单总数、用户总数、总销售额和订单状态统计
 */
import { ref, onMounted } from 'vue'                         // 引入Vue响应式API和生命周期钩子
import { ShoppingBag, Document, User, List, TrendCharts, Plus, ShoppingCart } from '@element-plus/icons-vue' // 引入Element Plus图标
import { getProductList } from '../../api/product'           // 引入商品API
import { getAdminOrderList } from '../../api/order'          // 引入订单API

/**
 * 统计数据（商品数、订单数、用户数、销售额）
 */
const stats = ref({ products: 0, orders: 0, users: 0, sales: 0 })
/**
 * 订单状态统计（待付款、待发货、待收货、已完成）
 */
const orderStatus = ref({ pending: 0, paid: 0, shipped: 0, completed: 0 })

/**
 * 格式化价格
 * @param {number} price - 价格
 * @returns {string} 格式化后的价格字符串
 */
function formatPrice(price) { return Number(price).toFixed(2) }

/**
 * 加载统计数据
 * 通过并行请求商品列表和订单列表获取统计信息
 */
async function loadStats() {
  try {
    // 并行请求商品和订单数据
    const [productRes, orderRes] = await Promise.all([
      getProductList({ page: 1, pageSize: 1 }),
      getAdminOrderList({ page: 1, pageSize: 100 })
    ])
    // 更新商品总数
    if (productRes.code === 200) stats.value.products = productRes.data?.total || 0
    // 更新订单相关统计
    if (orderRes.code === 200) {
      const list = orderRes.data?.list || []
      stats.value.orders = orderRes.data?.total || list.length
      stats.value.sales = orderRes.data?.totalSales || 0
      // 计算去重后的用户数
      stats.value.users = [...new Set(list.map(o => o.userId))].length
      // 统计各状态订单数
      orderStatus.value = {
        pending: list.filter(o => o.status === 'pending').length,
        paid: list.filter(o => o.status === 'paid').length,
        shipped: list.filter(o => o.status === 'shipped').length,
        completed: list.filter(o => o.status === 'completed').length
      }
    }
  } catch (error) { console.error('Failed to load stats:', error) }
}

/**
 * 页面挂载时加载统计数据
 */
onMounted(() => loadStats())
</script>

<style scoped>
/**
 * 主内容区域样式
 */
.main-content { padding: 24px; }
/**
 * 页面头部样式
 */
.page-header { margin-bottom: 24px; }
/**
 * 页面标题样式
 */
.page-title { font-size: 24px; font-weight: 600; color: var(--text-color); margin: 0 0 8px; }
/**
 * 页面描述样式
 */
.page-desc { font-size: 14px; color: var(--text-secondary); margin: 0; }

/**
 * 统计卡片网格样式（4列）
 */
.stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 24px; }
/**
 * 统计卡片样式
 */
.stat-card { background: #fff; border-radius: var(--radius); padding: 20px; display: flex; align-items: center; gap: 16px; }
/**
 * 统计图标样式
 */
.stat-icon { width: 56px; height: 56px; border-radius: 12px; display: flex; align-items: center; justify-content: center; font-size: 24px; }
/**
 * 商品统计图标颜色（蓝色）
 */
.stat-icon.products { background: #e6f7ff; color: #1890ff; }
/**
 * 订单统计图标颜色（橙色）
 */
.stat-icon.orders { background: #fff7e6; color: #fa8c16; }
/**
 * 用户统计图标颜色（绿色）
 */
.stat-icon.users { background: #f6ffed; color: #52c41a; }
/**
 * 销售额统计图标颜色（粉色）
 */
.stat-icon.sales { background: #fff0f6; color: #eb2f96; }
/**
 * 统计信息区域样式
 */
.stat-info { flex: 1; }
/**
 * 统计数值样式
 */
.stat-value { font-size: 24px; font-weight: 700; color: var(--text-color); }
/**
 * 统计标签样式
 */
.stat-label { font-size: 13px; color: var(--text-secondary); }

/**
 * 区域行样式（2列）
 */
.section-row { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
/**
 * 区域卡片样式
 */
.section-card { background: #fff; border-radius: var(--radius); padding: 20px; }
/**
 * 区域标题样式
 */
.section-title { font-size: 16px; font-weight: 600; color: var(--text-color); margin: 0 0 16px; }

/**
 * 状态列表样式
 */
.status-list { display: flex; flex-direction: column; gap: 12px; }
/**
 * 状态项样式
 */
.status-item { display: flex; align-items: center; justify-content: space-between; }
/**
 * 状态圆点样式
 */
.status-dot { width: 8px; height: 8px; border-radius: 50%; margin-right: 8px; }
/**
 * 待付款状态颜色（红色）
 */
.status-dot.pending { background: #f56c6c; }
/**
 * 待发货状态颜色（橙色）
 */
.status-dot.paid { background: #e6a23c; }
/**
 * 待收货状态颜色（蓝色）
 */
.status-dot.shipped { background: #409eff; }
/**
 * 已完成状态颜色（绿色）
 */
.status-dot.completed { background: #67c23a; }
/**
 * 状态名称样式
 */
.status-name { flex: 1; font-size: 14px; color: var(--text-color); }
/**
 * 状态数量样式
 */
.status-count { font-size: 16px; font-weight: 600; color: var(--text-color); }

/**
 * 快捷操作区域样式（2列网格）
 */
.quick-actions { display: grid; grid-template-columns: repeat(2, 1fr); gap: 12px; }
/**
 * 快捷操作按钮样式
 */
.action-btn { display: flex; align-items: center; justify-content: center; padding: 16px; background: #f5f7fa; border-radius: var(--radius); text-decoration: none; color: var(--text-color); transition: all 0.2s; gap: 8px; }
/**
 * 快捷操作按钮悬停样式
 */
.action-btn:hover { background: #409eff; color: #fff; }
</style>