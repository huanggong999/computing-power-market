<template>
  <div class="upload-page">
    <Breadcrumb :router-list="routerList"></Breadcrumb>
    <div class="upload-content">
      <div class="card">
        <!-- <div class="item mb30">
          <div class="label">上传到</div>
          <div class="value">
            <el-radio-group v-model="form.uploadTo">
              <el-radio-button label="当前目录" value="now"></el-radio-button>
              <el-radio-button
                label="指定目录"
                value="target"
              ></el-radio-button>
            </el-radio-group>
            <el-select
              v-model="form.folder"
              :disabled="form.uploadTo === 'now'"
            >
              <el-option
                v-for="item in folderOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </div>
        </div> -->
        <div class="item mb65">
          <div class="label">待上传</div>
          <div class="value">
            <div class="gray-card">
              <div class="btns flx-align-center">
                <!-- <el-button type="primary" plain>选择文件</el-button> -->
                <!-- <el-button type="primary" plain>选择文件夹</el-button> -->
                <!-- <el-button>清空列表</el-button> -->
              </div>
              <el-upload
                class="upload-demo upload-card flx-center"
                drag
                action="#"
                multiple
                :on-change="handleFileChange"
                :show-file-list="false"
                :auto-upload="false"
              >
                <img src="../../../assets/icon/upload-icon.png" />
                <div class="title">点击或将文件拖拽到这里上传</div>
                <div class="tips">
                  支持上传多个文件和文件夹，重名的文件将被覆盖，最大文件限制在5GiB内。如需上传更大文件，请点击下载TOS
                  Browser或tosutil
                </div>
              </el-upload>
            </div>
            <div class="file-list">
              <div
                class="file-item"
                v-for="(item, index) in fileList"
                :key="item.id"
              >
                <div class="info">
                  <div class="name">{{ item.name }}</div>
                </div>
                <img
                  class="delete-icon"
                  src="../../../assets/icon/delete-icon.png"
                  @click="handleRemoveFile(index)"
                />
              </div>
            </div>
          </div>
        </div>
        <div class="footer">
          <el-button>取消</el-button>
          <el-button type="primary" @click="handleConfirmUpload"
            >上传</el-button
          >
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts" name="Storage">
import { uploadFileAPI } from '@/api/storage'
import { toPage } from '@/utils'
import { ElMessage } from 'element-plus'
import { useRoute } from 'vue-router'

const route = useRoute()
const name = ref(route.query.name)
const bucketId = ref(route.query.bucketId)
const folderId = ref(route.query.fileId)
const routerList = computed(() => [
  { name: '对象存储', path: '/objectStorage' },
  // {
  //   name: '存储桶详情',
  //   path: `/storageDetail?name=${name.value}&id=${bucketId.value}`,
  // },
  { name: '上传文件', path: '' },
])

const fileList = ref<any>([])
// 目标上传文件变更
const handleFileChange = (file: any) => {
  fileList.value.push(file.raw)
}
// 删除上传文件
const handleRemoveFile = (index: number) => {
  fileList.value.splice(index, 1) // 从文件列表中移除指定文件
  ElMessage.success('文件已删除')
}
// 确认上传
const handleConfirmUpload = async () => {
  if (fileList.value.length === 0) {
    ElMessage.warning('请先选择文件')
    return
  }
  let params: any = {
    bucketId: bucketId.value,
  }
  if (folderId.value) params.fileId = folderId.value
  // 逐个上传文件
  for (const file of fileList.value) {
    const formData = new FormData()
    formData.append('file', file)
    await uploadFileAPI(formData, params) // 调用上传接口
  }
  toPage(`/storageDetail?name=${name.value}&id=${bucketId.value}`)
}
</script>
<style lang="scss" scoped>
@import './index.scss';
</style>
