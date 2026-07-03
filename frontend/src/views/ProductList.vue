<template>
  <!-- 商品列表页面组件 -->
  <div class="product-list page-container">
    <div class="main-width">
      <!-- 搜索栏 -->
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

      <!-- 分类筛选区域 -->
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
        <!-- 清除筛选按钮 -->
        <el-tag v-if="selectedCategory" type="info" @click="clearCategory">清除筛选</el-tag>
      </div>

      <!-- 商品数量统计 -->
      <div class="result-count">共 {{ total }} 件商品</div>

      <!-- 骨架屏加载组件 -->
      <el-skeleton :loading="loading" animated :count="4">
        <!-- 骨架屏模板 -->
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
        <!-- 实际内容 -->
        <template #default>
          <!-- 错误提示 -->
          <el-alert v-if="error" :title="error" type="error" show-icon :closable="false" style="margin-bottom:16px" />
          <!-- 空状态 -->
          <el-empty v-if="!error && products.length === 0 && !loading" description="暂无商品" />
          <!-- 商品列表 -->
          <el-row v-if="products.length > 0" :gutter="20">
            <el-col :xs="12" :sm="12" :md="6" v-for="(product, idx) in products" :key="product.id" style="margin-bottom:20px">
              <ProductCard :product="product" :placeholder="getPlaceholderSVG(product.name, idx)" />
            </el-col>
          </el-row>
        </template>
      </el-skeleton>

      <!-- 分页组件 -->
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
/**
 * ProductList商品列表页面组件
 * 展示商品列表，支持搜索、分类筛选和分页功能
 */
import { ref, onMounted, watch } from 'vue'               // 引入Vue响应式API和生命周期钩子
import { useRoute, useRouter } from 'vue-router'           // 引入路由相关API
import { Search } from '@element-plus/icons-vue'           // 引入搜索图标
import { getProductList, getCategoryList, searchProducts } from '../api/product' // 引入商品API
import ProductCard from '../components/ProductCard.vue'    // 引入商品卡片组件

// 获取路由实例
const route = useRoute()
const router = useRouter()

/**
 * 商品列表数据
 */
const products = ref([])
/**
 * 分类列表数据
 */
const categories = ref([])
/**
 * 加载状态
 */
const loading = ref(true)
/**
 * 错误信息
 */
const error = ref('')
/**
 * 当前页码
 */
const page = ref(1)
/**
 * 每页条数
 */
const pageSize = ref(12)
/**
 * 商品总数
 */
const total = ref(0)
/**
 * 搜索关键词
 */
const keyword = ref('')
/**
 * 选中的分类ID
 */
const selectedCategory = ref(null)

/**
 * 占位图颜色数组，用于生成商品占位SVG
 */
const placeholderColors = ['#FF6B6B', '#4ECDC4', '#45B7D1', '#96CEB4', '#FFEAA7', '#DDA0DD', '#98D8C8', '#F7DC6F', '#BB8FCE', '#85C1E9', '#F0B27A', '#82E0AA']

/**
 * 生成SVG占位图
 * @param {string} text - 商品名称
 * @param {number} index - 商品索引
 * @returns {string} SVG图片数据URL
 */
function getPlaceholderSVG(text, index) {
  // 根据索引选择颜色
  const color = placeholderColors[Math.abs(index) % placeholderColors.length]
  // 截取商品名称前6个字符
  const label = encodeURIComponent(String(text || '商品').slice(0, 6))
  // 返回SVG数据URL
  return `data:image/svg+xml,${encodeURIComponent(`<svg xmlns="http://www.w3.org/2000/svg" width="200" height="200" viewBox="0 0 200 200"><rect width="200" height="200" fill="${color}" rx="8"/><text x="100" y="110" text-anchor="middle" fill="white" font-size="16" font-family="Arial">${label}</text></svg>`)}`
}

/**
 * 获取商品分类列表
 * 从后端获取分类数据用于筛选
 */
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

/**
 * 获取商品列表数据
 * 根据关键词或分类筛选条件获取商品
 */
async function fetchProducts() {
  loading.value = true
  error.value = ''
  try {
    let res
    // 如果有搜索关键词，调用搜索接口
    if (keyword.value.trim()) {
      res = await searchProducts(keyword.value.trim(), { page: page.value, pageSize: pageSize.value })
    } else {
      // 否则调用商品列表接口，支持分类筛选
      res = await getProductList({ page: page.value, pageSize: pageSize.value, categoryId: selectedCategory.value })
    }
    if (res.code === 200) {
      // 兼容不同的返回格式
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

/**
 * 处理搜索
 * 重置页码并重新获取商品列表
 */
function handleSearch() {
  page.value = 1
  fetchProducts()
}

/**
 * 选择分类
 * @param {number} id - 分类ID
 */
function selectCategory(id) {
  selectedCategory.value = id
  page.value = 1
  fetchProducts()
}

/**
 * 清除分类筛选
 */
function clearCategory() {
  selectedCategory.value = null
  page.value = 1
  fetchProducts()
}

/**
 * 处理分页变化
 * 页码或每页条数变化时重新获取商品列表
 */
function handlePageChange() {
  fetchProducts()
}

/**
 * 监听路由参数变化
 * 当URL参数变化时更新筛选条件并重新获取商品列表
 */
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

/**
 * 页面挂载时初始化分类数据
 */
onMounted(() => {
  fetchCategories()
})
</script>

<style scoped>
/**
 * 搜索栏样式
 */
.search-bar {
  display: flex;                                            /* flex布局 */
  gap: 12px;                                                /* 间距12px */
  margin-bottom: 20px;                                      /* 下外边距20px */
  max-width: 500px;                                         /* 最大宽度500px */
}

/**
 * 搜索输入框样式
 */
.search-bar .el-input {
  flex: 1;                                                  /* 占满剩余空间 */
}

/**
 * 筛选区域样式
 */
.filter-section {
  display: flex;                                            /* flex布局 */
  align-items: center;                                      /* 垂直居中 */
  gap: 12px;                                                /* 间距12px */
  flex-wrap: wrap;                                          /* 自动换行 */
  margin-bottom: 16px;                                      /* 下外边距16px */
}

/**
 * 筛选标签样式
 */
.filter-label {
  font-size: 14px;                                          /* 字号14px */
  color: var(--text-color);                                 /* 文字颜色 */
}

/**
 * 筛选标签的el-tag样式
 */
.filter-section .el-tag {
  cursor: pointer;                                          /* 鼠标指针变为手型 */
}

/**
 * 商品数量统计样式
 */
.result-count {
  font-size: 14px;                                          /* 字号14px */
  color: var(--text-secondary);                             /* 次要文字颜色 */
  margin-bottom: 16px;                                      /* 下外边距16px */
}

/**
 * 骨架屏网格布局样式
 */
.skeleton-grid {
  display: grid;                                            /* grid布局 */
  grid-template-columns: repeat(4, 1fr);                    /* 4列等宽 */
  gap: 20px;                                                /* 间距20px */
}

/**
 * 骨架屏卡片样式
 */
.skeleton-card {
  border-radius: var(--radius);                             /* 圆角 */
  overflow: hidden;                                         /* 溢出隐藏 */
  background: #fff;                                         /* 白色背景 */
}

/**
 * 分页区域样式
 */
.pagination-section {
  display: flex;                                            /* flex布局 */
  justify-content: center;                                  /* 水平居中 */
  margin-top: 30px;                                         /* 上外边距30px */
  padding-bottom: 20px;                                     /* 下内边距20px */
}
</style>