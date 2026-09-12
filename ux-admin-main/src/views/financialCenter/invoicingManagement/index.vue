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
      :isPage="false"
      type="none"
    >
      <template #customer="row">
        <div>
          <div class="flex">
            <div class="label">id ：</div>
            {{ row.customerId ?? "--" }}
          </div>
          <div class="flex">
            <div class="label">名称 ：</div>
            {{ row.customerName ?? "--" }}
          </div>
          <div class="flex">
            <div class="label">手机号 ：</div>
            {{ row.phone ?? "--" }}
          </div>
        </div>
      </template>
      <template #proof="row">
        <el-link :href="row.proof ? row.proof : ''" type="primary">
          {{ row.proof ? "下载发票" : "未开票" }}
        </el-link>
      </template>
      <template #status="row">
        <div class="flx-center">
          <el-tag :type="enumTag('statusAuditTag', row.status)">
            {{ enumType("statusAuditEnum", row.status) }}
          </el-tag>
          <el-tooltip
            v-if="row.status === 3"
            :content="row.remark"
            placement="bottom"
          >
            <el-icon class="ml5"><QuestionFilled /></el-icon>
          </el-tooltip>
        </div>
      </template>
      <template #operation="row">
        <div v-if="row.status === 1">
          <el-button link type="primary" @click="handleInvoice(row.id, 2)">
            已开票
          </el-button>
          <el-button link type="primary" @click="handleInvoice(row.id, 1)">
            开票失败
          </el-button>
        </div>
        <el-button link type="primary" @click="openPopover('check', row)">
          详情
        </el-button>
      </template>
    </ProTable>

    <Drawer
      v-model="addOrEdit"
      :title="popoverTitle"
      :IsFooter="false"
      @closePopover="closePopover"
    >
      <Descriptions title="开票信息" :list="list">
        <template #extra>
          <el-tag>
            {{ enumType("invoiceStatusEnum", dataForm.status) }}
          </el-tag>
        </template>
      </Descriptions>

      <Descriptions class="mt20" title="联系人信息" :list="contactNameList" />
      <Descriptions
        class="mt20"
        v-if="dataForm.status === 3"
        title="备注"
        :list="remarkList"
      />
      <Descriptions
        v-if="dataForm.status === 2"
        class="mt20"
        title="发票凭证"
        :list="proofList"
      >
        <template #proof>
          <el-link :href="dataForm.proof" type="primary"> 下载发票 </el-link>
        </template>
      </Descriptions>
    </Drawer>

    <el-dialog
      v-model="invoiceVisible"
      width="20%"
      :title="invoiceTitle"
      center
    >
      <el-input
        type="textarea"
        v-if="invoiceForm.type === 1"
        v-model="invoiceForm.remark"
        placeholder="请输入失败理由"
      />
      <UploadFile
        v-if="invoiceForm.type === 2"
        v-model:fileUrl="invoiceForm.proof"
        :fileType="fileType"
      >
        <template #tip>
          <div class="tac mt10">请上传.pdf 标准格式文件，大小不超过5MB</div>
        </template>
      </UploadFile>
      <template #footer>
        <el-button type="primary" @click="submit"> 确定 </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts" name="InvoicingManagement">
import {
  getInvoicingManagementListApi,
  getInvoicingManagementOperationApi,
} from "@/api/invoicingManagement";
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
  openPopover,
  dataForm,
  popoverTitle,
  addOrEdit,
  closePopover,
} = useTable({
  api: getInvoicingManagementListApi,
  title: "详情",
  isPage: false,
});

const columns: ColumnProps[] = [
  {
    prop: "applicationTime",
    label: "申请时间",
    search: {
      el: "date-picker",
      dateType: "daterange",
      dateEnum: ["startDay", "endDay"],
      valueFormat: "YYYY-MM-DD",
    },
    width: 170,
  },
  { prop: "customer", label: "发票申请用户信息", width: 180, slot: true },
  { prop: "invoiceTitle", label: "发票抬头", width: 170 },
  {
    prop: "invoiceType",
    label: "发票类型",
    value: (row) => enumType("invoiceTypeEnum", row.invoiceType),
    width: 130,
  },
  { prop: "medium", label: "发票介质", width: 100 },
  { prop: "invoiceNumber", label: "发票号码", width: 200 },
  { prop: "amount", label: "发票金额", width: 120 },
  { prop: "status", label: "状态", slot: true, width: 100 },
  { prop: "companyName", label: "公司名称", width: 230 },
  { prop: "email", label: "邮箱", width: 183 },
  { prop: "proof", label: "发票凭证", slot: true, width: 90 },
  { prop: "operation", label: "操作", slot: true, width: 150, fixed: "right" },
];

