<template>
  <div class="table-box">
    <Breadcrumb :router-list="routerList" />
    <div class="table-box position-relative breadcrumbTable">
      <div class="card top">
        <div class="flx-justify-between mb20">
          <TipText content="发票抬头" />
          <el-button type="primary" :icon="CirclePlus" @click="createTitle">
            添加发票抬头
          </el-button>
        </div>
        <div class="table-box-table">
          <ProTable
            :IsRefresh="false"
            :isPage="false"
            :columns="columns"
            :tableData="titleList"
          >
            <template #invoiceType="row">
              {{ row.invoiceType == 1 ? "增值税普通发票" : "增值税专用发票" }}
            </template>
            <template #handle="row">
              <div class="table-handle flx-center">
                <span
                  class="handle"
                  style="color: #3972fd"
                  @click="updateTitle(row)"
                  >编辑</span
                >
                <el-popconfirm
                  title="确认删除当前发票抬头吗?"
                  @confirm="deleteHandler('title', row.id)"
                  confirm-button-text="确认"
                  cancel-button-text="取消"
                >
                  <template #reference>
                    <span style="color: #ff4151">删除</span>
                  </template>
                </el-popconfirm>
              </div>
            </template>
          </ProTable>
        </div>
      </div>
      <div class="card bottom">
        <div class="flx-justify-between mb20">
          <TipText content="电子邮箱" />
          <el-button type="primary" :icon="CirclePlus" @click="createEmail">
            添加邮箱
          </el-button>
        </div>
        <div class="table-box-table">
          <ProTable
            :isPage="false"
            :IsRefresh="false"
            :columns="emailColumns"
            :tableData="emailList"
          >
            <template #handle="row">
              <div class="table-handle flx-center">
                <span style="color: #3972fd" @click="updateEmail(row)"
                  >编辑</span
                >
                <el-popconfirm
                  title="将该邮箱设置为默认邮箱吗?"
                  @confirm="setDefaultEmail(row)"
                  confirm-button-text="确认"
                  cancel-button-text="取消"
                >
                  <template #reference>
                    <span style="color: #3972fd">默认邮箱</span>
                  </template>
                </el-popconfirm>

                <el-popconfirm
                  title="确认删除当前邮箱吗?"
                  @confirm="deleteHandler('email', row.id)"
                  confirm-button-text="确认"
                  cancel-button-text="取消"
                >
                  <template #reference>
                    <span style="color: #ff4151">删除</span>
                  </template>
                </el-popconfirm>
              </div>
            </template>
          </ProTable>
        </div>
      </div>
    </div>
    <el-dialog
      v-model="InvoiceTitleDialog"
      :title="dialogType == 'create' ? '新增抬头信息' : '更新抬头信息'"
      width="578"
    >
      <ProForm
        ref="invoiceFormRef"
        v-model="invoiceForm"
        :formColumns="invoiceFormCol"
        style="margin-top: 20px"
        :labelWidth="120"
      >
        <template #invoiceTitle>
          <el-input
            :disabled="invoiceForm.billingType === 1"
            v-model="invoiceForm.invoiceTitle"
          ></el-input>
        </template>
      </ProForm>
      <div v-if="invoiceForm.billingType === 1">
        <ProForm
          v-model="PersonalForm"
          :formColumns="PersonalFormCol"
          :labelWidth="120"
        />
      </div>

      <div v-if="invoiceForm.billingType === 2">
        <ProForm
          v-if="invoiceForm.invoiceType === 1"
          v-model="companyForm"
          :formColumns="companyFormCol"
          :labelWidth="120"
        />
        <ProForm
          v-if="invoiceForm.invoiceType === 2"
          v-model="companyForm"
          :formColumns="companyFormCol"
          :labelWidth="120"
        />
      </div>

      <!-- <ProForm
        v-show="invoiceForm.billingType === 2"
        v-model="companyForm"
        :formColumns="companyFormCol"
        :labelWidth="120"
      >
      </ProForm> -->
      <div class="btns flx-align-center">
        <el-button class="cancel" @click="InvoiceTitleDialog = false"
          >取消</el-button
        >
        <el-button type="primary" @click="submitTitleHandler()">确定</el-button>
      </div>
    </el-dialog>
    <el-dialog
      v-model="emailDialog"
      :title="dialogType === 'create' ? '新增电子邮箱' : '修改电子邮箱'"
      width="578"
    >
      <ProForm
        v-model="emailForm"
        :formColumns="emailFormCol"
        style="margin-top: 20px"
      >
      </ProForm>
      <div class="btns flx-align-center">
        <el-button class="cancel" @click="emailDialog = false">取消</el-button>
        <el-button type="primary" @click="submitEmailHandler">确定</el-button>
      </div></el-dialog
    >
  </div>
</template>

