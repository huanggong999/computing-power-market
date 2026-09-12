<template>
  <div class="availability-page" v-loading="loading">
    <div class="availability-toolbar card">
      <div>
        <div class="page-title">火山云GPU资源管理</div>
        <div class="page-subtitle">实时可售资源与火山云 ECS 目录合并展示</div>
      </div>
      <div class="toolbar-actions">
        <el-tag v-if="catalog.source" :type="sourceTagType">
          {{ sourceLabel }}
        </el-tag>
        <el-button type="primary" :icon="Refresh" @click="loadCatalog">
          刷新
        </el-button>
      </div>
    </div>

    <el-alert
      v-if="errorMessage"
      class="availability-alert"
      type="error"
      :title="errorMessage"
      show-icon
      :closable="false"
    />

    <el-alert
      v-else-if="catalog.mergeMode === 'availability+catalog' || catalog.availabilitySource"
      class="availability-alert"
      type="success"
      :title="catalog.mergeMessage || '实时可用资源与完整 ECS 目录已合并'"
      show-icon
      :closable="false"
    />

    <el-alert
      v-else-if="catalog.pricing?.status === 'unavailable'"
      class="availability-alert"
      type="warning"
      :title="catalog.pricing?.error || '当前目录价格暂不可用，实例规格仍可查看'"
      show-icon
      :closable="false"
    />

    <div class="metadata-grid card">
      <div><span>快照日期</span><strong>{{ catalog.date || "实时目录" }}</strong></div>
      <div><span>目录更新时间</span><strong>{{ formatTime(catalog.updatedAt) }}</strong></div>
      <div><span>实时可用更新时间</span><strong>{{ formatTime(catalog.availabilityUpdatedAt) }}</strong></div>
      <div><span>价格状态</span><strong>{{ pricingStatus }}</strong></div>
      <div><span>目录实例数</span><strong>{{ pricingTotal }}</strong></div>
      <div><span>实时可售实例规格数</span><strong>{{ availableInstanceTotal }}</strong></div>
      <div><span>地域数量</span><strong>{{ regions.length }}</strong></div>
    </div>

    <div class="filter-bar card">
      <el-select v-model="filters.region" clearable placeholder="全部地域" class="filter-control">
        <el-option v-for="region in regionOptions" :key="region" :label="region" :value="region" />
      </el-select>
      <el-select v-model="filters.gpuModel" clearable filterable placeholder="全部 GPU 型号" class="filter-control">
        <el-option v-for="model in gpuModelOptions" :key="model" :label="model" :value="model" />
      </el-select>
      <el-select v-model="filters.gpuCount" clearable placeholder="全部 GPU 卡数" class="filter-control gpu-count-filter">
        <el-option v-for="count in gpuCountOptions" :key="count" :label="`${count} 卡`" :value="String(count)" />
      </el-select>
      <el-input v-model="filters.instanceType" clearable placeholder="搜索实例规格" class="filter-control instance-filter" />
      <el-button :icon="RefreshLeft" @click="resetFilters">重置</el-button>
      <span class="filter-result">匹配 {{ filteredRegions.length }} 个地域</span>
    </div>

    <el-card shadow="never" class="region-card">
      <template #header>
        <div class="section-header">
          <span>地域可售资源</span>
          <span class="section-count">{{ regions.length }} 个地域</span>
        </div>
      </template>

      <el-collapse v-if="filteredRegions.length" v-model="activeRegions">
        <el-collapse-item
          v-for="region in filteredRegions"
          :key="region.region"
          :name="region.region"
                >
          <template #title>
            <div class="region-title">
              <span>{{ region.region || "未知地域" }}</span>
              <el-tag v-if="region.error" size="small" type="danger">无货/异常</el-tag>
              <el-tag v-else size="small" type="success">
                {{ gpuSpecCount(region) }} 个 GPU 规格
              </el-tag>
            </div>
          </template>

          <el-alert
            v-if="region.error"
            type="warning"
            :title="region.error"
            show-icon
            :closable="false"
          />
          <template v-else>
            <div v-if="gpuSpecs(region).length" class="spec-list">
              <el-card
                v-for="spec in gpuSpecs(region)"
                :key="`${spec.gpuModel}-${spec.gpuMemory}`"
                shadow="never"
                class="spec-card"
              >
                <template #header>
                  <div class="spec-title">
                    <span>{{ spec.gpuModel || "未知型号" }}</span>
                    <el-tag size="small">{{ spec.gpuMemory || "显存未知" }}</el-tag>
                    <span class="spec-count">卡数：{{ countsText(spec.gpuCounts) }}</span>
                  </div>
                </template>
                <el-table :data="instanceTypes(spec)" border size="small">
                  <el-table-column prop="instanceTypeId" label="实例规格" min-width="190" show-overflow-tooltip />
                  <el-table-column label="按量/小时" width="120" fixed="left">
                    <template #default="{ row }">{{ priceText(row.price, spec.price) }}</template>
                  </el-table-column>
                  <el-table-column label="包月" width="120" fixed="left">
                    <template #default="{ row }">{{ priceText(row.priceMonthly, spec.priceMonthly) }}</template>
                  </el-table-column>
                  <el-table-column prop="gpuCount" label="GPU卡数" width="90" />
                  <el-table-column prop="cpuCores" label="CPU核数" width="90" />
                  <el-table-column prop="cpuModel" label="CPU型号" min-width="180" show-overflow-tooltip />
                  <el-table-column label="内存(GiB)" width="110">
                    <template #default="{ row }">{{ valueText(row.memGib) }}</template>
                  </el-table-column>
                </el-table>
                <el-empty v-if="!instanceTypes(spec).length" description="暂无有货实例规格" />
              </el-card>
            </div>
            <el-empty v-else description="暂无可售 GPU" />
          </template>
        </el-collapse-item>
      </el-collapse>
      <el-empty v-else description="没有匹配的火山云 GPU 数据" />
    </el-card>
  </div>
