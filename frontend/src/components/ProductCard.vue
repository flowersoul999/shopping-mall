<template>
  <div class="product-card hover-card">
    <div class="product-image" @click="goToDetail">
      <el-image
        :src="product.image || product.img || product.pic"
        :alt="product.name || product.productName"
        fit="cover"
        lazy
      >
        <template #placeholder>
          <div class="image-placeholder skeleton-placeholder" />
        </template>
        <template #error>
          <div class="image-placeholder">
            <el-icon :size="32"><Picture /></el-icon>
          </div>
        </template>
      </el-image>
    </div>
    <div class="product-info">
      <h3 class="product-name" @click="goToDetail">{{ product.name || product.productName }}</h3>
      <div class="product-meta">
        <span class="price-text">&yen;{{ formatPrice(product.price) }}</span>
        <span class="sales">已售 {{ product.sales || product.soldCount || 0 }}</span>
      </div>
      <el-button class="add-cart-btn" type="primary" size="small" @click.stop="handleAddToCart">
        <el-icon><ShoppingCart /></el-icon>
        加入购物车
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { Picture, ShoppingCart } from '@element-plus/icons-vue'
import { useCartStore } from '../store/cart'
import { useUserStore } from '../store/user'
import { ElMessage } from 'element-plus'

const props = defineProps({
  product: {
    type: Object,
    required: true
  }
})

const router = useRouter()
const cartStore = useCartStore()
const userStore = useUserStore()

function formatPrice(price) {
  return Number(price).toFixed(2)
}

function goToDetail() {
  const id = props.product.id || props.product.productId
  if (id) {
    router.push(`/product/${id}`)
  }
}

async function handleAddToCart() {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  await cartStore.addToCart(props.product)
}
</script>

<style scoped>
.product-card {
  background: #fff;
  border-radius: var(--radius);
  overflow: hidden;
  cursor: pointer;
  border: 1px solid var(--border-color);
}

.product-image {
  width: 100%;
  aspect-ratio: 1;
  overflow: hidden;
  background: #f8f8f8;
}

.product-image .el-image {
  width: 100%;
  height: 100%;
}

.image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #c0c4cc;
}

.product-info {
  padding: 12px;
}

.product-name {
  font-size: 14px;
  font-weight: 400;
  color: var(--text-color);
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-bottom: 8px;
  min-height: 39px;
}

.product-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.sales {
  font-size: 12px;
  color: var(--text-secondary);
}

.add-cart-btn {
  width: 100%;
  margin-top: 8px;
  padding: 6px;
}
</style>
