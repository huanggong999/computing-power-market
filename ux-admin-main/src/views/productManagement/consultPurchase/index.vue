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
        <div class="flex">
          <div class="label">名称 ：</div>
          {{ row.userName ?? "--" }}
        </div>
        <div class="flex">
          <div class="label">手机号 ：</div>
          {{ row.phone ?? "--" }}
        </div>
      </template>
      <template #customerInfo="row">
        <div class="flex">
          <div class="label">名称 ：</div>
          {{ row.agiCustomerName ?? "--" }}
        </div>
        <div class="flex">
          <div class="label">手机号 ：</div>
          {{ row.mobile ?? "--" }}
        </div>
      </template>
      <template #actualStatus="row">
        <el-tag :type="enumTag('productStatusTag', row.actualStatus)">
          {{ enumType("productStatusEnum", row.actualStatus) }}
        </el-tag>
      </template>
      <template #userPwd="row">
        <el-button type="primary" link @click="viewPwd(row.userpwdList)">
          查看密码
        </el-button>
      </template>
      <template #payStatus="row">
        <el-tag :type="enumTag('productPayStatusTag', row.payStatus)">
          {{ enumType("productPayStatusEnum", row.payStatus) }}
        </el-tag>
      </template>
      <template #operation="row">
        <!-- <el-button
          type="primary"
          link
          @click="openPopover('edit', networkProductDetailApi, row.id)"
        >
          {{ row.actualStatus === 1 ? "开通" : "编辑" }}
        </el-button> -->
        <el-button
          link
          type="danger"
          @click="removeFn(networkValueDeleteApi, row.id)"
        >
          删除
        </el-button>
      </template>
    </ProTable>
    <!-- <Drawer
      v-model="addOrEdit"
      title="开通"
      :IsFooter="isEdit && openParams.userPwdList.length > 0"
      @closePopover="closePopover"
      @submit="openFn"
    >
      <Descriptions class="mt10" :list="descriptionsList" />
      <el-space
        class="mt10"
        v-if="dataForm.actualStatus === 1 && openParams.userPwdList.length > 0"
      >
        <div>IP地区：</div>
        <el-input
          v-model="openParams.ipAddress"
          placeholder="请输入地区"
        ></el-input>
      </el-space>
      <el-form ref="publicInfosRef" :model="openParams">
        <el-card
          class="mt10"
          v-for="(item, index) in openParams.userPwdList"
          :key="index"
        >
          <el-form-item
            v-for="(el, i) in list"
            :key="i"
            :label="el.label"
            :prop="`userPwdList.${index}.${el.value}`"
            :rules="rules(el.label)"
          >
            <el-input
              type="text"
              :disabled="el.disabled"
              v-model="item[el.value]"
              placeholder=""
              clearable
            />
          </el-form-item>
        </el-card>
      </el-form>
    </Drawer> -->

    <Drawer
      v-model="userPwd"
      :IsFooter="false"
      title="查看密码"
      width="500"
      @closePopover="() => (userPwd = false)"
    >
      <el-descriptions
        class="mb20"
        border
        :column="1"
        v-for="(item, index) in userPwdList"
        :key="index"
      >
        <el-descriptions-item label="用户账号">
          {{ item.email ?? "--" }}
        </el-descriptions-item>
        <el-descriptions-item label="内网IP">
          {{ item.ip ?? "--" }}
        </el-descriptions-item>
        <el-descriptions-item label="公网IP">
          {{ item.publicIp ?? "--" }}
        </el-descriptions-item>
        <el-descriptions-item label="初始密码">
          {{ item.pwd ?? "--" }}
        </el-descriptions-item>
      </el-descriptions>
    </Drawer>
  </div>
</template>

<script setup lang="ts" name="ConsultPurchase">
import {
  networkValueApi,
  networkValueDeleteApi,
} from "@/api/productManagement";
// import { useHandleData } from "@/hooks/useHandleData";
import { useTable } from "@/hooks/useTable";
import { enumType } from "@/utils/Enum";
import {
  productStatusSelectEnum,
  productPayStatusSelectEnum,
} from "@/utils/selectEnum";
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
  addOrEdit,
  removeFn,
} = useTable({ api: networkValueApi, initParams: { formType: 2 } });
// closePopover,
// openPopover,

