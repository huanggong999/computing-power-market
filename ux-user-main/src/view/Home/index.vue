<template>
  <div class="home-box">
    <TopBar />
    <div class="content">
      <transition appear name="fade-transform" mode="out-in">
        <router-view />
      </transition>
    </div>
    <Footer class="footer" />
  </div>
</template>

<script setup lang="ts" name="Home"></script>
<style lang="scss" scoped>
.home-box {

  // width: 100vw;
  .content {
    min-height: calc(100vh - 62px - 100px - 50px);
  }
}
</style>
