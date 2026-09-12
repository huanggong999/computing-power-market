<template>
  <el-drawer
    v-model="visible"
    :title="title"
    size="60%"
    :destroy-on-close="true"
    @close="handleClose"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-width="120px"
      class="resource-form"
    >
      <el-tabs v-model="activeTab">
        <!-- 基本信息 -->
        <el-tab-pane label="基本信息" name="basic">
          <el-form-item label="来源节点">
            <el-select
              v-model="selectedClusterNodeName"
              filterable
              clearable
              remote
              reserve-keyword
              :remote-method="handleClusterSearch"
              :loading="clusterLoading"
              placeholder="请选择集群节点，选择后自动带出资源信息"
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
                    {{ getNodeGpuModel(node) || "--" }} / {{ getNodeStatus(node) || "--" }} / 总 {{ getNodeGpuCount(node) }} / 可用 {{ getNodeAvailableGpus(node) }}
                  </span>
                </div>
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="机器ID" prop="machineId">
            <el-input v-model="formData.machineId" placeholder="请输入机器ID" />
          </el-form-item>
          <el-form-item label="机器UUID" prop="machineUuid">
            <el-input v-model="formData.machineUuid" placeholder="请输入机器UUID" />
          </el-form-item>
          <el-form-item label="地区" prop="regionCode">
            <el-select v-model="formData.regionCode" placeholder="请选择地区" @change="onRegionChange">
              <el-option
                v-for="item in regionList"
                :key="item.regionCode"
                :label="item.regionName"
                :value="item.regionCode"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="专区" prop="zoneCode">
            <el-select v-model="formData.zoneCode" placeholder="请选择专区">
              <el-option
                v-for="item in filteredZoneList"
                :key="item.zoneCode"
                :label="item.zoneName"
                :value="item.zoneCode"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="可租至" prop="rentableUntil">
            <el-date-picker
              v-model="formData.rentableUntil"
              type="date"
              placeholder="选择可租截止日期"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-radio-group v-model="formData.status">
              <el-radio :value="1">上架</el-radio>
              <el-radio :value="2">下架</el-radio>
              <el-radio :value="3">维护中</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-tab-pane>

        <!-- GPU配置 -->
        <el-tab-pane label="GPU配置" name="gpu">
          <el-form-item label="GPU规格" prop="specId">
            <el-select v-model="formData.specId" placeholder="请选择GPU规格" @change="onSpecChange">
              <el-option
                v-for="item in specList"
                :key="item.id"
                :label="item.model"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="GPU数量" prop="gpuCount">
            <el-input-number v-model="formData.gpuCount" :min="1" :max="16" />
          </el-form-item>
          <el-form-item label="GPU驱动" prop="gpuDriver">
            <el-select
              v-model="formData.gpuDriver"
              filterable
              clearable
              placeholder="请选择GPU驱动"
              @change="onGpuDriverChange"
            >
              <el-option
                v-for="item in gpuDriverOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="CUDA版本" prop="cudaVersion">
            <el-select
              v-model="formData.cudaVersion"
              filterable
              clearable
              :disabled="!formData.gpuDriver"
              placeholder="请选择CUDA版本"
            >
              <el-option
                v-for="item in cudaVersionOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="缓存优化">
            <el-switch
              v-model="formData.cacheOptimized"
              :active-value="true"
              :inactive-value="false"
              active-text="是"
              inactive-text="否"
            />
          </el-form-item>
        </el-tab-pane>

        <!-- CPU & 内存 -->
        <el-tab-pane label="CPU & 内存" name="cpuMemory">
          <el-form-item label="CPU核数" prop="cpuCores">
            <el-input-number v-model="formData.cpuCores" :min="1" :max="256" />
          </el-form-item>
          <el-form-item label="CPU型号" prop="cpuModel">
            <el-input v-model="formData.cpuModel" placeholder="请输入CPU型号" />
          </el-form-item>
          <el-form-item label="内存大小" prop="memorySize">
            <el-input v-model="formData.memorySize" placeholder="例如：512 GB" />
          </el-form-item>
        </el-tab-pane>

        <!-- 存储 -->
        <el-tab-pane label="存储" name="storage">
          <el-form-item label="系统盘" prop="systemDisk">
            <el-input v-model="formData.systemDisk" placeholder="例如：500 GB SSD" />
          </el-form-item>
          <el-form-item label="数据盘" prop="dataDisk">
            <el-input v-model="formData.dataDisk" placeholder="例如：8 TB NVMe" />
          </el-form-item>
          <el-form-item label="可扩容" prop="expandable">
            <el-input v-model="formData.expandable" placeholder="例如：16 TB" />
          </el-form-item>
        </el-tab-pane>

        <!-- 价格配置 -->
        <el-tab-pane label="价格配置" name="price">
          <el-table :data="priceList" border style="width: 100%">
            <el-table-column prop="billingType" label="计费类型" width="120">
              <template #default="{ row }">
                {{ billingTypeMap[row.billingType] }}
              </template>
            </el-table-column>
            <el-table-column label="原价" width="180">
              <template #default="{ row }">
                <el-input-number
                  v-model="row.unitPrice"
                  :precision="2"
                  :min="0"
                  placeholder="请输入原价"
                />
              </template>
            </el-table-column>
            <el-table-column label="折扣价" width="180">
              <template #default="{ row }">
                <el-input-number
                  v-model="row.discountPrice"
                  :precision="2"
                  :min="0"
                  placeholder="请输入折扣价"
                />
              </template>
            </el-table-column>
            <el-table-column label="折扣率">
              <template #default="{ row }">
                <el-input
                  v-model="row.discountRate"
                  placeholder="例如：85%"
                />
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- 库存配置 -->
        <el-tab-pane label="库存配置" name="stock">
          <el-form-item label="可用数量" prop="availableCount">
            <el-input-number v-model="formData.availableCount" :min="0" :max="9999" />
          </el-form-item>
          <el-form-item label="总数量" prop="totalCount">
            <el-input-number v-model="formData.totalCount" :min="1" :max="9999" />
          </el-form-item>
        </el-tab-pane>
      </el-tabs>
    </el-form>

    <template #footer>
      <div style="flex: auto">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleSubmit">提交</el-button>
      </div>
    </template>
  </el-drawer>
