/** 充值中心 */
<template>
  <div style="height: 94%">
    <Breadcrumb :router-list="routerList" />
    <div class="card authentication table-box">
      <!-- 未认证 -->
      <div class="select-content" v-if="(userInfo.certStatus == 1 || userInfo.certStatus == 4) && authStep == 1">
        <div class="tip">实名认证直接影响账号和资源的归属，如果企业用户使用个人信息进行实名认证， 后续出现人员变动或账号纠纷时，可能会影响企业用户的业务，甚至造成经济损失。
          在进行实名认证之前，请确认您在火山引擎购买和使用的资源是属于个人还是企业。详情请查看</div>
        <div class="title mt12">选择认证方式</div>
        <div class="select-box mt20">
          <div class="select-card">
            <div class="title">企业认证</div>
            <div class="intro">账号归属于企业，适用于企业、政府、事业单位、团体、组织等，完成实名认证可开增值税普通发票、增值税专用发票、组织（非企业）增值税普通发票</div>
            <div class="selection">
              <div class="info">
                <div class="label">企业证件认证</div>
                <div class="detail">企业证件附件 | 法人信息</div>
              </div>
              <el-button class="btn" type="primary" @click="toAuthenticate(1)">立即认证</el-button>
            </div>
            <!-- <div class="selection">
              <div class="info">
                <div class="label">法人人脸识别</div>
                <div class="detail">企业证件附件 | 法人信息 | 人脸识别</div>
              </div>
              <el-button class="btn" type="primary" @click="toAuthenticate(2)">立即认证</el-button>
            </div> -->
          </div>
          <div class="select-card">
            <div class="title">个人认证</div>
            <div class="intro">适用于个人用户，账号归属于个人，认证后可按需升级企业认证</div>
            <div class="selection">
              <div class="info">
                <div class="label">个人证件认证</div>
                <div class="detail">身份证信息校验</div>
              </div>
              <el-button class="btn" type="primary" @click="toAuthenticate(3)">立即认证</el-button>
            </div>
            <!-- <div class="selection">
              <div class="info">
                <div class="label">人脸识别</div>
                <div class="detail">身份证信息校验 | 人脸识别</div>
              </div>
              <el-button class="btn" type="primary" @click="toAuthenticate(4)">立即认证</el-button>
            </div> -->
          </div>
        </div>
      </div>

      <!-- 认证窗口 -->
      <div class="auth-window" v-if="authStep == 2">
        <!-- 企业认证 -->
        <div v-if="authType == 1 || authType == 2">
          <TipText content="认证信息" fontSize="16" />
          <ProForm ref="proFormRef" v-model="companyBasicForm" :formColumns="companyBasicFormCol" :label-width="140"
            class="mt20" v-if="
              (userInfo.certStatus == 1 || userInfo.certStatus == 4) && isForm
            "></ProForm>
          <TipText content="法人信息" fontSize="16" />
          <ProForm ref="proFormRef" v-model="legalPersonForm" :formColumns="legalPersonFormCol" :label-width="140"
            class="mt20" v-if="
              (userInfo.certStatus == 1 || userInfo.certStatus == 4) && isForm
            "></ProForm>
        </div>
        <!-- 个人认证 -->
        <div v-else-if="authType == 3 || authType == 4">
          <TipText content="认证信息" fontSize="16" />
          <ProForm ref="proFormRef" v-model="personForm" :formColumns="personFormCol" class="mt20" v-if="
            (userInfo.certStatus == 1 || userInfo.certStatus == 4) && isForm
          ">
            <template #idCard>
              <el-input :value="personForm.idCard" disabled></el-input>
            </template>
          </ProForm>
        </div>
      </div>

      <!-- 人脸窗口 -->
      <div class="face-window" v-else-if="authStep == 3" style="margin-left: -50px;margin-top:-20px;">
        <div class="label">扫码进行人脸认证</div>
        <QrCode :text=qrUrl />
        <div class="tip">请打开微信APP，打开【首页-扫一扫】</div>
      </div>

      <!-- 认证成功 -->
      <div class="auth-card success" v-else-if="userInfo.certStatus == 3">
        <div class="left">
          <div class="title success-title flx-align-center">
            <img src="../../../assets/icon/success-auth-icon.png" />已完成{{ userInfo.certType == 1 ? '个人' : '企业' }}实名认证
          </div>
          <div class="list" v-if="userInfo.certType == 1">
            <div class="item">
              <div class="label">姓名</div>
              <div class="value">{{ userInfo.realName }}</div>
            </div>
            <div class="item">
              <div class="label">身份证</div>
              <div class="value">{{ userInfo.idCard }}</div>
            </div>
          </div>
          <div class="list" v-else-if="userInfo.certType == 2">
            <div class="item">
              <div class="label">企业名称</div>
              <div class="value">{{ userInfo.companyName }}</div>
            </div>
            <div class="item">
              <div class="label">企业法人</div>
              <div class="value">{{ userInfo.realName }}</div>
            </div>
            <div class="item">
              <div class="label">法人身份证</div>
              <div class="value">{{ userInfo.idCard }}</div>
            </div>
            <div class="item">
              <div class="label">企业注册地址</div>
              <div class="value">{{ userInfo.companyAddress }}</div>
            </div>
            <div class="item">
              <div class="label">企业统一社会信用代码</div>
              <div class="value">{{ userInfo.companyCode }}</div>
            </div>
            <div class="item">
              <div class="label">认证时间</div>
              <div class="value">{{ userInfo.companyVerifyTime }}</div>
            </div>
          </div>
        </div>
        <div class="right">
          <img src="../../../assets/icon/authbg-icon.png" />
        </div>
      </div>

      <!-- 认证审核中 -->
      <div class="auth-card warn" v-else-if="userInfo.certStatus == 2">
        <div class="left">
          <div class="title warn-title flx-align-center">
            <img src="../../../assets/icon/warn-auth-icon.png" />{{ userInfo.certType == 1 ? '个人' : '企业' }}实名认证审核中
          </div>
          <div class="list">
            <div class="item">
              <div class="label">企业名称</div>
              <div class="value">{{ userInfo.companyName }}</div>
            </div>
            <div class="item">
              <div class="label">企业法人</div>
              <div class="value">{{ userInfo.realName }}</div>
            </div>
            <div class="item">
              <div class="label">法人身份证</div>
              <div class="value">{{ userInfo.idCard }}</div>
            </div>
            <div class="item">
              <div class="label">企业注册地址</div>
              <div class="value">{{ userInfo.companyAddress }}</div>
            </div>
            <div class="item">
              <div class="label">企业统一社会信用代码</div>
              <div class="value">{{ userInfo.companyCode }}</div>
            </div>
            <div class="item">
              <div class="label">认证时间</div>
              <div class="value">{{ userInfo.companyVerifyTime }}</div>
            </div>
          </div>
        </div>
        <div class="right">
          <img src="../../../assets/icon/authbg-icon.png" />
        </div>
      </div>

      <!-- 认证失败 -->
      <div class="auth-card danger" v-else-if="userInfo.certStatus == 4">
        <div class="left">
          <div class="title danger-title flx-align-center">
            <img src="../../../assets/icon/danger-auth-icon.png" />{{ userInfo.certType == 1 ? '个人' : '企业' }}实名认证审核失败
          </div>

        </div>
        <div class="right">
          <img src="../../../assets/icon/authbg-icon.png" />
        </div>
      </div>
      <div class="remark mt10" v-if="userInfo.certStatus == 4 && isForm == false">
        审核备注: {{ userInfo.companyVerifyRemark }}
      </div>



      <!-- 底部确认栏 -->
      <div class="footer flx-align-center"
        v-if="(userInfo.certStatus == 1 || userInfo.certStatus == 4) && (authStep == 2 || authStep == 3)">
        <el-checkbox v-model="isAgree">
          <template #default>
            我已阅读并同意<span style="color: #3972fd" @click="openDocs">《服务条款》</span>和<span style="color: #3972fd"
              @click="openDocs">《隐私协议》</span>
          </template>
        </el-checkbox>
        <div class="btns" v-if="
          (userInfo.certStatus == 1 || userInfo.certStatus == 4) &&
          isForm && authStep == 2
        ">
          <el-button @click="cancelSubmit(1)">取消</el-button>
          <el-button type="primary" @click="submitForm">{{ (authType == 2 || authType == 4) ? '下一步' : '提交资料'
          }}</el-button>
        </div>
        <div class="btns" v-else-if="
          (userInfo.certStatus == 1 || userInfo.certStatus == 4) &&
          isForm && authStep == 3
        ">
          <el-button @click="cancelSubmit(2)">取消</el-button>
          <el-button type="primary" @click="submitForm">我已认证</el-button>
        </div>
        <div class="btns" v-else-if="userInfo.certStatus == 4">
          <el-button type="primary" @click="remake">重新提交</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts" name="authentication">
