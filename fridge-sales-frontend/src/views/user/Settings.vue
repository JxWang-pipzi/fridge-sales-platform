<template>
  <div class="settings-page">
    <div class="container">
      <div class="page-header">
        <router-link to="/profile" class="back-link">
          <el-icon><ArrowLeft /></el-icon>
          <span>返回个人中心</span>
        </router-link>
        <h1 class="page-title">账号设置</h1>
      </div>

      <div class="settings-content">
        <div class="settings-section">
          <div class="section-header">
            <h2 class="section-title">个人信息</h2>
            <p class="section-desc">管理您的账号基本信息</p>
          </div>

          <div class="settings-card">
            <el-form ref="infoFormRef" :model="infoForm" :rules="infoRules" label-position="top" class="settings-form">
              <div class="form-row">
                <el-form-item label="用户名" prop="username">
                  <el-input
                    v-model="infoForm.username"
                    placeholder="请输入用户名"
                    size="large"
                    clearable
                    :disabled="infoLoading"
                  />
                </el-form-item>
              </div>

              <div class="form-row">
                <el-form-item label="手机号" prop="phone">
                  <el-input
                    v-model="infoForm.phone"
                    placeholder="请输入手机号"
                    size="large"
                    clearable
                    :disabled="infoLoading"
                  />
                </el-form-item>
              </div>

              <div class="form-row">
                <el-form-item label="邮箱" prop="email">
                  <el-input
                    v-model="infoForm.email"
                    placeholder="请输入邮箱"
                    size="large"
                    clearable
                    :disabled="infoLoading"
                  />
                </el-form-item>
              </div>

              <div class="form-actions">
                <el-button type="primary" size="large" :loading="infoLoading" @click="handleUpdateInfo">
                  保存修改
                </el-button>
              </div>
            </el-form>
          </div>
        </div>

        <div class="settings-section">
          <div class="section-header">
            <h2 class="section-title">修改密码</h2>
            <p class="section-desc">定期修改密码可以提高账号安全性</p>
          </div>

          <div class="settings-card">
            <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-position="top" class="settings-form">
              <div class="form-row">
                <el-form-item label="旧密码" prop="oldPassword">
                  <el-input
                    v-model="passwordForm.oldPassword"
                    type="password"
                    placeholder="请输入旧密码"
                    size="large"
                    show-password
                    :disabled="passwordLoading"
                  />
                </el-form-item>
              </div>

              <div class="form-row">
                <el-form-item label="新密码" prop="newPassword">
                  <el-input
                    v-model="passwordForm.newPassword"
                    type="password"
                    placeholder="请输入新密码（至少6位）"
                    size="large"
                    show-password
                    :disabled="passwordLoading"
                  />
                </el-form-item>
              </div>

              <div class="form-row">
                <el-form-item label="确认新密码" prop="confirmPassword">
                  <el-input
                    v-model="passwordForm.confirmPassword"
                    type="password"
                    placeholder="请再次输入新密码"
                    size="large"
                    show-password
                    :disabled="passwordLoading"
                  />
                </el-form-item>
              </div>

              <div class="form-actions">
                <el-button type="primary" size="large" :loading="passwordLoading" @click="handleUpdatePassword">
                  修改密码
                </el-button>
              </div>
            </el-form>
          </div>
        </div>

        <div class="settings-section danger-zone">
          <div class="section-header">
            <h2 class="section-title danger-title">危险操作</h2>
            <p class="section-desc">以下操作不可逆，请谨慎操作</p>
          </div>

          <div class="settings-card danger-card">
            <div class="danger-item">
              <div class="danger-info">
                <h3>退出登录</h3>
                <p>退出当前账号，需要重新登录</p>
              </div>
              <el-button type="danger" plain @click="handleLogout">
                退出登录
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { updateUserInfo, updatePassword } from '@/api/user'

const router = useRouter()
const userStore = useUserStore()

const infoFormRef = ref(null)
const passwordFormRef = ref(null)
const infoLoading = ref(false)
const passwordLoading = ref(false)

