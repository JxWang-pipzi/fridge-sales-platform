<template>
  <div class="product-card" role="article" :aria-label="`${product.name}，价格${product.price}元`">
    <router-link :to="`/product/${product.id}`" class="product-link" :aria-label="`查看${product.name}详情`">
      <div class="product-image-container">
        <img
          :src="displayImage"
          :alt="product.name"
          class="product-image"
          @error="handleImageError"
        />
      </div>
    </router-link>
    
    <svg
      class="favorite-icon"
      :class="{ active: isFavorite }"
      @click.prevent.stop="handleFavorite"
      :aria-label="isFavorite ? `取消收藏${product.name}` : `收藏${product.name}`"
      xmlns="http://www.w3.org/2000/svg"
      viewBox="0 0 1024 1024"
    >
      <path d="m512 747.84 228.16 119.936a6.4 6.4 0 0 0 9.28-6.72l-43.52-254.08 184.512-179.904a6.4 6.4 0 0 0-3.52-10.88l-255.104-37.12L517.76 147.904a6.4 6.4 0 0 0-11.52 0L392.192 379.072l-255.104 37.12a6.4 6.4 0 0 0-3.52 10.88L318.08 606.976l-43.584 254.08a6.4 6.4 0 0 0 9.28 6.72zM313.6 924.48a70.4 70.4 0 0 1-102.144-74.24l37.888-220.928L88.96 472.96A70.4 70.4 0 0 1 128 352.896l221.76-32.256 99.2-200.96a70.4 70.4 0 0 1 126.208 0l99.2 200.96 221.824 32.256a70.4 70.4 0 0 1 39.04 120.064L774.72 629.376l37.888 220.928a70.4 70.4 0 0 1-102.144 74.24L512 820.096l-198.4 104.32z" />
    </svg>

    <div class="product-content">
      <router-link :to="`/product/${product.id}`" class="product-link" :aria-label="`查看${product.name}详情`">
        <div class="product-brand">{{ product.brand || parsedName.brand }}</div>
        <h3 class="product-name">{{ displayName }}</h3>
        <div v-if="displaySku" class="product-sku">SKU: {{ displaySku }}</div>
      </router-link>

      <div class="product-meta" aria-label="商品评分和销量">
        <div class="rating" :aria-label="`评分${getIntegerRating(product.rating)}星`">
          <el-icon class="star-icon"><Star /></el-icon>
          <span>{{ getIntegerRating(product.rating) }}</span>
        </div>
        <span class="separator">|</span>
        <span class="sales">已售 {{ product.sales || 0 }}</span>
      </div>

      <div class="product-tags" aria-label="商品规格">
        <span v-if="product.capacity" class="tag">{{ product.capacity }}</span>
        <span v-if="product.energyLevel" class="tag energy-tag">{{ product.energyLevel }}</span>
      </div>

      <div class="product-price-container" aria-label="商品价格">
        <span class="current-price">¥{{ formatPrice(product.price) }}</span>
      </div>

      <button 
        class="add-cart-btn" 
        @click="handleAddCart"
        :aria-label="`将${product.name}加入购物车`"
        type="button"
      >
        <el-icon><ShoppingCart /></el-icon>
        加入购物车
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ShoppingCart, Star } from '@element-plus/icons-vue'
import { useFavoriteStore } from '@/stores/favorite'
import { useCartStore } from '@/stores/cart'
import { parseProductName } from '@/utils/product'
import productImage from '@/assets/images/fridge.jpg'

