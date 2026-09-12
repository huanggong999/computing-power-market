<template>
  <div class="banner">
    <div class="text-box" v-if="!isSearch && bannerList.length">
      <el-carousel
        height="680px"
        class="mb20"
        motion-blur
        v-if="bannerList.length"
      >
        <el-carousel-item
          v-for="item in bannerList"
          :key="item.id"
          class="to"
          @click="skip(item.skipType)"
        >
          <img :src="item.image" />
        </el-carousel-item>
      </el-carousel>
      <!-- <div class="title">AI智算云<span style="color: blue">平台</span></div>
        <div class="context">
          丰富的GPU算力资源与人工智能基础设施，帮助构建AI社区开源生态
        </div>
        <el-button type="primary" @click="toRegister">免费注册 ></el-button> -->
    </div>
    <Search
      v-else-if="isSearch"
      class="banner-search"
      v-model="model"
      :search-title="searchTitle"
      @search="() => emit('search')"
    />
    <div v-else></div>
  </div>
</template>

<script setup lang="ts" name="Banner">
import { getBannerListApi } from '@/api/banner'
import { useVModel } from '../../utils/useVModel'
interface IBannerParams {
  isSearch: boolean
  searchTitle?: string
  modelValue?: string
  bannerType?: number
  clickHandler?: () => void
}
import { LOGIN_URL } from '@/config'
import { useUserInfo } from '@/store'
import { getToken } from '@/utils/auth'
import { useRouter } from 'vue-router'
import { toPage } from '@/utils'
import { ElMessage } from 'element-plus'

const router = useRouter()
const props = defineProps<IBannerParams>()
const emit = defineEmits(['update:modelValue', 'search'])
const model = useVModel(props, 'modelValue', emit)
const bannerList = ref<any>([])
const initComponent = async () => {
  if (!props.isSearch) {
    await getBannerListApi({ type: props.bannerType }).then((res) => {
      bannerList.value = res.data
    })
  }
}

initComponent()

const skip = (type: string) => {
  switch (type) {
    case 'ACTIVITY':
      toPage('/activityCenter')
      break
    case 'COUPON':
      if (getToken()) {
        toPage('/voucherCollectionCenter')
      } else {
        ElMessage.warning('请先登录')
        router.push(LOGIN_URL)
      }

      break
    case 'BUDDY':
      if (router.currentRoute.value.fullPath != '/partner') toPage('/partner')
      else {
        emit('currentTab')
      }
      break
  }
}
</script>
<style lang="scss" scoped>
.banner {
  min-height: 680px;
  &-search {
    width: 80%;
    margin: auto;
    position: relative;
    top: 80px;
    padding-top: 100px;
  }

  .text-box {
    margin-top: 98px;
    img {
      height: 100%;
      width: 100%;
    }
    .to {
      &:hover {
        cursor: pointer;
      }
    }
  }
}
</style>
