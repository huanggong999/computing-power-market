<template>
  <div class="create-instance">
    <div class="flex">
      <Breadcrumb :router-list="routerList"></Breadcrumb>
      <div v-if="userInfo.totalBalance < 0" class="owe-tip ml20">
        当前账户总余额欠费,暂不支持创建实例。请先缴清欠款后再尝试创建实例。
      </div>
    </div>
    <!-- 卡片一 -->
    <div class="card one">
      <TipText class="fwb" content="基础信息" fontSize="20" />
      <ProForm
        ref="firstFormRef"
        v-model="firstForm"
        :formColumns="firstFormCol"
        style="margin-top: 20px"
      >
        <template #region>
          <div class="count-type">
            <div class="choose flx-align-center">
              <el-select class="mr12" width="308px" v-model="firstForm.region">
                <el-option
                  v-for="item in regionList"
                  :key="item.id"
                  :label="item.name"
                  :value="item.regions"
                />
              </el-select>
              <el-button>随机可用区</el-button>
            </div>
            <div class="tips">
              创建成功后不支持更换地域和可用区，不同地域间内网隔离，建议选择距离您业务更近的地域，可以降低网络延时
            </div>
          </div>
        </template>
      </ProForm>
    </div>
    <!-- 卡片二 -->
    <div class="card two mt20">
      <TipText class="fwb" content="实例配置" fontSize="20" />
      <ProForm
        ref="secondFormRef"
        v-model="secondForm"
        :formColumns="secondFormCol"
        style="margin-top: 20px"
      >
        <template #specification="row">
          <div class="specification">
            <!-- todo: 恢复 -->
            <div class="line1 flx-align-center mb20">
              <!-- <el-select placeholder="请选择vCPU"></el-select>
              <el-select placeholder="请选择实例规格"></el-select> -->
            </div>
            <div class="line2 mb28">
              <el-radio-group v-model="secondForm.ecsTypeEnum">
                <el-radio-button
                  v-for="(item, index) in ecsTypeEnum"
                  :key="index"
                  :label="item.value"
                  :value="item.label"
                />
              </el-radio-group>
              GPU型号:<el-input
                v-model="secondTableParam.gpuModel"
                :validate-event="false"
              ></el-input>
              <el-button type="primary" @click="secondTableSearchFn"
                >查询</el-button
              >
              <el-button style="margin-left: 0px" @click="resetSecondTableFn"
                >重置</el-button
              >
            </div>
            <div class="line3 mb20">
              <ProTable
                ref="secondTable"
                :columns="secondTableCol"
                :tableData="secondTableData"
                :search-param="secondTableParam"
                :isPage="false"
                :pageData="pageData"
                :search-fn="secondTableSearch"
                :getList="secondTableGet"
                :IsRefresh="false"
                type="radio"
                :radioData="secondTableSelect"
                style="width: 100%; max-height: 300px; overflow: auto"
                :loading="secondTableLoading"
              >
                <template #ecsType="row">
                  {{
                    ecsTypeEnum.filter(
                      (item) => item.label == row["ecsType"]
                    )[0].value
                  }}
                </template>
                <template #cpuNumber="row"> {{ row.cpuNumber }} vCpu </template>
                <template #price="row">
                  {{
                    firstForm.chargeType === "POSTPAID_BY_HOUR"
                      ? row.hoursPrice + "/小时"
                      : row.monthPrice + "/月"
                  }}
                </template>
              </ProTable>
            </div>
            <div class="line4 flx-align-center mt20">
              当前所选:
              <div v-if="secondTableSelect">
                <span class="blue tag">{{
                  secondTableSelect
                    ? ecsTypeEnum.filter(
                        (item) => item.label == secondTableSelect.ecsType
                      )[0].value
                    : "-"
                }}</span
                ><span class="blue"
                  >{{ secondTableSelect ? secondTableSelect.ecsScale : "-" }}|{{
                    secondTableSelect ? secondTableSelect.cpuNumber : "-"
                  }}
                  vCPU|{{
                    secondTableSelect ? secondTableSelect.memorySize : "-"
                  }}GB</span
                >
              </div>
            </div>
          </div>
        </template>
        <template #visibility="row">
          <div class="visibility mb40">
            <div class="line1">
              <el-radio-group v-model="secondForm.visibility">
                <el-radio-button label="公共镜像" value="public" />
                <el-radio-button label="社区镜像" value="shared" />
                <el-radio-button label="自定义镜像" value="private" />
              </el-radio-group>
              <el-table
                v-if="secondForm.visibility === 'shared'"
                :data="sharedImgTableData"
                style="width: 1000px"
                :cell-style="{
                  textAlign: 'center',
                  fontSize: '13px',
                }"
                :header-cell-style="{
                  textAlign: 'center',
                  fontSize: '13px',
                }"
                border
                class="mt28"
              >
                <el-table-column type="selection" width="55" />
                <el-table-column property="name" label="名称/ID" />
                <el-table-column label="容量">
                  <template #default="scope">{{ scope.row.date }}</template>
                </el-table-column>
                <el-table-column property="name" label="操作系统" />
                <el-table-column property="address" label="操作时间" />
              </el-table>
            </div>
            <div class="line2 flx-align-center mt20">
              <el-select
                placeholder="请选择镜像"
                v-model="secondForm.platform"
                class="mr20"
              >
                <el-option
                  v-for="item in platformList"
                  :key="item"
                  :label="item"
                  :value="item"
                ></el-option>
              </el-select>
              <el-select
                placeholder="请选择镜像版本"
                v-model="secondForm.platformVersion"
                value-key="osName"
                class="mr20"
              >
                <el-option
                  v-for="item in platformVersionList"
                  :key="item.osName"
                  :label="item.osName"
                  :value="item"
                ></el-option>
              </el-select>
              <div class="flex-align-center">
                <el-checkbox
                  v-model="secondForm.isAgent"
                  label="启动时安装批量作业Agent"
                  size="large"
                />
                <!-- <el-tooltip
                  class="box-item"
                  effect="dark"
                  content="Left prompts info"
                  placement="top-start"
                >
                  <el-icon><QuestionFilled /></el-icon>
                </el-tooltip> -->
              </div>
            </div>
          </div>
        </template>
        <template #storage="row">
          <div class="storage mb40">
            <div class="line1">
              <el-table
                :data="diskData"
                style="width: 100%"
                :cell-style="{
                  textAlign: 'center',
                  fontSize: '13px',
                }"
                :header-cell-style="{
                  textAlign: 'center',
                  fontSize: '13px',
                }"
                border
              >
                <el-table-column label="用途">
                  <template #default="scope">{{ scope.row.useWay }}</template>
                </el-table-column>
                <el-table-column label="类型" width="200">
                  <template #default="scope">{{
                    scope.row.volumeType
                  }}</template>
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
                      scope.row.size
                        ? scope.row.size
                        : scope.row.volumeCapacity
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
        <template #ip="row">
          <div class="ip flx-align-center">
            <el-tooltip
              class="box-item"
              effect="dark"
              content="Left prompts info"
              placement="top-start"
            >
              <el-icon>
                <QuestionFilled />
              </el-icon>
            </el-tooltip>
            <div class="flx-align-center ml43 mr17">
              弹性公网IP
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
            <!-- <el-radio-group v-model="secondForm.isAgent">
              <el-radio :value="ipshare"
                ><span style="color: #000; font-size: 16px; font-weight: 400"
                  >分配弹性公网IP</span
                ></el-radio
              >
            </el-radio-group> -->
            <el-checkbox
              v-model="secondForm.isElastic"
              label="分配弹性公网IP"
              size="large"
            />
          </div>
        </template>
      </ProForm>
    </div>
    <div class="card three mt20">
      <TipText class="fwb" content="登陆方式" fontSize="20" />
      <ProForm
        ref="thirdFormRef"
        v-model="thirdForm"
        :formColumns="thirdFormCol"
        style="margin-top: 20px"
      >
        <template #userName>
          <el-input disabled :value="thirdForm.userName"></el-input>
        </template>
      </ProForm>
    </div>
    <div class="card four mt20">
      <TipText class="fwb" content="实例配置" fontSize="20" />
      <ProForm
        ref="fourthFormRef"
        v-model="fourthForm"
        :formColumns="fourthFormCol"
        style="margin-top: 20px"
      >
        <template #instanceName>
          <div class="flx-align-center" style="gap: 10px; width: 100%">
            <el-input
              style="flex-grow: 1"
              placeholder="请输入实例名称"
              v-model="fourthForm.instanceName"
            ></el-input>
            <el-button @click="fourthForm.instanceName = generateRandomText()"
              >随机生成</el-button
            >
          </div>
        </template>
      </ProForm>
    </div>
    <div class="card five mt20 flx-align-center">
      <div
        class="time flx-align-center mr60"
        v-if="firstForm.chargeType === 'POSTPAID_BY_MONTH'"
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
      <div class="label">实例数量</div>
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
      <!-- productType==2 -->
      <div class="flx-align-center" v-if="productType == 2">
        <div class="label">带宽(M)</div>
        <div class="control flx-align-center ml32 mr60">
          <div
            class="reduce flx-center same"
            @click="bandwidth > 1 ? bandwidth-- : ''"
          >
            -
          </div>
          <div class="num">{{ bandwidth }}M</div>
          <div class="plus flx-center same" @click="bandwidth++">+</div>
        </div>
      </div>
      <div class="flx-align-center mr64 footer-fix">
        <div class="label">服务器费用</div>
        <div class="price ml14">
          ￥{{ (footerPrice.allocationPrice * instanceNum).toFixed(2) }}/{{
            firstForm.chargeType === "POSTPAID_BY_HOUR"
              ? "时"
              : duration.result.durationUnit === "YEAR"
              ? `年`
              : "月"
          }}
        </div>
      </div>
      <div class="flx-align-center mr64" v-if="productType == 1">
        <div class="label">公网流量费</div>
        <div class="price ml14">￥{{ footerPrice.ipPrice }}/GB</div>
      </div>
      <div class="flx-align-center mr64" v-else-if="productType == 2">
        <div class="label">公网流量费</div>
        <div class="price ml14">
          ￥{{ footerPrice.ipPrice }}/{{
            firstForm.chargeType === "POSTPAID_BY_HOUR"
              ? "时"
              : duration.result.durationUnit === "YEAR"
              ? `年`
              : "月"
          }}
        </div>
      </div>
      <div class="flx-align-center mr64 footer-fix">
        <div class="label">配置费用</div>
        <div class="price ml14">
          ￥{{ (footerPrice.parts * instanceNum).toFixed(2) }}/{{
            firstForm.chargeType === "POSTPAID_BY_HOUR"
              ? "时"
              : duration.result.durationUnit === "YEAR"
              ? `年`
              : "月"
          }}
        </div>
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
  </div>
