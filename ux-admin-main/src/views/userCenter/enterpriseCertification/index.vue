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
      type="none"
    >
      <template #companyImg="row">
        <ImagePreview :src="row.companyImg" />
      </template>
      <template #companyStatus="row">
        <el-tag
          :type="enumTag('enterpriseCertificationStatusTag', row.companyStatus)"
        >
          {{ enumType("enterpriseCertificationStatusEnum", row.companyStatus) }}
        </el-tag>
      </template>
      <template #operation="row">
        <div v-if="[2].includes(row.companyStatus)">
          <el-button link type="primary" @click="verifyFn(row.id, 3)">
            通过
          </el-button>
          <el-button link type="danger" @click="verifyFn(row.id, 4)">
            拒绝
          </el-button>
        </div>
        <span v-if="[3, 4].includes(row.companyStatus)"> 已审核 </span>
      </template>
    </ProTable>

    <el-dialog v-model="verifyVisible" width="20%" title="驳回理由" center>
      <el-input
        type="textarea"
        v-model="verifyForm.companyVerifyRemark"
        placeholder="请输入驳回理由"
      />
      <template #footer>
        <el-button type="primary" @click="submit"> 确定 </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts" name="EnterpriseCertification">
import {
  enterpriseCertificationVerifyApi,
  getEnterpriseCertificationListApi,
} from "@/api/userCenter";
import { useHandleData } from "@/hooks/useHandleData";
import { useTable } from "@/hooks/useTable";
import { enumType } from "@/utils/Enum";
import { enumTag } from "@/utils/enumTag";
import { enterpriseCertificationStatusSelectEnum } from "@/utils/selectEnum";

const {
  tableData,
  refreshFn,
  pageData,
  getList,
  searchParam,
  searchFn,
  resetFn,
} = useTable({ api: getEnterpriseCertificationListApi });

const columns: ColumnProps[] = [
  {
    label: "企业名称",
    prop: "companyName",
    search: { el: "input" },
    width: 220,
  },
  { label: "企业统一社会信用代码", prop: "companyCode", width: 200 },
  { label: "企业注册地址", prop: "companyAddress", width: 260 },
  { label: "企业营业执照", prop: "companyImg", slot: true, width: 123 },
  { label: "企业联系人名称", prop: "companyContactName", width: 140 },
  { label: "企业联系人电话", prop: "companyContactPhone", width: 140 },
  {
    label: "企业审核状态",
    prop: "companyStatus",
    slot: true,
    search: { el: "select" },
    enum: enterpriseCertificationStatusSelectEnum,
    width: 120,
  },
  { label: "企业审核备注", prop: "companyVerifyRemark", width: 140 },
  { label: "企业审核时间", prop: "companyVerifyTime", width: 180 },
  { label: "操作", prop: "operation", slot: true, width: 120 },
];

const verifyVisible = ref(false);
const verifyForm = ref<TKeyValue>({});
const verifyEnum = {
  3: () => submit(),
  4: () => (verifyVisible.value = true),
};
const verifyFn = (id: string, status: 3 | 4) => {
  verifyForm.value.id = id;
  verifyForm.value.companyStatus = status;
  verifyEnum[status]();
};

const submit = async () => {
  const typeEnum = { 3: "通过", 4: "拒绝" };
  const msg = ` ${
    typeEnum[verifyForm.value.companyStatus as keyof typeof typeEnum]
  }`;
  await useHandleData(enterpriseCertificationVerifyApi, verifyForm.value, msg);
  verifyVisible.value = false;
  getList();
};
watch(
  () => verifyVisible.value,
  (val) => {
    if (!val) {
      delete verifyForm.value.companyVerifyRemark;
    }
  }
);
</script>
<style lang="scss" scoped></style>
