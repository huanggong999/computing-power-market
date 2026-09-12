<template>
  <div class="table-search">
    <el-form ref="formRef" :model="searchParam" inline>
      <template v-for="item in props.columns" :key="index">
        <el-form-item
          :label="`${!item.isSearchLabel ? item.label : ''}`"
          :prop="item.prop"
        >
          <SearchFormItem
            v-if="item.search?.el !== 'slot'"
            :searchParam="props.searchParam"
            :column="item"
          />
          <!-- 动态插槽 -->
          <slot
            v-else="item.search?.el === 'slot'"
            :name="item.search.slotName"
          />
        </el-form-item>
      </template>
      <el-form-item>
        <div class="operation">
          <el-button type="primary" @click="props.searchFn"> 查询 </el-button>
          <el-button @click="props.resetFn">重置</el-button>
        </div>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts" name="SearchForm">
import SearchFormItem from "./components/SearchFormItem.vue";
interface ProTableProps {
  columns?: ColumnProps[]; // 搜索配置列
  searchParam: TKeyValue; // 搜索参数
  searchFn?: () => void; // 搜索方法
  resetFn?: () => void; // 重置方法
}
const props = withDefaults(defineProps<ProTableProps>(), {
  columns: () => [],
  searchParams: () => ({}),
});
</script>
<style lang="scss" scoped></style>
