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
      <template #operation="row">
        <el-button type="primary" link @click="handleClick(row.json)">
          详情
        </el-button>
        <el-button
          link
          type="danger"
          @click="removeFn(formDataDeleteApi, row.id)"
        >
          删除
        </el-button>
      </template>
    </ProTable>

    <MyFormCreate v-model="isShowModel" :rule="rule" />
  </div>
</template>

<script setup lang="ts" name="FormDataRecovery">
import { useTable } from "@/hooks/useTable";
import MyFormCreate from "../components/MyFormCreate.vue";
import { formDataDeleteApi, formDataPageApi } from "@/api/productManagement";

const rule = ref("");

const {
  tableData,
  pageData,
  searchParam,
  refreshFn,
  getList,
  searchFn,
  resetFn,
  removeFn,
} = useTable({ api: formDataPageApi, title: "咨询表单" });

const isShowModel = ref(false);

const columns: ColumnProps[] = [
  { label: "产品名称", prop: "productName", search: { el: "input" } },
  { label: "表单名称", prop: "formName", search: { el: "input" } },
  { label: "用户名称", prop: "userName" },
  { label: "用户手机号", prop: "phone" },
  { label: "创建时间", prop: "createTime" },
  { label: "操作", prop: "operation", slot: true },
];

const handleClick = (value: string) => {
  rule.value = value;
  isShowModel.value = true;
};
</script>
<style lang="scss" scoped></style>
