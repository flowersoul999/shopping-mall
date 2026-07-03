<template>
  <!-- 商品卡片组件 -->
  <!-- 展示商品图片、名称、价格、销量和加入购物车按钮 -->
  <div class="product-card hover-card">
    <!-- 商品图片区域，点击跳转到商品详情页 -->
    <div class="product-image" @click="goToDetail">
      <el-image
        :src="product.image || product.img || product.pic"
        :alt="product.name || product.productName"
        fit="cover"
        lazy
      >
        <!-- 图片加载中占位 -->
        <template #placeholder>
          <div class="image-placeholder skeleton-placeholder" />
        </template>
        <!-- 图片加载失败占位 -->
        <template #error>
          <div class="image-placeholder">
            <el-icon :size="32"><Picture /></el-icon>
          </div>
        </template>
      </el-image>
    </div>
    <!-- 商品信息区域 -->
    <div class="product-info">
      <!-- 商品名称，点击跳转到商品详情页 -->
      <h3 class="product-name" @click="goToDetail">{{ product.name || product.productName }}</h3>
      <!-- 商品元信息：价格和销量 -->
      <div class="product-meta">
        <span class="price-text">&yen;{{ formatPrice(product.price) }}</span>
        <span class="sales">已售 {{ product.sales || product.soldCount || 0 }}</span>
      </div>
      <!-- 加入购物车按钮 -->
      <el-button class="add-cart-btn" type="primary" size="small" @click.stop="handleAddToCart">
        <el-icon><ShoppingCart /></el-icon>
        加入购物车
      </el-button>
    </div>
  </div>
</template>

<script setup>
/**
 * ProductCard商品卡片组件
 * 展示单个商品的信息，包括图片、名称、价格和销量
 * 提供点击查看详情和加入购物车功能
 */
import { useRouter } from 'vue-router'                      // 引入路由实例
import { Picture, ShoppingCart } from '@element-plus/icons-vue' // 引入Element Plus图标
import { useCartStore } from '../store/cart'                // 引入购物车状态管理
import { useUserStore } from '../store/user'                // 引入用户状态管理
import { ElMessage } from 'element-plus'                    // 引入消息提示组件

/**
 * 定义组件属性
 * @param {Object} product - 商品对象（必填）
 */
const props = defineProps({
  product: {
    type: Object,
    required: true
  }
})

// 获取路由实例
const router = useRouter()
// 获取购物车store实例
const cartStore = useCartStore()
// 获取用户store实例
const userStore = useUserStore()

/**
 * 格式化价格显示
 * @param {number|string} price - 价格值
 * @returns {string} 格式化后的价格字符串（保留两位小数）
 */
function formatPrice(price) {
  return Number(price).toFixed(2)
}

/**
 * 跳转到商品详情页
 * 获取商品ID并导航到对应详情页
 */
function goToDetail() {
  const id = props.product.id || props.product.productId     // 兼容不同字段名
  if (id) {
    router.push(`/product/${id}`)
  }
}

/**
 * 处理加入购物车操作
 * 未登录时提示登录，登录后调用购物车store添加商品
 */
async function handleAddToCart() {
  // 判断用户是否登录
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  // 调用购物车store添加商品
  await cartStore.addToCart(props.product)
}
</script>

<style scoped>
/**
 * 商品卡片主样式
 */
.product-card {
  background: #fff;                                         /* 白色背景 */
  border-radius: var(--radius);                             /* 圆角 */
  overflow: hidden;                                         /* 隐藏溢出 */
  cursor: pointer;                                          /* 鼠标指针 */
  border: 1px solid var(--border-color);                    /* 边框 */
}

/**
 * 商品图片区域样式
 */
.product-image {
  width: 100%;                                              /* 宽度100% */
  aspect-ratio: 1;                                          /* 正方形比例 */
  overflow: hidden;                                         /* 隐藏溢出 */
  background: #f8f8f8;                                      /* 浅灰色背景 */
}

/**
 * 图片组件样式
 */
.product-image .el-image {
  width: 100%;                                              /* 宽度100% */
  height: 100%;                                             /* 高度100% */
}

/**
 * 图片占位符样式
 */
.image-placeholder {
  width: 100%;                                              /* 宽度100% */
  height: 100%;                                             /* 高度100% */
  display: flex;                                            /* flex布局 */
  align-items: center;                                      /* 垂直居中对齐 */
  justify-content: center;                                  /* 水平居中对齐 */
  color: #c0c4cc;                                          /* 灰色 */
}

/**
 * 商品信息区域样式
 */
.product-info {
  padding: 12px;                                            /* 内边距12px */
}

/**
 * 商品名称样式
 */
.product-name {
  font-size: 14px;                                          /* 字号14px */
  font-weight: 400;                                         /* 常规粗细 */
  color: var(--text-color);                                 /* 文字颜色 */
  line-height: 1.4;                                         /* 行高1.4 */
  display: -webkit-box;                                     /* 多行省略 */
  -webkit-line-clamp: 2;                                    /* 最多显示2行 */
  -webkit-box-orient: vertical;                             /* 垂直排列 */
  overflow: hidden;                                         /* 隐藏溢出 */
  margin-bottom: 8px;                                       /* 下外边距8px */
  min-height: 39px;                                         /* 最小高度39px */
}

/**
 * 商品元信息样式
 */
.product-meta {
  display: flex;                                            /* flex布局 */
  align-items: center;                                      /* 垂直居中对齐 */
  justify-content: space-between;                            /* 两端对齐 */
}

/**
 * 销量文字样式
 */
.sales {
  font-size: 12px;                                          /* 字号12px */
  color: var(--text-secondary);                             /* 次要文字颜色 */
}

/**
 * 加入购物车按钮样式
 */
.add-cart-btn {
  width: 100%;                                              /* 宽度100% */
  margin-top: 8px;                                          /* 上外边距8px */
  padding: 6px;                                             /* 内边距6px */
}
</style>
