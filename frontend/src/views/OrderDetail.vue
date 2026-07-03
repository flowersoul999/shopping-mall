<template>
  <!-- 订单详情页面组件 -->
  <div class="order-detail page-container">
    <div class="main-width">
      <!-- 页面头部（返回按钮和标题） -->
      <div class="page-header">
        <el-button @click="goBack">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
        <h2 class="page-title">订单详情</h2>
      </div>

      <!-- 骨架屏加载组件 -->
      <el-skeleton :loading="loading" animated>
        <!-- 骨架屏模板 -->
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
        <!-- 实际内容 -->
        <template #default>
          <!-- 订单不存在状态 -->
          <div v-if="!loading && !order" class="empty-order">
            <el-empty description="订单不存在" />
          </div>

          <!-- 订单详情内容 -->
          <div v-else-if="order">
            <!-- 订单信息区域 -->
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

            <!-- 收货地址区域 -->
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

            <!-- 商品清单区域 -->
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

            <!-- 费用明细区域 -->
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

            <!-- 订单备注区域（有备注时显示） -->
            <div v-if="order.remark" class="detail-section">
              <div class="section-header">
                <el-icon><ChatLineSquare /></el-icon>
                <span>订单备注</span>
              </div>
              <div class="remark-content">{{ order.remark }}</div>
            </div>

            <!-- 底部操作按钮 -->
            <div class="detail-footer">
              <!-- 待付款：去支付 -->
              <el-button
                v-if="order.status === 'pending'"
                type="primary"
                size="large"
                @click="handlePay(order.id)"
              >
                去支付
              </el-button>
              <!-- 待发货/待收货：确认收货 -->
              <el-button
                v-if="order.status === 'paid' || order.status === 'shipped'"
                type="primary"
                size="large"
                @click="handleConfirm(order.id)"
              >
                确认收货
              </el-button>
              <!-- 已完成：返回订单列表 -->
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
/**
 * OrderDetail订单详情页面组件
 * 展示订单详细信息，包括订单信息、收货地址、商品清单、费用明细和订单备注，支持支付和确认收货操作
 */
import { ref, onMounted } from 'vue'                         // 引入Vue响应式API和生命周期钩子
import { useRouter, useRoute } from 'vue-router'             // 引入路由相关API
import { ArrowLeft, Ticket, MapLocation, ShoppingBag, Picture, Document, ChatLineSquare } from '@element-plus/icons-vue' // 引入Element Plus图标
import { getOrderDetail, updateOrderStatus } from '../api/order' // 引入订单API
import { ElMessage } from 'element-plus'                     // 引入Element Plus消息提示

// 获取路由实例
const router = useRouter()
const route = useRoute()
/**
 * 加载状态
 */
const loading = ref(true)
/**
 * 订单详情数据
 */
const order = ref(null)

/**
 * 返回上一页
 */
function goBack() {
  router.back()
}

/**
 * 跳转到订单列表页
 */
function goToOrders() {
  router.push('/orders')
}

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
 * 加载订单详情
 * 从后端获取指定ID的订单详细信息
 */
async function loadOrder() {
  // 从路由参数获取订单ID
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
      // 刷新订单详情
      await loadOrder()
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
      // 刷新订单详情
      await loadOrder()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    console.error('Confirm failed:', error)
    ElMessage.error('操作失败，请稍后重试')
  }
}

/**
 * 页面挂载时加载订单详情
 */
onMounted(() => {
  loadOrder()
})
</script>

<style scoped>
/**
 * 页面头部样式
 */
.page-header {
  display: flex;                                            /* flex布局 */
  align-items: center;                                      /* 垂直居中 */
  gap: 12px;                                                /* 间距12px */
  margin-bottom: 20px;                                      /* 下外边距20px */
}

/**
 * 页面标题样式
 */
.page-title {
  font-size: 20px;                                          /* 字号20px */
  color: var(--text-color);                                 /* 文字颜色 */
}

/**
 * 详情区域样式
 */
.detail-section {
  background: #fff;                                         /* 白色背景 */
  border-radius: var(--radius);                             /* 圆角 */
  padding: 16px;                                           /* 内边距16px */
  margin-bottom: 16px;                                      /* 下外边距16px */
}

/**
 * 区域标题样式
 */
