<template>
  <!-- 购物车页面组件 -->
  <div class="cart page-container">
    <div class="main-width">
      <!-- 页面标题 -->
      <h2 class="page-title">购物车</h2>

      <!-- 骨架屏加载组件 -->
      <el-skeleton :loading="loading" animated>
        <!-- 骨架屏模板 -->
        <template #template>
          <div v-for="i in 3" :key="i" class="skeleton-cart-item">
            <div class="skeleton-img"><el-skeleton-item variant="image" style="width:100px;height:100px" /></div>
            <div class="skeleton-info">
              <el-skeleton-item variant="p" style="width:60%" />
              <el-skeleton-item variant="p" style="width:40%" />
              <div style="display:flex;align-items:center;gap:12px;margin-top:12px">
                <el-skeleton-item variant="button" style="width:80px" />
                <el-skeleton-item variant="button" style="width:60px" />
              </div>
            </div>
          </div>
        </template>
        <!-- 实际内容 -->
        <template #default>
          <!-- 空状态 -->
          <el-empty v-if="cartStore.cartList.length === 0 && !loading" description="购物车是空的" />
          <!-- 购物车内容 -->
          <div v-else>
            <!-- 购物车列表 -->
            <div class="cart-list">
              <div v-for="item in cartStore.cartList" :key="item.id" class="cart-item">
                <!-- 购物车项左侧（复选框和图片） -->
                <div class="cart-item-left">
                  <!-- 选择复选框 -->
                  <div class="cart-checkbox">
                    <el-checkbox v-model="item.selected" @change="handleSelect" />
                  </div>
                  <!-- 商品图片 -->
                  <div class="cart-image" @click="goToProduct(item.productId)">
                    <el-image :src="item.productImage || item.image" fit="cover" style="width:100%;height:100%">
                      <template #error>
                        <div class="image-placeholder">
                          <el-icon :size="24"><Picture /></el-icon>
                        </div>
                      </template>
                    </el-image>
                  </div>
                </div>
                <!-- 购物车项右侧（信息和操作） -->
                <div class="cart-item-right">
                  <!-- 商品名称 -->
                  <div class="cart-item-name" @click="goToProduct(item.productId)">{{ item.productName || item.name }}</div>
                  <!-- 商品价格 -->
                  <div class="cart-item-price">&yen;{{ formatPrice(item.price) }}</div>
                  <!-- 操作区域（数量调整和删除） -->
                  <div class="cart-item-actions">
                    <el-input-number
                      v-model="item.quantity"
                      :min="1"
                      :max="item.stock"
                      :step="1"
                      size="small"
                      @change="handleQuantityChange(item.id, $event)"
                    />
                    <el-button type="text" @click="handleRemove(item.id)">删除</el-button>
                  </div>
                </div>
              </div>
            </div>

            <!-- 购物车底部（结算栏） -->
            <div class="cart-footer">
              <!-- 全选区域 -->
              <div class="select-all">
                <el-checkbox :checked="isAllSelected" @change="handleSelectAll">全选</el-checkbox>
                <span class="selected-count">已选 {{ selectedCount }} 件</span>
              </div>
              <!-- 合计区域 -->
              <div class="total-section">
                <span class="total-label">合计：</span>
                <span class="total-price">&yen;{{ formatPrice(cartStore.totalPrice) }}</span>
                <!-- 结算按钮（无选中项时禁用） -->
                <el-button type="primary" size="large" :disabled="cartStore.selectedItems.length === 0" @click="handleCheckout">
                  结算
                </el-button>
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
 * Cart购物车页面组件
 * 展示用户购物车列表，支持商品选择、数量调整、删除和结算功能
 */
import { ref, computed, onMounted } from 'vue'           // 引入Vue响应式API和生命周期钩子
import { useRouter } from 'vue-router'                     // 引入路由实例
import { Picture } from '@element-plus/icons-vue'          // 引入图片图标
import { useCartStore } from '../store/cart'               // 引入购物车状态管理

// 获取路由实例
const router = useRouter()
// 获取购物车store实例
const cartStore = useCartStore()
// 加载状态
const loading = ref(true)

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
 * 处理单个商品选择状态变化
 * 由于选中状态已通过v-model双向绑定，此方法留作扩展使用
 */
function handleSelect() {
}

/**
 * 是否全选（计算属性）
 * 判断购物车中所有商品是否都被选中
 */
const isAllSelected = computed(() => {
  return cartStore.cartList.length > 0 && cartStore.cartList.every(item => item.selected !== false)
})

/**
 * 选中商品总数（计算属性）
 * 统计所有选中商品的数量之和
 */
const selectedCount = computed(() => {
  return cartStore.cartList.filter(item => item.selected !== false).reduce((count, item) => count + (item.quantity || 1), 0)
})

/**
 * 处理全选/取消全选
 * @param {boolean} val - 是否选中
 */
function handleSelectAll(val) {
  cartStore.cartList.forEach(item => {
    item.selected = val
  })
}

/**
 * 处理数量变化
 * @param {number} id - 购物车项ID
 * @param {number} quantity - 新数量
 */
function handleQuantityChange(id, quantity) {
  cartStore.updateQuantity(id, quantity)
}

/**
 * 处理删除商品
 * @param {number} id - 购物车项ID
 */
function handleRemove(id) {
  cartStore.removeItem(id)
}

