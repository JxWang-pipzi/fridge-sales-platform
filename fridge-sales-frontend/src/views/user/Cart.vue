<template>
  <div class="cart-page">
    <div class="container">
      <div class="page-header">
        <h1 class="page-title">购物车</h1>
        <p class="page-desc">{{ cartList.length > 0 ? `共 ${cartList.length} 件商品` : '您的购物车是空的' }}</p>
      </div>

      <div v-loading="loading" class="cart-content">
        <template v-if="cartList.length > 0">
          <div class="cart-layout">
            <div class="cart-main">
              <div class="cart-header">
                <label class="select-all">
                  <input 
                    type="checkbox" 
                    v-model="selectAll" 
                    @change="handleSelectAll"
                    aria-label="全选商品"
                  />
                  <span>全选</span>
                </label>
                <span class="header-product">商品信息</span>
                <span class="header-price">单价</span>
                <span class="header-quantity">数量</span>
                <span class="header-total">小计</span>
                <span class="header-action">操作</span>
              </div>

              <div class="cart-list" role="list">
                <div v-for="item in cartList" :key="item.id" class="cart-item" role="listitem">
                  <label class="item-select">
                    <input 
                      type="checkbox" 
                      v-model="item.selected" 
                      @change="handleItemSelect"
                      :aria-label="`选择${item.productName}`"
                    />
                  </label>
                  <div class="item-product">
                    <div class="item-info">
                      <router-link :to="`/product/${item.productId}`" class="item-name">
                        {{ getParsedName(item.productName).displayName }}
                      </router-link>
                      <div v-if="getParsedName(item.productName).sku" class="item-sku">
                        SKU: {{ getParsedName(item.productName).sku }}
                      </div>
                      <div class="item-spec" v-if="item.spec">{{ item.spec }}</div>
                      <div class="item-stock" v-if="item.stock">库存: {{ item.stock }}件</div>
                    </div>
                  </div>
                  <div class="item-price">
                    <span class="current-price">¥{{ item.price }}</span>
                  </div>
                  <div class="item-quantity">
                    <button 
                      class="qty-btn" 
                      @click="decreaseQty(item)" 
                      :disabled="item.quantity <= 1 || item.updating"
                      :aria-label="`减少${item.productName}数量`"
                      type="button"
                    >-</button>
                    <label :for="`qty-${item.id}`" class="sr-only">{{ item.productName }}数量</label>
                    <input 
                      :id="`qty-${item.id}`"
                      type="number" 
                      v-model.number="item.quantity" 
                      class="qty-input" 
                      min="1" 
                      :max="item.stock || 99" 
                      :disabled="item.updating"
                      @change="handleQuantityChange(item)"
                      :aria-label="`${item.productName}数量`"
                    />
                    <button 
                      class="qty-btn" 
                      @click="increaseQty(item)" 
                      :disabled="item.quantity >= (item.stock || 99) || item.updating"
                      :aria-label="`增加${item.productName}数量`"
                      type="button"
                    >+</button>
                    <transition name="fade">
                      <el-icon v-if="item.updating" class="updating-icon"><Loading /></el-icon>
                    </transition>
                  </div>
                  <div class="item-total">¥{{ (item.price * item.quantity).toFixed(2) }}</div>
                  <div class="item-action">
                    <button 
                      class="action-btn favorite-btn" 
                      :class="{ 'is-favorite': item.isFavorite }" 
                      @click="handleToggleFavorite(item)"
                      :aria-label="item.isFavorite ? `取消收藏${item.productName}` : `收藏${item.productName}`"
                      :aria-pressed="item.isFavorite"
                      type="button"
                    >
                      <el-icon><Star /></el-icon>
                      {{ item.isFavorite ? '已收藏' : '收藏' }}
                    </button>
                    <button 
                      class="action-btn delete-btn" 
                      @click="handleRemove(item)"
                      :aria-label="`删除${item.productName}`"
                      type="button"
                    >
                      <el-icon><Delete /></el-icon>
                      删除
                    </button>
                  </div>
                </div>
              </div>
            </div>

            <aside class="cart-sidebar">
              <div class="summary-card">
                <h3 class="summary-title">订单摘要</h3>
                <div class="summary-row">
                  <span>商品总额</span>
                  <span>¥{{ totalPrice.toFixed(2) }}</span>
                </div>
                <div class="summary-row" v-if="discount > 0">
                  <span>优惠</span>
                  <span class="discount">-¥{{ discount.toFixed(2) }}</span>
                </div>
                <div class="summary-row">
                  <span>运费</span>
                  <span class="free-shipping">免运费</span>
                </div>
                <div class="summary-divider"></div>
                <div class="summary-total">
                  <span>应付总额</span>
                  <span class="total-price">¥{{ (totalPrice - discount).toFixed(2) }}</span>
                </div>
                <div class="summary-count">已选择 {{ selectedCount }} 件商品</div>
                <button 
                  class="checkout-btn" 
                  @click="handleCheckout" 
                  :disabled="selectedCount === 0"
                  :aria-label="`结算${selectedCount}件商品`"
                  type="button"
                >
                  去结算
                </button>
              </div>

              <div class="promo-card">
                <h4 class="promo-title">优惠券</h4>
                <div class="promo-input-group">
                  <label for="promo-code" class="sr-only">优惠码</label>
                  <input 
                    id="promo-code"
                    type="text" 
                    v-model="promoCode" 
                    placeholder="输入优惠码" 
                    class="promo-input"
                    aria-label="优惠码"
                  />
                  <button 
                    class="promo-btn" 
                    @click="applyPromo"
                    aria-label="使用优惠码"
                    type="button"
                  >使用</button>
                </div>
              </div>

              <div class="service-card">
                <div class="service-item">
                  <el-icon><CircleCheck /></el-icon>
                  <span>正品保障</span>
                </div>
                <div class="service-item">
                  <el-icon><Van /></el-icon>
                  <span>免费配送</span>
                </div>
                <div class="service-item">
                  <el-icon><Refresh /></el-icon>
                  <span>7天退换</span>
                </div>
              </div>
            </aside>
          </div>

          <div class="cart-actions">
            <button 
              class="clear-btn" 
              @click="handleClearSelected"
              aria-label="删除选中商品"
              type="button"
            >
              删除选中商品
            </button>
            <button 
              class="clear-btn" 
              @click="handleClearCart"
              aria-label="清空购物车"
              type="button"
            >
              清空购物车
            </button>
          </div>
        </template>

        <div v-else class="empty-state">
          <div class="empty-icon">
            <el-icon :size="64"><ShoppingCart /></el-icon>
          </div>
          <h3>购物车是空的</h3>
          <p>快去挑选心仪的冰箱吧</p>
          <router-link to="/products" class="shop-btn">去购物</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Star, Delete, CircleCheck, Van, Refresh, ShoppingCart, Loading } from '@element-plus/icons-vue'
