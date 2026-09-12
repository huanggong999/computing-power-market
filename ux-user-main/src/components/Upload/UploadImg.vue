<template>
  <div class="upload-box">
    <el-upload action="#" :class="['upload', disabled ? 'disabled' : '', drag ? 'no-border' : '']" :multiple="false"
      :disabled="disabled" :show-file-list="false" :http-request="handleHttpUpload" :before-upload="beforeUpload"
      :on-success="uploadSuccess" :on-error="uploadError" :drag="drag" :on-remove="deleteImg"
      :accept="fileType.join(',')">
      <template v-if="props.imageUrl">
        <el-button id="uploadRef" style="display: none"></el-button>
        <img :src="props.imageUrl" class="upload-image" />
        <div class="upload-handle" @click.stop>
          <div class="handle-icon" @click="editImg" v-if="!props.disabled">
            <el-icon>
              <Edit />
            </el-icon>
            <span>编辑</span>
          </div>
          <div class="handle-icon" @click="imgViewVisible = true">
            <el-icon>
              <ZoomIn />
            </el-icon>
            <span>查看</span>
          </div>
          <div class="handle-icon" @click="deleteImg" v-if="!props.disabled">
            <el-icon>
              <Delete />
            </el-icon>
            <span>删除</span>
          </div>
        </div>
      </template>
      <template v-else>
        <div class="upload-empty">
          <slot name="empty">
            <el-icon>
              <Plus />
            </el-icon>
          </slot>
        </div>
      </template>
    </el-upload>
    <div class="el-upload__tip">
      <slot name="tip"></slot>
    </div>
    <el-image-viewer v-if="imgViewVisible" @close="imgViewVisible = false" :url-list="[imageUrl]" />
  </div>
</template>

<script setup lang="ts" name="UploadImg">
import { TUploadMessage, UploadMessage, UploadFn } from '@/api/Upload'

import { ElMessage, UploadProps, UploadRequestOptions } from 'element-plus'
import { Plus, Edit, ZoomIn, Delete } from '@element-plus/icons-vue'

interface UploadFileProps {
  imageUrl: string // 图片地址 ==> 必传
  api?: (params: any) => Promise<any> // 上传图片的 api 方法，一般项目上传都是同一个 api 方法，在组件里直接引入即可 ==> 非必传
  drag?: boolean // 是否支持拖拽上传 ==> 非必传（默认为 true）
  disabled?: boolean // 是否禁用上传组件 ==> 非必传（默认为 false）
  fileSize?: number // 图片大小限制 ==> 非必传（默认为 5M）
  fileType?: File.ImageMimeType[] // 图片类型限制 ==> 非必传（默认为 ["image/jpeg", "image/png", "image/gif"]）
  height?: string // 组件高度 ==> 非必传（默认为 150px）
  width?: string // 组件宽度 ==> 非必传（默认为 150px）
  borderRadius?: string // 组件边框圆角 ==> 非必传（默认为 8px）
}
/**
 * @description 图片上传
 * @param options upload 所有配置项
 * */
interface UploadEmits {
  (e: 'update:imageUrl', value: string): void
  (e: 'update:info', value: any): void
}
const emit = defineEmits<UploadEmits>()
const props = withDefaults(defineProps<UploadFileProps>(), {
  imageUrl: '',
  drag: true,
  disabled: false,
  fileSize: 5,
  fileType: () => ['image/jpeg', 'image/png', 'image/gif'],
  height: '150px',
  width: '150px',
  borderRadius: '8px',
})

// 查看图片
const imgViewVisible = ref(false)

const handleHttpUpload = async (options: UploadRequestOptions) => {
  let formData = new FormData()
  formData.append('file', options.file)
  try {
    const api = props.api ?? UploadFn
    const { data } = await api(formData)
    if (props.api) {
      if (!data.ocrzs) {
        ElMessage.warning('请上传清晰的照片')
        return
      }
      emit('update:info', data)
      return
    }
    emit('update:imageUrl', data.url)
  } catch (error) {
    options.onError(error as any)
  }
}

const message = computed(() => (row: TUploadMessage) => UploadMessage[row])

/**
 * @description 文件上传之前判断
 * @param rawFile 选择的文件
 * */
const beforeUpload: UploadProps['beforeUpload'] = ({ size, type }) => {
  const imgSize = size / 1024 / 1024 < props.fileSize
  const imgType = props.fileType.includes(type as File.ImageMimeType)
  const msgTye = imgType ? 'FileSize' : 'Type'
  if (!imgType || !imgSize) ElNotification.error(message.value(msgTye))
  return imgType && imgSize
}
const uploadSuccess = () => ElNotification.success(message.value('Success'))
const uploadError = () => ElNotification.error(message.value('Error'))
const deleteImg = () => emit('update:imageUrl', '')
const editImg = () => {
  nextTick(() => {
    const dom: HTMLButtonElement | null = document.querySelector(`#uploadRef`)
    dom && dom.click()
  })
}
</script>

<style scoped lang="scss">
.is-error {
  .upload {

    :deep(.el-upload),
    :deep(.el-upload-dragger) {
      border: 1px dashed var(--el-color-danger) !important;

      &:hover {
        border-color: var(--el-color-primary) !important;
      }
    }
  }
}

:deep(.disabled) {

  .el-upload,
  .el-upload-dragger {
    cursor: not-allowed !important;
    background: var(--el-disabled-bg-color);
    border: 1px dashed var(--el-border-color-darker) !important;

    &:hover {
      border: 1px dashed var(--el-border-color-darker) !important;
    }
  }
}

.upload-box {
  :deep(.upload) {
    .el-upload {
      position: relative;
      display: flex;
      align-items: center;
      justify-content: center;
      width: v-bind(width);
      height: v-bind(height);
      overflow: hidden;
      border: 1px dashed var(--el-border-color-darker);
      border-radius: v-bind(borderRadius);
      transition: var(--el-transition-duration-fast);

      &:hover {
        border-color: var(--el-color-primary);

        .upload-handle {
          opacity: 1;
        }
      }

      .el-upload-dragger {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 100%;
        height: 100%;
        padding: 0;
        overflow: hidden;
        background-color: transparent;
        border: 1px dashed var(--el-border-color-darker);
        border-radius: v-bind(borderRadius);

        &:hover {
          border: 1px dashed var(--el-color-primary);
        }
      }

      .el-upload-dragger.is-dragover {
        background-color: var(--el-color-primary-light-9);
        border: 2px dashed var(--el-color-primary) !important;
      }

      .upload-image {
        width: 100%;
        height: 100%;
        object-fit: contain;
      }

      .upload-empty {
        position: relative;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        font-size: 12px;
        line-height: 30px;
        color: var(--el-color-info);

        .el-icon {
          font-size: 28px;
          color: var(--el-text-color-secondary);
        }
      }

      .upload-handle {
        position: absolute;
        top: 0;
        right: 0;
        box-sizing: border-box;
        display: flex;
        align-items: center;
        justify-content: center;
        width: 100%;
        height: 100%;
        cursor: pointer;
        background: rgb(0 0 0 / 60%);
        opacity: 0;
        transition: var(--el-transition-duration-fast);

        .handle-icon {
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          padding: 0 6%;
          color: aliceblue;

          .el-icon {
            margin-bottom: 40%;
            font-size: 130%;
            line-height: 130%;
          }

          span {
            font-size: 85%;
            line-height: 85%;
          }
        }
      }
    }
  }

  .el-upload__tip {
    line-height: 18px;
    text-align: center;
  }
}
</style>
