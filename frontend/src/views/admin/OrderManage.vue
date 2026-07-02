<template>
  <div class="main-content">
    <div class="page-header">
        <h2 class="page-title">订单管理</h2>
      </div>

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

      <el-table :data="orderList" stripe style="width: 100%">
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="userId" label="用户ID" width="80" />
        <el-table-column prop="receiverName" label="收货人" width="100" />
        <el-table-column prop="receiverPhone" label="手机号" width="120" />
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
        <el-table-column prop="totalPrice" label="订单金额" width="120">
          <template #default="scope">
            <span class="price">&yen;{{ formatPrice(scope.row.totalPrice) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="viewDetail(scope.row)">查看详情</el-button>
            <el-button
              v-if="scope.row.status === 'paid'"
              type="primary"
              size="small"
              @click="handleShip(scope.row.id)"
            >
              发货
            </el-button>
            <el-button
              v-if="scope.row.status === 'shipped'"
              type="primary"
              size="small"
              @click="handleComplete(scope.row.id)"
            >
              完成订单
            </el-button>
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
          <div v-if="currentOrder.remark" class="detail-row">
            <span class="detail-label">订单备注</span>
            <span class="detail-value">{{ currentOrder.remark }}</span>
          </div>
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
import { ref, reactive, onMounted } from 'vue'
import { Picture } from '@element-plus/icons-vue'
import { getAdminOrderList, updateOrderStatus, getOrderDetail } from '../../api/order'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const showDetail = ref(false)
const orderList = ref([])
const currentOrder = ref(null)

const searchForm = reactive({
  orderNo: '',
  status: 'all'
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const stats = reactive({
  total: 0,
  totalSales: 0,
  pending: 0,
  paid: 0,
  shipped: 0,
  completed: 0
})

function formatPrice(price) {
  return Number(price).toFixed(2)
}

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

function getStatusType(status) {
  const map = {
    pending: '',
    paid: 'warning',
    shipped: 'primary',
    completed: 'success',
    cancelled: 'info'
  }
  return map[status] || ''
}

async function loadOrders() {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      pageSize: pagination.pageSize
    }
    if (searchForm.status !== 'all') {
      params.status = searchForm.status
    }
    const res = await getAdminOrderList(params)
    if (res.code === 200) {
      orderList.value = res.data?.list || []
      pagination.total = res.data?.total || 0
      stats.total = res.data?.total || 0
      stats.totalSales = res.data?.totalSales || 0
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

async function handleShip(orderId) {
  try {
    const res = await updateOrderStatus({ id: orderId, status: 'shipped' })
    if (res.code === 200) {
      ElMessage.success('发货成功')
      await loadOrders()
    } else {
      ElMessage.error(res.message || '发货失败')
    }
  } catch (error) {
    console.error('Ship failed:', error)
    ElMessage.error('发货失败，请稍后重试')
  }
}

async function handleComplete(orderId) {
  try {
    const res = await updateOrderStatus({ id: orderId, status: 'completed' })
    if (res.code === 200) {
      ElMessage.success('订单已完成')
      await loadOrders()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    console.error('Complete failed:', error)
    ElMessage.error('操作失败，请稍后重试')
  }
}

async function handleCancel(orderId) {
  try {
    const res = await updateOrderStatus({ id: orderId, status: 'cancelled' })
    if (res.code === 200) {
      ElMessage.success('订单已取消')
      await loadOrders()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    console.error('Cancel failed:', error)
    ElMessage.error('操作失败，请稍后重试')
  }
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.page-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  font-size: 20px;
  color: var(--text-color);
}

.stats-card {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}

.stat-item {
  background: #fff;
  border-radius: var(--radius);
  padding: 16px;
  text-align: center;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-color);
  margin-bottom: 4px;
}

.stat-value.sales {
  color: #f56c6c;
}

.stat-value.pending {
  color: #f56c6c;
}

.stat-value.paid {
  color: #e6a23c;
}

.stat-value.shipped {
  color: #409eff;
}

.stat-value.completed {
  color: #67c23a;
}

.stat-label {
  font-size: 13px;
  color: var(--text-secondary);
}

.search-bar {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 20px;
}

.price {
  font-weight: 600;
  color: #f56c6c;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.detail-content {
  padding: 8px 0;
}

.detail-row {
  display: flex;
  padding: 8px 0;
  border-bottom: 1px solid var(--border-color);
}

.detail-label {
  width: 100px;
  font-size: 14px;
  color: var(--text-secondary);
  flex-shrink: 0;
}

.detail-value {
  flex: 1;
  font-size: 14px;
  color: var(--text-color);
}

.detail-value.price {
  font-size: 18px;
  font-weight: 700;
}

.product-list {
  flex: 1;
}

.product-item {
  padding: 8px 0;
  overflow: hidden;
}

.product-name {
  font-size: 14px;
  color: var(--text-color);
}

.product-price {
  font-size: 13px;
  color: #f56c6c;
  margin-top: 4px;
}

.product-info {
  margin-bottom: 8px;
  overflow: hidden;
}
</style>