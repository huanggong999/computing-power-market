<template>
  <div class="object table-box">
    <div class="tips flx-center">
      <el-icon><Document /></el-icon><span> &nbsp; 使用指南</span>
    </div>

    <div class="container">
      <!-- 顶部bar -->
      <TipText class="fwb" content="桶列表" fontSize="20">
        <!-- 搜索栏 -->
        <template #end>
          <div class="searchBar">
            <el-input
              v-model="searchParam.bucketName"
              placeholder="添加筛选条件"
              :prefix-icon="Search"
            ></el-input>
            <el-button type="primary" @click="searchFn">搜索</el-button>
            <el-button @click="resetFn">重置</el-button>
            <el-button
              type="primary"
              :icon="CirclePlus"
              @click="toPage('/createBucket')"
              >创建桶</el-button
            >
            <!-- <el-button class="export-btn">导出</el-button> -->
          </div>
        </template>
      </TipText>
      <!-- 内容区 -->
      <div class="mt26">
        <ProTable
          :columns="columns"
          :tableData="tableData"
          :page-data="pageData"
          :get-list="getList"
          :is-page="true"
          :IsRefresh="false"
        >
          <template #redundancyType="row">
            <span v-if="row.redundancyType == 0">单冗余</span>
            <span v-if="row.redundancyType == 1">多AZ冗余</span>
          </template>
          <template #bucketStrategy="row">
            <span v-if="row.bucketStrategy == 0">私有</span>
            <span v-if="row.bucketStrategy == 1">公共读</span>
            <span v-if="row.bucketStrategy == 2">公共读写</span>
          </template>
          <template #handle="row">
            <div class="flx-center handle">
              <div
                class="text-button"
                style="color: #3972fd"
                @click="toDetail(row.name, row.id)"
              >
                详情
              </div>
              <el-popconfirm
                title="确认删除当前桶吗?"
                @confirm="deleteHandler(row.name)"
                confirm-button-text="确认"
                cancel-button-text="取消"
              >
                <template #reference>
                  <div class="text-button" style="color: #ff4151">删除</div>
                </template>
              </el-popconfirm>
            </div>
          </template>
        </ProTable>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts" name="Instance">
import { deleteBucketAPI, getBucketListAPI } from '@/api/storage'
import { getAmountListAPI } from '@/api/user'
import { useTable } from '@/hooks/useTable'
import { toPage } from '@/utils'
import { Search, CirclePlus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
const search = ref('')

const { tableData, pageData, getList, searchFn, searchParam, resetFn } =
  useTable({
    requestApi: getBucketListAPI,
  })
const columns: ColumnProps[] = [
  { prop: 'name', label: '桶名称' },
  { prop: 'region', label: '区域' },
  { prop: 'storageType', label: '默认存储类型' },
  { prop: 'redundancyType', label: '冗余类型', slot: true },
  { prop: 'bucketStrategy', label: '桶策略', slot: true },
  { prop: 'createTime', label: '创建时间' },
  { prop: 'handle', label: '操作', slot: true },
]
// 查看桶详情
const toDetail = (name: string, id: string) => {
  toPage(`/storageDetail?name=${name}&id=${id}`)
}
// 删除桶
const deleteHandler = (name: string) => {
  deleteBucketAPI(name).then((res: any) => {
    if (res.code === 200) {
      ElMessage.success('删除成功')
      getList()
    }
  })
}
</script>
<style lang="scss" scoped>
@import './index.scss';
</style>
