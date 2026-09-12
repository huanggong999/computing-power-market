/** 创建集群 */
<template>
  <Breadcrumb :router-list="routerList" />
  <div v-if="userInfo.totalBalance < 0" class="owe-tip ml20 mb20">
    当前账户总余额欠费,暂不支持创建实例。请先缴清欠款后再尝试创建实例。
  </div>
  <div class="card">
    <TipText content="基础信息" class="mb20" />
    <ProForm
      ref="basicInfoRef"
      v-model="basicInfo"
      :label-width="140"
      :formColumns="basicInfoFormCol"
    />
  </div>
  <div class="card mt20">
    <TipText content="网络配置" class="mb20" />
    <ProForm
      ref="networkConfigRef"
      v-model="basicInfo"
      :label-width="120"
      :formColumns="networkConfigFormCol"
    />
  </div>
  <!-- <div class="card mt20">
    <TipText content="运营配置" class="mb20" />
    <ProForm
      ref="operationalConfigRef"
      v-model="basicInfo"
      :label-width="120"
      :formColumns="operationalConfigFormCol"
    />
  </div> -->
  <div class="card mt20">
    <TipText content="节点信息" class="mb20" />
    <ProForm
      ref="nodeInfoFormRef"
      v-model="basicInfo"
      :label-width="120"
      :formColumns="nodeInfoFormCol"
    />
  </div>
  <div class="card mt20">
    <TipText content="节点池配置" class="mb20" />
    <ProForm
      ref="nodePoolConfigFormRef"
      v-model="basicInfo"
      :label-width="120"
      :formColumns="nodePoolConfigFormCol"
    >
      <template #specification>
        <div class="specification">
          <div class="line2 mb28">
            <el-radio-group v-model="basicInfo.ecsTypeEnum">
              <el-radio-button
                v-for="(item, index) in ecsTypeEnum"
                :key="index"
                :label="item.label"
                :value="item.value"
              />
            </el-radio-group>
          </div>
          <div class="line3 mb20">
            <ProTable
              ref="specificationTable"
              :page-data="pageData"
              :columns="specificationTableCol"
              :tableData="specificationTableData"
              :search-param="specificationTableParam"
              :search-fn="specificationTableSearch"
              :getList="specificationTableGet"
              :IsRefresh="false"
              type="radio"
              :radioData="specificationTableSelect"
              style="width: 100%"
              :is-page="false"
              :loading="specificationTableLoading"
            >
              <template #ecsType="row">
                {{
                  ecsTypeEnum.filter((item) => item.value == row["ecsType"])[0]
                    .label
                }}
              </template>
              <template #cpuNumber="row"> {{ row.cpuNumber }} vCpu </template>
              <template #price="row">
                {{
                  basicInfo.chargeType === "POSTPAID_BY_HOUR"
                    ? row.hoursPrice + "/小时"
                    : row.monthPrice + "/月"
                }}
              </template>
            </ProTable>
            <!-- <Pagination :page-data="pageData" :PageChange="specificationTableGet" /> -->
          </div>
          <div class="line4 flx-align-center mt20">
            当前所选:
            <div v-if="specificationTableSelect">
              <span class="blue tag">{{
                specificationTableSelect
                  ? ecsTypeEnum.filter(
                      (item) => item.value == specificationTableSelect.ecsType
                    )[0].label
                  : "-"
              }}</span
              ><span class="blue"
                >{{
                  specificationTableSelect
                    ? specificationTableSelect.ecsScale
                    : "-"
                }}|{{
                  specificationTableSelect
                    ? specificationTableSelect.cpuNumber
                    : "-"
                }}
                vCPU|{{
                  specificationTableSelect
                    ? specificationTableSelect.memorySize
                    : "-"
                }}GB</span
              >
            </div>
          </div>
        </div>
      </template>
    </ProForm>
  </div>
  <div class="card mt20">
    <TipText content="系统配置" class="mb20" />
    <ProForm
      ref="systemConfigFormRef"
      v-model="basicInfo"
      :label-width="120"
      :formColumns="systemConfigFormCol"
    >
      <template #visibility="row">
        <div class="visibility mb40">
          <div class="line1">
            <el-radio-group v-model="basicInfo.visibility">
              <el-radio label="公共镜像" value="public" />
            </el-radio-group>
          </div>
          <div class="line2 flx-align-center mt20">
            <el-select
              placeholder="请选择镜像"
              v-model="basicInfo.platform"
              class="mr20"
            >
              <el-option
                v-for="item in platformEnum"
                :key="item.label"
                :label="item.label"
                :value="item.value"
              ></el-option>
            </el-select>
            <el-select
              placeholder="请选择镜像版本"
              v-model="basicInfo.platformVersion"
              value-key="imageName"
              class="mr20"
            >
              <el-option
                v-for="item in platformVersionList"
                :key="item.imageName"
                :label="item.imageName"
                :value="item"
              ></el-option>
            </el-select>
            <el-checkbox
              v-model="basicInfo.isAgent"
              label="启动时安装批量作业Agent"
              size="large"
            />
          </div>
        </div>
      </template>
      <template #storage="row">
        <div class="storage mb40">
          <div class="line1">
            <el-table :data="diskData" style="width: 100%" border>
              <el-table-column label="用途">
                <template #default="scope">{{ scope.row.useWay }}</template>
              </el-table-column>
              <el-table-column label="类型" width="200">
                <template #default="scope">{{ scope.row.volumeType }}</template>
              </el-table-column>
              <el-table-column label="性能级别" width="400">
                <template #default="scope">{{
                  scope.row.performanceLevel
                    ? scope.row.performanceLevel
                    : scope.row.volumeScale
                }}</template>
              </el-table-column>
              <el-table-column label="容量">
                <template #default="scope"
                  >{{
                    scope.row.size ? scope.row.size : scope.row.volumeCapacity
                  }}GiB</template
                >
              </el-table-column>
              <el-table-column label="IOPS">
                <template #default="scope">{{
                  scope.row.iops ? scope.row.iops : scope.row.ioPs
                }}</template>
              </el-table-column>
              <el-table-column label="吞吐量" width="200">
                <template #default="scope"
                  >{{ scope.row.throughput }}MB/S</template
                >
              </el-table-column>
              <el-table-column label="操作">
                <template #default="scope">
                  <span
                    class="red"
                    v-if="scope.row.useWay !== '系统盘'"
                    @click="deleteDataDisk"
                    >删除</span
                  ></template
                >
              </el-table-column>
            </el-table>
          </div>
          <!-- todo: 恢复 -->
          <div class="line2 flx-center mt20">
            <span class="flx-align-center" @click="addDataDisk"
              ><el-icon>
                <CirclePlus />
              </el-icon>
              添加数据盘</span
            >
            您还可以挂载{{ cloudLimit }}块数据盘
          </div>
        </div>
      </template>
    </ProForm>
  </div>
  <div class="card mt20 mb60">
    <TipText content="安全配置" class="mb20" />
    <ProForm
      ref="securityConfigFormRef"
      v-model="basicInfo"
      :label-width="120"
      :formColumns="securityConfigFormCol"
    >
      <template #test>
        <el-tooltip
          class="box-item"
          effect="dark"
          content="Top Left prompts info"
          placement="top-start"
        >
          <el-icon color="#ccc">
            <QuestionFilled />
          </el-icon>
        </el-tooltip>
        <el-switch
          class="ml20"
          placeholder="请选择"
          v-model.trim="basicInfo.test"
          active-text="开启"
          inactive-text="关闭"
          inline-prompt
        />
      </template>
    </ProForm>
  </div>
  <!-- <div class="card mt20">
    <TipText content="组件配置" class="mb20" />
    <div class="componentConfig bgColor">
      <el-checkbox
        v-model="checkAll"
        :indeterminate="isIndeterminate"
        @change="handleCheckAllChange"
      >
        全部
      </el-checkbox>
      <el-checkbox-group
        v-model="checkedCities"
        @change="handleCheckedCitiesChange"
      >
        <el-checkbox
          v-for="el in componentConfigList"
          :key="el"
          :label="el"
          :value="el"
        />
      </el-checkbox-group>
    </div>
  </div> -->
  <div class="card five mt20 flx-align-center">
    <div
      class="time flx-align-center mr60"
      v-if="basicInfo.chargeType === 'POSTPAID_BY_MONTH'"
    >
      <div class="label mr16">购买时长</div>
      <el-select value-key="result" style="width: 118px" v-model="duration">
        <el-option
          v-for="(item, index) in durationList"
          :key="index"
          :label="item.label"
          :value="item"
        ></el-option>
      </el-select>
    </div>
    <div class="label">节点数量</div>
    <div class="control flx-align-center ml32 mr60">
      <div
        class="reduce flx-center same"
        @click="instanceNum > 1 ? instanceNum-- : ''"
      >
        -
      </div>
      <div class="num">{{ instanceNum }}台</div>
      <div class="plus flx-center same" @click="instanceNum++">+</div>
    </div>
    <div class="flx-align-center mr64 footer-fix">
      <div class="label">配置费用</div>
      <div class="price ml14">
        ￥{{ footerPrice.allocationPrice }}/{{
          basicInfo.chargeType === "POSTPAID_BY_HOUR"
            ? "时"
            : duration.result.durationUnit === "YEAR"
            ? ` ${duration.result.num == 1 ? "" : duration.result.num}年`
            : "月"
        }}
      </div>
      <el-tooltip
        class="box-item ml6"
        effect="dark"
        content="Left prompts info"
        placement="top-start"
      >
        <el-icon>
          <QuestionFilled />
        </el-icon>
      </el-tooltip>
    </div>
    <div class="flx-align-center">
      <div class="label">公网流量费</div>
      <div class="price ml14">￥{{ footerPrice.ipPrice }}/GB</div>
      <el-tooltip
        class="box-item ml6"
        effect="dark"
        content="Left prompts info"
        placement="top-start"
      >
        <el-icon>
          <QuestionFilled />
        </el-icon>
      </el-tooltip>
    </div>
    <div class="confirm">
      <el-button>取消</el-button>
      <el-button
        type="primary"
        @click="toPay"
        :disabled="userInfo.totalBalance < 0"
        >确认订单</el-button
      >
    </div>
  </div>
