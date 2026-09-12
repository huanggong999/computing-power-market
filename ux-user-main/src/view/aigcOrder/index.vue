<template>
  <div class="top-btn flx-align-center">
    <el-radio-group size="large" v-model="tableRadio" @change="changeMonth">
      <!-- <el-radio-button label="咨询记录" value="record" /> -->
      <el-radio-button label="订单记录" value="order" />
    </el-radio-group>
    <el-button
      style="z-index: 100"
      type="primary"
      plain
      @click="toPage('/networkProduct')"
      >新增产品</el-button
    >
  </div>

  <div class="table-box card position-relative" v-if="tableRadio === 'order'">
    <div class="flx-align-center">
      <SearchForm
        :columns="orderSearchCol"
        :searchParam="orderSearchParam"
        :searchFn="orderSearchFn"
        :resetFn="orderResetFn"
      >
      </SearchForm>
    </div>

    <ProTable
      type="none"
      :columns="orderCol"
      :tableData="orderTableData"
      :pageData="orderPageData"
      :IsRefresh="false"
      :get-list="orderGetList"
    >
      <template #agiCustomerName="row">
        <el-button type="primary" link @click="toDetail(row)">
          {{ row.agiCustomerName }}
        </el-button>
      </template>
      <template #actualStatus="row">
        <el-tag
          :type="enumType('actualStatusTag', row.actualStatus)"
          v-if="row.actualStatus != 2"
        >
          {{ enumType("actualStatusEnum", row.actualStatus) }}
        </el-tag>
        <el-tag
          color="#E6A23C"
          type="warning"
          effect="dark"
          v-else-if="row.actualStatus == 2 && row.expireStatus != 8"
        >
          {{ enumType("expireStatusEnum", row.expireStatus) }}
        </el-tag>
        <el-tag
          type="success"
          v-else-if="row.actualStatus == 2 && row.expireStatus == 8"
        >
          {{ enumType("expireStatusEnum", row.expireStatus) }}
        </el-tag>
      </template>
      <!-- <template #orderStatus="row">
        <el-tag :type="enumType('orderStatusTag', row.orderStatus)">
          {{ enumType('orderStatusEnum', row.orderStatus) }}
        </el-tag>
      </template> -->
      <template #operation="row">
        <!-- <span v-if="row.actualStatus == '2'">
          <el-button link type="warning">去支付</el-button>
          <el-divider direction="vertical" />
        </span> -->
        <el-button link type="primary" @click="toDetail(row)"> 详情 </el-button>
        <el-button
          link
          type="warning"
          @click="toRenew(row)"
          v-if="row.chargeType != 'POSTPAID_BY_HOUR'"
        >
          续费
        </el-button>
        <el-button link type="info" @click="editProductName(row)">
          修改名称
        </el-button>
      </template>
    </ProTable>
    <!-- 详情弹窗 -->
    <el-dialog title="账号信息" v-model="detailDialog" width="1000">
      <div class="dialog-content">
        <div
          class="footer"
          v-if="
            detailInfo.actualStatus != 1 &&
            detailInfo.actualStatus != 3 &&
            detailInfo.actualStatus != 5
          "
        >
          <!-- <el-button type="primary" @click="btnHandler('account')">增加账户</el-button>
          <el-button type="primary" @click="btnHandler('ip')">增加IP</el-button>
          <el-button type="primary" @click="btnHandler('bandwidth')">带宽扩容</el-button> -->

          <!-- v-if="detailInfo.chargeType != 'POSTPAID_BY_HOUR'" -->
          <el-button type="primary" @click="btnHandler('renew', detailInfo)"
            >续费</el-button
          >
          <el-button type="primary" @click="btnHandler('edit', detailInfo)"
            >变更套餐</el-button
          >
          <el-popconfirm
            confirm-button-text="确认"
            cancel-button-text="取消"
            :icon="InfoFilled"
            icon-color="#626AEF"
            title="是否确认上线?"
            v-if="detailInfo.actualStatus == 4"
            @confirm="btnHandler('start', detailInfo)"
          >
            <template #reference>
              <el-button type="success">上线</el-button>
            </template>
          </el-popconfirm>
          <el-popconfirm
            confirm-button-text="确认"
            cancel-button-text="取消"
            :icon="InfoFilled"
            icon-color="#626AEF"
            title="是否确认暂停?"
            v-if="detailInfo.actualStatus == 2"
            @confirm="btnHandler('stop', detailInfo)"
          >
            <template #reference>
              <el-button type="warning">暂停</el-button>
            </template>
          </el-popconfirm>
          <el-popconfirm
            confirm-button-text="确认"
            cancel-button-text="取消"
            :icon="InfoFilled"
            icon-color="#626AEF"
            title="是否确认撤线?"
            v-if="detailInfo.actualStatus == 2 || detailInfo.actualStatus == 4"
            @confirm="btnHandler('cancel', detailInfo)"
          >
            <template #reference>
              <el-button type="danger">撤线</el-button>
            </template>
          </el-popconfirm>
        </div>
        <div class="flx-3">
          <div class="info-item">
            <div class="label">产品名称</div>
            <div class="value">{{ detailInfo.productName }}</div>
          </div>
          <div class="info-item">
            <div class="label">付费方式</div>
            <div class="value">
              {{
                detailInfo.chargeType == "POSTPAID_BY_MONTH"
                  ? "包月"
                  : detailInfo.chargeType == "POSTPAID_BY_YEAR"
                  ? "包年"
                  : "按天计费"
              }}
            </div>
          </div>
          <div class="info-item">
            <div class="label">运行状态</div>
            <div class="value">
              <el-tag
                :type="enumType('actualStatusTag', detailInfo.actualStatus)"
              >
                {{ enumType("actualStatusEnum", detailInfo.actualStatus) }}
              </el-tag>
            </div>
          </div>
        </div>
        <div class="flx-3 mt36">
          <div class="info-item">
            <div class="label">开始时间</div>
            <div class="value">
              {{
                detailInfo.actualAgiOpenTime
                  ? detailInfo.actualAgiOpenTime
                  : "--"
              }}
            </div>
          </div>
          <div class="info-item">
            <div class="label">到期时间</div>
            <div class="value">
              {{
                detailInfo.actualAgiExpireTime
                  ? detailInfo.actualAgiExpireTime
                  : "--"
              }}
            </div>
          </div>
          <!-- 按天计费不展示 -->
          <div
            class="info-item"
            v-if="detailInfo.chargeType != 'POSTPAID_BY_HOUR'"
          >
            <div class="label">自动续费</div>
            <div class="value">
              <el-switch
                v-model="detailInfo.isAutoRenew"
                class="ml-2"
                :loading="autoLoading"
                :active-value="1"
                :inactive-value="0"
                @change="beforeChangeAutoRenew"
                style="
                  --el-switch-on-color: #13ce66;
                  --el-switch-off-color: #1111;
                "
              />
            </div>
          </div>
        </div>
        <div class="flx-1">
          <div class="info-item">
            <div class="label">账户数</div>
            <div class="value">{{ detailInfo.networkCount }}</div>
          </div>
        </div>
        <div class="flx-2" v-if="detailInfo.isIpDisplay !== 0">
          <div class="info-item">
            <div class="label">IP地址数</div>
            <div class="value">{{ detailInfo.ipCount }}</div>
          </div>
        </div>
        <div class="flx-1" v-if="detailInfo.isBandwidthDisplay !== 0">
          <div class="info-item">
            <div class="label">带宽</div>
            <div class="value">{{ detailInfo.bandwidth }}</div>
          </div>
        </div>
        <div class="flx-1">
          <div class="info-item">
            <div class="label">联系电话</div>
            <div class="value">{{ detailInfo.mobile }}</div>
          </div>
        </div>
        <div class="flx-1">
          <div class="info-item">
            <div class="label">用户账号</div>
            <div class="value" v-if="!detailInfo.userPwdList">--</div>
            <div class="psw-list" v-else>
              <div
                class="psw-item"
                v-for="(item, index) in detailInfo.userPwdList"
                :key="index"
              >
                <div class="info">
                  <div class="email">
                    <span style="color: black">登录邮箱：</span>{{ item.email
                    }}<img
                      id="copyicon"
                      src="../../assets/icon/copy-icon.png"
                      @click="copyText(item.email)"
                    />
                  </div>
                  <div class="text">
                    初始密码：{{ item.pwd
                    }}<img
                      id="copyicon"
                      src="../../assets/icon/copy-icon.png"
                      @click="copyText(item.pwd)"
                    /><el-button
                      link
                      type="primary"
                      v-if="
                        detailInfo.actualStatus == 2 ||
                        detailInfo.actualStatus == 4
                      "
                      @click="resetPassword(item.email)"
                      >重置密码</el-button
                    >
                  </div>
                </div>
                <div class="info">
                  <div class="text">
                    公网IP：{{ item.publicIp ? item.publicIp : "--"
                    }}<img
                      v-if="item.publicIp"
                      id="copyicon"
                      src="../../assets/icon/copy-icon.png"
                      @click="copyText(item.publicIp)"
                    />
                  </div>
                  <div class="text">
                    IP地区：{{ item.ipAddress ? item.ipAddress : "--" }}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="tip-card mt36">
          <p>使用提示</p>
          <p>
            1、苹果，安卓手机，电脑端直接到应用市场搜索下载：飞连客户端（苹果手机请使用国内的苹果
            ID 下载），下载链接：<a
              target="_blank"
              href="https://www.volcengine.com/product/feilian/download"
              style="color: #3972fd; text-decoration: none"
              >https://www.volcengine.com/product/feilian/download</a
            >
          </p>
          <p>
            2、打开客户端并输入企业识别码：agi-turbo
            <img
              id="copyicon"
              src="../../assets/icon/copy-icon.png"
              @click="copyText(`agi-turbo`)"
            />
          </p>
          <p>3、输入账号</p>
          <p>4、输入密码</p>
          <p>5、根据提示修改密码。</p>
          <p>6、点击VPN网络，拨号成功。</p>
        </div>
      </div>
    </el-dialog>
    <el-dialog v-model="editDialog"></el-dialog>
  </div>
