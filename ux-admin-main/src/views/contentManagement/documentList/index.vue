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
        <el-button @click="openPopover('add', { status: 'OK', sort: 0 })">
          新增
        </el-button>
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
          @click="openPopover('edit', documentDetailApi, row.id)"
        >
          编辑
        </el-button>
        <el-button
          link
          type="danger"
          @click="removeFn(documentDeleteApi, row.id, row.name)"
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
      size="80%"
      @submit="
        submit({
          addSubmitApi: documentSaveApi,
          editSubmitApi: documentUpdateApi,
        })
      "
    >
      <ProForm
        ref="proFormRef"
        v-model="dataForm"
        :formColumns="formColumns"
        :disabled="disabled"
        :label-width="100"
      >
        <template #typeId>
          <el-cascader
            style="width: 100%"
            v-model="typeTypes"
            :options="options"
            :props="props"
          />
        </template>
      </ProForm>
    </Drawer>
  </div>
</template>

<script setup lang="ts" name="DocumentList">
import {
  documentDeleteApi,
  documentDetailApi,
  documentPageApi,
  documentSaveApi,
  documentTypeTypeListApi,
  documentUpdateApi,
} from "@/api/document";
import { useTable } from "@/hooks/useTable";
import { enumType } from "@/utils/Enum";
import { enumTag } from "@/utils/enumTag";
import { StatusEnum } from "@/utils/radioEnum";
import { statusSelectNumEnum } from "@/utils/selectEnum";

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
} = useTable({ api: documentPageApi, title: "文档" });

const columns: ColumnProps[] = [
  { prop: "name", label: "名称", search: { el: "input" } },
  { prop: "typeName", label: "文档分类" },
  { prop: "sort", label: "排序" },
  {
    prop: "status",
    label: "状态",
    slot: true,
    search: { el: "select" },
    enum: statusSelectNumEnum,
  },
  { prop: "createTime", label: "创建时间" },
  { prop: "createBy", label: "发布者" },
  { prop: "operation", label: "操作", slot: true },
];
const formColumns: IFormColumnsProps[] = [
  { label: "文档名称", prop: "name", el: "input" },
  { label: "文档分类", prop: "typeId", el: "slot" },
  { label: "排序", prop: "sort", el: "number" },
  { label: "状态", prop: "status", el: "radio", radioList: StatusEnum },
  { label: "简介", prop: "intro", el: "input" },
  { label: "文档介绍", prop: "introduce", el: "markdownEditor" },
];

const options = ref<any[]>([]);
const getParent = async () => {
  const { data } = await documentTypeTypeListApi();
  options.value = data;
};
getParent();

const typeTypes = ref<any[]>([]);
const props = {
  value: "id",
  label: "name",
  children: "children",
};
watch(
  () => addOrEdit.value,
  (newVal) => {
    if (!newVal) return (typeTypes.value = []);
    if (newVal) {
      typeTypes.value = dataForm.value.typeTypes ?? [];
    }
  },
  { immediate: true }
);
watch(
  () => typeTypes.value,
  (newVal) => {
    dataForm.value.typeId = newVal[newVal.length - 1];
  }
);
</script>
<style lang="scss" scoped></style>
