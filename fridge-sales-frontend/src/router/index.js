import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { usePermissionStore } from '@/stores/permission'
import { constantRoutes, asyncRoutes } from './routes'

const router = createRouter({
  history: createWebHistory(),
  routes: [...constantRoutes, ...asyncRoutes],
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0 }
    }
  }
})

router.beforeEach(async (to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 冰箱销售系统` : '冰箱销售系统'
  
  const userStore = useUserStore()
  const permissionStore = usePermissionStore()

  if (userStore.isLoggedIn) {
    if (permissionStore.routes.length === 0) {
      const roles = userStore.isAdmin ? ['admin'] : ['user']
      await permissionStore.generateRoutes(roles)
    }
  }

  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
    return
  }
  
  // 检查是否是管理员路由（包括子路由）
  const isAdminRoute = to.meta.requiresAdmin || to.path.startsWith('/admin')
  if (isAdminRoute && !userStore.isAdmin) {
    next({ name: 'Home' })
    return
  }
  
  if ((to.name === 'Login' || to.name === 'Register') && userStore.isLoggedIn) {
    next({ name: 'Home' })
    return
  }
  
  next()
})

export default router
