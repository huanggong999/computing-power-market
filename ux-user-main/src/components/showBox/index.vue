<template>
  <div class="show-box">
    <div class="title">{{ props.title }}</div>
    <slot class="box" name="box"></slot>
  </div>
</template>

<script setup lang="ts" name="ShowBox">
const props = defineProps<{ title: string }>()
</script>
<style scoped lang="scss">
.show-box {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;

  .title {
    font-weight: bold;
    font-size: 40px;
    color: #000000;
    margin-bottom: 34px;
  }
}
</style>
