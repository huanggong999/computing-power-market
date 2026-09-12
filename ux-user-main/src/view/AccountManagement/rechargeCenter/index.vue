/** 充值中心 */
<template>
  <div class="position-relative card recharge">
    <TipText content="充值金额" />
    <div class="list ml20 mt28 flex">
      <div
        class="list-item cup mr30"
        :class="{ active: item === flag }"
        v-for="item in priceList"
        :key="item"
        @click="changeFlag(item)"
      >
        <span>￥</span>{{ item }}
      </div>
    </div>
    <p class="mt20 mb12 ml20">自定义充值金额</p>
    <el-input
      class="ml20 mb30 account-input"
      v-model="amount"
      :precision="2"
      :controls="false"
      placeholder="请输入金额"
      size="large"
      style="font-size: 20px"
      @input="handleInput"
    />
    <TipText content="支付方式" />
    <div class="list ml20 mt28 flex">
      <div
        class="list-item flx-align-center cup mr30"
        :class="{ active: item.onlinePayType === createParams.onlinePayType }"
        v-for="(item, index) in payList"
        :key="index"
        @click="() => (createParams.onlinePayType = item.onlinePayType)"
      >
        <div class="icon mr12">
          <el-image
            :src="`https://ai-cloud-system.tos-cn-beijing.volces.com/imgs/userSideImage/${item.icon}.png`"
          />
        </div>
        <div>
          <div class="title">{{ item.title }}</div>
          <div class="tip mt5">{{ item.tip }}</div>
        </div>
      </div>
    </div>
    <div
      class="union mt20 mb30"
      v-if="createParams.onlinePayType === 'UNION_PAY'"
    >
      <div class="title mb16">对公转账汇款指引</div>
      <div class="flx-2 mb26">
        <div
          class="info-card"
          @click="activeBank = 'china'"
          :style="activeBank === 'china' ? 'border-color:blue' : ''"
        >
          <div class="title">境内</div>
          <div class="list">
            <div class="item flx-align-center">
              <div class="label">开户名称</div>
              <div class="value">{{ china.account }}</div>
            </div>
            <div class="item flx-align-center">
              <div class="label">开户银行</div>
              <div class="value">{{ china.bankName }}</div>
            </div>
            <div class="item flx-align-center">
              <div class="label">开户名称</div>
              <div class="value">{{ china.bankNo }}</div>
            </div>
          </div>
        </div>
        <div
          class="info-card"
          @click="activeBank = 'noChina'"
          :style="activeBank === 'noChina' ? 'border-color:blue' : ''"
        >
          <div class="title">境外</div>
          <div class="list">
            <div class="item flx-align-center">
              <div class="label">开户名称</div>
              <div class="value">{{ noChina.account }}</div>
            </div>
            <div class="item flx-align-center">
              <div class="label">开户银行</div>
              <div class="value">{{ noChina.bankName }}</div>
            </div>
            <div class="item flx-align-center">
              <div class="label">开户名称</div>
              <div class="value">{{ noChina.bankNo }}</div>
            </div>
            <div class="item flx-align-center">
              <div class="label">SWIFT CODE</div>
              <div class="value">{{ noChina.swiftCode }}</div>
            </div>
          </div>
        </div>
      </div>
      <div class="step mb26">
        <div class="step-icon">
          <img src="../../../assets/icon/step1-icon.png" />
        </div>
        <div class="step-info">
          <div class="title">确定汇款户名</div>
          <div class="step-item">
            • 请选择以上【境内】或【境外】两种对公账户进行汇款
          </div>
          <div class="step-item">
            •
            若汇款户名为非本实名主体，为保障资金安全与合规，请先添加汇款户名再向以上账户进行汇款
          </div>
          <el-input
            v-model="accountName"
            placeholder="请输入汇款主体"
            class="account-input"
          />
        </div>
      </div>
      <div class="step">
        <div class="step-icon">
          <img src="../../../assets/icon/step2-icon.png" />
        </div>
        <div class="step-info">
          <div class="title">确定汇款户名</div>
          <div class="step-item">
            •
            使用与本账号实名信息一致的账号进行汇款，汇款至对公账号，最快1小时左右到账
          </div>
          <div class="step-item">
            • 向对公账号汇款到账后，无需操作，系统将自动认领至您的逸云数智账户
          </div>
          <div class="step-item">
            •
            向对公账号汇款，需要您至汇款认领页面发起人工认领，认领充值到您逸云数智后，系统将通过短信通知您充值结果
          </div>
        </div>
      </div>
    </div>
    <TipText content="活动赠送代金券" class="mt28 mb20" />
    <div class="send-list mb30">
      <div class="small-card" v-for="(item, index) in activity" :key="index">
        <div class="top flx-align-center">
          <div class="left flx-center">
            <span class="icon">￥</span>{{ item.couponAmount }}
          </div>
          <div class="right">
            <div class="name">代金券</div>
            <div class="time">
              {{ `${item.couponStartTime}~${item.couponEndTime} ` }}
            </div>
          </div>
          <!-- 根据需求 -->
        </div>
        <div class="bottom flx-align-center">
          <div class="text">
            充值额度满 {{ item.rechargeAmount }} 即自动赠送
          </div>
        </div>
      </div>
    </div>
    <div class="bottom flx-justify-between">
      <div class="flx-align-center">
        <el-checkbox v-model="IsAgreement" />
        <p class="ml10 cup">
          <span @click="() => (IsAgreement = !IsAgreement)">
            请先阅读并同意</span
          >
          <span class="agreement" @click="openDocs"
            >《逸云数智产品和服务协议》</span
          >
        </p>
      </div>
      <div class="flx-align-center">
        <div class="total mr36">
          合计 <span class="highlight">￥</span>
          <span class="price highlight">{{ actualAmountPaid }}</span>
        </div>

        <el-button type="primary" @click="topUpFn"> 立即充值 </el-button>
      </div>
    </div>
    <div>
      <WeChatPay
        v-model="IsWeChatPay"
        :text="payData.onlinePayParam"
        :orderNo="payData.orderNo"
        :price="createParams.amount"
        @closePopover="finishPay"
      />
    </div>

    <el-button class="history" type="primary" plain @click="checkHistory"
      >对公转账历史</el-button
    >
    <el-dialog title="参与情况" v-model="historyDialog" width="1400">
      <TipText class="fwb mt15 mb15" content="基础信息" fontSize="16" />
      <div class="card info-card mb15">
        <ProTable
          type="none"
          :columns="columns"
          :tableData="tableData"
          :page-data="pageData"
          :get-list="getList"
          :IsRefresh="false"
        >
          <template #bankType="row">
            {{ row.bankType === 1 ? "国内" : "国外" }}
          </template>
          <template #status="row">
            <el-tag type="info" v-if="row.status == 1">待核实</el-tag>
            <el-tag type="success" v-if="row.status == 2">已打款</el-tag>
            <el-tag type="warning" v-if="row.status == 3">未打款</el-tag>
          </template>
        </ProTable>
      </div>
    </el-dialog>
    <el-dialog v-model="successDialog" width="400" top="198px">
      <div class="success-dialog">
        <img
          src="https://ai-cloud-system.tos-cn-beijing.volces.com/imgs/images/pay-success.png"
          class="mb22"
        />
        <div class="title mb22">支付成功</div>
        <div class="tips mb30">您已成功提交申请,请等待审核~</div>
        <div class="flx-align-center">
          <el-button type="primary" plain @click="backWay" class="mr30"
            >返回</el-button
          >
          <el-button type="primary" @click="checkWay">查看订单</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts" name="RechargeCenter">
