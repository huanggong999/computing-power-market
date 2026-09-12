<template>
  <div class="card activity-box position-relative">
    <TipText class="fwb mb28" content="基础信息" fontSize="20">
      <template #end>
        <div class="btns flx-align-center">
          <el-input
            :prefix-icon="Search"
            placeholder="请输入名称/手机号"
            v-model="tableParam.nameOrPhone"
          />
          <el-button type="primary" @click="tableSearch">查询</el-button>
          <el-button @click="resetHandler">重置</el-button>
        </div>
      </template>
    </TipText>
    <ProTable
      ref="tableRef"
      :page-data="pageData"
      :columns="tableCol"
      :tableData="tableData"
      :search-param="tableParam"
      :search-fn="tableSearch"
      :getList="tableGet"
      :IsRefresh="false"
      :is-page="false"
    >
      <template #info="row">
        <div>{{ row.registerUsername }}</div>
        <div>{{ row.registerUserPhone }}</div>
      </template>
    </ProTable>
    <Pagination
      :page-data="pageData"
      :PageChange="tableGet"
      :pageSizes="[10, 15, 30, 50]"
    />
  </div>
</template>

<script setup lang="ts" name="VoucherCollectionCenter">
import { getActivityRecordApi } from '@/api/activity'
import { useTable } from '@/hooks/useTable'
import { Search } from '@element-plus/icons-vue'
import { useRoute } from 'vue-router'
const route = useRoute()
const tableCol: ColumnProps[] = [
  { label: '邀请时间', prop: 'invitationTime' },
  { label: '注册用户ID/Key', prop: 'registerUserId', width: 400 },

  { label: '注册用户信息', prop: 'info', slot: true },
  {
    label: '关联订单号',
    prop: 'orderNo',
  },
]
const {
  pageData,
  getList: tableGet,
  tableData: tableData,
  searchFn: tableSearch,
  searchParam: tableParam,
} = useTable({
  requestApi: getActivityRecordApi,
  requestAuto: false,
})
const resetHandler = () => {
  tableParam.value = { activeId: route.query.id }
  pageData.value.pageNo = 1
  tableSearch()
}
const initPage = () => {
  tableParam.value = { activeId: route.query.id }
  tableSearch()
}
initPage()
</script>
<style lang="scss" scoped>
.activity-box {
  .btns {
    gap: 10px;
    .el-input {
      width: 200px;
    }
    .el-button {
      margin: 0;
    }
  }
}
</style>
