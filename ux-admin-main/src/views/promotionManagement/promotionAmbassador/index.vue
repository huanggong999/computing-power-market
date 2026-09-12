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
      type="none"
    >
      <template #tableHeader>
        <div class="flx-align-center">
          <el-radio-group class="mt10 mb10" v-model="initStatus">
            <el-radio-button
              v-for="item in radioGroup"
              :key="item.label"
              :value="item.value"
            >
              {{ item.label }}
            </el-radio-button>
          </el-radio-group>
          <div class="ml50 flx-align-center">
            <div class="mr30">默认佣金比例</div>
            <div class="mr30">一级佣金比例：{{ dataForm.firstScale }}%</div>
            <div class="mr30">二级佣金比例：{{ dataForm.twoScale }}%</div>
            <el-button
              link
              type="primary"
              class="mt15"
              @click="configVisible = true"
            >
              修改佣金比例
              <el-icon class="el-icon--right"><ArrowRight /></el-icon>
            </el-button>
          </div>
        </div>
      </template>
      <template #userId="row">
        <div>{{ row.userId }}</div>
        <div>{{ row.shareKey }}</div>
      </template>
      <template #userName="row">
        <div>{{ row.userName }}</div>
        <div>{{ row.userPhone }}</div>
      </template>
      <template #name="row">
        <div class="flex"><span>真实姓名：</span>{{ row.name ?? "--" }}</div>
        <div class="flex"><span>联系方式：</span>{{ row.phone ?? "--" }}</div>
      </template>
      <template #firstScale="row">
        <div>一级：{{ row.firstScale }}%</div>
        <div>二级：{{ row.twoScale }}%</div>
      </template>
      <template #parentName="row">
        <div v-if="row.parentUserId != 0">
          <div class="flex">
            <span>真实姓名：</span>{{ row.parentName ?? "--" }}
          </div>
          <div class="flex">
            <span>联系方式：</span>{{ row.parentPhone ?? "--" }}
          </div>
        </div>
        <span v-else> 暂无上级 </span>
      </template>

      <template #firstCount="row">
        <div>
          <el-button
            type="primary"
            link
            @click="openDistributor(1, row.userId)"
          >
            一级：{{ row.firstCount }}
          </el-button>
        </div>
        <el-button type="primary" link @click="openDistributor(2, row.userId)">
          二级：{{ row.twoCount }}
        </el-button>
      </template>

      <template #status="row">
        <div class="flx-center">
          <el-tag :type="enumTag('statusAuditTag', row.status)">
            {{ enumType("statusAuditEnum", row.status) }}
          </el-tag>
          <el-tooltip
            v-if="row.status === 3"
            :content="row.verifyRemark"
            placement="bottom"
          >
            <el-icon class="ml5"><QuestionFilled /></el-icon>
          </el-tooltip>
        </div>
      </template>

      <template #operation="row">
        <div v-if="row.status === 1">
          <el-button type="primary" link @click="handleAudit(row.id, 2)">
            通过
          </el-button>
          <el-button type="primary" link @click="handleAudit(row.id, 3)">
            驳回
          </el-button>
        </div>
        <el-button
          type="primary"
          v-if="row.status !== 1"
          link
          @click="modifyFn(row)"
        >
          修改佣金比例
        </el-button>
        <el-button
          type="primary"
          v-if="row.status !== 1 && row.parentUserId == 0"
          link
          @click="openSetParent(row.userId)"
        >
          设置上级
        </el-button>
        <el-button
          type="primary"
          v-if="row.status !== 1 && row.parentUserId != 0"
          link
          @click="openSetParent(row.userId)"
        >
          修改上级
        </el-button>
      </template>
    </ProTable>

    <el-dialog v-model="setParentVisible" title="设置上级" center width="20%">
      <ProForm
        class="mt10"
        v-model="setParentForm"
        :formColumns="setParentFormColumns"
      />
      <template #footer>
        <el-button type="primary" @click="setParentFn"> 确 定 </el-button>
      </template>
    </el-dialog>
    <el-dialog v-model="auditVisible" title="审核驳回" center>
      <ProForm class="mt10" v-model="auditForm" :formColumns="formColumns" />
      <template #footer>
        <el-button type="primary" @click="rejectFn"> 确认驳回 </el-button>
      </template>
    </el-dialog>
    <el-dialog
      v-model="configVisible"
      width="20%"
      title="修改默认佣金比例"
      center
    >
      <ProForm
        class="mt10"
        v-model="dataForm"
        :formColumns="commissionRateFormColumns"
        :label-width="120"
      >
        <template #firstScale>
          <el-input-number
            v-model="dataForm.firstScale"
            :min="1"
            :max="100"
            :step="10"
            :precision="2"
            placeholder="请输入"
          >
            <template #suffix>
              <span>%</span>
            </template>
          </el-input-number>
        </template>
        <template #twoScale>
          <el-input-number
            v-model="dataForm.twoScale"
            :min="1"
            :max="100"
            :step="10"
            :precision="2"
            placeholder="请输入"
          >
            <template #suffix>
              <span>%</span>
            </template>
          </el-input-number>
        </template>
      </ProForm>

      <template #footer>
        <el-button type="primary" @click="submit"> 确定 </el-button>
      </template>
    </el-dialog>
    <el-dialog
      v-model="commissionRateVisible"
      width="20%"
      title="修改佣金比例"
      center
    >
      <ProForm
        class="mt10"
        v-model="auditForm"
        :formColumns="commissionRateFormColumns"
        :label-width="120"
      >
        <template #firstScale>
          <el-input-number
            v-model="commissionRateForm.firstScale"
            :min="1"
            :max="100"
            :step="10"
            :precision="2"
            placeholder="请输入"
          >
            <template #suffix>
              <span>%</span>
            </template>
          </el-input-number>
        </template>
        <template #twoScale>
          <el-input-number
            v-model="commissionRateForm.twoScale"
            :min="1"
            :max="100"
            :step="10"
            :precision="2"
            placeholder="请输入"
          >
            <template #suffix>
              <span>%</span>
            </template>
          </el-input-number>
        </template>
      </ProForm>

      <template #footer>
        <el-button type="primary" @click="modifyCommissionRateFn">
          确定
        </el-button>
      </template>
    </el-dialog>

    <Distributor
      v-model="distributorVisible"
      :type="distributorType"
      :userID="distributorUserId"
    />
  </div>
