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
      <template #userInfo="row">
        <div>{{ row.userName }}</div>
        <div>{{ row.userPhone }}</div>
      </template>
      <template #bankInfo="row">
        <div>真实姓名:{{ row.bankUserName }}</div>
        <div>开户银行:{{ row.bank }}</div>
        <div>银行卡号:{{ row.bankNo }}</div>
      </template>
      <template #status="row">
        <div class="flex-center">
          <el-tag :type="enumTag('statusWithdrawalTag', row.status)">
            {{ enumType("statusWithdrawalEnum", row.status) }}
          </el-tag>
          <el-tooltip
            v-if="row.status === 3"
            :content="row.verifyRemark"
            placement="bottom"
          >
            <el-icon class="ml5"><QuestionFilled /></el-icon>
          </el-tooltip>
        </div>
      </template>
      <template #operation="row">
        <template v-for="item in operationBtn">
          <el-button
            link
            type="primary"
            v-if="item.show!(row)"
            @click="item.click(row)"
          >
            {{ item.label }}
          </el-button>
        </template>
      </template>
    </ProTable>

    <Drawer
      v-model="addOrEdit"
      :title="popoverTitle"
      :IsFooter="false"
      @closePopover="closePopover"
    >
      <Descriptions title="账户信息" :list="userList" :column="2">
        <template #extra>
          <el-tag>
            {{ enumType("statusWithdrawalEnum", dataForm.status) }}
          </el-tag>
        </template>
      </Descriptions>
      <Descriptions
        class="mt15 mb15"
        title="审核信息"
        :list="auditList"
        :column="2"
      />
      <Descriptions title="收款账户信息" :list="receivingAccountList" />
      <Descriptions
        v-if="dataForm.status === 3"
        class="mt15"
        title="审核"
        :list="verifyList"
      />
      <div class="mt15" v-if="dataForm.status === 4">
        <h3>凭证照片</h3>
        <ImagePreview :src="dataForm.withImage" :width="200" :height="100" />
      </div>
    </Drawer>

    <el-dialog v-model="verifyVisible" width="20%" :title="verifyTitle" center>
      <el-input
        type="textarea"
        v-if="verifyForm.status === 3"
        v-model="verifyForm.verifyRemark"
        placeholder="请输入驳回理由"
      />
      <div class="upload" v-if="verifyForm.status === 4">
        <UploadImg v-model:imageUrl="verifyForm.withImage" />
      </div>
      <template #footer>
        <el-button type="primary" @click="submit"> 确定 </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts" name="WithdrawalApplication">
import {
  promotionWithdrawalFirstPageApi,
  promotionWithdrawalVerifyApi,
} from "@/api/promotionManagement";
import { useTable } from "@/hooks/useTable";
import { descriptionEnum } from "@/utils/commFun";
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
  dataForm,
  openPopover,
  addOrEdit,
  popoverTitle,
  closePopover,
} = useTable({ api: promotionWithdrawalFirstPageApi, title: "详情" });
const columns: ColumnProps[] = [
  { prop: "orderNo", label: "提现单号", width: 200 },
  { prop: "userId", label: "用户ID", width: 210 },
  { prop: "userInfo", label: "用户信息", slot: true, width: 200 },
  { prop: "name", label: "真实姓名", noShow: true, search: { el: "input" } },
  { prop: "bankInfo", label: "收款账户", slot: true, width: 250 },
  { prop: "totalAmount", label: "提现金额", width: 120 },
  { prop: "serviceRate", label: "代扣费率", width: 100 },
  { prop: "withdrawalAmount", label: "实际到账金额", width: 123 },
  {
    prop: "createTime",
    label: "提现时间",
    search: {
      el: "date-picker",
      dateType: "daterange",
      dateEnum: ["startTime", "endTime"],
      valueFormat: "YYYY-MM-DD",
    },
    width: 180,
  },
  { prop: "status", label: "提现状态", slot: true, width: 100 },
  { prop: "operation", label: "操作", slot: true, width: 160 },
];

