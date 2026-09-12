<template>
  <div class="breadcrumb flx-align-center mb23">
    <el-icon><House /></el-icon>
    <div style="">当前位置：</div>
    <el-breadcrumb :separator-icon="ArrowRight" style="z-index: 3">
      <el-breadcrumb-item
        v-for="(item, index) in props.routerList"
        :key="index"
        :to="{ path: item.path }"
        >{{ item.name }}</el-breadcrumb-item
      >
    </el-breadcrumb>
  </div>
</template>

<script setup lang="ts" name="AppCard">
import { ArrowRight } from "@element-plus/icons-vue";
interface IRouteInfo {
  name: string;
  path?: string;
}
interface IBreadCrumbParam {
  routerList: IRouteInfo[];
}
const props = defineProps<IBreadCrumbParam>();
</script>
<style lang="scss" scoped>
.breadcrumb {
  z-index: 3;
  .el-icon {
    width: 22px;
    height: 22px;
  }

  height: 22px;
  font-weight: 400;
  font-size: 16px;
  color: #83889d;
}
.el-breadcrumb {
  font-weight: 400;
  font-size: 16px;
  :deep(.el-breadcrumb__inner) {
    color: #000000;
  }
  :deep(.is-link) {
    color: #83889d;
  }
}
</style>
