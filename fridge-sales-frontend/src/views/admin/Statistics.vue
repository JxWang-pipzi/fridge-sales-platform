<template>
  <div class="statistics-page">
    <div class="page-header">
      <div class="header-left">
        <h3>销售统计</h3>
        <div class="current-time">
          <el-icon><Calendar /></el-icon>
          <span>{{ currentDate }} {{ currentTime }}</span>
        </div>
      </div>
      <div class="header-right">
        <el-tag type="success" effect="plain">
          <el-icon class="pulse-icon"><Connection /></el-icon>
          实时更新中
        </el-tag>
        <el-button type="primary" :icon="Refresh" :loading="refreshLoading" @click="refreshAllData">刷新数据</el-button>
      </div>
    </div>

    <el-row :gutter="20" class="summary-row">
      <el-col :span="6">
        <div class="summary-card">
          <div class="summary-icon" style="background: linear-gradient(135deg, #409eff, #66b1ff);">
            <el-icon :size="28"><Money /></el-icon>
          </div>
          <div class="summary-info">
            <p class="summary-value">¥{{ formatPrice(overview.todaySales) }}</p>
            <p class="summary-label">今日销售额</p>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="summary-card">
          <div class="summary-icon" style="background: linear-gradient(135deg, #67c23a, #85ce61);">
            <el-icon :size="28"><List /></el-icon>
          </div>
          <div class="summary-info">
            <p class="summary-value">{{ overview.todayOrders }}</p>
            <p class="summary-label">今日订单数</p>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="summary-card">
          <div class="summary-icon" style="background-color: #e6a23c;">
            <el-icon :size="28"><Money /></el-icon>
          </div>
          <div class="summary-info">
            <p class="summary-value">¥{{ formatPrice(overview.totalSales) }}</p>
            <p class="summary-label">累计销售额</p>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="summary-card">
          <div class="summary-icon" style="background-color: #67c23a;">
            <el-icon :size="28"><List /></el-icon>
          </div>
          <div class="summary-info">
            <p class="summary-value">{{ overview.totalOrders }}</p>
            <p class="summary-label">累计订单数</p>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="filter-row">
      <el-col :span="4">
        <el-select v-model="trendType" @change="fetchSalesTrend">
          <el-option label="按天" value="day" />
          <el-option label="按周" value="week" />
          <el-option label="按月" value="month" />
        </el-select>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <div class="chart-card">
          <div class="chart-header">
            <h3>销售趋势</h3>
            <span class="update-time">更新于: {{ lastUpdateTime }}</span>
          </div>
          <div ref="salesTrendChartRef" class="chart-container"></div>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="chart-card">
          <div class="chart-header">
            <h3>订单趋势</h3>
            <span class="update-time">更新于: {{ lastUpdateTime }}</span>
          </div>
          <div ref="orderTrendChartRef" class="chart-container"></div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <div class="chart-card">
          <div class="chart-header">
            <h3>商品分类销售占比</h3>
            <span class="update-time">更新于: {{ lastUpdateTime }}</span>
          </div>
          <div ref="categoryPieChartRef" class="chart-container"></div>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="chart-card">
          <div class="chart-header">
            <h3>热销商品排行</h3>
            <span class="update-time">更新于: {{ lastUpdateTime }}</span>
          </div>
          <div ref="hotProductChartRef" class="chart-container"></div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import * as echarts from 'echarts'
import { Calendar, Connection, Refresh, Money, List } from '@element-plus/icons-vue'
import { getStatisticsOverview, getSalesTrend, getCategorySales, getHotProducts } from '@/api/admin'
import { ElMessage } from 'element-plus'

const trendType = ref('day')
const lastUpdateTime = ref('')
const currentDate = ref('')
const currentTime = ref('')
const refreshLoading = ref(false)

const overview = ref({
  todaySales: 0,
  todayOrders: 0,
  totalSales: 0,
  totalOrders: 0
})

const salesTrendChartRef = ref(null)
const orderTrendChartRef = ref(null)
const categoryPieChartRef = ref(null)
const hotProductChartRef = ref(null)

let salesTrendChart = null
let orderTrendChart = null
let categoryPieChart = null
let hotProductChart = null
let timeInterval = null
let refreshInterval = null

