<template>
  <div class="my-page">
    <div class="container">
      <div class="my-layout">
        <!-- 左侧用户信息卡片 -->
        <aside class="user-info-card">
          <div class="avatar-section">
            <div class="avatar">
              <el-avatar :size="80" :src="userStore.userInfo?.avatar">
                {{ userStore.userInfo?.username?.charAt(0)?.toUpperCase() }}
              </el-avatar>
            </div>
            <h3 class="username">{{ userStore.userInfo?.username }}</h3>
            <p class="user-email">{{ userStore.userInfo?.email || '暂未设置邮箱' }}</p>
          </div>

          <div class="info-list">
            <div class="info-item">
              <span class="info-label">手机号码</span>
              <span class="info-value">{{ userStore.userInfo?.phone || '未设置' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">会员等级</span>
              <span class="info-value member-level">普通会员</span>
            </div>
            <div class="info-item">
              <span class="info-label">积分余额</span>
              <span class="info-value points">1,280</span>
            </div>
          </div>

          <div class="logout-section">
            <button class="logout-btn" @click="handleLogout">
              <el-icon><SwitchButton /></el-icon>
              退出登录
            </button>
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
              <div class="stat-item">
                <div class="stat-number pending-pay">1</div>
                <div class="stat-label">待支付</div>
              </div>
              <div class="stat-item">
                <div class="stat-number pending-ship">1</div>
                <div class="stat-label">待发货</div>
              </div>
              <div class="stat-item">
                <div class="stat-number pending-receive">1</div>
                <div class="stat-label">待收货</div>
              </div>
              <div class="stat-item">
                <div class="stat-number completed">0</div>
                <div class="stat-label">已完成</div>
              </div>
            </div>
          </div>

          <!-- 最近浏览卡片 -->
          <div class="recent-view-card">
            <div class="card-header">
              <h2 class="card-title">最近浏览</h2>
            </div>
            <div class="empty-state">
              <el-icon :size="48"><Document /></el-icon>
              <p>暂无浏览记录</p>
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
                <el-badge :value="3" :hidden="false" class="badge" />
              </router-link>
              <router-link to="/favorites" class="access-item">
                <div class="access-icon">
                  <el-icon><Star /></el-icon>
                </div>
                <span>我的收藏</span>
                <el-icon class="arrow"><Right /></el-icon>
              </router-link>
              <router-link to="/addresses" class="access-item">
                <div class="access-icon">
                  <el-icon><Location /></el-icon>
                </div>
                <span>收货地址</span>
                <el-icon class="arrow"><Right /></el-icon>
              </router-link>
              <router-link to="/account-settings" class="access-item">
                <div class="access-icon">
                  <el-icon><Setting /></el-icon>
                </div>
                <span>账号设置</span>
                <el-icon class="arrow"><Right /></el-icon>
              </router-link>
            </div>
          </div>

          <!-- 猜你喜欢卡片 -->
          <div class="recommend-card">
            <div class="card-header">
              <h2 class="card-title">猜你喜欢</h2>
            </div>
            <div class="recommend-list">
              <div v-for="i in 3" :key="i" class="recommend-item">
                <div class="recommend-image"></div>
                <div class="recommend-info">
                  <p class="recommend-name">推荐商品 {{ i }}</p>
                  <p class="recommend-price">¥1,999</p>
                </div>
              </div>
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
import { Right, SwitchButton, ShoppingCart, Star, Location, Setting, Document } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    userStore.logout()
    router.push('/login')
    ElMessage.success('已退出登录')
  } catch {
    // 用户取消操作
  }
}
</script>

<style scoped>
.my-page {
  padding: 24px 0;
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.my-layout {
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: 24px;
}

/* 用户信息卡片 */
.user-info-card {
  background-color: #ffffff;
  border-radius: 12px;
  padding: 32px;
  height: fit-content;
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

.logout-btn:hover {
  background-color: #fef2f2;
}

/* 主内容区 */
.main-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 通用卡片样式 */
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

/* 订单统计 */
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

/* 最近浏览 */
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

/* 快捷入口 */
.access-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
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

/* 推荐商品 */
.recommend-list {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.recommend-item {
  background-color: #f9fafb;
  border-radius: 8px;
  overflow: hidden;
  transition: transform 0.2s, box-shadow 0.2s;
}

.recommend-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1);
}

.recommend-image {
  width: 100%;
  height: 160px;
  background-color: #e5e7eb;
}

.recommend-info {
  padding: 12px;
}

.recommend-name {
  font-size: 14px;
  color: #1f2937;
  margin: 0 0 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.recommend-price {
  font-size: 16px;
  color: #ef4444;
  font-weight: bold;
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .my-layout {
    grid-template-columns: 1fr;
  }

  .order-stats {
    grid-template-columns: repeat(4, 1fr);
  }

  .access-list {
    grid-template-columns: repeat(2, 1fr);
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
