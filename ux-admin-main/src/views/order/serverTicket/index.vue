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
        <div class="ml50" v-if="!row.instanceStatusList.length">请先开通</div>
        <el-table v-else :data="row.instanceStatusList" :border="true">
          <el-table-column align="center" label="公网IP" prop="publicIp" />
          <el-table-column align="center" label="私网IP" prop="privateIp" />
          <el-table-column align="center" label="访问端口" prop="port" />
          <el-table-column
            align="center"
            label="访问方式"
            prop="accessMethod"
          />
          <el-table-column
            align="center"
            label="用户控制状态"
            prop="userStatus"
          >
            <template #default="{ row }">
              <el-tag
                :type="enumTag('workOrderUserControlStatusTag', row.userStatus)"
              >
                {{ enumType("workOrderUserControlStatusEnum", row.userStatus) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            label="运维状态"
            prop="operationStatus"
          >
            <template #default="{ row }">
              <el-tag
                :type="enumTag('workOrderStatusTag', row.operationStatus)"
              >
                {{ enumType("workOrderOpsStatusEnum", row.operationStatus) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column align="center" label="操作">
            <template #default="{ row }">
              <el-button
                type="danger"
                link
                v-if="[2].includes(row.operationStatus)"
                @click="workOperationFn(row.instanceId, row.publicIp, 1)"
              >
                停机
              </el-button>
              <el-button
                type="danger"
                link
                v-if="[2, 4].includes(row.operationStatus)"
                @click="workOperationFn(row.instanceId, row.publicIp, 2)"
              >
                销毁
              </el-button>
              <el-button
                type="danger"
                link
                v-if="[2, 4].includes(row.operationStatus)"
                @click="workOperationFn(row.instanceId, row.publicIp, 3)"
              >
                重启
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </template>
      <template #customerInfoQueryKey="row">
        <div>
          <div class="flex">
            <div class="label">ID ：</div>
            {{ row.userId ?? "--" }}
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

      <template #storeInfo="row">
        <div>
          <div class="flex">
            <div class="label">系统盘 ：</div>
            {{
              row.systemDisk
                ? `${row.systemDisk} (${row.systemDiskSize}G)`
                : "--"
            }}
          </div>
          <div class="flex">
            <div class="label">数据盘 ：</div>
            {{
              row.dataDisk ? `${row.dataDisk}  (${row.dataDiskSize}G)` : "--"
            }}
          </div>
        </div>
      </template>

      <template #login="row">
        <div>
          <div class="flex">
            <div class="label">登录名 ：</div>
            {{ row.loginName ?? "--" }}
          </div>
          <div class="flex">
            <div class="label">密码 ：</div>
            {{ row.loginPassword ?? "--" }}
          </div>
        </div>
      </template>
      <template #operation="row">
        <template v-for="item in operationBtn">
          <el-button
            link
            :type="item.type"
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
      @closePopover="closePopover"
      :disabled="disabled"
      size="50%"
      @submit="submit"
    >
      <Descriptions :list="workOrder" title="工单信息" :column="2" />
      <Descriptions
        class="mt10"
        :list="userInfo"
        title="用户信息"
        :column="2"
      />
      <Descriptions
        class="mt10"
        :list="instanceInfo"
        title="实例信息"
        :column="2"
      />
      <Descriptions
        class="mt10"
        :list="imageInfo"
        title="镜像信息"
        :column="2"
      />
      <Descriptions
        class="mt10"
        :list="storeInfo"
        title="存储信息"
        :column="2"
      />
      <Descriptions
        class="mt10"
        :list="accountInfo"
        title="账密信息"
        :column="3"
      />
      <Descriptions
        class="mt10"
        :list="openTime"
        title="开通期限"
        :column="3"
      />
      <Descriptions class="mt10" :list="networkInfo" title="公网信息">
        <template #isAllocatePublicIp>
          <el-tag :type="dataForm.isAllocatePublicIp ? 'success' : 'danger'">
            {{ dataForm.isAllocatePublicIp ? "是" : "否" }}
          </el-tag>
        </template>
      </Descriptions>

      <el-form ref="publicInfosRef" :model="openParams">
        <el-card
          class="mt10"
          v-for="(item, index) in openParams.publicInfos"
          :key="index"
        >
          <el-row :gutter="10">
            <el-col :span="12" v-for="(el, i) in list" :key="i">
              <el-form-item
                :label="el.label"
                :prop="`publicInfos.${index}.${el.value}`"
                :rules="rules(el.label, el.required)"
              >
                <el-select
                  v-model="item[el.value]"
                  v-if="el.value === 'accessMethod'"
                >
                  <el-option label="SSH" value="SSH" />
                </el-select>
                <el-input
                  v-else
                  v-model="item[el.value]"
                  placeholder=""
                  clearable
                />
              </el-form-item>
            </el-col>
          </el-row>
        </el-card>
      </el-form>
    </Drawer>
  </div>
</template>

<script setup lang="ts" name="ServerTicket">
import {
  openWorkApi,
  workDetailApi,
  workListApi,
  workOperationApi,
} from "@/api/order";
import { useHandleData } from "@/hooks/useHandleData";
import { useTable } from "@/hooks/useTable";
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
  addOrEdit,
  popoverTitle,
  closePopover,
  disabled,
  dataForm,
  openPopover,
} = useTable({ api: workListApi, title: "服务器工单" });
const columns: ColumnProps[] = [
  {
    prop: "status",
    label: "状态",
    width: 80,
    type: "expand",
    slot: true,
    fixed: "left",
  },
  {
    prop: "customerInfoQueryKey",
    label: "账户信息",
    width: 250,
    search: { el: "input" },
    slot: true,
  },
  { prop: "workNo", label: "工单工号", width: 180 },
  { prop: "orderNo", label: "关联订单号", width: 230 },
  { prop: "productName", label: "产品名称", width: 200 },
  {
    prop: "productType",
    label: "产品类型",
    value: (row) => enumType("workOrderProductTypeEnum", row.productType),
    width: 200,
  },
  {
    prop: "region",
    label: "可用区",
    value: (row) => enumType("regionEnum", row.region),
    width: 200,
  },
  { prop: "imageBrand", label: "镜像", width: 200 },
  { prop: "ecsScale", label: "规格", width: 200 },
  { prop: "storeInfo", label: "存储信息", slot: true, width: 230 },
  {
    prop: "isAllocatePublicIp",
    label: "是否分配公网IP",
    width: 180,
    value: (row) => (row.isAllocatePublicIp ? "是" : "否"),
  },
  {
    prop: "chargeType",
    label: "续费方式",
    width: 200,
    value: (row) => enumType("billingTypeEnum", row.chargeType),
  },
  { prop: "quantity", label: "数量" },
  { prop: "expireTime", label: "到期时间", width: 200 },
  { prop: "login", label: "服务器账密", slot: true, width: 200 },
  { prop: "createTime", label: "创建时间", width: 200 },
  { prop: "payTime", label: "支付时间", width: 200 },
  { prop: "operation", label: "操作", slot: true, width: 200, fixed: "right" },
];

const rules = (msg: string, required: boolean = true) => {
  return {
    required,
    message: `请输入${msg}`,
    trigger: ["blur", "change"],
  };
};

const list = computed(() => [
  {
    label: "公网地址",
    value: "publicIp",
    required: dataForm.value.isAllocatePublicIp,
  },
  { label: "访问端口", value: "port", required: true },
  { label: "私网地址", value: "privateIp", required: true },
  { label: "访问方式", value: "accessMethod", required: true },
]);

const openParams = ref<{ id: string; publicInfos: TKeyValue[] }>({
  id: "",
  publicInfos: [],
});
let params = { publicIp: "", privateIp: "", port: "", accessMethod: "SSH" };

watch(
  () => dataForm.value.quantity,
  (val) => {
    openParams.value.publicInfos = !!val
      ? Array.from({ length: val }, () => ({ ...params }))
      : [];
  }
);
const publicInfosRef = ref();
const submit = () => {
  openParams.value.id = dataForm.value.id;
  publicInfosRef.value.validate(async (valid: boolean) => {
    if (!valid) return false;

    await openWorkApi(openParams.value);
    ElMessage.success("开通成功");
    getList();
    closePopover();
  });
};
const getValue = (key: string) => dataForm.value[key] ?? "--";

const router = useRouter();
// ? 工单信息
const workOrder = computed(() => [
  { label: "工单号", value: getValue("workNo") },
  { label: "关联订单号", value: getValue("orderNo") },
]);
// ? 用户信息
const userInfo = computed(() => [
  { label: "名称", value: getValue("customerName") },
  { label: "手机号", value: getValue("phone") },
]);
// ? 实例信息
const instanceInfo = computed(() => [
  { label: "开通产品", value: getValue("productName") },
  {
    label: "产品类型",
    value: enumType("workOrderProductTypeEnum", dataForm.value.productType),
  },
  { label: "区域", value: enumType("regionEnum", dataForm.value.region) },
  { label: "随机可用区", value: getValue("zone") },
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
  { label: "镜像类型", value: getValue("imageType") },
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
// ? 公网信息
const networkInfo = computed(() => [
  { label: "是否分配公网", value: "isAllocatePublicIp", slot: true },
]);
// ? 账密信息
const accountInfo = computed(() => [
  { label: "登录名", value: getValue("loginName") },
  { label: "登录密码", value: getValue("loginPassword") },
  { label: "链接地址", value: getValue("linkAddress") },
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
const typeMap = {
  1: { label: "停机", operationType: "1" },
  2: { label: "销毁", operationType: "5" },
  3: { label: "重启", operationType: "3" },
};
const workOperationFn = async (
  instanceId: string,
  publicIp: string,
  operationType: 1 | 2 | 3
) => {
  let msg = `是否对公网IP为【${publicIp}】进行【${typeMap[operationType].label}】操作?`;
  await useHandleData(workOperationApi, { instanceId, operationType }, msg);
  getList();
};
//#region 操作按钮
const operationBtn: IOperationBtnItem[] = [
  {
    label: "开通回执信息",
    type: "primary",
    click: (row) => openPopover("edit", workDetailApi, row.id),
    show: (row) => !row.instanceStatusList.length,
  },

  {
    label: "查看",
    click: ({ id }) =>
      router.push({ path: "/order/serverTicketDetail", query: { id } }),
    type: "primary",
    show: () => true,
  },
];
//#endregion
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
