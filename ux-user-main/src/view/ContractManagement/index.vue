<template>
  <div class="table-box position-relative">
    <el-radio-group v-model="activeName" @change="handleClick">
      <!-- <el-radio-button label="产品报价合同" value="product" /> -->
      <el-radio-button label="订单合同" value="order" />
      <el-radio-button label="授信额合同" value="credit" />
      <!-- <el-radio-button label="架构合同" value="architecture" /> -->
    </el-radio-group>

    <div class="table-box card" v-if="activeName === 'order'">
      <div class="flx-align-center">
        <SearchForm
          :columns="columnsMap[activeName].searchColumns"
          :searchParam="searchParam"
          :searchFn="searchFn"
          :resetFn="resetFn"
          style="margin-left: auto"
        >
        </SearchForm>
        <el-button
          type="primary"
          style="margin-bottom: 10px; margin-left: -20px"
          v-if="activeName === 'order'"
          :icon="CirclePlus"
          @click="toPage('/contractManagement/applyForAContract?type=1')"
        >
          申请电子合同
        </el-button>
        <el-button
          type="primary"
          style="margin-bottom: 10px; margin-left: 10px"
          v-if="activeName === 'order'"
          :icon="CirclePlus"
          @click="contract"
        >
          申请纸质合同
        </el-button>
      </div>
      <ProTable
        type="none"
        :columns="columnsMap[activeName].columns"
        :tableData="tableData"
        :pageData="pageData"
        :IsRefresh="false"
        :get-list="getList"
      >
        <template #type="row">
          <div v-if="row.type == 1">电子</div>
          <div v-else>纸质</div>
        </template>
        <template #linkOrderNo="row">
          {{ row.linkOrderNo || "--" }}
        </template>
        <template #status="row">
          <!-- 电子 -->
          <el-tag type="info" v-if="row.type == 1 && row.status == 1"
            >待沟通</el-tag
          >
          <el-tag type="warning" v-if="row.type == 1 && row.status == 2"
            >待签署</el-tag
          >
          <el-tag type="success" v-if="row.type == 1 && row.status == 3"
            >已签署</el-tag
          >
          <el-tag type="danger" v-if="row.type == 1 && row.status == 4"
            >已过期</el-tag
          >
          <!-- 纸质 -->
          <el-tag type="info" v-if="row.type == 2 && row.status == 1"
            >待乙方确认</el-tag
          >
          <el-tag type="warning" v-if="row.type == 2 && row.status == 2"
            >待归档</el-tag
          >
          <el-tag type="success" v-if="row.type == 2 && row.status == 3"
            >已签署</el-tag
          >
          <el-tag type="danger" v-if="row.type == 2 && row.status == 5"
            >审核不通过</el-tag
          >
          <el-tag type="danger" v-if="row.type == 2 && row.status == 6">
            归档审核不通</el-tag
          >
          <el-tag type="warning" v-if="row.type == 2 && row.status == 8"
            >待上传</el-tag
          >
        </template>
        <template #uploadImg="row">
          <div v-if="!row.uploadImg">--</div>
          <div v-else><a :href="row.uploadImg">下载附件</a></div>
        </template>
        <template #signUploadImg="row">
          <div v-if="!row.signUploadImg">--</div>
          <div v-else><a :href="row.signUploadImg">下载附件</a></div>
        </template>
        <template #handle="row">
          <!-- 电子 -->
          <span
            v-if="row.type == 1 && row.status == 2"
            class="blue"
            @click="handleMap('sign', row)"
            >签订</span
          >
          <span
            v-if="row.type == 1 && row.status == 3"
            class="blue"
            @click="handleMap('check', row)"
            >查看</span
          >
          <span
            v-if="row.type == 1 && row.status == 3"
            class="blue"
            @click="handleMap('download', row)"
            >下载合同</span
          >
          <!-- 纸质 -->
          <span
            v-if="row.type == 2 && row.status == 5"
            class="blue"
            @click="paperHandleMap('again', row, '1')"
            >重新申请</span
          >
          <span
            v-if="row.type == 2 && (row.status == 8 || row.status == 6)"
            class="blue"
            @click="paperHandleMap('upload', row, '1')"
            >上传合同</span
          >
        </template>
      </ProTable>
    </div>
    <div class="table-box card" v-else-if="activeName === 'credit'">
      <div class="flx-align-center">
        <SearchForm
          :columns="creditSearchColumns"
          :searchParam="creditSearchParam"
          :searchFn="creditSearchFn"
          :resetFn="creditResetFn"
          style="margin-left: auto"
        >
        </SearchForm>
        <el-button
          type="primary"
          style="margin-bottom: 10px; margin-left: -20px"
          :icon="CirclePlus"
          @click="openApplyDialog(1)"
        >
          申请电子合同
        </el-button>
        <el-button
          type="primary"
          style="margin-bottom: 10px; margin-left: 10px"
          :icon="CirclePlus"
          @click="openApplyDialog(2)"
        >
          申请纸质合同
        </el-button>
      </div>
      <ProTable
        type="none"
        :columns="creditColumns"
        :tableData="creditTableData"
        :pageData="creditPageData"
        :IsRefresh="false"
        :get-list="creditGetList"
      >
        <template #type="row">
          <div v-if="row.type == 1">电子</div>
          <div v-else>纸质</div>
        </template>
        <template #uploadImg="row">
          <div v-if="!row.uploadImg">--</div>
          <div v-else><a :href="row.uploadImg">下载附件</a></div>
        </template>
        <template #signUploadImg="row">
          <div v-if="!row.signUploadImg">--</div>
          <div v-else><a :href="row.signUploadImg">下载附件</a></div>
        </template>
        <template #status="row">
          <!-- 电子 -->
          <el-tag type="info" v-if="row.type == 1 && row.status == 1"
            >待沟通</el-tag
          >
          <el-tag type="warning" v-if="row.type == 1 && row.status == 2"
            >待签署</el-tag
          >
          <el-tag type="success" v-if="row.type == 1 && row.status == 3"
            >已签署</el-tag
          >
          <el-tag type="danger" v-if="row.type == 1 && row.status == 4"
            >已过期</el-tag
          >
          <!-- 纸质 -->
          <el-tag type="info" v-if="row.type == 2 && row.status == 1"
            >待乙方确认</el-tag
          >
          <el-tag type="warning" v-if="row.type == 2 && row.status == 2"
            >待归档</el-tag
          >
          <el-tag type="success" v-if="row.type == 2 && row.status == 3"
            >已签署</el-tag
          >
          <el-tag type="danger" v-if="row.type == 2 && row.status == 5"
            >审核不通过</el-tag
          >
          <el-tag type="danger" v-if="row.type == 2 && row.status == 6">
            归档审核不通</el-tag
          >
          <el-tag type="warning" v-if="row.type == 2 && row.status == 8"
            >待上传</el-tag
          >
        </template>
        <template #handle="row">
          <!-- 电子 -->
          <span
            v-if="row.type == 1 && row.status == 2"
            class="blue"
            @click="handleMap('sign', row)"
            >签订</span
          >
          <span
            v-if="row.type == 1 && row.status == 3"
            class="blue"
            @click="handleMap('check', row)"
            >查看</span
          >
          <span
            v-if="row.type == 1 && row.status == 3"
            class="blue"
            @click="handleMap('download', row)"
            >下载合同</span
          >
          <!-- 纸质 -->
          <span
            v-if="row.type == 2 && row.status == 5"
            class="blue"
            @click="paperHandleMap('again', row, '2')"
            >重新申请</span
          >
          <span
            v-if="row.type == 2 && (row.status == 8 || row.status == 6)"
            class="blue"
            @click="paperHandleMap('upload', row, '2')"
            >上传合同</span
          >
        </template>
      </ProTable>
    </div>
    <div class="table-box card" v-else-if="activeName === 'architecture'">
      <div class="flx-align-center">
        <SearchForm
          :columns="creditSearchColumns"
          :searchParam="creditSearchParam"
          :searchFn="creditSearchFn"
          :resetFn="creditResetFn"
          style="margin-left: auto"
        >
        </SearchForm>
        <el-button
          type="primary"
          style="margin-bottom: 10px; margin-left: -20px"
          :icon="CirclePlus"
          @click="openApplyDialog(1)"
        >
          申请电子合同
        </el-button>
        <el-button
          type="primary"
          style="margin-bottom: 10px; margin-left: 10px"
          :icon="CirclePlus"
          @click="openApplyDialog(2)"
        >
          申请纸质合同
        </el-button>
      </div>
      <ProTable
        type="none"
        :columns="creditColumns"
        :tableData="creditTableData"
        :pageData="creditPageData"
        :IsRefresh="false"
        :get-list="creditGetList"
      >
        <template #type="row">
          <div v-if="row.type == 1">电子</div>
          <div v-else>纸质</div>
        </template>
        <template #uploadImg="row">
          <div v-if="!row.uploadImg">--</div>
          <div v-else><a :href="row.uploadImg">下载附件</a></div>
        </template>
        <template #signUploadImg="row">
          <div v-if="!row.signUploadImg">--</div>
          <div v-else><a :href="row.signUploadImg">下载附件</a></div>
        </template>
        <template #status="row">
          <!-- 电子 -->
          <el-tag type="info" v-if="row.type == 1 && row.status == 1"
            >待沟通</el-tag
          >
          <el-tag type="warning" v-if="row.type == 1 && row.status == 2"
            >待签署</el-tag
          >
          <el-tag type="success" v-if="row.type == 1 && row.status == 3"
            >已签署</el-tag
          >
          <el-tag type="danger" v-if="row.type == 1 && row.status == 4"
            >已过期</el-tag
          >
          <!-- 纸质 -->
          <el-tag type="info" v-if="row.type == 2 && row.status == 1"
            >待乙方确认</el-tag
          >
          <el-tag type="warning" v-if="row.type == 2 && row.status == 2"
            >待归档</el-tag
          >
          <el-tag type="success" v-if="row.type == 2 && row.status == 3"
            >已签署</el-tag
          >
          <el-tag type="danger" v-if="row.type == 2 && row.status == 5"
            >审核不通过</el-tag
          >
          <el-tag type="danger" v-if="row.type == 2 && row.status == 6">
            归档审核不通</el-tag
          >
          <el-tag type="warning" v-if="row.type == 2 && row.status == 8"
            >待上传</el-tag
          >
        </template>
        <template #handle="row">
          <!-- 电子 -->
          <span
            v-if="row.type == 1 && row.status == 2"
            class="blue"
            @click="handleMap('sign', row)"
            >签订</span
          >
          <span
            v-if="row.type == 1 && row.status == 3"
            class="blue"
            @click="handleMap('check', row)"
            >查看</span
          >
          <span
            v-if="row.type == 1 && row.status == 3"
            class="blue"
            @click="handleMap('download', row)"
            >下载合同</span
          >
          <!-- 纸质 -->
          <span
            v-if="row.type == 2 && row.status == 5"
            class="blue"
            @click="paperHandleMap('again', row, '2')"
            >重新申请</span
          >
          <span
            v-if="row.type == 2 && (row.status == 8 || row.status == 6)"
            class="blue"
            @click="paperHandleMap('upload', row, '2')"
            >上传合同</span
          >
        </template>
      </ProTable>
    </div>
    <el-dialog v-model="applyDialog" title="申请授信额合同" width="30%">
      <div class="apply-list">
        <div class="apply-item flx-align-center">
          <div class="label">甲方名称:</div>
          <el-input v-model="applyForm.clientContactPerson" />
        </div>
        <div class="apply-item flx-align-center">
          <div class="label">甲方联系人:</div>
          <el-input v-model="applyForm.clientContactPersonName" />
        </div>
        <div class="tips" style="margin-left: 100px; padding-top: 6px">
          需填写企业法定代表人/授权人真实名称
        </div>
        <div class="apply-item flx-align-center">
          <div class="label">甲方联系方式:</div>
          <el-input v-model="applyForm.clientContactPhone" />
        </div>

        <div class="apply-item flx-align-center">
          <div class="label">备注:</div>
          <el-input v-model="applyForm.remark" type="textarea" />
        </div>
      </div>
      <el-checkbox v-model="isCreditConfirm"
        ><span class="check-text"
          >我已阅读并同意<span style="color: #3972fd" @click.stop="openDocs"
            >《逸云数智产品和服务协议》</span
          ></span
        ></el-checkbox
      >
      <template #footer>
        <div class="btns">
          <el-button @click="applyDialog = false">取消</el-button>
          <el-button type="primary" @click="applyCreditContract"
            >确认</el-button
          >
        </div>
      </template>
    </el-dialog>
    <el-dialog
      v-model="reApplyDialog"
      :title="uploadFileType == '1' ? '申请订单合同' : '申请授信额合同'"
      width="30%"
    >
      <div class="apply-list">
        <div class="apply-item flx-align-center">
          <div class="label">甲方名称:</div>
          <el-input v-model="reApplyForm.clientContactPerson" />
        </div>
        <div class="apply-item flx-align-center">
          <div class="label">甲方联系人:</div>
          <el-input v-model="reApplyForm.clientContactPersonName" />
        </div>
        <div class="tips" style="margin-left: 100px; padding-top: 6px">
          需填写企业法定代表人/授权人真实名称
        </div>
        <div class="apply-item flx-align-center">
          <div class="label">甲方联系方式:</div>
          <el-input v-model="reApplyForm.clientContactPhone" />
        </div>

        <div class="apply-item flx-align-center">
          <div class="label">备注:</div>
          <el-input v-model="reApplyForm.remark" type="textarea" />
        </div>
      </div>
      <el-checkbox v-model="isCreditConfirm"
        ><span class="check-text"
          >我已阅读并同意<span style="color: #3972fd" @click.stop="openDocs"
            >《逸云数智产品和服务协议》</span
          ></span
        ></el-checkbox
      >
      <template #footer>
        <div class="btns">
          <el-button @click="reApplyDialog = false">取消</el-button>
          <el-button type="primary" @click="reApplyCreditContract"
            >确认</el-button
          >
        </div>
      </template>
    </el-dialog>
    <el-dialog v-model="uploadDialog" title="上传纸质合同" width="30%">
      <el-upload
        v-model:file-list="fileList"
        class="upload-demo"
        action="#"
        :multiple="false"
        :limit="1"
        :http-request="handleHttpUpload"
      >
        <el-button type="primary">去上传</el-button>
        <template #tip>
          <div class="el-upload__tip">仅支持pdf文件</div>
        </template>
      </el-upload>
      <template #footer>
        <div class="btns">
          <el-button @click="uploadDialog = false">取消</el-button>
          <el-button type="primary" @click="uploadHandle">确认</el-button>
        </div>
      </template>
    </el-dialog>
    <!-- <div class="apply-item flx-align-center" v-if="uploadType === 2">
          <div class="label">附件:</div>
          <el-upload class="upload-demo" :limit="1" v-model:file-list="applyForm.fileList" action="#"
            :on-change="handleFileChange" :auto-upload="false">
            <el-button type="primary">上传文件</el-button>
          </el-upload>
        </div> -->
  </div>
