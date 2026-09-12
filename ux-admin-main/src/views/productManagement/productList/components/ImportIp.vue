<template>
  <el-dialog
    v-model="model"
    :title="`批量添加IP`"
    :destroy-on-close="true"
    center
    width="500"
  >
    <el-form class="drawer-multiColumn-form" label-width="100px">
      <el-form-item label="模板下载 :">
        <el-button type="primary" :icon="Download" @click="downloadTemp">
          点击下载
        </el-button>
      </el-form-item>
      <el-form-item label="文件上传 :">
        <el-upload
          action="#"
          class="upload"
          :drag="true"
          :limit="1"
          :multiple="true"
          :show-file-list="true"
          :http-request="uploadExcel"
          :before-upload="beforeExcelUpload"
          :on-exceed="handleExceed"
          :on-success="excelUploadSuccess"
          :on-error="excelUploadError"
          :accept="fileType.join(',')"
        >
          <slot name="empty">
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">
              将文件拖到此处，或<em>点击上传</em>
            </div>
          </slot>
          <template #tip>
            <slot name="tip">
              <div class="el-upload__tip">
                请上传 .xls , .xlsx 标准格式文件，文件最大为
                {{ fileSize }}M
              </div>
            </slot>
          </template>
        </el-upload>
      </el-form-item>
    </el-form>
  </el-dialog>
</template>

<script setup lang="ts" name="ImportIp">
import {
  productIpDownloadTemplateApi,
  productIpImportIpApi,
} from "@/api/productManagement";
import { TUploadMessage, UploadMessage } from "@/api/Upload";
import { useDownload } from "@/hooks/useDownload";
import { useVModel } from "@/utils/useVModel";
import { Download } from "@element-plus/icons-vue";
import { UploadProps, UploadRequestOptions } from "element-plus";

const props = defineProps<{ productId: string; modelValue: boolean }>();
const emit = defineEmits(["success", "close", "update:modelValue"]);
const model = useVModel(props, "modelValue", emit);
const fileType = reactive([
  "application/vnd.ms-excel",
  "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
]);
const fileSize = 10;
const message = computed(() => (row: TUploadMessage) => UploadMessage[row]);

const close = () => emit("close");

// 下载模板
const downloadTemp = () => useDownload(productIpDownloadTemplateApi, `IP模板`);
const uploadExcel = async (param: UploadRequestOptions) => {
  let excelFormData = new FormData();
  excelFormData.append("file", param.file);
  await productIpImportIpApi(props.productId, excelFormData);
  close();
  emit("success");
};
const beforeExcelUpload: UploadProps["beforeUpload"] = ({ size, type }) => {
  const imgSize = size / 1024 / 1024 < fileSize;
  const imgType = fileType.includes(type as File.ExcelMimeType);
  const msgTye = imgType ? "FileSize" : "Type";
  if (!imgType || !imgSize) ElNotification.error(message.value(msgTye));
  return imgType && imgSize;
};
const excelUploadError = () => ElNotification.error(message.value("Error"));
const excelUploadSuccess = () =>
  ElNotification.success(message.value("Success"));
const handleExceed = () => ElNotification.error(message.value("Exceed"));
</script>
<style lang="scss" scoped></style>
