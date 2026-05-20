<template>
  <div class="home-page">
    <div class="banner-section">
      <div
        v-for="(banner, index) in banners"
        :key="index"
        class="banner-slide"
        :class="{ active: currentBannerIndex === index }"
      >
        <img :src="banner.image" :alt="banner.title" class="banner-image" />
        <div class="banner-overlay">
          <div class="banner-content">
            <h2 class="banner-title">{{ banner.title }}</h2>
            <p class="banner-subtitle">{{ banner.subtitle }}</p>
            <router-link to="/products" class="banner-button">
              立即选购
              <el-icon><Right /></el-icon>
            </router-link>
          </div>
        </div>
      </div>

      <div class="banner-indicators">
        <button
          v-for="(_, index) in banners"
          :key="index"
          class="indicator"
          :class="{ active: currentBannerIndex === index }"
          @click="currentBannerIndex = index"
        />
      </div>
    </div>

    <section class="services-section">
      <div class="container">
        <div class="services-grid">
          <div class="service-item">
            <div class="service-icon blue">
              <el-icon><Van /></el-icon>
            </div>
            <div class="service-content">
              <div class="service-title">全国包邮</div>
              <div class="service-desc">满199元免运费</div>
            </div>
          </div>
          <div class="service-item">
            <div class="service-icon green">
              <el-icon><CircleCheck /></el-icon>
            </div>
            <div class="service-content">
              <div class="service-title">正品保障</div>
              <div class="service-desc">品牌官方授权</div>
            </div>
          </div>
          <div class="service-item">
            <div class="service-icon orange">
              <el-icon><Service /></el-icon>
            </div>
            <div class="service-content">
              <div class="service-title">品质认证</div>
              <div class="service-desc">国家质检通过</div>
            </div>
          </div>
          <div class="service-item">
            <div class="service-icon red">
              <el-icon><Refresh /></el-icon>
            </div>
            <div class="service-content">
              <div class="service-title">7天退换</div>
              <div class="service-desc">无忧购物体验</div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="categories-section">
      <div class="container">
        <div class="section-with-sidebar">
          <div class="sidebar-promo left-promo">
            <div class="promo-card">
              <div class="promo-icon">🎁</div>
              <div class="promo-title">新人专享</div>
              <div class="promo-desc">首单立减200元</div>
              <router-link to="/register" class="promo-btn">立即领取</router-link>
            </div>
          </div>
          
          <div class="main-content">
            <div class="section-header">
              <div>
                <h2 class="section-title">商品分类</h2>
                <p class="section-subtitle">全方位满足您的选购需求</p>
              </div>
              <router-link to="/products" class="section-more">
                查看全部
                <el-icon><Right /></el-icon>
              </router-link>
            </div>
            <div class="categories-grid">
              <router-link to="/products?category=对开门" class="category-card">
                <div class="category-icon blue">
                  <el-icon><Refrigerator /></el-icon>
                </div>
                <div class="category-name">对开门</div>
                <div class="category-desc">大容量双开门</div>
                <div class="category-count">12款在售</div>
              </router-link>
              <router-link to="/products?category=法式多门" class="category-card">
                <div class="category-icon purple">
                  <el-icon><Grid /></el-icon>
                </div>
                <div class="category-name">法式多门</div>
                <div class="category-desc">欧式精致设计</div>
                <div class="category-count">8款在售</div>
              </router-link>
              <router-link to="/products?category=三门" class="category-card">
                <div class="category-icon green">
                  <el-icon><Box /></el-icon>
                </div>
                <div class="category-name">三门</div>
                <div class="category-desc">经典实用三区</div>
                <div class="category-count">15款在售</div>
              </router-link>
              <router-link to="/products?category=智能冰箱" class="category-card">
                <div class="category-icon cyan">
                  <el-icon><Monitor /></el-icon>
                </div>
                <div class="category-name">智能冰箱</div>
                <div class="category-desc">AI 智慧管理</div>
                <div class="category-count">6款在售</div>
              </router-link>
              <router-link to="/products?category=嵌入式" class="category-card">
                <div class="category-icon red">
                  <el-icon><Box /></el-icon>
                </div>
                <div class="category-name">嵌入式</div>
                <div class="category-desc">完美融入橱柜</div>
                <div class="category-count">5款在售</div>
              </router-link>
              <router-link to="/products?category=单门" class="category-card">
                <div class="category-icon orange">
                  <el-icon><Box /></el-icon>
                </div>
                <div class="category-name">单门</div>
                <div class="category-desc">小巧省空间</div>
                <div class="category-count">10款在售</div>
              </router-link>
            </div>
          </div>
          
          <div class="sidebar-promo right-promo">
            <div class="promo-card">
              <div class="promo-icon">🚚</div>
              <div class="promo-title">免费配送</div>
              <div class="promo-desc">全国包邮上门</div>
              <router-link to="/products" class="promo-btn">查看详情</router-link>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="products-section">
      <div class="container">
        <div class="section-with-sidebar">
          <div class="sidebar-promo left-promo">
            <div class="promo-card highlight">
              <div class="promo-icon">⚡</div>
              <div class="promo-title">闪购特惠</div>
              <div class="promo-desc">每日10点开抢</div>
              <router-link to="/products" class="promo-btn">立即抢购</router-link>
            </div>
            <div class="promo-card">
              <div class="promo-icon">💎</div>
              <div class="promo-title">会员专享</div>
              <div class="promo-desc">积分兑好礼</div>
              <router-link to="/profile" class="promo-btn">查看详情</router-link>
            </div>
          </div>
          
          <div class="main-content">
            <div class="section-header">
              <h2 class="section-title">热销推荐</h2>
              <router-link to="/products" class="section-more">
                查看更多
                <el-icon><Right /></el-icon>
              </router-link>
            </div>
            <div v-loading="loading" class="products-grid">
              <ProductCard
                v-for="product in hotProducts"
                :key="product.id"
                :product="product"
                @addCart="handleAddCart"
                @favorite="handleFavorite"
              />
            </div>
          </div>
          
          <div class="sidebar-promo right-promo">
            <div class="rank-card">
              <div class="rank-header">
                <span class="rank-icon">🏆</span>
                <span class="rank-title">热销榜</span>
              </div>
              <div class="rank-list">
                <div class="rank-item">
                  <span class="rank-num top">1</span>
                  <span class="rank-name">海尔对开门</span>
                </div>
                <div class="rank-item">
                  <span class="rank-num top">2</span>
                  <span class="rank-name">美的法式多门</span>
                </div>
                <div class="rank-item">
                  <span class="rank-num top">3</span>
                  <span class="rank-name">西门子智能</span>
                </div>
                <div class="rank-item">
                  <span class="rank-num">4</span>
                  <span class="rank-name">小米智能冰箱</span>
                </div>
                <div class="rank-item">
                  <span class="rank-num">5</span>
                  <span class="rank-name">容声三门</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="products-section white-bg">
      <div class="container">
        <div class="section-with-sidebar">
          <div class="sidebar-promo left-promo">
            <div class="service-card">
              <div class="service-header">服务保障</div>
              <div class="service-list">
                <div class="service-item">
                  <span class="service-icon">✓</span>
                  <span>正品保障</span>
                </div>
                <div class="service-item">
                  <span class="service-icon">✓</span>
                  <span>全国联保</span>
                </div>
                <div class="service-item">
                  <span class="service-icon">✓</span>
                  <span>免费安装</span>
                </div>
                <div class="service-item">
                  <span class="service-icon">✓</span>
                  <span>7天退换</span>
                </div>
              </div>
            </div>
          </div>
          
          <div class="main-content">
            <div class="section-header">
              <h2 class="section-title">新品上市</h2>
              <router-link to="/products" class="section-more">
                查看更多
                <el-icon><Right /></el-icon>
              </router-link>
            </div>
            <div v-loading="loading" class="products-grid">
              <ProductCard
                v-for="product in newProducts"
                :key="product.id"
                :product="product"
                @addCart="handleAddCart"
                @favorite="handleFavorite"
              />
            </div>
          </div>
          
          <div class="sidebar-promo right-promo">
            <div class="news-card">
              <div class="news-header">最新资讯</div>
              <div class="news-list">
                <div class="news-item">
                  <span class="news-dot"></span>
                  <span>2026冰箱选购指南</span>
                </div>
                <div class="news-item">
                  <span class="news-dot"></span>
                  <span>智能冰箱功能解析</span>
                </div>
                <div class="news-item">
                  <span class="news-dot"></span>
                  <span>节能冰箱推荐榜</span>
                </div>
                <div class="news-item">
                  <span class="news-dot"></span>
                  <span>冰箱保养小技巧</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="brands-section">
      <div class="container">
        <h2 class="section-title">热门品牌</h2>
        <div class="brands-grid">
          <router-link
            v-for="brand in brands"
            :key="brand"
            :to="`/products?brand=${brand}`"
            class="brand-item"
          >
            <div class="brand-name">{{ brand }}</div>
          </router-link>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Right, Van, Service, CircleCheck, Refresh, Refrigerator, Grid, Box, Monitor } from '@element-plus/icons-vue'
