import { ElMessage } from "element-plus";

import i18n from "@/languages";
const { t } = i18n.global;

export default {
  mounted(el: any, binding: any) {
    el.$value = binding.value;
    el.handler = () => {
      const textarea = document.createElement("textarea");
      textarea.readOnly = true;
      textarea.style.position = "absolute";
      textarea.style.left = "-9999px";
      textarea.value = el.$value;
      document.body.appendChild(textarea);
      textarea.select();
      textarea.setSelectionRange(0, textarea.value.length);
      const result = document.execCommand("Copy");
      if (result) {
        ElMessage.success(t("home.copySuccess"));
      }
      document.body.removeChild(textarea);
    };
    el.addEventListener("click", el.handler);
  },
  updated(el: any, binding: any) {
    el.$value = binding.value;
  },
  unmounted(el: any) {
    el.removeEventListener("click", el.handler);
  },
};
