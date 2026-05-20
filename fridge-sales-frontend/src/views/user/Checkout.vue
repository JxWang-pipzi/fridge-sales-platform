<template>
  <div class="checkout-page">
    <div class="container">
      <div class="page-header">
        <h1 class="page-title">确认订单</h1>
      </div>

      <div v-loading="loading" class="checkout-content">
        <div class="checkout-main">
          <div class="section-card">
            <div class="section-header">
              <h3>
                <el-icon><Location /></el-icon>
                收货地址
              </h3>
              <button class="add-btn" @click="showAddressDialog = true">
                <el-icon><Plus /></el-icon>
                新增地址
              </button>
            </div>
            <div class="address-list" v-if="addressList.length > 0">
              <div
                v-for="addr in addressList"
                :key="addr.id"
                class="address-item"
                :class="{ active: selectedAddressId === addr.id }"
                @click="selectedAddressId = addr.id"
              >
                <div class="address-radio">
                  <el-icon v-if="selectedAddressId === addr.id" class="checked-icon"><CircleCheckFilled /></el-icon>
                  <div v-else class="unchecked-circle"></div>
                </div>
                <div class="address-info">
                  <div class="address-top">
                    <span class="receiver-name">{{ addr.receiverName }}</span>
                    <span class="receiver-phone">{{ addr.receiverPhone }}</span>
                    <span v-if="addr.isDefault" class="default-tag">默认</span>
                  </div>
                  <div class="address-detail">
                    {{ addr.province }}{{ addr.city }}{{ addr.district }}{{ addr.detailAddress }}
                  </div>
                </div>
              </div>
            </div>
            <div v-else class="empty-address">
              <p>暂无收货地址，请先添加</p>
            </div>
          </div>

          <div class="section-card">
            <div class="section-header">
              <h3>
                <el-icon><Goods /></el-icon>
                商品清单
              </h3>
            </div>
            <div class="product-list">
              <div v-for="item in cartItems" :key="item.id" class="product-item">
                <img :src="item.productImage || defaultImage" :alt="item.productName" class="product-image" @error="handleImageError" />
                <div class="product-info">
                  <div class="product-name">{{ parseProductName(item.productName).displayName }}</div>
                  <div v-if="parseProductName(item.productName).sku" class="product-sku">SKU: {{ parseProductName(item.productName).sku }}</div>
                  <div class="product-spec" v-if="item.spec">{{ item.spec }}</div>
                </div>
                <div class="product-price">¥{{ item.price }}</div>
                <div class="product-quantity">x{{ item.quantity }}</div>
                <div class="product-total">¥{{ (item.price * item.quantity).toFixed(2) }}</div>
              </div>
            </div>
          </div>

          <div class="section-card">
            <div class="section-header">
              <h3>
                <el-icon><EditPen /></el-icon>
                订单备注
              </h3>
            </div>
            <div class="remark-input">
              <el-input
                v-model="orderRemark"
                type="textarea"
                :rows="3"
                placeholder="请输入订单备注（选填）"
                maxlength="200"
                show-word-limit
              />
            </div>
          </div>
        </div>

        <div class="checkout-sidebar">
          <div class="summary-card">
            <h3 class="summary-title">订单金额</h3>
            <div class="summary-row">
              <span>商品总额</span>
              <span>¥{{ totalPrice.toFixed(2) }}</span>
            </div>
            <div class="summary-row">
              <span>运费</span>
              <span class="free-shipping">免运费</span>
            </div>
            <div class="summary-divider"></div>
            <div class="summary-total">
              <span>应付金额</span>
              <span class="total-price">¥{{ totalPrice.toFixed(2) }}</span>
            </div>
            <button class="submit-btn" @click="handleSubmit" :loading="submitting" :disabled="!canSubmit">
              提交订单
            </button>
          </div>

          <div class="tips-card">
            <h4>温馨提示</h4>
            <ul>
              <li>请确认收货地址无误</li>
              <li>支持 7 天无理由退换货</li>
              <li>如有问题请联系客服</li>
            </ul>
          </div>
        </div>
      </div>
    </div>

    <el-dialog v-model="showAddressDialog" title="新增收货地址" width="500px">
      <el-form ref="addressFormRef" :model="addressForm" :rules="addressRules" label-width="80px">
        <el-form-item label="收货人" prop="receiverName">
          <el-input v-model="addressForm.receiverName" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="receiverPhone">
          <el-input v-model="addressForm.receiverPhone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="省份" prop="province">
          <el-input v-model="addressForm.province" placeholder="请输入省份" />
        </el-form-item>
        <el-form-item label="城市" prop="city">
          <el-input v-model="addressForm.city" placeholder="请输入城市" />
        </el-form-item>
        <el-form-item label="区县" prop="district">
          <el-input v-model="addressForm.district" placeholder="请输入区县" />
        </el-form-item>
        <el-form-item label="详细地址" prop="detailAddress">
          <el-input v-model="addressForm.detailAddress" type="textarea" :rows="2" placeholder="请输入详细地址" />
        </el-form-item>
        <el-form-item label="默认地址">
          <el-switch v-model="addressForm.isDefault" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddressDialog = false">取消</el-button>
        <el-button type="primary" @click="handleAddAddress" :loading="addingAddress">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Location, Plus, CircleCheckFilled, Goods, EditPen } from '@element-plus/icons-vue'
