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
          type="primary"
          @click="
            openPopover('add', {
              status: 1,
              sort: 0,
              bandwidth: 2,
              isIpDisplay: 1,
              isBandwidthDisplay: 1,
            })
          "
        >
          新增
        </el-button>
      </template>
      <template #status="row">
        <el-tag :type="enumTag('statusNumTag', row.status)">
          {{ enumType("statusNumEnum", row.status) }}
        </el-tag>
      </template>
      <template #operation="row">
        <el-button link type="primary" @click="checkIp(row)">
          查看IP列表
        </el-button>
        <el-button
          link
          type="primary"
          @click="openPopover('edit', productDetailApi, row.id)"
        >
          编辑
        </el-button>
        <el-button
          link
          type="danger"
          @click="removeFn(productDeleteApi, row.id, row.name)"
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
      @submit="
        submit({
          addSubmitApi: productSaveApi,
          editSubmitApi: productUpdateApi,
        })
      "
      size="50%"
      :isSave="!dataForm.id"
      @save="save"
    >
      <ProForm
        ref="proFormRef"
        v-model="dataForm"
        :formColumns="formColumns"
        :disabled="disabled"
        :label-width="180"
      >
        <template #bandwidthPrice>
          <el-space>
            <el-input-number
              placeholder="请输入"
              :controls="false"
              :precision="2"
              v-model="dataForm.bandwidthPrice"
              :min="0"
            />
            <div>
              是否展示：
              <el-radio-group v-model="dataForm.isBandwidthDisplay">
                <el-radio
                  v-for="(item, index) in isShowEnum"
                  :key="index"
                  :label="item.label"
                  :value="item.label"
                >
                  {{ item.description }}
                </el-radio>
              </el-radio-group>
            </div>
          </el-space>
        </template>
        <template #ipPrice>
          <el-space>
            <el-input-number
              placeholder="请输入"
              :controls="false"
              :precision="2"
              v-model="dataForm.ipPrice"
              :min="0"
            />
            <div>
              是否展示：
              <el-radio-group v-model="dataForm.isIpDisplay">
                <el-radio
                  v-for="(item, index) in isShowEnum"
                  :key="index"
                  :label="item.label"
                  :value="item.label"
                >
                  {{ item.description }}
                </el-radio>
              </el-radio-group>
            </div>
          </el-space>
        </template>
        <template #crossedPrice>
          <el-space>
            <el-input-number
              placeholder="请输入"
              :controls="false"
              :precision="2"
              v-model="dataForm.crossedPrice"
              :min="0"
            />
            <div>
              是否展示：
              <el-radio-group v-model="dataForm.isIpCountDisplay">
                <el-radio
                  v-for="(item, index) in isShowEnum"
                  :key="index"
                  :label="item.label"
                  :value="item.label"
                >
                  {{ item.description }}
                </el-radio>
              </el-radio-group>
            </div>
          </el-space>
        </template>
        <template #payPrice>
          <el-input-number
            placeholder="请输入"
            :controls="false"
            :precision="2"
            v-model="dataForm.payPrice"
            :min="0"
          />
          <el-text class="ml10">
            （售价 = 账户单价/20+宽带单价/20+IP单价/20）
          </el-text>
        </template>
      </ProForm>
    </Drawer>

    <ProductIP
      v-model="isVisible"
      :productId="productId"
      :ipAddress="ipAddress"
      @closeCheckIp="isVisible = false"
    />
  </div>
</template>

<script setup lang="ts" name="ProductList">
import {
  inquiryFormPageApi,
  productDeleteApi,
  productDetailApi,
  productPageApi,
  productSaveApi,
  productUpdateApi,
} from "@/api/productManagement";
import { useTable } from "@/hooks/useTable";
import { enumType } from "@/utils/Enum";
import { enumTag } from "@/utils/enumTag";
import {
  productSpecificationEnum,
  statusNumberEnum,
  isShowEnum,
} from "@/utils/radioEnum";
import ProductIP from "./components/ProductIP.vue";

const {
  tableData,
  pageData,
  searchParam,
  refreshFn,
  getList,
  searchFn,
  resetFn,
  openPopover,
  popoverTitle,
  addOrEdit,
  closePopover,
  disabled,
  submit,
  proFormRef,
  dataForm,
  removeFn,
  save,
} = useTable({ api: productPageApi, title: "产品列表" });

