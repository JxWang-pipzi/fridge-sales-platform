<template>
  <nav class="navbar" role="navigation" aria-label="主导航">
    <a href="#main-content" class="skip-link">跳转到主要内容</a>
    <div class="navbar-container">
      <div class="navbar-top">
        <router-link to="/" class="logo" aria-label="返回首页">
          <div class="logo-icon">
            <span class="logo-text">冰</span>
          </div>
          <span class="brand-name">优选冰箱</span>
        </router-link>

        <div class="search-box" role="search">
          <label for="search-input" class="sr-only">搜索冰箱型号、品牌</label>
          <input
            id="search-input"
            type="search"
            placeholder="搜索冰箱型号、品牌..."
            v-model="searchQuery"
            @keyup.enter="handleSearch"
            aria-label="搜索商品"
          />
          <button class="search-btn" @click="handleSearch" aria-label="搜索" type="button">
            <el-icon><Search /></el-icon>
          </button>
        </div>

        <div class="navbar-actions">
          <template v-if="userStore.isLoggedIn">
            <router-link to="/favorites" class="action-link" aria-label="我的收藏">
              <el-icon><Star /></el-icon>
              <span>收藏</span>
            </router-link>

            <router-link to="/cart" class="action-link cart-link" aria-label="购物车">
              <el-icon><ShoppingCart /></el-icon>
              <span>购物车</span>
              <span v-if="cartCount > 0" class="cart-badge" aria-label="购物车商品数量">{{ cartCount }}</span>
            </router-link>

            <div 
              class="dropdown-wrapper" 
              @mouseenter="showDropdown = true" 
              @mouseleave="showDropdown = false"
              @keydown.esc="showDropdown = false"
            >
              <button 
                class="action-link dropdown-trigger" 
                :class="{ active: showDropdown }"
                @click="showDropdown = !showDropdown"
                @keydown.enter="showDropdown = !showDropdown"
                @keydown.space.prevent="showDropdown = !showDropdown"
                aria-expanded="showDropdown"
                aria-haspopup="true"
                aria-label="我的账户菜单"
                type="button"
              >
                <el-icon><User /></el-icon>
                <span>我的</span>
                <el-icon class="dropdown-arrow"><ArrowDown /></el-icon>
              </button>
              <transition name="el-fade-in-linear">
                <div v-show="showDropdown" class="dropdown-menu" role="menu">
                  <router-link to="/profile" class="dropdown-item" role="menuitem" tabindex="0">
                    <el-icon><User /></el-icon>
                    <span>个人中心</span>
                  </router-link>
                  <router-link to="/orders" class="dropdown-item" role="menuitem" tabindex="0">
                    <el-icon><Document /></el-icon>
                    <span>我的订单</span>
                  </router-link>
                  <router-link to="/favorites" class="dropdown-item" role="menuitem" tabindex="0">
                    <el-icon><Star /></el-icon>
                    <span>我的收藏</span>
                  </router-link>
                  <div class="dropdown-divider" role="separator"></div>
                  <router-link v-if="userStore.isAdmin" to="/admin" class="dropdown-item" role="menuitem" tabindex="0">
                    <el-icon><Setting /></el-icon>
                    <span>后台管理</span>
                  </router-link>
                  <button class="dropdown-item logout-btn" @click="handleLogout" role="menuitem" type="button">
                    <el-icon><SwitchButton /></el-icon>
                    <span>退出登录</span>
                  </button>
                </div>
              </transition>
            </div>
          </template>

          <template v-else>
            <router-link to="/login" class="action-link">登录</router-link>
            <router-link to="/register" class="action-link register-btn">注册</router-link>
          </template>
        </div>
      </div>

      <div class="navbar-categories" role="navigation" aria-label="商品分类导航">
        <router-link 
          to="/home" 
          class="category-link" 
          :class="{ active: route.path === '/home' }"
          aria-current="page"
        >
          首页
        </router-link>
        <router-link 
          to="/products" 
          class="category-link" 
          :class="{ active: route.path === '/products' && !route.query.category }"
          aria-current="page"
        >
          全部商品
        </router-link>
        <router-link 
          v-for="category in categories" 
          :key="category.id" 
          :to="`/products?category=${category.name}`" 
          class="category-link"
          :class="{ active: route.query.category === category.name }"
          :aria-current="route.query.category === category.name ? 'page' : null"
        >
          {{ category.name }}
        </router-link>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'
import { getCategoryList } from '@/api/category'
import { Search, Star, ShoppingCart, User, ArrowDown, Document, SwitchButton, Setting } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()

const searchQuery = ref('')
const showDropdown = ref(false)
const categories = ref([])

const cartCount = ref(cartStore.cartCount)

watch(() => cartStore.cartCount, (newCount) => {
  cartCount.value = newCount
})

watch(() => userStore.isLoggedIn, (isLoggedIn) => {
  if (isLoggedIn) {
    cartStore.fetchCartCount()
  } else {
    cartStore.resetCart()
    cartCount.value = 0
  }
})

onMounted(async () => {
  if (userStore.isLoggedIn) {
    cartStore.fetchCartCount()
  }
  
  try {
    const res = await getCategoryList()
    categories.value = res.data
  } catch (error) {
    console.error('获取分类失败', error)
  }
})

const isActive = (path) => {
  return route.path === path || route.path.startsWith(path + '/')
}

const handleSearch = () => {
  if (searchQuery.value.trim()) {
    router.push({ path: '/products', query: { keyword: searchQuery.value } })
  }
}