</template>

<script setup lang="ts" name="Instance">
import { computed, onMounted, ref } from "vue";
import { generateUUID, toPage, generateRandomText } from "@/utils";
import {
  getEcsListApi,
  getRegionListApi,
  getEcsImgListApi,
  buildOrderInfoApi,
} from "@/api/order";
import { ISourceRegion } from "@/api/types/order";
import { useTable } from "@/hooks/useTable";
import { getDataConfigApi, getEnvConfigApi } from "@/api/env";
import { useOrder } from "@/store/modules/order";
import { IBuildOrderParams } from "@/api/types/order";
import { checkPassword } from "@/utils/password";
import { ElMessage } from "element-plus";
import { useRoute } from "vue-router";
import { useUserInfo } from "@/store";
import { debounce } from "lodash";

const orderDetail = useOrder();
const router = useRoute();
const routerList = ref([
  { name: "实例", path: "/cloud/instance" },
  { name: "创建实例", path: "" },
]);
const userInfo = useUserInfo();
let ecsId = router.query.ecsId;
let productType = router.query.productType ? router.query.productType : "1";
// 卡片一
let firstFormRef = ref();
const firstForm = ref<any>({
  chargeType: "POSTPAID_BY_HOUR",
  region: router.query.regionsZones ? router.query.regionsZones : "CN_BEIJING",
});
const firstFormCol: IFormColumnsProps[] = [
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
    label: "区域选择",
    prop: "region",
    el: "slot",
    required: true,
    tips: "创建成功后不支持更换地域和可用区，不同地域间内网隔离，建议选择距离您业务更近的地域，可以降低网络延时",
  },
];
const regionList = ref<ISourceRegion[]>([]);

