<template>
  <!-- 订单列表页面组件 -->
  <div class="order-list page-container">
    <div class="main-width">
      <!-- 页面标题 -->
      <h2 class="page-title">我的订单</h2>

      <!-- 订单状态标签页 -->
      <div class="order-tabs">
        <el-button
          v-for="tab in tabs"
          :key="tab.value"
          :type="activeTab === tab.value ? 'primary' : 'default'"
          @click="activeTab = tab.value"
        >
          {{ tab.label }}
        </el-button>
      </div>

      <!-- 骨架屏加载组件 -->
      <el-skeleton :loading="loading" animated>
        <!-- 骨架屏模板 -->
        <template #template>
          <div v-for="i in 3" :key="i" class="skeleton-order">
            <div style="margin-bottom:12px">
              <el-skeleton-item variant="p" style="width:30%" />
              <el-skeleton-item variant="p" style="width:20%;float:right" />
            </div>
            <div v-for="j in 2" :key="j" style="display:flex;gap:12px;margin-bottom:12px">
              <el-skeleton-item variant="image" style="width:60px;height:60px" />
              <div style="flex:1">
                <el-skeleton-item variant="p" style="width:60%" />
                <el-skeleton-item variant="p" style="width:40%" />
              </div>
            </div>
            <div style="text-align:right">
              <el-skeleton-item variant="button" style="width:100px" />
            </div>
          </div>
        </template>
        <!-- 实际内容 -->
        <template #default>
          <!-- 空状态 -->
          <el-empty v-if="orderList.length === 0 && !loading" description="暂无订单" />

          <!-- 订单列表 -->
          <div v-else class="order-card-list">
            <div v-for="order in orderList" :key="order.id" class="order-card">
              <!-- 订单头部（订单号和状态） -->
              <div class="order-header">
                <span class="order-no">订单号：{{ order.orderNo }}</span>
                <span :class="['order-status', order.status]">{{ getStatusText(order.status) }}</span>
              </div>

              <!-- 订单商品列表 -->
              <div class="order-items">
                <div
                  v-for="item in order.items"
                  :key="item.id"
                  class="order-item"
                  @click="goToProduct(item.productId)"
                >
                  <!-- 商品图片 -->
                  <div class="item-image">
                    <el-image :src="item.productImage" fit="cover" style="width:100%;height:100%">
                      <template #error>
                        <div class="image-placeholder">
                          <el-icon :size="16"><Picture /></el-icon>
                        </div>
                      </template>
                    </el-image>
                  </div>
                  <!-- 商品信息 -->
                  <div class="item-info">
                    <div class="item-name">{{ item.productName }}</div>
                    <div class="item-bottom">
                      <span class="item-price">&yen;{{ formatPrice(item.price) }}</span>
                      <span class="item-quantity">x{{ item.quantity }}</span>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 订单底部（合计和操作按钮） -->
              <div class="order-footer">
                <span class="order-total">合计：<strong>&yen;{{ formatPrice(order.totalPrice) }}</strong></span>
                <div class="order-actions">
                  <!-- 待付款：去支付 -->
                  <el-button
                    v-if="order.status === 'pending'"
                    type="primary"
                    @click="handlePay(order.id)"
                  >
                    去支付
                  </el-button>
                  <!-- 待发货：确认收货（实际应为发货后） -->
                  <el-button
                    v-if="order.status === 'paid'"
                    type="primary"
                    @click="handleConfirm(order.id)"
                  >
                    确认收货
                  </el-button>
                  <!-- 待收货：确认收货 -->
                  <el-button
                    v-if="order.status === 'shipped'"
                    type="primary"
                    @click="handleConfirm(order.id)"
                  >
                    确认收货
                  </el-button>
                  <!-- 已完成：查看详情 -->
                  <el-button
                    v-if="order.status === 'completed'"
                    type="default"
                    @click="goToDetail(order.id)"
                  >
                    查看详情
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </template>
      </el-skeleton>
    </div>
  </div>
</template>

<script setup>
/**
 * OrderList订单列表页面组件
 * 展示用户订单列表，支持按状态筛选和订单操作（支付、确认收货等）
 */
import { ref, onMounted, watch } from 'vue'               // 引入Vue响应式API和生命周期钩子
import { useRouter } from 'vue-router'                     // 引入路由实例
import { Picture } from '@element-plus/icons-vue'          // 引入图片图标
import { getOrderList, updateOrderStatus } from '../api/order' // 引入订单API
import { ElMessage } from 'element-plus'                   // 引入Element Plus消息提示