</template>

<script setup lang="ts" name="Order">
import {
  agiResetPasswordApi,
  cancelLineApi,
  editProductNameApi,
  getAgicOrderInfoApi,
  stopLineApi,
  updateAutoRenewStatusApi,
  upLineApi,
} from "@/api/networkProduct";
import { getAIGCFormListApi, getAgicOrderListAPI } from "@/api/order";
import { useTable } from "@/hooks/useTable";
import { useProduction } from "@/store/modules/networkProduct";
import { useAgicProduct } from "@/store/modules/agic";
import { toPage, copyText } from "@/utils";
import { enumType } from "@/utils/Enum";
import { orderStatusEnum, orderTypeEnum } from "@/utils/radioEnum";
import { ElMessage, ElMessageBox } from "element-plus";
import { onUnmounted } from "vue";

// const {
//   tableData: recordTableData,
//   searchParam: recordSearchParam,
//   searchFn: recordSearchFn,
//   getList: recordGetList,
//   pageData: recordPageData,
//   resetFn: recordResetFn,
// } = useTable({
//   requestApi: getAIGCFormListApi,
//   initParams: { formType: 2 },
// })

const recordCol: ColumnProps[] = [
  {
    prop: "productName",
    label: "产品名称",
    width: 140,
  },
  { prop: "networkCount", label: "购买产品数量", width: 100 },
  { prop: "networkDay", label: "产品天数", width: 100 },
  { prop: "bandwidth", label: "带宽(M) 数量", width: 100 },
  { prop: "actualStatus", label: "产品状态", width: 100, slot: true },
  { prop: "userpwd", label: "账号密码", width: 170, slot: true },
  { prop: "actualAgiOpenTime", label: "实际产品开通时间" },
  { prop: "actualAgiExpireTime", label: "产品到期时间" },
  { prop: "createTime", label: "创建时间", width: 170 },
  { prop: "payTime", label: "支付时间", width: 170 },
  { prop: "payStatus", label: "支付状态", width: 100, slot: true },
  { prop: "handle", label: "操作", width: 100, slot: true, fixed: "right" },
  // { prop: 'premiumPrice', label: '平台溢价金额', width: 120 },
  // { prop: 'userDiscountAmount', label: '用户折扣金额', width: 120 },
  // { prop: 'couponAmount', label: '优惠券满减金额', width: 130 },
  // { prop: 'voucherAmount', label: '代金券抵扣金额', width: 130 },
  // { prop: 'onlinePayAmount', label: '在线支付金额', width: 170 },
  // { prop: 'finalPayAmount', label: '实付金额', width: 100 },
  // { prop: 'recordStatus', label: '状态', slot: true, width: 80 },
];
// 搜索
const recordSearchCol: ColumnProps[] = [
  { prop: "productName", label: "产品名称", search: { el: "input" } },
];

