<template>
  <div class="table-box">
    <ProTable
      :columns="columns"
      :tableData="tableData"
      :pageData="pageData"
      :search-param="searchParam"
      :refreshFn="refreshFn"
      :getList="getList"
    >
      <template #tableHeader>
        <el-button
          @click="
            openPopover('add', { status: 'OK', dataScope: 'ALL', roleSort: 0 })
          "
        >
          新增
        </el-button>
      </template>
      <template #dataScope="row">
        {{ enumType("dataScopeEnum", row.dataScope) }}
      </template>
      <template #operation="row">
        <el-button
          link
          type="primary"
          @click="openPopover('edit', getRoleDetailApi, row.id)"
        >
          编辑
        </el-button>
        <el-button link type="danger" @click="removeFn(deleteRoleApi, row.id)">
          删除
        </el-button>
      </template>
    </ProTable>
    <Drawer
      v-model="addOrEdit"
      :title="popoverTitle"
      @closePopover="closePopover"
      :disabled="disabled"
      @submit="submit({ addSubmitApi: addRoleApi, editSubmitApi: editRoleApi })"
    >
      <template #default>
        <ProForm
          ref="proFormRef"
          v-model="dataForm"
          :formColumns="formColumns"
          :disabled="disabled"
        >
          <template #menuIds>
            <el-tree
              ref="treeRef"
              :data="treeData"
              :props="defaultProps"
              show-checkbox
              node-key="id"
              :default-checked-keys="defaultCheckedKeys"
              @check="handleCheckChange"
            />
          </template>
        </ProForm>
      </template>
    </Drawer>
  </div>
</template>

<script setup lang="ts" name="Role">
import { getMenuTreeApi } from "@/api/menu";
import {
  getRoleListApi,
  getRoleDetailApi,
  addRoleApi,
  editRoleApi,
  deleteRoleApi,
} from "@/api/role";
import { useTable } from "@/hooks/useTable";
import { enumType } from "@/utils/Enum";
import { StatusEnum } from "@/utils/radioEnum";
import { ElTree } from "element-plus";

const {
  tableData,
  pageData,
  refreshFn,
  getList,
  searchParam,
  openPopover,
  addOrEdit,
  popoverTitle,
  closePopover,
  disabled,
  dataForm,
  proFormRef,
  submit,
  removeFn,
} = useTable({ api: getRoleListApi, title: "角色" });

const columns: ColumnProps[] = [
  { prop: "roleName", label: "角色名称" },
  { prop: "roleSort", label: "排序" },
  { prop: "dataScope", label: "数据权限类型", slot: true },
  { prop: "remark", label: "备注" },
  { prop: "operation", label: "操作", slot: true },
];
const formColumns: IFormColumnsProps[] = [
  { label: "角色名称", prop: "roleName", el: "input" },
  { label: "角色描述", prop: "remark", el: "textarea" },
  { label: "状态", prop: "status", el: "radio", radioList: StatusEnum },
  { label: "排序", prop: "roleSort", el: "number" },
  { label: "角色权限", prop: "menuIds", el: "slot" },
];

// 树状图
const treeData = await getMenuTreeApi().then(({ data }) => data);
const rootIdsWithChildren = treeData
  .filter(
    ({ parentId, children }: TKeyValue) => parentId == 0 && children.length > 0
  )
  .map((el: any) => el.id);
const defaultProps = { label: "menuName" };
// 树节点实例
const treeRef = ref<InstanceType<typeof ElTree>>();
// 点击树节点
const handleCheckChange = (_: any, val: any) => {
  const { checkedKeys, halfCheckedKeys } = val;
  dataForm.value.menuIds = [...checkedKeys, ...halfCheckedKeys];
};
const filterMenuIds = (menuIds: number[], rootIds: number[]): number[] =>
  menuIds?.filter((id) => !rootIds.includes(id)) ?? [];

// 默认选中的节点 (回显)
const defaultCheckedKeys = computed(() =>
  filterMenuIds(dataForm.value.menuIds, rootIdsWithChildren)
);
</script>
<style lang="scss" scoped></style>