import { ElMessage } from 'element-plus'
import { useUserInfo } from '@/store'
import { UploadIdCardFn, UploadOcrFn } from '@/api/Upload'
import { ref } from 'vue'
import { applyRealNameVerifyApi, getEidResultApi } from '@/api/auth'
import { toPage } from '@/utils'
const userInfo = useUserInfo()
// 是否为表单
const isForm = ref(true)
if (userInfo.certStatus == 4) {
  isForm.value = false
}
// 认证步骤
const authStep = ref<number>(1)
// 认证方式(1:企业证件认证 2:法人人脸识别 3:个人证件认证 4:人脸识别)
const authType = ref(1)
// 二维码链接
const qrUrl = ref('https://eyunai.net/mpQrcode?eToken=')
const eToken = ref('')
// 进行认证(1:企业证件认证 2:法人人脸识别 3:个人证件认证 4:人脸识别)
const toAuthenticate = (type: number) => {
  authType.value = type
  authStep.value = 2
}
const proFormRef = ref<any>()
const routerList = ref([
  { name: '账户中心', path: '/accountCenter' },
  { name: '实名认证' },
])
// 企业基本信息
const companyBasicFormCol: IFormColumnsProps[] = [
  {
    prop: 'companyImg',
    label: '企业营业执照',
    el: 'img',
    api: UploadOcrFn,
    besideProp: 'companyImgInfo',
    tips: '请上传最新的营业执照，格式要求: 原件照片、扫描件或者加盖公章的复印件，支持.jpg .jpeg .png格式照片，大小不超过5M。'
  },
  {
    prop: 'companyName',
    label: '企业名称',
    el: 'input',
  },
  {
    prop: 'companyAddress',
    label: '企业注册地址',
    el: 'input',
  },

  {
    prop: 'companyCode',
    label: '企业统一社会信用代码',
    el: 'input',
  },
]
const companyBasicForm = ref<any>({
  companyName: userInfo.companyName ? userInfo.companyName : '',
  companyAddress: userInfo.companyAddress ? userInfo.companyAddress : '',
  companyCode: userInfo.companyCode ? userInfo.companyCode : '',
  companyImg: userInfo.companyImg ? userInfo.companyImg : '',
  companyImgInfo: '',
})
// 企业法人信息
const legalPersonFormCol: IFormColumnsProps[] = [

  {
    prop: 'name',
    label: '姓名',
    el: 'input',
  },
  {
    prop: 'idCard',
    label: '身份证号码',
    el: 'input',
  },
]
const legalPersonForm = ref<any>({
  name: userInfo.realName
    ? userInfo.realName
    : '',
  idCard: ''
})
// 个人信息
const personFormCol: IFormColumnsProps[] = [
  {
    prop: 'idCardFrontImg',
    label: '上传照片(人像面)',
    el: 'img',
    api: UploadIdCardFn,
    besideProp: 'cardFront',
    itemLabelWidth: 110,
    tips: '支持jpg、png格式，大小不超过5M，请确保身份证信息清晰'
  },
  {
    prop: 'idCardBackImg',
    label: '上传照片(国徽面)',
    el: 'img',
    api: UploadIdCardFn,
    besideProp: 'cardBack',
    itemLabelWidth: 110,
    tips: '支持jpg、png格式，大小不超过5M，请确保身份证信息清晰'
  },
  {
    prop: 'name',
    label: '姓名',
    el: 'input',
  },
  {
    prop: 'idCard',
    label: '身份证号码',
    el: 'slot',

  },
]
const personForm = ref<any>({
  name: '',
  idCard: '',
  idCardFrontImg: '',
  idCardBackImg: '',
  cardFront: {},
  cardBack: {}
})
watch(
  () => companyBasicForm.value.companyImgInfo,
  (newVal) => {
    companyBasicForm.value.companyImg = newVal.url
    legalPersonForm.value.name = newVal.ocrzs.data.license_main['法定代表人']
    companyBasicForm.value.companyAddress = newVal.ocrzs.data.license_main['住所']
    companyBasicForm.value.companyName = newVal.ocrzs.data.license_main['名称']
    companyBasicForm.value.companyCode = newVal.ocrzs.data.license_main['统一社会信用代码']
  }
)
watch(
  () => personForm.value.cardFront,
  (newVal) => {
    personForm.value.idCardFrontImg = newVal.url
    personForm.value.name = newVal.ocrzs.data.card_front.name
    personForm.value.idCard = newVal.ocrzs.data.card_front.id_number

  }
)
watch(
  () => personForm.value.cardBack,
  (newVal) => {
    personForm.value.idCardBackImg = newVal.url
  }
)
// todo: 检查表单是否完善（需根据类别
const hasEmptyValues = (): boolean => {
  if (authType.value == 1 || authType.value == 2) {
    for (const key in companyBasicForm.value) {
      console.log(key, companyBasicForm.value[key])
      if (companyBasicForm.value[key] === '' && key !== 'companyImgInfo') {
        return true // 如果有空值，返回 true
      }
    }
    for (const key in legalPersonForm.value) {
      console.log(key, legalPersonForm.value[key])
      if (legalPersonForm.value[key].trim() === '') {
        return true // 如果有空值，返回 true
      }
    }
  } else {
    for (const key in personForm.value) {
      if (!personForm.value[key]) {
        return true // 如果有空值，返回 true
      }
    }
  }
  return false
}
const isAgree = ref(false)
const cancelSubmit = (step: number) => {
  if (step == 1) {
    authStep.value = 1
    isForm.value = true
    authType.value = 1
  } else if (step == 2) {
    authStep.value = 2
    isForm.value = true
  }
}
// 提交表单内容 根据认证方式authType分别处理,如果是2、4则需要人脸认证=>authStep=3
const submitForm = () => {
  if (hasEmptyValues()) {
    ElMessage.warning('请完善表单内容')
    return
  }
  if (!isAgree.value) {
    ElMessage.error('请先同意协议')
    return
  }
  if (authStep.value == 2) {
    if (authType.value == 1 || authType.value == 3) {
      notToAuthNym()
    } else {
      toAuthNym()
    }
  }
  else if (authStep.value == 3) {
    getEidResultApi().then((res: any) => {
      if (res.data) {
        ElMessage.success('认证成功!')
        userInfo.getUserInfo()
        // 跳转到控制台主页
        toPage('/console')
      } else {
        ElMessage.error('暂未获取到认证结果，请稍后再试')
        return
      }
    })
  }
}
// 需要人脸识别
const toAuthNym = () => {
  // 调用实名认证接口获取eToken
  const data: any = {
    certType: (authType.value == 1 || authType.value == 2) ? "2" : "1", // 2: 企业认证 1: 个人认证
    isFace: 1,
  }
  if (authType.value == 1 || authType.value == 2) {
    data.companyName = companyBasicForm.value.companyName;
    data.companyCode = companyBasicForm.value.companyCode;
    data.companyAddress = companyBasicForm.value.companyAddress;
    data.companyImg = companyBasicForm.value.companyImg;
    data.name = legalPersonForm.value.name;
    data.idCard = legalPersonForm.value.idCard;
  } else {
    data.name = personForm.value.name;
    data.idCard = personForm.value.idCard;
  }
  applyRealNameVerifyApi(data).then((res: any) => {
    if (res.code == 200 && res.data.success) {
      eToken.value = res.data.eidToken;
      qrUrl.value = 'https://eyunai.net/mpQrcode?eToken=' + eToken.value;
      authStep.value = 3; // 跳转到人脸识别页面
    } else {
      if (res.data.failReason) {
        ElMessage.error(res.data.failReason);
        return;
      }
      ElMessage.error('请检查信息是否有效');
    }
  })

}
// 不需要人脸识别
const notToAuthNym = () => {
  // 调用实名认证接口获取eToken
  const data: any = {
    certType: (authType.value == 1 || authType.value == 2) ? "2" : "1", // 2: 企业认证 1: 个人认证
    isFace: 0,
  }
  if (authType.value == 1 || authType.value == 2) {
    data.companyName = companyBasicForm.value.companyName;
    data.companyCode = companyBasicForm.value.companyCode;
    data.companyAddress = companyBasicForm.value.companyAddress;
    data.companyImg = companyBasicForm.value.companyImg;
    data.name = legalPersonForm.value.name;
    data.idCard = legalPersonForm.value.idCard;
  } else {
    data.name = personForm.value.name;
    data.idCard = personForm.value.idCard;
  }
  applyRealNameVerifyApi(data).then((res: any) => {
    if (res.code == 200 && res.data.success) {
      ElMessage.success('提交成功')
      userInfo.getUserInfo()
      //跳转到控制台主页
      toPage('/console')
    } else {
      if (res.data.failReason) {
        ElMessage.error(res.data.failReason);
        return;
      }
      ElMessage.error('请检查信息是否有效');
    }
  })
};
// 重新提交表单
const remake = () => {
  isForm.value = true
}
// 查看文档
const openDocs = () => {
  let a = window.location.origin + '/#/'
  a += 'docsView/ProductAgreement'
  window.open(a, '_blank')
}
</script>
<style lang="scss" scoped>
.authentication {
  border: 0;
}

