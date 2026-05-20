<template>
  <div class="address-page">
    <div class="container">
      <div class="page-header">
        <h1 class="page-title">收货地址</h1>
        <p class="page-subtitle">管理您的收货地址</p>
      </div>

      <div v-loading="loading" class="address-content">
        <div class="address-toolbar">
          <button class="add-btn" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            添加新地址
          </button>
        </div>

        <div v-if="addressList.length > 0" class="address-list">
          <div
            v-for="addr in addressList"
            :key="addr.id"
            class="address-card"
            :class="{ 'is-default': addr.isDefault }"
          >
            <div class="address-card-main">
              <div class="address-header">
                <div class="receiver-info">
                  <span class="receiver-name">{{ addr.receiverName }}</span>
                  <span class="receiver-phone">{{ addr.receiverPhone }}</span>
                </div>
                <span v-if="addr.isDefault" class="default-badge">默认</span>
              </div>
              <div class="address-detail">
                <el-icon><Location /></el-icon>
                <span>{{ addr.province }}{{ addr.city }}{{ addr.district }}{{ addr.detailAddress }}</span>
              </div>
            </div>
            <div class="address-actions">
              <button
                v-if="!addr.isDefault"
                class="action-btn set-default-btn"
                @click="handleSetDefault(addr.id)"
              >
                设为默认
              </button>
              <button class="action-btn edit-btn" @click="handleEdit(addr)">
                编辑
              </button>
              <button class="action-btn delete-btn" @click="handleDelete(addr.id)">
                删除
              </button>
            </div>
          </div>
        </div>

        <div v-else class="empty-state">
          <div class="empty-icon">
            <el-icon :size="64"><Location /></el-icon>
          </div>
          <p class="empty-text">暂无收货地址</p>
          <p class="empty-hint">添加收货地址，方便您快速下单</p>
          <button class="empty-add-btn" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            添加地址
          </button>
        </div>
      </div>
    </div>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑地址' : '新增地址'"
      width="520px"
      :close-on-click-modal="false"
      class="address-dialog"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-position="top"
        class="address-form"
      >
        <div class="form-row">
          <el-form-item label="收货人" prop="receiverName" class="form-item-half">
            <el-input
              v-model="formData.receiverName"
              placeholder="请输入收货人姓名"
              maxlength="20"
            />
          </el-form-item>
          <el-form-item label="手机号码" prop="receiverPhone" class="form-item-half">
            <el-input
              v-model="formData.receiverPhone"
              placeholder="请输入手机号码"
              maxlength="11"
            />
          </el-form-item>
        </div>

        <div class="form-row">
          <el-form-item label="省份" prop="province" class="form-item-third">
            <el-input
              v-model="formData.province"
              placeholder="省份"
              maxlength="20"
            />
          </el-form-item>
          <el-form-item label="城市" prop="city" class="form-item-third">
            <el-input
              v-model="formData.city"
              placeholder="城市"
              maxlength="20"
            />
          </el-form-item>
          <el-form-item label="区/县" prop="district" class="form-item-third">
            <el-input
              v-model="formData.district"
              placeholder="区/县"
              maxlength="20"
            />
          </el-form-item>
        </div>

        <el-form-item label="详细地址" prop="detailAddress">
          <el-input
            v-model="formData.detailAddress"
            type="textarea"
            :rows="3"
            placeholder="请输入详细地址，如街道、门牌号等"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>

        <el-form-item v-if="!isEdit || addressList.length === 0">
          <el-checkbox v-model="formData.setAsDefault">设为默认收货地址</el-checkbox>
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <button class="cancel-btn" @click="dialogVisible = false">取消</button>
          <button class="submit-btn" :disabled="submitting" @click="handleSubmit">
            {{ submitting ? '保存中...' : '保存' }}
          </button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Location } from '@element-plus/icons-vue'
import {
  getAddressList,
  addAddress,
  updateAddress,
  deleteAddress,
  setDefaultAddress
} from '@/api/user'

const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const addressList = ref([])
const formRef = ref(null)

const formData = reactive({
  id: null,
  receiverName: '',
  receiverPhone: '',
  province: '',
  city: '',
  district: '',
  detailAddress: '',
  setAsDefault: false
})

const formRules = {
  receiverName: [
    { required: true, message: '请输入收货人姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度为2-20个字符', trigger: 'blur' }
  ],
  receiverPhone: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  province: [
    { required: true, message: '请输入省份', trigger: 'blur' }
  ],
  city: [
    { required: true, message: '请输入城市', trigger: 'blur' }
  ],
  district: [
    { required: true, message: '请输入区/县', trigger: 'blur' }
  ],
  detailAddress: [
    { required: true, message: '请输入详细地址', trigger: 'blur' },
    { min: 5, max: 100, message: '详细地址长度为5-100个字符', trigger: 'blur' }
  ]
}