/**
 * 处理结算
 * 跳转到结算页面
 */
function handleCheckout() {
  router.push('/checkout')
}

/**
 * 页面挂载时初始化购物车数据
 * 获取购物车列表后默认选中所有商品
 */
onMounted(() => {
  cartStore.fetchCartList().finally(() => {
    loading.value = false
    // 默认选中所有商品
    cartStore.cartList.forEach(item => {
      item.selected = true
    })
  })
})
</script>

<style scoped>
/**
 * 购物车列表样式
 */
.cart-list {
  background: #fff;                                         /* 白色背景 */
  border-radius: var(--radius);                             /* 圆角 */
  overflow: hidden;                                         /* 溢出隐藏 */
  margin-bottom: 20px;                                      /* 下外边距20px */
}

/**
 * 购物车项样式
 */
.cart-item {
  display: flex;                                            /* flex布局 */
  padding: 16px;                                           /* 内边距16px */
  border-bottom: 1px solid var(--border-color);             /* 底部边框 */
}

/**
 * 最后一个购物车项去除底部边框
 */
.cart-item:last-child {
  border-bottom: none;                                      /* 无边框 */
}

/**
 * 购物车项左侧区域样式
 */
.cart-item-left {
  display: flex;                                            /* flex布局 */
  gap: 12px;                                                /* 间距12px */
  flex-shrink: 0;                                          /* 不缩小 */
}

/**
 * 复选框样式
 */
.cart-checkbox {
  margin-top: 35px;                                         /* 上外边距35px，垂直居中 */
}

/**
 * 商品图片样式
 */
.cart-image {
  width: 100px;                                            /* 固定宽度100px */
  height: 100px;                                           /* 固定高度100px */
  background: #f8f8f8;                                     /* 浅灰色背景 */
  border-radius: var(--radius);                             /* 圆角 */
  overflow: hidden;                                         /* 溢出隐藏 */
  cursor: pointer;                                          /* 鼠标指针变为手型 */
}

/**
 * 购物车项右侧区域样式
 */
.cart-item-right {
  flex: 1;                                                  /* 占满剩余空间 */
  display: flex;                                            /* flex布局 */
  flex-direction: column;                                   /* 垂直排列 */
  justify-content: space-between;                            /* 两端对齐 */
  padding: 8px 0;                                          /* 上下内边距8px */
}

/**
 * 商品名称样式
 */
.cart-item-name {
  font-size: 14px;                                          /* 字号14px */
  color: var(--text-color);                                 /* 文字颜色 */
  line-height: 1.4;                                         /* 行高1.4 */
  cursor: pointer;                                          /* 鼠标指针变为手型 */
}

/**
 * 商品名称悬停样式
 */
.cart-item-name:hover {
  color: var(--primary);                                    /* 主题色 */
}

/**
 * 商品价格样式
 */
.cart-item-price {
  font-size: 18px;                                          /* 字号18px */
  font-weight: 700;                                         /* 字重700 */
  color: #f56c6c;                                          /* 红色 */
}

/**
 * 操作区域样式
 */
.cart-item-actions {
  display: flex;                                            /* flex布局 */
  align-items: center;                                      /* 垂直居中 */
  gap: 16px;                                                /* 间距16px */
}

/**
 * 购物车底部结算栏样式
 */
.cart-footer {
  display: flex;                                            /* flex布局 */
  align-items: center;                                      /* 垂直居中 */
  justify-content: space-between;                            /* 两端对齐 */
  background: #fff;                                         /* 白色背景 */
  padding: 16px;                                           /* 内边距16px */
  border-radius: var(--radius);                             /* 圆角 */
  position: sticky;                                         /* 粘性定位 */
  bottom: 0;                                                /* 固定在底部 */
}

/**
 * 全选区域样式
 */
.select-all {
  display: flex;                                            /* flex布局 */
  align-items: center;                                      /* 垂直居中 */
  gap: 12px;                                                /* 间距12px */
}

/**
 * 选中数量样式
 */
.selected-count {
  font-size: 14px;                                          /* 字号14px */
  color: var(--text-secondary);                             /* 次要文字颜色 */
}

/**
 * 合计区域样式
 */
.total-section {
  display: flex;                                            /* flex布局 */
  align-items: center;                                      /* 垂直居中 */
  gap: 12px;                                                /* 间距12px */
}

/**
 * 合计标签样式
 */
.total-label {
  font-size: 14px;                                          /* 字号14px */
  color: var(--text-color);                                 /* 文字颜色 */
}

/**
 * 合计价格样式
 */
.total-price {
  font-size: 24px;                                          /* 字号24px */
  font-weight: 700;                                         /* 字重700 */
  color: #f56c6c;                                          /* 红色 */
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
 * 骨架屏购物车项样式
 */
.skeleton-cart-item {
  display: flex;                                            /* flex布局 */
  padding: 16px;                                           /* 内边距16px */
  gap: 12px;                                                /* 间距12px */
  border-bottom: 1px solid var(--border-color);             /* 底部边框 */
}

/**
 * 骨架屏图片样式
 */
.skeleton-img {
  flex-shrink: 0;                                          /* 不缩小 */
}

/**
 * 骨架屏信息样式
 */
.skeleton-info {
  flex: 1;                                                  /* 占满剩余空间 */
}
</style>