<template>
  <div class="user-list">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <el-tag :type="userCount > 0 ? 'primary' : 'info'">共 {{ userCount }} 个用户</el-tag>
        </div>
      </template>
      <pro-table
        ref="tableRef"
        title="用户管理"
        :columns="columns"
        :request="getAdminUserList"
        :selection="true"
      >
        <template #status="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </template>
        <template #role="{ row }">
          <el-tag :type="row.role === 'admin' ? 'danger' : 'primary'" size="small">
            {{ row.role === 'admin' ? '管理员' : '普通用户' }}
          </el-tag>
        </template>
        <template #actions="{ row }">
          <ActionDropdown
            :show-view="true"
            :show-toggle="true"
            :show-reset="true"
            :toggle-status="row.status === 1"
            toggle-on-text="启用"
            toggle-off-text="禁用"
            @view="handleEdit(row)"
            @toggle="handleStatusChange(row)"
            @reset="handleResetPassword(row)"
          />
        </template>
      </pro-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import ProTable from '@/components/pro/ProTable.vue'
import ActionDropdown from '@/components/ActionDropdown.vue'
import { getAdminUserList, updateUserStatus, resetUserPassword } from '@/api/admin'

const router = useRouter()
const tableRef = ref(null)
const userCount = ref(0)

const columns = [
  { prop: 'id', label: 'ID', width: 80 },
  { prop: 'username', label: '用户名', search: true },
  { prop: 'email', label: '邮箱', search: true },
  { prop: 'phone', label: '手机号', search: true },
  { 
    prop: 'role', 
    label: '角色', 
    search: true, 
    valueType: 'select',
    options: [
      { label: '管理员', value: 'admin' },
      { label: '普通用户', value: 'user' }
    ],
    slot: 'role'
  },
  { 
    prop: 'status', 
    label: '状态', 
    slot: 'status',
    search: true,
    valueType: 'select',
    options: [
      { label: '正常', value: 1 },
      { label: '禁用', value: 0 }
    ]
  },
  { prop: 'createTime', label: '注册时间', width: 180 },
  { prop: 'actions', label: '操作', slot: 'actions', width: 80, fixed: 'right' }
]

const handleEdit = (row) => {
  router.push(`/admin/user-detail/${row.id}`)
}

const handleStatusChange = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  const actionText = newStatus === 1 ? '启用' : '禁用'
  try {
    await ElMessageBox.confirm(`确定要${actionText}该用户吗？`, '提示', { type: 'warning' })
    await updateUserStatus(row.id, newStatus)
    ElMessage.success(`用户已${actionText}`)
    tableRef.value.fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
      console.error(error)
    }
  }
}

const handleResetPassword = async (row) => {
  try {
    await ElMessageBox.confirm('确定要重置该用户的密码吗？', '提示', { type: 'warning' })
    await resetUserPassword(row.id)
    ElMessage.success('密码已重置为默认密码')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('重置密码失败')
      console.error(error)
    }
  }
}

const fetchUserCount = async () => {
  try {
    const res = await getAdminUserList({ current: 1, size: 1 })
    if (res.code === 200 && res.data) {
      userCount.value = res.data.total || 0
    }
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  fetchUserCount()
})
</script>

<style scoped>
.user-list {
  padding: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>