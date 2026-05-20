<template>
  <div class="inventory-list">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>库存管理</span>
          <div class="header-stats">
            <el-tag type="danger" v-if="lowStockCount > 0">{{ lowStockCount }} 个商品库存不足</el-tag>
            <el-tag type="success" v-else>库存充足</el-tag>
          </div>
        </div>
      </template>
      <pro-table
        ref="tableRef"
        title="库存管理"
        :columns="columns"
        :request="getInventoryList"
      >
        <template #stock="{ row }">
          <el-tag :type="row.stock < (row.lowStockThreshold || 10) ? 'danger' : 'success'">
            {{ row.stock }}
          </el-tag>
          <el-tooltip content="库存过低" v-if="row.stock < (row.lowStockThreshold || 10)">
            <el-icon class="low-stock-icon"><Warning /></el-icon>
          </el-tooltip>
        </template>
        <template #status="{ row }">
          <el-tag :type="row.stock < (row.lowStockThreshold || 10) ? 'danger' : 'success'">
            {{ row.stock < (row.lowStockThreshold || 10) ? '库存不足' : '库存充足' }}
          </el-tag>
        </template>
        <template #actions="{ row }">
          <ActionDropdown
            :show-edit="true"
            @edit="handleEdit(row)"
          />
        </template>
      </pro-table>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      title="调整库存"
      width="400px"
      append-to-body
    >
      <el-form :model="form" label-width="100px">
        <el-form-item label="商品名称">
          <span>{{ currentItem?.productName }}</span>
        </el-form-item>
        <el-form-item label="当前库存">
          <span>{{ currentItem?.stock }}</span>
        </el-form-item>
        <el-form-item label="调整后库存">
          <el-input-number v-model="form.stock" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Warning } from '@element-plus/icons-vue'
import ProTable from '@/components/pro/ProTable.vue'
import ActionDropdown from '@/components/ActionDropdown.vue'
import { getInventoryList, updateStock } from '@/api/inventory'

const tableRef = ref(null)
const dialogVisible = ref(false)
const currentItem = ref(null)
const submitting = ref(false)
const lowStockCount = ref(0)
const form = reactive({
  stock: 0
})

const columns = [
  { prop: 'id', label: 'ID', width: 80 },
  { prop: 'productName', label: '商品名称', search: true },
  { prop: 'stock', label: '库存数量', slot: 'stock', sortable: true },
  { prop: 'lowStockThreshold', label: '预警阈值', width: 120 },
  { prop: 'status', label: '状态', slot: 'status', width: 100 },
  { prop: 'actions', label: '操作', slot: 'actions', width: 80, fixed: 'right' }
]

const handleEdit = (row) => {
  currentItem.value = row
  form.stock = row.stock
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!currentItem.value) return
  submitting.value = true
  try {
    await updateStock(currentItem.value.id, form.stock)
    ElMessage.success('库存更新成功')
    dialogVisible.value = false
    tableRef.value.fetchData()
  } catch (error) {
    ElMessage.error('库存更新失败')
    console.error(error)
  } finally {
    submitting.value = false
  }
}

const fetchLowStockCount = async () => {
  try {
    const res = await getInventoryList({ current: 1, size: 1000 })
    if (res.code === 200 && res.data && res.data.records) {
      lowStockCount.value = res.data.records.filter(
        item => item.stock < (item.lowStockThreshold || 10)
      ).length
    }
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  fetchLowStockCount()
})
</script>

<style scoped>
.inventory-list {
  padding: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.header-stats {
  display: flex;
  gap: 10px;
}
.low-stock-icon {
  margin-left: 5px;
  color: #f56c6c;
  vertical-align: middle;
}
</style>