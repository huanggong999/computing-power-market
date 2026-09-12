<template>
  <el-drawer
    :z-index="1000"
    v-model="model"
    :title="props.title"
    :size="props.size"
    :destroy-on-close="true"
    :before-close="closePopover"
  >
    <slot />
    <template #footer v-if="props.IsFooter">
      <div style="flex: auto">
        <el-button @click="closePopover">取消</el-button>
        <el-button v-if="props.isSave" @click="save">保存</el-button>
        <el-button v-if="!props.disabled" type="primary" @click="submit">
          提交
        </el-button>
      </div>
    </template>
  </el-drawer>
</template>

<script setup lang="ts" name="Drawer">
import { useVModel } from "@/utils/useVModel";

interface IParamsDrawer {
  modelValue: boolean;
  IsFooter?: boolean; // 隐藏底部
  title: string; // 标题
  disabled?: boolean; // 禁用提交按钮
  size?: string;
  isSave?: boolean;
}

const props = withDefaults(defineProps<IParamsDrawer>(), {
  IsFooter: true,
  size: "30%",
});

const emit = defineEmits([
  "submit",
  "closePopover",
  "save",
  "update:modelValue",
]);
const model = useVModel(props, "modelValue", emit);
const closePopover = () => emit("closePopover");
const submit = () => emit("submit");

const save = () => emit("save");
</script>
<style lang="scss" scoped></style>
