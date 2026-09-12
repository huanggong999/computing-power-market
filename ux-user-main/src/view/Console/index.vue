/** 主控台 */
<template>
  <div class="table-box">
    <div class="top-card">
      <TipText
        class="alert"
        content="不使用时请及时停用，以免产生额外费用"
        text-color="#ff4151"
        before-color="#ff4151"
        fontSize="18"
      />
      <div class="invite-card">
        <img src="../../assets/images/invite-card.png" />
        <el-button class="btn" type="primary" plain @click="toExtend"
          >立即邀请</el-button
        >
      </div>
    </div>

    <el-row class="mt20">
      <el-col :span="19" class="pr20">
        <div class="card">
          <TipText class="fwb" content="资源概览">
            <template #end>
              <el-button type="primary" @click="toPage('/cloud/createInstance')"
                >创建服务器</el-button
              >
              <el-button
                type="primary"
                @click="
                  toPage(
                    `/cloud/createInstance?ecsId=${productList[0].id}&productType=2&regionsZones=${productList[0].regionsZones}`
                  )
                "
              >
                创建智选服务
              </el-button>
            </template>
          </TipText>
          <div class="mt28 flx-justify-between">
            <ConsoleCard
              v-for="(item, index) in resourceOverview"
              :key="index"
              :data="item"
              type="resourceOverview"
            />
          </div>
        </div>
        <div class="card mt20">
          <TipText class="fwb" content="个人账户" />
          <div class="mt28 flx-justify-between">
            <ConsoleCard
              v-for="(item, index) in account"
              :key="index"
              type="account"
              :data="item"
            />
          </div>
          <div class="mt20 mb20 flx-justify-between">
            <ConsoleCard
              v-for="(item, index) in vouchers"
              :key="index"
              :data="item"
              type="vouchers"
            />
          </div>
        </div>
      </el-col>

      <el-col :span="5" class="pr10">
        <div class="helpCenter">
          <div class="top flx-justify-between">
            <TipText class="fwb" content="帮助中心" />
            <el-icon class="cup">
              <ArrowRightBold />
            </el-icon>
          </div>
          <div
            class="text mt35 flex"
            v-for="(item, index) in helpCenterData"
            :key="index"
          >
            <div class="icon mr8" :class="item.icon" />
            <div class="txt cup">{{ item.title }}</div>
          </div>
        </div>
        <div class="accountCenter mt10">
          <el-button
            class="btn"
            type="primary"
            round
            @click="toPage('/accountCenter')"
          >
            点击进入
          </el-button>
        </div>
        <div class="service mt20">
          <img
            src="../../assets/images/tech-service.png"
            alt="QRCode"
            class="code"
          />
          <p>微信扫码，联系客服</p>
        </div>
      </el-col>
    </el-row>
    <div v-if="partnerDialog" class="partnerDialog">
      <div class="title mb20">推广申请</div>
      <div class="gray-card">
        欢迎您参与逸雲数智合伙人生态合作！<br />
        为参与逸雲数智合伙人生态合作活动（以下简称：本活动），表示您认可：“诚信、共赢、自由”的理念，并以此原则共同推动AGI。<br />
        请您务必审慎阅读、充分理解各条款内容，特别是免除或者限制责任的条款，以及开通或使用某项服务的单独协议、规则。限制、免责条款提示您注意。<br />
        除非您已阅读并接受本协议及相关协议、规则等所有条款，否则，您可以选择不参与本活动。您通过网络页面点击确认、邮件确认、书面签署或其他逸雲数智认可的方式选择接受本协议，或者您参与本活动，即视为您已阅读并同意上述协议、规则等的约束。<br />
        您有违反本协议的任何行为时，逸雲数智有权依照违反情况，随时单方限制、中止或终止您参与本活动，并有权追究您的相关责任。<br />
        本协议由您与逸云数智科技（深圳）有限责任公司（简称“逸雲数智”）签订。<br />
        1．术语含义如无特别说明，下列术语在本协议中的含义为：<br />
        1.1生态合作人：所有参与逸雲数智生态合伙人合作的主体，包括但不限于企业和个人用户，简称为“您”。成功申请参与本活动的服务大使均可获得一个服务链接，服务链接将作为计算服务费用的有效标识。<br />
        1.2服务内容：逸云数智官网活动页面公布的拟推广的全系列产品。<br />
        1.3客户：通过生态合伙人的服务链接进入逸雲数智官网下达有效订单并在指定期限内有现金支付的主体。<br />
        1.4有效订单：客户关联成功并购买服务产品，通过服务链接完成该产品的购买并支付的订单为有效订单（如有变更以合作活动页规则的定义为准）。<br />
        1.5服务费结算比例：生态合伙人享有的服务费结算比例。逸雲数智有权根据运营状况单方面的自主调整服务费结算比例。<br />
      </div>
      <ProForm
        ref="proFormRef"
        v-model="formParams"
        :formColumns="formCol"
        :labelWidth="150"
        class="mt30 flx-align-center form-group"
      ></ProForm>
      <div class="footer flx-align-center">
        <el-checkbox v-model="isConfirm"
          ><span class="check-text"
            >我已阅读并同意<span style="color: #3972fd" @click="openDocs"
              >《AI云平台隐私协议》</span
            ></span
          ></el-checkbox
        >
        <el-button type="primary" class="mt16 mb20" @click="confirmForm"
          >提交</el-button
        >
      </div>
    </div>
    <el-dialog
      width="300"
      v-model="successDialog"
      align-center
      class="success-dialog"
    >
      <div class="content">
        <img src="../../assets/images/pay-success.png" alt="" />
        <div class="title">提交成功</div>
        <div class="value">资料正在审核中,请耐心等待</div>
        <div class="btns">
          <el-button @click="successDialog = false">返回</el-button>
        </div>
      </div>
    </el-dialog>
    <div class="mask" v-if="partnerDialog" @click="partnerDialog = false"></div>
  </div>
