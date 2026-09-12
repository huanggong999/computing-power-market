<template>
  <div class="tool-bar-lf">
    <CollapseIcon id="collapseIcon" />
    <Breadcrumb id="breadcrumb" v-if="Global.breadcrumb" />
  </div>
</template>

<script lang="ts" setup name="ToolBarLeft">
import CollapseIcon from "./components/CollapseIcon.vue";
import Breadcrumb from "./components/Breadcrumb.vue";
import { useGlobalStore } from "@/store";
const Global = useGlobalStore();
</script>
<style lang="scss" scoped>
.tool-bar-lf {
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  white-space: nowrap;
}
</style>
