<template>
  <div class="table-box">
    <ProTable
      :columns="columns"
      :tableData="tableData"
      :pageData="pageData"
      :search-param="searchParam"
      :refreshFn="refreshFn"
      :getList="getList"
      :searchFn="searchFn"
      :resetFn="resetFn"
      type="none"
    >
      <template #tableHeader>
        <el-button @click="openPopover('add', { status: 1, sort: 0 })">
          新增
        </el-button>
      </template>
      <template #status="row">
        <el-tag :type="enumTag('statusNumTag', row.status)">
          {{ enumType("statusNumEnum", row.status) }}
        </el-tag>
      </template>
      <template #image="row"><ImagePreview :src="row.image" /> </template>
      <template #imageTwo="row"><ImagePreview :src="row.imageTwo" /> </template>

      <template #operation="row">
        <el-button
          link
          type="primary"
          @click="openPopover('edit', newsGetDetailsApi, row.id)"
        >
          编辑
        </el-button>
        <el-button
          link
          type="danger"
          @click="removeFn(newsDeleteApi, row.id, row.name)"
        >
          删除
        </el-button>
      </template>
    </ProTable>
    <Drawer
      v-model="addOrEdit"
      :title="popoverTitle"
      @closePopover="closePopover"
      :disabled="disabled"
      @submit="
        submit({ addSubmitApi: newsSaveApi, editSubmitApi: newsUpdateApi })
      "
      :isSave="!dataForm.id"
      @save="save"
    >
      <ProForm
        ref="proFormRef"
        v-model="dataForm"
        :formColumns="formColumns"
        :disabled="disabled"
        :label-width="140"
      >
        <template #image>
          <UploadImg :disabled="disabled" v-model:imageUrl="dataForm.image">
            <template #tip> 请上传比例为 4 : 3 的图片 </template>
          </UploadImg>
        </template>
        <template #imageTwo>
          <UploadImg :disabled="disabled" v-model:imageUrl="dataForm.imageTwo">
            <template #tip> 请上传比例为 4 : 3 的图片 </template>
          </UploadImg>
        </template>
      </ProForm>
    </Drawer>
  </div>
</template>

<script setup lang="ts" name="News">
import {
  newsDeleteApi,
  newsGetDetailsApi,
  newsPageApi,
  newsSaveApi,
  newsUpdateApi,
} from "@/api/operationsManagement";
import { useTable } from "@/hooks/useTable";
import { enumType } from "@/utils/Enum";
import { enumTag } from "@/utils/enumTag";
import { statusNumberEnum } from "@/utils/radioEnum";

const {
  tableData,
  pageData,
  searchParam,
  refreshFn,
  getList,
  openPopover,
  searchFn,
  resetFn,
  addOrEdit,
  popoverTitle,
  closePopover,
  disabled,
  submit,
  proFormRef,
  dataForm,
  removeFn,
  save,
} = useTable({ api: newsPageApi, title: "新闻" });

const columns: ColumnProps[] = [
  { prop: "id", label: "ID" },
  { prop: "name", label: "标题", search: { el: "input" } },
  { prop: "image", label: "官网图片", slot: true },
  { prop: "imageTwo", label: "小程序图片", slot: true },
  { prop: "sort", label: "排序" },
  { prop: "status", label: "状态", slot: true },
  { prop: "createBy", label: "发布人" },
  { prop: "operation", label: "操作", slot: true },
];
const formColumns: IFormColumnsProps[] = [
  { label: "标题", prop: "name", el: "input" },
  { label: "官网图片", prop: "image", el: "slot" },
  { label: "小程序图片", prop: "imageTwo", el: "slot" },
  { label: "排序", prop: "sort", el: "number" },
  { label: "状态", prop: "status", el: "radio", radioList: statusNumberEnum },
  { label: "内容", prop: "content", el: "wangEditor" },
];
</script>
<style lang="scss" scoped></style>
