<template>
  <div id="markdownEditor" ref="markdownEditor" />
</template>

<script setup lang="ts" name="MarkdownEditor">
interface IMarkdownEditorProps {
  modelValue: string | undefined; // 富文本值 ==> 必传
  disabled?: boolean;
}
const props = withDefaults(defineProps<IMarkdownEditorProps>(), {
  disabled: false,
});
import Vditor from "vditor";
import "vditor/dist/index.css";
import { UploadFn } from "@/api/Upload";

const emit = defineEmits(["update:modelValue"]);
const markdownEditor = ref();

const contentEditor = ref<Vditor | null>(null);
const initVditor = () => {
  contentEditor.value = new Vditor("markdownEditor", {
    height: 460,
    width: "100%",
    mode: "ir", // 即时渲染模式
    // mode: "sv", // 分屏预览
    // mode: "wysiwyg", // 所见即所得模式
    icon: "material",
    value: props.modelValue,
    cdn: `https://ld246.com/js/lib/vditor`,
    preview: { mode: "both", actions: [] },
    placeholder: "请输入内容...",
    toolbarConfig: { pin: true },
    toolbar: [
      "emoji",
      "headings",
      "bold",
      "italic",
      "strike",
      "link",
      "list",
      "ordered-list",
      "check",
      "outdent",
      "indent",
      "quote",
      "line",
      "code",
      "inline-code",
      "insert-before",
      "insert-after",
      {
        //自定义上传
        hotkey: "",
        name: "upload",
        tipPosition: "s",
        tip: "上传图片",
        className: "right",
      },
      "table",
      "fullscreen",
      "edit-mode",
      "undo",
      "redo",
      {
        name: "more",
        toolbar: [
          "code-theme",
          "content-theme",
          "export",
          "outline",
          "preview",
        ],
      },
    ],
    upload: {
      accept: "image/*,video/*,audio/*",
      handler: (files: File[]) => {
        const formData = new FormData();
        files.forEach((file) => formData.append("file", file));
        UploadFn(formData)
          .then((res) => {
            if (res.data.url) {
              const file = files[0];
              const insertContent = getInsertContent(file.type, res.data.url);
              contentEditor.value?.insertValue(insertContent);
            }
          })
          .catch((error) => {
            console.error("上传失败:", error);
          });
        return "";
      },
    },
    cache: { enable: false },
    outline: { enable: true, position: "left" },
    blur: (val) => emit("update:modelValue", val),
  });
};

onMounted(() => initVditor());
onBeforeUnmount(() => contentEditor.value?.destroy());

// 新增函数：根据文件类型生成插入内容
const getInsertContent = (fileType: string, url: string): string => {
  switch (true) {
    case fileType.startsWith("video/"):
      return `<video src="${url}" width=500 height=300 controls />`;
    case fileType.startsWith("audio/"):
      return `<audio src="${url}" controls></audio>`;
    default:
      return `![](${url})`;
  }
};
</script>
<style lang="scss" scoped></style>