</template>

<script setup lang="ts" name="Console">
import { toPage } from "@/utils";
import ConsoleCard from "./components/ConsoleCard.vue";
import { useUserInfo } from "@/store";
import { getHomeDataApi, getResourceDataApi } from "@/api/console";
import { getUserCouponListAPI } from "@/api/user";
import { getInvoiceAmountListApi } from "@/api/invoice";
import { applyExtendApi, getExtendInfoApi } from "@/api/partner";
import { ElMessage } from "element-plus";
import { getToken } from "@/utils/auth";
import { initDynamicRouter } from "@/routes/dynamicRouter";
import { LOGIN_URL } from "@/config";
import { getComputedListApi } from "@/api/instance";
import { constant } from "lodash";

const helpCenterData = [
  { title: "快速入门", icon: "quickStartIcon" },
  { title: "常见问题", icon: "questionIcon" },
];
const userInfo = useUserInfo();

const consoleForm = ref({
  ecsNumber: "",
  containerNumber: "",
  objectStorageNumber: "",
  imgNumber: "",
});
onMounted(() => {
  getUserCouponListAPI({ pageNo: 1, pageSize: 10 }, {}).then((res) => {
    console.log(res);
    userInfo.$state.couponNum = res.data.dataTotal;
  });
});
// 获取资源概览数据
const getConsoleDataApi = async () => {
  const { data } = await getHomeDataApi();
  consoleForm.value = data;
};
getConsoleDataApi();

