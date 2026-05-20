<template>
  <div class="my-page">
    <div class="container">
      <div class="my-layout">
        <!-- 左侧栏 -->
        <aside class="left-aside">
          <!-- 用户信息卡片 -->
          <div class="user-info-card">
            <div class="avatar-section">
              <div class="avatar">
                <el-avatar :size="80" :src="userStore.user?.avatar">
                  <svg width="80" height="80" viewBox="0 0 100 100" xmlns="http://www.w3.org/2000/svg">
                    <circle cx="50" cy="50" r="50" fill="#e0e0e0"/>
                    <circle cx="50" cy="38" r="14" fill="#ffffff"/>
                    <path d="M50 52 C 30 52, 22 70, 22 86 L 78 86 C 78 70, 70 52, 50 52 Z" fill="#ffffff"/>
                  </svg>
                </el-avatar>
              </div>
              <h3 class="username">{{ userStore.user?.nickname || userStore.user?.username }}</h3>
              <p class="user-email">{{ userStore.user?.email || '暂未设置邮箱' }}</p>
            </div>

            <div class="info-list">
              <div class="info-item">
                <span class="info-label">手机号码</span>
                <span class="info-value">{{ userStore.user?.phone || '未设置' }}</span>
                <button class="edit-btn" @click="handleEditPhone">
                  <el-icon><Edit /></el-icon>
                </button>
              </div>
            </div>

            <div class="logout-section">
              <button class="logout-btn" :disabled="loggingOut" @click="handleLogout">
                <el-icon v-if="!loggingOut"><SwitchButton /></el-icon>
                <el-icon v-else class="loading-icon"><Loading /></el-icon>
                {{ loggingOut ? '退出中...' : '退出登录' }}
              </button>
            </div>
          </div>

          <!-- 快捷入口卡片 -->
          <div class="quick-access-card">
            <div class="card-header">
              <h2 class="card-title">快捷入口</h2>
            </div>
            <div class="access-list">
              <router-link to="/orders" class="access-item">
                <div class="access-icon">
                  <el-icon><ShoppingCart /></el-icon>
                </div>
                <span>我的订单</span>
                <el-icon class="arrow"><Right /></el-icon>
                <el-badge :value="orderStats.unpaid + orderStats.unshipped + orderStats.unreceived" :hidden="(orderStats.unpaid + orderStats.unshipped + orderStats.unreceived) === 0" class="badge" />
              </router-link>
              <router-link to="/favorites" class="access-item">
                <div class="access-icon">
                  <el-icon><Star /></el-icon>
                </div>
                <span>我的收藏</span>
                <el-icon class="arrow"><Right /></el-icon>
              </router-link>
              <router-link to="/address" class="access-item">
                <div class="access-icon">
                  <el-icon><Location /></el-icon>
                </div>
                <span>收货地址</span>
                <el-icon class="arrow"><Right /></el-icon>
              </router-link>
              <router-link to="/settings" class="access-item">
                <div class="access-icon">
                  <el-icon><Setting /></el-icon>
                </div>
                <span>账号设置</span>
                <el-icon class="arrow"><Right /></el-icon>
              </router-link>
            </div>
          </div>
        </aside>

        <!-- 右侧主要内容区 -->
        <main class="main-content">
          <!-- 我的订单卡片 -->
          <div class="orders-card">
            <div class="card-header">
              <h2 class="card-title">我的订单</h2>
              <router-link to="/orders" class="view-all">
                查看全部
                <el-icon><Right /></el-icon>
              </router-link>
            </div>
            <div class="order-stats">
              <div class="stat-item" @click="router.push('/orders?status=0')">
                <div class="stat-number pending-pay">{{ orderStats.unpaid }}</div>
                <div class="stat-label">待支付</div>
              </div>
              <div class="stat-item" @click="router.push('/orders?status=1')">
                <div class="stat-number pending-ship">{{ orderStats.unshipped }}</div>
                <div class="stat-label">待发货</div>
              </div>
              <div class="stat-item" @click="router.push('/orders?status=2')">
                <div class="stat-number pending-receive">{{ orderStats.unreceived }}</div>
                <div class="stat-label">待收货</div>
              </div>
              <div class="stat-item" @click="router.push('/orders?status=3')">
                <div class="stat-number completed">{{ orderStats.completed }}</div>
                <div class="stat-label">已完成</div>
              </div>
            </div>
          </div>

          <!-- 最近浏览卡片 -->
          <div class="recent-view-card">
            <div class="card-header">
              <h2 class="card-title">最近浏览</h2>
              <button v-if="browseHistory.length > 0" class="clear-btn" @click="handleClearHistory">
                <el-icon><Delete /></el-icon>
                <span>清空</span>
              </button>
            </div>
            <div v-if="browseHistory.length === 0" class="empty-state">
              <el-icon :size="48"><Clock /></el-icon>
              <p>暂无浏览记录</p>
            </div>
            <div v-else>
              <div class="history-list">
                <router-link 
                  v-for="item in paginatedHistory" 
                  :key="item.id" 
                  :to="`/product/${item.id}`"
                  class="history-item"
                >
                  <div class="history-image">
                    <img :src="item.image || defaultImage" :alt="item.name" @error="($event) => $event.target.src = defaultImage" />
                  </div>
                  <div class="history-content">
                    <div class="history-brand">{{ item.brand }}</div>
                    <h3 class="history-name">{{ item.name }}</h3>
                    <div class="history-meta">
                      <div class="rating">
                        <el-icon class="star-icon"><Star /></el-icon>
                        <span>{{ getIntegerRating(item.rating) }}</span>
                      </div>
                      <span class="separator">|</span>
                      <span class="sales">已售 {{ item.sales || 0 }}</span>
                    </div>
                    <div class="history-tags">
                      <span v-if="item.capacity" class="tag">{{ item.capacity }}</span>
                      <span v-if="item.energyLevel" class="tag energy-tag">{{ item.energyLevel }}</span>
                    </div>
                    <div class="history-price-container">
                      <span class="history-price">¥{{ formatPrice(item.price) }}</span>
                    </div>
                  </div>
                </router-link>
              </div>
              <div v-if="totalHistoryPages > 1" class="history-pagination">
                <button 
                  class="pagination-btn" 
                  :disabled="currentHistoryPage === 1" 
                  @click="currentHistoryPage--"
                >
                  上一页
                </button>
                <span class="pagination-info">{{ currentHistoryPage }} / {{ totalHistoryPages }}</span>
                <button 
                  class="pagination-btn" 
                  :disabled="currentHistoryPage === totalHistoryPages" 
                  @click="currentHistoryPage++"
                >
                  下一页
                </button>
              </div>
            </div>
          </div>

          <!-- 猜你喜欢卡片 -->
          <div class="recommend-card">
            <div class="card-header">
              <h2 class="card-title">猜你喜欢</h2>
              <button class="refresh-btn" @click="loadRecommendProducts" :disabled="recommendLoading">
                <el-icon :class="{ 'loading-icon': recommendLoading }"><Refresh /></el-icon>
                <span>换一批</span>
              </button>
            </div>
            <div v-if="recommendLoading" class="recommend-loading">
              <el-icon class="loading-icon"><Loading /></el-icon>
              <span>加载中...</span>
            </div>
            <div v-else-if="recommendProducts.length === 0" class="empty-state">
              <el-icon :size="48"><Document /></el-icon>
              <p>暂无推荐商品</p>
            </div>
            <div v-else class="recommend-list">
              <router-link 
                v-for="product in recommendProducts" 
                :key="product.id" 
                :to="`/product/${product.id}`"
                class="recommend-item"
              >
                <div class="recommend-image">
                  <img :src="product.image || defaultImage" :alt="product.name" @error="($event) => $event.target.src = defaultImage" />
                </div>
                <div class="recommend-info">
                  <p class="recommend-brand">{{ product.brand }}</p>
                  <p class="recommend-name">{{ product.name }}</p>
                  <div class="recommend-meta">
                    <span class="recommend-price">¥{{ formatPrice(product.price) }}</span>
                    <span class="recommend-sales">已售 {{ product.sales || 0 }}</span>
                  </div>
                </div>
              </router-link>
            </div>
          </div>
        </main>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Right, SwitchButton, ShoppingCart, Star, Location, Setting, Document, Edit, Loading, Refresh, Delete, Clock } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { getOrderStats } from '@/api/order'
