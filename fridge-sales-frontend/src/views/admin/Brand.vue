<template>
  <div class="brand-page">
    <div class="page-header">
      <h2 class="page-title">品牌管理</h2>
      <button class="add-btn" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        添加品牌
      </button>
    </div>

    <div class="table-container">
      <el-table :data="brandList" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="name" label="品牌名称" align="center" />
        <el-table-column prop="sort" label="排序" width="80" align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑品牌' : '添加品牌'" width="500px">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="80px">
        <el-form-item label="品牌名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入品牌名称" maxlength="50" />
        </el-form-item>
        <el-form-item label="Logo" prop="logo">
          <el-input v-model="formData.logo" placeholder="请输入Logo URL" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入品牌描述" maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="formData.sort" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">正常</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getBrandList, addBrand, updateBrand, deleteBrand } from '@/api/brand'

const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const brandList = ref([])
const formRef = ref(null)

const formData = reactive({
  id: null,
  name: '',
  logo: '',
  description: '',
  sort: 0,
  status: 1
})

const formRules = {
  name: [
    { required: true, message: '请输入品牌名称', trigger: 'blur' },
    { min: 1, max: 50, message: '品牌名称长度为1-50个字符', trigger: 'blur' }
  ]
}

const fetchBrandList = async () => {
  loading.value = true
  try {
    const res = await getBrandList()
    brandList.value = res.data || []
  } catch (error) {
    console.error('获取品牌列表失败:', error)
    ElMessage.error('获取品牌列表失败')
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  formData.id = null
  formData.name = ''
  formData.logo = ''
  formData.description = ''
  formData.sort = 0
  formData.status = 1
  formRef.value?.resetFields()
}

const handleAdd = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  formData.id = row.id
  formData.name = row.name
  formData.logo = row.logo || ''
  formData.description = row.description || ''
  formData.sort = row.sort || 0
  formData.status = row.status
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
  } catch {
    return
  }

  submitting.value = true
  try {
    if (isEdit.value) {
      await updateBrand(formData)
      ElMessage.success('品牌修改成功')
    } else {
      await addBrand(formData)
      ElMessage.success('品牌添加成功')
    }
    dialogVisible.value = false
    fetchBrandList()
  } catch (error) {
    console.error('保存品牌失败:', error)
    ElMessage.error('保存品牌失败')
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除品牌"${row.name}"吗？`, '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteBrand(row.id)
    ElMessage.success('品牌删除成功')
    fetchBrandList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除品牌失败:', error)
      ElMessage.error('删除品牌失败')
    }
  }
}

onMounted(() => {
  fetchBrandList()
})
</script>

<style scoped>
.brand-page {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  color: #1d1d1f;
  margin: 0;
}

.add-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  background-color: #0071e3;
  color: #ffffff;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.add-btn:hover {
  background-color: #0077ed;
}

.table-container {
  background-color: #ffffff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.brand-logo {
  width: 40px;
  height: 40px;
  object-fit: contain;
  border-radius: 4px;
}

.no-logo {
  color: #86868b;
  font-size: 12px;
}
</style>
