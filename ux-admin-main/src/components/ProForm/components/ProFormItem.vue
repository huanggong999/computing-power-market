<template>
  <!-- input -->
  <el-input
    v-if="props.column.el === 'input'"
    :disabled="props.disabled"
    placeholder="请输入"
    v-model.trim="model[props.column.prop]"
    :maxlength="props.maxlength"
    :show-word-limit="true"
  />
  <!-- phone -->
  <el-input
    v-if="props.column.el === 'phone'"
    :disabled="props.disabled"
    placeholder="请输入手机号"
    :maxlength="11"
    v-model.trim="model[props.column.prop]"
  />
  <!-- number-->
  <el-input-number
    v-if="props.column.el === 'number'"
    :disabled="props.disabled"
    placeholder="请输入"
    :precision="0"
    v-model="model[props.column.prop]"
    :min="0"
    :max="props.maxlength"
  />
  <!-- price -->
  <el-input-number
    v-if="props.column.el === 'price'"
    :disabled="props.disabled"
    placeholder="请输入"
    :controls="false"
    :precision="2"
    v-model="model[props.column.prop]"
    :min="0"
  />
  <!-- password -->
  <el-input
    v-if="props.column.el === 'password'"
    :disabled="props.disabled"
    placeholder="请输入密码"
    type="password"
    show-password
    v-model.trim="model[props.column.prop]"
  />

  <!-- textarea -->
  <el-input
    v-if="props.column.el === 'textarea'"
    :disabled="props.disabled"
    type="textarea"
    v-model="model[props.column.prop]"
    :show-word-limit="true"
    placeholder="请输入"
    :maxlength="props.maxlength"
    autosize
  />
  <!-- 单选 -->
  <el-radio-group
    :disabled="props.disabled"
    v-if="props.column.el === 'radio'"
    v-model="model[props.column.prop]"
  >
    <el-radio
      v-for="(item, index) in props.column.radioList"
      :key="index"
      :label="item.label"
      :value="item.label"
    >
      {{ item.description }}
    </el-radio>
  </el-radio-group>
  <!-- 多选 -->
  <el-checkbox-group
    :disabled="props.disabled"
    v-if="props.column.el === 'checkbox'"
    v-model="model[props.column.prop]"
  >
    <el-checkbox
      v-for="(item, index) in props.column.radioList"
      :key="index"
      :label="item.description"
      :value="item.label"
    />
  </el-checkbox-group>
  <!-- 下拉选择 -->
  <el-select
    v-if="props.column.el === 'select'"
    :disabled="props.disabled"
    :multiple="props.column.selectMultiple"
    v-model="model[props.column.prop]"
  >
    <el-option
      v-for="(item, index) in props.column.selectList"
      :key="index"
      :label="item[props.column.selectLabel!]"
      :value="item[props.column.selectValue!]"
    ></el-option>
  </el-select>
  <!-- 分页数据 -->
  <el-select
    v-if="props.column.el === 'selectPage'"
    :disabled="props.disabled"
    :multiple="props.column.selectMultiple"
    v-model="model[props.column.prop]"
  >
    <el-option
      v-for="(item, index) in selectPageList"
      :key="index"
      :label="item[props.column.selectLabel!]"
      :value="item[props.column.selectValue!]"
    ></el-option>
    <template #footer>
      <Pagination
        :pageData="pageData"
        :PageChange="getList"
        layout="prev, pager, next"
        :background="false"
      />
    </template>
  </el-select>
  <!-- 单张图片 -->
  <UploadImg
    :disabled="props.disabled"
    v-if="props.column.el === 'img'"
    v-model:imageUrl="model[props.column.prop]"
  />
  <!-- 图集 -->
  <UploadImgs
    :disabled="props.disabled"
    :limit="props.column.limit"
    v-if="props.column.el === 'imgs'"
    v-model:imgList="model[props.column.prop]"
  >
    <template #tip>
      <div class="upload-tip">
        支持批量上传，最多上传 {{ props.column.limit }} 张图片
      </div>
    </template>
  </UploadImgs>
  <!-- 日期 -->
  <el-date-picker
    :disabled="props.disabled"
    v-if="props.column.el === 'date-picker'"
    v-model="timeList"
    :type="props.column.dateType"
    rangeSeparator="至"
    start-placeholder="开始时间"
    end-placeholder="结束时间"
    :value-format="props.column.valueFormat"
    @change="changeTime"
    :time-format="props.column.timeFormat"
    @blur="blurTime"
  />

  <!-- 富文本 -->
  <WangEditor
    v-if="props.column.el === 'wangEditor'"
    v-model="model[props.column.prop]"
    :disabled="props.disabled"
  />

  <!-- MD文档 -->
  <MarkdownEditor
    v-if="props.column.el === 'markdownEditor'"
    v-model="model[props.column.prop]"
    :disabled="props.disabled"
  />
</template>

<script setup lang="ts" name="ProFormItem">
import { useVModel } from "@/utils/useVModel";
interface ProFormItem {
  column: IFormColumnsProps;
  modelValue: TKeyValue;
  disabled: boolean;
  maxlength?: number;
}
const props = defineProps<ProFormItem>();

const emit = defineEmits(["update:modelValue"]);
const model = useVModel(props, "modelValue", emit);

// 日期类型是否为数组
const type = computed(() => !props.column.dateEnum?.length);

// 日期
const timeList = ref();
const flag = ref(false);
watchEffect(() => {
  if (flag) if (type.value) timeList.value = model.value[props.column.prop];
  if (!type.value && !flag.value)
    timeList.value = [
      model.value[props.column.dateEnum![0]],
      model.value[props.column.dateEnum![1]],
    ];
});
// 修改日期
const changeTime = (val: any): void => {
  if (type.value) return (model.value[props.column.prop] = val);
  flag.value = true;
  model.value[props.column.dateEnum![0]] = val[0];
};
const blurTime = () => {
  if (type.value) return;
  model.value[props.column.dateEnum![1]] = timeList.value[1];
};

//#region 下拉分页

const pageData = ref({ pageSize: 10, pageNo: 1, total: 1 });
const pageParams = computed(() => ({
  pageSize: pageData.value.pageSize,
  pageNo: pageData.value.pageNo,
}));

const selectPageList = ref([]);
const getList = async () => {
  const { data } = await props.column.pageFn!(
    { ...pageParams.value, ...props.column.selectParams },
    true
  );
  const { dataTotal, list, pageNo, pageSize } = data;
  selectPageList.value = list;
  updatePageData({ pageSize, total: +dataTotal, pageNo });
};
const updatePageData = (resPageTable: PageData) =>
  Object.assign(pageData.value, resPageTable);

onMounted(() => {
  if (props.column.el === "selectPage") getList();
});

//#endregion
</script>
<style lang="scss" scoped></style>
