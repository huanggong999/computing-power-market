<template>
  <div class="banner">
    <div class="title mb30">{{ product.name }}</div>
    <div class="content mb44">
      {{ product.memo }}
    </div>
    <div class="price mb20">
      <span class="label">价格：</span>
      <span class="value"
        ><span class="small">￥</span>{{ product.payPrice }}元/月</span
      >
    </div>

    <el-button type="primary" class="mr20" plain @click="openDialog('consult')"
      >立即咨询
    </el-button>
    <!-- <el-button type="primary" @click="openDialog('pay')">立即购买 </el-button> -->
    <el-button type="primary" @click="toUrl(`/networkProductForm?id=${id}`)"
      >立即购买
    </el-button>
  </div>
  <div class="part" v-html="product.detail"></div>
  <!-- 咨询表单 -->
  <div v-if="consultDialog" class="consultDialog">
    <div class="close-btn" @click="consultDialog = false">x</div>
    <img src="../../assets/images/consultDialog.png" alt="" />
    <div class="dialog-form">
      <div class="title">产品咨询</div>
      <FormCreate
        :rule="consultRule"
        v-model:api="fApi"
        :option="consultOptions"
        v-model="consultFormParams"
      />
      <el-checkbox v-model="isConfirm"
        ><span class="check-text"
          >我已阅读并同意<span style="color: #3972fd" @click.stop="openDocs"
            >《逸云数智产品和服务协议》</span
          ></span
        ></el-checkbox
      >
      <!-- <el-button type="primary" class="mt16 mb20" @click="confirmForm"
        >提交</el-button
      > -->
      <div class="tip flx-center mb5 mt10">
        表单提交后，会有工作人员与您电话联系
      </div>
      <div class="tip flx-center">如果您现在就需要帮助，可以与我们客服联系</div>
    </div>
  </div>
  <div class="mask" v-if="consultDialog" @click="consultDialog = false"></div>
  <!-- 购买表单 -->
  <div v-if="payDialog" class="consultDialog">
    <div class="close-btn" @click="payDialog = false">x</div>
    <img src="../../assets/images/consultDialog.png" alt="" />
    <div class="dialog-form">
      <div class="title">产品购买</div>
      <FormCreate
        :rule="payRule"
        v-model:api="fApi"
        :option="payOptions"
        v-model="payFormParams"
      />

      <el-checkbox v-model="isConfirm"
        ><span class="check-text"
          >我已阅读并同意<span style="color: #3972fd" @click.stop="openDocs"
            >《逸云数智产品和服务协议》</span
          ></span
        ></el-checkbox
      >
      <div class="tip flx-center mb5 mt10">
        表单提交后，会有工作人员与您电话联系
      </div>
      <div class="tip flx-center">如果您现在就需要帮助，可以与我们客服联系</div>
    </div>
  </div>
  <div class="mask" v-if="payDialog" @click="payDialog = false"></div>
  <el-dialog
    width="300"
    v-model="successDialog"
    align-center
    class="success-dialog"
  >
    <div class="content">
      <img src="../../assets/images/pay-success.png" alt="" />
      <div class="title">提交成功</div>
      <div class="value">提交成功，我们会尽快与您联系</div>
      <div class="btns">
        <el-button @click="successDialog = false">返回</el-button>
        <el-button type="primary" @click="toUrl('/aigcOrder')"
          >返回控制台</el-button
        >
      </div>
    </div>
  </el-dialog>
</template>

<script setup lang="ts" name="productDetail">
import {
  getNetWorkPayFormApi,
  getNetWorkProductDetailAPI,
  getNetWorkProductFormAPI,
  submitNetWorkFormApi,
  submitNetWorkPayFormApi,
} from '@/api/networkProduct'
import { useProduction } from '@/store/modules/networkProduct'
import { toPage } from '@/utils'
import formCreate from '@form-create/element-ui'
import { ElMessage } from 'element-plus'
import { watchEffect, ref } from 'vue'
import { useRoute } from 'vue-router'
const FormCreate = formCreate.$form()
const targetNetworkProduction = useProduction()
const route = useRoute()
const id = ref(route.query.id)

const consultDialog = ref(false)
const payDialog = ref(false)
const openType = ref(route.query.type)
watchEffect(() => {
  if (openType.value === 'consult') {
    consultDialog.value = true
  } else if (openType.value === 'pay') {
    payDialog.value = true
  }
})
// 网络产品详情
const product = ref<any>({ name: '', memo: '', referPrice: '', detail: '' })