const formatPrice = (price) => {
  if (!price) return '0.00'
  return Number(price).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

const updateTime = () => {
  const now = new Date()
  currentDate.value = now.toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' })
  currentTime.value = now.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit', second: '2-digit' })
  lastUpdateTime.value = now.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit', second: '2-digit' })
}

const fetchOverview = async () => {
  try {
    const res = await getStatisticsOverview()
    if (res.data) {
      overview.value = {
        todaySales: res.data.todaySales || 0,
        todayOrders: res.data.todayOrders || 0,
        totalSales: res.data.totalSales || 0,
        totalOrders: res.data.totalOrders || 0
      }
    }
  } catch (error) {
    console.error('获取概览数据失败:', error)
  }
}

const fetchSalesTrend = async () => {
  try {
    const res = await getSalesTrend(trendType.value)
    initSalesTrendChart(res.data || [])
    initOrderTrendChart(res.data || [])
  } catch (error) {
    console.error('获取销售趋势失败:', error)
    initSalesTrendChart([])
    initOrderTrendChart([])
  }
}

const fetchCategorySales = async () => {
  try {
    const res = await getCategorySales()
    initCategoryPieChart(res.data || [])
  } catch (error) {
    console.error('获取分类销售失败:', error)
    initCategoryPieChart([])
  }
}

const fetchHotProducts = async () => {
  try {
    const res = await getHotProducts(10)
    initHotProductChart(res.data || [])
  } catch (error) {
    console.error('获取热销商品失败:', error)
    initHotProductChart([])
  }
}

const initSalesTrendChart = (data) => {
  if (!salesTrendChartRef.value) return
  if (salesTrendChart) salesTrendChart.dispose()
  salesTrendChart = echarts.init(salesTrendChartRef.value)
  
  const last10Days = data.slice(-10)
  const dates = last10Days.map(item => item.date || item.label || '')
  const values = last10Days.map(item => item.sales || item.value || 0)
  
  const option = {
    tooltip: { 
      trigger: 'axis', 
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#eee',
      borderWidth: 1,
      padding: [12, 16],
      textStyle: {
        color: '#333'
      },
      formatter: (params) => {
        const item = params[0]
        return `
          <div style="font-weight: 500; margin-bottom: 8px; color: #333;">${item.axisValue}</div>
          <div style="display: flex; align-items: center; gap: 8px;">
            <span style="display: inline-block; width: 10px; height: 10px; background: #409eff; border-radius: 50%;"></span>
            <span>销售额：</span>
            <span style="font-weight: 600; color: #409eff;">¥${Number(item.value).toLocaleString()}</span>
          </div>
        `
      }
    },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: dates, axisLabel: { rotate: 45 } },
    yAxis: { type: 'value', name: '销售额(元)', axisLabel: { formatter: '¥{value}' } },
    series: [{
      name: '销售额',
      type: 'line',
      smooth: true,
      data: values,
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(64, 158, 255, 0.5)' },
          { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
        ])
      },
      lineStyle: { color: '#409eff', width: 2 },
      itemStyle: { color: '#409eff' },
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowColor: 'rgba(64, 158, 255, 0.5)'
        }
      }
    }]
  }
  salesTrendChart.setOption(option)
}

const initOrderTrendChart = (data) => {
  if (!orderTrendChartRef.value) return
  if (orderTrendChart) orderTrendChart.dispose()
  orderTrendChart = echarts.init(orderTrendChartRef.value)
  
  const last10Days = data.slice(-10)
  const dates = last10Days.map(item => item.date || item.label || '')
  const values = last10Days.map(item => item.orders || item.count || 0)
  
  const option = {
    tooltip: { 
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#eee',
      borderWidth: 1,
      padding: [12, 16],
      textStyle: {
        color: '#333'
      },
      formatter: (params) => {
        const item = params[0]
        return `
          <div style="font-weight: 500; margin-bottom: 8px; color: #333;">${item.axisValue}</div>
          <div style="display: flex; align-items: center; gap: 8px;">
            <span style="display: inline-block; width: 10px; height: 10px; background: #67c23a; border-radius: 50%;"></span>
            <span>订单数：</span>
            <span style="font-weight: 600; color: #67c23a;">${item.value} 单</span>
          </div>
        `
      }
    },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: dates, axisLabel: { rotate: 45 } },
    yAxis: { type: 'value', name: '订单数' },
    series: [{
      name: '订单数',
      type: 'bar',
      data: values,
      itemStyle: { 
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#67c23a' },
          { offset: 1, color: '#85ce61' }
        ]),
        borderRadius: [4, 4, 0, 0]
      },
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowColor: 'rgba(103, 194, 58, 0.5)'
        }
      }
    }]
  }
  orderTrendChart.setOption(option)
}

