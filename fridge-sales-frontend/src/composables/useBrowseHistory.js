import { ref, watch } from 'vue'

const STORAGE_KEY = 'browse_history'
const MAX_HISTORY_COUNT = 18

function loadFromStorage() {
  try {
    const data = localStorage.getItem(STORAGE_KEY)
    if (!data) return []
    const parsed = JSON.parse(data)
    if (!Array.isArray(parsed)) return []
    return parsed
  } catch (e) {
    console.error('解析浏览历史失败:', e)
    return []
  }
}

function saveToStorage(history) {
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(history))
  } catch (e) {
    console.error('保存浏览历史失败:', e)
  }
}

const browseHistory = ref(loadFromStorage())

watch(browseHistory, (newHistory) => {
  saveToStorage(newHistory)
}, { deep: true })

export function useBrowseHistory() {
  const addToHistory = (product) => {
    if (!product || !product.id) return

    const existingIndex = browseHistory.value.findIndex(item => item.id === product.id)
    if (existingIndex !== -1) {
      browseHistory.value.splice(existingIndex, 1)
    }

    const historyItem = {
      id: product.id,
      name: product.name,
      price: product.price,
      image: product.images?.[0] || product.image || '',
      brand: product.brand || '',
      rating: product.rating || 0,
      sales: product.sales || 0,
      capacity: product.capacity || null,
      energyLevel: product.energyLevel || null,
      viewTime: Date.now()
    }

    browseHistory.value.unshift(historyItem)

    if (browseHistory.value.length > MAX_HISTORY_COUNT) {
      browseHistory.value = browseHistory.value.slice(0, MAX_HISTORY_COUNT)
    }
  }

  const clearHistory = () => {
    browseHistory.value = []
  }

  const removeFromHistory = (productId) => {
    const index = browseHistory.value.findIndex(item => item.id === productId)
    if (index !== -1) {
      browseHistory.value.splice(index, 1)
    }
  }

  return {
    browseHistory,
    addToHistory,
    clearHistory,
    removeFromHistory
  }
}