const columns: ColumnProps[] = [
  { label: "创建时间", prop: "createTime", width: 200 },
  { label: "购买时间", prop: "payTime", width: 200 },
  {
    label: "产品名称",
    prop: "productName",
    search: { el: "input" },
    width: 200,
  },
  // { label: "表单名称", prop: "formName", search: { el: "input" }, width: 180 },
  {
    label: "用户名称",
    prop: "nickname",
    search: { el: "input" },
    noShow: true,
  },
  {
    label: "用户手机号",
    prop: "mobile",
    search: { el: "input" },
    noShow: true,
  },
  {
    label: "客户名称",
    prop: "customerName",
    search: { el: "input" },
    noShow: true,
  },
  {
    label: "客户手机号",
    prop: "customerMobile",
    search: { el: "input" },
    noShow: true,
  },
  { label: "邮箱", prop: " email", search: { el: "input" }, noShow: true },
  { label: "用户信息", prop: "userInfo", slot: true, width: 180 },
  { label: "客户信息", prop: "customerInfo", slot: true, width: 180 },
  {
    label: "支付状态",
    prop: "payStatus",
    slot: true,
    width: 100,
    search: { el: "select" },
    enum: productPayStatusSelectEnum,
  },
  { label: "宽带(M)", prop: "bandwidth", width: 90 },
  // value: (row) => (row.actualStatus === 1 ? "--" : row.bandwidth),
  { prop: "ipCount", label: "IP数量", width: 90 },
  {
    prop: "chargeType",
    label: "计费类型",
    width: 90,
    value: (row) => enumType("billingTypeEnum", row.chargeType),
  },
  {
    prop: "duration",
    label: "计费时长",
    width: 90,
    value: (row) =>
      row.duration
        ? row.duration + enumType("durationUnitEnum", row.durationUnit)
        : "--",
  },
  { prop: "originalAmount", label: "原价", width: 80 },
  // payPrice
  {
    prop: "payPrice",
    label: "实付价",
    width: 80,
    search: { el: "input" },
    value: (row) => row.payAmount || "--",
  },
  { prop: "networkCount", label: "购买产品数量", width: 120 },
  // value: (row) => (row.actualStatus === 1 ? "--" : row.networkCount),
  {
    prop: "actualAgiOpenTime",
    label: "开通时间",
    width: 170,
    search: {
      el: "date-picker",
      dateType: "datetimerange",
      dateEnum: ["startTime", "endTime"],
      valueFormat: "YYYY-MM-DD HH:mm:ss",
    },
  },
  { prop: "actualAgiExpireTime", label: "过期时间", width: 170 },
  { prop: "userPwd", label: "账户账号及密码", width: 170, slot: true },
  { prop: "remark", label: "备注", width: 170 },
  {
    prop: "actualStatus",
    label: "产品状态",
    slot: true,
    search: { el: "select" },
    enum: productStatusSelectEnum,
    width: 100,
    fixed: "right",
  },
  { label: "操作", prop: "operation", slot: true, fixed: "right" },
];

const openParams = ref<{
  id: string;
  userPwdList: TKeyValue[];
  ipAddress?: string;
}>({ id: "", userPwdList: [], ipAddress: "" });
// const list = computed(() => [
//   { label: "用户账号", value: "email", disabled: true },
//   { label: "初始密码", value: "pwd", disabled: true },
//   { label: "IP地址", value: "ip", disabled: !isEdit.value },
// ]);
// const rules = (msg: string, required: boolean = true) => {
//   return {
//     required,
//     message: `请输入${msg}`,
//     trigger: ["blur", "change"],
//   };
// };

const isEdit = ref(false);

watch(
  () => dataForm.value.networkCount,
  (val) => {
    isEdit.value =
      dataForm.value.userPwdList?.some((el: TKeyValue) => !el.ip) ?? true;
    openParams.value.userPwdList = !!val
      ? !isEdit.value
        ? dataForm.value.userPwdList
        : dataForm.value.userPwdList.reduce(
            (acc: TKeyValue[], el: TKeyValue) => {
              if (!el.ip) acc.push(el);
              return acc;
            },
            []
          )
      : [];
  }
);
// const publicInfosRef = ref();
// &  开通
// const openFn = async () => {
//   openParams.value.id = dataForm.value.id;

//   if (!openParams.value.ipAddress && dataForm.value.actualStatus === 1)
//     return ElMessage.warning("请输入IP地址");
//   if (dataForm.value.actualStatus !== 1) delete openParams.value.ipAddress;
//   publicInfosRef.value.validate(async (valid: boolean) => {
//     if (!valid) return false;
//     await useHandleData(networkValueOpenApi, openParams.value, "开通");
//     closePopover();
//     getList();
//   });
// };
// const getValue = (key: string) => dataForm.value[key] ?? "--";

// const descriptionsList = computed(() => [
//   {
//     label: "购买时长",
//     value: enumType("billingTypeEnum", dataForm.value.chargeType),
//   },
//   { label: "账户数", value: getValue("networkCount") },
//   { label: "IP地址数", value: getValue("ipCount") },
//   { label: "带宽（M）", value: getValue("bandwidth") },
//   { label: "联系电话", value: getValue("mobile") },
//   { label: "地区", value: getValue("ipAddress") },
// ]);

// ? 查看密码
const userPwd = ref(false);
const userPwdList = ref<TKeyValue[]>([]);
const viewPwd = (list: TKeyValue[]) => {
  if (!list) return ElMessage.warning("暂无数据");
  userPwd.value = true;
  userPwdList.value = list;
};

watch(
  () => addOrEdit.value,
  (val) => {
    if (!val) {
      openParams.value.ipAddress = "";
      openParams.value.userPwdList = [];
      openParams.value.id = "";
    }
  }
);
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
