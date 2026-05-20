<template>
  <div class="order-detail-page">
    <div class="container">
      <div v-loading="loading" class="detail-content">
        <template v-if="order">
          <div class="page-header">
            <el-button text @click="router.back()">
              <el-icon><ArrowLeft /></el-icon>
              返回订单列表
            </el-button>
            <h1 class="page-title">订单详情</h1>
          </div>

          <div class="order-status-card">
            <div class="status-info">
              <div class="status-icon" :class="`status-${order.status}`">
                <el-icon v-if="order.status === 0" :size="32"><Clock /></el-icon>
                <el-icon v-else-if="order.status === 1" :size="32"><Box /></el-icon>
                <el-icon v-else-if="order.status === 2" :size="32"><Van /></el-icon>
                <el-icon v-else-if="order.status === 3" :size="32"><CircleCheck /></el-icon>
                <el-icon v-else :size="32"><CircleClose /></el-icon>
              </div>
              <div class="status-text">
                <h2>{{ getStatusText(order.status) }}</h2>
                <p>{{ getStatusDesc(order.status) }}</p>
              </div>
            </div>
            <div class="status-actions">
              <el-button v-if="order.status === 0" type="primary" :loading="paying" @click="handlePay">
                {{ paying ? '支付中...' : '立即付款' }}
              </el-button>
              <el-button v-if="order.status === 0" :disabled="cancelling" @click="handleCancel">
                {{ cancelling ? '取消中...' : '取消订单' }}
              </el-button>
              <el-button v-if="order.status === 2" type="success" :loading="confirming" @click="handleConfirm">
                {{ confirming ? '确认中...' : '确认收货' }}
              </el-button>
              <el-button v-if="order.status === 3 || order.status === 4" type="danger" :loading="deleting" @click="handleDelete">
                {{ deleting ? '删除中...' : '删除订单' }}
              </el-button>
            </div>
          </div>

          <div class="info-cards">
            <div class="info-card">
              <h3 class="card-title">订单信息</h3>
              <div class="info-list">
                <div class="info-row">
                  <span class="label">订单编号</span>
                  <span class="value">{{ order.orderNo }}</span>
                </div>
                <div class="info-row">
                  <span class="label">创建时间</span>
                  <span class="value">{{ formatDate(order.createTime) }}</span>
                </div>
                <div class="info-row" v-if="order.payTime">
                  <span class="label">支付时间</span>
                  <span class="value">{{ formatDate(order.payTime) }}</span>
                </div>
                <div class="info-row" v-if="order.deliverTime">
                  <span class="label">发货时间</span>
                  <span class="value">{{ formatDate(order.deliverTime) }}</span>
                </div>
                <div class="info-row" v-if="order.receiveTime">
                  <span class="label">收货时间</span>
                  <span class="value">{{ formatDate(order.receiveTime) }}</span>
                </div>
              </div>
            </div>

            <div class="info-card">
              <h3 class="card-title">收货信息</h3>
              <div class="info-list">
                <div class="info-row">
                  <span class="label">收货人</span>
                  <span class="value">{{ order.receiverName }}</span>
                </div>
                <div class="info-row">
                  <span class="label">联系电话</span>
                  <span class="value">{{ order.receiverPhone }}</span>
                </div>
                <div class="info-row">
                  <span class="label">收货地址</span>
                  <span class="value">{{ order.receiverAddress }}</span>
                </div>
              </div>
            </div>
          </div>

          <div class="products-card">
            <h3 class="card-title">商品信息</h3>
            <div class="products-list">
              <div v-for="item in order.orderItems" :key="item.id" class="product-item">
                <img :src="item.productImage || defaultImage" :alt="item.productName" class="product-image" @error="handleImageError" />
                <div class="product-info">
                  <router-link :to="`/product/${item.productId}`" class="product-name">
                    {{ parseProductName(item.productName).displayName }}
                  </router-link>
                  <div v-if="parseProductName(item.productName).sku" class="product-sku">
                    SKU: {{ parseProductName(item.productName).sku }}
                  </div>
                  <p class="product-spec">数量：{{ item.quantity }}</p>
                </div>
                <div class="product-price">
                  <div class="unit-price">¥{{ formatPrice(item.price) }}</div>
                  <div class="total-price">小计：¥{{ formatPrice(item.totalAmount) }}</div>
                </div>
              </div>
            </div>
          </div>

          <div class="amount-card">
            <div class="amount-row">
              <span>商品总额</span>
              <span>¥{{ formatPrice(order.totalAmount) }}</span>
            </div>
            <div class="amount-row">
              <span>运费</span>
              <span>¥0.00</span>
            </div>
            <div class="amount-row total">
              <span>实付金额</span>
              <span class="pay-amount">¥{{ formatPrice(order.payAmount) }}</span>
            </div>
          </div>
        </template>

        <div v-else-if="!loading" class="empty-state">
          <el-icon :size="64"><Document /></el-icon>
          <h3>订单不存在</h3>
          <router-link to="/orders">
            <el-button type="primary">返回订单列表</el-button>
          </router-link>
        </div>
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
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, Clock, Box, Van, CircleCheck, CircleClose, Document } from '@element-plus/icons-vue'
import { getOrderDetail, cancelOrder, confirmReceive, payOrder, deleteOrder } from '@/api/order'
import { parseProductName } from '@/utils/product'
import defaultImage from '@/assets/images/fridge.jpg'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const order = ref(null)
const paying = ref(false)
const cancelling = ref(false)
const confirming = ref(false)
const deleting = ref(false)
const paymentDialogVisible = ref(false)

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

