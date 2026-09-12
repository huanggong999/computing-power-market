<template>
  <div class="item flx-align-center">
    <div class="item-label mr30">{{ label }}</div>
    <div class="item-content flx-align-center">
      <p class="mr12">
        <span
          v-if="price && content !== undefined && content !== null"
          class="symbol"
        >
          ￥
        </span>
        {{ content ?? '--' }}
      </p>
      <el-image
        v-if="!!payType"
        style="width: 124px"
        :src="`https://ai-cloud-system.tos-cn-beijing.volces.com/imgs/userSideImage/${onlinePayTypeEnum[payType]}.png`"
      />
    </div>
  </div>
</template>

<script setup lang="ts" name="OrderDesc">
const onlinePayTypeEnum = {
  ALI_PAY: 'AlipayBg',
  WECHAT_PAY: 'WeChatPayBg',
  REMIT_PAY: 'RemitPayBg',
}
type TPayType = keyof typeof onlinePayTypeEnum
defineProps<{
  label: string
  content: string
  payType?: TPayType
  price?: boolean
}>()
</script>
<style lang="scss" scoped>
.item {
  width: calc(100% / 3);
  font-size: 16px;
  &-label {
    margin-right: 30px;
    width: 150px;
    text-align: right;
    color: #83889d;
  }
  &-content {
    .symbol {
      font-size: 14px;
    }
    color: #000;
  }
}
</style>
