<template>
  <div class="imageManagement table-box">
    <el-radio-group v-model="searchParams.visibility" size="large" class="image-tabs">
      <el-radio-button value="public">公共镜像</el-radio-button>
      <el-radio-button value="private">自定义镜像</el-radio-button>
      <el-radio-button value="shared">社区镜像</el-radio-button>
    </el-radio-group>

    <div v-if="isPrivateImage" class="mine-card card">
      <div class="mine-toolbar">
        <div>
          <div class="mine-title">我的镜像</div>
          <div class="mine-subtitle">
            <span>租户：{{ myImageMeta.tenant_id || '-' }}</span>
            <span v-if="myImageMeta.registry">Registry：{{ myImageMeta.registry }}</span>
            <span class="image-limit" :class="{ reached: myImageLimitReached }">
              已使用 {{ myImageCount }}/{{ MAX_MY_IMAGES }}
            </span>
          </div>
        </div>
        <div class="mine-actions">
          <el-input
            v-model="myImageKeyword"
            clearable
            placeholder="搜索镜像名称、Tag、地址"
            class="mine-search"
          />
          <el-button @click="loadMyImages">刷新</el-button>
          <el-button
            type="primary"
            :disabled="myImageLimitReached"
            :title="myImageLimitReached ? `自定义镜像最多只能上传${MAX_MY_IMAGES}个` : undefined"
            @click="openPushDialog"
          >上传镜像</el-button>
        </div>
      </div>

      <el-alert
        v-if="myImageMeta.degraded"
        class="mine-alert"
        type="warning"
        :closable="false"
        :title="myImageMeta.note || '镜像服务当前处于降级状态，请联系管理员配置 AccessKey。'"
      />

      <div v-if="myImageMeta.namespace_prefix" class="namespace-line">
        命名空间：{{ myImageMeta.namespace_prefix }}
      </div>

      <el-table
        v-loading="myImageLoading"
        :data="filteredMyImages"
        border
        class="imageTable mt20"
        empty-text="暂无自定义镜像"
      >
        <el-table-column label="镜像仓库" min-width="260">
          <template #default="scope">
            <div class="repo-cell">
              <div class="repo-name">{{ getRepoName(scope.row.repo) }}</div>
              <div class="repo-path">{{ scope.row.repo }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="tag" label="Tag" width="130" />
        <el-table-column label="完整镜像地址" min-width="360">
          <template #default="scope">
            <div class="image-address">
              <span>{{ scope.row.image }}</span>
              <el-button link type="primary" @click="copyText(scope.row.image)">复制</el-button>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="showPullCommand(scope.row)">下载命令</el-button>
            <el-button link type="primary" @click="copyText(scope.row.image)">复制地址</el-button>
            <el-button link type="danger" @click="deleteMyImage(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div v-else class="card">
      <div class="header mb26">
        <el-select
          placeholder="请选择镜像操作系统"
          v-model="searchParams.platform"
          style="width: 260px"
        >
          <el-option
            v-for="item in platformList"
            :key="item"
            :label="item"
            :value="item"
          />
        </el-select>
        <el-button type="primary" @click="searchList">查询</el-button>
        <el-button @click="resetParams">重置</el-button>
      </div>
      <el-table
        :data="imageTableData"
        style="width: 100%"
        class="imageTable mt28"
        border
        :cell-style="{ textAlign: 'center', fontSize: '13px' }"
        :header-cell-style="{ textAlign: 'center', fontSize: '13px' }"
      >
        <el-table-column property="imageId" label="名称/ID" />
        <el-table-column label="状态">
          <template #default="scope">
            <el-tag type="success" v-if="scope.row.status === 'available'">可用</el-tag>
            <el-tag type="danger" v-else>不可用</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="容量">
          <template #default="scope">{{ scope.row.size }}GB</template>
        </el-table-column>
        <el-table-column property="platform" label="架构类型" />
        <el-table-column property="osType" label="操作系统" />
        <el-table-column property="createdAt" label="创建时间">
          <template #default="scope">{{ formatTime(scope.row.createdAt, 'YYYY-MM-DD') }}</template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="scope">
            <span class="blue-font mr3" @click="checkDetail(scope.row)">详情</span>
            <span class="separator mr3">|</span>
            <span class="blue-font" @click="toPage('/cloud/createInstance')">创建实例</span>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-drawer v-model="drawerVisible" direction="rtl" size="60%" class="image-detail">
      <template #header>
        <div class="header flx-align-center">
          <el-tag class="success mr12" type="success">可用</el-tag>
          <div class="title">{{ targetImage.imageName }}</div>
        </div>
      </template>
      <div class="content">
        <TipText class="fwb mb28" content="基础信息" fontSize="20" />
        <div class="info-card mb30">
          <div class="info">
            <div class="label">名称</div>
            <div class="value">{{ targetImage.imageName }}</div>
          </div>
          <div class="info">
            <div class="label">ID</div>
            <div class="value">{{ targetImage.imageId }}</div>
          </div>
          <div class="info">
            <div class="label">架构类型</div>
            <div class="value">x86_64</div>
          </div>
          <div class="info">
            <div class="label">描述</div>
            <div class="value">{{ targetImage.description || '-' }}</div>
          </div>
          <div class="info">
            <div class="label">操作系统</div>
            <div class="value">{{ targetImage.osName }}</div>
          </div>
          <div class="info">
            <div class="label">容量</div>
            <div class="value">{{ targetImage.size }}GB</div>
          </div>
        </div>
        <TipText class="fwb mb28" content="其他信息" fontSize="20" />
        <div class="info-card mb30 compact">
          <div class="info">
            <div class="label">创建时间</div>
            <div class="value">{{ formatTime(targetImage.createdAt) }}</div>
          </div>
          <div class="info">
            <div class="label">更新时间</div>
            <div class="value">{{ formatTime(targetImage.updatedAt) }}</div>
          </div>
        </div>
      </div>
    </el-drawer>

    <el-dialog v-model="pushDialogVisible" width="520px" class="dialog" title="上传自定义镜像">
      <el-alert
        v-if="myImageLimitReached"
        class="mb16"
        type="warning"
        :closable="false"
        :title="`当前已有${myImageCount}个自定义镜像，最多只能上传${MAX_MY_IMAGES}个。`"
      />
      <el-form label-position="top" class="mt10">
        <el-form-item label="镜像名称" required>
          <el-input v-model="pushForm.name" placeholder="例如 myimg" />
        </el-form-item>
        <el-form-item label="Tag" required>
          <el-input v-model="pushForm.tag" placeholder="例如 latest" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pushDialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          :loading="commandLoading"
          :disabled="myImageLimitReached"
          @click="createPushCommand"
        >生成命令</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="commandDialogVisible" width="760px" class="command-dialog" :title="commandDialogTitle">
      <div v-if="commandResult.image" class="command-image">
        <span>{{ commandResult.image }}</span>
        <el-button link type="primary" @click="copyText(commandResult.image)">复制地址</el-button>
      </div>
      <el-alert
        v-if="commandResult.tip || commandResult.note"
        class="mb16"
        type="warning"
        :closable="false"
        :title="commandResult.tip || commandResult.note"
      />
      <div class="command-list">
        <div v-for="(command, index) in commandResult.commands" :key="index" class="command-item">
          <pre>{{ command }}</pre>
          <el-button size="small" @click="copyText(command)">复制</el-button>
        </div>
      </div>
      <template #footer>
        <el-button @click="commandDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="copyText(commandResult.commands.join('\n'))">复制全部</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts" name="imageManagement">
import dayjs from 'dayjs'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getImageListApi } from '@/api/instance'
import {
  deleteMyImageApi,
  getMyImageListApi,
  getMyImagePullCommandApi,
  getMyImagePushCommandApi,
} from '@/api/image'
import { copyText, toPage } from '@/utils'

