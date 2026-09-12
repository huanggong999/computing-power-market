<template>
  <el-dropdown trigger="click" @command="setAssemblySize">
    <i :class="'iconfont icon-contentright'" class="toolBar-icon"></i>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item
          v-for="item in assemblySizeList"
          :key="item.value"
          :command="item.value"
          :disabled="assemblySize === item.value"
        >
          {{ item.label }}
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup lang="ts" name="AssemblySize">
import { useGlobalStore } from "@/store";

const Global = useGlobalStore();
const assemblySize = computed(() => Global.assemblySize);

const assemblySizeList = [
  { label: "小型", value: "small" },
  { label: "默认", value: "default" },
  { label: "大型", value: "large" },
];
const setAssemblySize = (item: AssemblySizeType) => {
  if (assemblySize.value === item) return;
  Global.setGlobalState("assemblySize", item);
};
</script>
<style lang="scss" scoped></style>
