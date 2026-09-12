<template>
  <el-dialog
    v-model="model"
    :title="'选择镜像'"
    width="65%"
    center
    :destroy-on-close="true"
    :before-close="() => emit('update:modelValue', false)"
  >
    <ProTable
      type="radio"
      ref="ComProTableRef"
      :columns="columns"
      :tableData="dataList"
      :max-height="500"
      :IsRefresh="false"
      rowKey="imageId"
      :pageData="pageData"
      :getList="changTable"
    >
      <template #tableHeader>
        <el-radio-group
          class="mt10 mb10"
          v-model="visibility"
          @change="changeVisibility"
        >
          <el-radio-button
            v-for="item in radioGroup"
            :key="item.value"
            :value="item.value"
          >
            {{ item.label }}
          </el-radio-button>
        </el-radio-group>
      </template>
      <template #imageId="row">
        <div>{{ row.imageId }}</div>
        <div>{{ row.imageName }}</div>
      </template>
    </ProTable>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="emit('update:modelValue', false)">取消</el-button>
        <el-button type="primary" @click="confirm"> 确定 </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts" name="MirrorList">
import { imageListApi } from "@/api/applicationList";
import { useTable } from "@/hooks/useTable";
import { useVModel } from "@/utils/useVModel";

const props = defineProps<{ modelValue: boolean }>();

const emit = defineEmits(["update:modelValue", "submit"]);
const model = useVModel(props, "modelValue", emit);
const { tableData } = useTable({ requestAuto: false });

const myNextToken = ref("");
const visibility = ref("public");
const radioGroup = [
  { value: "public", label: "公共镜像" },
  { value: "shared", label: "共享镜像" },
];
// 切换镜像类型
const changeVisibility = () => {
  myNextToken.value = "";
  mirrorFn("refresh");
};
// 获取镜像列表
const mirrorFn = async (type: "refresh" | 0 = 0) => {
  const { data } = await imageListApi({
    nextToken: myNextToken.value,
    visibility: visibility.value,
  });
  const { nextToken, images } = data;
  tableData.value = !!type ? images : tableData.value.concat(images);
  myNextToken.value = nextToken;
  if (!!nextToken) mirrorFn();
  pageData.value.total = tableData.value.length;
  changTable();
};
const columns: ColumnProps[] = [
  { prop: "imageId", label: "名称/ID", slot: true },
  { prop: "platformVersion", label: "镜像版本" },
  { prop: "size", label: "容量", value: (row: any) => `${row.size}GB` },
  { prop: "platform", label: "架构类型" },
  { prop: "osType", label: "操作系统" },
];
const ComProTableRef = ref();
const confirm = () => {
  const { radio } = ComProTableRef.value;
  if (!radio) return ElMessage.error("请选择镜像");
  const { platformVersion, size, imageId } = tableData.value.find(
    (el) => el.imageId === radio
  );

  emit("submit", {
    platformVersion,
    size,
    imageId,
    visibility: visibility.value,
  });
  emit("update:modelValue", false);
};

watch(
  () => model.value,
  (val) => {
    if (val) return mirrorFn();
    tableData.value = [];
  }
);

// 手动分页
const pageData = ref({ pageSize: 10, pageNo: 1, total: 0 });
const dataList = ref<any[]>([]);
const changTable = () => {
  dataList.value = tableData.value.slice(
    (pageData.value.pageNo - 1) * pageData.value.pageSize,
    pageData.value.pageNo * pageData.value.pageSize
  );
};
</script>
<style lang="scss" scoped></style>