.section-header {
  display: flex;                                            /* flex布局 */
  align-items: center;                                      /* 垂直居中 */
  gap: 8px;                                                /* 间距8px */
  margin-bottom: 12px;                                      /* 下外边距12px */
  padding-bottom: 12px;                                     /* 下内边距12px */
  border-bottom: 1px solid var(--border-color);             /* 底部边框 */
}

/**
 * 区域标题文字样式
 */
.section-header span {
  font-size: 16px;                                          /* 字号16px */
  font-weight: 600;                                         /* 字重600 */
  color: var(--text-color);                                 /* 文字颜色 */
}

/**
 * 信息网格布局样式
 */
.info-grid {
  display: grid;                                            /* grid布局 */
  grid-template-columns: repeat(2, 1fr);                    /* 2列等宽 */
  gap: 12px;                                                /* 间距12px */
}

/**
 * 信息项样式
 */
.info-item {
  display: flex;                                            /* flex布局 */
  flex-direction: column;                                   /* 垂直排列 */
  gap: 4px;                                                /* 间距4px */
}

/**
 * 信息标签样式
 */
.info-label {
  font-size: 13px;                                          /* 字号13px */
  color: var(--text-secondary);                             /* 次要文字颜色 */
}

/**
 * 信息值样式
 */
.info-value {
  font-size: 14px;                                          /* 字号14px */
  color: var(--text-color);                                 /* 文字颜色 */
}

/**
 * 状态值样式
 */
.info-value.status {
  font-weight: 600;                                         /* 字重600 */
}

/**
 * 待付款状态颜色
 */
.info-value.status.pending {
  color: #f56c6c;                                          /* 红色 */
}

/**
 * 待发货状态颜色
 */
.info-value.status.paid {
  color: #e6a23c;                                          /* 橙色 */
}

/**
 * 待收货状态颜色
 */
.info-value.status.shipped {
  color: #409eff;                                          /* 蓝色 */
}

/**
 * 已完成状态颜色
 */
.info-value.status.completed {
  color: #67c23a;                                          /* 绿色 */
}

/**
 * 价格值样式
 */
.info-value.price {
  font-size: 18px;                                          /* 字号18px */
  font-weight: 700;                                         /* 字重700 */
  color: #f56c6c;                                          /* 红色 */
}

/**
 * 地址信息样式
 */
.address-info {
  padding: 12px;                                           /* 内边距12px */
  background: #fafafa;                                     /* 浅灰色背景 */
  border-radius: var(--radius);                             /* 圆角 */
}

/**
 * 地址头部样式
 */
.address-header {
  display: flex;                                            /* flex布局 */
  align-items: center;                                      /* 垂直居中 */
  gap: 12px;                                                /* 间距12px */
  margin-bottom: 8px;                                       /* 下外边距8px */
}

/**
 * 收件人姓名样式
 */
.address-name {
  font-size: 16px;                                          /* 字号16px */
  font-weight: 600;                                         /* 字重600 */
  color: var(--text-color);                                 /* 文字颜色 */
}

/**
 * 收件人电话样式
 */
.address-phone {
  font-size: 14px;                                          /* 字号14px */
  color: var(--text-secondary);                             /* 次要文字颜色 */
}

/**
 * 详细地址样式
 */
.address-detail {
  font-size: 14px;                                          /* 字号14px */
  color: var(--text-color);                                 /* 文字颜色 */
  line-height: 1.5;                                         /* 行高1.5 */
}

/**
 * 无地址状态样式
 */
.no-address {
  padding: 12px;                                           /* 内边距12px */
  color: var(--text-secondary);                             /* 次要文字颜色 */
  font-size: 14px;                                          /* 字号14px */
}

/**
 * 商品列表样式
 */
.product-list {
  display: flex;                                            /* flex布局 */
  flex-direction: column;                                   /* 垂直排列 */
  gap: 16px;                                                /* 间距16px */
}

/**
 * 商品项样式
 */
.product-item {
  display: flex;                                            /* flex布局 */
  gap: 12px;                                                /* 间距12px */
}

/**
 * 商品图片样式
 */
.product-image {
  width: 80px;                                            /* 固定宽度80px */
  height: 80px;                                           /* 固定高度80px */
  background: #f8f8f8;                                     /* 浅灰色背景 */
  border-radius: var(--radius);                             /* 圆角 */
  overflow: hidden;                                         /* 溢出隐藏 */
  flex-shrink: 0;                                          /* 不缩小 */
}