const getStatusText = (status) => {
  const texts = {
    0: '待付款',
    1: '待发货',
    2: '待收货',
    3: '交易完成',
    4: '已取消'
  }
  return texts[status] || '未知状态'
}

const getStatusDesc = (status) => {
  const descs = {
    0: '请尽快完成支付，超时订单将自动取消',
    1: '商家正在准备发货，请耐心等待',
    2: '商品已发出，请注意查收',
    3: '订单已完成，感谢您的购买',
    4: '订单已取消'
  }
  return descs[status] || ''
}

const fetchOrderDetail = async () => {
  loading.value = true
  try {
    const id = route.params.id
    const res = await getOrderDetail(id)
    order.value = res.data
  } catch (error) {
    console.error('获取订单详情失败:', error)
    ElMessage.error('获取订单详情失败')
  } finally {
    loading.value = false
  }
}

const handlePay = () => {
  paymentDialogVisible.value = true
}

const handlePaymentSelect = async (paymentType) => {
  try {
    paying.value = true
    paymentDialogVisible.value = false
    await payOrder(order.value.id, { paymentType })
    ElMessage.success(`支付成功，支付方式：${paymentType === 1 ? '支付宝' : '微信支付'}`)
    fetchOrderDetail()
  } catch (error) {
    console.error('支付失败:', error)
    ElMessage.error('支付失败')
  } finally {
    paying.value = false
  }
}

const handleCancel = async () => {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '取消订单', {
      confirmButtonText: '确定取消',
      cancelButtonText: '取消',
      type: 'warning'
    })
    cancelling.value = true
    await cancelOrder(order.value.id)
    ElMessage.success('订单已取消')
    fetchOrderDetail()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消订单失败:', error)
      ElMessage.error('取消订单失败')
    }
  } finally {
    cancelling.value = false
  }
}

const handleConfirm = async () => {
  try {
    await ElMessageBox.confirm('确认已收到商品吗？', '确认收货', {
      confirmButtonText: '确认收货',
      cancelButtonText: '取消',
      type: 'success'
    })
    confirming.value = true
    await confirmReceive(order.value.id)
    ElMessage.success('确认收货成功')
    fetchOrderDetail()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('确认收货失败:', error)
      ElMessage.error('确认收货失败')
    }
  } finally {
    confirming.value = false
  }
}