interface MyImageItem {
  repo: string
  tag: string
  image: string
}

const searchParams = ref({
  visibility: 'public',
  nextToken: '',
  maxResults: 100,
  instanceTypeId: '',
  osType: '',
  platform: '',
})

const platformList = [
  'CentOS',
  'Debian',
  'veLinux',
  'Windows Server',
  'Fedora',
  'OpenSUSE',
  'Ubuntu',
]

const nameRegex = /^[a-z0-9][a-z0-9._-]{0,127}$/
const tagRegex = /^[A-Za-z0-9_][A-Za-z0-9_.-]{0,127}$/
const MAX_MY_IMAGES = 10

const drawerVisible = ref(false)
const targetImage = ref<any>({})
const imageTableData = ref<any[]>([])
const myImages = ref<MyImageItem[]>([])
const myImageMeta = ref<any>({})
const myImageLoading = ref(false)
const myImageKeyword = ref('')
const pushDialogVisible = ref(false)
const commandDialogVisible = ref(false)
const commandDialogTitle = ref('镜像命令')
const commandLoading = ref(false)
const pushForm = reactive({ name: '', tag: 'latest' })
const commandResult = reactive<{ image: string; commands: string[]; tip?: string; note?: string }>({
  image: '',
  commands: [],
  tip: '',
  note: '',
})

const isPrivateImage = computed(() => searchParams.value.visibility === 'private')
const myImageCount = computed(() => new Set(myImages.value.map(getImageRepositoryKey).filter(Boolean)).size)
const myImageLimitReached = computed(() => myImageCount.value >= MAX_MY_IMAGES)
const filteredMyImages = computed(() => {
  const keyword = myImageKeyword.value.trim().toLowerCase()
  if (!keyword) return myImages.value
  return myImages.value.filter(item => [item.repo, item.tag, item.image].join(' ').toLowerCase().includes(keyword))
})

function formatTime(value: string, pattern = 'YYYY-MM-DD HH:mm:ss') {
  if (!value) return '-'
  const time = dayjs(value)
  return time.isValid() ? time.format(pattern) : '-'
}

