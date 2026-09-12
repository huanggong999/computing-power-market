<template>
  <div class="table-box">
    <el-radio-group v-model="tableType">
      <el-radio-button
        v-for="item in radioGroup"
        :key="item.value"
        :value="item.value"
      >
        {{ item.label }}
      </el-radio-button>
    </el-radio-group>
    <div class="card table-box">
      <BillOverview v-if="radioForm?.value === 'billOverview'" />
      <BillPage v-if="radioForm?.value === 'billDetails'" />
    </div>
  </div>
</template>

<script setup lang="ts" name="ExpenseBill">
import BillOverview from "./components/BillOverview.vue";
import BillPage from "./components/BillPage.vue";

const tableType = ref("billOverview");
const radioGroup = [
  { value: "billOverview", label: "账单总览" },
  { value: "billDetails", label: "账单明细" },
];
const radioForm = computed(() =>
  radioGroup.find((el) => el.value === tableType.value)
);
</script>
<style lang="scss" scoped></style>
