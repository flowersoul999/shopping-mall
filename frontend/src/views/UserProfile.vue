<template>
  <!-- 用户个人中心页面组件 -->
  <div class="profile page-container">
    <div class="main-width">
      <!-- 用户信息头部（头像、用户名、编辑按钮） -->
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

      <!-- 订单统计卡片（全部、待付款、待发货、待收货） -->
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

      <!-- 菜单区域 -->
      <div class="menu-section">
        <!-- 我的订单菜单组 -->
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

        <!-- 账户管理菜单组 -->
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

        <!-- 其他菜单组 -->
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

        <!-- 退出登录区域 -->
        <div class="logout-section">
          <el-button type="danger" @click="handleLogout">
            <el-icon><SwitchButton /></el-icon>
            退出登录
          </el-button>
        </div>
      </div>
    </div>

    <!-- 编辑资料弹窗 -->
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

    <!-- 修改密码弹窗 -->
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
/**
 * UserProfile用户个人中心页面组件
 * 展示用户基本信息、订单统计、功能菜单，支持编辑资料、修改密码、退出登录等操作
 */
import { ref, reactive, computed, onMounted } from 'vue'        // 引入Vue响应式API和生命周期钩子
import { useRouter } from 'vue-router'                         // 引入路由相关API
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
} from '@element-plus/icons-vue'                              // 引入Element Plus图标
import { useUserStore } from '../store/user'                   // 引入用户状态管理
import { useCartStore } from '../store/cart'                   // 引入购物车状态管理
import { updateUserInfo } from '../api/user'                   // 引入用户API
import { getOrderList } from '../api/order'                    // 引入订单API
import { ElMessage } from 'element-plus'                       // 引入Element Plus消息提示

// 获取路由实例和状态管理
const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()

/**
 * 编辑资料弹窗显示状态
 */
const showEditDialog = ref(false)
/**
 * 修改密码弹窗显示状态
 */
const showPasswordDialog = ref(false)
/**
 * 保存资料加载状态
 */
const saving = ref(false)
/**
 * 修改密码加载状态
 */
const changingPassword = ref(false)
/**
 * 编辑资料表单引用
 */
const editFormRef = ref(null)
/**
 * 修改密码表单引用
 */
const passwordFormRef = ref(null)

/**
 * 用户信息（从状态管理获取）
 */
const userInfo = computed(() => userStore.userInfo)

/**
 * 订单统计数据
 */
const orderStats = ref({
  total: 0,           // 全部订单数
  pending: 0,         // 待付款订单数
  paid: 0,            // 待发货订单数
  shipped: 0,         // 待收货订单数
  completed: 0        // 已完成订单数
})

/**
 * 编辑资料表单数据
 */
const editForm = reactive({
  username: '',       // 用户名（不可修改）
  email: '',          // 邮箱
  phone: '',          // 手机号
  nickname: ''        // 昵称
})

/**
 * 编辑资料表单验证规则
 */
const editRules = {
  email: [{ type: 'email', message: '请输入正确的邮箱', trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }]
}

/**
 * 修改密码表单数据
 */
const passwordForm = reactive({
  oldPassword: '',        // 旧密码
  newPassword: '',        // 新密码
  confirmPassword: ''     // 确认密码
})

/**
 * 修改密码表单验证规则
 */
const passwordRules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }, { min: 6, message: '密码至少6位', trigger: 'blur' }],
  confirmPassword: [{ required: true, message: '请确认新密码', trigger: 'blur' }, { validator: (rule, value, callback) => { if (value !== passwordForm.newPassword) callback(new Error('两次输入的密码不一致')); else callback() }, trigger: 'blur' }]
}

/**
 * 跳转到订单列表页（支持状态筛选）
 * @param {string} status - 订单状态（all/pending/paid/shipped/completed）
 */
function goToOrders(status) {
  router.push(`/orders${status !== 'all' ? '?status=' + status : ''}`)
}

