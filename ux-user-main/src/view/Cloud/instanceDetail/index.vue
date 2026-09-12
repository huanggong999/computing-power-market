<template>
  <div class="instance-detail">
    <Breadcrumb :router-list="routerList"></Breadcrumb>
    <div class="card" style="padding-bottom: 0">
      <TipText class="fwb" content="实例信息" fontSize="20" />
      <div class="mt28 mb30 info-card">
        <div class="info flx-align-center">
          <div class="label mr30">名称</div>
          <div class="value">
            {{
              instanceDetail.instanceName ? instanceDetail.instanceName : '--'
            }}
          </div>
        </div>
        <div class="info flx-align-center">
          <div class="label mr30">实例ID</div>
          <div class="value">
            {{ instanceDetail.instanceId ? instanceDetail.instanceId : '--' }}
          </div>
        </div>
        <div class="info flx-align-center">
          <div class="label mr30">实例规格</div>
          <div class="value">
            {{ instanceDetail.ecsScale ? instanceDetail.ecsScale : '--' }}
          </div>
        </div>
        <!-- <div class="info flx-align-center">
          <div class="label mr30">密钥</div>
          <div class="value">ins-2k40ki7a</div>
        </div> -->
      </div>
      <TipText class="fwb" content="网络信息" fontSize="20" />
      <div class="mt28 mb30 info-card">
        <div class="info flx-align-center">
          <div class="label mr30">所属网络</div>
          <div class="value">
            {{
              instanceDetail.networkList
                ? instanceDetail.networkList[0].type
                : '--'
            }}
          </div>
        </div>
        <div class="info flx-align-center">
          <div class="label mr30">主IPv4内网IP</div>
          <div class="value">
            {{
              instanceDetail.networkList
                ? instanceDetail.networkList[0].primaryIpAddress
                : '--'
            }}
          </div>
        </div>
        <div class="info flx-align-center">
          <div class="label mr30">所属子网</div>
          <div class="value">
            {{
              instanceDetail.networkList
                ? instanceDetail.networkList[0].subnetId
                : '--'
            }}
          </div>
        </div>
        <div class="info flx-align-center">
          <div class="label mr30">主IPv4公网IP</div>
          <div class="value">
            {{
              instanceDetail.eipAddress
                ? instanceDetail.eipAddress.ipAddress
                : '--'
            }}
          </div>
        </div>
      </div>
      <TipText class="fwb" content="配置信息" fontSize="20" />
      <div class="mt28 mb30 info-card">
        <div class="info flx-align-center">
          <div class="label mr30">内存</div>
          <div class="value">
            {{
              instanceDetail.memorySize
                ? instanceDetail.memorySize + 'GB'
                : '--'
            }}
          </div>
        </div>
        <div class="info flx-align-center">
          <div class="label mr30">CPU</div>
          <div class="value">
            {{ instanceDetail.cpuModel ? instanceDetail.cpuModel : '--' }}
          </div>
        </div>
        <div
          class="info flx-align-center"
          v-if="instanceDetail.productType == 2"
        >
          <div class="label mr30">公网带宽</div>
          <div class="value">ins-2k40ki7a</div>
        </div>
        <div class="info flx-align-center">
          <div class="label mr30">系统盘</div>
          <div class="value">
            {{
              instanceDetail.sysVolume
                ? instanceDetail.sysVolume.volumeId
                  ? instanceDetail.sysVolume.volumeId
                  : '--'
                : '--'
            }}
          </div>
        </div>
      </div>
      <TipText class="fwb" content="镜像信息" fontSize="20" />
      <div class="mt28 mb30 info-card">
        <div class="info flx-align-center">
          <div class="label mr30">镜像名称</div>
          <div class="value">
            {{ instanceDetail.osName ? instanceDetail.osName : '--' }}
          </div>
        </div>
        <div class="info flx-align-center">
          <div class="label mr30">镜像类型</div>
          <div class="value">
            {{ instanceDetail.osType ? instanceDetail.osType : '--' }}
          </div>
        </div>
        <div class="info flx-align-center">
          <div class="label mr30">镜像ID</div>
          <div class="value">
            {{ instanceDetail.imageId ? instanceDetail.imageId : '--' }}
          </div>
        </div>
      </div>
      <TipText class="fwb" content="计费信息" fontSize="20" />
      <div class="mt28 mb30 info-card">
        <div class="info flx-align-center">
          <div class="label mr30">实际计费模式</div>
          <div class="value">{{ payMap[instanceDetail.chargeType] }}</div>
        </div>
        <div class="info flx-align-center">
          <div class="label mr30">创建时间</div>
          <div class="value">{{ instanceDetail.createTime }}</div>
        </div>
        <div class="info flx-align-center">
          <div class="label mr30">网络计费模式</div>
          <div class="value">{{ payMap[instanceDetail.chargeType] }}</div>
        </div>
        <div class="info flx-align-center">
          <div class="label mr30">到期时间</div>
          <div class="value">
            {{ instanceDetail.expireTime ? instanceDetail.expireTime : '--' }}
          </div>
        </div>
      </div>
      <TipText
        class="fwb"
        content="访问地址信息"
        fontSize="20"
        v-if="instanceDetail.publicInfos"
      />
      <div
        class="mt28 mb30 info-card"
        v-for="(item, index) in instanceDetail.publicInfos"
        :key="index"
      >
        <div class="info flx-align-center">
          <div class="label mr30">公网地址</div>
          <div class="value">{{ item.publicIp }}</div>
        </div>
        <div class="info flx-align-center">
          <div class="label mr30">访问端口</div>
          <div class="value">{{ item.port }}</div>
        </div>
        <div class="info flx-align-center">
          <div class="label mr30">私密地址</div>
          <div class="value">{{ item.privateIp }}</div>
        </div>
        <div class="info flx-align-center">
          <div class="label mr30">访问方式</div>
          <div class="value">
            {{ item.accessMethod }}
          </div>
        </div>
      </div>
      <!-- 底部bar -- productType=1 -->
      <div
        class="footer flx-align-center"
        v-if="instanceDetail.instanceId && instanceDetail.productType == 1"
      >
        <el-button
          type="primary"
          @click="btnHandler('start')"
          v-if="instanceDetail.status !== 'RUNNING'"
          >启动</el-button
        >
        <el-button
          type="primary"
          @click="btnHandler('stop')"
          v-if="
            instanceDetail.status !== 'STOPPING' &&
            instanceDetail.status !== 'STOPPED'
          "
          >停止</el-button
        >
        <el-button
          type="primary"
          @click="btnHandler('restart')"
          v-if="
            instanceDetail.status !== 'REBOOTING' &&
            instanceDetail.status !== 'RUNNING'
          "
          >重启</el-button
        >
        <el-button
          type="primary"
          @click="btnHandler('delete')"
          v-if="
            instanceDetail.status !== 'RUNNING' &&
            instanceDetail.status !== 'DELETING'
          "
          >删除</el-button
        >
        <el-button type="primary" @click="btnHandler('reset')"
          >重置密码</el-button
        >
        <el-button type="primary" @click="btnHandler('renew')">续费</el-button>
        <el-icon>
          <MoreFilled />
        </el-icon>
      </div>
      <!-- 底部bar -- productType=2 -->
      <div
        class="footer flx-align-center"
        v-else-if="instanceDetail.productType == 2 && instanceDetail.status !== 'CREATING'"
      >
        <el-popconfirm
          title="确定停止当前实例吗?"
          @confirm="buildInstanceHandler('shutDown')"
          confirm-button-text="确认"
          cancel-button-text="取消"
        >
          <template #reference>
            <el-button
              type="primary"
              @click="btnHandler('shutDown')"
              v-if="instanceDetail.status !== 'SHUT_DOWN'"
              >停机</el-button
            >
          </template>
        </el-popconfirm>
        <el-popconfirm
          title="确定重启当前实例吗?"
          @confirm="buildInstanceHandler('restart2')"
          confirm-button-text="确认"
          cancel-button-text="取消"
        >
          <template #reference>
            <el-button type="primary" @click="btnHandler('restart2')"
              >重启</el-button
            >
          </template>
        </el-popconfirm>
        <!-- <el-button type="primary" @click="btnHandler('restart')" v-if="
          instanceDetail.status !== 'REBOOTING' &&
          instanceDetail.status !== 'RUNNING'
        ">续费</el-button> -->
        <el-popconfirm
          title="确定销毁当前实例吗?"
          @confirm="buildInstanceHandler('destroy')"
          confirm-button-text="确认"
          cancel-button-text="取消"
        >
          <template #reference>
            <el-button
              type="primary"
              @click="btnHandler('destroy')"
              v-if="instanceDetail.status !== 'EXPIRE'"
              >销毁</el-button
            ></template
          ></el-popconfirm
        >
      </div>
    </div>
    <el-dialog v-model="dialogVisible" width="954" class="dialog">
      <template #header>
        <div class="flx-align-center">
          <span class="title mr5">{{ dialogInfo.title }}</span>
          <el-tooltip
            class="box-item"
            effect="dark"
            :content="dialogInfo.titleTip"
            placement="top-start"
          >
            <el-icon style="color: #cccccc">
              <QuestionFilled />
            </el-icon>
          </el-tooltip>
        </div>
      </template>
      <!-- 停止实例 -->
      <div v-if="dialogInfo.type === 'stop'" class="stop">
        <el-table :data="targetInstance" border style="width: 100%">
          <el-table-column prop="instanceId" label="实例名称/ID" />
          <el-table-column prop="ecsScale" label="规格" />
          <el-table-column label="计费类型">
            <template #default="scope">
              {{ payMap[scope.row.chargeType] }}
            </template>
          </el-table-column>
          <el-table-column prop="address" label="是否包含本地盘">
            <template #default="scope">
              {{ scope.row.localVolumeList.length > 0 ? '是' : '否' }}
            </template></el-table-column
          >
        </el-table>
        <div class="label mb16">停机方式</div>
        <el-radio-group v-model="stopForm.func">
          <el-radio value="normal">停止</el-radio>
          <el-radio value="force">强制停止</el-radio>
        </el-radio-group>
        <div class="label">停止模式</div>
        <div class="select-card radio-card flx-align-center mb15">
          <div class="button">
            <el-radio v-model="stopForm.way" value="KeepCharging"></el-radio>
          </div>
          <div class="info">
            <div class="title mb12" style="color: #fba201">普通停机</div>
            <div class="content">停止后仍旧保留实例,并继续收费</div>
          </div>
        </div>
        <div class="radio-card flx-align-center">
          <div class="button">
            <el-radio v-model="stopForm.way" value="StopCharging"></el-radio>
          </div>
          <div class="info">
            <div class="title mb12">节省关机</div>
            <div class="content">
              <span>
                1、停机后，实例资源（CPU、GPU和内存）将停止计费，所挂载的云盘、镜像、弹性公网IP和带宽（固定带宽模式）将继续计费
              </span>
              <span>
                2、若空闲的实例资源不足，实例重启后可能会启动失败，可稍后尝试再次启动，或尝试更换实例规格。
              </span>
              <span>
                3、私网IP地址和实例停机前绑定的弹性公网IP地址，在实例启动后均保持不变。
              </span>
            </div>
          </div>
        </div>
      </div>
      <!-- 启动实例 -->
      <div v-else-if="dialogInfo.type === 'start'" class="restart">
        <el-table :data="targetInstance" border style="width: 100%">
          <el-table-column prop="instanceId" label="实例名称/ID" />
          <el-table-column prop="ecsScale" label="规格" width="180" />
          <el-table-column label="计费类型">
            <template #default="scope">
              {{ payMap[scope.row.chargeType] }}
            </template>
          </el-table-column>
        </el-table>
      </div>
      <!-- 重启实例 -->
      <div v-else-if="dialogInfo.type === 'restart'" class="restart">
        <el-table :data="targetInstance" border style="width: 100%">
          <el-table-column prop="instanceId" label="实例名称/ID" />
          <el-table-column prop="ecsScale" label="规格" width="180" />
          <el-table-column label="计费类型">
            <template #default="scope">
              {{ payMap[scope.row.chargeType] }}
            </template>
          </el-table-column>
        </el-table>
        <div class="label mb16">重启方式</div>
        <el-radio-group v-model="reStartForm.way">
          <el-radio value="reStart">重启</el-radio>
          <el-radio value="force">强制重启</el-radio>
        </el-radio-group>
      </div>
      <!-- 删除实例 -->
      <div v-if="dialogInfo.type === 'delete'" class="delete">
        <div class="warning mb26">
          <div class="item flx-align-center">
            <div class="circle">●</div>
            请选择是否删除关联云盘、网卡和弹性公网IP。如云盘、公网IP继续保留，将持续收取费用。
          </div>
          <div class="item flx-align-center">
            <div class="circle">●</div>
            删除过程中，请勿对相关资源进行任何操作，否则可能导致云服务器故障或资源删除失败。
          </div>
          <div class="item flx-align-center">
            <div class="circle">●</div>
            云盘删除后不可恢复，存在数据丢失风险，请在删除云盘前确认数据已备份。您可以使用快照或对象存储TOS服务做好数据备份工作。
          </div>
        </div>
        <div class="title mb15">
          确定删除所选的 <span style="color: #fba201">1</span> 台实例?
        </div>
        <!-- 删除实例列表 -->
        <el-collapse
          class="collapse"
          v-model="activeNames"
          @change="handleChange"
        >
          <el-collapse-item name="1">
            <template #title>
              <div class="collapse-title flx-align-center">
                <div class="label">
                  {{ instanceDetail.region }} / {{ instanceDetail.instanceId }}
                </div>
                <div class="date">{{ instanceDetail.createTime }} 创建</div>
              </div>
            </template>
            <div class="label mb14 flx-align-center">
              与该实例关联的云盘<el-tooltip
                class="box-item"
                effect="dark"
                :content="dialogInfo.titleTip"
                placement="top-start"
              >
                <el-icon style="color: #cccccc">
                  <QuestionFilled />
                </el-icon>
              </el-tooltip>
            </div>
            <el-table
              :data="targetInstance[0].volumeList"
              border
              style="width: 100%"
            >
              <!-- <el-table-column
                type="selection"
                :selectable="selectable"
                width="55"
              /> -->
              <el-table-column prop="count" label="云盘名称/ID" width="180" />
              <el-table-column prop="volumeType" label="云盘类型" />
              <el-table-column prop="size" label="规格" />
            </el-table>
            <div class="label mb14 flx-align-center">
              与该实例关联的公网IP<el-tooltip
                class="box-item"
                effect="dark"
                :content="dialogInfo.titleTip"
                placement="top-start"
              >
                <el-icon style="color: #cccccc">
                  <QuestionFilled />
                </el-icon>
              </el-tooltip>
            </div>

            <el-table
              :data="[instanceDetail.eipAddress]"
              border
              style="width: 100%"
            >
              <!-- <el-table-column
                type="selection"
                :selectable="selectable"
                width="55"
              /> -->
              <el-table-column prop="allocationId" label="弹性IP" />

              <el-table-column prop="ipAddress" label="IP地址" />
            </el-table>
          </el-collapse-item>
        </el-collapse>
        <el-checkbox
          v-model="knowDelete"
          :value="true"
          label="我已知晓即将删除的实例和相关资源，并了解相关风险。"
          size="large"
        />
      </div>
      <!-- 重置密码 -->
      <div v-if="dialogInfo.type === 'reset'" class="reset">
        <div class="warning mb20">
          <div class="item flx-align-center">
            <div class="circle">●</div>
            重置密码后需重启实例，新密码才会生效。您可勾选立即重启，或者在控制台手动重启。
          </div>
          <div class="item flx-align-center">
            <div class="circle">●</div>
            使用自定义镜像的实例在重置密码前必须安装密码插件，否则无法操作成功，具体操作参考文档安装密码插件。
          </div>
          <div class="item flx-align-center">
            <div class="circle">●</div>
            如果实例已经绑定SSH密钥对，重置密码后，密码和密钥对登录方式均生效。
          </div>
          <div class="item flx-align-center">
            <div class="circle">●</div>
            重置Windows的密码并重启后，建议10分钟内不要对实例再次重启/关机，可能会导致操作失败。
          </div>
        </div>
        <el-table :data="targetInstance" border style="width: 100%">
          <el-table-column prop="date" label="实例名称/ID" width="180" />
          <el-table-column prop="address" label="规格" />
          <el-table-column prop="address" label="计费类型" />
        </el-table>
        <div class="form flx-align-center mt30">
          <span class="mr20" style="width: 72px">新密码</span>
          <el-input type="password" show-password v-model="password"></el-input>
        </div>
        <div class="form flx-align-center mt24">
          <span class="mr20" style="width: 72px">确认密码</span>
          <el-input type="password" show-password v-model="password"></el-input>
        </div>
        <div class="form flx-align-center mt24">
          <span class="mr20" style="width: 72px">立即重启</span>
          <el-checkbox
            v-model="checked1"
            label="同意立即重启实例"
            size="large"
          />
        </div>
      </div>
      <!-- 续费 -->
      <div v-if="dialogInfo.type === 'renew'" class="renew">
        <el-table :data="targetInstance" border style="width: 100%">
          <el-table-column prop="date" label="实例名称/ID" width="180" />
          <el-table-column prop="address" label="规格" />
          <el-table-column prop="address" label="计费类型" />
        </el-table>
        <div class="form flx-align-center mt24">
          <span class="mr20" style="width: 72px">续费月份</span>
          <el-select
            v-model="value"
            placeholder="Select"
            style="width: 120px; margin-right: 10px"
          >
            <el-option
              v-for="item in dateOptions"
              :key="item"
              :label="item"
              :value="item"
            /> </el-select
          >个月
        </div>
      </div>
      <div class="btns mt30">
        <el-button @click="initDialog">取消</el-button>
        <el-button type="primary" @click="confirmDialog">确认</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts" name="Instance">
