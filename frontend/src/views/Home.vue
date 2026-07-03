<template>
  <!-- 首页组件 -->
  <!-- 包含轮播图、商品分类和推荐商品三个主要区域 -->
  <div class="home">
    <!-- 轮播图区域 -->
    <div class="main-width banner-section">
      <el-carousel height="380px" indicator-position="outside" :interval="4000">
        <el-carousel-item v-for="(banner, idx) in banners" :key="idx">
          <div class="banner-slide">
            <!-- 轮播图背景图片 -->
            <el-image :src="banner.image" fit="cover" style="width:100%;height:100%">
              <!-- 图片加载失败时显示渐变色背景 -->
              <template #error>
                <div class="banner-slide-fallback" :style="{ background: banner.bg }"></div>
              </template>
            </el-image>
            <!-- 轮播图文字内容 -->
            <div class="banner-content">
              <h2 class="banner-title">{{ banner.title }}</h2>
              <p class="banner-desc">{{ banner.desc }}</p>
              <el-button type="primary" size="large" round @click="goToProducts">立即抢购</el-button>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>
    <!-- 商品分类区域 -->
    <div class="main-width category-section">
      <div class="section-title">商品分类</div>
      <el-row :gutter="16">
        <el-col :span="3" v-for="cat in categories" :key="cat.id">
          <div class="category-item" @click="goToCategory(cat.id)">
            <div class="category-icon" :style="{ background: cat.color }">
              <el-icon :size="28"><component :is="cat.icon" /></el-icon>
            </div>
            <span class="category-name">{{ cat.name }}</span>
          </div>
        </el-col>
      </el-row>
    </div>
    <!-- 推荐商品区域 -->
    <div class="main-width recommend-section">
      <div class="section-title">推荐商品</div>
      <!-- 加载中状态 -->
      <div v-if="loading" style="padding:40px;text-align:center;color:#999">加载中...</div>
      <!-- 错误状态 -->
      <div v-else-if="error" style="padding:40px;text-align:center;color:red">{{ error }}</div>
      <!-- 正常状态 -->
      <template v-else>
        <!-- 空状态 -->
        <div v-if="products.length === 0" style="padding:40px;text-align:center;color:#999">暂无推荐商品</div>
        <!-- 商品列表 -->
        <el-row v-else :gutter="20">
          <el-col :xs="12" :sm="12" :md="6" v-for="(item, idx) in products" :key="item.id" style="margin-bottom:20px">
            <div style="background:#fff;border-radius:8px;overflow:hidden;cursor:pointer;border:1px solid #eee" @click="router.push('/product/'+item.id)">
              <div style="width:100%;aspect-ratio:1;overflow:hidden;background:#f8f8f8">
                <el-image :src="item.image" fit="cover" lazy style="width:100%;height:100%">
                  <template #error>
                    <div style="width:100%;height:100%;display:flex;align-items:center;justify-content:center;color:#ccc;font-size:13px">{{ (item.name||'').slice(0,4) }}</div>
                  </template>
                </el-image>
              </div>
              <div style="padding:12px">
                <div style="font-size:14px;color:#333;margin-bottom:8px;display:-webkit-box;-webkit-line-clamp:2;-webkit-box-orient:vertical;overflow:hidden">{{ item.name }}</div>
                <div style="font-size:18px;color:#e4393c;font-weight:700">&yen;{{ Number(item.price).toFixed(2) }}</div>
              </div>
            </div>
          </el-col>
        </el-row>
      </template>
    </div>
    <!-- 底部占位，防止内容被footer遮挡 -->
    <div style="height:40px"></div>
  </div>
</template>

<script setup>
/**
 * Home首页组件
 * 展示网站首页，包含轮播图、商品分类导航和推荐商品列表
 */
import { ref, onMounted } from 'vue'                       // 引入Vue响应式API和生命周期钩子
import { useRouter } from 'vue-router'                     // 引入路由实例
import { ShoppingBag, Monitor, Cellphone, Watch, Headset, Camera, Reading, Present } from '@element-plus/icons-vue' // 引入Element Plus图标
import { getProductList, getCategoryList } from '../api/product' // 引入商品API

