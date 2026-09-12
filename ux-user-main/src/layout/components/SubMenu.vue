<template>
  <template v-for="subItem in menuList" :key="subItem.path">
    <el-sub-menu v-if="subItem.children?.length" :index="subItem.path">
      <template #title>
        <el-icon>
          <component :is="subItem.meta.icon"></component>
        </el-icon>
        <span class="sle">{{ subItem.meta.title }}</span>
      </template>
      <SubMenu :menuList="subItem.children" />
    </el-sub-menu>
    <el-menu-item
      v-else
      :index="subItem.path"
      @click="handleClickMenu(subItem)"
    >
      <el-icon>
        <component :is="subItem.meta.icon"></component>
      </el-icon>
      <template #title>
        <span class="sle">{{ subItem.meta.title }}</span>
      </template>
    </el-menu-item>
  </template>
</template>

<script setup lang="ts" name="SubMenu">
defineProps<{ menuList: IMenuList[] }>()
const router = useRouter()
const handleClickMenu = (subItem: IMenuList) => router.push(subItem.path)
</script>
<style lang="scss" scoped>
.el-menu-item {
  border-radius: 8px;

  &:hover {
    color: var(--el-menu-active-color);
    background-color: #3972fd;
  }
  &.is-active {
    color: var(--el-menu-active-color) !important;
    background-color: #3972fd;
  }
}
.sle {
  font-size: 18px;
}
</style>
