<template>
  <div class="profile page-container">
    <div class="main-width">
      <div class="profile-header">
        <div class="avatar-section">
          <div class="avatar">
            <el-icon :size="48"><User /></el-icon>
          </div>
          <div class="user-info">
            <h2 class="user-name">{{ userInfo?.username || '用户' }}</h2>
            <p class="user-role">{{ userInfo?.role === 'admin' ? '管理员' : '普通用户' }}</p>
          </div>
        </div>
        <div class="edit-btn">
          <el-button type="primary" @click="showEditDialog = true">
            <el-icon><Edit /></el-icon>
            编辑资料
          </el-button>
        </div>
      </div>

      <div class="stats-row">
        <div class="stat-item" @click="goToOrders('all')">
          <div class="stat-value">{{ orderStats.total }}</div>
          <div class="stat-label">全部订单</div>
        </div>
        <div class="stat-item" @click="goToOrders('pending')">
          <div class="stat-value pending">{{ orderStats.pending }}</div>
          <div class="stat-label">待付款</div>
        </div>
        <div class="stat-item" @click="goToOrders('paid')">
          <div class="stat-value paid">{{ orderStats.paid }}</div>
          <div class="stat-label">待发货</div>
        </div>
        <div class="stat-item" @click="goToOrders('shipped')">
          <div class="stat-value shipped">{{ orderStats.shipped }}</div>
          <div class="stat-label">待收货</div>
        </div>
      </div>

      <div class="menu-section">
        <div class="menu-group">
          <h3 class="menu-title">我的订单</h3>
          <div class="menu-list">
            <div class="menu-item" @click="goToOrders('all')">
              <el-icon><ShoppingBag /></el-icon>
              <span>全部订单</span>
              <el-icon><ArrowRight /></el-icon>
            </div>
            <div class="menu-item" @click="goToOrders('pending')">
              <el-icon><Clock /></el-icon>
              <span>待付款</span>
              <span v-if="orderStats.pending > 0" class="badge">{{ orderStats.pending }}</span>
              <el-icon><ArrowRight /></el-icon>
            </div>
            <div class="menu-item" @click="goToOrders('paid')">
              <el-icon><Box /></el-icon>
              <span>待发货</span>
              <span v-if="orderStats.paid > 0" class="badge">{{ orderStats.paid }}</span>
              <el-icon><ArrowRight /></el-icon>
            </div>
            <div class="menu-item" @click="goToOrders('shipped')">
              <el-icon><Ship /></el-icon>
              <span>待收货</span>
              <span v-if="orderStats.shipped > 0" class="badge">{{ orderStats.shipped }}</span>
              <el-icon><ArrowRight /></el-icon>
            </div>
            <div class="menu-item" @click="goToOrders('completed')">
              <el-icon><CircleCheck /></el-icon>
              <span>已完成</span>
              <el-icon><ArrowRight /></el-icon>
            </div>
          </div>
        </div>

        <div class="menu-group">
          <h3 class="menu-title">账户管理</h3>
          <div class="menu-list">
            <div class="menu-item" @click="goToAddress">
              <el-icon><MapLocation /></el-icon>
              <span>收货地址</span>
              <el-icon><ArrowRight /></el-icon>
            </div>
            <div class="menu-item" @click="showPasswordDialog = true">
              <el-icon><Lock /></el-icon>
              <span>修改密码</span>
              <el-icon><ArrowRight /></el-icon>
            </div>
            <div class="menu-item" @click="goToCart">
              <el-icon><ShoppingCart /></el-icon>
              <span>购物车</span>
              <el-icon><ArrowRight /></el-icon>
            </div>
          </div>
        </div>

        <div class="menu-group">
          <h3 class="menu-title">其他</h3>
          <div class="menu-list">
            <div class="menu-item">
              <el-icon><Help /></el-icon>
              <span>帮助中心</span>
              <el-icon><ArrowRight /></el-icon>
            </div>
            <div class="menu-item">
              <el-icon><Setting /></el-icon>
              <span>设置</span>
              <el-icon><ArrowRight /></el-icon>
            </div>
          </div>
        </div>

        <div class="logout-section">
          <el-button type="danger" @click="handleLogout">
            <el-icon><SwitchButton /></el-icon>
            退出登录
          </el-button>
        </div>
      </div>
    </div>

    <el-dialog title="编辑资料" v-model="showEditDialog" width="400px">
      <el-form :model="editForm" :rules="editRules" ref="editFormRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="editForm.username" disabled />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="editForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="editForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="editForm.nickname" placeholder="请输入昵称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" @click="saveProfile" :loading="saving">
          <template #loading>保存中...</template>
          保存
        </el-button>
      </template>
    </el-dialog>

    <el-dialog title="修改密码" v-model="showPasswordDialog" width="400px">
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="100px">
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入旧密码" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请确认新密码" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPasswordDialog = false">取消</el-button>
        <el-button type="primary" @click="changePassword" :loading="changingPassword">
          <template #loading>修改中...</template>
          修改密码
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  User,
  Edit,
  ShoppingBag,
  Clock,
  Box,
  Ship,
  CircleCheck,
  MapLocation,
  Lock,
  ShoppingCart,
  Help,
  Setting,
  SwitchButton,
  ArrowRight
} from '@element-plus/icons-vue'
import { useUserStore } from '../store/user'
import { useCartStore } from '../store/cart'
import { updateUserInfo } from '../api/user'
import { getOrderList } from '../api/order'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()

