<template>
  <div class="admin-products-page">
    <div class="page-header">
      <h3>商品管理</h3>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        添加商品
      </el-button>
    </div>

    <div class="filter-section">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="商品名称">
          <el-input v-model="filterForm.keyword" placeholder="请输入商品名称" clearable style="width: 100%" />
        </el-form-item>
        <el-form-item label="商品分类">
          <el-select v-model="filterForm.category" placeholder="请选择分类" clearable style="width: 100%">
            <el-option label="单门冰箱" value="single" />
            <el-option label="双门冰箱" value="double" />
            <el-option label="对开门冰箱" value="side-by-side" />
            <el-option label="多门冰箱" value="multi" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filterForm.status" placeholder="请选择状态" clearable style="width: 100%">
            <el-option label="上架" value="on" />
            <el-option label="下架" value="off" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-section">
      <el-table 
        v-loading="loading" 
        :data="productList" 
        stripe
        :row-class-name="tableRowClassName"
        @rowmouseenter="handleRowHover"
        @rowmouseleave="handleRowLeave"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="商品图片" width="100">
          <template #default="{ row }">
            <el-image :src="row.image || defaultImage" :preview-src-list="[row.image || defaultImage]" fit="cover" class="product-image" />
          </template>
        </el-table-column>
        <el-table-column prop="name" label="商品名称" min-width="150" />
        <el-table-column prop="category" label="分类" width="100">
          <template #default="{ row }">
            {{ getCategoryText(row.category) }}
          </template>
        </el-table-column>
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">
            ¥{{ row.price }}
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="80" />
        <el-table-column prop="sales" label="销量" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80" fixed="right">
          <template #default="{ row }">
            <ActionDropdown
              :show-edit="true"
              :show-toggle="true"
              :show-delete="true"
              :toggle-status="row.status === 1"
              toggle-on-text="上架"
              toggle-off-text="下架"
              @edit="handleEdit(row)"
              @toggle="handleToggleStatus(row)"
              @delete="handleDelete(row)"
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" class="product-dialog">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入商品名称" style="width: 100%" />
        </el-form-item>
        <el-form-item label="商品分类" prop="category">
          <el-select v-model="form.category" placeholder="请选择分类" style="width: 100%">
            <el-option label="单门冰箱" value="single" />
            <el-option label="双门冰箱" value="double" />
            <el-option label="对开门冰箱" value="side-by-side" />
            <el-option label="多门冰箱" value="multi" />
          </el-select>
        </el-form-item>
        <el-form-item label="商品价格" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="原价">
          <el-input-number v-model="form.originalPrice" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="form.stock" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="商品描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入商品描述" style="width: 100%" />
        </el-form-item>
        <el-form-item label="商品图片">
          <el-upload
            action="#"
            list-type="picture-card"
            :auto-upload="false"
            :limit="5"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getProductList, createProduct, updateProduct, deleteProduct, updateProductStatus } from '@/api/product'
import ActionDropdown from '@/components/ActionDropdown.vue'
import defaultImage from '@/assets/images/fridge.jpg'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('添加商品')
const formRef = ref(null)
const toggleLoadingId = ref(null)
const deleteLoadingId = ref(null)

const filterForm = reactive({
  keyword: '',
  category: '',
  status: ''
})

const pagination = reactive({
  page: 1,
  pageSize: 10
})

const total = ref(0)
const productList = ref([])

const form = reactive({
  id: null,
  name: '',
  category: '',
  price: 0,
  originalPrice: 0,
  stock: 0,
  description: '',
  images: []
})

const rules = {
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存', trigger: 'blur' }]
}

const categoryMap = {
  single: '单门冰箱',
  double: '双门冰箱',
  'side-by-side': '对开门冰箱',
  multi: '多门冰箱'
}

const getCategoryText = (category) => categoryMap[category] || '未知'

const tableRowClassName = ({ row, rowIndex }) => {
  return 'product-row'
}

const handleRowHover = (row, column, cell, event) => {
  if (cell) {
    cell.style.transition = 'all 0.3s ease'
  }
}

const handleRowLeave = (row, column, cell, event) => {
  // 行离开时的处理
}

