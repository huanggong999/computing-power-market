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
        <el-button @click="openDialog('add')"> 新增规格 </el-button>
      </template>

      <template #status="row">
        <el-tag :type="row.status === 1 ? 'success' : 'info'">
          {{ row.status === 1 ? "启用" : "停用" }}
        </el-tag>
      </template>

      <template #tags="row">
        <el-tag v-for="tag in parseTags(row.tags)" :key="tag" class="tag-item">
          {{ tag }}
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
          @click="removeFn(gpuSpecDeleteApi, row.id, row.model)"
        >
          删除
        </el-button>
      </template>
    </ProTable>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="760px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item v-if="!formData.id" label="集群节点" prop="clusterNodeName">
          <el-select
            v-model="selectedClusterNodeName"
            filterable
            clearable
            remote
            reserve-keyword
            :remote-method="handleClusterSearch"
            :loading="clusterLoading"
            placeholder="请选择 GPU 集群节点，选择后自动带出可编辑字段"
            style="width: 100%"
            @change="onClusterNodeChange"
            @visible-change="onClusterSelectVisible"
          >
            <el-option
              v-for="node in clusterNodeOptions"
              :key="getNodeName(node)"
              :label="formatClusterNodeLabel(node)"
              :value="getNodeName(node)"
            >
              <div class="cluster-option">
                <span>{{ getNodeName(node) }}</span>
                <span class="cluster-option-sub">
                  {{ getNodeGpuModel(node) || "--" }} / 总 {{ getNodeGpuCount(node) }} / 可用 {{ getNodeAvailableGpus(node) }}
                </span>
              </div>
            </el-option>
          </el-select>
        </el-form-item>
        <el-alert
          v-if="!formData.id"
          class="cluster-tip"
          title="可先从 GPU 集群节点中选择一条记录，系统会自动填充型号、GPU 数量和节点资源信息；填充后所有字段仍可手动修改。"
          type="info"
          :closable="false"
          show-icon
        />
        <el-form-item label="型号" prop="model">
          <el-input v-model="formData.model" placeholder="如：RTX 5090" />
        </el-form-item>
        <el-form-item label="显存(GB)" prop="vram">
          <el-input-number v-model="formData.vram" :min="1" :max="1024" />
        </el-form-item>
        <el-form-item label="架构" prop="architecture">
          <el-input v-model="formData.architecture" placeholder="如：Ampere" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            placeholder="请输入描述"
          />
        </el-form-item>
        <el-divider content-position="left">集群带出信息</el-divider>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="节点名称" prop="clusterNodeName">
              <el-input v-model="formData.clusterNodeName" placeholder="选择集群节点后自动填充" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="节点状态" prop="clusterStatus">
              <el-input v-model="formData.clusterStatus" placeholder="如：Ready" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="GPU总量" prop="gpuCount">
              <el-input-number v-model="formData.gpuCount" :min="0" :max="1024" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="已分配GPU" prop="allocatedGpus">
              <el-input-number v-model="formData.allocatedGpus" :min="0" :max="1024" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="可用GPU" prop="availableGpus">
              <el-input-number v-model="formData.availableGpus" :min="0" :max="1024" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="CPU核数" prop="cpuTotalCores">
              <el-input-number v-model="formData.cpuTotalCores" :min="0" :max="4096" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="CPU型号" prop="cpuModel">
              <el-input
                v-model="formData.cpuModel"
                clearable
                placeholder="请输入CPU型号，如 Intel Xeon Gold 6338；选择集群节点后可自动填充"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="内存(GiB)" prop="memoryTotalGi">
              <el-input-number v-model="formData.memoryTotalGi" :min="0" :max="1048576" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="磁盘(GiB)" prop="diskTotalGi">
              <el-input-number v-model="formData.diskTotalGi" :min="0" :max="10485760" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="标签" prop="tags">
          <el-select
            v-model="formData.tags"
            multiple
            filterable
            allow-create
            default-first-option
            placeholder="选择或输入标签后回车"
          >
            <el-option label="热门" value="热门" />
            <el-option label="最新" value="最新" />
            <el-option label="专业级" value="专业级" />
            <el-option label="大显存" value="大显存" />
            <el-option label="高性价比" value="高性价比" />
            <el-option label="入门级" value="入门级" />
            <el-option label="弹性虚拟GPU" value="弹性虚拟GPU" />
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

<script setup lang="ts" name="GpuSpec">
import {
  gpuSpecDeleteApi,
  gpuSpecPageApi,
  gpuSpecSaveApi,
  gpuSpecUpdateApi,
  gpuSpecStatusApi,
} from "@/api/gpuSpec";
import { gpuClusterNodesApi } from "@/api/gpuCluster";
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
  api: gpuSpecPageApi,
  title: "GPU规格",
});

