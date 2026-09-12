<template>
  <div class="table-box">
    <ProTable
      :tableData="tableData"
      :columns="columns"
      :isPage="false"
      :refreshFn="refreshFn"
    >
      <template #tableHeader>
        <el-button @click="openPopover('add')"> 新增 </el-button>
      </template>
      <template #icon="row">
        <el-icon> <component :is="row.icon" /></el-icon>
      </template>
      <template #visible="row">
        <el-tag :type="enumTag('isShowTag', row.visible)">
          {{ enumType("isShowEnum", row.visible) }}
        </el-tag>
      </template>
      <template #operation="row">
        <template v-for="item in operationBtn">
          <el-button
            link
            :type="item.type"
            v-if="item.show!(row)"
            @click="item.click(row)"
          >
            {{ item.label }}
          </el-button>
        </template>
      </template>
    </ProTable>
    <Drawer
      v-model="addOrEdit"
      :title="popoverTitle"
      @closePopover="closePopover"
      @submit="
        submit({ addSubmitApi: saveMenuApi, editSubmitApi: updateMenuApi })
      "
    >
      <template #default>
        <ProForm
          ref="proFormRef"
          v-model="dataForm"
          :formColumns="formColumns"
          :labelWidth="100"
        >
          <!-- 父级菜单 -->
          <template #parentId>
            <el-tree-select
              v-model="dataForm.parentId"
              :props="defaultProps"
              :data="treeSelectList"
              :render-after-expand="false"
              check-strictly="true"
              clearable
            />
          </template>
          <!-- 图标 -->
          <template #icon>
            <el-popover
              v-model:visible="showChooseIcon"
              placement="bottom-start"
              :width="540"
              trigger="click"
              @show="showSelectIcon"
            >
              <template #reference>
                <el-input class="input-icon" v-model="dataForm.icon" readonly>
                  <template #append>
                    <el-icon class="el-input__icon">
                      <!-- <component :is="dataForm.icon || Calendar"></component> -->
                      <component :is="Calendar" />
                    </el-icon>
                  </template>
                </el-input>
              </template>
              <!-- 选择图标 -->
              <SelectIcon ref="iconSelectRef" @selectedIcon="selectedIcon" />
            </el-popover>
          </template>
          <!-- 外链 -->
          <template #isFrame>
            <el-tooltip
              content="选择是外链则路由地址需要以`http(s)://`开头"
              placement="top"
            >
              <el-icon><question-filled /></el-icon>
            </el-tooltip>
            <el-radio-group v-model="dataForm.isFrame" class="ml10">
              <el-radio
                v-for="item in yesOrNoEnum"
                :key="item.label"
                :label="item.label"
              >
                {{ item.description }}
              </el-radio>
            </el-radio-group>
          </template>
          <!-- 路由地址 -->
          <template #path>
            <div class="flx-center" style="width: 100%">
              <el-tooltip
                content="访问的路由地址，如：`user`，如外网地址需内链访问则以`http(s)://`开头"
                placement="top"
              >
                <el-icon><question-filled /></el-icon>
              </el-tooltip>
              <el-input
                v-model="dataForm.path"
                placeholder="请输入路由地址"
                class="ml10"
              />
            </div>
          </template>
          <!-- 是否缓存 -->
          <template #isCache>
            <el-tooltip
              content="选择是则会被`keep-alive`缓存，需要匹配组件的`name`和地址保持一致"
              placement="top"
            >
              <el-icon><question-filled /></el-icon>
            </el-tooltip>
            <el-radio-group v-model="dataForm.isCache" class="ml10">
              <el-radio
                v-for="item in menuIsCacheEnum"
                :key="item.label"
                :label="item.label"
              >
                {{ item.description }}
              </el-radio>
            </el-radio-group>
          </template>
          <!-- 路由地址 -->
          <template #component>
            <div class="flx-center" style="width: 100%">
              <el-tooltip
                content="访问的组件路径，如：`system/user/index`，默认在`views`目录下`user`，如外网地址需内链访问则以`http(s)://`开头"
                placement="top"
              >
                <el-icon><question-filled /></el-icon>
              </el-tooltip>
              <el-input
                v-model="dataForm.component"
                placeholder="请输入组件路径"
                class="ml10"
              />
            </div>
          </template>
          <!-- 显示状态 -->
          <template #visible>
            <el-tooltip
              content="选择隐藏则路由将不会出现在侧边栏，但仍然可以访问"
              placement="top"
            >
              <el-icon><question-filled /></el-icon>
            </el-tooltip>
            <el-radio-group v-model="dataForm.visible" class="ml10">
              <el-radio
                v-for="item in menuVisibleEnum"
                :key="item.label"
                :label="item.label"
              >
                {{ item.description }}
              </el-radio>
            </el-radio-group>
          </template>
          <!-- 菜单状态 -->
          <template #status>
            <el-tooltip
              content="选择停用则路由将不会出现在侧边栏，也不能被访问"
              placement="top"
            >
              <el-icon><question-filled /></el-icon>
            </el-tooltip>
            <el-radio-group v-model="dataForm.status" class="ml10">
              <el-radio
                v-for="item in StatusEnum"
                :key="item.label"
                :label="item.label"
              >
                {{ item.description }}
              </el-radio>
            </el-radio-group>
          </template>
        </ProForm>
      </template>
    </Drawer>
  </div>
