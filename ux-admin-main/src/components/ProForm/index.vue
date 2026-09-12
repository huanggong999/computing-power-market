<template>
  <el-form
    ref="formRef"
    :model="model"
    :rules="rules"
    :label-width="props.labelWidth"
  >
    <template v-for="item in props.formColumns" :key="item.prop">
      <el-form-item
        class="pr"
        v-if="item.visible ?  item.visible!(model) : true "
        :label="item.label"
        :prop="item.prop"
        :label-width="item.itemLabelWidth"
      >
        <div :id="`Key_${item.prop}`" class="booth" />
        <ProFormItem
          v-if="item.el !== 'slot'"
          :column="item"
          v-model="model"
          :disabled="props.disabled || item.disabled!"
          :maxlength="item.maxLength"
        />
        <slot v-else :name="item.prop" />
      </el-form-item>
    </template>
  </el-form>
</template>

<script setup lang="ts" name="ProForm">
import ProFormItem from "./components/ProFormItem.vue";
import { useVModel } from "@/utils/useVModel";
import { FormInstance } from "element-plus";

const formRef = ref<FormInstance>();

interface IParamsProForm {
  modelValue: TKeyValue;
  formColumns: IFormColumnsProps[];
  labelWidth?: number;
  disabled?: boolean;
}

const props = withDefaults(defineProps<IParamsProForm>(), {
  labelWidth: 80,
  disabled: false,
});

// 规则校验
const ruleList = computed(() => {
  const result: TKeyValue = {};
  props.formColumns.forEach((el) => {
    let ruleObj: IRormRule = { trigger: "blur" };
    ruleObj.required = el.required === false ? false : true;
    if (!!el.validator) {
      ruleObj.validator = el.validator;
    } else {
      ruleObj.message = "必填项不为空";
    }
    result[el.prop] = [ruleObj];
  });
  return result;
});

defineExpose({ formRef });
// 表单校验规则
const rules = computed(() => ruleList.value);

const emit = defineEmits(["update:modelValue"]);
const model = useVModel(props, "modelValue", emit);
</script>
<style lang="scss" scoped>
.pr {
  position: relative;
}
.booth {
  position: absolute;
  top: -13px;
}
</style>
