<template>
  <div class="order-detail" v-loading="loading">
    <el-card shadow="never" class="mb-4">
      <template #header>
        <div class="card-header">
          <span>订单信息</span>
          <el-tag :type="getStatusType(order.status)">{{ getStatusText(order.status) }}</el-tag>
        </div>
      </template>
      <el-descriptions :column="3" border>
        <el-descriptions-item label="订单号">{{ order.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatTime(order.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="支付时间">{{ formatTime(order.payTime) || '-' }}</el-descriptions-item>
        <el-descriptions-item label="发货时间">{{ formatTime(order.deliverTime) || '-' }}</el-descriptions-item>
        <el-descriptions-item label="完成时间">{{ formatTime(order.receiveTime) || '-' }}</el-descriptions-item>
        <el-descriptions-item label="支付方式">
          {{ order.status === 0 ? '-' : (order.paymentType === 1 ? '支付宝' : '微信') }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card shadow="never" class="mb-4">
      <template #header>
        <span>收货人信息</span>
      </template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="收货人">{{ order.receiverName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ order.receiverPhone }}</el-descriptions-item>
        <el-descriptions-item label="收货地址" :span="2">{{ order.receiverAddress }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card shadow="never" class="mb-4">
      <template #header>
        <span>商品信息</span>
      </template>
      <el-table :data="order.orderItems" border style="width: 100%">
        <el-table-column prop="productImage" label="商品图片" width="100">
          <template #default="{ row }">
            <el-image 
              :src="row.productImage || defaultImage" 
              :preview-src-list="[row.productImage || defaultImage]"
              style="width: 50px; height: 50px" 
              fit="cover"
            >
              <template #error>
                <div class="image-error">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
          </template>
        </el-table-column>
        <el-table-column prop="productName" label="商品名称" />
        <el-table-column prop="sku" label="SKU" width="120">
          <template #default="{ row }">{{ row.sku || '-' }}</template>
        </el-table-column>
        <el-table-column prop="price" label="单价" width="120">
          <template #default="{ row }">¥{{ row.price?.toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="80" align="center" />
        <el-table-column prop="totalAmount" label="小计" width="120">
          <template #default="{ row }">¥{{ row.totalAmount?.toFixed(2) }}</template>
        </el-table-column>
      </el-table>
      <div class="total-price">
        <span>订单总额：</span>
        <span class="price">¥{{ order.totalAmount?.toFixed(2) || '0.00' }}</span>
      </div>
    </el-card>

    <div class="footer-actions">
      <el-button @click="$router.back()">返回列表</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { Picture } from '@element-plus/icons-vue'
import { getAdminOrderDetail } from '@/api/order'
import fridgeImage from '@/assets/images/fridge.jpg'

const route = useRoute()
const loading = ref(false)
const defaultImage = fridgeImage
const order = ref({
  orderItems: []
})

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

const formatTime = (time) => {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 19)
}

const fetchOrderDetail = async () => {
  loading.value = true
  try {
    const res = await getAdminOrderDetail(route.params.id)
    if (res.success || res.code === 200) {
      order.value = res.data || {}
    }
  } catch (error) {
    console.error('获取订单详情失败', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  if (route.params.id) {
    fetchOrderDetail()
  }
})
</script>

<style scoped>
.order-detail {
  padding: 20px;
}
.mb-4 {
  margin-bottom: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.total-price {
  margin-top: 20px;
  text-align: right;
  font-size: 16px;
}
.total-price .price {
  color: #f56c6c;
  font-size: 20px;
  font-weight: bold;
}
.footer-actions {
  text-align: center;
  margin-top: 20px;
}

.image-error {
  width: 50px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  color: #909399;
  font-size: 24px;
}
</style>
