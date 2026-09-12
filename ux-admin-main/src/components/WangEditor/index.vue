<template>
  <div :class="['editor-box', disabled ? 'editor-disabled' : '']">
    <Toolbar
      class="editor-toolbar"
      :editor="editorRef"
      :default-config="toolbarConfig"
      :mode="mode"
      v-if="!hideToolBar"
    />

    <Editor
      class="editor-content'"
      :style="{ height }"
      :mode="mode"
      v-model="model"
      :default-config="editorConfig"
      @on-created="handleCreated"
    />
  </div>
</template>

<script setup lang="ts" name="WangEditor">
import { UploadFn } from "@/api/Upload";
import { useVModel } from "@/utils/useVModel";
import { IEditorConfig, IToolbarConfig } from "@wangeditor/editor";
import { Editor, Toolbar } from "@wangeditor/editor-for-vue";
import "@wangeditor/editor/dist/css/style.css";

interface RichEditorProps {
  modelValue: string | undefined; // 富文本值 ==> 必传
  toolbarConfig?: Partial<IToolbarConfig>; // 工具栏配置 ==> 非必传（默认为空）
  editorConfig?: Partial<IEditorConfig>; // 编辑器配置 ==> 非必传（默认为空）
  height?: string; // 富文本高度 ==> 非必传（默认为 500px）
  mode?: "default" | "simple"; // 富文本模式 ==> 非必传（默认为 default）
  hideToolBar?: boolean; // 是否隐藏工具栏 ==> 非必传（默认为false）
  disabled?: boolean; // 是否禁用编辑器 ==> 非必传（默认为false）
}

const props = withDefaults(defineProps<RichEditorProps>(), {
  toolbarConfig: () => {
    return {
      excludeKeys: [],
    };
  },
  editorConfig: () => {
    return {
      placeholder: "请输入内容...",
      MENU_CONF: {},
    };
  },
  height: "500px",
  mode: "default",
  hideToolBar: false,
  disabled: false,
});

const emit = defineEmits(["update:modelValue"]);
const model = useVModel(props, "modelValue", emit);

// 富文本 DOM 元素
const editorRef = shallowRef();

// 禁用
if (props.disabled) nextTick(() => editorRef.value.disable());

const handleCreated = (editor: any) => {
  editorRef.value = editor;
  editorRef.value.uploadImgMaxSize = 10 * 1024 * 1024;
};

type InsertFnType = (url: string, name?: string) => void;
// 配置图片上传
props.editorConfig.MENU_CONF!["uploadImage"] = {
  customUpload(file: File, insertFn: InsertFnType) {
    handleFileUpload(file, insertFn);
  },
};
// 配置视频上传
props.editorConfig.MENU_CONF!["uploadVideo"] = {
  customUpload(file: File, insertFn: InsertFnType) {
    handleFileUpload(file, insertFn);
  },
};

// 辅助上传函数
const handleFileUpload = async (file: File, insertFn: InsertFnType) => {
  if (!file) return;
  let formData = new FormData();
  formData.append("file", file);
  try {
    const { data } = await UploadFn(formData);
    insertFn(data.url);
  } catch (error) {}
};
</script>
<style scoped lang="scss">
@use "./index.scss";
</style>