</template>

<script setup lang="ts" name="CreateCluster">
import { getDataConfigApi, getEnvConfigApi } from "@/api/env";
import { buildOrderInfoApi, getEcsListApi } from "@/api/order";
import { useTable } from "@/hooks/useTable";
import { generateUUID, toPage } from "@/utils";
import {
  kubernetesVersionTypeEnum,
  loginTypeEnum,
  nodeSourceTypeEnum,
  securityGroupTypeEnum,
  regionEnum,
  ecsTypeEnum,
  workerNodeTypeEnum,
  platformEnum,
} from "@/utils/radioEnum";
import { ElMessage } from "element-plus";
import { checkPassword } from "@/utils/password";
import { useUserInfo } from "@/store";
import { debounce } from "lodash";
import { useOrder } from "@/store/modules/order";
import { getAvailableImageVersionApi } from "@/api/container";
const userInfo = useUserInfo();
const orderDetail = useOrder();
const routerList = ref([
  { name: "容器列表", path: "/containerList" },
  { name: "创建集群" },
]);
const basicInfo = ref<any>({
  kubernetesVersion: "1.30",
  resourcePublicAccessDefaultEnabled: true,
  apiServerPublicAccessEnabled: true,
  createNow: true,
  nodeSource: true,
  visibility: "public",
  chargeType: "POSTPAID_BY_HOUR",
  ecsTypeEnum: "GENERAL_COMPUTE",
  region: true,
  platform: "",
  isAgent: true,
  default: true,
  isOpenSecurityHardening: true,
  platformVersion: { type: "" },
});
//#region  组件配置
const checkAll = ref(false);
const checkedCities = ref();
const isIndeterminate = ref(false);
// const handleCheckAllChange = (val: any) => {
//   checkedCities.value = val ? componentConfigList : []
//   isIndeterminate.value = false
// }
const componentConfigList = [
  "cuda-devel",
  "pytorch-2.1.2",
  "tensorflow-2.15.0",
  "paddlepaddle-2.6.0",
  "tensorrt-8.5.1",
];
const handleCheckedCitiesChange = (value: any) => {
  const checkedCount = value.length;
  const totalComponentConfig = componentConfigList.length;
  checkAll.value = checkedCount === totalComponentConfig;
  isIndeterminate.value =
    checkedCount > 0 && checkedCount < totalComponentConfig;
};
//#endregion
//
// 基础信息
const basicInfoFormCol: IFormColumnsProps[] = [
  { label: "容器名称", prop: "clusterName", el: "input" },
  {
    label: "Kubernetes 版本",
    prop: "kubernetesVersion",
    el: "radioButton",
    radioList: kubernetesVersionTypeEnum,
  },
];
// 网络配置
const networkConfigFormCol: IFormColumnsProps[] = [
  {
    label: "公网访问",
    prop: "resourcePublicAccessDefaultEnabled",
    el: "switch",
    inlinePrompt: true,
    tips: "开启后，自动为集群专有网络创建 NAT 网关并配置相应规则，满足集群内节点、应用访问公网的需求",
  },
  {
    label: "API Server 公网访问",
    prop: "apiServerPublicAccessEnabled",
    el: "switch",
    inlinePrompt: true,
    tips: "默认创建私网 CLB 用于私网访问，开启后，自动创建 EIP 并关联 CLB 用于集群 API Server IPv4 公网访问CLB 将默认使用「按量计费」的付费模式，建议谨慎修改付费方式，以避免欠费停机导致的集群不可用等问题。",
  },
];
// 运营配置
// const operationalConfigFormCol: IFormColumnsProps[] = [
//   {
//     label: "集群巡检",
//     prop: "",
//     el: "switch",
//     inlinePrompt: true,
//     tips: "扫描集群运行状况, 发现集群中存在的潜在风险。",
//   },
//   { label: "执行时间(每天)", prop: "time", el: "time-select" },
// ];
// 节点信息
const nodeInfoFormCol: IFormColumnsProps[] = [
  {
    label: "Worker 节点",
    prop: "createNow",
    el: "radioButton",
    radioList: workerNodeTypeEnum,
    tips: "同步创建Worker节点并加入到集群中",
  },
  {
    label: "节点来源",
    prop: "nodeSource",
    el: "radio",
    radioList: nodeSourceTypeEnum,
  },
];
// 节点池配置
const nodePoolConfigFormCol: IFormColumnsProps[] = [
  { label: "节点池名称", prop: "nodePoolName", el: "input" },
  {
    label: "计费类型",
    prop: "chargeType",
    el: "radioButton",
    radioList: [
      { label: "按时计费", value: "POSTPAID_BY_HOUR" },
      { label: "包年包月", value: "POSTPAID_BY_MONTH" },
    ],
    required: true,
    tips: "后付费模式，按照实际使用时长以小时为单位收费，适合短期弹性需求，灵活精准，避免浪费",
  },
  {
    label: "可用区",
    prop: "region",
    el: "radioButton",
    radioList: regionEnum,
  },
  // {
  //   label: '多子网调度策略',
  //   prop: '',
  //   el: 'radioButton',
  //   radioList: subnetPolicyTypeEnum,
  //   tips: '扩容出的节点会打散到多个可用区/子网，尽量保证扩容后的各个可用区/子网实例数相对均衡。此时子网的优先级顺序并不会发挥作用。',
  // },
  { label: "计算规格", prop: "specification", el: "slot" },
  // { label: '节点数量', prop: '', el: 'number', controls: false },
];
// 系统配置
const systemConfigFormCol: IFormColumnsProps[] = [
  { label: "操作系统", prop: "visibility", el: "slot" },
  { label: "存储", prop: "storage", el: "slot" },
  // { label: '数据盘', prop: '', el: 'slot' },
];
// 安全配置
const securityConfigFormCol: IFormColumnsProps[] = [
  {
    label: "安全组",
    prop: "default",
    el: "radio",
    radioList: securityGroupTypeEnum,
  },
  {
    label: "登录方式",
    prop: "default",
    el: "radioButton",
    radioList: loginTypeEnum,
  },
  { label: "root密码", prop: "password", el: "password" },
  { label: "确认密码", prop: "rePassword", el: "password" },
  {
    label: "安全加固",
    prop: "isOpenSecurityHardening",
    el: "switch",
    inlinePrompt: true,
  },
];

