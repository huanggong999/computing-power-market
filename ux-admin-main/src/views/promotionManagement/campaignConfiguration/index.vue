<template>
  <div class="table-box card">
    <ProForm v-model="dataForm" :formColumns="formColumns" :label-width="160">
      <template #couponList>
        <el-button type="primary" @click="couponVisible = true">
          赠送
        </el-button>
        <div class="mt20" v-if="!!dataForm.couponList">
          <ProTable
            type="none"
            :columns="couponColumns"
            :table-data="dataForm.couponList"
            :is-page="false"
            :-is-refresh="false"
          >
            <template #operation="row">
              <el-button type="primary" text @click="delCoupon(row)">
                移除
              </el-button>
            </template>
          </ProTable>
        </div>
      </template>
    </ProForm>
    <div class="flx-center">
      <el-button type="primary" @click="submit"> 保存 </el-button>
    </div>

    <Coupon v-model="couponVisible" @submit="addCoupon" />
  </div>
</template>

<script setup lang="ts" name="CampaignConfiguration">
import {
  promotionActivityDetailApi,
  promotionActivityUpdateApi,
} from "@/api/promotionManagement";
import { enumType } from "@/utils/Enum";
import { statusNumberEnum } from "@/utils/radioEnum";

const formColumns: IFormColumnsProps[] = [
  {
    label: "活动开启状态",
    prop: "status",
    el: "radio",
    radioList: statusNumberEnum,
  },
  {
    label: "活动持续时间",
    prop: "endTime",
    el: "date-picker",
    dateType: "datetime",
    valueFormat: "YYYY-MM-DD HH:mm:ss",
  },
  { label: "通过链接注册新用户", prop: "couponList", el: "slot" },
];

const dataForm = ref<TKeyValue>({});
const getConfig = async () => {
  const { data } = await promotionActivityDetailApi();
  dataForm.value = data;
};
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
  { prop: "count", label: "优惠卷数量" },
  {
    prop: "useTimeStart",
    label: "有效期",
    value: (row: any) => row.useTimeStart + "--" + row.useTimeEnd,
    width: 320,
  },
  { prop: "operation", label: "操作", slot: true },
];
const couponVisible = ref(false);

const addCoupon = (val: any[]) => (dataForm.value.couponList = val);
const delCoupon = (row: any) =>
  (dataForm.value.couponList = dataForm.value.couponList.filter(
    (item: any) => item.id !== row.id
  ));

const submit = async () => {
  await promotionActivityUpdateApi(dataForm.value);
  ElMessage.success("保存成功");
  getConfig();
};

onMounted(() => getConfig());
</script>
<style lang="scss" scoped></style>
