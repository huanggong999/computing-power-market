<template>
  <el-dropdown trigger="click">
    <div class="avatar">
      <img :src="avatar" alt="avatar" />
    </div>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item @click="logout">
          <el-icon><SwitchButton /></el-icon> 退出登录
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup lang="ts" name="Avatar">
import { LOGIN_URL } from "@/config";
import { useUser } from "@/store";
import { ElMessageBox } from "element-plus";
const userStore = useUser();
const router = useRouter();
const avatar = computed(() => userStore.avatar);

const logout = () => {
  ElMessageBox.confirm("您是否确认退出登录?", "温馨提示", {
    type: "warning",
  }).then(async () => {
    await userStore.LogOut();
    router.replace(LOGIN_URL);
    localStorage.clear();
    ElMessage.success("退出登录成功！");
  });
};
</script>
<style lang="scss" scoped>
.avatar {
  width: 40px;
  height: 40px;
  overflow: hidden;
  cursor: pointer;
  border-radius: 50%;
  img {
    width: 100%;
    height: 100%;
  }
}
</style>