</template>

<script setup lang="ts" name="ContractManagement">
import { UploadFn } from "@/api/Upload";
import {
  createOrderContractApi,
  getOrderContractListApi,
} from "@/api/contract";
import {
  auditCreditOrderAPI,
  getCreditOrderListAPI,
  uploadCreditOrderAPI,
  uploadNormalOrderAPI,
} from "@/api/order";
import { useTable } from "@/hooks/useTable";
import { useUserInfo } from "@/store";
import { toPage } from "@/utils";
import { CirclePlus } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
const userInfo = useUserInfo();
const route = useRoute();
const activeName = ref<TActiveName>("order");
const routeName = ref(route.query.type ? route.query.type.toString() : "order");
if (routeName.value === "credit") {
  activeName.value = "credit";
}
const { tableData, pageData, getList, searchParam, searchFn, resetFn } =
  useTable({
    requestAuto: false,
    requestApi: getOrderContractListApi,
  });

// 产品报价合同
const productColumns: ColumnProps[] = [
  { prop: "", label: "合同编号" },
  { prop: "", label: "价格有效期" },
  { prop: "", label: "创建时间" },
  { prop: "", label: "完成时间" },
  { prop: "status", label: "状态", slot: true },
  { prop: "", label: "备注" },
  { prop: "", label: "操作" },
];
// 订单合同
const orderColumns: ColumnProps[] = [
  { prop: "contractNo", label: "合同编号" },
  { prop: "type", label: "合同介质", slot: true },
  { prop: "createTime", label: "创建时间" },
  { prop: "completeTime", label: "完成时间" },
  { prop: "linkOrderNo", label: "关联订单/账单", slot: true },
  { prop: "status", label: "状态", slot: true },
  { prop: "uploadImg", label: "签署扫描件", slot: true },
  { prop: "signUploadImg", label: "已签署扫描件", slot: true },
  { prop: "remark", label: "备注" },
  { prop: "handle", label: "操作", slot: true },
];
// 搜索
const searchColumns: ColumnProps[] = [
  { prop: "contractNo", label: "合同编号", search: { el: "input" } },
  {
    prop: "startTime",
    label: "创建时间",
    search: {
      el: "date-picker",
      dateType: "daterange",
      dateEnum: ["startTime", "endTime"],
      valueFormat: "YYYY-MM-DD HH:mm:ss",
    },
  },
]; // 产品报价合同搜索
const productSearchColumns: ColumnProps[] = [
  {
    prop: "productTime",
    label: "价格有效期",
    search: {
      el: "date-picker",
      dateType: "daterange",
      dateEnum: ["productStartTime", "productEndTime"],
      valueFormat: "YYYY-MM-DD HH:mm:ss",
    },
  },
];

