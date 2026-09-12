<template>
  <div class="table-box">
    <ProTable
      :columns="columns"
      :tableData="tableData"
      :refreshFn="refreshFn"
      :pageData="pageData"
      :getList="getList"
      :searchParam="searchParam"
      :searchFn="searchFn"
      :resetFn="resetFn"
      type="none"
    >
      <template #namePhoneSearchKey="row">
        <div class="flex"><span>用户名：</span> {{ row.customerName }}</div>
        <div class="flex"><span>手机号：</span>{{ row.phone }}</div>
      </template>
      <template #parentInfo="row">
        <div class="flex"><span>用户名：</span> {{ row.pname ?? "--" }}</div>
        <div class="flex"><span>手机号：</span>{{ row.pphone ?? "--" }}</div>
      </template>
      <template #status="row">
        <el-switch
          v-model="row.status"
          inline-prompt
          active-text="正常"
          inactive-text="停用"
          active-value="OK"
          inactive-value="DEACTIVATED"
        />
      </template>
      <template #operation="row">
        <div class="flx-center">
          <el-button type="primary" link @click="openPopover('edit', row)">
            详情
          </el-button>
          <el-dropdown class="ml10" placement="bottom">
            <el-button type="primary" plain icon="DArrowRight" link>
              更多
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <template v-for="item in operationBtn" :key="item.label">
                  <el-dropdown-item>
                    <el-button
                      link
                      v-if="item.show!(row)"
                      @click="item.click(row)"
                    >
                      {{ item.label }}
                    </el-button>
                  </el-dropdown-item>
                </template>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </template>
    </ProTable>
    <Coupon v-model="couponVisible" @submit="addCoupon" />
    <Drawer
      v-model="addOrEdit"
      title="客户详情"
      size="80%"
      @closePopover="closePopover"
    >
      <Descriptions :list="userList" title="账户信息" :column="2" />
      <el-radio-group class="mt10 mb10" v-model="tableType">
        <el-radio-button
          v-for="item in radioGroup"
          :key="item.value"
          :value="item.value"
        >
          {{ item.label }}
        </el-radio-button>
      </el-radio-group>
      <BillOverview
        v-if="tableType === 'billOverview'"
        :customerId="dataForm.id"
      />
      <BillDetails
        v-if="tableType === 'billDetails'"
        :customerId="dataForm.id"
      />
      <Balance v-if="tableType === 'balance'" :customerId="dataForm.id" />

      <Instance v-if="tableType === 'instance'" :customerId="dataForm.id" />
      <Container v-if="tableType === 'container'" :customerId="dataForm.id" />
    </Drawer>

    <el-dialog v-model="setParentVisible" title="设置上级" center width="20%">
      <ProForm
        class="mt10"
        v-model="setParentForm"
        :formColumns="setParentFormColumns"
      />
      <template #footer>
        <el-button type="primary" @click="setParentFn"> 确 定 </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="setCouponVisible" title="赠送优惠券" width="60%" center>
      <div class="flx-align-center">
        <h3 class="mr20">请选择赠送券</h3>
        <el-button type="primary" @click="couponVisible = true">
          选择优惠券
        </el-button>
      </div>
      <ProTable
        v-if="couponList.length"
        type="none"
        :columns="couponColumns"
        :table-data="couponList"
        :is-page="false"
        :-is-refresh="false"
      >
        <template #operation="row">
          <el-button type="primary" text @click="delCoupon(row)">
            移除
          </el-button>
        </template>
      </ProTable>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="setCouponVisible = false">取消</el-button>
          <el-button type="primary" @click="giveCouponFn"> 确定 </el-button>
        </div>
      </template>
    </el-dialog>
    <el-dialog v-model="productDiscountVisible" title="设置产品折扣" center>
      <div class="mt30">
        <div class="tip mb10">
          折扣范围：0-1，小数点后最多三位, 0.9相当于90%
        </div>
        <el-descriptions :column="2" border direction="horizontal">
          <template
            v-for="item in productDiscountParams.list"
            :key="item.label"
          >
            <el-descriptions-item
              :label="enumType('resourceDiscountEnum', item.sourceType)"
              v-if="!item.hidden"
              :align="item.align"
            >
              <el-input-number
                v-model="item.discountRation"
                :min="0"
                :max="1"
                :precision="3"
                :step="0.001"
                placeholder="请输入"
              />
            </el-descriptions-item>
          </template>
        </el-descriptions>

        <el-tag type="primary" class="mt10 mb10">AGIC</el-tag>

        <div class="agic">
          <el-descriptions :column="2" border direction="horizontal">
            <template
              v-for="item in productDiscountParams.customerNetworkList"
              :key="item.label"
            >
              <el-descriptions-item width="200px" :label="item.networkName">
                <el-input-number
                  v-model="item.discountRation"
                  :min="0"
                  :max="1"
                  :precision="3"
                  :step="0.001"
                  placeholder="请输入"
                />
              </el-descriptions-item>
            </template>
          </el-descriptions>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="productDiscountVisible = false">取消</el-button>
          <el-button @click="resetProductDiscount"> 重置 </el-button>
          <el-button type="primary" @click="updateCustomerDiscount">
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>
    <el-dialog v-model="voucherVisible" title="赠送代金券" center width="30%">
      <ProForm
        ref="voucherFormRef"
        v-model="voucherParams"
        :formColumns="voucherFormColumns"
        :label-width="120"
      />
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="voucherVisible = false">取消</el-button>
          <el-button type="primary" @click="addVoucher"> 确定 </el-button>
        </div>
      </template>
    </el-dialog>
    <el-dialog
      v-model="creditLineVisible"
      title="添加授信额"
      center
      width="30%"
    >
      <ProForm
        ref="creditLineFormRef"
        v-model="creditLineParams"
        :formColumns="creditLineFormColumns"
        :label-width="120"
      />
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="creditLineVisible = false">取消</el-button>
          <el-button type="primary" @click="addCreditLine"> 确定 </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts" name="UserManagement">
