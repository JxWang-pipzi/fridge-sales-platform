<template>
  <div class="permission-container">
    <el-row :gutter="20">
      <el-col :span="10">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>角色列表</span>
              <el-tag :type="roleCount > 0 ? 'primary' : 'info'">共 {{ roleCount }} 个角色</el-tag>
            </div>
          </template>
          <el-table :data="roleList" v-loading="loadingRoles" stripe>
            <el-table-column prop="id" label="ID" width="60" />
            <el-table-column prop="roleName" label="角色名称">
              <template #default="{ row }">
                {{ row.roleName || '-' }}
              </template>
            </el-table-column>
            <el-table-column prop="roleKey" label="角色标识">
              <template #default="{ row }">
                <el-tag type="info" size="small">{{ row.roleKey || '-' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
                  {{ row.status === 1 ? '正常' : '禁用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="80">
              <template #default="{ row }">
                <ActionDropdown
                  :show-edit="true"
                  @edit="handleRoleSelect(row)"
                />
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      
      <el-col :span="14">
        <el-card v-if="currentRole" shadow="hover">
           <template #header>
             <div class="card-header">
               <span>配置权限: <strong>{{ currentRole.roleName }}</strong></span>
               <el-button type="primary" :loading="saving" @click="handleSavePermissions">保存配置</el-button>
             </div>
           </template>
           <div class="permission-tree-container" v-loading="loadingPerms">
             <el-tree
               ref="permTreeRef"
               :data="permissionTree"
               show-checkbox
               node-key="id"
               :props="{ label: 'name', children: 'children' }"
               default-expand-all
               :default-checked-keys="checkedPermissions"
             />
           </div>
        </el-card>
        <el-empty v-else description="请从左侧选择一个角色进行权限配置" />
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import ActionDropdown from '@/components/ActionDropdown.vue'
import { getRoles, getPermissions, getRolePermissions, updateRolePermissions } from '@/api/permission'

const permTreeRef = ref(null)
const currentRole = ref(null)
const permissionTree = ref([])
const checkedPermissions = ref([])
const loadingPerms = ref(false)
const saving = ref(false)
const loadingRoles = ref(false)
const roleList = ref([])
const roleCount = ref(0)

const fetchRoles = async () => {
  loadingRoles.value = true
  try {
    const res = await getRoles()
    if (res.code === 200) {
      roleList.value = res.data || []
      roleCount.value = roleList.value.length
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('获取角色列表失败')
  } finally {
    loadingRoles.value = false
  }
}

const fetchAllPermissions = async () => {
  try {
    const res = await getPermissions()
    if (res.code === 200) {
      permissionTree.value = res.data
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('获取权限列表失败')
  }
}

const handleRoleSelect = async (row) => {
  currentRole.value = row
  loadingPerms.value = true
  try {
    const res = await getRolePermissions(row.id)
    if (res.code === 200) {
      checkedPermissions.value = res.data
      if (permTreeRef.value) {
        permTreeRef.value.setCheckedKeys(res.data)
      }
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('获取角色权限失败')
  } finally {
    loadingPerms.value = false
  }
}

const handleSavePermissions = async () => {
  if (!currentRole.value) return
  saving.value = true
  try {
    const checkedKeys = permTreeRef.value.getCheckedKeys()
    const halfCheckedKeys = permTreeRef.value.getHalfCheckedKeys()
    const allKeys = [...checkedKeys, ...halfCheckedKeys]
    
    await updateRolePermissions(currentRole.value.id, { permissionIds: allKeys })
    ElMessage.success('权限配置已保存')
  } catch (error) {
    console.error(error)
    ElMessage.error('保存权限配置失败')
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  fetchRoles()
  fetchAllPermissions()
})
</script>

<style scoped>
.permission-container {
  padding: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.permission-tree-container {
  min-height: 300px;
}
</style>