<template>
  <div class="page">
    <div class="tip-card flx-align-center" @click="serviceDialog = true">
      <el-icon color="#3972FD"><QuestionFilled /></el-icon
      >推广有疑问？可点击添加管理员微信咨询
    </div>
    <div class="box">
      <div class="line">
        <div class="card">
          <TipText class="fwb mb28" content="推广收入" fontSize="18" />
          <div class="account-card">
            <div class="label">可提现金额</div>
            <div class="value">
              {{
                personalExtendInfo.canWithdrawalAmount
                  ? personalExtendInfo.canWithdrawalAmount
                  : '0'
              }}
            </div>
            <el-button @click="applyWithdraw">提现</el-button>
          </div>
        </div>
        <div class="card">
          <TipText class="fwb mb28" content="收款人信息" fontSize="18" />
          <div class="gray-card">
            <div class="info">
              <div class="label">真实姓名</div>
              <div class="value">
                {{
                  personalExtendInfo.bankUserName
                    ? personalExtendInfo.bankUserName
                    : '-'
                }}
              </div>
            </div>
            <div class="info">
              <div class="label">身份证</div>
              <div class="value">
                {{
                  personalExtendInfo.idCard ? personalExtendInfo.idCard : '-'
                }}
              </div>
            </div>
            <div class="info">
              <div class="label">收款方开户行</div>
              <div class="value">
                {{ personalExtendInfo.bank ? personalExtendInfo.bank : '-' }}
              </div>
            </div>
            <div class="info">
              <div class="label">银行账号</div>
              <div class="value">
                {{
                  personalExtendInfo.bankNo ? personalExtendInfo.bankNo : '-'
                }}
              </div>
            </div>
            <div class="info">
              <div class="label">开户地址</div>
              <div class="value">
                {{
                  personalExtendInfo.address ? personalExtendInfo.address : '-'
                }}
              </div>
            </div>
            <el-button type="primary" @click="updateBankInfo"
              ><el-icon><Edit /></el-icon>编辑</el-button
            >
          </div>
        </div>
      </div>
      <div class="card record">
        <TipText class="fwb mb28" content="月度结算记录" fontSize="18" />
        <ProTable
          type="none"
          :columns="columns"
          :tableData="tableData"
          :IsRefresh="false"
          :is-page="false"
        >
          <template #status="row">
            <div class="status">
              <el-tag v-if="row.status == '1'" type="warning" size="small"
                >审核中</el-tag
              >
              <el-tag v-if="row.status == '2'" type="primary" size="small"
                >通过</el-tag
              >
              <el-tag v-if="row.status == '3'" type="danger" size="small"
                >未通过</el-tag
              >
              <el-tag v-if="row.status == '4'" type="success" size="small"
                >已打款</el-tag
              >
            </div>
          </template>
        </ProTable>
        <Pagination
          :page-data="pageData"
          :PageChange="getList"
          :pageSizes="[10, 15, 30, 100]"
        />
      </div>
    </div>
    <el-dialog title="客服二维码" v-model="serviceDialog" width="200">
      <div class="service flx-center">
        <img class="service" :src="config.qrCode" />
      </div>
    </el-dialog>
    <el-dialog title="收款人信息" v-model="bankDialog" width="400">
      <ProForm
        ref="bankFormRef"
        v-model="bankForm"
        :formColumns="bankFormCol"
        style="margin-top: 20px"
      ></ProForm>
      <template #footer>
        <el-button type="primary" @click="submitBankHandler">确认</el-button>
        <el-button @click="bankDialog = false">取消</el-button>
      </template>
    </el-dialog>
    <el-dialog title="申请提现" v-model="withdrawDialog" width="400">
      <ProForm
        ref="withdrawFormRef"
        v-model="withdrawForm"
        :formColumns="withdrawFormCol"
        style="margin-top: 20px"
      ></ProForm>
      <template #footer>
        <el-button type="primary" @click="submitWithdrawHandler"
          >确认</el-button
        >
        <el-button @click="withdrawDialog = false">取消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts" name="Console">
import {
  extendUpdateBankApi,
  extendWithdrawApi,
  getExtendConfigApi,
  getExtendInfoApi,
  getExtendWithdrawListApi,
} from '@/api/partner'
import { IPersonalExtendInfo } from '@/api/types/partner'
import { useTable } from '@/hooks/useTable'
import { ElMessage } from 'element-plus'
// 个人推广信息
const personalExtendInfo = ref<IPersonalExtendInfo>({})
// 客服二维码弹窗
const serviceDialog = ref(false)
const config = ref({
  qrCode: 'https://www.baidu.com',
})

