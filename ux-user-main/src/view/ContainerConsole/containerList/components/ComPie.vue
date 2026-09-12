<template>
  <div ref="chartDom" class="echarts"></div>
</template>

<script setup lang="ts" name="ComPie">
import * as echarts from 'echarts'
type EChartsOption = echarts.EChartsOption

const props = withDefaults(defineProps<{ pieData: any[]; color: any[] }>(), {
  color: () => ['#3BA46F', '#FF4151', '#FBA201'],
})

const chartDom = ref()
const myChart = ref()
const option = ref<EChartsOption | any>({ series: [] })
onMounted(() => {
  myChart.value = echarts.init(chartDom.value)
  option.value = {
    color: props.color,
    tooltip: { trigger: 'item' },
    series: [
      {
        type: 'pie',
        radius: ['40%', '70%'],
        padAngle: 3,
        itemStyle: { borderRadius: 3 },
        label: { show: false },
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)',
          },
        },
        data: props.pieData,
      },
    ],
  }
  option.value && myChart.value.setOption(option.value)
})
watch(
  () => props.pieData,
  (newData) => {
    if (myChart.value) {
      option.value.series[0].data = newData
      myChart.value.setOption(option.value)
    }
  },
  { deep: true }
)
</script>
<style lang="scss" scoped>
.echarts {
  width: 100%;
  height: 100%;
}
</style>