import { getRandomProducts } from '@/api/product'
import { useBrowseHistory } from '@/composables/useBrowseHistory'
import { ref, computed, watch, onMounted } from 'vue'
import defaultImage from '@/assets/images/fridge.jpg'

const router = useRouter()
const userStore = useUserStore()
const { browseHistory, clearHistory } = useBrowseHistory()

const HISTORY_PAGE_SIZE = 6
const currentHistoryPage = ref(1)

const totalHistoryPages = computed(() => Math.ceil(browseHistory.value.length / HISTORY_PAGE_SIZE))

const paginatedHistory = computed(() => {
  const start = (currentHistoryPage.value - 1) * HISTORY_PAGE_SIZE
  const end = start + HISTORY_PAGE_SIZE
  return browseHistory.value.slice(start, end)
})

watch(browseHistory, () => {
  if (currentHistoryPage.value > totalHistoryPages.value) {
    currentHistoryPage.value = Math.max(1, totalHistoryPages.value)
  }
})

const loggingOut = ref(false)
const recommendLoading = ref(false)
const recommendProducts = ref([])

const orderStats = ref({
  unpaid: 0,
  unshipped: 0,
  unreceived: 0,
  completed: 0
})

const loadOrderStats = async () => {
  try {
    const res = await getOrderStats()
    if (res.code === 200) {
      orderStats.value = res.data
    }
  } catch (error) {
    console.error('获取订单统计失败:', error)
  }
}

