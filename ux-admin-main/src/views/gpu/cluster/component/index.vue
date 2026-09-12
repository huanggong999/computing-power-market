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
      row-key="id"
    >
      <template #tableHeader>
        <el-button type="primary" :icon="Plus" @click="openDialog('add')">
          新增组件
        </el-button>
      </template>

      <template #status="row">
        <el-tag :type="row.status === 1 ? 'success' : 'info'">
          {{ row.status === 1 ? "启用" : "停用" }}
        </el-tag>
      </template>

      <template #os="row">
        {{ formatOs(row) }}
      </template>

      <template #imageAddress="row">
        <el-tooltip :content="row.imageAddress" placement="top" :show-after="300">
          <span class="image-text">{{ row.imageAddress }}</span>
        </el-tooltip>
      </template>

      <template #operation="row">
        <el-button link type="primary" :icon="Edit" @click="openDialog('edit', row)">
          编辑
        </el-button>
        <el-button link type="primary" :icon="SwitchButton" @click="toggleStatus(row)">
          {{ row.status === 1 ? "停用" : "启用" }}
        </el-button>
      </template>
    </ProTable>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="680px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="110px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="组件名称" prop="componentName">
              <el-input v-model="formData.componentName" placeholder="如：PyTorch" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="组件版本" prop="componentVersion">
              <el-input v-model="formData.componentVersion" placeholder="如：2.8.0" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="Python版本" prop="pythonVersion">
              <el-input v-model="formData.pythonVersion" placeholder="如：3.10" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="CUDA版本" prop="cudaVersion">
              <el-input v-model="formData.cudaVersion" placeholder="如：12.8" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="操作系统" prop="osName">
              <el-input v-model="formData.osName" placeholder="如：Ubuntu" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="系统版本" prop="osVersion">
              <el-input v-model="formData.osVersion" placeholder="如：22.04" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="镜像地址" prop="imageAddress">
              <el-input v-model="formData.imageAddress" placeholder="请输入镜像地址" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序" prop="sortOrder">
              <el-input-number v-model="formData.sortOrder" :min="0" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="formData.status">
                <el-radio :value="1">启用</el-radio>
                <el-radio :value="0">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="说明" prop="description">
              <el-input
                v-model="formData.description"
                type="textarea"
                :rows="3"
                placeholder="请输入说明"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts" name="GpuComponent">
import {
  gpuComponentListApi,
  gpuComponentSaveApi,
  gpuComponentStatusApi,
  gpuComponentUpdateApi,
} from "@/api/gpuCluster";
import { useTable } from "@/hooks/useTable";
import { Edit, Plus, SwitchButton } from "@element-plus/icons-vue";
import type { FormInstance } from "element-plus";

const {
  tableData,
  pageData,
  searchParam,
  refreshFn,
  getList,
  searchFn,
  resetFn,
} = useTable({
  api: gpuComponentListApi,
  title: "组件管理",
});

const columns: ColumnProps[] = [
  {
    prop: "componentName",
    label: "组件名称",
    width: 150,
    search: { el: "input", key: "componentName" },
  },
  { prop: "componentVersion", label: "组件版本", width: 130 },
  { prop: "pythonVersion", label: "Python", width: 120 },
  { prop: "os", label: "操作系统", slot: true, width: 140 },
  { prop: "cudaVersion", label: "CUDA", width: 120 },
  { prop: "imageAddress", label: "镜像地址", slot: true, width: 420 },
  {
    prop: "status",
    label: "状态",
    slot: true,
    width: 110,
    search: { el: "select" },
    enum: [
      { value: 1, label: "启用" },
      { value: 0, label: "停用" },
    ],
  },
  { prop: "description", label: "说明", width: 180 },
  { prop: "operation", label: "操作", slot: true, width: 180, fixed: "right" },
];

const dialogVisible = ref(false);
const dialogTitle = ref("");
const formRef = ref<FormInstance>();

const formData = reactive({
  id: undefined as number | undefined,
  componentName: "",
  componentVersion: "",
  pythonVersion: "",
  osName: "Ubuntu",
  osVersion: "",
  cudaVersion: "",
  imageAddress: "",
  description: "",
  sortOrder: 0,
  status: 1,
});

const rules = {
  componentName: [{ required: true, message: "请输入组件名称", trigger: "blur" }],
  imageAddress: [{ required: true, message: "请输入镜像地址", trigger: "blur" }],
};

const formatOs = (row: any) => {
  return [row.osName, row.osVersion].filter(Boolean).join(" ") || "--";
};

const resetFormData = () => {
  Object.assign(formData, {
    id: undefined,
    componentName: "",
    componentVersion: "",
    pythonVersion: "",
    osName: "Ubuntu",
    osVersion: "",
    cudaVersion: "",
    imageAddress: "",
    description: "",
    sortOrder: 0,
    status: 1,
  });
};

const openDialog = (type: "add" | "edit", row?: any) => {
  dialogTitle.value = type === "add" ? "新增组件" : "编辑组件";
  if (type === "edit" && row) {
    Object.assign(formData, {
      id: row.id,
      componentName: row.componentName || "",
      componentVersion: row.componentVersion || "",
      pythonVersion: row.pythonVersion || "",
      osName: row.osName || "Ubuntu",
      osVersion: row.osVersion || "",
      cudaVersion: row.cudaVersion || "",
      imageAddress: row.imageAddress || "",
      description: row.description || "",
      sortOrder: row.sortOrder ?? 0,
      status: row.status ?? 1,
    });
  } else {
    resetFormData();
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
    const data = { ...formData };
    if (formData.id) {
      await gpuComponentUpdateApi(data);
    } else {
      await gpuComponentSaveApi(data);
    }
    ElMessage.success("操作成功");
    dialogVisible.value = false;
    getList();
  });
};

const toggleStatus = async (row: any) => {
  const newStatus = row.status === 1 ? 0 : 1;
  await gpuComponentStatusApi(row.id, newStatus);
  ElMessage.success("状态更新成功");
  getList();
};
</script>

<style lang="scss" scoped>
.image-text {
  display: inline-block;
  max-width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  vertical-align: middle;
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", monospace;
  font-size: 12px;
  color: var(--el-text-color-regular);
}
</style>