</template>

<script setup lang="ts" name="VolcanoGpuAvailability">
import { computed, onMounted, ref } from "vue";
import { Refresh, RefreshLeft } from "@element-plus/icons-vue";
import dayjs from "dayjs";
import { gpuCatalogApi } from "@/api/gpuAvailability";

type Availability = {
  success?: boolean;
  source?: string;
  date?: string;
  generatedAt?: string;
  updatedAt?: string;
  availabilityUpdatedAt?: string;
  availabilitySource?: string;
  mergeMode?: string;
  mergeMessage?: string;
  pricing?: { status?: string; error?: string; total?: number; available?: number };
  regions?: Region[];
};
type Region = { region?: string; gpuSpecs?: GpuSpec[]; error?: string };
type GpuSpec = {
  gpuModel?: string;
  gpuMemory?: string;
  gpuCounts?: number[];
  price?: PriceValue | number | string | null;
  priceMonthly?: PriceValue | number | string | null;
  instanceTypes?: InstanceType[];
};
type InstanceType = {
  instanceTypeId?: string;
  gpuCount?: number;
  cpuCores?: number;
  memGib?: number;
  price?: PriceValue | number | string | null;
  priceMonthly?: PriceValue | number | string | null;
};
type PriceValue = { available?: boolean; unitPrice?: number | null; error?: string; currency?: string };

const loading = ref(false);
const errorMessage = ref("");
const catalog = ref<Availability>({ regions: [] });
const activeRegions = ref<string[]>([]);
const filters = ref({ region: "", gpuModel: "", gpuCount: "", instanceType: "" });
const regions = computed(() => catalog.value.regions || []);
const regionOptions = computed(() => regions.value.map((item) => item.region || "").filter(Boolean));
const gpuModelOptions = computed(() => Array.from(new Set(regions.value.flatMap((region) => gpuSpecs(region).map((spec) => spec.gpuModel || "")).filter(Boolean))).sort());
const gpuCountOptions = computed(() => Array.from(new Set(
  regions.value.flatMap((region) => gpuSpecs(region).flatMap((spec) => [
    ...(Array.isArray(spec.gpuCounts) ? spec.gpuCounts : []),
    ...instanceTypes(spec).map((item) => item.gpuCount),
  ])),
)).filter((count) => Number.isFinite(Number(count))).sort((a, b) => Number(a) - Number(b)));
const filteredRegions = computed(() => regions.value.map((region) => {
  const filteredSpecs = gpuSpecs(region)
    .map((spec) => ({
      ...spec,
      instanceTypes: instanceTypes(spec).filter((item) =>
        (!filters.value.gpuCount || Number(item.gpuCount) === Number(filters.value.gpuCount)) &&
        (!filters.value.instanceType || String(item.instanceTypeId || "")
          .toLowerCase()
          .includes(filters.value.instanceType.toLowerCase())),
      ),
    }))
    .filter((spec) => !filters.value.gpuModel || spec.gpuModel === filters.value.gpuModel)
    .filter((spec) => !filters.value.gpuCount || (
      (Array.isArray(spec.gpuCounts) && spec.gpuCounts.includes(Number(filters.value.gpuCount))) ||
      instanceTypes(spec).some((item) => Number(item.gpuCount) === Number(filters.value.gpuCount))
    ))
    .filter((spec) => (!filters.value.instanceType && !filters.value.gpuCount) || instanceTypes(spec).length > 0);
  return { ...region, gpuSpecs: filteredSpecs };
}).filter((region) =>
  (!filters.value.region || region.region === filters.value.region) &&
  (Boolean(region.error) || gpuSpecs(region).length > 0),
));

const sourceLabel = computed(() => catalog.value.availabilitySource
  ? "实时可售 + ECS 目录"
  : catalog.value.source === "volcengine-ecs" ? "火山云 ECS 目录" : catalog.value.source || "数据不可用");
