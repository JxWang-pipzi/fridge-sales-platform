<template>
  <div class="search-bar" :class="{ 'is-loading': isLoading }" role="search" aria-label="商品搜索">
    <label for="category-select" class="sr-only">选择分类</label>
    <el-select
      id="category-select"
      v-model="selectedCategory"
      placeholder="全部分类"
      clearable
      class="category-select"
      :disabled="isLoading"
      @change="handleCategoryChange"
      aria-label="商品分类"
    >
      <el-option
        v-for="item in categories"
        :key="item.id"
        :label="item.name"
        :value="item.id"
      />
    </el-select>
    <label for="search-keyword" class="sr-only">搜索关键词</label>
    <el-input
      id="search-keyword"
      v-model="searchKeyword"
      placeholder="搜索冰箱品牌、型号..."
      class="search-input"
      :class="{ 'has-error': hasError }"
      clearable
      :disabled="isLoading"
      maxlength="50"
      show-word-limit
      @keyup.enter="handleSearch"
      @focus="handleFocus"
      @blur="handleBlur"
      aria-label="搜索关键词"
      :aria-invalid="hasError"
      :aria-describedby="hasError ? 'search-error' : null"
    >
      <template #prefix>
        <el-icon :class="{ 'is-loading-icon': isLoading }" aria-hidden="true"><Search /></el-icon>
      </template>
    </el-input>
    <el-button 
      type="primary" 
      :loading="isLoading"
      :disabled="isLoading || !hasValidInput"
      @click="handleSearch"
      aria-label="搜索"
    >
      <template v-if="!isLoading">
        <el-icon aria-hidden="true"><Search /></el-icon>
        搜索
      </template>
      <template v-else>
        搜索中...
      </template>
    </el-button>
    <transition name="error-fade">
      <span v-if="hasError" id="search-error" class="error-message" role="alert">{{ errorMessage }}</span>
    </transition>
  </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import { Search } from '@element-plus/icons-vue'

const props = defineProps({
  categories: {
    type: Array,
    default: () => []
  },
  keyword: {
    type: String,
    default: ''
  },
  categoryId: {
    type: [Number, String],
    default: null
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['search'])

const searchKeyword = ref(props.keyword)
const selectedCategory = ref(props.categoryId)
const isLoading = ref(false)
const hasError = ref(false)
const errorMessage = ref('')
const isFocused = ref(false)

watch(() => props.keyword, (val) => {
  searchKeyword.value = val
})

watch(() => props.categoryId, (val) => {
  selectedCategory.value = val
})

watch(() => props.loading, (val) => {
  isLoading.value = val
})

const hasValidInput = computed(() => {
  const keyword = searchKeyword.value?.trim() || ''
  return keyword.length >= 0
})

const validateInput = () => {
  const keyword = searchKeyword.value?.trim() || ''
  
  if (keyword.length > 0 && keyword.length < 2) {
    hasError.value = true
    errorMessage.value = '搜索关键词至少需要2个字符'
    return false
  }
  
  const specialCharsPattern = /[<>{}[\]\\\/]/
  if (specialCharsPattern.test(keyword)) {
    hasError.value = true
    errorMessage.value = '搜索关键词包含非法字符'
    return false
  }
  
  hasError.value = false
  errorMessage.value = ''
  return true
}

const handleFocus = () => {
  isFocused.value = true
  hasError.value = false
}

const handleBlur = () => {
  isFocused.value = false
  validateInput()
}

const handleCategoryChange = () => {
  handleSearch()
}

const handleSearch = () => {
  if (!validateInput()) {
    return
  }
  
  if (isLoading.value) return
  
  isLoading.value = true
  
  emit('search', {
    keyword: searchKeyword.value?.trim() || '',
    categoryId: selectedCategory.value
  })
  
  setTimeout(() => {
    isLoading.value = false
  }, 3000)
}
</script>

<style scoped>
.search-bar {
  display: flex;
  align-items: center;
  gap: 10px;
  background: #fff;
  padding: 10px 20px;
  border-radius: 25px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.2s ease;
  position: relative;
}

.search-bar:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.search-bar.is-loading {
  opacity: 0.8;
  pointer-events: none;
}

.category-select {
  width: 150px;
  min-height: 48px;
}

.category-select :deep(.el-input__wrapper) {
  min-height: 48px;
  border-radius: 20px;
  transition: all 0.2s ease;
}

.category-select :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px var(--blue-primary) inset;
}

.category-select :deep(.el-input__wrapper:focus-within) {
  box-shadow: 0 0 0 2px var(--blue-primary) inset;
}

.search-input {
  width: 400px;
  min-height: 48px;
}

.search-input :deep(.el-input__wrapper) {
  min-height: 48px;
  border-radius: 20px;
  transition: all 0.2s ease;
}

.search-input :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px var(--blue-primary) inset;
}

.search-input :deep(.el-input__wrapper:focus-within) {
  box-shadow: 0 0 0 2px var(--blue-primary) inset;
}

.search-input.has-error :deep(.el-input__wrapper) {
  box-shadow: 0 0 0 2px var(--destructive) inset;
}

.search-input :deep(.el-input__prefix) {
  transition: all 0.2s ease;
}

.is-loading-icon {
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

.search-bar :deep(.el-button) {
  min-height: 48px;
  min-width: 100px;
  border-radius: 20px;
  transition: all 0.2s ease;
}

.search-bar :deep(.el-button:hover:not(:disabled)) {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
}

.search-bar :deep(.el-button:active:not(:disabled)) {
  transform: translateY(0);
}

.search-bar :deep(.el-button:disabled) {
  background-color: #9ca3af;
  cursor: not-allowed;
  opacity: 0.6;
}

.error-message {
  position: absolute;
  bottom: -24px;
  left: 20px;
  font-size: 12px;
  color: var(--destructive);
  white-space: nowrap;
}

.error-fade-enter-active,
.error-fade-leave-active {
  transition: opacity 0.2s ease;
}

.error-fade-enter-from,
.error-fade-leave-to {
  opacity: 0;
}

@media (max-width: 768px) {
  .search-bar {
    flex-wrap: wrap;
    border-radius: 8px;
    padding: 10px;
  }
  
  .category-select {
    width: 100%;
  }
  
  .search-input {
    width: 100%;
  }
}
</style>