const columns: ColumnProps[] = [
  { prop: "id", label: "ID", width: 80 },
  { prop: "model", label: "型号", search: { el: "input" } },
  { prop: "vram", label: "显存(GB)" },
  { prop: "architecture", label: "架构" },
  { prop: "description", label: "描述" },
  { prop: "clusterNodeName", label: "来源节点", width: 160 },
  { prop: "gpuCount", label: "GPU总量", width: 100 },
  { prop: "availableGpus", label: "可用GPU", width: 100 },
  { prop: "cpuModel", label: "CPU型号", width: 180 },
  { prop: "tags", label: "标签", slot: true },
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
const selectedClusterNodeName = ref("");
const clusterLoading = ref(false);
const clusterNodeOptions = ref<any[]>([]);

const formData = reactive({
  id: undefined,
  model: "",
  vram: 24,
  architecture: "",
  description: "",
  clusterNodeName: "",
  clusterStatus: "",
  gpuCount: 0,
  allocatedGpus: 0,
  availableGpus: 0,
  cpuTotalCores: 0,
  cpuModel: "",
  memoryTotalGi: 0,
  diskTotalGi: 0,
  tags: [] as string[],
  sortOrder: 0,
  status: 1,
});

const rules = {
  model: [{ required: true, message: "请输入型号", trigger: "blur" }],
  vram: [{ required: true, message: "请输入显存", trigger: "change" }],
};

const parseTags = (tags: string | string[]) => {
  if (!tags) return [];
  if (Array.isArray(tags)) return tags;
  try {
    return JSON.parse(tags);
  } catch {
    return tags.split(",");
  }
};

const parseVram = (vram: string | number) => {
  const value = Number.parseInt(String(vram), 10);
  return Number.isNaN(value) ? 24 : value;
};

const toNumber = (value: any) => {
  const number = Number(value);
  return Number.isFinite(number) ? number : 0;
};

const parseVramFromModel = (model: any) => {
  const match = String(model || "").match(/(\d+)\s*GB/i);
  return match ? Number(match[1]) : undefined;
};

const buildDescriptionFromNode = (node: any) => {
  const items = [
    node.node_name ? `来源节点：${node.node_name}` : "",
    node.status ? `状态：${node.status}` : "",
    node.gpu_count != null ? `GPU总量：${node.gpu_count}` : "",
    node.available_gpus != null ? `可用GPU：${node.available_gpus}` : "",
  ].filter(Boolean);
  return items.join("；");
};

const getCpuModelFromNode = (node: any) => {
  return (
    node.cpu?.model ||
    node.cpu?.name ||
    node.cpu_model ||
    node.cpuModel ||
    node.processor ||
    node.processor_model ||
    node.processorModel ||
    ""
  );
};

const getNodeField = (node: any, ...keys: string[]) => {
  for (const key of keys) {
    if (node?.[key] !== undefined && node?.[key] !== null && node?.[key] !== "") {
      return node[key];
    }
  }
  return undefined;
};

const getNodeName = (node: any) =>
  getNodeField(node, "node_name", "nodeName", "clusterNodeName", "name") || "";

const getNodeGpuModel = (node: any) =>
  getNodeField(node, "gpu_model", "gpuModel");

const getNodeGpuCount = (node: any) =>
  getNodeField(node, "gpu_count", "gpuCount") ?? 0;

const getNodeAllocatedGpus = (node: any) =>
  getNodeField(node, "allocated_gpus", "allocatedGpus") ?? 0;

const getNodeAvailableGpus = (node: any) =>
  getNodeField(node, "available_gpus", "availableGpus") ?? 0;

const getNodeCpuTotalCores = (node: any) =>
  getNodeField(node?.cpu, "total_cores", "totalCores") ?? getNodeField(node, "cpu_total_cores", "cpuTotalCores");

const getNodeMemoryTotalGi = (node: any) =>
  getNodeField(node?.memory, "total_gi", "totalGi") ?? getNodeField(node, "memory_total_gi", "memoryTotalGi");

const getNodeDiskTotalGi = (node: any) =>
  getNodeField(node?.disk, "total_gi", "totalGi") ?? getNodeField(node, "disk_total_gi", "diskTotalGi");

const getPageList = (data: any) => {
  if (Array.isArray(data)) return data;
  if (Array.isArray(data?.list)) return data.list;
  if (Array.isArray(data?.records)) return data.records;
  if (Array.isArray(data?.rows)) return data.rows;
  if (Array.isArray(data?.data)) return data.data;
  return [];
};

const resetFormData = () => {
  Object.assign(formData, {
    id: undefined,
    model: "",
    vram: 24,
    architecture: "",
    description: "",
    clusterNodeName: "",
    clusterStatus: "",
    gpuCount: 0,
    allocatedGpus: 0,
    availableGpus: 0,
    cpuTotalCores: 0,
    cpuModel: "",
    memoryTotalGi: 0,
    diskTotalGi: 0,
    tags: [],
    sortOrder: 0,
    status: 1,
  });
  selectedClusterNodeName.value = "";
};

const formatClusterNodeLabel = (node: any) =>
  `${getNodeName(node) || "--"} / ${getNodeGpuModel(node) || "--"} / 可用 ${getNodeAvailableGpus(node)}`;

const fetchClusterNodes = async (keyword = "") => {
  clusterLoading.value = true;
  try {
    const params: any = {
      pageNo: 1,
      pageSize: 100,
    };
    if (keyword) {
      params.gpuModel = keyword;
    }
    const { data } = await gpuClusterNodesApi(params);
    clusterNodeOptions.value = getPageList(data);
  } finally {
    clusterLoading.value = false;
  }
};

const handleClusterSearch = (keyword: string) => {
  fetchClusterNodes(keyword);
};

const onClusterSelectVisible = (visible: boolean) => {
  if (visible && !clusterNodeOptions.value.length) fetchClusterNodes();
};

const onClusterNodeChange = (nodeName: string) => {
  const node = clusterNodeOptions.value.find((item) => getNodeName(item) === nodeName);
  if (!node) return;
  const parsedVram = parseVramFromModel(getNodeGpuModel(node));
  Object.assign(formData, {
    model: getNodeGpuModel(node) || formData.model,
    vram: parsedVram || formData.vram,
    architecture: formData.architecture,
    description: formData.description || buildDescriptionFromNode(node),
    clusterNodeName: getNodeName(node),
    clusterStatus: getNodeField(node, "status", "clusterStatus") || "",
    gpuCount: toNumber(getNodeGpuCount(node)),
    allocatedGpus: toNumber(getNodeAllocatedGpus(node)),
    availableGpus: toNumber(getNodeAvailableGpus(node)),
    cpuTotalCores: toNumber(getNodeCpuTotalCores(node)),
    cpuModel: getCpuModelFromNode(node) || formData.cpuModel,
    memoryTotalGi: toNumber(getNodeMemoryTotalGi(node)),
    diskTotalGi: toNumber(getNodeDiskTotalGi(node)),
  });
};

const openDialog = (type: "add" | "edit", row?: any) => {
  dialogTitle.value = type === "add" ? "新增GPU规格" : "编辑GPU规格";
  if (type === "edit" && row) {
    Object.assign(formData, {
      ...row,
      vram: parseVram(row.vram),
      tags: parseTags(row.tags),
      clusterNodeName: row.clusterNodeName || "",
      clusterStatus: row.clusterStatus || "",
      gpuCount: toNumber(row.gpuCount),
      allocatedGpus: toNumber(row.allocatedGpus),
      availableGpus: toNumber(row.availableGpus),
      cpuTotalCores: toNumber(row.cpuTotalCores),
      cpuModel: row.cpuModel || "",
      memoryTotalGi: toNumber(row.memoryTotalGi),
      diskTotalGi: toNumber(row.diskTotalGi),
    });
    selectedClusterNodeName.value = row.clusterNodeName || "";
  } else {
    resetFormData();
    fetchClusterNodes();
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
    const data = {
      ...formData,
      vram: `${formData.vram} GB`,
      tags: JSON.stringify(formData.tags),
    };
    if (formData.id) {
      await gpuSpecUpdateApi(data);
    } else {
      await gpuSpecSaveApi(data);
    }
    ElMessage.success("操作成功");
    dialogVisible.value = false;
    getList();
  });
};

const toggleStatus = async (row: any) => {
  const newStatus = row.status === 1 ? 0 : 1;
  await gpuSpecStatusApi(row.id, newStatus);
  ElMessage.success("状态更新成功");
  getList();
};
</script>

<style lang="scss" scoped>
.tag-item {
  margin-right: 4px;
}

.cluster-tip {
  margin: 0 0 18px;
}

.cluster-option {
  display: flex;
  justify-content: space-between;
  gap: 16px;
}

.cluster-option-sub {
  color: var(--el-text-color-secondary);
  font-size: 12px;
}
</style>
