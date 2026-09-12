<template>
  <div class="table-box">
    <ProTable
      :columns="columns"
      :tableData="tableData"
      :refreshFn="refreshFn"
      :pageData="pageData"
      :getList="getList"
      :searchParam="searchParam"
      :searchFn="searchFn"
      :resetFn="resetFn"
    >
      <template #tableHeader>
        <el-button @click="openPopover('add', { status: 'OK' })">
          新增
        </el-button>
      </template>
      <template #avatar="row">
        <ImagePreview :src="row.avatar" />
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
          :disabled="row.id == '1'"
          @click="openPasswordPopover(row.id)"
        >
          更新密码
        </el-button>
        <el-button
          link
          type="primary"
          :disabled="row.id == '1'"
          @click="editFn(row.id)"
        >
          编辑
        </el-button>
        <el-button
          link
          type="danger"
          :disabled="row.id == '1'"
          @click="removeFn(delAccountApi, row.id)"
        >
          删除
        </el-button>
      </template>
    </ProTable>
    <Drawer
      :title="popoverTitle"
      v-model="addOrEdit"
      :disabled="disabled"
      @closePopover="closePopover"
      @submit="
        submit({ addSubmitApi: addAccountApi, editSubmitApi: editAccountApi })
      "
    >
      <ProForm
        ref="proFormRef"
        v-model="dataForm"
        :formColumns="formColumns"
        :label-width="100"
        :disabled="disabled"
      >
        <template #password>
          <el-input
            v-model="dataForm.password"
            placeholder="请输入密码"
            type="password"
            show-password
            :disabled="!!dataForm.id"
          />
        </template>
      </ProForm>
    </Drawer>

    <el-dialog
      v-model="updatePasswordVisible"
      title="修改密码"
      width="30%"
      center
      destroy-on-close
    >
      <ProForm
        v-model="updatePasswordParams"
        :formColumns="updatePasswordFormColumns"
        :disabled="disabled"
      />
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="updatePasswordVisible = false">取消</el-button>
          <el-button type="primary" @click="updatePassword"> 确定 </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts" name="Account">
import {
  addAccountApi,
  delAccountApi,
  editAccountApi,
  getAccountDetailApi,
  getAccountListApi,
} from "@/api/account";
import { getRoleListApi } from "@/api/role";
import { useTable } from "@/hooks/useTable";
import { checkPhoneNumber } from "@/utils/eleValidate";
import { enumType } from "@/utils/Enum";
import { enumTag } from "@/utils/enumTag";
import { sexRadioEnum, StatusEnum } from "@/utils/radioEnum";
import { statusSelectEnum } from "@/utils/selectEnum";

const {
  searchParam,
  getList,
  refreshFn,
  pageData,
  tableData,
  removeFn,
  popoverTitle,
  addOrEdit,
  disabled,
  closePopover,
  dataForm,
  openPopover,
  submit,
  proFormRef,
  searchFn,
  resetFn,
} = useTable({ api: getAccountListApi, title: "账号" });
const columns: ColumnProps[] = [
  { prop: "avatar", label: "头像", slot: true },
  { prop: "userName", label: "用户名" },
  { prop: "nickName", label: "昵称" },
  { prop: "phone", label: "手机号" },
  { prop: "sex", label: "性别", value: (row) => enumType("sexEnum", row.sex) },
  {
    prop: "status",
    label: "状态",
    slot: true,
    search: { el: "select" },
    enum: statusSelectEnum,
  },
  { prop: "remark", label: "描述" },
  { prop: "operation", label: "操作", slot: true, width: 230 },
];

const initFormCol: IFormColumnsProps[] = [
  { label: "头像", prop: "avatar", el: "img" },
  { label: "昵称", prop: "nickName", el: "input" },
  { label: "用户名", prop: "userName", el: "input" },
  { label: "手机号", prop: "phone", el: "phone", validator: checkPhoneNumber },
  { label: "性别", prop: "sex", el: "radio", radioList: sexRadioEnum },
  {
    label: "角色",
    prop: "roleId",
    el: "selectPage",
    selectLabel: "roleName",
    selectValue: "id",
    pageFn: getRoleListApi,
  },

  { label: "状态", prop: "status", el: "radio", radioList: StatusEnum },
  { label: "密码", prop: "password", el: "slot" },
  { label: "描述", prop: "memo", el: "textarea", required: false },
];

const updateFormColumns = computed(() =>
  initFormCol.filter((item) => item.label !== "密码")
);
const formColumns = computed(() =>
  !!dataForm.value.id ? updateFormColumns.value : initFormCol
);

// 编辑
const editFn = async (id: string) => {
  const { data } = await getAccountDetailApi(id);
  const { role, ...rest } = data;
  openPopover("edit", { ...rest, roleId: role.roleId });
};

// 更新密码
const updatePasswordVisible = ref(false);

const updatePasswordParams = ref();
const updatePasswordFormColumns: IFormColumnsProps[] = [
  { label: "密码", prop: "password", el: "password" },
];

const openPasswordPopover = async (id: string) => {
  const { data } = await getAccountDetailApi(id);
  updatePasswordParams.value = data;
  updatePasswordVisible.value = true;
};
const updatePassword = async () => {
  if (!updatePasswordParams.value.password)
    return ElMessage.error("密码不能为空");
  await editAccountApi(updatePasswordParams.value);
  ElMessage.success("操作成功");
  getList();
  updatePasswordVisible.value = false;
};
</script>
<style lang="scss" scoped></style>
