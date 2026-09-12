<template>
  <SearchForm
    v-if="searchList.length > 0"
    :columns="searchList"
    :searchParam="props.searchParam!"
    :searchFn="searchFn"
    :resetFn="resetFn"
  >
    <template
      v-for="item in searchSlot"
      :key="item.prop"
      #[item.search?.slotName!]
    >
      <slot :name="item.search?.slotName" />
    </template>
  </SearchForm>
  <div
    class="card table-main"
    :style="{
      height: props.maxHeight ? props.maxHeight + 'px' : 'auto',
    }"
  >
    <div class="table-header">
      <div class="header-button-lf">
        <slot
          name="tableHeader"
          :selectedListIds="selectedListIds"
          :selectedList="selectedList"
          :isSelected="isSelected"
        />
      </div>
      <div class="header-button-ri" v-if="props.IsRefresh">
        <slot name="toolButton">
          <el-tooltip
            class="box-item"
            effect="dark"
            content="刷新"
            placement="top-start"
          >
            <el-button :icon="Refresh" circle @click="refreshFn" />
          </el-tooltip>
        </slot>
      </div>
    </div>

    <el-table
      ref="tableRef"
      v-bind="$attrs"
      :data="props.tableData"
      :border="props.border"
      :row-key="props.rowKey"
      @selection-change="selectionChange"
    >
      <el-table-column
        v-if="props.type !== 'none'"
        :type="props.type"
        align="center"
        :label="props.tableHeaderTitle"
        :reserve-selection="props.type === 'selection'"
        :width="props.tableHeaderWidth"
        :selectable="selectable"
      >
        <template #default="scope">
          <el-radio
            v-if="props.type === 'radio'"
            :label="scope.row[rowKey]"
            :value="scope.row[rowKey]"
            v-model="radio"
          >
            <i></i>
          </el-radio>
        </template>
      </el-table-column>
      <template v-for="item in tableList" :key="item.prop || item.label">
        <el-table-column
          :type="item.type"
          :prop="item.prop"
          :label="item.label"
          :align="item.align ?? 'center'"
          :show-overflow-tooltip="item.prop === 'operation' ? false : true"
          :width="item.width"
          :fixed="item.fixed"
        >
          <template #default="{ row }">
            <span v-if="!item.slot">
              {{ item.value ? item.value(row) : row[item.prop] ?? "--" }}
            </span>
            <slot v-if="item.slot" :name="item.prop" v-bind="row"></slot>
          </template>
        </el-table-column>
      </template>
    </el-table>
    <Pagination
      v-if="props.isPage"
      :pageData="props.pageData!"
      :PageChange="getList!"
    />
  </div>
</template>

<script setup lang="ts" name="ProTable">
import { ElTable, TableProps } from "element-plus";
import { Refresh } from "@element-plus/icons-vue";
import { isType } from "@/utils";
import { useSelection } from "@/hooks/useSelection";
import { isRef, watch } from "vue";

interface ProTableProps extends Partial<Omit<TableProps<any>, "data">> {
  columns?: ColumnProps[]; // 列配置项
  title?: string; // 表格标题，目前只在打印的时候用到 ==> 非必传
  border?: boolean; // 是否带有纵向边框 ==> 非必传（默认为true）
  rowKey?: string; // 行数据的 Key，用来优化 Table 的渲染，当表格数据多选时，所指定的 id ==> 非必传（默认为 id）
  type?: TypeProps; // 首列类型
  tableData: any[]; // 表格数据
  isPage?: boolean;
  pageData?: PageData; // 分页
  getList?: () => void; // 获取数据
  refreshFn?: () => void; // 刷新
  resetFn?: () => void; // 重置
  searchFn?: () => void; // 搜索方法
  searchParam?: TKeyValue; // 搜索参数
  maxHeight?: number; // 表格最大高度
  radioData?: string; // 单选回显数据
  tableHeaderTitle?: string; // 表头首列标题
  tableHeaderWidth?: number; // 表头首列宽
  selectable?: (row: any, index: number) => boolean; // 是否可选
  IsRefresh?: boolean; // 是否显示刷新按钮
}

const props = withDefaults(defineProps<ProTableProps>(), {
  IsRefresh: true,
  border: true,
  rowKey: "id",
  columns: () => [],
  type: "index",
  isPage: true,
  radioData: "",
  tableHeaderTitle: "序号",
  tableHeaderWidth: 55,
});

const radio = ref(props.radioData);

// 表格多选
const { isSelected, selectedList, selectedListIds, selectionChange } =
  useSelection(props.rowKey);

// 搜索
const searchList = computed(() =>
  props.columns.filter((item) => !!item.search)
);
// 动态插槽
const searchSlot = computed(() =>
  searchList.value.filter((item) => item.search?.el === "slot")
);

const tableList = computed(() => props.columns.filter((item) => !item.noShow));

const enumMap = ref(new Map<string, TKeyValue[]>());
provide("enumMap", enumMap);

// 辅助获取枚举数据
const getEnumData = async (el: ColumnProps) => {
  const enumValue = isRef(el.enum) ? el.enum.value : el.enum;
  if (isType(enumValue) === "array") return enumValue;
  const { data } = await (enumValue as (params?: any) => Res<any>)();
  return data?.list ?? data ?? [];
};

// 使用 watch 监听 searchList 和 enum 的变化
watch(
  () => searchList.value.map((el) => isRef(el.enum) ? el.enum.value : el.enum),
  async () => {
    for (const el of searchList.value) {
      if (["select"].includes(el.search!.el)) {
        const data = await getEnumData(el);
        enumMap.value.set(el.prop, data);
      }
    }
  },
  { immediate: true, deep: true }
);

const tableRef = ref<InstanceType<typeof ElTable>>();
defineExpose({ radio, tableRef, isSelected, selectedList, selectedListIds });
</script>
