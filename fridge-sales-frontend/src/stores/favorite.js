import { defineStore } from 'pinia'
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getFavoriteList, addFavorite, removeFavorite } from '@/api/favorite'

export const useFavoriteStore = defineStore('favorite', () => {
  const favoriteIds = ref(new Set())
  const favoriteList = ref([])

  async function fetchFavoriteList() {
    try {
      const res = await getFavoriteList()
      favoriteList.value = res.data || []
      favoriteIds.value = new Set(favoriteList.value.map(item => item.productId))
    } catch (error) {
      console.error('获取收藏列表失败:', error)
    }
  }

  async function toggleFavorite(productId) {
    try {
      const isFav = favoriteIds.value.has(productId)
      
      if (isFav) {
        await removeFavorite(productId)
        favoriteIds.value.delete(productId)
        favoriteList.value = favoriteList.value.filter(item => item.productId !== productId)
        ElMessage.success('已取消收藏')
      } else {
        await addFavorite(productId)
        favoriteIds.value.add(productId)
        await fetchFavoriteList()
        ElMessage.success('收藏成功')
      }
      
      return !isFav
    } catch (error) {
      console.error('收藏操作失败:', error)
      throw error
    }
  }

  function isFavorite(productId) {
    return favoriteIds.value.has(productId)
  }

  return {
    favoriteIds,
    favoriteList,
    fetchFavoriteList,
    toggleFavorite,
    isFavorite
  }
})
