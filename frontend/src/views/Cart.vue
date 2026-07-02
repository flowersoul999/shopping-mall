<template>
  <div class="cart page-container">
    <div class="main-width">
      <h2 class="page-title">购物车</h2>

      <el-skeleton :loading="loading" animated>
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
        <template #default>
          <el-empty v-if="cartStore.cartList.length === 0 && !loading" description="购物车是空的" />
          <div v-else>
            <div class="cart-list">
              <div v-for="item in cartStore.cartList" :key="item.id" class="cart-item">
                <div class="cart-item-left">
                  <div class="cart-checkbox">
                    <el-checkbox v-model="item.selected" @change="handleSelect" />
                  </div>
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
                <div class="cart-item-right">
                  <div class="cart-item-name" @click="goToProduct(item.productId)">{{ item.productName || item.name }}</div>
                  <div class="cart-item-price">&yen;{{ formatPrice(item.price) }}</div>
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

            <div class="cart-footer">
              <div class="select-all">
                <el-checkbox :checked="isAllSelected" @change="handleSelectAll">全选</el-checkbox>
                <span class="selected-count">已选 {{ selectedCount }} 件</span>
              </div>
              <div class="total-section">
                <span class="total-label">合计：</span>
                <span class="total-price">&yen;{{ formatPrice(cartStore.totalPrice) }}</span>
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
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Picture } from '@element-plus/icons-vue'
import { useCartStore } from '../store/cart'

const router = useRouter()
const cartStore = useCartStore()
const loading = ref(true)

function formatPrice(price) {
  return Number(price).toFixed(2)
}

function goToProduct(productId) {
  router.push(`/product/${productId}`)
}

function handleSelect() {
}

const isAllSelected = computed(() => {
  return cartStore.cartList.length > 0 && cartStore.cartList.every(item => item.selected !== false)
})

const selectedCount = computed(() => {
  return cartStore.cartList.filter(item => item.selected !== false).reduce((count, item) => count + (item.quantity || 1), 0)
})

function handleSelectAll(val) {
  cartStore.cartList.forEach(item => {
    item.selected = val
  })
}

function handleQuantityChange(id, quantity) {
  cartStore.updateQuantity(id, quantity)
}

function handleRemove(id) {
  cartStore.removeItem(id)
}

function handleCheckout() {
  router.push('/checkout')
}

onMounted(() => {
  cartStore.fetchCartList().finally(() => {
    loading.value = false
    cartStore.cartList.forEach(item => {
      item.selected = true
    })
  })
})
</script>

<style scoped>
.cart-list {
  background: #fff;
  border-radius: var(--radius);
  overflow: hidden;
  margin-bottom: 20px;
}

.cart-item {
  display: flex;
  padding: 16px;
  border-bottom: 1px solid var(--border-color);
}

.cart-item:last-child {
  border-bottom: none;
}

.cart-item-left {
  display: flex;
  gap: 12px;
  flex-shrink: 0;
}

.cart-checkbox {
  margin-top: 35px;
}

.cart-image {
  width: 100px;
  height: 100px;
  background: #f8f8f8;
  border-radius: var(--radius);
  overflow: hidden;
  cursor: pointer;
}

.cart-item-right {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 8px 0;
}

.cart-item-name {
  font-size: 14px;
  color: var(--text-color);
  line-height: 1.4;
  cursor: pointer;
}

.cart-item-name:hover {
  color: var(--primary);
}

.cart-item-price {
  font-size: 18px;
  font-weight: 700;
  color: #f56c6c;
}

.cart-item-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.cart-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  padding: 16px;
  border-radius: var(--radius);
  position: sticky;
  bottom: 0;
}

.select-all {
  display: flex;
  align-items: center;
  gap: 12px;
}

.selected-count {
  font-size: 14px;
  color: var(--text-secondary);
}

.total-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.total-label {
  font-size: 14px;
  color: var(--text-color);
}

.total-price {
  font-size: 24px;
  font-weight: 700;
  color: #f56c6c;
}

.image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #c0c4cc;
}

.skeleton-cart-item {
  display: flex;
  padding: 16px;
  gap: 12px;
  border-bottom: 1px solid var(--border-color);
}

.skeleton-img {
  flex-shrink: 0;
}

.skeleton-info {
  flex: 1;
}
</style>