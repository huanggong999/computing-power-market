/** * 发票管理 */
<template>
  <div class="table-box">
    <Breadcrumb :router-list="routerList" />
    <div class="table-box breadcrumbTable card">
      <div class="top">
        <el-row :gutter="24">
          <el-col :span="18">
            <div class="left">
              <div class="tip c6">可开发票金额</div>
              <div class="mt15 flx-justify-between">
                <div class="money">{{ amountData.amount }}</div>
                <el-button
                  color="#D7E2F6"
                  round
                  style="
                    color: #3972fd;
                    font-size: 16px;
                    width: 120px;
                    height: 40px;
                    background: #d7e2f6;
                    border-radius: 30px 30px 30px 30px;
                  "
                  @click="requestInvoice"
                >
                  <!-- @click="toPage('/invoiceManagement/applyBilling')" -->
                  申请开票
                </el-button>
              </div>
              <div class="block mt30 flex">
                <div
                  class="block-item"
                  v-for="(item, index) in blockItem"
                  :key="index"
                >
                  <div class="tip">{{ item.title }}</div>
                  <div class="money mt14 fz32" :style="{ color: item.color }">
                    {{ item.money }}
                  </div>
                </div>
              </div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="right">
              <div class="flx-justify-between mb30">
                <div class="tip">发票信息与接收方式</div>
                <el-button
                  round
                  type="primary"
                  plain
                  @click="toPage('/invoiceManagement/manageInvoice')"
                >
                  去管理<el-icon class="el-icon--right"><ArrowRight /></el-icon>
                </el-button>
              </div>
              <div class="block">
                <div
                  class="block-item flex mb22"
                  v-for="(item, index) in invoice"
                  :key="index"
                >
                  <div class="block-item-left tip mr30">{{ item.title }}</div>
                  <div>{{ item.value }}</div>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
      <div class="detail mt42">
        <div class="flx-justify-betweet mb50">
          <TipText content="发票明细" />
        </div>
        <ProTable
          :IsRefresh="false"
          type="none"
          :columns="columns"
          :tableData="tableData"
          :page-data="pageData"
          :get-list="getList"
          :is-page="false"
        >
          <template #invoiceType="row">
            <div v-if="row.invoiceType == 1">增值税普通发票</div>
            <div v-if="row.invoiceType == 2">增值税专用发票</div>
          </template>
          <template #status="row">
            <div v-if="row.status == 1">开票中</div>
            <div v-if="row.status == 2">已开票</div>
            <div v-if="row.status == 3">开票失败</div>
          </template>
          <template #handle="row">
            <el-button
              link
              type="primary"
              v-if="row.proof"
              @click="downloadInvoice(row.proof)"
              >下载发票</el-button
            >
            <el-button link type="warning" @click="openDetail(row)"
              >详情</el-button
            >
          </template>
        </ProTable>
      </div>
    </div>
    <el-drawer
      v-model="drawerVisible"
      title="I am the title"
      :with-header="false"
    >
      <div class="detail-title">开票信息</div>
      <el-descriptions border class="mt30" size="large" :column="1">
        <el-descriptions-item label="申请时间">{{
          detail.applicationTime ? detail.applicationTime : "--"
        }}</el-descriptions-item>
        <el-descriptions-item label="发票抬头">{{
          detail.invoiceTitle ? detail.invoiceTitle : "--"
        }}</el-descriptions-item>
        <el-descriptions-item label="发票类型">{{
          detail.invoiceType == 1 ? "个人" : "企业"
        }}</el-descriptions-item>
        <el-descriptions-item label="发票介质">{{
          detail.medium ? detail.medium : "--"
        }}</el-descriptions-item>
        <el-descriptions-item label="发票号码">{{
          detail.invoiceNumber ? detail.invoiceNumber : "--"
        }}</el-descriptions-item>
        <el-descriptions-item label="发票金额">{{
          detail.amount ? detail.amount : "--"
        }}</el-descriptions-item>
        <el-descriptions-item label="公司名称">{{
          detail.companyName ? detail.companyName : "--"
        }}</el-descriptions-item>
        <el-descriptions-item label="公司地址">{{
          detail.companyAddress ? detail.companyAddress : "--"
        }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{
          detail.email ? detail.email : "--"
        }}</el-descriptions-item>
        <el-descriptions-item label="纳税人编号">{{
          detail.companyCode ? detail.companyCode : "--"
        }}</el-descriptions-item>
        <el-descriptions-item label="企业联系人名称">{{
          detail.companyContactName ? detail.companyContactName : "--"
        }}</el-descriptions-item>
        <el-descriptions-item label="企业联系人电话">{{
          detail.companyContactPhone ? detail.companyContactPhone : "--"
        }}</el-descriptions-item>
      </el-descriptions>
    </el-drawer>
  </div>
