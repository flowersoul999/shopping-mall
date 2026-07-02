<template>
  <div class="main-content">
    <div class="page-header">
      <h2 class="page-title">仪表盘</h2>
      <p class="page-desc">欢迎来到管理后台，查看系统概览</p>
    </div>
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
    <div class="section-row">
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
import { ref, onMounted } from 'vue'
import { ShoppingBag, Document, User, List, TrendCharts, Plus, ShoppingCart } from '@element-plus/icons-vue'
import { getProductList } from '../../api/product'
import { getAdminOrderList } from '../../api/order'

const stats = ref({ products: 0, orders: 0, users: 0, sales: 0 })
const orderStatus = ref({ pending: 0, paid: 0, shipped: 0, completed: 0 })

function formatPrice(price) { return Number(price).toFixed(2) }

async function loadStats() {
  try {
    const [productRes, orderRes] = await Promise.all([
      getProductList({ page: 1, pageSize: 1 }),
      getAdminOrderList({ page: 1, pageSize: 100 })
    ])
    if (productRes.code === 200) stats.value.products = productRes.data?.total || 0
    if (orderRes.code === 200) {
      const list = orderRes.data?.list || []
      stats.value.orders = orderRes.data?.total || list.length
      stats.value.sales = orderRes.data?.totalSales || 0
      stats.value.users = [...new Set(list.map(o => o.userId))].length
      orderStatus.value = {
        pending: list.filter(o => o.status === 'pending').length,
        paid: list.filter(o => o.status === 'paid').length,
        shipped: list.filter(o => o.status === 'shipped').length,
        completed: list.filter(o => o.status === 'completed').length
      }
    }
  } catch (error) { console.error('Failed to load stats:', error) }
}

onMounted(() => loadStats())
</script>

<style scoped>
.main-content { padding: 24px; }
.page-header { margin-bottom: 24px; }
.page-title { font-size: 24px; font-weight: 600; color: var(--text-color); margin: 0 0 8px; }
.page-desc { font-size: 14px; color: var(--text-secondary); margin: 0; }

.stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 24px; }
.stat-card { background: #fff; border-radius: var(--radius); padding: 20px; display: flex; align-items: center; gap: 16px; }
.stat-icon { width: 56px; height: 56px; border-radius: 12px; display: flex; align-items: center; justify-content: center; font-size: 24px; }
.stat-icon.products { background: #e6f7ff; color: #1890ff; }
.stat-icon.orders { background: #fff7e6; color: #fa8c16; }
.stat-icon.users { background: #f6ffed; color: #52c41a; }
.stat-icon.sales { background: #fff0f6; color: #eb2f96; }
.stat-info { flex: 1; }
.stat-value { font-size: 24px; font-weight: 700; color: var(--text-color); }
.stat-label { font-size: 13px; color: var(--text-secondary); }

.section-row { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
.section-card { background: #fff; border-radius: var(--radius); padding: 20px; }
.section-title { font-size: 16px; font-weight: 600; color: var(--text-color); margin: 0 0 16px; }

.status-list { display: flex; flex-direction: column; gap: 12px; }
.status-item { display: flex; align-items: center; justify-content: space-between; }
.status-dot { width: 8px; height: 8px; border-radius: 50%; margin-right: 8px; }
.status-dot.pending { background: #f56c6c; }
.status-dot.paid { background: #e6a23c; }
.status-dot.shipped { background: #409eff; }
.status-dot.completed { background: #67c23a; }
.status-name { flex: 1; font-size: 14px; color: var(--text-color); }
.status-count { font-size: 16px; font-weight: 600; color: var(--text-color); }

.quick-actions { display: grid; grid-template-columns: repeat(2, 1fr); gap: 12px; }
.action-btn { display: flex; align-items: center; justify-content: center; padding: 16px; background: #f5f7fa; border-radius: var(--radius); text-decoration: none; color: var(--text-color); transition: all 0.2s; gap: 8px; }
.action-btn:hover { background: #409eff; color: #fff; }
</style>