const infoForm = reactive({
  username: '',
  phone: '',
  email: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validatePhone = (rule, value, callback) => {
  if (!value) {
    callback()
  } else if (!/^1[3-9]\d{9}$/.test(value)) {
    callback(new Error('请输入正确的手机号'))
  } else {
    callback()
  }
}

const validateEmail = (rule, value, callback) => {
  if (!value) {
    callback()
  } else if (!/^[\w-]+(\.[\w-]+)*@[\w-]+(\.[\w-]+)+$/.test(value)) {
    callback(new Error('请输入正确的邮箱'))
  } else {
    callback()
  }
}

const validateConfirmPassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请再次输入新密码'))
  } else if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const infoRules = {
  username: [
    { min: 2, max: 20, message: '用户名长度为2-20个字符', trigger: 'blur' }
  ],
  phone: [
    { validator: validatePhone, trigger: 'blur' }
  ],
  email: [
    { validator: validateEmail, trigger: 'blur' }
  ]
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入旧密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const loadUserInfo = () => {
  const user = userStore.user
  if (user) {
    infoForm.username = user.username || ''
    infoForm.phone = user.phone || ''
    infoForm.email = user.email || ''
  }
}

const handleUpdateInfo = async () => {
  if (!infoFormRef.value) return

  await infoFormRef.value.validate(async (valid) => {
    if (!valid) return

    infoLoading.value = true
    try {
      const data = {}
      if (infoForm.username && infoForm.username !== userStore.user?.username) {
        data.username = infoForm.username
      }
      if (infoForm.phone) {
        data.phone = infoForm.phone
      }
      if (infoForm.email) {
        data.email = infoForm.email
      }

      if (Object.keys(data).length === 0) {
        ElMessage.info('没有需要修改的信息')
        return
      }

      const res = await updateUserInfo(data)
      if (res.code === 200) {
        userStore.setUser(res.data)
        ElMessage.success('个人信息更新成功')
      } else {
        ElMessage.error(res.message || '更新失败')
      }
    } catch (error) {
      console.error('更新个人信息失败:', error)
      ElMessage.error(error.message || '更新失败，请稍后重试')
    } finally {
      infoLoading.value = false
    }
  })
}

const handleUpdatePassword = async () => {
  if (!passwordFormRef.value) return

  await passwordFormRef.value.validate(async (valid) => {
    if (!valid) return

    passwordLoading.value = true
    try {
      const res = await updatePassword({
        oldPassword: passwordForm.oldPassword,
        newPassword: passwordForm.newPassword
      })
      if (res.code === 200) {
        ElMessage.success('密码修改成功，请重新登录')
        userStore.logout()
        router.push('/login')
      } else {
        ElMessage.error(res.message || '修改失败')
      }
    } catch (error) {
      console.error('修改密码失败:', error)
      ElMessage.error(error.message || '修改失败，请稍后重试')
    } finally {
      passwordLoading.value = false
    }
  })
}

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '退出确认', {
      confirmButtonText: '确定退出',
      cancelButtonText: '取消',
      type: 'warning'
    })
    userStore.logout()
    router.push('/login')
    ElMessage.success('已退出登录')
  } catch {
  }
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.settings-page {
  min-height: calc(100vh - 60px);
  background: #f5f5f7;
  padding: 32px 0;
}

.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 20px;
}

.page-header {
  margin-bottom: 32px;
}

.back-link {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: #0071e3;
  font-size: 14px;
  font-weight: 500;
  text-decoration: none;
  margin-bottom: 16px;
  transition: color 0.2s;
}

.back-link:hover {
  color: #0077ed;
}

.page-title {
  font-size: 32px;
  font-weight: 700;
  color: #1d1d1f;
  margin: 0;
  letter-spacing: -0.5px;
}

.settings-content {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.settings-section {
  background: transparent;
}

.section-header {
  margin-bottom: 16px;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: #1d1d1f;
  margin: 0 0 6px;
  letter-spacing: -0.3px;
}

.section-desc {
  font-size: 14px;
  color: #86868b;
  margin: 0;
}

.settings-card {
  background: #ffffff;
  border-radius: 18px;
  padding: 32px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.settings-form {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.settings-form :deep(.el-form-item) {
  margin-bottom: 0;
}

.settings-form :deep(.el-form-item__label) {
  font-size: 13px;
  font-weight: 600;
  color: #1d1d1f;
  padding-bottom: 8px;
  letter-spacing: -0.2px;
}

.settings-form :deep(.el-input__wrapper) {
  border-radius: 12px !important;
  border: 1px solid #d2d2d7 !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
  padding: 0 16px !important;
  background: #f5f5f7 !important;
}

.settings-form :deep(.el-input__wrapper:hover) {
  border-color: #86868b !important;
}

.settings-form :deep(.el-input__wrapper.is-focus) {
  border-color: #0071e3 !important;
  box-shadow: 0 0 0 4px rgba(0, 113, 227, 0.15) !important;
  background: #ffffff !important;
}

.settings-form :deep(.el-input__inner) {
  color: #1d1d1f !important;
  font-size: 15px !important;
  letter-spacing: -0.2px;
}

.settings-form :deep(.el-input__inner::placeholder) {
  color: #86868b !important;
}

.settings-form :deep(.el-form-item__error) {
  font-size: 12px;
  padding-top: 6px;
}

.form-row {
  margin-bottom: 20px;
}

.form-row:last-of-type {
  margin-bottom: 0;
}

.form-actions {
  margin-top: 28px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

.form-actions .el-button {
  min-width: 140px;
  border-radius: 12px;
  font-weight: 600;
  height: 48px;
  font-size: 15px;
  letter-spacing: -0.2px;
}

.form-actions .el-button--primary {
  background: #0071e3;
  border-color: #0071e3;
}

.form-actions .el-button--primary:hover {
  background: #0077ed;
  border-color: #0077ed;
}

.danger-zone .section-title {
  color: #ff3b30;
}

.danger-card {
  border: 1px solid #ffccc7;
  background: #fff;
}

.danger-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 4px 0;
}

.danger-info h3 {
  font-size: 15px;
  font-weight: 600;
  color: #1d1d1f;
  margin: 0 0 4px;
}

.danger-info p {
  font-size: 13px;
  color: #86868b;
  margin: 0;
}

.danger-item .el-button {
  border-radius: 10px;
  font-weight: 500;
}

@media (max-width: 768px) {
  .settings-page {
    padding: 20px 0;
  }

  .page-title {
    font-size: 26px;
  }

  .settings-card {
    padding: 24px;
    border-radius: 14px;
  }

  .danger-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .danger-item .el-button {
    width: 100%;
  }
}
</style>
