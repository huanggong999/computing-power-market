<template>
  <div class="table-box">
    <el-row>
      <el-col :span="24" class="top-card">
        <TipText class="fwb" content="我的资源" fontSize="20" />
        <div class="title mr16 mt46">
          算力服务器
          <span class="ml50"
            ><span class="num pr20" style="margin-left: -20px">{{
              computedServerNum
            }}</span
            >台</span
          >
        </div>
        <div class="mt28 flx-justify-between first-row">
          <ConsoleCard
            v-for="(item, index) in resourceOverview"
            :key="index"
            :data="item"
            type="resourceOverview"
            @click="toPage('/cloud/instance')"
          >
          </ConsoleCard>
          <img
            src="@/assets/images/createExam.png"
            class="create-exam"
            @click="toPage('/cloud/createInstance')"
          />
        </div>
        <div class="mt30">
          <el-table
            :data="regionTable"
            style="width: 100%"
            :cell-style="{
              textAlign: 'center',
              fontSize: '13px',
            }"
            :header-cell-style="{
              textAlign: 'center',
              fontSize: '13px',
            }"
          >
            <el-table-column label="地域">
              <template #default="scope">{{
                regionList[scope.row.region]
              }}</template>
            </el-table-column>
            <el-table-column prop="ecsNumber" label="算力服务器" />
            <el-table-column prop="ecsRunningNumber" label="运行中" />
            <el-table-column prop="ecsStoppedNumber" label="已停止" />
            <el-table-column prop="cloudStorageNumber" label="云盘" />
            <el-table-column prop="imgNumber" label="镜像" />
            <el-table-column label="操作">
              <template #default>
                <div class="create" @click="toPage('/cloud/createInstance')">
                  新建
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-col>
    </el-row>
    <div class="mt20 bottom" style="display: grid">
      <div class="card" style="position: relative">
        <TipText class="fwb" content="帮助文档" fontSize="20" />
        <div class="urls mt40 pb22" style="display: grid">
          <div
            class="url"
            v-for="(item, index) in helpCenterData"
            :key="index"
            @click="handleClick(item.url)"
          >
            · {{ item.label }}
          </div>
        </div>
        <div class="more" style="top: 20px; left: 1040px">查看更多 ></div>
      </div>
      <div class="card" style="position: relative">
        <TipText class="fwb" content="常用功能" fontSize="20" />
        <div class="f-box mt28">
          <FunctionCard
            v-for="(item, index) in functionList"
            :key="index"
            :data="item"
          ></FunctionCard>
          <div class="more" style="top: 20px; left: 380px">查看更多 ></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts" name="Resource">
import ConsoleCard from '../components/ConsoleCard.vue'
import FunctionCard from '../components/FunctionCard.vue'
import { useUserInfo } from '@/store'
import { getHomeDataApi, getResourceDataApi } from '@/api/console'
import { toPage } from '@/utils'
const user = useUserInfo()
const consoleForm = ref<any>({
  ecsNumber: '',
  containerNumber: '',
  objectStorageNumber: '',
  imgNumber: '',
})
const resourceForm = ref<any>({
  ecsRunningNumber: '',
  ecsStoppedNumber: '',
  expiringNumber: '',
  expireNumber: '',
})
const regionTable = ref<any>([])
const regionList: any = {
  CN_BEIJING: '华北2(北京)',
  CN_SHANGHAI: '华东2(上海)',
  CN_GUANGZHOU: '华南1(广州)',
  AP_SOUTHEAST_1: '亚太东南1(柔佛)',
  CN_HONGKONG: '香港',
}
const getConsoleData = async () => {
  const { data } = await getHomeDataApi()
  consoleForm.value = data
}
const getResourceData = async () => {
  const { data } = await getResourceDataApi()
  resourceOverview.value.cloudServer.number = data.ecsRunningNumber
  resourceOverview.value.containerCluster.number = data.ecsStoppedNumber
  resourceOverview.value.objectStorage.number = data.expiringNumber
  resourceOverview.value.mirrorWarehouse.number = data.expireNumber

  regionTable.value = data.regionsNumberList
}
onMounted(() => {
  getConsoleData()
  getResourceData()
})

const resourceOverview = ref({
  cloudServer: {
    title: '运行中',
    number: '0',
    bgColor: 'linear-gradient( 135deg, #DDFFE6 0%, #FFFFFF 100%)',
    numColor: '#3BA46F',
  },
  containerCluster: {
    title: '已停止',
    number: '0',
    bgColor: 'linear-gradient( 136deg, #E6EEFF 0%, #FFFFFF 100%)',
    numColor: '#3972FD',
  },
  objectStorage: {
    title: '将到期',
    number: '0',
    bgColor: 'linear-gradient( 134deg, #FEF0F0 0%, #FFFFFF 100%)',
    numColor: '#FF4151',

    btnBg: '#D7E2F6',
    btnColor: '#3972FD',
    IsCup: true,
  },
  mirrorWarehouse: {
    title: '已过期',
    number: '0',
    bgColor: 'linear-gradient( 135deg, #FDF5E3 0%, #FFFFFF 100%)',
    numColor: '#FBA201',
    // btnText: '开发票',
    btnBg: '#D7E2F6',
    btnColor: '#3972FD',
    IsCup: true,
    // btnClick: () => toPage('/invoiceManagement'),
  },
})
const computedServerNum = computed(
  () =>
    Number(resourceOverview.value.cloudServer.number) +
    Number(resourceOverview.value.containerCluster.number) +
    Number(resourceOverview.value.mirrorWarehouse.number)
)
const functionList = [
  {
    imgSrc: `https://ai-cloud-system.tos-cn-beijing.volces.com/imgs/images/renew-icon.png`,
    title: '充值中心',
    url: '/rechargeCenter',
  },
  {
    imgSrc:
      'https://ai-cloud-system.tos-cn-beijing.volces.com/imgs/images/quota-icon.png',
    title: '容器列表',
    url: '/containerList',
  },
  {
    imgSrc:
      'https://ai-cloud-system.tos-cn-beijing.volces.com/imgs/images/produce-icon.png',
    title: '订单管理',
    url: '/order',
  },
  {
    imgSrc:
      'https://ai-cloud-system.tos-cn-beijing.volces.com/imgs/images/size-icon.png',
    title: '合同管理',
    url: '/contractManagement',
  },
]
const helpCenterData = [
  {
    label: '算力服务器-产品简介',
    url: '/helpDocs?id=46',
  },
  {
    label: '算力服务器-快速入门',
    url: '/helpDocs?id=47',
  },
]
const handleClick = (url: string) => {
  toLocalPage(url)
}
const toLocalPage = (url: string) => {
  toPage(url)
  window.scrollTo({ top: 0, behavior: 'smooth' })
}
</script>
<style lang="scss" scoped>
@import './index.scss';
</style>
