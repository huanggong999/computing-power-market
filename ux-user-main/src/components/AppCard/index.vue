<template>
  <div class="app-card">
    <img :src="img" />
    <div class="title">{{ name }}</div>
    <div class="intro">{{ intro }}</div>
    <div class="tags">
      <div class="tag" v-for="item in tagArr" :key="item">{{ item }}</div>
    </div>
  </div>
</template>

<script setup lang="ts" name="AppCard">
interface INavCard {
  // companyProfile: string
  // companyName: string
  item: any
  name: string
  intro: string
  publishUserAvatar: string
  publishUserName: string
  createTime: any
  isGood?: boolean
  tags?: string
  img: string
}
const props = defineProps<INavCard>()
const tagArr = props.tags.split(',')
</script>
<style lang="scss" scoped>
.app-card {
  width: 470px;
  height: 411px;
  background: #FFFFFF;
  box-shadow: 0px 0px 8px 1px rgba(153, 153, 153, 0.18);
  border-radius: 10px 10px 10px 10px;
  display: flex;
  flex-direction: column;
  gap: 20px;

  &:hover {
    box-shadow: 0px 0px 20px 1px rgba(196, 213, 255, 0.8);
    cursor: pointer;
  }

  img {
    width: 470px;
    height: 238px;
    border-radius: 10px 10px 0px 0px;
  }

  .title {

    font-weight: 500;
    font-size: 24px;
    color: #333333;
    padding: 0px 16px;
  }

  .intro {
    min-height: 40px;
    font-weight: 400;
    font-size: 16px;
    color: #83889D;
    padding: 0px 16px;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .tags {
    display: flex;
    gap: 10px;
    align-items: center;
    padding: 0px 16px;

    .tag {
      max-width: 130px;
      height: 30px;
      background: #FFFFFF;
      border-radius: 4px 4px 4px 4px;
      border: 1px solid #CCCCCC;
      overflow: hidden;
      text-overflow: ellipsis; //文本溢出显示省略号
      white-space: nowrap; //文本不会换行
      padding: 4px 10px;
      font-weight: 400;
      font-size: 16px;
      color: #666666;
    }
  }
}
</style>