const fetchProducts = async () => {
  loading.value = true
  try {
    const res = await getProductList({
      page: pagination.page,
      pageSize: pagination.pageSize,
      ...filterForm
    })
    productList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('获取商品列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  fetchProducts()
}

const handleReset = () => {
  filterForm.keyword = ''
  filterForm.category = ''
  filterForm.status = ''
  pagination.page = 1
  fetchProducts()
}

const handleSizeChange = () => {
  pagination.page = 1
  fetchProducts()
}

const handlePageChange = () => {
  fetchProducts()
}

const resetForm = () => {
  form.id = null
  form.name = ''
  form.category = ''
  form.price = 0
  form.originalPrice = 0
  form.stock = 0
  form.description = ''
  form.images = []
}

const handleAdd = () => {
  resetForm()
  dialogTitle.value = '添加商品'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  resetForm()
  dialogTitle.value = '编辑商品'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitLoading.value = true
    if (form.id) {
      await updateProduct(form.id, form)
      ElMessage.success('修改成功')
    } else {
      await createProduct(form)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    fetchProducts()
  } catch (error) {
    console.error('保存失败:', error)
  } finally {
    submitLoading.value = false
  }
}

const handleToggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  const statusText = newStatus === 1 ? '上架' : '下架'
  try {
    await ElMessageBox.confirm(
      `确定要${statusText}商品「${row.name}」吗？`,
      `${statusText}确认`,
      { 
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        customClass: 'confirm-dialog'
      }
    )
    toggleLoadingId.value = row.id
    await updateProductStatus(row.id, newStatus)
    ElMessage.success(`商品「${row.name}」已${statusText}`)
    fetchProducts()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(`${statusText}失败，请重试`)
      console.error('操作失败:', error)
    }
  } finally {
    toggleLoadingId.value = null
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除商品「${row.name}」吗？删除后无法恢复。`,
      '删除确认',
      { 
        type: 'error',
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        customClass: 'confirm-dialog'
      }
    )
    deleteLoadingId.value = row.id
    await deleteProduct(row.id)
    ElMessage.success(`商品「${row.name}」已删除`)
    fetchProducts()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败，请重试')
      console.error('删除失败:', error)
    }
  } finally {
    deleteLoadingId.value = null
  }
}

onMounted(() => {
  fetchProducts()
})
</script>

<style scoped>
.admin-products-page {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  height: 100%;
  overflow-y: auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
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

.product-image {
  width: 60px;
  height: 60px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

:deep(.product-row) {
  transition: all 0.3s ease;
}

:deep(.el-table__row:hover > td) {
  background-color: #ecf5ff !important;
  transform: scale(1.01);
}

:deep(.el-table__body tr.hover-row > td) {
  background-color: #ecf5ff !important;
}

:deep(.confirm-dialog) {
  border-radius: 8px;
}

:deep(.confirm-dialog .el-message-box__header) {
  padding: 16px 20px 10px;
}

:deep(.confirm-dialog .el-message-box__content) {
  padding: 20px;
}

:deep(.confirm-dialog .el-message-box__message) {
  font-size: 14px;
  color: #606266;
}

:deep(.el-dialog) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.product-dialog .el-dialog__header) {
  background: linear-gradient(135deg, #2563eb 0%, #0ea5e9 100%);
  padding: 16px 20px;
  margin-right: 0;
  margin-left: 0;
  display: flex;
  align-items: center;
}

:deep(.product-dialog .el-dialog__title) {
  color: #fff;
  font-weight: 600;
  flex: 1;
}

:deep(.product-dialog .el-dialog__headerbtn) {
  position: relative;
  top: auto;
  right: 0;
  width: 32px;
  height: 32px;
  margin-left: auto;
}

:deep(.product-dialog .el-dialog__headerbtn .el-dialog__close) {
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

:deep(.product-dialog .el-dialog__headerbtn:hover .el-dialog__close) {
  color: #fff;
  background-color: rgba(255, 255, 255, 0.2);
}

:deep(.product-dialog .el-dialog__body) {
  padding: 24px;
}

:deep(.el-dialog__footer) {
  padding: 16px 24px;
  border-top: 1px solid #eee;
}
</style>
