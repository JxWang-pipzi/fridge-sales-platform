<template>
  <div class="forgot-password-page">
    <div class="forgot-wrapper">
      <div class="forgot-header">
        <div class="brand-logo">
          <div class="logo-icon">
            <span>冰</span>
          </div>
          <span class="brand-name">优选冰箱</span>
        </div>
        <h2 class="page-title">忘记密码</h2>
        <p class="page-desc">请输入您的注册邮箱，我们将发送验证码</p>
      </div>

      <div class="forgot-card">
        <el-form ref="formRef" :model="form" class="forgot-form" @submit.prevent aria-label="忘记密码表单">
          <el-form-item>
            <div class="form-label" id="email-label">邮箱地址</div>
            <el-input
              id="email"
              v-model="form.email"
              placeholder="请输入注册邮箱"
              size="large"
              clearable
              :disabled="loading || success"
              @blur="validateEmail"
              aria-labelledby="email-label"
              aria-required="true"
            />
            <transition name="error-fade">
              <div v-if="error" class="field-error">{{ error }}</div>
            </transition>
          </el-form-item>

          <el-form-item>
            <div class="form-label">验证码</div>
            <el-input
              v-model="form.verificationCode"
              placeholder="请输入验证码"
              size="large"
              clearable
              :disabled="loading || success"
            />
          </el-form-item>

          <el-form-item>
            <div class="form-label">新密码</div>
            <el-input
              v-model="form.newPassword"
              type="password"
              placeholder="请输入新密码（6-20位）"
              size="large"
              show-password
              :disabled="loading || success"
            />
          </el-form-item>

          <el-form-item>
            <div class="form-label">确认新密码</div>
            <el-input
              v-model="form.confirmPassword"
              type="password"
              placeholder="请再次输入新密码"
              size="large"
              show-password
              :disabled="loading || success"
            />
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              :disabled="loading || success"
              class="submit-btn"
              @click="handleSubmit"
              aria-label="重置密码"
            >
              {{ loading ? '重置中...' : '重置密码' }}
            </el-button>
          </el-form-item>
        </el-form>

        <transition name="success-fade">
          <div v-if="success" class="success-message">
            <el-icon><CircleCheckFilled /></el-icon>
            <div class="success-content">
              <p class="success-title">重置成功</p>
              <p class="success-desc">密码已成功重置，请使用新密码登录</p>
              <p class="success-tip">即将跳转到登录页面...</p>
            </div>
          </div>
        </transition>

        <div class="forgot-footer">
          <router-link to="/login" class="back-link">
            <el-icon><ArrowLeft /></el-icon>
            返回登录
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { CircleCheckFilled, ArrowLeft } from '@element-plus/icons-vue'
import { sendCode, forgotPassword } from '@/api/user'

const router = useRouter()

const loading = ref(false)
const success = ref(false)
const error = ref('')
const formRef = ref(null)

const form = reactive({
  email: '',
  verificationCode: '',
  newPassword: '',
  confirmPassword: ''
})

const validateEmail = () => {
  error.value = ''
  if (!form.email) {
    error.value = '请输入邮箱地址'
    return false
  }
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(form.email)) {
    error.value = '请输入有效的邮箱地址'
    return false
  }
  return true
}

const validatePassword = () => {
  error.value = ''
  if (!form.newPassword) {
    error.value = '请输入新密码'
    return false
  }
  if (form.newPassword.length < 6 || form.newPassword.length > 20) {
    error.value = '密码长度为 6-20 个字符'
    return false
  }
  if (form.newPassword !== form.confirmPassword) {
    error.value = '两次输入的密码不一致'
    return false
  }
  return true
}

const handleSendCode = async () => {
  if (!validateEmail()) return

  loading.value = true
  error.value = ''

  try {
    await sendCode(form.email)
    ElMessage.success('验证码已发送，请查看控制台')

    // 获取用户输入的验证码
    const userCode = prompt('请输入验证码（控制台已显示）:')
    if (userCode && userCode.trim()) {
      form.verificationCode = userCode.trim()
    } else {
      ElMessage.warning('请输入验证码')
      loading.value = false
      return
    }

    success.value = false
  } catch (err) {
    error.value = err.message || '发送失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

const handleSubmit = async () => {
  if (!validateEmail()) return
  if (!validatePassword()) return

  loading.value = true
  error.value = ''

  try {
    await forgotPassword({
      email: form.email,
      verificationCode: form.verificationCode,
      newPassword: form.newPassword,
      confirmPassword: form.confirmPassword
    })
    ElMessage.success('密码重置成功，请使用新密码登录')
    setTimeout(() => {
      router.push('/login')
    }, 1500)
  } catch (err) {
    error.value = err.message || '重置失败，请稍后重试'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.forgot-password-page {
  min-height: calc(100vh - 60px);
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f3f4f6;
  padding: 48px 16px;
}

.forgot-wrapper {
  width: 100%;
  max-width: 400px;
}

.forgot-header {
  text-align: center;
  margin-bottom: 32px;
}

.brand-logo {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-bottom: 16px;
}

.logo-icon {
  width: 48px;
  height: 48px;
  background-color: var(--blue-primary);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-icon span {
  color: #ffffff;
  font-weight: bold;
  font-size: 20px;
}

.brand-name {
  font-size: 24px;
  font-weight: bold;
  color: var(--foreground);
}

.page-title {
  font-size: 24px;
  font-weight: bold;
  color: var(--foreground);
  margin-bottom: 8px;
}

.page-desc {
  color: var(--muted-foreground);
  font-size: 14px;
}

.forgot-card {
  background-color: #ffffff;
  border-radius: var(--radius);
  border: 1px solid var(--border);
  padding: 32px;
}

.forgot-form {
  margin-bottom: 24px;
}

.form-label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: var(--foreground);
  margin-bottom: 8px;
}

.submit-btn {
  width: 100%;
  background-color: var(--blue-primary);
  border-color: var(--blue-primary);
}

.submit-btn:hover:not(:disabled) {
  background-color: var(--blue-hover);
  border-color: var(--blue-hover);
}

.field-error {
  color: #ef4444;
  font-size: 12px;
  margin-top: 4px;
  padding-left: 4px;
}

.success-message {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  background-color: #f0fdf4;
  border: 1px solid #bbf7d0;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
}

.success-message .el-icon {
  color: #22c55e;
  font-size: 24px;
  flex-shrink: 0;
}

.success-title {
  font-weight: 600;
  color: #166534;
  margin: 0 0 4px 0;
}

.success-desc {
  color: #15803d;
  font-size: 14px;
  margin: 0 0 8px 0;
}

.success-tip {
  color: #16a34a;
  font-size: 12px;
  margin: 0;
}

.forgot-footer {
  text-align: center;
  margin-top: 24px;
}

.back-link {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  color: var(--blue-primary);
  font-size: 14px;
  text-decoration: none;
  transition: color 0.2s;
}

.back-link:hover {
  color: var(--blue-hover);
}

.error-fade-enter-active,
.error-fade-leave-active {
  transition: all 0.3s ease;
}

.error-fade-enter-from,
.error-fade-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

.success-fade-enter-active,
.success-fade-leave-active {
  transition: all 0.3s ease;
}

.success-fade-enter-from,
.success-fade-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}
</style>