const showEditDialog = ref(false)
const showPasswordDialog = ref(false)
const saving = ref(false)
const changingPassword = ref(false)
const editFormRef = ref(null)
const passwordFormRef = ref(null)

const userInfo = computed(() => userStore.userInfo)

const orderStats = ref({
  total: 0,
  pending: 0,
  paid: 0,
  shipped: 0,
  completed: 0
})

const editForm = reactive({
  username: '',
  email: '',
  phone: '',
  nickname: ''
})

const editRules = {
  email: [{ type: 'email', message: '请输入正确的邮箱', trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }]
}

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }, { min: 6, message: '密码至少6位', trigger: 'blur' }],
  confirmPassword: [{ required: true, message: '请确认新密码', trigger: 'blur' }, { validator: (rule, value, callback) => { if (value !== passwordForm.newPassword) callback(new Error('两次输入的密码不一致')); else callback() }, trigger: 'blur' }]
}

function goToOrders(status) {
  router.push(`/orders${status !== 'all' ? '?status=' + status : ''}`)
}

function goToAddress() {
  router.push('/address')
}

function goToCart() {
  router.push('/cart')
}

function handleLogout() {
  userStore.logout()
}

async function loadOrderStats() {
  try {
    const res = await getOrderList({ page: 1, pageSize: 100 })
    if (res.code === 200) {
      const list = res.data?.list || []
      orderStats.value = {
        total: list.length,
        pending: list.filter(o => o.status === 'pending').length,
        paid: list.filter(o => o.status === 'paid').length,
        shipped: list.filter(o => o.status === 'shipped').length,
        completed: list.filter(o => o.status === 'completed').length
      }
    }
  } catch (error) {
    console.error('Failed to load order stats:', error)
  }
}

function initEditForm() {
  editForm.username = userInfo.value?.username || ''
  editForm.email = userInfo.value?.email || ''
  editForm.phone = userInfo.value?.phone || ''
  editForm.nickname = userInfo.value?.nickname || ''
}

async function saveProfile() {
  if (!editFormRef.value) return

  await editFormRef.value.validate(async (valid) => {
    if (!valid) return

    saving.value = true

    try {
      const res = await updateUserInfo(editForm)
      if (res.code === 200) {
        ElMessage.success('资料更新成功')
        await userStore.fetchUserInfo()
        showEditDialog.value = false
      } else {
        ElMessage.error(res.message || '更新失败')
      }
    } catch (error) {
      console.error('Update profile failed:', error)
      ElMessage.error('更新失败，请稍后重试')
    } finally {
      saving.value = false
    }
  })
}

async function changePassword() {
  if (!passwordFormRef.value) return

  await passwordFormRef.value.validate(async (valid) => {
    if (!valid) return

    changingPassword.value = true

    try {
      const res = await updateUserInfo(passwordForm)
      if (res.code === 200) {
        ElMessage.success('密码修改成功，请重新登录')
        userStore.logout()
      } else {
        ElMessage.error(res.message || '修改失败')
      }
    } catch (error) {
      console.error('Change password failed:', error)
      ElMessage.error('修改失败，请稍后重试')
    } finally {
      changingPassword.value = false
    }
  })
}

onMounted(() => {
  loadOrderStats()
  initEditForm()
})
</script>

<style scoped>
.profile-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: linear-gradient(135deg, var(--primary) 0%, var(--primary-dark) 100%);
  border-radius: var(--radius);
  padding: 24px;
  margin-bottom: 20px;
  color: #fff;
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 16px;
}

.avatar {
  width: 80px;
  height: 80px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-name {
  font-size: 24px;
  font-weight: 700;
  margin: 0;
}

.user-role {
  font-size: 14px;
  opacity: 0.8;
  margin: 4px 0 0;
}

.edit-btn .el-button {
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.4);
  color: #fff;
}

.edit-btn .el-button:hover {
  background: rgba(255, 255, 255, 0.3);
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}

.stat-item {
  background: #fff;
  border-radius: var(--radius);
  padding: 20px;
  text-align: center;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.stat-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-color);
  margin-bottom: 4px;
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

.stat-label {
  font-size: 14px;
  color: var(--text-secondary);
}

.menu-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.menu-group {
  background: #fff;
  border-radius: var(--radius);
  overflow: hidden;
}

.menu-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-secondary);
  padding: 12px 16px;
  margin: 0;
  border-bottom: 1px solid var(--border-color);
}

.menu-list {
  padding: 8px 0;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 14px 16px;
  cursor: pointer;
  transition: background 0.2s;
  gap: 12px;
}

.menu-item:hover {
  background: #f5f7fa;
}

.menu-item span:first-child {
  flex: 1;
  font-size: 14px;
  color: var(--text-color);
}

.menu-item .badge {
  background: #f56c6c;
  color: #fff;
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 10px;
}

.logout-section {
  background: #fff;
  border-radius: var(--radius);
  padding: 16px;
  text-align: center;
}
</style>