const loadRecommendProducts = async () => {
  recommendLoading.value = true
  try {
    const res = await getRandomProducts(3)
    if (res.code === 200) {
      recommendProducts.value = res.data || []
    }
  } catch (error) {
    console.error('获取推荐商品失败:', error)
    recommendProducts.value = []
  } finally {
    recommendLoading.value = false
  }
}

const formatPrice = (price) => {
  return Number(price).toLocaleString()
}

const getIntegerRating = (rating) => {
  return Math.floor(rating || 0)
}

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '退出确认', {
      confirmButtonText: '确定退出',
      cancelButtonText: '取消',
      type: 'warning'
    })
    loggingOut.value = true
    userStore.logout()
    router.push('/login')
    ElMessage.success('已退出登录')
  } catch {
  } finally {
    loggingOut.value = false
  }
}

const handleEditPhone = () => {
  ElMessage.info('手机号修改功能暂未开放')
}

const handleClearHistory = () => {
  ElMessageBox.confirm('确定要清空浏览记录吗？', '清空确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    clearHistory()
    ElMessage.success('浏览记录已清空')
  }).catch(() => {})
}

onMounted(() => {
  loadOrderStats()
  loadRecommendProducts()
})
</script>

<style scoped>
.my-page {
  padding: 24px 0;
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 16px;
}

.my-layout {
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: 24px;
}

.left-aside {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.user-info-card {
  background-color: #ffffff;
  border-radius: 12px;
  padding: 32px;
}

.avatar-section {
  text-align: center;
  padding-bottom: 24px;
  border-bottom: 1px solid #e5e7eb;
  margin-bottom: 24px;
}

.avatar {
  margin-bottom: 16px;
}

.username {
  font-size: 20px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 8px;
}

.user-email {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
}

.info-list {
  margin-bottom: 24px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f3f4f6;
}

.info-item:last-child {
  border-bottom: none;
}

.edit-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  padding: 0;
  background: none;
  border: 1px solid var(--border);
  border-radius: 6px;
  color: var(--muted-foreground);
  cursor: pointer;
  transition: all 0.2s;
}

.edit-btn:hover {
  border-color: var(--blue-primary);
  color: var(--blue-primary);
  background-color: #eff6ff;
}

.info-label {
  font-size: 14px;
  color: #6b7280;
}

.info-value {
  font-size: 14px;
  color: #1f2937;
  font-weight: 500;
}

.member-level {
  color: #d97706;
}

.points {
  color: #2563eb;
}

.logout-section {
  text-align: center;
  padding-top: 16px;
  border-top: 1px solid #e5e7eb;
}

.logout-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 10px 24px;
  background: none;
  border: 1px solid #ef4444;
  border-radius: 8px;
  color: #ef4444;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.logout-btn:hover:not(:disabled) {
  background-color: #fef2f2;
}

.logout-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.loading-icon {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.main-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.orders-card,
.recent-view-card,
.quick-access-card,
.recommend-card {
  background-color: #ffffff;
  border-radius: 12px;
  padding: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
}

.view-all {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #2563eb;
  font-size: 14px;
  text-decoration: none;
  transition: color 0.2s;
}

.view-all:hover {
  color: #1d4ed8;
}

.order-stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.stat-item {
  text-align: center;
  padding: 20px;
  background-color: #f9fafb;
  border-radius: 8px;
  transition: background-color 0.2s;
}

.stat-item:hover {
  background-color: #f3f4f6;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #6b7280;
}

.pending-pay {
  color: #f97316;
}

.pending-ship {
  color: #2563eb;
}

.pending-receive {
  color: #8b5cf6;
}

.completed {
  color: #10b981;
}

.empty-state {
  text-align: center;
  padding: 48px 24px;
  color: #9ca3af;
}

.empty-state .el-icon {
  margin-bottom: 16px;
}

.empty-state p {
  margin: 0;
  font-size: 14px;
}

.access-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.access-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background-color: #f9fafb;
  border-radius: 8px;
  text-decoration: none;
  transition: all 0.2s;
  position: relative;
}

