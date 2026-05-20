<template>
  <div class="orders-page">
    <div class="container">
      <div class="page-header">
        <h1 class="page-title">我的订单</h1>
      </div>

      <!-- 订单状态筛选 -->
      <div class="filter-tabs">
        <button
          class="filter-tab"
          :class="{ active: activeTab === 'all' }"
          :disabled="loading"
          @click="activeTab = 'all'; fetchOrders()"
        >
          全部订单
          <span v-if="orderCounts.all > 0" class="tab-count">{{ orderCounts.all }}</span>
        </button>
        <button
          class="filter-tab"
          :class="{ active: activeTab === 'pending' }"
          :disabled="loading"
          @click="activeTab = 'pending'; fetchOrders()"
        >
          待付款
          <span v-if="orderCounts.pending > 0" class="tab-count warning">{{ orderCounts.pending }}</span>
        </button>
        <button
          class="filter-tab"
          :class="{ active: activeTab === 'paid' }"
          :disabled="loading"
          @click="activeTab = 'paid'; fetchOrders()"
        >
          待发货
          <span v-if="orderCounts.paid > 0" class="tab-count primary">{{ orderCounts.paid }}</span>
        </button>
        <button
          class="filter-tab"
          :class="{ active: activeTab === 'shipped' }"
          :disabled="loading"
          @click="activeTab = 'shipped'; fetchOrders()"
        >
          待收货
          <span v-if="orderCounts.shipped > 0" class="tab-count success">{{ orderCounts.shipped }}</span>
        </button>
        <button
          class="filter-tab"
          :class="{ active: activeTab === 'completed' }"
          :disabled="loading"
          @click="activeTab = 'completed'; fetchOrders()"
        >
          已完成
        </button>
      </div>

      <!-- 订单列表 -->
      <div v-loading="loading" class="orders-list">
        <template v-if="orderList.length > 0">
          <div v-for="order in orderList" :key="order.id" class="order-card">
            <!-- 订单头部 -->
            <div class="order-header">
              <div class="header-left">
                <span class="order-no">订单号：{{ order.orderNo }}</span>
                <span class="order-time">{{ formatDate(order.createTime) }}</span>
              </div>
              <div class="header-right">
                <span class="status-tag" :class="`status-${order.status}`">
                  {{ getStatusText(order.status) }}
                </span>
              </div>
            </div>

            <!-- 订单商品 -->
            <div class="order-items">
              <div v-for="item in order.orderItems" :key="item.id" class="order-item">
                <div class="item-left">
                  <img :src="item.productImage || defaultImage" :alt="item.productName" class="item-image" @error="handleImageError" />
                  <div class="item-info">
                    <router-link :to="`/product/${item.productId}`" class="item-name">
                      {{ getParsedName(item.productName).displayName }}
                    </router-link>
                    <div v-if="getParsedName(item.productName).sku" class="item-sku">
                      SKU: {{ getParsedName(item.productName).sku }}
                    </div>
                    <p class="item-spec">数量：{{ item.quantity }}</p>
                  </div>
                </div>
                <div class="item-right">
                  <div class="item-price">¥{{ formatPrice(item.price) }}</div>
                  <div class="item-total">¥{{ formatPrice(item.price * item.quantity) }}</div>
                </div>
              </div>
            </div>

            <!-- 收货信息 -->
            <div class="receiver-info">
              <h4>收货信息</h4>
              <p><span class="label">收货人：</span>{{ order.receiverName }}</p>
              <p><span class="label">联系电话：</span>{{ order.receiverPhone }}</p>
              <p><span class="label">详细地址：</span>{{ order.receiverAddress }}</p>
            </div>

            <!-- 订单底部 -->
            <div class="order-footer">
              <div class="order-total">
                <span>订单金额：</span>
                <span class="total-price">¥{{ formatPrice(order.totalAmount) }}</span>
              </div>
              <div class="order-actions">
                <el-button v-if="order.status === 0" type="primary" :loading="order.paying" @click="handlePay(order)">
                  {{ order.paying ? '支付中...' : '立即付款' }}
                </el-button>
                <el-button v-if="order.status === 0" :disabled="order.cancelling" @click="handleCancel(order)">
                  {{ order.cancelling ? '取消中...' : '取消订单' }}
                </el-button>
                <el-button v-if="order.status === 2" type="success" :loading="order.confirming" @click="handleConfirm(order)">
                  {{ order.confirming ? '确认中...' : '确认收货' }}
                </el-button>
                <el-button @click="handleViewDetail(order)">
                  查看详情
                </el-button>
                <el-button v-if="order.status === 3 || order.status === 4" type="danger" :loading="order.deleting" @click="handleDelete(order)">
                  {{ order.deleting ? '删除中...' : '删除订单' }}
                </el-button>
              </div>
            </div>
          </div>
        </template>

        <div v-else class="empty-state">
          <el-icon :size="64"><Document /></el-icon>
          <h3>暂无订单</h3>
          <p>快去挑选心仪的商品吧</p>
          <router-link to="/products">
            <el-button type="primary">去购物</el-button>
          </router-link>
        </div>
      </div>

      <!-- 分页 -->
      <div v-if="total > 0" class="pagination">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[5, 10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </div>

    <!-- 支付方式选择对话框 -->
    <el-dialog 
      v-model="paymentDialogVisible" 
      title="选择支付方式" 
      width="400px"
      :close-on-click-modal="false"
      class="payment-dialog"
    >
      <div class="payment-buttons">
        <el-button 
          type="primary" 
          class="payment-btn alipay"
          @click="handlePaymentSelect(1)"
        >
          支付宝
        </el-button>
        <el-button 
          type="success" 
          class="payment-btn wechat"
          @click="handlePaymentSelect(2)"
        >
          微信支付
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Document } from '@element-plus/icons-vue'
import { getOrderList, cancelOrder, confirmReceive, payOrder, deleteOrder } from '@/api/order'
import { parseProductName } from '@/utils/product'
import { logAction, logSuccess, logError } from '@/utils/logger'
import defaultImage from '@/assets/images/fridge.jpg'

