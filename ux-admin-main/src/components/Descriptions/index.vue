<template>
  <el-descriptions
    :column="props.column"
    :border="props.border"
    :direction="props.direction"
    :title="props.title"
  >
    <template #extra>
      <slot name="extra" />
    </template>
    <template v-for="item in props.list" :key="item.label">
      <el-descriptions-item
        :label="item.label"
        v-if="!item.hidden"
        :align="item.align"
      >
        <slot v-if="item.slot" :name="item.value" />
        <span v-else> {{ item.value }}</span>
      </el-descriptions-item>
    </template>
  </el-descriptions>
</template>

<script setup lang="ts" name="Descriptions">
interface IParamsDescriptions {
  list: IDescriptionsItem[];
  column?: number;
  border?: boolean;
  direction?: "vertical" | "horizontal";
  title?: string;
}
const props = withDefaults(defineProps<IParamsDescriptions>(), {
  column: 1,
  border: true,
  direction: "horizontal",
});
</script>
<style lang="scss" scoped></style>