import { getCartList } from '@/api/cart'
import { createOrder } from '@/api/order'
import { getAddressList, addAddress } from '@/api/user'
import { useCartStore } from '@/stores/cart'
import { parseProductName } from '@/utils/product'
import defaultImage from '@/assets/images/fridge.jpg'

const router = useRouter()
const route = useRoute()
const cartStore = useCartStore()

const loading = ref(false)
const submitting = ref(false)
const addingAddress = ref(false)
const showAddressDialog = ref(false)

const addressList = ref([])
const cartItems = ref([])
const selectedAddressId = ref(null)
const orderRemark = ref('')
const addressFormRef = ref(null)

const addressForm = reactive({
  receiverName: '',
  receiverPhone: '',
  province: '',
  city: '',
  district: '',
  detailAddress: '',
  isDefault: false
})

const addressRules = {
  receiverName: [{ required: true, message: '请输入收货人姓名', trigger: 'blur' }],
  receiverPhone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  province: [{ required: true, message: '请输入省份', trigger: 'blur' }],
  city: [{ required: true, message: '请输入城市', trigger: 'blur' }],
  district: [{ required: true, message: '请输入区县', trigger: 'blur' }],
  detailAddress: [{ required: true, message: '请输入详细地址', trigger: 'blur' }]
}

const totalPrice = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
})

const canSubmit = computed(() => {
  return selectedAddressId.value && cartItems.value.length > 0
})

const fetchAddressList = async () => {
  try {
    const res = await getAddressList()
    addressList.value = res.data || []
    const defaultAddr = addressList.value.find(addr => addr.isDefault)
    if (defaultAddr) {
      selectedAddressId.value = defaultAddr.id
    } else if (addressList.value.length > 0) {
      selectedAddressId.value = addressList.value[0].id
    }
  } catch (error) {
    console.error('获取地址列表失败:', error)
  }
}

const fetchCartItems = async () => {
  try {
    const res = await getCartList()
    const selectedIds = route.query.items ? route.query.items.split(',').map(Number) : []
    const allItems = res.data || []
    
    if (selectedIds.length > 0) {
      cartItems.value = allItems.filter(item => selectedIds.includes(item.id))
    } else {
      cartItems.value = allItems
    }
    
    if (cartItems.value.length === 0) {
      ElMessage.warning('请选择要结算的商品')
      router.push('/cart')
    }
  } catch (error) {
    console.error('获取购物车失败:', error)
  }
}

const handleAddAddress = async () => {
  try {
    await addressFormRef.value.validate()
    addingAddress.value = true
    await addAddress(addressForm)
    ElMessage.success('添加成功')
    showAddressDialog.value = false
    fetchAddressList()
    addressFormRef.value.resetFields()
  } catch (error) {
    console.error('添加地址失败:', error)
    if (error && typeof error === 'object' && !error.message) {
      const messages = []
      Object.keys(error).forEach(key => {
        if (Array.isArray(error[key])) {
          messages.push(...error[key])
        }
      })
      if (messages.length > 0) {
        ElMessage.error(messages[0])
      } else {
        ElMessage.error('添加地址失败，请检查输入信息')
      }
    } else if (error?.message) {
      ElMessage.error(error.message)
    } else {
      ElMessage.error('添加地址失败，请重试')
    }
  } finally {
    addingAddress.value = false
  }
}

const handleSubmit = async () => {
  if (!selectedAddressId.value) {
    ElMessage.warning('请选择收货地址')
    return
  }
  
  if (cartItems.value.length === 0) {
    ElMessage.warning('购物车中没有商品')
    return
  }
  
  try {
    await ElMessageBox.confirm('确认提交订单吗？', '提示', {
      type: 'info'
    })
    
    submitting.value = true
    console.log('[操作日志] 开始提交订单')
    console.log('[操作日志] 选中的购物车商品ID:', cartItems.value.map(item => item.id))
    
    const orderData = {
      addressId: selectedAddressId.value,
      cartItemIds: cartItems.value.map(item => item.id)
    }
    
    const res = await createOrder(orderData)
    console.log('[操作日志] 订单创建成功，订单ID:', res.data)
    
    console.log('[操作日志] 开始刷新购物车数量')
    await cartStore.fetchCartCount()
    console.log('[操作日志] 购物车数量已更新:', cartStore.cartCount)
    
    ElMessage.success('订单创建成功')
    router.push(`/orders`)
  } catch (error) {
    if (error !== 'cancel') {
      console.error('[操作日志] 创建订单失败:', error)
      ElMessage.error('订单创建失败，请重试')
    }
  } finally {
    submitting.value = false
  }
}

const handleImageError = (e) => {
  e.target.src = defaultImage
}

