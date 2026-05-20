<template>
  <div class="product-detail-page">
    <div class="container">
      <Breadcrumb :items="breadcrumbItems" />
      
      <div v-loading="loading" class="product-content">
        <div class="product-main">
          <div class="product-gallery">
            <div class="main-image" @click="openImagePreview">
              <img :src="currentImage || defaultImage" :alt="product.name" @error="handleImageError" />
              <div class="preview-hint">
                <el-icon><ZoomIn /></el-icon>
                <span>点击预览</span>
              </div>
            </div>
            <div v-if="product.images?.length" class="thumbnail-list">
              <div
                v-for="(img, index) in product.images"
                :key="index"
                class="thumbnail-item"
                :class="{ active: currentImage === img }"
                @click="currentImage = img"
              >
                <img :src="img" :alt="`${product.name}-${index}`" @error="handleImageError" />
              </div>
            </div>
          </div>

          <div class="product-info">
            <div class="product-brand">{{ product.brand || parsedName.brand }}</div>
            <h1 class="product-name">{{ displayName }}</h1>
            <div v-if="displaySku" class="product-sku">SKU: {{ displaySku }}</div>
            
            <div class="rating-section">
              <div class="stars">
                <el-icon v-for="i in 5" :key="i" class="star-icon" :class="{ filled: i <= getIntegerRating(product.rating) }">
                  <Star />
                </el-icon>
              </div>
              <span class="rating-value">{{ getIntegerRating(product.rating) }}</span>
              <span class="review-count">({{ reviewStats.total || 0 }} 条评价)</span>
              <span class="sales-count">已售 {{ product.sales || 0 }} 件</span>
            </div>

            <div class="price-section">
              <div class="price-row">
                <span class="current-price">¥{{ formatPrice(product.price) }}</span>
              </div>
            </div>

            <div class="specs-section">
              <div class="spec-row">
                <span class="spec-label">型号</span>
                <span class="spec-value">{{ product.model || '标准款' }}</span>
              </div>
              <div class="spec-row">
                <span class="spec-label">容量</span>
                <span class="spec-value">{{ product.capacity ? `${product.capacity}L` : '标准容量' }}</span>
              </div>
              <div class="spec-row">
                <span class="spec-label">能效</span>
                <span class="spec-value">
                  <span class="energy-tag">{{ product.energyLevel || '一级能效' }}</span>
                </span>
              </div>
              <div class="spec-row">
                <span class="spec-label">颜色</span>
                <span class="spec-value">{{ product.color || '白色' }}</span>
              </div>
            </div>

            <div class="quantity-section">
              <span class="quantity-label">数量</span>
              <div class="quantity-control">
                <button 
                  class="qty-btn" 
                  @click="decreaseQty" 
                  :disabled="quantity <= 1"
                  aria-label="减少数量"
                  type="button"
                >-</button>
                <label for="product-quantity" class="sr-only">商品数量</label>
                <input 
                  id="product-quantity"
                  type="number" 
                  v-model.number="quantity" 
                  class="qty-input" 
                  min="1" 
                  :max="product.stock || 99"
                  aria-label="商品数量"
                />
                <button 
                  class="qty-btn" 
                  @click="increaseQty" 
                  :disabled="quantity >= (product.stock || 99)"
                  aria-label="增加数量"
                  type="button"
                >+</button>
              </div>
              <span class="stock-info">库存 {{ product.stock || 99 }} 件</span>
            </div>

            <div class="action-buttons">
              <button 
                class="add-cart-btn" 
                :disabled="addingToCart" 
                @click="handleAddToCart"
                aria-label="加入购物车"
                type="button"
              >
                <el-icon v-if="!addingToCart"><ShoppingCart /></el-icon>
                <el-icon v-else class="loading-icon"><Loading /></el-icon>
                {{ addingToCart ? '添加中...' : '加入购物车' }}
              </button>
              <button 
                class="buy-now-btn" 
                :disabled="buyingNow" 
                @click="handleBuyNow"
                aria-label="立即购买"
                type="button"
              >
                {{ buyingNow ? '处理中...' : '立即购买' }}
              </button>
            </div>

            <div class="extra-actions">
              <button 
                class="extra-btn" 
                :class="{ active: isFavorite, 'is-loading': togglingFavorite }" 
                :disabled="togglingFavorite" 
                @click="handleFavorite"
                :aria-label="isFavorite ? '取消收藏' : '收藏'"
                :aria-pressed="isFavorite"
                type="button"
              >
                <el-icon v-if="!togglingFavorite">
                  <StarFilled v-if="isFavorite" />
                  <Star v-else />
                </el-icon>
                <el-icon v-else class="loading-icon"><Loading /></el-icon>
                {{ togglingFavorite ? '处理中...' : (isFavorite ? '已收藏' : '收藏') }}
              </button>
              <button 
                class="extra-btn" 
                @click="handleShare"
                aria-label="分享" 
                type="button"
              >
                <el-icon><Share /></el-icon>
                分享
              </button>
            </div>
          </div>
        </div>

        <div class="product-details">
          <div class="tabs-header" role="tablist" aria-label="商品详情标签页">
            <button
              v-for="tab in tabs"
              :key="tab.value"
              class="tab-btn"
              :class="{ active: activeTab === tab.value }"
              @click="activeTab = tab.value"
              :aria-selected="activeTab === tab.value"
              :aria-controls="`tab-panel-${tab.value}`"
              :id="`tab-btn-${tab.value}`"
              role="tab"
              type="button"
            >
              {{ tab.label }}
            </button>
          </div>

          <div class="tab-content">
            <div 
              v-show="activeTab === 'detail'" 
              class="detail-content"
              role="tabpanel"
              id="tab-panel-detail"
              aria-labelledby="tab-btn-detail"
            >
              <div class="detail-section">
                <h3 class="detail-title">商品描述</h3>
                <p class="detail-text">
                  海尔智能变频对开门冰箱，采用最新变频技术，节能静音。配备智能温控系统，精准控温保鲜。大容量设计，满足全家储存需求。
                </p>
              </div>

              <div class="detail-section">
                <h3 class="detail-title">规格参数</h3>
                <div class="specs-grid">
                  <div v-for="(value, key) in productSpecs" :key="key" class="spec-item">
                    <span class="spec-label">{{ key }}</span>
                    <span class="spec-value">{{ value }}</span>
                  </div>
                </div>
              </div>

              <div class="detail-section">
                <h3 class="detail-title">产品特性</h3>
                <ul class="features-list">
                  <li v-for="feature in productFeatures" :key="feature" class="feature-item">
                    {{ feature }}
                  </li>
                </ul>
              </div>
            </div>

            <div 
              v-show="activeTab === 'specs'" 
              class="specs-content"
              role="tabpanel"
              id="tab-panel-specs"
              aria-labelledby="tab-btn-specs"
            >
              <table class="specs-table">
                <tr v-for="(value, key) in productSpecs" :key="key">
                  <td class="spec-name">{{ key }}</td>
                  <td class="spec-value">{{ value }}</td>
                </tr>
              </table>
            </div>

            <div 
              v-show="activeTab === 'reviews'" 
              class="reviews-content"
              role="tabpanel"
              id="tab-panel-reviews"
              aria-labelledby="tab-btn-reviews"
            >
              <div class="reviews-header">
                <div class="rating-summary">
                  <div class="rating-number">{{ reviewStats.avgRating?.toFixed(1) || '0.0' }}</div>
                  <div class="rating-stars">
                    <el-icon v-for="i in 5" :key="i" class="star-icon" :class="{ filled: i <= Math.round(reviewStats.avgRating || 0) }">
                      <Star />
                    </el-icon>
                  </div>
                  <div class="rating-count">{{ reviewStats.total || 0 }} 条评价</div>
                </div>
                <button class="write-review-btn" @click="openReviewDialog">
                  <el-icon><Edit /></el-icon>
                  写评价
                </button>
              </div>
              
              <div v-if="reviewList.length > 0" class="reviews-list">
                <div v-for="review in reviewList" :key="review.id" class="review-item">
                  <div class="review-header">
                    <div class="reviewer-avatar">{{ review.username?.charAt(0) }}</div>
                    <div class="reviewer-info">
                      <span class="reviewer-name">{{ review.username }}</span>
                      <div class="review-rating">
                        <el-icon v-for="i in 5" :key="i" class="star-icon" :class="{ filled: i <= review.rating }">
                          <Star />
                        </el-icon>
                      </div>
                    </div>
                    <span class="review-time">{{ formatReviewTime(review.createTime) }}</span>
                    <button v-if="review.userId === userStore.user?.id" class="delete-review-btn" @click="handleDeleteReview(review.id)">
                      <el-icon><Delete /></el-icon>
                    </button>
                  </div>
                  <p class="review-content">{{ review.content }}</p>
                </div>
              </div>
              <div v-else class="no-reviews">
                <p>暂无评价</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 评价对话框 -->
    <el-dialog 
      v-model="showReviewDialog" 
      title="发表评价" 
      width="500px"
      aria-label="发表评价对话框"
      :close-on-press-escape="true"
      :close-on-click-modal="true"
    >
      <div class="review-form">
        <div class="form-item">
          <label id="rating-label">评分</label>
          <div class="rating-select" role="radiogroup" aria-labelledby="rating-label">
            <el-icon
              v-for="i in 5"
              :key="i"
              class="star-select"
              :class="{ active: i <= reviewForm.rating }"
              @click="reviewForm.rating = i"
              @keydown.enter="reviewForm.rating = i"
              @keydown.space.prevent="reviewForm.rating = i"
              :aria-label="`${i}星`"
              :aria-checked="i === reviewForm.rating"
              role="radio"
              tabindex="0"
            >
              <Star />
            </el-icon>
            <span class="rating-text">{{ reviewForm.rating }} 分</span>
          </div>
        </div>
        <div class="form-item">
          <label for="review-content">评价内容</label>
          <el-input
            id="review-content"
            v-model="reviewForm.content"
            type="textarea"
            :rows="4"
            placeholder="请输入您的评价内容..."
            maxlength="500"
            show-word-limit
            aria-required="true"
          />
        </div>
      </div>
      <template #footer>
        <el-button @click="showReviewDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitReview">提交评价</el-button>
      </template>
    </el-dialog>

    <!-- 图片预览对话框 -->
    <el-dialog v-model="showImagePreview" title="商品图片预览" width="800px" class="image-preview-dialog">
      <div class="preview-container">
        <el-carousel
          ref="carouselRef"
          :initial-index="currentImageIndex"
          indicator-position="outside"
          height="600px"
          @change="handleCarouselChange"
        >
          <el-carousel-item v-for="(img, index) in product.images" :key="index">
            <div class="preview-image-wrapper">
              <img :src="img" :alt="`${product.name}-${index}`" class="preview-image" @error="handleImageError" />
            </div>
          </el-carousel-item>
        </el-carousel>
      </div>
      <div class="preview-thumbnails">
        <div
          v-for="(img, index) in product.images"
          :key="index"
          class="preview-thumb-item"
          :class="{ active: currentImageIndex === index }"
          @click="setPreviewImage(index)"
        >
          <img :src="img" :alt="`${product.name}-${index}`" @error="handleImageError" />
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ShoppingCart, Star, StarFilled, Share, Edit, Delete, ZoomIn, Loading } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'
import { useFavoriteStore } from '@/stores/favorite'
import { getProductDetail } from '@/api/product'
import { getReviewList, getReviewStats, checkHasPurchased, addReview, deleteReview } from '@/api/review'
import { useBrowseHistory } from '@/composables/useBrowseHistory'
import Breadcrumb from '@/components/common/Breadcrumb.vue'
import { parseProductName } from '@/utils/product'
import defaultImage from '@/assets/images/fridge.jpg'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()
const favoriteStore = useFavoriteStore()
const { addToHistory } = useBrowseHistory()

