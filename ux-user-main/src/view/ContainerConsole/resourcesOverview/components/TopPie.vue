<template>
  <div class="itemPie card flx-justify-between">
    <div class="tac">
      <div class="fz44 c0">{{ TopPieData.total }}</div>
      <div class="mt17 c6">{{ TopPieData.title }}</div>
    </div>
    <ComPie class="pie" :pieData="TopPieData.data" />
    <div class="fz16 c6">
      <div
        class="mt14"
        v-for="(item, index) in TopPieData.data"
        :key="index"
        :class="classEnum[item.name]"
      >
        <span>{{ item.name }}</span>
        <span class="c0 ml20">{{ item.value }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts" name="TopPie">
import ComPie from "./ComPie.vue";

interface ITopPieDataProps {
  data: TKeyValue[];
  total: number | string;
  title: string;
}

defineProps<{ TopPieData: ITopPieDataProps }>();

const classEnum: TKeyValue = {
  正常: "normal",
  异常: "abnormal",
  其他: "other",
};
</script>
<style lang="scss" scoped>
.itemPie {
  height: 200px;
  padding: 0 38px 0 50px;
  font-size: 20px;

  .pie {
    width: 40%;
  }
  .fz44 {
    font-size: 44px;
  }
  .c6 {
    color: #666;
  }
  .c0 {
    color: #000;
  }
  .fz16 {
    font-size: 16px;
  }
  .normal,
  .abnormal,
  .other {
    position: relative;
    &::before {
      content: "";
      width: 8px;
      height: 8px;
      border-radius: 50%;
      position: absolute;
      left: -20px;
      bottom: 5px;
    }
  }
  .normal::before {
    background: #3ba46f;
  }
  .abnormal::before {
    background: #ff4151;
  }
  .other::before {
    background: #fba201;
  }
}
</style>