const {
  tableData: orderTableData,
  searchParam: orderSearchParam,
  searchFn: orderSearchFn,
  getList: orderGetList,
  pageData: orderPageData,
  resetFn: orderResetFn,
} = useTable({
  requestApi: getAgicOrderListAPI,
  initParams: { orderType: "PRODUCT", orderStatus: "PAID" },
  requestAuto: false,
});
orderSearchParam.value.actualStatus = 2;
orderSearchFn();

const orderCol: ColumnProps[] = [
  { prop: "agiCustomerName", label: "客户名称", slot: true, width: 230 },
  {
    prop: "",
    label: "出口区域",
    width: 200,
    value: (row) =>
      row.orderSourceList.map((el: any) => el.productName).join(",") || "--",
  },
  // {
  //   prop: 'orderType',
  //   label: '类型',
  //   value: (row) => enumType('orderTypeEnum', row.orderType),
  //   width: 110,
  // },
  // { prop: 'networkCount', label: '购买产品数量', width: 170 },
  {
    prop: "bandwidth",
    label: "带宽(M)",
    width: 170,
    value: (row) =>
      // 判断 row.orderSourceList.map((el: any) => el.productName).join(",") 这个值是否包含 “短视频” 三个字
      row.orderSourceList
        .map((el: any) => el.productName)
        .join(",")
        .includes("短视频")
        ? "--"
        : row.bandwidth ?? "--",
  },
  // { prop: 'ipCount', label: 'ip数量', width: 170 },
  { prop: "mobile", label: "手机号", width: 150 },
  { prop: "actualStatus", label: "产品状态", width: 100, slot: true },
  // { prop: 'actualAgiOpenTime', label: '实际产品开通时间', width: 170 },
  { prop: "actualAgiExpireTime", label: "产品到期时间", width: 170 },
  // { prop: 'createTime', label: '创建时间', width: 170 },
  // { prop: 'payTime', label: '支付时间', width: 170 },
  // { prop: 'premiumPrice', label: '平台溢价金额', width: 120 },
  // { prop: 'userDiscountAmount', label: '用户折扣金额', width: 120 },
  // { prop: 'couponAmount', label: '优惠券满减金额', width: 130 },
  // { prop: 'voucherAmount', label: '代金券抵扣金额', width: 130 },
  // { prop: 'onlinePayAmount', label: '在线支付金额', width: 170 },
  // { prop: 'finalPayAmount', label: '实付金额', width: 100 },
  // { prop: 'orderStatus', label: '状态', slot: true, width: 80 },
  { prop: "orderNo", label: "订单号" },
  { prop: "operation", label: "操作", slot: true, fixed: "right", width: 200 },
];

