<template>
  <div class="application-detail">
    <!-- 面包屑 -->
    <div style="width: 74%">
      <el-breadcrumb :separator-icon="ArrowRight">
        <el-breadcrumb-item :to="{ path: '/model' }">模型</el-breadcrumb-item>
        <el-breadcrumb-item>{{ detail.name }}</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <div class="container">
      <div class="left">
        <!-- 概要 -->
        <div class="info card">
          <div class="company">
            <!-- <img class="profile" :src="detail.publishUserAvatar" />
            <div class="name">{{ detail.createBy }}</div> -->
          </div>
          <div class="title">{{ detail.name }}</div>
          <div class="content">
            {{ detail.intro }}
          </div>
          <div class="only only-flex">
            <div class="tags" v-for="(item, index) in detail.tags" :key="index">
              <div class="tag">{{ item }}</div>
            </div>
          </div>
        </div>
        <!-- 详情 -->
        <div class="detail card" ref="blogRef">
          <v-md-preview ref="previewRef" :text="detail.introduce" height="400"></v-md-preview>
        </div>
      </div>
      <div class="right">
        <!-- 目录 -->
        <div class="catalog card">
          <div class="title">目录</div>
          <div ref="directoryRef">
            <div v-for="anchor in catalogList" :key="anchor.id" :style="{
              padding: `5px 0 5px ${anchor.indent * 20}px`,
              color: anchor.indent === 0 ? 'black' : '#83889D',
              'font-size': '16px',
            }" @click="directoryClick(anchor)" class="directory-item" :id="anchor.id">
              <span v-if="anchor.indent === 0"></span> {{ anchor.title }}
            </div>
          </div>
        </div>
        <!-- 服务器选项 -->
        <!-- <div class="specification card">
            <div class="title">{{ detail.name }}</div>
            <div class="section">
              <div class="label">版本:</div>
              <el-select
                v-model="version"
                style="width: 322px"
                size="large"
                placeholder="请选择版本"
              >
                <el-option
                  v-for="item in [detail.version]"
                  :key="item"
                  :label="item"
                  :value="item"
                />
              </el-select>
            </div>
            <div class="section">
              <div class="label">镜像大小:</div>
              <div class="value">{{ detail.mirrorSize }}</div>
            </div>
            <el-button @click="create">创建应用</el-button>
          </div> -->
      </div>
    </div>
    <!-- 虚化背景div -->
    <div class="bg-circle"></div>
  </div>
</template>

<script setup lang="ts" name="applicationDetail">
import { ArrowRight } from '@element-plus/icons-vue'
import { onMounted, ref } from 'vue'
import { getModelDetailAPI } from '@/api/former'
import { IAppItem } from '@/api/types/home'
import { toPage } from '@/utils'
// import { useRouter } from 'vue-router'

interface IServerProps {
  title: string
  content: string
  specification: string
  bandwidth: string
  price: string
}
const props = defineProps<IServerProps>()
let previewRef = ref()
let blogRef = ref()
const route = useRoute()

const directoryRef = ref()
const id = ref(route.query.id)
const detail = ref<IAppItem>({})

// 版本
let version = ref('')

// 目录标题列表
interface ICatalogTitle {
  id: string
  title?: string
  lineIndex?: string
  indent: number
  pixel?: number
}
let catalogList = ref<ICatalogTitle[]>([])
//初始化目录树
const directoryInit = () => {
  const anchors = previewRef.value.$el.querySelectorAll('h1,h2,h3,h4,h5,h6')
  const arr = Array.from(anchors).filter(
    (title: any) => !!title!.innerText.trim()
  )

  if (!arr.length) {
    catalogList.value = []
    return
  }
  const hTags = Array.from(
    new Set(arr.map((title: any) => title.tagName))
  ).sort()

  catalogList.value = arr.map((el: any) => ({
    id: 'directory-' + el.getAttribute('data-v-md-line'),
    title: el.innerText,
    lineIndex: el.getAttribute('data-v-md-line'),
    indent: hTags.indexOf(el.tagName),
    // pixel: el.getBoundingClientRect().top - 60,
  }))
}

let directoryId = ref('')

//目录点击事件
const directoryClick = (anchor: any) => {
  const { lineIndex } = anchor
  const heading = previewRef.value.$el.querySelector(
    `[data-v-md-line="${lineIndex}"]`
  )
  // 滚动浏览器视窗至详情区域
  window.scrollTo({
    top: 400,
    behavior: 'smooth',
  })
  // 滚动详情滚动条至目标标题处
  if (heading) {
    // removeScrollEventListener()
    directoryId.value = anchor.id
    previewRef.value.scrollToTarget({
      target: heading,
      scrollContainer: document.querySelector('.main-container'),
      top: 60,
    })
  }
}

//滚动事件监听
const scrollEventListener = () => {
  let pixel = blogRef.value.scrollTop + blogRef.value.offsetTop + 1
  const title = catalogList.value.reduce((prev: any, curr: any) => {
    if (
      curr.pixel <= pixel &&
      (prev === null || pixel - curr.pixel < pixel - prev.pixel)
    ) {
      return curr
    }
    return prev
  }, null)
  if (title) {
    directoryRef.value.scrollTop =
      (directoryRef.value.scrollHeight * title.pixel) /
      blogRef.value.scrollHeight
    directoryId.value = title.id
  }
}

//注册滚动事件
const addScrollEventListener = () => {
  blogRef.value.addEventListener('scroll', scrollEventListener)
}

// //销毁滚动事件
// const removeScrollEventListener = () => {
//   blogRef.value.removeEventListener('scroll', scrollEventListener)
// }

// 获取应用详情
const getApplication = async (id: number) => {
  let { data } = await getModelDetailAPI(id)
  data.tags = data.tags.split(',')
  detail.value = data
}
const create = () => {
  if (version.value) {
    toPage('/cloud/createInstance')
  } else {
    ElMessage.warning('请选择版本')
  }
}
onMounted(async () => {
  getApplication(id.value)
  // 初始化目录
  setTimeout(() => {
    directoryInit()
  }, 1500)
  // addScrollEventListener()
})
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
      display: grid;
      grid-template-rows: 228px 1204px;
      gap: 32px;

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
        height: 100%;
        overflow: auto;
      }
    }

    .right {
      display: grid;
      grid-template-rows: 540px 390px;
      gap: 32px;

      .catalog {
        overflow: auto;
        padding: 30px 0px 0px 30px;

        .title {
          font-weight: bold;
          font-size: 24px;
          color: #000000;
          margin-bottom: 25px;
        }
      }

      .specification {
        padding: 40px 30px;
        display: flex;
        flex-direction: column;
        gap: 50px;

        .title {
          font-weight: bold;
          font-size: 24px;
          color: #000000;
          justify-self: center;
          align-self: center;
        }

        .section {
          display: flex;
          align-items: center;

          .label {
            min-width: 60px;
            font-weight: 400;
            font-size: 16px;
            color: #83889d;
            padding-right: 1em;
          }

          .value {
            font-weight: 400;
            font-size: 16px;
            color: #000000;
          }
        }

        .el-button {
          width: 370px;
          height: 60px;
          background: #d7e2f6;
          border-radius: 12px 12px 12px 12px;
          font-weight: 500;
          font-size: 18px;
          color: #3972fd;
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
}
</style>
