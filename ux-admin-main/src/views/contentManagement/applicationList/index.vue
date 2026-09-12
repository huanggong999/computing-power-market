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
      <template #typeIdSearch>
        <el-cascader
          v-model="searchParam.typeId"
          :options="appleTypeList"
          :props="cascaderProps"
          @change="handleSearchChange"
        />
      </template>
      <template #img="row">
        <ImagePreview :src="row.img" />
      </template>
      <template #publishUserAvatar="row">
        <ImagePreview circle :src="row.publishUserAvatar" />
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
          @click="openPopover('edit', applicationDetailApi, row.id)"
        >
          编辑
        </el-button>
        <el-button
          link
          type="danger"
          @click="removeFn(applicationDeleteApi, row.id, row.name)"
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
        submit({
          addSubmitApi: applicationSaveApi,
          editSubmitApi: applicationUpdateApi,
        })
      "
    >
      <ProForm
        ref="proFormRef"
        v-model="dataForm"
        :formColumns="formColumns"
        :disabled="disabled"
        :label-width="160"
      >
        <template #typeId>
          <el-cascader
            v-model="typeId"
            :options="appleTypeList"
            :props="cascaderProps"
            @change="handleForm"
          />
        </template>
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
                {{
                  dataForm.mirrorVersion ? `${dataForm.mirrorVersion}` : "--"
                }}
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

<script setup lang="ts" name="ApplicationList">
import {
  applicationDeleteApi,
  applicationDetailApi,
  applicationListApi,
  applicationSaveApi,
  applicationUpdateApi,
} from "@/api/applicationList";
import { applyTypeAllApi } from "@/api/applyType";
import { useTable } from "@/hooks/useTable";
import { StatusEnum } from "@/utils/radioEnum";
import { statusSelectNumEnum } from "@/utils/selectEnum";
import { InputInstance } from "element-plus";
import MirrorList from "./components/MirrorList.vue";
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
  searchFn,
  resetFn,
  addOrEdit,
  popoverTitle,
  closePopover,
  disabled,
  submit,
  proFormRef,
  dataForm,
  removeFn,
} = useTable({ api: applicationListApi, title: "应用列表" });

const appleTypeList = ref<any[]>([]);
const getTypeList = async () => {
  const { data } = await applyTypeAllApi();
  appleTypeList.value = data.filter((el) => !!el.childrenList.length);
};
getTypeList();
const cascaderProps = { label: "name", value: "id", children: "childrenList" };
const handleSearchChange = (val: any) => (searchParam.value.typeId = val[1]);
const userInfo = useUser();

const columns: ColumnProps[] = [
  { prop: "id", label: "ID", width: 100 },
  {
    prop: "typeId",
    label: "关联应用分类",
    search: { el: "slot", slotName: "typeIdSearch" },
    value: (row) => row.typeName,
    width: 180,
  },
  { prop: "name", label: "应用名称", width: 180, search: { el: "input" } },
  { prop: "img", label: "应用图片", width: 100, slot: true },
  { prop: "intro", label: "应用简介" },
  {
    prop: "publishUserName",
    label: "发布者",
    width: 150,
    search: { el: "input" },
  },
  { prop: "publishUserAvatar", label: "发布者头像", width: 100, slot: true },
  { prop: "mirrorVersion", label: "版本" },
  { prop: "mirrorSize", label: "镜像大小", width: 100 },
  { prop: "sort", label: "排序", width: 80 },
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
  { label: "应用关联分类", prop: "typeId", el: "slot" },
  { label: "应用名称", prop: "name", el: "input" },
  { label: "应用简介", prop: "intro", el: "input" },
  { label: "应用图片", prop: "img", el: "img" },
  { label: "应用标签", prop: "tags", el: "slot" },
  { label: "应用版本/镜像大小", prop: "version", el: "slot", required: false },
  { label: "排序", prop: "sort", el: "number" },
  { label: "应用状态", prop: "status", el: "radio", radioList: StatusEnum },
  { label: "应用介绍", prop: "introduce", el: "markdownEditor" },
];

const typeId = ref<any[]>([]);

watch(
  () => addOrEdit.value,
  (val) => {
    if (!val) {
      typeId.value = [];
      tagList.value = [];
      return;
    }
    if (dataForm.value.id) {
      typeId.value = dataForm.value.typeTypes || [];
      tagList.value = (dataForm.value.tags || "").split(",");
    }
  }
);
const handleForm = (val: any) => (dataForm.value.typeId = val[1]);

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
