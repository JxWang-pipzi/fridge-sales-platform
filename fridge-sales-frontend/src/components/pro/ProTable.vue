<template>
  <div class="pro-table">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>{{ title }}</span>
          <div class="card-header-actions">
            <slot name="toolbar"></slot>
            <el-tooltip content="导出" placement="top">
              <el-button circle icon="Download" @click="handleExport" />
            </el-tooltip>
            <el-popover placement="bottom" title="列设置" :width="200" trigger="click">
              <template #reference>
                <el-button circle icon="Setting" />
              </template>
              <el-checkbox-group v-model="visibleColumns">
                <div v-for="col in columns" :key="col.prop" class="column-setting-item">
                  <el-checkbox :value="col.prop" :label="col.label" />
                </div>
              </el-checkbox-group>
            </el-popover>
          </div>
        </div>
      </template>
      
      <!-- Search Form -->
      <div v-if="searchColumns.length > 0" class="search-form">
        <el-form :inline="true" :model="searchForm" class="demo-form-inline">
          <el-form-item v-for="col in searchColumns" :key="col.prop" :label="col.label">
            <el-input v-if="!col.valueType || col.valueType === 'input'" v-model="searchForm[col.prop]" :placeholder="col.label" clearable />
            <el-select v-else-if="col.valueType === 'select'" v-model="searchForm[col.prop]" :placeholder="col.label" clearable>
              <el-option v-for="opt in col.options" :key="opt.value" :label="opt.label" :value="opt.value" />
            </el-select>
            <el-date-picker
              v-else-if="col.valueType === 'date'"
              v-model="searchForm[col.prop]"
              type="date"
              placeholder="选择日期"
              value-format="YYYY-MM-DD"
              clearable
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table
        v-loading="loading"
        :data="data"
        style="width: 100%"
        border
        @selection-change="handleSelectionChange"
      >
        <el-table-column v-if="selection" type="selection" width="55" />
        <template v-for="col in columns" :key="col.prop">
          <el-table-column v-if="!col.hideInTable && visibleColumns.includes(col.prop)" v-bind="col">
            <template #default="scope" v-if="col.render">
              <component :is="col.render" :row="scope.row" :index="scope.$index" />
            </template>
            <template #default="scope" v-else-if="$slots[col.prop]">
              <slot :name="col.prop" v-bind="scope"></slot>
            </template>
          </el-table-column>
        </template>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Download, Setting } from '@element-plus/icons-vue'

const props = defineProps({
  title: String,
  columns: {
    type: Array,
    default: () => []
  },
  request: {
    type: Function,
    required: true
  },
  selection: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['selection-change'])

const loading = ref(false)
const data = ref([])
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const searchForm = reactive({})
const visibleColumns = ref([])

const searchColumns = computed(() => {
  return props.columns.filter(col => col.search)
})

watch(() => props.columns, (newVal) => {
  visibleColumns.value = newVal.map(col => col.prop)
}, { immediate: true })

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.currentPage,
      size: pagination.pageSize,
      ...searchForm
    }
    const res = await props.request(params)
    // Adapt to different API responses structure
    if (res.code === 200 || res.success) {
       const result = res.data || res
       data.value = result.list || result.records || []
       pagination.total = parseInt(result.total) || 0
    } else {
       // Fallback or error handling
       data.value = []
       pagination.total = 0
    }
  } catch (error) {
    console.error('Fetch data failed:', error)
    data.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

const handleExport = () => {
  ElMessage.success('正在导出数据，请稍候...')
  setTimeout(() => {
    ElMessage.success('导出成功！(Mock)')
  }, 1500)
}


const handleSearch = () => {
  pagination.currentPage = 1
  fetchData()
}

const handleReset = () => {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = ''
  })
  handleSearch()
}

const handleSizeChange = (val) => {
  pagination.pageSize = val
  fetchData()
}

const handleCurrentChange = (val) => {
  pagination.currentPage = val
  fetchData()
}

const handleSelectionChange = (val) => {
  emit('selection-change', val)
}

onMounted(() => {
  // Initialize search form with default values if any
  searchColumns.value.forEach(col => {
      if (col.defaultValue !== undefined) {
          searchForm[col.prop] = col.defaultValue
      }
  })
  fetchData()
})

defineExpose({
  fetchData,
  handleSearch,
  handleReset
})
</script>

<style scoped>
.pro-table {
  margin-bottom: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
.search-form {
  margin-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
  padding-bottom: 20px;
}
</style>