const loading = ref(false)

const product = ref({})
const currentImage = ref('')
const quantity = ref(1)
const activeTab = ref('detail')
const reviews = ref([])
const selectedColor = ref('白色')

const reviewList = ref([])
const reviewStats = ref({ total: 0, avgRating: 0 })
const hasPurchased = ref(false)
const showReviewDialog = ref(false)
const reviewForm = ref({
  rating: 5,
  content: ''
})

const addingToCart = ref(false)
const buyingNow = ref(false)
const togglingFavorite = ref(false)

const showImagePreview = ref(false)
const currentImageIndex = ref(0)
const carouselRef = ref(null)

const isFavorite = computed(() => favoriteStore.isFavorite(product.value.id))

const parsedName = computed(() => parseProductName(product.value.name))

const displayName = computed(() => parsedName.value.displayName)

const displaySku = computed(() => product.value.sku || parsedName.value.sku)

const colorOptions = ['白色', '银色', '黑色', '香槟金']

const breadcrumbItems = computed(() => [
  { name: '首页', path: '/home' },
  { name: '商品列表', path: '/products' },
  { name: product.value.name || '商品详情', path: '' }
])

const tabs = [
  { label: '商品详情', value: 'detail' },
  { label: '规格参数', value: 'specs' },
  { label: '用户评价', value: 'reviews' }
]

