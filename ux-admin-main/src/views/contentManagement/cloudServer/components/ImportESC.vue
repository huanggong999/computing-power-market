<template>
  <el-dialog
    v-model="visible"
    :title="`导入${productTypeTitle[props.type]}服务器`"
    width="40%"
    center
    destroy-on-close
  >
    <div class="tip ml25 mt40">
      <el-form class="drawer-multiColumn-form mt10" label-width="100px">
        <el-form-item label="模板下载 :">
          <div v-if="props.type === 1">
            <el-text class="mr10"> (通过火山价格计算器下载获取文件) </el-text>
            <el-link
              href="https://www.volcengine.com/pricing?product=ECS&tab=1"
              target="_blank"
              type="primary"
            >
              去下载
            </el-link>
          </div>
          <el-button
            v-else
            type="primary"
            :icon="Download"
            @click="downloadTemp"
          >
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
            :before-upload="beforeUpload"
            :accept="fileType.join(',')"
            :auto-upload="false"
            ref="uploadRef"
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
                  请上传 .xls , .xlsx 标准格式文件，
                </div>
              </slot>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item label="模板下载 :">
          <el-radio-group v-model="ecsType">
            <el-radio-button
              v-for="item in serverTypeEnum"
              :key="item.label"
              :label="item.description"
              :value="item.label"
            />
          </el-radio-group>
        </el-form-item>
      </el-form>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="close">取消</el-button>
        <el-button type="primary" @click="confirm">确定</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts" name="ImportESC">
import {
  downloadTemplateApi,
  importPersonalEcsApi,
  syncEcsApi,
  TEcsType,
} from "@/api/cloudServer";
import { TUploadMessage, UploadMessage } from "@/api/Upload";
import { useDownload } from "@/hooks/useDownload";
import { serverTypeEnum } from "@/utils/radioEnum";
import { useVModel } from "@/utils/useVModel";
import { Download } from "@element-plus/icons-vue";
import {
  UploadInstance,
  UploadProps,
  UploadRequestOptions,
} from "element-plus";

interface ImportESC {
  modelValue: boolean;
  type: 1 | 2;
}

const props = defineProps<ImportESC>();
const emit = defineEmits(["update:modelValue", "submit"]);
const visible = useVModel(props, "modelValue", emit);
watch(
  () => visible.value,
  (val) => {
    if (!val) ecsType.value = "GENERAL_COMPUTE";
  }
);

const close = () => emit("update:modelValue", false);

const ecsType = ref<TEcsType>("GENERAL_COMPUTE");
const fileType = [
  "application/vnd.ms-exce",
  "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
];
const message = computed(() => (row: TUploadMessage) => UploadMessage[row]);
const uploadRef = ref<UploadInstance>();
// ? 上传文件类型
const beforeUpload: UploadProps["beforeUpload"] = ({ type }) => {
  if (!fileType.includes(type)) {
    ElNotification.error(message.value("Type"));
    return false;
  }
};
// & 下载文件
const downloadTemp = () =>
  useDownload(downloadTemplateApi, "自建服务器规格模板");
// ? 上传文件类型对应的处理函数
const productTypeTitle = ["", "火山引擎", "自建"];

const productTypeFn = [
  () => {},
  (data: FormData) => syncEcsApi(ecsType.value, data),
  (data: FormData) => importPersonalEcsApi(ecsType.value, data),
];
const confirm = () => uploadRef.value!.submit();
const uploadExcel = async (param: UploadRequestOptions) => {
  let excelFormData = new FormData();
  excelFormData.append("file", param.file);
  await productTypeFn[props.type](excelFormData);
  ElMessage.success("导入成功");
  close();
  emit("submit");
};
</script>
<style lang="scss" scoped></style>