import { useRoute } from 'vue-router'
import {
  getUserInstanceDetailApi,
  startInstanceApi,
  stopInstanceApi,
  reStartInstanceApi,
  deleteInstanceApi,
  buildInstanceHandlerApi,
} from '@/api/instance'
import { ElMessage } from 'element-plus'
import { toPage } from '@/utils'
const router = useRoute()
const instanceDetail = ref<any>({})
// 目标实例
const targetInstance = ref<any>({})
// 枚举
const statusMap: any = {
  CREATING: '创建中',
  RUNNING: '运行中',
  STOPPING: '停止中',
  STOPPED: '已停止',
  REBOOTING: '重启中',
  STARTING: '启动中',
  REBUILDING: '重装中',
  RESIZING: '更配中',
  ERROR: '错误',
  DELETING: '删除中',
}
const payMap: any = {
  POSTPAID_BY_HOUR: '按量付费',
  POSTPAID_BY_MONTH: '包月',
  POSTPAID_BY_YEAR: '包年',
}
// 初始化页面
const init = () => {
  getUserInstanceDetailApi(router.query.id).then((res: any) => {
    instanceDetail.value = res.data
    targetInstance.value = [instanceDetail.value]
  })
}
onMounted(() => {
  init()
})

// 弹窗组件
const dialogVisible = ref(false)