import { createOrderApi } from "@/api/order";
import {
  createRemitAccountApi,
  getRechargeActivityListApi,
  getRemitAccountInfoApi,
  getRemitAccountListApi,
} from "@/api/recharge";
import { useTable } from "@/hooks/useTable";
import { useUserInfo } from "@/store";
import { toPage } from "@/utils";
// import { toPage } from "@/utils";
import { aliPay } from "@/utils/pay";
import { ElMessage } from "element-plus";
import { debounce } from "lodash";
const historyDialog = ref(false);
const priceList = [10, 50, 100, 200, 500, 1000];
const successDialog = ref(false);
const payList = [
  {
    title: "微信扫码支付",
    tip: "打开微信扫一扫支付",
    icon: "WeChatPay",
    onlinePayType: "WECHAT_PAY",
  },
  {
    title: "支付宝扫码支付",
    tip: "打开支付宝扫一扫支付",
    icon: "Alipay",
    onlinePayType: "ALI_PAY",
  },
  {
    title: "对公转账汇款",
    tip: "使用银行卡对公转账",
    icon: "UnionPay",
    onlinePayType: "UNION_PAY",
  },
];
// 对公信息
const accountName = ref("");
const activeBank = ref("china");
const china = ref<any>({
  account: "-",
  bankName: "-",
  bankNo: "-",
});
const noChina = ref<any>({
  account: "-",
  bankName: "-",
  bankNo: "-",
  swiftCode: "-",
});
const IsAgreement = ref(false);
// 选择金额
const flag = ref();
const changeFlag = (el: number) => {
  if (!!amount.value) amount.value = null;
  flag.value = flag.value === el ? null : el;
};

