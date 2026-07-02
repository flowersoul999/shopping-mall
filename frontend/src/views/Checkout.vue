<template>
  <div class="checkout page-container">
    <div class="main-width">
      <h2 class="page-title">确认订单</h2>

      <el-skeleton :loading="loading" animated>
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
        <template #default>
          <div v-if="cartItems.length === 0" class="empty-cart">
            <el-empty description="购物车为空，请先添加商品" />
            <el-button type="primary" @click="goToProducts">去购物</el-button>
          </div>

          <div v-else>
            <div class="checkout-section address-section">
              <div class="section-header">
                <el-icon><MapLocation /></el-icon>
                <span>收货地址</span>
                <el-button type="text" @click="goToAddress">管理地址</el-button>
              </div>
              <div v-if="addressList.length === 0" class="no-address">
                <el-empty description="请添加收货地址" />
                <el-button type="primary" @click="goToAddress">去添加</el-button>
              </div>
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
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { MapLocation, ShoppingBag, Ticket, Check, Picture } from '@element-plus/icons-vue'
import { useCartStore } from '../store/cart'
import { getAddressList } from '../api/address'
import { createOrder } from '../api/order'
import { ElMessage } from 'element-plus'

const router = useRouter()
const cartStore = useCartStore()

const loading = ref(true)
const submitting = ref(false)
const addressList = ref([])
const selectedAddressId = ref(null)
const remark = ref('')

const cartItems = computed(() => {
  return cartStore.cartList.filter(item => item.selected !== false)
})

const totalCount = computed(() => {
  return cartItems.value.reduce((count, item) => count + (item.quantity || 1), 0)
})

const productTotal = computed(() => {
  return cartItems.value.reduce((total, item) => total + (item.price || 0) * (item.quantity || 1), 0)
})

const totalPrice = computed(() => {
  return productTotal.value
})

function formatPrice(price) {
  return Number(price).toFixed(2)
}

function goToProducts() {
  router.push('/products')
}

function goToAddress() {
  router.push('/address')
}

function selectAddress(id) {
  selectedAddressId.value = id
}

async function loadAddresses() {
  try {
    const res = await getAddressList()
    if (res.code === 200) {
      addressList.value = res.data || []
      const defaultAddr = addressList.value.find(addr => addr.isDefault)
      if (defaultAddr) {
        selectedAddressId.value = defaultAddr.id
      } else if (addressList.value.length > 0) {
        selectedAddressId.value = addressList.value[0].id
      }
    }
  } catch (error) {
    console.error('Failed to load addresses:', error)
  }
}

async function submitOrder() {
  if (!selectedAddressId.value) {
    ElMessage.warning('请选择收货地址')
    return
  }

  if (cartItems.value.length === 0) {
    ElMessage.warning('购物车为空')
    return
  }

  submitting.value = true

  try {
    for (const item of cartItems.value) {
      await cartStore.addToCart({ productId: item.productId, quantity: item.quantity })
    }

    const orderData = {
      addressId: selectedAddressId.value,
      remark: remark.value
    }

    const res = await createOrder(orderData)

    if (res.code === 200) {
      ElMessage.success('订单提交成功')
      await cartStore.clearCart()
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

onMounted(async () => {
  await cartStore.fetchCartList()
  await loadAddresses()
  loading.value = false
})
</script>

<style scoped>
.checkout-section {
  background: #fff;
  border-radius: var(--radius);
  padding: 16px;
  margin-bottom: 16px;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--border-color);
}

.section-header span {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-color);
}

.section-header .product-count {
  margin-left: auto;
  font-size: 14px;
  font-weight: normal;
  color: var(--text-secondary);
}

.no-address {
  text-align: center;
  padding: 24px 0;
}

.no-address .el-button {
  margin-top: 16px;
}

.address-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.address-item {
  display: flex;
  flex-direction: column;
  padding: 12px;
  border: 2px solid transparent;
  border-radius: var(--radius);
  background: #fafafa;
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
}

.address-item:hover {
  border-color: var(--primary-light);
}

.address-item.active {
  border-color: var(--primary);
  background: #f0f5ff;
}

.address-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.address-name {
  font-size: 14px;
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

.address-check {
  position: absolute;
  right: 12px;
  bottom: 12px;
  color: var(--primary);
  font-size: 18px;
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

.order-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.order-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
}

.order-label {
  color: var(--text-secondary);
}

.order-value {
  color: var(--text-color);
}

.order-value.discount {
  color: #67c23a;
}

.order-row.total {
  padding-top: 12px;
  border-top: 1px dashed var(--border-color);
  margin-top: 8px;
}

.order-row.total .order-label {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-color);
}

.order-row.total .order-value {
  font-size: 24px;
  font-weight: 700;
  color: #f56c6c;
}

.remark-section {
  margin-top: 16px;
}

.checkout-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  padding: 16px;
  border-radius: var(--radius);
  position: sticky;
  bottom: 0;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
}

.footer-left {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.footer-label {
  font-size: 14px;
  color: var(--text-secondary);
}

.footer-total {
  font-size: 24px;
  font-weight: 700;
  color: #f56c6c;
}

.empty-cart {
  text-align: center;
  padding: 60px 0;
}

.empty-cart .el-button {
  margin-top: 16px;
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