const specificationTableCol: ColumnProps[] = [
  { label: "规格组", prop: "ecsType", slot: true },
  { label: "实例规格", prop: "ecsScale" },

  { label: "vCPU", prop: "cpuNumber", slot: true },
  {
    label: "内存",
    prop: "memorySize",
  },
  {
    label: "参考价格",
    prop: "price",
    slot: true,
  },
];
const specificationTable = ref<any>(null);
const specificationTableLoading = ref(false);
const {
  pageData,
  getList: specificationTableGet,
  tableData: specificationTableData,
  searchFn: specificationTableSearch,
  searchParam: specificationTableParam,
} = useTable({
  requestApi: getEcsListApi,
  requestAuto: false,
});
// 获取当前所选ecs
const specificationTableSelect = computed(() => {
  return specificationTable.value ? specificationTable.value.radio : null;
});
let abortController: any = null;
watch(
  () => basicInfo.value.ecsTypeEnum,
  () => {
    // 取消上一次请求
    if (abortController) {
      console.log("取消了上次的请求");
      abortController.abort();
    }
    abortController = new AbortController();
    specificationTableParam.value = {
      sourceRegions: "CN_BEIJING",
      ecsTypeEnum: basicInfo.value.ecsTypeEnum,
    };
    specificationTableLoading.value = true;
    specificationTableGet(undefined, abortController.signal).then(() => {
      if (!abortController.signal.aborted) {
        specificationTableLoading.value = false;
        specificationTableData.value.forEach((item) => {
          if (!item.zoneList) {
            item.disabledRadio = true;
          }
        });
      }
    });
  },
  { immediate: true }
);
// 镜像版本数据
const platformVersionList = ref<any>([]);
const platform = computed(() => basicInfo.value.platform);
// 监听 specificationTableSelect.value
watch(
  () => specificationTableSelect.value,
  (newVal) => {
    if (!newVal) return;
    fetchImageVersions();
  }
);