<script setup lang="ts" name="ManageInvoice">
import {
  createInvoiceEmailApi,
  createInvoiceTitleApi,
  deleteInvoiceEmailApi,
  deleteInvoiceTitleApi,
  getInvoiceEmailListApi,
  getInvoiceTitleListApi,
  setDefaultEmailApi,
  updateInvoiceEmailApi,
  updateInvoiceTitleApi,
} from "@/api/invoice";
import { useUserInfo } from "@/store";
import { CirclePlus } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";

const routerList = ref([
  { name: "主控抬", path: "/console" },
  { name: "发票管理", path: "/invoiceManagement" },
  { name: "管理发票信息与接收方式", path: "" },
]);
const columns: ColumnProps[] = [
  { prop: "invoiceTitle", label: "发票抬头" },
  { prop: "invoiceType", label: "发票类型", slot: true },
  { prop: "handle", label: "操作", slot: true },
];
const emailColumns: ColumnProps[] = [
  { prop: "email", label: "电子邮箱" },
  { prop: "handle", label: "操作", slot: true },
];
const titleList = ref([]);
const emailList = ref([]);
const targetTitleId = ref();
// 初始化页面数据
const initPage = () => {
  getInvoiceTitleListApi().then((res) => {
    titleList.value = res.data;
  });
  getInvoiceEmailListApi().then((res) => {
    emailList.value = res.data;
  });
  targetTitleId.value = null;
};
initPage();
const dialogType = ref("create");
// 添加发票抬头
const InvoiceTitleDialog = ref(false);
const invoiceFormRef = ref();
const invoiceForm = ref({
  billingType: 1,
  invoiceType: 1,
  invoiceTitle: "个人",
});
const invoiceFormCol: IFormColumnsProps[] = [
  {
    label: "开票类型",
    prop: "billingType",
    el: "radio",
    radioList: [
      { label: "个人", value: 1 },
      { label: "企业", value: 2 },
    ],
    required: true,
    // itemLabelWidth: 100,
  },
  {
    label: "发票类型",
    prop: "invoiceType",
    el: "radio",
    radioList: [
      { label: "增值税普通发票", value: 1 },
      { label: "增值税专用发票", value: 2 },
    ],
    required: true,
    // itemLabelWidth: 100,
  },
  {
    label: "发票抬头",
    prop: "invoiceTitle",
    el: "slot",
    required: true,
    // itemLabelWidth: 100,
  },
];
const companyForm = ref<TKeyValue>({});
// const companyForm = ref({
//   number: "",
//   deposit: "",
//   phoneNumber: "",
//   account: "",
//   address: "",
//   contact: "",
//   contactPhone: "",
// });
const userInfo = useUserInfo();
const PersonalFormCol: IFormColumnsProps[] = [
  {
    label: "联系人",
    prop: "contact",
    el: "input",
    // required: invoiceForm.value.invoiceType == 2 ? true : false,
    required: true,
    // itemLabelWidth: 100,
  },
  {
    label: "联系电话",
    prop: "phoneNumber",
    el: "input",
    // required: invoiceForm.value.invoiceType == 2 ? true : false,
    required: true,
    // itemLabelWidth: 100,
  },
];
const PersonalForm = ref({ contact: "", phoneNumber: "" });
onMounted(() => {});
const companyFormCol = computed((): IFormColumnsProps[] => [
  {
    label: "纳税人识别号",
    prop: "number",
    el: "input",
    required: true,
    // itemLabelWidth: 100,
  },
  {
    label: "基本开户银行",
    prop: "deposit",
    el: "input",
    required: invoiceForm.value.invoiceType == 2 ? true : false,
    // itemLabelWidth: 100,
  },
  {
    label: "企业电话",
    prop: "phoneNumber",
    el: "input",
    // required: invoiceForm.value.invoiceType == 2 ? true : false,
    required: true,
    // itemLabelWidth: 100,
  },
  {
    label: "基本开户账号",
    prop: "account",
    el: "input",
    required: invoiceForm.value.invoiceType == 2 ? true : false,
    // itemLabelWidth: 100,
  },
  {
    label: "企业注册地址",
    prop: "address",
    el: "input",
    required: invoiceForm.value.invoiceType == 2 ? true : false,
    // itemLabelWidth: 100,
  },
  {
    label: "企业联系人",
    prop: "contact",
    el: "input",
    // required: invoiceForm.value.invoiceType == 2 ? true : false,
    required: true,
    // itemLabelWidth: 100,
  },
  {
    label: "联系人电话",
    prop: "contactPhone",
    el: "input",
    // required: invoiceForm.value.invoiceType == 2 ? true : false,
    required: true,
    // itemLabelWidth: 100,
  },
]);
// 监听发票类型
watch(
  () => invoiceForm.value.billingType,
  (newVal) => {
    if (newVal === 1) {
      invoiceForm.value.invoiceTitle = "个人";
    } else {
      if (dialogType.value === "update") return;
      invoiceForm.value.invoiceTitle = userInfo.companyName
        ? userInfo.companyName
        : "";
      companyForm.value = {
        number: userInfo.companyCode ? userInfo.companyCode : "",
        address: userInfo.companyAddress ? userInfo.companyAddress : "",
        phoneNumber: userInfo.companyContactPhone
          ? userInfo.companyContactPhone
          : "",
        contact: userInfo.companyContactName ? userInfo.companyContactName : "",
        contactPhone: "",
        deposit: "",
        account: "",
      };
    }
  }
);

