<template>
  <div class="favorites-page">
    <div class="container">
      <div class="page-header">
        <h1 class="page-title">我的收藏</h1>
        <p class="page-subtitle">管理您收藏的商品</p>
      </div>

      <div v-loading="loading" class="favorites-content">
        <div v-if="favorites.length > 0" class="favorites-grid">
          <div v-for="item in favorites" :key="item.id" class="favorite-card">
            <router-link :to="`/product/${item.productId}`" class="product-link">
              <div class="product-image-container">
                <img
                  :src="item.productImage || defaultImage"
                  :alt="item.productName"
                  class="product-image"
                  @error="handleImageError"
                />
                <button
                  type="button"
                  class="remove-favorite-btn"
                  @click.prevent="handleRemoveFavorite(item.productId)"
                  title="取消收藏"
                >
                  <el-icon><Close /></el-icon>
                </button>
              </div>
              <div class="product-info">
                <h3 class="product-name">{{ getParsedName(item.productName).displayName }}</h3>
                <div v-if="getParsedName(item.productName).sku" class="product-sku">SKU: {{ getParsedName(item.productName).sku }}</div>
                <div class="product-price">¥{{ formatPrice(item.productPrice) }}</div>
              </div>
            </router-link>
            <button class="add-cart-btn" @click="handleAddToCart(item)">
              <el-icon><ShoppingCart /></el-icon>
              加入购物车
            </button>
          </div>
        </div>

        <div v-else class="empty-state">
          <el-icon class="empty-icon"><Star /></el-icon>
          <p class="empty-text">暂无收藏商品</p>
          <router-link to="/products" class="browse-btn">去逛逛</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Star, ShoppingCart, Close } from '@element-plus/icons-vue'
import { useFavoriteStore } from '@/stores/favorite'
import { useCartStore } from '@/stores/cart'
import { parseProductName } from '@/utils/product'
import defaultImage from '@/assets/images/fridge.jpg'

const router = useRouter()
const favoriteStore = useFavoriteStore()
const cartStore = useCartStore()

const loading = ref(false)
const favorites = ref([])

const formatPrice = (price) => {
  return Number(price).toLocaleString()
}

const getParsedName = (name) => {
  return parseProductName(name)
}

const handleImageError = (e) => {
  e.target.src = defaultImage
}

const fetchFavorites = async () => {
  loading.value = true
  try {
    await favoriteStore.fetchFavoriteList()
    favorites.value = favoriteStore.favoriteList
  } catch (error) {
    console.error('获取收藏列表失败:', error)
    ElMessage.error('获取收藏列表失败')
  } finally {
    loading.value = false
  }
}

const handleRemoveFavorite = async (productId) => {
  try {
    await favoriteStore.toggleFavorite(productId)
    favorites.value = favorites.value.filter(item => item.productId !== productId)
  } catch (error) {
    console.error('取消收藏失败:', error)
    ElMessage.error('取消收藏失败')
  }
}

const handleAddToCart = async (item) => {
  try {
    await cartStore.addToCart({
      id: item.productId,
      name: item.productName,
      price: item.productPrice,
      image: item.productImage
    })
    ElMessage.success('已添加到购物车')
  } catch (error) {
    console.error('添加购物车失败:', error)
    ElMessage.error('添加购物车失败')
  }
}

onMounted(() => {
  fetchFavorites()
})
</script>

<style scoped>
.favorites-page {
  padding: 24px 0;
  min-height: calc(100vh - 60px);
  background-color: #f3f4f6;
}

.container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 16px;
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-size: 1.875rem;
  font-weight: bold;
  color: var(--foreground);
  margin: 0 0 8px 0;
}

.page-subtitle {
  color: var(--muted-foreground);
  margin: 0;
}

.favorites-content {
  background-color: #ffffff;
  border-radius: var(--radius);
  padding: 24px;
}

.favorites-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 24px;
}

.favorite-card {
  background-color: #ffffff;
  border: 1px solid var(--border);
  border-radius: var(--radius);
  overflow: hidden;
  transition: box-shadow 0.3s, transform 0.3s;
}

.favorite-card:hover {
  box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.product-link {
  text-decoration: none;
  color: inherit;
  display: block;
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

.favorite-card:hover .product-image {
  transform: scale(1.05);
}

.remove-favorite-btn {
  position: absolute;
  top: 8px;
  right: 8px;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.9);
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
  color: #ef4444;
  opacity: 0;
}

.favorite-card:hover .remove-favorite-btn {
  opacity: 1;
}

.remove-favorite-btn:hover {
  background-color: #ef4444;
  color: #ffffff;
}

.product-info {
  padding: 16px;
}

.product-name {
  font-size: 14px;
  font-weight: 500;
  color: var(--foreground);
  margin: 0 0 4px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.4;
}

.product-sku {
  font-size: 12px;
  color: #9ca3af;
  font-family: 'Courier New', monospace;
  margin-bottom: 8px;
}

.product-price {
  font-size: 20px;
  font-weight: bold;
  color: var(--destructive);
}

.add-cart-btn {
  width: 100%;
  padding: 10px 16px;
  background-color: var(--blue-primary);
  color: #ffffff;
  border: none;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  transition: background-color 0.2s;
}

.add-cart-btn:hover {
  background-color: var(--blue-hover);
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.empty-icon {
  font-size: 64px;
  color: #d1d5db;
  margin-bottom: 16px;
}

.empty-text {
  font-size: 16px;
  color: var(--muted-foreground);
  margin: 0 0 24px;
}

.browse-btn {
  display: inline-block;
  padding: 10px 24px;
  background-color: var(--blue-primary);
  color: #ffffff;
  text-decoration: none;
  border-radius: 8px;
  font-size: 14px;
  transition: background-color 0.2s;
}

.browse-btn:hover {
  background-color: var(--blue-hover);
}

@media (max-width: 768px) {
  .favorites-grid {
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
    gap: 16px;
  }
}
</style>
