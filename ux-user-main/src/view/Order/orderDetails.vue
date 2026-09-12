<template>
  <div v-loading="loading" class="order-detail-page">
    <Breadcrumb :router-list="routerList" />

    <section class="detail-hero">
      <div class="status-block">
        <span class="status-dot" :class="orderForm.orderStatus || 'UNKNOWN'"></span>
        <div>
          <p>订单状态</p>
          <h1>{{ enumType("orderStatusEnum", orderForm.orderStatus) }}</h1>
        </div>
      </div>
      <div class="hero-meta">
        <span>订单号：{{ orderForm.orderNo || "--" }}</span>
        <span>创建时间：{{ orderForm.createTime || "--" }}</span>
      </div>
    </section>

    <section class="detail-grid">
      <article class="detail-card basic-card">
        <div class="card-title">
          <h2>基本信息</h2>
        </div>
        <dl class="info-grid">
          <div>
            <dt>订单号</dt>
            <dd>{{ orderForm.orderNo || "--" }}</dd>
          </div>
          <div>
            <dt>订单类型</dt>
            <dd>{{ enumType("orderTypeEnum", orderForm.orderType) }}</dd>
          </div>
          <div>
            <dt>创建时间</dt>
            <dd>{{ orderForm.createTime || "--" }}</dd>
          </div>
          <div>
            <dt>支付时间</dt>
            <dd>{{ orderForm.payTime || "--" }}</dd>
          </div>
          <div>
            <dt>支付渠道</dt>
            <dd>{{ paymentLabel(orderForm) }}</dd>
          </div>
          <div>
            <dt>支付流水号</dt>
            <dd>{{ orderForm.onlinePaySerialNumber || "--" }}</dd>
          </div>
        </dl>
      </article>

      <article class="detail-card amount-card">
        <div class="card-title">
          <h2>费用信息</h2>
        </div>
        <div class="total-pay">
          <span>实付金额</span>
          <strong>¥{{ formatMoney(orderForm.finalPayAmount) }}</strong>
        </div>
        <dl class="amount-list">
          <div>
            <dt>产品原价</dt>
            <dd>¥{{ formatMoney(orderForm.originalPrice) }}</dd>
          </div>
          <div>
            <dt>在线支付</dt>
            <dd>¥{{ formatMoney(orderForm.onlinePayAmount) }}</dd>
          </div>
          <div>
            <dt>优惠券减免</dt>
            <dd>-¥{{ formatMoney(orderForm.couponAmount) }}</dd>
          </div>
          <div>
            <dt>代金券抵扣</dt>
            <dd>-¥{{ formatMoney(orderForm.voucherAmount) }}</dd>
          </div>
        </dl>
      </article>
    </section>

    <section class="detail-card product-section">
      <div class="card-title with-note">
        <div>
          <h2>商品明细</h2>
          <p>展示本订单包含的资源、配置、计费方式和有效期。</p>
        </div>
      </div>

      <el-empty v-if="!orderTableData.length" description="暂无商品明细" />

      <div v-else class="product-list">
        <article v-for="item in orderTableData" :key="item.id || item.sourceId || item.productName" class="product-item">
          <div class="product-main">
            <div class="product-icon">
              <el-icon><Box /></el-icon>
            </div>
            <div>
              <h3>{{ item.productName || "--" }}</h3>
              <dl class="config-grid">
                <div
                  v-for="config in configItems(item)"
                  :key="config.label"
                  :class="{ wide: config.wide }"
                >
                  <dt>{{ config.label }}</dt>
                  <dd>{{ config.value }}</dd>
                </div>
              </dl>
            </div>
          </div>
          <dl class="product-meta">
            <div>
              <dt>计费方式</dt>
              <dd>{{ enumType("billingTypeEnum", item.chargeType) }}</dd>
            </div>
            <div>
              <dt>购买时长</dt>
              <dd>{{ formatDuration(item) }}</dd>
            </div>
            <div>
              <dt>实例ID/名称</dt>
              <dd>{{ item.sourceName || "--" }}</dd>
            </div>
            <div>
              <dt>起止时间</dt>
              <dd>{{ item.createTime || "--" }} 至 {{ item.expiresTime || "--" }}</dd>
            </div>
          </dl>
          <div class="product-price">
            <span>实付</span>
            <strong>¥{{ formatMoney(item.finalUnitPrice) }}</strong>
            <small>平台价 ¥{{ formatMoney(item.unitPrice) }}</small>
          </div>
        </article>
      </div>

      <Pagination v-if="pageData.total > pageData.pageSize" :pageData="pageData" :PageChange="getTableData" />
    </section>

    <section class="detail-card pay-section">
      <div class="card-title">
        <h2>支付明细</h2>
      </div>
      <el-table :data="paymentDetailsTableData" border>
        <el-table-column label="支付流水编号" prop="onlinePaySerialNumber" min-width="180" show-overflow-tooltip />
        <el-table-column label="订单号" prop="orderNo" min-width="180" show-overflow-tooltip />
        <el-table-column label="支付渠道" min-width="120">
          <template #default="{ row }">{{ paymentLabel(row) }}</template>
        </el-table-column>
        <el-table-column label="在线支付金额" min-width="120">
          <template #default="{ row }">¥{{ formatMoney(row.onlinePayAmount) }}</template>
        </el-table-column>
        <el-table-column label="优惠券减免" min-width="120">
          <template #default="{ row }">¥{{ formatMoney(row.couponAmount) }}</template>
        </el-table-column>
        <el-table-column label="代金券抵扣" min-width="120">
          <template #default="{ row }">¥{{ formatMoney(row.voucherAmount) }}</template>
        </el-table-column>
        <el-table-column label="实付金额" min-width="120">
          <template #default="{ row }">¥{{ formatMoney(row.finalPayAmount) }}</template>
        </el-table-column>
      </el-table>
    </section>
  </div>
