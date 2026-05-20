<template>
  <div class="register-page">
    <div class="register-wrapper">
      <div class="register-header">
        <div class="brand-logo">
          <div class="logo-icon">
            <span>冰</span>
          </div>
          <span class="brand-name">优选冰箱</span>
        </div>
        <h2 class="welcome-title">创建账号</h2>
        <p class="welcome-desc">加入我们，享受优质冰箱产品</p>
      </div>

      <div class="register-card">
        <el-form ref="registerFormRef" :model="registerForm" class="register-form" @submit.prevent aria-label="注册表单">
          <el-form-item>
            <div class="form-label" id="username-label">用户名</div>
            <el-input
              id="username"
              v-model="registerForm.username"
              placeholder="请输入用户名"
              size="large"
              clearable
              :disabled="loading"
              @blur="validateField('username')"
              aria-labelledby="username-label"
              aria-required="true"
            />
            <transition name="error-fade">
              <div v-if="fieldErrors.username" class="field-error">{{ fieldErrors.username }}</div>
            </transition>
          </el-form-item>

          <el-form-item>
            <div class="form-label" id="email-label">邮箱</div>
            <el-input
              id="email"
              v-model="registerForm.email"
              placeholder="请输入邮箱地址"
              size="large"
              clearable
              :disabled="loading"
              @blur="validateField('email')"
              aria-labelledby="email-label"
              aria-required="true"
            />
            <transition name="error-fade">
              <div v-if="fieldErrors.email" class="field-error">{{ fieldErrors.email }}</div>
            </transition>
          </el-form-item>

          <el-form-item>
            <div class="form-label" id="phone-label">手机号</div>
            <el-input
              id="phone"
              v-model="registerForm.phone"
              placeholder="请输入手机号"
              size="large"
              clearable
              :disabled="loading"
              @blur="validateField('phone')"
              aria-labelledby="phone-label"
              aria-required="true"
            />
            <transition name="error-fade">
              <div v-if="fieldErrors.phone" class="field-error">{{ fieldErrors.phone }}</div>
            </transition>
          </el-form-item>

          <el-form-item>
            <div class="form-label" id="password-label">密码</div>
            <el-input
              id="password"
              v-model="registerForm.password"
              type="password"
              placeholder="请输入密码（6-20位）"
              size="large"
              show-password
              :disabled="loading"
              @blur="validateField('password')"
              aria-labelledby="password-label"
              aria-required="true"
            />
            <transition name="error-fade">
              <div v-if="fieldErrors.password" class="field-error">{{ fieldErrors.password }}</div>
            </transition>
          </el-form-item>

          <el-form-item>
            <div class="form-label" id="confirmPassword-label">确认密码</div>
            <el-input
              id="confirmPassword"
              v-model="registerForm.confirmPassword"
              type="password"
              placeholder="请再次输入密码"
              size="large"
              show-password
              :disabled="loading"
              @blur="validateField('confirmPassword')"
              @keyup.enter="handleRegister"
              aria-labelledby="confirmPassword-label"
              aria-required="true"
            />
            <transition name="error-fade">
              <div v-if="fieldErrors.confirmPassword" class="field-error">{{ fieldErrors.confirmPassword }}</div>
            </transition>
          </el-form-item>

          <transition name="error-fade">
            <div v-if="submitError" class="submit-error">
              <el-icon><WarningFilled /></el-icon>
              <span>{{ submitError }}</span>
            </div>
          </transition>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              :disabled="loading"
              class="register-btn"
              @click="handleRegister"
              aria-label="注册"
            >
              {{ loading ? '注册中...' : '立即注册' }}
            </el-button>
          </el-form-item>
        </el-form>

        <div class="register-footer">
          <span>已有账号？</span>
          <router-link to="/login" class="login-link">立即登录</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { WarningFilled } from '@element-plus/icons-vue'
import { register } from '@/api/user'

const router = useRouter()