// 监听 basicInfo.value.visibility
watch(
  () => basicInfo.value.visibility,
  () => {
    fetchImageVersions();
  }
);

// 监听 platform
watch(platform, () => {
  fetchImageVersions();
});

// 封装逻辑
const fetchImageVersions = () => {
  if (!specificationTableSelect.value) return;
  getAvailableImageVersionApi({
    instanceTypeId: specificationTableSelect.value.ecsScale,
    type: basicInfo.value.platform,
  }).then((res) => {
    platformVersionList.value = res.data.map((item: any) => item);
    basicInfo.value.platformVersion = { type: "" };
  });
};
// 基本信息(系统云盘)
const diskConfig = ref<any>({});
// (云存储)
const cloudDataConfig = ref<any>({});
const cloudLimit = ref(8);
// 数据盘表格数据
const diskData = ref<any[]>([]);
// 当前配置订单信息
let targetOrderDetail: any = {};
// uuid
const uuid = ref("");
// 底部数据
const instanceNum = ref(1);
const duration = ref<any>({
  label: "1个月",
  result: {
    num: 1,
    durationUnit: "MONTH",
  },
});
const durationList = [
  {
    label: "1个月",
    result: {
      num: 1,
      durationUnit: "MONTH",
    },
  },

  {
    label: "2个月",
    result: {
      num: 2,
      durationUnit: "MONTH",
    },
  },
  {
    label: "3个月",
    result: {
      num: 3,
      durationUnit: "MONTH",
    },
  },
  {
    label: "4个月",
    result: {
      num: 4,
      durationUnit: "MONTH",
    },
  },
  {
    label: "5个月",
    result: {
      num: 5,
      durationUnit: "MONTH",
    },
  },

  {
    label: "6个月",
    result: {
      num: 6,
      durationUnit: "MONTH",
    },
  },

  {
    label: "1年",
    result: {
      num: 1,
      durationUnit: "YEAR",
    },
  },
  {
    label: "2年",
    result: {
      num: 2,
      durationUnit: "YEAR",
    },
  },
  {
    label: "3年",
    result: {
      num: 3,
      durationUnit: "YEAR",
    },
  },
];
const footerPrice: any = ref({
  allocationPrice: 0,
  parts: [],
  ipPrice: 0,
});
// 构建订单信息
const buildOrder = () => {
  const data: any = {
    orderType: "NEW_RESOURCE",
    orderSource: [
      {
        uid: uuid.value,
        regionsId: "CN_BEIJING",
        sourceType: "CONTAINER",
        chargeType: "POSTPAID_BY_HOUR",
        configDetail: {
          clusterName: basicInfo.value.clusterName,
          kubernetesVersion: basicInfo.value.kubernetesVersion,
          resourcePublicAccessDefaultEnabled:
            basicInfo.value.resourcePublicAccessDefaultEnabled,
          apiServerPublicAccessEnabled:
            basicInfo.value.apiServerPublicAccessEnabled,
          nodePoolName: basicInfo.value.nodePoolName,
          nodePoolNumber: instanceNum.value,
          isOpenSecurityHardening: basicInfo.value.isOpenSecurityHardening,
        },
      },
      {
        uid: uuid.value,
        regionsId: "CN_BEIJING",
        sourceType: "ECS",
        chargeType:
          basicInfo.value.chargeType === "POSTPAID_BY_MONTH"
            ? duration.value.result.durationUnit === "YEAR"
              ? "POSTPAID_BY_YEAR"
              : "POSTPAID_BY_MONTH"
            : "POSTPAID_BY_HOUR",
        duration:
          basicInfo.value.chargeType === "POSTPAID_BY_MONTH"
            ? duration.value.result.num
            : null,
        durationUnit:
          basicInfo.value.chargeType === "POSTPAID_BY_MONTH"
            ? duration.value.result.durationUnit
            : null,
        configDetail: {
          ...specificationTableSelect.value,
        },
      },
    ],
  };
  // // 拥有存储盘的情况
  if (diskData.value.length > 1) {
    diskData.value.forEach((item, index) => {
      if (index > 0) {
        const param = {
          uid: uuid.value,
          regionsId: "CN_BEIJING",
          sourceType: "CLOUD_STORAGE",
          configDetail: item,
          chargeType:
            basicInfo.value.chargeType === "POSTPAID_BY_MONTH"
              ? duration.value.result.durationUnit === "YEAR"
                ? "POSTPAID_BY_YEAR"
                : "POSTPAID_BY_MONTH"
              : "POSTPAID_BY_HOUR",
          duration:
            basicInfo.value.chargeType === "POSTPAID_BY_MONTH"
              ? duration.value.result.num
              : null,
          durationUnit:
            basicInfo.value.chargeType === "POSTPAID_BY_MONTH"
              ? duration.value.result.durationUnit
              : null,
        };
        data.orderSource?.push(param);
      }
    });
  }
  buildOrderInfoApi(data).then((res: any) => {
    // 保存当前订单信息
    targetOrderDetail = res.data;
    // 计算底部价格
    footerPrice.value.allocationPrice = res.data.finalPayAmount;
    if (basicInfo.value.chargeType === "POSTPAID_BY_HOUR") {
      footerPrice.value.allocationPrice = res.data.premiumPrice;
    }
    if (duration.value.result.durationUnit === "MONTH") {
      footerPrice.value.allocationPrice /= duration.value.result.num;
      footerPrice.value.allocationPrice = Number(
        footerPrice.value.allocationPrice.toFixed(2)
      );
    }
    // 底部价格计算公网流量费及其他费用的组合
    footerPrice.value.parts = [];
    res.data.orderSourceList.forEach((item: any) => {
      if (item.sourceType === "CLOUD_NETWORK") {
        footerPrice.value.ipPrice = item.finalUnitPrice;
      } else {
        footerPrice.value.parts.push({
          type: item.sourceType,
          price: item.finalUnitPrice,
        });
      }
    });
  });
};
// 添加数据盘
const addDataDisk = () => {
  if (diskData.value.length < 1) {
    ElMessage.warning("请先选择镜像");
    return;
  }

  if (cloudLimit.value) {
    diskData.value.push({
      useWay: "数据盘",
      ...cloudDataConfig.value,
    });
    cloudLimit.value--;
    buildOrder();
  } else {
    ElMessage.warning("已达到最大值");
  }
};
// 删除数据盘
const deleteDataDisk = () => {
  cloudLimit.value++;
  diskData.value.length--;
  buildOrder();
};
const initFunc = async () => {
  // 获取云盘基础配置
  getEnvConfigApi("ECS_SYSTEM_VOLUME").then((res: any) => {
    diskConfig.value = res.data.configValue;
  });
  // 获取数据盘基础配置
  getDataConfigApi("CN_BEIJING").then((res) => {
    cloudDataConfig.value = res.data.list[0];
  });

  uuid.value = generateUUID();
};
// ecs表格选中后，清空表盘旧数据并构建订单信息
watch(
  () => specificationTableSelect.value,
  () => {
    // 重置盘表数据
    diskData.value.length = 0;
    cloudLimit.value = 8;
    diskData.value.push({
      useWay: "系统盘",
      ...diskConfig.value,
    });

    // 构建订单
    buildOrder();
  }
);
const toPay = debounce(
  () => {
    if (!basicInfo.value.platformVersion.type) {
      ElMessage.error("请先完善服务器相关配置信息(镜像版本)！");
      return;
    }
    if (!checkPassword(basicInfo.value.password)) {
      ElMessage.error(
        "密码必须至少8个字符，包含大写字母、小写字母、数字和特殊符号!"
      );
      return;
    }
    if (basicInfo.value.password !== basicInfo.value.rePassword) {
      ElMessage.error("两次密码不一致,请检查");
      return;
    }
    if (basicInfo.value.instanceName) {
      const regex = /^[A-Za-z\u4e00-\u9fa5]/;
      if (!regex.test(basicInfo.value.instanceName)) {
        ElMessage.warning("实例名称需要以数字或字母开头！");
        return;
      }
    }
    diskData.value.forEach((item) => {
      item.uid = uuid.value;
    });
    // 存储服务器信息
    const detail = {
      ...basicInfo.value,
      chargeType:
        basicInfo.value.chargeType === "POSTPAID_BY_MONTH"
          ? duration.value.result.durationUnit === "YEAR"
            ? "POSTPAID_BY_YEAR"
            : "POSTPAID_BY_MONTH"
          : "POSTPAID_BY_HOUR",
      instance: specificationTableSelect.value,
      disk: diskData.value,
      instanceNum: instanceNum.value,
      duration: duration.value.result,
      zoneId: targetOrderDetail.orderSourceList[0].configDetail.zoneList[0].id,
      targetOrderDetail: targetOrderDetail,
      container: {
        clusterName: basicInfo.value.clusterName,
        kubernetesVersion: basicInfo.value.kubernetesVersion,
        resourcePublicAccessDefaultEnabled:
          basicInfo.value.resourcePublicAccessDefaultEnabled,
        apiServerPublicAccessEnabled:
          basicInfo.value.apiServerPublicAccessEnabled,
        nodePoolName: basicInfo.value.nodePoolName,
        nodePoolNumber: instanceNum.value,
        isOpenSecurityHardening: basicInfo.value.isOpenSecurityHardening,
      },
    };
    orderDetail.setOrderDetail(detail);
    // 跳转
    toPage("/ContainerConsole/containerPay");
  },
  1000,
  { leading: true, trailing: false }
);
// 页面启动请求
onMounted(() => {
  initFunc();
});
</script>
<style lang="scss" scoped>
.componentConfig {
  padding: 28px 24px !important;
}

