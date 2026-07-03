<template>
  <!-- 订单管理页面组件 -->
  <div class="main-content">
    <!-- 页面头部（标题） -->
    <div class="page-header">
        <h2 class="page-title">订单管理</h2>
      </div>

      <!-- 统计卡片（订单总数、总销售额、各状态订单数） -->
      <div class="stats-card">
        <div class="stat-item">
          <div class="stat-value">{{ stats.total || 0 }}</div>
          <div class="stat-label">订单总数</div>
        </div>
        <div class="stat-item">
          <div class="stat-value sales">&yen;{{ formatPrice(stats.totalSales || 0) }}</div>
          <div class="stat-label">总销售额</div>
        </div>
        <div class="stat-item">
          <div class="stat-value pending">{{ stats.pending || 0 }}</div>
          <div class="stat-label">待付款</div>
        </div>
        <div class="stat-item">
          <div class="stat-value paid">{{ stats.paid || 0 }}</div>
          <div class="stat-label">待发货</div>
        </div>
        <div class="stat-item">
          <div class="stat-value shipped">{{ stats.shipped || 0 }}</div>
          <div class="stat-label">待收货</div>
        </div>
        <div class="stat-item">
          <div class="stat-value completed">{{ stats.completed || 0 }}</div>
          <div class="stat-label">已完成</div>
        </div>
      </div>

      <!-- 搜索栏（订单号搜索、状态筛选） -->
      <div class="search-bar">
        <el-input
          v-model="searchForm.orderNo"
          placeholder="搜索订单号"
          prefix-icon="Search"
          style="width: 250px"
          @keyup.enter="loadOrders"
        />
        <el-select
          v-model="searchForm.status"
          placeholder="订单状态"
          style="width: 150px"
          @change="loadOrders"
        >
          <el-option label="全部" value="all" />
          <el-option label="待付款" value="pending" />
          <el-option label="待发货" value="paid" />
          <el-option label="待收货" value="shipped" />
          <el-option label="已完成" value="completed" />
        </el-select>
        <el-button type="primary" @click="loadOrders">搜索</el-button>
      </div>

      <!-- 订单列表表格 -->
      <el-table :data="orderList" stripe style="width: 100%">
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="userId" label="用户ID" width="80" />
        <el-table-column prop="receiverName" label="收货人" width="100" />
        <el-table-column prop="receiverPhone" label="手机号" width="120" />
        <!-- 商品信息列（带图片和名称） -->
        <el-table-column label="商品信息" min-width="200">
          <template #default="scope">
            <div v-for="item in scope.row.items" :key="item.id" class="product-info">
              <el-image :src="item.productImage" style="width:40px;height:40px;float:left;margin-right:8px" fit="cover">
                <template #error>
                  <div style="width:40px;height:40px;background:#f5f5f5;display:flex;align-items:center;justify-content:center">
                    <el-icon :size="16"><Picture /></el-icon>
                  </div>
                </template>
              </el-image>
              <span style="font-size:13px">{{ item.productName }} x{{ item.quantity }}</span>
            </div>
          </template>
        </el-table-column>
        <!-- 订单金额列（红色高亮） -->
        <el-table-column prop="totalPrice" label="订单金额" width="120">
          <template #default="scope">
            <span class="price">&yen;{{ formatPrice(scope.row.totalPrice) }}</span>
          </template>
        </el-table-column>
        <!-- 状态列（标签形式） -->
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="160" />
        <!-- 操作列（查看详情、发货、完成订单、取消订单） -->
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="viewDetail(scope.row)">查看详情</el-button>
            <!-- 待发货状态：发货按钮 -->
            <el-button
              v-if="scope.row.status === 'paid'"
              type="primary"
              size="small"
              @click="handleShip(scope.row.id)"
            >
              发货
            </el-button>
            <!-- 待收货状态：完成订单按钮 -->
            <el-button
              v-if="scope.row.status === 'shipped'"
              type="primary"
              size="small"
              @click="handleComplete(scope.row.id)"
            >
              完成订单
            </el-button>
            <!-- 待付款状态：取消订单按钮 -->
            <el-button
              v-if="scope.row.status === 'pending'"
              type="danger"
              size="small"
              @click="handleCancel(scope.row.id)"
            >
              取消订单
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadOrders"
          @current-change="loadOrders"
        />
      </div>

      <!-- 订单详情弹窗 -->
      <el-dialog title="订单详情" v-model="showDetail" width="600px">
        <div v-if="currentOrder" class="detail-content">
          <div class="detail-row">
            <span class="detail-label">订单号</span>
            <span class="detail-value">{{ currentOrder.orderNo }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">下单时间</span>
            <span class="detail-value">{{ currentOrder.createTime }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">订单状态</span>
            <el-tag :type="getStatusType(currentOrder.status)">
              {{ getStatusText(currentOrder.status) }}
            </el-tag>
          </div>
          <div class="detail-row">
            <span class="detail-label">收货人</span>
            <span class="detail-value">{{ currentOrder.receiverName }} {{ currentOrder.receiverPhone }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">收货地址</span>
            <span class="detail-value">{{ currentOrder.addressDetail }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">订单金额</span>
            <span class="detail-value price">&yen;{{ formatPrice(currentOrder.totalPrice) }}</span>
          </div>
          <!-- 订单备注（有备注时显示） -->
          <div v-if="currentOrder.remark" class="detail-row">
            <span class="detail-label">订单备注</span>
            <span class="detail-value">{{ currentOrder.remark }}</span>
          </div>
          <!-- 商品清单 -->
          <div class="detail-row">
            <span class="detail-label">商品清单</span>
            <div class="product-list">
              <div v-for="item in currentOrder.items" :key="item.id" class="product-item">
                <el-image :src="item.productImage" style="width:60px;height:60px;float:left;margin-right:12px" fit="cover">
                  <template #error>
                    <div style="width:60px;height:60px;background:#f5f5f5;display:flex;align-items:center;justify-content:center">
                      <el-icon :size="20"><Picture /></el-icon>
                    </div>
                  </template>
                </el-image>
                <div>
                  <div class="product-name">{{ item.productName }}</div>
                  <div class="product-price">¥{{ formatPrice(item.price) }} x{{ item.quantity }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <template #footer>
          <el-button @click="showDetail = false">关闭</el-button>
        </template>
      </el-dialog>
  </div>
</template>

<script setup>
/**
 * OrderManage订单管理页面组件
 * 管理后台订单管理页面，支持订单列表查看、搜索、状态筛选、发货、完成订单和取消订单操作
 */
import { ref, reactive, onMounted } from 'vue'              // 引入Vue响应式API和生命周期钩子
import { Picture } from '@element-plus/icons-vue'           // 引入Element Plus图标
import { getAdminOrderList, updateOrderStatus, getOrderDetail } from '../../api/order' // 引入订单API
import { ElMessage } from 'element-plus'                    // 引入Element Plus消息提示

/**
 * 加载状态
 */
const loading = ref(false)
/**
 * 订单详情弹窗显示状态
 */
const showDetail = ref(false)
/**
 * 订单列表数据
 */
const orderList = ref([])
/**
 * 当前选中的订单详情
 */
const currentOrder = ref(null)

/**
 * 搜索表单数据
 */
const searchForm = reactive({
  orderNo: '',        // 订单号
  status: 'all'       // 订单状态筛选（all/pending/paid/shipped/completed）
})

/**
 * 分页数据
 */
const pagination = reactive({
  page: 1,            // 当前页码
  pageSize: 10,       // 每页数量
  total: 0            // 总记录数
})

/**
 * 统计数据
 */
const stats = reactive({
  total: 0,           // 订单总数
  totalSales: 0,      // 总销售额
  pending: 0,         // 待付款订单数
  paid: 0,            // 待发货订单数
  shipped: 0,         // 待收货订单数
  completed: 0        // 已完成订单数
})

/**
 * 格式化价格
 * @param {number} price - 价格
 * @returns {string} 格式化后的价格字符串
 */
function formatPrice(price) {
  return Number(price).toFixed(2)
}

/**
 * 获取订单状态中文描述
 * @param {string} status - 订单状态码
 * @returns {string} 状态中文描述
 */
function getStatusText(status) {
  const map = {
    pending: '待付款',
    paid: '待发货',
    shipped: '待收货',
    completed: '已完成',
    cancelled: '已取消'
  }
  return map[status] || status
}

/**
 * 获取订单状态对应的标签类型
 * @param {string} status - 订单状态码
 * @returns {string} Element Plus标签类型
 */
function getStatusType(status) {
  const map = {
    pending: '',       // 默认灰色
    paid: 'warning',   // 橙色警告
    shipped: 'primary', // 蓝色
    completed: 'success', // 绿色成功
    cancelled: 'info'   // 灰色信息
  }
  return map[status] || ''
}

/**
 * 加载订单列表
 * 根据搜索条件和分页参数获取订单数据
 */
async function loadOrders() {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      pageSize: pagination.pageSize
    }
    // 状态筛选条件
    if (searchForm.status !== 'all') {
      params.status = searchForm.status
    }
    const res = await getAdminOrderList(params)
    if (res.code === 200) {
      orderList.value = res.data?.list || []
      pagination.total = res.data?.total || 0
      stats.total = res.data?.total || 0
      stats.totalSales = res.data?.totalSales || 0
      // 统计各状态订单数
      stats.pending = orderList.value.filter(o => o.status === 'pending').length
      stats.paid = orderList.value.filter(o => o.status === 'paid').length
      stats.shipped = orderList.value.filter(o => o.status === 'shipped').length
      stats.completed = orderList.value.filter(o => o.status === 'completed').length
    }
  } catch (error) {
    console.error('Failed to load orders:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 查看订单详情
 * @param {Object} order - 订单对象
 */
async function viewDetail(order) {
  try {
    const res = await getOrderDetail(order.id)
    if (res.code === 200) {
      currentOrder.value = res.data
      showDetail.value = true
    }
  } catch (error) {
    console.error('Failed to get order detail:', error)
    ElMessage.error('获取订单详情失败')
  }
}

/**
 * 处理发货
 * 将订单状态更新为已发货
 * @param {number} orderId - 订单ID
 */
async function handleShip(orderId) {
  try {
    const res = await updateOrderStatus({ id: orderId, status: 'shipped' })
    if (res.code === 200) {
      ElMessage.success('发货成功')
      // 刷新订单列表
      await loadOrders()
    } else {
      ElMessage.error(res.message || '发货失败')
    }
  } catch (error) {
    console.error('Ship failed:', error)
    ElMessage.error('发货失败，请稍后重试')
  }
}

/**
 * 处理完成订单
 * 将订单状态更新为已完成
 * @param {number} orderId - 订单ID
 */
async function handleComplete(orderId) {
  try {
    const res = await updateOrderStatus({ id: orderId, status: 'completed' })
    if (res.code === 200) {
      ElMessage.success('订单已完成')
      // 刷新订单列表
      await loadOrders()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    console.error('Complete failed:', error)
    ElMessage.error('操作失败，请稍后重试')
  }
}

/**
 * 处理取消订单
 * 将订单状态更新为已取消
 * @param {number} orderId - 订单ID
 */
async function handleCancel(orderId) {
  try {
    const res = await updateOrderStatus({ id: orderId, status: 'cancelled' })
    if (res.code === 200) {
      ElMessage.success('订单已取消')
      // 刷新订单列表
      await loadOrders()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    console.error('Cancel failed:', error)
    ElMessage.error('操作失败，请稍后重试')
  }
}

/**
 * 页面挂载时加载订单列表
 */
onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
/**
 * 页面头部样式
 */
.page-header {
  display: flex;                                            /* flex布局 */
  align-items: center;                                      /* 垂直居中 */
  margin-bottom: 20px;                                      /* 下外边距20px */
}

/**
 * 页面标题样式
 */
.page-title {
  font-size: 20px;                                          /* 字号20px */
  color: var(--text-color);                                 /* 文字颜色 */
}

/**
 * 统计卡片网格样式（6列）
 */
.stats-card {
  display: grid;                                            /* grid布局 */
  grid-template-columns: repeat(6, 1fr);                    /* 6列等宽 */
  gap: 16px;                                                /* 间距16px */
  margin-bottom: 20px;                                      /* 下外边距20px */
}

/**
 * 统计项样式
 */
.stat-item {
  background: #fff;                                         /* 白色背景 */
  border-radius: var(--radius);                             /* 圆角 */
  padding: 16px;                                           /* 内边距16px */
  text-align: center;                                       /* 居中对齐 */
}

/**
 * 统计数值样式
 */
.stat-value {
  font-size: 24px;                                          /* 字号24px */
  font-weight: 700;                                         /* 字重700 */
  color: var(--text-color);                                 /* 文字颜色 */
  margin-bottom: 4px;                                       /* 下外边距4px */
}

/**
 * 销售额颜色（红色）
 */
.stat-value.sales {
  color: #f56c6c;                                          /* 红色 */
}

/**
 * 待付款状态颜色（红色）
 */
.stat-value.pending {
  color: #f56c6c;                                          /* 红色 */
}

/**
 * 待发货状态颜色（橙色）
 */
.stat-value.paid {
  color: #e6a23c;                                          /* 橙色 */
}

/**
 * 待收货状态颜色（蓝色）
 */
.stat-value.shipped {
  color: #409eff;                                          /* 蓝色 */
}

/**
 * 已完成状态颜色（绿色）
 */
.stat-value.completed {
  color: #67c23a;                                          /* 绿色 */
}

/**
 * 统计标签样式
 */
.stat-label {
  font-size: 13px;                                          /* 字号13px */
  color: var(--text-secondary);                             /* 次要文字颜色 */
}

/**
 * 搜索栏样式
 */
.search-bar {
  display: flex;                                            /* flex布局 */
  gap: 12px;                                                /* 间距12px */
  align-items: center;                                      /* 垂直居中 */
  margin-bottom: 20px;                                      /* 下外边距20px */
}

/**
 * 价格样式（红色高亮）
 */
.price {
  font-weight: 600;                                         /* 字重600 */
  color: #f56c6c;                                          /* 红色 */
}

/**
 * 分页组件样式（右对齐）
 */
.pagination {
  display: flex;                                            /* flex布局 */
  justify-content: flex-end;                                /* 右对齐 */
  margin-top: 20px;                                         /* 上外边距20px */
}

/**
 * 详情内容样式
 */
.detail-content {
  padding: 8px 0;                                          /* 上下内边距8px */
}

/**
 * 详情行样式
 */
.detail-row {
  display: flex;                                            /* flex布局 */
  padding: 8px 0;                                          /* 上下内边距8px */
  border-bottom: 1px solid var(--border-color);             /* 底部边框 */
}

/**
 * 详情标签样式
 */
.detail-label {
  width: 100px;                                            /* 固定宽度100px */
  font-size: 14px;                                          /* 字号14px */
  color: var(--text-secondary);                             /* 次要文字颜色 */
  flex-shrink: 0;                                          /* 不缩小 */
}

/**
 * 详情值样式
 */
.detail-value {
  flex: 1;                                                  /* 占满剩余空间 */
  font-size: 14px;                                          /* 字号14px */
  color: var(--text-color);                                 /* 文字颜色 */
}

/**
 * 详情价格样式
 */
.detail-value.price {
  font-size: 18px;                                          /* 字号18px */
  font-weight: 700;                                         /* 字重700 */
}

/**
 * 商品列表样式
 */
.product-list {
  flex: 1;                                                  /* 占满剩余空间 */
}

/**
 * 商品项样式
 */
.product-item {
  padding: 8px 0;                                          /* 上下内边距8px */
  overflow: hidden;                                         /* 溢出隐藏 */
}

/**
 * 商品名称样式
 */
.product-name {
  font-size: 14px;                                          /* 字号14px */
  color: var(--text-color);                                 /* 文字颜色 */
}

/**
 * 商品价格样式
 */
.product-price {
  font-size: 13px;                                          /* 字号13px */
  color: #f56c6c;                                          /* 红色 */
  margin-top: 4px;                                          /* 上外边距4px */
}

/**
 * 商品信息样式
 */
.product-info {
  margin-bottom: 8px;                                       /* 下外边距8px */
  overflow: hidden;                                         /* 溢出隐藏 */
}
</style>