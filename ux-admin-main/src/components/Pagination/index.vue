<template>
  <el-pagination
    :background="props.background"
    :current-page="props.pageData.pageNo"
    :page-size="pageData.pageSize"
    :page-sizes="[10, 25, 50, 100]"
    :total="props.pageData.total"
    :layout="props.layout"
    @size-change="handleSizeChange"
    @current-change="handleCurrentChange"
  >
  </el-pagination>
</template>

<script setup lang="ts" name="Pagination">
interface PaginationProps {
  pageData: PageData;
  PageChange: () => void;
  layout?: string;
  background?: boolean;
}
const handleSizeChange = (val: number) => (
  (props.pageData.pageSize = val), props.PageChange()
);
const handleCurrentChange = (val: number) => (
  (props.pageData.pageNo = val), props.PageChange()
);
const props = withDefaults(defineProps<PaginationProps>(), {
  layout: "total, sizes, prev, pager, next, jumper",
  background: true,
});
</script>
