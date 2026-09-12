<template>
  <el-form
    ref="ruleFormRef"
    :model="model"
    status-icon
    label-position="top"
    label-width="120px"
    class="demo-ruleForm"
  >
    <el-form-item
      label="账号"
      prop="username"
      :class="model.username ? 'aColor' : 'active'"
    >
      <el-input v-model="model.username" type="text" autocomplete="off">
        <template #prefix>
          <el-icon class="el-input__icon"><user /></el-icon>
        </template>
      </el-input>
    </el-form-item>
    <el-form-item
      label="密码"
      prop="password"
      :class="model.password ? 'aColor' : 'active'"
    >
      <el-input
        v-model="model.password"
        type="password"
        autocomplete="off"
        @keyup.enter="submit"
      >
        <template #prefix>
          <el-icon class="el-input__icon"><lock /></el-icon>
        </template>
      </el-input>
    </el-form-item>
    <el-form-item
      label="验证码"
      prop="code"
      :class="model.code ? 'aColor' : 'active'"
    >
      <el-input
        v-model="model.code"
        @keyup.enter="submit"
        type="text"
        autocomplete="off"
        style="width: 73%"
      />
      <div class="login-code">
        <img :src="props.codeUrl" @click="getCode" class="login-code-img" />
      </div>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts" name="UserNameLogin">
import { useVModel } from "@/utils/useVModel";
const props = defineProps<{ modelValue: ILoginParams; codeUrl: string }>();
const emit = defineEmits(["submit", "update:modelValue", "getCode"]);
const model = useVModel(props, "modelValue", emit);
const submit = () => emit("submit");
const getCode = () => emit("getCode");
</script>
<style lang="scss" scoped>
.login-code {
  width: 27%;
  height: 30px;
  text-align: right;
  img {
    height: 100%;
    cursor: pointer;
    vertical-align: top;
  }
}
</style>
