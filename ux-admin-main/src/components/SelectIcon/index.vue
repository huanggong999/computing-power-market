<template>
  <el-input
    v-model="iconName"
    clearable
    placeholder="请输入图标名称"
    :suffix-icon="Search"
  />
  <div class="icon-body mt20">
    <div
      class="icon-item flx-align-center mb10"
      v-for="(name, index) in iconList"
      :index="index"
      @click="selectedIcon(name)"
    >
      <component :is="name" style="width: 2rem" :key="index" />
      <span class="icon-name ml5">{{ name }}</span>
    </div>
  </div>
</template>

<script setup lang="ts" name="SelectIcon">
import { Search } from "@element-plus/icons-vue";
import * as Icons from "@element-plus/icons-vue";
const iconName = ref("");
const iconList = ref(Object.keys(Icons));

watch(
  () => iconName.value,
  () => {
    iconList.value = Object.keys(Icons);
    iconList.value = iconList.value.filter((item) =>
      item.includes(iconName.value)
    );
  }
);

const emit = defineEmits(["selectedIcon"]);
const selectedIcon = (name: string) => {
  nextTick(() => document.body.click());
  emit("selectedIcon", name);
};

const reset = () => (iconName.value = "");
defineExpose({ reset });
</script>
<style lang="scss" scoped>
.icon-body {
  display: flex;
  flex-wrap: wrap;
  align-content: flex-start;
  height: 300px;
  overflow-y: scroll;
  .icon-item {
    width: calc(100% / 3);
    max-height: 32px;
    box-sizing: border-box;
    cursor: pointer;
    border-radius: 5px;
    &:hover {
      background-color: var(--el-color-primary-light-9);
    }
    .active {
      background-color: var(--el-color-primary-light-9);
    }
  }
}
</style>
