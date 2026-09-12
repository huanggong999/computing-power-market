<template>
  <div
    class="card-box"
    :style="{
      background: props.data.bgColor,
    }"
    :class="{
      [props.type]: props.type,
      IsBtn: props.data.btnText,
      cup: !props.data.IsCup,
    }"
  >
    <div class="left flex">
      <div class="title flx-align-center">
        {{ props.data.title }}
        <el-icon v-if="props.type !== 'resourceOverview'" class="ml5 mt5">
          <QuestionFilled />
        </el-icon>
      </div>
      <div class="text mt15">
        <span :style="{ color: props.data.numColor }" class="number">
          {{ props.data.number }}
        </span>
        <span v-if="props.type === 'vouchers'"> 张 </span>
      </div>
    </div>
    <el-button
      :color="props.data.btnBg"
      :style="{ color: props.data.btnColor }"
      class="mt40 button"
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
  title?: string // 标题
  number: string // 数字
  numColor?: string // 数字颜色
  bgColor?: string // 背景色
  btnText?: string // 按钮文字
  btnBg?: string // 按钮背景色
  btnColor?: string // 按钮文字颜色
  IsCup?: boolean // 是否显示按钮
  bgSrc?: string // 背景图片
}

const props = defineProps<{
  data: IPropsData // 数据
  type: classType // 类型
}>()
</script>
<style lang="scss" scoped>
.card-box {
  height: 122px;
  width: 274px;
  border-radius: 10px;
  box-sizing: border-box;
  background: no-repeat;
  background-size: 100% 100%;

  padding-left: 40px;
  display: flex;
  .left {
    flex-direction: column;
    justify-content: center;
  }
  .button {
    width: 88px;
    height: 40px;
    background: #d7e2f6;
    border-radius: 26px 26px 26px 26px;
  }
}

.IsBtn {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 50px;
}

.resourceOverview {
  width: 100%;
  height: 122px;
}
.account {
  width: 49%;
}
.vouchers {
  width: 274px;
  height: 122px;
}
.resourceOverview,
.vouchers,
.account {
  .title,
  .text {
    color: #666;
    .number {
      font-size: 44px;
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