const router = useRouter()

const loading = ref(false)
const activeTab = ref('all')
const orderList = ref([])
const total = ref(0)
let refreshTimer = null

const paymentDialogVisible = ref(false)
const currentPayingOrder = ref(null)

const orderCounts = ref({
  all: 0,
  pending: 0,
  paid: 0,
  shipped: 0,
  completed: 0
})

const pagination = reactive({
  page: 1,
  pageSize: 10
})

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const formatPrice = (price) => {
  return Number(price).toFixed(2)
}

const getParsedName = (name) => {
  return parseProductName(name)
}

const getStatusType = (status) => {
  const types = {
    0: 'warning',
    1: 'primary',
    2: 'success',
    3: 'success',
    4: 'info'
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    0: '待付款',
    1: '待发货',
    2: '待收货',
    3: '已完成',
    4: '已取消'
  }
  return texts[status] || '未知状态'
}

const statusMap = {
  'all': null,
  'pending': 0,
  'paid': 1,
  'shipped': 2,
  'completed': 3
}

const fetchOrders = async () => {
  logAction('获取订单列表', { tab: activeTab.value, page: pagination.page })
  loading.value = true
  try {
    const params = {
      current: pagination.page,
      size: pagination.pageSize
    }
    const tabStatus = statusMap[activeTab.value]
    if (tabStatus !== null && tabStatus !== undefined) {
      params.status = tabStatus
    }
    const res = await getOrderList(params)
    orderList.value = (res.data?.records || []).map(item => ({
      ...item,
      paying: false,
      cancelling: false,
      confirming: false,
      deleting: false
    }))
    total.value = res.data?.total || 0
    logSuccess('获取订单列表成功', { count: orderList.value.length })
  } catch (error) {
    logError('获取订单列表失败', error)
    ElMessage.error('获取订单列表失败')
  } finally {
    loading.value = false
  }
}

const fetchOrderCounts = async () => {
  try {
    const res = await getOrderList({ current: 1, size: 1000 })
    const orders = res.data?.records || []
    orderCounts.value = {
      all: orders.length,
      pending: orders.filter(o => o.status === 0).length,
      paid: orders.filter(o => o.status === 1).length,
      shipped: orders.filter(o => o.status === 2).length,
      completed: orders.filter(o => o.status === 3).length
    }
  } catch (error) {
    console.error('获取订单统计失败:', error)
  }
}

const handlePay = (order) => {
  logAction('点击支付按钮', { orderId: order.id, orderNo: order.orderNo })
  currentPayingOrder.value = order
  paymentDialogVisible.value = true
}

