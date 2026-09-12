<template>
  <div class="table-box">
    <ProTable
      :columns="columns"
      :tableData="tableData"
      :pageData="pageData"
      :refreshFn="refreshFn"
      :getList="getList"
      type="none"
    >
      <template #type="row">
        <el-tag :type="row.type === 1 ? 'primary' : 'success'">
          {{ enumType("creditContractTypeEnum", row.type) }}
        </el-tag>
      </template>
      <template #status="row">
        <el-tag
          v-if="row.type === 1"
          :type="enumTag('creditContractStatusTag', row.status)"
        >
          {{ enumType("creditContractStatusEnum", row.status) }}
        </el-tag>
        <el-tag
          v-if="row.type === 2"
          :type="enumTag('creditContractStatusTag', row.status)"
        >
          {{ enumType("creditContractAuditStatusEnum", row.status) }}
        </el-tag>
      </template>
      <template #uploadImg="row">
        <ImagePreview v-if="row.uploadImg" :src="row.uploadImg" />
        <span v-else> -- </span>
      </template>
      <template #signUploadImg="row">
        <el-link
          :href="row.signUploadImg"
          type="primary"
          v-if="row.signUploadImg"
        >
          查看合同
        </el-link>
        <span v-else> -- </span>
      </template>
      <template #operation="row">
        <div v-if="[1].includes(row.type)">
          <el-button
            v-if="[1].includes(row.status)"
            type="primary"
            link
            @click="openPopover('add', { id: row.id })"
          >
            发起合同
          </el-button>
          <el-button
            v-if="[2].includes(row.status)"
            type="primary"
            link
            @click="revoke(row.id)"
          >
            撤回合同
          </el-button>
          <el-link
            :href="row.fileDownloadUrl"
            type="primary"
            v-if="[3].includes(row.status)"
          >
            查看合同
          </el-link>
          <span v-if="![1, 2, 3].includes(row.status)"> -- </span>
        </div>
        <div v-if="[2].includes(row.type)">
          <el-button
            v-if="[1].includes(row.status)"
            type="primary"
            link
            @click="openUpload(row.id)"
          >
            上传扫描件
          </el-button>
          <el-button
            v-if="[2].includes(row.status)"
            type="primary"
            link
            @click="verify(row.id, 3)"
          >
            确认归档
          </el-button>
          <el-button
            v-if="[2].includes(row.status)"
            type="primary"
            link
            @click="verify(row.id, 6)"
          >
            驳回
          </el-button>
          <el-button
            v-if="[1].includes(row.status)"
            type="primary"
            link
            @click="verify(row.id, 5)"
          >
            驳回
          </el-button>
          <span v-if="![1, 2].includes(row.status)"> -- </span>
        </div>
      </template>
    </ProTable>

    <el-dialog
      v-model="addOrEdit"
      title="发起合同"
      width="30%"
      center
      :destroy-on-close="true"
      :before-close="closePopover"
    >
      <ProForm
        ref="proFormRef"
        v-model="dataForm"
        :formColumns="formColumns"
        :label-width="100"
      />
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closePopover">取消</el-button>
          <el-button type="primary" @click="send"> 确定 </el-button>
        </div>
      </template>
    </el-dialog>
    <el-dialog width="30%" v-model="verifyVisible" title="驳回备注" center>
      <ProForm
        class="mt10"
        v-model="verifyParams"
        :formColumns="verifyFormColumns"
      />
      <template #footer>
        <el-button type="primary" @click="rejectFn"> 确认驳回 </el-button>
      </template>
    </el-dialog>
    <el-dialog width="30%" v-model="uploadVisible" title="上传扫描件" center>
      <ProForm
        class="mt10"
        v-model="verifyParams"
        :formColumns="uploadFormColumns"
        :labelWidth="100"
      >
        <template #uploadImg>
          <div style="width: calc(100% - 100px)">
            <UploadFile
              v-model:fileUrl="verifyParams.uploadImg"
              :fileType="fileType"
            >
              <template #tip>
                <div class="tac">请上传.pdf 标准格式文件，大小不超过5MB</div>
              </template>
            </UploadFile>
          </div>
        </template>
      </ProForm>
      <template #footer>
        <el-button type="primary" @click="confirm"> 确认上传 </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts" name="CreditLimitContract">
