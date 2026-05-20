<template>
  <div class="product-list">
    <pro-table
      ref="tableRef"
      title="商品管理"
      :columns="columns"
      :request="getProductList"
      selection
      @selection-change="handleSelectionChange"
    >
      <template #toolbar>
        <el-button type="primary" icon="Plus" @click="handleAdd">新建商品</el-button>
        <el-button type="danger" icon="Delete" :disabled="!selection.length" @click="handleBatchDelete">批量删除</el-button>
      </template>

      <!-- Custom Renders -->
      <template #image="{ row }">
        <el-image 
          :src="getImageUrl(row.image)" 
          :preview-src-list="[getImageUrl(row.image)]" 
          fit="cover" 
          style="width: 50px; height: 50px; border-radius: 4px;"
        >
          <template #error>
            <img :src="defaultImage" style="width: 50px; height: 50px; border-radius: 4px; object-fit: cover;" />
          </template>
        </el-image>
      </template>

      <template #status="{ row }">
        <el-tag :type="row.status === 1 ? 'success' : 'info'">
          {{ row.status === 1 ? '上架' : '下架' }}
        </el-tag>
      </template>

      <template #actions="{ row }">
        <ActionDropdown
          :show-edit="true"
          :show-delete="true"
          @edit="handleEdit(row)"
          @delete="handleDelete(row)"
        />
      </template>
    </pro-table>

    <!-- Edit Dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="currentId ? '编辑商品' : '新建商品'"
      width="600px"
      destroy-on-close
    >
      <pro-form
        ref="formRef"
        :schema="formSchema"
        :model-value="formData"
        label-width="100px"
        class="grid-layout"
        @submit="handleSubmit"
        @reset="dialogVisible = false"
      >
        <template #image="{ model }">
          <el-upload
            class="avatar-uploader"
            action="#"
            :show-file-list="false"
            :auto-upload="false"
            :on-change="(file) => handleFileChange(file, model)"
          >
            <img :src="getImageUrl(model.image)" class="avatar" />
          </el-upload>
        </template>
        <template #actions="{ submit, reset }">
          <div class="dialog-footer">
            <el-button @click="reset">取消</el-button>
            <el-button type="primary" :loading="submitLoading" @click="submit">确定</el-button>
          </div>
        </template>
      </pro-form>
    </el-dialog>

    <!-- Price Audit Dialog -->
    <el-dialog v-model="auditVisible" title="价格审核" width="400px">
      <div v-if="auditRow">
        <p>商品名称：{{ auditRow.name }}</p>
        <p>当前价格：¥{{ auditRow.price }}</p>
        <p>原价：¥{{ auditRow.originalPrice }}</p>
        <el-alert title="请确认价格设置是否合理" type="warning" :closable="false" style="margin-top: 10px" />
      </div>
      <template #footer>
        <el-button @click="auditVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAudit">通过审核</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Picture as IconPicture, Delete } from '@element-plus/icons-vue'
import ProTable from '@/components/pro/ProTable.vue'
import ProForm from '@/components/pro/ProForm.vue'
import ActionDropdown from '@/components/ActionDropdown.vue'
import { getProductList, createProduct, updateProduct, deleteProduct, batchDeleteProduct, auditProductPrice } from '@/api/product'
import defaultImage from '@/assets/images/fridge.jpg'

const tableRef = ref(null)
const formRef = ref(null)
const dialogVisible = ref(false)
const auditVisible = ref(false)
const submitLoading = ref(false)
const currentId = ref(null)
const selection = ref([])
const auditRow = ref(null)

const formData = reactive({
  name: '',
  brand: '',
  categoryId: null,
  sku: '',
  price: 0,
  originalPrice: 0,
  stock: 0,
  description: '',
  image: '',
  status: 1
})

const getImageUrl = (image) => {
  if (!image) return defaultImage
  if (image.startsWith('data:')) return image
  return defaultImage
}

const columns = [
  { prop: 'id', label: 'ID', width: 80, align: 'center' },
  { prop: 'image', label: '图片', width: 100, slot: 'image' },
  { prop: 'name', label: '商品名称', search: true },
  { prop: 'brand', label: '品牌', width: 100, search: true, align: 'center' },
  { prop: 'sku', label: 'SKU', width: 120, search: true, align: 'center' },
  { 
    prop: 'categoryId', 
    label: '分类', 
    search: true, 
    valueType: 'select',
    align: 'center',
    options: [
      { label: '单门冰箱', value: 1 },
      { label: '双门冰箱', value: 2 },
      { label: '对开门冰箱', value: 3 },
      { label: '多门冰箱', value: 4 },
      { label: '嵌入式冰箱', value: 5 }
    ],
    render: ({ row }) => {
      const map = { 
        1: '单门冰箱', 
        2: '双门冰箱', 
        3: '对开门冰箱', 
        4: '多门冰箱',
        5: '嵌入式冰箱'
      }
      return map[row.categoryId] || '-'
    }
  },
  { prop: 'price', label: '价格', width: 100, render: ({ row }) => `¥${row.price}`, align: 'center' },
  { prop: 'stock', label: '库存', width: 100, align: 'center' },
  { 
    prop: 'status', 
    label: '状态', 
    search: true, 
    valueType: 'select', 
    align: 'center',
    options: [
      { label: '上架', value: 1 },
      { label: '下架', value: 0 }
    ],
    slot: 'status' 
  },
  { prop: 'actions', label: '操作', width: 150, fixed: 'right', slot: 'actions', hideInTable: false, align: 'center' }
]