const createTitle = () => {
  invoiceForm.value = {
    billingType: 1,
    invoiceType: 1,
    invoiceTitle: "个人",
  };
  PersonalForm.value = { contact: "", phoneNumber: "" };
  dialogType.value = "create";
  InvoiceTitleDialog.value = true;
};
const updateTitle = (row: any) => {
  const {
    number,
    deposit,
    account,
    address,
    phoneNumber,
    contact,
    contactPhone,
    billingType,
    invoiceType,
    invoiceTitle,
  } = row;

  nextTick(() => {
    PersonalForm.value = { contact, phoneNumber };
    invoiceForm.value = { billingType, invoiceType, invoiceTitle };
    companyForm.value = {
      number,
      deposit,
      account,
      address,
      phoneNumber,
      contact,
      contactPhone,
    };
  });
  console.log("row", row);
  console.log("companyForm", companyForm.value);
  targetTitleId.value = row.id;
  dialogType.value = "update";
  InvoiceTitleDialog.value = true;
};
const submitTitleHandler = () => {
  let param: any = invoiceForm.value;
  if (invoiceForm.value.billingType === 1) {
    param = { ...invoiceForm.value, ...PersonalForm.value };
    const { phoneNumber, contact } = param;
    if (!phoneNumber || !contact) return ElMessage.error("请填写完整信息");
  }
  if (invoiceForm.value.billingType === 2) {
    param = { ...invoiceForm.value, ...companyForm.value };
    //
    const { number, contact, phoneNumber } = param;
    if (!number || !contact || !phoneNumber)
      return ElMessage.error("请填写完整信息");
  }

  // 新增
  if (dialogType.value === "create") {
    createInvoiceTitleApi(param).then((res) => {
      if (res.code === 200) {
        ElMessage.success("新增发票抬头成功!");
        InvoiceTitleDialog.value = false;
        initPage();
      }
    });
  }
  // 更新
  else {
    param.id = targetTitleId.value;
    console.log(param);
    updateInvoiceTitleApi(param).then((res) => {
      if (res.code === 200) {
        ElMessage.success("更新发票抬头成功!");
        InvoiceTitleDialog.value = false;
        initPage();
      }
    });
  }
};
// 邮箱
const emailDialog = ref(false);
const emailForm = ref({ email: "" });
const emailFormCol: IFormColumnsProps[] = [
  {
    label: "电子邮箱",
    prop: "email",
    el: "input",
    required: true,
    itemLabelWidth: 100,
  },
];
const targetEmailId = ref();
const createEmail = () => {
  emailForm.value = {
    email: "",
  };
  dialogType.value = "create";
  emailDialog.value = true;
};
const updateEmail = (row: any) => {
  emailForm.value.email = row.email;
  console.log(row);
  targetEmailId.value = row.id;
  dialogType.value = "update";
  emailDialog.value = true;
};
const submitEmailHandler = () => {
  // 新增
  if (dialogType.value === "create") {
    createInvoiceEmailApi(emailForm.value).then((res) => {
      if (res.code === 200) {
        ElMessage.success("新增邮箱成功!");
        emailDialog.value = false;
        initPage();
      }
    });
  }
  // 更新
  else {
    const param = {
      email: emailForm.value.email,
      id: targetEmailId.value,
    };
    updateInvoiceEmailApi(param).then((res) => {
      if (res.code === 200) {
        ElMessage.success("更新邮箱成功!");
        emailDialog.value = false;
        initPage();
      }
    });
  }
};

// 删除处理
const deleteHandler = (type: string, id: number) => {
  if (type === "title") {
    deleteInvoiceTitleApi(id).then((res) => {
      if (res.code === 200) {
        ElMessage.success("删除成功!");
        initPage();
      }
    });
  } else if (type === "email") {
    deleteInvoiceEmailApi(id).then((res) => {
      if (res.code === 200) {
        ElMessage.success("删除成功!");
        initPage();
      }
    });
  }
};
// 设置默认邮箱
const setDefaultEmail = (row: any) => {
  setDefaultEmailApi({ id: row.id, email: row.email }).then((res) => {
    if (res.code === 200) {
      ElMessage.success("设置成功!");
      initPage();
    }
  });
};
</script>
<style lang="scss" scoped>
.top,
.bottom {
  height: 49.5%;
}
.bottom {
  margin-top: 1%;
}

.table-box-table {
  max-height: calc(100% - 52px);
}
.table-handle {
  gap: 10px;
  span {
    &:hover {
      cursor: pointer;
    }
  }
}
.btns {
  .cancel {
    margin-left: auto;
  }
}
</style>