// 提现信息
const withdrawDialog = ref(false)
const withdrawForm = ref({ amount: 0 })
const withdrawFormCol: IFormColumnsProps[] = [
  {
    label: '提现金额',
    prop: 'amount',
    el: 'input',
    required: true,
    itemLabelWidth: 90,
    valueFormat: 'number',
  },
]
const applyWithdraw = () => {
  withdrawForm.value = { amount: 0 }
  withdrawDialog.value = true
}
const submitWithdrawHandler = () => {
  if (
    typeof withdrawForm.value.amount === 'string' &&
    Number(withdrawForm.value.amount) > 0 &&
    Number(withdrawForm.value.amount) <=
      Number(personalExtendInfo.value.canWithdrawalAmount)
  ) {
    extendWithdrawApi(withdrawForm.value.amount).then((res) => {
      if (res.code === 200) {
        ElMessage.success('更新成功')
        initPage()
        withdrawDialog.value = false
      }
    })
  } else {
    ElMessage.error('提现金额有误')
  }
}
// 收款人信息
const bankDialog = ref(false)
const bankForm = ref({})
const bankFormCol: IFormColumnsProps[] = [
  {
    label: '真实姓名',
    prop: 'bankUserName',
    el: 'input',
    required: true,
    itemLabelWidth: 90,
  },
  {
    label: '身份证',
    prop: 'idCard',
    el: 'input',
    required: true,
    itemLabelWidth: 90,
  },
  {
    label: '收款方开户行',
    prop: 'bank',
    el: 'input',
    required: true,
    itemLabelWidth: 90,
  },
  {
    label: '银行账号',
    prop: 'bankNo',
    el: 'input',
    required: true,
    itemLabelWidth: 90,
  },
  {
    label: '开户地址',
    prop: 'address',
    el: 'input',
    required: true,
    itemLabelWidth: 90,
  },
]
const updateBankInfo = () => {
  bankForm.value = personalExtendInfo.value
  bankDialog.value = true
}
const submitBankHandler = () => {
  extendUpdateBankApi(bankForm.value).then((res) => {
    if (res.code === 200) {
      ElMessage.success('更新成功')
      initPage()
      bankDialog.value = false
    }
  })
}
// 表格数据
const columns: ColumnProps[] = [
  { prop: 'orderNo', label: '提现单号' },

  { prop: 'withdrawalAmount', label: '提现金额' },
  { prop: 'serviceRate', label: '代扣费率' },
  { prop: 'createTime', label: '提现时间' },
  {
    prop: 'status',
    label: '提现状态',
    slot: true,
  },
]

// 获取提现分页信息
const { tableData, pageData, getList } = useTable({
  requestApi: getExtendWithdrawListApi,
  requestAuto: false,
})
// 初始化页面
const initPage = () => {
  getExtendConfigApi().then((res: any) => {
    config.value = res.data
  })
  getExtendInfoApi().then((res: any) => {
    personalExtendInfo.value = res.data
  })
  getList()
}
initPage()
</script>
<style lang="scss" scoped>
.page {
  display: flex;
  flex-direction: column;
  gap: 20px;
  position: relative;
  .tip-card {
    gap: 8px;
    min-height: 60px;
    background: linear-gradient(86deg, #e6eeff 0%, rgba(255, 255, 255, 0) 100%);
    border-radius: 10px 10px 10px 10px;
    border: 1px solid #3972fd;
    font-weight: 400;
    font-size: 18px;
    color: #000;
    padding-left: 15px;
    &:hover {
      cursor: pointer;
    }
  }
  .service {
    widows: 200px;
    height: 200px;
  }
  .box {
    display: flex;
    flex-direction: column;
    gap: 20px;
    height: 100%;
    .card {
      padding: 26px 24px;
    }
    .line {
      display: grid;
      grid-template-columns: 496px 1fr;
      gap: 20px;

      .account-card {
        position: relative;
        height: 80%;
        border-radius: 10px;
        padding: 34px 40px;
        background: url('https://ai-cloud-system.tos-cn-beijing.volces.com/imgs/userSideImage/accountBalanceBg.png')
          no-repeat;
        background-size: 100% 100%;
        color: #fff;
        .label {
          font-weight: 400;
          font-size: 20px;
          margin-bottom: 38px;
        }
        .value {
          font-weight: 400;
          font-size: 44px;
        }
        .el-button {
          position: absolute;
          top: 28px;
          left: 334px;
          width: 92px;
          height: 41px;
          background: #ffffff;
          border-radius: 21px 21px 21px 21px;
          font-weight: 500;
          font-size: 18px;
          color: #3972fd;
        }
      }
      .gray-card {
        padding: 30px;
        position: relative;
        width: 100%;
        height: 80%;
        background: #f7f8fb;
        border-radius: 10px 10px 10px 10px;
        display: grid;
        grid-template-columns: 1fr 1fr;
        .info {
          display: flex;
          align-items: center;
          gap: 30px;
          .label {
            font-weight: 400;
            font-size: 16px;
            color: #83889d;
            width: 96px;
            text-align: right;
          }
          .value {
            font-weight: 400;
            font-size: 16px;
          }
        }
        .el-button {
          position: absolute;
          top: 20px;
          left: 90%;
          width: 79px;
          height: 32px;
          background: #d7e2f6;
          border-radius: 16px 16px 16px 16px;
          border: 0;
          font-weight: 500;
          font-size: 16px;
          color: #3972fd;
        }
      }
    }
    .record {
    }
  }
}
</style>
