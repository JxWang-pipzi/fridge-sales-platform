<template>
  <div class="products-page">
    <div class="container">
      <div class="page-header">
        <h1 class="page-title">全部商品</h1>
        <p class="page-desc">精选优质冰箱，品质保障</p>
      </div>

      <div class="products-layout">
        <aside class="filter-sidebar">
          <div class="filter-section">
            <h3 class="filter-title">商品分类</h3>
            <div class="filter-options">
              <label
                v-for="cat in categories"
                :key="cat.value"
                class="radio-option"
              >
                <input
                  type="radio"
                  name="category"
                  :value="cat.value"
                  v-model="filterForm.category"
                  :disabled="loading"
                  @change="selectCategory(cat.value)"
                />
                <span class="radio-label">{{ cat.label }}</span>
              </label>
            </div>
          </div>

          <div class="filter-section">
            <h3 class="filter-title">价格区间</h3>
            <div class="filter-options">
              <label
                v-for="price in priceRanges"
                :key="price.value"
                class="radio-option"
              >
                <input
                  type="radio"
                  name="priceRange"
                  :value="price.value"
                  v-model="selectedPriceRange"
                  :disabled="loading"
                  @change="selectPriceRange(price)"
                />
                <span class="radio-label">{{ price.label }}</span>
              </label>
            </div>
          </div>

          <div class="filter-section">
            <h3 class="filter-title">品牌</h3>
            <div class="filter-options brand-options">
              <label
                v-for="brand in brands"
                :key="brand"
                class="radio-option"
              >
                <input
                  type="radio"
                  name="brand"
                  :value="brand === '全部' ? '' : brand"
                  v-model="filterForm.brand"
                  :disabled="loading"
                  @change="selectBrand(brand === '全部' ? '' : brand)"
                />
                <span class="radio-label">{{ brand }}</span>
              </label>
            </div>
          </div>

          <button class="reset-btn" :disabled="loading" @click="handleReset">
            <el-icon v-if="loading"><Loading /></el-icon>
            重置筛选
          </button>
        </aside>

        <main class="products-main">
          <div class="toolbar">
            <div class="search-box">
              <input
                v-model="filterForm.keyword"
                type="text"
                placeholder="搜索商品..."
                class="search-input"
                :disabled="loading"
                @keyup.enter="handleSearch"
              />
              <button class="search-btn" :disabled="loading" @click="handleSearch">
                <el-icon><Search /></el-icon>
              </button>
            </div>

            <div class="sort-tabs">
              <button
                v-for="sort in sortOptions"
                :key="sort.value"
                class="sort-tab"
                :class="{ active: sortBy === sort.value }"
                :disabled="loading"
                @click="changeSort(sort.value)"
              >
                {{ sort.label }}
              </button>
            </div>

            <div class="result-count">
              <template v-if="loading">
                <el-icon class="loading-icon"><Loading /></el-icon>
                加载中...
              </template>
              <template v-else>
                共 {{ total }} 件商品
              </template>
            </div>
          </div>

          <div v-loading="loading" element-loading-text="加载商品中..." class="products-grid">
            <transition-group name="product-list">
              <ProductCard
                v-for="product in productList"
                :key="product.id"
                :product="product"
                @addCart="handleAddToCart"
                @favorite="handleFavorite"
              />
            </transition-group>
          </div>

          <transition name="fade">
            <div v-if="productList.length === 0 && !loading" class="empty-state">
              <div class="empty-icon">
                <el-icon :size="64"><Box /></el-icon>
              </div>
              <h3>暂无符合条件的商品</h3>
              <p>请尝试调整筛选条件或搜索其他关键词</p>
              <button class="reset-btn primary" @click="handleReset">清除筛选</button>
            </div>
          </transition>

          <div v-if="total > 0" class="pagination">
            <el-pagination
              v-model:current-page="pagination.page"
              v-model:page-size="pagination.pageSize"
              :page-sizes="[12, 24, 36, 48]"
              :total="total"
              :disabled="loading"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleSizeChange"
              @current-change="handlePageChange"
            />
          </div>
        </main>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, Loading, Box } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { getProductList } from '@/api/product'
import ProductCard from '@/components/common/ProductCard.vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loading = ref(false)

const filterForm = reactive({
  keyword: '',
  category: '',
  brand: '',
  minPrice: null,
  maxPrice: null
})

const selectedPriceRange = ref('')

const categories = ref([
  { label: '全部', value: '' },
  { label: '双门冰箱', value: '双门' },
  { label: '三门冰箱', value: '三门' },
  { label: '对开门冰箱', value: '对开门' },
  { label: '多门冰箱', value: '多门' },
  { label: '嵌入式冰箱', value: '嵌入式' }
])