import { getCartList, updateCartItem, removeFromCart, clearCart } from '@/api/cart'
import { getFavoriteList, addFavorite, removeFavorite } from '@/api/favorite'
import { useCartStore } from '@/stores/cart'
import { parseProductName } from '@/utils/product'
import { logAction, logSuccess, logError } from '@/utils/logger'
import defaultImage from '@/assets/images/fridge.jpg'

const router = useRouter()
const cartStore = useCartStore()

const loading = ref(false)
const promoCode = ref('')
const discount = ref(0)
const favoriteSet = ref(new Set())

const cartList = ref([])

const selectAll = computed({
  get: () => cartList.value.length > 0 && cartList.value.every(item => item.selected),
  set: () => {}
})

const selectedCount = computed(() => {
  return cartList.value.filter(item => item.selected).reduce((sum, item) => sum + item.quantity, 0)
})

const totalPrice = computed(() => {
  return cartList.value
    .filter(item => item.selected)
    .reduce((sum, item) => sum + item.price * item.quantity, 0)
})

const getParsedName = (name) => {
  return parseProductName(name)
}

const fetchCartList = async () => {
  logAction('获取购物车列表')
  loading.value = true
  try {
    const [cartRes, favRes] = await Promise.all([
      getCartList(),
      getFavoriteList()
    ])
    favoriteSet.value = new Set((favRes.data || []).map(item => item.productId))
    cartList.value = (cartRes.data || []).map(item => ({
      ...item,
      selected: true,
      isFavorite: favoriteSet.value.has(item.productId),
      updating: false
    }))
    logSuccess('获取购物车成功', { count: cartList.value.length })
  } catch (error) {
    logError('获取购物车失败', error)
  } finally {
    loading.value = false
  }
}

