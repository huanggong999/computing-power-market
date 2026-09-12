<template>
  <div class="order-center-page" v-loading="loading">
    <section class="order-header">
      <div>
        <p class="eyebrow">费用与订单</p>
        <h1>订单管理</h1>
      </div>
      <div class="header-summary">
        <div class="summary-item">
          <span>订单总数</span>
          <strong>{{ pageData.total || 0 }}</strong>
        </div>
        <div class="summary-item success">
          <span>已支付</span>
          <strong>{{ statusCount.PAID }}</strong>
        </div>
        <div class="summary-item muted">
          <span>已取消/退款</span>
          <strong>{{ statusCount.CLOSED }}</strong>
        </div>
      </div>
    </section>

    <section class="filter-panel">
      <div class="quick-tabs">
        <el-radio-group v-model="monthRadio" @change="changeMonth">
          <el-radio-button label="本月" value="thisMonth" />
          <el-radio-button label="上月" value="lastMonth" />
        </el-radio-group>
        <el-radio-group v-model="statusTab" @change="changeStatusTab">
          <el-radio-button label="全部" value="" />
          <el-radio-button label="已支付" value="PAID" />
          <el-radio-button label="已取消" value="CANCELED" />
          <el-radio-button label="已退款" value="REFUNDED" />
        </el-radio-group>
      </div>

      <el-form class="filter-form" :model="searchParam" label-position="top">
        <el-form-item label="订单号">
          <el-input
            v-model="searchParam.orderNo"
            clearable
            placeholder="请输入订单号"
            @keyup.enter="searchFn"
          />
        </el-form-item>
        <el-form-item label="订单类型">
          <el-select v-model="searchParam.orderType" clearable placeholder="全部类型">
            <el-option
              v-for="item in orderTypeEnum"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="创建时间">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
            clearable
          />
        </el-form-item>
        <el-form-item class="filter-actions" label=" ">
          <el-button type="primary" :icon="Search" @click="searchFn">查询</el-button>
          <el-button :icon="RefreshLeft" @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </section>

    <section class="order-list-panel">
      <div class="list-toolbar">
        <div>
          <h2>订单列表</h2>
          <p>按云资源订单常用字段聚合展示，便于核对购买内容与支付状态。</p>
        </div>
        <el-button :icon="Refresh" @click="getList">刷新</el-button>
      </div>

      <el-empty v-if="!tableData.length" description="暂无订单" />

      <div v-else class="order-list">
        <article v-for="item in tableData" :key="item.orderNo" class="order-card">
          <div class="order-card-head">
            <div class="order-identity">
              <span class="order-no">订单号：{{ item.orderNo || '--' }}</span>
              <el-tag :type="enumType('orderStatusTag', item.orderStatus)" effect="light">
                {{ enumType('orderStatusEnum', item.orderStatus) }}
              </el-tag>
            </div>
            <span class="order-time">创建时间：{{ item.createTime || '--' }}</span>
          </div>

          <div class="order-card-body">
            <div class="product-cell">
              <div class="product-icon">
                <el-icon><Document /></el-icon>
              </div>
              <div>
                <h3>{{ productNames(item) }}</h3>
                <p>{{ enumType('orderTypeEnum', item.orderType) }}</p>
              </div>
            </div>

            <dl class="order-meta">
              <div>
                <dt>支付时间</dt>
                <dd>{{ item.payTime || '--' }}</dd>
              </div>
              <div>
                <dt>支付方式</dt>
                <dd>{{ paymentLabel(item) }}</dd>
              </div>
              <div>
                <dt>优惠抵扣</dt>
                <dd>{{ formatMoney(discountAmount(item)) }}</dd>
              </div>
            </dl>

            <div class="amount-cell">
              <span>实付金额</span>
              <strong>¥{{ formatMoney(item.finalPayAmount) }}</strong>
              <small>在线支付 ¥{{ formatMoney(item.onlinePayAmount) }}</small>
            </div>

            <div class="operation-cell">
              <el-button type="primary" plain @click="toDetail(item)">
                详情
                <el-icon class="el-icon--right"><ArrowRight /></el-icon>
              </el-button>
            </div>
          </div>
        </article>
      </div>

      <Pagination :pageData="pageData" :PageChange="getList" />
    </section>
  </div>
</template>

<script setup lang="ts" name="Order">
import { getOrderListAPI } from "@/api/order";
import { useTable } from "@/hooks/useTable";
import { enumType } from "@/utils/Enum";
import { orderTypeEnum } from "@/utils/radioEnum";
import { ArrowRight, Document, Refresh, RefreshLeft, Search } from "@element-plus/icons-vue";
import dayjs from "dayjs";
import { useRoute, useRouter } from "vue-router";

const route = useRoute();
const router = useRouter();
const loading = ref(false);
const statusTab = ref("");
const dateRange = ref<string[]>([]);
const { tableData, searchParam, searchFn: tableSearchFn, getList: requestList, pageData } =
  useTable({ requestApi: getOrderListAPI, requestAuto: false });