const fetchAddressList = async () => {
  loading.value = true
  try {
    const res = await getAddressList()
    addressList.value = res.data || []
  } catch (error) {
    console.error('获取地址列表失败:', error)
    ElMessage.error('获取地址列表失败')
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  formData.id = null
  formData.receiverName = ''
  formData.receiverPhone = ''
  formData.province = ''
  formData.city = ''
  formData.district = ''
  formData.detailAddress = ''
  formData.setAsDefault = false
  formRef.value?.resetFields()
}

const handleAdd = () => {
  isEdit.value = false
  resetForm()
  if (addressList.value.length === 0) {
    formData.setAsDefault = true
  }
  dialogVisible.value = true
}

const handleEdit = (addr) => {
  isEdit.value = true
  formData.id = addr.id
  formData.receiverName = addr.receiverName
  formData.receiverPhone = addr.receiverPhone
  formData.province = addr.province
  formData.city = addr.city
  formData.district = addr.district
  formData.detailAddress = addr.detailAddress
  formData.setAsDefault = false
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
  } catch {
    return
  }

  submitting.value = true
  try {
    const data = {
      id: formData.id,
      receiverName: formData.receiverName,
      receiverPhone: formData.receiverPhone,
      province: formData.province,
      city: formData.city,
      district: formData.district,
      detailAddress: formData.detailAddress
    }

    if (isEdit.value) {
      await updateAddress(data)
      ElMessage.success('地址修改成功')
    } else {
      const res = await addAddress(data)
      if (formData.setAsDefault && res.data) {
        await setDefaultAddress(res.data)
      }
      ElMessage.success('地址添加成功')
    }

    dialogVisible.value = false
    fetchAddressList()
  } catch (error) {
    console.error('保存地址失败:', error)
    ElMessage.error('保存地址失败，请重试')
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该地址吗？', '删除确认', {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await deleteAddress(id)
    ElMessage.success('地址删除成功')
    fetchAddressList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除地址失败:', error)
      ElMessage.error('删除地址失败')
    }
  }
}

const handleSetDefault = async (id) => {
  try {
    await setDefaultAddress(id)
    ElMessage.success('已设为默认地址')
    fetchAddressList()
  } catch (error) {
    console.error('设置默认地址失败:', error)
    ElMessage.error('设置默认地址失败')
  }
}

onMounted(() => {
  fetchAddressList()
})
</script>

<style scoped>
.address-page {
  padding: 24px 0;
  background-color: #f5f5f7;
  min-height: calc(100vh - 60px);
}

.container {
  max-width: 960px;
  margin: 0 auto;
  padding: 0 16px;
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-size: 28px;
  font-weight: 600;
  color: #1d1d1f;
  margin: 0 0 8px;
  letter-spacing: -0.5px;
}

.page-subtitle {
  font-size: 14px;
  color: #86868b;
  margin: 0;
}

