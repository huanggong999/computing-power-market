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
    >
      <template #tableHeader>
        <el-button @click="openDrawer('add')"> 新增资源 </el-button>
      </template>

      <template #status="row">
        <el-tag :type="getStatusTagType(row.status)">
          {{ getStatusText(row.status) }}
        </el-tag>
      </template>

      <template #stock="row">
        <span>{{ row.availableCount }}/{{ row.totalCount }}</span>
      </template>

      <template #price="row">
        <span>¥{{ row.unitPrice }}</span>
        <span v-if="row.discountPrice" class="discount-price">
          ¥{{ row.discountPrice }}
        </span>
      </template>

      <template #operation="row">
        <el-button link type="primary" @click="openDrawer('edit', row)">
          编辑
        </el-button>
        <el-button link type="primary" @click="toggleStatus(row)">
          {{ row.status === 1 ? "下架" : "上架" }}
        </el-button>
        <el-button
          link
          type="danger"
          @click="removeFn(gpuResourceDeleteApi, row.id, row.machineId)"
        >
          删除
        </el-button>
      </template>
    </ProTable>

    <ResourceDrawer
      v-model="drawerVisible"
      :title="drawerTitle"
      :data="drawerData"
      @submit="handleSubmit"
      @close="closeDrawer"
    />
  </div>
</template>

<script setup lang="ts" name="GpuResource">
import {
  gpuResourceDeleteApi,
  gpuResourceDetailApi,
  gpuResourcePageApi,
  gpuResourceSaveApi,
  gpuResourceUpdateApi,
  gpuResourceStatusApi,
} from "@/api/gpuResource";
import { gpuSpecListApi } from "@/api/gpuSpec";
import { gpuRegionListApi } from "@/api/gpuRegion";
import { gpuZoneListApi } from "@/api/gpuZone";
import { useTable } from "@/hooks/useTable";
import ResourceDrawer from "./components/ResourceDrawer.vue";

const {
  tableData,
  pageData,
  searchParam,
  refreshFn,
  getList,
  searchFn,
  resetFn,
  removeFn,
} = useTable({
  api: gpuResourcePageApi,
  title: "GPU资源",
});

// 下拉数据
const specList = ref([]);
const regionList = ref([]);
const zoneList = ref([]);

const loadSelectData = async () => {
  const [specRes, regionRes, zoneRes] = await Promise.all([
    gpuSpecListApi(),
    gpuRegionListApi(),
    gpuZoneListApi(),
  ]);
  specList.value = specRes.data || [];
  regionList.value = regionRes.data || [];
  zoneList.value = zoneRes.data || [];
};

onMounted(() => {
  loadSelectData();
});

const columns: ColumnProps[] = [
  { prop: "resourceNo", label: "资源编号", search: { el: "input" } },
  { prop: "machineId", label: "机器ID", search: { el: "input" } },
  { prop: "machineUuid", label: "机器UUID" },
  { prop: "model", label: "GPU型号", search: { el: "select", key: "specId" }, enum: specList, fieldNames: { label: "model", value: "id" } },
  { prop: "vram", label: "显存" },
  { prop: "regionName", label: "地区", search: { el: "select", key: "regionCode" }, enum: regionList, fieldNames: { label: "regionName", value: "regionCode" } },
  { prop: "zoneName", label: "专区", search: { el: "select", key: "zoneCode" }, enum: zoneList, fieldNames: { label: "zoneName", value: "zoneCode" } },
  { prop: "cpuCores", label: "CPU核数" },
  { prop: "cpuModel", label: "CPU型号" },
  { prop: "memorySize", label: "内存大小" },
  { prop: "stock", label: "库存", slot: true },
  { prop: "price", label: "价格", slot: true },
  { prop: "rentableUntil", label: "可租至" },
  {
    prop: "status",
    label: "状态",
    slot: true,
    search: { el: "select" },
    enum: [
      { value: 1, label: "上架" },
      { value: 2, label: "下架" },
      { value: 3, label: "维护中" },
    ],
  },
  { prop: "operation", label: "操作", slot: true, width: 200 },
];

// Drawer相关
const drawerVisible = ref(false);
const drawerTitle = ref("");
const drawerData = ref({});

const openDrawer = async (type: "add" | "edit", row?: any) => {
  drawerTitle.value = type === "add" ? "新增GPU资源" : "编辑GPU资源";
  if (type === "edit" && row?.id) {
    const res = await gpuResourceDetailApi(row.id);
    drawerData.value = res.data || { ...row };
  } else {
    drawerData.value = {};
  }
  drawerVisible.value = true;
};

const closeDrawer = () => {
  drawerVisible.value = false;
  drawerData.value = {};
};

const handleSubmit = async (formData: any) => {
  if (formData.id) {
    await gpuResourceUpdateApi(formData);
  } else {
    await gpuResourceSaveApi(formData);
  }
  ElMessage.success("操作成功");
  getList();
  closeDrawer();
};

const toggleStatus = async (row: any) => {
  const newStatus = row.status === 1 ? 2 : 1;
  await gpuResourceStatusApi(row.id, newStatus);
  ElMessage.success("状态更新成功");
  getList();
};

const getStatusTagType = (status: number) => {
  switch (status) {
    case 1:
      return "success";
    case 2:
      return "info";
    case 3:
      return "warning";
    default:
      return "info";
  }
};

const getStatusText = (status: number) => {
  switch (status) {
    case 1:
      return "上架";
    case 2:
      return "下架";
    case 3:
      return "维护中";
    default:
      return "未知";
  }
};
</script>

<style lang="scss" scoped>
.discount-price {
  color: #f56c6c;
  margin-left: 8px;
}
</style>
