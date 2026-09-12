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
        <el-button
          @click="openPopover('add', { ids: '0', status: 'OK', sort: 0 })"
        >
          新增
        </el-button>
      </template>
      <template #status="row">
        <el-tag :type="enumTag('statusTag', row.status)">
          {{ enumType("statusEnum", row.status) }}
        </el-tag>
      </template>
      <template #operation="row">
        <el-button link type="primary" @click="openPopover('edit', row)">
          编辑
        </el-button>
        <el-button
          link
          type="primary"
          v-if="row.level != '4'"
          @click="
            openPopover('add', {
              status: 'OK',
              sort: 0,
              ids: row.parentId === '0' ? row.id : row.ids + ',' + row.id,
            })
          "
        >
          新增
        </el-button>
        <el-button
          link
          type="danger"
          @click="removeFn(documentTypeDeleteApi, row.id, row.name)"
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
          addSubmitApi: documentTypeSaveApi,
          editSubmitApi: documentTypeUpdateApi,
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
        <template #parentId>
          <el-cascader
            style="width: 100%"
            v-model="parentIds"
            :options="options"
            :props="props"
          />
        </template>
      </ProForm>
    </Drawer>
  </div>
</template>

<script setup lang="ts" name="DocumentType">
import {
  documentTypeDeleteApi,
  documentTypePageApi,
  documentTypeSaveApi,
  documentTypeTypeListApi,
  documentTypeUpdateApi,
} from "@/api/document";
import { useTable } from "@/hooks/useTable";
import { enumType } from "@/utils/Enum";
import { enumTag } from "@/utils/enumTag";
import { StatusEnum } from "@/utils/radioEnum";

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
} = useTable({ api: documentTypePageApi, title: "文档分类" });

const columns: ColumnProps[] = [
  { prop: "name", label: "名称" },
  {
    prop: "level",
    label: "级别",
    value: (row: any) => enumType("documentLevelEnum", row.level),
  },
  { prop: "status", label: "状态", slot: true },
  { prop: "sort", label: "排序" },
  { prop: "createTime", label: "创建时间" },
  { prop: "createBy", label: "发布者" },
  { prop: "operation", label: "操作", slot: true },
];

const formColumns: IFormColumnsProps[] = [
  { label: "分类名称", prop: "name", el: "input" },
  { label: "排序", prop: "sort", el: "number" },
  { label: "状态", prop: "status", el: "radio", radioList: StatusEnum },
  { label: "上级菜单", prop: "parentId", el: "slot" },
];
const firstType = reactive({ id: "0", name: "主目录" });
const options = ref<any[]>([]);

const filterFn = (data: any[]) => {
  return data.filter((item: any) => {
    if (item.children) {
      item.children = filterFn(item.children);
    }
    return item.level != "4";
  });
};

const getParent = async () => {
  const { data } = await documentTypeTypeListApi();
  options.value = [firstType, ...filterFn(data)];
};
const props = {
  value: "id",
  label: "name",
  children: "children",
  checkStrictly: true,
};
const parentIds = ref<any[]>([0]);

watch(
  () => addOrEdit.value,
  (newVal) => {
    if (!newVal) return (parentIds.value = []), getParent();
    if (newVal) {
      parentIds.value = dataForm.value.ids.split(",");
    }
  },
  { immediate: true }
);

watch(
  () => parentIds.value,
  (newVal) => {
    dataForm.value.ids = newVal.join(",");
    dataForm.value.parentId = newVal[newVal.length - 1];
    // 计算级别
    dataForm.value.level = newVal[0] === "0" ? 1 : newVal.length + 1;
  }
);
</script>
<style lang="scss" scoped></style>