import {
  addCustomerVoucherApi,
  getUserListApi,
  giveCouponApi,
  updateCustomerDiscountApi,
  addCustomerCreditLineApi,
  openExtendApi,
  updateParentApi,
} from "@/api/userCenter";
import { useTable } from "@/hooks/useTable";
import { enumType } from "@/utils/Enum";
import Instance from "./components/Instance.vue";
import BillOverview from "./components/BillOverview.vue";
import BillDetails from "./components/BillDetails.vue";
import Balance from "./components/Balance.vue";
import { statusSelectEnum } from "@/utils/selectEnum";
import Container from "./components/Container.vue";
import { useHandleData } from "@/hooks/useHandleData";
import { ElMessageBox } from "element-plus";
import { promotionAmbassadorPageApi } from "@/api/promotionManagement";

const {
  tableData,
  refreshFn,
  pageData,
  getList,
  searchParam,
  searchFn,
  resetFn,
  addOrEdit,
  dataForm,
  openPopover,
  closePopover,
} = useTable({ title: "用户管理", api: getUserListApi });

const customerId = ref<string>("");

const columns: ColumnProps[] = [
  { prop: "accountId", label: "账号ID", width: 130, fixed: true },
  {
    prop: "namePhoneSearchKey",
    label: "账户信息",
    slot: true,
    search: { el: "input" },
    width: 180,
  },
  { prop: "parentInfo", label: "上级信息", slot: true, width: 180 },
  { prop: "instanceTotal", label: "实例资源创建数", width: 130 },
  { prop: "instanceRunNumber", label: "实例资源在用数", width: 130 },
  { prop: "balance", label: "余额", width: 100 },
  { prop: "voucherAmount", label: "代金券", width: 100 },
  { prop: "creditAmount", label: "信用额", width: 100 },
  {
    prop: "ECS",
    label: "服务器折扣",
    value: (row) => sourceTypeEnum(row, "ECS"),
    width: 120,
  },
  {
    prop: "CLOUD_STORAGE",
    label: "容器折扣",
    value: (row) => sourceTypeEnum(row, "VKE"),
    width: 100,
  },
  {
    prop: "CR",
    label: "镜像仓库折扣",
    value: (row) => sourceTypeEnum(row, "CR"),
    width: 115,
  },
  {
    prop: "CLOUD_OBJECT_STORAGE",
    label: "存储折扣",
    value: (row) => sourceTypeEnum(row, "CLOUD_OBJECT_STORAGE"),
    width: 100,
  },
  {
    prop: "CLOUD_NETWORK",
    label: "网络折扣",
    value: (row) => sourceTypeEnum(row, "CLOUD_NETWORK"),
    width: 100,
  },
  // {
  //   prop: "AGIC",
  //   label: "AGI-C",
  //   value: (row) => sourceTypeEnum(row, "AGIC"),
  //   width: 100,
  // },
  { prop: "totalConsumeAmount", label: "累计消费金额", width: 120 },
  { prop: "createTime", label: "创建时间", width: 170 },
  {
    prop: "status",
    label: "状态",
    slot: true,
    width: 80,
    search: { el: "select" },
    enum: statusSelectEnum,
    fixed: "right",
  },
  { prop: "operation", label: "操作", slot: true, width: 120, fixed: "right" },
];