// 卡片二
const secondTableLoading = ref(true);
let secondFormRef = ref();
const secondForm = ref({
  ecsTypeEnum: "GPU",
  isAgent: true,
  isElastic: true,
  visibility: "public",
  platform: "",
  platformVersion: { osType: "" },
});
const secondFormCol: IFormColumnsProps[] = [
  {
    label: "计算规格",
    prop: "specification",
    el: "slot",
  },
  {
    label: "镜像",
    prop: "visibility",
    el: "slot",
    required: true,
  },
  {
    label: "存储",
    prop: "storage",
    el: "slot",
    required: true,
  },
  {
    label: "公网ip",
    prop: "ip",
    el: "slot",
    required: true,
  },
];
const secondTable = ref();
const ecsTypeEnum = [
  { label: "GPU", value: "GPU" },
  { label: "GENERAL_COMPUTE", value: "通用型计算" },
  { label: "COMPUTE", value: "计算型" },
  { label: "GENERAL", value: "通用型" },
];

const secondTableCol: ColumnProps[] = [
  { label: "规格组", prop: "ecsType", slot: true },
  {
    label: "GPU型号",
    prop: "gpuModel",
  },
  {
    label: "GPU显存(GB)",
    prop: "gpuMemory",
  },
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
// 获取ecs列表

const {
  getList: secondTableGet,
  tableData: secondTableData,
  searchFn: secondTableSearch,
  searchParam: secondTableParam,
  pageData,
} = useTable({
  requestApi: getEcsListApi,
  requestAuto: false,
});
pageData.value = {
  pageNo: 1,
  pageSize: 100,
  total: 1,
};
let abortController: any = null;
// 同时监听地域和计算机类型,有任何一项改变都重新调用ecs列表查询
watch(
  () => firstForm.value.region,
  () => {
    // 取消上一次请求
    if (abortController) {
      console.log("取消了上次的请求");
      abortController.abort();
    }
    abortController = new AbortController();
    secondTableParam.value = {
      sourceRegions: firstForm.value.region,
      ecsTypeEnum: secondForm.value.ecsTypeEnum,
      ecsId: ecsId ? ecsId : null,
      productType: productType ? productType : 1,
    };
    secondTableLoading.value = true;

    secondTableGet(undefined, abortController.signal).then(() => {
      if (!abortController.signal.aborted) {
        secondTableLoading.value = false;
        if (ecsId) {
          secondTable.value.radio =
            secondTableData.value.find((item) => item.id == ecsId) || null;
          // 清空ecsId
          ecsId = null;
        }
        secondTableData.value.forEach((item) => {
          item.sku = true;
          if (!item.zoneList && productType == "1") {
            item.disabledRadio = true;
          }
        });
      }
    });
  },
  { immediate: true, deep: true } // 立即执行并深度监听
);

watch(
  () => secondForm.value.ecsTypeEnum,
  () => {
    // 取消上一次请求
    if (abortController) {
      console.log("取消了上次的请求");
      abortController.abort();
    }
    abortController = new AbortController();
    secondTableParam.value = {
      sourceRegions: firstForm.value.region,
      ecsTypeEnum: secondForm.value.ecsTypeEnum,
      ecsId: ecsId ? ecsId : null,
      productType: productType ? productType : 1,
    };
    secondTableLoading.value = true;
    secondTableGet(undefined, abortController.signal).then(() => {
      if (!abortController.signal.aborted) {
        secondTableLoading.value = false;
        secondTableData.value.forEach((item) => {
          item.sku = true;
          if (!item.zoneList && productType == "1") {
            item.disabledRadio = true;
          }
        });
      } else {
        console.log("请求被取消了");
      }
    });
  }
);
// 搜索方法(也要拦截上一次请求)
const secondTableSearchFn = () => {
  // 取消上一次请求
  if (abortController) {
    console.log("取消了上次的请求");
    abortController.abort();
  }
  abortController = new AbortController();

  // 更新查询参数
  secondTableParam.value = {
    sourceRegions: firstForm.value.region,
    ecsTypeEnum: secondForm.value.ecsTypeEnum,
    gpuModel: secondTableParam.value.gpuModel,
    ecsId: ecsId ? ecsId : null,
    productType: productType ? productType : 1,
  };

  // 设置加载状态
  secondTableLoading.value = true;

  // 发起新请求
  secondTableGet(undefined, abortController.signal).then(() => {
    if (!abortController.signal.aborted) {
      secondTableLoading.value = false;
      secondTableData.value.forEach((item) => {
        item.sku = true;
        if (!item.zoneList && productType == "1") {
          item.disabledRadio = true;
        }
      });
    } else {
      console.log("请求被取消了");
    }
  });
};
const resetSecondTableFn = () => {
  if (abortController) {
    console.log("取消了上次的请求");
    abortController.abort();
  }
  abortController = new AbortController();

  // 更新查询参数
  secondTableParam.value = {
    sourceRegions: firstForm.value.region,
    ecsTypeEnum: secondForm.value.ecsTypeEnum,
    ecsId: ecsId ? ecsId : null,
    productType: productType ? productType : 1,
  };

  // 设置加载状态
  secondTableLoading.value = true;

  // 发起新请求
  secondTableGet(undefined, abortController.signal).then(() => {
    if (!abortController.signal.aborted) {
      secondTableLoading.value = false;
      secondTableData.value.forEach((item) => {
        item.sku = true;
        if (!item.zoneList && productType == "1") {
          item.disabledRadio = true;
        }
      });
    } else {
      console.log("请求被取消了");
    }
  });
};
// 获取当前所选ecs
const secondTableSelect = computed(() => {
  return secondTable.value ? secondTable.value.radio : null;
});
//todo: 变更所选实例时要更新镜像的选择
// 获取镜像数据
const platformList = [
  "CentOS",
  "Debian",
  "veLinux",
  "Windows Server",
  "Fedora",
  "OpenSUSE",
  "Ubuntu",
];
// 镜像版本数据
const platformVersionList = ref<any>([]);
// 镜像列表
const sharedImgTableData = ref<any>([]);
// 根据镜像数据获取可选的镜像版本·
watch(
  () => [
    secondTableSelect.value,
    secondForm.value.visibility,
    secondForm.value.platform,
  ],
  () => {
    // 判断有效性
    getEcsImgListApi({
      visibility: secondForm.value.visibility,
      instanceTypeId:
        productType == "2" ? null : secondTableSelect.value.ecsScale,
      platform: secondForm.value.platform,
      region: firstForm.value.region,
    }).then((res) => {
      // 赋值镜像列表，重置镜像版本值
      platformVersionList.value = res.data.images.map((item: any) => item);
      secondForm.value.platformVersion = { osType: "" };
    });
  }
);

// 基本信息(系统云盘)
const diskConfig = ref<any>({
  iops: 2280,
  size: 40,
  hoursPrice: 0.0021,
  monthPrice: 3,
  throughput: 110,
  volumeType: "ESSD_FlexPL",
  oneYearPrice: 9.96,
  twoYearPrice: 16.8,
  threeYearPrice: 19.8,
  performanceLevel: "FlexPL(单盘IOPS基准性能上限5万)",
});
// (云存储)
// todo: 有可能是数组（多个可以选的存储盘）
const cloudDataConfig = ref<any>({});
const cloudLimit = ref(8);
// 当前uuid
const uuid = ref("");
// 数据盘表格数据
const diskData = ref<any[]>([]);
// 当前配置订单信息
let targetOrderDetail: any = {};
// 构建订单信息
const buildOrder = () => {
  const data: IBuildOrderParams = {
    orderType: "NEW_RESOURCE",
    orderSource: [
      {
        uid: uuid.value,
        regionsId: firstForm.value.region,
        sourceType: "ECS",
        productType: productType ? productType : 1,
        chargeType:
          firstForm.value.chargeType === "POSTPAID_BY_MONTH"
            ? duration.value.result.durationUnit === "YEAR"
              ? "POSTPAID_BY_YEAR"
              : "POSTPAID_BY_MONTH"
            : "POSTPAID_BY_HOUR",
        duration:
          firstForm.value.chargeType === "POSTPAID_BY_MONTH"
            ? duration.value.result.num
            : null,
        durationUnit:
          firstForm.value.chargeType === "POSTPAID_BY_MONTH"
            ? duration.value.result.durationUnit
            : null,
        configDetail: {
          ...secondTableSelect.value,
        },
      },
    ],
  };
  // 拥有存储盘的情况
  if (diskData.value.length > 1) {
    diskData.value.forEach((item, index) => {
      if (index > 0) {
        const param = {
          uid: uuid.value,
          regionsId: firstForm.value.region,
          productType: productType ? productType : 1,
          sourceType: "CLOUD_STORAGE",
          configDetail: item,
          chargeType:
            firstForm.value.chargeType === "POSTPAID_BY_MONTH"
              ? duration.value.result.durationUnit === "YEAR"
                ? "POSTPAID_BY_YEAR"
                : "POSTPAID_BY_MONTH"
              : "POSTPAID_BY_HOUR",
          duration:
            firstForm.value.chargeType === "POSTPAID_BY_MONTH"
              ? duration.value.result.num
              : null,
          durationUnit:
            firstForm.value.chargeType === "POSTPAID_BY_MONTH"
              ? duration.value.result.durationUnit
              : null,
        };
        data.orderSource?.push(param);
      }
    });
  }
  if (productType == "2") {
    data.bandwidth = bandwidth.value;
  }
  buildOrderInfoApi(data).then((res: any) => {
    // 保存当前订单信息
    targetOrderDetail = res.data;
    // 计算底部价格
    if (firstForm.value.chargeType === "POSTPAID_BY_HOUR") {
      // footerPrice.value.allocationPrice =
      footerPrice.value.allocationPrice =
        res.data.orderSourceList[0].userDiscountAmount == 0
          ? res.data.orderSourceList[0].premiumPrice
          : res.data.orderSourceList[0].userDiscountAmount;
      // res.data.orderSourceList[0].userDiscountAmount ??
      // res.data.orderSourceList[0].premiumPrice;
      //   Number(res.data.orderSourceList[0].premiumPrice) -
      //   Number(res.data.orderSourceList[0].userDiscountAmount)
    }
    if (
      firstForm.value.chargeType === "POSTPAID_BY_MONTH" &&
      duration.value.result.durationUnit === "MONTH"
    ) {
      footerPrice.value.allocationPrice =
        Number(res.data.orderSourceList[0].finalUnitPrice) /
        Number(res.data.orderSourceList[0].duration);
    }
    if (
      firstForm.value.chargeType === "POSTPAID_BY_MONTH" &&
      duration.value.result.durationUnit === "YEAR"
    ) {
      footerPrice.value.allocationPrice =
        Number(res.data.orderSourceList[0].finalUnitPrice) /
        Number(res.data.orderSourceList[0].duration);
    }
    // 底部价格计算公网流量费及其他费用的组合
    footerPrice.value.ipPrice = res.data.orderSourceList[2].premiumPrice;
    footerPrice.value.parts = 0;
    res.data.orderSourceList.forEach((item, index) => {
      if (index == 0 || index == 2) {
        return;
      } else {
        // 累加所有盘价格
        footerPrice.value.parts += Number(item.premiumPrice);
      }
    });
    if (firstForm.value.chargeType === "POSTPAID_BY_MONTH") {
      footerPrice.value.parts /= Number(res.data.orderSourceList[0].duration);
    }
    // 数据处理
    footerPrice.value.allocationPrice =
      footerPrice.value.allocationPrice.toFixed(2);
    footerPrice.value.ipPrice = footerPrice.value.ipPrice.toFixed(2);
    footerPrice.value.parts = parseFloat(footerPrice.value.parts.toFixed(2));
  });
};

// ecs表格选中后，清空表盘旧数据并构建订单信息
watch(
  () => secondTableSelect.value,
  () => {
    // 重置盘表数据
    diskData.value.length = 0;
    cloudLimit.value = 8;
    diskData.value.push({
      useWay: "系统盘",
      ...diskConfig.value,
    });
    // if (newVal.osType === 'Windows') {
    //   thirdForm.value.userName = 'administrator'
    // } else {
    //   thirdForm.value.userName = 'root'
    // }
    // 构建订单
    buildOrder();
  }
);
// 监听计费类型，如果计费类型变更时已有选中的实例ecs，则重新构建订单
watch(
  () => firstForm.value.chargeType,
  () => {
    if (secondTableSelect.value) {
      buildOrder();
    }
  }
);

// 根据所选镜像版本对实例用户名进行赋值
watch(
  () => secondForm.value.platformVersion,
  (newVal) => {
    if (newVal.osType === "Windows") {
      thirdForm.value.userName = "administrator";
    } else {
      thirdForm.value.userName = "root";
    }
  }
);
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
// 卡片三
let thirdFormRef = ref();
const thirdForm = ref({ userName: "root", password: "", rePassword: "" });
const thirdFormCol: IFormColumnsProps[] = [
  {
    label: "登陆名",
    prop: "userName",
    el: "slot",
    required: true,
  },
  {
    label: "登录密码",
    prop: "password",
    el: "password",
    required: true,
  },
  {
    label: "确认密码",
    prop: "rePassword",
    el: "password",
    required: true,
    tips: "密码必须至少8个字符，包含大写字母、小写字母、数字和特殊符号!",
  },
];

// 卡片四
let fourthFormRef = ref();
const fourthForm = ref({ instanceName: "" });
const fourthFormCol: IFormColumnsProps[] = [
  {
    label: "实例名称",
    prop: "instanceName",
    el: "slot",
    required: true,
  },
  {
    label: "主机名",
    prop: "hostName",
    el: "input",
    required: false,
  },
  {
    label: "实例描述",
    prop: "instanceDescription",
    el: "input",
    required: false,
  },
];
// 底部数据
const instanceNum = ref(1);
// 只有自建服务器才会有
const bandwidth = ref(1);
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
const footerPrice = ref<any>({
  allocationPrice: 0,
  parts: 0,
  ipPrice: 0,
});
watch(
  () => duration.value.result,
  () => {
    if (secondTableSelect.value) {
      buildOrder();
    }
  }
);
watch(
  () => instanceNum.value,
  () => {
    if (secondTableSelect.value) {
      buildOrder();
    }
  }
);
// 监听带宽个数
watch(
  () => bandwidth.value,
  () => {
    if (secondTableSelect.value) {
      buildOrder();
    }
  }
);
// 页面启动请求
getRegionListApi().then((res: any) => {
  regionList.value = res.data;
  if (!firstForm.value.region) {
    firstForm.value.region = regionList.value[0].regions;
  }
});

const initFunc = async () => {
  // 获取可用地区

  // 获取云盘基础配置
  getEnvConfigApi("ECS_SYSTEM_VOLUME").then((res: any) => {
    diskConfig.value = res.data.configValue;
  });
  // 获取数据盘基础配置
  getDataConfigApi(firstForm.value.region).then((res) => {
    cloudDataConfig.value = res.data.list[0];
  });

  uuid.value = generateUUID();
};
onMounted(() => {
  initFunc();
});

// 跳转付款页面
const toPay = debounce(
  () => {
    if (!secondForm.value.platformVersion.osType) {
      ElMessage.error("请先完善服务器相关配置信息(镜像版本)！");
      return;
    }
    if (!checkPassword(thirdForm.value.password)) {
      ElMessage.error(
        "密码必须至少8个字符，包含大写字母、小写字母、数字和特殊符号!"
      );
      return;
    }
    if (thirdForm.value.password !== thirdForm.value.rePassword) {
      ElMessage.error("两次密码不一致,请检查");
      return;
    }
    if (fourthForm.value.instanceName) {
      const regex = /^[A-Za-z\u4e00-\u9fa5]/;
      if (!regex.test(fourthForm.value.instanceName)) {
        ElMessage.warning("实例名称需要以数字或字母开头！");
        return;
      }
    }
    diskData.value.forEach((item) => {
      item.uid = uuid.value;
    });
    // 存储服务器信息
    const detail = {
      ...firstForm.value,
      ...secondForm.value,
      ...thirdForm.value,
      ...fourthForm.value,
      chargeType:
        firstForm.value.chargeType === "POSTPAID_BY_MONTH"
          ? duration.value.result.durationUnit === "YEAR"
            ? "POSTPAID_BY_YEAR"
            : "POSTPAID_BY_MONTH"
          : "POSTPAID_BY_HOUR",
      instance: secondTableSelect.value,
      disk: diskData.value,
      instanceNum: instanceNum.value,
      bandwidth: bandwidth.value,
      duration: duration.value.result,
      productType: productType,
      zoneId:
        productType == "1"
          ? targetOrderDetail.orderSourceList[0].configDetail.zoneList[0].id
          : "cn-beijing-a",
      targetOrderDetail: targetOrderDetail,
    };
    orderDetail.setOrderDetail(detail);
    // 跳转
    toPage("/cloud/instancePay");
  },
  1000,
  { leading: true, trailing: false }
);
</script>
<style lang="scss" scoped>
@import "./index.scss";
</style>
