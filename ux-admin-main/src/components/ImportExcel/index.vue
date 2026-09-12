<template>
  <el-dialog
    v-model="dialogVisible"
    :title="`批量添加${props.title}`"
    :destroy-on-close="true"
    center
    width="500"
  >
    <el-form class="drawer-multiColumn-form" label-width="100px">
      <el-form-item label="模板下载 :">
        <el-button type="primary" :icon="Download" @click="downloadTemp"
          >点击下载</el-button
        >
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
          :accept="props.fileType!.join(',')"
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
                {{ props.fileSize }}M
              </div>
            </slot>
          </template>
        </el-upload>
      </el-form-item>
    </el-form>
  </el-dialog>
</template>

<script setup lang="ts" name="ImportExcel">
import { TUploadMessage, UploadMessage } from "@/api/Upload";
import { useDownload } from "@/hooks/useDownload";
import { Download } from "@element-plus/icons-vue";
import { UploadProps, UploadRequestOptions } from "element-plus";

interface ExcelParameterProps {
  title?: string; // 标题
  fileSize?: number; // 上传文件的大小
  fileType?: File.ExcelMimeType[]; // 上传文件的类型
  tempApi?: (params: any) => Promise<any>; // 下载模板的Api
  importApi?: (params: any) => Promise<any>; // 批量导入的Api
  getTableList?: () => void; // 获取表格数据的Api
  importLink?: string; // 下载地址
}

// dialog状态
const dialogVisible = ref(false);

const props = withDefaults(defineProps<ExcelParameterProps>(), {
  title: "",
  fileSize: 5,
  fileType: () => [
    "application/vnd.ms-excel",
    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
  ],
});
// 下载模板
const downloadTemp = async () => {
  ElMessage.info("正在下载...");
  if (props.tempApi) return useDownload(props.tempApi, `${props.title}模板`);
  const down = document.createElement("a");
  down.href = props.importLink!;
  down.click();
  down.remove();
};
// 文件上传
const uploadExcel = async (param: UploadRequestOptions) => {
  let excelFormData = new FormData();
  excelFormData.append("file", param.file);

  await props.importApi!(excelFormData);
  props.getTableList && props.getTableList();
  dialogVisible.value = false;
};

const message = computed(() => (row: TUploadMessage) => UploadMessage[row]);
/**
 * @description 文件上传之前判断
 * @param file 上传的文件
 * */
const beforeExcelUpload: UploadProps["beforeUpload"] = ({ size, type }) => {
  const imgSize = size / 1024 / 1024 < props.fileSize;
  const imgType = props.fileType.includes(type as File.ExcelMimeType);
  const msgTye = imgType ? "FileSize" : "Type";
  if (!imgType || !imgSize) ElNotification.error(message.value(msgTye));
  return imgType && imgSize;
};
const excelUploadError = () => ElNotification.error(message.value("Error"));
const excelUploadSuccess = () =>
  ElNotification.success(message.value("Success"));
const handleExceed = () => ElNotification.error(message.value("Exceed"));

defineExpose({ dialogVisible });
</script>

<style lang="scss" scoped></style>