const handleSelectAll = (val) => {
  cartList.value.forEach(item => {
    item.selected = val.target.checked
  })
}

const handleItemSelect = () => {
}

const decreaseQty = (item) => {
  if (item.quantity > 1 && !item.updating) {
    item.quantity--
    handleQuantityChange(item)
  }
}

const increaseQty = (item) => {
  if (item.quantity < (item.stock || 99) && !item.updating) {
    item.quantity++
    handleQuantityChange(item)
  }
}

const handleQuantityChange = async (item) => {
  logAction('修改商品数量', { productId: item.productId, productName: item.productName, quantity: item.quantity })
  if (item.quantity < 1) item.quantity = 1
  if (item.quantity > (item.stock || 99)) item.quantity = item.stock || 99
  
  item.updating = true
  try {
    await updateCartItem({ id: item.id, quantity: item.quantity })
    logSuccess('更新数量成功', { productId: item.productId, quantity: item.quantity })
    ElMessage.success('数量已更新')
    await cartStore.fetchCartCount()
  } catch (error) {
    logError('更新数量失败', error)
    ElMessage.error('更新数量失败')
    await fetchCartList()
  } finally {
    item.updating = false
  }
}

const handleRemove = async (item) => {
  logAction('点击删除商品', { productId: item.productId, productName: item.productName })
  try {
    await ElMessageBox.confirm(`确定要删除"${item.productName}"吗？`, '删除确认', {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await removeFromCart(item.id)
    logSuccess('删除商品成功', { productId: item.productId, productName: item.productName })
    ElMessage.success('删除成功')
    await fetchCartList()
    await cartStore.fetchCartCount()
  } catch (error) {
    if (error !== 'cancel') {
      logError('删除商品失败', error)
      ElMessage.error('删除失败')
    }
  }
}

const handleClearSelected = async () => {
  const selectedItems = cartList.value.filter(item => item.selected)
  if (selectedItems.length === 0) {
    ElMessage.warning('请选择要删除的商品')
    return
  }
  logAction('删除选中商品', { count: selectedItems.length })
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedItems.length} 件商品吗？`, '删除确认', {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
    for (const item of selectedItems) {
      await removeFromCart(item.id)
    }
    logSuccess('删除选中商品成功', { count: selectedItems.length })
    ElMessage.success('删除成功')
    await fetchCartList()
    await cartStore.fetchCartCount()
  } catch (error) {
    if (error !== 'cancel') {
      logError('删除选中商品失败', error)
      ElMessage.error('删除失败')
    }
  }
}

const handleClearCart = async () => {
  logAction('点击清空购物车')
  try {
    await ElMessageBox.confirm('确定要清空购物车吗？此操作不可撤销。', '清空购物车', {
      confirmButtonText: '确定清空',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await clearCart()
    logSuccess('清空购物车成功')
    ElMessage.success('购物车已清空')
    await fetchCartList()
    await cartStore.fetchCartCount()
  } catch (error) {
    if (error !== 'cancel') {
      logError('清空购物车失败', error)
      ElMessage.error('清空失败')
    }
  }
}

const applyPromo = () => {
  if (!promoCode.value) {
    ElMessage.warning('请输入优惠码')
    return
  }
  ElMessage.info('优惠码功能暂未开放')
}

const handleCheckout = () => {
  const selectedItems = cartList.value.filter(item => item.selected)
  if (selectedItems.length === 0) {
    ElMessage.warning('请选择要结算的商品')
    return
  }
  const itemIds = selectedItems.map(item => item.id).join(',')
  logAction('提交购物车结算', { items: itemIds, count: selectedItems.length })
  router.push(`/checkout?items=${itemIds}`)
}

const handleToggleFavorite = async (item) => {
  logAction('切换收藏状态', { productId: item.productId, productName: item.productName, currentStatus: item.isFavorite })
  try {
    if (item.isFavorite) {
      await removeFavorite(item.productId)
      item.isFavorite = false
      favoriteSet.value.delete(item.productId)
      logSuccess('取消收藏成功', { productId: item.productId })
      ElMessage.success('已取消收藏')
    } else {
      await addFavorite(item.productId)
      item.isFavorite = true
      favoriteSet.value.add(item.productId)
      logSuccess('收藏成功', { productId: item.productId })
      ElMessage.success('收藏成功')
    }
  } catch (error) {
    logError('收藏操作失败', error)
    ElMessage.error('操作失败，请重试')
  }
}

onMounted(() => {
  fetchCartList()
})
</script>

<style scoped>
.cart-page {
  padding: 24px 0;
  background-color: #f3f4f6;
  min-height: calc(100vh - 60px);
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-size: 1.875rem;
  font-weight: bold;
  color: var(--foreground);
  margin-bottom: 8px;
}

.page-desc {
  color: var(--muted-foreground);
}

.cart-content {
  background-color: #ffffff;
  border-radius: var(--radius);
  border: 1px solid var(--border);
  padding: 24px;
  min-height: 400px;
}

.cart-layout {
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 24px;
}

.cart-header {
  display: grid;
  grid-template-columns: 80px 2fr 100px 160px 100px 120px;
  align-items: center;
  padding: 16px;
  background-color: #f9fafb;
  border-radius: 8px;
  margin-bottom: 16px;
  font-weight: 500;
  color: var(--muted-foreground);
  font-size: 14px;
  gap: 8px;
}

.select-all {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.select-all input {
  width: 16px;
  height: 16px;
  cursor: pointer;
}

.header-product {
  text-align: center;
  padding-left: 0;
  display: flex;
  justify-content: center;
}

.header-price,
.header-quantity,
.header-total,
.header-action {
  text-align: center;
}

.cart-item {
  display: grid;
  grid-template-columns: 80px 2fr 100px 160px 100px 120px;
  align-items: center;
  padding: 20px 16px;
  border-bottom: 1px solid var(--border);
  gap: 8px;
}

.item-select {
  display: flex;
  justify-content: center;
  align-items: center;
}

.item-select input {
  width: 16px;
  height: 16px;
  cursor: pointer;
}

.item-product {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding-left: 16px;
  min-width: 0;
}

.item-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
  align-items: center;
  text-align: center;
}

.item-name {
  font-size: 14px;
  font-weight: 500;
  color: var(--foreground);
  text-decoration: none;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.5;
  transition: color 0.2s;
  word-break: break-word;
  text-align: center;
}

.item-name:hover {
  color: var(--blue-primary);
}

.item-sku {
  font-size: 12px;
  color: #9ca3af;
  font-family: 'Courier New', monospace;
  margin-bottom: 2px;
  text-align: center;
}

.item-spec {
  font-size: 12px;
  color: var(--muted-foreground);
  margin-bottom: 2px;
  text-align: center;
}

.item-stock {
  font-size: 12px;
  color: var(--muted-foreground);
  text-align: center;
}

.item-price {
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
}

.current-price {
  color: var(--destructive);
  font-weight: 500;
}

.item-quantity {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0;
  position: relative;
  width: 100%;
}

.item-total {
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  color: var(--destructive);
  font-weight: bold;
  font-size: 16px;
}

.item-action {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  width: 100%;
  min-width: 0;
}

.updating-icon {
  position: absolute;
  right: -24px;
  color: var(--blue-primary);
  animation: spin 1s linear infinite;
  font-size: 16px;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.qty-btn {
  width: 36px;
  height: 36px;
  min-width: 36px;
  min-height: 36px;
  border: 1px solid var(--border);
  background-color: #ffffff;
  cursor: pointer;
  font-size: 18px;
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
  width: 56px;
  height: 36px;
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

.action-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  padding: 6px 10px;
  min-height: 32px;
  background: none;
  border: none;
  color: var(--muted-foreground);
  cursor: pointer;
  font-size: 12px;
  transition: color 0.2s;
  border-radius: 4px;
  white-space: nowrap;
}

.action-btn:hover {
  color: var(--blue-primary);
}

.action-btn:focus-visible {
  outline: 2px solid var(--blue-primary);
  outline-offset: 2px;
}

.favorite-btn.is-favorite {
  color: #f59e0b;
}

.favorite-btn.is-favorite:hover {
  color: #d97706;
}

.delete-btn:hover {
  color: var(--destructive);
}

.cart-sidebar {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.summary-card {
  background-color: #ffffff;
  border: 1px solid var(--border);
  border-radius: var(--radius);
  padding: 20px;
}

.summary-title {
  font-size: 16px;
  font-weight: 500;
  color: var(--foreground);
  margin-bottom: 16px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
  font-size: 14px;
  color: var(--muted-foreground);
}

.discount {
  color: #16a34a;
}

.free-shipping {
  color: #16a34a;
}

.summary-divider {
  height: 1px;
  background-color: var(--border);
  margin: 16px 0;
}

.summary-total {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.summary-total span:first-child {
  font-size: 14px;
  color: var(--foreground);
}

.total-price {
  font-size: 24px;
  font-weight: bold;
  color: var(--destructive);
}

.summary-count {
  font-size: 12px;
  color: var(--muted-foreground);
  margin-bottom: 16px;
}

.checkout-btn {
  width: 100%;
  padding: 16px;
  min-height: 56px;
  background-color: var(--blue-primary);
  color: #ffffff;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;
}

.checkout-btn:hover:not(:disabled) {
  background-color: var(--blue-hover);
}

.checkout-btn:focus-visible {
  outline: 2px solid var(--blue-primary);
  outline-offset: 2px;
}

.checkout-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.promo-card {
  background-color: #ffffff;
  border: 1px solid var(--border);
  border-radius: var(--radius);
  padding: 16px;
}

.promo-title {
  font-size: 14px;
  font-weight: 500;
  color: var(--foreground);
  margin-bottom: 12px;
}

.promo-input-group {
  display: flex;
  gap: 8px;
}

.promo-input {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid var(--border);
  border-radius: 6px;
  font-size: 14px;
}

.promo-input:focus {
  outline: none;
  border-color: var(--blue-primary);
}

.promo-btn {
  padding: 8px 16px;
  background-color: var(--foreground);
  color: #ffffff;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.2s;
}

.promo-btn:hover {
  background-color: #1f2937;
}

.service-card {
  background-color: #ffffff;
  border: 1px solid var(--border);
  border-radius: var(--radius);
  padding: 16px;
  display: flex;
  justify-content: space-around;
}

.service-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  color: var(--muted-foreground);
  font-size: 12px;
}

.cart-actions {
  display: flex;
  gap: 12px;
  margin-top: 16px;
}

.clear-btn {
  padding: 8px 16px;
  background-color: #ffffff;
  color: var(--muted-foreground);
  border: 1px solid var(--border);
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
}

.clear-btn:hover {
  color: var(--destructive);
  border-color: var(--destructive);
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
}

.empty-icon {
  color: var(--muted-foreground);
  margin-bottom: 16px;
}

.empty-state h3 {
  font-size: 18px;
  font-weight: 500;
  color: var(--foreground);
  margin-bottom: 8px;
}

.empty-state p {
  color: var(--muted-foreground);
  margin-bottom: 24px;
}

.shop-btn {
  display: inline-block;
  padding: 12px 32px;
  background-color: var(--blue-primary);
  color: #ffffff;
  border-radius: 8px;
  text-decoration: none;
  font-weight: 500;
  transition: background-color 0.2s;
}

.shop-btn:hover {
  background-color: var(--blue-hover);
}

@media (max-width: 1024px) {
  .cart-layout {
    grid-template-columns: 1fr;
  }

  .cart-sidebar {
    order: -1;
  }
}

@media (max-width: 768px) {
  .cart-header {
    display: none;
  }

  .cart-item {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .item-select {
    justify-content: flex-start;
  }

  .item-product {
    padding-left: 0;
  }

  .item-price,
  .item-quantity,
  .item-total,
  .item-action {
    justify-content: flex-start;
    text-align: left;
  }

  .item-action {
    flex-direction: row;
    justify-content: flex-start;
  }
}
</style>
