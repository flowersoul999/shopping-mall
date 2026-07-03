<template>
  <!-- 商品详情页面组件 -->
  <div class="product-detail page-container">
    <div class="main-width">
      <!-- 骨架屏加载组件 -->
      <el-skeleton :loading="loading" animated>
        <!-- 骨架屏模板 -->
        <template #template>
          <div class="skeleton-detail">
            <div class="skeleton-image"><el-skeleton-item variant="image" style="width:400px;height:400px" /></div>
            <div class="skeleton-info">
              <el-skeleton-item variant="h1" style="width:60%" />
              <el-skeleton-item variant="p" style="width:80%" />
              <el-skeleton-item variant="p" style="width:40%" />
              <el-skeleton-item variant="p" style="width:100%" />
              <el-skeleton-item variant="p" style="width:100%" />
              <el-skeleton-item variant="p" style="width:100%" />
              <div style="margin-top:20px">
                <el-skeleton-item variant="button" style="width:150px" />
                <el-skeleton-item variant="button" style="width:150px;margin-left:12px" />
              </div>
            </div>
          </div>
        </template>
        <!-- 实际内容 -->
        <template #default>
          <!-- 错误提示 -->
          <el-alert v-if="error" :title="error" type="error" show-icon :closable="false" />
          <!-- 商品详情内容 -->
          <div v-else-if="product" class="detail-content">
            <!-- 商品图片区域 -->
            <div class="detail-image">
              <el-image :src="product.image" fit="contain" style="width:100%;height:100%">
                <!-- 图片加载失败时显示占位图 -->
                <template #error>
                  <div class="image-placeholder">
                    <el-icon :size="64"><Picture /></el-icon>
                  </div>
                </template>
              </el-image>
            </div>
            <!-- 商品信息区域 -->
            <div class="detail-info">
              <!-- 商品标题 -->
              <h1 class="product-title">{{ product.name }}</h1>
              <!-- 商品描述 -->
              <p class="product-desc">{{ product.description }}</p>
              <!-- 商品价格 -->
              <div class="product-price">
                <span class="price-label">售价</span>
                <span class="price-value">&yen;{{ formatPrice(product.price) }}</span>
              </div>
              <!-- 商品元数据（销量、库存、分类） -->
              <div class="product-meta">
                <span class="meta-item">
                  <el-icon><ShoppingCart /></el-icon>
                  已售 {{ product.sales }} 件
                </span>
                <span class="meta-item">
                  <el-icon><Box /></el-icon>
                  库存 {{ product.stock }} 件
                </span>
                <span class="meta-item">
                  <el-icon><Grid /></el-icon>
                  {{ product.categoryName || '未分类' }}
                </span>
              </div>
              <!-- 数量选择 -->
              <div class="quantity-select">
                <span class="quantity-label">数量</span>
                <el-input-number
                  v-model="quantity"
                  :min="1"
                  :max="product.stock"
                  :step="1"
                  size="small"
                />
              </div>
              <!-- 操作按钮 -->
              <div class="action-buttons">
                <!-- 加入购物车按钮 -->
                <el-button type="primary" size="large" @click="handleAddToCart">
                  <el-icon><ShoppingCart /></el-icon>
                  加入购物车
                </el-button>
                <!-- 立即购买按钮 -->
                <el-button type="success" size="large" @click="handleBuyNow">
                  <el-icon><Ticket /></el-icon>
                  立即购买
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
 * ProductDetail商品详情页面组件
 * 展示商品详细信息，支持加入购物车和立即购买功能
 */
import { ref, onMounted } from 'vue'                       // 引入Vue响应式API和生命周期钩子
import { useRoute, useRouter } from 'vue-router'           // 引入路由相关API
import { Picture, ShoppingCart, Box, Grid, Ticket } from '@element-plus/icons-vue' // 引入Element Plus图标
import { getProductDetail } from '../api/product'          // 引入商品API
import { useCartStore } from '../store/cart'               // 引入购物车状态管理
import { useUserStore } from '../store/user'               // 引入用户状态管理
import { ElMessage } from 'element-plus'                   // 引入Element Plus消息提示

// 获取路由实例
const route = useRoute()
const router = useRouter()

/**
 * 商品详情数据
 */
const product = ref(null)
/**
 * 加载状态
 */
const loading = ref(true)
/**
 * 错误信息
 */
const error = ref('')
/**
 * 购买数量
 */
const quantity = ref(1)

// 获取购物车和用户store实例
const cartStore = useCartStore()
const userStore = useUserStore()

/**
 * 格式化价格
 * @param {number} price - 价格
 * @returns {string} 格式化后的价格字符串
 */
function formatPrice(price) {
  return Number(price).toFixed(2)
}

/**
 * 获取商品详情
 * 从后端获取指定ID的商品详细信息
 */
