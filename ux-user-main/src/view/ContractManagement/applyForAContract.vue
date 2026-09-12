<template>
  <div class="table-box position-relative">
    <Breadcrumb :router-list="routerList" />
    <div class="breadcrumbTable card">
      <ProTable
        v-if="step"
        type="selection"
        :columns="columns"
        :tableData="tableData"
        :pageData="pageData"
        :IsRefresh="false"
        :get-list="getList"
        ref="proTableRef"
      >
      </ProTable>
      <div v-else class="form">
        <h4 class="title mb42">合同双方信息</h4>
        <el-form class="form-box" :model="firstPartyForm" label-position="top">
          <div class="left">
            <el-form-item label="甲方名称">
              <el-input
                v-model="firstPartyForm.clientName"
                :disabled="userInfo.type == 2"
              />
              <div class="tips">个人请填写真实姓名</div>
            </el-form-item>

            <el-form-item label="联系人">
              <el-input v-model="firstPartyForm.clientContactPerson" />
              <div class="tips">请填写签署人真实姓名，用于合同实名认证</div>
            </el-form-item>
            <el-form-item label="联系电话">
              <el-input v-model="firstPartyForm.clientContactPhone" />
              <div class="tips">请填写签署人真实手机号，用于接收签署短信</div>
            </el-form-item>
            <el-form-item label="地址">
              <el-input
                v-model="firstPartyForm.clientContactAddress"
                show-word-limit
              />
            </el-form-item>
          </div>
          <div class="right">
            <el-form-item label="乙方名称">
              <el-input :disabled="true" v-model="secondPartyForm.clientName" />
              <div class="tips"></div>
            </el-form-item>
            <el-form-item label="联系人">
              <el-input
                :disabled="true"
                v-model="secondPartyForm.clientContactPerson"
              />
              <div class="tips"></div>
            </el-form-item>
            <el-form-item label="联系电话">
              <el-input
                :disabled="true"
                v-model="secondPartyForm.clientContactPhone"
              />
              <div class="tips"></div>
            </el-form-item>
            <el-form-item label="地址">
              <el-input
                :disabled="true"
                v-model="secondPartyForm.clientContactAddress"
              />
            </el-form-item>
          </div>
        </el-form>
      </div>
    </div>
    <div
      class="card mt5 flx-align-center"
      :class="[step ? 'flx-justify-between' : 'flx-justify-end']"
    >
      <el-text v-if="step">
        已选
        <span class="num">{{ selectNum }}</span>
        条
      </el-text>
      <div>
        <el-button
          :disabled="!selectNum && step"
          type="primary"
          plain
          @click="nextStep"
          v-if="!route.query.step"
        >
          {{ step ? "下一步" : "上一步" }}
        </el-button>
        <el-button v-if="!step" type="primary" @click="createContract">
          {{ type == "1" ? "创建电子合同" : "申请纸质合同" }}
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts" name="ApplyForAContract">
import {
  createOrderContractApi,
  getCompanyContractInfoApi,
} from "@/api/contract";
import { getOrderListAPI } from "@/api/order";
import { useTable } from "@/hooks/useTable";
import { useUserInfo } from "@/store";
import { toPage } from "@/utils";
import { enumType } from "@/utils/Enum";
import { ElMessage } from "element-plus";
import { useRoute } from "vue-router";
const route = useRoute();
const routerList = ref([
  { name: "合同管理", path: "/contractManagement" },
  { name: "申请合同" },
]);
const type = ref(route.query.type);
// 步骤
const step = ref(route.query.step !== "1");
const userInfo = useUserInfo();
const { tableData, pageData, getList } = useTable({
  requestApi: getOrderListAPI,
  initParams: { orderStatus: "PAID" },
});

const columns: ColumnProps[] = [
  { prop: "orderNo", label: "订单号" },
  {
    prop: "",
    label: "产品名称",

    value: (row) =>
      row.orderSourceList.map((el: any) => el.productName).join(",") || "--",
  },
  {
    prop: "orderType",
    label: "类型",
    value: (row) => enumType("orderTypeEnum", row.orderType),
    width: 110,
  },
  { prop: "createTime", label: "创建时间", width: 170 },

  { prop: "premiumPrice", label: "应付金额", width: 100 },
  { prop: "finalPayAmount", label: "实付金额", width: 100 },
];

const proTableRef = ref();

const selectNum = computed(() => {
  return proTableRef.value ? proTableRef.value.selectedListIds.length : 0;
});

// 甲方
const firstPartyForm = ref<any>({
  clientName: "",
  clientContactPerson: "",
  clientContactPhone: "",
  clientContactAddress: "",
});
// 乙方
const secondPartyForm = ref({
  clientName: "",
  clientContactPerson: "",
  clientContactPhone: "",
  clientContactAddress: "",
});
const selectIds = ref([]);
const nextStep = () => {
  if (step.value) {
    selectIds.value = proTableRef.value.selectedListIds;
  } else {
    selectIds.value = [];
  }
  step.value = !step.value;
  console.log(selectIds.value);
};
watch(
  () => step.value,
  (val) => {
    //已绑定公司
    if (userInfo.type == 2) {
      firstPartyForm.value.clientName = userInfo.companyName;
      firstPartyForm.value.clientContactPerson = userInfo.companyContactName;
      firstPartyForm.value.clientContactPhone = userInfo.companyContactPhone;
      firstPartyForm.value.clientContactAddress = userInfo.companyAddress;
    }
    if (!val) {
      getCompanyContractInfoApi().then((res) => {
        secondPartyForm.value = res.data;
      });
    }
  },
  { immediate: true }
);
const createContract = () => {
  const data = {
    orderIds: selectIds.value,
    clientName: firstPartyForm.value.clientName,
    clientContactPerson: firstPartyForm.value.clientContactPerson,
    clientContactPhone: firstPartyForm.value.clientContactPhone,
    clientContactAddress: firstPartyForm.value.clientContactAddress,
    type: type.value,
  };
  createOrderContractApi(data).then((res: any) => {
    if (res.code === 200) {
      ElMessage.success("创建合同成功");
      toPage("/contractManagement");
    }
  });
};
</script>
<style lang="scss" scoped>
.tips {
  color: gray;
  height: 16px;
  width: 100%;
  text-align: right;
}

.num {
  color: var(--el-color-primary);
}

.flx-justify-end {
  justify-content: flex-end;
}

.flx-justify-between {
  justify-content: space-between;
}

.form {
  padding: 30px 70px;

  .title {
    font-size: 30px;
  }

  &-box {
    display: flex;
    justify-content: space-between;

    :deep(.el-form-item) {
      .el-form-item__label {
        color: #000;
        font-size: 20px;
      }

      .el-form-item__content {
        .el-input {
          font-size: 16px;
          height: 46px;
        }
      }
    }

    .left,
    .right {
      width: 48%;
      padding: 32px 46px;
      border-radius: 10px;
    }

    .left {
      background: #f7f8fb;
    }

    .right {
      border: 1px solid #e5e5e5;
    }
  }
}
</style>