// 获取路由实例
const router = useRouter()

/**
 * 推荐商品列表
 */
const products = ref([])
/**
 * 商品分类列表
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
 * 分类图标颜色数组
 */
const catColors = ['#ff6b6b','#4ecdc4','#45b7d1','#96ceb4','#ffeaa7','#dda0dd','#98d8c8','#f7dc6f','#bb8fce','#85c1e9','#f0b27a','#82e0aa']
/**
 * 分类图标组件数组
 */
const catIcons = [Monitor, ShoppingBag, Watch, Headset, Camera, Reading, Present, Cellphone, Monitor, ShoppingBag, Watch, Headset]

/**
 * 轮播图数据配置
 */
const banners = [
  { title:'商城大促',desc:'不要998，不要888',bg:'linear-gradient(135deg,#2b6ec1,#4a90d9)',image:'/static/images/轮播图1.png' },
  { title:'新品首发',desc:'想要啥有啥',bg:'linear-gradient(135deg,#e4393c,#ff6b6b)',image:'/static/images/轮播图2.png' },
  { title:'品质生活',desc:'牢大只卖正品',bg:'linear-gradient(135deg,#2ecc71,#27ae60)',image:'/static/images/轮播图3.png' }
]

/**
 * 获取商品分类列表
 * 从后端获取分类数据，并为每个分类分配颜色和图标
 */
async function fetchCategories() {
  try {
    const res = await getCategoryList()
    if (res.code === 200) {
      categories.value = (res.data || []).map((c,i) => ({ 
        ...c, 
        color: catColors[i % catColors.length], 
        icon: catIcons[i % catIcons.length] 
      }))
    }
  } catch {}
}

/**
 * 获取推荐商品列表
 * 从后端获取商品数据，显示前8个推荐商品
 */
async function fetchProducts() {
  loading.value = true
  error.value = ''
  try {
    const res = await getProductList({ page: 1, pageSize: 8 })
    if (res.code === 200) {
      products.value = res.data?.list || []
      console.log('products loaded:', products.value.length, products.value)
    } else {
      error.value = res.message || '加载商品失败'
    }
  } catch (e) {
    error.value = '网络请求失败'
    console.error('fetch error:', e)
  } finally {
    loading.value = false
  }
}

/**
 * 跳转到商品列表页
 */
function goToProducts() { router.push('/products') }

/**
 * 跳转到指定分类的商品列表页
 * @param {number} id - 分类ID
 */
function goToCategory(id) { router.push({ path:'/products', query:{ categoryId:id } }) }

/**
 * 页面挂载时初始化数据
 */
onMounted(() => { 
  fetchCategories()
  fetchProducts() 
})
</script>

<style scoped>
/**
 * 首页主样式
 */
.home { min-height:100vh }

/**
 * 轮播图区域样式
 */
.banner-section { padding-top:20px }

/**
 * 轮播图单页样式
 */
.banner-slide { height:100%;display:flex;align-items:center;padding:0 60px;position:relative;overflow:hidden }
.banner-slide .el-image { position:absolute;top:0;left:0;width:100%;height:100% }
.banner-slide-fallback { position:absolute;top:0;left:0;width:100%;height:100% }

/**
 * 轮播图内容样式
 */
.banner-content { color:#000;position:relative;z-index:1 }
.banner-title { font-size:36px;font-weight:700;margin-bottom:12px }
.banner-desc { font-size:16px;margin-bottom:20px;opacity:.9 }

/**
 * 区域标题样式
 */
.section-title { font-size:22px;font-weight:700;color:#333;margin:32px 0 20px;padding-left:12px;border-left:4px solid #2b6ec1 }

/**
 * 分类项样式
 */
.category-item { text-align:center;cursor:pointer }
.category-icon { width:60px;height:60px;border-radius:12px;display:flex;align-items:center;justify-content:center;margin:0 auto 8px;color:#fff }
.category-name { font-size:13px;color:#666 }

/**
 * 内容宽度限制
 */
.main-width { max-width:1200px;margin:0 auto;padding:0 16px }
</style>
