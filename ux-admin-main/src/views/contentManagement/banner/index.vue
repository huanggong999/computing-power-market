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
        <el-button @click="openPopover('add', { status: 'OK', sort: 0 })">
          新增
        </el-button>
      </template>
      <template #image="row">
        <ImagePreview :src="row.image" />
      </template>
      <template #status="row">
        <el-tag :type="enumTag('statusTag', row.status)">
          {{ enumType("statusEnum", row.status) }}
        </el-tag>
      </template>
      <template #operation="row">
        <el-button
          link
          type="primary"
          @click="openPopover('edit', bannerDetailApi, row.id)"
        >
          编辑
        </el-button>
        <el-button
          link
          type="danger"
          @click="removeFn(bannerDeleteApi, row.id, row.name)"
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
        submit({ addSubmitApi: bannerSaveApi, editSubmitApi: bannerUpdateApi })
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
            <template #tip> {{ tip }} </template>
          </UploadImg>
        </template>
      </ProForm>
    </Drawer>
  </div>
</template>

<script setup lang="ts" name="Banner">
import {
  bannerDeleteApi,
  bannerDetailApi,
  bannerPageApi,
  bannerSaveApi,
  bannerUpdateApi,
} from "@/api/banner";
import { useTable } from "@/hooks/useTable";
import { enumType } from "@/utils/Enum";
import { enumTag } from "@/utils/enumTag";
import {
  bannerJumpPositionEnum,
  bannerPositionEnum,
  bannerTypeEnum,
  StatusEnum,
} from "@/utils/radioEnum";
import { bannerTypeSelectEnum } from "@/utils/selectEnum";

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
} = useTable({ api: bannerPageApi, title: "轮播图" });
const columns: ColumnProps[] = [
  { prop: "id", label: "ID" },
  { prop: "name", label: "轮播图名称", search: { el: "input" } },
  {
    prop: "equipmentType",
    label: "轮播图类型",
    search: { el: "select" },
    enum: bannerTypeSelectEnum,
    value: (row) => enumType("bannerTypeEnum", row.equipmentType),
  },
  { prop: "image", label: "图片", slot: true },
  { prop: "sort", label: "排序" },
  {
    prop: "type",
    label: "位置",
    value: (row) => enumType("bannerPositionEnum", row.type),
  },
  {
    prop: "skipType",
    label: "跳转位置",
    value: (row) => enumType("bannerJumpPositionEnum", row.skipType),
  },
  { prop: "status", label: "状态", slot: true },
  { prop: "operation", label: "操作", slot: true },
];
const formColumns: IFormColumnsProps[] = [
  { label: "轮播图名称", prop: "name", el: "input" },
  {
    label: "轮播图类型",
    prop: "equipmentType",
    el: "radio",
    radioList: bannerTypeEnum,
  },
  {
    label: "轮播图位置",
    prop: "type",
    el: "radio",
    radioList: bannerPositionEnum,
  },
  {
    label: "轮播图跳转位置",
    prop: "skipType",
    el: "radio",
    radioList: bannerJumpPositionEnum,
  },
  { label: "排序", prop: "sort", el: "number" },
  { label: "图片", prop: "image", el: "slot" },
  { label: "状态", prop: "status", el: "radio", radioList: StatusEnum },
];

const tip = computed(() =>
  dataForm.value.equipmentType === 2
    ? "请上传750px * 266px的图片"
    : "请上传1920px * 680px的图片"
);
</script>
<style lang="scss" scoped></style>
