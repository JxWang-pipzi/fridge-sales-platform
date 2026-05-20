<template>
  <div class="order-list">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>订单管理</span>
          <div class="header-stats">
            <el-tag type="warning">待发货: {{ pendingCount }}</el-tag>
            <el-tag type="primary">总订单: {{ totalOrders }}</el-tag>
          </div>
        </div>
      </template>
      <pro-table
        ref="tableRef"
        title="订单管理"
        :columns="columns"
        :request="getAdminOrderList"
      >
        <template #status="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ getStatusText(row.status) }}
          </el-tag>
        </template>

        <template #actions="{ row }">
          <ActionDropdown
            :show-view="true"
            :show-ship="row.status === 1"
            :show-edit="true"
            @view="handleDetail(row)"
            @ship="handleShip(row)"
            @edit="handleChangeStatus(row)"
          />
        </template>
      </pro-table>
    </el-card>

    <el-dialog v-model="statusDialogVisible" title="修改订单状态" width="400px">
      <el-form :model="statusForm" label-width="100px">
        <el-form-item label="当前状态">
          <el-tag :type="getStatusType(currentStatusRow?.status)">{{ getStatusText(currentStatusRow?.status) }}</el-tag>
        </el-form-item>
        <el-form-item label="新状态">
          <el-select v-model="statusForm.status" placeholder="请选择状态">
            <el-option label="待支付" :value="0" />
            <el-option label="待发货" :value="1" />
            <el-option label="待收货" :value="2" />
            <el-option label="已完成" :value="3" />
            <el-option label="已取消" :value="4" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="statusDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmStatusChange">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import ProTable from '@/components/pro/ProTable.vue'
import ActionDropdown from '@/components/ActionDropdown.vue'
import { getAdminOrderList, updateOrderStatus, shipOrder } from '@/api/order'

const router = useRouter()
const tableRef = ref(null)
const statusDialogVisible = ref(false)
const currentStatusRow = ref(null)
const pendingCount = ref(0)
const totalOrders = ref(0)
const statusForm = reactive({
  status: null
})

const columns = [
  { prop: 'orderNo', label: '订单号', search: true },
  { prop: 'userId', label: '用户ID', width: 100 },
  { prop: 'totalAmount', label: '总价', render: ({ row }) => `¥${row.totalAmount?.toFixed(2) || '0.00'}` },
  { 
    prop: 'status', 
    label: '状态', 
    search: true, 
    valueType: 'select',
    options: [
      { label: '待支付', value: 0 },
      { label: '待发货', value: 1 },
      { label: '待收货', value: 2 },
      { label: '已完成', value: 3 },
      { label: '已取消', value: 4 }
    ],
    slot: 'status'
  },
  { prop: 'createTime', label: '创建时间', width: 180 },
  { prop: 'actions', label: '操作', slot: 'actions', width: 80, fixed: 'right' }
]

const getStatusType = (status) => {
  const map = {
    0: 'warning',
    1: 'primary',
    2: 'success',
    3: 'info',
    4: 'danger'
  }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = {
    0: '待支付',
    1: '待发货',
    2: '待收货',
    3: '已完成',
    4: '已取消'
  }
  return map[status] || '未知'
}

const handleDetail = (row) => {
  router.push(`/admin/order-detail/${row.id}`)
}

const handleShip = async (row) => {
  try {
    await ElMessageBox.confirm(`确定对订单 ${row.orderNo} 进行发货操作吗？`, '发货确认', { type: 'warning' })
    await shipOrder(row.id, {
      expressCompany: '顺丰速运',
      expressNo: 'SF' + Date.now()
    })
    ElMessage.success('发货成功')
    tableRef.value?.fetchData()
    fetchOrderStats()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('发货失败')
  }
}

const handleChangeStatus = (row) => {
  currentStatusRow.value = row
  statusForm.status = row.status
  statusDialogVisible.value = true
}

const confirmStatusChange = async () => {
  if (!currentStatusRow.value) return
  try {
    await updateOrderStatus(currentStatusRow.value.id, { status: statusForm.status })
    ElMessage.success('状态修改成功')
    statusDialogVisible.value = false
    tableRef.value?.fetchData()
    fetchOrderStats()
  } catch (error) {
    ElMessage.error('状态修改失败')
  }
}

const fetchOrderStats = async () => {
  try {
    const res = await getAdminOrderList({ current: 1, size: 1000 })
    if (res.code === 200 && res.data) {
      const records = res.data.records || []
      totalOrders.value = res.data.total || 0
      pendingCount.value = records.filter(item => item.status === 1).length
    }
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  fetchOrderStats()
})
</script>

<style scoped>
.order-list {
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
</style>