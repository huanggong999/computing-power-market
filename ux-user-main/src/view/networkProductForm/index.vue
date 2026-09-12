<template>
  <div class="content" ref="content">
    <div style="width: 74%">
      <el-breadcrumb :separator-icon="ArrowRight">
        <el-breadcrumb-item :to="{ path: '/networkProduct' }"
          >网络产品</el-breadcrumb-item
        >
        <el-breadcrumb-item>AGI-C表单</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <div class="owe-tip ml20" v-if="unableDay">
      当前账户总余额低于50元,暂不支持按天计费创建实例。
    </div>
    <div
      class="card product mt20"
      v-loading="isPayLoading"
      element-loading-text="创建订单..."
    >
      <div class="title mb30">AGI-C购买表单</div>
      <TipText
        class="fwb mb30"
        :content="`购买产品   [${
          targetNetworkProduction.production.name
            ? targetNetworkProduction.production.name
            : ''
        }]`"
      ></TipText>

      <!-- 表单详情 -->
      <ProForm
        ref="proFormRef"
        style="width: 400px"
        v-model="agicForm"
        :formColumns="agicFormCol"
        class="mt20"
      >
        <!-- 计费类型 -->
        <template #chargeType>
          <el-radio-group
            v-model="agicForm.chargeType"
            :disabled="agicProduct.agic.editType == 'count'"
          >
            <el-radio-button label="包年包月" value="POSTPAID_BY_MONTH" />
            <el-radio-button
              label="按天计费"
              value="POSTPAID_BY_HOUR"
              :disabled="
                !!agicProduct.agic.productId &&
                agicProduct.agic.chargeType != 'POSTPAID_BY_HOUR'
              "
            />
          </el-radio-group>
        </template>

        <!-- 动态展示购买时长 -->
        <template #duration>
          <div
            v-if="
              agicForm.chargeType === 'POSTPAID_BY_MONTH' ||
              agicForm.chargeType === 'POSTPAID_BY_YEAR'
            "
            class="month"
          >
            <el-select
              :disabled="agicProduct.agic.editType == 'count'"
              value-key="result"
              style="width: 210px"
              v-model="durationValue"
              placeholder="请选择"
            >
              <el-option
                v-for="(item, index) in durationList"
                :key="index"
                :label="item.label"
                :value="item"
              ></el-option>
            </el-select>
            <div class="flex swi">
              自动续费<el-switch
                v-model="agicForm.isAutoRenew"
                class="ml-2"
                :active-value="1"
                :inactive-value="0"
                style="
                  --el-switch-on-color: #13ce66;
                  --el-switch-off-color: #1111;
                "
              />
            </div>
          </div>

          <div v-else>按天计费</div>
        </template>
        <template #networkCount>
          <el-input-number
            v-model="agicForm.networkCount"
            :disabled="agicProduct.agic.editType == 'renew'"
          ></el-input-number
        ></template>
        <template #ipCount>
          <el-input-number
            v-if="displayOrBind.isIpDisplay"
            v-model="agicForm.ipCount"
            :disabled="agicProduct.agic.editType == 'renew'"
          ></el-input-number
        ></template>
        <template #bandwidth>
          <el-input-number
            v-if="displayOrBind.isBandwidthDisplay"
            v-model="agicForm.bandwidth"
            :disabled="agicProduct.agic.editType == 'renew'"
          ></el-input-number
        ></template>
        <template #agiCustomerName>
          <el-input
            style="width: 210px; margin-bottom: 5px"
            placeholder="请输入客户名称"
            v-model="orderInfo.agiCustomerName"
            :disabled="agicProduct.agic.productId"
          ></el-input>
        </template>
        <template #mobile>
          <el-input
            style="width: 210px; margin-bottom: 5px"
            placeholder="请输入联系电话 "
            v-model="orderInfo.mobile"
            :disabled="agicProduct.agic.productId"
          ></el-input>
        </template>
        <!-- 动态展示邮箱数目 -->
        <template #email>
          <div>
            <el-input
              style="width: 203px; margin-bottom: 5px"
              placeholder="请输入邮箱"
              v-for="(item, index) in agicEmailForm"
              :key="index"
              v-model="agicEmailForm[index]"
              :disabled="index < agicProduct.agic.networkCount"
            >
            </el-input>
          </div>
        </template>
        <template #remark>
          <el-input
            style="width: 210px; margin-bottom: 5px"
            type="textarea"
            placeholder="请输入"
            v-model="orderInfo.remark"
            :rows="3"
          ></el-input
        ></template>
      </ProForm>
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
          :name="item.name"
          card-type="small"
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

      <!-- <FormCreate :rule=" payRule" v-model:api="fApi" :option="payOptions" v-model="payFormParams" /> -->
      <TipText class="fwb mt30" content="支付方式"></TipText>
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
      <div class="footer mt40" v-if="isFloatingFooter"></div>
      <div
        class="footer mt40 flx-align-center"
        :class="{ fixed: isFloatingFooter }"
        ref="footer"
      >
        <el-checkbox v-model="isAgree">
          <template #default>
            我已阅读并同意<span style="color: #3972fd" @click="openDocs"
              >《逸云数智产品和服务协议》</span
            >
          </template>
        </el-checkbox>
        <div class="end flx-align-center">
          <div class="label mr34 pt20">
            余额支付<span class="detail">￥ {{ balancePayAmount }}</span>
          </div>
          <div class="label mr34 pt20">
            代金券支付<span class="detail">￥ {{ voucherPayAmount }}</span>
          </div>
          <div class="label mr34 pt20">
            授信额<span class="detail">￥{{ creditPayAmount }}</span>
          </div>
          <div class="label mr34 pt20">
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
            class="btn"
            type="primary"
            @click="createOrder"
            :disabled="
              unableDay || (isEdit && agicForm.chargeType == 'POSTPAID_BY_HOUR')
            "
            >立即支付</el-button
          >
        </div>
      </div>
    </div>
  </div>
  <!-- 成功弹窗 -->
  <el-dialog
    width="300"
    v-model="successDialog"
    align-center
    class="success-dialog"
  >
    <div class="success-content">
      <img src="../../assets/images/pay-success.png" alt="" />
      <div class="title">支付成功</div>
      <div class="value">您已完成订单购买·</div>
      <div class="btns">
        <el-button @click="toUrl('/networkProduct')">返回</el-button>
        <el-button type="primary" @click="toUrl('/aigcOrder')"
          >返回控制台</el-button
        >
      </div>
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

<script setup lang="ts" name="productDetail">
import {
  buildAgicOrderInfoApi,
  checkEmailExistApi,
  checkIpNumApi,
  createAgicOrderApi,
  submitNetWorkPayFormApi,
} from "@/api/networkProduct";
import PayCard from "../Cloud/components/PayCard.vue";
import { useUserInfo } from "@/store";
import { ArrowRight } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { onBeforeUnmount, onMounted, onUnmounted, ref } from "vue";
import { useRoute } from "vue-router";
import { toPage } from "@/utils";
import { aliPay } from "@/utils/pay";
import { debounce } from "lodash";
import { useAgicProduct } from "@/store/modules/agic";
import { useProduction } from "@/store/modules/networkProduct";
import { getUserCouponListAPI } from "@/api/user";
const route = useRoute();
const agicProduct = useAgicProduct();
const targetNetworkProduction = useProduction();
const userInfo = useUserInfo();
onMounted(() => {
  userInfo.getUserInfo();
});
const id = ref(route.query.id ? route.query.id : agicProduct.agic.productId);
const isEdit = ref(
  route.query.edit || agicProduct.agic.productId ? true : false
);
const unableDay = computed(
  () =>
    user.totalBalance < 50 && agicForm.value.chargeType == "POSTPAID_BY_HOUR"
);

const couponList = ref<any>([]);

const isDisCount = ref(true);
const selectDisCount = ref<any>({ id: "" });
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
const getCoupon = () => {
  getUserCouponListAPI(
    { pageNo: 1, pageSize: 100 },
    {
      sourceList: [{ sourceType: "AGIC", price: billAmount.value }],
    }
  ).then((res) => {
    couponList.value = res.data.list;
  });
};
const selectCoupon = (item: any) => {
  if (selectDisCount.value.id === item.id) {
    selectDisCount.value = {};
  } else selectDisCount.value = item;
};

// 表单
const agicFormCol: IFormColumnsProps[] = [
  {
    label: "计费类型",
    prop: "chargeType",
    el: "slot",
    radioList: [
      {
        label: "包年包月",
        value: "POSTPAID_BY_MONTH",
        // disabled: agicProduct.agic.chargeType == 'POSTPAID_BY_HOUR',
      },
      {
        label: "按天计费",
        value: "POSTPAID_BY_HOUR",
        disabled:
          !!agicProduct.agic.productId &&
          agicProduct.agic.chargeType != "POSTPAID_BY_HOUR",
      },
    ],
    required: false,
  },
  // 如果付费方式是按天，则隐藏
  {
    prop: "duration",
    label: "购买时长",
    el: "slot",
    required: false,
  },

  {
    prop: "networkCount",
    label: "账户数",
    el: "slot",
    controls: true,
    required: false,
  },
  {
    prop: "ipCount",
    label: "IP地址数",
    el: "slot",
    controls: true,
    required: false,
  },
  {
    prop: "bandwidth",
    label: "带宽(M)",
    el: "slot",
    controls: true,
    required: false,
  },
  {
    prop: "agiCustomerName",
    label: "客户名称",
    el: "slot",
    required: false,
  },
  {
    prop: "mobile",
    label: "联系电话",
    el: "slot",
    required: false,
  },
  // 根据账户数动态处理
  {
    prop: "email",
    label: "联系邮箱",
    el: "slot",
    required: false,
  },
  {
    prop: "remark",
    label: "备注",
    el: "slot",
    required: false,
  },
];
// 获取三要素字段关系
const displayOrBind = computed(() => {
  // 如果agicProduct.displayOrBind中的isIpDisplay为false,则删除agicFormCol中的ipCount字段
  if (agicProduct.displayOrBind.isIpDisplay == 0) {
    agicFormCol.splice(
      agicFormCol.findIndex((item) => item.prop == "ipCount"),
      1
    );
  }
  // 如果agicProduct.displayOrBind中的isBandwidthDisplay为false,则删除agicFormCol中的bandwidth字段
  if (agicProduct.displayOrBind.isBandwidthDisplay == 0) {
    agicFormCol.splice(
      agicFormCol.findIndex((item) => item.prop == "bandwidth"),
      1
    );
  }
  return agicProduct.displayOrBind;
});
const agicForm = ref<any>({
  chargeType:
    agicProduct.agic.chargeType == "POSTPAID_BY_HOUR"
      ? agicProduct.agic.chargeType
      : "POSTPAID_BY_MONTH",
  networkCount: agicProduct.agic.networkCount
    ? Number(agicProduct.agic.networkCount)
    : 1,
  ipCount: agicProduct.agic.ipCount ? Number(agicProduct.agic.ipCount) : 1,
  bandwidth: agicProduct.agic.bandwidth
    ? Number(agicProduct.agic.bandwidth)
    : 1,
  isAutoRenew: agicProduct.agic.isAutoRenew ? agicProduct.agic.isAutoRenew : 1,
});
const orderInfo = ref<any>({
  agiCustomerName: agicProduct.agic.agiCustomerName
    ? agicProduct.agic.agiCustomerName
    : "",
  mobile: agicProduct.agic.mobile ? agicProduct.agic.mobile : "",
  remark: agicProduct.agic.remark ? agicProduct.agic.remark : "",
});
const durationValue = ref<any>({
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
    label: "7个月",
    result: {
      num: 7,
      durationUnit: "MONTH",
    },
  },
  {
    label: "8个月",
    result: {
      num: 8,
      durationUnit: "MONTH",
    },
  },
  {
    label: "9个月",
    result: {
      num: 9,
      durationUnit: "MONTH",
    },
  },
  {
    label: "10个月",
    result: {
      num: 10,
      durationUnit: "MONTH",
    },
  },
  {
    label: "11个月",
    result: {
      num: 11,
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
// 检测agicProduct.agic中有没有duration和durationUnit这两个字段，有的话赋值给durationValue病将其label改为命中durationList的label
if (
  agicProduct.agic.editType == "count" &&
  agicProduct.agic.duration &&
  agicProduct.agic.durationUnit
) {
  durationValue.value = durationList.find(
    (item) =>
      item.result.num == agicProduct.agic.duration &&
      item.result.durationUnit == agicProduct.agic.durationUnit
  );
}

// 邮箱数组
const agicEmailForm = ref<any>(
  agicForm.value.networkCount
    ? new Array(agicForm.value.networkCount).fill("")
    : [""]
);
// 如果agicProduct.agic.productId存在，则根据agicProduct.agic.email来初始化agicEamilForm
if (agicProduct.agic.productId) {
  agicEmailForm.value = agicProduct.agic.emails;
}
watch(
  () => agicForm.value.networkCount,
  (newCount, oldCount) => {
    // 如果是续费订单则不允许修改
    const minCount = agicProduct.agic.productId
      ? Number(agicProduct.agic.networkCount)
      : 1;

    // 边界判断并修正 networkCount
    if (newCount < minCount) {
      agicForm.value.networkCount = minCount;
      return;
    }

    if (oldCount < minCount) {
      return;
    }
    const currentEmails = agicEmailForm.value;
    // 根据 newCount 增减 agicEmailForm.value 的长度
    if (newCount > oldCount) {
      agicEmailForm.value = [
        ...currentEmails,
        ...Array.from({ length: newCount - oldCount }, () => ""),
      ];
    } else if (newCount < oldCount) {
      agicEmailForm.value = currentEmails.slice(0, newCount);
    }
    if (displayOrBind.value.isAccountIpBinding) {
      // 如果是账户IP绑定，则需要将IP数也同步到网络数
      agicForm.value.ipCount = newCount;
    }
  },
  { immediate: true, deep: true }
);
// ip地址数必须大于1
watch(
  () => agicForm.value.ipCount,
  (newCount) => {
    const minCount = agicProduct.agic.productId
      ? Number(agicProduct.agic.ipCount)
      : 1;
    if (newCount < minCount) {
      agicForm.value.ipCount = minCount;
    }
    if (displayOrBind.value.isAccountIpBinding) {
      // 如果是账户IP绑定，则需要将IP数也同步到网络数
      agicForm.value.networkCount = newCount;
    }
  }
);
// 带宽必须大于1
watch(
  () => agicForm.value.bandwidth,
  (newCount) => {
    const minCount = agicProduct.agic.productId
      ? Number(agicProduct.agic.bandwidth)
      : 1;
    if (newCount < minCount) {
      agicForm.value.bandwidth = minCount;
    }
  }
);

// 结算订单
const payData = ref({ onlinePayParam: "", orderNo: "", onlinePay: "" });
const IsWeChatPay = ref(false);
// 构建订单
const buildOrder = async () => {
  // 对表单信息进行有效性检查
  const data: any = {
    orderType: "PRODUCT",
    orderSource: [
      {
        sourceType: "CLOUD_NETWORK",
        chargeType: agicForm.value.chargeType,
        duration:
          agicForm.value.chargeType != "POSTPAID_BY_HOUR"
            ? durationValue.value.result.num
            : null, // 如果不是包年包月，默认为null
        durationUnit:
          agicForm.value.chargeType != "POSTPAID_BY_HOUR"
            ? durationValue.value.result.durationUnit
            : null, // 如果没有选择，默认为null
      },
    ],
    couponId: selectDisCount.value.id,
    networkProductId: id.value,
    networkCount: agicForm.value.networkCount,
    ipCount: agicForm.value.ipCount,
    bandwidth: agicForm.value.bandwidth,
    isAutoRenew: agicForm.value.isAutoRenew,
    mobile: orderInfo.value.mobile,
    agiCustomerName: orderInfo.value.agiCustomerName,
    email: agicEmailForm.value.join(","),
    remark: orderInfo.value.remark,
  };
  if (data.orderSource[0].durationUnit === "YEAR") {
    data.orderSource[0].chargeType = "POSTPAID_BY_YEAR";
  }
  // 根据agicProduct.agic.type分辨订单类型(没有：PRODUCT,renew:RENEW_PRODUCT,count:UPGRADE_PRODUCT)
  if (agicProduct.agic.editType === "renew") {
    data.orderType = "RENEW_PRODUCT";
    data.orderNo = agicProduct.agic.orderNo;
    data.networkPayValueId = agicProduct.agic.id;
  } else if (agicProduct.agic.editType === "count") {
    data.orderType = "UPGRADE_PRODUCT";
    data.networkCount =
      agicForm.value.networkCount - agicProduct.agic.networkCount;
    data.bandwidth = agicForm.value.bandwidth - agicProduct.agic.bandwidth;
    data.ipCount = agicForm.value.ipCount - agicProduct.agic.ipCount;
    // 邮箱也取networkCount插值后面部分
    data.email = agicEmailForm.value
      .slice(agicProduct.agic.networkCount)
      .join(",");
    data.orderNo = agicProduct.agic.orderNo;
    data.networkPayValueId = agicProduct.agic.id;
  }
  await buildAgicOrderInfoApi(data).then((res: any) => {
    total.value.payAmount = res.data.finalPayAmount;
  });
};
//

// 支付方式
// 支付卡片
const user = useUserInfo();
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
    bgSrc: "ali-pay",
    flag: "ALI_PAY",
    noShow: true,
  },
  {
    title: "微信支付",
    bgSrc: "wechat-pay",
    flag: "WECHAT_PAY",
    noShow: true,
  },
];
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
const total = ref({
  discountAmount: 0,
  payAmount: 0,
});
// 实付总金额（账单金额-优惠券金额）
const billAmount = computed(() => {
  let totalPrice = total.value.payAmount;
  if (isDisCount.value && selectDisCount.value.id) {
    totalPrice = totalPrice - selectDisCount.value.deductionAmount;
    totalPrice < 0 && (totalPrice = 0);
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
// 支付方法
const isPayLoading = ref(false);
const createOrder = debounce(
  async () => {
    if (!isAgree.value) {
      ElMessage.warning("请阅读并同意平台协议!");
      return;
    }
    const data: any = {
      orderType: "PRODUCT",
      orderSource: [
        {
          sourceType: "CLOUD_NETWORK",
          chargeType: agicForm.value.chargeType,
          duration:
            agicForm.value.chargeType != "POSTPAID_BY_HOUR"
              ? durationValue.value.result.num
              : null, // 如果不是包年包月，默认为null
          durationUnit:
            agicForm.value.chargeType != "POSTPAID_BY_HOUR"
              ? durationValue.value.result.durationUnit
              : null, // 如果没有选择，默认为null
        },
      ],
      couponId: selectDisCount.value.id,
      networkProductId: id.value,
      networkCount: agicForm.value.networkCount,
      ipCount: agicForm.value.ipCount,
      bandwidth: agicForm.value.bandwidth,
      mobile: orderInfo.value.mobile,
      agiCustomerName: orderInfo.value.agiCustomerName,
      isAutoRenew: agicForm.value.isAutoRenew,
      email: agicEmailForm.value.join(","),
      remark: orderInfo.value.remark,
      voucherPay: selectPay.value.includes("voucherPay") ? true : false,
      balancePay: selectPay.value.includes("balancePay") ? true : false,
      creditLinePay: selectPay.value.includes("creditLinePay") ? true : false,
      onlinePayType: selectPay.value.includes("ALI_PAY")
        ? "ALI_PAY"
        : selectPay.value.includes("WECHAT_PAY")
        ? "WECHAT_PAY"
        : null,
    };
    // 处理年
    if (data.orderSource[0].durationUnit === "YEAR") {
      data.orderSource[0].chargeType = "POSTPAID_BY_YEAR";
    }
    if (onlinePayAmount.value && !data.onlinePayType) {
      ElMessage.warning("请选择支付方式!");
      return;
    }
    // 检查手机号是否填写
    if (!orderInfo.value.mobile) {
      ElMessage.warning("请填写联系电话!");
      return;
    }
    // 邮箱合法校验（不能重复，不能为空，需要符合邮箱规则）
    let valid = true;
    // 邮箱校验正则
    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    let errorEmail = "";
    if (agicProduct.agic.networkCount) {
      let copyAgicEmailForm = JSON.parse(JSON.stringify(agicEmailForm.value));

      console.log(agicProduct.agic.networkCount);
      copyAgicEmailForm
        .slice(agicProduct.agic.networkCount)
        .forEach((item: string) => {
          if (!item || !emailRegex.test(item)) {
            valid = false;
            errorEmail = item;
            return;
          }
        });
    } else {
      agicEmailForm.value.forEach((item: string) => {
        if (!item || !emailRegex.test(item)) {
          valid = false;
          errorEmail = item;
          return;
        }
      });
    }
    if (!valid) {
      ElMessage.warning(
        `请检查邮箱${errorEmail ? errorEmail : ""}是否正确填写!`
      );
      return;
    }
    // 检测重复邮箱
    const emailSet = new Set(agicEmailForm.value);
    if (emailSet.size !== agicEmailForm.value.length) {
      ElMessage.warning("联系邮箱不能重复!");
      return;
    }
    if (agicProduct.agic.editType === "renew") {
      data.orderType = "RENEW_PRODUCT";
      data.orderNo = agicProduct.agic.orderNo;
      data.networkPayValueId = agicProduct.agic.id;
    } else if (agicProduct.agic.editType === "count") {
      data.orderType = "UPGRADE_PRODUCT";
      // networkCount、bandwidth、ipcount传差值
      data.networkCount =
        agicForm.value.networkCount - agicProduct.agic.networkCount;
      data.bandwidth = agicForm.value.bandwidth - agicProduct.agic.bandwidth;
      data.ipCount = agicForm.value.ipCount - agicProduct.agic.ipCount;
      // 邮箱也取networkCount插值后面部分
      data.email = agicEmailForm.value
        .slice(agicProduct.agic.networkCount)
        .join(",");
      data.orderNo = agicProduct.agic.orderNo;
      data.networkPayValueId = agicProduct.agic.id;
    }
    // 创建订单前对邮箱是否在飞连创建过做校验
    let emails = data.email.split(",");
    let isHave = false;

    isPayLoading.value = true;
    if (!agicProduct.agic.editType || agicProduct.agic.editType != "renew") {
      await checkEmailExistApi({ emails }).then((res: any) => {
        if (res.code === 200) {
          if (res.data.length > 0) {
            isPayLoading.value = false;
            ElMessage.error(res.data + " 邮箱已存在,请重新填写邮箱");
            isHave = true;
            return;
          }
        }
      });
    }
    if (isHave) {
      return;
    }
    // 检查ip数目是否足够
    const ipData = {
      productId: id.value,
      ipNumber: agicForm.value.ipCount,
    };
    if (agicProduct.agic.productId) {
      ipData.ipNumber -= agicProduct.agic.ipCount!;
    }
    await checkIpNumApi(ipData).then((res) => {
      if (res.data == false) {
        isPayLoading.value = false;
        ElMessage.error("资源已用完，请耐心等待，后台尽快为您补充资源。");
        isHave = true;
        return;
      }
    });
    if (isHave) {
      isPayLoading.value = false;
      return;
    }
    // 创建订单
    createAgicOrderApi(data)
      .then((res: any) => {
        isPayLoading.value = false;
        if (res.code === 200) {
          // if (data.orderSource[0].chargeType === 'POSTPAID_BY_HOUR') {
          //   successDialog.value = true
          // } else {
          payData.value = res.data;
          console.log(payData.value);
          if (payData.value.onlinePay === "WECHAT_PAY")
            return (IsWeChatPay.value = true);
          else if (payData.value.onlinePay === "ALI_PAY") {
            return aliPay(res.data.orderNo);
            // return aliPay(res.data.onlinePayParam);
          } else successDialog.value = true;
          // }

          // 支付成功
          // successDialog.value = true
        }
      })
      .finally(() => {
        isPayLoading.value = false;
      });
  },
  1000,
  { leading: true, trailing: false }
);
const wechatPrice = computed(() => Number(onlinePayAmount.value).toFixed(2));
const successDialog = ref(false);

const openDocs = () => {
  let a = window.location.origin + "/#/";
  a += "docsView/ProductAgreement";
  window.open(a, "_blank");
};

const toUrl = (url: string) => {
  successDialog.value = false;
  toPage(url);
};
const finishPay = () => {
  IsWeChatPay.value = false;
  successDialog.value = true;
  user.getUserInfo();
};

// 关于付款条
const isFloatingFooter = ref(true);
const content = ref(null);

const handleScroll = () => {
  const contentRect = content.value.getBoundingClientRect();
  const footerHeight = 49; // 你footer实际高度
  const windowHeight = window.innerHeight;

  if (contentRect.bottom <= windowHeight - footerHeight) {
    // 内容底部已可见，锁定到底部
    isFloatingFooter.value = false;
  } else {
    // 内容没有滚到底，footer悬浮
    isFloatingFooter.value = true;
  }
};
watch(
  () => billAmount.value,
  () => {
    console.log(
      "selectDisCount",
      !!selectDisCount.value.id,
      selectDisCount.value
    );

    if (!!selectDisCount.value.id) return;
    getCoupon();
  },
  { immediate: true }
);

// 同时监听账户数 IP数 带宽 时长 进行订单构建
watch(
  () => [
    agicForm.value.networkCount,
    agicForm.value.ipCount,
    agicForm.value.bandwidth,
    agicForm.value.chargeType,
    durationValue.value.result,
  ],
  () => {
    selectDisCount.value = { id: "" };
    if (agicProduct.agic.editType == "count") {
      // 如果差值都是0就不构建订单
      if (
        agicForm.value.networkCount - agicProduct.agic.networkCount === 0 &&
        agicForm.value.ipCount - agicProduct.agic.ipCount === 0 &&
        agicForm.value.bandwidth - agicProduct.agic.bandwidth === 0
      ) {
        buildOrder();
        return;
      }
      if (
        agicForm.value.networkCount - agicProduct.agic.networkCount < 0 ||
        agicForm.value.ipCount - agicProduct.agic.ipCount < 0 ||
        agicForm.value.bandwidth - agicProduct.agic.bandwidth < 0
      ) {
        return;
      }
    }
    buildOrder();
  },
  { immediate: true, deep: true }
);

onMounted(() => {
  window.addEventListener("scroll", handleScroll);
});

onBeforeUnmount(() => {
  window.removeEventListener("scroll", handleScroll);
});
onUnmounted(() => {
  agicProduct.resetAgicDetail();
  targetNetworkProduction.clearProductionDetail();
});
</script>
<style lang="scss" scoped>
.content {
  padding-top: 128px;
  padding-left: 229px;

  .product {
    width: 1470px;
    background: #ffffff;
    box-shadow: 0px 0px 16px 1px rgba(196, 213, 255, 0.5);
    border-radius: 16px 16px 16px 16px;
    padding: 30px 36px;
    position: relative;
    .title {
      width: 196px;
      height: 40px;
      font-family: PingFang SC, PingFang SC;
      font-weight: bold;
      font-size: 28px;
      // 不换行
      white-space: nowrap;
    }

    .pay-list {
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      gap: 20px;
    }

    .footer {
      padding: 20px;
      height: 89px;
      background: #f7f8fb;
      border-radius: 10px 10px 10px 10px;
      display: flex;
      align-items: center;
      justify-content: space-between;

      .label {
        font-weight: 400;
        font-size: 18px;
        color: #666666;
      }

      .detail {
        padding-left: 8px;
        font-weight: 500;
        font-size: 18px;
        color: #000000;
      }

      .price {
        display: flex;
        align-items: center;
        gap: 10px;
        font-size: 16px;
        color: #666666;
      }
    }
    .fixed {
      position: fixed;
      bottom: 0;
      left: 262px;
      width: 71%;
      padding: 30px;
      box-sizing: border-box;
    }
  }
}
.month {
  display: flex;
  align-items: center;
  gap: 30px;
  .swi {
    // 不换行
    white-space: nowrap;
    display: flex;
    align-items: center;
    gap: 10px;
  }
}
.mask {
  position: fixed;
  /* 固定定位，确保遮罩层覆盖整个页面 */
  top: 0;
  /* 从页面顶部开始 */
  left: 0;
  /* 从页面左边开始 */
  width: 100vw;
  /* 宽度占满整个视口 */
  height: 100vh;
  /* 高度占满整个视口 */
  background-color: rgba(0, 0, 0, 0.5);
  /* 半透明黑色背景 */
  z-index: 10;
  /* 确保遮罩层在最上层 */
}

.owe-tip {
  font-size: 20px;
  background-color: #e4a2a86e;
  color: rgb(255, 65, 81);
  display: flex;
  justify-content: flex-start;
  align-items: flex-start;
  height: 100%;
  padding: 0 10px 0 10px;
  border-radius: 4px;
  margin-top: 20px;
  margin-left: -30px;
  width: 520px;
}

.success-dialog {
  .success-content {
    padding-top: 10px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    gap: 22px;

    img {
      width: 72px;
      height: 72px;
    }

    .title {
      font-weight: 500;
      font-size: 20px;
    }

    .value {
      font-weight: 400;
      font-size: 16px;
    }

    .btns {
      .el-button {
        width: 118px;
        height: 42px;
        border-radius: 4px 4px 4px 4px;
      }
    }
  }
}
:deep(.el-form-item__label) {
  padding: 0 20px 0 0;
  margin-left: 30px;
  font-size: 18px;
  color: #00000094;
  text-align: justify;
  text-align-last: justify;
  display: block;
  width: 100%;
  white-space: nowrap;
  width: 90px !important;
}
</style>
