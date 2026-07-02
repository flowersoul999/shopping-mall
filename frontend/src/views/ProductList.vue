<template>
  <div class="product-list page-container">
    <div class="main-width">
      <div class="search-bar">
        <el-input
          v-model="keyword"
          placeholder="搜索商品"
          :prefix-icon="Search"
          clearable
          @keyup.enter="handleSearch"
        />
        <el-button type="primary" @click="handleSearch">搜索</el-button>
      </div>

      <div class="filter-section">
        <div class="filter-label">分类筛选：</div>
        <el-tag
          v-for="cat in categories"
          :key="cat.id"
          :type="selectedCategory === cat.id ? 'primary' : 'info'"
          :closable="selectedCategory === cat.id"
          @click="selectCategory(cat.id)"
          @close="clearCategory"
        >
          {{ cat.name }}
        </el-tag>
        <el-tag v-if="selectedCategory" type="info" @click="clearCategory">清除筛选</el-tag>
      </div>

      <div class="result-count">共 {{ total }} 件商品</div>

      <el-skeleton :loading="loading" animated :count="4">
        <template #template>
          <div class="skeleton-grid">
            <div v-for="i in 4" :key="i" class="skeleton-card">
              <el-skeleton-item variant="image" style="width:100%;height:200px" />
              <div style="padding:12px">
                <el-skeleton-item variant="p" style="width:60%;height:16px;margin-bottom:8px" />
                <el-skeleton-item variant="p" style="width:30%;height:20px" />
              </div>
            </div>
          </div>
        </template>
        <template #default>
          <el-alert v-if="error" :title="error" type="error" show-icon :closable="false" style="margin-bottom:16px" />
          <el-empty v-if="!error && products.length === 0 && !loading" description="暂无商品" />
          <el-row v-if="products.length > 0" :gutter="20">
            <el-col :xs="12" :sm="12" :md="6" v-for="(product, idx) in products" :key="product.id" style="margin-bottom:20px">
              <ProductCard :product="product" :placeholder="getPlaceholderSVG(product.name, idx)" />
            </el-col>
          </el-row>
        </template>
      </el-skeleton>

      <div class="pagination-section" v-if="total > pageSize">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[8, 12, 20]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handlePageChange"
          @current-change="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import { getProductList, getCategoryList, searchProducts } from '../api/product'
import ProductCard from '../components/ProductCard.vue'

const route = useRoute()
const router = useRouter()

const products = ref([])
const categories = ref([])
const loading = ref(true)
const error = ref('')
const page = ref(1)
const pageSize = ref(12)
const total = ref(0)
const keyword = ref('')
const selectedCategory = ref(null)

const placeholderColors = ['#FF6B6B', '#4ECDC4', '#45B7D1', '#96CEB4', '#FFEAA7', '#DDA0DD', '#98D8C8', '#F7DC6F', '#BB8FCE', '#85C1E9', '#F0B27A', '#82E0AA']

function getPlaceholderSVG(text, index) {
  const color = placeholderColors[Math.abs(index) % placeholderColors.length]
  const label = encodeURIComponent(String(text || '商品').slice(0, 6))
  return `data:image/svg+xml,${encodeURIComponent(`<svg xmlns="http://www.w3.org/2000/svg" width="200" height="200" viewBox="0 0 200 200"><rect width="200" height="200" fill="${color}" rx="8"/><text x="100" y="110" text-anchor="middle" fill="white" font-size="16" font-family="Arial">${label}</text></svg>`)}`
}

async function fetchCategories() {
  try {
    const res = await getCategoryList()
    if (res.code === 200) {
      categories.value = res.data || []
    }
  } catch {
    categories.value = []
  }
}

async function fetchProducts() {
  loading.value = true
  error.value = ''
  try {
    let res
    if (keyword.value.trim()) {
      res = await searchProducts(keyword.value.trim(), { page: page.value, pageSize: pageSize.value })
    } else {
      res = await getProductList({ page: page.value, pageSize: pageSize.value, categoryId: selectedCategory.value })
    }
    if (res.code === 200) {
      products.value = res.data?.list || res.data?.records || res.data || []
      total.value = res.data?.total || 0
    } else {
      error.value = res.msg || '加载商品失败'
    }
  } catch {
    error.value = '网络请求失败，请检查网络连接'
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  page.value = 1
  fetchProducts()
}

function selectCategory(id) {
  selectedCategory.value = id
  page.value = 1
  fetchProducts()
}

function clearCategory() {
  selectedCategory.value = null
  page.value = 1
  fetchProducts()
}

function handlePageChange() {
  fetchProducts()
}

watch(() => route.query, (newQuery) => {
  if (newQuery.keyword) {
    keyword.value = newQuery.keyword
  }
  if (newQuery.categoryId) {
    selectedCategory.value = parseInt(newQuery.categoryId)
  }
  page.value = 1
  fetchProducts()
}, { immediate: true })

onMounted(() => {
  fetchCategories()
})
</script>

<style scoped>
.search-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  max-width: 500px;
}

.search-bar .el-input {
  flex: 1;
}

.filter-section {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  margin-bottom: 16px;
}

.filter-label {
  font-size: 14px;
  color: var(--text-color);
}

.filter-section .el-tag {
  cursor: pointer;
}

.result-count {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 16px;
}

.skeleton-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.skeleton-card {
  border-radius: var(--radius);
  overflow: hidden;
  background: #fff;
}

.pagination-section {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  padding-bottom: 20px;
}
</style>