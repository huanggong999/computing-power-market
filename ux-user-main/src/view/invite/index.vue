<template>
  <div class="page flx-center">
    <div class="card">
      <img src="../../assets/icon/invite-icon.png" />
      <div class="label">{{ inviteInfo.name }} 邀请您加入</div>
      <div class="company">逸云数智</div>
      <el-button @click="toPageHandler('login')"
        >我已有账号，登陆后再关联</el-button
      >
      <el-button type="primary" @click="toPageHandler('register')"
        >还没有账号，注册后关联</el-button
      >
    </div>
  </div>
</template>

<script setup lang="ts" name="invite">
import { getExtendByCodeApi } from '@/api/partner'
import { toPage } from '@/utils'
import { ElMessage } from 'element-plus'
import { useRoute } from 'vue-router'

const router = useRoute()
const code = router.query?.code
const inviteInfo = ref<any>({ ddd: '' })
const initPage = () => {
  if (code) {
    getExtendByCodeApi({ code }).then((res) => {
      inviteInfo.value = res.data
    })
  }
}
initPage()
// 跳转登录页
const toPageHandler = (type: string) => {
  if (code) {
    localStorage.setItem('code', code + '')
    if (type === 'login') {
      localStorage.setItem('IsLogin', 'true')
    } else {
      localStorage.setItem('IsLogin', 'false')
    }
  }
  ElMessage.success('正在为您跳转...')
  toPage('/login')
}
</script>
<style lang="scss" scoped>
.page {
  height: 100vh;
  background: url(../../assets/images/invite-bg.png);
  margin-bottom: -50px;
  .card {
    width: 660px;
    height: 346px;
    border-radius: 8px 8px 8px 8px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    margin-bottom: -40px;
    img {
      width: 122px;
      height: 122px;
    }
    .label {
      font-weight: 500;
      font-size: 20px;
      margin-bottom: 10px;
    }
    .company {
      font-weight: 400;
      font-size: 16px;
      color: #3972fd;
      margin-bottom: 20px;
    }
    .el-button {
      width: 256px;
      height: 42px;
      border-radius: 4px 4px 4px 4px;
      font-weight: 400;
      font-size: 16px;

      margin: 0;
      margin-bottom: 12px;
    }
  }
}
</style>
