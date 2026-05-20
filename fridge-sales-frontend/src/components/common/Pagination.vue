<template>
  <div class="pagination-container">
    <el-pagination
      v-model:current-page="currentPage"
      :page-size="pageSize"
      :total="total"
      :page-sizes="[10, 20, 50, 100]"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  current: {
    type: Number,
    default: 1
  },
  total: {
    type: Number,
    default: 0
  },
  pageSize: {
    type: Number,
    default: 10
  }
})

const emit = defineEmits(['change'])

const currentPage = ref(props.current)

watch(() => props.current, (val) => {
  currentPage.value = val
})

const handleSizeChange = (val) => {
  emit('change', { page: 1, size: val })
}

const handleCurrentChange = (val) => {
  emit('change', { page: val, size: props.pageSize })
}
</script>

<style scoped>
.pagination-container {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

.pagination-container :deep(.el-pagination) {
  gap: 8px;
}

.pagination-container :deep(.el-pagination .el-pager li) {
  min-width: 36px;
  height: 36px;
  min-height: 48px;
  line-height: 36px;
  border-radius: 8px;
  background-color: #ffffff;
  border: 1px solid var(--border);
  transition: all 0.2s ease;
  cursor: pointer;
  font-weight: 500;
}

.pagination-container :deep(.el-pagination .el-pager li:hover) {
  color: var(--blue-primary);
  border-color: var(--blue-primary);
  background-color: #eff6ff;
}

.pagination-container :deep(.el-pagination .el-pager li.is-active) {
  background-color: var(--blue-primary);
  border-color: var(--blue-primary);
  color: #ffffff;
}

.pagination-container :deep(.el-pagination .el-pager li.is-active:hover) {
  background-color: var(--blue-hover);
}

.pagination-container :deep(.el-pagination button) {
  min-width: 36px;
  height: 36px;
  min-height: 48px;
  border-radius: 8px;
  background-color: #ffffff;
  border: 1px solid var(--border);
  transition: all 0.2s ease;
  cursor: pointer;
}

.pagination-container :deep(.el-pagination button:hover:not(:disabled)) {
  color: var(--blue-primary);
  border-color: var(--blue-primary);
  background-color: #eff6ff;
}

.pagination-container :deep(.el-pagination button:active:not(:disabled)) {
  transform: scale(0.95);
}

.pagination-container :deep(.el-pagination button:disabled) {
  background-color: #f3f4f6;
  border-color: #e5e7eb;
  color: #9ca3af;
  cursor: not-allowed;
  opacity: 0.6;
}

.pagination-container :deep(.el-pagination button:focus) {
  outline: 2px solid var(--blue-primary);
  outline-offset: 2px;
}

.pagination-container :deep(.el-pagination .el-pagination__sizes .el-select) {
  min-height: 48px;
}

.pagination-container :deep(.el-pagination .el-pagination__jump) {
  min-height: 48px;
  display: flex;
  align-items: center;
}

.pagination-container :deep(.el-pagination .el-pagination__jump .el-input__wrapper) {
  min-height: 36px;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.pagination-container :deep(.el-pagination .el-pagination__jump .el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px var(--blue-primary) inset;
}

.pagination-container :deep(.el-pagination .el-pagination__jump .el-input__wrapper:focus-within) {
  box-shadow: 0 0 0 2px var(--blue-primary) inset;
}
</style>
