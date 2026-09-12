<template>
  <div
    class="pay-card"
    :style="{ '--backgroundSrc': backgroundSrc }"
    :class="[props.selected > -1 ? 'selected' : '']"
  >
    <div class="info flx-align-between">
      <div class="title">{{ title }}</div>
      <div class="pay-num" v-if="!props.noShow">
        支付<span class="num">￥{{ count }}</span>
      </div>
    </div>
    <div class="tip" v-if="!props.noShow">
      账户可用余额
      <span style="color: #3972fd">{{ balance }}</span>
      元，余额不够支付，充值后在订单管理页进行支付如果您有正在使用中的后付费产品，请保证有足够余额。
    </div>
    <img
      v-if="props.selected > -1 ? true : false"
      class="select-icon"
      src="@/assets/icon/pay-select.png"
    />
  </div>
</template>

<script setup lang="ts" name="payCard">
import { computed } from 'vue'
interface IPayCardParams {
  title: string
  count?: number
  balance?: number
  bgSrc: string
  selected: number
  noShow?: boolean
}
const props = defineProps<IPayCardParams>()
const backgroundSrc = computed(
  () =>
    `url("https://ai-cloud-system.tos-cn-beijing.volces.com/imgs/images/${props.bgSrc}.png")`
)
</script>
<style lang="scss" scoped>
.pay-card {
  &:hover {
    cursor: pointer;
  }
  position: relative;
  background-image: var(--backgroundSrc);
  background-size: 100% 100%;
  height: 165px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 28px 24px;
  border: 1px solid #dadada;
  border-radius: 10px 10px 10px 10px;
  .info {
    display: flex;
    align-items: center;
    justify-content: space-between;
    .title {
      font-weight: 500;
      font-size: 24px;
      color: #333333;
    }
    .pay-num {
      font-weight: 400;
      font-size: 18px;
      color: #83889d;
    }
    .num {
      font-weight: 500;
      font-size: 24px;
      color: #ff4151;
    }
  }
  .tip {
    font-weight: 400;
    font-size: 16px;
    color: #83889d;
  }
  .select-icon {
    width: 27px;
    height: 27px;
    position: absolute;
    top: 0;
    left: 513px;
  }
}
.selected {
  border-radius: 10px 10px 10px 10px;
  border: 2px solid #3972fd;
}
</style>