</template>

<script setup lang="ts" name="OrderDetails">
import { getOrderListAPI, getOrderSourceListAPI } from "@/api/order";
import { useTable } from "@/hooks/useTable";
import { enumType } from "@/utils/Enum";
import { Box } from "@element-plus/icons-vue";

const route = useRoute();
const cachedOrder = JSON.parse(localStorage.getItem("orderForm") || "{}");
const orderForm = ref<any>(cachedOrder);
const loading = ref(false);

const routerList = ref([
  { name: "订单管理", path: "/order" },
  { name: "订单详情" },
]);

const paymentDetailsTableData = computed(() => [
  {
    onlinePaySerialNumber: orderForm.value.onlinePaySerialNumber,
    orderNo: orderForm.value.orderNo,
    onlinePayType: orderForm.value.onlinePayType,
    onlinePayAmount: orderForm.value.onlinePayAmount,
    finalPayAmount: orderForm.value.finalPayAmount,
    couponAmount: orderForm.value.couponAmount,
    voucherAmount: orderForm.value.voucherAmount,
  },
]);

const orderTableData = ref<any[]>([]);

const durationUnitMap: Record<string, string> = {
  HOUR: "小时",
  DAY: "天",
  MONTH: "月",
  YEAR: "年",
  CAPACITY: "GB",
};

type ConfigDisplayItem = {
  label: string;
  value: string;
  wide?: boolean;
};

const formatDuration = (row: any) => {
  if (!row.duration) return "无";
  return `${row.duration}${durationUnitMap[row.durationUnit] || row.durationUnit || ""}`;
};

const pushConfigItem = (
  list: ConfigDisplayItem[],
  label: string,
  value: any,
  wide = false
) => {
  if (value === undefined || value === null || value === "") return;
  list.push({ label, value: String(value), wide });
};

const gpuConfigItems = (row: any): ConfigDisplayItem[] => {
  const detail = row.configDetail || {};
  const gpuSpec = detail.gpuSpec || {};
  const resource = detail.resource || {};
  const list: ConfigDisplayItem[] = [];

  pushConfigItem(list, "GPU型号", gpuSpec.model);
  pushConfigItem(list, "GPU数量", gpuSpec.count ? `${gpuSpec.count} 张` : "");
  pushConfigItem(list, "显存", gpuSpec.gpuMemory);
  pushConfigItem(list, "CPU", resource.cpu ? `${resource.cpu} 核` : "");
  pushConfigItem(list, "CPU型号", resource.cpuModel, true);
  pushConfigItem(list, "内存", resource.memory);
  pushConfigItem(list, "镜像", detail.image, true);
  pushConfigItem(list, "系统盘", resource.systemDisk);
  pushConfigItem(list, "数据盘", resource.dataDisk);
  pushConfigItem(list, "地域", detail.region);
  pushConfigItem(list, "可用区", detail.zone);
  pushConfigItem(list, "GPU驱动", detail.gpuDriver);
  pushConfigItem(list, "CUDA版本", detail.cudaVersion);

  return list;
};

const genericConfigItems = (row: any): ConfigDisplayItem[] => {
  const detail = row.configDetail || {};
  const list: ConfigDisplayItem[] = [];
  Object.entries(detail).forEach(([key, value]) => {
    if (typeof value === "object" && value !== null) {
      pushConfigItem(list, key, JSON.stringify(value), true);
      return;
    }
    pushConfigItem(list, key, value);
  });
  return list;
};

const configItems = (row: any): ConfigDisplayItem[] => {
  const list = row.productName?.startsWith("GPU Pod")
    ? gpuConfigItems(row)
    : genericConfigItems(row);
  if (list.length) return list;
  return [{ label: "配置", value: row.sourceName || "无", wide: true }];
};

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

const { pageParams, pageData, updatePageData } = useTable({
  requestAuto: false,
});

const getTableData = async () => {
  if (!orderForm.value.id) return;
  const { data } = await getOrderSourceListAPI(
    orderForm.value.id,
    pageParams.value
  );
  const { dataTotal, list, pageNo, pageSize } = data;
  orderTableData.value = list || [];
  updatePageData({ pageSize, pageNo, total: +dataTotal });
};