const sourceTagType = computed(() => catalog.value.success === false ? "danger" : catalog.value.pricing?.status === "unavailable" ? "warning" : "success");
const pricingStatus = computed(() => catalog.value.pricing?.status === "unavailable" ? "价格未开通" : "可用");
const pricingTotal = computed(() => catalog.value.pricing?.total ?? "--");
const availableInstanceTotal = computed(() => regions.value.reduce(
  (total, region) => total + gpuSpecs(region).reduce((specTotal, spec) => specTotal + instanceTypes(spec).length, 0),
  0,
));

const loadCatalog = async () => {
  loading.value = true;
  errorMessage.value = "";
  try {
    const response = await gpuCatalogApi();
    const data = response?.data || {};
    catalog.value = { ...data, regions: Array.isArray(data.regions) ? data.regions : [] };
    const firstAvailableRegion = regions.value.find((item) => !item.error)?.region;
    activeRegions.value = firstAvailableRegion ? [firstAvailableRegion] : [];
    if (data.success === false) errorMessage.value = data.message || "火山云 GPU 目录获取失败";
  } catch (error: any) {
    errorMessage.value = error?.message || "火山云 GPU 目录获取失败";
    catalog.value = { regions: [] };
  } finally {
    loading.value = false;
  }
};

const resetFilters = () => {
  filters.value = { region: "", gpuModel: "", gpuCount: "", instanceType: "" };
};

const gpuSpecs = (region: Region) => Array.isArray(region.gpuSpecs) ? region.gpuSpecs : [];
const instanceTypes = (spec: GpuSpec) => Array.isArray(spec.instanceTypes) ? spec.instanceTypes : [];
const gpuSpecCount = (region: Region) => gpuSpecs(region).length;
const countsText = (counts?: number[]) => Array.isArray(counts) && counts.length ? counts.join(" / ") : "--";
const valueText = (value: unknown) => value === null || value === undefined || value === "" ? "--" : String(value);
const priceText = (
  value: PriceValue | number | string | null | undefined,
  fallback?: PriceValue | number | string | null,
) => {
  const number = priceNumber(value) ?? priceNumber(fallback);
  return number === null ? "--" : `¥${number.toFixed(2)}`;
};
const priceNumber = (value: PriceValue | number | string | null | undefined): number | null => {
  if (value === null || value === undefined) return null;
  if (typeof value === "number") return Number.isFinite(value) && value > 0 ? value : null;
  if (typeof value === "string") {
    const number = Number(value);
    return Number.isFinite(number) && number > 0 ? number : null;
  }
  if (value.available === false) return null;
  const number = Number(value.unitPrice ?? (value as any).unit_price ?? (value as any).discountAmount);
  return Number.isFinite(number) && number > 0 ? number : null;
};
const formatTime = (value?: string) => value ? dayjs(value).format("YYYY-MM-DD HH:mm:ss") : "--";

onMounted(loadCatalog);
</script>

<style lang="scss" scoped>
.availability-page { padding: 12px; }
.availability-toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
.page-title { font-size: 18px; font-weight: 600; color: var(--el-text-color-primary); }
.page-subtitle { margin-top: 6px; color: var(--el-text-color-secondary); font-size: 13px; }
.toolbar-actions { display: flex; align-items: center; gap: 12px; }
.availability-alert { margin-bottom: 12px; }
.filter-bar { display: flex; align-items: center; gap: 10px; margin-bottom: 12px; }
.filter-control { width: 190px; }
.gpu-count-filter { width: 160px; }
.instance-filter { width: 220px; }
.filter-result { margin-left: auto; color: var(--el-text-color-secondary); font-size: 13px; }
.metadata-grid { display: grid; grid-template-columns: repeat(4, minmax(150px, 1fr)); gap: 12px; margin-bottom: 12px; }
.metadata-grid div { display: flex; flex-direction: column; gap: 6px; }
.metadata-grid span { color: var(--el-text-color-secondary); font-size: 13px; }
.metadata-grid strong { color: var(--el-text-color-primary); font-size: 16px; }
.region-card, .spec-card { border: 1px solid var(--el-border-color-lighter); }
.section-header, .region-title, .spec-title { display: flex; align-items: center; gap: 10px; }
.section-header { justify-content: space-between; }
.section-count, .spec-count { color: var(--el-text-color-secondary); font-size: 12px; }
.region-title { width: 100%; justify-content: space-between; padding-right: 12px; }
.spec-list { display: flex; flex-direction: column; gap: 12px; }
.spec-title { font-weight: 600; }
.spec-title .spec-count { margin-left: auto; }
@media (max-width: 760px) {
  .availability-toolbar { align-items: flex-start; gap: 12px; flex-direction: column; }
  .filter-bar { align-items: stretch; flex-wrap: wrap; }
  .filter-control, .instance-filter, .gpu-count-filter { width: 100%; }
  .filter-result { width: 100%; margin-left: 0; }
  .metadata-grid { grid-template-columns: repeat(2, minmax(120px, 1fr)); }
}
</style>