import ProductCard from '@/components/common/ProductCard.vue'
import { getHotProducts, getRandomProducts } from '@/api/product'

const router = useRouter()

const currentBannerIndex = ref(0)
const favorites = ref([])
const loading = ref(false)

const banners = [
  {
    image: 'https://picsum.photos/seed/fridge1/1920/600',
    title: '智能变频 节能新选择',
    subtitle: '一级能效冰箱，全场特惠'
  },
  {
    image: 'https://picsum.photos/seed/fridge2/1920/600',
    title: '法式多门 精致生活',
    subtitle: '高端品质，限时优惠'
  },
  {
    image: 'https://picsum.photos/seed/fridge3/1920/600',
    title: '智能大屏 科技新体验',
    subtitle: '触控屏冰箱，引领未来'
  }
]

const hotProducts = ref([])
const newProducts = ref([])

const brands = ['海尔', '美的', '西门子', '三星', '小米', '容声', '海信', 'SMEG']

let bannerTimer = null

const fetchHomeData = async () => {
  loading.value = true
  try {
    console.log('[操作日志] [阶段 2][获取首页商品] 时间：' + Date.now() + ' | 参数：{limit: 8}')
    const [hotRes, newRes] = await Promise.all([
      getHotProducts(8),
      getRandomProducts(8)
    ])
    hotProducts.value = hotRes.data || []
    newProducts.value = newRes.data || []
    console.log('[操作日志] [阶段 4][获取首页商品成功] 时间：' + Date.now() + ' | 参数：{limit: 8} | 结果：热门商品' + hotProducts.value.length + '个，新品' + newProducts.value.length + '个')
  } catch (error) {
    console.error('[操作日志] [失败][阶段 2][获取首页商品] 时间：' + Date.now() + ' | 原因：' + error.message + ' | 参数：{limit: 8}')
    ElMessage.error('获取商品数据失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  bannerTimer = setInterval(() => {
    currentBannerIndex.value = (currentBannerIndex.value + 1) % banners.length
  }, 5000)

  const stored = localStorage.getItem('favorites')
  if (stored) {
    favorites.value = JSON.parse(stored)
  }
  
  fetchHomeData()
})