const brands = ref([
  '全部',
  '海尔',
  '美的',
  '西门子',
  '容声',
  '松下',
  '格力',
  '美菱',
  '卡萨帝',
  '小米',
  '海信',
  '三星',
  'SMEG'
])

const priceRanges = ref([
  { label: '全部价格', value: '', min: null, max: null },
  { label: '0-1000 元', value: '0-1000', min: 0, max: 1000 },
  { label: '1000-3000 元', value: '1000-3000', min: 1000, max: 3000 },
  { label: '3000-5000 元', value: '3000-5000', min: 3000, max: 5000 },
  { label: '5000-10000 元', value: '5000-10000', min: 5000, max: 10000 },
  { label: '10000 元以上', value: '10000+', min: 10000, max: null }
])

const sortOptions = [
  { label: '综合排序', value: 'default' },
  { label: '价格升序', value: 'price-asc' },
  { label: '价格降序', value: 'price-desc' },
  { label: '销量优先', value: 'sales' }
]

const sortBy = ref('default')
const productList = ref([])
const total = ref(0)
const pagination = reactive({
  page: 1,
  pageSize: 12
})

const fetchProducts = async () => {
  loading.value = true
  try {
    const res = await getProductList({
      page: pagination.page,
      pageSize: pagination.pageSize,
      keyword: filterForm.keyword,
      category: filterForm.category,
      brand: filterForm.brand,
      minPrice: filterForm.minPrice,
      maxPrice: filterForm.maxPrice,
      sortBy: sortBy.value
    })
    productList.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('获取商品列表失败:', error)
    ElMessage.error('获取商品列表失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

const selectCategory = (value) => {
  filterForm.category = value
  pagination.page = 1
  fetchProducts()
}

const selectBrand = (brand) => {
  filterForm.brand = brand === '' ? '' : brand
  pagination.page = 1
  fetchProducts()
}

const selectPriceRange = (price) => {
  filterForm.minPrice = price.min
  filterForm.maxPrice = price.max
  pagination.page = 1
  fetchProducts()
}

const handleSearch = () => {
  pagination.page = 1
  fetchProducts()
}

const handleReset = () => {
  filterForm.keyword = ''
  filterForm.category = ''
  filterForm.brand = ''
  filterForm.minPrice = null
  filterForm.maxPrice = null
  selectedPriceRange.value = ''
  sortBy.value = 'default'
  pagination.page = 1
  fetchProducts()
}

const changeSort = (value) => {
  sortBy.value = value
  pagination.page = 1
  fetchProducts()
}

const handleSizeChange = () => {
  pagination.page = 1
  fetchProducts()
}

const handlePageChange = () => {
  fetchProducts()
}

const handleAddToCart = async (product) => {
  try {
    await cartStore.addToCart(product, 1)
    ElMessage.success('已添加到购物车')
  } catch (error) {
    console.error('添加购物车失败:', error)
    if (error.response && error.response.status === 401) {
      ElMessage.warning('请先登录')
      router.push('/login')
    } else {
      ElMessage.error('添加购物车失败，请重试')
    }
  }
}

const handleFavorite = ({ product, isFavorite }) => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  ElMessage.success(isFavorite ? '已取消收藏' : '已添加收藏')
}

onMounted(() => {
  if (route.query.keyword) {
    filterForm.keyword = route.query.keyword
  }
  if (route.query.category) {
    filterForm.category = route.query.category
  }
  fetchProducts()
})

watch(() => route.query, (newQuery) => {
  if (newQuery.keyword !== undefined) {
    filterForm.keyword = newQuery.keyword || ''
    pagination.page = 1
    fetchProducts()
  }
  if (newQuery.category !== undefined) {
    filterForm.category = newQuery.category || ''
    pagination.page = 1
    fetchProducts()
  }
}, { immediate: false })
</script>

<style scoped>
.products-page {
  padding: 24px 0;
  background-color: #f8fafc;
  min-height: calc(100vh - 60px);
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--foreground);
  margin-bottom: 8px;
  position: relative;
  padding-left: 1rem;
}

.page-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 1.75rem;
  background: var(--blue-gradient);
  border-radius: 9999px;
}

.page-desc {
  color: var(--muted-foreground);
  font-size: 0.875rem;
}

.products-layout {
  display: grid;
  grid-template-columns: 260px 1fr;
  gap: 24px;
}

.filter-sidebar {
  background-color: #ffffff;
  border-radius: 1rem;
  border: 1px solid var(--border);
  padding: 24px;
  height: fit-content;
  position: sticky;
  top: 90px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04);
}

.filter-section {
  margin-bottom: 24px;
}

