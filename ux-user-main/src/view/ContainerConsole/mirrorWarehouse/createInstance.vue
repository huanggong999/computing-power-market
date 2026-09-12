/** 创建实例 */
<template>
  <Breadcrumb :router-list="routerList" />
  <div class="card mb20">
    <div v-if="userInfo.totalBalance < 0" class="owe-tip ml20 mb20">
      当前账户总余额欠费,暂不支持创建实例。请先缴清欠款后再尝试创建实例。
    </div>
    <TipText class="fwb table-header mb30" content="购买信息" fontSize="20"></TipText>
    <ProForm ref="FormRef" v-model="form" :formColumns="formCol" style="margin-top: 20px">
      <template #project>
        <el-input disabled :value="form.project"></el-input>
      </template>
      <template #domain>
        <span>{{ showDomain }}</span></template>
    </ProForm>
  </div>
  <div class="card mb20">
    <TipText class="fwb table-header mb30" content="选择规格实例" fontSize="20"></TipText>
    <ProForm ref="FormRef" v-model="form" :formColumns="secondFormCol" style="margin-top: 20px">
      <template #size>
        <div class="size">
          <div class="left-card">
            <div class="title">标准版</div>
            <div class="content">
              标准产品规格，满足大部分企业镜像安全托管诉求及大规模分发场景
            </div>
          </div>
          <div class="right-card flx-align-center">
            <div class="title mr40">配额</div>
            <div class="box">
              <div class="info">
                <div class="name">命名空间</div>
                <div class="content">1个</div>
              </div>
              <div class="info">
                <div class="name">OCI制品空间</div>
                <div class="content">1个</div>
              </div>
              <div class="info">
                <div class="name">VPC计入配额</div>
                <div class="content">1个</div>
              </div>
            </div>
          </div>
          <div class="tip flx-center">标准版默认</div>
        </div>
      </template>
      >
    </ProForm>
  </div>
  <div class="card pay">
    <div class="pay-card flx-align-center">
      <div class="pay-box flx-align-center">
        <div class="label">{{ orderSourceList[0].productName }}</div>
        <div class="num">￥{{ orderSourceList[0].premiumPrice }}/时</div>
      </div>
      <div class="pay-box flx-align-center">
        <div class="label">{{ orderSourceList[1].productName }}</div>
        <div class="num">￥{{ orderSourceList[1].premiumPrice }}/GB</div>
      </div>
      <div class="pay-box flx-align-center">
        <div class="label">{{ orderSourceList[2].productName }}</div>
        <div class="num">￥{{ orderSourceList[2].premiumPrice }}/GB</div>
      </div>
      <div class="btns">
        <el-button>取消</el-button>
        <el-button type="primary" @click="toPay" :disabled="userInfo.totalBalance < 0">确认订单</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts" name="CreateCluster">
import { checkRepositoryImageApi } from '@/api/image'
import { buildOrderInfoApi } from '@/api/order'
import TipText from '@/components/TipTitle/TipText.vue'
import { useUserInfo } from '@/store'
import { useOrder } from '@/store/modules/order'
import { toPage } from '@/utils'
import { ElMessage } from 'element-plus'

