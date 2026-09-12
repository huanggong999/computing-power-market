<template>
  <el-dialog
    v-model="model"
    title="微信支付"
    center
    width="30%"
    destroy-on-close
    :close-on-click-modal="false"
    :close-on-press-escape="false"
  >
    <div class="code">
      <QrCode :text="props.text" />
      <div class="mt50">
        <div class="text ml20 mt40">订单编号 ： {{ props.orderNo }}</div>
        <div class="text ml20 mt40">支付金额 ：￥ {{ props.price }}</div>
      </div>

      <div class="tip">
        <el-image
          class="icon mr5"
          src="https://ai-cloud-system.tos-cn-beijing.volces.com/imgs/userSideImage/WeChatPay.png"
        />
        打开微信app扫码支付
      </div>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="closePopover">我已支付</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts" name="weChatPay">
import { weChatPayCallbackApi } from '@/api/order'
import { useVModel } from '@/utils/useVModel'
import { ElMessage } from 'element-plus'

interface IweChatPayProps {
  text: string
  orderNo: string
  price: number | string
  modelValue?: boolean
}
const props = withDefaults(defineProps<IweChatPayProps>(), {})

const emit = defineEmits(['submit', 'closePopover', 'update:modelValue'])
const model = useVModel(props, 'modelValue', emit)
const closePopover = async () => {
  const res = await weChatPayCallbackApi(props.orderNo)
  if (res.data.orderStatus === 'PAID') {
    ElMessage.success('支付成功!')
    emit('closePopover')
  } else {
    ElMessage.warning('您还未支付!')
  }
}
</script>
<style lang="scss" scoped>
.code {
  width: 100%;
  height: 230px;
  display: flex;
  .tip {
    position: absolute;
    bottom: 40px;
    left: 45px;
    display: flex;
    align-items: center;
    color: #000000;
    .icon {
      width: 28px;
      height: 28px;
    }
  }
}
</style>