.select-content {
  z-index: 11;

  .tip {
    width: 100%;
    padding: 20px;
    background: linear-gradient(86deg, #FEF0F0 0%, rgba(255, 255, 255, 0) 100%);
    border-radius: 10px 10px 10px 10px;
    font-weight: 400;
    font-size: 16px;
    color: #FF4151;

  }

  .title {

    font-weight: bold;
    font-size: 24px;

  }

  .select-box {
    display: flex;
    align-items: center;
    justify-content: space-around;

    .select-card {
      width: 750px;
      height: 510px;
      background: #F7F8FB;
      border-radius: 10px 10px 10px 10px;
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 30px 34px;
      gap: 40px;

      .title {

        font-weight: bold;
        font-size: 30px;

      }

      .intro {
        font-weight: 400;
        font-size: 20px;
        color: #83889D;
        min-height: 50px;
      }

      .selection {
        width: 690px;
        height: 121px;
        background: #FFFFFF;
        box-shadow: 0px 3px 16px 1px rgba(208, 212, 230, 0.5);
        border-radius: 10px 10px 10px 10px;
        border: 1px solid #3972FD;
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 28px 30px;

        .info {
          display: flex;
          flex-direction: column;
          gap: 8px;

          .label {

            font-weight: bold;
            font-size: 24px;

          }

          .detail {

            font-weight: 400;
            font-size: 18px;
            color: #3972FD;

          }


        }

        .btn {
          width: 138px;
          height: 49px;
          font-size: 18px;
        }
      }
    }
  }

}

.face-window {
  display: flex;
  flex-direction: column;
  /* justify-content: center; */
  align-items: center;
  padding-top: 62px;
  gap: 30px;

  .label {
    font-weight: bold;
    font-size: 30px;
  }

  .code-img {
    width: 214px;
    height: 214px;
    border-radius: 0px 0px 0px 0px;
    border: 1px solid #E5E5E5;
  }

  .tip {

    font-weight: 400;
    font-size: 16px;
    color: #83889D;

  }
}

.auth-card {
  margin-top: 30px;
  width: 100%;
  height: 192px;
  border-radius: 10px 10px 10px 10px;
  border: 1px solid #e5e5e5;
  padding: 24px 40px;
  display: grid;
  grid-template-columns: 1fr 127px;

  .left {
    display: flex;
    flex-direction: column;
    gap: 34px;

    .title {
      gap: 4px;

      img {
        height: 28px;
        width: 28px;
      }

      font-weight: bold;
      font-size: 20px;
    }

    .list {
      display: grid;
      grid-template-columns: 1fr 1fr 1fr;
      column-gap: 20px;
      grid-template-rows: 1fr 1fr;

      .item {
        display: flex;

        .label {
          font-weight: 400;
          font-size: 16px;
          color: #83889d;
          min-width: 170px;
        }

        .value {
          font-weight: 400;
          font-size: 16px;
          color: #000000;
        }
      }
    }
  }

  .right {
    display: flex;
    justify-content: center;
    align-items: center;

    img {
      width: 127px;
      height: 144px;
    }
  }
}

.success {
  background: linear-gradient(180deg, #ddffe6 0%, #ffffff 100%);
}

.success-title {
  font-weight: bold;
  font-size: 20px;
  color: #3ba46f;
}

.warn {
  background: linear-gradient(180deg, #fdf5e3 0%, #ffffff 100%);
}

.warn-title {
  font-weight: bold;
  font-size: 20px;
  color: #fba201;
}

.danger {
  background: linear-gradient(180deg, #fef0f0 0%, #ffffff 100%);
}

.danger-title {
  font-weight: bold;
  font-size: 20px;
  color: #ff4151;
}

.footer {
  width: 100%;
  min-height: 89px;
  background: #f7f8fb;
  border-radius: 10px 10px 10px 10px;
  margin-top: auto;
  padding: 20px;

  .btns {
    margin-left: auto;

  }

  .el-button {
    width: 138px;
    height: 49px;
    border-radius: 4px 4px 4px 4px;
    font-size: 18px;
  }
}
</style>