// 授信额合同
const creditColumns: ColumnProps[] = [
  { prop: "contractNo", label: "合同编号" },
  { prop: "type", label: "合同介质", slot: true },
  { prop: "clientContactPerson", label: "甲方名称" },
  { prop: "clientContactPersonName", label: "甲方联系人" },
  { prop: "clientContactPhone", label: "甲方联系电话" },
  { prop: "amount", label: "金额" },
  { prop: "zq", label: "账期" },
  { prop: "createTime", label: "创建时间" },
  { prop: "completeTime", label: "完成时间" },
  { prop: "status", label: "状态", slot: true },
  { prop: "uploadImg", label: "签署扫描件", slot: true },
  { prop: "signUploadImg", label: "已签署扫描件", slot: true },
  { prop: "remark", label: "备注" },
  { prop: "handle", label: "操作", slot: true },
];
const creditSearchColumns: ColumnProps[] = [
  { prop: "contractNo", label: "合同编号", search: { el: "input" } },
];
const {
  tableData: creditTableData,
  pageData: creditPageData,
  getList: creditGetList,
  searchParam: creditSearchParam,
  searchFn: creditSearchFn,
  resetFn: creditResetFn,
} = useTable({
  requestAuto: false,
  requestApi: getCreditOrderListAPI,
});
const columnsMap = {
  product: {
    columns: productColumns,
    searchColumns: [...productSearchColumns, ...searchColumns],
  },
  order: { columns: orderColumns, searchColumns: searchColumns },
  credit: {},
  architecture: {
    columns: creditColumns,
    searchColumns: creditSearchColumns,
  },
};
type TActiveName = keyof typeof columnsMap;