onUnmounted(() => {
  if (bannerTimer) {
    clearInterval(bannerTimer)
  }
})

const handleAddCart = (product) => {
  const cart = JSON.parse(localStorage.getItem('cart') || '[]')
  const existingItem = cart.find(item => item.productId === product.id)

  if (existingItem) {
    existingItem.quantity += 1
  } else {
    cart.push({ productId: product.id, quantity: 1 })
  }

  localStorage.setItem('cart', JSON.stringify(cart))
}

const handleFavorite = ({ product, isFavorite: isFav }) => {
  let newFavorites
  if (isFav) {
    newFavorites = favorites.value.filter(id => id !== product.id)
  } else {
    newFavorites = [...favorites.value, product.id]
  }
  favorites.value = newFavorites
  localStorage.setItem('favorites', JSON.stringify(newFavorites))
}
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  background-color: #f3f4f6;
}

.banner-section {
  position: relative;
  height: 400px;
  overflow: hidden;
}

@media (min-width: 768px) {
  .banner-section {
    height: 500px;
  }
}

.banner-slide {
  position: absolute;
  inset: 0;
  opacity: 0;
  transition: opacity 1s;
}

.banner-slide.active {
  opacity: 1;
}

.banner-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.banner-overlay {
  position: absolute;
  inset: 0;
  background-color: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
}

