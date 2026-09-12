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
        <el-button @click="openDialog('add')"> 新增专区 </el-button>
      </template>

      <template #status="row">
        <el-tag :type="row.status === 1 ? 'success' : 'info'">
          {{ row.status === 1 ? "启用" : "停用" }}
        </el-tag>
      </template>

      <template #operation="row">
        <el-button link type="primary" @click="openDialog('edit', row)">
          编辑
        </el-button>
        <el-button link type="primary" @click="toggleStatus(row)">
          {{ row.status === 1 ? "停用" : "启用" }}
        </el-button>
        <el-button
          link
          type="danger"
          @click="removeFn(gpuZoneDeleteApi, row.id, row.zoneName)"
        >
          删除
        </el-button>
      </template>
    </ProTable>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="专区编码" prop="zoneCode">
          <el-input v-model="formData.zoneCode" placeholder="如：v100-zone" />
        </el-form-item>
        <el-form-item label="专区名称" prop="zoneName">
          <el-input v-model="formData.zoneName" placeholder="如：V100专区" />
        </el-form-item>
        <el-form-item label="所属地区" prop="regionCode">
          <el-select v-model="formData.regionCode" clearable placeholder="请选择所属地区">
            <el-option
              v-for="item in regionList"
              :key="item.regionCode"
              :label="item.regionName"
              :value="item.regionCode"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="formData.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts" name="GpuZone">
import {
  gpuZoneDeleteApi,
  gpuZonePageApi,
  gpuZoneSaveApi,
  gpuZoneUpdateApi,
  gpuZoneStatusApi,
} from "@/api/gpuZone";
import { gpuRegionListApi } from "@/api/gpuRegion";
import { useTable } from "@/hooks/useTable";
import type { FormInstance } from "element-plus";

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
  api: gpuZonePageApi,
  title: "GPU专区",
});

const regionList = ref<any[]>([]);

const columns: ColumnProps[] = [
  { prop: "id", label: "ID", width: 80 },
  { prop: "zoneCode", label: "专区编码", search: { el: "input" } },
  { prop: "zoneName", label: "专区名称", search: { el: "input" } },
  { prop: "regionCode", label: "所属地区", search: { el: "select" }, enum: regionList, fieldNames: { label: "regionName", value: "regionCode" } },
  { prop: "sortOrder", label: "排序" },
  {
    prop: "status",
    label: "状态",
    slot: true,
    search: { el: "select" },
    enum: [
      { value: 1, label: "启用" },
      { value: 0, label: "停用" },
    ],
  },
  { prop: "operation", label: "操作", slot: true, width: 200 },
];

const dialogVisible = ref(false);
const dialogTitle = ref("");
const formRef = ref<FormInstance>();

const formData = reactive({
  id: undefined,
  zoneCode: "",
  zoneName: "",
  regionCode: undefined,
  sortOrder: 0,
  status: 1,
});

const rules = {
  zoneCode: [{ required: true, message: "请输入专区编码", trigger: "blur" }],
  zoneName: [{ required: true, message: "请输入专区名称", trigger: "blur" }],
};

const openDialog = (type: "add" | "edit", row?: any) => {
  dialogTitle.value = type === "add" ? "新增专区" : "编辑专区";
  if (type === "edit" && row) {
    Object.assign(formData, row);
  } else {
    Object.assign(formData, {
      id: undefined,
      zoneCode: "",
      zoneName: "",
      regionCode: undefined,
      sortOrder: 0,
      status: 1,
    });
  }
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  if (!formRef.value) return;
  await formRef.value.validate(async (valid: boolean) => {
    if (!valid) {
      ElMessage.warning("请填写完整信息");
      return;
    }
    if (formData.id) {
      await gpuZoneUpdateApi(formData);
    } else {
      await gpuZoneSaveApi(formData);
    }
    ElMessage.success("操作成功");
    dialogVisible.value = false;
    getList();
  });
};

const toggleStatus = async (row: any) => {
  const newStatus = row.status === 1 ? 0 : 1;
  await gpuZoneStatusApi(row.id, newStatus);
  ElMessage.success("状态更新成功");
  getList();
};

onMounted(async () => {
  const { data } = await gpuRegionListApi();
  regionList.value = (data || []).filter((item: any) => item.status === 1);
});
</script>
