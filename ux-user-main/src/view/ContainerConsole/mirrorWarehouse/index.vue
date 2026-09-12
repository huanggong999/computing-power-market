/** 镜像仓库 */
<template>
  <div class="warehouse">
    <div class="tips flx-center">
      <el-icon><Document /></el-icon><span> &nbsp; 使用指南</span>
    </div>
    <div class="card">
      <!-- <TipText class="fwb table-header mb28" content="镜像列表" fontSize="20">
        <template #end>
          <div class="searchBar">
            <el-input
              :prefix-icon="Search"
              placeholder="添加筛选条件"
            ></el-input>
            <el-button type="primary">搜索</el-button>
            <el-button>重置</el-button>
            <el-button
              type="primary"
              :icon="CirclePlus"
              @click="toPage('/createMirrorInstance')"
              >创建实例</el-button
            >
          </div>
        </template>
      </TipText> -->
      <div class="content">
        <ProTable
          ref="tableRef"
          :page-data="pageData"
          :columns="tableCol"
          :tableData="tableData"
          :search-param="tableParam"
          :search-fn="tableSearch"
          :getList="tableGet"
          :IsRefresh="false"
          :is-page="false"
        >
          <template #tableHeader>
            <div class="table-header mb26">
              <TipText class="fwb" content="镜像列表" fontSize="20">
                <template #end>
                  <div class="searchBar">
                    <el-input
                      :prefix-icon="Search"
                      placeholder="添加筛选条件"
                      v-model="tableParam.instanceName"
                    ></el-input>
                    <el-button type="primary" @click="tableSearch"
                      >搜索</el-button
                    >
                    <el-button @click="tableReset">重置</el-button>
                    <el-button
                      type="primary"
                      :icon="CirclePlus"
                      @click="toPage('/createMirrorInstance')"
                      >创建实例</el-button
                    >
                    <!-- <el-button>导出</el-button> -->
                  </div>
                </template>
              </TipText>
            </div>
          </template>
          <template #status="row">
            <el-tag type="info" v-if="row.status == 'CREATING'">创建中</el-tag>
            <el-tag type="success" v-if="row.status == 'RUNNING'"
              >运行中</el-tag
            >
            <el-tag type="danger" v-if="row.status == 'STOPPED'">已停止</el-tag>
            <el-tag type="warning" v-if="row.status == 'RESIZING'"
              >更配中</el-tag
            >
            <el-tag type="danger" v-if="row.status == 'ERROR'"> 错误 </el-tag>
            <el-tag type="danger" v-if="row.status == 'DELETING'"
              >删除中</el-tag
            >
          </template>
          <template #chargeType="row">
            <span v-if="row.chargeType == 'POSTPAID_BY_HOUR'">按量计费</span>
            <span v-else>包年包月</span>
          </template>
          <template #handle="row">
            <span class="blue" @click="toDetail(row)">进入实例</span>
          </template>
        </ProTable>
      </div>
    </div>
    <div class="card">
      <TipText
        class="fwb table-header mb39"
        content="实例设置说明"
        fontSize="20"
      >
        <template #end>
          <el-radio-group v-model="right.radio">
            <el-radio-button label="Docker" value="Docker" />
            <el-radio-button label="Nerdctl" value="Nerdctl" />
          </el-radio-group>
        </template>
      </TipText>
      <div class="right-content" v-if="right.radio === 'Docker'">
        <div class="step">
          <div class="title flx-align-center mb8">
            <div class="num">1</div>
            创建镜像仓库
          </div>
          <div class="info ml32 mb8">实例登录,上传/下载私有镜像,快捷指令:</div>
          <div class="copy-card flx-center ml32">
            {{
              `docker login —username=${userName}
            vcicr-cn-beijing.cr.volces.com`
            }}
            <img src="../../../assets//icon/copy-icon.png" />
          </div>
        </div>
        <div class="step">
          <div class="title flx-align-center mb8">
            <div class="num">2</div>
            OCI制品仓库
          </div>
          <div class="info ml32 mb8">OCI制品仓库，管理容器镜像</div>
        </div>
        <div class="step">
          <div class="title flx-align-center mb8">
            <div class="num">3</div>
            推送/拉取镜像
          </div>
          <div class="info ml32 mb8">使用命令行完成镜像推取，快捷指令:</div>
          <div class="copy-card flx-center ml32 mb10">
            docker pull
            vcicr-cn-beijing.cr.volces.com/[命名空间]/[镜像仓库]:[镜像版本号]
            <img src="../../../assets//icon/copy-icon.png" />
          </div>
          <div class="copy-card flx-center ml32">
            docker pull
            vcicr-cn-beijing.cr.volces.com/[命名空间]/[镜像仓库]:[镜像版本号]
            <img src="../../../assets//icon/copy-icon.png" />
          </div>
        </div>
      </div>
      <div class="right-content" v-else-if="right.radio === 'Nerdctl'">
        <div class="step">
          <div class="title flx-align-center mb8">
            <div class="num">1</div>
            登录Registry
          </div>
          <div class="info ml32 mb8"></div>
          <div class="copy-card flx-center ml32">
            {{
              `nerdctl login --username=${userName}
            vcicr-cn-beijing.cr.volces.com`
            }}
            <img src="../../../assets//icon/copy-icon.png" />
          </div>
        </div>
        <div class="step">
          <div class="title flx-align-center mb8">
            <div class="num">2</div>
            下载镜像
          </div>
          <div class="copy-card flx-center ml32">
            nerdctl pull
            vcicr-cn-beijing.cr.volces.com/namespace/oci:[镜像版本号]
            <img src="../../../assets//icon/copy-icon.png" />
          </div>
        </div>
        <div class="step">
          <div class="title flx-align-center mb8">
            <div class="num">3</div>
            上传镜像
          </div>

          <div class="copy-card flx-center ml32 mb10">
            nerdctl push
            vcicr-cn-beijing.cr.volces.com/namespace/oci:[镜像版本号]
            <img src="../../../assets//icon/copy-icon.png" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts" name="CreateCluster">