.address-content {
  background-color: #ffffff;
  border-radius: 18px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.address-toolbar {
  margin-bottom: 20px;
}

.add-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  background-color: #0071e3;
  color: #ffffff;
  border: none;
  border-radius: 980px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.add-btn:hover {
  background-color: #0077ed;
  transform: scale(1.02);
}

.add-btn:active {
  transform: scale(0.98);
}

.address-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.address-card {
  border: 1px solid #d2d2d7;
  border-radius: 12px;
  padding: 20px;
  transition: all 0.2s ease;
}

.address-card:hover {
  border-color: #0071e3;
  box-shadow: 0 4px 16px rgba(0, 113, 227, 0.12);
}

.address-card.is-default {
  border-color: #0071e3;
  background-color: #f5faff;
}

.address-card-main {
  margin-bottom: 16px;
}

.address-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.receiver-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.receiver-name {
  font-size: 16px;
  font-weight: 600;
  color: #1d1d1f;
}

.receiver-phone {
  font-size: 14px;
  color: #86868b;
}

.default-badge {
  padding: 4px 10px;
  background-color: #0071e3;
  color: #ffffff;
  font-size: 12px;
  font-weight: 500;
  border-radius: 4px;
}

.address-detail {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  color: #1d1d1f;
  font-size: 14px;
  line-height: 1.6;
}

.address-detail .el-icon {
  color: #0071e3;
  margin-top: 2px;
  flex-shrink: 0;
}

.address-actions {
  display: flex;
  gap: 12px;
  padding-top: 16px;
  border-top: 1px solid #f5f5f7;
}

.action-btn {
  padding: 8px 16px;
  border: 1px solid #d2d2d7;
  border-radius: 6px;
  background-color: #ffffff;
  font-size: 13px;
  color: #1d1d1f;
  cursor: pointer;
  transition: all 0.2s ease;
}

.action-btn:hover {
  border-color: #0071e3;
  color: #0071e3;
}

.set-default-btn:hover {
  background-color: #f5faff;
}

.edit-btn:hover {
  background-color: #f5faff;
}

.delete-btn {
  color: #ff3b30;
  border-color: #ff3b30;
}

.delete-btn:hover {
  background-color: #fff5f5;
  border-color: #ff3b30;
  color: #ff3b30;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.empty-icon {
  color: #d2d2d7;
  margin-bottom: 20px;
}

.empty-text {
  font-size: 18px;
  font-weight: 500;
  color: #1d1d1f;
  margin: 0 0 8px;
}

.empty-hint {
  font-size: 14px;
  color: #86868b;
  margin: 0 0 24px;
}

.empty-add-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 12px 28px;
  background-color: #0071e3;
  color: #ffffff;
  border: none;
  border-radius: 980px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.empty-add-btn:hover {
  background-color: #0077ed;
  transform: scale(1.02);
}

.address-dialog :deep(.el-dialog) {
  border-radius: 18px;
  overflow: hidden;
}

.address-dialog :deep(.el-dialog__header) {
  padding: 20px 24px;
  border-bottom: 1px solid #f5f5f7;
  display: flex;
  align-items: center;
}

.address-dialog :deep(.el-dialog__title) {
  font-size: 18px;
  font-weight: 600;
  color: #1d1d1f;
  flex: 1;
}

.address-dialog :deep(.el-dialog__headerbtn) {
  position: relative;
  top: auto;
  right: 0;
  width: 36px;
  height: 36px;
  margin-left: auto;
}

.address-dialog :deep(.el-dialog__headerbtn .el-dialog__close) {
  font-size: 20px;
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.2s ease;
  color: #1d1d1f;
}

.address-dialog :deep(.el-dialog__headerbtn:hover .el-dialog__close) {
  background-color: #f5f5f7;
}

.address-dialog :deep(.el-dialog__body) {
  padding: 24px;
}

.address-form :deep(.el-form-item__label) {
  font-size: 13px;
  font-weight: 500;
  color: #1d1d1f;
  padding-bottom: 6px;
}

.address-form :deep(.el-input__wrapper),
.address-form :deep(.el-textarea__inner) {
  border-radius: 8px;
  border-color: #d2d2d7;
  transition: all 0.2s ease;
}

.address-form :deep(.el-input__wrapper:hover),
.address-form :deep(.el-textarea__inner:hover) {
  border-color: #0071e3;
}

.address-form :deep(.el-input__wrapper.is-focus),
.address-form :deep(.el-textarea__inner:focus) {
  border-color: #0071e3;
  box-shadow: 0 0 0 3px rgba(0, 113, 227, 0.15);
}

.form-row {
  display: flex;
  gap: 16px;
}

.form-item-half {
  flex: 1;
}

.form-item-third {
  flex: 1;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.cancel-btn {
  padding: 10px 24px;
  background-color: #ffffff;
  border: 1px solid #d2d2d7;
  border-radius: 980px;
  font-size: 14px;
  font-weight: 500;
  color: #1d1d1f;
  cursor: pointer;
  transition: all 0.2s ease;
}

.cancel-btn:hover {
  background-color: #f5f5f7;
}

.submit-btn {
  padding: 10px 32px;
  background-color: #0071e3;
  border: none;
  border-radius: 980px;
  font-size: 14px;
  font-weight: 500;
  color: #ffffff;
  cursor: pointer;
  transition: all 0.2s ease;
}

.submit-btn:hover:not(:disabled) {
  background-color: #0077ed;
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .page-title {
    font-size: 24px;
  }

  .address-content {
    padding: 16px;
  }

  .address-card {
    padding: 16px;
  }

  .address-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .receiver-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }

  .address-actions {
    flex-wrap: wrap;
  }

  .action-btn {
    flex: 1;
    min-width: 80px;
    text-align: center;
  }

  .form-row {
    flex-direction: column;
    gap: 0;
  }

  .form-item-third {
    width: 100%;
  }
}
</style>
