/** 实例详情 */
<template>
  <Breadcrumb :router-list="routerList" />
  <div class="warehouse">
    <div class="tips flx-center">
      <el-icon><Document /></el-icon><span> &nbsp; 使用指南</span>
    </div>
    <div>
      <el-radio-group v-model="leftOption.radio" size="large">
        <el-radio-button label="镜像仓库概览" value="1" />
        <el-radio-button label="OCI概览信息" value="2" />
        <el-radio-button label="镜像使用指南" value="3" />
        <el-radio-button label="Chart使用指南" value="4" />
      </el-radio-group>
      <div class="card">
        <!-- 镜像仓库概览 -->
        <div class="content preview" v-if="leftOption.radio === '1'">
          <div class="title-box flx-align-center mb23">
            <div class="title">{{ instanceDetail.instanceName }}</div>
            <el-tag
              class="ml10"
              type="info"
              v-if="instanceDetail.status == 'CREATING'"
              >创建中</el-tag
            >
            <el-tag
              class="ml10"
              type="success"
              v-if="instanceDetail.status == 'RUNNING'"
              >运行中</el-tag
            >
            <el-tag
              class="ml10"
              type="danger"
              v-if="instanceDetail.status == 'STOPPED'"
              >已停止</el-tag
            >
            <el-tag
              class="ml10"
              type="warning"
              v-if="instanceDetail.status == 'RESIZING'"
              >更配中</el-tag
            >
            <el-tag
              class="ml10"
              type="danger"
              v-if="instanceDetail.status == 'ERROR'"
            >
              错误
            </el-tag>
            <el-tag
              class="ml10"
              type="danger"
              v-if="instanceDetail.status == 'DELETING'"
              >删除中</el-tag
            >
            <el-button
              type="warning"
              plain
              style="margin-left: auto"
              @click="setPsw"
              >设置仓库实例密码</el-button
            >
          </div>
          <div class="info-box mb30">
            <div class="info flx-align-center">
              <div class="label">username</div>
              <div class="value">
                {{ instanceDetail.username ? instanceDetail.username : '--' }}
              </div>
            </div>
            <div class="info flx-align-center">
              <div class="label">登录实例命令</div>
              <div class="value flx-align-center">
                <el-tooltip class="mr70" placement="bottom" effect="light">
                  <template #content>
                    <div class="copy-card-1 flx-center">
                      docker pull
                      vcicr-cn-beijing.cr.volces.com/[命名空间]/[镜像仓库]:[镜像版本号]
                      <img src="../../../assets/icon/copy-icon.png" />
                    </div>
                  </template>
                  <div class="command">Docker命令</div>
                </el-tooltip>

                <el-tooltip placement="bottom" effect="light">
                  <template #content>
                    <div class="copy-card-1 flx-center">
                      docker pull
                      vcicr-cn-beijing.cr.volces.com/[命名空间]/[镜像仓库]:[镜像版本号]
                      <img src="../../../assets/icon/copy-icon.png" />
                    </div>
                  </template>
                  <div class="command">Nerdctl命令</div>
                </el-tooltip>
              </div>
            </div>
            <div class="info flx-align-center">
              <div class="label">访问域名</div>
              <div class="value">{{ instanceDetail.domain }}</div>
            </div>
          </div>
          <!-- <div class="gray-card mb20">
            <TipText
              class="fwb table-header mb30"
              content="资源使用情况"
              fontSize="20"
            ></TipText>
            <div class="detail-box flx-center">
              <div
                class="detail"
                style="padding-right: 165px; border-right: 1px solid #e5e5e5"
              >
                <div class="label">当前存储容量</div>
                <div class="value">0.06<span class="unit">GB</span></div>
              </div>
              <div class="detail" style="padding-left: 165px">
                <div class="label">本月公网流出流量</div>
                <div class="value">0.06<span class="unit">MB</span></div>
              </div>
            </div>
          </div> -->
          <div class="gray-card">
            <TipText
              class="fwb table-header mb30"
              content="资源使用情况"
              fontSize="20"
            ></TipText>
            <div class="info-box">
              <div class="info flx-align-center">
                <div class="label">实例名称</div>
                <div class="value">{{ instanceDetail.instanceName }}</div>
              </div>
              <div class="info flx-align-center">
                <div class="label">付费类型</div>
                <div class="value">按量计费</div>
              </div>
              <div class="info flx-align-center">
                <div class="label">实例规格</div>
                <div class="value">{{ instanceDetail.version }}</div>
              </div>
              <div class="info flx-align-center">
                <div class="label">地域</div>
                <div class="value">{{ instanceDetail.region }}</div>
              </div>
              <div class="info flx-align-center">
                <div class="label">项目</div>
                <div class="value">{{ instanceDetail.project }}</div>
              </div>
            </div>
          </div>
        </div>
        <!-- OCI概览信息 -->
        <div class="content oci" v-else-if="leftOption.radio === '2'">
          <div class="title-box flx-align-center mb23">
            <div class="title">{{ instanceDetail.instanceName }}</div>
            <el-tag
              class="ml30"
              type="info"
              v-if="instanceDetail.status == 'CREATING'"
              >创建中</el-tag
            >
            <el-tag
              class="ml30"
              type="success"
              v-if="instanceDetail.status == 'RUNNING'"
              >运行中</el-tag
            >
            <el-tag
              class="ml30"
              type="danger"
              v-if="instanceDetail.status == 'STOPPED'"
              >已停止</el-tag
            >
            <el-tag
              class="ml30"
              type="warning"
              v-if="instanceDetail.status == 'RESIZING'"
              >更配中</el-tag
            >
            <el-tag
              class="ml30"
              type="danger"
              v-if="instanceDetail.status == 'ERROR'"
            >
              错误
            </el-tag>
            <el-tag
              class="ml30"
              type="danger"
              v-if="instanceDetail.status == 'DELETING'"
              >删除中</el-tag
            >
          </div>
          <div class="grid-3">
            <div class="item">
              <div class="item-title">命名空间</div>
              <div class="item-content">
                {{ instanceOci.namespace ? instanceOci.namespace : '--' }}
              </div>
            </div>
            <div class="item">
              <div class="item-title">OCI制品仓库名称</div>
              <div class="item-content">
                {{ instanceOci.ociName ? instanceOci.ociName : '--' }}
              </div>
            </div>
            <div class="item">
              <div class="item-title">类型</div>
              <el-tag>{{ instanceOci.type }}</el-tag>
            </div>
          </div>
          <el-radio-group v-model="ociOption.radio" class="mt30 mb20">
            <el-radio-button label="镜像" value="Image" />
            <el-radio-button label="Chart" value="Chart" />
          </el-radio-group>
          <ProTable
            ref="tableRef"
            :page-data="pageData"
            :columns="tableCol"
            :tableData="tableData"
            :search-param="tableParam"
            :search-fn="tableSearch"
            :getList="tableGet"
            :IsRefresh="false"
            type="radio"
            :is-page="false"
          >
            <template #tableHeader>
              <div class="fwb table-header mb26">
                <TipText class="fwb" content="镜像列表" fontSize="20">
                  <template #end>
                    <div class="gray-tip flx-align-center">
                      每个仓库至多上传5000个镜像版本
                    </div>
                    <div class="search-bar flx-align-center">
                      <el-input
                        placeholder="请输入镜像版本名称"
                        prefix-icon="Search"
                        style="width: 260px"
                        v-model="tableParam.imageVersionName"
                      ></el-input>
                      <el-button type="primary" @click="tableSearchHandler"
                        >查询</el-button
                      >
                      <el-button @click="tableResetHandler">重置</el-button>
                    </div>
                  </template>
                </TipText>
              </div>
            </template>
            <template #handle>
              <span @click="deleteHandler">删除</span>
            </template>
          </ProTable>
        </div>
        <!-- 镜像使用指南 -->
        <div class="content handbook" v-else-if="leftOption.radio === '3'">
          <TipText
            class="alert"
            content="上传时,请勿[Chart 版本号]和[镜像版本号]设置为同一版本号,避免版本号覆盖导致老数据丢失"
            text-color="#ff4151"
            before-color="#ff4151"
            fontSize="16"
          ></TipText>
          <el-radio-group v-model="handbookOption.radio" class="mt30 mb20">
            <el-radio-button label="Docker" value="Docker" />
            <el-radio-button label="Nerdctl" value="Nerdctl" />
          </el-radio-group>
          <div class="gray-card" v-if="handbookOption.radio === 'Docker'">
            <div class="group">
              <div class="label">登录Registry</div>
              <div class="order">
                {{
                  `docker login —username=${userName}
                vcicr-cn-beijing.cr.volces.com`
                }}
                <img
                  src="../../../assets/icon/copy-icon.png"
                  @click="
                    copyText(
                      `docker login —username=${userName}
                vcicr-cn-beijing.cr.volces.com`
                    )
                  "
                />
              </div>
            </div>
            <div class="group">
              <div class="label">下载镜像</div>
              <div class="order">
                docker
                pull vcicr-cn-beijing.cr.volces.com/namespace/oci:[镜像版本号]
                <img
                  src="../../../assets/icon/copy-icon.png"
                  @click="
                    copyText(
                      'docker pull vcicr-cn-beijing.cr.volces.com/namespace/oci:[镜像版本号]'
                    )
                  "
                />
              </div>
            </div>
            <div class="group">
              <div class="label">上传镜像</div>

              <div class="order">
                {{
                  `docker login —username=${userName}
                vcicr-cn-beijing.cr.volces.com`
                }}
                <img
                  src="../../../assets/icon/copy-icon.png"
                  @click="
                    copyText(`docker login —username=${userName}
                vcicr-cn-beijing.cr.volces.com`)
                  "
                />
              </div>
              <div class="order">
                docker
                pull vcicr-cn-beijing.cr.volces.com/namespace/oci:[镜像版本号]
                <img
                  src="../../../assets/icon/copy-icon.png"
                  @click="
                    copyText(
                      'docker tag [ImageId] vcicr-cn-beijing.cr.volces.com/namespace/oci:[镜像版本号]'
                    )
                  "
                />
              </div>
              <div class="order">
                docker
                push vcicr-cn-beijing.cr.volces.com/namespace/oci:[镜像版本号]
                <img
                  src="../../../assets/icon/copy-icon.png"
                  @click="
                    copyText(
                      'docker push vcicr-cn-beijing.cr.volces.com/namespace/oci:[镜像版本号]'
                    )
                  "
                />
              </div>
            </div>
          </div>
          <div class="gray-card" v-else-if="handbookOption.radio === 'Nerdctl'">
            <div class="group">
              <div class="label">登录Registry</div>
              <div class="order">
                {{
                  `nerdctl login --username=${userName} vcicr-cn-beijing.cr.volces.com`
                }}
                <img
                  src="../../../assets/icon/copy-icon.png"
                  @click="
                    copyText(`nerdctl login --username=${userName}
                vcicr-cn-beijing.cr.volces.com`)
                  "
                />
              </div>
            </div>
            <div class="group">
              <div class="label">下载镜像</div>
              <div class="order">
                nerdctl pull
                vcicr-cn-beijing.cr.volces.com/namespace/oci:[镜像版本号]
                <img
                  src="../../../assets/icon/copy-icon.png"
                  @click="
                    copyText(
                      'nerdctl pull vcicr-cn-beijing.cr.volces.com/namespace/oci:[镜像版本号]'
                    )
                  "
                />
              </div>
            </div>
            <div class="group">
              <div class="label">上传镜像</div>

              <div class="order">
                nerdctl push
                vcicr-cn-beijing.cr.volces.com/namespace/oci:[镜像版本号]
                <img
                  src="../../../assets/icon/copy-icon.png"
                  @click="
                    copyText(
                      'nerdctl push vcicr-cn-beijing.cr.volces.com/namespace/oci:[镜像版本号]'
                    )
                  "
                />
              </div>
            </div>
          </div>
          <div class="blue-tip">
            请根据实际需求替换示例中的 [ImageId] 和 [镜像版本号] 参数。
          </div>
        </div>
        <!-- Chart使用指南 -->
        <div class="content handbook" v-else-if="leftOption.radio === '4'">
          <TipText
            class="alert"
            content="上传时,请勿[Chart 版本号]和[镜像版本号]设置为同一版本号,避免版本号覆盖导致老数据丢失"
            text-color="#ff4151"
            before-color="#ff4151"
            fontSize="16"
          ></TipText>

          <div class="gray-card mt20">
            <div class="group">
              <div class="label">前置条件</div>
              <div class="order" style="background: none; border: none">
                使用前请确保已安装 Helm 3 客户端，详情请参考官网。
              </div>
              <div class="order">
                开启 OCI 使用支持：export HELM_EXPERIMENTAL_OCI=1
                <img
                  src="../../../assets/icon/copy-icon.png"
                  @click="copyText('export HELM_EXPERIMENTAL_OCI=1')"
                />
              </div>
              <div class="order" style="background: none; border: none">
                因helm版本兼容性问题，在推拉文件时请统一使用3.7前或3.7后版本。
              </div>
            </div>
            <div class="group">
              <div class="label">登录 Registry</div>
              <div class="order">
                {{
                  `helm registry login
                —username=${userName} vcicr-cn-beijing.cr.volces.com`
                }}
                <img
                  src="../../../assets/icon/copy-icon.png"
                  @click="
                    copyText(
                      `helm registry login —username=${userName} vcicr-cn-beijing.cr.volces.com`
                    )
                  "
                />
              </div>
            </div>
            <div class="group">
              <div class="label">上传镜像</div>
              <div class="front-tip">helm 3.7 版本前</div>
              <div class="order">
                helm chart
                pull vcicr-cn-beijing.cr.volces.com/namespace/oci:[Chart 版本号]
                <img
                  src="../../../assets/icon/copy-icon.png"
                  @click="
                    copyText(
                      `helm chart pull vcicr-cn-beijing.cr.volces.com/namespace/oci:[Chart 版本号]`
                    )
                  "
                />
              </div>
              <div class="front-tip">helm 3.7 版本后</div>
              <div class="order">
                docker helm pull
                oci://vcicr-cn-beijing.cr.volces.com/namespace/oci —version
                [Chart 版本号]
                <img
                  src="../../../assets/icon/copy-icon.png"
                  @click="
                    copyText(
                      ' docker helm pull oci://vcicr-cn-beijing.cr.volces.com/namespace/oci —version'
                    )
                  "
                />
              </div>
            </div>
            <div class="group">
              <div class="label">上传Chart</div>
              <div class="front-tip">helm 3.7 版本前</div>
              <div class="order">
                {{
                  `helm registry login
                --username=${userName} vcicr-cn-beijing.cr.volces.com`
                }}
                <img
                  src="../../../assets/icon/copy-icon.png"
                  @click="
                    copyText(
                      `helm registry login --username=${userName} vcicr-cn-beijing.cr.volces.com`
                    )
                  "
                />
              </div>
              <div class="order">
                helm chart save [Chart 名称]
                vcicr-cn-beijing.cr.volces.com/namespace/oci:[Chart 版本号]
                <img
                  src="../../../assets/icon/copy-icon.png"
                  @click="
                    copyText(
                      'helm chart save [Chart 名称] vcicr-cn-beijing.cr.volces.com/namespace/oci:[Chart 版本号]'
                    )
                  "
                />
              </div>
              <div class="order">
                helm chart
                push vcicr-cn-beijing.cr.volces.com/namespace/oci:[Chart 版本号]
                <img
                  src="../../../assets/icon/copy-icon.png"
                  @click="
                    copyText(
                      'helm chart push vcicr-cn-beijing.cr.volces.com/namespace/oci:[Chart 版本号]'
                    )
                  "
                />
              </div>
              <div class="front-tip">helm 3.7 版本后</div>
              <div class="order">
                {{
                  `helm registry login --username=${userName}
                vcicr-cn-beijing.cr.volces.com`
                }}

                <img
                  src="../../../assets/icon/copy-icon.png"
                  @click="
                    copyText(
                      `helm registry login --username=${userName} vcicr-cn-beijing.cr.volces.com`
                    )
                  "
                />
              </div>
              <div class="order">
                helm package [Chart 名称] --version [Chart 版本号]
                <img
                  src="../../../assets/icon/copy-icon.png"
                  @click="
                    copyText(
                      'helm package [Chart 名称] --version [Chart 版本号]'
                    )
                  "
                />
              </div>
              <div class="order">
                helm push [[Chart 名称]-[Chart 版本号].tgz]
                oci://vcicr-cn-beijing.cr.volces.com/namespace
                <img
                  src="../../../assets/icon/copy-icon.png"
                  @click="
                    copyText(
                      'helm push [[Chart 名称]-[Chart 版本号].tgz] oci://vcicr-cn-beijing.cr.volces.com/namespace'
                    )
                  "
                />
              </div>
            </div>
          </div>
          <div class="blue-tip">
            请根据实际需求替换示例中的 [ImageId] 和 [镜像版本号] 参数。
          </div>
        </div>
      </div>
    </div>

    <div class="card">
      <TipText
        class="fwb table-header mb39"
        content="实例设置说明"
        fontSize="20"
      >
        <template #end>
          <el-radio-group v-model="rightOption.radio">
            <el-radio-button label="Docker" value="Docker" />
            <el-radio-button label="Nerdctl" value="Nerdctl" />
          </el-radio-group>
        </template>
      </TipText>
      <div class="right-content" v-if="rightOption.radio === 'Docker'">
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
            <img
              src="../../../assets/icon/copy-icon.png"
              @click="
                copyText(`docker login —username=${userName}
            vcicr-cn-beijing.cr.volces.com`)
              "
            />
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
            <img
              src="../../../assets/icon/copy-icon.png"
              @click="
                copyText(
                  ' docker pull vcicr-cn-beijing.cr.volces.com/[命名空间]/[镜像仓库]:[镜像版本号]'
                )
              "
            />
          </div>
          <div class="copy-card flx-center ml32">
            docker pull
            vcicr-cn-beijing.cr.volces.com/[命名空间]/[镜像仓库]:[镜像版本号]
            <img
              src="../../../assets/icon/copy-icon.png"
              @click="
                copyText(
                  'docker pull vcicr-cn-beijing.cr.volces.com/[命名空间]/[镜像仓库]:[镜像版本号]'
                )
              "
            />
          </div>
        </div>
      </div>
      <div class="right-content" v-else-if="rightOption.radio === 'Nerdctl'">
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
            <img
              src="../../../assets//icon/copy-icon.png"
              @click="
                copyText(`nerdctl login --username=${userName}
            vcicr-cn-beijing.cr.volces.com`)
              "
            />
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
            <img
              src="../../../assets//icon/copy-icon.png"
              @click="
                copyText(
                  'nerdctl pull vcicr-cn-beijing.cr.volces.com/namespace/oci:[镜像版本号]'
                )
              "
            />
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
            <img
              src="../../../assets//icon/copy-icon.png"
              @click="
                copyText(
                  ' nerdctl push vcicr-cn-beijing.cr.volces.com/namespace/oci:[镜像版本号]'
                )
              "
            />
          </div>
        </div>
      </div>
    </div>
    <el-dialog v-model="dialogVisible" title="设置存储仓库密码" width="500">
      <el-input label="密码" type="password" v-model="password"></el-input>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="setPswHandler"> 确认 </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts" name="CreateCluster">
