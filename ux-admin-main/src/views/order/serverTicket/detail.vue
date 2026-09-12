<template>
  <div class="table-box card">
    <el-affix :offset="120">
      <div class="tar">
        <el-button type="primary" @click="back">返回</el-button>
      </div>
    </el-affix>
    <Descriptions :list="workOrder" title="工单信息" :column="2">
      <template #operationStatus>
        <el-tag :type="enumTag('workOrderStatusTag', dataForm.operationStatus)">
          {{ enumType("workOrderOpsStatusEnum", dataForm.operationStatus) }}
        </el-tag>
      </template>
      <template #userStatus>
        <el-tag
          :type="enumTag('workOrderUserControlStatusTag', dataForm.userStatus)"
        >
          {{ enumType("workOrderUserControlStatusEnum", dataForm.userStatus) }}
        </el-tag>
      </template>
    </Descriptions>
    <Descriptions class="mt10" :list="userInfo" title="用户信息" :column="3" />
    <Descriptions
      class="mt10"
      :list="instanceInfo"
      title="实例信息"
      :column="4"
    />
    <Descriptions class="mt10" :list="imageInfo" title="镜像信息" :column="3" />
    <Descriptions class="mt10" :list="storeInfo" title="存储信息" :column="2" />
    <Descriptions
      class="mt10"
      :list="accountInfo"
      title="账密信息"
      :column="3"
    />
    <Descriptions class="mt10" :list="openTime" title="开通期限" :column="3" />
    <Descriptions class="mt10" :list="networkInfo" title="公网信息">
      <template #isAllocatePublicIp>
        <el-tag :type="dataForm.isAllocatePublicIp ? 'success' : 'danger'">
          {{ dataForm.isAllocatePublicIp ? "是" : "否" }}
        </el-tag>
      </template>
    </Descriptions>
    <el-descriptions
      v-for="(item, index) in dataForm.instanceStatusList"
      :key="index"
      class="mt10"
      :column="2"
      border
    >
      <el-descriptions-item width="200" label="公网IP">
        {{ item.publicIp ?? "--" }}
      </el-descriptions-item>
      <el-descriptions-item width="200" label="私网IP">
        {{ item.privateIp ?? "--" }}
      </el-descriptions-item>
      <el-descriptions-item width="200" label="访问端口">
        {{ item.port ?? "--" }}
      </el-descriptions-item>
      <el-descriptions-item width="200" label="访问方式">
        {{ item.accessMethod ?? "--" }}
      </el-descriptions-item>
    </el-descriptions>
  </div>
</template>

<script setup lang="ts" name="ServerTicketDetail">
import { workDetailApi } from "@/api/order";
import { useTabs } from "@/store";
import { enumType } from "@/utils/Enum";
import { enumTag } from "@/utils/enumTag";

const dataForm = ref<TKeyValue>({});

const route = useRoute();
const getDetail = async () => {
  const { data } = await workDetailApi(route.query.id as string);
  dataForm.value = data;
};
const getValue = (key: string) => dataForm.value[key] ?? "--";
// ? 工单信息
const workOrder = computed(() => [
  { label: "工单号", value: getValue("workNo") },
  { label: "关联订单号", value: getValue("orderNo") },
]);
// ? 用户信息
const userInfo = computed(() => [
  { label: "名称", value: getValue("customerName") },
  { label: "手机号", value: getValue("phone") },
  { label: "账号ID", value: getValue("userId") },
]);

// ? 实例信息
const instanceInfo = computed(() => [
  { label: "开通产品", value: getValue("productName") },
  {
    label: "产品类型",
    value: enumType("workOrderProductTypeEnum", dataForm.value.productType),
  },
  { label: "区域", value: enumType("regionEnum", dataForm.value.region) },
  { label: "随机可用区", value: dataForm.value.zone?.name ?? "--" },
  {
    label: "服务器类型",
    value: enumType("ecsTypeEnum", dataForm.value.ecsTypeEnum),
  },
  { label: "实例规格", value: getValue("ecsScale") },
  { label: "vCPU", value: getValue("cpuModel") },
  { label: "内存", value: getValue("memorySize") },
  { label: "GPU型号", value: getValue("gpuModel") },
  { label: "GPU显存", value: getValue("gpuMemory") },
]);
// ? 镜像信息
const imageInfo = computed(() => [
  {
    label: "镜像类型",
    value: enumType("imageTypeEnum", getValue("imageType")),
  },
  { label: "镜像品牌", value: getValue("imageBrand") },
  { label: "操作系统", value: getValue("os") },
]);
// ? 存储信息
const storeInfo = computed(() => [
  { label: "系统盘", value: getValue("systemDisk") },
  { label: "容量", value: getValue("systemDiskSize") },
  { label: "数据盘", value: getValue("dataDisk") },
  { label: "容量", value: getValue("dataDiskSize") },
]);

// ? 账密信息
const accountInfo = computed(() => [
  { label: "登录名", value: getValue("loginName") },
  { label: "登录密码", value: getValue("loginPassword") },
  // { label: "链接地址", value: getValue("linkAddress") },
]);
// ? 开通期限
const openTime = computed(() => [
  {
    label: "开通方式",
    value: enumType("billingTypeEnum", dataForm.value.chargeType),
  },
  { label: "已开通时间（天）", value: getValue("usedTime") },
  { label: "开通数量（台）", value: getValue("quantity") },
]);
// ? 公网信息
const networkInfo = computed(() => [
  { label: "是否分配公网", value: "isAllocatePublicIp", slot: true },
]);

const tabStore = useTabs();
// const keep
const back = () => tabStore.removeTabs(route.fullPath);
onMounted(getDetail);
</script>
<style lang="scss" scoped>
.tar {
  text-align: right;
}
</style>
