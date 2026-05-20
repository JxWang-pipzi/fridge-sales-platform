<template>
  <div class="admin-orders-page">
    <div class="page-header">
      <h3>订单管理</h3>
    </div>

    <div class="filter-section">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="订单号">
          <el-input v-model="filterForm.orderNo" placeholder="请输入订单号" clearable style="width: 100%" />
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select v-model="filterForm.status" placeholder="请选择状态" clearable style="width: 100%">
            <el-option label="待付款" :value="0" />
            <el-option label="待发货" :value="1" />
            <el-option label="待收货" :value="2" />
            <el-option label="已完成" :value="3" />
            <el-option label="已取消" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="下单时间">
          <el-date-picker
            v-model="filterForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-section">
      <el-table v-loading="loading" :data="orderList" stripe style="width: 100%">
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="username" label="用户" width="100" />
        <el-table-column label="商品信息" min-width="200">
          <template #default="{ row }">
            <div v-for="item in row.orderItems" :key="item.id" class="order-item-info">
              {{ item.productName }} x {{ item.quantity }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="订单金额" width="100">
          <template #default="{ row }">
            ¥{{ row.totalAmount }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="160" />
        <el-table-column label="操作" width="80" fixed="right">
          <template #default="{ row }">
            <ActionDropdown
              :show-view="true"
              :show-ship="row.status === 1"
              :show-complete="row.status === 2"
              :show-cancel="row.status === 0"
              @view="handleViewDetail(row)"
              @ship="handleShip(row)"
              @complete="handleComplete(row)"
              @cancel="handleCancel(row)"
            />
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </div>

    <el-dialog 
      v-model="detailVisible" 
      title="订单详情" 
      width="800px"
      destroy-on-close
      :close-on-click-modal="false"
      class="order-detail-dialog"
    >
      <div class="order-detail-content">
        <el-descriptions :column="2" border style="width: 100%">
          <el-descriptions-item label="订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="订单状态">
            <el-tag :type="getStatusType(currentOrder.status)" size="small">
              {{ getStatusText(currentOrder.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="用户名">{{ currentOrder.username }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ currentOrder.phone }}</el-descriptions-item>
          <el-descriptions-item label="收货地址" :span="2">{{ currentOrder.address }}</el-descriptions-item>
          <el-descriptions-item label="下单时间">{{ currentOrder.createTime }}</el-descriptions-item>
          <el-descriptions-item label="支付时间">{{ currentOrder.payTime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="支付方式">
            {{ currentOrder.status === 0 ? '-' : (currentOrder.paymentType === 1 ? '支付宝' : '微信支付') }}
          </el-descriptions-item>
          <el-descriptions-item label="订单金额">
            <span style="color: #ef4444; font-weight: bold;">¥{{ currentOrder.totalAmount }}</span>
          </el-descriptions-item>
        </el-descriptions>

        <div class="product-section">
          <h4 class="section-title">商品信息</h4>
          <el-table :data="currentOrder.orderItems" border style="width: 100%">
            <el-table-column label="商品图片" width="80">
              <template #default="{ row }">
                <el-image :src="row.productImage" fit="cover" style="width: 50px; height: 50px;" />
              </template>
            </el-table-column>
            <el-table-column prop="productName" label="商品名称" />
            <el-table-column prop="price" label="单价" width="100">
              <template #default="{ row }">¥{{ row.price }}</template>
            </el-table-column>
            <el-table-column prop="quantity" label="数量" width="80" />
            <el-table-column label="小计" width="100">
              <template #default="{ row }">¥{{ row.price * row.quantity }}</template>
            </el-table-column>
          </el-table>
        </div>

        <div class="total-section">
          <span class="total-label">订单总额：</span>
          <span class="total-price">¥{{ currentOrder.totalAmount }}</span>
        </div>
      </div>
    </el-dialog>

    <el-dialog v-model="shipVisible" title="订单发货" width="500px" class="ship-dialog">
      <div class="ship-order-info">
        <span class="label">订单号：</span>
        <span class="value">{{ shipForm.orderNo }}</span>
      </div>
      <el-form ref="shipFormRef" :model="shipForm" :rules="shipRules" label-width="100px">
        <el-form-item label="物流公司" prop="expressCompany">
          <el-select v-model="shipForm.expressCompany" placeholder="请选择物流公司" style="width: 100%">
            <el-option label="顺丰速运" value="顺丰速运" />
            <el-option label="中通快递" value="中通快递" />
            <el-option label="圆通快递" value="圆通快递" />
            <el-option label="韵达快递" value="韵达快递" />
            <el-option label="申通快递" value="申通快递" />
          </el-select>
        </el-form-item>
        <el-form-item label="物流单号" prop="expressNo">
          <el-input v-model="shipForm.expressNo" placeholder="请输入物流单号" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="shipVisible = false">取消</el-button>
        <el-button type="primary" :loading="shipLoading" @click="handleConfirmShip">确认发货</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAdminOrderList, updateOrderStatus, shipOrder } from '@/api/order'
import ActionDropdown from '@/components/ActionDropdown.vue'

const loading = ref(false)
const shipLoading = ref(false)
const detailVisible = ref(false)
const shipVisible = ref(false)
const shipFormRef = ref(null)

const filterForm = reactive({
  orderNo: '',
  status: '',
  dateRange: null
})

const pagination = reactive({
  page: 1,
  pageSize: 10
})

const total = ref(0)
const orderList = ref([])
const currentOrder = ref({})
const shipForm = reactive({
  orderId: null,
  orderNo: '',
  expressCompany: '',
  expressNo: ''
})

const shipRules = {
  expressCompany: [{ required: true, message: '请选择物流公司', trigger: 'change' }],
  expressNo: [{ required: true, message: '请输入物流单号', trigger: 'blur' }]
}

const statusMap = {
  0: { text: '待付款', type: 'warning' },
  1: { text: '待发货', type: 'info' },
  2: { text: '待收货', type: '' },
  3: { text: '已完成', type: 'success' },
  4: { text: '已取消', type: 'danger' }
}

const getStatusText = (status) => statusMap[status]?.text || '未知'
const getStatusType = (status) => statusMap[status]?.type || ''

const fetchOrders = async () => {
  loading.value = true
  try {
    const res = await getAdminOrderList({
      page: pagination.page,
      pageSize: pagination.pageSize,
      ...filterForm
    })
    orderList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('获取订单列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  fetchOrders()
}

const handleReset = () => {
  filterForm.orderNo = ''
  filterForm.status = ''
  filterForm.dateRange = null
  pagination.page = 1
  fetchOrders()
}

const handleSizeChange = () => {
  pagination.page = 1
  fetchOrders()
}

const handlePageChange = () => {
  fetchOrders()
}

const handleViewDetail = (row) => {
  currentOrder.value = row
  detailVisible.value = true
}

const handleShip = (row) => {
  shipForm.orderId = row.id
  shipForm.orderNo = row.orderNo
  shipForm.expressCompany = ''
  shipForm.expressNo = ''
  shipVisible.value = true
}

const handleConfirmShip = async () => {
  try {
    await shipFormRef.value.validate()
    shipLoading.value = true
    await shipOrder(shipForm.orderId, {
      expressCompany: shipForm.expressCompany,
      expressNo: shipForm.expressNo
    })
    ElMessage.success(`订单「${shipForm.orderNo}」发货成功`)
    shipVisible.value = false
    fetchOrders()
  } catch (error) {
    if (error !== false) {
      ElMessage.error('发货失败，请重试')
      console.error('发货失败:', error)
    }
  } finally {
    shipLoading.value = false
  }
}

const handleCancel = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要取消订单「${row.orderNo}」吗？取消后订单将无法恢复。`,
      '取消订单确认',
      { 
        type: 'error',
        confirmButtonText: '确定取消',
        cancelButtonText: '返回',
        customClass: 'confirm-dialog'
      }
    )
    await updateOrderStatus(row.id, 4)
    ElMessage.success(`订单「${row.orderNo}」已取消`)
    fetchOrders()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消订单失败，请重试')
      console.error('取消订单失败:', error)
    }
  }
}

const handleComplete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要将订单「${row.orderNo}」标记为已完成吗？`,
      '确认完成订单',
      { 
        type: 'success',
        confirmButtonText: '确认完成',
        cancelButtonText: '取消',
        customClass: 'confirm-dialog'
      }
    )
    await updateOrderStatus(row.id, 3)
    ElMessage.success(`订单「${row.orderNo}」已完成`)
    fetchOrders()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('确认完成失败，请重试')
      console.error('确认完成失败:', error)
    }
  }
}

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.admin-orders-page {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  height: 100%;
  overflow-y: auto;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h3 {
  font-size: 18px;
  color: #333;
}

.filter-section {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.table-section {
  margin-bottom: 20px;
}

.order-item-info {
  font-size: 13px;
  color: #666;
  line-height: 1.8;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.ship-order-info {
  background: #f5f7fa;
  padding: 12px 16px;
  border-radius: 6px;
  margin-bottom: 20px;
}

.ship-order-info .label {
  color: #909399;
  font-size: 14px;
}

.ship-order-info .value {
  color: #303133;
  font-weight: 500;
  font-size: 14px;
}

.order-detail-content {
  padding: 0;
}

.product-section {
  margin-top: 24px;
}

.section-title {
  font-size: 16px;
  color: #333;
  margin: 0 0 12px 0;
  padding-bottom: 8px;
  border-bottom: 2px solid #409eff;
}

.total-section {
  margin-top: 20px;
  text-align: right;
  padding: 16px 0;
  border-top: 2px solid #e5e7eb;
}

.total-label {
  font-size: 16px;
  color: #666;
  margin-right: 12px;
}

.total-price {
  font-size: 24px;
  color: #f56c6c;
  font-weight: bold;
}

:deep(.el-table__row:hover > td) {
  background-color: #ecf5ff !important;
  transition: background-color 0.3s ease;
}

:deep(.el-dialog) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.order-detail-dialog .el-dialog__header) {
  background: #409eff;
  padding: 16px 20px;
  margin-right: 0;
  margin-left: 0;
  display: flex;
  align-items: center;
}

:deep(.order-detail-dialog .el-dialog__title) {
  color: #fff;
  font-weight: 500;
  flex: 1;
}

:deep(.order-detail-dialog .el-dialog__headerbtn) {
  position: relative;
  top: auto;
  right: 0;
  width: 32px;
  height: 32px;
  margin-left: auto;
}

:deep(.order-detail-dialog .el-dialog__headerbtn .el-dialog__close) {
  color: #fff;
  font-size: 18px;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.2s ease;
}

:deep(.order-detail-dialog .el-dialog__headerbtn:hover .el-dialog__close) {
  color: #fff;
  background-color: rgba(255, 255, 255, 0.2);
}

:deep(.order-detail-dialog .el-dialog__body) {
  padding: 20px 24px 24px;
}

:deep(.ship-dialog .el-dialog__header) {
  background: #67c23a;
  padding: 16px 20px;
  margin-right: 0;
  margin-left: 0;
  display: flex;
  align-items: center;
}

:deep(.ship-dialog .el-dialog__title) {
  color: #fff;
  font-weight: 500;
  flex: 1;
}

:deep(.ship-dialog .el-dialog__headerbtn) {
  position: relative;
  top: auto;
  right: 0;
  width: 32px;
  height: 32px;
  margin-left: auto;
}

:deep(.ship-dialog .el-dialog__headerbtn .el-dialog__close) {
  color: #fff;
  font-size: 18px;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.2s ease;
}

:deep(.ship-dialog .el-dialog__headerbtn:hover .el-dialog__close) {
  color: #fff;
  background-color: rgba(255, 255, 255, 0.2);
}

:deep(.ship-dialog .el-dialog__body) {
  padding: 24px;
}

:deep(.el-dialog__footer) {
  padding: 16px 24px;
  border-top: 1px solid #eee;
}

:deep(.confirm-dialog) {
  border-radius: 8px;
}

:deep(.el-descriptions) {
  width: 100%;
}

:deep(.el-descriptions__label) {
  font-weight: 500;
  background-color: #fafafa;
}
</style>
