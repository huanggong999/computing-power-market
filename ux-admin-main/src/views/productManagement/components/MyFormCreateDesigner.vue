<template>
  <FcDesigner ref="designer" :config="config" class="my-form-create-designer" />
  <FcComputed
    ref="hiddenFormRef"
    :options="selectOptions"
    :Info="hiddenInfo"
    @updateInfo="updateInfo"
  />
</template>

<script setup lang="ts" name="MyFormCreateDesigner">
import FcComputed from "./FcComputed.vue";

const props = defineProps<{ json: string }>();
const designer = ref();

const hiddenFormRef = ref();

const selectOptions = computed(() =>
  JSON.parse(designer.value?.getJson() ?? "[]")
);

const hiddenInfo = ref<TKeyValue>({});
const config = ref({
  hiddenMenu: ["layout", "subform", "aide"],
  showDevice: false, //是否显示多端适配选项
  showLanguage: false, //是否显示多语言配置
  showJsonPreview: false, //是否显示json预览按钮
  showEventForm: false, //是否显示组件的事件配置表单
  showStyleForm: false, //是否显示组件的样式配置表单
  showControl: false, //是否显示组件联动
  showFormConfig: false, //是否显示表单配置
  showValidateForm: true, //是否显示组件的验证配置表单
  showInputData: false, //是否显示输入数据
  showCustomProps: false, //是否显示自定义属性

  switchType: false,
  //

  hiddenItem: [
    "radio",
    "password",
    "checkbox",
    "switch",
    "rate",
    "timePicker",
    "timeRange",
    "slider",
    "datePicker",
    "dateRange",
    "colorPicker",
    "cascader",
    "upload",
    "elTransfer",
    "tree",
    "elTreeSelect",
    "fcEditor",
    "inputNumber",
  ],
  hiddenItemConfig: {
    default: ["disabled", "field"],
    select: [
      "disabled",
      "remote",
      "defaultFirstOption",
      "reserveKeyword",
      "filterable",
      "allowCreate",
      "remoteMethod",
    ],
  },
  componentRule: {
    default: {
      prepend: true,
      rule() {
        return [
          {
            type: "button",
            field: "mark",
            title: "隐藏条件",
            props: { size: "small", innerText: "设置隐藏条件" },
            inject: true,
            on: {
              click: (inject: any) => {
                nextTick(() => (hiddenInfo.value = inject.api.activeRule));
                hiddenFormRef.value.open();
              },
            },
          },
        ];
      },
    },
  },
});

const updateInfo = (info: TKeyValue) => {
  designer.value.setRule(
    modifyByField(
      JSON.parse(designer.value?.getJson()),
      hiddenInfo.value.field,
      info
    )
  );
  hiddenFormRef.value.close();
};

const modifyByField = (
  originArray: TKeyValue[],
  targetField: string,
  hiddenChanges: TKeyValue
) =>
  originArray.map((item) => {
    // 匹配目标字段
    if (item.field === targetField) {
      // 创建新对象，保留原有属性，仅更新hidden
      return {
        ...item,
        computed: { hidden: hiddenChanges },
      };
    }
    // 非目标项保持原样
    return item;
  });

//
watch(designer, (newVal) => {
  if (!!newVal && !!props.json) {
    designer.value.setRule(JSON.parse(props.json));
  }
});

defineExpose({ designer });
</script>
<style lang="scss" scoped>
.my-form-create-designer {
  height: 600px;
  :deep(.el-container.is-vertical) {
    width: 420px !important;
  }
}
</style>
