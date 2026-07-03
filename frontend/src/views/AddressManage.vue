<template>
  <!-- 收货地址管理页面组件 -->
  <div class="address-manage page-container">
    <div class="main-width">
      <!-- 页面头部（标题和添加按钮） -->
      <div class="page-header">
        <h2 class="page-title">收货地址</h2>
        <el-button type="primary" @click="showAddDialog = true">
          <el-icon><Plus /></el-icon>
          添加地址
        </el-button>
      </div>

      <!-- 骨架屏加载组件 -->
      <el-skeleton :loading="loading" animated>
        <!-- 骨架屏模板 -->
        <template #template>
          <div v-for="i in 3" :key="i" class="skeleton-item">
            <el-skeleton-item variant="h3" style="width:30%" />
            <el-skeleton-item variant="p" style="width:50%" />
            <el-skeleton-item variant="p" style="width:70%" />
            <div style="display:flex;gap:8px;margin-top:8px">
              <el-skeleton-item variant="button" style="width:80px" />
              <el-skeleton-item variant="button" style="width:80px" />
            </div>
          </div>
        </template>
        <!-- 实际内容 -->
        <template #default>
          <!-- 无地址状态 -->
          <div v-if="addressList.length === 0 && !loading" class="empty-address">
            <el-empty description="暂无收货地址" />
            <el-button type="primary" @click="showAddDialog = true">添加收货地址</el-button>
          </div>

          <!-- 地址列表 -->
          <div v-else class="address-list">
            <div v-for="addr in addressList" :key="addr.id" class="address-card">
              <!-- 地址头部（姓名、电话、默认标签） -->
              <div class="address-header">
                <span class="address-name">{{ addr.name }}</span>
                <span class="address-phone">{{ addr.phone }}</span>
                <el-tag v-if="addr.isDefault" type="success" size="small">默认</el-tag>
              </div>
              <!-- 详细地址 -->
              <div class="address-detail">
                {{ addr.province }}{{ addr.city }}{{ addr.district }}{{ addr.detail }}
              </div>
              <!-- 操作按钮（编辑、删除、设为默认） -->
              <div class="address-actions">
                <el-button type="text" @click="editAddress(addr)">编辑</el-button>
                <el-button type="text" @click="deleteAddress(addr.id)" v-if="!addr.isDefault">删除</el-button>
                <el-button type="text" @click="setDefault(addr.id)" v-if="!addr.isDefault">设为默认</el-button>
              </div>
            </div>
          </div>
        </template>
      </el-skeleton>
    </div>

    <!-- 添加/编辑地址弹窗 -->
    <el-dialog :title="isEdit ? '编辑地址' : '添加地址'" v-model="showAddDialog" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="收货人" prop="name">
          <el-input v-model="form.name" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="手机号码" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号码" />
        </el-form-item>
        <el-form-item label="省份" prop="province">
          <el-input v-model="form.province" placeholder="请输入省份" />
        </el-form-item>
        <el-form-item label="城市" prop="city">
          <el-input v-model="form.city" placeholder="请输入城市" />
        </el-form-item>
        <el-form-item label="区县" prop="district">
          <el-input v-model="form.district" placeholder="请输入区县" />
        </el-form-item>
        <el-form-item label="详细地址" prop="detail">
          <el-input v-model="form.detail" type="textarea" :rows="2" placeholder="请输入详细地址" />
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="form.isDefault">设为默认地址</el-checkbox>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="saveAddress" :loading="saving">
          <template #loading>保存中...</template>
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * AddressManage收货地址管理页面组件
 * 管理用户收货地址，支持添加、编辑、删除和设为默认地址功能
 */
import { ref, onMounted } from 'vue'                         // 引入Vue响应式API和生命周期钩子
import { Plus } from '@element-plus/icons-vue'              // 引入Element Plus图标
import { getAddressList, addAddress, updateAddress, deleteAddress as deleteAddressApi } from '../api/address' // 引入地址API
import { ElMessage } from 'element-plus'                     // 引入Element Plus消息提示

/**
 * 加载状态
 */
const loading = ref(true)
/**
 * 保存加载状态
 */
const saving = ref(false)
/**
 * 添加地址弹窗显示状态
 */
const showAddDialog = ref(false)
/**
 * 是否为编辑模式
 */
const isEdit = ref(false)
/**
 * 表单引用
 */
const formRef = ref(null)
/**
 * 地址列表数据
 */
const addressList = ref([])

/**
 * 地址表单数据
 */
const form = ref({
  id: null,              // 地址ID（新增时为null）
  name: '',              // 收货人姓名
  phone: '',             // 手机号码
  province: '',          // 省份
  city: '',              // 城市
  district: '',          // 区县
  detail: '',            // 详细地址
  isDefault: false       // 是否为默认地址
})

/**
 * 表单验证规则
 */
const rules = {
  name: [{ required: true, message: '请输入收货人姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号码', trigger: 'blur' }, { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }],
  province: [{ required: true, message: '请输入省份', trigger: 'blur' }],
  city: [{ required: true, message: '请输入城市', trigger: 'blur' }],
  district: [{ required: true, message: '请输入区县', trigger: 'blur' }],
  detail: [{ required: true, message: '请输入详细地址', trigger: 'blur' }]
}

/**
 * 加载地址列表
 * 从后端获取用户所有收货地址
 */
