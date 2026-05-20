<template>
  <div class="empty-container">
    <div class="empty-content">
      <div class="empty-icon">
        <svg viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
          <circle cx="32" cy="32" r="28" stroke="#e5e7eb" stroke-width="2" fill="#f9fafb"/>
          <path d="M22 28c0-5.523 4.477-10 10-10s10 4.477 10 10" stroke="#9ca3af" stroke-width="2" stroke-linecap="round"/>
          <circle cx="24" cy="36" r="2" fill="#9ca3af"/>
          <circle cx="40" cy="36" r="2" fill="#9ca3af"/>
          <path d="M26 44c2-2 4-3 6-3s4 1 6 3" stroke="#9ca3af" stroke-width="2" stroke-linecap="round"/>
        </svg>
      </div>
      <h3 class="empty-title">{{ title }}</h3>
      <p class="empty-description">{{ text }}</p>
      <div v-if="showButton" class="empty-action">
        <el-button type="primary" size="large" @click="$emit('action')">
          <el-icon v-if="buttonIcon"><component :is="buttonIcon" /></el-icon>
          {{ buttonText }}
        </el-button>
      </div>
      <div v-if="defaultSuggestions.length > 0" class="empty-suggestions">
        <p class="suggestions-title">您可以尝试：</p>
        <ul class="suggestions-list">
          <li v-for="(suggestion, index) in defaultSuggestions" :key="index">
            {{ suggestion }}
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  text: {
    type: String,
    default: '暂无数据'
  },
  title: {
    type: String,
    default: '暂无内容'
  },
  imageSize: {
    type: Number,
    default: 120
  },
  showButton: {
    type: Boolean,
    default: false
  },
  buttonText: {
    type: String,
    default: '去逛逛'
  },
  buttonIcon: {
    type: [Object, String],
    default: null
  },
  type: {
    type: String,
    default: 'default',
    validator: (value) => ['default', 'search', 'cart', 'favorite', 'error'].includes(value)
  },
  suggestions: {
    type: Array,
    default: () => []
  }
})

defineEmits(['action'])

const defaultSuggestions = computed(() => {
  if (props.suggestions.length > 0) return props.suggestions
  
  const suggestionMap = {
    search: ['检查输入的关键词是否正确', '尝试使用其他关键词搜索', '清除筛选条件重新搜索'],
    cart: ['浏览商品并添加到购物车', '查看您的收藏夹', '探索热门推荐商品'],
    favorite: ['浏览商品并收藏喜欢的', '查看热门商品推荐', '搜索您感兴趣的商品'],
    error: ['刷新页面重试', '检查网络连接', '稍后再试'],
    default: []
  }
  
  return suggestionMap[props.type] || []
})
</script>

<style scoped>
.empty-container {
  padding: 60px 20px;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px;
}

.empty-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  max-width: 400px;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.empty-icon {
  width: 120px;
  height: 120px;
  margin-bottom: 24px;
  animation: bounce 2s ease-in-out infinite;
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-8px);
  }
}

.empty-icon svg {
  width: 100%;
  height: 100%;
}

.empty-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--foreground);
  margin: 0 0 8px;
}

.empty-description {
  font-size: 14px;
  color: var(--muted-foreground);
  margin: 0 0 24px;
  line-height: 1.6;
}

.empty-action {
  margin-bottom: 24px;
}

.empty-action :deep(.el-button) {
  min-height: 48px;
  min-width: 140px;
  padding: 12px 24px;
  border-radius: 24px;
  font-size: 15px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.empty-action :deep(.el-button:hover) {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(37, 99, 235, 0.3);
}

.empty-action :deep(.el-button:active) {
  transform: translateY(0);
}

.empty-suggestions {
  background-color: #f9fafb;
  border-radius: 12px;
  padding: 16px 20px;
  width: 100%;
  text-align: left;
}

.suggestions-title {
  font-size: 13px;
  font-weight: 500;
  color: #6b7280;
  margin: 0 0 8px;
}

.suggestions-list {
  margin: 0;
  padding-left: 20px;
  list-style-type: disc;
}

.suggestions-list li {
  font-size: 13px;
  color: #9ca3af;
  line-height: 1.8;
  transition: color 0.2s ease;
}

.suggestions-list li:hover {
  color: var(--blue-primary);
}

@media (max-width: 768px) {
  .empty-container {
    padding: 40px 16px;
    min-height: 250px;
  }
  
  .empty-icon {
    width: 100px;
    height: 100px;
    margin-bottom: 16px;
  }
  
  .empty-title {
    font-size: 16px;
  }
  
  .empty-description {
    font-size: 13px;
  }
}
</style>