import { getEcsListApi } from '@/api/order'
import { useTable } from '@/hooks/useTable'
import { Search, CirclePlus } from '@element-plus/icons-vue'
import { ref } from 'vue'
import { toPage } from '@/utils'
import {
  getImageRepositoryListApi,
  getRepositoryUsernameApi,
} from '@/api/image'
const tableCol: ColumnProps[] = [
  { label: '实例名称', prop: 'instanceName' },
  { label: '状态', prop: 'status', slot: true },

  { label: '版本', prop: 'version' },
  {
    label: '计费方式',
    prop: 'chargeType',
    slot: true,
  },
  {
    label: '创建时间',
    prop: 'createTime',
  },
  {
    label: '操作',
    prop: 'handle',
    slot: true,
  },
]
// 获取镜像列表
const {
  pageData,
  getList: tableGet,
  tableData: tableData,
  searchFn: tableSearch,
  searchParam: tableParam,
  resetFn: tableReset,
} = useTable({
  requestApi: getImageRepositoryListApi,
  requestAuto: false,
})
// 获取userName
const userName = ref('')
const getUserName = () => {
  getRepositoryUsernameApi().then((res) => {
    if (res.code === 200) {
      userName.value = res.data
    }
  })
}
const right = ref({
  radio: 'Docker',
})
// 查看仓库详情
const toDetail = (row: any) => {
  toPage(`/mirrorInstance?instanceName=${row.instanceName}`)
}
const initPage = () => {
  tableGet()
  getUserName()
}
initPage()
</script>
<style lang="scss" scoped>
.warehouse {
  display: grid;
  grid-template-columns: 1fr 440px;
  gap: 20px;
  height: 100%;
  .card {
    z-index: 100;
  }
  .el-radio-group {
    display: flex;
    flex-wrap: nowrap;
  }
  .tips {
    position: absolute;
    top: 130px;
    left: 1840px;
    font-weight: 400;
    font-size: 18px;
    color: #3972fd;
  }
  .table-header {
    width: 61svw;
    .searchBar {
      display: flex;
      gap: 10px;
      .el-input {
        width: 260px;
      }
      .el-button {
        margin: 0px;
      }
    }
  }
  .step {
    margin-bottom: 30px;
    .title {
      font-weight: 500;
      font-size: 18px;
      color: #333333;
      .num {
        width: 24px;
        height: 24px;
        background: #ffffff;
        border: 1px solid #e5e5e5;
        border-radius: 50%;
        font-weight: 400;
        font-size: 18px;
        color: #3972fd;
        text-align: center;
        margin-right: 8px;
      }
    }
    .info {
      font-weight: 400;
      font-size: 16px;
      color: #83889d;
    }
    .copy-card {
      padding: 12px;
      background: #ffffff;
      border-radius: 4px;
      border: 1px solid #e5e5e5;
      font-weight: 400;
      font-size: 16px;
      color: #83889d;
      img {
        height: 22px;
        width: 22px;
        &:hover {
          cursor: pointer;
        }
      }
    }
  }
}
.blue {
  color: #3972fd;
  &:hover {
    cursor: pointer;
  }
}
</style>
