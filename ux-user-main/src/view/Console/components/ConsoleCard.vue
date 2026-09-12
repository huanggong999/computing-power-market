<template>
  <div
    class="card-box"
    :style="{ '--bgSrc': bgSrc }"
    :class="{
      [props.type]: props.type,
      bgColor: props.data.bgColor,
      IsBtn: props.data.btnText,
      cup: !props.data.IsCup,
    }"
    @click="clickCard"
  >
    <div class="left flex">
      <div class="title flx-align-center">
        {{ props.data.title }}
        <el-icon v-if="props.type !== 'resourceOverview'" class="ml5 mt5">
          <QuestionFilled />
        </el-icon>
      </div>
      <div class="text mt15">
        <span class="number"> {{ props.data.number }} </span>
        <span v-if="props.type === 'vouchers'">
          {{ props.data.letter ? props.data.letter : '张' }}
        </span>
      </div>
    </div>
    <el-button
      :color="props.data.btnBg"
      :style="{ color: props.data.btnColor }"
      class="mt40"
      round
      v-if="!!props.data.btnText"
      @click="props.data.btnClick!(props.data)"
    >
      {{ props.data.btnText }}
    </el-button>
  </div>
</template>

<script setup lang="ts" name="ConsoleCard">
type classType = 'resourceOverview' | 'account' | 'vouchers'

interface IPropsData {
  title: string // 标题
  number: string | number // 数字
  bgColor?: boolean // 是否显示背景色
  btnText?: string // 按钮文字
  btnBg?: string // 按钮背景色
  btnColor?: string // 按钮文字颜色
  IsCup?: boolean // 是否显示按钮
  bgSrc?: string // 背景图片
  btnClick?: (val: any) => void // 按钮点击事件
  letter?: string // 单位
}

const props = defineProps<{
  data: IPropsData // 数据
  type: classType // 类型
}>()

const bgSrc = computed(
  () =>
    `url("https://ai-cloud-system.tos-cn-beijing.volces.com/imgs/userSideImage/${props.data.bgSrc}.png")`
)

const clickCard = () => {
  if (!props.data.IsCup) return props.data.btnClick!(props.data)
}
</script>
<style lang="scss" scoped>
.card-box {
  border-radius: 10px;
  box-sizing: border-box;
  background: no-repeat;
  background-size: 100% 100%;
  background-image: var(--bgSrc);
  padding-left: 40px;
  display: flex;
  .left {
    flex-direction: column;
    justify-content: center;
  }
}

.IsBtn {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 50px;
}

.resourceOverview {
  width: 24%;
  height: 122px;
}
.account {
  width: 49%;
}
.vouchers {
  width: 32%;
}
.vouchers,
.account {
  height: 150px;
  .title,
  .text {
    color: #666;
    .number {
      font-size: 44px;
      color: #000;
    }
  }
}
.bgColor {
  background: linear-gradient(270deg, #3f97fe 0%, #3972fd 100%);
  .title,
  .text {
    color: #fff;
    .number {
      color: #fff;
    }
  }
}
</style>