.bgColor {
  background: #f7f8fb;
  border-radius: 10px;
  padding: 32px 28px;
}

.left {
  font-size: 18px;
  color: #666;
}

.number {
  color: #ff4151;
}

.specification {
  .line1 {
    gap: 12px;

    .el-select {
      width: 308px;
    }

    .el-input {
      width: 308px;
    }

    .el-button {
      margin-left: 0;
    }
  }

  .line4 {
    height: 56px;
    background: #e6eeff;
    border-radius: 10px 10px 10px 10px;
    font-weight: 400;
    font-size: 16px;
    color: #83889d;
    padding: 17px 16px;

    .blue {
      color: #3972fd;
    }

    .tag {
      border-radius: 4px 4px 4px 4px;
      border: 1px solid #3972fd;
      margin-left: 13px;
      margin-right: 8px;
      padding: 0 2px;
    }
  }
}

.mirror {
  .line2 {
    gap: 24px;
  }
}

.visibility {
  .el-select {
    width: 308px;
  }
}

.storage {
  .line2 {
    span {
      font-weight: 500;
      font-size: 16px;
      color: #3972fd;
      margin-right: 10px;

      &:hover {
        cursor: pointer;
      }
    }

    font-weight: 400;
    font-size: 16px;
    color: #83889d;

    height: 56px;
    border-radius: 10px 10px 10px 10px;
    border: 1px solid #cccccc;
  }
}

