<template>
  <div class="metrics-chart">
    <div v-if="title" class="chart-title">{{ title }}</div>
    <el-empty v-if="!data.length" description="暂无数据" />
    <div
      v-else
      ref="chartRef"
      class="chart-container"
      :style="{ height: height + 'px' }"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted, onUnmounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import type { MetricDataPoint } from '@/types/instance'

interface Props {
  title?: string
  data: MetricDataPoint[]
  unit?: string
  color?: string
  yAxisMax?: number
  yAxisMin?: number
  height?: number
  loading?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  title: '',
  unit: '%',
  color: '#409EFF',
  yAxisMax: 100,
  yAxisMin: 0,
  height: 200,
  loading: false,
})

const chartRef = ref<HTMLDivElement>()
let chartInstance: echarts.ECharts | null = null
let resizeObserver: ResizeObserver | null = null

const formatTime = (timestamp: number): string => {
  const date = new Date(timestamp)
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${hours}:${minutes}`
}

const initChart = () => {
  if (!chartRef.value || !props.data.length) return

  if (chartInstance) {
    chartInstance.dispose()
  }

  chartInstance = echarts.init(chartRef.value)

  const xData = props.data.map((item) => formatTime(item.timestamp))
  const yData = props.data.map((item) => item.value)

  const option: echarts.EChartsOption = {
    grid: {
      top: 30,
      left: 16,
      right: 16,
      bottom: 16,
      containLabel: true,
    },
    tooltip: {
      trigger: 'axis',
      formatter: (params: any) => {
        const param = Array.isArray(params) ? params[0] : params
        return `${param.name}<br/>${param.value} ${props.unit}`
      },
    },
    xAxis: {
      type: 'category',
      data: xData,
      boundaryGap: false,
      axisLine: {
        lineStyle: {
          color: '#DCDFE6',
        },
      },
      axisTick: {
        show: false,
      },
      axisLabel: {
        color: '#909399',
        fontSize: 12,
      },
    },
    yAxis: {
      type: 'value',
      min: props.yAxisMin,
      max: props.yAxisMax,
      axisLine: {
        show: false,
      },
      axisTick: {
        show: false,
      },
      splitLine: {
        lineStyle: {
          color: '#EBEEF5',
          type: 'dashed',
        },
      },
      axisLabel: {
        color: '#909399',
        fontSize: 12,
        formatter: `{value} ${props.unit}`,
      },
    },
    series: [
      {
        type: 'line',
        data: yData,
        smooth: true,
        symbol: 'none',
        lineStyle: {
          color: props.color,
          width: 2,
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: props.color + '40' },
            { offset: 1, color: props.color + '05' },
          ]),
        },
      },
    ],
  }

  chartInstance.setOption(option)

  if (props.loading) {
    chartInstance.showLoading({
      text: '加载中...',
      color: props.color,
      textColor: '#303133',
      maskColor: 'rgba(255, 255, 255, 0.8)',
    })
  } else {
    chartInstance.hideLoading()
  }
}

const handleResize = () => {
  chartInstance?.resize()
}

watch(
  () => props.data,
  () => {
    nextTick(() => {
      initChart()
    })
  },
  { deep: true }
)

watch(
  () => props.loading,
  (val) => {
    if (!chartInstance) return
    if (val) {
      chartInstance.showLoading({
        text: '加载中...',
        color: props.color,
        textColor: '#303133',
        maskColor: 'rgba(255, 255, 255, 0.8)',
      })
    } else {
      chartInstance.hideLoading()
    }
  }
)

onMounted(() => {
  nextTick(() => {
    initChart()

    if (chartRef.value) {
      resizeObserver = new ResizeObserver(() => {
        handleResize()
      })
      resizeObserver.observe(chartRef.value)
    }
  })
})

onUnmounted(() => {
  resizeObserver?.disconnect()
  if (chartInstance) {
    chartInstance.dispose()
    chartInstance = null
  }
})
</script>

<style lang="scss" scoped>
.metrics-chart {
  background: #ffffff;
  border-radius: 8px;
  border: 1px solid #EBEEF5;
  padding: 16px;

  .chart-title {
    font-size: 14px;
    font-weight: 500;
    color: #303133;
    margin-bottom: 12px;
  }

  .chart-container {
    width: 100%;
  }
}
</style>
