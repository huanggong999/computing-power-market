<template>
  <div class="application-detail">
    <!-- 面包屑 -->
    <div style="width: 74%">
      <el-breadcrumb :separator-icon="ArrowRight">
        <el-breadcrumb-item :to="{ path: '/activityCenter' }"
          >活动中心</el-breadcrumb-item
        >
        <el-breadcrumb-item>{{ detail.name }}</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <div class="container">
      <div class="left">
        <div class="detail card" ref="blogRef">
          <div class="title">{{ detail.name }}</div>
          <v-md-preview
            ref="previewRef"
            :text="detail.details"
            height="400"
          ></v-md-preview>
          <div class="url flx-align-center" v-if="detail.link">
            <div>活动链接:</div>
            <div class="link">{{ detail.link }}</div>
            <div class="copy" @click="copyText(detail.link)">复制</div>
          </div>
        </div>
      </div>
      <div class="right">
        <!-- 活动推荐 -->
        <div class="right-title">活动推荐</div>
        <div
          class="recommend"
          v-for="item in detail.recommendedList"
          :key="item.id"
        >
          <img
            :src="item.picture"
            @click="toPage(`/activityCenter/detail?id=${item.id}`)"
          />
        </div>
      </div>
    </div>
    <!-- 虚化背景div -->
    <div class="bg-circle"></div>
  </div>
</template>

<script setup lang="ts" name="applicationDetail">
import { ArrowRight } from '@element-plus/icons-vue'
import { onMounted, ref } from 'vue'
import { getDataDetailAPI } from '@/api/record'
import { toPage, copyText } from '@/utils'
import { useRoute } from 'vue-router'
import { getActivityDetailApi } from '@/api/activity'
// import { useRouter } from 'vue-router'

let previewRef = ref()
let blogRef = ref()
const route = useRoute()

const id = ref(route.query.id)
const detail = ref<any>({})

// 获取应用详情
const getActivity = async (id: number) => {
  let { data } = await getActivityDetailApi(id)

  detail.value = data
}

getActivity(id.value)
</script>

<style lang="scss" scoped>
.application-detail {
  position: relative;
  margin-top: 146px;
  margin-bottom: 50px;
  display: flex;
  flex-direction: column;
  align-items: center;

  .container {
    padding-top: 18px;
    display: grid;
    grid-template-columns: 1010px 430px;
    gap: 30px;

    .left {
      .info {
        padding: 30px;
        display: flex;
        flex-direction: column;
        gap: 15px;

        .company {
          display: flex;
          align-items: center;

          img {
            width: 38px;
            height: 38px;
            background: #cccccc;
            border-radius: 8px 8px 8px 8px;
            margin-right: 12px;
          }

          .name {
            font-weight: 400;
            font-size: 16px;
          }
        }

        .title {
          padding-left: 50px;
          font-weight: bold;
          font-size: 24px;
          color: #000000;
        }

        .content {
          padding-left: 50px;
          width: 900px;
          font-weight: 400;
          font-size: 16px;
          color: #666666;
        }

        .only {
          padding-left: 50px;

          .tags {
            margin-right: 12px;
            display: flex;
            align-items: center;
            gap: 12px;

            .tag {
              background: #ffffff;
              border-radius: 4px;
              border: 1px solid #cccccc;
              padding: 4px 10px;
              font-weight: 400;
              font-size: 16px;
              color: #666666;
            }
          }
        }
      }

      .detail {
        max-height: 100%;
        overflow: auto;
        display: flex;
        flex-direction: column;
        .title {
          font-weight: bold;
          font-size: 40px;
          color: #000000;
          margin-bottom: 8px;
        }
        .date {
          font-weight: 400;
          font-size: 16px;
          color: #999999;
          margin-bottom: 14px;
        }
        .url {
          min-width: 100%;
          margin-top: 30px;
          width: 90px;
          font-weight: 400;
          font-size: 18px;
          color: #333333;

          .link {
            padding-left: 6px;
            font-weight: 400;
            font-size: 18px;
            color: #83889d;
            line-height: 25px;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
            width: 600px;
          }
          .copy {
            padding-left: 15px;
            font-weight: 400;
            font-size: 18px;
            color: #3972fd;
            &:hover {
              cursor: pointer;
            }
          }
        }
      }
    }

    .right {
      display: flex;
      flex-direction: column;
      gap: 30px;
      .right-title {
        font-weight: bold;
        font-size: 24px;
        color: #000000;
        margin-bottom: 5px;
      }
      .recommend {
        width: 430px;
        height: 218px;
        background: #d9d9d9;
        border-radius: 10px 10px 10px 10px;
        img {
          width: 100%;
          height: 100%;
          background: #d9d9d9;
          border-radius: 10px 10px 10px 10px;
        }
        &:hover {
          cursor: pointer;
        }
      }
    }
  }

  .card {
    background: #ffffff;
    box-shadow: 0px 0px 16px 1px rgba(196, 213, 255, 0.5);
    border-radius: 16px 16px 16px 16px;
  }
}

/* 虚化背景 */
.bg-circle {
  position: absolute;
  left: 1069px;
  top: 0px;
  width: 861px;
  height: 777px;
  background: #c8d7fc;
  opacity: 0.3;
  filter: blur(50px);
  z-index: -1;
}
</style>