const sourceTypeEnum = (row: any, type: TSourceType) => {
  let item = row.discountList.find((el: any) => el.sourceType === type);
  return item.discountRation === 0
    ? "0"
    : `${(item.discountRation * 100).toFixed(1)}%`;
};

//#region 设置产品折扣
// 设置产品折扣
const productDiscountVisible = ref(false);
// 设置产品折扣参数
const productDiscountParams = ref<ISetUpPDParams>({
  customerId: "",
  list: [],
  customerNetworkList: [],
});

//#region  重置产品折扣

const resetDiscount = <T extends { discountRation: number }>(arr: T[] = []) =>
  arr.map((item) => ({ ...item, discountRation: 1 }));
const resetProductDiscount = async () => {
  try {
    await ElMessageBox.confirm(`是否重置产品折扣?`, "温馨提示", {
      type: "warning",
    });
    const { list, customerNetworkList, ...rest } = unref(productDiscountParams);
    const params: ISetUpPDParams = {
      ...rest,
      list: resetDiscount(list),
      customerNetworkList: resetDiscount(customerNetworkList),
    };
    await updateCustomerDiscountApi(params);
    productDiscountVisible.value = false;
    ElMessage.success("设置成功");
    getList();
  } catch (error) {}
};
//#endregion

const openSetUpPD = (row: any) => {
  const excludedSourceTypes = [
    "CLOUD_IMAGE",
    "CLB",
    "NAT_GATEWAY",
    "CONTAINER",
    "AGIC",
  ] as const;
  const productDiscountEnum = row.discountList
    .filter((el: TKeyValue) => !excludedSourceTypes.includes(el.sourceType))
    .map(({ sourceType, discountRation }: TKeyValue) => ({
      sourceType,
      discountRation,
    }));
  productDiscountParams.value = {
    customerId: row.id,
    list: productDiscountEnum,
    customerNetworkList: row.customerNetworkList.map((item: TKeyValue) => ({
      ...item,
      discountRation: item.discountRation ?? 1,
    })),
  };
  productDiscountVisible.value = true;
};

const updateCustomerDiscount = async () => {
  await updateCustomerDiscountApi(productDiscountParams.value);
  productDiscountVisible.value = false;
  ElMessage.success("设置成功");
  getList();
};

watch(
  () => productDiscountVisible.value,
  () => {
    if (!productDiscountVisible.value) {
      productDiscountParams.value = {
        customerId: "",
        list: [],
        customerNetworkList: [],
      };
    }
  }
);

//#endregion

//#region 代金券
const voucherFormRef = ref<any>(null);
const voucherVisible = ref(false);
const voucherParams = ref<TKeyValue>({});

// 打开赠送代金券弹窗
const openVoucher = (id: string) => {
  voucherParams.value.customerId = id;
  voucherVisible.value = true;
};
// 添加代金券
const addVoucher = () => {
  if (!voucherFormRef.value.formRef) return;
  if (Date.now() > new Date(voucherParams.value.useTimeEnd).getTime())
    return ElMessage.error("使用截止日期不能早于当前时间");
  voucherFormRef.value.formRef.validate(async (valid: boolean) => {
    if (!valid) return false;
    await addCustomerVoucherApi(voucherParams.value);
    ElMessage.success("操作成功");
    getList();
    voucherVisible.value = false;
  });
};
// 代金券表单
const voucherFormColumns: IFormColumnsProps[] = [
  { label: "代金券名称", prop: "voucherName", el: "input" },
  {
    label: "使用截止日期",
    prop: "useTimeEnd",
    el: "date-picker",
    dateType: "datetime",
    valueFormat: "YYYY-MM-DD HH:mm:ss",
  },
  { label: "代金券金额", prop: "totalAmount", el: "price" },
];
watch(
  () => voucherVisible.value,
  (val) => {
    if (!val) {
      voucherParams.value = {};
    }
  }
);

//#endregion

//#region 授信额
const creditLineVisible = ref(false);
const creditLineFormRef = ref<any>(null);
const creditLineParams = ref<TKeyValue>({});
// 打开添加授信额弹窗
const openCreditLine = (id: string) => {
  creditLineParams.value.customerId = id;
  creditLineVisible.value = true;
};
// 添加授信额
const addCreditLine = () => {
  if (!creditLineFormRef.value.formRef) return;
  if (Date.now() > new Date(creditLineParams.value.useTimeEnd).getTime())
    return ElMessage.error("使用截止日期不能早于当前时间");
  creditLineFormRef.value.formRef.validate(async (valid: boolean) => {
    if (!valid) return false;
    await addCustomerCreditLineApi(creditLineParams.value);
    ElMessage.success("操作成功");
    getList();
    creditLineVisible.value = false;
  });
};
// 授信额表单
const creditLineFormColumns: IFormColumnsProps[] = [
  {
    label: "使用截止日期",
    prop: "useTimeEnd",
    el: "date-picker",
    dateType: "datetime",
    valueFormat: "YYYY-MM-DD HH:mm:ss",
  },
  { label: "授信额", prop: "totalAmount", el: "price" },
];

