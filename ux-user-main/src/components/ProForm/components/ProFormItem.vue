<template>
  <!-- input -->
  <el-input
    v-if="props.column.el === 'input'"
    :disabled="props.disabled"
    :placeholder="props.column.placeholder ?? '请输入'"
    v-model.trim="model[props.column.prop]"
    :maxlength="props.maxLength"
    :show-word-limit="true"
    :prefix-icon="props.column.prefixIcon"
    :suffix-icon="props.column.suffixIcon"
    :style="props.column.style"
  />
  <!-- number -->
  <el-input-number
    v-if="props.column.el === 'number'"
    :disabled="props.disabled"
    placeholder="请输入"
    :precision="props.column.precision ?? 0"
    v-model="model[props.column.prop]"
    :controls="props.column.controls"
    :min="props.column.minNumber"
    pass
    :step="props.column.numberStep"
    :style="props.column.style"
  />
  <!-- password -->
  <el-input
    v-if="props.column.el === 'password'"
    type="password"
    show-password
    :disabled="props.disabled"
    :placeholder="props.column.placeholder ?? '请输入'"
    v-model.trim="model[props.column.prop]"
    :prefix-icon="props.column.prefixIcon"
    :suffix-icon="props.column.suffixIcon"
    :style="props.column.style"
  />
  <!-- code -->
  <template v-if="props.column.el === 'code'">
    <el-input
      :disabled="props.disabled"
      :placeholder="props.column.placeholder ?? '请输入'"
      v-model.trim="model[props.column.prop]"
      :maxlength="props.maxLength"
      :show-word-limit="true"
      :prefix-icon="props.column.prefixIcon"
      :suffix-icon="props.column.suffixIcon"
      :style="props.column.style ?? { width: '68%' }"
    />
    <AuthCode
      class="codeBtn"
      :type="props.column.codeType!"
      :phone="model[props.column.codePhone!]"
      :tmsg="props.column.tmsg!"
      @authCodeId="(uid) => model[props.column.authUid!] = uid"
      :style="{
        height: props.column.style?.height,
        fontSize: props.column.style?.fontSize,
      }"
    />
  </template>
  <!-- phone -->
  <el-input
    v-if="props.column.el === 'phone'"
    :disabled="props.disabled"
    :placeholder="props.column.placeholder ?? '请输入手机号'"
    :maxlength="11"
    :show-word-limit="true"
    v-model.trim="model[props.column.prop]"
    :prefix-icon="props.column.prefixIcon"
    :suffix-icon="props.column.suffixIcon"
    :style="props.column.style"
  />
  <!-- switch 开关 -->
  <el-switch
    v-if="props.column.el === 'switch'"
    :placeholder="props.column.placeholder ?? '请选择'"
    v-model.trim="model[props.column.prop]"
    :active-text="props.column.activeText || '开启'"
    :inactive-text="props.column.inactiveText || '关闭'"
    :inline-prompt="props.column.inlinePrompt"
  />

  <!-- 单选按钮 -->
  <el-radio-group
    v-if="props.column.el === 'radioButton'"
    v-model="model[props.column.prop]"
  >
    <el-radio-button
      v-for="(item, index) in props.column.radioList"
      :key="index"
      :label="item.label"
      :value="item.value"
      :disabled="item.disabled"
    />
  </el-radio-group>
  <!-- 单选 -->
  <el-radio-group
    :disabled="props.disabled"
    v-if="props.column.el === 'radio'"
    v-model="model[props.column.prop]"
  >
    <el-radio
      v-for="(item, index) in props.column.radioList"
      :key="index"
      :label="item.label"
      :value="item.value"
      :disabled="item.disabled"
    />
  </el-radio-group>
  <!-- 下拉选择 -->
  <el-select
    v-if="props.column.el === 'select'"
    :disabled="props.disabled"
    :multiple="props.column.selectMultiple"
    v-model="model[props.column.prop]"
    collapse-tags-tooltip
    :max-collapse-tags="3"
  >
    <el-option
      v-for="(item, index) in props.column.selectList"
      :key="index"
      :label="item[props.column.selectLabel]"
      :value="item[props.column.selectValue]"
    ></el-option>
  </el-select>
  <!-- 日期 -->
  <el-date-picker
    :disabled="props.disabled"
    v-if="props.column.el === 'date-picker'"
    v-model="timeList"
    :type="props.column.dateType"
    rangeSeparator="至"
    start-placeholder="开始时间"
    end-placeholder="结束时间"
    :value-format="props.column.valueFormat"
    @change="changeTime"
    :time-format="props.column.timeFormat"
    @blur="blurTime"
  />
  <!-- 时间 -->
  <el-time-select
    v-if="props.column.el === 'time-select'"
    v-model="model[props.column.prop]"
    style="width: 240px"
    :placeholder="props.column.placeholder ?? '请选择'"
    :min-time="props.column.minTime"
    :start="props.column.timeSelectStart ?? '00:00'"
    :step="props.column.timeSelectStep ?? '00:15'"
    :end="props.column.timeSelectEnd ?? '23:59:59'"
    :format="props.column.valueFormat"
  />
  <!-- img -->
  <UploadImg
    :disabled="props.disabled"
    v-if="props.column.el === 'img'"
    v-model:imageUrl="model[props.column.prop]"
    v-model:info="model[props.column.besideProp]"
    :api="props.column.api"
  />
  <div v-if="props.column.tips" class="tips">
    {{ props.column.tips }}
  </div>
</template>

<script setup lang="ts" name="ProFormItem">
import { useVModel } from '@/utils/useVModel'
import AuthCode from '@/view/Login/components/AuthCode.vue'

interface IPropsProFormItem {
  modelValue: TKeyValue
  column: IFormColumnsProps
  disabled: boolean
  maxLength?: number
}
const props = defineProps<IPropsProFormItem>()
const emit = defineEmits(['update:modelValue', 'blur'])
const model = useVModel(props, 'modelValue', emit)

// 日期类型是否为数组
const type = computed(() => !props.column.dateEnum?.length)

// 日期
const timeList = ref()
const flag = ref(false)
watchEffect(() => {
  if (flag) if (type.value) timeList.value = model.value[props.column.prop]
  if (!type.value && !flag.value)
    timeList.value = [
      model.value[props.column.dateEnum![0]],
      model.value[props.column.dateEnum![1]],
    ]
})
// 修改日期
const changeTime = (val: any): void => {
  if (type.value) return (model.value[props.column.prop] = val)
  flag.value = true
  model.value[props.column.dateEnum![0]] = val[0]
}
const blurTime = () => {
  if (type.value) return
  model.value[props.column.dateEnum![1]] = timeList.value[1]
}
</script>

<style lang="scss" scoped>
.codeBtn {
  width: 28%;
  margin-left: 4%;
}
.tips {
  // margin-top: 12px;
  font-weight: 400;
  // font-size: 16px;
  width: 100%;
  color: #83889d;
}
</style>