const handlePaymentSelect = async (paymentType) => {
  if (!currentPayingOrder.value) return
  
  logAction('选择支付方式', { orderId: currentPayingOrder.value.id, paymentType: paymentType === 1 ? '支付宝' : '微信' })
  try {
    currentPayingOrder.value.paying = true
    paymentDialogVisible.value = false
    await payOrder(currentPayingOrder.value.id, { paymentType })
    logSuccess('支付成功', { orderId: currentPayingOrder.value.id, paymentType })
    ElMessage.success(`支付成功，支付方式：${paymentType === 1 ? '支付宝' : '微信支付'}`)
    fetchOrders()
    fetchOrderCounts()
  } catch (error) {
    logError('支付失败', error)
    ElMessage.error('支付失败')
  } finally {
    currentPayingOrder.value.paying = false
    currentPayingOrder.value = null
  }
}

const handleCancel = async (order) => {
  logAction('点击取消订单', { orderId: order.id, orderNo: order.orderNo })
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '取消订单', {
      confirmButtonText: '确定取消',
      cancelButtonText: '取消',
      type: 'warning'
    })
    order.cancelling = true
    await cancelOrder(order.id)
    logSuccess('取消订单成功', { orderId: order.id })
    ElMessage.success('订单已取消')
    fetchOrders()
    fetchOrderCounts()
  } catch (error) {
    if (error !== 'cancel') {
      logError('取消订单失败', error)
      ElMessage.error('取消订单失败')
    }
  } finally {
    order.cancelling = false
  }
}

const handleConfirm = async (order) => {
  logAction('点击确认收货', { orderId: order.id, orderNo: order.orderNo })
  try {
    await ElMessageBox.confirm('确认已收到商品吗？', '确认收货', {
      confirmButtonText: '确认收货',
      cancelButtonText: '取消',
      type: 'success'
    })
    order.confirming = true
    await confirmReceive(order.id)
    logSuccess('确认收货成功', { orderId: order.id })
    ElMessage.success('确认收货成功')
    fetchOrders()
    fetchOrderCounts()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('确认收货失败:', error)
      ElMessage.error('确认收货失败')
    }
  } finally {
    order.confirming = false
  }
}

const handleViewDetail = (order) => {
  logAction('查看订单详情', { orderId: order.id, orderNo: order.orderNo })
  router.push(`/order-detail/${order.id}`)
}

const handleDelete = async (order) => {
  logAction('点击删除订单', { orderId: order.id, orderNo: order.orderNo })
  try {
    await ElMessageBox.confirm('确定要删除该订单吗？删除后无法恢复。', '删除订单', {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
    order.deleting = true
    await deleteOrder(order.id)
    logSuccess('删除订单成功', { orderId: order.id })
    ElMessage.success('订单已删除')
    fetchOrders()
    fetchOrderCounts()
  } catch (error) {
    if (error !== 'cancel') {
      logError('删除订单失败', error)
      ElMessage.error('删除订单失败')
    }
  } finally {
    order.deleting = false
  }
}

const handleSizeChange = () => {
  pagination.page = 1
  fetchOrders()
}

const handlePageChange = () => {
  fetchOrders()
}

const handleImageError = (e) => {
  e.target.src = defaultImage
}

const startAutoRefresh = () => {
  refreshTimer = setInterval(() => {
    fetchOrders()
    fetchOrderCounts()
  }, 10000)
}

const stopAutoRefresh = () => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
    refreshTimer = null
  }
}

onMounted(() => {
  fetchOrders()
  fetchOrderCounts()
  startAutoRefresh()
})

onUnmounted(() => {
  stopAutoRefresh()
})
</script>