// 操作按钮
const operationBtn: IOperationBtnItem[] = [
  {
    label: "通过",
    show: (row: any) => row.status === 1,
    click: (row: any) => handleAudit(row.id, 2),
  },
  {
    label: "驳回",
    show: (row: any) => row.status === 1,
    click: (row: any) => handleAudit(row.id, 3),
  },
  {
    label: "打款",
    show: (row: any) => row.status === 2,
    click: (row: any) => handleAudit(row.id, 4),
  },
  {
    label: "详情",
    show: () => true,
    click: (row: any) => openPopover("check", row),
  },
];

const verifyForm = ref<TKeyValue>({ id: "", status: 0 });
const verifyFn = async () => {
  await promotionWithdrawalVerifyApi(verifyForm.value);
  ElMessage.success("操作成功");
  getList();
};

const verifyEnum = {
  2: () => verifyFn(),
  3: () => (verifyVisible.value = true),
  4: () => (verifyVisible.value = true),
};
type THandleAudit = keyof typeof verifyEnum;
const handleAudit = (id: string, status: THandleAudit) => {
  verifyForm.value.id = id;
  verifyForm.value.status = status;
  verifyEnum[status]();
};
const verifyTitle = computed(() =>
  verifyForm.value.status === 3 ? "驳回理由" : "打款凭证"
);

const verifyVisible = ref(false);
// 审核
const submit = () => {
  const { status, verifyRemark, withImage } = verifyForm.value;
  const errorMessages = { 3: "请输入驳回理由", 4: "请上传打款凭证" };
  if ((status === 3 && !verifyRemark) || (status === 4 && !withImage))
    return ElMessage.error(errorMessages[status as keyof typeof errorMessages]);
  verifyVisible.value = false;
  verifyFn();
};

watch(
  () => verifyVisible.value,
  (val) => {
    if (!val) {
      delete verifyForm.value.verifyRemark;
      delete verifyForm.value.withImage;
    }
  }
);

const userList = ref<IDescriptionsItem[]>([
  { label: "用户名", value: dataForm.value.userName },
  { label: "手机号", value: dataForm.value.userPhone },
  { label: "用户ID", value: dataForm.value.userId },
]);
const auditList = ref<IDescriptionsItem[]>([
  { label: "提交时间", value: dataForm.value.createTime },
  { label: "审核时间", value: dataForm.value.verifyTime },
]);
const receivingAccountList = ref<IDescriptionsItem[]>([
  { label: "真实姓名", value: dataForm.value.bankUserName },
  { label: "身份证", value: dataForm.value.idCard },
  { label: "收款开户银行", value: dataForm.value.bank },
  { label: "银行卡号", value: dataForm.value.bankNo },
  { label: "开户地址", value: dataForm.value.address },
]);
const verifyList = ref<IDescriptionsItem[]>([
  { label: "驳回理由", value: dataForm.value.verifyRemark },
]);

watchEffect(() => {
  const map = {
    用户ID: dataForm.value.userId,
    用户名: dataForm.value.userName,
    手机号: dataForm.value.userPhone,
    提交时间: dataForm.value.createTime,
    审核时间: dataForm.value.verifyTime,
    真实姓名: dataForm.value.bankUserName,
    身份证: dataForm.value.idCard,
    收款开户银行: dataForm.value.bank,
    银行卡号: dataForm.value.bankNo,
    开户地址: dataForm.value.address,
    驳回理由: dataForm.value.verifyRemark,
  };

  [
    userList.value,
    auditList.value,
    receivingAccountList.value,
    verifyList.value,
  ].forEach((list) => descriptionEnum(list, map));
});
</script>
<style lang="scss" scoped>
.upload {
  margin: 10px auto;
  width: 150px;
}
</style>
