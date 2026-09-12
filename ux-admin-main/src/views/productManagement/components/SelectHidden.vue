<template>
  <el-select v-model="model" placeholder="请选择">
    <el-option
      v-for="item in props.options"
      :key="item.value"
      :label="item[props.label]"
      :value="item[props.value]"
    />
  </el-select>
</template>

<script setup lang="ts" name="SelectHidden">
import { useVModel } from "@/utils/useVModel";

const props = withDefaults(
  defineProps<{
    modelValue: string;
    options: TKeyValue[];
    label?: string;
    value?: string;
  }>(),
  { label: "label", value: "value" }
);

const emit = defineEmits(["update:modelValue"]);
const model = useVModel(props, "modelValue", emit);
</script>
<style lang="scss" scoped></style>