.banner-content {
  text-align: center;
  color: #ffffff;
}

.banner-title {
  font-size: 2.25rem;
  font-weight: bold;
  margin-bottom: 1rem;
}

@media (min-width: 768px) {
  .banner-title {
    font-size: 3.75rem;
  }
}

.banner-subtitle {
  font-size: 1.125rem;
  margin-bottom: 2rem;
}

@media (min-width: 768px) {
  .banner-subtitle {
    font-size: 1.5rem;
  }
}

.banner-button {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  background: #ffffff;
  color: #0071e3;
  padding: 0.875rem 2rem;
  border-radius: 12px;
  text-decoration: none;
  font-weight: 600;
  font-size: 0.95rem;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid transparent;
}

.banner-button:hover {
  transform: translateY(-2px);
  background: #f5f5f7;
  border-color: #0071e3;
}

.banner-indicators {
  position: absolute;
  bottom: 1rem;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 0.5rem;
}

.indicator {
  width: 8px;
  height: 8px;
  min-width: 8px;
  min-height: 8px;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.4);
  border: none;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  padding: 0;
}

.indicator.active {
  background-color: #ffffff;
  width: 28px;
  height: 8px;
  min-width: 28px;
  min-height: 8px;
  border-radius: 4px;
  box-shadow: 0 0 8px rgba(255, 255, 255, 0.5);
}

.services-section {
  background-color: #ffffff;
  padding: 2rem 0;
  border-bottom: 1px solid var(--border);
}

.services-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1rem;
  justify-items: center;
  max-width: 1000px;
  margin: 0 auto;
}

