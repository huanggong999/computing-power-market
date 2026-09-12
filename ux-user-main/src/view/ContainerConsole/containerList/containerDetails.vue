/** 容器详情 */
<template>
  <Breadcrumb :router-list="routerList" />
  <div class="card position-relative">
    <TipText content="基础信息" class="mb20" />
    <div class="item flex flex-wrap mb20">
      <div
        class="flex basicInfo"
        v-for="(item, index) in baseInfoList"
        :key="index"
      >
        <div class="label">{{ item.label }}</div>
        <div class="value sle">
          <span v-if="item.label !== '状态'"> {{ item.value }} </span>
          <el-tag v-else :type="tagType('运行中')">{{ item.value }}</el-tag>
        </div>
      </div>
    </div>
    <TipText content="集群网络信息" class="mb20" />
    <div class="item flex flex-wrap mb20">
      <div
        class="flex basicInfo"
        v-for="(item, index) in networkInfoList"
        :key="index"
      >
        <div class="label">{{ item.label }}</div>
        <div class="value sle">{{ item.value }}</div>
      </div>
    </div>
    <TipText content="集群 API Server 配置" class="mb20" />
    <div class="item flex flex-wrap mb20">
      <div
        class="flex basicInfo"
        v-for="(item, index) in serverConfig"
        :key="index"
      >
        <div class="label">{{ item.label }}</div>
        <div class="value sle">{{ item.value }}</div>
      </div>
    </div>
    <TipText content="云服务器 ECS 资源信息 " class="mb20" />
    <div class="item flex flex-wrap mb20">
      <!-- <div> -->

      <div
        class="flex basicInfo"
        v-for="(item, index) in serverResource"
        :key="index"
      >
        <div class="label">{{ item.label }}</div>
        <div class="value sle">{{ item.value }}</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts" name="ContainerDetails">
import { getContainerDetailApi } from '@/api/container'
import { useRoute } from 'vue-router'
const route = useRoute()

const id = ref(route.query.id)
const pageData = ref<any>({})
const initPage = () => {
  getContainerDetailApi(id.value).then((res) => {
    pageData.value = res.data
  })
}
initPage()
const routerList = ref([
  { name: '容器列表', path: '/containerList' },
  { name: '容器详情' },
])
const baseInfoList = computed(() => [
  {
    label: '名称',
    value: pageData.value.clusterName ? pageData.value.clusterName : '--',
  },
  {
    label: '集群ID',
    value: pageData.value.clusterId ? pageData.value.clusterId : '--',
  },
  { label: '状态', value: pageData.value.status },

  {
    label: 'Kubernetes版本',
    value: pageData.value.kubernetesVersion
      ? pageData.value.kubernetesVersion
      : '--',
  },
])
const typeEnum = {
  创建中: { text: '创建中', type: 'warning' },
  运行中: { text: '运行中', type: 'success' },
  已停止: { text: '已停止', type: 'danger' },
  更配中: { text: '更配中', type: 'warning' },
  错误: { text: '错误', type: 'danger' },
  删除中: { text: '删除中', type: 'danger' },
}
//
type TagType = keyof typeof typeEnum

const tagType = (type: TagType): any => typeEnum[type].type

// 集群网络信息
const networkInfoList = computed(() => [
  {
    label: '所属私有网络',
    value: pageData.value.vpcType ? pageData.value.vpcType : '--',
  },
  {
    label: '网络模型',
    value: pageData.value.networkModel ? pageData.value.networkModel : '--',
  },
  {
    label: 'Pod子网',
    value: pageData.value.podSubnet ? pageData.value.podSubnet : '--',
  },

  {
    label: 'Service CIDRv4',
    value: pageData.value.serviceCidrV4 ? pageData.value.serviceCidrV4 : '--',
  },
  {
    label: 'Proxy 模式',
    value: pageData.value.proxyMode ? pageData.value.proxyMode : '--',
  },

  {
    label: '节点默认安全组',
    value: pageData.value.nodeSecurityGroup
      ? pageData.value.nodeSecurityGroup
      : '--',
  },
  {
    label: 'Pod 默认安全组',
    value: pageData.value.podSecurityGroup
      ? pageData.value.podSecurityGroup
      : '--',
  },
])
// 集群 API Server 配置
const serverConfig = computed(() => [
  {
    label: '控制面子网',
    value: pageData.value.controlPlaneSubnet
      ? pageData.value.controlPlaneSubnet
      : '--',
  },

  {
    label: '私网访问',
    value: pageData.value.privateAccess ? pageData.value.privateAccess : '--',
  },
  {
    label: '负载均衡',
    value: pageData.value.loadBalancer ? pageData.value.loadBalancer : '--',
  },
  {
    label: 'API Server 公网访问',
    value: pageData.value.apiServerPublicAccess
      ? pageData.value.apiServerPublicAccess
      : '--',
  },
])
// 云服务器 ECS 资源使用量
const serverResource = computed(() => [
  {
    label: 'CPU（Core）',
    value: pageData.value.cpuCore ? pageData.value.cpuCore : '--',
  },
  {
    label: '内存（GiB）',
    value: pageData.value.memoryGiB ? pageData.value.memoryGiB : '--',
  },
  {
    label: '实例（个）',
    value: pageData.value.instanceNumber ? pageData.value.instanceNumber : '--',
  },
])
const customColors = [{ color: '#FBA201', percentage: 100 }]
</script>
<style lang="scss" scoped>
.item {
  padding: 30px 42px;
  background: #f7f8fb;
  border-radius: 10px;
  font-size: 16px;
  line-height: 32px;
  .label {
    color: #83889d;
    width: 150px;
    text-align: right;
  }
  .value {
    margin-left: 30px;
    color: #000000;
  }
  .basicInfo {
    width: 50%;
  }
  .progress {
    padding-left: 50px;
  }
}
.flex-wrap {
  flex-wrap: wrap;
}
</style>
