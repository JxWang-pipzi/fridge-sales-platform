<template>
  <div class="admin-page">
    <div class="page-header">
      <h2>待处理订单</h2>
    </div>

    <el-tabs v-model="activeTab" @tab-change="handleTabChange" class="order-tabs">
      <el-tab-pane label="待付款" name="0">
        <template #label>
          <span class="tab-label">
            <el-icon><Clock /></el-icon>
            待付款
            <el-badge v-if="tabCounts['0']" :value="tabCounts['0']" class="tab-badge" />
          </span>
        </template>
      </el-tab-pane>
      <el-tab-pane label="待发货" name="1">
        <template #label>
          <span class="tab-label">
            <el-icon><Box /></el-icon>
            待发货
            <el-badge v-if="tabCounts['1']" :value="tabCounts['1']" class="tab-badge" />
          </span>
        </template>
      </el-tab-pane>
      <el-tab-pane label="已发货" name="2">
        <template #label>
          <span class="tab-label">
            <el-icon><Van /></el-icon>
            已发货
            <el-badge v-if="tabCounts['2']" :value="tabCounts['2']" class="tab-badge" />
          </span>
        </template>
      </el-tab-pane>
      <el-tab-pane label="已完成" name="3">
        <template #label>
          <span class="tab-label">
            <el-icon><CircleCheck /></el-icon>
            已完成
            <el-badge v-if="tabCounts['3']" :value="tabCounts['3']" class="tab-badge" />
          </span>
        </template>
      </el-tab-pane>
    </el-tabs>

    <el-table :data="orderList" v-loading="loading" stripe style="width: 100%">
      <el-table-column prop="orderNo" label="订单号" min-width="180" />
      <el-table-column prop="receiverName" label="收货人" min-width="100" />
      <el-table-column prop="receiverPhone" label="联系电话" min-width="130" />
      <el-table-column prop="totalAmount" label="订单金额" min-width="120">
        <template #default="{ row }">
          ¥{{ row.totalAmount?.toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="下单时间" min-width="180">
        <template #default="{ row }">
          {{ formatTime(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" min-width="150" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleView(row)">查看</el-button>
          <el-button v-if="row.status === 1" type="success" link @click="handleShip(row)">发货</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog 
      v-model="detailVisible" 
      title="订单详情" 
      width="900px"
      destroy-on-close
      :close-on-click-modal="false"
      class="order-detail-dialog"
    >
      <div class="order-detail">
        <div class="detail-section">
          <h3>订单信息</h3>
          <el-descriptions :column="2" border style="width: 100%">
            <el-descriptions-item label="订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
            <el-descriptions-item label="订单状态">
              <el-tag :type="getStatusType(currentOrder.status)">
                {{ getStatusText(currentOrder.status) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="下单时间">{{ formatTime(currentOrder.createTime) }}</el-descriptions-item>
            <el-descriptions-item label="支付方式">
              {{ currentOrder.status === 0 ? '-' : (currentOrder.paymentType === 1 ? '支付宝' : '微信') }}
            </el-descriptions-item>
            <el-descriptions-item label="订单金额" class="amount">
              <span class="price">¥{{ currentOrder.totalAmount?.toFixed(2) }}</span>
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <div class="detail-section">
          <h3>收货信息</h3>
          <el-descriptions :column="2" border style="width: 100%">
            <el-descriptions-item label="收货人">{{ currentOrder.receiverName }}</el-descriptions-item>
            <el-descriptions-item label="联系电话">{{ currentOrder.receiverPhone }}</el-descriptions-item>
            <el-descriptions-item label="收货地址" :span="2">{{ currentOrder.receiverAddress }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <div class="detail-section">
          <h3>商品信息</h3>
          <el-table :data="currentOrder.orderItems" border style="width: 100%">
            <el-table-column label="商品图片" width="100">
              <template #default="{ row }">
                <img 
                  :src="row.productImage || fridgeImage" 
                  @error="($event) => $event.target.src = fridgeImage"
                  style="width: 60px; height: 60px; border-radius: 4px; object-fit: cover;"
                />
              </template>
            </el-table-column>
            <el-table-column prop="productName" label="商品名称" min-width="200">
              <template #default="{ row }">
                <div class="product-name-cell">
                  <div class="product-name">{{ row.productName }}</div>
                  <div v-if="row.sku" class="product-sku">SKU: {{ row.sku }}</div>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="单价" width="120">
              <template #default="{ row }">
                ¥{{ row.price?.toFixed(2) }}
              </template>
            </el-table-column>
            <el-table-column prop="quantity" label="数量" width="80" align="center" />
            <el-table-column label="小计" width="120">
              <template #default="{ row }">
                <span class="price">¥{{ row.totalAmount?.toFixed(2) }}</span>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </el-dialog>

    <el-dialog 
      v-model="shipVisible" 
      title="发货确认" 
      width="600px"
      destroy-on-close
      :close-on-click-modal="false"
      class="ship-dialog"
    >
      <div class="ship-content">
        <el-alert
          title="发货确认"
          type="info"
          :closable="false"
          show-icon
          style="margin-bottom: 20px;"
        />
        <el-descriptions :column="1" border style="width: 100%">
          <el-descriptions-item label="订单号">{{ currentShipOrder.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="收货人">{{ currentShipOrder.receiverName }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ currentShipOrder.receiverPhone }}</el-descriptions-item>
          <el-descriptions-item label="收货地址">{{ currentShipOrder.receiverAddress }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button @click="shipVisible = false">取消</el-button>
        <el-button type="primary" :loading="shipLoading" @click="confirmShip">
          确认发货
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Picture, Clock, Box, Van, CircleCheck } from '@element-plus/icons-vue'
import request from '@/utils/request'
import fridgeImage from '@/assets/images/fridge.jpg'
import { logAction, logSuccess, logError, logAdminAction } from '@/utils/logger'

const loading = ref(false)
const activeTab = ref('0')
const orderList = ref([])
const detailVisible = ref(false)
const shipVisible = ref(false)
const shipLoading = ref(false)
const currentOrder = ref({})
const currentShipOrder = ref({})
const tabCounts = ref({ '0': 0, '1': 0, '2': 0, '3': 0 })

const formatTime = (time) => {
  if (!time) return ''
  return time.replace('T', ' ')
}

const getStatusText = (status) => {
  const statusMap = {
    0: '待付款',
    1: '待发货',
    2: '待收货',
    3: '已完成',
    4: '已取消'
  }
  return statusMap[status] || '未知'
}

const getStatusType = (status) => {
  const typeMap = {
    0: 'warning',
    1: 'primary',
    2: 'info',
    3: 'success',
    4: 'danger'
  }
  return typeMap[status] || ''
}

const fetchOrders = async () => {
  logAction('管理员获取订单列表', { status: activeTab.value })
  loading.value = true
  try {
    const res = await request({
      url: '/admin/order/list',
      method: 'get',
      params: { status: activeTab.value, current: 1, size: 50 }
    })
    orderList.value = res.data?.records || []
    logSuccess('获取订单列表成功', { count: orderList.value.length })
  } catch (error) {
    logError('获取订单列表失败', error)
  } finally {
    loading.value = false
  }
}

const handleTabChange = () => {
  logAction('切换订单标签', { tab: activeTab.value })
  fetchOrders()
}

const handleView = async (row) => {
  logAdminAction('admin', '查看订单详情', { orderId: row.id, orderNo: row.orderNo })
  try {
    const res = await request({ url: `/admin/order/${row.id}`, method: 'get' })
    currentOrder.value = res.data || {}
    detailVisible.value = true
  } catch (error) {
    logError('获取订单详情失败', error)
    ElMessage.error('获取订单详情失败')
  }
}

const handleShip = (row) => {
  logAdminAction('admin', '点击发货按钮', { orderId: row.id, orderNo: row.orderNo })
  currentShipOrder.value = row
  shipVisible.value = true
}

const confirmShip = async () => {
  logAdminAction('admin', '确认发货', { orderId: currentShipOrder.value.id, orderNo: currentShipOrder.value.orderNo })
  try {
    shipLoading.value = true
    await request({ 
      url: `/admin/order/deliver/${currentShipOrder.value.id}`, 
      method: 'put',
      data: {
        expressCompany: '顺丰速运',
        expressNo: 'SF' + Date.now()
      }
    })
    logSuccess('发货成功', { orderId: currentShipOrder.value.id, orderNo: currentShipOrder.value.orderNo })
    ElMessage.success(`订单「${currentShipOrder.value.orderNo}」发货成功`)
    shipVisible.value = false
    fetchOrders()
    fetchTabCounts()
  } catch (error) {
    logError('发货失败', error)
    ElMessage.error('发货失败，请重试')
  } finally {
    shipLoading.value = false
  }
}

const fetchTabCounts = async () => {
  try {
    const promises = ['0', '1', '2', '3'].map(status => 
      request({
        url: '/admin/order/list',
        method: 'get',
        params: { status, current: 1, size: 1 }
      })
    )
    const results = await Promise.all(promises)
    results.forEach((res, index) => {
      tabCounts.value[index.toString()] = res.data?.total || 0
    })
  } catch (error) {
    console.error('获取订单数量失败:', error)
  }
}

onMounted(() => {
  fetchOrders()
  fetchTabCounts()
})
</script>

<style scoped>
.admin-page {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  height: 100%;
  overflow-y: auto;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 20px;
  color: #333;
}

.order-tabs {
  margin-bottom: 20px;
}

.tab-label {
  display: flex;
  align-items: center;
  gap: 6px;
}

.tab-badge {
  margin-left: 4px;
}

:deep(.el-tabs__item) {
  transition: all 0.3s ease;
  padding: 0 24px;
}

:deep(.el-tabs__item.is-active) {
  font-weight: 600;
  color: #409eff;
}

:deep(.el-tabs__active-bar) {
  transition: all 0.3s ease;
}

:deep(.el-tabs__content) {
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

.order-detail {
  padding: 0;
}

.ship-content {
  padding: 10px 0;
}

.detail-section {
  margin-bottom: 24px;
}

.detail-section h3 {
  font-size: 16px;
  color: #333;
  margin: 0 0 12px 0;
  padding-bottom: 8px;
  border-bottom: 2px solid #409eff;
}

.price {
  color: #f56c6c;
  font-weight: bold;
  font-size: 14px;
}

.image-placeholder {
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  color: #909399;
  font-size: 24px;
  border-radius: 4px;
}

.product-name-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.product-name {
  font-size: 14px;
  color: #333;
}

.product-sku {
  font-size: 12px;
  color: #909399;
  font-family: 'Courier New', monospace;
}

:deep(.el-descriptions) {
  width: 100%;
}

:deep(.el-descriptions__label) {
  font-weight: 500;
  color: #606266;
}

:deep(.el-table) {
  margin-top: 0;
  width: 100%;
}

:deep(.el-table__row:hover > td) {
  background-color: #ecf5ff !important;
  transition: background-color 0.3s ease;
}

:deep(.el-dialog) {
  border-radius: 8px;
  overflow: hidden;
}
</style>
