<template>
  <div class="admin-layout">
    <el-container>
      <el-aside width="220px" class="admin-aside">
        <div class="logo">
          <el-icon :size="24"><Refrigerator /></el-icon>
          <span>管理后台</span>
        </div>
        <el-menu
          :default-active="activeMenu"
          :default-openeds="defaultOpeneds"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409eff"
          router
        >
          <el-menu-item index="/admin/dashboard">
            <el-icon><DataAnalysis /></el-icon>
            <span>数据概览</span>
          </el-menu-item>
          
          <el-sub-menu index="product">
            <template #title>
              <el-icon><Goods /></el-icon>
              <span>商品管理</span>
            </template>
            <el-menu-item index="/admin/products">商品列表</el-menu-item>
            <el-menu-item index="/admin/categories">分类管理</el-menu-item>
          </el-sub-menu>
          
          <el-sub-menu index="order">
            <template #title>
              <el-icon><List /></el-icon>
              <span>订单管理</span>
            </template>
            <el-menu-item index="/admin/orders">订单列表</el-menu-item>
            <el-menu-item index="/admin/orders/pending">待处理订单</el-menu-item>
          </el-sub-menu>
          
          <el-sub-menu index="user">
            <template #title>
              <el-icon><User /></el-icon>
              <span>用户管理</span>
            </template>
            <el-menu-item index="/admin/users">用户列表</el-menu-item>
            <el-menu-item index="/admin/reviews">评价管理</el-menu-item>
          </el-sub-menu>
          
          <el-sub-menu index="statistics">
            <template #title>
              <el-icon><TrendCharts /></el-icon>
              <span>数据统计</span>
            </template>
            <el-menu-item index="/admin/statistics">销售统计</el-menu-item>
            <el-menu-item index="/admin/statistics/products">商品统计</el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-aside>

      <el-container>
        <el-header class="admin-header">
          <div class="header-left">
            <el-breadcrumb separator="/">
              <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">首页</el-breadcrumb-item>
              <el-breadcrumb-item>{{ currentPageTitle }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          <div class="header-right">
            <el-dropdown trigger="click" @command="handleCommand">
              <span class="user-info">
                <el-avatar :size="32">{{ userStore.userInfo?.username?.charAt(0)?.toUpperCase() }}</el-avatar>
                <span class="username">{{ userStore.userInfo?.username }}</span>
                <el-icon><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="home">
                    <span><el-icon><House /></el-icon>返回首页</span>
                  </el-dropdown-item>
                  <el-dropdown-item command="logout" divided>
                    <span><el-icon><SwitchButton /></el-icon>退出登录</span>
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>

        <el-main class="admin-main">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { 
  DataAnalysis, 
  Goods, 
  List, 
  User, 
  TrendCharts, 
  ArrowDown, 
  House, 
  SwitchButton,
  Refrigerator
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

const defaultOpeneds = ref(['product', 'order', 'user', 'statistics'])

const pageTitles = {
  '/admin/dashboard': '数据概览',
  '/admin/products': '商品列表',
  '/admin/categories': '分类管理',
  '/admin/orders': '订单列表',
  '/admin/orders/pending': '待处理订单',
  '/admin/users': '用户列表',
  '/admin/reviews': '评价管理',
  '/admin/statistics': '销售统计',
  '/admin/statistics/products': '商品统计'
}

const currentPageTitle = computed(() => pageTitles[route.path] || '管理后台')

const handleCommand = (command) => {
  switch (command) {
    case 'home':
      router.push('/home')
      break
    case 'logout':
      userStore.logout()
      break
  }
}
</script>

<style scoped>
.admin-layout {
  height: 100vh;
  width: 100vw;
}

.admin-layout .el-container {
  height: 100%;
  width: 100%;
}

.admin-aside {
  background-color: #304156;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  background-color: #263445;
}

.el-menu {
  border-right: none;
}

.admin-header {
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.username {
  color: #333;
}

.admin-main {
  background-color: #f0f2f5;
  padding: 20px;
}

:deep(.el-sub-menu__title) {
  background-color: #304156 !important;
}

:deep(.el-sub-menu__title:hover) {
  background-color: #263445 !important;
}

:deep(.el-menu-item.is-active) {
  background-color: #263445 !important;
}
</style>