async function loadAddresses() {
  try {
    const res = await getAddressList()
    if (res.code === 200) {
      addressList.value = res.data || []
    }
  } catch (error) {
    console.error('Failed to load addresses:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 添加新地址
 * 重置表单并打开添加地址弹窗
 */
function addNewAddress() {
  isEdit.value = false
  form.value = {
    id: null,
    name: '',
    phone: '',
    province: '',
    city: '',
    district: '',
    detail: '',
    isDefault: false
  }
  showAddDialog.value = true
}

/**
 * 编辑地址
 * 将指定地址数据填充到表单并打开编辑弹窗
 * @param {Object} addr - 地址对象
 */
function editAddress(addr) {
  isEdit.value = true
  form.value = {
    id: addr.id,
    name: addr.name,
    phone: addr.phone,
    province: addr.province || '',
    city: addr.city || '',
    district: addr.district || '',
    detail: addr.detail || '',
    isDefault: addr.isDefault || false
  }
  showAddDialog.value = true
}

/**
 * 保存地址
 * 根据模式（新增/编辑）调用不同的API
 */
async function saveAddress() {
  if (!formRef.value) return
  
  // 表单验证
  await formRef.value.validate(async (valid) => {
    if (!valid) return

    saving.value = true

    try {
      if (isEdit.value) {
        // 编辑模式：更新地址
        const res = await updateAddress(form.value)
        if (res.code === 200) {
          ElMessage.success('地址更新成功')
        } else {
          ElMessage.error(res.message || '更新失败')
        }
      } else {
        // 新增模式：添加地址
        const res = await addAddress(form.value)
        if (res.code === 200) {
          ElMessage.success('地址添加成功')
        } else {
          ElMessage.error(res.message || '添加失败')
        }
      }
      // 关闭弹窗并刷新地址列表
      showAddDialog.value = false
      await loadAddresses()
    } catch (error) {
      console.error('Save address failed:', error)
      ElMessage.error('保存失败，请稍后重试')
    } finally {
      saving.value = false
    }
  })
}

/**
 * 删除地址
 * @param {number} id - 地址ID
 */
async function deleteAddress(id) {
  try {
    const res = await deleteAddressApi(id)
    if (res.code === 200) {
      ElMessage.success('地址已删除')
      // 刷新地址列表
      await loadAddresses()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error) {
    console.error('Delete address failed:', error)
    ElMessage.error('删除失败，请稍后重试')
  }
}

/**
 * 设置默认地址
 * @param {number} id - 地址ID
 */
async function setDefault(id) {
  try {
    const addr = addressList.value.find(item => item.id === id)
    if (addr) {
      addr.isDefault = true
      const res = await updateAddress(addr)
      if (res.code === 200) {
        ElMessage.success('已设为默认地址')
        // 刷新地址列表
        await loadAddresses()
      } else {
        ElMessage.error(res.message || '操作失败')
      }
    }
  } catch (error) {
    console.error('Set default failed:', error)
    ElMessage.error('操作失败，请稍后重试')
  }
}

/**
 * 页面挂载时加载地址列表
 */
onMounted(() => {
  loadAddresses()
})
</script>

<style scoped>
/**
 * 页面头部样式
 */
.page-header {
  display: flex;                                            /* flex布局 */
  align-items: center;                                      /* 垂直居中 */
  justify-content: space-between;                            /* 两端对齐 */
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
 * 无地址状态样式
 */
.empty-address {
  text-align: center;                                       /* 居中对齐 */
  padding: 60px 0;                                          /* 上下内边距60px */
}

/**
 * 无地址状态下按钮样式
 */
.empty-address .el-button {
  margin-top: 16px;                                         /* 上外边距16px */
}

/**
 * 地址列表样式
 */
.address-list {
  display: flex;                                            /* flex布局 */
  flex-direction: column;                                   /* 垂直排列 */
  gap: 16px;                                                /* 间距16px */
}

/**
 * 地址卡片样式
 */
.address-card {
  background: #fff;                                         /* 白色背景 */
  border-radius: var(--radius);                             /* 圆角 */
  padding: 16px;                                           /* 内边距16px */
  border: 1px solid var(--border-color);                    /* 边框 */
}

/**
 * 地址头部样式（姓名、电话、默认标签）
 */
.address-header {
  display: flex;                                            /* flex布局 */
  align-items: center;                                      /* 垂直居中 */
  gap: 12px;                                                /* 间距12px */
  margin-bottom: 8px;                                       /* 下外边距8px */
}

/**
 * 收货人姓名样式
 */
.address-name {
  font-size: 16px;                                          /* 字号16px */
  font-weight: 600;                                         /* 字重600 */
  color: var(--text-color);                                 /* 文字颜色 */
}

/**
 * 手机号码样式
 */
.address-phone {
  font-size: 14px;                                          /* 字号14px */
  color: var(--text-secondary);                             /* 次要文字颜色 */
}

/**
 * 详细地址样式
 */
.address-detail {
  font-size: 14px;                                          /* 字号14px */
  color: var(--text-color);                                 /* 文字颜色 */
  line-height: 1.5;                                         /* 行高1.5 */
  margin-bottom: 12px;                                      /* 下外边距12px */
}

/**
 * 地址操作按钮样式
 */
.address-actions {
  display: flex;                                            /* flex布局 */
  gap: 16px;                                                /* 间距16px */
}

/**
 * 操作按钮文字样式
 */
.address-actions .el-button {
  font-size: 13px;                                          /* 字号13px */
}

/**
 * 骨架屏项样式
 */
.skeleton-item {
  background: #fff;                                         /* 白色背景 */
  border-radius: var(--radius);                             /* 圆角 */
  padding: 16px;                                           /* 内边距16px */
  margin-bottom: 16px;                                      /* 下外边距16px */
}
</style>