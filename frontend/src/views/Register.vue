<template>
  <!-- 注册页面组件 -->
  <div class="register page-container">
    <!-- 注册卡片 -->
    <div class="auth-card">
      <h2>注册</h2>
      <!-- 注册表单 -->
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="0"
        size="large"
        @submit.prevent="handleRegister"
      >
        <!-- 用户名输入框 -->
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="用户名"
            :prefix-icon="User"
          />
        </el-form-item>
        <!-- 密码输入框 -->
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <!-- 确认密码输入框 -->
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="确认密码"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <!-- 注册按钮 -->
        <el-form-item>
          <el-button
            type="primary"
            native-type="submit"
            :loading="loading"
            class="submit-btn"
          >
            注册
          </el-button>
        </el-form-item>
      </el-form>
      <!-- 登录链接 -->
      <div class="auth-link">
        已有账号？
        <router-link to="/login">立即登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * Register注册页面组件
 * 提供用户注册功能，包含用户名、密码和确认密码输入框，以及表单验证
 */
import { ref, reactive } from 'vue'                       // 引入Vue响应式API
import { User, Lock } from '@element-plus/icons-vue'      // 引入Element Plus图标
import { useUserStore } from '../store/user'              // 引入用户状态管理

// 获取用户store实例
const userStore = useUserStore()
// 表单引用
const formRef = ref(null)
// 加载状态
const loading = ref(false)

/**
 * 表单数据对象
 */
const form = reactive({
  username: '',                                            // 用户名
  password: '',                                             // 密码
  confirmPassword: ''                                       // 确认密码
})

/**
 * 自定义验证函数：验证确认密码是否与密码一致
 * @param {Object} rule - 验证规则
 * @param {string} value - 确认密码值
 * @param {Function} callback - 回调函数
 */
const validateConfirm = (rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

/**
 * 表单验证规则
 */
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度3-20位', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirm, trigger: 'blur' }
  ]
}

/**
 * 处理注册表单提交
 * 先验证表单，验证通过后调用用户store的register方法
 */
async function handleRegister() {
  // 验证表单，失败时返回false
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  // 设置加载状态
  loading.value = true
  // 调用用户store的register方法
  await userStore.register({
    username: form.username,
    password: form.password
  })
  // 恢复加载状态
  loading.value = false
}
</script>

<style scoped>
/**
 * 注册页面主样式
 */
.register {
  display: flex;                                            /* flex布局 */
  justify-content: center;                                  /* 水平居中 */
  align-items: center;                                      /* 垂直居中 */
}

/**
 * 注册卡片样式
 */
.auth-card {
  width: 400px;                                            /* 固定宽度 */
  padding: 32px;                                           /* 内边距 */
  background: #fff;                                         /* 白色背景 */
  border-radius: var(--radius);                             /* 圆角 */
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);               /* 阴影效果 */
}

/**
 * 卡片标题样式
 */
.auth-card h2 {
  text-align: center;                                       /* 居中对齐 */
  font-size: 22px;                                          /* 字号22px */
  color: var(--text-color);                                 /* 文字颜色 */
  margin-bottom: 24px;                                      /* 下外边距 */
}

/**
 * 提交按钮样式
 */
.submit-btn {
  width: 100%;                                              /* 宽度100% */
}

/**
 * 登录链接样式
 */
.auth-link {
  text-align: center;                                       /* 居中对齐 */
  font-size: 13px;                                          /* 字号13px */
  color: var(--text-secondary);                             /* 次要文字颜色 */
}

/**
 * 链接颜色
 */
.auth-link a {
  color: var(--primary);                                    /* 主题色 */
}
</style>
