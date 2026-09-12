<template>
  <div class="pay">
    <Breadcrumb :router-list="routerList"></Breadcrumb>
    <div class="card">
      <!--订单 -->
      <div v-if="user.totalBalance < 0" class="owe-tip ml20 mb20">
        当前账户总余额欠费,暂不支持创建实例。请先缴清欠款后再尝试创建实例。
      </div>
      <TipText class="fwb" content="待支付订单" fontSize="20" />

      <el-table class="mt28" :data="tableData" style="width: 100%" border>
        <el-table-column property="orderNo" label="订单号" />
        <el-table-column property="productName" label="产品名称" />
        <el-table-column label="类型">
          <template #default="scope">
            {{
              scope.row.chargeType === "POSTPAID_BY_HOUR"
                ? "按量计费"
                : "包年包月"
            }}
          </template>
        </el-table-column>
        <el-table-column property="sourceName" label="配置">
          <template #default="scope">
            {{ scope.row.sourceName ? scope.row.sourceName : "-" }}
          </template>
        </el-table-column>
        <el-table-column property="address" label="单价">
          <template #default="scope">
            {{
              scope.row.chargeType === "POSTPAID_BY_HOUR"
                ? `${scope.row.premiumPrice}/时`
                : scope.row.durationUnit === "MONTH"
                ? `${scope.row.finalUnitPrice / scope.row.duration}/月`
                : `${scope.row.finalUnitPrice} / ${scope.row.duration}年`
            }}
          </template>
        </el-table-column>

        <el-table-column label="数量">
          <template #default="scope">
            {{
              [
                "NAT 网关资源费用",
                "容器服务托管费用",
                "负载均衡资源费用",
                "公网IP",
              ].indexOf(scope.row.productName) > -1
                ? 1
                : order.$state.order.instanceNum
            }}
          </template>
        </el-table-column>
        <el-table-column
          label="时长"
          v-if="order.$state.order.chargeType !== 'POSTPAID_BY_HOUR'"
        >
          <template #default>
            {{ order.$state.order.duration.num }}
            {{
              order.$state.order.duration.durationUnit === "MONTH"
                ? "个月"
                : "年"
            }}
          </template>
        </el-table-column>

        <el-table-column label="订单应付金额">
          <template #default="scope">
            {{ scope.row.totalPrice ? scope.row.totalPrice : "-" }}
          </template>
        </el-table-column>
      </el-table>

      <!-- 优惠券 -->
      <TipText class="fwb mt38" content="选择优惠券" fontSize="20">
        <template #end>
          <div class="end flx-align-center">
            <el-checkbox v-model="isDisCount" label="使用优惠券抵扣" />
            <div class="price ml30" v-if="selectDisCount.id">
              ￥{{ selectDisCount.deductionAmount }}
            </div>
          </div>
        </template>
      </TipText>
      <div class="coupon-list mt35">
        <CouponCard
          v-for="item in usefulCouponList"
          :key="item.id"
          card-type="small"
          :name="item.name"
          :show-btn="false"
          :type="item.type"
          :couponRemark="item.couponRemark"
          :deductionAmount="item.deductionAmount"
          :useTimeStart="item.useTimeStart"
          :useTimeEnd="item.useTimeEnd"
          :customerReceive="item.customerReceive"
          :selected="selectDisCount.id === item.id"
          @click="selectCoupon(item)"
          btn-type="select"
        ></CouponCard>
      </div>
      <div
        v-if="usefulCouponList.length === 0"
        class="no-use-coupon flx-align-center"
      >
        暂无可用优惠券
      </div>
      <div class="spread flx-center mt24" v-if="couponList.length > 4">
        <div v-if="!isSpread" @click="isSpread = !isSpread">展开更多</div>
        <div v-else @click="isSpread = !isSpread">收起</div>
      </div>
      <!-- 结算方式 -->
      <TipText class="fwb mt40" content="结算方式" fontSize="20" />
      <div class="pay-list mt28 pb70">
        <PayCard
          v-for="(item, index) in payCardList"
          :key="index"
          :title="item.title"
          :count="item.count"
          :balance="item.balance"
          :bgSrc="item.bgSrc"
          :selected="selectPay.indexOf(item.flag)"
          :no-show="item.noShow"
          @click="choosePay(item.flag)"
        ></PayCard>
      </div>
      <!-- footer -->
      <div class="footer mt40 flx-align-center">
        <el-checkbox v-model="isAgree">
          <template #default>
            我已阅读并同意<span style="color: #3972fd" @click="openDocs"
              >《逸云数智产品和服务协议》</span
            >
          </template>
        </el-checkbox>
        <div class="end flx-align-center">
          <div class="label mr34">
            余额支付<span class="detail">￥ {{ balancePayAmount }}</span>
          </div>
          <div class="label mr34">
            代金券支付<span class="detail">￥ {{ voucherPayAmount }}</span>
          </div>
          <div class="label mr34">
            授信额<span class="detail">￥{{ creditPayAmount }}</span>
          </div>
          <div class="label mr34">
            在线支付<span class="detail">￥{{ onlinePayAmount }}</span>
          </div>
          <div class="label mr34">
            实付金额<span style="color: red"
              >￥<span style="font-weight: bold; font-size: 28px">{{
                billAmount
              }}</span></span
            >
          </div>
          <el-button
            type="primary"
            @click="createOrder"
            :disabled="user.totalBalance < 0"
            >立即支付</el-button
          >
        </div>
      </div>
    </div>
  </div>

  <el-dialog v-model="payDialog" width="990" top="198px">
    <div class="pay-dialog">
      <div class="title">扫一扫付款</div>
      <div class="price">
        <span style="font-weight: 500; font-size: 16px">￥</span>800
      </div>
      <div class="content">
        <div class="left flx-align-center mr56">
          <div class="code"></div>
          <div class="text mt25">
            打开{{
              selectPay.includes("WECHAT_PAY") ? "微信" : "支付宝"
            }}app扫码支付
          </div>
        </div>
        <div class="right pt145">
          <div class="info flx-align-center mb30">
            <div class="label mr26">订单编号</div>
            <div class="value">D486486148646848641</div>
          </div>

          <div class="info flx-align-center mb30">
            <div class="label mr26">支付金额</div>
            <div class="value">￥800</div>
          </div>
          <div class="info flx-align-center">
            <div class="label mr26">商品名称</div>
            <div class="value">云账户充值</div>
          </div>
        </div>
      </div>
      <div class="button flx-center mt30">我已支付</div>
    </div>
  </el-dialog>
  <el-dialog v-model="successDialog" width="660" top="198px">
    <div class="success-dialog">
      <img
        src="https://ai-cloud-system.tos-cn-beijing.volces.com/imgs/images/pay-success.png"
        class="mb22"
      />
      <div class="title mb22">支付成功</div>
      <div class="tips mb30">您已成功购买产品,快去使用起来吧~</div>
      <el-button type="primary" @click="backWay">返回</el-button>
    </div>
  </el-dialog>
  <WeChatPay
    v-model="IsWeChatPay"
    :text="payData.onlinePayParam"
    :orderNo="payData.orderNo"
    :price="wechatPrice"
    @closePopover="finishPay"
  />
