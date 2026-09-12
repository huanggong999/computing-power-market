<template>
  <ProTable
    :columns="columns"
    :tableData="tableData"
    :pageData="pageData"
    :search-param="searchParam"
    :refreshFn="refreshFn"
    :getList="getList"
    :searchFn="searchFn"
    :resetFn="resetFn"
    :maxHeight="500"
    type="none"
  >
    <template #instanceName="row">
      <div class="instance-name">
        <div class="instance-id">{{ row.instanceId ?? "--" }}</div>
        <div class="instance-name-text">{{ row.instanceName ?? "--" }}</div>
      </div>
    </template>
    <template #operation="row">
      <div
        v-if="
          ![
            'CREATING',
            'STOPPING',
            'REBOOTING',
            'STARTING',
            'REBUILDING',
            'RESIZING',
            'ERROR',
            'DELETING',
          ].includes(row.status)
        "
      >
        <el-button
          v-if="!['STOPPED'].includes(row.status)"
          type="primary"
          link
          @click="operation('stop', row)"
        >
          停止
        </el-button>
        <el-button v-else type="primary" link @click="operation('delete', row)">
          删除
        </el-button>
      </div>
      <span v-else> --- </span>
    </template>
  </ProTable>

  <el-dialog
    v-model="stopVisible"
    title="停止实例"
    width="30%"
    center
    :destroy-on-close="true"
    :before-close="closeVisible"
  >
    <el-form ref="form" :model="operationParams" label-width="80px">
      <el-form-item label="停机方式">
        <el-radio-group v-model="operationParams.stoppedMode">
          <el-radio value="KeepCharging">普通停机模式</el-radio>
          <el-radio value="StopCharging">节省停机模式</el-radio>
        </el-radio-group>
        <div class="tip">
          {{ stopTip[operationParams.stoppedMode as TStopTip] }}
        </div>
      </el-form-item>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="closeVisible">取消</el-button>
        <el-button type="primary" @click="stopInstances"> 确定 </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts" name="Instance">
import {
  deletedInstancesApi,
  getInstancePageListApi,
  stopInstancesApi,
} from "@/api/userCenter";
import { useTable } from "@/hooks/useTable";
import { enumType } from "@/utils/Enum";
import { ElMessageBox } from "element-plus";

const props = defineProps<{ customerId: string | number | undefined }>();
const {
  tableData,
  pageData,
  getList,
  searchParam,
  searchFn,
  resetFn,
  refreshFn,
} = useTable({
  api: getInstancePageListApi,
  initParams: { customerId: props.customerId },
});
const columns: ColumnProps[] = [
  { prop: "instanceName", label: "ID/名称", slot: true },
  {
    prop: "status",
    label: "状态",
    value: (row: any) => enumType("instanceStatusEnum", row.status),
  },
  {
    prop: "zone",
    label: "可用区",
    value: (row: any) => row.zone?.name ?? "--",
  },
  { prop: "osName", label: "镜像" },
  { prop: "cale", label: "规格" },
  {
    prop: "volumeType",
    label: "系统盘",
    value: (row: any) => row.systemVolume?.volumeType ?? "--",
  },
  {
    prop: "ipAddress",
    label: "主IPv4地址",
    value: (row: any) => row.eipAddress?.ipAddress ?? "--",
  },
  // { prop: "", label: "实例计费类型" },
  {
    prop: "chargeType",
    label: "续费方式",
    value: (row: any) => enumType("billingTypeEnum", row.chargeType),
  },
  { prop: "operation", label: "操作", slot: true },
];

const stopVisible = ref(false);
const closeVisible = () => (stopVisible.value = false);
const openVisible = () => (stopVisible.value = true);

// 存放操作相关信息
const operationName = ref<string>("");
const operationSystemVolume = ref<string>("");
const operationParams = ref<TKeyValue>({});

const deletedInstances = async (ids: string[]) => {
  await ElMessageBox.confirm(`是否删除${operationName.value}?`, "温馨提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
    draggable: true,
  });
  await deletedInstancesApi(operationSystemVolume.value, ids);
  ElMessage.success(`删除${operationName.value}成功`);
  getList();
};
const stopInstances = async () => {
  await stopInstancesApi(operationSystemVolume.value, operationParams.value);
  ElMessage.success(`停止${operationName.value}成功`);
  closeVisible();
  getList();
};

const operationEnum = { stop: openVisible, delete: deletedInstances } as const;
type TOperationType = keyof typeof operationEnum;

const stopTip = {
  KeepCharging: "停机后实例及其相关资源仍被保留且持续计费，费用和停机前一致。",
  StopCharging:
    "停机后实例的计算资源（vCPU、GPU和内存）将被回收且停止计费，所挂载的云盘、镜像、公网IP仍被保留且持续计费。",
};
type TStopTip = keyof typeof stopTip;
const operation = (type: TOperationType, params: TKeyValue) => {
  const { systemVolume, id, instanceName } = params;
  operationName.value = instanceName;
  operationSystemVolume.value = systemVolume.regionId;
  operationParams.value = {
    idList: [id],
    statusEnum: "RUNNING",
    stoppedMode: "KeepCharging",
  };
  operationEnum[type]([id]);
};
</script>
<style lang="scss" scoped></style>
