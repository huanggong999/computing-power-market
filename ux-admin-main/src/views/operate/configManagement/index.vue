<template>
  <div class="table-box card">
    <h2>平台溢价</h2>
    <div class="flx-align-center">
      <el-icon><Warning /></el-icon>
      <h3 class="ml10">设置全局溢价</h3>
      <p class="ml10">默认溢价为 90% , 请注意调整</p>
    </div>
    <div class="flx-align-center">
      <p class="ml25 mr10">
        当前平台溢价:
        {{ conversionPercentage(premiumForm.configValue.priceRatio) }}
      </p>
      <el-button link type="primary" @click="openPopover(premiumForm)">
        修改溢价 <el-icon class="el-icon--right"><ArrowRight /></el-icon>
      </el-button>
      <h4 class="flx-align-center ml5 mr10">
        <el-icon class="mt4 mr5"><Warning /></el-icon> 特殊说明
      </h4>
      <p>溢价公式 ==> 火山引擎各类产品原价 * 溢价比例 = 平台呈现原价价格</p>
    </div>
    <div>
      <h3 class="ml25">价格计算公式</h3>
      <div
        class="ml25 flx-align-center"
        v-for="item in priceCalculationFormula"
        :key="item.serialNumber"
      >
        <div class="number mr15">{{ item.serialNumber }}</div>
        <div class="text">
          <p>{{ item.title }}</p>
          <p>{{ item.text }}</p>
        </div>
      </div>
    </div>
    <h2>退款</h2>
    <div class="flx-align-center">
      <el-icon><Warning /></el-icon>
      <h3 class="ml10">设置平台退款比例</h3>
      <p class="ml10">默认退款比例为 90% , 请注意调整</p>
    </div>
    <div class="flx-align-center">
      <p class="ml25 mr10">
        当前退款比例:
        {{ conversionPercentage(refundForm.configValue.priceRatio) }}
      </p>
      <el-button link type="primary" @click="openPopover(refundForm)">
        修改比例 <el-icon class="el-icon--right"><ArrowRight /></el-icon>
      </el-button>
      <h4 class="flx-align-center ml5 mr10">
        <el-icon class="mt4 mr5"><Warning /></el-icon> 特殊说明
      </h4>
      <p>
        退款金额 ==> 火山引擎各类产品退款金额 * 平台退款比例 =
        实际退还到用户账户金额
      </p>
    </div>

    <!-- 默认配置 -->
    <div>
      <el-row :gutter="30">
        <el-col class="mt20" :xs="24" :sm="24" :md="24" :lg="12" :xl="12">
          <Descriptions
            :list="serverSystemCloudList"
            :column="3"
            title="服务器系统云盘"
          >
            <template #extra>
              <el-button
                link
                type="primary"
                @click="openPopover(serverSystemCloudDisk)"
              >
                服务器系统云盘
                <el-icon class="el-icon--right"><ArrowRight /></el-icon>
              </el-button>
            </template>
          </Descriptions>
        </el-col>
        <el-col class="mt20" :xs="24" :sm="24" :md="24" :lg="12" :xl="12">
          <Descriptions
            :list="eipAddressList"
            :column="3"
            title="公网ip默认配置"
          >
            <template #extra>
              <el-button
                link
                type="primary"
                @click="openPopover(eipAddressForm)"
              >
                服务器系统云盘
                <el-icon class="el-icon--right"><ArrowRight /></el-icon>
              </el-button>
            </template>
          </Descriptions>
        </el-col>
      </el-row>
    </div>
    <Drawer
      v-model="addOrEdit"
      @closePopover="closePopover"
      title="修改配置"
      @submit="submit"
    >
      <el-form ref="formRef" :model="dataForm" :label-width="80">
        <el-form-item
          :label="
            dataForm.configKey === 'SELL_PRICE_RATIO' ? '溢价比例' : '退款比例'
          "
          v-if="
            ['SELL_PRICE_RATIO', 'RETURN_PRICE_RATIO'].includes(
              dataForm.configKey
            )
          "
        >
          <el-input-number
            v-model="dataForm.configValue.priceRatio"
            :min="0"
            :max="dataForm.configKey === 'SELL_PRICE_RATIO' ? 10 : 1"
            :precision="2"
            :controls="false"
          />
        </el-form-item>
        <template v-if="dataForm.configKey === 'ECS_SYSTEM_VOLUME'">
          <el-form-item label="云盘大小">
            <el-input-number
              v-model="dataForm.configValue.size"
              :controls="false"
              :max="2048"
              :min="40"
              :precision="0"
            >
              <template #suffix>
                <span>GiB</span>
              </template>
            </el-input-number>
          </el-form-item>
          <el-form-item label="按量计费">
            <el-input-number
              v-model="dataForm.configValue.hoursPrice"
              :controls="false"
            />
          </el-form-item>
          <el-form-item label="包年包月">
            <el-input-number
              v-model="dataForm.configValue.monthPrice"
              :controls="false"
            />
          </el-form-item>
          <el-form-item label="1年">
            <el-input-number
              v-model="dataForm.configValue.oneYearPrice"
              :controls="false"
            />
          </el-form-item>
          <el-form-item label="2年">
            <el-input-number
              v-model="dataForm.configValue.twoYearPrice"
              :controls="false"
            />
          </el-form-item>
          <el-form-item label="3年">
            <el-input-number
              v-model="dataForm.configValue.threeYearPrice"
              :controls="false"
            />
          </el-form-item>
        </template>
        <template v-if="dataForm.configKey === 'EIP_ADDRESS'">
          <el-form-item label="宽带">
            <el-input-number
              v-model="dataForm.configValue.bandwidthMbps"
              :controls="false"
              :max="200"
              :min="1"
              :precision="0"
            >
              <template #suffix>
                <span>mb</span>
              </template>
            </el-input-number>
          </el-form-item>
          <el-form-item label="价格">
            <el-input-number
              v-model="dataForm.configValue.trafficPrice"
              :controls="false"
            >
              <template #suffix>
                <span>/gb</span>
              </template>
            </el-input-number>
          </el-form-item>
        </template>
      </el-form>
    </Drawer>
  </div>