</template>

<script setup lang="ts" name="ResourceDrawer">
import { gpuSpecListApi } from "@/api/gpuSpec";
import { gpuRegionListApi } from "@/api/gpuRegion";
import { gpuZoneListApi } from "@/api/gpuZone";
import { gpuClusterNodesApi, gpuComponentListApi } from "@/api/gpuCluster";
import type { FormInstance } from "element-plus";

interface Props {
  modelValue: boolean;
  title: string;
  data: any;
}

const props = defineProps<Props>();
const emit = defineEmits(["update:modelValue", "submit", "close"]);

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit("update:modelValue", val),
});

const formRef = ref<FormInstance>();
const activeTab = ref("basic");

// 下拉数据
const specList = ref<any[]>([]);
const regionList = ref<any[]>([]);
const zoneList = ref<any[]>([]);
const filteredZoneList = ref<any[]>([]);
const componentList = ref<any[]>([]);
const clusterLoading = ref(false);
const clusterNodeOptions = ref<any[]>([]);
const selectedClusterNodeName = ref("");

const billingTypeMap: Record<string, string> = {
  on_demand: "按量计费",
  hourly: "按小时",
  daily: "按天",
  weekly: "按周",
  monthly: "按月",
};

const defaultPriceList = () => [
  { billingType: "on_demand", unitPrice: 0, discountPrice: 0, discountRate: "100%" },
  { billingType: "hourly", unitPrice: 0, discountPrice: 0, discountRate: "100%" },
  { billingType: "daily", unitPrice: 0, discountPrice: 0, discountRate: "100%" },
  { billingType: "weekly", unitPrice: 0, discountPrice: 0, discountRate: "100%" },
  { billingType: "monthly", unitPrice: 0, discountPrice: 0, discountRate: "100%" },
];

// 价格列表
const priceList = ref(defaultPriceList());

const formData = reactive<any>({
  id: undefined,
  machineId: "",
  machineUuid: "",
  regionCode: undefined,
  zoneCode: undefined,
  clusterId: "",
  clusterName: "",
  clusterNodeName: "",
  rentableUntil: undefined,
  status: 1,
  specId: undefined,
  gpuCount: 1,
  gpuDriver: "",
  cudaVersion: "",
  cacheOptimized: false,
  cpuCores: 1,
  cpuModel: "",
  memorySize: "",
  systemDisk: "",
  dataDisk: "",
  expandable: "",
  availableCount: 0,
  totalCount: 1,
});

