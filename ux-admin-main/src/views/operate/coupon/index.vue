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
      <template #tableHeader>
        <el-button @click="openPopover('add', defaultDataForm)">
          新增
        </el-button>
      </template>
      <template #status="row">
        <el-tag :type="enumTag('statusTag', row.status)">
          {{ enumType("statusEnum", row.status) }}
        </el-tag>
      </template>
      <template #operation="row">
        <el-button
          link
          type="primary"
          @click="openPopover('edit', couponDetailApi, row.id)"
        >
          编辑
        </el-button>
        <el-button
          link
          type="danger"
          @click="removeFn(couponDeleteApi, row.id)"
        >
          删除
        </el-button>
      </template>
    </ProTable>
    <Drawer
      v-model="addOrEdit"
      :title="popoverTitle"
      @closePopover="closePopover"
      :disabled="disabled"
      @submit="
        submit({ addSubmitApi: couponSaveApi, editSubmitApi: couponUpdateApi })
      "
      :isSave="!dataForm.id"
      @save="save"
    >
      <ProForm
        ref="proFormRef"
        v-model="dataForm"
        :formColumns="formColumns"
        :disabled="disabled"
        :label-width="100"
      />
    </Drawer>
  </div>
</template>

<script setup lang="ts" name="Coupon">
import {
  couponDeleteApi,
  couponDetailApi,
  couponPageApi,
  couponSaveApi,
  couponUpdateApi,
} from "@/api/coupon";
import { useTable } from "@/hooks/useTable";
import { enumType } from "@/utils/Enum";
import { enumTag } from "@/utils/enumTag";
import {
  couponConditionRadioEnum,
  couponScopeRadioEnum,
  couponTypeRadioEnum,
  StatusEnum,
} from "@/utils/radioEnum";
import { statusSelectEnum } from "@/utils/selectEnum";

const {
  tableData,
  pageData,
  searchParam,
  refreshFn,
  getList,
  openPopover,
  addOrEdit,
  popoverTitle,
  closePopover,
  disabled,
  submit,
  proFormRef,
  dataForm,
  removeFn,
  searchFn,
  resetFn,
  save,
} = useTable({ api: couponPageApi, title: "优惠券" });

const defaultDataForm = { type: "FULL_REDUCTION", status: "OK", sort: 0 };

const columns: ColumnProps[] = [
  { prop: "name", label: "优惠券名称", width: 130 },
  {
    prop: "type",
    label: "优惠券类型",
    width: 100,
    value: (row: any) => enumType("couponTypeEnum", row.type),
  },
  { prop: "thresholdAmount", label: "满足金额", width: 90 },
  { prop: "deductionAmount", label: "抵扣金额", width: 90 },
  { prop: "receiveNum", label: "领取次数", width: 90 },
  {
    prop: "receiveTimeStart",
    label: "领取时间",
    width: 330,
    value: (row: any) => row.receiveTimeStart + "--" + row.receiveTimeEnd,
  },
  {
    prop: "useTimeStart",
    label: "使用时间",
    width: 330,
    search: {
      el: "date-picker",
      dateType: "daterange",
      dateEnum: ["useStartTime", "useEndTime"],
      valueFormat: "YYYY-MM-DD HH:mm:ss",
    },
    value: (row: any) => row.useTimeStart + "--" + row.useTimeEnd,
  },
  {
    prop: "receiveType",
    label: "领取条件",
    width: 100,
    value: (row: any) => enumType("couponReceiveTypeEnum", row.receiveType),
  },
  { prop: "count", label: "优惠券数量", width: 100 },
  {
    prop: "rangeType",
    label: "使用范围",
    width: 90,
    value: (row: any) => enumType("couponRangeEnum", row.rangeType),
  },
  {
    prop: "status",
    label: "状态",
    search: { el: "select" },
    enum: statusSelectEnum,
    slot: true,
    width: 80,
  },
  { prop: "sort", label: "排序", width: 60 },
  { prop: "couponRemark", label: "优惠券备注", width: 130 },
  { prop: "createBy", label: "发布者" },
  { prop: "operation", label: "操作", slot: true, width: 120, fixed: "right" },
];
const formColumns: IFormColumnsProps[] = [
  { label: "优惠券名称", prop: "name", el: "input" },
  {
    label: "优惠券类型",
    prop: "type",
    el: "radio",
    radioList: couponTypeRadioEnum,
  },
  { label: "满足金额", prop: "thresholdAmount", el: "price" },
  { label: "抵扣金额", prop: "deductionAmount", el: "price" },
  { label: "领取次数", prop: "receiveNum", el: "number" },
  {
    label: "领取时间",
    prop: "receiveTimeStart",
    el: "date-picker",
    dateType: "datetimerange",
    dateEnum: ["receiveTimeStart", "receiveTimeEnd"],
    valueFormat: "YYYY-MM-DD HH:mm:ss",
  },
  {
    label: "使用时间",
    prop: "receiveTimeStart",
    el: "date-picker",
    dateType: "datetimerange",
    dateEnum: ["useTimeStart", "useTimeEnd"],
    valueFormat: "YYYY-MM-DD HH:mm:ss",
  },
  {
    label: "领取条件",
    prop: "receiveType",
    el: "radio",
    radioList: couponConditionRadioEnum,
  },
  { label: "优惠券数量", prop: "count", el: "number" },
  {
    label: "使用范围",
    prop: "rangeType",
    el: "radio",
    radioList: couponScopeRadioEnum,
  },
  { label: "状态", prop: "status", el: "radio", radioList: StatusEnum },
  { label: "排序", prop: "sort", el: "number" },
  {
    label: "优惠券备注",
    prop: "couponRemark",
    el: "textarea",
    required: false,
    maxLength: 30,
  },
];
</script>
<style lang="scss" scoped></style>