const loading = ref(false)
const registerFormRef = ref(null)
const submitError = ref('')
const isSubmitting = ref(false)

const registerForm = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  phone: ''
})

const fieldErrors = reactive({
  username: '',
  email: '',
  phone: '',
  password: '',
  confirmPassword: ''
})

const validateField = (field) => {
  fieldErrors[field] = ''

  switch (field) {
    case 'username':
      if (!registerForm.username) {
        fieldErrors.username = '请输入用户名'
      } else if (registerForm.username.length < 2 || registerForm.username.length > 20) {
        fieldErrors.username = '用户名长度为 2-20 个字符'
      }
      break
    case 'email':
      if (!registerForm.email) {
        fieldErrors.email = '请输入邮箱'
      } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(registerForm.email)) {
        fieldErrors.email = '请输入正确的邮箱地址'
      }
      break
    case 'phone':
      if (!registerForm.phone) {
        fieldErrors.phone = '请输入手机号'
      } else if (!/^1[3-9]\d{9}$/.test(registerForm.phone)) {
        fieldErrors.phone = '请输入正确的手机号'
      }
      break
    case 'password':
      if (!registerForm.password) {
        fieldErrors.password = '请输入密码'
      } else if (registerForm.password.length < 6 || registerForm.password.length > 20) {
        fieldErrors.password = '密码长度为 6-20 个字符'
      }
      if (registerForm.confirmPassword && registerForm.confirmPassword !== registerForm.password) {
        fieldErrors.confirmPassword = '两次输入的密码不一致'
      }
      break
    case 'confirmPassword':
      if (!registerForm.confirmPassword) {
        fieldErrors.confirmPassword = '请确认密码'
      } else if (registerForm.confirmPassword !== registerForm.password) {
        fieldErrors.confirmPassword = '两次输入的密码不一致'
      }
      break
  }
}

const debounce = (fn, delay) => {
  let timer = null
  return function (...args) {
    if (timer) clearTimeout(timer)
    timer = setTimeout(() => {
      fn.apply(this, args)
    }, delay)
  }
}

