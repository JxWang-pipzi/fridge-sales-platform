<template>
  <div class="admin-page">
    <div class="page-header">
      <h2>商品统计</h2>
    </div>

    <el-row :gutter="20">
      <el-col :span="12">
        <el-card header="销量TOP10">
          <div v-if="topSales.length" class="chart-container">
            <div v-for="(item, index) in topSales" :key="item.id" class="rank-item">
              <span class="rank" :class="{ top: index < 3 }">{{ index + 1 }}</span>
              <span class="name">{{ item.name }}</span>
              <span class="sales">{{ item.sales }} 件</span>
            </div>
          </div>
          <el-empty v-else description="暂无数据" />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card header="库存预警">
          <div v-if="lowStock.length" class="chart-container">
            <div v-for="item in lowStock" :key="item.id" class="stock-item">
              <span class="name">{{ item.name }}</span>
              <el-progress
                :percentage="(item.stock / 100) * 100"
                :status="item.stock < 20 ? 'exception' : 'warning'"
                :stroke-width="10"
              />
              <span class="stock">库存: {{ item.stock }}</span>
            </div>
          </div>
          <el-empty v-else description="暂无库存预警" />
        </el-card>
      </el-col>
    </el-row>

    <el-card header="商品销售分布" style="margin-top: 20px;">
      <div ref="chartRef" style="height: 400px;"></div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import request from '@/utils/request'

const topSales = ref([])
const lowStock = ref([])
const chartRef = ref(null)

const fetchTopSales = async () => {
  try {
    const res = await request({
      url: '/product/list',
      method: 'get',
      params: { pageSize: 10, sortBy: 'sales' }
    })
    topSales.value = res.data?.records || []
  } catch (error) {
    console.error('获取销量TOP10失败:', error)
  }
}

const fetchLowStock = async () => {
  try {
    const res = await request({
      url: '/product/list',
      method: 'get',
      params: { pageSize: 10 }
    })
    const products = res.data?.records || []
    lowStock.value = products.filter(p => p.stock < 50).sort((a, b) => a.stock - b.stock)
  } catch (error) {
    console.error('获取库存预警失败:', error)
  }
}

const initChart = () => {
  if (!chartRef.value || !topSales.value.length) return
  
  const chart = echarts.init(chartRef.value)
  const option = {
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: topSales.value.map(item => item.brand) },
    yAxis: { type: 'value', name: '销量' },
    series: [{
      name: '销量',
      type: 'bar',
      data: topSales.value.map(item => item.sales),
      itemStyle: { color: '#409eff' }
    }]
  }
  chart.setOption(option)
}

onMounted(async () => {
  await fetchTopSales()
  await fetchLowStock()
  initChart()
})
</script>

<style scoped>
.admin-page {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.page-header {
  margin-bottom: 20px;
  padding: 0 4px;
}

.page-header h2 {
  margin: 0;
  font-size: 18px;
}

.el-row {
  flex: 1;
  margin-bottom: 20px;
}

.el-col {
  height: 100%;
}

.el-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

:deep(.el-card__body) {
  flex: 1;
  overflow-y: auto;
}

.chart-container{
  max-height: 300px;
  overflow-y: auto;
}

.rank-item {
  display: flex;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.rank {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0f0f0;
  border-radius: 4px;
  margin-right: 12px;
  font-size: 12px;
}

.rank.top {
  background: #409eff;
  color: #fff;
}

.name {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.sales {
  color: #409eff;
  font-weight: 500;
}

.stock-item {
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.stock-item .name {
  display: block;
  margin-bottom: 8px;
}

.stock-item .stock {
  display: block;
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
}
</style>
