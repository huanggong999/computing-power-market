<template>
  <div class="application-detail">
    <div class="bar mb50">
      <img src="../../../assets/images/newsbar.png" />
    </div>
    <!-- 面包屑 -->
    <div style="width: 74%;margin-bottom:20px">
      <el-breadcrumb :separator-icon="ArrowRight">
        <el-breadcrumb-item :to="{ path: '/news' }">新闻动态</el-breadcrumb-item>
        <el-breadcrumb-item>{{ detail.name }}</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <div class="card detail">
      <div class="name">{{ detail.name }}</div>
      <div class="createTime">发布时间:{{ detail.createTime }}</div>
      <div class="split"></div>
      <div class="content">
        <v-md-preview ref="previewRef" :text="detail.content" height="400"></v-md-preview>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts" name="applicationDetail">
import { getNewsDetailApi } from '@/api/news';
import { ArrowRight } from '@element-plus/icons-vue';
const route = useRoute()

const id = ref(route.query.id)
const detail = ref<any>({})
const initPage = () => {
  getNewsDetailApi(id.value).then(res => {
    detail.value = res.data
  })
}
initPage()
// 初始化页面数据
</script>

<style lang="scss" scoped>
.application-detail {
  position: relative;
  margin-top: 100px;
  margin-bottom: 50px;
  display: flex;
  flex-direction: column;
  align-items: center;

  .bar {
    height: 370px;
    width: 100%;

    img {
      height: 100%;
      width: 100%;
    }
  }

  .detail {
    width: 1470px;
    background: #FFFFFF;
    border-radius: 16px 16px 16px 16px;
    padding: 70px 75px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;

    .name {

      font-weight: bold;
      font-size: 28px;
      margin-bottom: 10px;
    }

    .createTime {

      font-weight: 400;
      font-size: 14px;
      color: #83889D;
      margin-bottom: 20px;
    }

    .split {
      width: 100%;
      height: 1px;
      background-color: #dfdfe0;
    }
  }

}
</style>