const invoiceVisible = ref(false);
const invoiceTitle = computed(() =>
  invoiceForm.value.type === 1 ? "开票失败" : "开票成功"
);
const invoiceForm = ref<TKeyValue>({ id: "", type: 1 });

const handleInvoice = (id: string, status: 1 | 2) => {
  invoiceForm.value.id = id;
  invoiceForm.value.type = status;
  invoiceVisible.value = true;
};

const fileType = ["application/pdf"];

const submit = async () => {
  const { type, remark, proof } = invoiceForm.value;
  const errorMessages = { 1: "请输入失败理由", 2: "请上传发票凭证" };
  if ((type === 1 && !remark) || (type === 2 && !proof))
    return ElMessage.error(errorMessages[type as keyof typeof errorMessages]);
  invoiceVisible.value = false;
  await getInvoicingManagementOperationApi(invoiceForm.value);
  ElMessage.success("操作成功");
  getList();
};
watch(
  () => invoiceVisible.value,
  (val) => {
    if (!val) {
      delete invoiceForm.value.remark;
      delete invoiceForm.value.proof;
    }
  }
);

const list = ref<IDescriptionsItem[]>([
  { label: "申请时间", value: "" },
  { label: "用户id", value: "" },
  { label: "用户名", value: "" },
  { label: "手机号", value: "" },
  { label: "发票抬头", value: "" },
  { label: "联系人电话", value: "" },
  { label: "发票类型", value: "" },
  { label: "发票介质", value: "" },
  { label: "发票号码", value: "" },
  { label: "发票金额", value: "" },
  { label: "公司地址", value: "" },
  { label: "邮箱", value: "" },
  { label: "纳税人编号", value: "" },
  // { label: "企业联系人名称", value: "" },
  // { label: "企业联系人电话", value: "" },
  // { label: "联系人名称", value: "" },
  // { label: "联系人电话", value: "" },
]);

const remarkList = ref<IDescriptionsItem[]>([{ label: "备注", value: "" }]);
const proofList = ref<IDescriptionsItem[]>([
  { label: "发票凭证", value: "proof", slot: true },
]);
const contactNameList = ref<IDescriptionsItem[]>([]);

watchEffect(() => {
  const {
    applicationTime,
    customerId,
    customerName,
    phone,
    invoiceTitle,
    invoiceType,
    medium,
    invoiceNumber,
    amount,
    remark,
    proof,
    companyAddress,
    email,
    companyCode,
    companyContactName,
    companyContactPhone,
    contactPhone,
  } = dataForm.value;
  const map = {
    申请时间: applicationTime,
    用户id: customerId,
    用户名: customerName,
    手机号: phone,
    发票抬头: invoiceTitle,
    联系人电话: contactPhone,
    发票类型: enumType("invoiceTypeEnum", invoiceType),
    发票介质: medium,
    发票号码: invoiceNumber,
    发票金额: amount,
    备注: remark,
    发票凭证: proof,
    公司地址: companyAddress,
    邮箱: email,
    纳税人编号: companyCode,
    [invoiceTitle === "个人" ? "联系人名称" : "企业联系人名称"]:
      companyContactName,
    [invoiceTitle === "个人" ? "联系人电话" : "企业联系人电话"]:
      companyContactPhone,
  };
  contactNameList.value = [
    {
      label: invoiceTitle === "个人" ? "联系人名称" : "企业联系人名称",
      value: "",
    },
    {
      label: invoiceTitle === "个人" ? "联系人电话" : "企业联系人电话",
      value: "",
    },
  ];
  [
    list.value,
    contactNameList.value,
    remarkList.value,
    proofList.value,
  ].forEach((item) => {
    console.log("🚀 ~ item:", item);
    descriptionEnum(item, map);
  });
});
</script>
<style lang="scss" scoped>
.flex {
  display: flex;
  .label {
    width: 65px;
    flex-shrink: 0;
    text-align: right;
  }
}
</style>