const props = defineProps({
  product: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['addCart', 'favorite'])

const router = useRouter()
const favoriteStore = useFavoriteStore()
const cartStore = useCartStore()

const imageError = ref(false)

const displayImage = computed(() => {
  if (imageError.value) {
    return productImage
  }
  const img = props.product.image
  if (img) {
    return img
  }
  return productImage
})

const handleImageError = () => {
  imageError.value = true
}

const isFavorite = computed(() => favoriteStore.isFavorite(props.product.id))

const parsedName = computed(() => parseProductName(props.product.name))

const displayName = computed(() => parsedName.value.displayName)

const displaySku = computed(() => props.product.sku || parsedName.value.sku)

const formatPrice = (price) => {
  return Number(price).toLocaleString()
}

const getIntegerRating = (rating) => {
  return Math.floor(rating || 0)
}

const handleAddCart = async () => {
  console.log('[操作日志] 用户点击加入购物车，商品:', props.product.name, '商品ID:', props.product.id)
  if (!localStorage.getItem('token')) {
    console.log('[操作日志] 用户未登录，提示登录')
    ElMessage.warning('请先登录')
    return
  }
  try {
    console.log('[操作日志] 开始调用 cartStore.addToCart，数量: 1')
    await cartStore.addToCart(props.product, 1)
    console.log('[操作日志] 成功加入购物车，商品:', props.product.name, '当前购物车数量:', cartStore.cartCount)
    ElMessage.success('已添加到购物车')
  } catch (error) {
    console.error('[操作日志] 添加购物车失败:', error)
    if (error.response?.status === 401) {
      ElMessage.warning('登录已过期，请重新登录')
    } else {
      ElMessage.error('添加购物车失败')
    }
  }
}

const handleFavorite = async () => {
  console.log('[操作日志] 用户点击收藏按钮，商品:', props.product.name, '当前收藏状态:', isFavorite.value)
  if (!localStorage.getItem('token')) {
    console.log('[操作日志] 用户未登录，提示登录')
    ElMessage.warning('请先登录')
    return
  }
  try {
    await favoriteStore.toggleFavorite(props.product.id)
    emit('favorite', { product: props.product, isFavorite: !isFavorite.value })
  } catch (error) {
    console.error('[操作日志] 收藏操作失败:', error)
  }
}

onMounted(() => {
  // 如果已登录，获取收藏列表
  if (localStorage.getItem('token')) {
    favoriteStore.fetchFavoriteList()
  }
})
</script>

<style scoped>
.product-card {
  background-color: #ffffff;
  border-radius: 1.25rem;
  border: 1px solid var(--border);
  overflow: hidden;
  transition: all 0.3s ease;
  position: relative;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04);
}

.product-card:hover {
  box-shadow: 0 16px 48px rgba(0, 0, 0, 0.1);
  transform: translateY(-6px);
  border-color: transparent;
}

.product-card:active {
  transform: translateY(-3px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
}

.product-link {
  text-decoration: none;
  color: inherit;
}

.product-image-container {
  position: relative;
  width: 100%;
  padding-top: 100%;
  overflow: hidden;
}

.product-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.product-card:hover .product-image {
  transform: scale(1.05);
}

.favorite-icon {
  position: absolute;
  top: 12px;
  right: 12px;
  width: 28px;
  height: 28px;
  cursor: pointer;
  color: #9ca3af;
  transition: all 0.3s;
  z-index: 2;
}

.favorite-icon:hover {
  color: #fbbf24;
  transform: scale(1.15);
}

.favorite-icon.active {
  color: #fbbf24;
  fill: #fbbf24;
}

.product-content {
  padding: 16px;
}

.product-brand {
  font-size: 12px;
  color: var(--muted-foreground);
  margin-bottom: 4px;
}

.product-name {
  font-size: 14px;
  font-weight: 500;
  color: var(--foreground);
  margin: 0 0 8px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.4;
  transition: color 0.2s;
}

.product-sku {
  font-size: 12px;
  color: #9ca3af;
  margin-bottom: 8px;
  font-family: 'Courier New', monospace;
}

.product-link:hover .product-name {
  color: var(--blue-primary);
}

.product-meta {
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

.product-tags {
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

.product-price-container {
  display: flex;
  align-items: baseline;
  gap: 8px;
  margin-bottom: 12px;
}

.current-price {
  font-size: 22px;
  font-weight: 800;
  color: #ef4444;
}

.add-cart-btn {
  width: 100%;
  padding: 12px 16px;
  min-height: 44px;
  background: #ffffff;
  color: #0071e3;
  border: 1px solid #0071e3;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.add-cart-btn:hover {
  transform: translateY(-2px);
  background: #f5f5f7;
}

.add-cart-btn:active {
  transform: translateY(0);
}

.add-cart-btn:focus {
  outline: 2px solid var(--blue-primary);
  outline-offset: 2px;
}

.add-cart-btn:disabled {
  background-color: #9ca3af;
  cursor: not-allowed;
  opacity: 0.6;
}
</style>