const loadOrder = async () => {
  const orderNo = String(route.query.orderNo || cachedOrder.orderNo || "");
  if (!orderNo) {
    ElMessage.error("缺少订单号，无法加载订单详情");
    return;
  }

  loading.value = true;
  try {
    const { data } = await getOrderListAPI({ orderNo, pageNo: 1, pageSize: 1 });
    const order = data?.list?.find((item: any) => item.orderNo === orderNo);
    if (!order) throw new Error("订单不存在或无权查看");
    orderForm.value = order;
    await getTableData();
  } catch (error: any) {
    ElMessage.error(error?.message || "订单详情加载失败");
  } finally {
    loading.value = false;
  }
};

loadOrder();

onUnmounted(() => localStorage.removeItem("orderForm"));
</script>

<style lang="scss" scoped>
.order-detail-page {
  min-height: 100%;
  padding: 18px 20px 24px;
  background: #fff8ed;
  color: #1f2633;
}

.order-detail-page {
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

  :deep(.el-table th.el-table__cell) {
    background: #fffaf2;
    color: #8a5a2b;
  }

  :deep(.el-table--border .el-table__cell) {
    border-color: #ffe1bd;
  }

  :deep(.el-pagination.is-background .el-pager li.is-active) {
    background: #ff8a00;
  }
}

.detail-hero,
.detail-card {
  border: 1px solid #f1dfc7;
  border-radius: 4px;
  background: #fff;
  box-shadow: 0 1px 2px rgba(18, 29, 51, 0.04);
}

.detail-hero {
  display: flex;
  justify-content: space-between;
  gap: 20px;
  margin: 14px 0;
  padding: 18px 20px;
}

.status-block {
  display: flex;
  align-items: center;
  gap: 12px;

  p {
    margin: 0 0 4px;
    color: #6b7280;
    font-size: 13px;
  }

  h1 {
    margin: 0;
    font-size: 24px;
    font-weight: 600;
  }
}

.status-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #8a92a6;

  &.PAID {
    background: #0a8f64;
  }

  &.UNPAID {
    background: #ff6a00;
  }

  &.CANCELED {
    background: #8a92a6;
  }

  &.REFUNDED {
    background: #d93026;
  }
}

.hero-meta {
  display: flex;
  flex-wrap: wrap;
  align-content: center;
  justify-content: flex-end;
  gap: 8px 18px;
  color: #6b7280;
  font-size: 13px;
}

.detail-grid {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 360px;
  gap: 14px;
  margin-bottom: 14px;
}

.detail-card {
  padding: 18px 20px;
}

.card-title {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #ffe1bd;

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

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px 28px;
  margin: 0;

  dt {
    margin-bottom: 6px;
    color: #8a92a6;
    font-size: 13px;
  }

  dd {
    margin: 0;
    color: #303846;
    font-size: 14px;
    word-break: break-all;
  }
}

.total-pay {
  padding: 4px 0 18px;
  border-bottom: 1px solid #ffe1bd;

  span {
    display: block;
    color: #6b7280;
    font-size: 13px;
  }

  strong {
    display: block;
    margin-top: 6px;
    color: #ff6a00;
    font-size: 30px;
    font-weight: 600;
  }
}

.amount-list {
  display: grid;
  gap: 12px;
  margin: 16px 0 0;

  div {
    display: flex;
    justify-content: space-between;
    gap: 14px;
  }

  dt,
  dd {
    margin: 0;
    font-size: 13px;
  }

  dt {
    color: #6b7280;
  }

  dd {
    color: #303846;
    font-weight: 500;
  }
}

.product-section,
.pay-section {
  margin-top: 14px;
}

.product-list {
  display: grid;
  gap: 10px;
}

.product-item {
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(320px, 0.7fr) minmax(130px, 0.32fr);
  gap: 18px;
  align-items: flex-start;
  padding: 14px;
  border: 1px solid #f1dfc7;
  border-radius: 4px;
  background: #fff;
}

.product-main {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  min-width: 0;

  h3 {
    margin: 0 0 8px;
    color: #1f2633;
    font-size: 16px;
    font-weight: 600;
  }

}

.config-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(120px, 1fr));
  gap: 8px 10px;
  margin: 0;

  div {
    min-width: 0;
    padding: 8px 10px;
    border: 1px solid #f6e3c8;
    border-radius: 4px;
    background: #fffaf2;
  }

  .wide {
    grid-column: span 2;
  }

  dt {
    margin-bottom: 4px;
    color: #9a6a35;
    font-size: 12px;
    line-height: 18px;
  }

  dd {
    margin: 0;
    color: #303846;
    font-size: 13px;
    line-height: 20px;
    overflow-wrap: anywhere;
    word-break: break-word;
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

.product-meta {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px 18px;
  margin: 0;

  dt {
    margin-bottom: 5px;
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

.product-price {
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

@media (max-width: 980px) {
  .detail-hero,
  .card-title {
    flex-direction: column;
  }

  .hero-meta {
    justify-content: flex-start;
  }

  .detail-grid,
  .product-item {
    grid-template-columns: 1fr;
  }

  .info-grid,
  .product-meta,
  .config-grid {
    grid-template-columns: 1fr;
  }

  .config-grid .wide {
    grid-column: auto;
  }

  .product-price {
    text-align: left;
  }
}
</style>
