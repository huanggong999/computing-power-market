/** 账户中心 */
<template>
  <div class="card position-relative">
    <div class="top-part">
      <div class="info">
        <div class="title">账号信息</div>
        <div class="info-card">
          <div class="portrait">
            <img
              :src="
                userInfo.$state.avatar
                  ? userInfo.$state.avatar
                  : '../../../assets/images/sss.jpg'
              "
              alt=""
            />
          </div>
          <div class="info-detail">
            <div class="name">{{ userInfo.customerName }}</div>
            <div class="create-time">
              注册时间: {{ userInfo.registerTime }}
              <!-- 个人用户  certStatus  = 1 未认证 2 认证中 3 已认证 4 认证失败 -->
              <!-- {{ authenticationIcon[userInfo.certStatus!] }} -->
              <div
                @click="toAuth"
                class="flag"
                :style="{
                    color: userInfo.certStatus === 3  &&  userInfo.type === 2 ? '#3972FD': authenticationIcon[userInfo.certStatus!].color,
                    backgroundColor:userInfo.certStatus === 3  &&  userInfo.type === 2 ? '#E6EEFF': authenticationIcon[userInfo.certStatus!].bgColor,
                  }"
              >
                <img
                  src="../../../assets/icon/not-authentication-icon.png"
                  v-if="userInfo.certStatus === 1"
                />
                <img
                  src="../../../assets/icon/authenticating-icon.png"
                  v-if="userInfo.certStatus === 2"
                />
                <img
                  src="../../../assets/icon/authentication-icon.png"
                  v-if="userInfo.certStatus === 3 && userInfo.type === 2"
                />
                <img
                  src="../../../assets/icon/personalAuthentication-icon.png"
                  v-if="userInfo.certStatus === 3 && userInfo.type === 1"
                />
                <img
                  src="../../../assets/icon/err-authentication-icon.png"
                  v-if="userInfo.certStatus === 4"
                />

                <div>
                  {{ enumType("certStatusEnum", userInfo.certStatus!) }}
                </div>
              </div>

              <!-- <div
                class="flag not-authentication"
                v-if="userInfo.type == 1"
                @click="toAuth"
              >
                <img src="../../../assets/icon/not-authentication-icon.png" />
                未认证 >
              </div>
              <div
                class="flag authentication"
                v-if="userInfo.type == 2"
                @click="toAuth"
              >
                <img src="../../../assets/icon/authentication-icon.png" />
                实名认证
              </div> -->
            </div>
          </div>
        </div>
        <div class="psw-phone flx-align-center">
          <div class="item">
            <div class="label">密码</div>
            <div class="value">**********</div>
          </div>
          <div class="item">
            <div class="label">安全手机</div>
            <div class="value">
              {{ userInfo.phone ? userInfo.phone : "--" }}
            </div>
            <el-icon class="edit-btn" @click="changePhoneVisible = true">
              <Edit />
            </el-icon>
          </div>
        </div>
      </div>
      <div class="top">
        <div class="account">
          <div class="tip">
            账户余额
            <el-icon class="ml5 mt5">
              <QuestionFilled />
            </el-icon>
          </div>
          <div class="price account-price mt10">
            {{ userInfo.totalBalance }}
          </div>
        </div>
        <div class="bottom flx-justify-between mt30">
          <div class="flex">
            <div>
              <div class="tip">现金余额</div>
              <div class="price mt14">{{ userInfo.balance }}</div>
            </div>
            <el-divider direction="vertical" class="divider" height="50px" />
            <div>
              <div class="tip">欠费余额</div>
              <div class="price mt14">{{ userInfo.arrearsAmount }}</div>
            </div>
          </div>
          <el-button
            round
            color="#FBA201"
            style="color: #fff"
            @click="() => toPage('/rechargeCenter')"
          >
            立即充值
          </el-button>
        </div>
      </div>
    </div>
    <div class="mt20 mb20 flx-justify-between">
      <ConsoleCard
        v-for="(item, index) in vouchers"
        :key="index"
        :data="item"
        type="vouchers"
        :letter="item.title === '代金券' ? '元' : undefined"
      />
    </div>
    <ProTable
      type="none"
      :columns="columns"
      :tableData="tableData"
      :page-data="pageData"
      :get-list="getList"
      :IsRefresh="false"
    >
      <template #tableHeader>
        <el-radio-group v-model="amountType" @change="changeAmountType">
          <el-radio-button value="BALANCE">账户余额明细</el-radio-button>
          <el-radio-button value="VOUCHER">代金券明细</el-radio-button>
          <el-radio-button value="CREDIT">授信额明细</el-radio-button>
        </el-radio-group>
      </template>
    </ProTable>
  </div>
  <el-dialog
    v-model="changePhoneVisible"
    title="修改安全手机"
    center
    width="30%"
    :modal-append-to-body="true"
  >
    <ProForm
      ref="proFormRef"
      v-model="changePhoneParams"
      :formColumns="changePhoneCol"
      :label-width="120"
      class="mt20"
    >
    </ProForm>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="changePhoneVisible = false">取消</el-button>
        <el-button type="primary" @click="changePhone"> 确定 </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts" name="AccountCenter">