.filter-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--foreground);
  margin-bottom: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-title::before {
  content: '';
  width: 4px;
  height: 14px;
  background: var(--blue-gradient);
  border-radius: 9999px;
}

.filter-options {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.radio-option {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  font-size: 14px;
  color: var(--muted-foreground);
  transition: all 0.2s;
  padding: 8px 10px;
  border-radius: 0.5rem;
}

.radio-option:hover {
  color: var(--foreground);
  background: #f8fafc;
}

.radio-option input[type="radio"] {
  width: 16px;
  height: 16px;
  cursor: pointer;
  accent-color: #2563eb;
}

.radio-option input[type="radio"]:checked + .radio-label {
  color: #2563eb;
  font-weight: 600;
}

.radio-label {
  flex: 1;
}

.price-range {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.price-input {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid var(--border);
  border-radius: 6px;
  font-size: 14px;
  width: 80px;
}

.price-input:focus {
  outline: none;
  border-color: var(--blue-primary);
}

.reset-btn {
  width: 100%;
  padding: 10px 16px;
  background: #f8fafc;
  color: var(--foreground);
  border: 1px solid #e2e8f0;
  border-radius: 0.75rem;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s;
}

.reset-btn:hover {
  background: #f1f5f9;
  border-color: #cbd5e1;
}

.products-main {
  background-color: transparent;
  padding: 0;
}

.toolbar {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
  flex-wrap: wrap;
  background: #ffffff;
  padding: 16px 20px;
  border-radius: 1rem;
  border: 1px solid #e2e8f0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.03);
}

.search-box {
  flex: 1;
  min-width: 200px;
  max-width: 400px;
  position: relative;
}

.search-input {
  width: 100%;
  padding: 10px 44px 10px 16px;
  border: 1px solid #e2e8f0;
  border-radius: 9999px;
  font-size: 14px;
  background: #f8fafc;
  transition: all 0.2s;
}

.search-input:focus {
  outline: none;
  border-color: #3b82f6;
  background: #ffffff;
  box-shadow: 0 0 0 4px rgba(37, 99, 235, 0.1);
}

.search-btn {
  position: absolute;
  right: 6px;
  top: 50%;
  transform: translateY(-50%);
  background: var(--blue-gradient);
  border: none;
  color: #ffffff;
  cursor: pointer;
  padding: 6px;
  border-radius: 9999px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.search-btn:hover {
  transform: translateY(-50%) scale(1.05);
}

.sort-tabs {
  display: flex;
  gap: 6px;
  background: #f8fafc;
  padding: 4px;
  border-radius: 9999px;
}

.sort-tab {
  padding: 8px 14px;
  background: none;
  border: none;
  border-radius: 9999px;
  color: var(--muted-foreground);
  cursor: pointer;
  font-size: 13px;
  font-weight: 500;
  transition: all 0.2s;
}

.sort-tab:hover {
  color: var(--foreground);
}

.sort-tab.active {
  background: var(--blue-gradient);
  color: #ffffff;
  box-shadow: 0 2px 8px rgba(37, 99, 235, 0.3);
}

.result-count {
  color: var(--muted-foreground);
  font-size: 14px;
  margin-left: auto;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(1, 1fr);
  gap: 20px;
  min-height: 200px;
}

@media (min-width: 640px) {
  .products-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (min-width: 1024px) {
  .products-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: var(--muted-foreground);
}

.empty-icon {
  margin-bottom: 16px;
  color: #d1d5db;
}

.empty-state h3 {
  font-size: 18px;
  font-weight: 500;
  color: var(--foreground);
  margin-bottom: 8px;
}

.empty-state p {
  margin-bottom: 16px;
}

.empty-state .reset-btn.primary {
  background-color: var(--blue-primary);
  color: #ffffff;
  border-color: var(--blue-primary);
}

.empty-state .reset-btn.primary:hover {
  background-color: var(--blue-hover);
  border-color: var(--blue-hover);
}

.loading-icon {
  animation: spin 1s linear infinite;
  margin-right: 4px;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.product-list-enter-active,
.product-list-leave-active {
  transition: all 0.3s ease;
}

.product-list-enter-from,
.product-list-leave-to {
  opacity: 0;
  transform: translateY(20px);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.brand-options {
  max-height: 200px;
  overflow-y: auto;
}

.reset-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.sort-tab:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.search-input:disabled {
  background-color: #f3f4f6;
  cursor: not-allowed;
}

.search-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.radio-option input[type="radio"]:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.radio-option input[type="radio"]:disabled + .radio-label {
  opacity: 0.6;
}

.pagination {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .products-layout {
    grid-template-columns: 1fr;
  }

  .filter-sidebar {
    position: static;
  }
}
</style>