const getList = async () => {
  loading.value = true;
  try {
    await requestList();
  } finally {
    loading.value = false;
  }
};

const searchFn = () => {
  syncDateRangeToSearch();
  tableSearchFn();
};

const resetSearch = () => {
  searchParam.value = {};
  dateRange.value = [];
  monthRadio.value = "";
  statusTab.value = "";
  searchFn();
};

const initialOrderNo = String(route.query.orderNo || "");
if (initialOrderNo) searchParam.value.orderNo = initialOrderNo;
getList();

watch(
  () => route.query.orderNo,
  (orderNo) => {
    const value = String(orderNo || "");
    if (searchParam.value.orderNo === value) return;
    searchParam.value.orderNo = value;
    searchFn();
  }
);

watch(
  dateRange,
  (range) => {
    if (!range?.length) {
      searchParam.value.startTime = "";
      searchParam.value.endTime = "";
      monthRadio.value = "";
    }
  },
  { deep: true }
);

const monthRadio = ref("");

const monthMap = {
  thisMonth: [
    dayjs().startOf("month").format("YYYY-MM-DD HH:mm:ss"),
    dayjs().endOf("month").format("YYYY-MM-DD HH:mm:ss"),
  ],
  lastMonth: [
    dayjs().subtract(1, "month").startOf("month").format("YYYY-MM-DD HH:mm:ss"),
    dayjs().subtract(1, "month").endOf("month").format("YYYY-MM-DD HH:mm:ss"),
  ],
};

type TMonthMep = keyof typeof monthMap;
const getMatchingMonth = (startTime: string, endTime: string): string => {
  const { thisMonth, lastMonth } = monthMap;
  if (startTime === thisMonth[0] && endTime === thisMonth[1]) return "thisMonth";
  if (startTime === lastMonth[0] && endTime === lastMonth[1]) return "lastMonth";
  return "";
};

watch(
  () => [searchParam.value.startTime, searchParam.value.endTime],
  ([startTime, endTime]) =>
    (monthRadio.value = getMatchingMonth(startTime, endTime))
);

const changeMonth = () => {
  const range = monthMap[monthRadio.value as TMonthMep];
  if (!range) return;
  dateRange.value = range;
  searchParam.value.startTime = range[0];
  searchParam.value.endTime = range[1];
  getList();
};

const changeStatusTab = () => {
  searchParam.value.orderStatus = statusTab.value;
  searchFn();
};

const syncDateRangeToSearch = () => {
  searchParam.value.startTime = dateRange.value?.[0] || "";
  searchParam.value.endTime = dateRange.value?.[1] || "";
};

const productNames = (row: any) =>
  row.orderSourceList?.map((el: any) => el.productName).filter(Boolean).join("、") || "--";

const discountAmount = (row: any) =>
  Number(row.couponAmount || 0) + Number(row.voucherAmount || 0);

const paymentLabel = (row: any) => {
  if (row.onlinePayType) return enumType("onlinePaymentEnum", row.onlinePayType);
  return Number(row.onlinePayAmount || 0) > 0 ? "在线支付" : "无在线支付";
};

const formatMoney = (value: any) => {
  const amount = Number(value || 0);
  return amount.toLocaleString("zh-CN", {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  });
};

const statusCount = computed(() => ({
  PAID: tableData.value.filter((item: any) => item.orderStatus === "PAID").length,
  CLOSED: tableData.value.filter((item: any) =>
    ["CANCELED", "REFUNDED"].includes(item.orderStatus)
  ).length,
}));

const toDetail = (row: TKeyValue) => {
  localStorage.setItem("orderForm", JSON.stringify(row));
  router.push({ path: "/orderDetails", query: { orderNo: row.orderNo } });
};

</script>

<style lang="scss" scoped>
.order-center-page {
  min-height: 100%;
  padding: 18px 20px 24px;
  background: #fff8ed;
  color: #1f2633;
}

.order-center-page {
  :deep(.el-button--primary) {
    border-color: #ff8a00;
    background: #ff8a00;
    color: #fff;
  }

  :deep(.el-button--primary:hover),
  :deep(.el-button--primary:focus) {
    border-color: #ff9f29;
    background: #ff9f29;
  }

  :deep(.el-button--primary.is-plain) {
    border-color: #ffb45c;
    background: #fff7ec;
    color: #d96c00;
  }

  :deep(.el-button--primary.is-plain:hover),
  :deep(.el-button--primary.is-plain:focus) {
    border-color: #ff8a00;
    background: #ff8a00;
    color: #fff;
  }

  :deep(.el-radio-button__inner:hover) {
    color: #d96c00;
  }

  :deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
    border-color: #ff8a00;
    background: #ff8a00;
    box-shadow: -1px 0 0 0 #ff8a00;
    color: #fff;
  }

  :deep(.el-input__wrapper.is-focus),
  :deep(.el-select .el-input.is-focus .el-input__wrapper),
  :deep(.el-date-editor.is-active),
  :deep(.el-date-editor.is-active:hover) {
    box-shadow: 0 0 0 1px #ff8a00 inset;
  }

  :deep(.el-pagination.is-background .el-pager li.is-active) {
    background: #ff8a00;
  }
}