const productSpecs = computed(() => ({
  '型号': product.value.model || '-',
  '容量': product.value.capacity ? `${product.value.capacity}L` : '-',
  '能效等级': product.value.energyLevel || '-',
  '颜色': product.value.color || '-',
  '尺寸': product.value.dimensions || '-',
  '品牌': product.value.brand || '-'
}))

const productFeatures = computed(() => {
  const features = []
  if (product.value.description) {
    features.push(product.value.description)
  }
  return features
})

const formatPrice = (price) => {
  return Number(price).toLocaleString()
}

const getIntegerRating = (rating) => {
  return Math.floor(rating || 0)
}

const handleImageError = (e) => {
  e.target.src = defaultImage
}

const openImagePreview = () => {
  if (product.value.images?.length) {
    currentImageIndex.value = product.value.images.indexOf(currentImage.value) || 0
    showImagePreview.value = true
  }
}

const setPreviewImage = (index) => {
  currentImageIndex.value = index
  if (carouselRef.value) {
    carouselRef.value.setActiveItem(index)
  }
}

const handleCarouselChange = (index) => {
  currentImageIndex.value = index
}

const fetchProductDetail = async () => {
  loading.value = true
  try {
    const res = await getProductDetail(route.params.id)
    product.value = res.data || {}
    
    if (product.value.images) {
      try {
        const parsed = JSON.parse(product.value.images)
        product.value.images = Array.isArray(parsed) && parsed.length > 0 ? parsed : [product.value.image || defaultImage]
      } catch {
        product.value.images = product.value.image ? [product.value.image] : [defaultImage]
      }
    } else {
      product.value.images = product.value.image ? [product.value.image] : [defaultImage]
    }
    
    currentImage.value = product.value.images?.[0] || defaultImage
    reviews.value = product.value.reviews || []
    
    addToHistory(product.value)
    
    fetchReviews()
    fetchReviewStats()
    checkPurchased()
  } catch (error) {
    console.error('获取商品详情失败:', error)
    ElMessage.error('获取商品详情失败')
  } finally {
    loading.value = false
  }
}

