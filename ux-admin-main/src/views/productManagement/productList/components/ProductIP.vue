<template>
  <Drawer
    v-model="model"
    :isFooter="false"
    title="查看IP列表"
    size="85%"
    @closePopover="closeCheckIp"
  >
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
        type="selection"
        ref="proTableRef"
      >
        <template #tableHeader="{ selectedListIds }">
          <el-button
            type="primary"
            @click="openPopover('add', { productId, ipAddress })"
          >
            新增
          </el-button>
          <el-button type="primary" @click="importFn"> 导入 </el-button>

          <el-button
            @click="removeFn(productIpDeleteApi, selectedListIds)"
            :disabled="!selectedListIds.length"
          >
            批量删除
          </el-button>
          <el-button type="primary" @click="exportFn(selectedListIds)">
            导出 <span class="tip ml5">(默认全部导出)</span>
          </el-button>
        </template>
        <template #status="row">
          <el-tag :type="enumTag('productIpStatusTag', row.status)">
            {{ enumType("productIpStatusEnum", row.status) }}
          </el-tag>
        </template>
        <template #operation="row">
          <el-button link type="primary" @click="openPopover('edit', row)">
            编辑
          </el-button>
          <el-button
            link
            type="danger"
            @click="removeFn(productIpDeleteApi, [row.id], row.name)"
          >
            删除
          </el-button>
        </template>
      </ProTable>
    </div>
  </Drawer>

  <Drawer
    v-model="addOrEdit"
    :title="popoverTitle"
    @closePopover="closePopover"
    @submit="
      submit({
        addSubmitApi: productIpSaveApi,
        editSubmitApi: productIpUpdateApi,
      })
    "
  >
    <ProForm ref="proFormRef" v-model="dataForm" :formColumns="formColumns" />
  </Drawer>

  <ImportIp
    v-model="importVisible"
    :productId="productId"
    @close="importVisible = false"
    @success="getList"
  />
</template>

<script setup lang="ts" name="ProductIP">
import {
  productIpDeleteApi,
  productIpExportApi,
  productIpPageApi,
  productIpSaveApi,
  productIpUpdateApi,
} from "@/api/productManagement";
import { useTable } from "@/hooks/useTable";
import { enumType } from "@/utils/Enum";
import { enumTag } from "@/utils/enumTag";
import { productIpStatusEnum } from "@/utils/radioEnum";
import { productIpStatusSelectEnum } from "@/utils/selectEnum";
import { useVModel } from "@/utils/useVModel";
import ImportIp from "./ImportIp.vue";
import { useDownload } from "@/hooks/useDownload";

interface IPropsProductIP {
  modelValue: boolean;
  productId: string;
  ipAddress: string;
}
const emit = defineEmits(["closeCheckIp", "update:modelValue"]);
const props = defineProps<IPropsProductIP>();
const productId = computed(() => props.productId);
const model = useVModel(props, "modelValue", emit);
const {
  tableData,
  pageData,
  searchParam,
  refreshFn,
  getList,
  searchFn,
  resetFn,
  openPopover,
  removeFn,
  addOrEdit,
  popoverTitle,
  closePopover,
  dataForm,
  submit,
  proFormRef,
  proTableRef,
} = useTable({
  api: productIpPageApi,
  requestAuto: false,
  initParams: { productId },
  title: "产品IP列表",
});

const columns: ColumnProps[] = [
  { label: "商品ID", prop: "id", width: 120 },
  { label: "AGI-C产品ID", prop: "productId", width: 120 },
  { label: "内网IP", prop: "ip", search: { el: "input" } },
  { label: "公网IP", prop: "publicIp", search: { el: "input" } },
  { label: "IP地区", prop: "ipAddress" },
  {
    label: "状态",
    prop: "status",
    slot: true,
    search: { el: "select" },
    width: 100,
    enum: productIpStatusSelectEnum,
  },
  {
    label: "客户账号",
    prop: "customerName",
    width: 200,
    search: { el: "input" },
    value: (row) => row.nickname,
  },
  { label: "客户邮箱", prop: "email", width: 200, search: { el: "input" } },
  { label: "启用时间", prop: "useTime", width: 170 },
  { label: "创建时间", prop: "createTime", width: 170 },
  { label: "操作", prop: "operation", slot: true, width: 110, fixed: "right" },
];

const formColumns: IFormColumnsProps[] = [
  { label: "内网IP", prop: "ip", el: "input" },
  { label: "公网IP", prop: "publicIp", el: "input" },
  { label: "IP地区", prop: "ipAddress", el: "input", disabled: true },
  {
    label: "状态",
    prop: "status",
    el: "radio",
    radioList: productIpStatusEnum,
    visible: (row) => !!row.id,
  },
  {
    label: "客户账号",
    prop: "nickname",
    el: "input",
    required: false,
    visible: (row) => [1, 2].includes(row.status),
    disabled: true,
  },
];

const importVisible = ref(false);
const importFn = () => (importVisible.value = true);

const exportFn = async (ids: string[]) => {
  await useDownload(productIpExportApi, "产品IP", {
    productId: productId.value,
    ids,
    ...searchParam.value,
  });
  proTableRef.value.tableRef.clearSelection();
};

watch(
  () => model.value,
  (val) => {
    if (!val) return;
    getList();
  }
);

const closeCheckIp = () => emit("closeCheckIp");
</script>
<style lang="scss" scoped>
.tip {
  font-size: 12px;
}
</style>
