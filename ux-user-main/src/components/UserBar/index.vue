<template>
  <div class="user-bar flx-align-center">
    <el-dropdown v-if="user.$state.userId">
      <span class="el-dropdown-link">
        {{ user.$state.customerName }}
        <el-icon class="el-icon--right"><arrow-down /></el-icon>
      </span>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item @click="user.LogOut">退出登录</el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<script setup lang="ts" name="TopBar">
import { useUserInfo } from '@/store'
const user = useUserInfo()
</script>
<style lang="scss" scoped>
.user-bar {
  font-size: 16px;
}
</style>
