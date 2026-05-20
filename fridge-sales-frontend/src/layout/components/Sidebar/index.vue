<template>
  <div class="sidebar-container">
    <div class="logo">
      <el-icon :size="24"><Refrigerator /></el-icon>
      <span>管理后台</span>
    </div>
    <el-scrollbar wrap-class="scrollbar-wrapper">
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :default-openeds="defaultOpeneds"
        background-color="#304156"
        text-color="#bfcbd9"
        :unique-opened="false"
        active-text-color="#409eff"
        :collapse-transition="false"
        mode="vertical"
      >
        <sidebar-item v-for="route in permission_routes" :key="route.path" :item="route" :base-path="basePath" />
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { usePermissionStore } from '@/stores/permission'
import SidebarItem from './SidebarItem.vue'

const route = useRoute()
const router = useRouter()
const permissionStore = usePermissionStore()

const isCollapse = false
const basePath = '/admin'
const defaultOpeneds = ['product', 'order', 'user', 'statistics']

const permission_routes = computed(() => {
  const adminRoute = permissionStore.routes.find(r => r.path === '/admin')
  return adminRoute ? adminRoute.children : []
})

const activeMenu = computed(() => {
  const { meta, path } = route
  if (meta.activeMenu) {
    return meta.activeMenu
  }
  return path
})
</script>

<style lang="scss" scoped>
.sidebar-container {
  transition: width 0.28s;
  width: 210px !important;
  background-color: #304156;
  height: 100%;
  position: fixed;
  font-size: 0px;
  top: 0;
  bottom: 0;
  left: 0;
  z-index: 1001;
  overflow: visible;

  .logo {
    height: 50px;
    line-height: 50px;
    text-align: center;
    color: #fff;
    font-size: 16px;
    font-weight: 600;
    overflow: hidden;
    background-color: #2b2f3a;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
  }

  .el-scrollbar {
    height: calc(100% - 50px);
    overflow: visible;
  }

  :deep(.el-scrollbar__view) {
    height: 100%;
    overflow: visible;
  }

  :deep(.el-scrollbar__wrap) {
    overflow: visible;
  }

  :deep(.el-menu) {
    border: none;
    height: 100%;
    width: 100% !important;
  }
}
</style>