// 获取路由实例
const router = useRouter()
/**
 * 加载状态
 */
const loading = ref(true)
/**
 * 当前激活的标签页
 */
const activeTab = ref('all')
/**
 * 订单列表数据
 */
const orderList = ref([])

/**
 * 订单状态标签页配置
 */
const tabs = [
  { label: '全部', value: 'all' },
  { label: '待付款', value: 'pending' },
  { label: '待发货', value: 'paid' },
  { label: '待收货', value: 'shipped' },
  { label: '已完成', value: 'completed' }
]

/**
 * 获取订单状态中文描述
 * @param {string} status - 订单状态码
 * @returns {string} 状态中文描述
 */
function getStatusText(status) {
  const map = {
    pending: '待付款',
    paid: '待发货',
    shipped: '待收货',
    completed: '已完成',
    cancelled: '已取消'
  }
  return map[status] || status
}

/**
 * 格式化价格
 * @param {number} price - 价格
 * @returns {string} 格式化后的价格字符串
 */
function formatPrice(price) {
  return Number(price).toFixed(2)
}

/**
 * 跳转到商品详情页
 * @param {number} productId - 商品ID
 */
function goToProduct(productId) {
  router.push(`/product/${productId}`)
}

/**
 * 跳转到订单详情页
 * @param {number} orderId - 订单ID
 */
function goToDetail(orderId) {
  router.push(`/order/${orderId}`)
}

/**
 * 加载订单列表
 * 根据当前选中的标签页筛选订单
 */
async function loadOrders() {
  loading.value = true
  try {
    const params = {
      page: 1,
      pageSize: 20
    }
    // 如果不是"全部"标签，添加状态筛选条件
    if (activeTab.value !== 'all') {
      params.status = activeTab.value
    }
    const res = await getOrderList(params)
    if (res.code === 200) {
      orderList.value = res.data?.list || []
    }
  } catch (error) {
    console.error('Failed to load orders:', error)
    orderList.value = []
  } finally {
    loading.value = false
  }
}

/**
 * 处理支付
 * 将订单状态更新为已支付
 * @param {number} orderId - 订单ID
 */
async function handlePay(orderId) {
  try {
    const res = await updateOrderStatus({ id: orderId, status: 'paid' })
    if (res.code === 200) {
      ElMessage.success('支付成功')
      // 刷新订单列表
      await loadOrders()
    } else {
      ElMessage.error(res.message || '支付失败')
    }
  } catch (error) {
    console.error('Payment failed:', error)
    ElMessage.error('支付失败，请稍后重试')
  }
}

/**
 * 处理确认收货
 * 将订单状态更新为已完成
 * @param {number} orderId - 订单ID
 */