// 自定义充值金额
const amount = ref();
watch(
  () => amount.value,
  (val) => {
    if (val) flag.value = null;
  }
);
// 创建订单参数
const createParams = ref({
  orderType: "BALANCE",
  amount: 0,
  onlinePayType: "WECHAT_PAY",
});

const actualAmountPaid = computed(() => amount.value || flag.value || 0);

const userInfo = useUserInfo();

// 支付完成
const payData = ref({ onlinePayParam: "", orderNo: "", onlinePay: "" });
const IsWeChatPay = ref(false);
const topUpFn = debounce(
  async () => {
    if (!IsAgreement.value)
      return ElMessage.warning("请先阅读并同意逸云数智产品和服务协议");
    // 如果是对公转账汇款
    if (createParams.value.onlinePayType === "UNION_PAY") {
      if (!actualAmountPaid.value) {
        ElMessage.warning("请选择充值金额");
        return;
      }
      if (!accountName.value) {
        ElMessage.warning("请输入汇款主体");
        return;
      }
      let param = {
        bankType: "",
        bankName: "",
        bankNo: "",
        swiftCode: "",
        amount: 0,
        remitName: "",
        account: "",
      };
      if (activeBank.value === "china") {
        param.bankType = "1";
        param.bankName = china.value.bankName;
        param.bankNo = china.value.bankNo;
        param.account = china.value.account;
      } else {
        param.bankType = "2";
        param.bankName = noChina.value.bankName;
        param.bankNo = noChina.value.bankNo;
        param.swiftCode = noChina.value.swiftCode;
        param.account = noChina.value.account;
      }
      param.remitName = accountName.value;
      param.amount = amount.value || flag.value;
      createRemitAccountApi(param).then((res) => {
        if (res.code === 200) {
          successDialog.value = true;
          accountName.value = "";
          amount.value = null;
          window.scrollTo({ top: 0, behavior: "smooth" });
        }
      });
    } else {
      createParams.value.amount = amount.value || flag.value;
      if (!createParams.value.amount)
        return ElMessage.warning("请选择充值金额");
      const { data } = await createOrderApi(createParams.value);

      payData.value = data;
      if (payData.value.onlinePay === "WECHAT_PAY")
        return (IsWeChatPay.value = true);

      aliPay(data.orderNo);
      // 充值成功
      // userInfo.getUserInfo();
      // toPage("/accountCenter");
    }
  },
  1000,
  { leading: true, trailing: false }
);
const finishPay = () => {
  IsWeChatPay.value = false;
  toPage("/accountCenter");
};
const backWay = () => {
  successDialog.value = false;
};
const checkWay = () => {
  successDialog.value = false;
  checkHistory();
};
// handleInput监听自定义输入金额
const handleInput = () => {
  amount.value = amount.value.replace(/[^\d]/g, "");
  // 通过正则表达式确保输入值为正整数
  const regex = /^[1-9]\d*$/; // 匹配正整数
  if (!regex.test(amount.value)) {
    // 如果输入的值不是正整数，移除最后一个字符
    amount.value = amount.value.slice(0, -1);
  }
  // 限制最大值为9999
  if (parseInt(amount.value) > 99999999) {
    amount.value = "99999999";
  }
};
// 查看对公转账历史
const checkHistory = () => {
  historyDialog.value = true;
  pageData.value.pageNo = 1;
  pageData.value.pageSize = 10;
  getList();
};
const columns: ColumnProps[] = [
  { prop: "remitName", label: "汇款主体名" },
  {
    prop: "bankType",
    label: "银行类型",
    slot: true,
  },
  { prop: "bankName", label: "银行名称" },
  { prop: "account", label: "开户名称" },
  { prop: "bankNo", label: "银行卡号" },
  { prop: "swiftCode", label: "swiftCode" },
  { prop: "amount", label: "打款金额" },
  { prop: "createTime", label: "创建时间" },
  { prop: "status", label: "状态", slot: true },
];
// 对公打款数据
const { tableData, pageData, getList } = useTable({
  requestApi: getRemitAccountListApi,
  requestAuto: false,
});
const openDocs = () => {
  let a = window.location.origin + "/#/";
  a += "docsView/ProductAgreement";
  window.open(a, "_blank");
};
// 充值活动
const activity = ref<any>();
// 初始化页面数据
const initPage = () => {
  getRemitAccountInfoApi().then(({ data }) => {
    china.value = data.china;
    noChina.value = data.noChina;
  });
  getRechargeActivityListApi().then(({ data }) => {
    if (data.length > 0) {
      activity.value = data;
    }
  });
  getList();
};
initPage();
</script>
<style lang="scss" scoped>
.recharge {
  min-height: 100%;
  overflow: auto;
  display: flex;
  flex-direction: column;

  .list {
    &-item {
      border: 2px solid #e5e5e5;
      border-radius: 4px;
      padding: 16px 64px;
      font-size: 32px;
      position: relative;
      span {
        font-size: 16px;
      }
      .title {
        font-size: 18px;
        color: #000;
      }
      .tip {
        font-size: 16px;
        color: #83889d;
      }
      .icon {
        width: 32px;
        height: 32px;
        position: absolute;
        left: 20px;
      }
    }
    .active {
      border: 2px solid #3972fd;
      border-radius: 6px;
      &::before {
        content: "";
        position: absolute;
        background-image: url("@/assets/userSideImage/rechargeOptions.png");
        background-size: 100% 100%;
        width: 32px;
        height: 32px;
        right: 0;
        top: 0;
      }
    }
  }
  .account-input {
    width: 400px;
    height: 50px;
    font-size: 20px;
    line-height: 32px;
  }
  .union {
    padding: 30px;
    background: #f7f8fb;
    border-radius: 8px 8px 8px 8px;

    width: 100%;
    .title {
      font-weight: 400;
      font-size: 20px;
      color: #000000;
    }
    .flx-2 {
      display: flex;
      gap: 40px;
      height: 220px;
      .info-card {
        width: 520px;
        height: 220px;
        background: #ffffff;
        border-radius: 8px 8px 8px 8px;
        border: 1px solid #e5e5e5;
        padding: 20px 26px;
        display: flex;
        flex-direction: column;
        &:hover {
          cursor: pointer;
        }
        .title {
          font-weight: 400;
          font-size: 18px;
          margin-bottom: 16px;
        }
        .list {
          flex: 1;
          display: flex;
          flex-direction: column;
          gap: 10px;
          .item {
            gap: 20px;
            .label {
              width: 104px;
              font-weight: 400;
              font-size: 16px;
              color: #83889d;
            }
            .item {
              font-weight: 400;
              font-size: 16px;
            }
          }
        }
      }
    }
    .step {
      display: flex;
      gap: 14px;
      .step-icon {
        height: 40px;
        width: 26px;
        img {
          height: 100%;
          width: 100%;
        }
      }
      .step-info {
        display: flex;
        flex-direction: column;
        gap: 12px;
        padding-top: 6px;
        .title {
          font-weight: 400;
          font-size: 20px;
          color: #000000;
        }
        .step-item {
          font-weight: 400;
          font-size: 16px;
          color: #83889d;
        }
      }
    }
  }
  .bottom {
    margin-top: auto;
    background: #f7f8fb;
    font-size: 16px;
    padding: 20px;
    border-radius: 10px;
    width: 98%;
    .price {
      font-size: 28px;
    }
    .highlight {
      color: #ff4151;
    }
    .agreement {
      color: #3972fd;
    }
  }
  .history {
    position: absolute;
    left: 91%;
    font-size: 16px;
  }
}
.success-dialog {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  img {
    height: 72px;
    width: 72px;
  }
  .title {
    font-weight: 500;
    font-size: 20px;
    color: #000000;
  }
  .tips {
    font-weight: 400;
    font-size: 16px;
    color: #000000;
  }
  .el-button {
    width: 118px;
    height: 42px;

    border-radius: 4px 4px 4px 4px;
  }
}
.send-list {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-left: 16px;
  .small-card {
    width: 367px;
    height: 132px;
    border-top: 8px solid #f4aa2a;
    border-left: 2px solid #fff;
    border-right: 2px solid #fff;
    border-bottom: 2px solid #fff;
    border-radius: 10px;
    background: #ffffff;
    box-shadow: 0px 0px 16px 1px rgba(196, 213, 255, 0.5);
    display: flex;
    flex-direction: column;
    padding: 12px;

    .top {
      position: relative;
      gap: 20px;
      border-bottom: 1px solid rgb(232, 232, 232);
      .left {
        color: #ff4151;
        font-weight: bold;
        font-size: 28px;
        padding-bottom: 12px;
        .icon {
          font-size: 16px;
          font-weight: bold;
        }
      }
      .right {
        display: flex;
        flex-direction: column;
        gap: 13px;
        margin-bottom: 10px;

        .name {
          width: 136px;
          height: 28px;
          font-weight: 500;
          font-size: 20px;
          color: #333333;
          line-height: 28px;
          text-align: left;

          overflow: hidden;
          text-overflow: ellipsis; //文本溢出显示省略号
          white-space: nowrap; //文本不会换行
        }
        .time {
          width: 248px;
          height: 20px;
          font-weight: 400;
          font-size: 14px;
          color: #83889d;
          line-height: 20px;
          text-align: left;
          overflow: hidden;
          text-overflow: ellipsis; //文本溢出显示省略号
          white-space: nowrap; //文本不会换行
        }
      }
      .select {
        position: absolute;
        top: 4px;
        left: 300px;
        width: 22px;
        height: 22px;
        img {
          width: 22px;
          height: 22px;
        }
      }
    }
    .bottom {
      padding-top: 12px;
      display: flex;
      align-items: center;
      font-weight: 400;
      font-size: 14px;
      color: #333333;
      text-align: left;
      overflow: hidden;
      text-overflow: ellipsis; //文本溢出显示省略号
      white-space: nowrap; //文本不会换行
      .text {
        padding-top: 6px;
      }
    }
  }
}
</style>
