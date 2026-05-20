import Layout from '@/layout/index.vue'

export const constantRoutes = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/user/Home.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/products',
    name: 'Products',
    component: () => import('@/views/user/Products.vue'),
    meta: { title: '商品列表' }
  },
  {
    path: '/product/:id',
    name: 'ProductDetail',
    component: () => import('@/views/user/ProductDetail.vue'),
    meta: { title: '商品详情' }
  },
  {
    path: '/cart',
    name: 'Cart',
    component: () => import('@/views/user/Cart.vue'),
    meta: { title: '购物车', requiresAuth: true }
  },
  {
    path: '/checkout',
    name: 'Checkout',
    component: () => import('@/views/user/Checkout.vue'),
    meta: { title: '确认订单', requiresAuth: true }
  },
  {
    path: '/favorites',
    name: 'Favorites',
    component: () => import('@/views/user/Favorites.vue'),
    meta: { title: '我的收藏', requiresAuth: true }
  },
  {
    path: '/address',
    name: 'Address',
    component: () => import('@/views/user/Address.vue'),
    meta: { title: '收货地址', requiresAuth: true }
  },
  {
    path: '/orders',
    name: 'Orders',
    component: () => import('@/views/user/Orders.vue'),
    meta: { title: '我的订单', requiresAuth: true }
  },
  {
    path: '/order-detail/:id',
    name: 'OrderDetail',
    component: () => import('@/views/user/OrderDetail.vue'),
    meta: { title: '订单详情', requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/user/Profile.vue'),
    meta: { title: '个人中心', requiresAuth: true }
  },
  {
    path: '/settings',
    name: 'Settings',
    component: () => import('@/views/user/Settings.vue'),
    meta: { title: '账号设置', requiresAuth: true }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/user/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/user/Register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/forgot-password',
    name: 'ForgotPassword',
    component: () => import('@/views/user/ForgotPassword.vue'),
    meta: { title: '忘记密码' }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue'),
    meta: { title: '页面不存在' }
  }
]

export const asyncRoutes = [
  {
    path: '/admin',
    component: Layout,
    redirect: '/admin/dashboard',
    name: 'Admin',
    meta: { title: '管理后台', icon: 'Refrigerator', requiresAuth: true, requiresAdmin: true },
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: { title: '数据概览', icon: 'DataAnalysis' }
      },
      {
        path: 'product',
        name: 'AdminProduct',
        redirect: '/admin/products',
        meta: { title: '商品管理', icon: 'Goods' },
        children: [
          {
            path: 'products',
            name: 'AdminProducts',
            component: () => import('@/views/admin/product/index.vue'),
            meta: { title: '商品列表', icon: 'Goods' }
          },
          {
            path: 'categories',
            name: 'AdminCategories',
            component: () => import('@/views/admin/Categories.vue'),
            meta: { title: '分类管理', icon: 'Files' }
          },
          {
            path: 'brands',
            name: 'AdminBrands',
            component: () => import('@/views/admin/Brand.vue'),
            meta: { title: '品牌管理', icon: 'Medal' }
          }
        ]
      },
      {
        path: 'order',
        name: 'AdminOrder',
        meta: { title: '订单管理', icon: 'List' },
        children: [
          {
            path: 'orders',
            name: 'AdminOrders',
            component: () => import('@/views/admin/order/index.vue'),
            meta: { title: '订单列表', icon: 'List' }
          },
          {
            path: 'pending',
            name: 'AdminPendingOrders',
            component: () => import('@/views/admin/PendingOrders.vue'),
            meta: { title: '待处理订单', icon: 'Timer' }
          }
        ]
      },
      {
        path: 'order-detail/:id',
        component: () => import('@/views/admin/order/detail.vue'),
        hidden: true
      },
      {
        path: 'user',
        name: 'AdminUser',
        meta: { title: '用户管理', icon: 'User' },
        children: [
          {
            path: 'users',
            name: 'AdminUsers',
            component: () => import('@/views/admin/user/index.vue'),
            meta: { title: '用户列表', icon: 'User' }
          },
          {
            path: 'reviews',
            name: 'AdminReviews',
            component: () => import('@/views/admin/Reviews.vue'),
            meta: { title: '评价管理', icon: 'ChatLineSquare' }
          }
        ]
      },
      {
        path: 'user-detail/:id',
        component: () => import('@/views/admin/user/detail.vue'),
        hidden: true
      },
      {
        path: 'statistics',
        name: 'AdminStatisticsGroup',
        redirect: '/admin/statistics/sales',
        meta: { title: '数据统计', icon: 'TrendCharts' },
        children: [
          {
            path: 'sales',
            name: 'AdminSalesStatistics',
            component: () => import('@/views/admin/Statistics.vue'),
            meta: { title: '销售统计', icon: 'TrendCharts' }
          },
          {
            path: 'products',
            name: 'AdminProductStatistics',
            component: () => import('@/views/admin/ProductStatistics.vue'),
            meta: { title: '商品统计', icon: 'PieChart' }
          }
        ]
      },
      {
        path: 'permissions',
        name: 'AdminPermissions',
        component: () => import('@/views/admin/permission/index.vue'),
        meta: { title: '权限管理', icon: 'Lock' }
      },
      {
        path: 'inventory',
        name: 'AdminInventory',
        component: () => import('@/views/admin/inventory/index.vue'),
        meta: { title: '库存管理', icon: 'Box' }
      }
    ]
  }
]