onMounted(() => {
  loading.value = true
  Promise.all([fetchAddressList(), fetchCartItems()]).finally(() => {
    loading.value = false
  })
})
</script>

<style scoped>
.checkout-page {
  padding: 24px 0;
  background-color: #f3f4f6;
  min-height: calc(100vh - 60px);
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-size: 1.875rem;
  font-weight: bold;
  color: var(--foreground);
}

.checkout-content {
  display: grid;
  grid-template-columns: 1fr 360px;
  gap: 24px;
}

.checkout-main {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.section-card {
  background-color: #ffffff;
  border-radius: var(--radius);
  border: 1px solid var(--border);
  padding: 20px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-header h3 {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: var(--foreground);
  margin: 0;
}

.add-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  background-color: var(--blue-primary);
  color: #ffffff;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.2s;
}

.add-btn:hover {
  background-color: var(--blue-hover);
}

.address-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.address-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px;
  border: 2px solid var(--border);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.address-item:hover {
  border-color: var(--blue-primary);
}

.address-item.active {
  border-color: var(--blue-primary);
  background-color: #eff6ff;
}

.address-radio {
  margin-right: 12px;
  display: flex;
  align-items: center;
}

.unchecked-circle {
  width: 1em;
  height: 1em;
  border: 1px solid #dcdfe6;
  border-radius: 50%;
  box-sizing: border-box;
}

.checked-icon {
  color: var(--blue-primary);
  font-size: 1.2em;
}

.address-info {
  flex: 1;
}

.address-top {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.receiver-name {
  font-size: 16px;
  font-weight: 600;
  color: var(--foreground);
}

.receiver-phone {
  color: var(--muted-foreground);
}

.default-tag {
  padding: 2px 8px;
  background-color: var(--blue-primary);
  color: #ffffff;
  font-size: 12px;
  border-radius: 4px;
}

.address-detail {
  color: var(--muted-foreground);
  font-size: 14px;
  line-height: 1.6;
}

.empty-address {
  text-align: center;
  padding: 40px 20px;
  color: var(--muted-foreground);
}

.product-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.product-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px 0;
  border-bottom: 1px solid var(--border);
}

.product-item:last-child {
  border-bottom: none;
}

.product-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  background-color: #f3f4f6;
  border-radius: 8px;
}

.product-info {
  flex: 1;
}

.product-name {
  font-size: 14px;
  font-weight: 500;
  color: var(--foreground);
  margin-bottom: 4px;
}

.product-spec {
  font-size: 12px;
  color: var(--muted-foreground);
}

.product-price,
.product-quantity,
.product-total {
  width: 100px;
  text-align: center;
}

.product-price {
  color: var(--destructive);
}

.product-total {
  color: var(--destructive);
  font-weight: 600;
}

.remark-input {
  margin-top: 8px;
}

.checkout-sidebar {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.summary-card {
  background-color: #ffffff;
  border: 1px solid var(--border);
  border-radius: var(--radius);
  padding: 20px;
}

.summary-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--foreground);
  margin-bottom: 16px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
  font-size: 14px;
  color: var(--muted-foreground);
}

.free-shipping {
  color: #16a34a;
}

.summary-divider {
  height: 1px;
  background-color: var(--border);
  margin: 16px 0;
}

.summary-total {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.summary-total span:first-child {
  font-size: 14px;
  color: var(--foreground);
}

.total-price {
  font-size: 24px;
  font-weight: bold;
  color: var(--destructive);
}

.submit-btn {
  width: 100%;
  padding: 14px;
  background-color: var(--blue-primary);
  color: #ffffff;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s;
}

.submit-btn:hover:not(:disabled) {
  background-color: var(--blue-hover);
}

.submit-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.tips-card {
  background-color: #ffffff;
  border: 1px solid var(--border);
  border-radius: var(--radius);
  padding: 16px;
}

.tips-card h4 {
  font-size: 14px;
  font-weight: 600;
  color: var(--foreground);
  margin-bottom: 12px;
}

.tips-card ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.tips-card li {
  font-size: 13px;
  color: var(--muted-foreground);
  margin-bottom: 8px;
  padding-left: 16px;
  position: relative;
}

.tips-card li::before {
  content: '•';
  position: absolute;
  left: 0;
  color: var(--blue-primary);
}

@media (max-width: 1024px) {
  .checkout-content {
    grid-template-columns: 1fr;
  }
  
  .checkout-sidebar {
    order: -1;
  }
}

.checkout-page :deep(.el-dialog .el-input__wrapper) {
  border: none !important;
  box-shadow: none !important;
  background-color: #f5f5f7 !important;
}

.checkout-page :deep(.el-dialog .el-textarea__inner) {
  border: none !important;
  box-shadow: none !important;
  background-color: #f5f5f7 !important;
}

.checkout-page :deep(.el-dialog .el-input__inner) {
  background-color: transparent !important;
}

.checkout-page :deep(.el-dialog .el-form-item__content) {
  background-color: #f5f5f7 !important;
  border-radius: 8px !important;
}
</style>