.five {
  z-index: 100;
  width: 86%;
  position: fixed;
  top: 90%;

  .label {
    font-weight: 400;
    font-size: 18px;
    color: #666666;
  }

  .control {
    gap: 14px;
    height: 24px;

    .same {
      width: 24px;
      border-radius: 4px;
      font-weight: bolder;
      font-size: 20px;
      padding-bottom: 3px;

      &:hover {
        cursor: pointer;
        user-select: none;
      }
    }

    .reduce {
      border: #cccccc 1px solid;

      color: #999999;
    }

    .plus {
      background-color: #3972fd;

      color: #fff;
    }
  }

  .price {
    font-weight: 500;
    font-size: 18px;
    color: #ff4151;
  }

  .el-icon {
    color: #cccccc;
  }
}

.tips {
  font-weight: 400;
  font-size: 16px;
  color: #83889d;
  margin-top: 12px;
}

.el-select {
  width: 308px;
}

.confirm {
  margin-left: auto;
}

.red {
  color: red;

  &:hover {
    cursor: pointer;
  }
}

.owe-tip {
  font-size: 16px;
  background-color: #e4a2a86e;
  color: rgb(255, 65, 81);
  display: flex;
  justify-content: flex-start;
  /* 水平对齐（可选，默认是左对齐） */
  align-items: flex-start;
  width: 45%;
  padding: 10px;
  border-radius: 4px;
}
</style>
