<template>
  <div class="application b">
    <Banner :is-search="true" search-title="应用" v-model="searchParams" @search="search"></Banner>
    <div class="app-box">
      <div class="app-left">
        <Sidebar :info="applyType" @screen="screen" />
      </div>
      <div class="app-right">
        <AppCard v-for="item in appList" :key="item.id" @click="toDetail(item)" :name="item.name" :intro="item.intro"
          :publish-user-avatar="item.publishUserAvatar" :publish-user-name="item.publishUserName"
          :create-time="item.createTime" :isGood="item.isGood" :img="item.img" :tags="item.tags"></AppCard>
        <div></div>
        <!-- <Pagination
            :page-data="pageData"
            :PageChange="getList"
            :pageSizes="[9, 18, 56, 118]"
          /> -->
      </div>
    </div>
  </div>
</template>

<script setup lang="ts" name="TaskCenter">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

import { getApplyTypeListAPI } from '@/api/application'
import { useTable } from '@/hooks/useTable'
import Sidebar from './components/Sidebar.vue'
import { getApplyListAPI } from '@/api/application'
import { IAppItem } from '@/api/types/home'

// const { tableData, pageData, getList } = useTable({
//   requestApi: getApplyListAPI,
//   requestAuto: false,
// })
// pageData.value.pageSize = 9
// getList()
const router = useRouter()
let searchParams = ref('')
const applyType = ref([])
const form = ref({
  pageNo: 1,
  pageSize: 30,
  typeId: '',
  name: '',
})
const appList = ref<IAppItem[]>([])
const toDetail = (item: any) => {
  router.push({ path: '/application/detail', query: { id: item.id } })
  window.scrollTo({ top: 0, behavior: 'smooth' })
}
const getApplyTypeList = async () => {
  let { data } = await getApplyTypeListAPI()
  applyType.value = data
}
const getApplyList = async () => {
  let { data } = await getApplyListAPI({
    ...form.value,
  })
  appList.value = data.list
}
const screen = (e: string) => {
  form.value.typeId = e
  getApplyList()
}
const search = (e: string) => {
  form.value.name = searchParams.value
  getApplyList()
}
onMounted(() => {
  getApplyTypeList()
  getApplyList()
})
</script>
<style lang="scss" scoped>
.application {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.b {
  background: url('@/assets/images/app-top.png') no-repeat;
}

.app-box {
  display: grid;
  grid-template-columns: 380px 1090px;
  margin-top: -160px;

  .app-left {
    .left-card {
      .card-top {
        display: flex;
        align-items: center;
        gap: 14px;
        margin-bottom: 32px;

        img {
          width: 40px;
          height: 40px;
        }

        .title {
          font-weight: 400;
          font-size: 26px;
          color: #000000;
        }
      }

      .card-content {
        .container {
          display: flex;
          flex-wrap: wrap;
          /* 允许换行 */
          padding: 0;
          /* 去掉默认内边距 */
          list-style-type: none;
          /* 去掉默认列表样式 */
        }

        .item {
          width: calc(50% - 10px);
          /* 每个项目占一半的宽度，减去间距 */
          margin-bottom: 26px;
          /* 项目间的垂直间距 */
          box-sizing: border-box;
          /* 包含内边距和边框 */
          position: relative;
          /* 使伪元素相对于列表项定位 */
          font-weight: 400;
          font-size: 22px;
          color: #83889d;
          padding-left: 30px;
        }

        .item::before {
          content: '•';
          /* 圆点符号 */
          position: absolute;
          left: -8px;
          color: #83889d;
        }

        .liActive {
          color: #3972fd;
        }
      }
    }
  }

  .app-right {
    width: 1090px;
    background: #f7f8fb;
    border-radius: 16px 16px 16px 16px;
    border: 1px solid #ffffff;

    display: grid;
    grid-template-columns: repeat(2, 1fr);
    padding: 40px 50px;
    gap: 40px;
  }
}
</style>
