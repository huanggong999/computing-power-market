<template>
  <div class="product-card">
    <img :src="props.image" alt="" @click.stop="toUrl('')" />
    <div class="detail">
      <div class="name">{{ props.name }}</div>
      <!-- <div class="detail"></div> -->
      <div class="referPrice">
        <div class="item">
          <div class="label">售价:</div>
          <div class="value">
            <span class="small">￥</span>{{ props.payPrice }}元/月
          </div>

          <!-- <span class="crossed">￥{{ props.crossedPrice }}/天</span> -->
        </div>
        <div class="item" v-if="props.isIpCountDisplay == 1">
          <div class="label">IP单价:</div>
          <div class="value">
            <span class="small">￥</span>{{ props.crossedPrice }}元/个
          </div>
        </div>
      </div>
      <div class="normalPrice">
        <div class="label">参考价:</div>
        <div class="value">￥{{ props.referPrice }}元/月</div>
      </div>
      <div class="btns flx-align-center">
        <el-button type="primary" @click.stop="toUrl('consult')" plain
          >立即咨询</el-button
        >
        <el-button type="primary" @click.stop="toUrl('buy')"
          >立即购买</el-button
        >
      </div>
    </div>
  </div>
</template>

<script setup lang="ts" name="AppCard">
import { useAgicProduct } from '@/store/modules/agic'
import { useProduction } from '@/store/modules/networkProduct'
import { toPage } from '@/utils'
import { getToken } from '@/utils/auth'
import { ElMessage } from 'element-plus'

interface INavCard {
  data: any
  id: string
  formId: string
  name: string
  detail: string
  referPrice: number
  crossedPrice: number
  payPrice: number
  image: string
  payFormId: string
  isIpCountDisplay: any
}
const props = defineProps<INavCard>()
const targetNetworkProduction = useProduction()
const agicProduct = useAgicProduct()
const toUrl = (type?: string) => {
  let url = `/networkProductDetail?id=${props.id}`
  console.log('aaa', props.data)
  targetNetworkProduction.setProductionDetail(props.data)
  agicProduct.setDisplayOrBind({
    isAccountIpBinding: props.data.isAccountIpBinding,
    isBandwidthDisplay: props.data.isBandwidthDisplay,
    isIpDisplay: props.data.isIpDisplay,
  })
  if (type === 'consult') {
    url += '&type=consult'
  } else if (type === 'buy') {
    url = `/networkProductForm?id=${props.id}`
  }
  toPage(url)
  window.scrollTo({ top: 0, behavior: 'smooth' })
}
const toBuy = () => {
  // 检查用户是否登录
  if (getToken()) {
    // 处理购买逻辑
    toPage(`/payControl`)
    targetNetworkProduction.setProductionDetail(props.data)
    window.scrollTo({ top: 0, behavior: 'smooth' })
  } else {
    ElMessage.warning('账号未登录，正在为您跳转登录页...')
    toPage('/login')
  }
}
</script>
<style lang="scss" scoped>
.product-card {
  width: 342px;
  height: 461px;
  background: linear-gradient(121deg, #ffffff 0%, #e6eeff 100%);
  box-shadow: 0px 0px 16px 1px rgba(196, 213, 255, 0.5);
  border-radius: 16px 16px 16px 16px;
  display: flex;
  flex-direction: column;

  img {
    width: 342px;
    height: 180px;
    background: #e6eeff;
    border-radius: 16px 16px 0px 0px;

    &:hover {
      cursor: pointer;
    }
  }

  .detail {
    display: flex;
    flex-direction: column;
    gap: 15px;
    padding: 15px 20px;
    flex: 1;

    .name {
      font-weight: bold;
      font-size: 24px;
      display: -webkit-box;
      -webkit-box-orient: vertical;
      -webkit-line-clamp: 2;
      /* 限制在一个块元素显示的文本的行数 */
      overflow: hidden;
    }

    .detail {
      font-weight: 400;
      font-size: 16px;
      flex-grow: 1;
    }

    .referPrice {
      margin-top: auto;
      margin-bottom: 15px;
      display: grid;
      grid-template-columns: 1fr 1fr;
      .label {
        font-weight: 400;
        font-size: 14px;
        color: #3972fd;
      }

      .value {
        font-weight: bold;
        font-size: 18px;
        color: #3972fd;

        .small {
          font-size: 16px;
        }

        .crossed {
          font-weight: 400;
          font-size: 16px;
          color: #999999;
          text-decoration-line: line-through;
        }
      }
    }

    .normalPrice {
      display: flex;
      align-items: center;
      gap: 3px;

      .label {
        font-weight: 400;
        font-size: 16px;
        color: #ff4151;
      }

      .value {
        font-weight: 400;
        font-size: 16px;
        color: #ff4151;
      }
    }

    .el-button {
      width: 302px;
      height: 52px;
      border-radius: 26px 26px 26px 26px;
    }
  }
}
</style>
