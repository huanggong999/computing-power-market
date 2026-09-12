<template>
  <div>
    <Breadcrumb :router-list="routerList" />
    <div class="table-box breadcrumbTable">
      <template v-if="!IsSubmitOrder">
        <div class="tip mb22">
          <div
            class="tip-item mt12 ml22"
            v-for="(item, index) in tipList"
            :key="index"
          >
            {{ item }}
          </div>
        </div>
        <div>
          <el-radio-group v-model="pageSearchParam.type">
            <el-radio-button :value="1">按账期开票</el-radio-button>
            <el-radio-button :value="2">按消费时间开票</el-radio-button>
            <el-radio-button :value="3">提前开票</el-radio-button>
          </el-radio-group>
        </div>
        <div class="table-v2">
          <el-auto-resizer>
            <template #default="{ width, height }">
              <el-table-v2
                :columns="columns"
                :data="tableData"
                fixed
                :width="width"
                :height="height"
              >
                <template #bill="row" v-if="pageSearchParam.type == 1">
                  <div class="export" @click="exportDetail(row)">导出明细</div>
                </template>
              </el-table-v2>
            </template>
          </el-auto-resizer>
        </div>
      </template>
      <template v-else>
        <div class="card table-box mb10">
          <TipText content="发票抬头" class="mb10" />
          <ProTable
            :isPage="false"
            :IsRefresh="false"
            type="radio"
            :columns="titleCol"
            :tableData="titleTableData"
            :maxHeight="90"
            :radioData="titleTableRadio"
            ref="titleTableRef"
          >
            <template #invoiceType="row">
              {{ row.invoiceType == 1 ? "增值税普通发票" : "增值税专用发票" }}
            </template>
            <template #billingType="row">
              {{ row.billingType == 1 ? "个人" : "企业" }}
            </template>
          </ProTable>
        </div>
        <div class="card table-box mb10">
          <TipText content="电子邮箱" class="mb10" />
          <ProTable
            :isPage="false"
            :IsRefresh="false"
            type="radio"
            :columns="emailCol"
            :tableData="emailTableData"
            :maxHeight="90"
            ref="emailTableRef"
            :radioData="emailTableRadio"
          />
        </div>
        <div class="card table-box mb10">
          <TipText content="开票内容" class="mb10" />
          <ProTable
            :isPage="false"
            :IsRefresh="false"
            type="radio"
            :columns="contentCol"
            :tableData="contentTableData"
            :maxHeight="90"
            ref="contentTableRef"
            :radioData="contentTableRadio"
          />
        </div>
      </template>

      <div class="card total" v-if="!IsSubmitOrder">
        合计:
        <span class="ml20 mr20"> {{ totalPrice }} </span>
        <el-button
          type="primary"
          :disabled="!totalPrice"
          plain
          @click="nextStep"
        >
          下一步
        </el-button>
      </div>
      <div class="card total" v-else>
        合计:
        <span class="ml20 mr20"> {{ finalPrice }} </span>
        <el-button
          type="primary"
          :disabled="!couldSubmit"
          plain
          @click="submitHandler"
        >
          提交
        </el-button>
      </div>
    </div>
  </div>
  <el-dialog v-model="isCompleted" width="800" center>
    <div class="flx-center content">
      <el-icon size="92" color="#3BA46F"><CircleCheckFilled /></el-icon>
      <p class="mt22 title fwb">开票提交成功</p>
      <p class="mt22">审核通过并开票后，电子发票将会发送到您的邮箱，请留意</p>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="toPage('/invoiceManagement')"
          >查看开票记录</el-button
        >
        <el-button type="primary" @click="toPage('/invoiceManagement')">
          返回
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="tsx" name="ApplyBilling">
import {
  applyInvoiceApi,
  exportInvoiceDetailApi,
  getEnableInvoiceListApi,
  getInvoiceEmailListApi,
  getInvoiceTitleListApi,
} from "@/api/invoice";
import { CheckboxValueType, ElButton, ElCheckbox } from "element-plus";
import { useTable } from "@/hooks/useTable";
import { toPage } from "@/utils";
import { FunctionalComponent } from "vue";