const orderDetail = useOrder()
const userInfo = useUserInfo()
const routerList = ref([
  { name: '镜像仓库', path: '/mirrorWarehouse' },
  { name: '创建实例', path: '' },
])
const FormRef = ref<any>(null)
const formCol: IformColumnsProps[] = [
  {
    label: '名称',
    prop: 'instanceName',
    el: 'input',
    required: true,
    tips: "支持小写字母、数字、分隔符'-'  首位需为字母  长度为3-30个字符",
  },
  {
    label: '项目',
    prop: 'project',
    disabled: true,
    el: 'slot',
    required: true,
  },
  {
    label: '系统域名',
    prop: 'domain',
    el: 'slot',
    required: true,
  },
  {
    label: '计费类型',
    prop: 'chargeType',
    radioList: [{ label: '按量计费', value: 'POSTPAID_BY_HOUR' }],
    el: 'radioButton',
    required: true,
  },
]
const secondFormCol: IformColumnsProps[] = [
  {
    label: '规格实例',
    prop: 'size',
    el: 'slot',
    required: true,
  },
]
const form = ref({
  instanceName: '',
  chargeType: 'POSTPAID_BY_HOUR',
  project: 'default(默认项目)',
  domain: 'cn-beijing.cr.volces.com',
})
const showDomain = computed(() => {
  return `${form.value.instanceName}-${form.value.domain}`
})
const orderSourceList = ref<any>([
  {
    product: '配置费用',
    premiumPrice: 0,
  },
  {
    product: '公网流出流量费用',
    premiumPrice: 0,
  },
  {
    product: '存储容量费用',
    premiumPrice: 0,
  },
])
const orderRes = ref<any>({})
// 构建订单
const buildOrder = () => {
  const data = {
    orderType: 'NEW_RESOURCE',
    orderSource: [
      {
        regionId: 'CN_BEIJING',
        sourceType: 'CR',
        chargeType: 'POSTPAID_BY_HOUR',
      },
    ],
  }
  buildOrderInfoApi(data).then((res: any) => {
    console.log(res)
    orderRes.value = res.data
    orderSourceList.value = res.data.orderSourceList
  })
}
buildOrder()
const toPay = async () => {
  if (form.value.instanceName === '') {
    ElMessage.warning('请输入实例名称')
    return
  }
  if (form.value.instanceName) {
    const regex = /^[a-z][a-z0-9-]{2,29}$/ // 以小写字母开头，仅支持小写字母、数字和-，长度3-30
    if (!regex.test(form.value.instanceName)) {
      ElMessage.warning(
        '实例名称需要以小写字母开头，仅支持小写字母、数字和-，且长度为3-30个字符！'
      )
      return
    }
  }
  await checkRepositoryImageApi(form.value.instanceName).then((res: any) => {
    if (res.data) {
      ElMessage.warning('实例名称已存在，请重新输入')
      return
    } else {
      const data = {
        ...form.value,
        ...orderRes.value,
      }
      orderDetail.setOrderDetail(data)
      toPage('/payMirrorInstance')
    }
  })
}
</script>
<style lang="scss" scoped>
.size {
  position: relative;
  width: 715px;
  height: 134px;
  display: grid;
  grid-template-columns: 1fr 415px;
  border-radius: 0px 0px 0px 0px;
  border: 1px solid #d9d9d9;

  .left-card {
    width: 300px;
    height: 134px;
    background: #e6eeff;
    border-radius: 0px 0px 0px 0px;
    border-right: 1px solid #d9d9d9;
    padding: 20px 18px;

    .title {
      font-weight: 500;
      font-size: 20px;
      color: #3972fd;
    }

    .content {
      font-weight: 400;
      font-size: 16px;
      color: #83889d;
    }
  }

  .right-card {
    padding: 22px 26px;

    .title {
      width: 18px;

      font-weight: 500;
      font-size: 18px;
      color: #333333;
    }

    .box {
      display: flex;
      flex-direction: column;
      justify-content: space-between;

      .info {
        display: flex;
        justify-content: space-between;
        width: 240px;

        .name {
          height: 22px;

          font-weight: 400;
          font-size: 16px;
          color: #83889d;
        }

        .content {}
      }
    }
  }

  .tip {
    position: absolute;
    top: 0;
    left: 637px;
    width: 78px;
    height: 28px;
    background: #e6eeff;
    border-radius: 0px 0px 0px 0px;
    font-weight: 400;
    font-size: 14px;
    color: #3972fd;
  }
}

.pay {
  padding: 20px 24px;

  .pay-card {
    height: 89px;
    background: #f7f8fb;
    border-radius: 10px 10px 10px 10px;
    padding-left: 28px;
    gap: 67px;

    .pay-box {
      gap: 14px;

      .label {
        font-weight: 400;
        font-size: 18px;
        color: #666666;
      }

      .num {
        font-weight: 500;
        font-size: 18px;
        color: #ff4151;
      }
    }

    .btns {
      justify-self: flex-end;
      margin-left: auto;

      .el-button {
        width: 138px;
        height: 49px;

        font-weight: 500;
        font-size: 18px;

        border-radius: 4px 4px 4px 4px;
      }
    }
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
