<template>
  <div class="card-box" @click="handleClick">
    <img class="left" :src="props.data.imgSrc" />

    <div class="right flx-center">{{ props.data.title }}</div>
  </div>
</template>

<script setup lang="ts" name="FunctionCard">
import { toPage } from '@/utils';

interface IPropsData {
  title?: string // 标题
  imgSrc: string // 图标
  url: string // 目标路由
}

const props = defineProps<{
  data: IPropsData // 数据
}>()

const handleClick = () => {
  toPage(props.data.url)
}
</script>
<style lang="scss" scoped>
.card-box {
  width: 226px;
  height: 86px;
  background: #ffffff;
  border-radius: 10px 10px 10px 10px;
  border: 1px solid #e5e5e5;
  padding: 18px 52px 18px 28px;
  display: flex;
  gap: 16px;

  &:hover {
    cursor: pointer;
  }

  .left {
    width: 50px;
    height: 50px;
  }

  .right {
    font-family: PingFang SC, PingFang SC;
    font-weight: 400;
    font-size: 20px;
    color: #333333;
    white-space: nowrap;
  }
}
</style>