// 搜索
const statusEnum = [
  { label: "未开通", value: 1 },
  { label: "运行中", value: 2 },
  { label: "已过期", value: 3 },
  { label: "已暂停", value: 4 },
  { label: "已撤线", value: 5 },
];
const orderSearchCol: ColumnProps[] = [
  {
    prop: "agiCustomerName",
    label: "客户名称",
    search: { el: "input" },
  },
  { prop: "productName", label: "出口区域", search: { el: "input" } },
  { prop: "bandwidth", label: "带宽", search: { el: "input" } },
  { prop: "orderNo", label: "订单号", search: { el: "input" } },
  { prop: "mobile", label: "手机号", search: { el: "input" } },
  { prop: "email", label: "邮箱", search: { el: "input" } },
  {
    prop: "actualStatus",
    label: "状态",
    search: { el: "select" },
    fieldNames: { label: "label", value: "value" },
    enum: statusEnum,
  },
  // { prop: 'orderId', label: '到期时间', search: { el: 'input' } },
];

// 详情
const toDetail = (row: TKeyValue) => {
  getAgicOrderInfoApi(row.networkValueId).then((res) => {
    detailInfo.value = res.data;
    detailDialog.value = true;
  });
  // localStorage.setItem('orderForm', JSON.stringify(row))
  // toPage('/orderDetails')
};