@media (max-width: 768px) {
  .services-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

.service-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.service-icon {
  width: 3rem;
  height: 3rem;
  border-radius: 9999px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.service-icon.blue {
  background-color: #dbeafe;
  color: var(--blue-primary);
}

.service-icon.green {
  background-color: #dcfce7;
  color: #16a34a;
}

.service-icon.purple {
  background-color: #f3e8ff;
  color: #9333ea;
}

.service-icon.orange {
  background-color: #ffedd5;
  color: #ea580c;
}

.service-icon.red {
  background-color: #fee2e2;
  color: #dc2626;
}

.service-icon.cyan {
  background-color: #cffafe;
  color: #0891b2;
}

.service-icon.purple {
  background-color: #f3e8ff;
  color: #9333ea;
}

.service-icon .el-icon {
  width: 1.5rem;
  height: 1.5rem;
}

.service-title {
  font-weight: 500;
  color: var(--foreground);
}

.service-desc {
  font-size: 0.875rem;
  color: var(--muted-foreground);
}

.categories-section {
  padding: 3rem 0;
  background: linear-gradient(180deg, #ffffff 0%, #f8fafc 100%);
}

.section-with-sidebar {
  display: flex;
  gap: 1.5rem;
  align-items: flex-start;
}

.sidebar-promo {
  width: 180px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.main-content {
  flex: 1;
  min-width: 0;
}

.promo-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 1.25rem;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  border: 1px solid #e2e8f0;
  transition: all 0.3s;
}

.promo-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

.promo-card.highlight {
  background: #ffffff;
  color: #1e293b;
  position: relative;
  border: none;
}

.promo-badge {
  position: absolute;
  top: 8px;
  right: 8px;
  background: #ff3b30;
  color: #ffffff;
  font-size: 10px;
  padding: 2px 6px;
  border-radius: 4px;
  font-weight: 600;
}

.promo-icon {
  font-size: 2rem;
  margin-bottom: 0.5rem;
}

.promo-title {
  font-size: 1rem;
  font-weight: 600;
  margin-bottom: 0.25rem;
}

.promo-desc {
  font-size: 0.75rem;
  opacity: 0.8;
  margin-bottom: 0.75rem;
}

.promo-btn {
  display: inline-block;
  padding: 0.375rem 1rem;
  background: #0071e3;
  color: #ffffff;
  border-radius: 9999px;
  font-size: 0.75rem;
  font-weight: 500;
  text-decoration: none;
  transition: all 0.2s;
}

.promo-btn:hover {
  background: #0077ed;
  transform: scale(1.02);
}

.promo-card.highlight .promo-btn {
  background: #0071e3;
  color: #ffffff;
}

.flash-sale-card {
  background: #ff6b6b;
  border-radius: 12px;
  padding: 1.25rem;
  color: #ffffff;
  text-align: center;
}

.flash-header {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  margin-bottom: 1rem;
}

.flash-icon {
  font-size: 1.25rem;
}

.flash-title {
  font-size: 1rem;
  font-weight: 600;
}

.flash-timer {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.25rem;
  margin-bottom: 1rem;
}

.timer-item {
  background: rgba(255, 255, 255, 0.2);
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-weight: 600;
  font-size: 0.875rem;
}

.timer-sep {
  font-weight: 600;
}

.flash-products {
  margin-bottom: 1rem;
}

.flash-item {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

.flash-price {
  font-size: 1.25rem;
  font-weight: 700;
}

.flash-original {
  font-size: 0.75rem;
  text-decoration: line-through;
  opacity: 0.7;
}

.flash-btn {
  display: inline-block;
  padding: 0.5rem 1.5rem;
  background: #ffffff;
  color: #ff6b6b;
  border-radius: 9999px;
  font-size: 0.75rem;
  font-weight: 600;
  text-decoration: none;
  transition: all 0.2s;
}

.flash-btn:hover {
  transform: scale(1.05);
}

.rank-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 1.25rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  border: 1px solid #e2e8f0;
}

.rank-header {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 1rem;
  padding-bottom: 0.75rem;
  border-bottom: 1px solid #e2e8f0;
}

.rank-icon {
  font-size: 1.25rem;
}

.rank-title {
  font-size: 1rem;
  font-weight: 600;
  color: #1e293b;
}

.rank-list {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.rank-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.375rem 0;
}

.rank-num {
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #e2e8f0;
  color: #64748b;
  border-radius: 4px;
  font-size: 0.75rem;
  font-weight: 600;
}

.rank-num.top {
  background: #0071e3;
  color: #ffffff;
}

.rank-name {
  font-size: 0.875rem;
  color: #374151;
}

.service-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 1.25rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  border: 1px solid #e2e8f0;
}

.service-header {
  font-size: 1rem;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 1rem;
  padding-bottom: 0.75rem;
  border-bottom: 1px solid #e2e8f0;
}

.service-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.service-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.875rem;
  color: #374151;
}

.service-icon {
  color: #10b981;
  font-weight: 600;
}

.news-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 1.25rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  border: 1px solid #e2e8f0;
}

