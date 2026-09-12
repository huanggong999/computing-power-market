<template>
  <Banner :is-search="false" :banner-type="3" @current-tab="toExtend"></Banner>
  <v-md-preview
    ref="previewRef"
    :text="configDetail.introduce"
    height="400"
  ></v-md-preview>

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
</template>

<script setup lang="ts" name="productDetail">
import {
  applyExtendApi,
  getExtendConfigApi,
  getExtendInfoApi,
} from '@/api/partner'
import { useUserInfo } from '@/store'
import { getToken } from '@/utils/auth'
import { LOGIN_URL } from '@/config'
import { ElMessage } from 'element-plus'
import { toPage } from '@/utils'
import { watchEffect } from 'vue'
import { useRouter } from 'vue-router'
import { IPersonalExtendInfo } from '@/api/types/partner'
import { initDynamicRouter } from '@/routes/dynamicRouter'
const router = useRouter()
const userInfo = useUserInfo()
// 推广配置
let previewRef = ref<IPersonalExtendInfo>()
let configDetail = ref({
  introduce: '',
})
const initPage = () => {
  getExtendConfigApi().then((res: any) => {
    configDetail.value = res.data
  })
}
initPage()

// 弹窗
const successDialog = ref(false)
const partnerDialog = ref(false)
const proFormRef = ref(null)
const formParams = ref({ name: '', phone: '' })
const isConfirm = ref(false)
const formCol: IFormColumnsProps[] = [
  {
    placeholder: '请输入您的真实姓名',
    prop: 'name',
    label: '真实姓名',
    el: 'input',
  },
  {
    placeholder: '请输入您的联系电话',
    prop: 'phone',
    label: '联系电话',
    el: 'input',
  },
]
const toExtend = () => {
  // 检测用户是否登录
  if (getToken()) {
    // 检查申请状态
    getExtendInfoApi().then(async ({ data }: any) => {
      if (!data.status) {
        window.scrollTo({ top: 0, behavior: 'smooth' })
        partnerDialog.value = true
      } else if (data.status == 1) {
        ElMessage.warning('当前账号已申请，请耐心等待。')
      } else if (data.status == 2) {
        ElMessage.success('当前账号已申请成功！请勿重复申请。')
        await initDynamicRouter()
        toPage('/popularize/customerManagement')
      } else {
        ElMessage.error(
          `申请失败。失败理由:${data.verifyRemark}，您可重新申请。`
        )
        window.scrollTo({ top: 0, behavior: 'smooth' })
        partnerDialog.value = true
      }
    })
  } else {
    ElMessage.warning('账号未登录，正在为您跳转登录页...')
    router.push(LOGIN_URL)
  }
}
const confirmForm = () => {
  if (!isConfirm.value) {
    ElMessage.error('请先同意协议')
    return
  }
  if (formParams.value.name && formParams.value.phone) {
    let params = { ...formParams.value, userId: userInfo.userId }
    applyExtendApi(params).then((res) => {
      if (res.code === 200) {
        ElMessage.success('提交成功')
        successDialog.value = true
      }
    })
  } else {
    ElMessage.error('请填写完整信息')
  }
}
const openDocs = () => {
  let a = window.location.origin + '/#/'
  a += 'docsView/privacyPolicy'
  window.open(a, '_blank')
}
watchEffect(() => {
  if (partnerDialog.value) {
    window.scrollTo({ top: 0, behavior: 'smooth' })
    document.body.style.overflow = 'hidden' // 禁用滚动
  } else {
    document.body.style.overflow = '' // 恢复滚动
  }
})
watch(
  () => successDialog.value,
  (newVal) => {
    if (!newVal) {
      partnerDialog.value = false
    }
  }
)
</script>
<style lang="scss" scoped>
/* .banner {
  height: 556px;
  padding-top: 211px;
  background: url('@/assets/images/partner-banner.png') no-repeat;
  background-size: 100%;
  z-index: 5;
  padding-left: 225px;
  .title {
    font-weight: 800;
    font-size: 40px;
  }
  .content {
    font-weight: 400;
    font-size: 20px;
  }

  .el-button {
    width: 174px;
    height: 60px;
    border-radius: 30px 30px 30px 30px;
    font-weight: 400;
    font-size: 18px;
    color: #ffffff;
  }
} */
.part {
  display: flex;
  flex-direction: column;
  align-items: center;

  .title {
    font-weight: bold;
    font-size: 40px;
    margin-bottom: 17px;
  }
  img {
    width: 200px;
    height: 200px;
  }
}

.partnerDialog {
  position: absolute;
  top: 0;
  width: 930px;
  height: 680px;
  background: #ffffff;
  border-radius: 8px 8px 8px 8px;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 111;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 22px 24px;

  .form-group {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 28px;
    width: 100%;
    ::v-deep .el-form-item {
      display: flex;
      flex-direction: column;

      .el-form-item__label {
        font-weight: 400;
        font-size: 16px;
        display: block;
      }
      .el-form-item__content {
        width: 100%;
        border-radius: 4px 4px 4px 4px;
        border: 1px solid #d9d9d9;
        border: 0;
      }
    }
  }

  .title {
    font-weight: 500;
    font-size: 24px;
  }
  .gray-card {
    width: 880px;
    height: 610px;
    background: #f7f8fb;
    border-radius: 8px 8px 8px 8px;
    padding: 20px 24px;
    overflow: auto;
    white-space: pre-line;
    font-weight: 400;
    font-size: 18px;
  }
  .footer {
    width: 100%;

    .check-text {
      font-weight: 400;
      font-size: 16px;
      color: #666666;
    }
    .el-button {
      margin-left: auto;
      width: 220px;
      height: 40px;
      border-radius: 4px 4px 4px 4px;
    }
  }
  .tip {
    font-weight: 400;
    font-size: 16px;
  }
}
.success-dialog {
  .content {
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
.mask {
  position: fixed; /* 固定定位，确保遮罩层覆盖整个页面 */
  top: 0; /* 从页面顶部开始 */
  left: 0; /* 从页面左边开始 */
  width: 100vw; /* 宽度占满整个视口 */
  height: 100vh; /* 高度占满整个视口 */
  background-color: rgba(0, 0, 0, 0.5); /* 半透明黑色背景 */
  z-index: 101; /* 确保遮罩层在最上层 */
}
</style>