const routerList = ref([
  { name: "主控台", path: "/console" },
  { name: "发票管理", path: "/invoiceManagement" },
  { name: "申请开票", path: "" },
]);
const tipList = [
  "发票需基于消费生成订单或账单后方可申请开具发票，充值未消费的金额不可申请发票。单个订单、账单不可拆分开票，但可合并开票。一般会在收到发票申请后7个工作日内为您开具发票。",
  "发票抬头需与实名认证主体名称一致，不可自主修改。若企业需要变更发票抬头，请先根据变更情况完成认证主体变更。个人账号无法开具企业抬头发票，若需要修改为企业抬头，则需进行个人认证升级为企业认证前往实名认证。",
  "一张发票只能有一个发票类目及税率。当涉及多个类目和税率时，系统会自动生成多张发票。",
  "若您在火山引擎官网购买了第三方服务商提供的产品，您可在开票方选项中选择该服务商，发起相应产品订单的开票申请。自2024年7月1日起，对于按量计费产品，当月消费不能马上开票，需要次月第3个自然日才可开具发票。",
  "您可能还想了解发票信息与接收方式，开票操作指引，退票/取消操作指引，欠票说明，数电发票介绍&常见问题QA。发票开具遇到问题也可点击屏幕右侧联系客服。",
];
const { tableData, getList, searchParam } = useTable({
  requestApi: getEnableInvoiceListApi,
  requestAuto: false,
});
const pageSearchParam = ref({
  type: 1,
});
type SelectionCellProps = {
  value: boolean;
  intermediate?: boolean;
  ariaLabel?: string;
  disabled?: boolean;
  onChange: (value: CheckboxValueType) => void;
};
const SelectionCell: FunctionalComponent<SelectionCellProps> = ({
  value,
  intermediate = false,
  ariaLabel,
  onChange,
  disabled,
}) =>
  h(ElCheckbox, {
    onChange,
    modelValue: value,
    ariaLabel,
    indeterminate: intermediate,
    disabled,
  });

const columns: any[] = [
  {
    key: "selection",
    width: 50,
    cellRenderer: ({ rowData }: any) => {
      console.log("🚀 ~ rowData:", rowData);
      const onChange = (value: CheckboxValueType) => (rowData.checked = value);
      return h(SelectionCell, {
        value: rowData.checked,
        ariaLabel: "选择",
        onChange,
        disabled: rowData.tag != 0,
      });
    },

    headerCellRenderer: () => {
      const _data = unref(tableData);
      const onChange = (value: CheckboxValueType) =>
        (tableData.value = _data.map((row) => {
          row.checked = value;
          return row;
        }));
      const allSelected = _data.every((row) => row.checked);
      const containsChecked = _data.some((row) => row.checked);

      return h(SelectionCell, {
        value: allSelected,
        intermediate: containsChecked && !allSelected,
        ariaLabel: "选择",
        onChange,
      });
    },
  },
  {
    key: "bill",
    title: "账期",
    dataKey: "bill",
    width: 300,
    align: "center",
    cellRenderer: ({ cellData: bill }: TKeyValue) => bill ?? "--",
  },
  {
    title: "开票方",
    dataKey: "productName",
    align: "center",
    width: 300,
    cellRenderer: ({ cellData: productName }: TKeyValue) => productName ?? "--",
  },
  {
    title: "可开票总额",
    dataKey: "totalAmount",
    align: "center",
    width: 300,
    cellRenderer: ({ cellData: totalAmount }: TKeyValue) => totalAmount ?? "--",
  },
  {
    title: "可开票金额",
    dataKey: "amount",
    align: "center",
    width: 300,
    cellRenderer: ({ cellData: amount }: TKeyValue) => amount ?? "--",
  },
  {
    title: "操作",
    dataKey: "handle",
    align: "center",
    width: 300,
    cellRenderer: ({ rowData }: TKeyValue) =>
      h(
        ElButton,
        {
          type: "danger",
          link: true,
          onClick: () => exportDetail(rowData),
        },
        "导出明细",
      ),
  },
];

const tableList = computed(
  () => tableData.value.filter((item) => item.checked && item.tag == 0) ?? [],
);

const totalPrice = computed(
  () =>
    tableList.value &&
    tableList.value
      .map((el: any) => el.amount)
      .reduce((acc: number, curr: number) => Number(acc) + Number(curr), 0),
);

const tableRef = ref();

// 导出账单明细
const exportDetail = (data: any) => {
  // 这里需要对数据的billIds进行处理
  let ids = data.billIds.split(",").map((el: any) => {
    return el;
  });
  exportInvoiceDetailApi([...ids]).then((res: any) => {
    if (res) {
      // 创建一个临时的URL
      const url = URL.createObjectURL(res);

      // 创建一个<a>元素
      const a = document.createElement("a");

      // 设置下载文件的名字
      a.href = url;
      a.download = "123.xlsx";

      // 模拟点击<a>元素，触发下载
      a.click();

      // 释放临时的URL对象
      URL.revokeObjectURL(url);
    }
  });
};

