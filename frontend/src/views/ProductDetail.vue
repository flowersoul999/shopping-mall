<template>
  <div class="product-detail page-container">
    <div class="main-width">
      <el-skeleton :loading="loading" animated>
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
        <template #default>
          <el-alert v-if="error" :title="error" type="error" show-icon :closable="false" />
          <div v-else-if="product" class="detail-content">
            <div class="detail-image">
              <el-image :src="product.image" fit="contain" style="width:100%;height:100%">
                <template #error>
                  <div class="image-placeholder">
                    <el-icon :size="64"><Picture /></el-icon>
                  </div>
                </template>
              </el-image>
            </div>
            <div class="detail-info">
              <h1 class="product-title">{{ product.name }}</h1>
              <p class="product-desc">{{ product.description }}</p>
              <div class="product-price">
                <span class="price-label">售价</span>
                <span class="price-value">&yen;{{ formatPrice(product.price) }}</span>
              </div>
              <div class="product-meta">
                <span class="meta-item">
                  <el-icon><ShoppingCart /></el-icon>
                  已售 {{ product.sales }} 件
                </span>
                <span class="meta-item">
                  <el-icon><Package /></el-icon>
                  库存 {{ product.stock }} 件
                </span>
                <span class="meta-item">
                  <el-icon><Grid /></el-icon>
                  {{ product.categoryName || '未分类' }}
                </span>
              </div>
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
              <div class="action-buttons">
                <el-button type="primary" size="large" @click="handleAddToCart">
                  <el-icon><ShoppingCart /></el-icon>
                  加入购物车
                </el-button>
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
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Picture, ShoppingCart, Package, Grid, Ticket } from '@element-plus/icons-vue'
import { getProductDetail } from '../api/product'
import { useCartStore } from '../store/cart'
import { useUserStore } from '../store/user'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

const product = ref(null)
const loading = ref(true)
const error = ref('')
const quantity = ref(1)

const cartStore = useCartStore()
const userStore = useUserStore()

function formatPrice(price) {
  return Number(price).toFixed(2)
}

async function fetchProduct() {
  loading.value = true
  error.value = ''
  try {
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

async function handleAddToCart() {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  if (!product.value) return
  await cartStore.addToCart({
    id: product.value.id,
    quantity: quantity.value
  })
}

function handleBuyNow() {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  handleAddToCart().then(() => {
    router.push('/checkout')
  })
}

onMounted(() => {
  fetchProduct()
})
</script>

<style scoped>
.detail-content {
  display: flex;
  gap: 40px;
  padding-top: 20px;
}

.detail-image {
  width: 400px;
  height: 400px;
  background: #f8f8f8;
  border-radius: var(--radius);
  overflow: hidden;
  flex-shrink: 0;
}

.detail-info {
  flex: 1;
  min-width: 0;
}

.product-title {
  font-size: 24px;
  font-weight: 600;
  color: var(--text-color);
  margin-bottom: 12px;
  line-height: 1.4;
}

.product-desc {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.6;
  margin-bottom: 20px;
}

.product-price {
  display: flex;
  align-items: baseline;
  gap: 8px;
  margin-bottom: 20px;
}

.price-label {
  font-size: 14px;
  color: var(--text-secondary);
}

.price-value {
  font-size: 32px;
  font-weight: 700;
  color: #f56c6c;
}

.product-meta {
  display: flex;
  gap: 24px;
  margin-bottom: 20px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  color: var(--text-secondary);
}

.quantity-select {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
}

.quantity-label {
  font-size: 14px;
  color: var(--text-color);
}

.action-buttons {
  display: flex;
  gap: 12px;
}

.action-buttons .el-button {
  width: 180px;
}

.image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #c0c4cc;
}

.skeleton-detail {
  display: flex;
  gap: 40px;
  padding-top: 20px;
}

.skeleton-image {
  flex-shrink: 0;
}

.skeleton-info {
  flex: 1;
}
</style>