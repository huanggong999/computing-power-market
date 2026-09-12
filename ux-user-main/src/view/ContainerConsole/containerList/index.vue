/** 容器列表 */
<template>
  <div class="table-box card position-relative container-list">
    <div class="flx-align-center">
      <TopPie class="pie" :TopPieData="clusterPieData" />
      <TopPie class="pie" :TopPieData="nodePieData" />
      <!-- <TopPie class="pie" :TopPieData="workloadPieData" /> -->
    </div>
    <ProTable
      type="none"
      :columns="columns"
      :tableData="tableData"
      :search-fn="searchFn"
      :search-param="searchParam"
      :reset-fn="resetFn"
      :is-page="false"
      :-is-refresh="false"
    >
      <template #tableHeader>
        <div class="table-header mb26">
          <TipText class="fwb" content="容器列表" fontSize="20">
            <template #end>
              <div class="searchBar">
                <el-input
                  :prefix-icon="Search"
                  placeholder="添加筛选条件"
                  v-model="searchParam.clusterName"
                ></el-input>
                <el-button type="primary" @click="searchFn">搜索</el-button>
                <el-button @click="resetFn">重置</el-button>
                <el-button
                  type="primary"
                  :icon="CirclePlus"
                  @click="toPage('/createCluster')"
                  >创建集群</el-button
                >
                <!-- <el-button>导出</el-button> -->
              </div>
            </template>
          </TipText>
        </div>
      </template>
      <template #vci="row">
        <div>cpu: {{ row.cpuNumber }} core</div>
        <div>内存: {{ row.memorySize }} GiB</div>
      </template>
      <template #handle="row">
        <div class="btns flx-center">
          <div class="blue" @click="detailHandler(row)">详情</div>
          <!-- <div class="blue mr10" @click="expandedHandler">扩容</div> -->
          <el-popconfirm
            title="确认删除当前容器吗?"
            @confirm="deleteHandler(row.id)"
            confirm-button-text="确认"
            cancel-button-text="取消"
          >
            <template #reference>
              <div class="blue">删除</div>
            </template>
          </el-popconfirm>
        </div>
      </template>
    </ProTable>
  </div>
</template>

<script setup lang="ts" name="ContainerList">
import { useTable } from '@/hooks/useTable'
import { toPage } from '@/utils'
import { Search, CirclePlus } from '@element-plus/icons-vue'
import TopPie from './components/TopPie.vue'
import {
  deleteContainerApi,
  getContainerListApi,
  getContainerOverviewApi,
} from '@/api/container'

const clusterPieData = ref({
  total: '5',
  title: '集群总数',
  data: [
    { name: '正常', value: 0 },
    { name: '异常', value: 0 },
    { name: '其他', value: 0 },
  ],
})

const nodePieData = ref({
  total: '6',
  title: '节点总数',
  data: [
    { name: '正常', value: 0 },
    { name: '异常', value: 0 },
    { name: '其他', value: 0 },
  ],
})
// const workloadPieData = ref({
//   total: '5',
//   title: '工作负载总数',
//   data: [
//     { name: '正常', value: 3 },
//     { name: '其他', value: 2 },
//   ],
//   color: ['#3972FD', '#FBA201'],
// })
const initPieData = () => {
  getContainerOverviewApi().then(({ data }) => {
    clusterPieData.value = {
      total: data.containerTotal,
      title: '集群总数',
      data: [
        { name: '正常', value: data.containerNormalNumber },
        { name: '异常', value: data.containerAbnormalNumber },
        { name: '其他', value: data.containerOtherNumber },
      ],
    }
    nodePieData.value = {
      total: data.nodePoolTotal,
      title: '节点总数',
      data: [
        { name: '正常', value: data.nodePoolNormalNumber },
        { name: '异常', value: data.nodePoolAbnormalNumber },
        { name: '其他', value: data.nodePoolOtherNumber },
      ],
    }
  })
}
const columns: ColumnProps[] = [
  { prop: 'clusterName', label: 'ID/名称' },
  { prop: 'status', label: '状态' },
  // { prop: '', label: '可用节点' },
  // { prop: '', label: 'vCPU用量' },
  // { prop: '', label: '内存用量' },
  { prop: 'vciNumber', label: 'VCI实例数' },
  { prop: 'vci', label: 'VCI用量', slot: true },
  { prop: 'kubernetesVersion', label: 'Kubernetes 版本' },
  { prop: 'handle', label: '操作', slot: true },
]
const { tableData, getList, searchParam, searchFn } = useTable({
  requestAuto: false,
  requestApi: getContainerListApi,
})
const resetFn = () => {
  searchParam.value.clusterName = ''
  searchFn()
}
// 扩容
const expandedHandler = (row: any) => {
  console.log('扩容')
}
// 查看详情
const detailHandler = (row: any) => {
  toPage(`/containerDetails?id=${row.id}`)
}
// 删除
const deleteHandler = (id: any) => {
  deleteContainerApi(id).then(() => {
    getList()
  })
}
const initPage = () => {
  getList()
  initPieData()
}
initPage()
</script>
<style lang="scss" scoped>
.container-list {
  display: grid;
  grid-template-rows: 210px 1fr;
  gap: 20px;
}
.pie {
  width: calc((100% - 30px) / 3);
  height: 100%;
  margin-right: 20px;
}
.table-header {
  width: 84vw;
  .searchBar {
    display: flex;
    gap: 10px;
    .el-input {
      width: 260px;
    }
    .el-button {
      margin: 0px;
    }
    .export-btn {
      border: #3972fd 1px solid;
      color: #3972fd;
    }
  }
}
.blue {
  color: #3972fd;
  margin-right: 20px;
  &:hover {
    cursor: pointer;
  }
}
</style>
