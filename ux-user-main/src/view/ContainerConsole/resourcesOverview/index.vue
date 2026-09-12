/** 资源概览 */
<template>
  <div class="table-box position-relative">
    <div class="flx-justify-between">
      <TopPie class="pie" :TopPieData="clusterPieData" />
      <TopPie class="pie" :TopPieData="nodePieData" />
      <TopPie class="pie" :TopPieData="workloadPieData" />
    </div>
    <div class="table-box card mt10">
      <TipText content="我的资源" class="mb10" />
      <div class="list flex">
        <ResourceCard v-for="el in 9" :key="el" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts" name="ResourcesOverview">
import ResourceCard from "./components/ResourceCard.vue";
import TopPie from "./components/TopPie.vue";

const clusterPieData = ref({
  total: "5",
  title: "集群总数",
  data: [
    { name: "正常", value: 3 },
    { name: "异常", value: 1 },
    { name: "其他", value: 1 },
  ],
});

const nodePieData = ref({
  total: "6",
  title: "节点总数",
  data: [
    { name: "正常", value: 3 },
    { name: "异常", value: 2 },
    { name: "其他", value: 1 },
  ],
});
const workloadPieData = ref({
  total: "5",
  title: "工作负载总数",
  data: [
    { name: "正常", value: 3 },
    { name: "其他", value: 2 },
  ],
  color: ["#3972FD", "#FBA201"],
});
</script>
<style lang="scss" scoped>
.pie {
  width: calc((100% - 30px) / 3);
}
.list {
  flex-wrap: wrap;
}
</style>
