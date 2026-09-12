<template>
  <div class="show-card">
    <img class="card-img" :src="imgSrc" alt="图片加载中" />
    <div class="title">
      {{ title }}
    </div>
    <div class="intro">
      {{ intro }}
    </div>
    <div class="tags">
      <div class="tag" v-for="item in tagArr" :key="item">{{ item }}</div>
    </div>
    <div class="footer">
      <!-- <div class="author">
        <img :src="info.publishUserAvatar" />
        <div class="name">{{ info.publishUserName }}</div>
      </div> -->
    </div>
  </div>
</template>

<script setup lang="ts" name="ShowCard">
interface ICardProps {
  imgSrc: string
  title: string
  intro?: string
  info: {
    publishUserAvatar: string
    publishUserName: string
    [property: string]: any
  }
  isFavorite?: boolean
  isVisited?: boolean

}
const props = defineProps<ICardProps>()
const tagArr = computed(() => {
  return props.info.tags.split(',')
})
</script>
<style lang="scss" scoped>
.show-card {
  position: relative;
  width: 345px;
  height: 235px;
  background: #FFFFFF;
  box-shadow: 0px 0px 8px 1px rgba(153, 153, 153, 0.18);
  border-radius: 16px 16px 16px 16px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;

  &:hover {
    box-shadow: 0px 0px 20px 1px rgba(196, 213, 255, 0.8);
    cursor: pointer;
  }

  .card-img {
    width: 42px;
    height: 42px;
    background: #CCCCCC;
    border-radius: 8px 8px 8px 8px;
  }


  .title {
    max-width: 283px;
    font-family: PingFang SC, PingFang SC;
    font-weight: 500;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .intro {
    max-width: 305px;
    height: 22px;
    font-weight: 400;
    font-size: 16px;
    color: #83889D;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .tags {
    display: flex;
    gap: 12px;
    align-items: center;
    max-width: 350px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;

    .tag {
      max-width: 130px;
      height: 30px;
      background: #FFFFFF;
      border-radius: 4px 4px 4px 4px;
      border: 1px solid #CCCCCC;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      padding: 4px 10px;
      font-weight: 400;
      font-size: 16px;
      color: #666666;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
  }

  .footer {
    display: flex;
    justify-content: space-between;

    .author {
      display: flex;
      gap: 12px;
      font-weight: 400;
      font-size: 16px;
      color: #83889d;
      max-width: 330px;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;

      img {
        width: 20px;
        height: 20px;
        border-radius: 50%;
      }

      .name {
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }

      .btns {
        display: flex;
        gap: 15px;

        img {
          width: 22px;
          height: 22px;
        }
      }
    }

  }
}
</style>
