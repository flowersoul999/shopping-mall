<template>
  <div class="order-detail page-container">
    <div class="main-width">
      <div class="page-header">
        <el-button @click="goBack">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
        <h2 class="page-title">订单详情</h2>
      </div>

      <el-skeleton :loading="loading" animated>
        <template #template>
          <div class="skeleton-section">
            <el-skeleton-item variant="h3" style="width:40%" />
            <div style="margin-top:12px">
              <el-skeleton-item variant="p" style="width:60%" />
              <el-skeleton-item variant="p" style="width:50%" />
              <el-skeleton-item variant="p" style="width:40%" />
            </div>
          </div>
          <div class="skeleton-section" style="margin-top:16px">
            <el-skeleton-item variant="h3" style="width:25%" />
            <div v-for="i in 2" :key="i" style="display:flex;gap:12px;margin-top:12px">
              <el-skeleton-item variant="image" style="width:80px;height:80px" />
              <div style="flex:1">
                <el-skeleton-item variant="p" style="width:70%" />
                <el-skeleton-item variant="p" style="width:40%" />
                <el-skeleton-item variant="p" style="width:30%" />
              </div>
            </div>
          </div>
          <div class="skeleton-section" style="margin-top:16px">
            <el-skeleton-item variant="h3" style="width:25%" />
            <div style="margin-top:12px">
              <el-skeleton-item variant="p" style="width:50%" />
              <el-skeleton-item variant="p" style="width:50%" />
              <el-skeleton-item variant="p" style="width:50%" />
              <el-skeleton-item variant="p" style="width:50%" />
            </div>
          </div>
        </template>
        <template #default>
          <div v-if="!loading && !order" class="empty-order">
            <el-empty description="订单不存在" />
          </div>

          <div v-else-if="order">
            <div class="detail-section">
              <div class="section-header">
                <el-icon><Ticket /></el-icon>
                <span>订单信息</span>
              </div>
              <div class="info-grid">
                <div class="info-item">
                  <span class="info-label">订单号</span>
                  <span class="info-value">{{ order.orderNo }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">下单时间</span>
                  <span class="info-value">{{ order.createTime }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">订单状态</span>
                  <span :class="['info-value status', order.status]">{{ getStatusText(order.status) }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">实付金额</span>
                  <span class="info-value price">&yen;{{ formatPrice(order.totalPrice) }}</span>
                </div>
              </div>
            </div>

            <div class="detail-section">
              <div class="section-header">
                <el-icon><MapLocation /></el-icon>
                <span>收货地址</span>
              </div>
              <div class="address-info" v-if="order.address">
                <div class="address-header">
                  <span class="address-name">{{ order.address.name }}</span>
                  <span class="address-phone">{{ order.address.phone }}</span>
                </div>
                <div class="address-detail">{{ order.address.province }}{{ order.address.city }}{{ order.address.district }}{{ order.address.detail }}</div>
              </div>
              <div v-else class="no-address">地址信息已删除</div>
            </div>

            <div class="detail-section">
              <div class="section-header">
                <el-icon><ShoppingBag /></el-icon>
                <span>商品清单</span>
              </div>
              <div class="product-list">
                <div v-for="item in order.items" :key="item.id" class="product-item">
                  <div class="product-image">
                    <el-image :src="item.productImage" fit="cover" style="width:100%;height:100%">
                      <template #error>
                        <div class="image-placeholder">
                          <el-icon :size="20"><Picture /></el-icon>
                        </div>
                      </template>
                    </el-image>
                  </div>
                  <div class="product-info">
                    <div class="product-name">{{ item.productName }}</div>
                    <div class="product-spec">规格：默认</div>
                    <div class="product-bottom">
                      <span class="product-price">&yen;{{ formatPrice(item.price) }}</span>
                      <span class="product-quantity">x{{ item.quantity }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div class="detail-section">
              <div class="section-header">
                <el-icon><Document /></el-icon>
                <span>费用明细</span>
              </div>
              <div class="fee-list">
                <div class="fee-row">
                  <span class="fee-label">商品金额</span>
                  <span class="fee-value">&yen;{{ formatPrice(order.totalPrice) }}</span>
                </div>
                <div class="fee-row">
                  <span class="fee-label">运费</span>
                  <span class="fee-value">免运费</span>
                </div>
                <div class="fee-row">
                  <span class="fee-label">优惠</span>
                  <span class="fee-value discount">-¥0.00</span>
                </div>
                <div class="fee-row total">
                  <span class="fee-label">实付金额</span>
                  <span class="fee-value total-price">&yen;{{ formatPrice(order.totalPrice) }}</span>
                </div>
              </div>
            </div>

            <div v-if="order.remark" class="detail-section">
              <div class="section-header">
                <el-icon><ChatLineSquare /></el-icon>
                <span>订单备注</span>
              </div>
              <div class="remark-content">{{ order.remark }}</div>
            </div>

            <div class="detail-footer">
              <el-button
                v-if="order.status === 'pending'"
                type="primary"
                size="large"
                @click="handlePay(order.id)"
              >
                去支付
              </el-button>
              <el-button
                v-if="order.status === 'paid' || order.status === 'shipped'"
                type="primary"
                size="large"
                @click="handleConfirm(order.id)"
              >
                确认收货
              </el-button>
              <el-button
                v-if="order.status === 'completed'"
                type="default"
                size="large"
                @click="goToOrders"
              >
                返回订单列表
              </el-button>
            </div>
          </div>
        </template>
      </el-skeleton>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ArrowLeft, Ticket, MapLocation, ShoppingBag, Picture, Document, ChatLineSquare } from '@element-plus/icons-vue'
import { getOrderDetail, updateOrderStatus } from '../api/order'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const loading = ref(true)
const order = ref(null)

function goBack() {
  router.back()
}

function goToOrders() {
  router.push('/orders')
}

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

async function loadOrder() {
  const orderId = route.params.id
  if (!orderId) {
    loading.value = false
    return
  }

  loading.value = true
  try {
    const res = await getOrderDetail(orderId)
    if (res.code === 200) {
      order.value = res.data
    }
  } catch (error) {
    console.error('Failed to load order:', error)
  } finally {
    loading.value = false
  }
}

async function handlePay(orderId) {
  try {
    const res = await updateOrderStatus({ id: orderId, status: 'paid' })
    if (res.code === 200) {
      ElMessage.success('支付成功')
      await loadOrder()
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
      await loadOrder()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    console.error('Confirm failed:', error)
    ElMessage.error('操作失败，请稍后重试')
  }
}

onMounted(() => {
  loadOrder()
})
</script>

<style scoped>
.page-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.page-title {
  font-size: 20px;
  color: var(--text-color);
}

.detail-section {
  background: #fff;
  border-radius: var(--radius);
  padding: 16px;
  margin-bottom: 16px;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--border-color);
}

.section-header span {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-color);
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-label {
  font-size: 13px;
  color: var(--text-secondary);
}

.info-value {
  font-size: 14px;
  color: var(--text-color);
}

.info-value.status {
  font-weight: 600;
}

.info-value.status.pending {
  color: #f56c6c;
}

.info-value.status.paid {
  color: #e6a23c;
}

.info-value.status.shipped {
  color: #409eff;
}

.info-value.status.completed {
  color: #67c23a;
}

.info-value.price {
  font-size: 18px;
  font-weight: 700;
  color: #f56c6c;
}

.address-info {
  padding: 12px;
  background: #fafafa;
  border-radius: var(--radius);
}

.address-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.address-name {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-color);
}

.address-phone {
  font-size: 14px;
  color: var(--text-secondary);
}

.address-detail {
  font-size: 14px;
  color: var(--text-color);
  line-height: 1.5;
}

.no-address {
  padding: 12px;
  color: var(--text-secondary);
  font-size: 14px;
}

.product-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.product-item {
  display: flex;
  gap: 12px;
}

.product-image {
  width: 80px;
  height: 80px;
  background: #f8f8f8;
  border-radius: var(--radius);
  overflow: hidden;
  flex-shrink: 0;
}

.product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.product-name {
  font-size: 14px;
  color: var(--text-color);
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-spec {
  font-size: 12px;
  color: var(--text-secondary);
  margin-top: 4px;
}

.product-bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: auto;
}

.product-price {
  font-size: 16px;
  font-weight: 700;
  color: #f56c6c;
}

.product-quantity {
  font-size: 14px;
  color: var(--text-secondary);
}

.fee-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.fee-row {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
}

.fee-label {
  color: var(--text-secondary);
}

.fee-value {
  color: var(--text-color);
}

.fee-value.discount {
  color: #67c23a;
}

.fee-row.total {
  padding-top: 12px;
  border-top: 1px dashed var(--border-color);
}

.fee-row.total .fee-label {
  font-weight: 600;
  color: var(--text-color);
}

.fee-row.total .fee-value {
  font-size: 20px;
  font-weight: 700;
  color: #f56c6c;
}

.remark-content {
  padding: 12px;
  background: #fafafa;
  border-radius: var(--radius);
  font-size: 14px;
  color: var(--text-color);
  line-height: 1.6;
}

.detail-footer {
  display: flex;
  justify-content: center;
  padding: 16px;
  background: #fff;
  border-radius: var(--radius);
}

.empty-order {
  text-align: center;
  padding: 60px 0;
}

.image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #c0c4cc;
}

.skeleton-section {
  background: #fff;
  border-radius: var(--radius);
  padding: 16px;
}
</style>