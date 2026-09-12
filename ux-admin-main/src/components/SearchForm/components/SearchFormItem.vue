<template>
  <el-input
    v-if="props.column.search?.el === 'input'"
    :placeholder="placeholder"
    clearable
    v-model.trim="props.searchParam[searchKey]"
  />

  <el-select
    v-if="props.column.search?.el === 'select'"
    :placeholder="placeholder"
    clearable
    v-model.trim="props.searchParam[searchKey]"
    style="width: 200px"
  >
    <el-option
      v-for="(item, index) in columnEnum"
      :key="item.id || item[fieldNames.value] || index"
      :label="item[fieldNames.label]"
      :value="item[fieldNames.value]"
    />
  </el-select>
  <!-- 分页 -->
  <el-select
    v-if="props.column.search?.el === 'selectPage'"
    :placeholder="placeholder"
    v-model.trim="props.searchParam[searchKey]"
    clearable
    remote
    style="width: 200px"
  >
    <el-option
      v-for="(item, index) in selectPageList"
      :key="index"
      :label="item[fieldNames.label]"
      :value="item[fieldNames.value]"
    />

    <template #footer>
      <Pagination
        :pageData="pageData"
        :PageChange="getList"
        layout="prev, pager, next"
        :background="false"
      />
    </template>
  </el-select>
  <el-date-picker
    v-if="props.column.search?.el === 'date-picker'"
    v-model="timeList"
    clearable
    :type="props.column.search.dateType"
    :rangeSeparator="placeholder.rangeSeparator"
    :start-placeholder="placeholder.startPlaceholder"
    :end-placeholder="placeholder.endPlaceholder"
    :value-format="props.column.search.valueFormat"
    @change="changeTime"
  ></el-date-picker>
</template>

<script setup lang="ts" name="SearchFormItem">
interface SearchFormItem {
  column: ColumnProps;
  searchParam: TKeyValue;
}
const props = defineProps<SearchFormItem>();
const searchKey = computed(() => props.column.search?.key ?? props.column.prop);

//
const placeholder = computed(() => {
  const { search, label } = props.column;
  if (search?.el === "date-picker") {
    if (
      ["datetimerange", "daterange", "monthrange"].includes(
        search.dateType as string
      )
    ) {
      return {
        rangeSeparator: "至",
        startPlaceholder: "开始时间",
        endPlaceholder: "结束时间",
      };
    }
  }
  const other =
    search?.placeholder ??
    (search?.el.includes("input") ? "请输入" : "请选择") + label;
  return other;
});
// 判断 fieldNames 设置 label && value && children 的 key 值
const fieldNames = computed(() => {
  return {
    label: props.column.fieldNames?.label ?? "label",
    value: props.column.fieldNames?.value ?? "value",
  };
});
// 日期
const timeList = ref("");
// 日期类型是否为数组
const type = computed(() => {
  return !props.column.search?.dateEnum?.length;
});
// 清空日期
watchEffect(() => {
  if (
    props.column.search?.el === "date-picker" &&
    (!props.searchParam[searchKey.value] ||
      !props.searchParam[props.column.search!.dateEnum![0]])
  )
    timeList.value = "";
});

// 修改日期
const changeTime = (val: any): void => {
  if (type.value) return (props.searchParam[searchKey.value] = val);
  props.searchParam[props.column.search!.dateEnum![0]] = val[0];
  props.searchParam[props.column.search!.dateEnum![1]] = val[1];
};

const enumMap = inject("enumMap", ref(new Map()));
const columnEnum = computed(() => enumMap.value.get(props.column.prop));

//#region 下拉分页
const pageData = ref({ pageSize: 10, pageNo: 1, total: 1 });
const pageParams = computed(() => ({
  size: pageData.value.pageSize,
  current: pageData.value.pageNo,
}));
const selectPageList = ref([]);

const getList = async () => {
  props.searchParam[searchKey.value] = "";
  const { data } = await props.column.pageFn!(pageParams.value, true);
  const { total, list, pageNo, pageSize } = data;
  selectPageList.value = list;
  updatePageData({ pageSize, total, pageNo });
};

const updatePageData = (resPageTable: PageData) =>
  Object.assign(pageData.value, resPageTable);

onMounted(() => {
  if (props.column.search?.el === "selectPage") getList();
});
//#endregion
</script>
<style lang="scss" scoped></style>