async function fetchProduct() {
  loading.value = true
  error.value = ''
  try {
    // 从路由参数获取商品ID并调用API
    const res = await getProductDetail(route.params.id)
    if (res.code === 200) {
      product.value = res.data
    } else {
      error.value = res.msg || '加载商品详情失败'
    }
  } catch {
    error.value = '网络请求失败'
  } finally {
    loading.value = false
  }
}

/**
 * 处理加入购物车
 * 验证登录状态后将商品加入购物车
 */
async function handleAddToCart() {
  // 验证登录状态
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  if (!product.value) return
  // 调用购物车store的addToCart方法
  await cartStore.addToCart({
    id: product.value.id,
    quantity: quantity.value
  })
}

/**
 * 处理立即购买
 * 验证登录状态后先加入购物车，再跳转到结算页面
 */
function handleBuyNow() {
  // 验证登录状态
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  // 先加入购物车，成功后跳转到结算页面
  handleAddToCart().then(() => {
    router.push('/checkout')
  })
}

/**
 * 页面挂载时获取商品详情
 */
onMounted(() => {
  fetchProduct()
})
</script>

<style scoped>
/**
 * 详情内容容器样式
 */
.detail-content {
  display: flex;                                            /* flex布局 */
  gap: 40px;                                                /* 间距40px */
  padding-top: 20px;                                        /* 上内边距20px */
}

/**
 * 商品图片区域样式
 */
.detail-image {
  width: 400px;                                            /* 固定宽度400px */
  height: 400px;                                           /* 固定高度400px */
  background: #f8f8f8;                                     /* 浅灰色背景 */
  border-radius: var(--radius);                             /* 圆角 */
  overflow: hidden;                                         /* 溢出隐藏 */
  flex-shrink: 0;                                          /* 不缩小 */
}

/**
 * 商品信息区域样式
 */
.detail-info {
  flex: 1;                                                  /* 占满剩余空间 */
  min-width: 0;                                             /* 允许缩小 */
}

/**
 * 商品标题样式
 */
.product-title {
  font-size: 24px;                                          /* 字号24px */
  font-weight: 600;                                         /* 字重600 */
  color: var(--text-color);                                 /* 文字颜色 */
  margin-bottom: 12px;                                      /* 下外边距12px */
  line-height: 1.4;                                         /* 行高1.4 */
}

/**
 * 商品描述样式
 */
.product-desc {
  font-size: 14px;                                          /* 字号14px */
  color: var(--text-secondary);                             /* 次要文字颜色 */
  line-height: 1.6;                                         /* 行高1.6 */
  margin-bottom: 20px;                                      /* 下外边距20px */
}

/**
 * 商品价格样式
 */
.product-price {
  display: flex;                                            /* flex布局 */
  align-items: baseline;                                    /* 基线对齐 */
  gap: 8px;                                                /* 间距8px */
  margin-bottom: 20px;                                      /* 下外边距20px */
}

/**
 * 价格标签样式
 */
.price-label {
  font-size: 14px;                                          /* 字号14px */
  color: var(--text-secondary);                             /* 次要文字颜色 */
}

/**
 * 价格数值样式
 */
.price-value {
  font-size: 32px;                                          /* 字号32px */
  font-weight: 700;                                         /* 字重700 */
  color: #f56c6c;                                          /* 红色 */
}

/**
 * 商品元数据样式
 */
.product-meta {
  display: flex;                                            /* flex布局 */
  gap: 24px;                                                /* 间距24px */
  margin-bottom: 20px;                                      /* 下外边距20px */
}

/**
 * 元数据项样式
 */
.meta-item {
  display: flex;                                            /* flex布局 */
  align-items: center;                                      /* 垂直居中 */
  gap: 4px;                                                /* 间距4px */
  font-size: 14px;                                          /* 字号14px */
  color: var(--text-secondary);                             /* 次要文字颜色 */
}

/**
 * 数量选择样式
 */
.quantity-select {
  display: flex;                                            /* flex布局 */
  align-items: center;                                      /* 垂直居中 */
  gap: 12px;                                                /* 间距12px */
  margin-bottom: 24px;                                      /* 下外边距24px */
}

/**
 * 数量标签样式
 */
.quantity-label {
  font-size: 14px;                                          /* 字号14px */
  color: var(--text-color);                                 /* 文字颜色 */
}

/**
 * 操作按钮区域样式
 */
.action-buttons {
  display: flex;                                            /* flex布局 */
  gap: 12px;                                                /* 间距12px */
}

/**
 * 操作按钮样式
 */
.action-buttons .el-button {
  width: 180px;                                            /* 固定宽度180px */
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
 * 骨架屏详情样式
 */
.skeleton-detail {
  display: flex;                                            /* flex布局 */
  gap: 40px;                                                /* 间距40px */
  padding-top: 20px;                                        /* 上内边距20px */
}

/**
 * 骨架屏图片样式
 */
.skeleton-image {
  flex-shrink: 0;                                          /* 不缩小 */
}

/**
 * 骨架屏信息样式
 */
.skeleton-info {
  flex: 1;                                                  /* 占满剩余空间 */
}
</style>