import {
  deleteRepositoryImageApi,
  getImageRepositoryDetailApi,
  getOCIOverviewListApi,
  getRepositoryUsernameApi,
  setRepositoryPasswordApi,
} from '@/api/image'
import { copyText } from '@/utils'
import { getEcsListApi } from '@/api/order'
import { useTable } from '@/hooks/useTable'
import { useRoute } from 'vue-router'
import { getOCIOverviewApi } from '../../../api/image'
import { ElMessage } from 'element-plus'
// 获取实例名称
const route = useRoute()

const instanceName = ref(route.query.instanceName)
// 获取userName
const userName = ref('')
const getUserName = () => {
  getRepositoryUsernameApi().then((res) => {
    if (res.code === 200) {
      userName.value = res.data
    }
  })
}
const routerList = ref([
  { name: '镜像仓库', path: '/mirrorWarehouse' },
  { name: '实例详情', path: '' },
])
const leftOption = ref({
  radio: '1',
})
const rightOption = ref({
  radio: 'Docker',
})
const ociOption = ref({
  radio: 'Image',
})
const handbookOption = ref({
  radio: 'Docker',
})
const tableCol: ColumnProps[] = [
  { label: '镜像版本', prop: 'imageVersion' },
  { label: '网络地址', prop: 'networkAddress' },

  { label: '大小', prop: 'imageSize' },
  {
    label: 'Digest',
    prop: 'imageDigest',
  },
  {
    label: '操作系统/架构',
    prop: 'osArch',
  },
  {
    label: '更新时间',
    prop: 'updateTime',
  },
  {
    label: '操作',
    prop: 'handle',
    slot: true,
  },
]

