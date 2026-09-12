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
      <template #status="row">
        <el-tag :type="enumTag('remitStatusTag', row.status)">
          {{ enumType("remitStatusEnum", row.status) }}
        </el-tag>
      </template>
      <template #operation="row">
        <el-button
          v-if="[1, 3].includes(row.status)"
          type="primary"
          link
          @click="paymentFn(row.id, row.customerName)"
        >
          打款
        </el-button>
        <span v-if="[2].includes(row.status)"> -- </span>
      </template>
    </ProTable>
  </div>
</template>

<script setup lang="ts" name="PublicPayment">
import { remitPageApi, remitVerifyApi } from "@/api/financialCenter";
import { useHandleData } from "@/hooks/useHandleData";
import { useTable } from "@/hooks/useTable";
import { enumType } from "@/utils/Enum";
import { enumTag } from "@/utils/enumTag";
import { remitStatusSelectEnum } from "@/utils/selectEnum";

const {
  tableData,
  pageData,
  searchParam,
  refreshFn,
  getList,
  searchFn,
  resetFn,
} = useTable({ api: remitPageApi });

const columns: ColumnProps[] = [
  { prop: "customerName", label: "用户账号", width: 130 },
  { prop: "phone", label: "用户手机号", width: 130 },
  {
    prop: "bankType",
    label: "银行类型",
    value: (row) => enumType("remitBankTypeEnum", row.bankType),
    width: 100,
  },
  { prop: "bankName", label: "银行名称", width: 160 },
  { prop: "account", label: "开户名称", width: 203 },
  { prop: "bankNo", label: "银行卡号", width: 160 },
  { prop: "swiftCode", label: "SWIFT Code", width: 180 },
  { prop: "amount", label: "打款金额", width: 100 },
  {
    prop: "remitName",
    label: "汇款主体名",
    search: { el: "input" },
    width: 130,
  },
  { prop: "createTime", label: "创建时间", width: 170 },
  {
    prop: "status",
    label: "状态",
    slot: true,
    width: 100,
    search: { el: "select" },
    enum: remitStatusSelectEnum,
  },
  { prop: "operation", label: "操作", slot: true, width: 80 },
];

const paymentForm = ref({ id: "", status: 2 });
const paymentFn = async (id: string, name: string) => {
  paymentForm.value.id = id;
  let msg = `对账号为【${name}】的打款申请进行打款操作`;
  await useHandleData(remitVerifyApi, paymentForm.value, msg);
  getList();
};
</script>
<style lang="scss" scoped></style>
