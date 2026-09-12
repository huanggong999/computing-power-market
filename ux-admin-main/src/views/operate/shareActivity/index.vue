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
        <el-button @click="openPopover('add')"> 新增 </el-button>
      </template>
      <template #status="row">
        <el-tag :type="enumTag('productStatusTag', row.status)">
          {{ enumType("activityStatusEnum", row.status) }}
        </el-tag>
      </template>
      <template #operation="row">
        <el-button
          link
          type="primary"
          @click="openPopover('edit', activeCenterGetActiveDetailsApi, row.id)"
        >
          编辑
        </el-button>
        <el-button
          link
          type="danger"
          @click="removeFn(activeCenterDeleteApi, row.id, row.name)"
        >
          删除
        </el-button>
        <el-button link type="primary" @click="openActivityLog(row.id)">
          活动记录
        </el-button>
      </template>
    </ProTable>
    <Drawer
      v-model="addOrEdit"
      :title="popoverTitle"
      @closePopover="closePopover"
      :disabled="disabled"
      @submit="saveActivity"
      size="66%"
      :isSave="!dataForm.id"
      @save="save"
    >
      <ProForm
        ref="proFormRef"
        v-model="dataForm"
        :formColumns="formColumns"
        :disabled="disabled"
        :label-width="150"
      >
        <template #couponIds>
          <div>
            <el-button
              class="mb20"
              type="primary"
              @click="openComCoupon('signUpForFree')"
            >
              指定优惠券
            </el-button>
            <SelectCoupon
              :tableData="signUpForFreeList"
              @delCoupon="(row) => delCoupon(row, 'signUpForFree')"
            />
          </div>
        </template>
        <template #couponIds2>
          <div>
            <p>
              <span class="mr10">下单达标金额</span>
              <el-input-number
                v-model="dataForm.targetAmount"
                :precision="2"
                :controls="false"
              >
                <template #suffix>
                  <span>元</span>
                </template>
              </el-input-number>
            </p>

            <el-button
              type="primary"
              class="mb20"
              @click="openComCoupon('freeGiftForOrder')"
            >
              指定优惠券
            </el-button>

            <SelectCoupon
              :tableData="freeGiftForOrderList"
              @delCoupon="(row) => delCoupon(row, 'freeGiftForOrder')"
            />
          </div>
        </template>
      </ProForm>
    </Drawer>

    <Coupon v-model="couponVisible" @submit="addCoupon" />

    <ActivityLog v-model="activityLog" :activeId="activityLogId" />
  </div>
</template>

<script setup lang="ts" name="ShareActivity">
import {
  activeCenterDeleteApi,
  activeCenterGetActiveDetailsApi,
  activeCenterPageApi,
  activeCenterSaveApi,
  activeCenterUpdateApi,
} from "@/api/operationsManagement";
import { useTable } from "@/hooks/useTable";
import SelectCoupon from "./components/SelectCoupon.vue";
import { enumType } from "@/utils/Enum";
import { activityStatusSelectEnum } from "@/utils/selectEnum";
import ActivityLog from "./components/ActivityLog.vue";
import { enumTag } from "@/utils/enumTag";

const {
  tableData,
  pageData,
  searchParam,
  refreshFn,
  getList,
  searchFn,
  resetFn,
  openPopover,
  addOrEdit,
  popoverTitle,
  closePopover,
  disabled,
  submit,
  proFormRef,
  dataForm,
  removeFn,
  save,
} = useTable({ api: activeCenterPageApi, title: "分享活动" });

const columns: ColumnProps[] = [
  { prop: "id", label: "ID" },
  { prop: "name", label: "活动名称", search: { el: "input" } },
  {
    prop: "",
    label: "活动有效期",
    value: (row: any) => row.startDate + "--" + row.endDate,
    search: {
      el: "date-picker",
      dateType: "daterange",
      dateEnum: ["startDay", "endDay"],
      valueFormat: "YYYY-MM-DD",
    },
  },
  { prop: "targetAmount", label: "下单达标金额" },
  { prop: "createTime", label: "创建时间" },
  {
    prop: "status",
    label: "状态",
    slot: true,
    search: { el: "select" },
    enum: activityStatusSelectEnum,
  },
  { prop: "operation", label: "操作", slot: true },
];
const formColumns: IFormColumnsProps[] = [
  { label: "活动名称", prop: "name", el: "input" },
  {
    label: "活动有效期",
    prop: "startDate",
    el: "date-picker",
    dateType: "daterange",
    dateEnum: ["startDate", "endDate"],
    valueFormat: "YYYY-MM-DD",
  },

  { label: "活动封面图", prop: "picture", el: "img" },
  { label: "活动详情", prop: "details", el: "wangEditor" },
  {
    label: "通过链接注册用户赠送邀请人",
    prop: "couponIds",
    required: false,
    el: "slot",
  },
  {
    label: "通过链接注册用户，下单达标后，赠送邀请人",
    prop: "couponIds2",
    required: false,
    el: "slot",
  },
];

const signUpForFreeList = ref([]);
const freeGiftForOrderList = ref([]);

const couponVisible = ref(false);
const couponMap = {
  signUpForFree: signUpForFreeList,
  freeGiftForOrder: freeGiftForOrderList,
};
type TOpenType = keyof typeof couponMap;
const openType = ref<TOpenType>("signUpForFree");

const openComCoupon = (type: TOpenType) => {
  openType.value = type;
  couponVisible.value = true;
};

const addCoupon = (row: any) => (couponMap[openType.value].value = row);

const delCoupon = (row: any, type: TOpenType) => {
  couponMap[type].value = couponMap[type].value.filter(
    (item: any) => item.id !== row.id
  );
};

watchEffect(() => {
  dataForm.value.couponIds = signUpForFreeList.value.map(
    (item: any) => item.id
  );
  dataForm.value.couponIds2 = freeGiftForOrderList.value.map(
    (item: any) => item.id
  );
});

watch(
  () => addOrEdit.value,
  (val) => {
    if (!val) {
      signUpForFreeList.value = [];
      freeGiftForOrderList.value = [];
    }
  }
);
watch(
  () => dataForm.value.id,
  (val) => {
    if (val) {
      signUpForFreeList.value = dataForm.value.list;
      freeGiftForOrderList.value = dataForm.value.list2;
      delete dataForm.value.list;
      delete dataForm.value.list2;
    }
  }
);

const activityLog = ref(false);
const activityLogId = ref("");
const openActivityLog = (id: string) => {
  activityLog.value = true;
  activityLogId.value = id;
};

const saveActivity = () => {
  if (
    signUpForFreeList.value.length === 0 &&
    freeGiftForOrderList.value.length === 0
  )
    return ElMessage.error("请选择优惠券");
  submit({
    addSubmitApi: activeCenterSaveApi,
    editSubmitApi: activeCenterUpdateApi,
  });
  localStorage.removeItem("分享活动");
};
</script>
<style lang="scss" scoped></style>