// 获取实例详情
const instanceDetail = ref<any>({
  domain: '',
  status: '',
})
const getDetail = async () => {
  let { data } = await getImageRepositoryDetailApi(instanceName.value)
  instanceDetail.value = data
}

// 获取OCI概览
const instanceOci = ref<any>({})
const getOciDetail = async () => {
  let { data } = await getOCIOverviewApi(instanceName.value)
  instanceOci.value = data
}

// 获取OCI镜像列表

const {
  pageData,
  getList: tableGet,
  tableData: tableData,
  searchFn: tableSearch,
  searchParam: tableParam,
} = useTable({
  requestApi: getOCIOverviewListApi,
  requestAuto: false,
  initParams: { instanceName: instanceName.value, pageNo: 1, pageSize: 10 },
})
// 搜索方法
const tableSearchHandler = () => {
  tableParam.value = {
    ...tableParam.value,
    instanceName: instanceName.value,
    type: ociOption.value.radio,
    pageNo: 1,
    pageSize: 10,
  }
  tableSearch()
}
const tableResetHandler = () => {
  tableParam.value = {
    instanceName: instanceName.value,
    type: ociOption.value.radio,
    pageNo: 1,
    pageSize: 10,
  }
  tableSearch()
}
watch(
  () => ociOption.value.radio,
  (newVal) => {
    console.log(newVal)
    tableParam.value = {
      instanceName: instanceName.value,
      type: newVal,
      pageNo: 1,
      pageSize: 10,
    }
    tableSearchHandler()
  }
)
// 设置实例密码
const dialogVisible = ref(false)
const password = ref('')
const setPsw = () => {
  console.log('设置实例密码')
  dialogVisible.value = true
}
const setPswHandler = () => {
  const data = {
    instanceName: instanceName.value,
    password: password.value,
  }
  setRepositoryPasswordApi(data).then((res) => {
    if (res.code === 200) {
      dialogVisible.value = false
      ElMessage.success('设置密码成功')
      getDetail()
    }
  })
}
const deleteHandler = (row: any) => {
  deleteRepositoryImageApi({
    instanceName: instanceName.value,
    namespace: instanceOci.value.namespace,
    ociName: instanceOci.value.ociName,
    imageVersions: row.imageVersion,
  }).then((res) => {
    if (res.code === 200) {
      ElMessage.success('删除成功')
      getDetail()
    }
  })
}
const initPage = () => {
  getUserName()
  getDetail()
  getOciDetail()
}
watch(
  () => instanceDetail.value,
  (newVal) => {
    if (newVal.status === 'RUNNING') {
      tableParam.value = {
        instanceName: instanceName.value,
        type: ociOption.value.radio,
      }
      tableGet()
    }
  }
)
initPage()
</script>
<style lang="scss" scoped>
.warehouse {
  display: grid;
  grid-template-columns: 1fr 440px;
  gap: 20px;
  height: 94%;
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
.copy-card-1 {
  padding: 12px;
  background: #ffffff;
  border-radius: 4px;
  width: 276px;
  height: 84px;
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
.preview {
  .title-box {
    .title {
      font-weight: 500;
      font-size: 34px;

      max-width: 600px;
      overflow: hidden;
      text-overflow: ellipsis; //文本溢出显示省略号
      white-space: nowrap; //文本不会换行
    }

    .el-button {
      margin-left: auto;
      width: 158px;
      height: 34px;
      border-radius: 4px 4px 4px 4px;
      border: 1px solid #fba201;
    }
  }
  .info-box {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 30px;
    .info {
      .label {
        font-weight: 400;
        font-size: 16px;
        color: #83889d;
      }
      .value {
        font-weight: 400;
        font-size: 16px;
        margin-left: 30px;
        .command {
          margin-right: 70px;
          width: 90px;

          font-weight: 400;
          font-size: 16px;
          color: #3972fd;

          text-decoration-line: underline;
          &:hover {
            cursor: pointer;
          }
        }
      }
    }
  }

  .gray-card {
    height: 240px;
    background: #f7f8fb;
    border-radius: 8px 8px 8px 8px;
    padding: 26px 24px 30px;
    .detail-box {
      .detail {
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        .label {
          font-weight: 400;
          font-size: 20px;
          color: #666666;
        }
        .value {
          font-weight: bold;
          font-size: 44px;
          .unit {
            font-weight: 400;
            font-size: 20px;
            color: #666666;
          }
        }
      }
    }
  }
}
.oci {
  .title-box {
    .title {
      font-weight: 500;
      font-size: 34px;

      max-width: 600px;
      overflow: hidden;
      text-overflow: ellipsis; //文本溢出显示省略号
      white-space: nowrap; //文本不会换行
    }
    .el-button {
      margin-left: auto;
      width: 158px;
      height: 34px;
      border-radius: 4px 4px 4px 4px;
      border: 1px solid #fba201;
    }
  }
  .grid-3 {
    display: grid;
    grid-template-columns: 1fr 1fr 1fr;
    .item {
      display: flex;
      align-items: center;
      gap: 30px;
      .item-title {
        font-weight: 400;
        font-size: 16px;
        color: #83889d;
      }
      .item-content {
        font-weight: 400;
        font-size: 16px;
        color: #333333;
      }
    }
  }
  .gray-tip {
    margin-left: 25px;
    font-weight: 400;
    font-size: 16px;
    color: #83889d;
    margin-right: 330px;
    white-space: nowrap;
  }
  .search-bar {
    gap: 10px;
    .el-button {
      margin: 0;
    }
  }
}
.handbook {
  .gray-card {
    background: #f7f8fb;
    border-radius: 8px 8px 8px 8px;
    padding: 30px;
  }
  .group {
    .label {
      font-weight: 500;
      font-size: 18px;
      color: #333333;
      margin-bottom: 12px;
    }
    .front-tip {
      font-weight: 400;
      font-size: 16px;
      color: #3972fd;
      margin-bottom: 12px;
    }
    .order {
      padding: 12px 30px;
      display: flex;
      align-content: center;
      background: #ffffff;
      border-radius: 4px 4px 4px 4px;
      border: 1px solid #e5e5e5;
      font-weight: 400;
      font-size: 16px;
      color: #83889d;
      margin-bottom: 12px;
      &:nth-last-child(1) {
        margin-bottom: 30px;
      }
     img  {
        margin-left: auto;
        height: 24px;
        width: 24px;
        &:hover {
          cursor: pointer;
        }
      }
    }
  }
  .blue-tip {
    font-weight: 400;
    font-size: 18px;
    color: #3972fd;
    margin-top: 15px;
  }
}
.alert {
  width: 100%;
  border-radius: 10px;
  padding: 18px;
  padding-left: 40px;
  background: linear-gradient(86deg, #fef0f0 0%, rgba(255, 255, 255, 0) 100%);
  position: relative;
  &::before {
    left: 20px;
  }
}
</style>
