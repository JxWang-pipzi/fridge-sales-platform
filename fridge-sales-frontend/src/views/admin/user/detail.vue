<template>
  <div class="user-detail">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户详情</span>
          <el-button @click="$router.back()">返回</el-button>
        </div>
      </template>
      
      <pro-form
        v-if="user"
        :schema="schema"
        :model-value="user"
        @submit="handleSubmit"
        @reset="fetchUser"
      >
        <template #status="{ model }">
           <el-switch
             v-model="model.status"
             :active-value="1"
             :inactive-value="0"
             active-text="正常"
             inactive-text="禁用"
           />
        </template>
      </pro-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import ProForm from '@/components/pro/ProForm.vue'
import { getUserDetail, updateAdminUser } from '@/api/admin'

const route = useRoute()
const router = useRouter()
const user = ref(null)

const schema = [
  { prop: 'username', label: '用户名', required: true, props: { disabled: true } },
  { prop: 'email', label: '邮箱', required: true },
  { prop: 'phone', label: '手机号' },
  { 
    prop: 'role', 
    label: '角色', 
    type: 'select', 
    options: [
      { label: '管理员', value: 'admin' },
      { label: '普通用户', value: 'user' }
    ]
  },
  { prop: 'status', label: '状态', slot: 'status' }
]

const fetchUser = async () => {
  const id = route.params.id
  if (!id) return
  try {
    const res = await getUserDetail(id)
    if (res.code === 200) {
      user.value = res.data
    }
  } catch (error) {
    console.error(error)
  }
}

const handleSubmit = async (formData) => {
  try {
    await updateAdminUser(user.value.id, formData)
    ElMessage.success('更新成功')
    router.back()
  } catch (error) {
    console.error(error)
  }
}

onMounted(fetchUser)
</script>

<style scoped>
.user-detail {
  padding: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
