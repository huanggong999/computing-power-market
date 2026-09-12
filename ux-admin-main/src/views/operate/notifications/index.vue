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
        <el-button @click="readAllMessage"> 一键已读 </el-button>
      </template>
      <template #status="row">
        <el-tag :type="enumTag('messageStatusTag', row.status)">
          {{ enumType("messageStatusEnum", row.status) }}
        </el-tag>
      </template>
      <template #operation="row">
        <el-button
          type="primary"
          link
          @click="handleClick(row.msgType, row.id)"
        >
          查看
        </el-button>
      </template>
    </ProTable>
  </div>
</template>

<script setup lang="ts" name="Notifications">
import { getMessagePageApi, readAllMessageApi, readMessageApi } from "@/api";
import { useTable } from "@/hooks/useTable";
import { useKeepAlive } from "@/store";
import { enumType } from "@/utils/Enum";
import { enumTag } from "@/utils/enumTag";
import { messageStatusSelectEnum } from "@/utils/selectEnum";

const {
  tableData,
  pageData,
  searchParam,
  refreshFn,
  getList,
  searchFn,
  resetFn,
} = useTable({ api: getMessagePageApi, title: "消息通知" });

const columns: ColumnProps[] = [
  {
    prop: "msgType",
    label: "消息类型",
    value: (row) => enumType("messageTypeEnum", row.msgType),
  },
  { prop: "text", label: "消息说明" },
  {
    prop: "status",
    label: "状态",
    slot: true,
    search: { el: "select" },
    enum: messageStatusSelectEnum,
  },
  { prop: "createTime", label: "创建时间" },
  { prop: "operation", label: "操作", slot: true },
];
const readAllMessage = async () => {
  await readAllMessageApi();
  refreshFn();
};

// 消息类型 1  收到客户表单   2  收到客户购买表达  3 推广申请  4 发票申请  5  合同申请 6 IP库预警  7 自建服务器工单 8 续费通知  9 升级通知
const map = [
  {},
  { name: "客户表单", url: "/productManagement/formDataRecovery" },
  { name: "购买表单", url: "/productManagement/consultPurchase" },
  { name: "推广申请", url: "/promotionManagement/promotionAmbassador" },
  { name: "发票申请", url: "/financialCenter/invoicingManagement" },
  { name: "合同申请", url: "/financialCenter/orderContract" },
  { name: "IP库预警", url: "/productManagement/productList" },
  { name: "自建服务器工单", url: "/order/serverTicket" },
  { name: "续费通知", url: "/productManagement/purchaseOrder" },
  { name: "升级通知", url: "/productManagement/purchaseOrder" },
];
const route = useRoute();
const router = useRouter();
const keepAliveStore = useKeepAlive();
const handleClick = (msgType: number, id: string) => {
  //   跳转页面并打开
  router.push({ path: map[msgType].url }).then(() => {
    if (!route.meta.noCache) {
      keepAliveStore.removeKeepAliveName(route.name as string);
      nextTick(() => {
        keepAliveStore.addKeepAliveName(route.name as string);
      });
    }
  });
  // 已读消息
  readMessage(id);
};
// 已读消息
const readMessage = async (msgId: string) => await readMessageApi(msgId);
</script>
<style lang="scss" scoped></style>