const handleDelete = async () => {
  try {
    await ElMessageBox.confirm('确定要删除该订单吗？删除后无法恢复。', '删除订单', {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
    deleting.value = true
    await deleteOrder(order.value.id)
    ElMessage.success('订单已删除')
    router.push('/orders')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除订单失败:', error)
      ElMessage.error('删除订单失败')
    }
  } finally {
    deleting.value = false
  }
}

const handleImageError = (e) => {
  e.target.src = defaultImage
}

onMounted(() => {
  fetchOrderDetail()
})
</script>

<style scoped>
.order-detail-page {
  padding: 24px 0;
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 16px;
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-size: 24px;
  font-weight: bold;
  color: #1f2937;
  margin-top: 16px;
}

.order-status-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 32px;
  background-color: #ffffff;
  border-radius: 12px;
  margin-bottom: 24px;
}

.status-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.status-icon {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.status-icon.status-0 {
  background-color: #fef3c7;
  color: #d97706;
}

.status-icon.status-1 {
  background-color: #dbeafe;
  color: #2563eb;
}

.status-icon.status-2 {
  background-color: #d1fae5;
  color: #059669;
}

.status-icon.status-3 {
  background-color: #dcfce7;
  color: #16a34a;
}

.status-icon.status-4 {
  background-color: #f3f4f6;
  color: #6b7280;
}

.status-text h2 {
  font-size: 24px;
  font-weight: bold;
  color: #1f2937;
  margin: 0 0 8px;
}

.status-text p {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
}

.status-actions {
  display: flex;
  gap: 12px;
}

.info-cards {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
  margin-bottom: 24px;
}

.info-card,
.products-card,
.amount-card {
  background-color: #ffffff;
  border-radius: 12px;
  padding: 24px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e5e7eb;
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-row {
  display: flex;
  justify-content: space-between;
}

.info-row .label {
  color: #6b7280;
  font-size: 14px;
}

.info-row .value {
  color: #1f2937;
  font-size: 14px;
}

.products-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.product-item {
  display: flex;
  gap: 16px;
  padding: 16px 0;
  border-bottom: 1px solid #f3f4f6;
}

.product-item:last-child {
  border-bottom: none;
}

.product-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
  background-color: #f3f4f6;
}

.product-info {
  flex: 1;
}

.product-name {
  font-size: 14px;
  color: #1f2937;
  text-decoration: none;
  font-weight: 500;
  display: block;
  margin-bottom: 8px;
  transition: color 0.2s;
}

.product-name:hover {
  color: #2563eb;
}

.product-spec {
  font-size: 13px;
  color: #9ca3af;
  margin: 0;
}

.product-price {
  text-align: right;
}

.unit-price {
  font-size: 14px;
  color: #6b7280;
  margin-bottom: 4px;
}

.total-price {
  font-size: 14px;
  color: #ef4444;
  font-weight: 500;
}

.amount-card {
  margin-top: 24px;
}

.amount-row {
  display: flex;
  justify-content: flex-end;
  gap: 24px;
  padding: 12px 0;
  font-size: 14px;
  color: #6b7280;
}

.amount-row.total {
  border-top: 1px solid #e5e7eb;
  padding-top: 16px;
  margin-top: 8px;
  font-size: 16px;
  color: #1f2937;
  font-weight: 500;
}

.pay-amount {
  font-size: 24px;
  color: #ef4444;
  font-weight: bold;
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
  margin: 0 0 24px;
}

@media (max-width: 768px) {
  .info-cards {
    grid-template-columns: 1fr;
  }

  .order-status-card {
    flex-direction: column;
    gap: 20px;
    text-align: center;
  }

  .status-info {
    flex-direction: column;
  }

  .product-item {
    flex-direction: column;
    align-items: flex-start;
  }

  .product-price {
    text-align: left;
    width: 100%;
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