const formSchema = [
  { label: '商品名称', prop: 'name', required: true, span: 24, fullWidth: true },
  { label: '品牌', prop: 'brand', required: false, span: 12 },
  { label: 'SKU 编码', prop: 'sku', required: false, span: 12, props: { placeholder: '请输入 SKU 编码' } },
  { 
    label: '分类', 
    prop: 'categoryId', 
    type: 'select', 
    required: false,
    options: [
      { label: '单门冰箱', value: 1 },
      { label: '双门冰箱', value: 2 },
      { label: '对开门冰箱', value: 3 },
      { label: '多门冰箱', value: 4 },
      { label: '嵌入式冰箱', value: 5 }
    ],
    span: 12
  },
  { label: '价格', prop: 'price', type: 'number', required: true, props: { min: 0, precision: 2 }, span: 12 },
  { label: '原价', prop: 'originalPrice', type: 'number', props: { min: 0, precision: 2 }, span: 12 },
  { label: '库存', prop: 'stock', type: 'number', required: true, props: { min: 0 }, span: 12 },
  { label: '状态', prop: 'status', type: 'radio', options: [{ label: '上架', value: 1 }, { label: '下架', value: 0 }], span: 24, fullWidth: true },
  { label: '图片', prop: 'image', slot: 'image', span: 24, fullWidth: true },
  { label: '描述', prop: 'description', type: 'textarea', span: 24, fullWidth: true, props: { rows: 3 } }
]

const handleSelectionChange = (val) => {
  selection.value = val
}

const handleAdd = () => {
  currentId.value = null
  Object.assign(formData, {
    name: '',
    brand: '',
    categoryId: null,
    sku: '',
    price: 0,
    originalPrice: 0,
    stock: 0,
    description: '',
    image: '',
    status: 1
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  currentId.value = row.id
  Object.assign(formData, {
    name: row.name || '',
    brand: row.brand || '',
    categoryId: row.categoryId || null,
    sku: row.sku || '',
    price: row.price || 0,
    originalPrice: row.originalPrice || 0,
    stock: row.stock || 0,
    description: row.description || '',
    image: row.image || '',
    status: row.status !== undefined ? row.status : 1
  })
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该商品吗？', '提示', { type: 'warning' })
    await deleteProduct(row.id)
    ElMessage.success('删除成功')
    tableRef.value?.fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleBatchDelete = async () => {
  if (!selection.value.length) return
  try {
    await ElMessageBox.confirm(`确定删除选中的 ${selection.value.length} 个商品吗？`, '批量删除', { type: 'warning' })
    const ids = selection.value.map(item => item.id)
    await batchDeleteProduct(ids) // Mock API
    ElMessage.success('批量删除成功')
    tableRef.value?.fetchData()
    selection.value = []
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('批量删除失败')
  }
}

const handleAudit = (row) => {
  auditRow.value = row
  auditVisible.value = true
}

const confirmAudit = async () => {
  if (!auditRow.value) return
  try {
    await auditProductPrice(auditRow.value.id, auditRow.value.price)
    ElMessage.success('价格审核通过')
    auditVisible.value = false
  } catch (error) {
    ElMessage.error('审核失败')
  }
}

const handleSubmit = async (data) => {
  submitLoading.value = true
  try {
    if (currentId.value) {
      await updateProduct(currentId.value, data)
      ElMessage.success('更新成功')
    } else {
      await createProduct(data)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    tableRef.value?.fetchData()
  } catch (error) {
    ElMessage.error('提交失败')
  } finally {
    submitLoading.value = false
  }
}

const handleFileChange = (file, model) => {
  // Mock upload: read file as data URL
  const reader = new FileReader()
  reader.readAsDataURL(file.raw)
  reader.onload = () => {
    model.image = reader.result
  }
}
</script>

<style scoped>
.dialog-footer {
  text-align: right;
}
.avatar-uploader .avatar {
  width: 100px;
  height: 100px;
  display: block;
}
.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}
.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  text-align: center;
  line-height: 100px;
}
.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  color: #909399;
}
</style>
