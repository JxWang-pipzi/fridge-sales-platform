<template>
  <div class="admin-page">
    <div class="page-header">
      <h2>评价管理</h2>
    </div>

    <el-table :data="reviewList" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户" width="120">
        <template #default="{ row }">
          {{ row.username || '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="productName" label="商品" min-width="200">
        <template #default="{ row }">
          {{ row.productName || '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="rating" label="评分" width="180">
        <template #default="{ row }">
          <div class="rating-cell">
            <el-rate v-model="row.rating" disabled />
            <span class="rating-text">{{ row.rating }}分</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="content" label="内容" min-width="200" show-overflow-tooltip />
      <el-table-column prop="createTime" label="时间" width="180" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">
            {{ row.status === 1 ? '显示' : '隐藏' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="80">
        <template #default="{ row }">
          <ActionDropdown
            :show-toggle="true"
            :show-delete="true"
            :toggle-status="row.status === 1"
            toggle-on-text="显示"
            toggle-off-text="隐藏"
            @toggle="handleToggleStatus(row)"
            @delete="handleDelete(row)"
          />
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="pagination.page"
      v-model:page-size="pagination.pageSize"
      :total="pagination.total"
      :page-sizes="[10, 20, 50]"
      layout="total, sizes, prev, pager, next"
      @size-change="fetchReviews"
      @current-change="fetchReviews"
      style="margin-top: 20px; justify-content: flex-end;"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAdminReviewList, updateReviewStatus, deleteReviewByAdmin } from '@/api/review'
import ActionDropdown from '@/components/ActionDropdown.vue'

const loading = ref(false)
const reviewList = ref([])
const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const fetchReviews = async () => {
  loading.value = true
  try {
    const res = await getAdminReviewList(pagination.page, pagination.pageSize)
    reviewList.value = res.data?.records || []
    pagination.total = res.data?.total || 0
  } catch (error) {
    console.error('获取评价列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleToggleStatus = async (row) => {
  try {
    await updateReviewStatus(row.id, row.status === 1 ? 0 : 1)
    ElMessage.success('操作成功')
    fetchReviews()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该评价吗？', '提示', { type: 'warning' })
    await deleteReviewByAdmin(row.id)
    ElMessage.success('删除成功')
    fetchReviews()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  fetchReviews()
})
</script>

<style scoped>
.admin-page {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  height: 100%;
  overflow-y: auto;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 18px;
}

.rating-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.rating-text {
  font-size: 14px;
  color: #ff9900;
  font-weight: 500;
}
</style>