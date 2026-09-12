<template>
  <el-image
    :src="props.src"
    fit="cover"
    :style="`width:${props.width}px;height:${props.height}px`"
    :preview-src-list="[props.src!]"
    :preview-teleported="true"
    :class="{ circle: props.circle }"
  >
    <template #error>
      <div class="image-slot">
        <el-icon><Picture /></el-icon>
      </div>
    </template>
  </el-image>
</template>

<script setup lang="ts" name="ImagePreview">
interface IParamsImagePreview {
  height?: number;
  width?: number;
  circle?: boolean;
  src: string | undefined;
}

const props = withDefaults(defineProps<IParamsImagePreview>(), {
  height: 35,
  width: 35,
  circle: false,
});
</script>

<style lang="scss" scoped>
.circle {
  border-radius: 50% !important;
}

.el-image {
  border-radius: 5px;
  background-color: #ebeef5;
  box-shadow: 0 0 5px 3px #ccc;
  :deep(.el-image__inner) {
    transition: all 0.3s;
    cursor: pointer;
    &:hover {
      transform: scale(1.2);
    }
  }
  :deep(.image-slot) {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%;
    height: 100%;
    color: #909399;
    font-size: 30px;
  }
}
</style>