</template>

<script setup lang="ts" name="ConfigManagement">
import { getConfigApi, updateConfigApi } from "@/api";

// 溢价
const premiumForm = ref<IConfig<TKeyValue>>({
  configKey: "SELL_PRICE_RATIO",
  configValue: { priceRatio: 0 },
});
// 退款
const refundForm = ref<IConfig<TKeyValue>>({
  configKey: "RETURN_PRICE_RATIO",
  configValue: { priceRatio: 0 },
});
// 服务器系统云盘
const serverSystemCloudDisk = ref<IConfig<TKeyValue>>({
  configKey: "ECS_SYSTEM_VOLUME",
  configValue: {},
});
// 公网ip默认配置
const eipAddressForm = ref<IConfig<TKeyValue>>({
  configKey: "EIP_ADDRESS",
  configValue: {},
});

// 定义配置项映射
const configMappings = [
  { key: "EIP_ADDRESS", target: eipAddressForm },
  { key: "ECS_SYSTEM_VOLUME", target: serverSystemCloudDisk },
  { key: "RETURN_PRICE_RATIO", target: refundForm },
  { key: "SELL_PRICE_RATIO", target: premiumForm },
];

const fetchConfig = async (key: TConfigKey, target: Ref<TKeyValue>) => {
  const { data } = await getConfigApi(key);
  target.value = data;
};

const fetchAllConfigs = async () => {
  await Promise.all(
    configMappings.map(({ key, target }) =>
      fetchConfig(key as TConfigKey, target)
    )
  );
};
fetchAllConfigs();

// 修改百分比
const conversionPercentage = (num: number | string): string => {
  const parsedNum = parseFloat(num as string); // 解析为浮点数
  if (isNaN(parsedNum)) return "0.00%"; // 返回默认值
  return `${(parsedNum * 100).toFixed(2)}%`; // 格式化为百分比
};

const addOrEdit = ref(false);
const closePopover = () => (addOrEdit.value = false);
const dataForm = ref<IConfig<TKeyValue>>({
  configKey: "",
  configValue: {},
});

const openPopover = (data: IConfig<TKeyValue>) => {
  dataForm.value = JSON.parse(JSON.stringify(data));
  addOrEdit.value = true;
};
const submit = async () => {
  await updateConfigApi(dataForm.value.configKey, dataForm.value.configValue);
  const findObj = configMappings.find(
    (item) => item.key === dataForm.value.configKey
  );
  fetchConfig(findObj!.key as TConfigKey, findObj!.target);
  closePopover();
};

// 服务器系统云盘
const serverSystemCloudDiskValue = computed(
  () => serverSystemCloudDisk.value.configValue
);
const serverSystemCloudList = computed(() => [
  { label: "云盘大小 (GiB)", value: serverSystemCloudDiskValue.value.size },
  { label: "按量计费", value: serverSystemCloudDiskValue.value.hoursPrice },
  { label: "包年包月", value: serverSystemCloudDiskValue.value.monthPrice },
  { label: "1年", value: serverSystemCloudDiskValue.value.oneYearPrice },
  { label: "2年", value: serverSystemCloudDiskValue.value.twoYearPrice },
  { label: "3年", value: serverSystemCloudDiskValue.value.threeYearPrice },
]);
// 公网ip默认配置
const eipAddressValue = computed(() => eipAddressForm.value.configValue);
const eipAddressList = computed(() => [
  { label: "宽带 (mb)", value: eipAddressValue.value.bandwidthMbps },
  { label: "价格 (/gb)", value: eipAddressValue.value.trafficPrice },
]);

// 价格计算公式
const priceCalculationFormula = [
  {
    serialNumber: 1,
    title: "1次溢价计算, AI平台原价",
    text: "平台呈现原价价格 = 火山引擎各类产品原价 * 溢价比例",
  },
  {
    serialNumber: 2,
    title: "2次溢价计算, AI平台产品活动价",
    text: "平台产品活动价 = 平台呈现原价价格 * 用户设置的产品溢价比例",
  },
  {
    serialNumber: 3,
    title: "3次溢价计算, AI平台产品实付价",
    text: "平台产品实付价 = 平台产品活动价格 - 代金券抵扣金额/优惠金额",
  },
];
</script>
<style lang="scss" scoped>
h1,
h2,
h3,
h4,
h5,
h6,
p {
  margin: 10px 0;
}
.number {
  border: 1px solid #bbb;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  text-align: center;
  line-height: 30px;
}
</style>
