<template>
  <div class="create-bucket">
    <Breadcrumb :router-list="routerList"></Breadcrumb>
    <div class="card">
      <ProForm
        ref="formRef"
        v-model="form"
        :formColumns="formCol"
        style="margin-top: 20px"
        class="basic-form"
      >
        <template #acl>
          <el-switch
            size="large"
            v-model="form.acl"
            inline-prompt
            active-text="启动"
            inactive-text="关闭"
          /><span class="ml15" style="font-weight: 400; color: #83889d"
            >桶内新上传对象默认私有</span
          ></template
        >
        <template #project>
          <div class="form-project">
            <el-select></el-select>
            <el-button>随机</el-button>
          </div>
        </template>
      </ProForm>
      <div class="high-set flx-align-center" @click="isHighSet = !isHighSet">
        高级设置
        <img v-if="!isHighSet" src="../../../assets/icon/blue-down-icon.png" />
        <img v-else src="../../../assets/icon/blue-up-icon.png" />
      </div>
      <ProForm
        v-if="isHighSet"
        ref="highFormRef"
        v-model="form"
        :formColumns="highFormCol"
        style="margin-top: 20px"
      >
        <template #az>
          <el-switch
            size="large"
            v-model="form.az"
            inline-prompt
            active-text="启动"
            inactive-text="关闭"
          /><span class="ml15" style="font-weight: 400; color: #83889d"
            >开通后TOS将提供同地域多个数据中心的容灾能力，提高数据可用性。</span
          >
        </template>
      </ProForm>
      <div class="footer flx-align-center">
        <el-button @click="toPage('/objectStorage')">取消</el-button>
        <el-button type="primary" @click="confirmHandler">确认</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts" name="Storage">
import { checkBucketExistAPI, createBucketAPI } from '@/api/storage'
import { toPage } from '@/utils'
import { ElMessage } from 'element-plus'

// 路由
const routerList = [
  { name: '对象存储', path: '/objectStorage' },
  { name: '创建存储桶', path: '' },
]
// 卡片一
let formRef = ref()
const form = ref({
  name: '',
  isVersion: 'false',
  redundancyType: '0',
  az: false,
})
const formCol: IFormColumnsProps[] = [
  {
    label: '名称',
    prop: 'name',
    el: 'input',
    placeholder: '请输入',
    itemLabelWidth: 110,
  },
  // {
  //   label: '区域',
  //   prop: 'region',
  //   el: 'radioButton',
  //   radioList: [
  //     { label: '华北2(北京)', value: 'POSTPAID_BY_HOUR' },
  //     { label: '华东2(上海)', value: 'POSTPAID_BY_MONTH' },
  //   ],
  //   required: true,
  //   itemLabelWidth: 110,
  // },
  {
    label: '版本控制',
    prop: 'isVersion',
    el: 'radioButton',
    radioList: [
      { label: '开通', value: 'true' },
      { label: '不开通', value: 'false' },
    ],
    itemLabelWidth: 110,
  },
  {
    label: '桶策略',
    prop: 'redundancyType',
    el: 'radioButton',
    radioList: [
      { label: '私有', value: '0' },
      { label: '公共读', value: '1' },
      { label: '公共读写', value: '2' },
    ],
    required: true,
    itemLabelWidth: 110,
  },
  // {
  //   label: '对象ACL默认策略',
  //   prop: 'acl',
  //   el: 'slot',
  //   required: true,
  //   itemLabelWidth: 110,
  // },
  // {
  //   label: '项目',
  //   prop: 'project',
  //   el: 'slot',
  //   required: true,
  //   itemLabelWidth: 110,
  // },
]
// 是否启用高级设置
const isHighSet = ref(false)
const highFormRef = ref()

const highFormCol: IFormColumnsProps[] = [
  {
    label: '多AZ冗余',
    prop: 'az',
    el: 'slot',
    itemLabelWidth: 110,
  },
  // {
  //   label: '服务端加密',
  //   prop: 'server',
  //   el: 'radioButton',
  //   radioList: [
  //     { label: '无', value: '1' },
  //     { label: 'SSE-TOS', value: '2' },
  //     { label: 'SSE-KMS', value: '3' },
  //   ],
  //   itemLabelWidth: 110,
  // },
  // {
  //   label: '自定义密钥',
  //   prop: 'diy',
  //   el: 'select',
  //   required: true,
  //   itemLabelWidth: 110,
  // },
]
const validateAndProcessString = (input) => {
  // 定义正则表达式
  const regex = /^[a-z0-9][a-z0-9-]{1,61}[a-z0-9]$/

  // 检查长度是否在3到63之间
  if (input.length < 3 || input.length > 63) {
    return false // 或者你可以返回一个错误信息
  }

  // 检查是否符合正则表达式
  if (!regex.test(input)) {
    return false // 或者你可以返回一个错误信息
  }

  // 如果通过验证，返回处理后的字符串（或者直接返回原字符串）
  return true
}
const confirmHandler = async () => {
  if (!validateAndProcessString(form.value.name)) {
    ElMessage.warning('请检查桶名称是否合规')
    return
  }
  await checkBucketExistAPI(form.value.name).then((res: any) => {
    if (res.data) {
      ElMessage.warning('桶名称已存在')
    } else {
      const data = {
        name: form.value.name,
        isVersion: form.value.isVersion,
        bucketStrategy: form.value.redundancyType,
        redundancyType: form.value.az ? 1 : 0,
      }
      createBucketAPI(data).then((res: any) => {
        if (res.code === 200) {
          ElMessage.success('创建成功')
          toPage('/objectStorage')
        } else {
          ElMessage.error('创建失败')
        }
      })
    }
  })
}
</script>
<style lang="scss" scoped>
@import './index.scss';
</style>
