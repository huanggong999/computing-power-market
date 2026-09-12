<template>
  <el-pagination
    :background="props.background"
    :current-page="props.pageData.pageNo"
    :page-size="pageData.pageSize"
    :page-sizes="pageSizes"
    :total="props.pageData.total"
    :layout="props.layout"
    @size-change="handleSizeChange"
    @current-change="handleCurrentChange"
  />
</template>

<script setup lang="ts" name="Pagination">
interface PaginationProps {
  pageData: PageData;
  PageChange: () => void;
  layout?: string;
  background?: boolean;
  pageSizes?: number[];
}
const props = withDefaults(defineProps<PaginationProps>(), {
  layout: "total, sizes, prev, pager, next, jumper",
  background: true,
  pageSizes: () => [10, 25, 50, 100],
});
const emit = defineEmits(["getList"]);

const handleSizeChange = (val: number) => (
  (props.pageData.pageSize = val), props.PageChange()
);
const handleCurrentChange = (val: number) => (
  (props.pageData.pageNo = val), props.PageChange()
);
</script>
<style lang="scss" scoped>
.el-pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
