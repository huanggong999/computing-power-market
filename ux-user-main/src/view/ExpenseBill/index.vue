<template>
  <div class="table-box position-relative">
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
      <BillOverview
        v-if="radioForm?.value === 'billOverview'"
        :title="radioForm.label"
      />
      <BillPage
        v-if="radioForm?.value === 'billDetails'"
        :title="radioForm.label"
      />
    </div>
  </div>
</template>

<script setup lang="ts" name="ExpenseBill">
import BillPage from "./components/BillPage.vue";
import BillOverview from "./components/BillOverview.vue";
const tableType = ref("billOverview");
const radioForm = computed(() =>
  radioGroup.find((el) => el.value === tableType.value)
);

const radioGroup = [
  { value: "billOverview", label: "账单总览" },
  { value: "billDetails", label: "账单明细" },
];
</script>
<style lang="scss" scoped></style>