const dialogInfo = ref({
  type: 'stop',
  title: '停止实例',
  titleTip: '这是删除实例',
})
const routerList = [
  { name: '实例', path: '/cloud/instance' },
  { name: '实例详情', path: '' },
]
const password = ref('')
const dateOptions = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]

const btnHandler = (type: string) => {
  switch (type) {
    case 'start':
      dialogInfo.value.type = 'start'
      dialogInfo.value.title = '启动实例'
      dialogInfo.value.titleTip = '是否确认启动该实例？'
      break
    case 'stop':
      dialogInfo.value.type = 'stop'
      dialogInfo.value.title = '停止实例'
      dialogInfo.value.titleTip = '是否确认停止该实例？'

      break
    case 'restart':
      dialogInfo.value.type = 'restart'
      dialogInfo.value.title = '确定重启所选的实例吗？'
      dialogInfo.value.titleTip = '是否确认重启该实例？'
      break
    case 'delete':
      dialogInfo.value.type = 'delete'
      dialogInfo.value.title = '删除实例'
      dialogInfo.value.titleTip = '是否确认删除该实例？'
      break
    case 'reset':
      dialogInfo.value.type = 'reset'
      dialogInfo.value.title = '重置密码'
      dialogInfo.value.titleTip = '是否确认重置密码？'
      break
    case 'renew':
      dialogInfo.value.type = 'renew'
      dialogInfo.value.title = '续费'
      dialogInfo.value.titleTip = '是否续费？'
      break
    case 'shutDown':
      dialogInfo.value.type = 'shutDown'
      dialogInfo.value.title = '停机'
      dialogInfo.value.titleTip = '是否停机？'
      break
    case 'restart2':
      dialogInfo.value.type = 'restart2'
      dialogInfo.value.title = '重启'
      dialogInfo.value.titleTip = '是否重启？'
      break
    case 'destroy':
      dialogInfo.value.type = 'destroy'
      dialogInfo.value.title = '销毁'
      dialogInfo.value.titleTip = '是否销毁？'
      break
  }
  if (type !== 'shutDown' && type !== 'restart2' && type !== 'destroy') {
    dialogVisible.value = true
  }
}
// 初始化dialog
const initDialog = () => {
  dialogVisible.value = false
}
// 提交dialog
const confirmDialog = () => {
  switch (dialogInfo.value.type) {
    case 'start':
      startHandler()
      break
    case 'stop':
      stopHandler()
      break
    case 'restart':
      reStartHandler()
      break
    case 'delete':
      deleteHandler()
      break
    case 'reset':
      break
    case 'renew':
      break
  }
  dialogVisible.value = true
}