const handleLogout = async () => {
  try {
    await userStore.logout()
    router.push('/login')
  } catch (error) {
    console.error('退出登录失败:', error)
  }
}
</script>

<style scoped>
.navbar {
  background-color: #ffffff;
  border-bottom: 1px solid var(--border);
  position: sticky;
  top: 0;
  z-index: 50;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.navbar-container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 16px;
}

.navbar-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  text-decoration: none;
  min-height: 48px;
  padding: 8px;
  border-radius: 8px;
}

.logo:focus-visible {
  outline: 2px solid var(--blue-primary);
  outline-offset: 2px;
}

.logo-icon {
  width: 36px;
  height: 36px;
  background: #0071e3;
  border-radius: 0.75rem;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(0, 113, 227, 0.25);
}

.logo-text {
  color: #ffffff;
  font-weight: bold;
  font-size: 16px;
}

.brand-name {
  font-size: 20px;
  font-weight: bold;
  color: var(--foreground);
}

.search-box {
  flex: 1;
  max-width: 480px;
  margin: 0 64px;
  position: relative;
}

.search-box input {
  width: 100%;
  padding: 12px 52px 12px 18px;
  border: 1px solid #e2e8f0;
  border-radius: 9999px;
  font-size: 14px;
  transition: all 0.3s;
  min-height: 44px;
  background: #f8fafc;
}

.search-box input:focus {
  border-color: #3b82f6;
  outline: none;
  box-shadow: 0 0 0 4px rgba(37, 99, 235, 0.1);
  background: #ffffff;
}

.search-btn {
  position: absolute;
  right: 6px;
  top: 50%;
  transform: translateY(-50%);
  background: #ffffff;
  border: 1px solid #d2d2d7;
  color: #0071e3;
  cursor: pointer;
  padding: 8px;
  min-width: 32px;
  min-height: 32px;
  transition: all 0.3s;
  border-radius: 9999px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.search-btn:hover {
  transform: translateY(-50%) scale(1.05);
  border-color: #0071e3;
}

.search-btn:focus-visible {
  outline: 2px solid var(--blue-primary);
  outline-offset: 2px;
}

.navbar-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.action-link {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #0071e3;
  text-decoration: none;
  font-size: 14px;
  transition: all 0.2s;
  min-height: 48px;
  padding: 12px 16px;
  border-radius: 12px;
  cursor: pointer;
  border: 1px solid #d2d2d7;
  background: transparent;
  font-family: inherit;
}

.action-link:hover {
  color: #0071e3;
  border-color: #0071e3;
}

.action-link:focus-visible {
  outline: 2px solid var(--blue-primary);
  outline-offset: 2px;
}

.cart-link {
  position: relative;
}

.cart-badge {
  position: absolute;
  top: 4px;
  right: 0;
  background-color: var(--destructive);
  color: #ffffff;
  font-size: 12px;
  min-width: 20px;
  height: 20px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 6px;
}

.register-btn {
  background: #ffffff;
  color: #0071e3;
  padding: 10px 18px;
  border-radius: 12px;
  transition: all 0.3s;
  font-weight: 600;
  font-size: 13px;
}

.register-btn:hover {
  transform: translateY(-2px);
  border-color: #0071e3;
  background: #f5f5f7;
}

.dropdown-wrapper {
  position: relative;
}

.dropdown-trigger {
  cursor: pointer;
  position: relative;
}

.dropdown-trigger.active {
  color: var(--blue-primary);
}

.dropdown-arrow {
  margin-left: 4px;
  font-size: 12px;
  transition: transform 0.3s;
}

.dropdown-trigger.active .dropdown-arrow {
  transform: rotate(180deg);
}

.dropdown-menu {
  position: absolute;
  top: 100%;
  right: 0;
  margin-top: 8px;
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  min-width: 160px;
  padding: 8px 0;
  z-index: 1000;
  border: 1px solid #e5e7eb;
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  color: #374151;
  text-decoration: none;
  font-size: 14px;
  transition: background-color 0.2s;
  cursor: pointer;
  border: none;
  background: transparent;
  width: 100%;
  text-align: left;
  font-family: inherit;
  min-height: 48px;
}

.dropdown-item:hover {
  background-color: #f3f4f6;
  color: var(--blue-primary);
}

.dropdown-item:focus-visible {
  outline: 2px solid var(--blue-primary);
  outline-offset: -2px;
  background-color: #f3f4f6;
}

.dropdown-item .el-icon {
  font-size: 16px;
}

.dropdown-divider {
  height: 1px;
  background-color: #e5e7eb;
  margin: 8px 0;
}

.logout-btn {
  color: #ef4444;
}

.logout-btn:hover {
  background-color: #fef2f2;
  color: #dc2626;
}

.navbar-categories {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px 0;
  border-top: 1px solid var(--border);
}

.category-link {
  color: #374151;
  text-decoration: none;
  font-size: 14px;
  transition: color 0.2s;
  padding: 12px 8px;
  min-height: 48px;
  border-radius: 8px;
}

.category-link:hover,
.category-link.active {
  color: var(--blue-primary);
  font-weight: 600;
}

.category-link.active {
  position: relative;
}

.category-link.active::after {
  content: '';
  position: absolute;
  bottom: 4px;
  left: 50%;
  transform: translateX(-50%);
  width: 40px;
  height: 3px;
  background: #0071e3;
  border-radius: 9999px;
}

.category-link:focus-visible {
  outline: 2px solid var(--blue-primary);
  outline-offset: 2px;
}
</style>