</template>

<script setup lang="ts" name="InvoiceManagement">
import {
  getInvoiceAmountListApi,
  getInvoiceEmailListApi,
  getInvoiceInfoApi,
  getInvoiceListApi,
} from "@/api/invoice";
import { useTable } from "@/hooks/useTable";
import { toPage } from "@/utils";

const amountData = ref<any>({});
const defaultEmail = ref();
const routerList = ref([
  { name: "主控台", path: "/console" },
  { name: "发票管理" },
]);

// 申请开票
const requestInvoice = async () => {
  const { data } = await getInvoiceInfoApi();
  if (data) return toPage("/invoiceManagement/applyBilling");
  ElMessage.error("请先完善发票抬头和邮箱");
  toPage("/invoiceManagement/manageInvoice");
};

const blockItem = computed(() => ({
  one: {
    title: "开票总额",
    money: amountData.value.totalAmount,
    color: "#000",
  },
  two: {
    title: "已开票金额",
    money: amountData.value.alreadyAmount,
    color: "#3BA46F",
  },
  three: {
    title: "暂不可开票金额",
    money: amountData.value.cannotAmount,
    color: "#FF4151",
  },
}));
const invoice = computed(() => ({
  one: { title: "抬头", value: "抬头" },
  two: { title: "发票类型", value: "增值税普通发票" },
  three: {
    title: "默认邮箱",
    value: defaultEmail.value ? defaultEmail.value : "--",
  },
}));
const columns: ColumnProps[] = [
  { prop: "applicationTime", label: "申请时间" },
  { prop: "invoiceTitle", label: "发票抬头" },
  { prop: "invoiceType", label: "发票类型", slot: true },
  { prop: "medium", label: "发票介质" },
  { prop: "invoiceNumber", label: "发票号码" },
  { prop: "amount", label: "发票金额" },
  { prop: "companyName", label: "公司名称" },
  { prop: "email", label: "邮箱" },
  { prop: "remark", label: "备注" },
  { prop: "status", label: "状态", slot: true },
  // todo:发票操作需要补充
  { prop: "handle", label: "操作", slot: true, fixed: "right" },
];
const typeEnum = [
  { label: "门店", value: "1" },
  { label: "旅行社", value: "2" },
];
const searchList: ColumnProps[] = [
  {
    prop: "type",
    label: "开票类型",
    isSearchLabel: true,
    search: { el: "select" },
    fieldNames: { label: "label", value: "value" },
    enum: typeEnum,
  },
];

const { tableData, pageData, getList, resetFn, searchFn, searchParam } =
  useTable({
    requestApi: getInvoiceListApi,
    requestAuto: false,
  });
const initPage = () => {
  // 获取开票金额
  getInvoiceAmountListApi().then((res) => {
    amountData.value = res.data;
  });
  // 获取默认邮箱
  getInvoiceEmailListApi().then((res) => {
    res.data.forEach((item: any) => {
      if (item.isDefault) {
        defaultEmail.value = item.email;
      }
    });
  });
  // 获取开票列表
  getList();
};
initPage();

// 取消开票
const cancelInvoice = () => {};
// 下载发票
const downloadInvoice = (proof: any) => {
  // 创建一个临时的URL
  window.open(proof);
};
// 查看详情
const detail = ref<any>({});
const drawerVisible = ref(false);
const openDetail = (row: any) => {
  detail.value = row;
  drawerVisible.value = true;
};
</script>
<style lang="scss" scoped>
.top {
  height: 270px;
  .tip {
    font-size: 20px;
    color: #999;
  }
  .c6 {
    color: #666 !important;
  }
  .fz32 {
    font-size: 32px !important;
  }
  .left {
    background: url("@/assets/userSideImage/invokableBg.png") no-repeat;
    background-size: 100% 100%;
    height: 100%;
    height: 270px;
    padding: 36px;

    .money {
      font-size: 44px;
      color: #3972fd;
    }

    .block {
      &-item {
        &:not(:last-child) {
          padding-right: 50px;
          position: relative;
          margin-right: 50px;
          ::before {
            content: "";
            position: absolute;
            right: 0;
            top: 50%;
            transform: translateY(-50%);
            width: 1px;
            height: 60px;
            background: #e5e5e5;
          }
        }
      }
    }
  }
  .right {
    width: 100%;
    height: 100%;
    border: 1px solid #e5e5e5;
    border-radius: 10px;
    padding: 24px 26px;
    .block {
      &-item {
        &-left {
          width: 80px;
        }
      }
    }
  }
}
.btn {
  &:hover {
    cursor: pointer;
  }
}
.detail-title {
  font-size: 24px;
  font-weight: bolder;
}
.big {
  font-size: 20px !important;
}
</style>
