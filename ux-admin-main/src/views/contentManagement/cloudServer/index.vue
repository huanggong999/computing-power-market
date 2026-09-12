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
        <el-button @click="openImportTemplate(1)">
          导入火山引擎服务器
        </el-button>
        <el-button @click="openImportTemplate(2)"> 导入自建服务器 </el-button>
        <el-button @click="openPopover('add')"> 新增服务器 </el-button>
      </template>
      <template #operation="row">
        <el-button link type="primary" @click="openPopover('edit', row)">
          编辑
        </el-button>
        <el-button link type="danger" @click="removeFn(deleteEcsApi, row.id)">
          删除
        </el-button>
      </template>
    </ProTable>

    <Drawer
      v-model="addOrEdit"
      :title="popoverTitle"
      @closePopover="closePopover"
      :disabled="disabled"
      @submit="submit({ addSubmitApi: addEcsApi, editSubmitApi: updateEcsApi })"
      size="40%"
      :isSave="!dataForm.id"
      @save="save"
    >
      <ProForm
        ref="proFormRef"
        v-model="dataForm"
        :formColumns="formColumns"
        :disabled="disabled"
        :label-width="200"
      >
        <template #productType>
          <el-radio-group v-model="dataForm.productType" @change="changeType">
            <el-radio
              v-for="(item, index) in productTypeEnum"
              :key="index"
              :label="item.label"
              :value="item.label"
            >
              {{ item.description }}
            </el-radio>
          </el-radio-group>
        </template>
        <template #regionsZones>
          <el-select v-model="dataForm.regionsZones" placeholder="">
            <template v-if="dataForm.productType === 1">
              <el-option
                v-for="item in volcanoCloudServerAvailabilityZone"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              >
              </el-option>
            </template>

            <template v-if="dataForm.productType === 2">
              <el-option
                v-for="item in ecsAvailableZoneSelectEnum"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              >
              </el-option>
            </template>
          </el-select>
        </template>
      </ProForm>
    </Drawer>

    <ImportESC
      :type="productType"
      v-model="synchronousVisible"
      @submit="getList"
    />
  </div>
</template>

<script setup lang="ts" name="CloudServer">
import {
  addEcsApi,
  deleteEcsApi,
  ecsPageApi,
  updateEcsApi,
} from "@/api/cloudServer";
import { useTable } from "@/hooks/useTable";
import { enumType } from "@/utils/Enum";
import { productTypeEnum, serverTypeEnum } from "@/utils/radioEnum";
import {
  ecsAvailableZoneSelectEnum,
  productTypSelectEnum,
  serverTypeSelectEnum,
  volcanoCloudServerAvailabilityZone,
} from "@/utils/selectEnum";

import ImportESC from "./components/ImportESC.vue";

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
  proFormRef,
  dataForm,
  removeFn,
  save,
} = useTable({ api: ecsPageApi, title: "云服务器" });
const columns: ColumnProps[] = [
  {
    prop: "productType",
    label: "服务器类型",
    width: 120,
    value: (row) => enumType("workOrderProductTypeEnum", row.productType),
    search: { el: "select" },
    enum: productTypSelectEnum,
  },
  { prop: "hoursPrice", label: "按量计费价格", width: 120 },
  { prop: "monthPrice", label: "包年包月价格", width: 120 },
  { prop: "oneYearPrice", label: "1年价格", width: 120 },
  { prop: "twoYearPrice", label: "2年价格", width: 120 },
  { prop: "threeYearPrice", label: "3年价格", width: 120 },
  {
    prop: "ecsTypeEnum",
    label: "ecs类型",
    value: (row) => enumType("ecsTypeEnum", row.ecsType),
    search: { el: "select" },
    enum: serverTypeSelectEnum,
    width: 130,
  },
  { prop: "ecsScale", label: "服务器规格", width: 150 },
  { prop: "cpuNumber", label: "cpu数量", width: 90 },
  { prop: "memorySize", label: "内存大小", width: 90 },
  { prop: "cpuModel", label: "cpu型号", width: 180 },
  {
    prop: "sourceRegions",
    label: "可用区",
    value: (row) => enumType("regionEnum", row.regionsZones),
    search: { el: "select" },
    // enum: regionSelectEnum,
    enum: ecsAvailableZoneSelectEnum,
    width: 143,
  },
  { prop: "gpuModel", label: "gpu型号", width: 130 },
  { prop: "gpuMemory", label: "gpu内存", width: 130 },
  { prop: "operation", label: "操作", fixed: "right", width: 120, slot: true },
];

const synchronousVisible = ref(false);

// 导入类型 1 火山云引擎  2 自建服务器
const productType = ref<1 | 2>(1);
// 打开导入模板弹窗
const openImportTemplate = (type: 1 | 2) => {
  productType.value = type;
  synchronousVisible.value = true;
};
const changeType = () => (dataForm.value.regionsZones = "");

const formColumns: IFormColumnsProps[] = [
  {
    prop: "productType",
    label: "类型",
    // el: "radio",
    el: "slot",
    radioList: productTypeEnum,
  },
  {
    prop: "ecsType",
    label: "ecs类型",
    el: "radio",
    radioList: serverTypeEnum,
  },
  {
    prop: "regionsZones",
    label: "可用区",
    el: "slot",
    // selectLabel: "label",
    // selectValue: "value",
    // selectList: ecsAvailableZoneSelectEnum,
  },

  { prop: "hoursPrice", label: "按量计费价格", el: "price" },
  {
    prop: "ipPrice",
    label: "公网流量费用(时/元/M)",
    el: "price",
    visible: (val: TKeyValue) => {
      if (val.productType === 1) delete val.ipPrice;
      return val.productType === 2;
    },
  },
  { prop: "monthPrice", label: "包年包月价格", el: "price" },
  { prop: "oneYearPrice", label: "1年价格", el: "price" },
  { prop: "twoYearPrice", label: "2年价格", el: "price" },
  { prop: "threeYearPrice", label: "3年价格", el: "price" },
  { prop: "ecsScale", label: "服务器规格", el: "input" },
  { prop: "cpuNumber", label: "cpu数量", el: "number" },
  { prop: "memorySize", label: "内存大小", el: "number" },
  { prop: "cpuModel", label: "cpu型号", el: "input" },
  { prop: "gpuModel", label: "gpu型号", el: "input", required: false },
  { prop: "gpuMemory", label: "gpu内存", el: "input", required: false },
];
</script>
<style lang="scss" scoped></style>