.news-header {
  font-size: 1rem;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 1rem;
  padding-bottom: 0.75rem;
  border-bottom: 1px solid #e2e8f0;
}

.news-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.news-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.875rem;
  color: #374151;
  cursor: pointer;
  transition: color 0.2s;
}

.news-item:hover {
  color: #0071e3;
}

.news-dot {
  width: 6px;
  height: 6px;
  background: #0071e3;
  border-radius: 50%;
  flex-shrink: 0;
}

@media (max-width: 1200px) {
  .sidebar-promo {
    width: 150px;
  }
}

@media (max-width: 1024px) {
  .sidebar-promo {
    display: none;
  }
  
  .section-with-sidebar {
    display: block;
  }
}

.products-section {
  padding: 3rem 0;
}

.products-section.white-bg {
  background-color: #ffffff;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 2rem;
}

.section-title {
  font-size: 1.5rem;
  font-weight: bold;
  color: var(--foreground);
  margin-bottom: 0.25rem;
  position: relative;
  padding-left: 1rem;
}

.section-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 2rem;
  background: #0071e3;
  border-radius: 9999px;
}

.section-subtitle {
  font-size: 0.875rem;
  color: var(--muted-foreground);
}

.categories-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1.25rem;
}

@media (min-width: 640px) {
  .categories-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (min-width: 1024px) {
  .categories-grid {
    grid-template-columns: repeat(6, 1fr);
  }
}

.category-card {
  background: #ffffff;
  border-radius: 1.25rem;
  padding: 1.5rem 1rem;
  text-align: center;
  text-decoration: none;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.75rem;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  border: 1px solid transparent;
}

.category-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.08);
  border-color: #e2e8f0;
}

.category-icon {
  width: 3.5rem;
  height: 3.5rem;
  border-radius: 1rem;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.category-card:hover .category-icon {
  transform: scale(1.1);
}

.category-icon.blue {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
}

.category-icon.purple {
  background: linear-gradient(135deg, #a855f7 0%, #7c3aed 100%);
}

.category-icon.green {
  background: linear-gradient(135deg, #22c55e 0%, #16a34a 100%);
}

.category-icon.cyan {
  background: linear-gradient(135deg, #06b6d4 0%, #0891b2 100%);
}

.category-icon.red {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
}

.category-icon.orange {
  background: linear-gradient(135deg, #f97316 0%, #ea580c 100%);
}

.category-icon .el-icon {
  width: 1.75rem;
  height: 1.75rem;
  color: #ffffff;
}

.category-name {
  font-weight: 600;
  font-size: 0.95rem;
  color: var(--foreground);
}

.category-desc {
  font-size: 0.75rem;
  color: var(--muted-foreground);
}

.category-count {
  font-size: 0.7rem;
  color: #94a3b8;
  background: #f1f5f9;
  padding: 0.25rem 0.75rem;
  border-radius: 9999px;
}

.section-more {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  color: var(--blue-primary);
  text-decoration: none;
  font-size: 0.875rem;
  transition: color 0.2s;
}

.section-more:hover {
  color: var(--blue-hover);
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(1, 1fr);
  gap: 1.5rem;
}

@media (min-width: 640px) {
  .products-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (min-width: 1024px) {
  .products-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}

.brands-section {
  padding: 3rem 0;
}

.brands-section .section-title {
  margin-bottom: 1.5rem;
}

.brands-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1rem;
}

@media (min-width: 768px) {
  .brands-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}

.brand-item {
  background-color: #ffffff;
  border: 1px solid var(--border);
  border-radius: var(--radius);
  padding: 1.5rem;
  text-align: center;
  text-decoration: none;
  transition: box-shadow 0.3s, transform 0.3s;
}

.brand-item:hover {
  box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.brand-name {
  font-size: 1.25rem;
  font-weight: bold;
  color: var(--foreground);
}
</style>