const getComponentType = (component: any) => component?.componentName || "";

const getComponentSortOrder = (component: any) => {
  const sortOrder = Number(component?.sortOrder);
  return Number.isFinite(sortOrder) ? sortOrder : 0;
};

const extractCudaVersion = (component: any) => {
  const text = `${component?.baseImage || ""} ${component?.imageAddress || ""}`;
  const matched = text.match(/(?:cuda|cu)\s*[-:]?\s*v?(\d+(?:\.\d+){0,2})/i);
  return matched?.[1] || "";
};

const gpuDriverOptions = computed(() => {
  const optionMap = new Map<string, { label: string; value: string; sortOrder: number }>();

  componentList.value.forEach((component) => {
    const componentType = getComponentType(component);
    if (!componentType) return;

    const existing = optionMap.get(componentType);
    const sortOrder = getComponentSortOrder(component);
    if (!existing || sortOrder < existing.sortOrder) {
      optionMap.set(componentType, {
        label: componentType,
        value: componentType,
        sortOrder,
      });
    }
  });

  return Array.from(optionMap.values()).sort((a, b) => a.sortOrder - b.sortOrder);
});

const cudaVersionOptions = computed(() => {
  if (!formData.gpuDriver) return [];

  const optionMap = new Map<string, { label: string; value: string; sortOrder: number }>();
  componentList.value
    .filter((component) => getComponentType(component) === formData.gpuDriver)
    .forEach((component) => {
      const cudaVersion = extractCudaVersion(component);
      if (!cudaVersion) return;

      const existing = optionMap.get(cudaVersion);
      const sortOrder = getComponentSortOrder(component);
      if (!existing || sortOrder < existing.sortOrder) {
        optionMap.set(cudaVersion, {
          label: cudaVersion,
          value: cudaVersion,
          sortOrder,
        });
      }
    });

  return Array.from(optionMap.values()).sort((a, b) => a.sortOrder - b.sortOrder);
});

const rules = {
  machineId: [{ required: true, message: "请输入机器ID", trigger: "blur" }],
  machineUuid: [{ required: true, message: "请输入机器UUID", trigger: "blur" }],
  regionCode: [{ required: true, message: "请选择地区", trigger: "change" }],
  specId: [{ required: true, message: "请选择GPU规格", trigger: "change" }],
  gpuCount: [{ required: true, message: "请输入GPU数量", trigger: "change" }],
  cpuCores: [{ required: true, message: "请输入CPU核数", trigger: "change" }],
  memorySize: [{ required: true, message: "请输入内存大小", trigger: "change" }],
  systemDisk: [{ required: true, message: "请输入系统盘大小", trigger: "change" }],
  availableCount: [{ required: true, message: "请输入可用数量", trigger: "change" }],
  totalCount: [{ required: true, message: "请输入总数量", trigger: "change" }],
};

// 加载下拉数据
const loadSelectData = async () => {
  const [specRes, regionRes, zoneRes, componentRes] = await Promise.all([
    gpuSpecListApi(),
    gpuRegionListApi(),
    gpuZoneListApi(),
    gpuComponentListApi({ pageNo: 1, pageSize: 1000, status: 1 }),
  ]);
  specList.value = (specRes.data || []).filter((item: any) => item.status === 1);
  regionList.value = (regionRes.data || []).filter((item: any) => item.status === 1);
  zoneList.value = (zoneRes.data || []).filter((item: any) => item.status === 1);
  componentList.value = getPageList(componentRes.data).filter((item: any) => item.status === 1);
  filteredZoneList.value = formData.regionCode ? getZonesByRegion(formData.regionCode) : zoneList.value;
};

const getZonesByRegion = (regionCode: any) => {
  return zoneList.value.filter((zone) => !zone.regionCode || zone.regionCode === regionCode);
};

