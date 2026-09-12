<template>
  <el-dialog v-model="model" title="详情" width="65%" center>
    <FormCreate :rule="rules" :option="option" :disabled="true" />
  </el-dialog>
</template>

<script setup lang="ts" name="MyFormCreate">
import { useVModel } from "@/utils/useVModel";
import formCreate from "@form-create/element-ui";

const props = defineProps<{ modelValue: boolean; rule: any }>();
const emit = defineEmits(["update:modelValue"]);
const model = useVModel(props, "modelValue", emit);

const option = {
  submitBtn: {
    innerText: "关闭",
    type: "primary",
    click: () => (model.value = false),
  },
};

const rules = ref<any[]>([]);
watch(
  () => model.value,
  (val) => {
    if (val) return (rules.value = formCreate.parseJson(props.rule));
    rules.value = [];
  }
);
</script>
<style lang="scss" scoped></style>
