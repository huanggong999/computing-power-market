<template>
  <div class="empty">
    <el-empty class="empty" :description="description" />
  </div>
</template>

<script setup lang="ts" name="MyEmpty">
withDefaults(defineProps<{ description: string }>(), {
  description: "暂无数据",
});
</script>
<style lang="scss" scoped>
.empty {
  width: 100%;
}
</style>