// 暂存已选账期
const selectedBill = ref<any>([]);
const IsSubmitOrder = ref(false);

// 发票抬头表格
const titleCol: ColumnProps[] = [
  { prop: "invoiceTitle", label: "发票抬头" },
  { prop: "invoiceType", label: "发票类型", slot: true },
  { prop: "billingType", label: "开票类型", slot: true },
];
const { tableData: titleTableData, getList: getTitleList } = useTable({
  requestApi: getInvoiceTitleListApi,
  requestAuto: false,
});

// 邮箱表格
const emailCol: ColumnProps[] = [{ prop: "email", label: "电子邮箱" }];
const { tableData: emailTableData, getList: getEmailList } = useTable({
  requestApi: getInvoiceEmailListApi,
  requestAuto: false,
});
const isCompleted = ref(false);

// 开票内容表格
const contentCol: ColumnProps[] = [
  { prop: "productName", label: "项目名称" },
  { prop: "size", label: "规格型号" },
  { prop: "unit", label: "单位" },
  { prop: "price", label: "单价" },
  { prop: "totalPrice", label: "金额" },
  { prop: "tax", label: "税率" },
  { prop: "taxPrice", label: "税额" },
];
const contentTableData = computed(() => [
  {
    productName: "*信息技术服务*云服务费",
    size: '无(发票上显示为"无")',
    unit: "套",
    num: 1,
    price: finalPrice.value,
    totalPrice: finalPrice.value,
    tax: "6%",
    taxPrice: finalPrice.value * 0.06,
  },
]);
const finalPrice = ref(0);
// 下一步
const nextStep = () => {
  IsSubmitOrder.value = true;
  finalPrice.value = totalPrice.value;
  tableList.value.forEach((item: any) => {
    item.billIds.split(",").map((el: any) => {
      selectedBill.value.push(el);
    });
  });
};
const titleTableRef = ref();
const titleTableRadio = computed(() => {
  return titleTableRef.value ? titleTableRef.value.radio : null;
});
const emailTableRef = ref();
const emailTableRadio = computed(() => {
  return emailTableRef.value ? emailTableRef.value.radio : null;
});
const contentTableRef = ref();
const contentTableRadio = computed(() => {
  return contentTableRef.value ? contentTableRef.value.radio : null;
});
// 是否可以提交
const couldSubmit = computed(() => {
  if (
    titleTableRadio.value &&
    emailTableRadio.value &&
    contentTableRadio.value
  ) {
    return true;
  } else return false;
});
// 提交处理方法
const submitHandler = () => {
  const data = {
    billIds: selectedBill.value,
    invoiceTitleId: titleTableRadio.value.id,
    invoiceEmailId: emailTableRadio.value.id,
    invoicePrice: finalPrice.value,
    type: pageSearchParam.value.type,
  };
  applyInvoiceApi(data).then((res) => {
    if (res.code === 200) {
      isCompleted.value = true;
    }
  });
};
watch(
  () => pageSearchParam.value.type,
  () => {
    initPage();
    console.log(tableRef.value);
    nextTick(() => {
      tableRef.value.selectedList = [];
      tableRef.value.selectedListIds = [];
    });
  },
);
// 初始化页面
const initPage = () => {
  searchParam.value = pageSearchParam.value;
  getList();
  getTitleList();
  getEmailList();
};
initPage();
</script>
<style lang="scss" scoped>
.table-v2 {
  height: 400px;
  box-sizing: border-box;
}

.tip {
  font-size: 16px;
  color: #666666;
  font-weight: 400;
  background: linear-gradient(86deg, #fef0f0 0%, rgba(255, 255, 255, 0) 100%);
  border-radius: 10px;
  padding: 20px;
  padding-top: 12px;
  &-item {
    position: relative;
    &::before {
      content: "";
      width: 8px;
      height: 8px;
      border-radius: 50%;
      border: 2px solid #ff4151;
      top: 50%;
      transform: translateY(-50%);

      position: absolute;
      left: -22px;
    }
  }
}
.total {
  display: flex;
  justify-content: flex-end;
  position: fixed;
  width: 86%;
  z-index: 90;
  bottom: 0;
}
.content {
  flex-direction: column;
  .title {
    font-size: 20px;
    color: #000;
  }
}
.breadcrumbTable {
  padding-bottom: 70px;
}
.export {
  color: red;
  &:hover {
    cursor: pointer;
  }
}
</style>
