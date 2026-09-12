<template>
  <vue-qr ref="qrCode" :text="props.text" :size="props.size" />
</template>

<script setup lang="ts" name="QrCode">
import vueQr from "vue-qr/src/packages/vue-qr.vue";
interface IQrCodeProps {
  text: string;
  size?: number;
  logo?: string;
  logoSize?: number;
  logoPadding?: number;
  colorDark?: string;
  colorLight?: string;
  correctLevel?: number;
}
const props = withDefaults(defineProps<IQrCodeProps>(), {
  text: "",
  size: 200,
  logo: "",
  logoSize: 30,
  logoPadding: 5,
  colorDark: "#000000",
  colorLight: "#ffffff",
  correctLevel: 3, // 精度
});
</script>
<style lang="scss" scoped></style>