//启动
const startHandler = () => {
  const data = {
    region: instanceDetail.value.region,
    idList: [router.query.id],
  }
  startInstanceApi(data).then((res: any) => {
    if (res.code === 200) {
      ElMessage.success('操作成功')
      dialogVisible.value = false
      toPage(`/cloud/instance`)
    }
  })
}

// 停止
// 停止表单
const stopForm = ref<any>({
  func: 'normal',
  way: 'KeepCharging',
})
const stopHandler = () => {
  const data = {
    region: instanceDetail.value.region,
    idList: [router.query.id],
    stoppedMode: stopForm.value.way,
  }
  stopInstanceApi(data).then((res: any) => {
    if (res.code === 200) {
      ElMessage.success('操作成功')
      dialogVisible.value = false
      toPage(`/cloud/instance`)
    }
  })
}
// 重启
// 重启表单
const reStartForm = ref<any>({
  way: 'reStart',
})
const reStartHandler = () => {
  const data = {
    region: instanceDetail.value.region,
    idList: [router.query.id],
  }
  reStartInstanceApi(data).then((res: any) => {
    if (res.code === 200) {
      ElMessage.success('操作成功')
      dialogVisible.value = false
      toPage(`/cloud/instance`)
    }
  })
}
// 删除
// 删除表单
const knowDelete = ref(false)
const deleteHandler = () => {
  if (!knowDelete.value) {
    ElMessage.warning('请确认已知晓删除风险!')
    return
  }
  const data = {
    region: instanceDetail.value.region,
    idList: [router.query.id],
  }
  deleteInstanceApi(data).then((res: any) => {
    if (res.code === 200) {
      ElMessage.success('操作成功')
      dialogVisible.value = false
      toPage(`/cloud/instance`)
    }
  })
}

// 自建服务器处理方法集合
const buildInstanceHandler = (type: string) => {
  const data: any = {
    instanceId: instanceDetail.value.id,
    operationType: '',
  }
  switch (type) {
    case 'shutDown':
      data.operationType = '1'
      break
    case 'restart2':
      data.operationType = '3'
      break
    case 'destroy':
      data.operationType = '2'
      break
  }
  buildInstanceHandlerApi(data).then((res: any) => {
    if (res.data) ElMessage.success('操作申请成功，待管理员执行,请勿重复申请')
    init()
  })
}
</script>
<style lang="scss" scoped>
@import './index.scss';
</style>
