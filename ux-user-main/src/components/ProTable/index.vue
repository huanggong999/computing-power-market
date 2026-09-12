<template>
  <SearchForm
    v-if="searchList.length > 0"
    :columns="searchList"
    :searchParam="props.searchParam!"
    :searchFn="searchFn"
    :resetFn="resetFn"
  >
    <template
      v-for="(item, index) in searchSlot"
      :key="index"
      #[item.search?.slotName!]
    >
      <slot :name="item.search?.slotName" />
    </template>
  </SearchForm>
  <div
    class="table-main"
    :style="{
      height: props.maxHeight ? props.maxHeight + 'px' : '',
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
      :cell-style="{
        textAlign: 'center',
        fontSize: '13px',
      }"
      :header-cell-style="{
        textAlign: 'center',
        fontSize: '13px',
      }"
      v-loading="props.loading"
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
            v-if="props.type === 'radio' && scope.row.sku"
            :label="scope.row"
            v-model="radio"
            :disabled="scope.row.disabledRadio"
          >
            <i></i>
            <div class="not-radio" v-if="scope.row.disabledRadio">罄</div>
            <div class="have-radio" v-else>购</div>
          </el-radio>
          <el-radio
            v-else-if="props.type === 'radio'"
            :label="scope.row"
            v-model="radio"
            :disabled="scope.row.disabledRadio"
          >
            <i></i>
          </el-radio>

          <!-- <el-radio
            v-if="props.type === 'radio' && !scope.row.disabledRadio"
            :label="scope.row"
            v-model="radio"
            :disabled="scope.row.disabledRadio"
          >
            <i></i>
          </el-radio> -->
          <!--  <div class="not-radio" v-if="!scope.row.disabledRadio">罄</div> -->
        </template>
      </el-table-column>
      <template v-for="item in tableList" :key="item.prop">
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
              {{ item.value ? item.value(row) : (row[item.prop] ?? "--") }}
            </span>
            <slot v-else :name="item.prop" v-bind="row"></slot>
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
import { useSelection } from "@/hooks/useSelection";
import { isType } from "@/utils";
import { Refresh } from "@element-plus/icons-vue";
import { ElTable } from "element-plus";
interface IProTableProps {
  columns: ColumnProps[]; // 列配置项
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
  loading?: boolean;
}

const props = withDefaults(defineProps<IProTableProps>(), {
  IsRefresh: true,
  border: true,
  rowKey: "id",
  columns: () => [],
  type: "index",
  isPage: true,
  radioData: "",
  tableHeaderTitle: "序号",
  tableHeaderWidth: 70,
});

// 表格多选
const { isSelected, selectedList, selectedListIds, selectionChange } =
  useSelection(props.rowKey);
// 表格数据
const tableList = computed(() => props.columns.filter((item) => !item.noShow));

// 单选数据
const radio = ref(props.radioData);
const tableRef = ref<InstanceType<typeof ElTable>>();

// 搜索
const searchList = computed(() =>
  props.columns.filter((item) => !!item.search),
);
// 动态插槽
const searchSlot = computed(() =>
  searchList.value.filter((item) => item.search?.el === "slot"),
);
const enumMap = ref(new Map<string, TKeyValue[]>());
// 辅助获取枚举数据
const getEnumData = async (el: ColumnProps) => {
  return isType(el.enum) === "array"
    ? el.enum
    : (await (el.enum as (params?: any) => Res<any>)()).data.list;
};
searchList.value.forEach(async (el: ColumnProps) => {
  if (["select"].includes(el.search!.el))
    enumMap.value.set(el.prop, await getEnumData(el));
});

provide("enumMap", enumMap);
defineExpose({ radio, tableRef, isSelected, selectedList, selectedListIds });
</script>
<style lang="scss" scoped>
.not-radio {
  width: 26px;
  height: 24px;
  border-radius: 4px 4px 4px 4px;
  border: 1px solid #cccccc;
  margin-left: 10px;
  margin-top: 2px;
  margin-bottom: 2px;
  font-weight: 400;
  font-size: 16px;
  color: #cccccc;
  cursor: default;
}
.have-radio {
  width: 26px;
  height: 24px;
  border-radius: 4px 4px 4px 4px;
  border: 1px solid #fba201;
  margin-left: 10px;
  margin-top: 2px;
  margin-bottom: 2px;
  font-weight: 400;
  font-size: 16px;
  color: #fba201;
  cursor: default;
}
</style>