function getRepoName(repo: string) {
  return repo?.split('/').filter(Boolean).pop() || repo || '-'
}

function getImageRepositoryKey(image: MyImageItem) {
  const value = String(image.repo || image.image || '').trim()
  if (!value) return ''
  const tagSeparator = value.lastIndexOf(':')
  return tagSeparator > value.lastIndexOf('/') ? value.slice(0, tagSeparator) : value
}

function unwrapExternalResult(res: any) {
  const data = res?.data || {}
  if (data.success === false) {
    ElMessage.warning(data.message || data.note || '镜像服务暂时不可用')
  }
  return data
}

function resetCommandResult() {
  commandResult.image = ''
  commandResult.commands = []
  commandResult.tip = ''
  commandResult.note = ''
}

async function loadMyImages() {
  myImageLoading.value = true
  try {
    const res = await getMyImageListApi()
    const data = unwrapExternalResult(res)
    myImageMeta.value = data
    myImages.value = Array.isArray(data.images) ? data.images : []
  } finally {
    myImageLoading.value = false
  }
}

function openPushDialog() {
  if (myImageLimitReached.value) {
    ElMessage.warning(`自定义镜像最多只能上传${MAX_MY_IMAGES}个`)
    return
  }
  pushForm.name = ''
  pushForm.tag = 'latest'
  pushDialogVisible.value = true
}

async function createPushCommand() {
  if (myImageLimitReached.value) {
    ElMessage.warning(`自定义镜像最多只能上传${MAX_MY_IMAGES}个`)
    pushDialogVisible.value = false
    return
  }
  const name = pushForm.name.trim()
  const tag = pushForm.tag.trim() || 'latest'
  if (!nameRegex.test(name)) {
    ElMessage.warning('镜像名称只能包含小写字母、数字、点、下划线和中划线，并且必须以字母或数字开头')
    return
  }
  if (!tagRegex.test(tag)) {
    ElMessage.warning('Tag 只能包含字母、数字、点、下划线和中划线')
    return
  }
  commandLoading.value = true
  try {
    const res = await getMyImagePushCommandApi({ name, tag })
    const data = unwrapExternalResult(res)
    if (!data.success) return
    showCommandDialog('上传镜像命令', data)
    pushDialogVisible.value = false
  } finally {
    commandLoading.value = false
  }
}

async function showPullCommand(row: MyImageItem) {
  const res = await getMyImagePullCommandApi({ repo: row.repo, tag: row.tag || 'latest' })
  const data = unwrapExternalResult(res)
  if (!data.success) return
  showCommandDialog('下载镜像命令', data)
}

function showCommandDialog(title: string, data: any) {
  resetCommandResult()
  commandDialogTitle.value = title
  commandResult.image = data.image || ''
  commandResult.commands = Array.isArray(data.commands) ? data.commands : []
  commandResult.tip = data.tip || ''
  commandResult.note = data.note || ''
  commandDialogVisible.value = true
}

async function deleteMyImage(row: MyImageItem) {
  try {
    await ElMessageBox.confirm(`确认删除镜像 ${getRepoName(row.repo)}:${row.tag || 'latest'}？`, '删除镜像', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消',
    })
  } catch {
    return
  }
  const res = await deleteMyImageApi({ repo: row.repo, tag: row.tag || 'latest' })
  const data = unwrapExternalResult(res)
  if (data.success === false) return
  ElMessage.success('删除成功')
  loadMyImages()
}

function checkDetail(row: any) {
  targetImage.value = row
  drawerVisible.value = true
}

function initTable() {
  imageTableData.value = []
  searchParams.value.nextToken = ''
}

function resetParams() {
  searchParams.value.platform = ''
  searchParams.value.instanceTypeId = ''
  if (isPrivateImage.value) {
    myImageKeyword.value = ''
    loadMyImages()
  } else {
    searchList()
  }
}

async function getImageList() {
  if (isPrivateImage.value) {
    await loadMyImages()
    return
  }
  const param: any = {
    visibility: searchParams.value.visibility,
    maxResults: 10,
  }
  if (searchParams.value.nextToken) param.nextToken = searchParams.value.nextToken
  const res = await getImageListApi(param)
  imageTableData.value = Array.isArray(res?.data?.images) ? res.data.images : []
}

async function searchList() {
  if (isPrivateImage.value) {
    await loadMyImages()
    return
  }
  const param: any = {
    visibility: searchParams.value.visibility,
    platform: searchParams.value.platform,
    instanceTypeId: searchParams.value.instanceTypeId,
    maxResults: 10,
  }
  if (searchParams.value.nextToken) param.nextToken = searchParams.value.nextToken
  const res = await getImageListApi(param)
  imageTableData.value = Array.isArray(res?.data?.images) ? res.data.images : []
}

watch(
  () => searchParams.value.visibility,
  () => {
    initTable()
    resetParams()
  }
)

onMounted(() => {
  getImageList()
})
</script>

<style lang="scss" scoped>
@import './index.scss';
</style>