const cloudServer = ref(0);
// 资源概览
const resourceOverview = computed(() => ({
  cloudServer: {
    title: "算力服务器",
    number: cloudServer.value,
    bgSrc: "cloudServerCardBg",
    btnClick: () => toPage("/cloud/resource"),
  },
  containerCluster: {
    title: "容器集群",
    number: consoleForm.value.containerNumber,
    bgSrc: "containerClusterCardBg",
    btnClick: () => toPage("/containerList"),
  },
  objectStorage: {
    title: "对象存储",
    number: consoleForm.value.objectStorageNumber,
    bgSrc: "objectStorageCardBg",
    btnClick: () => toPage("/objectStorage"),
  },
  mirrorWarehouse: {
    title: "镜像仓库",
    number: consoleForm.value.imgNumber,
    bgSrc: "mirrorWarehouseCardBg",
    btnClick: () => toPage("/mirrorWarehouse"),
  },
}));
// 个人账户
const account = ref({
  balance: {
    title: "账户余额",
    number: userInfo.totalBalance,
    bgColor: true,
    btnText: "立即充值",
    btnBg: "#FBA201",
    btnColor: "#FFF",
    IsCup: true,
    btnClick: () => toPage("/rechargeCenter"),
  },
  invoiceAmount: {
    title: "可开发票金额",
    number: userInfo.amount,
    bgSrc: "invoiceBg",
    bgColor: false,
    btnText: "开发票",
    btnBg: "#D7E2F6",
    btnColor: "#3972FD",
    IsCup: true,
    btnClick: () => toPage("/invoiceManagement"),
  },
});
// 优惠券
const vouchers = ref({
  balance: {
    title: "优惠券",
    number: userInfo.couponNum,
    bgSrc: "discountCouponBg",
    btnClick: () => toPage("/discountCouponList"),
  },
  balance1: {
    title: "代金券",
    number: userInfo.voucherBalance,
    bgSrc: "voucherBg",
    letter: "元",
  },
  balance2: {
    title: "授信额",
    number: userInfo.creditAmount,
    bgSrc: "CreditLineBg",
    letter: "元",
    btnClick: () => toPage("/contractManagement?type=credit"),
  },
});

// 邀请大使
const successDialog = ref(false);
const partnerDialog = ref(false);
const proFormRef = ref(null);
const formParams = ref({ name: "", phone: "" });
const isConfirm = ref(false);
const formCol: IFormColumnsProps[] = [
  {
    placeholder: "请输入您的真实姓名",
    prop: "name",
    label: "真实姓名",
    el: "input",
  },
  {
    placeholder: "请输入您的联系电话",
    prop: "phone",
    label: "联系电话",
    el: "input",
  },
];
const toExtend = () => {
  // 检查申请状态
  getExtendInfoApi().then(async ({ data }: any) => {
    if (!data.status) {
      partnerDialog.value = true;
    } else if (data.status == 1) {
      ElMessage.warning("当前账号已申请，请耐心等待。");
    } else if (data.status == 2) {
      await initDynamicRouter();
      toPage("/popularize/customerManagement");
    } else {
      ElMessage.error(
        `申请失败。失败理由:${data.verifyRemark}，您可重新申请。`
      );
      partnerDialog.value = true;
    }
  });
};
const confirmForm = () => {
  if (!isConfirm.value) {
    ElMessage.error("请先同意协议");
    return;
  }
  if (formParams.value.name && formParams.value.phone) {
    let params = { ...formParams.value, userId: userInfo.userId };
    applyExtendApi(params).then((res) => {
      if (res.code === 200) {
        ElMessage.success("提交成功");
        successDialog.value = true;
      }
    });
  } else {
    ElMessage.error("请填写完整信息");
  }
};
watchEffect(() => {
  if (partnerDialog.value) {
    document.body.style.overflow = "hidden"; // 禁用滚动
  } else {
    document.body.style.overflow = ""; // 恢复滚动
  }
});
watch(
  () => successDialog.value,
  (newVal) => {
    if (!newVal) {
      partnerDialog.value = false;
    }
  }
);
const openDocs = () => {
  let a = window.location.origin + "/#/";
  a += "docsView/privacyPolicy";
  window.open(a, "_blank");
};
const initPage = () => {
  getInvoiceAmountListApi().then((res) => {
    userInfo.amount = res.data.amount;
  });
  getResourceDataApi().then(({ data }) => {
    cloudServer.value =
      Number(data.ecsRunningNumber) +
      Number(data.ecsStoppedNumber) +
      Number(data.expiringNumber);
  });
};
initPage();
const productList = ref<TKeyValue[]>([]);
const getProduct = async () => {
  const res = await getComputedListApi({ productType: 2 });
  productList.value = res.data.list;
};
getProduct();
</script>
<style lang="scss" scoped>
@import "./index.scss";
</style>
