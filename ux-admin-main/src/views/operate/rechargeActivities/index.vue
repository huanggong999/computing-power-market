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
        <el-button @click="openPopover('add', { status: 1 })"> 新增 </el-button>
      </template>
      <template #status="row">
        <el-tag :type="enumTag('statusNumTag', row.status)">
          {{ enumType("statusNumEnum", row.status) }}
        </el-tag>
      </template>
      <template #operation="row">
        <el-button
          link
          type="primary"
          @click="openPopover('edit', rechargeGetDetailsApi, row.id)"
        >
          编辑
        </el-button>
        <el-button
          link
          type="danger"
          @click="removeFn(rechargeDeleteApi, row.id, row.name)"
        >
          删除
        </el-button>
        <el-button link type="primary" @click="openActivityLog(row.id)">
          参与记录
        </el-button>
      </template>
    </ProTable>
    <Drawer
      v-model="addOrEdit"
      :title="popoverTitle"
      @closePopover="closePopover"
      :disabled="disabled"
      @submit="saveRechargeReceiver"
    >
      <ProForm
        ref="proFormRef"
        v-model="dataForm"
        :formColumns="formColumns"
        :disabled="disabled"
        :label-width="150"
      >
        <template #receivers>
          <div>
            <el-button type="primary" @click="addRechargeReceiver">
              添加充值奖励层级
            </el-button>
            <div>
              <div v-for="(item, index) in receivers" :key="index">
                <el-tag class="mt10">{{ item.level }}级权益</el-tag>
                <div class="flx-justify-between mt10">
                  <div class="label mr10">充值金额</div>
                  <el-input-number :precision="2" v-model="item.rechargeAmount">
                    <template #prefix>满</template>
                    <template #suffix>元</template>
                  </el-input-number>
                </div>
                <div class="flx-justify-between mt10">
                  <div class="label mr10">送代金券</div>
                  <el-input-number :precision="2" v-model="item.couponAmount">
                    <template #prefix>送</template>
                    <template #suffix>元</template>
                  </el-input-number>
                </div>
              </div>
            </div>
          </div>
        </template>
      </ProForm>
    </Drawer>

    <ParticipationRecord v-model="activityLog" :rechargeId="activityLogId" />
  </div>
</template>

<script setup lang="ts" name="RechargeActivities">
import {
  rechargeDeleteApi,
  rechargeGetDetailsApi,
  rechargePageApi,
  rechargeSaveApi,
  rechargeUpdateApi,
} from "@/api/operationsManagement";
import { useTable } from "@/hooks/useTable";
import { statusNumberEnum } from "@/utils/radioEnum";
import { rechargeActivitiesSelectEnum } from "@/utils/selectEnum";
import ParticipationRecord from "./components/ParticipationRecord.vue";
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
  openPopover,
  addOrEdit,
  popoverTitle,
  closePopover,
  disabled,
  submit,
  proFormRef,
  dataForm,
  removeFn,
} = useTable({ api: rechargePageApi, title: "充值活动" });
const columns: ColumnProps[] = [
  { prop: "id", label: "ID" },
  { prop: "name", label: "活动名称", search: { el: "input" } },
  {
    prop: "rechargeStartTime",
    label: "有效期",
    value: (row: any) => row.rechargeStartTime + "--" + row.rechargeEndTime,
    search: {
      el: "date-picker",
      dateType: "daterange",
      dateEnum: ["startTime", "endTime"],
      valueFormat: "YYYY-MM-DD",
    },
  },
  {
    prop: "status",
    label: "状态",
    slot: true,
    search: { el: "select" },
    enum: rechargeActivitiesSelectEnum,
  },
  { prop: "createBy", label: "发布者" },
  { prop: "operation", label: "操作", slot: true },
];
const formColumns: IFormColumnsProps[] = [
  { prop: "name", label: "充值活动名称", el: "input" },
  {
    prop: "rechargeStartTime",
    label: "充值奖励活动周期",
    el: "date-picker",
    dateType: "datetimerange",
    dateEnum: ["rechargeStartTime", "rechargeEndTime"],
    valueFormat: "YYYY-MM-DD HH:mm:ss",
  },
  {
    prop: "couponStartTime",
    label: "奖励代金券有效日期",
    el: "date-picker",
    dateType: "datetimerange",
    dateEnum: ["couponStartTime", "couponEndTime"],
    valueFormat: "YYYY-MM-DD HH:mm:ss",
  },
  { label: "状态", prop: "status", el: "radio", radioList: statusNumberEnum },
  { prop: "receivers", label: "充值奖励", el: "slot" },
];
const receivers = ref<TKeyValue[]>([]);
const addRechargeReceiver = () => {
  receivers.value.push({
    level: receivers.value.length + 1,
    rechargeAmount: null,
    couponAmount: null,
  });
};
watch(receivers, (newVal) => (dataForm.value.receivers = newVal), {
  deep: true,
});
watch(
  () => dataForm.value.id,
  (newVal) => {
    if (newVal) receivers.value = dataForm.value.receivers || [];
  }
);
watch(addOrEdit, (newVal) => {
  if (!newVal) receivers.value = [];
});

const saveRechargeReceiver = () => {
  const isComplete = receivers.value.every(
    (item) => !!item.rechargeAmount && !!item.couponAmount
  );
  if (!isComplete) return ElMessage.error("请填写完整");
  submit({ addSubmitApi: rechargeSaveApi, editSubmitApi: rechargeUpdateApi });
};

const activityLog = ref(false);
const activityLogId = ref("");
const openActivityLog = (id: string) => {
  activityLog.value = true;
  activityLogId.value = id;
};
</script>
<style lang="scss" scoped>
.label {
  flex-shrink: 0;
}
.el-input-number {
  width: 100%;
}
</style>
