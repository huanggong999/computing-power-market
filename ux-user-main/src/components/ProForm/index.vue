<template>
  <el-form
    ref="formRef"
    :model="model"
    :rules="rules"
    status-icon
    :label-position="labelPosition"
    :label-width="props.labelWidth"
  >
    <!-- <el-form-item
      v-for="(item, index) in props.formColumns"
      :key="index"
      :label="item.label"
      :prop="item.prop"
      :label-width="item.itemLabelWidth"
    >
      <ProFormItem
        v-if="item.el !== 'slot'"
        :column="item"
        v-model="model"
        :disabled="props.disabled"
        :maxLength="item.maxLength"
      />
      <slot v-else :name="item.prop" />
    </el-form-item> -->
    <el-form-item
      v-for="(item, index) in columns"
      :key="index"
      :label="item.label ?? ''"
      :prop="item.prop"
      :label-width="item.itemLabelWidth"
    >
      <ProFormItem
        v-if="item.el !== 'slot'"
        v-model="model"
        :column="item"
        :disabled="props.disabled"
        :maxLength="item.maxLength"
      />
      <slot v-if="item.el === 'slot'" :name="item.prop" />
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts" name="ProForm">
import { FormInstance } from "element-plus";
import ProFormItem from "./components/ProFormItem.vue";
interface IPropsProForm {
  modelValue: TKeyValue;
  formColumns: IFormColumnsProps[];
  labelWidth?: number;
  labelPosition?: TLabelPosition;
  disabled?: boolean;
}

const formRef = ref<FormInstance>();
const props = withDefaults(defineProps<IPropsProForm>(), {
  labelWidth: 80,
  disabled: false,
  labelPosition: "right",
});
const columns = computed(() => props.formColumns);
const model = computed({
  get: () => props.modelValue,
  set: (value) => emit("update:modelValue", value),
});
// 规则校验
const generateRules = (formColumns: IFormColumnsProps[]) => {
  const result: TKeyValue = {};
  const defaultMessage = "必填项不为空";
  formColumns.forEach((el: IFormColumnsProps) => {
    const rule: IRormRule = {
      required: el.required !== false, // 默认为必填,
      message: el.required !== false ? defaultMessage : "",
      trigger: "blur",
      validator: el.validator || undefined, // 若有自定义校验器则使用
    };
    result[el.prop] = [rule]; // 将规则对象添加到对应字段的键下
  });
  return result;
};
const rules = ref(generateRules(props.formColumns));
watch(
  () => props.formColumns,
  (val) => {
    formRef.value?.resetFields();
    rules.value = generateRules(val);
  },
  { deep: true }
);

const emit = defineEmits(["update:modelValue"]);
defineExpose({ formRef });
</script>
<style lang="scss" scoped>
.el-form-item__label {
  padding-right: 60px;
}
.tips {
  margin-top: 12px;
  font-weight: 400;
  font-size: 16px;
  color: #83889d;
}
</style>
