<template>
  <div class="application b">
    <Banner
      :is-search="true"
      search-title="算力"
      v-model="searchParam.name"
      @search="getList"
    ></Banner>
    <div class="app-box">
      <ServerCard
        v-for="item in serverList"
        :key="item.ecsId"
        :id="item.ecsId"
        :title="item.name"
        :content="item.remark"
        :specification="item.ecsScale"
        :cpu-number="item.cpuNumber"
        :gpu-model="item.gpuModel"
        :memory-size="item.memorySize"
        :bandwidth="item.bandWidth"
        :price="item.hoursPrice"
        :payPriceText="item.payPriceText"
        :payPriceColor="item.payPriceColor"
        :productType="1"
        :regionsZones="item.regionsZones"
        :zoneList="item.zoneList"
      ></ServerCard>
    </div>
  </div>
</template>

<script setup lang="ts" name="TaskCenter">
import { ref } from "vue";
// import { computedList } from '../../assets/js/index'
import { useTable } from "@/hooks/useTable";
import { getComputedListApi } from "@/api/instance";
import {
  showFullScreenLoading,
  tryHideFullScreenLoading,
} from "@/config/serviceLoading";

// const serverList = ref(computedList)
const {
  tableData: serverList,
  pageData,
  getList,
  searchFn,
  searchParam,
} = useTable({
  requestApi: getComputedListApi,
  initParams: { productType: 1 },
});

showFullScreenLoading();

watch(
  () => pageData.value.total,
  () => tryHideFullScreenLoading(),
  { deep: true }
);

const copyList = ref([]);
copyList.value = JSON.parse(JSON.stringify(serverList.value));
const search = () => {
  serverList.value = copyList.value.filter((item: any) => {
    return item.title.indexOf(searchParams.value) > -1;
  });
};
</script>
<style lang="scss" scoped>
.application {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.b {
  background: url("@/assets/images/app-top.png") no-repeat;
}

.app-box {
  display: grid;
  width: 1470px;
  min-height: 1449px;
  margin-top: -160px;
  display: grid;
  grid-template-columns: repeat(4, 1fr);

  grid-template-rows: repeat(2, 1fr);
  gap: 34px;
  margin-bottom: 80px;
}
</style>
