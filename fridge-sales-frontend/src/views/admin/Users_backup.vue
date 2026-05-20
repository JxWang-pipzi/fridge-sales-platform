<template>
  <div class="admin-users-page">
    <div class="page-header">
      <h3>用户管理</h3>
    </div>

    <div class="filter-section">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="用户名">
          <el-input v-model="filterForm.username" placeholder="请输入用户名" clearable style="width: 100%" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="filterForm.phone" placeholder="请输入手机号" clearable style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filterForm.status" placeholder="请选择状态" clearable style="width: 100%">
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-section">
      <el-table v-loading="loading" :data="userList" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="role" label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="row.role === 'admin' ? 'danger' : 'primary'" size="small">
              {{ row.role === 'admin' ? '管理员' : '普通用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="160" />
        <el-table-column label="操作" width="80" fixed="right">
          <template #default="{ row }">
            <ActionDropdown
              :show-view="true"
              :show-toggle="true"
              :show-reset="true"
              :toggle-status="row.status === 1"
              toggle-on-text="启用"
              toggle-off-text="禁用"
              @view="handleViewDetail(row)"
              @toggle="handleToggleStatus(row)"
              @reset="handleResetPassword(row)"
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
      title="用户详情" 
      width="600px"
      destroy-on-close
      :close-on-click-modal="false"
      class="user-detail-dialog"
    >
      <el-descriptions :column="1" border style="width: 100%">
        <el-descriptions-item label="用户ID">{{ currentUser.id }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ currentUser.username }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ currentUser.nickname || '-' }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ currentUser.email }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ currentUser.phone }}</el-descriptions-item>
        <el-descriptions-item label="角色">
          <el-tag :type="currentUser.role === 'admin' ? 'danger' : 'primary'" size="small">
            {{ currentUser.role === 'admin' ? '管理员' : '普通用户' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentUser.status === 1 ? 'success' : 'danger'" size="small">
            {{ currentUser.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ currentUser.createTime }}</el-descriptions-item>
        <el-descriptions-item label="最后登录">{{ currentUser.lastLoginTime || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAdminUserList, updateUserStatus, resetUserPassword } from '@/api/admin'
import ActionDropdown from '@/components/ActionDropdown.vue'

const loading = ref(false)
const detailVisible = ref(false)
const toggleLoadingId = ref(null)
const resetLoadingId = ref(null)

const filterForm = reactive({
  username: '',
  phone: '',
  status: ''
})

const pagination = reactive({
  page: 1,
  pageSize: 10
})

const total = ref(0)
const userList = ref([])
const currentUser = ref({})

const fetchUsers = async () => {
  loading.value = true
  try {
    const res = await getAdminUserList({
      page: pagination.page,
      pageSize: pagination.pageSize,
      ...filterForm
    })
    userList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('获取用户列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  fetchUsers()
}

const handleReset = () => {
  filterForm.username = ''
  filterForm.phone = ''
  filterForm.status = ''
  pagination.page = 1
  fetchUsers()
}

const handleSizeChange = () => {
  pagination.page = 1
  fetchUsers()
}

const handlePageChange = () => {
  fetchUsers()
}

const handleViewDetail = (row) => {
  currentUser.value = row
  detailVisible.value = true
}

const handleToggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  const statusText = newStatus === 1 ? '启用' : '禁用'
  try {
    await ElMessageBox.confirm(
      `确定要${statusText}用户「${row.username}」吗？${newStatus === 0 ? '禁用后该用户将无法登录系统。' : ''}`,
      `${statusText}用户确认`,
      { 
        type: newStatus === 0 ? 'error' : 'warning',
        confirmButtonText: `确定${statusText}`,
        cancelButtonText: '取消',
        customClass: 'confirm-dialog'
      }
    )
    toggleLoadingId.value = row.id
    await updateUserStatus(row.id, newStatus)
    ElMessage.success(`用户「${row.username}」已${statusText}`)
    fetchUsers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(`${statusText}失败，请重试`)
      console.error('操作失败:', error)
    }
  } finally {
    toggleLoadingId.value = null
  }
}

const handleResetPassword = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要重置用户「${row.username}」的密码吗？密码将被重置为默认密码「123456」，用户需要使用新密码重新登录。`,
      '重置密码确认',
      { 
        type: 'warning',
        confirmButtonText: '确定重置',
        cancelButtonText: '取消',
        customClass: 'confirm-dialog',
        dangerouslyUseHTMLString: false
      }
    )
    resetLoadingId.value = row.id
    await resetUserPassword(row.id)
    ElMessage.success(`用户「${row.username}」的密码已重置为123456`)
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('重置密码失败，请重试')
      console.error('重置密码失败:', error)
    }
  } finally {
    resetLoadingId.value = null
  }
}

onMounted(() => {
  fetchUsers()
})
</script>

<style scoped>
.admin-users-page {
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

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

:deep(.el-table__row:hover > td) {
  background-color: #ecf5ff !important;
  transition: background-color 0.3s ease;
}

:deep(.el-dialog) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.user-detail-dialog .el-dialog__header) {
  background: #409eff;
  padding: 16px 20px;
  margin-right: 0;
  margin-left: 0;
  display: flex;
  align-items: center;
}

:deep(.user-detail-dialog .el-dialog__title) {
  color: #fff;
  font-weight: 500;
  flex: 1;
}

:deep(.user-detail-dialog .el-dialog__headerbtn) {
  position: relative;
  top: auto;
  right: 0;
  width: 32px;
  height: 32px;
  margin-left: auto;
}

:deep(.user-detail-dialog .el-dialog__headerbtn .el-dialog__close) {
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

:deep(.user-detail-dialog .el-dialog__headerbtn:hover .el-dialog__close) {
  color: #fff;
  background-color: rgba(255, 255, 255, 0.2);
}

:deep(.user-detail-dialog .el-dialog__body) {
  padding: 20px 24px 24px;
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
  width: 100px;
}
</style>