</template>

<script setup lang="ts" name="pay">
import { useOrder } from "@/store/modules/order";
import { useUserInfo } from "@/store";
import PayCard from "../components/PayCard.vue";
import { buildOrderInfoApi, createOrderApi } from "@/api/order";
import { getUserCouponListAPI, getUserVoucherListAPI } from "@/api/user";
import { generateUUID, toPage } from "@/utils";
import { ElMessage } from "element-plus";
import { aliPay } from "@/utils/pay";
import { debounce } from "lodash";

const order = useOrder();
const user = useUserInfo();

const payDialog = ref(false);
const successDialog = ref(false);
const tableData = ref<any>([]);
const total = ref({
  discountAmount: 0,
  payAmount: 0,
});

onMounted(() => {
  initFunc();
  // 获取
  tableData.value = order.$state.order.targetOrderDetail.orderSourceList;
  tableData.value.forEach((item: any) => {
    if (item.couponDiscountAmount)
      total.value.discountAmount += item.couponDiscountAmount;
    // 根据时长来计算订单价格
    if (
      order.$state.order.chargeType === "POSTPAID_BY_MONTH" ||
      order.$state.order.chargeType === "POSTPAID_BY_YEAR"
    ) {
      // 按月计费
      if (order.$state.order.duration.durationUnit === "MONTH") {
        item.totalPrice = item.finalUnitPrice * order.$state.order.instanceNum;
      } // 按年计费
      else {
        item.totalPrice = item.finalUnitPrice * order.$state.order.instanceNum;
      }
      // 所有配置最终应付金额
      total.value.payAmount += Number(item.totalPrice);
    } //按量计费
    else {
      total.value.payAmount +=
        Number(item.finalUnitPrice) * order.$state.order.instanceNum;
    }
  });
  total.value.discountAmount =
    total.value.discountAmount * order.$state.order.duration.num;
});

