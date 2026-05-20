<template>
  <nav class="breadcrumb">
    <ul class="breadcrumb-list">
      <li v-for="(item, index) in items" :key="index" class="breadcrumb-item">
        <router-link v-if="item.path && index < items.length - 1" :to="item.path" class="breadcrumb-link">
          {{ item.name }}
        </router-link>
        <span v-else class="breadcrumb-current">{{ item.name }}</span>
        <el-icon v-if="index < items.length - 1" class="breadcrumb-separator"><Right /></el-icon>
      </li>
    </ul>
  </nav>
</template>

<script setup>
import { Right } from '@element-plus/icons-vue'

defineProps({
  items: {
    type: Array,
    required: true,
    default: () => []
  }
})
</script>

<style scoped>
.breadcrumb {
  padding: 16px 0;
  margin-bottom: 16px;
}

.breadcrumb-list {
  display: flex;
  align-items: center;
  gap: 8px;
  list-style: none;
  padding: 0;
  margin: 0;
  flex-wrap: wrap;
}

.breadcrumb-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.breadcrumb-link {
  color: var(--blue-primary);
  text-decoration: none;
  transition: color 0.2s;
}

.breadcrumb-link:hover {
  color: var(--blue-hover);
  text-decoration: underline;
}

.breadcrumb-current {
  color: var(--muted-foreground);
}

.breadcrumb-separator {
  color: var(--muted-foreground);
  font-size: 12px;
}
</style>
