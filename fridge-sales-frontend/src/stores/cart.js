import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getCartCount, addToCart as addToCartApi, removeFromCart as removeFromCartApi, clearCart as clearCartApi } from '@/api/cart'

export const useCartStore = defineStore('cart', () => {
  const cartCount = ref(0)

  async function fetchCartCount() {
    console.log('[操作日志] cartStore.fetchCartCount 被调用')
    try {
      console.log('[操作日志] 开始从服务器获取购物车数量')
      const res = await getCartCount()
      const newCount = res.data || 0
      console.log('[操作日志] 服务器返回购物车数量:', newCount, '本地数量:', cartCount.value)
      cartCount.value = newCount
      console.log('[操作日志] 购物车数量已同步:', cartCount.value)
    } catch (error) {
      console.error('[操作日志] 获取购物车数量失败:', error)
    }
  }

  async function addToCart(product, quantity = 1) {
    console.log('[操作日志] cartStore.addToCart 被调用')
    console.log('[操作日志] 商品信息:', product.name, '商品ID:', product.id)
    console.log('[操作日志] 添加数量:', quantity)
    try {
      console.log('[操作日志] 开始调用 API addToCart')
      await addToCartApi({ productId: product.id, quantity })
      console.log('[操作日志] API 调用成功，更新购物车数量，当前数量:', cartCount.value, '增加:', quantity)
      cartCount.value += quantity
      console.log('[操作日志] 更新后购物车数量:', cartCount.value)
    } catch (error) {
      console.error('[操作日志] 添加购物车失败:', error)
      throw error
    }
  }

  async function removeFromCart(productId) {
    console.log('[操作日志] cartStore.removeFromCart 被调用，商品ID:', productId)
    try {
      console.log('[操作日志] 开始调用 API removeFromCart')
      await removeFromCartApi(productId)
      console.log('[操作日志] API 调用成功，当前数量:', cartCount.value)
      cartCount.value = Math.max(0, cartCount.value - 1)
      console.log('[操作日志] 更新后购物车数量:', cartCount.value)
    } catch (error) {
      console.error('[操作日志] 删除购物车失败:', error)
      throw error
    }
  }

  async function clearCart() {
    console.log('[操作日志] cartStore.clearCart 被调用')
    try {
      console.log('[操作日志] 开始调用 API clearCart')
      await clearCartApi()
      console.log('[操作日志] API 调用成功，清空购物车')
      cartCount.value = 0
      console.log('[操作日志] 购物车数量已重置为 0')
    } catch (error) {
      console.error('[操作日志] 清空购物车失败:', error)
      throw error
    }
  }

  function setCartCount(count) {
    console.log('[操作日志] cartStore.setCartCount 被调用，设置数量:', count)
    cartCount.value = count
  }

  function resetCart() {
    console.log('[操作日志] cartStore.resetCart 被调用，重置购物车')
    cartCount.value = 0
    console.log('[操作日志] 购物车数量已重置为 0')
  }

  return {
    cartCount,
    fetchCartCount,
    addToCart,
    removeFromCart,
    clearCart,
    setCartCount,
    resetCart
  }
})