const decreaseQty = () => {
  if (quantity.value > 1) quantity.value--
}

const increaseQty = () => {
  if (quantity.value < (product.value.stock || 99)) quantity.value++
}

const handleAddToCart = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  try {
    await cartStore.addToCart(product.value, quantity.value)
    ElMessage.success('已添加到购物车')
  } catch (error) {
    console.error('添加购物车失败:', error)
  }
}

const handleBuyNow = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  try {
    await cartStore.addToCart(product.value, quantity.value)
    router.push('/cart')
  } catch (error) {
    console.error('添加购物车失败:', error)
  }
}

const handleFavorite = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  try {
    await favoriteStore.toggleFavorite(product.value.id)
  } catch (error) {
    console.error('收藏操作失败:', error)
  }
}

const fetchReviews = async () => {
  try {
    const res = await getReviewList(product.value.id, 1, 10)
    reviewList.value = res.data?.records || []
  } catch (error) {
    console.error('获取评价列表失败:', error)
  }
}

const fetchReviewStats = async () => {
  try {
    const res = await getReviewStats(product.value.id)
    reviewStats.value = res.data || { total: 0, avgRating: 0 }
  } catch (error) {
    console.error('获取评价统计失败:', error)
  }
}

const checkPurchased = async () => {
  if (!userStore.isLoggedIn) {
    hasPurchased.value = false
    return
  }
  try {
    const res = await checkHasPurchased(product.value.id)
    hasPurchased.value = res.data
  } catch (error) {
    hasPurchased.value = false
  }
}

