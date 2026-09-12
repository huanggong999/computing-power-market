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
          @click="openPopover('add', { parentId: '0', status: 'OK', sort: 0 })"
        >
          新增
        </el-button>
      </template>
      <template #operation="row">
        <el-button link type="primary" @click="openPopover('edit', row)">
          编辑
        </el-button>
        <el-button
          link
          type="primary"
          v-if="row.parentId == '0'"
          @click="
            openPopover('edit', { parentId: row.id, status: 'OK', sort: 0 })
          "
        >
          新增
        </el-button>
        <el-button
          link
          type="danger"
          @click="removeFn(applyTypeDeleteApi, row.id, row.name)"
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
          addSubmitApi: applyTypeSaveApi,
          editSubmitApi: applyTypeUpdateApi,
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
          <el-select v-model="dataForm.parentId" placeholder="">
            <el-option
              v-for="item in parentList"
              :key="item.value"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </template>
      </ProForm>
    </Drawer>
  </div>
</template>

<script setup lang="ts" name="ApplyType">
import {
  applyTypeDeleteApi,
  applyTypeFirstListApi,
  applyTypePageApi,
  applyTypeSaveApi,
  applyTypeUpdateApi,
} from "@/api/applyType";
import { useTable } from "@/hooks/useTable";
import { StatusEnum } from "@/utils/radioEnum";

const {
  tableData,
  pageData,
  searchParam,
  refreshFn,
  getList,
  openPopover,
  addOrEdit,
  popoverTitle,
  closePopover,
  disabled,
  submit,
  dataForm,
  proFormRef,
  searchFn,
  resetFn,
  removeFn,
} = useTable({ api: applyTypePageApi, title: "应用分类" });

const columns: ColumnProps[] = [
  { prop: "name", label: "分类名称", align: "left" },
  { prop: "sort", label: "排序" },
  { prop: "createBy", label: "发布者" },
  { prop: "createTime", label: "创建时间" },
  { prop: "operation", label: "操作", slot: true },
];

//#region 获取一级分类
const firstType = reactive({ id: "0", name: "主目录" });
const parentList = ref<any[]>([]);
const getParent = async () => {
  const { data } = await applyTypeFirstListApi();
  parentList.value = [firstType, ...data];
};

watch(
  () => tableData.value,
  () => getParent()
);

//#endregion
const formColumns: IFormColumnsProps[] = [
  { label: "分类名称", prop: "name", el: "input" },
  { label: "排序", prop: "sort", el: "number" },
  { label: "状态", prop: "status", el: "radio", radioList: StatusEnum },
  { label: "上级菜单", prop: "parentId", el: "slot" },
];
</script>
<style lang="scss" scoped></style>