/**
 * 跳转到地址管理页
 */
function goToAddress() {
  router.push('/address')
}

/**
 * 跳转到购物车页
 */
function goToCart() {
  router.push('/cart')
}

/**
 * 处理退出登录
 */
function handleLogout() {
  userStore.logout()
}

/**
 * 加载订单统计数据
 * 通过获取订单列表计算各状态订单数量
 */
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

/**
 * 初始化编辑资料表单
 * 将用户当前信息填充到表单中
 */
function initEditForm() {
  editForm.username = userInfo.value?.username || ''
  editForm.email = userInfo.value?.email || ''
  editForm.phone = userInfo.value?.phone || ''
  editForm.nickname = userInfo.value?.nickname || ''
}

/**
 * 保存用户资料
 */
async function saveProfile() {
  if (!editFormRef.value) return

  // 表单验证
  await editFormRef.value.validate(async (valid) => {
    if (!valid) return

    saving.value = true

    try {
      const res = await updateUserInfo(editForm)
      if (res.code === 200) {
        ElMessage.success('资料更新成功')
        // 刷新用户信息
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

/**
 * 修改密码
 */
async function changePassword() {
  if (!passwordFormRef.value) return

  // 表单验证
  await passwordFormRef.value.validate(async (valid) => {
    if (!valid) return

    changingPassword.value = true

    try {
      const res = await updateUserInfo(passwordForm)
      if (res.code === 200) {
        ElMessage.success('密码修改成功，请重新登录')
        // 修改成功后退出登录
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

/**
 * 页面挂载时加载订单统计和初始化表单
 */
onMounted(() => {
  loadOrderStats()
  initEditForm()
})
</script>

<style scoped>
/**
 * 用户信息头部样式（渐变背景）
 */
.profile-header {
  display: flex;                                            /* flex布局 */
  align-items: center;                                      /* 垂直居中 */
  justify-content: space-between;                            /* 两端对齐 */
  background: linear-gradient(135deg, var(--primary) 0%, var(--primary-dark) 100%); /* 渐变背景 */
  border-radius: var(--radius);                             /* 圆角 */
  padding: 24px;                                           /* 内边距24px */
  margin-bottom: 20px;                                      /* 下外边距20px */
  color: #fff;                                              /* 文字白色 */
}

/**
 * 头像区域样式
 */
.avatar-section {
  display: flex;                                            /* flex布局 */
  align-items: center;                                      /* 垂直居中 */
  gap: 16px;                                                /* 间距16px */
}

/**
 * 头像样式（圆形）
 */
.avatar {
  width: 80px;                                             /* 宽度80px */
  height: 80px;                                            /* 高度80px */
  background: rgba(255, 255, 255, 0.2);                    /* 半透明白色背景 */
  border-radius: 50%;                                       /* 圆形 */
  display: flex;                                            /* flex布局 */
  align-items: center;                                      /* 垂直居中 */
  justify-content: center;                                  /* 水平居中 */
}

/**
 * 用户名样式
 */
.user-name {
  font-size: 24px;                                          /* 字号24px */
  font-weight: 700;                                         /* 字重700 */
  margin: 0;                                                /* 无外边距 */
}

/**
 * 用户角色样式
 */
.user-role {
  font-size: 14px;                                          /* 字号14px */
  opacity: 0.8;                                            /* 透明度80% */
  margin: 4px 0 0;                                          /* 上外边距4px */
}

/**
 * 编辑按钮样式（透明背景）
 */
.edit-btn .el-button {
  background: rgba(255, 255, 255, 0.2);                    /* 半透明白色背景 */
  border-color: rgba(255, 255, 255, 0.4);                   /* 半透明白色边框 */
  color: #fff;                                              /* 文字白色 */
}

/**
 * 编辑按钮悬停样式
 */
.edit-btn .el-button:hover {
  background: rgba(255, 255, 255, 0.3);                    /* 悬停时透明度增加 */
}

/**
 * 订单统计行样式（4列网格）
 */
.stats-row {
  display: grid;                                            /* grid布局 */
  grid-template-columns: repeat(4, 1fr);                    /* 4列等宽 */
  gap: 16px;                                                /* 间距16px */
  margin-bottom: 20px;                                      /* 下外边距20px */
}

/**
 * 统计项样式
 */
.stat-item {
  background: #fff;                                         /* 白色背景 */
  border-radius: var(--radius);                             /* 圆角 */
  padding: 20px;                                           /* 内边距20px */
  text-align: center;                                       /* 居中对齐 */
  cursor: pointer;                                          /* 鼠标指针 */
  transition: transform 0.2s, box-shadow 0.2s;              /* 过渡动画 */
}

/**
 * 统计项悬停样式（上浮+阴影）
 */
.stat-item:hover {
  transform: translateY(-2px);                              /* 向上移动2px */
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);              /* 添加阴影 */
}

/**
 * 统计数值样式
 */
.stat-value {
  font-size: 28px;                                          /* 字号28px */
  font-weight: 700;                                         /* 字重700 */
  color: var(--text-color);                                 /* 文字颜色 */
  margin-bottom: 4px;                                       /* 下外边距4px */
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
 * 统计标签样式
 */
.stat-label {
  font-size: 14px;                                          /* 字号14px */
  color: var(--text-secondary);                             /* 次要文字颜色 */
}

/**
 * 菜单区域样式
 */
.menu-section {
  display: flex;                                            /* flex布局 */
  flex-direction: column;                                   /* 垂直排列 */
  gap: 16px;                                                /* 间距16px */
}

/**
 * 菜单组样式
 */
.menu-group {
  background: #fff;                                         /* 白色背景 */
  border-radius: var(--radius);                             /* 圆角 */
  overflow: hidden;                                         /* 溢出隐藏 */
}

/**
 * 菜单标题样式
 */
.menu-title {
  font-size: 14px;                                          /* 字号14px */
  font-weight: 600;                                         /* 字重600 */
  color: var(--text-secondary);                             /* 次要文字颜色 */
  padding: 12px 16px;                                       /* 内边距12px 16px */
  margin: 0;                                                /* 无外边距 */
  border-bottom: 1px solid var(--border-color);             /* 底部边框 */
}

/**
 * 菜单列表样式
 */
.menu-list {
  padding: 8px 0;                                          /* 上下内边距8px */
}

/**
 * 菜单项样式
 */
.menu-item {
  display: flex;                                            /* flex布局 */
  align-items: center;                                      /* 垂直居中 */
  padding: 14px 16px;                                       /* 内边距14px 16px */
  cursor: pointer;                                          /* 鼠标指针 */
  transition: background 0.2s;                              /* 背景过渡 */
  gap: 12px;                                                /* 间距12px */
}

/**
 * 菜单项悬停样式
 */
.menu-item:hover {
  background: #f5f7fa;                                     /* 浅灰色背景 */
}

/**
 * 菜单项文字样式
 */
.menu-item span:first-child {
  flex: 1;                                                  /* 占满剩余空间 */
  font-size: 14px;                                          /* 字号14px */
  color: var(--text-color);                                 /* 文字颜色 */
}

/**
 * 菜单项徽章样式（数量角标）
 */
.menu-item .badge {
  background: #f56c6c;                                      /* 红色背景 */
  color: #fff;                                              /* 白色文字 */
  font-size: 12px;                                          /* 字号12px */
  padding: 2px 6px;                                        /* 内边距2px 6px */
  border-radius: 10px;                                      /* 圆角 */
}

/**
 * 退出登录区域样式
 */
.logout-section {
  background: #fff;                                         /* 白色背景 */
  border-radius: var(--radius);                             /* 圆角 */
  padding: 16px;                                           /* 内边距16px */
  text-align: center;                                       /* 居中对齐 */
}
</style>