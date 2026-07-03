<template>
  <!-- 确认订单页面组件 -->
  <div class="checkout page-container">
    <div class="main-width">
      <!-- 页面标题 -->
      <h2 class="page-title">确认订单</h2>

      <!-- 骨架屏加载组件 -->
      <el-skeleton :loading="loading" animated>
        <!-- 骨架屏模板 -->
        <template #template>
          <div class="skeleton-section">
            <el-skeleton-item variant="h3" style="width:20%" />
            <div style="margin-top:12px">
              <el-skeleton-item variant="p" style="width:80%" />
              <el-skeleton-item variant="p" style="width:60%" />
              <el-skeleton-item variant="p" style="width:50%" />
            </div>
          </div>
          <div class="skeleton-section" style="margin-top:16px">
            <el-skeleton-item variant="h3" style="width:20%" />
            <div v-for="i in 2" :key="i" style="margin-top:12px;display:flex;gap:12px">
              <el-skeleton-item variant="image" style="width:80px;height:80px" />
              <div style="flex:1">
                <el-skeleton-item variant="p" style="width:70%" />
                <el-skeleton-item variant="p" style="width:40%" />
                <el-skeleton-item variant="p" style="width:30%" />
              </div>
            </div>
          </div>
          <div class="skeleton-section" style="margin-top:16px">
            <el-skeleton-item variant="h3" style="width:20%" />
            <div style="margin-top:12px">
              <el-skeleton-item variant="p" style="width:40%" />
              <el-skeleton-item variant="p" style="width:40%" />
              <el-skeleton-item variant="p" style="width:40%" />
            </div>
            <el-skeleton-item variant="button" style="width:200px;margin-top:16px" />
          </div>
        </template>
        <!-- 实际内容 -->
        <template #default>
          <!-- 购物车为空状态 -->
          <div v-if="cartItems.length === 0" class="empty-cart">
            <el-empty description="购物车为空，请先添加商品" />
            <el-button type="primary" @click="goToProducts">去购物</el-button>
          </div>

          <!-- 订单确认内容 -->
          <div v-else>
            <!-- 收货地址区域 -->
            <div class="checkout-section address-section">
              <div class="section-header">
                <el-icon><MapLocation /></el-icon>
                <span>收货地址</span>
                <el-button type="text" @click="goToAddress">管理地址</el-button>
              </div>
              <!-- 无地址状态 -->
              <div v-if="addressList.length === 0" class="no-address">
                <el-empty description="请添加收货地址" />
                <el-button type="primary" @click="goToAddress">去添加</el-button>
              </div>
              <!-- 地址列表 -->
              <div v-else class="address-list">
                <div
                  v-for="addr in addressList"
                  :key="addr.id"
                  :class="['address-item', { active: selectedAddressId === addr.id }]"
                  @click="selectAddress(addr.id)"
                >
                  <div class="address-header">
                    <span class="address-name">{{ addr.name }}</span>
                    <span class="address-phone">{{ addr.phone }}</span>
                    <el-tag v-if="addr.isDefault" type="success" size="small">默认</el-tag>
                  </div>
                  <div class="address-detail">{{ addr.province }}{{ addr.city }}{{ addr.district }}{{ addr.detail }}</div>
                  <div v-if="selectedAddressId === addr.id" class="address-check">
                    <el-icon><Check /></el-icon>
                  </div>
                </div>
              </div>
            </div>

            <!-- 商品清单区域 -->
            <div class="checkout-section product-section">
              <div class="section-header">
                <el-icon><ShoppingBag /></el-icon>
                <span>商品清单</span>
                <span class="product-count">共 {{ totalCount }} 件商品</span>
              </div>
              <div class="product-list">
                <div v-for="item in cartItems" :key="item.id" class="product-item">
                  <div class="product-image">
                    <el-image :src="item.productImage || item.image" fit="cover" style="width:100%;height:100%">
                      <template #error>
                        <div class="image-placeholder">
                          <el-icon :size="20"><Picture /></el-icon>
                        </div>
                      </template>
                    </el-image>
                  </div>
                  <div class="product-info">
                    <div class="product-name">{{ item.productName || item.name }}</div>
                    <div class="product-spec">规格：默认</div>
                    <div class="product-bottom">
                      <span class="product-price">&yen;{{ formatPrice(item.price) }}</span>
                      <span class="product-quantity">x{{ item.quantity }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- 订单信息区域 -->
            <div class="checkout-section order-section">
              <div class="section-header">
                <el-icon><Ticket /></el-icon>
                <span>订单信息</span>
              </div>
              <div class="order-info">
                <div class="order-row">
                  <span class="order-label">商品金额</span>
                  <span class="order-value">&yen;{{ formatPrice(productTotal) }}</span>
                </div>
                <div class="order-row">
                  <span class="order-label">运费</span>
                  <span class="order-value">免运费</span>
                </div>
                <div class="order-row">
                  <span class="order-label">优惠</span>
                  <span class="order-value discount">-¥0.00</span>
                </div>
                <div class="order-row total">
                  <span class="order-label">实付金额</span>
                  <span class="order-value total-price">&yen;{{ formatPrice(totalPrice) }}</span>
                </div>
              </div>
              <!-- 订单备注 -->
              <div class="remark-section">
                <el-input
                  v-model="remark"
                  placeholder="请输入订单备注（选填）"
                  type="textarea"
                  :rows="2"
                  resize="none"
                />
              </div>
            </div>

            <!-- 结算底部栏 -->
            <div class="checkout-footer">
              <div class="footer-left">
                <span class="footer-label">共 {{ totalCount }} 件，合计：</span>
                <span class="footer-total">&yen;{{ formatPrice(totalPrice) }}</span>
              </div>
              <el-button type="primary" size="large" :disabled="!selectedAddressId || submitting" @click="submitOrder">
                <template #loading>提交中...</template>
                提交订单
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
 * Checkout确认订单页面组件
 * 展示订单确认信息，包括收货地址、商品清单、订单金额，支持提交订单功能
 */
import { ref, computed, onMounted } from 'vue'               // 引入Vue响应式API和生命周期钩子
import { useRouter } from 'vue-router'                       // 引入路由实例
import { MapLocation, ShoppingBag, Ticket, Check, Picture } from '@element-plus/icons-vue' // 引入Element Plus图标
import { useCartStore } from '../store/cart'                 // 引入购物车状态管理
import { getAddressList } from '../api/address'              // 引入地址API
import { createOrder } from '../api/order'                   // 引入订单API
import { ElMessage } from 'element-plus'                     // 引入Element Plus消息提示

// 获取路由实例
const router = useRouter()
// 获取购物车store实例
const cartStore = useCartStore()

/**
 * 加载状态
 */
const loading = ref(true)
/**
 * 提交状态
 */
const submitting = ref(false)
/**
 * 收货地址列表
 */
const addressList = ref([])
/**
 * 选中的地址ID
 */
const selectedAddressId = ref(null)
/**
 * 订单备注
 */
const remark = ref('')

/**
 * 选中的购物车商品（计算属性）
 * 筛选出购物车中已选中的商品
 */
const cartItems = computed(() => {
  return cartStore.cartList.filter(item => item.selected !== false)
})

/**
 * 商品总数（计算属性）
 * 统计所有选中商品的数量之和
 */
const totalCount = computed(() => {
  return cartItems.value.reduce((count, item) => count + (item.quantity || 1), 0)
})

/**
 * 商品总金额（计算属性）
 * 计算所有选中商品的价格总和
 */
const productTotal = computed(() => {
  return cartItems.value.reduce((total, item) => total + (item.price || 0) * (item.quantity || 1), 0)
})

/**
 * 实付金额（计算属性）
 * 当前等于商品总金额，后续可扩展运费、优惠等逻辑
 */
const totalPrice = computed(() => {
  return productTotal.value
})

/**
 * 格式化价格
 * @param {number} price - 价格
 * @returns {string} 格式化后的价格字符串
 */
function formatPrice(price) {
  return Number(price).toFixed(2)
}

/**
 * 跳转到商品列表页
 */
function goToProducts() {
  router.push('/products')
}

/**
 * 跳转到地址管理页
 */
function goToAddress() {
  router.push('/address')
}

/**
 * 选择收货地址
 * @param {number} id - 地址ID
 */
function selectAddress(id) {
  selectedAddressId.value = id
}

/**
 * 加载收货地址列表
 * 从后端获取地址数据，默认选中默认地址或第一个地址
 */
async function loadAddresses() {
  try {
    const res = await getAddressList()
    if (res.code === 200) {
      addressList.value = res.data || []
      // 优先选择默认地址
      const defaultAddr = addressList.value.find(addr => addr.isDefault)
      if (defaultAddr) {
        selectedAddressId.value = defaultAddr.id
      } else if (addressList.value.length > 0) {
        // 没有默认地址则选择第一个
        selectedAddressId.value = addressList.value[0].id
      }
    }
  } catch (error) {
    console.error('Failed to load addresses:', error)
  }
}

/**
 * 提交订单
 * 验证收货地址和购物车状态后，调用后端API创建订单
 */
async function submitOrder() {
  // 验证收货地址
  if (!selectedAddressId.value) {
    ElMessage.warning('请选择收货地址')
    return
  }

  // 验证购物车状态
  if (cartItems.value.length === 0) {
    ElMessage.warning('购物车为空')
    return
  }

  submitting.value = true

  try {
    // 重新同步购物车数据到后端
    for (const item of cartItems.value) {
      await cartStore.addToCart({ productId: item.productId, quantity: item.quantity })
    }

    // 构建订单数据
    const orderData = {
      addressId: selectedAddressId.value,
      remark: remark.value
    }

    // 调用创建订单API
    const res = await createOrder(orderData)

    if (res.code === 200) {
      ElMessage.success('订单提交成功')
      // 清空购物车
      await cartStore.clearCart()
      // 跳转到订单详情页
      router.push(`/order/${res.data.id}`)
    } else {
      ElMessage.error(res.message || '订单提交失败')
    }
  } catch (error) {
    console.error('Order creation failed:', error)
    ElMessage.error('订单提交失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}

/**
 * 页面挂载时初始化数据
 * 获取购物车列表和收货地址列表
 */
onMounted(async () => {
  await cartStore.fetchCartList()
  await loadAddresses()
  loading.value = false
})
</script>

<style scoped>
/**
 * 订单确认区域样式
 */
.checkout-section {
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
  margin-bottom: 16px;                                      /* 下外边距16px */
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
 * 商品数量样式
 */
.section-header .product-count {
  margin-left: auto;                                        /* 靠右对齐 */
  font-size: 14px;                                          /* 字号14px */
  font-weight: normal;                                      /* 正常字重 */
  color: var(--text-secondary);                             /* 次要文字颜色 */
}

/**
 * 无地址状态样式
 */
.no-address {
  text-align: center;                                       /* 居中对齐 */
  padding: 24px 0;                                          /* 上下内边距24px */
}

/**
 * 无地址状态按钮样式
 */
.no-address .el-button {
  margin-top: 16px;                                         /* 上外边距16px */
}

/**
 * 地址列表样式
 */
.address-list {
  display: flex;                                            /* flex布局 */
  flex-direction: column;                                   /* 垂直排列 */
  gap: 12px;                                                /* 间距12px */
}

/**
 * 地址项样式
 */
.address-item {
  display: flex;                                            /* flex布局 */
  flex-direction: column;                                   /* 垂直排列 */
  padding: 12px;                                           /* 内边距12px */
  border: 2px solid transparent;                            /* 透明边框 */
  border-radius: var(--radius);                             /* 圆角 */
  background: #fafafa;                                     /* 浅灰色背景 */
  cursor: pointer;                                          /* 鼠标指针变为手型 */
  transition: all 0.2s;                                     /* 过渡动画 */
  position: relative;                                       /* 相对定位 */
}

/**
 * 地址项悬停样式
 */
.address-item:hover {
  border-color: var(--primary-light);                       /* 浅色边框 */
}

/**
 * 地址项选中样式
 */
.address-item.active {
  border-color: var(--primary);                             /* 主题色边框 */
  background: #f0f5ff;                                     /* 浅蓝色背景 */
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
  font-size: 14px;                                          /* 字号14px */
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
 * 选中标记样式
 */
.address-check {
  position: absolute;                                       /* 绝对定位 */
  right: 12px;                                              /* 右距12px */
  bottom: 12px;                                             /* 下距12px */
  color: var(--primary);                                    /* 主题色 */
  font-size: 18px;                                          /* 字号18px */
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
 * 订单信息样式
 */
.order-info {
  display: flex;                                            /* flex布局 */
  flex-direction: column;                                   /* 垂直排列 */
  gap: 12px;                                                /* 间距12px */
}

/**
 * 订单行样式
 */
.order-row {
  display: flex;                                            /* flex布局 */
  align-items: center;                                      /* 垂直居中 */
  justify-content: space-between;                            /* 两端对齐 */
  font-size: 14px;                                          /* 字号14px */
}

/**
 * 订单标签样式
 */
.order-label {
  color: var(--text-secondary);                             /* 次要文字颜色 */
}

/**
 * 订单值样式
 */
.order-value {
  color: var(--text-color);                                 /* 文字颜色 */
}

/**
 * 优惠金额样式
 */
.order-value.discount {
  color: #67c23a;                                          /* 绿色 */
}

/**
 * 合计行样式
 */
.order-row.total {
  padding-top: 12px;                                        /* 上内边距12px */
  border-top: 1px dashed var(--border-color);               /* 顶部虚线边框 */
  margin-top: 8px;                                          /* 上外边距8px */
}

/**
 * 合计标签样式
 */
.order-row.total .order-label {
  font-size: 16px;                                          /* 字号16px */
  font-weight: 600;                                         /* 字重600 */
  color: var(--text-color);                                 /* 文字颜色 */
}

/**
 * 合计金额样式
 */
.order-row.total .order-value {
  font-size: 24px;                                          /* 字号24px */
  font-weight: 700;                                         /* 字重700 */
  color: #f56c6c;                                          /* 红色 */
}

/**
 * 备注区域样式
 */
.remark-section {
  margin-top: 16px;                                         /* 上外边距16px */
}

/**
 * 结算底部栏样式
 */
.checkout-footer {
  display: flex;                                            /* flex布局 */
  align-items: center;                                      /* 垂直居中 */
  justify-content: space-between;                            /* 两端对齐 */
  background: #fff;                                         /* 白色背景 */
  padding: 16px;                                           /* 内边距16px */
  border-radius: var(--radius);                             /* 圆角 */
  position: sticky;                                         /* 粘性定位 */
  bottom: 0;                                                /* 固定在底部 */
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);             /* 阴影效果 */
}

/**
 * 底部左侧样式
 */
.footer-left {
  display: flex;                                            /* flex布局 */
  align-items: baseline;                                    /* 基线对齐 */
  gap: 4px;                                                /* 间距4px */
}

/**
 * 底部标签样式
 */
.footer-label {
  font-size: 14px;                                          /* 字号14px */
  color: var(--text-secondary);                             /* 次要文字颜色 */
}

/**
 * 底部合计金额样式
 */
.footer-total {
  font-size: 24px;                                          /* 字号24px */
  font-weight: 700;                                         /* 字重700 */
  color: #f56c6c;                                          /* 红色 */
}

/**
 * 购物车为空状态样式
 */
.empty-cart {
  text-align: center;                                       /* 居中对齐 */
  padding: 60px 0;                                          /* 上下内边距60px */
}

/**
 * 购物车为空状态按钮样式
 */
.empty-cart .el-button {
  margin-top: 16px;                                         /* 上外边距16px */
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