import {
  creditContractPageApi,
  revokeContractApi,
  sendContractApi,
  uploadContractApi,
  verifyContractApi,
} from "@/api/userCenter";
import { useTable } from "@/hooks/useTable";
import { enumType } from "@/utils/Enum";
import { enumTag } from "@/utils/enumTag";
import { ElMessageBox } from "element-plus";
const {
  tableData,
  pageData,
  refreshFn,
  getList,
  addOrEdit,
  closePopover,
  openPopover,
  dataForm,
  proFormRef,
} = useTable({
  api: creditContractPageApi,
});
const columns: ColumnProps[] = [
  { prop: "customerName", label: "客户名称", width: 130 },
  { prop: "customerPhone", label: "手机号", width: 120 },
  { prop: "contractNo", label: "合同编号", width: 160 },
  { prop: "completeTime", label: "完成时间", width: 170 },
  { prop: "zq", label: "账期（天）", width: 100 },
  { prop: "status", label: "状态", slot: true, width: 130 },
  { prop: "type", label: "签署介质", width: 100, slot: true },
  { prop: "remark", label: "备注", width: 120 },
  { prop: "amount", label: "签署金额", width: 120 },
  {
    prop: "userType",
    label: "用户类型",
    width: 100,
    value: (row) => enumType("creditContractUserTypeEnum", row.userType),
  },
  { prop: "clientContactPerson", label: "甲方名称", width: 120 },
  { prop: "clientContactPersonName", label: "甲方联系人", width: 120 },
  { prop: "clientContactPhone", label: "甲方联系电话", width: 120 },
  { prop: "uploadImg", label: "合同文件", slot: true, width: 90 },
  { prop: "signUploadImg", label: "已签署扫描件", slot: true, width: 120 },
  { prop: "verifyRemark", label: "审核备注", width: 120 },
  { prop: "operation", label: "操作", slot: true, width: 150, fixed: "right" },
];
const formColumns: IFormColumnsProps[] = [
  { label: "合同金额", prop: "amount", el: "price" },
  { label: "账期(天)", prop: "zq", el: "number" },
];

const send = () => {
  if (!proFormRef.value.formRef) return;
  proFormRef.value.formRef.validate(async (valid: boolean) => {
    if (!valid) return false;
    await sendContractApi(dataForm.value);
    ElMessage.success("操作成功");
    getList();
    closePopover();
  });
};
//#region 上传扫描件
const uploadVisible = ref(false);
const uploadFormColumns: IFormColumnsProps[] = [
  { label: "合同金额", prop: "amount", el: "price" },
  { label: "账期(天)", prop: "zq", el: "number" },
  { label: "签署扫描件", prop: "uploadImg", el: "slot" },
];

const openUpload = (id: string) => {
  verifyParams.value.id = id;
  uploadVisible.value = true;
};
const confirm = async () => {
  await uploadContractApi(verifyParams.value);
  getList();
  ElMessage.success("操作成功");
  uploadVisible.value = false;
};
const fileType = ["application/pdf"];
//#endregion

//#region
const verifyParams = ref<TKeyValue>({});
const verifyVisible = ref(false);
const verifyFormColumns: IFormColumnsProps[] = [
  { label: "备注", prop: "verifyRemark", el: "textarea" },
];
const verifyAPIFn = async () => {
  await verifyContractApi(verifyParams.value);
  ElMessage.success("操作成功");
  getList();
};
const verifyEnum = {
  3: verifyAPIFn,
  5: () => (verifyVisible.value = true),
  6: () => (verifyVisible.value = true),
};
type TVerifyEnum = keyof typeof verifyEnum;
const rejectFn = () => {
  if (!verifyParams.value.verifyRemark) {
    ElMessage.error("请输入审核意见");
    return;
  }
  verifyAPIFn();
  verifyVisible.value = false;
};
const verify = async (id: string, status: TVerifyEnum) => {
  verifyParams.value.id = id;
  verifyParams.value.status = status;
  verifyEnum[status]();
};
watch([() => verifyVisible.value, () => uploadVisible.value], (val) => {
  if (!val) verifyParams.value = {};
});

//#endregion

// 撤回合同
const revoke = (id: string) => {
  ElMessageBox.confirm("确认撤回合同？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(async () => {
    await revokeContractApi({ id });
    ElMessage.success("撤回成功");
    getList();
  });
};
</script>
<style lang="scss" scoped></style>