</template>

<script setup lang="ts" name="PromotionAmbassador">
import {
  promotionAmbassadorPageApi,
  promotionAmbassadorRelevanceApi,
  promotionAmbassadorUpdateScaleApi,
  promotionAmbassadorVerifyApi,
  promotionConfigurationDetailApi,
  promotionConfigurationUpdateApi,
} from "@/api/promotionManagement";
import { useTable } from "@/hooks/useTable";
import { enumType } from "@/utils/Enum";
import Distributor from "./components/Distributor.vue";
import { enumTag } from "@/utils/enumTag";

type TInitStatus = 1 | 2 | 3 | "";

const initStatus = ref<TInitStatus>("");

const radioGroup = [
  { label: "全部", value: "" },
  { label: "审核中", value: 1 },
  { label: "通过", value: 2 },
  { label: "不通过", value: 3 },
];
watch(initStatus, (val) => {
  searchParam.value.status = val;
  getList();
});

const {
  tableData,
  pageData,
  searchParam,
  refreshFn,
  getList,
  searchFn,
  resetFn,
} = useTable({ api: promotionAmbassadorPageApi });

const columns: ColumnProps[] = [
  { prop: "userId", label: "用户ID", slot: true, width: 180 },
  { prop: "userName", label: "昵称信息", slot: true, width: 180 },
  //
  { prop: "name", label: "真实信息", slot: true, width: 200 },

  // { prop: "name", label: "真实姓名", width: 100 },
  // { prop: "phone", label: "联系方式", width: 133 },
  { prop: "name", label: "真实姓名", search: { el: "input" }, noShow: true },
  { prop: "phone", label: "联系方式", search: { el: "input" }, noShow: true },
  {
    prop: "firstScale",
    label: "佣金比例",
    value: (row: any) => row.firstScale + "%",
    width: 120,
    slot: true,
  },
  {
    prop: "parentName",
    label: "上级",
    value: (row: any) => row.firstScale + "%",
    width: 200,
    slot: true,
  },

  { prop: "firstCount", label: "分销用户", slot: true, width: 100 },
  { prop: "orderCount", label: "分销订单", width: 90 },
  { prop: "totalCommission", label: "累计分销佣金(元)", width: 150 },
  { prop: "status", label: "申请状态", width: 90, slot: true },
  {
    prop: "updateTime",
    label: "提交时间",
    search: {
      el: "date-picker",
      dateType: "daterange",
      dateEnum: ["startTime", "endTime"],
      valueFormat: "YYYY-MM-DD",
    },
    width: 180,
  },
  { prop: "operation", label: "操作", slot: true, fixed: "right", width: 200 },
];

