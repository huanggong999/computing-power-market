<template>
  <el-dialog
    v-model="showPopover"
    :title="'设置隐藏条件'"
    center
    :destroy-on-close="true"
    :before-close="close"
  >
    <ConditionBuilder
      ref="conditionBuilderRef"
      :options="props.options"
      :dataForm="infoComputed"
    />

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="showPopover = false">取消</el-button>
        <el-button type="primary" @click="submit">确定</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts" name="FcComputed">
import ConditionBuilder from "./ConditionBuilder.vue";

const props = defineProps<{ options: TKeyValue[]; Info: TKeyValue }>();
// 弹窗开关

const showPopover = ref(false);
const open = () => (showPopover.value = true);
const close = () => (showPopover.value = false);

const selectNode = ref("");
const judgmentConditionsValue = ref("==");
const hiddenValue = ref("");

watch(showPopover, (newVal) => {
  if (newVal) {
    selectNode.value = "";
    judgmentConditionsValue.value = "==";
    hiddenValue.value = "";
  }
});

const emit = defineEmits<{
  (e: "updateInfo", info: TKeyValue): void;
}>();

const groupItem = { condition: "==", field: "", value: "" };
const infoComputed = ref<TKeyValue>({});

watch(
  () => showPopover.value,
  (newVal) => {
    if (!newVal) return (infoComputed.value = {});
    infoComputed.value = !!props.Info._computed.hidden
      ? {
          mode: props.Info._computed.hidden.mode,
          group: [...props.Info._computed.hidden.group],
          invert: true,
        }
      : { mode: "AND", group: [groupItem], invert: true };
  }
);

const conditionBuilderRef = ref<InstanceType<typeof ConditionBuilder>>();

const submit = () => {
  infoComputed.value.group = conditionBuilderRef.value?.group;
  infoComputed.value.mode = conditionBuilderRef.value?.mode;
  emit("updateInfo", infoComputed.value);
};

defineExpose({ open, close });
</script>

<style lang="scss" scoped>
.select {
  width: 240px;
}
</style>