watch(
  () => creditLineVisible.value,
  (val) => {
    if (!val) {
      creditLineParams.value = {};
    }
  }
);

//#endregion

//#region 操作按钮
const operationBtn: IOperationBtnItem[] = [
  {
    label: "设置产品折扣",
    click: (row) => openSetUpPD(row),
    show: () => true,
  },
  {
    label: "赠送代金券",
    click: (row) => openVoucher(row.id),
    show: () => true,
  },
  {
    label: "添加授信额",
    click: (row) => openCreditLine(row.id),
    show: () => true,
  },
  {
    label: "指定赠券",
    click: (row) => openCoupon(row.id),
    show: () => true,
  },
  {
    label: "修改上级",
    click: (row) => openSetParent(row.id),
    show: () => true,
  },
  {
    label: "成为推广大使",
    click: (row) => openExtendFn(row.id, row.customerName),
    show: (row) => !row.extend,
  },
];
//#endregion

//#region 设置上级
// 设置上级参数
const setParentForm = ref<TKeyValue>({});
const setParentVisible = ref(false);

const setParentFormColumns: IFormColumnsProps[] = [
  {
    label: "选择上级",
    prop: "parenId",
    el: "selectPage",
    selectLabel: "userName",
    selectValue: "id",
    pageFn: promotionAmbassadorPageApi,
  },
];
// 修改上级
const openSetParent = (nextId: string) => {
  setParentForm.value.nextId = nextId;
  setParentVisible.value = true;
};
const setParentFn = async () => {
  await updateParentApi(setParentForm.value);
  ElMessage.success("操作成功");
  setParentVisible.value = false;
  getList();
};

//#endregion

// 成为推广大使
const openExtendFn = async (id: string, customerName: string) => {
  const msg = `对账号为【${customerName}】的用户进行成为推广大使操作`;
  await useHandleData(openExtendApi, id, msg);
  getList();
};

//#region 优惠券
const couponVisible = ref(false);
const setCouponVisible = ref(false);
const couponList = ref<any[]>([]);
const couponIds = computed(() => couponList.value.map((item: any) => item.id));

const openCoupon = (id: string) => {
  customerId.value = id;
  setCouponVisible.value = true;
};
const addCoupon = (val: TKeyValue[]) => (couponList.value = val);
const couponColumns: ColumnProps[] = [
  { prop: "name", label: "优惠券名称" },
  {
    prop: "type",
    label: "优惠券类型",
    value: (row: any) => enumType("couponTypeEnum", row.type),
  },

  { prop: "thresholdAmount", label: "满足金额" },
  { prop: "deductionAmount", label: "抵扣金额" },
  {
    prop: "rangeType",
    label: "使用范围",
    value: (row: any) => enumType("couponRangeEnum", row.rangeType),
  },
  { prop: "count", label: "优惠券数量" },
  {
    prop: "useTimeStart",
    label: "有效期",
    value: (row: any) => row.useTimeStart + "--" + row.useTimeEnd,
    width: 320,
  },
  { prop: "operation", label: "操作", slot: true },
];

watch(
  () => setCouponVisible.value,
  (val) => {
    if (!val) couponList.value = [];
  }
);

const delCoupon = (row: any) =>
  (couponList.value = couponList.value.filter(
    (item: any) => item.id !== row.id
  ));

// 赠送优惠券
const giveCouponFn = async () => {
  await giveCouponApi(customerId.value, couponIds.value);
  ElMessage.success("赠送成功");
  setCouponVisible.value = false;
  getList();
};
//#endregion

const userList = computed(() => [
  { label: "姓名", value: dataForm.value.customerName },
  { label: "手机号", value: dataForm.value.phone },
  { label: "账号状态", value: enumType("statusEnum", dataForm.value.status) },
  { label: "创建时间", value: dataForm.value.createTime },
]);

const tableType = ref("billOverview");
const radioGroup = [
  { value: "billOverview", label: "账单总览" },
  { value: "billDetails", label: "账单明细" },
  { value: "balance", label: "充值订单" },
  { value: "instance", label: "实例" },
  { value: "container", label: "容器" },
];
watch(
  () => addOrEdit.value,
  () => (tableType.value = "billOverview")
);
</script>
<style lang="scss" scoped>
.flex {
  display: flex;
}
.agic {
  max-height: 260px;
  overflow: auto;
}
</style>