type THandleAudit = 2 | 3;

const auditForm = ref<TKeyValue>({ id: "", status: "" });

const handleAudit = (id: string, type: THandleAudit) => {
  auditForm.value.id = id;
  auditForm.value.status = type;
  type === 3 ? (auditVisible.value = true) : auditFn();
};

const auditVisible = ref(false);
const formColumns: IFormColumnsProps[] = [
  { label: "审核意见", prop: "verifyRemark", el: "textarea" },
];

const auditFn = async () => {
  await promotionAmbassadorVerifyApi(auditForm.value);
  ElMessage.success("操作成功");
  getList();
};

watch(
  () => auditVisible.value,
  (val) => {
    if (!val) auditForm.value = {};
  }
);

const rejectFn = () => {
  if (!auditForm.value.verifyRemark) {
    ElMessage.error("请输入审核意见");
    return;
  }
  auditFn();
  auditVisible.value = false;
};

// 修改佣金比例
const commissionRateVisible = ref(false);
const commissionRateForm = ref<TKeyValue>({
  id: "",
  firstScale: "",
  twoScale: "",
});

const modifyFn = ({ id, firstScale, twoScale }: any) => {
  commissionRateForm.value.id = id;
  commissionRateForm.value.firstScale = firstScale;
  commissionRateForm.value.twoScale = twoScale;
  commissionRateVisible.value = true;
};

const commissionRateFormColumns: IFormColumnsProps[] = [
  { label: "一级佣金比例", prop: "firstScale", el: "slot" },
  { label: "二级佣金比例", prop: "twoScale", el: "slot" },
];
const modifyCommissionRateFn = async () => {
  await promotionAmbassadorUpdateScaleApi(commissionRateForm.value);
  ElMessage.success("操作成功");
  getList();
  commissionRateVisible.value = false;
};

const dataForm = ref<TKeyValue>({});

const configVisible = ref(false);
const getConfig = async () => {
  const { data } = await promotionConfigurationDetailApi();
  dataForm.value = data;
};
const submit = async () => {
  await promotionConfigurationUpdateApi(dataForm.value);
  ElMessage.success("保存成功");
  getConfig();
  configVisible.value = false;
};
onMounted(() => getConfig());

// 分销用户
const distributorVisible = ref(false);
const distributorUserId = ref("");
const distributorType = ref<1 | 2>(1);
const openDistributor = (type: 1 | 2, userId: string) => {
  distributorType.value = type;
  distributorUserId.value = userId;
  distributorVisible.value = true;
};

// 设置上级  setParent

const setParentVisible = ref(false);

// 设置上级参数
const setParentForm = ref<TKeyValue>({});

const setParentFormColumns: IFormColumnsProps[] = [
  {
    label: "选择上级",
    prop: "parenId",
    el: "selectPage",
    selectLabel: "userName",
    selectValue: "id",
    pageFn: promotionAmbassadorPageApi,
  },
];
const openSetParent = (nextId: string) => {
  setParentForm.value.nextId = nextId;
  setParentVisible.value = true;
};
const setParentFn = async () => {
  await promotionAmbassadorRelevanceApi(setParentForm.value);
  ElMessage.success("操作成功");
  setParentVisible.value = false;
  getList();
};
watch(
  () => setParentVisible.value,
  (val) => {
    if (!val) setParentForm.value = {};
  }
);
</script>
<style lang="scss" scoped>
.flex {
  display: flex;
}
</style>
