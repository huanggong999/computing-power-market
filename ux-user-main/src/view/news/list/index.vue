<template>
  <div class="application">
    <div class="bar mb50">
      <img src="../../../assets/images/newsbar.png" />
    </div>
    <div class="content-box card">
      <newsCard v-for="item in tableData" :key="item.id" :data="item" @click="toPage(`/news/detail?id=${item.id}`)">
      </newsCard>
    </div>
    <Pagination v-if="tableData.length" :page-data="pageData" :PageChange="getList" />
  </div>
</template>

<script setup lang="ts" name="TaskCenter">
import { getNewsListApi } from '@/api/news';
import { useTable } from '@/hooks/useTable';
import newsCard from '../components/newsCard.vue';
import { toPage } from '@/utils'
import { ref } from 'vue'
const { tableData, pageData, getList } =
  useTable({ requestApi: getNewsListApi });
</script>
<style lang="scss" scoped>
.application {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 100px;

  .bar {
    height: 370px;
    width: 100%;

    img {
      height: 100%;
      width: 100%;
    }
  }

}
</style>