</template>

<script setup lang="ts" name="Menus">
import {
  getMenuTreeApi,
  saveMenuApi,
  getMenuDetailApi,
  updateMenuApi,
  deleteMenuApi,
} from "@/api/menu";
import { useTable } from "@/hooks/useTable";
import { enumType } from "@/utils/Enum";
import { enumTag } from "@/utils/enumTag";
import {
  menuTypeEnum,
  yesOrNoEnum,
  StatusEnum,
  menuVisibleEnum,
  menuIsCacheEnum,
} from "@/utils/radioEnum";
import { Calendar } from "@element-plus/icons-vue";

const {
  tableData,
  refreshFn,
  addOrEdit,
  popoverTitle,
  closePopover,
  dataForm,
  proFormRef,
  openPopover,
  submit,
  removeFn,
} = useTable({ api: getMenuTreeApi, title: "菜单", isPage: false });

const columns: ColumnProps[] = [
  { prop: "menuName", label: "菜单名称", align: "left" },
  { prop: "icon", label: "菜单图标", slot: true, width: 100 },
  { prop: "path", label: "菜单路径" },
  { prop: "perms", label: "权限标识" },
  { prop: "component", label: "组件路径" },
  { prop: "visible", label: "是否显示", slot: true },
  { prop: "orderNum", label: "排序", width: 80 },
  { prop: "operation", label: "操作", slot: true },
];

//#region 插槽相关
const operationBtn: IOperationBtnItem[] = [
  {
    label: "编辑",
    type: "primary",
    show: () => true,
    click: (row: any) => openPopover("edit", getMenuDetailApi, row.id),
  },
  {
    label: "新增",
    type: "primary",
    show: () => true,
    click: (row: any) => openPopover("edit", { parentId: row.id }),
  },
  {
    label: "删除",
    type: "danger",
    show: () => true,
    click: (row: any) => removeFn(deleteMenuApi, row.id, row.menuName),
  },
];
//#endregion

const initFormColumn: IFormColumnsProps[] = [
  { label: "菜单类型", prop: "menuType", el: "radio", radioList: menuTypeEnum },
  { label: "上级菜单", prop: "parentId", el: "slot" },
  { label: "菜单名称", prop: "menuName", el: "input" },
  { label: "排序", prop: "orderNum", el: "number" },
];
// 目录
const MuLuColumns: IFormColumnsProps[] = [
  { label: "图标", prop: "icon", el: "slot" },
  { label: "是否外链", prop: "isFrame", el: "slot" },
  { label: "路由地址", prop: "path", el: "slot" },
  { label: "显示状态", prop: "visible", el: "slot" },
  { label: "菜单状态", prop: "status", el: "slot" },
];
// 菜单
const MenuColumns: IFormColumnsProps[] = [
  { label: "图标", prop: "icon", el: "slot" },
  { label: "是否外链", prop: "isFrame", el: "slot" },
  { label: "路由地址", prop: "path", el: "slot" },
  { label: "组件路径", prop: "component", el: "slot" },
  { label: "权限标识", prop: "perms", el: "input", required: false },
  { label: "是否缓存", prop: "isCache", el: "slot" },
  { label: "显示状态", prop: "visible", el: "slot" },
  { label: "菜单状态", prop: "status", el: "slot" },
];
// 按钮
const BtnColumns: IFormColumnsProps[] = [
  { label: "权限标识", prop: "perms", el: "input", required: false },
  { label: "菜单状态", prop: "status", el: "slot" },
];
const formColumns = ref<IFormColumnsProps[]>([...initFormColumn]);
watchEffect(() => {
  if (dataForm.value.menuType === "M")
    formColumns.value = [...initFormColumn, ...MuLuColumns];
  if (dataForm.value.menuType === "C")
    formColumns.value = [...initFormColumn, ...MenuColumns];
  if (dataForm.value.menuType === "F")
    formColumns.value = [...initFormColumn, ...BtnColumns];
});

//#region 图标
const iconSelectRef = ref<any>();
const showChooseIcon = ref(false);
const selectedIcon = (name: string) => (dataForm.value.icon = name);
const showSelectIcon = () => {
  iconSelectRef.value.reset();
  showChooseIcon.value = true;
};
//#endregion

const treeSelectList = computed(() => [
  ...[{ id: 0, menuName: "主类目" }],
  ...tableData.value,
]);
// 树形选择器
const defaultProps = {
  children: "children",
  label: "menuName",
  value: "id",
};
</script>
<style lang="scss" scoped>
.input-icon {
  cursor: pointer !important;
}
</style>
