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
        <el-button type="primary" @click="openPopover('add', { formType: 2 })">
          新增
        </el-button>
      </template>
      <template #status="row">
        <el-tag :type="enumTag('statusNumTag', row.status)">
          {{ enumType("statusNumEnum", row.status) }}
        </el-tag>
      </template>
      <template #operation="row">
        <el-button
          link
          type="primary"
          @click="openPopover('edit', inquiryFormDetailApi, row.id)"
        >
          编辑
        </el-button>
        <el-button
          link
          type="danger"
          @click="removeFn(inquiryFormDeleteApi, row.id, row.name)"
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
      size="100%"
      @submit="submitTheForm"
    >
      <ProForm
        ref="proFormRef"
        v-model="dataForm"
        :formColumns="formColumns"
        :disabled="disabled"
        :label-width="120"
        label-position="top"
      >
        <template #json>
          <MyFormCreateDesigner
            ref="myFormCreateDesigner"
            :json="dataForm.json"
          />
        </template>
      </ProForm>
    </Drawer>
  </div>
</template>

<script setup lang="ts" name="InquiryForm">
import { useTable } from "@/hooks/useTable";
import { statusNumberEnum } from "@/utils/radioEnum";
import MyFormCreateDesigner from "../components/MyFormCreateDesigner.vue";
import {
  inquiryFormDeleteApi,
  inquiryFormDetailApi,
  inquiryFormPageApi,
  inquiryFormSaveApi,
  inquiryFormUpdateApi,
} from "@/api/productManagement";
import { enumType } from "@/utils/Enum";
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
  popoverTitle,
  addOrEdit,
  proFormRef,
  closePopover,
  disabled,
  submit,
  dataForm,
  removeFn,
} = useTable({
  api: inquiryFormPageApi,
  title: "购买表单",
  initParams: { formType: 2 },
});

const columns: ColumnProps[] = [
  { label: "表单ID", prop: "id" },
  { label: "购买表单名称", prop: "name" },
  { label: "状态", prop: "status", slot: true },
  { prop: "createBy", label: "发布者" },
  { label: "创建时间", prop: "createTime" },
  { label: "操作", prop: "operation", slot: true },
];

const formColumns: IFormColumnsProps[] = [
  { label: "购买表单名称", prop: "name", el: "input" },
  { label: "状态", prop: "status", el: "radio", radioList: statusNumberEnum },
  { label: "表单内容", prop: "json", el: "slot" },
];
const myFormCreateDesigner = ref<any>(null);
const submitTheForm = () => {
  dataForm.value.json = myFormCreateDesigner.value?.designer.getJson();
  submit({
    addSubmitApi: inquiryFormSaveApi,
    editSubmitApi: inquiryFormUpdateApi,
  });
};
</script>
<style lang="scss" scoped></style>
