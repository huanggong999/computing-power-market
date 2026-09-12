<template>
  <div class="instance table-box">
    <div class="tips flx-center">
      <el-icon> <Document /> </el-icon><span> &nbsp; 使用指南</span>
    </div>

    <div class="container">
      <!-- 顶部bar -->
      <TipText class="fwb" content="实例明细" fontSize="20">
        <!-- 搜索栏 -->
        <template #end>
          <div class="searchBar">
            <el-input
              v-model="search"
              placeholder="添加筛选条件"
              :prefix-icon="Search"
            ></el-input>
            <el-button type="primary" @click="getList">搜索</el-button>
            <el-button @click="reset">重置</el-button>
            <el-button
              type="primary"
              :icon="CirclePlus"
              @click="toPage('/cloud/createInstance')"
              >创建实例</el-button
            >
            <!-- todo:恢复 -->
            <!-- <el-button class="export-btn">导出</el-button> -->
          </div>
        </template>
      </TipText>
      <!-- 内容区 -->
      <div class="mt26">
        <el-table
          :data="tableData"
          style="width: 100%"
          :cell-style="{
            textAlign: 'center',
            fontSize: '13px',
          }"
          :header-cell-style="{
            textAlign: 'center',
            fontSize: '13px',
          }"
        >
          <!-- todo:恢复 -->
          <!-- <el-table-column type="selection" /> -->
          <el-table-column property="instanceName" label="ID/名称" />
          <el-table-column property="status" label="状态">
            <template #default="scope">
              {{ statusMap[scope.row.status] }}
            </template></el-table-column
          >
          <el-table-column property="zone.name" label="可用区">
            <template #default="scope">
              {{ scope.row.zone ? scope.row.zone.name : '-' }}
            </template>
          </el-table-column>
          <el-table-column property="osName" label="镜像">
            <template #default="scope">
              {{ scope.row.osName ? scope.row.osName : '-' }}
            </template>
          </el-table-column>
          <el-table-column property="cale" label="规格">
            <template #default="scope">
              {{ scope.row.cale ? scope.row.cale : '-' }}
            </template>
          </el-table-column>
          <el-table-column property="systemVolume" label="系统盘">
            <template #default="scope">
              {{
                scope.row.systemVolume ? scope.row.systemVolume.volumeType : '-'
              }}
            </template>
          </el-table-column>
          <el-table-column property="ipArr" label="主IPv4地址">
            <template #default="scope">
              {{ scope.row.eipAddress ? scope.row.eipAddress.ipAddress : '-' }}
            </template>
          </el-table-column>
          <el-table-column property="chargeType" label="实例计费类型">
            <template #default="scope">
              {{ payMap[scope.row.chargeType] }}
            </template>
          </el-table-column>
          <el-table-column property="chargeType" label="续费方式">
            <template #default="scope">
              {{ payMap[scope.row.chargeType] }}
            </template>
          </el-table-column>
          <el-table-column label="操作">
            <template #default="scope">
              <span class="check" @click="checkDetail(scope.row)">详情</span>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <!-- 底部bar -->
      <!-- <div class="footer flx-align-center">
          <div class="choose">已选 <span class="num">1</span> 条</div>
          <el-button type="primary">启动</el-button>
          <el-button type="primary">停止</el-button>
          <el-button type="primary">重启</el-button>
          <el-button type="primary">删除</el-button>
          <el-button type="primary">重置密码</el-button>
          <el-button type="primary">续费</el-button>
          <el-icon><MoreFilled /></el-icon>
        </div> -->
      <Pagination
        :page-data="pageData"
        :PageChange="getList"
        :pageSizes="[9, 18, 56, 118]"
      />
    </div>
  </div>
</template>

<script setup lang="ts" name="Instance">
import { toPage } from '@/utils'
import { ref } from 'vue'
import { Search, CirclePlus } from '@element-plus/icons-vue'
import { getInstanceList } from '@/api/instance'
import { ElMessage } from 'element-plus'
interface User {
  date: string
  name: string
  address: string
}
const statusMap: any = {
  CREATING: '创建中',
  RUNNING: '运行中',
  STOPPING: '停止中',
  STOPPED: '已停止',
  REBOOTING: '重启中',
  STARTING: '启动中',
  REBUILDING: '重装中',
  RESIZING: '更配中',
  ERROR: '错误',
  DELETING: '删除中',
  HAVE_NOT_OPENED: '待开通',
  HAVE_OPENED: '已开通',
  CALCULATING: '计算中',
  SHUT_DOWN: '已停机',
  REFUNDED: '已退款',
  EXPIRE: '已过期',
}
const payMap: any = {
  POSTPAID_BY_HOUR: '按量付费',
  POSTPAID_BY_MONTH: '包月',
  POSTPAID_BY_YEAR: '包年',
}
// 翻页器
const pageData = ref({ pageSize: 9, pageNo: 1, total: 9 })
const tableData = ref<any>([])
let search = ref('')
const init = () => {
  getInstanceList({}).then((res: any) => {
    if (res.data.list) tableData.value = res.data.list
    pageData.value.total = Number(res.data.dataTotal)
  })
}
const getList = () => {
  let params = {
    pageNo: pageData.value.pageNo,
    pageSize: pageData.value.pageSize,
    instanceName: search.value,
  }
  getInstanceList(params).then((res: any) => {
    if (res.data) tableData.value = res.data.list
    pageData.value.total = Number(res.data.dataTotal)
  })
}
const reset = () => {
  pageData.value = { pageSize: 9, pageNo: 1, total: 9 }
  search.value = ''
  getList()
}
const checkDetail = (row: any) => {
  // console.log(row)
  // let arr = ['RUNNING', 'STOPPING', 'REBOOTING', 'STARTING', 'STOPPED']
  // if (row.instanceId) {
  toPage(`/cloud/instanceDetail?id=${row.id}`)
  //   return
  // } else {
  //   ElMessage.warning('该实例暂不支持查看')
}
// toPage(`/cloud/instanceDetail?id=${row.id}`)

onMounted(() => {
  init()
})
</script>
<style lang="scss" scoped>
@import './index.scss';
</style>