const onRegionChange = (regionCode: any) => {
  formData.zoneCode = undefined;
  filteredZoneList.value = getZonesByRegion(regionCode);
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

const getNodeStatus = (node: any) =>
  getNodeField(node, "status", "clusterStatus");

const getNodeGpuCount = (node: any) =>
  getNodeField(node, "gpu_count", "gpuCount") ?? 0;

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

const formatClusterNodeLabel = (node: any) =>
  `${getNodeName(node) || "--"} / ${getNodeGpuModel(node) || "--"} / ${getNodeStatus(node) || "--"} / 可用 ${getNodeAvailableGpus(node)}`;

const fetchClusterNodes = async (keyword = "") => {
  clusterLoading.value = true;
  try {
    const params: any = {
      pageNo: 1,
      pageSize: 200,
    };
    if (keyword) {
      params.gpuModel = keyword;
    }
    const { data } = await gpuClusterNodesApi(params);
    clusterNodeOptions.value = getPageList(data).sort((a: any, b: any) => {
      const aReady = String(getNodeStatus(a) || "").toLowerCase() === "ready" ? 0 : 1;
      const bReady = String(getNodeStatus(b) || "").toLowerCase() === "ready" ? 0 : 1;
      if (aReady !== bReady) return aReady - bReady;
      return toPositiveInt(getNodeAvailableGpus(b)) - toPositiveInt(getNodeAvailableGpus(a));
    });
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

const toNumber = (value: any) => {
  const numericValue = Number(value);
  return Number.isFinite(numericValue) ? numericValue : 0;
};

const toPositiveInt = (value: any) => {
  return Math.max(0, Math.round(toNumber(value)));
};

const formatGiB = (value: any) => {
  const numericValue = toNumber(value);
  if (!numericValue) return "";
  const displayValue = Number.isInteger(numericValue)
    ? numericValue
    : Number(numericValue.toFixed(2));
  return `${displayValue} GiB`;
};

const resolveRegionCode = (node: any) => {
  const region = getNodeField(node, "region", "region_code", "regionCode");
  const matched = regionList.value.find((item) => item.regionCode === region || item.regionName === region);
  return matched?.regionCode;
};

const findSpecByNode = (node: any) => {
  return specList.value.find((spec) => {
    return spec.clusterNodeName === getNodeName(node) || spec.model === getNodeGpuModel(node);
  });
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

const setIfValue = (key: string, value: any) => {
  if (value !== undefined && value !== null && value !== "") {
    formData[key] = value;
  }
};

const onSpecChange = (specId: any) => {
  const spec = specList.value.find((s) => s.id === specId);
  if (!spec) return;

  setIfValue("model", spec.model);
  setIfValue("vram", spec.vram);

  const gpuCount = toPositiveInt(spec.gpuCount);
  const allocatedGpus = toPositiveInt(spec.allocatedGpus);
  const hasAvailableGpus = spec.availableGpus !== undefined && spec.availableGpus !== null;
  const availableGpus = toPositiveInt(spec.availableGpus);
  const totalCount = Math.max(1, gpuCount || allocatedGpus + availableGpus || 1);

  if (gpuCount > 0) {
    formData.gpuCount = gpuCount;
  }
  if (spec.cpuTotalCores !== undefined && spec.cpuTotalCores !== null) {
    formData.cpuCores = Math.max(1, toPositiveInt(spec.cpuTotalCores));
  }

  setIfValue("cpuModel", spec.cpuModel);
  setIfValue("memorySize", formatGiB(spec.memoryTotalGi));
  setIfValue("dataDisk", formatGiB(spec.diskTotalGi));
  if (!formData.systemDisk && spec.diskTotalGi) {
    formData.systemDisk = "50 GiB";
  }

  formData.totalCount = totalCount;
  formData.availableCount = hasAvailableGpus ? Math.min(availableGpus, totalCount) : totalCount;

  if (spec.clusterNodeName) {
    if (!formData.machineId) {
      formData.machineId = spec.clusterNodeName;
    }
    if (!formData.machineUuid) {
      formData.machineUuid = spec.clusterNodeName;
    }
  }

  if (spec.clusterStatus) {
    formData.status = String(spec.clusterStatus).toLowerCase() === "ready" ? 1 : 3;
  }

  ElMessage.success("已带出规格相关配置，可继续手动修改");
};

const onGpuDriverChange = () => {
  const hasCurrentCudaVersion = cudaVersionOptions.value.some(
    (item) => item.value === formData.cudaVersion
  );
  if (!hasCurrentCudaVersion) {
    formData.cudaVersion = "";
  }
};

const onClusterNodeChange = (nodeName: string) => {
  const node = clusterNodeOptions.value.find((item) => getNodeName(item) === nodeName);
  if (!node) {
    formData.clusterId = "";
    formData.clusterName = "";
    formData.clusterNodeName = "";
    return;
  }

  const regionCode = resolveRegionCode(node);
  if (regionCode) {
    formData.regionCode = regionCode;
    filteredZoneList.value = getZonesByRegion(regionCode);
  }

  const spec = findSpecByNode(node);
  if (spec) {
    formData.specId = spec.id;
    onSpecChange(spec.id);
  }

  formData.clusterId = getNodeField(node, "cluster_id", "clusterId") || "";
  formData.clusterName = getNodeField(node, "cluster_name", "clusterName", "cluster") || "";
  formData.clusterNodeName = getNodeName(node);
  formData.machineId = getNodeName(node) || formData.machineId;
  formData.machineUuid = getNodeField(node, "uid", "uuid", "node_uid", "nodeUid") || getNodeName(node) || formData.machineUuid;
  formData.gpuCount = Math.max(1, toPositiveInt(getNodeGpuCount(node)));
  formData.cpuCores = Math.max(1, toPositiveInt(getNodeCpuTotalCores(node)));
  setIfValue("cpuModel", getCpuModelFromNode(node));
  setIfValue("memorySize", formatGiB(getNodeMemoryTotalGi(node)));
  setIfValue("dataDisk", formatGiB(getNodeDiskTotalGi(node)));
  if (!formData.systemDisk && getNodeDiskTotalGi(node)) {
    formData.systemDisk = "50 GiB";
  }
  formData.totalCount = Math.max(1, toPositiveInt(getNodeGpuCount(node)));
  formData.availableCount = Math.min(toPositiveInt(getNodeAvailableGpus(node)), formData.totalCount);
  formData.status = String(getNodeStatus(node) || "").toLowerCase() === "ready" ? 1 : 3;

  ElMessage.success("已带出集群节点资源配置，可继续手动修改");
};

const handleSubmit = async () => {
  if (!formRef.value) return;
  await formRef.value.validate((valid: boolean) => {
    if (!valid) {
      ElMessage.warning("请填写完整表单信息");
      return;
    }
    // 组装价格数据
    const submitData = {
      ...formData,
      prices: priceList.value,
    };
    emit("submit", submitData);
  });
};

const handleClose = () => {
  formRef.value?.resetFields();
  activeTab.value = "basic";
  priceList.value = defaultPriceList();
  emit("close");
};

const initFormData = () => {
  const newVal = props.data;
  if (!newVal || Object.keys(newVal).length === 0) {
    Object.assign(formData, {
      id: undefined,
      machineId: "",
      machineUuid: "",
      regionCode: undefined,
      zoneCode: undefined,
      clusterId: "",
      clusterName: "",
      clusterNodeName: "",
      rentableUntil: undefined,
      status: 1,
      specId: undefined,
      gpuCount: 1,
      gpuDriver: "",
      cudaVersion: "",
      cacheOptimized: false,
      cpuCores: 1,
      cpuModel: "",
      memorySize: "",
      systemDisk: "",
      dataDisk: "",
      expandable: "",
      availableCount: 0,
      totalCount: 1,
    });
    filteredZoneList.value = zoneList.value;
    selectedClusterNodeName.value = "";
    priceList.value = defaultPriceList();
    return;
  }

  const regionCode = newVal.regionCode || regionList.value.find((r) => r.regionName === newVal.regionName || r.regionName === newVal.region)?.regionCode;
  const zoneCode = newVal.zoneCode || zoneList.value.find((z) => z.zoneName === newVal.zoneName || z.zoneName === newVal.zone)?.zoneCode;

  Object.assign(formData, newVal, { regionCode, zoneCode });
  formData.cacheOptimized = Boolean(formData.cacheOptimized);
  filteredZoneList.value = getZonesByRegion(regionCode);
  selectedClusterNodeName.value = newVal.clusterNodeName || "";

  if (newVal.prices && newVal.prices.length > 0) {
    const existingPrices = newVal.prices;
    priceList.value = defaultPriceList().map((p) => {
      const found = existingPrices.find(
        (ep: any) => ep.billingType === p.billingType
      );
      return found ? { ...p, ...found } : p;
    });
  } else {
    priceList.value = defaultPriceList();
  }
};

watch(() => props.data, initFormData, { immediate: true });
watch([regionList, zoneList], () => {
  if (props.data && Object.keys(props.data).length > 0) {
    initFormData();
  }
});

onMounted(() => {
  loadSelectData();
  fetchClusterNodes();
});
</script>

<style lang="scss" scoped>
.resource-form {
  padding: 0 20px;
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
