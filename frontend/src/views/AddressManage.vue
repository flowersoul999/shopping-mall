<template>
  <div class="address-manage page-container">
    <div class="main-width">
      <div class="page-header">
        <h2 class="page-title">收货地址</h2>
        <el-button type="primary" @click="showAddDialog = true">
          <el-icon><Plus /></el-icon>
          添加地址
        </el-button>
      </div>

      <el-skeleton :loading="loading" animated>
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
        <template #default>
          <div v-if="addressList.length === 0 && !loading" class="empty-address">
            <el-empty description="暂无收货地址" />
            <el-button type="primary" @click="showAddDialog = true">添加收货地址</el-button>
          </div>

          <div v-else class="address-list">
            <div v-for="addr in addressList" :key="addr.id" class="address-card">
              <div class="address-header">
                <span class="address-name">{{ addr.name }}</span>
                <span class="address-phone">{{ addr.phone }}</span>
                <el-tag v-if="addr.isDefault" type="success" size="small">默认</el-tag>
              </div>
              <div class="address-detail">
                {{ addr.province }}{{ addr.city }}{{ addr.district }}{{ addr.detail }}
              </div>
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
import { ref, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { getAddressList, addAddress, updateAddress, deleteAddress as deleteAddressApi } from '../api/address'
import { ElMessage } from 'element-plus'

const loading = ref(true)
const saving = ref(false)
const showAddDialog = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const addressList = ref([])

const form = ref({
  id: null,
  name: '',
  phone: '',
  province: '',
  city: '',
  district: '',
  detail: '',
  isDefault: false
})

const rules = {
  name: [{ required: true, message: '请输入收货人姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号码', trigger: 'blur' }, { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }],
  province: [{ required: true, message: '请输入省份', trigger: 'blur' }],
  city: [{ required: true, message: '请输入城市', trigger: 'blur' }],
  district: [{ required: true, message: '请输入区县', trigger: 'blur' }],
  detail: [{ required: true, message: '请输入详细地址', trigger: 'blur' }]
}

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

async function saveAddress() {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return

    saving.value = true

    try {
      if (isEdit.value) {
        const res = await updateAddress(form.value)
        if (res.code === 200) {
          ElMessage.success('地址更新成功')
        } else {
          ElMessage.error(res.message || '更新失败')
        }
      } else {
        const res = await addAddress(form.value)
        if (res.code === 200) {
          ElMessage.success('地址添加成功')
        } else {
          ElMessage.error(res.message || '添加失败')
        }
      }
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

async function deleteAddress(id) {
  try {
    const res = await deleteAddressApi(id)
    if (res.code === 200) {
      ElMessage.success('地址已删除')
      await loadAddresses()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error) {
    console.error('Delete address failed:', error)
    ElMessage.error('删除失败，请稍后重试')
  }
}

async function setDefault(id) {
  try {
    const addr = addressList.value.find(item => item.id === id)
    if (addr) {
      addr.isDefault = true
      const res = await updateAddress(addr)
      if (res.code === 200) {
        ElMessage.success('已设为默认地址')
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

onMounted(() => {
  loadAddresses()
})
</script>

<style scoped>
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.page-title {
  font-size: 20px;
  color: var(--text-color);
}

.empty-address {
  text-align: center;
  padding: 60px 0;
}

.empty-address .el-button {
  margin-top: 16px;
}

.address-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.address-card {
  background: #fff;
  border-radius: var(--radius);
  padding: 16px;
  border: 1px solid var(--border-color);
}

.address-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.address-name {
  font-size: 16px;
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
  margin-bottom: 12px;
}

.address-actions {
  display: flex;
  gap: 16px;
}

.address-actions .el-button {
  font-size: 13px;
}

.skeleton-item {
  background: #fff;
  border-radius: var(--radius);
  padding: 16px;
  margin-bottom: 16px;
}
</style>