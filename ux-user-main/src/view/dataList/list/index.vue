<template>
  <div class="application b">
    <Banner
      :is-search="true"
      search-title="数据"
      v-model="searchParams"
      @search="search"
    ></Banner>
    <div class="app-box">
      <ShowCard
        v-for="item in dataList"
        :key="item.id"
        :img-src="item.cover"
        :title="item.name"
        :info="item"
        :intro="item.intro"
        :is-favorite="item.isFavorite"
        :is-visited="item.isVisited"
        @click="toDetail(item.id)"
      ></ShowCard>
    </div>
    <!-- <Pagination
        v-if="dataList.length"
        :page-data="pageData"
        :PageChange="getList"
        :pageSizes="[9, 18, 56, 118]"
      /> -->
  </div>
</template>

<script setup lang="ts" name="TaskCenter">
import { toPage } from '@/utils'
import { ref } from 'vue'
import { getDataListAPI } from '@/api/record'
import { IDataItem } from '@/api/types/home'

let searchParams = ref('')

const dataList = ref<IDataItem[]>([])
const form = ref({
  pageNo: 1,
  pageSize: 30,
  name: '',
})
const getModelList = async () => {
  let { data } = await getDataListAPI({
    ...form.value,
  })
  dataList.value = data.list
}
const search = (e: string) => {
  form.value.name = searchParams.value
  getModelList()
}
const toDetail = (id: any) => {
  toPage(`/data/detail?id=${id}`)
  window.scrollTo({ top: 0, behavior: 'smooth' })
}
onMounted(() => {
  getModelList()
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
  width: 1470px;
  min-height: 1449px;
  margin-top: -160px;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  column-gap: 58px;
  grid-template-rows: repeat(5, 1fr);

  margin-bottom: 80px;
}
</style>