const columns: ColumnProps[] = [
  { label: "商品ID", prop: "id", width: 120 },
  { label: "产品名称", prop: "name", search: { el: "input" }, width: 150 },
  { label: "国家地区", prop: "country", width: 150 },
  { label: "参考价格(元/天/M)", prop: "referPrice", width: 170 },
  { label: "划线价(元/天/M)", prop: "crossedPrice", width: 140 },
  { label: "售价(元/天/M)", prop: "payPrice", width: 130 },
  { label: "账户单价(元/月)", prop: "accountPrice", width: 180 },
  { label: "带宽单价(元/月)", prop: "bandwidthPrice", width: 180 },
  { label: "IP单价(元/月)", prop: "ipPrice", width: 180 },
  { label: "关联咨询表单", prop: "formName", width: 180 },
  { label: "关联购买表单", prop: "payFormName", width: 173 },
  { label: "产品描述", prop: "memo", width: 200 },
  { label: "排序", prop: "sort", width: 80 },
  { label: "状态", prop: "status", slot: true, width: 80 },
  { label: "发布者", prop: "createBy" },
  { label: "创建时间", prop: "createTime", width: 180 },
  { label: "操作", prop: "operation", slot: true, width: 230, fixed: "right" },
];
const formColumns: IFormColumnsProps[] = [
  { label: "产品名称", prop: "name", el: "input" },
  { label: "国家地区", prop: "country", el: "input" },
  { label: "产品封面图", prop: "image", el: "img" },
  //
  { label: "账户单价(元/月)", prop: "accountPrice", el: "price" },
  { label: "带宽单价(元/月)", prop: "bandwidthPrice", el: "slot" },
  { label: "IP单价(元/月)", prop: "ipPrice", el: "slot" },
  {
    label: "账户数是否关联IP数",
    prop: "isAccountIpBinding",
    el: "radio",
    radioList: isShowEnum,
  },
  //
  {
    label: "1个月折扣（%）",
    maxLength: 100,
    prop: "oneMonthDiscount",
    el: "number",
  },
  {
    label: "2个月折扣（%）",
    maxLength: 100,
    prop: "twoMonthDiscount",
    el: "number",
  },
  {
    label: "3个月折扣（%）",
    maxLength: 100,
    prop: "threeMonthDiscount",
    el: "number",
  },
  {
    label: "4个月折扣（%）",
    maxLength: 100,
    prop: "fourMonthDiscount",
    el: "number",
  },
  {
    label: "5个月折扣（%）",
    maxLength: 100,
    prop: "fiveMonthDiscount",
    el: "number",
  },
  {
    label: "6个月折扣（%）",
    maxLength: 100,
    prop: "sixMonthDiscount",
    el: "number",
  },
  {
    label: "7个月折扣（%）",
    maxLength: 100,
    prop: "sevenMonthDiscount",
    el: "number",
  },
  {
    label: "8个月折扣（%）",
    maxLength: 100,
    prop: "eightMonthDiscount",
    el: "number",
  },
  {
    label: "9个月折扣（%）",
    maxLength: 100,
    prop: "nineMonthDiscount",
    el: "number",
  },
  {
    label: "10个月折扣（%）",
    maxLength: 100,
    prop: "tenMonthDiscount",
    el: "number",
  },
  {
    label: "11个月折扣（%）",
    maxLength: 100,
    prop: "elevenMonthDiscount",
    el: "number",
  },
  {
    label: "一年折扣（%）",
    maxLength: 100,
    prop: "oneYearDiscount",
    el: "number",
  },
  {
    label: "二年折扣（%）",
    maxLength: 100,
    prop: "twoYearDiscount",
    el: "number",
  },
  {
    label: "三年折扣（%）",
    maxLength: 100,
    prop: "threeYearDiscount",
    el: "number",
  },

  //
  // { label: "建议参考价(元/天/M)", prop: "referPrice", el: "price" },
  // { label: "划线价(元/天/M)", prop: "crossedPrice", el: "price" },
  // { label: "售价(元/天/M)", prop: "payPrice", el: "slot" },
  { label: "参考价(元/月)", prop: "referPrice", el: "price" },
  { label: "IP单价(元/个)", prop: "crossedPrice", el: "slot" },
  { label: "售价(元/月)", prop: "payPrice", el: "slot" },
  // { label: "售价(元/天/M)", prop: "payPrice", el: "price" },
  {
    label: "产品规格",
    prop: "bandwidth",
    el: "radio",
    radioList: productSpecificationEnum,
  },
  { label: "产品描述", prop: "memo", el: "textarea" },
  { label: "排序", prop: "sort", el: "number" },
  { label: "状态", prop: "status", el: "radio", radioList: statusNumberEnum },
  {
    label: "关联咨询表单",
    prop: "formId",
    el: "selectPage",
    selectLabel: "name",
    selectValue: "id",
    pageFn: inquiryFormPageApi,
    selectParams: { formType: 1 },
  },
  {
    label: "关联购买表单",
    prop: "payFormId",
    el: "selectPage",
    selectLabel: "name",
    selectValue: "id",
    pageFn: inquiryFormPageApi,
    selectParams: { formType: 2 },
  },
  { label: "产品详情", prop: "detail", el: "wangEditor", required: false },
];
const isVisible = ref(false);
const productId = ref("");
const ipAddress = ref("");
const checkIp = (row: TKeyValue) => {
  productId.value = row.id;
  ipAddress.value = row.country;
  isVisible.value = true;
};
</script>
<style lang="scss" scoped></style>