const routerList = ref([
  { name: "容器列表", path: "/containerList" },
  { name: "创建集群", path: "/createCluster" },
  { name: "支付订单", path: "" },
]);
// 支付卡片
const payCardList = [
  {
    title: "代金券",
    count: user.$state.voucherBalance,
    balance: user.$state.voucherBalance,
    bgSrc: "voucher-pay",
    flag: "voucherPay",
    noShow: false,
  },
  {
    title: "余额支付",
    count: user.$state.balance,
    balance: user.$state.balance,
    bgSrc: "balance-pay",
    flag: "balancePay",
    noShow: false,
  },
  {
    title: "授信额",
    count: user.$state.creditAmount,
    balance: user.$state.creditAmount,
    bgSrc: "credit-pay",
    flag: "creditLinePay",
  },
  {
    title: "支付宝支付",
    // count: 800,
    // balance: 20,
    bgSrc: "ali-pay",
    flag: "ALI_PAY",
    noShow: true,
  },
  {
    title: "微信支付",
    // count: 800,
    // balance: 20,
    bgSrc: "wechat-pay",
    flag: "WECHAT_PAY",
    noShow: true,
  },
];
// 是否使用优惠券
const isDisCount = ref(true);
const selectDisCount = ref<any>({ id: "" });
const selectCoupon = (item: any) => {
  if (selectDisCount.value.id === item.id) {
    selectDisCount.value = {};
  } else selectDisCount.value = item;
};

// 支付方式
const selectPay = ref<string[]>([]);
const choosePay = (flag: string) => {
  // 除去已选的支付方式
  if (selectPay.value.indexOf(flag) > -1) {
    selectPay.value.splice(selectPay.value.indexOf(flag), 1);
  } else {
    // 如果是授信额支付，则清除所有的支付方式，仅保留授信额
    if (flag === "creditLinePay") {
      if (Number(billAmount.value) > Number(user.$state.creditAmount)) {
        ElMessage.warning("授信额不足，请选择其他支付方式");
        return;
      }
      selectPay.value = [];
      selectPay.value.push(flag);
    } else if (flag === "ALI_PAY" || flag === "WECHAT_PAY") {
      const i = selectPay.value.findIndex(
        (item) => item === "ALI_PAY" || item === "WECHAT_PAY"
      );
      if (i !== -1) {
        selectPay.value[i] = flag;
      } else {
        selectPay.value.push(flag);
      }
      // 找到授信额，如果有就去除
      if (selectPay.value.indexOf("creditLinePay") > -1) {
        selectPay.value.splice(selectPay.value.indexOf("creditLinePay"), 1);
      }
    } else {
      selectPay.value.push(flag);
      // 找到授信额，如果有就去除
      if (selectPay.value.indexOf("creditLinePay") > -1) {
        selectPay.value.splice(selectPay.value.indexOf("creditLinePay"), 1);
      }
    }
  }
};

// 是否同意平台服务协议
const isAgree = ref(false);
const couponList = ref<any>([]);
// 筛选可用优惠券
const isSpread = ref(false);
const usefulCouponList = computed(() => {
  let flag = couponList.value.filter((item: any) => {
    return item.thresholdAmount <= total.value.payAmount;
  });
  // 未展开且数组数量大于4
  if (!isSpread.value && flag.length > 4) {
    return flag.slice(0, 4);
  } else {
    return flag;
  }
});
const voucherList = ref<any>([]);
// 初始化页面(获取优惠券、代金券等)
const initFunc = () => {
  getUserCouponListAPI(
    { pageNo: 1, pageSize: 100 },
    {
      sourceList: [
        {
          sourceType: "CONTAINER",
          price: order.$state.order.targetOrderDetail.finalPayAmount,
        },
      ],
    }
  ).then((res) => {
    couponList.value = res.data.list;
  });
  getUserVoucherListAPI().then((res) => {
    voucherList.value = res.data.list;
    console.log(voucherList.value);
  });
};
// 实付总金额（账单金额-优惠券金额）
const billAmount = computed(() => {
  let totalPrice = total.value.payAmount;
  if (isDisCount.value && selectDisCount.value.id) {
    totalPrice -= selectDisCount.value.deductionAmount;
  }
  return Number(totalPrice).toFixed(2);
});
// 代金券支付费用(优先级更高)
const voucherPayAmount = computed(() => {
  if (selectPay.value.indexOf("voucherPay") != -1) {
    // 如果余额能够cover所有费用
    if (user.$state.voucherBalance >= Number(billAmount.value)) {
      return billAmount.value;
    } else {
      return user.$state.voucherBalance;
    }
  } else {
    return 0;
  }
});

