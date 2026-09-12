<template>
  <div class="upload-box">
    <el-upload
      action="#"
      class="upload"
      :drag="props.drag"
      :limit="1"
      :multiple="false"
      :show-file-list="true"
      :http-request="uploadExcel"
      :before-upload="beforeUpload"
      :accept="fileType.join(',')"
      :on-success="uploadSuccess"
      :on-error="uploadError"
      :on-remove="deleteFile"
      :on-exceed="handleExceed"
      ref="uploadRef"
      v-if="!props.fileUrl"
    >
      <slot name="empty">
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
      </slot>
      <template #tip>
        <slot name="tip" />
      </template>
    </el-upload>

    <div v-else class="file-box flx-justify-between">
      <el-tooltip class="box-item" effect="dark" :content="fileName">
        <div class="file-name sle">{{ fileName }}</div>
      </el-tooltip>
      <el-button class="btn" type="primary" @click="deleteFile" link>
        移除
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts" name="UploadFile">
import { TUploadMessage, UploadFn, UploadMessage } from "@/api/Upload";
import { UploadProps, UploadRequestOptions } from "element-plus";

interface UploadFileProps {
  fileUrl: string | undefined; // 文件地址 ==> 必传
  drag?: boolean; // 是否支持拖拽上传 ==> 非必传（默认为 true）
  fileSize?: number; // 文件大小限制 ==> 非必传（默认为 5M）
  fileType?: string[]; // 文件类型限制 ==> 非必传（默认为 ["image/jpeg", "image/png", "image/gif"]）
}

const props = withDefaults(defineProps<UploadFileProps>(), {
  drag: true,
  fileSize: 10,
  fileType: () => [
    "application/vnd.ms-excel",
    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
  ],
});
/**
 * @description 文件上传之前判断
 * @param rawFile 选择的文件
 * */
const beforeUpload: UploadProps["beforeUpload"] = ({ size, type }) => {
  const imgSize = size / 1024 / 1024 < props.fileSize;
  const imgType = props.fileType.includes(type as File.ExcelMimeType);
  const msgTye = imgType ? "FileSize" : "Type";
  if (!imgType || !imgSize) ElNotification.error(message.value(msgTye));
  return imgType && imgSize;
};

const fileName = ref("");
const emit = defineEmits(["update:fileUrl"]);
const uploadExcel = async (param: UploadRequestOptions) => {
  const { file, onError } = param;
  fileName.value = file.name;
  let formData = new FormData();
  formData.append("file", file);
  try {
    const { data } = await UploadFn(formData);
    emit("update:fileUrl", data.url);
  } catch (error) {
    onError(error as any);
  }
};

const message = computed(() => (row: TUploadMessage) => UploadMessage[row]);
const uploadSuccess = () => ElNotification.success(message.value("Success"));
const uploadError = () => ElNotification.error(message.value("Error"));
const deleteFile = () => emit("update:fileUrl", "");
const handleExceed = () => ElNotification.warning(message.value("Exceed"));
</script>
<style lang="scss" scoped>
.file-box {
  border: 1px solid #ccc;
  .file-name {
    border-right: 1px solid #ccc;
    padding: 10px;
  }
  .btn {
    width: 100px;
    flex-shrink: 1;
    padding: 10px;
  }
}
</style>