<style scoped>
.orders-page {
  padding: 24px 0;
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.page-title {
  font-size: 24px;
  font-weight: bold;
  color: #1f2937;
  margin-bottom: 24px;
}

.filter-tabs {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
  padding: 16px;
  background-color: #ffffff;
  border-radius: 12px;
}

.filter-tab {
  padding: 10px 20px;
  background: none;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  color: #6b7280;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.filter-tab:hover {
  border-color: #2563eb;
  color: #2563eb;
}

.filter-tab.active {
  background-color: #2563eb;
  border-color: #2563eb;
  color: #ffffff;
}

.filter-tab:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.tab-count {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 18px;
  height: 18px;
  padding: 0 6px;
  margin-left: 6px;
  font-size: 12px;
  font-weight: 500;
  background-color: #e5e7eb;
  border-radius: 9px;
  color: #6b7280;
}

.tab-count.warning {
  background-color: #fef3c7;
  color: #d97706;
}

.tab-count.primary {
  background-color: #dbeafe;
  color: #2563eb;
}

.tab-count.success {
  background-color: #d1fae5;
  color: #059669;
}

.filter-tab.active .tab-count {
  background-color: rgba(255, 255, 255, 0.2);
  color: #ffffff;
}

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.order-card {
  background-color: #ffffff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background-color: #f9fafb;
  border-bottom: 1px solid #e5e7eb;
}

.header-left {
  display: flex;
  gap: 16px;
  align-items: center;
}

.order-no {
  font-size: 14px;
  color: #6b7280;
}

.order-time {
  font-size: 14px;
  color: #9ca3af;
}

.status-tag {
  display: inline-flex;
  align-items: center;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.status-tag.status-0 {
  background-color: #fef3c7;
  color: #d97706;
}

.status-tag.status-1 {
  background-color: #dbeafe;
  color: #2563eb;
}

.status-tag.status-2 {
  background-color: #d1fae5;
  color: #059669;
}

.status-tag.status-3 {
  background-color: #dcfce7;
  color: #16a34a;
}

.status-tag.status-4 {
  background-color: #f3f4f6;
  color: #6b7280;
}

.order-items {
  padding: 20px;
}

.order-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #f3f4f6;
}

.order-item:last-child {
  border-bottom: none;
}

.item-left {
  display: flex;
  gap: 16px;
  align-items: center;
  flex: 1;
}

.item-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
  background-color: #f3f4f6;
}

.item-info {
  flex: 1;
}

.item-name {
  font-size: 14px;
  color: #1f2937;
  text-decoration: none;
  font-weight: 500;
  display: block;
  margin-bottom: 8px;
  transition: color 0.2s;
}

.item-name:hover {
  color: #2563eb;
}

.item-sku {
  font-size: 12px;
  color: #9ca3af;
  font-family: 'Courier New', monospace;
  margin-bottom: 4px;
}

.item-spec {
  font-size: 13px;
  color: #9ca3af;
  margin: 0;
}

.item-right {
  text-align: right;
}

.item-price {
  font-size: 14px;
  color: #6b7280;
  margin-bottom: 4px;
}

.item-total {
  font-size: 16px;
  color: #ef4444;
  font-weight: bold;
}

.receiver-info {
  padding: 16px 20px;
  background-color: #f9fafb;
  border-top: 1px solid #e5e7eb;
  border-bottom: 1px solid #e5e7eb;
}

.receiver-info h4 {
  font-size: 14px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 8px;
}

.receiver-info p {
  font-size: 13px;
  color: #6b7280;
  margin: 4px 0;
}

.receiver-info p .label {
  color: #374151;
  font-weight: 500;
  margin-right: 4px;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
}

.order-total {
  font-size: 14px;
  color: #6b7280;
}

.total-price {
  font-size: 20px;
  color: #ef4444;
  font-weight: bold;
  margin-left: 8px;
}

.order-actions {
  display: flex;
  gap: 12px;
}

.empty-state {
  text-align: center;
  padding: 80px 24px;
  background-color: #ffffff;
  border-radius: 12px;
}

.empty-state .el-icon {
  color: #d1d5db;
  margin-bottom: 16px;
}

.empty-state h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 8px;
}

.empty-state p {
  font-size: 14px;
  color: #9ca3af;
  margin: 0 0 24px;
}

.pagination {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .filter-tabs {
    flex-wrap: wrap;
  }

  .filter-tab {
    flex: 1;
    min-width: 80px;
    text-align: center;
  }

  .order-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .item-right {
    text-align: left;
    width: 100%;
  }

  .order-footer {
    flex-direction: column;
    gap: 16px;
    align-items: flex-end;
  }
}

.payment-buttons {
  display: flex;
  gap: 16px;
  justify-content: center;
}

.payment-btn {
  flex: 1;
  min-width: 140px;
  height: 48px;
  font-size: 16px;
  font-weight: 500;
  border-radius: 8px;
}

.payment-btn.alipay {
  background-color: #1677ff;
  border-color: #1677ff;
}

.payment-btn.alipay:hover {
  background-color: #4096ff;
  border-color: #4096ff;
}

.payment-btn.wechat {
  background-color: #07c160;
  border-color: #07c160;
}

.payment-btn.wechat:hover {
  background-color: #38b93c;
  border-color: #38b93c;
}
</style>