// 余额支付费用
const balancePayAmount = computed(() => {
  if (selectPay.value.indexOf("balancePay") != -1) {
    // 查看是否有勾选代金券付款
    let bill = JSON.parse(JSON.stringify(billAmount.value));
    if (selectPay.value.indexOf("voucherPay") != -1) {
      bill -= Number(voucherPayAmount.value);
    }
    // 如果余额能够cover所有费用
    if (user.$state.balance >= Number(bill)) {
      return bill;
    } else {
      return user.$state.balance;
    }
  } else {
    return 0;
  }
});

// 在线支付费用
const onlinePayAmount = computed(() => {
  if (selectPay.value.indexOf("creditLinePay") > -1) {
    return 0;
  }
  let amount = Number(billAmount.value);
  amount -= Number(voucherPayAmount.value);
  amount -= balancePayAmount.value;
  return Number(amount.toFixed(2));
});
// 授信额支付
const creditPayAmount = computed(() => {
  if (selectPay.value.indexOf("creditLinePay") > -1) {
    return Number(billAmount.value).toFixed(2);
  } else {
    return 0;
  }
});
const wechatPrice = computed(() => Number(onlinePayAmount.value).toFixed(2));
// 结算订单
const payData = ref({ onlinePayParam: "", orderNo: "", onlinePay: "" });
const IsWeChatPay = ref(false);
const createOrder = debounce(
  () => {
    if (!isAgree.value) {
      ElMessage.warning("请阅读并同意平台协议!");
      return;
    }
    const data = {
      orderType: "NEW_RESOURCE",
      couponId: selectDisCount.value.id,
      voucherPay: selectPay.value.includes("voucherPay") ? true : false,
      balancePay: selectPay.value.includes("balancePay") ? true : false,
      creditLinePay: selectPay.value.includes("creditLinePay") ? true : false,
      onlinePayType: selectPay.value.includes("ALI_PAY")
        ? "ALI_PAY"
        : selectPay.value.includes("WECHAT_PAY")
        ? "WECHAT_PAY"
        : null,
      orderSource: [
        {
          uid: order.$state.order.disk[0].uid,
          regionsId: "CN_BEIJING",
          sourceType: "CONTAINER",
          chargeType: "POSTPAID_BY_HOUR",
          configDetail: {
            clusterName: order.$state.order.container.clusterName,
            kubernetesVersion: order.$state.order.container.kubernetesVersion,
            resourcePublicAccessDefaultEnabled:
              order.$state.order.container.resourcePublicAccessDefaultEnabled,
            apiServerPublicAccessEnabled:
              order.$state.order.container.apiServerPublicAccessEnabled,
            nodePoolName: order.$state.order.container.nodePoolName,
            nodePoolNumber: order.$state.order.instanceNum,
            isOpenSecurityHardening:
              order.$state.order.container.isOpenSecurityHardening,
          },
        },
        {
          uid: order.$state.order.disk[0].uid,
          regionsId: "CN_BEIJING",
          sourceType: "ECS",
          chargeType: order.$state.order.chargeType,
          duration: order.$state.order.duration.num,
          durationUnit: order.$state.order.duration.durationUnit,
          configDetail: {
            id: order.$state.order.instance.id,
            ecsType: order.$state.order.instance.ecsType,
            ecsScale: order.$state.order.instance.ecsScale,
            cpuNumber: order.$state.order.instance.cpuNumber,
            memorySize: order.$state.order.instance.memorySize,
            cpuModel: order.$state.order.instance.cpuModel,
            gpuModel:
              order.$state.order.instance.gpuModel &&
              order.$state.order.instance.gpuModel != "-"
                ? order.$state.order.instance.gpuModel
                : null,
            gpuMemory:
              order.$state.order.instance.gpuMemory &&
              order.$state.order.instance.gpuMemory != "-"
                ? order.$state.order.instance.gpuMemory
                : null,
            regionsZones: order.$state.order.region,
            description: order.$state.order.instanceDescription,
            instanceName: order.$state.order.instanceName,
            hostName: order.$state.order.hostName,
            imageId: order.$state.order.platformVersion.imageId,
            installRunCommandAgent: true,
            password: order.$state.order.password,
            zoneId: order.$state.order.zoneId,
          },
        },
      ],
    };
    // 有数据盘
    if (order.$state.order.disk.length > 1) {
      let disk = order.$state.order.disk;
      disk.forEach((item: any, index: any) => {
        if (index > 0) {
          data.orderSource.push({
            uid: order.$state.order.disk[0].uid,
            regionsId: "CN_BEIJING",

            chargeType: order.$state.order.chargeType,
            duration: order.$state.order.duration.num,
            durationUnit: order.$state.order.duration.durationUnit,

            sourceType: "CLOUD_STORAGE",
            configDetail: item,
          });
        }
      });
    }
    // 多台服务器(暂时注释)
    // if (order.$state.order.instanceNum > 1) {
    //   let copy = JSON.parse(JSON.stringify(data.orderSource))
    //   console.log('copy', copy)
    //   let flag

    //   for (let i = 1; i < order.$state.order.instanceNum; i++) {
    //     flag = JSON.parse(JSON.stringify(copy))
    //     flag.shift()
    //     let uuid = generateUUID()
    //     flag.forEach((item: any) => {
    //       item.uid = uuid
    //     })
    //     data.orderSource.push(...flag)
    //   }
    // }
    if (onlinePayAmount.value && !data.onlinePayType) {
      ElMessage.warning("请选择支付方式!");
      return;
    }
    createOrderApi(data).then((res) => {
      if (res.code === 200) {
        // 第二个服务器才是
        if (data.orderSource[1].chargeType === "POSTPAID_BY_HOUR") {
          successDialog.value = true;
        } else {
          payData.value = res.data;
          if (payData.value.onlinePay === "WECHAT_PAY")
            return (IsWeChatPay.value = true);
          else if (payData.value.onlinePay === "ALI_PAY")
            // aliPay(res.data.onlinePayParam);
            aliPay(res.data.orderNo);
          else successDialog.value = true;
        }
      }
    });
  },
  1000,
  { leading: true, trailing: false }
);