const detailDialog = ref(false);
const detailInfo = ref<any>({
  isAutoRenew: 0,
});
const autoLoading = ref(false);
// 续费切换

const beforeChangeAutoRenew = (newVal) => {
  autoLoading.value = true;
  const data = {
    networkValueId: detailInfo.value.id,
    isAutoRenew: newVal,
  };
  // 接口改变续费状态后返回
  updateAutoRenewStatusApi(data)
    .then(() => {
      detailInfo.value.isAutoRenew = newVal;
    })
    .catch(() => {
      detailInfo.value.isAutoRenew = !newVal;
    })
    .finally(() => {
      autoLoading.value = false;
    });
};
const changeAutoRenew = (val) => {
  detailInfo.value.isAutoRenew = !detailInfo.value.isAutoRenew;
  console.log("changeAutoRenew", detailInfo.value.isAutoRenew);
};
const tableRadio = ref("order");

// 重置密码
const resetPassword = (email: any) => {
  agiResetPasswordApi(email).then((res) => {
    if (res.code == 200) {
      ElMessage.success("重置密码成功");
    } else {
      ElMessage.error("重置密码失败");
    }
  });
};

// 去支付
const targetNetworkProduction = useProduction();

const toPay = (row: any) => {
  const data = {
    networkPayValueId: row.id,
    networkProductId: row.productId,
    networkCount: row.networkCount,
    networkDay: row.networkDay,
    payFormValue: {
      json: row.json,
    },
  };
  targetNetworkProduction.setProductionDetail(data);
  toPage(`/payControl`);
  window.scrollTo({ top: 0, behavior: "smooth" });
};
// 详情处理
const agicProduct = useAgicProduct();