async function handleConfirm(orderId) {
  try {
    const res = await updateOrderStatus({ id: orderId, status: 'completed' })
    if (res.code === 200) {
      ElMessage.success('已确认收货')
      // 刷新订单列表
      await loadOrders()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    console.error('Confirm failed:', error)
    ElMessage.error('操作失败，请稍后重试')
  }
}

/**
 * 监听标签页变化
 * 切换标签时重新加载订单列表
 */
watch(activeTab, () => {
  loadOrders()
})

/**
 * 页面挂载时加载订单列表
 */
onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
/**
 * 订单标签页样式
 */
.order-tabs {
  display: flex;                                            /* flex布局 */
  gap: 8px;                                                /* 间距8px */
  margin-bottom: 20px;                                      /* 下外边距20px */
}

/**
 * 订单卡片列表样式
 */
.order-card-list {
  display: flex;                                            /* flex布局 */
  flex-direction: column;                                   /* 垂直排列 */
  gap: 16px;                                                /* 间距16px */
}

/**
 * 订单卡片样式
 */
.order-card {
  background: #fff;                                         /* 白色背景 */
  border-radius: var(--radius);                             /* 圆角 */
  padding: 16px;                                           /* 内边距16px */
}

/**
 * 订单头部样式
 */
.order-header {
  display: flex;                                            /* flex布局 */
  justify-content: space-between;                            /* 两端对齐 */
  align-items: center;                                      /* 垂直居中 */
  padding-bottom: 12px;                                     /* 下内边距12px */
  border-bottom: 1px solid var(--border-color);             /* 底部边框 */
}

/**
 * 订单号样式
 */
.order-no {
  font-size: 14px;                                          /* 字号14px */
  color: var(--text-secondary);                             /* 次要文字颜色 */
}

/**
 * 订单状态样式
 */
.order-status {
  font-size: 14px;                                          /* 字号14px */
  font-weight: 600;                                         /* 字重600 */
}

/**
 * 待付款状态颜色
 */
.order-status.pending {
  color: #f56c6c;                                          /* 红色 */
}

/**
 * 待发货状态颜色
 */
.order-status.paid {
  color: #e6a23c;                                          /* 橙色 */
}

/**
 * 待收货状态颜色
 */
.order-status.shipped {
  color: #409eff;                                          /* 蓝色 */
}

/**
 * 已完成状态颜色
 */
.order-status.completed {
  color: #67c23a;                                          /* 绿色 */
}

/**
 * 已取消状态颜色
 */
.order-status.cancelled {
  color: #909399;                                          /* 灰色 */
}

/**
 * 订单商品列表样式
 */
.order-items {
  padding: 12px 0;                                          /* 上下内边距12px */
}

/**
 * 订单商品项样式
 */
.order-item {
  display: flex;                                            /* flex布局 */
  gap: 12px;                                                /* 间距12px */
  padding: 8px 0;                                          /* 上下内边距8px */
  cursor: pointer;                                          /* 鼠标指针变为手型 */
}

/**
 * 商品图片样式
 */
.item-image {
  width: 60px;                                            /* 固定宽度60px */
  height: 60px;                                           /* 固定高度60px */
  background: #f8f8f8;                                     /* 浅灰色背景 */
  border-radius: var(--radius);                             /* 圆角 */
  overflow: hidden;                                         /* 溢出隐藏 */
  flex-shrink: 0;                                          /* 不缩小 */
}

/**
 * 商品信息样式
 */
.item-info {
  flex: 1;                                                  /* 占满剩余空间 */
  display: flex;                                            /* flex布局 */
  flex-direction: column;                                   /* 垂直排列 */
}

/**
 * 商品名称样式（支持两行省略）
 */
.item-name {
  font-size: 14px;                                          /* 字号14px */
  color: var(--text-color);                                 /* 文字颜色 */
  line-height: 1.4;                                         /* 行高1.4 */
  display: -webkit-box;                                     /* 弹性盒子 */
  -webkit-line-clamp: 2;                                    /* 最多两行 */
  -webkit-box-orient: vertical;                             /* 垂直方向 */
  overflow: hidden;                                         /* 溢出隐藏 */
}

/**
 * 商品底部（价格和数量）样式
 */
.item-bottom {
  display: flex;                                            /* flex布局 */
  justify-content: space-between;                            /* 两端对齐 */
  margin-top: auto;                                         /* 自动上推 */
}

/**
 * 商品价格样式
 */
.item-price {
  font-size: 14px;                                          /* 字号14px */
  font-weight: 600;                                         /* 字重600 */
  color: #f56c6c;                                          /* 红色 */
}

/**
 * 商品数量样式
 */
.item-quantity {
  font-size: 12px;                                          /* 字号12px */
  color: var(--text-secondary);                             /* 次要文字颜色 */
}

/**
 * 订单底部样式
 */
.order-footer {
  display: flex;                                            /* flex布局 */
  justify-content: space-between;                            /* 两端对齐 */
  align-items: center;                                      /* 垂直居中 */
  padding-top: 12px;                                        /* 上内边距12px */
  border-top: 1px solid var(--border-color);                 /* 顶部边框 */
}

/**
 * 订单合计样式
 */
.order-total {
  font-size: 14px;                                          /* 字号14px */
  color: var(--text-color);                                 /* 文字颜色 */
}

/**
 * 订单合计金额样式
 */
.order-total strong {
  font-size: 18px;                                          /* 字号18px */
  color: #f56c6c;                                          /* 红色 */
}

/**
 * 订单操作按钮区域样式
 */
.order-actions {
  display: flex;                                            /* flex布局 */
  gap: 8px;                                                /* 间距8px */
}

/**
 * 图片占位符样式
 */
.image-placeholder {
  width: 100%;                                              /* 宽度100% */
  height: 100%;                                             /* 高度100% */
  display: flex;                                            /* flex布局 */
  align-items: center;                                      /* 垂直居中 */
  justify-content: center;                                  /* 水平居中 */
  color: #c0c4cc;                                          /* 灰色图标 */
}

/**
 * 骨架屏订单样式
 */
.skeleton-order {
  background: #fff;                                         /* 白色背景 */
  border-radius: var(--radius);                             /* 圆角 */
  padding: 16px;                                           /* 内边距16px */
  margin-bottom: 16px;                                      /* 下外边距16px */
}
</style>