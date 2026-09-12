<template>
  <div class="nav-card">
    <img :src="imgSrc" />
    <div class="info">
      <div class="title">{{ title }}</div>
      <div class="content">{{ content }}</div>
    </div>
  </div>
</template>

<script setup lang="ts" name="NavCard">
interface INavCard {
  imgSrc: string
  title: string
  content: string
}
const props = defineProps<INavCard>()
</script>
<style lang="scss" scoped>
.nav-card {
  width: 470px;
  height: 125px;
  background: radial-gradient(#d5e6ff 0%, #edf2ff 55%, #e6fdff 100%);
  border-radius: 16px 16px 16px 16px;
  border: 1px solid #ffffff;
  display: flex;
  gap: 20px;
  img {
    width: 125px;
    height: 125px;
    border-radius: 0px 0px 0px 0px;
  }
  .info {
    display: flex;
    flex-direction: column;
    gap: 12px;
    justify-content: center;
    .title {
      font-weight: bold;
      font-size: 24px;
    }
    .content {
      font-weight: 400;
      font-size: 16px;
      color: #83889d;
    }
  }
}
</style>
