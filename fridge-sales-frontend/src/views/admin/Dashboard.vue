<template>
  <div class="dashboard">
    <div class="stats-cards">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-content">
              <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
                <el-icon size="28"><User /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.totalUsers }}</div>
                <div class="stat-label">用户总数</div>
              </div>
            </div>
            <div class="stat-footer">
              <span class="today-info">今日新增: {{ stats.todayUsers }}</span>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-content">
              <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
                <el-icon size="28"><Goods /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.totalProducts }}</div>
                <div class="stat-label">商品总数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-content">
              <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
                <el-icon size="28"><List /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.totalOrders }}</div>
                <div class="stat-label">订单总数</div>
              </div>
            </div>
            <div class="stat-footer">
              <span class="today-info">今日订单: {{ stats.todayOrders }}</span>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-content">
              <div class="stat-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);">
                <el-icon size="28"><Money /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">¥{{ formatMoney(stats.totalSales) }}</div>
                <div class="stat-label">销售总额</div>
              </div>
            </div>
            <div class="stat-footer">
              <span class="today-info">今日销售: ¥{{ formatMoney(stats.todaySales) }}</span>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="16">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>销售趋势</span>
              <el-radio-group v-model="trendType" size="small" @change="fetchTrendData">
                <el-radio-button value="day">按天</el-radio-button>
                <el-radio-button value="month">按月</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="chartRef" style="height: 350px;"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <span>分类销售占比</span>
          </template>
          <div ref="pieChartRef" style="height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { User, Goods, List, Money } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getOverview, getSalesTrend, getCategorySales } from '@/api/admin'

const chartRef = ref(null)
const pieChartRef = ref(null)
let lineChart = null
let pieChart = null
let refreshTimer = null

const trendType = ref('day')

const stats = ref({
  totalUsers: 0,
  totalProducts: 0,
  totalOrders: 0,
  totalSales: 0,
  todayUsers: 0,
  todayOrders: 0,
  todaySales: 0
})

const categoryData = ref([])

const formatMoney = (value) => {
  if (!value) return '0.00'
  const num = Number(value)
  if (num >= 10000) {
    return (num / 10000).toFixed(2) + '万'
  }
  return num.toFixed(2)
}

const fetchOverview = async () => {
  try {
    const res = await getOverview()
    if (res.code === 200 && res.data) {
      stats.value = res.data
    }
  } catch (error) {
    console.error('获取概览数据失败:', error)
  }
}

const fetchTrendData = async () => {
  try {
    const res = await getSalesTrend(trendType.value)
    if (res.code === 200 && res.data) {
      updateLineChart(res.data)
    }
  } catch (error) {
    console.error('获取销售趋势失败:', error)
  }
}

const fetchCategoryData = async () => {
  try {
    const res = await getCategorySales()
    if (res.code === 200 && res.data) {
      categoryData.value = res.data
      updatePieChart(res.data)
    }
  } catch (error) {
    console.error('获取分类销售失败:', error)
  }
}

const fetchAllData = async () => {
  await Promise.all([
    fetchOverview(),
    fetchTrendData(),
    fetchCategoryData()
  ])
}

const initLineChart = () => {
  if (chartRef.value) {
    lineChart = echarts.init(chartRef.value)
    const option = {
      tooltip: {
        trigger: 'axis',
        axisPointer: { type: 'shadow' }
      },
      legend: {
        data: ['销售额', '订单数']
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: []
      },
      yAxis: [
        {
          type: 'value',
          name: '销售额(元)',
          position: 'left'
        },
        {
          type: 'value',
          name: '订单数',
          position: 'right'
        }
      ],
      series: [
        {
          name: '销售额',
          type: 'bar',
          data: [],
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#83bff6' },
              { offset: 0.5, color: '#188df0' },
              { offset: 1, color: '#188df0' }
            ])
          }
        },
        {
          name: '订单数',
          type: 'line',
          yAxisIndex: 1,
          data: [],
          smooth: true,
          itemStyle: { color: '#67C23A' }
        }
      ]
    }
    lineChart.setOption(option)
  }
}

const updateLineChart = (data) => {
  if (!lineChart) return
  const dates = data.map(item => item.date)
  const sales = data.map(item => item.sales || 0)
  const orders = data.map(item => item.orders || 0)

  lineChart.setOption({
    xAxis: { data: dates },
    series: [
      { data: sales },
      { data: orders }
    ]
  })
}

const initPieChart = () => {
  if (pieChartRef.value) {
    pieChart = echarts.init(pieChartRef.value)
    const option = {
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        left: 'left'
      },
      series: [
        {
          name: '销售占比',
          type: 'pie',
          radius: ['40%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 10,
            borderColor: '#fff',
            borderWidth: 2
          },
          label: {
            show: false,
            position: 'center'
          },
          emphasis: {
            label: {
              show: true,
              fontSize: 16,
              fontWeight: 'bold'
            }
          },
          labelLine: {
            show: false
          },
          data: []
        }
      ]
    }
    pieChart.setOption(option)
  }
}

const updatePieChart = (data) => {
  if (!pieChart) return
  const pieData = data.map(item => ({
    name: item.categoryName || item.name || '其他',
    value: item.salesAmount || item.value || 0
  }))
  pieChart.setOption({
    series: [{ data: pieData }]
  })
}

const handleResize = () => {
  lineChart?.resize()
  pieChart?.resize()
}

onMounted(async () => {
  initLineChart()
  initPieChart()
  await fetchAllData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  lineChart?.dispose()
  pieChart?.dispose()
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.stat-card {
  margin-bottom: 20px;
}

.stat-content {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.stat-info {
  margin-left: 16px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}

.stat-footer {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #EBEEF5;
}

.today-info {
  font-size: 12px;
  color: #909399;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>