.access-item:hover {
  background-color: #f3f4f6;
  transform: translateX(4px);
}

.access-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #dbeafe;
  border-radius: 8px;
  color: #2563eb;
}

.access-item span {
  flex: 1;
  font-size: 14px;
  color: #1f2937;
  font-weight: 500;
}

.arrow {
  color: #9ca3af;
  font-size: 16px;
}

.badge {
  position: absolute;
  right: 40px;
  top: 12px;
}

.history-list {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.history-pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid var(--border);
}

.pagination-btn {
  padding: 8px 16px;
  background-color: #ffffff;
  border: 1px solid var(--border);
  border-radius: 8px;
  font-size: 13px;
  color: var(--foreground);
  cursor: pointer;
  transition: all 0.2s ease;
}

.pagination-btn:hover:not(:disabled) {
  border-color: var(--blue-primary);
  color: var(--blue-primary);
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination-info {
  font-size: 14px;
  color: #86868b;
}

.history-item {
  background-color: #ffffff;
  border-radius: 1.25rem;
  border: 1px solid var(--border);
  overflow: hidden;
  transition: all 0.3s ease;
  text-decoration: none;
  display: block;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04);
}

.history-item:hover {
  box-shadow: 0 16px 48px rgba(0, 0, 0, 0.1);
  transform: translateY(-6px);
  border-color: transparent;
}

.history-image {
  width: 100%;
  padding-top: 100%;
  position: relative;
  overflow: hidden;
  background-color: #f5f5f7;
}

.history-image img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.history-item:hover .history-image img {
  transform: scale(1.05);
}

.history-content {
  padding: 16px;
}

.history-brand {
  font-size: 12px;
  color: var(--muted-foreground);
  margin-bottom: 4px;
}

.history-name {
  font-size: 14px;
  font-weight: 500;
  color: var(--foreground);
  margin: 0 0 8px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.4;
}

.history-item:hover .history-name {
  color: var(--blue-primary);
}

.history-meta {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-bottom: 8px;
  font-size: 12px;
}

.rating {
  display: flex;
  align-items: center;
  gap: 2px;
  color: #6b7280;
}

.star-icon {
  color: #fbbf24;
  fill: #fbbf24;
}

.separator {
  color: #9ca3af;
}

.sales {
  color: #6b7280;
}

.history-tags {
  display: flex;
  gap: 4px;
  margin-bottom: 12px;
}

.tag {
  font-size: 12px;
  color: #6b7280;
}

.energy-tag {
  background-color: #dcfce7;
  color: #166534;
  padding: 2px 6px;
  border-radius: 4px;
}

.history-price-container {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.history-price {
  font-size: 22px;
  font-weight: 800;
  color: #ef4444;
}

.recommend-list {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.recommend-item {
  background-color: #f9fafb;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  text-decoration: none;
  display: block;
}

.recommend-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.1);
}

.recommend-image {
  width: 100%;
  height: 160px;
  background-color: #f5f5f7;
  overflow: hidden;
}

.recommend-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.recommend-item:hover .recommend-image img {
  transform: scale(1.05);
}

.recommend-info {
  padding: 16px;
}

.recommend-brand {
  font-size: 12px;
  color: #86868b;
  margin: 0 0 4px;
  font-weight: 500;
}

.recommend-name {
  font-size: 14px;
  color: #1d1d1f;
  margin: 0 0 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-weight: 600;
  line-height: 1.4;
}

.recommend-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.recommend-price {
  font-size: 16px;
  color: #1d1d1f;
  font-weight: 700;
}

.recommend-sales {
  font-size: 12px;
  color: #86868b;
}

.refresh-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: #f5f5f7;
  border: none;
  border-radius: 20px;
  color: #1d1d1f;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.refresh-btn:hover:not(:disabled) {
  background: #e8e8ed;
}

.refresh-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.refresh-btn .loading-icon {
  animation: spin 1s linear infinite;
}

.recommend-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 24px;
  color: #86868b;
  gap: 12px;
}

.recommend-loading .loading-icon {
  font-size: 24px;
  animation: spin 1s linear infinite;
}

@media (max-width: 1024px) {
  .my-layout {
    grid-template-columns: 1fr;
  }

  .order-stats {
    grid-template-columns: repeat(4, 1fr);
  }

  .recommend-list {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .order-stats {
    grid-template-columns: repeat(2, 1fr);
  }

  .access-list {
    grid-template-columns: 1fr;
  }

  .recommend-list {
    grid-template-columns: 1fr;
  }
}
</style>
