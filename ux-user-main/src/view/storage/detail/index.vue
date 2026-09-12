<template>
  <div class="storage-detail">
    <Breadcrumb :router-list="routerList"></Breadcrumb>
    <div class="storage-content">
      <el-radio-group v-model="menuOption.radio" size="large">
        <el-radio-button label="存储桶详情" value="1" />
        <el-radio-button label="文件列表" value="2" />
      </el-radio-group>
      <div class="card" style="padding-bottom: 0">
        <!-- 存储桶详情=> menuOption.radio === 1 -->
        <div v-if="menuOption.radio === '1'">
          <!-- 用量概览 -->
          <TipText class="fwb" content="用量概览" fontSize="20" />
          <div class="preview-list flx-align-center">
            <div class="preview-card">
              <div class="label">桶对象数量</div>
              <div class="value">
                {{ bucketObject.bucketObjectCount }}<span class="unit">个</span>
              </div>
            </div>
            <div class="preview-card">
              <div class="label">桶对象总容量</div>
              <div class="value">
                {{ bucketObject.bucketObjectSize }}<span class="unit">Gb</span>
              </div>
            </div>
            <!-- <div class="preview-card">
              <div class="label">桶今日外网下载总流量</div>
              <div class="value">--<span class="unit">MB</span></div>
            </div>
            <div class="preview-card">
              <div class="label">桶今日内网下载总流量</div>
              <div class="value">--<span class="unit">MB</span></div>
            </div>
            <div class="preview-card">
              <div class="label">桶今日CDN回源流量</div>
              <div class="value">--<span class="unit">MB</span></div>
            </div>
            <div class="preview-card">
              <div class="label">桶今日总请求次数</div>
              <div class="value">--<span class="unit">次</span></div>
            </div> -->
          </div>
          <!-- 集群网络消息 -->
          <TipText class="fwb" content="集群网络信息" fontSize="20" />
          <div class="gray-card">
            <div class="info-card">
              <div class="label">存储桶名称</div>
              <div class="value">{{ bucketObject.name }}</div>
              <img
                src="../../../assets/icon/copy-icon.png"
                @click="copyText(bucketObject.name)"
              />
            </div>
            <div class="info-card">
              <div class="label">地域</div>
              <div class="value">{{ bucketObject.region }}</div>
            </div>
            <div class="info-card">
              <div class="label">多AZ冗余</div>
              <div class="value">
                {{ bucketObject.redundancyType == 0 ? '单冗余' : '多AZ冗余' }}
              </div>
            </div>
            <div class="info-card">
              <div class="label">默认存储类型</div>
              <div class="value">{{ bucketObject.storageType }}</div>
            </div>
            <div class="info-card">
              <div class="label">桶策略</div>
              <div class="value" v-if="bucketObject.bucketStrategy == 0">
                私有
              </div>
              <div class="value" v-else-if="bucketObject.bucketStrategy == 1">
                公共读
              </div>
              <div class="value" v-else-if="bucketObject.bucketStrategy == 2">
                公共读写
              </div>
            </div>
            <!-- <div class="info-card">
              <div class="label">对象ACL默认策略</div>
              <div class="value">默认私有</div>
            </div> -->
            <!-- <div class="info-card">
              <div class="label">项目</div>
              <div class="value">default</div>
              <img src="../../../assets/icon/copy-icon.png" />
            </div> -->
            <div class="info-card">
              <div class="label">创建时间</div>
              <div class="value">{{ bucketObject.createTime }}</div>
            </div>
          </div>
          <!-- 访问域名 -->
          <TipText class="fwb mb28" content="访问域名" fontSize="20" />
          <ProTable
            type="none"
            class="mb30"
            :columns="bucketColumns"
            :tableData="bucketTableData"
            :get-list="getList"
            :IsRefresh="false"
            :is-page="false"
          >
          </ProTable>
        </div>
        <div v-else-if="menuOption.radio === '2'" class="table-box mb30">
          <TipText
            class="fwb flx-align-center"
            content="文件列表"
            fontSize="20"
            style="z-index: 100"
          >
            <template #end>
              <div class="searchBar flx-align-center mb28">
                <!-- <div class="left flx-align-center">
                  复制路径<img src="../../../assets/icon/copy-icon.png" />
                </div> -->
                <el-button type="primary" @click="toUpload">上传文件</el-button>
                <el-button type="primary" @click="createDialog = true"
                  >创建文件夹</el-button
                >
                <el-button
                  type="primary"
                  :disabled="!selectNum"
                  @click="batchMoveDialog = true"
                  >移动到</el-button
                >
                <el-button
                  type="primary"
                  :disabled="!selectNum"
                  @click="batchCopyDialog = true"
                  >复制到</el-button
                >
                <el-input
                  v-model="folderSearchParam.name"
                  placeholder="添加筛选条件"
                  :prefix-icon="Search"
                ></el-input>
                <el-button type="primary" @click="folderSearchFn"
                  >搜索</el-button
                >
              </div>
            </template>
          </TipText>
          <div class="folder-list flx-align-center" style="z-index: 101">
            <div
              class="folder-item text-button high"
              @click="toNextFolder(null)"
            >
              {{ bucketObject.name }} /
            </div>
            <div
              :class="{
                'folder-item': true,
                high: index !== folderList.length - 1,
                'text-button': index !== folderList.length - 1,
              }"
              v-for="(item, index) in folderList"
              :key="item.fileId"
              @click="toNextFolder(item.fileId)"
            >
              {{ item.fileName
              }}{{ index !== folderList.length - 1 ? ' /' : '' }}
            </div>
          </div>
          <ProTable
            ref="folderTableRef"
            type="selection"
            class="mb30"
            :columns="folderColumns"
            :tableData="folderTableData"
            :page-data="folderPageData"
            :get-list="getList"
            :IsRefresh="false"
            :is-page="false"
          >
            <template #name="row">
              <div
                :class="row.type == 1 ? 'text-button' : ''"
                @click="row.type == 1 ? toNextFolder(row.id) : ''"
              >
                {{ row.name }}
              </div>
            </template>
            <template #path="row">
              {{ row.type == 2 ? row.path : '--' }}
            </template>
            <template #size="row"> {{ row.size }}MB </template>
            <template #handle="row">
              <el-popconfirm
                title="确认删除当前文件夹吗?"
                @confirm="deleteFolderHandler(row.id)"
                confirm-button-text="确认"
                cancel-button-text="取消"
              >
                <template #reference>
                  <div class="text-button" style="color: #ff4151">删除</div>
                </template>
              </el-popconfirm>
            </template>
          </ProTable>
          <Pagination :page-data="folderPageData" :PageChange="folderGetList" />
        </div>
      </div>
    </div>
    <el-dialog title="创建文件夹" v-model="createDialog" :width="400">
      <div class="content mt20 flx-align-center">
        <div style="width: 100px">文件夹名称:</div>
        <el-input v-model="newFolderName" />
      </div>
      <div class="tip">
        文件夹名仅支持输入英文、数字和分隔符- 且需要英文或数字开头
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="createDialog = false">取消</el-button>
          <el-button type="primary" @click="createFolderHandler">
            确认
          </el-button>
        </div>
      </template>
    </el-dialog>
    <el-dialog title="移动文件操作" v-model="batchMoveDialog" :width="400">
      <div class="content mt20 flx-align-center">
        <div style="width: 100px">目标文件夹:</div>
        <el-select v-model="targetFolderId">
          <el-option
            v-for="item in batchFolderOptions"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="batchMoveDialog = false">取消</el-button>
          <el-button type="primary" @click="moveToFolderHandler">
            确认
          </el-button>
        </div>
      </template>
    </el-dialog>
    <el-dialog title="复制文件操作" v-model="batchCopyDialog" :width="400">
      <div class="content mt20 flx-align-center">
        <div style="width: 100px">目标文件夹:</div>
        <el-select v-model="targetFolderId">
          <el-option
            v-for="item in batchFolderOptions"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="batchCopyDialog = false">取消</el-button>
          <el-button type="primary" @click="copyFolderHandler">
            确认
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts" name="Storage">
import {
  copyFileApi,
  createFolderAPI,
  deleteFileAPI,
  getAllFolderByIdApi,
  getBucketDetailAPI,
  getFileListByIdAPI,
  moveFileApi,
} from '@/api/storage'
import { useTable } from '@/hooks/useTable'
import { copyText, toPage } from '@/utils'
import { Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
const route = useRoute()
const name = ref(route.query.name)
const bucketId = ref(route.query.id)
const folderId = ref(null)
const bucketObject = ref<any>({ bucketObjectCount: 0 })
const folderTableRef = ref<any>()
const routerList = [
  { name: '对象存储', path: '/objectStorage' },
  { name: '存储桶详情', path: '' },
]
const menuOption = ref({
  radio: '1',
})
// 文件夹路由
const folderList = ref<any>([])
const bucketColumns: ColumnProps[] = [
  { prop: 'label', label: '' },
  {
    prop: 'endpoint',
    label: 'EndPoint(地域节点)',
  },
  { prop: 'S3', label: 'S3 Endpoint' },
  { prop: 'bucket', label: 'Bucket域名' },
]
const bucketTableData = computed(() => [
  {
    label: '外网访问',
    endpoint: bucketObject.value.extranetEndpoint,
    S3: bucketObject.value.extranetS3Endpoint,
    bucket: bucketObject.value.extranetDomain,
  },
  {
    label: '内网访问',
    endpoint: bucketObject.value.intranetEndpoint,
    S3: bucketObject.value.intranetS3Endpoint,
    bucket: bucketObject.value.intranetDomain,
  },
])
const folderColumns: ColumnProps[] = [
  { prop: 'name', label: '文件名称', slot: true },
  {
    prop: 'size',
    label: '大小',
    slot: true,
  },
  { prop: 'path', label: '文件地址', slot: true },
  { prop: 'updateTime', label: '修改时间' },
  { prop: 'handle', label: '操作', slot: true },
]
const folderInitParam = ref({
  bucketId: bucketId.value,
  fileId: null,
})
const {
  tableData: folderTableData,
  searchParam: folderSearchParam,
  searchFn: folderSearchFn,
  getList: folderGetList,
  pageData: folderPageData,
} = useTable({
  requestApi: getFileListByIdAPI,
  initParams: {
    bucketId: folderInitParam.value.bucketId,
  },
})

// 创建文件夹
const createDialog = ref(false)
const newFolderName = ref('')
// 文件夹名正则判断
function folderNameTest(input: string) {
  // 正则表达式：以英文或数字开头，只允许英文、数字和-
  const regex = /^[a-zA-Z0-9][a-zA-Z0-9-]{0,254}$/

  // 检查输入是否符合正则表达式且长度在0-255之间
  if (regex.test(input) && input.length <= 255) {
    return true // 输入有效
  } else {
    return false // 输入无效
  }
}
const createFolderHandler = () => {
  const data: any = {
    bucketId: bucketId.value,
    name: newFolderName.value,
  }
  if (folderSearchParam.value.fileId) {
    data.fileId = folderSearchParam.value.fileId
  }
  if (folderNameTest(newFolderName.value)) {
    createFolderAPI(data).then((res: any) => {
      if (res.code === 200) {
        ElMessage.success('创建文件夹成功')
        newFolderName.value = ''
        createDialog.value = false
        folderGetList()
      }
    })
  } else {
    ElMessage.warning('请检查文件夹名是否符合规则')
  }
}

// 删除文件夹
const deleteFolderHandler = (id: any) => {
  deleteFileAPI(id).then((res: any) => {
    if (res.code === 200) {
      ElMessage.success('删除成功')
      folderGetList()
    }
  })
}

// 查看文件夹详情
const toNextFolder = (id?: any) => {
  folderSearchParam.value.fileId = id ? id : null
  folderGetList(true).then((res) => {
    folderList.value = res.fileList.reverse()
  })
  folderSearchFn()
}

// 上传文件
const toUpload = () => {
  if (folderSearchParam.value.fileId)
    toPage(
      `/storageDetail/upload?name=${name.value}&bucketId=${folderInitParam.value.bucketId}&fileId=${folderSearchParam.value.fileId}`
    )
  else
    toPage(
      `/storageDetail/upload?name=${name.value}&bucketId=${folderInitParam.value.bucketId}`
    )
}
const batchMoveDialog = ref(false)
const batchCopyDialog = ref(false)
const targetFolderId = ref()

const batchFolderOptions = ref<any>([])
const selectNum = computed(() => {
  return folderTableRef.value ? folderTableRef.value.selectedListIds.length : 0
})
// 移动到
const moveToFolderHandler = () => {
  const data = {
    fileId: targetFolderId.value,
    fileIdList: folderTableRef.value.selectedListIds,
  }
  moveFileApi(data).then((res: any) => {
    if (res.code === 200) {
      ElMessage.success('移动成功')
      targetFolderId.value = ''
      folderTableRef.value.isSelected = false
      folderTableRef.value.selectedList.length = 0
      folderTableRef.value.selectedListIds.length = 0
      initPage()
      folderGetList()
      batchMoveDialog.value = false
    }
  })
}

// 复制到
const copyFolderHandler = () => {
  const data = {
    fileId: targetFolderId.value,
    fileIdList: folderTableRef.value.selectedListIds,
  }
  copyFileApi(data).then((res: any) => {
    if (res.code === 200) {
      ElMessage.success('复制成功')
      targetFolderId.value = ''
      folderTableRef.value.isSelected = false
      folderTableRef.value.selectedList.length = 0
      folderTableRef.value.selectedListIds.length = 0
      initPage()
      folderGetList()
      batchCopyDialog.value = false
    }
  })
}
// 初始化页面数据
const initPage = () => {
  getBucketDetailAPI(name.value).then((res) => {
    bucketObject.value = res.data
    console.log(bucketObject.value)
  })
  getAllFolderByIdApi(bucketId.value).then((res) => {
    batchFolderOptions.value = res.data
  })
}
initPage()
// 获取表格数据
const getList = () => {}
</script>
<style lang="scss" scoped>
@import './index.scss';
</style>