const initCategoryPieChart = (data) => {
  if (!categoryPieChartRef.value) return
  if (categoryPieChart) categoryPieChart.dispose()
  categoryPieChart = echarts.init(categoryPieChartRef.value)
  
  const pieData = data.map(item => ({
    value: item.sales || item.value || 0,
    name: item.categoryName || item.name || ''
  }))
  
  const option = {
    tooltip: { 
      trigger: 'item',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#eee',
      borderWidth: 1,
      padding: [12, 16],
      textStyle: {
        color: '#333'
      },
      formatter: (params) => {
        return `
          <div style="display: flex; align-items: center; gap: 8px; margin-bottom: 4px;">
            <span style="display: inline-block; width: 10px; height: 10px; background: ${params.color}; border-radius: 50%;"></span>
            <span style="font-weight: 500;">${params.name}</span>
          </div>
          <div style="padding-left: 18px; color: #666;">
            销售额：<span style="font-weight: 600; color: #333;">¥${Number(params.value).toLocaleString()}</span>
          </div>
          <div style="padding-left: 18px; color: #666;">
            占比：<span style="font-weight: 600; color: #409eff;">${params.percent}%</span>
          </div>
        `
      }
    },
    legend: { orient: 'vertical', left: 'left', top: 'center' },
    series: [{
      name: '销售占比',
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['60%', '50%'],
      data: pieData,
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.3)',
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: true,
          fontWeight: 'bold'
        }
      },
      label: {
        show: pieData.length > 0,
        formatter: '{b}\n{d}%'
      },
      labelLine: {
        show: pieData.length > 0
      }
    }],
    graphic: pieData.length === 0 ? [{
      type: 'text',
      left: 'center',
      top: 'center',
      style: {
        text: '暂无销售数据',
        fontSize: 14,
        fill: '#999'
      }
    }] : []
  }
  categoryPieChart.setOption(option)
}

const initHotProductChart = (data) => {
  if (!hotProductChartRef.value) return
  if (hotProductChart) hotProductChart.dispose()
  hotProductChart = echarts.init(hotProductChartRef.value)
  
  const names = data.map(item => item.productName || item.name || '').reverse()
  const values = data.map(item => item.sales || item.value || 0).reverse()
  
  const option = {
    tooltip: { 
      trigger: 'axis', 
      axisPointer: { type: 'shadow' },
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#eee',
      borderWidth: 1,
      padding: [12, 16],
      textStyle: {
        color: '#333'
      },
      formatter: (params) => {
        const item = params[0]
        return `
          <div style="font-weight: 500; margin-bottom: 8px; color: #333;">${item.name}</div>
          <div style="display: flex; align-items: center; gap: 8px;">
            <span style="display: inline-block; width: 10px; height: 10px; background: linear-gradient(135deg, #409eff, #67c23a); border-radius: 50%;"></span>
            <span>销量：</span>
            <span style="font-weight: 600; color: #67c23a;">${item.value} 件</span>
          </div>
        `
      }
    },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'value', name: '销量' },
    yAxis: { type: 'category', data: names },
    series: [{
      name: '销量',
      type: 'bar',
      data: values,
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
          { offset: 0, color: '#409eff' },
          { offset: 1, color: '#67c23a' }
        ]),
        borderRadius: [0, 4, 4, 0]
      },
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowColor: 'rgba(64, 158, 255, 0.5)'
        }
      }
    }],
    graphic: data.length === 0 ? [{
      type: 'text',
      left: 'center',
      top: 'center',
      style: {
        text: '暂无热销商品数据',
        fontSize: 14,
        fill: '#999'
      }
    }] : []
  }
  hotProductChart.setOption(option)
}

const handleDateChange = () => {
  fetchSalesTrend()
}

const refreshAllData = async () => {
  refreshLoading.value = true
  try {
    updateTime()
    await Promise.all([
      fetchOverview(),
      fetchSalesTrend(),
      fetchCategorySales(),
      fetchHotProducts()
    ])
    ElMessage.success('数据已刷新')
  } catch (error) {
    ElMessage.error('数据刷新失败，请重试')
    console.error('刷新数据失败:', error)
  } finally {
    refreshLoading.value = false
  }
}

const handleResize = () => {
  salesTrendChart?.resize()
  orderTrendChart?.resize()
  categoryPieChart?.resize()
  hotProductChart?.resize()
}

onMounted(() => {
  updateTime()
  refreshAllData()
  
  timeInterval = setInterval(updateTime, 1000)
  refreshInterval = setInterval(refreshAllData, 30000)
  
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  if (timeInterval) clearInterval(timeInterval)
  if (refreshInterval) clearInterval(refreshInterval)
  window.removeEventListener('resize', handleResize)
  salesTrendChart?.dispose()
  orderTrendChart?.dispose()
  categoryPieChart?.dispose()
  hotProductChart?.dispose()
})
</script>

<style scoped>
.statistics-page {
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

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.header-left h3 {
  font-size: 20px;
  color: #333;
  margin: 0;
}

.current-time {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
  font-size: 14px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.pulse-icon {
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.summary-row {
  margin-bottom: 20px;
}

.summary-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.summary-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  background-color: #409eff;
}

.summary-value {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin: 0;
}

.summary-label {
  font-size: 14px;
  color: #999;
  margin: 4px 0 0;
}

.filter-row {
  margin-bottom: 20px;
}

.chart-row {
  margin-bottom: 20px;
}

.chart-card {
  background-color: #fff;
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 20px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.chart-header h3 {
  font-size: 16px;
  color: #333;
  margin: 0;
}

.update-time {
  font-size: 12px;
  color: #999;
}

.chart-container {
  height: 300px;
}
</style>