const toRenew = (row: any) => {
  getAgicOrderInfoApi(row.networkValueId).then((res) => {
    agicProduct.setAgicDetail(res.data, "renew");
    targetNetworkProduction.setProductionName(res.data.productName);
    agicProduct.setDisplayOrBind({
      isAccountIpBinding: res.data.isAccountIpBinding,
      isBandwidthDisplay: res.data.isBandwidthDisplay,
      isIpDisplay: res.data.isIpDisplay,
    });
    toPage("/networkProductForm?isEdit=true");
  });
};
const btnHandler = (type: string, target: any) => {
  switch (type) {
    // 变更套餐
    case "edit":
      agicProduct.setAgicDetail(target, "count");
      targetNetworkProduction.setProductionName(target.productName);
      agicProduct.setDisplayOrBind({
        isAccountIpBinding: target.isAccountIpBinding,
        isBandwidthDisplay: target.isBandwidthDisplay,
        isIpDisplay: target.isIpDisplay,
      });
      toPage("/networkProductForm?isEdit=true");
      break;
    // 上线
    case "start":
      upLineApi(target.id)
        .then(() => {
          ElMessage.success("上线成功");
          orderGetList();
          getAgicOrderInfoApi(target.id).then((res) => {
            detailInfo.value = res.data;
          });
        })
        .catch(() => {
          ElMessage.error("上线失败");
        });
      break;
    // 停线
    case "stop":
      stopLineApi(target.id)
        .then(() => {
          ElMessage.success("暂停成功");
          orderGetList();
          getAgicOrderInfoApi(target.id).then((res) => {
            detailInfo.value = res.data;
          });
        })
        .catch(() => {
          ElMessage.error("暂停失败");
        });
      break;
    // 撤线
    case "cancel":
      cancelLineApi(target.id)
        .then(() => {
          ElMessage.success("撤线成功");
          orderGetList();
          getAgicOrderInfoApi(target.id).then((res) => {
            detailInfo.value = res.data;
          });
        })
        .catch(() => {
          ElMessage.error("撤线失败");
        });
      break;
    // 续费
    case "renew":
      agicProduct.setAgicDetail(target, "renew");
      targetNetworkProduction.setProductionName(target.productName);
      agicProduct.setDisplayOrBind({
        isAccountIpBinding: target.isAccountIpBinding,
        isBandwidthDisplay: target.isBandwidthDisplay,
        isIpDisplay: target.isIpDisplay,
      });
      toPage("/networkProductForm?isEdit=true");
      break;
  }
};
// 编辑产品名称
const openEditDialog = () => {};
const editProductName = (row: any) => {
  ElMessageBox.prompt("请输入客户名称", "客户名称", {
    confirmButtonText: "确认",
    cancelButtonText: "取消",
    inputValue: row.agiCustomerName,
  }).then(({ value }) => {
    editProductNameApi({
      networkValueId: row.networkValueId,
      agiCustomerName: value,
    })
      .then((res) => {
        ElMessage.success("修改成功");
        orderGetList();
      })
      .catch(() => {
        ElMessage.error("修改失败");
      });
  });
};
</script>
<style lang="scss" scoped>
.top-btn {
  justify-content: space-between;
}
.dialog-content {
  display: flex;
  flex-direction: column;

  padding: 30px;
  padding-bottom: 0px;

  .info-item {
    display: flex;
    align-items: center;
    gap: 40px;

    .label {
      min-width: 64px;
      height: 22px;
      font-weight: 400;
      font-size: 16px;
      color: #83889d;
    }

    .value {
      font-weight: 400;
      font-size: 16px;
      color: #000000;
      max-width: 180px;
      // 超出就省略号
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .psw-list {
      display: flex;
      flex-direction: column;
      gap: 26px;

      .psw-item {
        display: flex;
        flex-direction: column;
        gap: 16px;
        padding: 10px;
        background-color: rgb(240, 240, 240);
        border-radius: 10px;
        width: 754px;
        .email {
          font-weight: 400;
          font-size: 16px;
          color: #3972fd;
        }

        .info {
          display: grid;
          grid-template-columns: repeat(2, 1fr);
          align-content: center;
          width: 800px;
          .text {
            font-weight: 400;
            font-size: 16px;
            color: #000000;
            margin-right: 30px;
          }
        }
      }
    }
  }

  // 数字为几就分几格
  .flx-1 {
    display: grid;
    grid-template-columns: repeat(1, 1fr);
    margin-top: 36px;
  }

  .flx-2 {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    margin-top: 36px;
  }

  .flx-3 {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
  }

  .tip-card {
    width: 974px;
    height: 228px;
    background: linear-gradient(86deg, #e6eeff 0%, rgba(255, 255, 255, 0) 100%);
    border-radius: 10px 10px 10px 10px;
    padding: 15px;
    font-weight: 400;
    font-size: 18px;
    color: #000000;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
  }

  #copyicon {
    height: 20px;
    width: 20px;
    margin-left: 10px;
    margin-right: 10px;
    &:hover {
      cursor: pointer;
    }
  }

  .footer {
    margin-top: -30px;
    margin-bottom: 20px;
    display: flex;
    justify-content: flex-end;
    gap: 5px;
  }
}
</style>