const doRegister = async () => {
  if (isSubmitting.value) return
  
  submitError.value = ''
  Object.keys(fieldErrors).forEach(key => {
    fieldErrors[key] = ''
  })

  let hasError = false

  if (!registerForm.username) {
    fieldErrors.username = '请输入用户名'
    hasError = true
  } else if (registerForm.username.length < 2 || registerForm.username.length > 20) {
    fieldErrors.username = '用户名长度为 2-20 个字符'
    hasError = true
  }

  if (!registerForm.email) {
    fieldErrors.email = '请输入邮箱'
    hasError = true
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(registerForm.email)) {
    fieldErrors.email = '请输入正确的邮箱地址'
    hasError = true
  }

  if (!registerForm.phone) {
    fieldErrors.phone = '请输入手机号'
    hasError = true
  } else if (!/^1[3-9]\d{9}$/.test(registerForm.phone)) {
    fieldErrors.phone = '请输入正确的手机号'
    hasError = true
  }

  if (!registerForm.password) {
    fieldErrors.password = '请输入密码'
    hasError = true
  } else if (registerForm.password.length < 6 || registerForm.password.length > 20) {
    fieldErrors.password = '密码长度为 6-20 个字符'
    hasError = true
  }

  if (!registerForm.confirmPassword) {
    fieldErrors.confirmPassword = '请确认密码'
    hasError = true
  } else if (registerForm.confirmPassword !== registerForm.password) {
    fieldErrors.confirmPassword = '两次输入的密码不一致'
    hasError = true
  }

  if (hasError) return

  isSubmitting.value = true
  loading.value = true

  try {
    await register({
      username: registerForm.username,
      email: registerForm.email,
      password: registerForm.password,
      phone: registerForm.phone
    })
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (error) {
    console.error('注册失败:', error)
    submitError.value = error.message || '注册失败，请稍后重试'
  } finally {
    loading.value = false
    isSubmitting.value = false
  }
}

const handleRegister = debounce(doRegister, 500)
</script>

<style scoped>
.register-page {
  min-height: calc(100vh - 60px);
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f7;
  padding: 60px 20px;
}

.register-wrapper {
  width: 100%;
  max-width: 440px;
}

.register-header {
  text-align: center;
  margin-bottom: 40px;
}

.brand-logo {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  margin-bottom: 24px;
}

.logo-icon {
  width: 56px;
  height: 56px;
  background: #0071e3;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 16px rgba(0, 113, 227, 0.25);
}

.logo-icon span {
  color: #ffffff;
  font-weight: 700;
  font-size: 24px;
  letter-spacing: -0.5px;
}

.brand-name {
  font-size: 28px;
  font-weight: 700;
  color: #1d1d1f;
  letter-spacing: -0.5px;
}

.welcome-title {
  font-size: 28px;
  font-weight: 600;
  color: #1d1d1f;
  margin-bottom: 10px;
  letter-spacing: -0.5px;
}

.welcome-desc {
  color: #6e6e73;
  font-size: 15px;
  letter-spacing: -0.2px;
}

.register-card {
  background-color: #ffffff;
  border-radius: 18px;
  box-shadow: 0 2px 16px rgba(0, 0, 0, 0.04);
  padding: 40px;
}

.register-form {
  margin-bottom: 28px;
}

.register-form :deep(.el-form-item) {
  margin-bottom: 20px;
}

.register-form :deep(.el-input__wrapper) {
  border-radius: 12px !important;
  border: none !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
  padding: 0 16px !important;
  box-sizing: border-box !important;
  background: #f5f5f7 !important;
}

.register-form :deep(.el-input__wrapper:hover) {
  border-color: #86868b !important;
}

.register-form :deep(.el-input__wrapper.is-focus) {
  border-color: #0071e3 !important;
  box-shadow: 0 0 0 4px rgba(0, 113, 227, 0.15) !important;
  background: #ffffff !important;
}

.register-form :deep(.el-input__inner) {
  border: none !important;
  outline: none !important;
  box-shadow: none !important;
  color: #1d1d1f !important;
  font-size: 15px !important;
  letter-spacing: -0.2px;
}

.register-form :deep(.el-input__inner::placeholder) {
  color: #86868b !important;
}

.form-label {
  display: block;
  font-size: 13px;
  font-weight: 600;
  color: #1d1d1f;
  margin-bottom: 10px;
  letter-spacing: -0.2px;
}

.register-btn {
  width: 100%;
  background: #ffffff;
  border: 1px solid #0071e3;
  border-radius: 12px;
  font-weight: 600;
  height: 50px;
  font-size: 16px;
  color: #0071e3;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  letter-spacing: -0.3px;
}

.register-btn:hover {
  transform: translateY(-1px);
  background: #f5f5f7;
}

.register-btn:active {
  transform: translateY(0);
}

.register-footer {
  text-align: center;
  color: #6e6e73;
  font-size: 14px;
  letter-spacing: -0.2px;
}

.login-link {
  color: #0071e3;
  font-weight: 600;
  margin-left: 4px;
  text-decoration: none;
  transition: color 0.2s;
}

.login-link:hover {
  color: #0077ed;
  text-decoration: underline;
}

.field-error {
  color: #ff3b30;
  font-size: 12px;
  margin-top: 8px;
  padding-left: 4px;
  font-weight: 500;
  letter-spacing: -0.2px;
}

.submit-error {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #fff5f5;
  border: 1px solid #ffd6d6;
  border-radius: 12px;
  padding: 14px 16px;
  margin-bottom: 16px;
  color: #ff3b30;
  font-size: 14px;
  font-weight: 500;
}

.submit-error .el-icon {
  font-size: 18px;
}

.error-fade-enter-active,
.error-fade-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.error-fade-enter-from,
.error-fade-leave-to {
  opacity: 0;
  transform: translateY(-6px);
}
</style>
