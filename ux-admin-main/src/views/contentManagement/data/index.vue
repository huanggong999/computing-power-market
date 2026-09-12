<template>
  <div class="table-box">
    <ProTable
      :columns="columns"
      :tableData="tableData"
      :pageData="pageData"
      :search-param="searchParam"
      :refreshFn="refreshFn"
      :getList="getList"
      :searchFn="searchFn"
      :resetFn="resetFn"
      type="none"
    >
      <template #tableHeader>
        <el-button
          @click="
            openPopover('add', {
              status: 'OK',
              sort: 0,
              publishUserName: userInfo.nickName,
              publishUserAvatar: userInfo.avatar,
            })
          "
        >
          新增
        </el-button>
      </template>
      <template #cover="row">
        <ImagePreview :src="row.cover" />
      </template>
      <template #publishUserAvatar="row">
        <ImagePreview class="img" circle :src="row.publishUserAvatar" />
      </template>
      <template #status="row">
        <el-tag :type="enumTag('statusTag', row.status)">
          {{ enumType("statusEnum", row.status) }}
        </el-tag>
      </template>
      <template #operation="row">
        <el-button
          link
          type="primary"
          @click="openPopover('edit', dataDetailApi, row.id)"
        >
          编辑
        </el-button>
        <el-button
          link
          type="danger"
          @click="removeFn(dataDeleteApi, row.id, row.name)"
        >
          删除
        </el-button>
      </template>
    </ProTable>
    <Drawer
      v-model="addOrEdit"
      :title="popoverTitle"
      @closePopover="closePopover"
      :disabled="disabled"
      size="80%"
      @submit="
        submit({ addSubmitApi: dataSaveApi, editSubmitApi: dataUpdateApi })
      "
    >
      <ProForm
        ref="proFormRef"
        v-model="dataForm"
        :formColumns="formColumns"
        :disabled="disabled"
        :label-width="160"
      >
        <template #tags>
          <div class="flex gap-2">
            <el-tag
              v-for="tag in tagList"
              :key="tag"
              class="mr5"
              closable
              :disable-transitions="false"
              @close="handleClose(tag)"
            >
              {{ tag }}
            </el-tag>
            <el-input
              v-if="inputVisible"
              ref="InputRef"
              v-model="tagValue"
              class="w-20"
              size="small"
              @keyup.enter="handleInputConfirm"
              @blur="handleInputConfirm"
            />
            <el-button v-else size="small" @click="showInput">
              添加标签
            </el-button>
          </div>
        </template>
        <template #version>
          <div>
            <el-button class="mb10" @click="isShowMirror = true">
              选择镜像
            </el-button>
            <el-button class="mb10" @click="clearMirror"> 取消选择 </el-button>
            <el-descriptions border>
              <el-descriptions-item label="版本">
                {{ dataForm.mirrorVersion ?? "--" }}
              </el-descriptions-item>
              <el-descriptions-item label="镜像">
                {{ dataForm.mirrorSize ? `${dataForm.mirrorSize}` : "--" }}
              </el-descriptions-item>
            </el-descriptions>
          </div>
        </template>
      </ProForm>
    </Drawer>
    <MirrorList v-model="isShowMirror" @submit="submitMirror" />
  </div>
</template>

<script setup lang="ts" name="Data">
import {
  dataDeleteApi,
  dataDetailApi,
  dataPageApi,
  dataSaveApi,
  dataUpdateApi,
} from "@/api/data";
import { useTable } from "@/hooks/useTable";
import { StatusEnum } from "@/utils/radioEnum";
import { InputInstance } from "element-plus";
import MirrorList from "../applicationList/components/MirrorList.vue";
import { statusSelectNumEnum } from "@/utils/selectEnum";
import { useUser } from "@/store";
import { enumType } from "@/utils/Enum";
import { enumTag } from "@/utils/enumTag";

const {
  tableData,
  pageData,
  searchParam,
  refreshFn,
  getList,
  openPopover,
  addOrEdit,
  popoverTitle,
  closePopover,
  disabled,
  submit,
  proFormRef,
  dataForm,
  searchFn,
  resetFn,
  removeFn,
} = useTable({ api: dataPageApi, title: "数据" });
const userInfo = useUser();
const columns: ColumnProps[] = [
  { prop: "name", label: "数据名称", search: { el: "input" } },
  { prop: "intro", label: "数据简介" },
  { prop: "cover", label: "封面图", width: 100, slot: true },
  {
    prop: "publishUserName",
    label: "发布者",
    width: 150,
    search: { el: "input" },
  },
  { prop: "publishUserAvatar", label: "发布者头像", width: 100, slot: true },
  { prop: "mirrorVersion", label: "版本" },
  { prop: "mirrorSize", label: "镜像大小", width: 100 },
  { prop: "sort", label: "排序", width: 100 },
  {
    prop: "status",
    label: "状态",
    slot: true,
    search: { el: "select" },
    enum: statusSelectNumEnum,
  },
  { prop: "operation", label: "操作", slot: true },
];
const formColumns: IFormColumnsProps[] = [
  { label: "数据名称", prop: "name", el: "input" },
  { label: "数据简介", prop: "intro", el: "input" },
  { label: "数据标签", prop: "tags", el: "slot" },
  { label: "应用版本/镜像大小", prop: "version", el: "slot", required: false },
  { label: "封面图", prop: "cover", el: "img" },
  { label: "排序", prop: "sort", el: "number" },
  { label: "数据状态", prop: "status", el: "radio", radioList: StatusEnum },
  { label: "数据介绍", prop: "introduce", el: "markdownEditor" },
];

watch(
  () => addOrEdit.value,
  (val) => {
    if (!val) {
      tagList.value = [];
      return;
    }
    if (dataForm.value.id) {
      tagList.value = (dataForm.value.tags || "").split(",");
    }
  }
);
const tagList = ref<string[]>([]);
watch(
  () => tagList.value,
  (val) => {
    dataForm.value.tags = val.join(",");
  },
  { deep: true }
);
const tagValue = ref("");
const inputVisible = ref(false);
const handleClose = (tag: string) =>
  tagList.value.splice(tagList.value.indexOf(tag), 1);
const handleInputConfirm = () => {
  if (!tagValue.value) return (inputVisible.value = false);
  if (tagList.value.includes(tagValue.value)) {
    ElMessage.info("标签已存在");
  } else {
    tagList.value.push(tagValue.value);
  }
  inputVisible.value = false;
  tagValue.value = "";
};
const InputRef = ref<InputInstance>();
const showInput = () => {
  inputVisible.value = true;
  nextTick(() => InputRef.value!.input!.focus());
};
// 选择镜像
const isShowMirror = ref(false);

const submitMirror = (from: TKeyValue) => {
  const { platformVersion, size, imageId, visibility } = from;
  dataForm.value.mirrorVersion = platformVersion;
  dataForm.value.mirrorSize = `${size}G`;
  dataForm.value.mirrorType = visibility;
  dataForm.value.mirrorId = imageId;
};
const clearMirror = () => {
  dataForm.value.mirrorVersion = "";
  dataForm.value.mirrorSize = "";
  dataForm.value.mirrorType = "";
  dataForm.value.mirrorId = "";
};
</script>
<style lang="scss" scoped></style>
