<template>
  <div class="login-page">
    <div class="login-wrapper">
      <div class="login-header">
        <div class="brand-logo">
          <div class="logo-icon">
            <span>冰</span>
          </div>
          <span class="brand-name">优选冰箱</span>
        </div>
        <h2 class="welcome-title">欢迎回来</h2>
        <p class="welcome-desc">登录您的账户继续购物</p>
      </div>

      <div class="login-card">
        <el-form ref="loginFormRef" :model="loginForm" class="login-form" @submit.prevent aria-label="登录表单">
          <el-form-item>
            <div class="form-label" id="username-label">用户名</div>
            <el-input
              id="username"
              v-model="loginForm.username"
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
            <div class="form-label" id="password-label">密码</div>
            <el-input
              id="password"
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              show-password
              :disabled="loading"
              @blur="validateField('password')"
              @keyup.enter="handleLogin"
              aria-labelledby="password-label"
              aria-required="true"
            />
            <transition name="error-fade">
              <div v-if="fieldErrors.password" class="field-error">{{ fieldErrors.password }}</div>
            </transition>
          </el-form-item>
          <el-form-item>
            <div class="form-options">
              <el-checkbox v-model="rememberMe" :disabled="loading">记住我</el-checkbox>
              <router-link to="/forgot-password" class="forgot-link">忘记密码？</router-link>
            </div>
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              :disabled="loading"
              class="login-btn"
              @click="handleLogin"
              aria-label="登录"
            >
              {{ loading ? '登录中...' : '登录' }}
            </el-button>
          </el-form-item>
        </el-form>

        <transition name="error-fade">
          <div v-if="submitError" class="submit-error">
            <el-icon><WarningFilled /></el-icon>
            <span>{{ submitError }}</span>
          </div>
        </transition>

        <div class="login-footer">
          <span>还没有账号？</span>
          <router-link to="/register" class="register-link">立即注册</router-link>
        </div>

        <div class="divider">
          <span>或者</span>
        </div>

        <div class="third-party-login">
          <button class="third-party-btn" :disabled="loading" @click="handleThirdPartyLogin('wechat')" aria-label="微信登录" type="button">
            微信登录
          </button>
          <button class="third-party-btn" :disabled="loading" @click="handleThirdPartyLogin('qq')" aria-label="QQ登录" type="button">
            QQ登录
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { WarningFilled } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const cartStore = useCartStore()

const loading = ref(false)
const rememberMe = ref(false)
const loginFormRef = ref(null)
const submitError = ref('')
const isSubmitting = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const fieldErrors = reactive({
  username: '',
  password: ''
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为3-20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6个字符', trigger: 'blur' }
  ]
}