const backWay = () => {
  successDialog.value = false;
  toPage("/containerList");
};
const finishPay = () => {
  IsWeChatPay.value = false;
  successDialog.value = true;
  user.getUserInfo();
};
const openDocs = () => {
  let a = window.location.origin + "/#/";
  a += "docsView/ProductAgreement";
  window.open(a, "_blank");
};
</script>
<style lang="scss" scoped>
@import "./index.scss";

.pay-dialog {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;

  .title {
    font-weight: 400;
    font-size: 20px;
    color: #666666;
  }

  .price {
    color: #ff4151;
    font-weight: bold;
    font-size: 40px;
    margin-top: 10px;
    margin-bottom: 26px;
  }

  .content {
    width: 910px;
    height: 402px;
    background: #f7f8fb;
    border-radius: 8px 8px 8px 8px;
    padding: 30px 115px;
    display: flex;

    .left {
      flex-direction: column;

      .code {
        width: 284px;
        height: 284px;
        border-radius: 0px 0px 0px 0px;
        background-color: white;
      }

      .text {
        font-weight: 400;
        font-size: 20px;
        color: #000000;
      }
    }

    .right {
      padding-top: 165px;
    }
  }

  .button {
    width: 138px;
    height: 49px;
    border-radius: 4px 4px 4px 4px;
    border: 1px solid #cccccc;
    font-weight: 500;
    font-size: 18px;
    color: #000000;

    &:hover {
      cursor: pointer;
    }
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
    background: #3972fd;
    border-radius: 4px 4px 4px 4px;
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
  height: 100%;
  padding: 0 10px 0 10px;
  border-radius: 4px;
  width: 34%;
  padding: 10px 10px;
}
</style>
