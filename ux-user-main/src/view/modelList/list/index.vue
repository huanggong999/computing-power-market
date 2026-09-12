<template>
  <div class="application b">
    <Banner
      :is-search="true"
      search-title="模型"
      v-model="searchParams"
      @search="search"
    ></Banner>
    <div class="app-box">
      <ShowCard
        v-for="item in modelList"
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
        v-if="modelList.length"
        :page-data="pageData"
        :PageChange="getList"
        :pageSizes="[9, 18, 56, 118]"
      /> -->
  </div>
</template>

<script setup lang="ts" name="TaskCenter">
import { getModelListAPI } from '@/api/former'
import { IModelItem } from '@/api/types/home'
import { toPage } from '@/utils'
const searchParams = ref('')
const modelList = ref<IModelItem[]>([])
const form = ref({
  pageNo: 1,
  pageSize: 30,
  name: '',
})

const getModelList = async () => {
  let { data } = await getModelListAPI({
    ...form.value,
  })
  modelList.value = data.list
}
const search = (e: string) => {
  form.value.name = searchParams.value
  getModelList()
}
const toDetail = (id: any) => {
  toPage(`/model/detail?id=${id}`)
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
  margin-top: -160px;
  min-height: 1449px;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  column-gap: 58px;
  grid-template-rows: repeat(5, 1fr);
  margin-bottom: 80px;
}
</style>
