<template>
  <Banner :is-search="false" :banner-type="1" />
  <ShowBox title="优惠券">
    <template v-slot:box>
      <div class="card-box" style="margin-bottom: 80px">
        <div class="card-list-3">
          <CouponCard v-for="(item, index) in couponList" card-type="normal" :name="item.name" :type="item.type"
            :key="index" :couponRemark="item.couponRemark" :deductionAmount="item.deductionAmount"
            :useTimeStart="item.useTimeStart" :useTimeEnd="item.useTimeEnd" :customerReceive="item.customerReceive"
            :customerId="item.id" :show-btn="true" @success="success"></CouponCard>
        </div>
      </div>
    </template>
  </ShowBox>
  <ShowBox title="算力资源">
    <template v-slot:box>
      <div class="card-box mb50">
        <div class="card-list-4">
          <ServerCard v-for="item in showServerList" :key="item.ecsId" :id="item.ecsId" :title="item.name"
            :content="item.remark" :specification="item.ecsScale" :cpu-number="item.cpuNumber"
            :gpu-model="item.gpuModel" :memory-size="item.memorySize" :bandwidth="item.bandWidth"
            :price="item.hoursPrice" :regionsZones="item.regionsZones"></ServerCard>
        </div>
      </div>
    </template>
  </ShowBox>
  <ShowBox title="应用">
    <template v-slot:box>
      <div class="card-box mb50">
        <div class="card-list-3">
          <AppCard v-for="item in appList" :key="item.id" @click="toDetail('app', item.id)" :name="item.name"
            :intro="item.intro" :publish-user-avatar="item.publishUserAvatar" :publish-user-name="item.publishUserName"
            :create-time="item.createTime" :isGood="item.isGood" :img="item.img" :tags="item.tags"></AppCard>
        </div>
      </div>
    </template>
  </ShowBox>
  <ShowBox title="模型">
    <template v-slot:box>
      <div class="card-box" style="margin-bottom: 50px">
        <div class="card-list-4">
          <ShowCard v-for="item in modelList" :key="item.id" :img-src="item.cover" :title="item.name"
            :intro="item.intro" :info="item" :is-favorite="item.isFavorite" :is-visited="item.isVisited"
            @click="toDetail('model', item.id)"></ShowCard>
        </div>
      </div>
    </template>
  </ShowBox>
  <ShowBox title="数据">
    <template v-slot:box>
      <div class="card-box data-list">
        <div class="card-list-4">
          <ShowCard v-for="item in dataList" :key="item.id" :img-src="item.cover" :title="item.name" :info="item"
            :intro="item.intro" :is-favorite="item.isFavorite" :is-visited="item.isVisited"
            @click="toDetail('data', item.id)"></ShowCard>
        </div>
      </div>
      <div class="app-nav-box">
        <div class="app-nav-list">
          <img src="../assets/images/hp-czsc.png" @click="navTo('czsc')" />
          <img src="../assets/images/hp-czsp.png" @click="navTo('czsp')" />
          <img src="../assets/images/hp-bzzx.png" @click="navTo('bzzx')" />
        </div>
      </div>
    </template>
  </ShowBox>

  <div class="bg-circle"></div>
</template>

<script setup lang="ts" name="HomeView">
import { getCouponListAPI } from '@/api/accountManagement'
import { getApplyListAPI } from '@/api/application'
import { getModelListAPI } from '@/api/former'
import { getDataListAPI } from '@/api/record'
import { getBannerListApi } from '@/api/banner'
import { toPage } from '@/utils'

import { getComputedListApi } from '@/api/instance'
import { useTable } from '@/hooks/useTable'
const couponList = ref<any>([])
const modelList = ref<any>([])
const appList = ref<any>([])
const dataList = ref<any>([])
const form = ref<any>({
  pageNo: 1,
  pageSize: 30,
})
// 查看详情
const toDetail = (type: string, id: number) => {
  switch (type) {
    case 'app':
      toPage(`/application/detail?id=${id}`)
      window.scrollTo({ top: 0, behavior: 'smooth' })
      break
    case 'model':
      toPage(`/model/detail?id=${id}`)
      window.scrollTo({ top: 0, behavior: 'smooth' })
      break
    case 'data':
      toPage(`/data/detail?id=${id}`)
      window.scrollTo({ top: 0, behavior: 'smooth' })
      break
  }
}
// 算力资源列表

const getCouponList = async () => {
  let { data } = await getCouponListAPI({
    ...form.value,
  })
  couponList.value = data.list
}
const getApplyList = async () => {
  let { data } = await getApplyListAPI({
    ...form.value,
  })
  appList.value = data.list
}
const getModelList = async () => {
  let { data } = await getModelListAPI({
    ...form.value,
  })
  modelList.value = data.list
}
const getDataList = async () => {
  let { data } = await getDataListAPI({
    ...form.value,
  })
  dataList.value = data.list
}

const navTo = (flag: string) => {
  console.log(flag)
}
const success = () => {
  getCouponList()
}
const { tableData: serverList } = useTable({
  requestApi: getComputedListApi,
  initParams: { status: '1' },
})
const showServerList = computed(() => {
  return serverList.value.slice(0, 8)
})
onMounted(() => {
  getCouponList()
  getApplyList()
  getModelList()
  getDataList()
})
</script>
<style lang="scss" scoped>
.b {
  height: 556px;
  padding-top: 100px;
  background: url('@/assets/images/home-top.png') no-repeat;
  background-size: 100%;
  z-index: 5;
}

.bg-circle {
  width: 1025px;
  height: 1025px;
  background: #89c9ff;
  opacity: 0.2;
  filter: blur(50px);
  position: absolute;
  top: 1822px;
  left: 448px;
  z-index: -1;
}

.card-box {
  .card-list-3 {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 30px;
  }

  .card-list-4 {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 58px;
  }
}

.app-nav-box {
  margin: 41px 0 111px;

  .app-nav-list {
    display: flex;
    gap: 30px;

    img {
      width: 470px;
      height: 125px;
      background: radial-gradient(#d5e6ff 0%, #edf2ff 55%, #e6fdff 100%);
      border-radius: 16px 16px 16px 16px;
      border: 1px solid #ffffff;

      &:hover {
        cursor: pointer;
      }
    }
  }
}

.data-list {
  margin-bottom: 109px;
}
</style>
