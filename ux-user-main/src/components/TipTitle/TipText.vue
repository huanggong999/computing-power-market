<template>
  <div class="tipCard">
    <div
      class="titText flx-align-center"
      :style="{
        '--beforeColor': beforeColor,
        '--textColor': textColor,
        '--fontSize': fontSize + 'px',
      }"
    >
      {{ content }}
    </div>
    <slot name="end"></slot>
  </div>
</template>

<script setup lang="ts" name="TitText">
withDefaults(
  defineProps<{
    beforeColor?: string // 左侧颜色
    textColor?: string // 字体颜色
    fontSize?: string // 字体大小
    content: string // 文本内容
  }>(),
  {
    beforeColor: '#3972FD',
    textColor: '#333',
    fontSize: '20',
  }
)
</script>
<style lang="scss" scoped>
.tipCard {
  display: flex;
  .titText {
    color: var(--textColor);
    font-size: var(--fontSize);
    line-height: 20px;
    width: 100%;
    padding-left: 20px;
    position: relative;
    font-weight: bold;
    &::before {
      position: absolute;
      top: 0;
      bottom: 0;
      left: 0;
      width: 4px;
      height: 28px;
      top: 50%;
      transform: translateY(-50%);
      content: '';
      background-color: var(--beforeColor);
    }
  }
}
</style>