.order-header {
  display: flex;
  justify-content: space-between;
  gap: 20px;
  margin-bottom: 16px;

  .eyebrow {
    margin: 0 0 6px;
    font-size: 13px;
    color: #6b7280;
  }

  h1 {
    margin: 0;
    font-size: 24px;
    font-weight: 600;
    line-height: 32px;
  }
}

.header-summary {
  display: flex;
  gap: 10px;
}

.summary-item {
  min-width: 118px;
  padding: 10px 14px;
  border: 1px solid #e5e9f2;
  border-radius: 4px;
  background: #fff;

  span {
    display: block;
    color: #6b7280;
    font-size: 12px;
  }

  strong {
    display: block;
    margin-top: 4px;
    color: #1f2633;
    font-size: 22px;
    font-weight: 600;
  }

  &.warning strong {
    color: #ff6a00;
  }

  &.muted strong {
    color: #8a5a2b;
  }

  &.success strong {
    color: #0a8f64;
  }
}

.filter-panel,
.order-list-panel {
  border: 1px solid #e5e9f2;
  border-radius: 4px;
  background: #fff;
  box-shadow: 0 1px 2px rgba(18, 29, 51, 0.04);
}

.filter-panel {
  padding: 16px;
}

.quick-tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 10px 18px;
  margin-bottom: 16px;
}

.filter-form {
  display: grid;
  grid-template-columns: minmax(180px, 1fr) minmax(160px, 0.7fr) minmax(280px, 1.2fr) auto;
  gap: 12px;
  align-items: end;

  :deep(.el-form-item) {
    margin-bottom: 0;
  }

  :deep(.el-date-editor.el-input__wrapper) {
    width: 100%;
  }
}

.filter-actions :deep(.el-form-item__content) {
  display: flex;
  flex-wrap: nowrap;
}

.order-list-panel {
  margin-top: 14px;
  padding: 16px;
}

.list-toolbar {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 14px;

  h2 {
    margin: 0;
    font-size: 18px;
    font-weight: 600;
  }

  p {
    margin: 6px 0 0;
    color: #6b7280;
    font-size: 13px;
  }
}

.order-list {
  display: grid;
  gap: 10px;
}

.order-card {
  border: 1px solid #e5e9f2;
  border-radius: 4px;
  background: #fff;
  transition: border-color 0.18s ease, box-shadow 0.18s ease;

  &:hover {
    border-color: #ffc987;
    box-shadow: 0 6px 18px rgba(255, 138, 0, 0.12);
  }
}

.order-card-head {
  display: flex;
  justify-content: space-between;
  gap: 14px;
  padding: 10px 14px;
  border-bottom: 1px solid #ffe1bd;
  background: #fffaf2;
  color: #6b7280;
  font-size: 13px;
}

.order-identity {
  display: flex;
  align-items: center;
  gap: 10px;
}

.order-no {
  color: #1f2633;
  font-weight: 500;
}

.order-card-body {
  display: grid;
  grid-template-columns: minmax(240px, 1.3fr) minmax(300px, 1.4fr) minmax(150px, 0.7fr) minmax(150px, auto);
  gap: 16px;
  align-items: center;
  padding: 16px 14px;
}

.product-cell {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;

  h3 {
    margin: 0 0 6px;
    overflow: hidden;
    color: #1f2633;
    font-size: 16px;
    font-weight: 600;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  p {
    margin: 0;
    color: #6b7280;
    font-size: 13px;
  }
}

.product-icon {
  display: grid;
  flex: 0 0 40px;
  width: 40px;
  height: 40px;
  place-items: center;
  border-radius: 4px;
  background: #fff1dd;
  color: #d96c00;
  font-size: 20px;
}

.order-meta {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  margin: 0;

  dt {
    margin-bottom: 6px;
    color: #8a92a6;
    font-size: 12px;
  }

  dd {
    margin: 0;
    overflow: hidden;
    color: #303846;
    font-size: 13px;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}

.amount-cell {
  text-align: right;

  span,
  small {
    display: block;
    color: #8a92a6;
    font-size: 12px;
  }

  strong {
    display: block;
    margin: 4px 0;
    color: #ff6a00;
    font-size: 20px;
    font-weight: 600;
  }
}

.operation-cell {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

@media (max-width: 900px) {
  .order-header,
  .list-toolbar,
  .order-card-head {
    flex-direction: column;
  }

  .header-summary,
  .quick-tabs {
    width: 100%;
  }

  .filter-form,
  .order-card-body {
    grid-template-columns: 1fr;
  }

  .order-meta {
    grid-template-columns: 1fr;
  }

  .amount-cell,
  .operation-cell {
    justify-content: flex-start;
    text-align: left;
  }
}
</style>