const successDialog = ref(false)
// 咨询表单
const consultRule = ref()
const consultFormParams = ref({})
const fApi = ref()
const consultOptions = ref({
  onSubmit: () => {
    if (!isConfirm.value) {
      ElMessage.error('请先同意协议')
      return
    }
    submitNetWorkFormApi({
      productId: id.value,
      json: JSON.stringify(consultRule.value),
    }).then((res: any) => {
      if (res.code === 200) {
        successDialog.value = true
      }
    })
  },
})
const openDocs = () => {
  let a = window.location.origin + '/#/'
  a += 'docsView/ProductAgreement'
  window.open(a, '_blank')
}
// 购买表单
const payRule = ref()
const payFormParams = ref({})
const payForm = ref({
  networkCount: 1,
  networkDay: 1,
  bandwidth: 1,
})
const payOptions = ref({
  onSubmit: () => {
    if (!isConfirm.value) {
      ElMessage.error('请先同意协议')
      return
    }
    // 处理购买逻辑
    let data = {
      ...payForm.value,
      productId: id.value,
      json: JSON.stringify(payRule.value),
    }
    submitNetWorkPayFormApi(data).then((res: any) => {
      if (res.code === 200) {
        successDialog.value = true
      }
    })
    // targetNetworkProduction.setProductionDetail(data)
    // toPage(`/payControl`)
    // window.scrollTo({ top: 0, behavior: 'smooth' })
  },
})
const isConfirm = ref(false)
const openDialog = (type: string) => {
  window.scrollTo({ top: 0, behavior: 'smooth' })
  if (type === 'consult') {
    consultDialog.value = true
  } else {
    payDialog.value = true
  }
}
const initPage = async () => {
  await getNetWorkProductDetailAPI(id.value).then((res) => {
    product.value = res.data
  })
  await getNetWorkProductFormAPI(id.value).then((res) => {
    consultFormParams.value = res.data
    consultRule.value = formCreate.parseJson(res.data.json)
  })
  await getNetWorkPayFormApi(id.value).then((res) => {
    payFormParams.value = res.data
    payRule.value = formCreate.parseJson(res.data.json)
    console.log(
      'formCreate.parseJson(res.data.json)',
      formCreate.parseJson(res.data.json)
    )
  })
}
onMounted(() => {
  initPage()
})
const toUrl = (url: string) => {
  successDialog.value = false
  consultDialog.value = false
  toPage(url)
}
const confirmForm = () => {
  if (!isConfirm.value) {
    ElMessage.error('请先同意协议')
    return
  }
  // if (consultFormParams.value.name && consultFormParams.value.phone) {
  //   ElMessage.success('提交成功')
  //   // consultDialog.value = false
  // } else {
  //   ElMessage.error('请填写完整信息')
  // }
}

watch(
  () => successDialog.value,
  (newVal) => {
    if (!newVal) {
      consultDialog.value = false
    }
  }
)
</script>
<style lang="scss" scoped>
.banner {
  height: 556px;
  padding-top: 159px;
  background: url('@/assets/images/network-banner.png') no-repeat;
  background-size: 100%;
  z-index: 5;
  padding-left: 225px;

  .title {
    font-weight: 800;
    font-size: 40px;
  }

  .content {
    width: 759px;
    font-weight: 400;
    font-size: 20px;
  }

  .price {
    .label {
      font-weight: 400;
      font-size: 16px;
    }

    .value {
      font-weight: 800;
      font-size: 30px;
      color: #ff4151;

      .small {
        font-size: 16px;
      }
    }
  }

  .el-button {
    width: 174px;
    height: 60px;

    border-radius: 30px 30px 30px 30px;
  }
}

.part {
  padding: 60px 225px 79px 225px;
}

/* 去掉 el-dialog__header 的内边距 */

.consultDialog {
  position: absolute;
  width: 1236px;
  min-height: 847px;
  background: #ffffff;
  border-radius: 8px 8px 8px 8px;
  top: 70%;
  left: 50%;
  transform: translate(-50%, -50%);
  display: grid;
  grid-template-columns: 606px 610px;
  z-index: 11;

  .close-btn {
    position: absolute;
    top: 0;
    right: 0;
    font-size: 30px;
    width: 40px;
    height: 40px;
    display: flex;
    justify-content: center;
    align-items: center;
    cursor: pointer;
  }

  img {
    width: 606px;
    height: 847px;
  }

  .dialog-form {
    padding: 32px 70px;

    .title {
      font-weight: 500;
      font-size: 36px;
      line-height: 50px;
      margin-bottom: 26px;
    }

    ::v-deep .el-form-item {
      display: flex;
      flex-direction: column;

      .el-form-item__label {
        font-weight: 400;
        font-size: 16px;
        display: block;
      }

      .el-form-item__content {
        width: 490px;
        height: 34px;
        border-radius: 4px 4px 4px 4px;
        border: 1px solid #d9d9d9;
        border: 0;
        margin: 0 !important;
      }

      .el-button {
        width: 100%;
      }
    }

    .check-text {
      font-weight: 400;
      font-size: 16px;
      color: #666666;
    }

    .el-button {
      width: 100%;
    }

    .tip {
      font-weight: 400;
      font-size: 16px;
    }
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
</style>