const openReviewDialog = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  await checkPurchased()
  
  if (!hasPurchased.value) {
    ElMessage.warning('只有购买过此商品的用户才能评价')
    return
  }
  
  showReviewDialog.value = true
}

const handleSubmitReview = async () => {
  if (!reviewForm.value.content.trim()) {
    ElMessage.warning('请输入评价内容')
    return
  }
  
  try {
    await addReview({
      productId: product.value.id,
      rating: reviewForm.value.rating,
      content: reviewForm.value.content
    })
    ElMessage.success('评价成功')
    showReviewDialog.value = false
    reviewForm.value = { rating: 5, content: '' }
    fetchReviews()
    fetchReviewStats()
  } catch (error) {
    ElMessage.error(error.message || '评价失败')
  }
}

const handleDeleteReview = async (reviewId) => {
  try {
    await ElMessageBox.confirm('确定要删除这条评价吗？', '提示', {
      type: 'warning'
    })
    await deleteReview(reviewId)
    ElMessage.success('删除成功')
    fetchReviews()
    fetchReviewStats()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

const formatReviewTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

onMounted(() => {
  fetchProductDetail()
})
</script>

<style scoped>
.product-detail-page {
  padding: 24px 0;
  background-color: #f8fafc;
  min-height: calc(100vh - 60px);
}

.product-content {
  background-color: #ffffff;
  border-radius: 1.25rem;
  border: 1px solid #e2e8f0;
  padding: 32px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04);
}

.product-main {
  display: grid;
  grid-template-columns: 500px 1fr;
  gap: 48px;
  margin-bottom: 48px;
}

.product-gallery {
  position: sticky;
  top: 80px;
}

.main-image {
  width: 100%;
  aspect-ratio: 1;
  background-color: #f9fafb;
  border-radius: var(--radius);
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.main-image img {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.preview-hint {
  position: absolute;
  bottom: 16px;
  left: 50%;
  transform: translateX(-50%);
  background-color: rgba(0, 0, 0, 0.6);
  color: #ffffff;
  padding: 6px 14px;
  border-radius: 9999px;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 6px;
  opacity: 0;
  transition: opacity 0.3s;
}

.main-image:hover .preview-hint {
  opacity: 1;
}

.thumbnail-list {
  display: flex;
  gap: 12px;
  margin-top: 16px;
}

.thumbnail-item {
  width: 72px;
  height: 72px;
  border: 2px solid var(--border);
  border-radius: 8px;
  cursor: pointer;
  overflow: hidden;
  transition: border-color 0.2s;
}

.thumbnail-item.active,
.thumbnail-item:hover {
  border-color: var(--blue-primary);
}

.thumbnail-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-info {
  display: flex;
  flex-direction: column;
}

.product-brand {
  font-size: 14px;
  color: var(--muted-foreground);
  margin-bottom: 8px;
}

.product-name {
  font-size: 24px;
  font-weight: bold;
  color: var(--foreground);
  margin-bottom: 8px;
  line-height: 1.4;
}

.product-sku {
  font-size: 14px;
  color: #9ca3af;
  margin-bottom: 16px;
  font-family: 'Courier New', monospace;
}

.rating-section {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 20px;
}

.stars {
  display: flex;
  gap: 2px;
}

.star-icon {
  color: #d1d5db;
  font-size: 16px;
}

.star-icon.filled {
  color: #fbbf24;
  fill: #fbbf24;
}

.rating-value {
  font-weight: 500;
  color: var(--foreground);
}

.review-count,
.sales-count {
  font-size: 14px;
  color: var(--muted-foreground);
}

.price-section {
  background: linear-gradient(135deg, #fef2f2 0%, #fee2e2 100%);
  padding: 24px;
  border-radius: 1rem;
  margin-bottom: 24px;
}

.price-row {
  display: flex;
  align-items: baseline;
  gap: 12px;
}

.current-price {
  font-size: 32px;
  font-weight: 800;
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.specs-section {
  margin-bottom: 24px;
}

.spec-row {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}

.spec-label {
  font-size: 14px;
  color: var(--muted-foreground);
  width: 60px;
}

.spec-value {
  font-size: 14px;
  color: var(--foreground);
}

.energy-tag {
  background-color: #dcfce7;
  color: #166534;
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
}

.color-options {
  display: flex;
  gap: 8px;
}

.color-btn {
  padding: 6px 16px;
  border: 1px solid var(--border);
  border-radius: 4px;
  background-color: #ffffff;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
}

.color-btn:hover {
  border-color: var(--blue-primary);
}

.color-btn.active {
  border-color: var(--blue-primary);
  background-color: var(--blue-primary);
  color: #ffffff;
}

.quantity-section {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
}

.quantity-label {
  font-size: 14px;
  color: var(--muted-foreground);
  width: 60px;
}

.quantity-control {
  display: flex;
  align-items: center;
}

.qty-btn {
  width: 40px;
  height: 40px;
  min-width: 40px;
  min-height: 40px;
  border: 1px solid var(--border);
  background-color: #ffffff;
  cursor: pointer;
  font-size: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.qty-btn:first-child {
  border-radius: 6px 0 0 6px;
}

.qty-btn:last-child {
  border-radius: 0 6px 6px 0;
}

.qty-btn:hover:not(:disabled) {
  background-color: #f3f4f6;
}

.qty-btn:focus-visible {
  outline: 2px solid var(--blue-primary);
  outline-offset: 2px;
}

.qty-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.qty-input {
  width: 60px;
  height: 40px;
  border: 1px solid var(--border);
  border-left: none;
  border-right: none;
  text-align: center;
  font-size: 14px;
}

.qty-input:focus {
  outline: none;
  border-color: var(--blue-primary);
}

.stock-info {
  font-size: 14px;
  color: var(--muted-foreground);
}

.action-buttons {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
}

.add-cart-btn {
  flex: 1;
  padding: 16px 24px;
  min-height: 52px;
  background: #ffffff;
  color: #2563eb;
  border: 2px solid #e2e8f0;
  border-radius: 0.875rem;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.3s;
}

.add-cart-btn:hover:not(:disabled) {
  border-color: #2563eb;
  background: #eff6ff;
  transform: translateY(-2px);
}

.add-cart-btn:focus-visible {
  outline: 2px solid var(--blue-primary);
  outline-offset: 2px;
}

.add-cart-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.buy-now-btn {
  flex: 1;
  padding: 16px 24px;
  min-height: 52px;
  background: var(--blue-gradient);
  color: #ffffff;
  border: none;
  border-radius: 0.875rem;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
}

.buy-now-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(37, 99, 235, 0.4);
}

.buy-now-btn:focus-visible {
  outline: 2px solid var(--blue-primary);
  outline-offset: 2px;
}

.buy-now-btn:disabled {
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

.extra-actions {
  display: flex;
  gap: 24px;
}

.extra-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  background: none;
  border: none;
  color: var(--muted-foreground);
  cursor: pointer;
  font-size: 14px;
  transition: color 0.2s;
  padding: 12px;
  min-height: 48px;
  border-radius: 8px;
}

.extra-btn:hover:not(:disabled) {
  color: var(--blue-primary);
}

.extra-btn:focus-visible {
  outline: 2px solid var(--blue-primary);
  outline-offset: 2px;
}

.extra-btn.active {
  color: #f59e0b;
}

.extra-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.extra-btn.is-loading {
  pointer-events: none;
}

.product-details {
  border-top: 1px solid var(--border);
  padding-top: 32px;
}

.tabs-header {
  display: flex;
  gap: 32px;
  border-bottom: 1px solid var(--border);
  margin-bottom: 24px;
}

.tab-btn {
  padding: 12px 0;
  background: none;
  border: none;
  color: var(--muted-foreground);
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  position: relative;
  transition: color 0.2s;
}

.tab-btn:hover {
  color: var(--foreground);
}

.tab-btn.active {
  color: var(--blue-primary);
}

.tab-btn.active::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  right: 0;
  height: 3px;
  background: var(--blue-gradient);
  border-radius: 9999px;
}

.detail-content {
  line-height: 1.8;
  color: var(--foreground);
}

.detail-section {
  margin-bottom: 32px;
}

.detail-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--foreground);
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--border);
}

.detail-text {
  color: var(--muted-foreground);
  line-height: 1.8;
}

.specs-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.spec-item {
  display: flex;
  justify-content: space-between;
  padding: 12px 16px;
  background-color: #f9fafb;
  border-radius: 8px;
}

.spec-label {
  color: var(--muted-foreground);
  font-weight: 500;
}

.spec-value {
  color: var(--foreground);
}

.features-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.feature-item {
  padding: 8px 0;
  padding-left: 24px;
  position: relative;
  color: var(--muted-foreground);
}

.feature-item::before {
  content: '•';
  position: absolute;
  left: 0;
  color: var(--blue-primary);
  font-size: 18px;
  line-height: 1.4;
}

.reviews-header {
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px solid var(--border);
}

.rating-summary {
  text-align: center;
}

.rating-number {
  font-size: 48px;
  font-weight: bold;
  color: var(--foreground);
  line-height: 1;
  margin-bottom: 8px;
}

.rating-stars {
  display: flex;
  justify-content: center;
  gap: 4px;
  margin-bottom: 8px;
}

.rating-count {
  color: var(--muted-foreground);
  font-size: 14px;
}

.specs-table {
  width: 100%;
  border-collapse: collapse;
}

.specs-table tr {
  border-bottom: 1px solid var(--border);
}

.specs-table td {
  padding: 16px;
}

.specs-table .spec-name {
  width: 200px;
  background-color: #f9fafb;
  color: var(--muted-foreground);
  font-weight: 500;
}

.reviews-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.review-item {
  padding-bottom: 24px;
  border-bottom: 1px solid var(--border);
}

.review-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.reviewer-avatar {
  width: 40px;
  height: 40px;
  background-color: var(--blue-primary);
  color: #ffffff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 500;
}

.reviewer-info {
  flex: 1;
}

.reviewer-name {
  font-weight: 500;
  color: var(--foreground);
  display: block;
  margin-bottom: 4px;
}

.review-rating {
  display: flex;
  gap: 2px;
}

.review-time {
  font-size: 14px;
  color: var(--muted-foreground);
}

.review-content {
  color: var(--foreground);
  line-height: 1.6;
}

.empty-reviews {
  text-align: center;
  padding: 48px;
  color: var(--muted-foreground);
}

.write-review-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background-color: var(--blue-primary);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.write-review-btn:hover {
  background-color: var(--blue-hover);
}

.delete-review-btn {
  padding: 4px;
  background: none;
  border: none;
  color: #9ca3af;
  cursor: pointer;
  transition: color 0.2s;
}

.delete-review-btn:hover {
  color: #ef4444;
}

.no-reviews {
  text-align: center;
  padding: 48px;
  color: var(--muted-foreground);
}

.review-form .form-item {
  margin-bottom: 20px;
}

.review-form label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: var(--foreground);
  margin-bottom: 8px;
}

.rating-select {
  display: flex;
  align-items: center;
  gap: 4px;
}

.star-select {
  font-size: 28px;
  color: #d1d5db;
  cursor: pointer;
  transition: color 0.2s;
}

.star-select.active {
  color: #f59e0b;
}

.rating-text {
  margin-left: 12px;
  font-size: 14px;
  color: var(--foreground);
}

.preview-container {
  display: flex;
  justify-content: center;
  align-items: center;
}

.preview-image-wrapper {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f9fafb;
}

.preview-image {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.preview-thumbnails {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-top: 16px;
}

.preview-thumb-item {
  width: 60px;
  height: 60px;
  border: 2px solid var(--border);
  border-radius: 8px;
  cursor: pointer;
  overflow: hidden;
  transition: border-color 0.2s;
}

.preview-thumb-item.active,
.preview-thumb-item:hover {
  border-color: var(--blue-primary);
}

.preview-thumb-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

@media (max-width: 1024px) {
  .product-main {
    grid-template-columns: 1fr;
  }

  .product-gallery {
    position: static;
  }
}

@media (max-width: 768px) {
  .product-content {
    padding: 16px;
  }

  .action-buttons {
    flex-direction: column;
  }
}
</style>