/**
 * 商品信息样式
 */
.product-info {
  flex: 1;                                                  /* 占满剩余空间 */
  display: flex;                                            /* flex布局 */
  flex-direction: column;                                   /* 垂直排列 */
}

/**
 * 商品名称样式（支持两行省略）
 */
.product-name {
  font-size: 14px;                                          /* 字号14px */
  color: var(--text-color);                                 /* 文字颜色 */
  line-height: 1.4;                                         /* 行高1.4 */
  display: -webkit-box;                                     /* 弹性盒子 */
  -webkit-line-clamp: 2;                                    /* 最多两行 */
  -webkit-box-orient: vertical;                             /* 垂直方向 */
  overflow: hidden;                                         /* 溢出隐藏 */
}

/**
 * 商品规格样式
 */
.product-spec {
  font-size: 12px;                                          /* 字号12px */
  color: var(--text-secondary);                             /* 次要文字颜色 */
  margin-top: 4px;                                          /* 上外边距4px */
}

/**
 * 商品底部（价格和数量）样式
 */
.product-bottom {
  display: flex;                                            /* flex布局 */
  align-items: center;                                      /* 垂直居中 */
  justify-content: space-between;                            /* 两端对齐 */
  margin-top: auto;                                         /* 自动上推 */
}

/**
 * 商品价格样式
 */
.product-price {
  font-size: 16px;                                          /* 字号16px */
  font-weight: 700;                                         /* 字重700 */
  color: #f56c6c;                                          /* 红色 */
}

/**
 * 商品数量样式
 */
.product-quantity {
  font-size: 14px;                                          /* 字号14px */
  color: var(--text-secondary);                             /* 次要文字颜色 */
}

/**
 * 费用列表样式
 */
.fee-list {
  display: flex;                                            /* flex布局 */
  flex-direction: column;                                   /* 垂直排列 */
  gap: 12px;                                                /* 间距12px */
}

/**
 * 费用行样式
 */
.fee-row {
  display: flex;                                            /* flex布局 */
  justify-content: space-between;                            /* 两端对齐 */
  font-size: 14px;                                          /* 字号14px */
}

/**
 * 费用标签样式
 */
.fee-label {
  color: var(--text-secondary);                             /* 次要文字颜色 */
}

/**
 * 费用值样式
 */
.fee-value {
  color: var(--text-color);                                 /* 文字颜色 */
}

/**
 * 优惠金额样式
 */
.fee-value.discount {
  color: #67c23a;                                          /* 绿色 */
}

/**
 * 合计行样式
 */
.fee-row.total {
  padding-top: 12px;                                        /* 上内边距12px */
  border-top: 1px dashed var(--border-color);               /* 顶部虚线边框 */
}

/**
 * 合计标签样式
 */
.fee-row.total .fee-label {
  font-weight: 600;                                         /* 字重600 */
  color: var(--text-color);                                 /* 文字颜色 */
}

/**
 * 合计金额样式
 */
.fee-row.total .fee-value {
  font-size: 20px;                                          /* 字号20px */
  font-weight: 700;                                         /* 字重700 */
  color: #f56c6c;                                          /* 红色 */
}

/**
 * 备注内容样式
 */
.remark-content {
  padding: 12px;                                           /* 内边距12px */
  background: #fafafa;                                     /* 浅灰色背景 */
  border-radius: var(--radius);                             /* 圆角 */
  font-size: 14px;                                          /* 字号14px */
  color: var(--text-color);                                 /* 文字颜色 */
  line-height: 1.6;                                         /* 行高1.6 */
}

/**
 * 详情底部样式
 */
.detail-footer {
  display: flex;                                            /* flex布局 */
  justify-content: center;                                  /* 水平居中 */
  padding: 16px;                                           /* 内边距16px */
  background: #fff;                                         /* 白色背景 */
  border-radius: var(--radius);                             /* 圆角 */
}

/**
 * 订单不存在状态样式
 */
.empty-order {
  text-align: center;                                       /* 居中对齐 */
  padding: 60px 0;                                          /* 上下内边距60px */
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
 * 骨架屏区域样式
 */
.skeleton-section {
  background: #fff;                                         /* 白色背景 */
  border-radius: var(--radius);                             /* 圆角 */
  padding: 16px;                                           /* 内边距16px */
}
</style>