getList();
creditGetList();
// 创建授信额合同·
const applyDialog = ref(false);

const applyForm = ref<any>({
  clientContactPerson: "",
  clientContactPhone: "",
  clientContactPersonName: "",
  fileList: [],
  remark: "",
});
const isCreditConfirm = ref(false);
watch(
  () => applyDialog.value,
  () => {
    applyForm.value = {
      clientContactPerson: userInfo.companyName ? userInfo.companyName : "",
      clientContactPersonName: userInfo.companyContactName
        ? userInfo.companyContactName
        : "",
      clientContactPhone: userInfo.phone ? userInfo.phone : "",
      remark: "",
    };
  }
);
// 分别上传类型(线上:1\线下:2)
const uploadType = ref(1);
// 上传文件预处理
const handleFileChange = (file: any) => {
  applyForm.value.fileList.push(file.raw);
};
const openApplyDialog = (type: number) => {
  uploadType.value = type;
  applyDialog.value = true;
};
const applyCreditContract = () => {
  if (!isCreditConfirm.value) {
    ElMessage.warning("确认条款信息");
    return;
  }
  if (
    !applyForm.value.clientContactPerson ||
    !applyForm.value.clientContactPhone ||
    applyForm.value.clientContactPerson.trim() === "" ||
    applyForm.value.clientContactPhone.trim() === ""
  ) {
    ElMessage.warning("请填写联系人名称和手机号");
    return;
  }
  // 所有条件都通过验证，执行主要逻辑
  const data = {
    id: applyForm.value.id,
    clientContactPerson: applyForm.value.clientContactPerson,
    clientContactPhone: applyForm.value.clientContactPhone,
    clientContactPersonName: applyForm.value.clientContactPersonName,
    remark: applyForm.value.remark,
    type: uploadType.value,
  };
  auditCreditOrderAPI(data)
    .then((res) => {
      if (res.code === 200) {
        ElMessage.success("申请成功");
        applyDialog.value = false;
        creditGetList();
      } else {
        ElMessage.error(`申请失败: ${res.message || "未知错误"}`);
      }
    })
    .catch((error) => {
      ElMessage.error(`请求失败: ${error.message || "未知错误"}`);
    });
};
const openDocs = () => {
  let a = window.location.origin + "/#/";
  a += "docsView/InformationControl";
  window.open(a, "_blank");
};
const handleClick = () => console.log(activeName.value);
// 表格处理操作
const handleMap = (type: string, row?: any) => {
  if (type === "sign") {
    window.open(row.signUrl, "_blank");
  } else {
    window.open(row.fileDownloadUrl, "_blank");
  }
};
// 纸质合同操作
// 再编辑合同
const reApplyDialog = ref(false);
const uploadDialog = ref(false);
const reApplyForm = ref<any>({
  clientContactPerson: "",
  clientContactPhone: "",
  clientContactPersonName: "",
  remark: "",
});
const fileList = ref();
const fileUrl = ref();
// 1:普通 2:授信
const uploadFileType = ref("1");
const uploadHandle = () => {
  if (uploadFileType.value == "1") {
    uploadNormalOrderAPI({
      id: reApplyForm.value.id,
      signUploadImg: fileUrl.value,
    }).then((res: any) => {
      if (res.code == 200) {
        ElMessage.success("上传成功");
        uploadDialog.value = false;
        getList();
      } else {
        ElMessage.error(`上传失败: ${res.message || "未知错误"}`);
      }
    });
  } else {
    uploadCreditOrderAPI({
      id: reApplyForm.value.id,
      signUploadImg: fileUrl.value,
    }).then((res: any) => {
      if (res.code == 200) {
        ElMessage.success("上传成功");
        uploadDialog.value = false;
        creditGetList();
      } else {
        ElMessage.error(`上传失败: ${res.message || "未知错误"}`);
      }
    });
  }
};
const paperHandleMap = (type: string, row?: any, uploadType?: string) => {
  if (uploadType) {
    uploadFileType.value = uploadType;
  }
  if (type == "again") {
    reApplyForm.value.id = row.id;
    // 订单合同
    if (uploadFileType.value == "1") {
      reApplyForm.value.clientContactPerson = row.clientName;
      reApplyForm.value.clientContactPhone = row.clientContactPhone;
      reApplyForm.value.clientContactPersonName = row.clientContactPerson;
    } else {
      reApplyForm.value.clientContactPerson = row.clientContactPerson;
      reApplyForm.value.clientContactPhone = row.clientContactPhone;
      reApplyForm.value.clientContactPersonName = row.clientContactPersonName;
    }
    // 授信额合同

    reApplyForm.value.remark = row.remark;
    reApplyForm.value.type = row.type;
    reApplyDialog.value = true;
  }
  if (type == "upload") {
    reApplyForm.value.id = row.id;
    uploadDialog.value = true;
  }
};
const reApplyCreditContract = () => {
  if (!isCreditConfirm.value) {
    ElMessage.warning("确认条款信息");
    return;
  }
  if (
    !reApplyForm.value.clientContactPerson ||
    !reApplyForm.value.clientContactPhone ||
    reApplyForm.value.clientContactPerson.trim() === "" ||
    reApplyForm.value.clientContactPhone.trim() === ""
  ) {
    ElMessage.warning("请填写联系人名称和手机号");
    return;
  }
  // 所有条件都通过验证，执行主要逻辑

  if (uploadFileType.value == "1") {
    const data = {
      id: reApplyForm.value.id,
      clientName: reApplyForm.value.clientContactPerson,
      clientContactPhone: reApplyForm.value.clientContactPhone,
      clientContactPerson: reApplyForm.value.clientContactPersonName,
      remark: reApplyForm.value.remark,
      type: reApplyForm.value.type,
    };
    createOrderContractApi(data).then((res: any) => {
      if (res.code == 200) {
        ElMessage.success("申请成功");
        reApplyDialog.value = false;
        getList();
      } else {
        ElMessage.error(`上传失败: ${res.message || "未知错误"}`);
      }
    });
  } else {
    const data = {
      id: reApplyForm.value.id,
      clientContactPerson: reApplyForm.value.clientContactPerson,
      clientContactPhone: reApplyForm.value.clientContactPhone,
      clientContactPersonName: reApplyForm.value.clientContactPersonName,
      remark: reApplyForm.value.remark,
      type: reApplyForm.value.type,
    };
    auditCreditOrderAPI(data)
      .then((res) => {
        if (res.code === 200) {
          ElMessage.success("申请成功");
          reApplyDialog.value = false;
          creditGetList();
        } else {
          ElMessage.error(`申请失败: ${res.message || "未知错误"}`);
        }
      })
      .catch((error) => {
        ElMessage.error(`请求失败: ${error.message || "未知错误"}`);
      });
  }
};
const handleHttpUpload = async (options: UploadRequestOptions) => {
  let formData = new FormData();
  formData.append("file", options.file);
  try {
    const { data } = await UploadFn(formData);
    fileUrl.value = data.url;
  } catch (error) {
    options.onError(error as any);
  }
};

const contract = async () => {
  // toPage('/contractManagement/applyForAContract?type=2')
  ElMessageBox.confirm(`是否关联订单号?`, "温馨提示", {
    confirmButtonText: "是",
    cancelButtonText: "否",
    type: "warning",
    draggable: true,
  })
    .then(() => toPage("/contractManagement/applyForAContract?type=2"))
    .catch(() => toPage("/contractManagement/applyForAContract?type=2&step=1"));
};
</script>
<style lang="scss" scoped>
.blue {
  color: blue;
  margin-right: 5px;

  &:hover {
    cursor: pointer;
  }
}

.apply-list {
  margin-top: 20px;

  .apply-item {
    gap: 20px;
    font-size: 16px;
    margin-top: 10px;

    .label {
      min-width: 110px;
      white-space: nowrap;
    }
  }
}
</style>