const validateField = (field) => {
  fieldErrors[field] = ''
  if (!loginForm[field]) {
    if (field === 'username') {
      fieldErrors[field] = '请输入用户名'
    } else if (field === 'password') {
      fieldErrors[field] = '请输入密码'
    }
  } else if (field === 'password' && loginForm.password.length < 6) {
    fieldErrors[field] = '密码至少6个字符'
  } else if (field === 'username' && (loginForm.username.length < 3 || loginForm.username.length > 20)) {
    fieldErrors[field] = '用户名长度为3-20个字符'
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

const doLogin = async () => {
  if (isSubmitting.value) return
  
  submitError.value = ''
  fieldErrors.username = ''
  fieldErrors.password = ''

  if (!loginForm.username) {
    fieldErrors.username = '请输入用户名'
    return
  }
  if (!loginForm.password) {
    fieldErrors.password = '请输入密码'
    return
  }
  if (loginForm.password.length < 6) {
    fieldErrors.password = '密码至少6个字符'
    return
  }

  isSubmitting.value = true
  loading.value = true
  const loginTime = Date.now()
  console.log('[操作日志] [阶段 1][用户登录] 时间：' + loginTime + ' | 参数：{username: ' + loginForm.username + '}')

  try {
    await userStore.loginAction(loginForm)
    await cartStore.fetchCartCount()
    console.log('[操作日志] [阶段 4][用户登录成功] 时间：' + Date.now() + ' | 参数：{username: ' + loginForm.username + '} | 结果：登录成功')
    ElMessage.success('登录成功')
    const redirect = route.query.redirect || '/home'
    router.push(redirect)
  } catch (error) {
    console.error('[操作日志] [失败][阶段 1][用户登录] 时间：' + Date.now() + ' | 原因：' + (error.message || '登录失败') + ' | 参数：{username: ' + loginForm.username + '}')
    submitError.value = error.message || '登录失败，请检查用户名和密码'
  } finally {
    loading.value = false
    isSubmitting.value = false
  }
}

const handleLogin = debounce(doLogin, 500)

const handleThirdPartyLogin = (type) => {
  if (loading.value) return
  ElMessage.info(`${type === 'wechat' ? '微信' : 'QQ'}登录功能暂未开放`)
}
</script>

<style scoped>
.login-page {
  min-height: calc(100vh - 60px);
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f7;
  padding: 60px 20px;
}

.login-wrapper {
  width: 100%;
  max-width: 400px;
}

.login-header {
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

.login-card {
  background-color: #ffffff;
  border-radius: 18px;
  box-shadow: 0 2px 16px rgba(0, 0, 0, 0.04);
  padding: 40px;
}

.login-form {
  margin-bottom: 28px;
}

.login-form :deep(.el-form-item) {
  margin-bottom: 24px;
}

.login-form :deep(.el-input__wrapper) {
  border-radius: 12px !important;
  border: 1px solid #d2d2d7 !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
  padding: 0 16px !important;
  box-sizing: border-box !important;
  background: #f5f5f7 !important;
}

.login-form :deep(.el-input__wrapper:hover) {
  border-color: #86868b !important;
}

.login-form :deep(.el-input__wrapper.is-focus) {
  border-color: #0071e3 !important;
  box-shadow: 0 0 0 4px rgba(0, 113, 227, 0.15) !important;
  background: #ffffff !important;
}

.login-form :deep(.el-input__inner) {
  border: none !important;
  outline: none !important;
  box-shadow: none !important;
  color: #1d1d1f !important;
  font-size: 15px !important;
  letter-spacing: -0.2px;
}

.login-form :deep(.el-input__inner::placeholder) {
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

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.forgot-link {
  color: #0071e3;
  font-size: 14px;
  font-weight: 500;
  text-decoration: none;
  transition: color 0.2s;
  letter-spacing: -0.2px;
}

.forgot-link:hover {
  color: #0077ed;
  text-decoration: underline;
}

.login-btn {
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

.login-btn:hover {
  transform: translateY(-1px);
  background: #f5f5f7;
}

.login-btn:active {
  transform: translateY(0);
}

.login-footer {
  text-align: center;
  color: #6e6e73;
  font-size: 14px;
  letter-spacing: -0.2px;
}

.register-link {
  color: #0071e3;
  font-weight: 600;
  margin-left: 4px;
  text-decoration: none;
  transition: color 0.2s;
}

.register-link:hover {
  color: #0077ed;
  text-decoration: underline;
}

.divider {
  position: relative;
  text-align: center;
  margin: 32px 0;
}

.divider::before {
  content: '';
  position: absolute;
  left: 0;
  right: 0;
  top: 50%;
  height: 1px;
  background: #d2d2d7;
}

.divider span {
  position: relative;
  background-color: #ffffff;
  padding: 0 20px;
  color: #86868b;
  font-size: 13px;
  font-weight: 500;
  letter-spacing: -0.2px;
}

.third-party-login {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.third-party-btn {
  padding: 14px 16px;
  border: 1px solid #d2d2d7;
  border-radius: 12px;
  background-color: #ffffff;
  color: #1d1d1f;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  letter-spacing: -0.2px;
}

.third-party-btn:hover {
  background-color: #f5f5f7;
  border-color: #86868b;
  transform: translateY(-1px);
}

.third-party-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
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