import { getAmountListAPI, updateUserPhoneAPI } from "@/api/user";
import { useTable } from "@/hooks/useTable";
import { useUserInfo } from "@/store";
import { toPage } from "@/utils";
import { enumType } from "@/utils/Enum";
import ConsoleCard from "@/view/Console/components/ConsoleCard.vue";
import { ElMessage } from "element-plus";

const userInfo = useUserInfo();
userInfo.getUserInfo();
const amountType = ref("BALANCE");
const { tableData, pageData, getList, searchFn, searchParam } = useTable({
  requestApi: getAmountListAPI,
  requestAuto: false,
});
searchParam.value.amountType = amountType.value;
getList();

const authenticationIcon = [
  {},
  { bgColor: "#FFF3DC", color: "#F0A50F" },
  { bgColor: "#FFF3DC", color: "#F0A50F" },
  {},
  { bgColor: "#FFF3DC", color: "#FF4151" },
];

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
const columns: ColumnProps[] = [
  { prop: "orderNo", label: "业务交易单号" },
  {
    prop: "transactionType",
    label: "交易类型",
    value: (row: any) => enumType("transactionTypeEnum", row.transactionType),
  },
  { prop: "createTime", label: "交易时间" },
  { prop: "amount", label: "变动金额" },
  { prop: "afterAmount", label: "现金余额" },
];
const changeAmountType = () => {
  searchParam.value.amountType = amountType.value;
  searchFn();
};

// 前往实名认证页面
const toAuth = () => {
  toPage("/authenticationCenter");
};

// 修改手机号
const changePhoneVisible = ref(false);
const changePhoneParams = ref({ phone: userInfo.$state.phone, uid: "" });
const changePhoneCol: IFormColumnsProps[] = [
  {
    prop: "phone",
    label: "手机号",
    el: "input",
  },
  {
    prop: "code",
    el: "code",
    authUid: "uid",
    codePhone: "phone",
    codeType: "SMS",
    tmsg: "4",
  },
];
const changePhone = async () => {
  await updateUserPhoneAPI(changePhoneParams.value);
  changePhoneVisible.value = false;
  ElMessage.success("修改成功");
  userInfo.getUserInfo();
};
watch(
  () => changePhoneParams.value.phone,
  () => {
    changePhoneParams.value.uid = "";
  }
);
</script>
<style lang="scss" scoped>
.top-part {
  display: flex;
  justify-content: space-between;
  gap: 20px;

  .info {
    height: 270px;
    flex: 1;
    border-radius: 10px;
    padding: 32px 40px;
    background: linear-gradient(180deg, #e6eeff 0%, #ffffff 100%);
    border-radius: 10px 10px 10px 10px;
    border: 1px solid #e5e5e5;
    display: flex;
    flex-direction: column;
    gap: 20px;

    .title {
      font-weight: 400;
      font-size: 20px;
      color: #666666;
    }

    .info-card {
      display: flex;
      gap: 20px;

      .portrait {
        height: 81px;
        width: 81px;
        border-radius: 50%;

        img {
          height: 81px;
          width: 81px;
          border-radius: 50%;
        }
      }

      .info-detail {
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        padding: 10px 0px;
        gap: 12px;

        .name {
          font-weight: bold;
          font-size: 24px;
          color: #333333;
        }

        .create-time {
          font-weight: 400;
          font-size: 16px;
          color: #83889d;
          display: flex;
          align-items: center;
          gap: 12px;

          .flag {
            width: 102px;
            height: 26px;
            border-radius: 4px 4px 4px 4px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 5px 7px;

            img {
              height: 16px;
              width: 16px;
            }

            &:hover {
              cursor: pointer;
            }
          }

          .authenticationStatus {
            font-size: 16px;
            // color: #3972fd;
            background: #e6eeff;
          }

          .authentication {
            background: #e6eeff;

            font-weight: 400;
            font-size: 16px;
            color: #3972fd;
          }

          .not-authentication {
            background: #fff3dc;
            font-weight: 400;
            font-size: 16px;
            color: #f0a50f;
          }
        }
      }
    }

    .psw-phone {
      display: flex;
      gap: 20px;

      .edit-btn {
        &:hover {
          cursor: pointer;
        }
      }

      .item {
        flex: 1;
        width: 320px;
        height: 56px;
        padding: 0 18px;
        background: #ffffff;
        border-radius: 10px 10px 10px 10px;
        border: 1px solid #d9d9d9;
        display: flex;
        justify-content: space-between;
        align-items: center;

        .label {
          font-weight: 400;
          font-size: 16px;
          color: #83889d;
        }

        .value {
          font-weight: 400;
          font-size: 16px;
          color: #000000;
        }
      }
    }
  }

  .top {
    height: 270px;
    flex: 1;
    border-radius: 10px;
    padding: 34px 40px;
    background: url("https://ai-cloud-system.tos-cn-beijing.volces.com/imgs/userSideImage/accountBalanceBg.png")
      no-repeat;
    background-size: 100% 100%;

    .tip {
      font-size: 20px;
      color: #d7e2f6;
      font-weight: 400;
    }

    .price {
      color: #ffffff;
      font-size: 32px;
    }

    .account-price {
      font-size: 44px;
    }

    .divider {
      height: 60px;
      margin: 10px 50px;
    }
  }
}
</style>
