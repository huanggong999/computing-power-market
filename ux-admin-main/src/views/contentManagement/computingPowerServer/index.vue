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
    >
      <template #tableHeader>
        <el-button @click="openPopover('add', { productType: 1 })">
          新增
        </el-button>
      </template>

      <template #status="row">
        <el-tag :type="enumTag('statusNumTag', row.status)">
          {{ enumType("yesOrNoEnum", row.status) }}
        </el-tag>
      </template>

      <template #operation="row">
        <el-button link type="primary" @click="openPopover('edit', row)">
          编辑
        </el-button>
        <el-button
          link
          type="danger"
          @click="removeFn(homeEcsDeleteApi, row.id, row.name)"
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
        submit({
          addSubmitApi: homeEcsSaveApi,
          editSubmitApi: homeEcsUpdateApi,
        })
      "
      :isSave="!dataForm.id"
      @save="save"
    >
      <ProForm
        ref="proFormRef"
        v-model="dataForm"
        :formColumns="formColumns"
        :disabled="disabled"
        :label-width="120"
      >
        <template #payPriceColor>
          <el-color-picker v-model="dataForm.payPriceColor" show-alpha />
        </template>
        <template #ecsId>
          <div>
            <el-button @click="IsSelectCloudServer = true">
              选择云服务器
            </el-button>
            <el-descriptions
              border
              class="mt10"
              v-if="dataForm.ecsId"
              :column="1"
            >
              <el-descriptions-item label="服务器类型">
                {{ enumType("ecsTypeEnum", dataForm.ecsType) }}
              </el-descriptions-item>
              <el-descriptions-item label="服务器规格">
                {{ dataForm.ecsScale ?? "--" }}
              </el-descriptions-item>
            </el-descriptions>
          </div>
        </template>
      </ProForm>
    </Drawer>

    <SelectCloudServer
      :productType="1"
      v-model="IsSelectCloudServer"
      @submit="selectCloudServer"
    />
  </div>
</template>

<script setup lang="ts" name="ComputingPowerServer">
import {
  homeEcsDeleteApi,
  homeEcsPageApi,
  homeEcsSaveApi,
  homeEcsUpdateApi,
} from "@/api/computingPowerServer";
import { useTable } from "@/hooks/useTable";
import { isHomeEnum } from "@/utils/radioEnum";
import SelectCloudServer from "./components/SelectCloudServer.vue";
import { enumType } from "@/utils/Enum";
import { isHomeSelectEnum } from "@/utils/selectEnum";
import { enumTag } from "@/utils/enumTag";

const {
  tableData,
  pageData,
  searchParam,
  refreshFn,
  getList,
  searchFn,
  resetFn,
  openPopover,
  addOrEdit,
  popoverTitle,
  closePopover,
  disabled,
  submit,
  dataForm,
  proFormRef,
  removeFn,
  save,
} = useTable({
  api: homeEcsPageApi,
  title: "火山算力服务器",
  initParams: { productType: 1 },
});

const columns: ColumnProps[] = [
  { prop: "name", label: "服务器名称", search: { el: "input" } },
  {
    prop: "ecsType",
    label: "服务器类型",
    value: (row) => enumType("ecsTypeEnum", row.ecsType),
  },
  { prop: "ecsScale", label: "服务器规格" },
  { prop: "remark", label: "简介" },
  {
    prop: "status",
    label: "是否首页展示",
    slot: true,
    search: { el: "select" },
    enum: isHomeSelectEnum,
  },
  { prop: "createBy", label: "发布者" },
  { prop: "operation", label: "操作", slot: true },
];

const formColumns: IFormColumnsProps[] = [
  { label: "服务器名称", prop: "name", el: "input" },
  { label: "选择服务器", prop: "ecsId", el: "slot" },
  { label: "是否首页展示", prop: "status", el: "radio", radioList: isHomeEnum },
  { label: "价格", prop: "payPriceText", el: "input", required: false },
  {
    label: "价格颜色",
    prop: "payPriceColor",
    el: "slot",
    required: false,
  },
  { label: "简介", prop: "remark", el: "textarea", required: false },
];

const IsSelectCloudServer = ref(false);

const selectCloudServer = (
  ecsId: string,
  ecsScale: string,
  ecsType: string
) => {
  dataForm.value.ecsId = ecsId;
  dataForm.value.ecsScale = ecsScale;
  dataForm.value.ecsType = ecsType;
};
</script>
<style lang="scss" scoped></style>
