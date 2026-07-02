<template>
  <div class="order-list page-container">
    <div class="main-width">
      <h2 class="page-title">我的订单</h2>

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

      <el-skeleton :loading="loading" animated>
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
        <template #default>
          <el-empty v-if="orderList.length === 0 && !loading" description="暂无订单" />

          <div v-else class="order-card-list">
            <div v-for="order in orderList" :key="order.id" class="order-card">
              <div class="order-header">
                <span class="order-no">订单号：{{ order.orderNo }}</span>
                <span :class="['order-status', order.status]">{{ getStatusText(order.status) }}</span>
              </div>

              <div class="order-items">
                <div
                  v-for="item in order.items"
                  :key="item.id"
                  class="order-item"
                  @click="goToProduct(item.productId)"
                >
                  <div class="item-image">
                    <el-image :src="item.productImage" fit="cover" style="width:100%;height:100%">
                      <template #error>
                        <div class="image-placeholder">
                          <el-icon :size="16"><Picture /></el-icon>
                        </div>
                      </template>
                    </el-image>
                  </div>
                  <div class="item-info">
                    <div class="item-name">{{ item.productName }}</div>
                    <div class="item-bottom">
                      <span class="item-price">&yen;{{ formatPrice(item.price) }}</span>
                      <span class="item-quantity">x{{ item.quantity }}</span>
                    </div>
                  </div>
                </div>
              </div>

              <div class="order-footer">
                <span class="order-total">合计：<strong>&yen;{{ formatPrice(order.totalPrice) }}</strong></span>
                <div class="order-actions">
                  <el-button
                    v-if="order.status === 'pending'"
                    type="primary"
                    @click="handlePay(order.id)"
                  >
                    去支付
                  </el-button>
                  <el-button
                    v-if="order.status === 'paid'"
                    type="primary"
                    @click="handleConfirm(order.id)"
                  >
                    确认收货
                  </el-button>
                  <el-button
                    v-if="order.status === 'shipped'"
                    type="primary"
                    @click="handleConfirm(order.id)"
                  >
                    确认收货
                  </el-button>
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
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { Picture } from '@element-plus/icons-vue'
import { getOrderList, updateOrderStatus } from '../api/order'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loading = ref(true)
const activeTab = ref('all')
const orderList = ref([])

const tabs = [
  { label: '全部', value: 'all' },
  { label: '待付款', value: 'pending' },
  { label: '待发货', value: 'paid' },
  { label: '待收货', value: 'shipped' },
  { label: '已完成', value: 'completed' }
]

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

function formatPrice(price) {
  return Number(price).toFixed(2)
}

function goToProduct(productId) {
  router.push(`/product/${productId}`)
}

function goToDetail(orderId) {
  router.push(`/order/${orderId}`)
}

async function loadOrders() {
  loading.value = true
  try {
    const params = {
      page: 1,
      pageSize: 20
    }
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

async function handlePay(orderId) {
  try {
    const res = await updateOrderStatus({ id: orderId, status: 'paid' })
    if (res.code === 200) {
      ElMessage.success('支付成功')
      await loadOrders()
    } else {
      ElMessage.error(res.message || '支付失败')
    }
  } catch (error) {
    console.error('Payment failed:', error)
    ElMessage.error('支付失败，请稍后重试')
  }
}

async function handleConfirm(orderId) {
  try {
    const res = await updateOrderStatus({ id: orderId, status: 'completed' })
    if (res.code === 200) {
      ElMessage.success('已确认收货')
      await loadOrders()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    console.error('Confirm failed:', error)
    ElMessage.error('操作失败，请稍后重试')
  }
}

watch(activeTab, () => {
  loadOrders()
})

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.order-tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 20px;
}

.order-card-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-card {
  background: #fff;
  border-radius: var(--radius);
  padding: 16px;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--border-color);
}

.order-no {
  font-size: 14px;
  color: var(--text-secondary);
}

.order-status {
  font-size: 14px;
  font-weight: 600;
}

.order-status.pending {
  color: #f56c6c;
}

.order-status.paid {
  color: #e6a23c;
}

.order-status.shipped {
  color: #409eff;
}

.order-status.completed {
  color: #67c23a;
}

.order-status.cancelled {
  color: #909399;
}

.order-items {
  padding: 12px 0;
}

.order-item {
  display: flex;
  gap: 12px;
  padding: 8px 0;
  cursor: pointer;
}

.item-image {
  width: 60px;
  height: 60px;
  background: #f8f8f8;
  border-radius: var(--radius);
  overflow: hidden;
  flex-shrink: 0;
}

.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.item-name {
  font-size: 14px;
  color: var(--text-color);
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.item-bottom {
  display: flex;
  justify-content: space-between;
  margin-top: auto;
}

.item-price {
  font-size: 14px;
  font-weight: 600;
  color: #f56c6c;
}

.item-quantity {
  font-size: 12px;
  color: var(--text-secondary);
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid var(--border-color);
}

.order-total {
  font-size: 14px;
  color: var(--text-color);
}

.order-total strong {
  font-size: 18px;
  color: #f56c6c;
}

.order-actions {
  display: flex;
  gap: 8px;
}

.image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #c0c4cc;
}

.skeleton-order {
  background: #fff;
  border-radius: var(--radius);
  padding: 16px;
  margin-bottom: 16px;
}
</style>