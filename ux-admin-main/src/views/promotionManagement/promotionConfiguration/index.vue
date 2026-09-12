<template>
  <div class="table-box card">
    <ProForm v-model="dataForm" :formColumns="formColumns" :label-width="140" />

    <div class="flx-center">
      <el-button type="primary" @click="submit"> 保存 </el-button>
    </div>
  </div>
</template>

<script setup lang="ts" name="PromotionConfiguration">
import {
  promotionConfigurationDetailApi,
  promotionConfigurationUpdateApi,
} from "@/api/promotionManagement";

const dataForm = ref<TKeyValue>({});
const getConfig = async () => {
  const { data } = await promotionConfigurationDetailApi();
  dataForm.value = data;
};

const formColumns: IFormColumnsProps[] = [
  { label: "推广客服配置", prop: "qrCode", el: "img" },
  { label: "推广官网介绍页面", prop: "introduce", el: "wangEditor" },
];

const submit = async () => {
  await promotionConfigurationUpdateApi(dataForm.value);
  ElMessage.success("保存成功");
  getConfig();
};

onMounted(() => getConfig());
</script>
<style lang="scss" scoped></style>
