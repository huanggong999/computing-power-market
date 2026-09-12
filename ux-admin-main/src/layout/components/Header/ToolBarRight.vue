<template>
  <div class="tool-bar-ri">
    <div class="header-icon">
      <!-- 大小 -->
      <!-- <AssemblySize id="assemblySize" /> -->
      <!-- 布局 -->
      <ThemeSetting id="themeSetting" />
      <!-- 信息 -->
      <Message id="fullscreen" />
      <!-- 全屏 -->
      <Fullscreen id="fullscreen" />
    </div>
    <span class="username">{{ name }}</span>
    <Avatar />
  </div>
</template>

<script lang="ts" setup name="ToolBarRight">
import { useUser } from "@/store";
// import AssemblySize from "./components/AssemblySize.vue";
import ThemeSetting from "./components/ThemeSetting.vue";
import Fullscreen from "./components/Fullscreen.vue";
import Avatar from "./components/Avatar.vue";
import Message from "./components/Message.vue";

const userStore = useUser();

const name = computed(() => userStore.nickName);
</script>
<style lang="scss" scoped>
.tool-bar-ri {
  display: flex;
  align-items: center;
  justify-content: center;
  padding-right: 25px;
  .header-icon {
    display: flex;
    align-items: center;
    & > * {
      margin-left: 21px;
    }
  }
  .username {
    margin: 0 20px;
    font-size: 15px;
  }
}
</style>
