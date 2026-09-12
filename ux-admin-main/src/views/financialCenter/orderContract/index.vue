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
      <template #status="row">
        <el-tag
          v-if="row.type === 1"
          :type="enumTag('creditContractStatusTag', row.status)"
        >
          {{ enumType("contractStatusEnum", row.status) }}
        </el-tag>
        <el-tag
          v-if="row.type === 2"
          :type="enumTag('creditContractStatusTag', row.status)"
        >
          {{ enumType("creditContractAuditStatusEnum", row.status) }}
        </el-tag>
      </template>
      <template #type="row">
        <el-tag :type="row.type === 1 ? 'primary' : 'success'">
          {{ enumType("creditContractTypeEnum", row.type) }}
        </el-tag>
      </template>
      <template #operation="row">
        <div v-if="[1].includes(row.type)">
          <el-link
            :href="row.fileDownloadUrl"
            type="primary"
            v-if="row.status === 3"
          >
            查看合同
          </el-link>
          <el-link
            :href="row.signUrl"
            type="primary"
            target="_blank"
            v-if="row.status === 2"
          >
            去签署
          </el-link>
          <span v-if="[1, 4].includes(row.status)"> -- </span>
        </div>

        <div v-if="[2].includes(row.type)">
          <el-link :href="row.uploadImg" type="primary" v-if="row.status === 3">
            查看合同
          </el-link>
          <el-button
            v-if="[1].includes(row.status)"
            type="primary"
            link
            @click="verify(row.id, 0)"
          >
            上传扫描件
          </el-button>
          <el-button
            v-if="[2].includes(row.status)"
            type="primary"
            link
            @click="verify(row.id, 3)"
          >
            确认归档
          </el-button>
          <el-button
            v-if="[2].includes(row.status)"
            type="primary"
            link
            @click="verify(row.id, 6)"
          >
            驳回
          </el-button>
          <el-button
            v-if="[1].includes(row.status)"
            type="primary"
            link
            @click="verify(row.id, 5)"
          >
            驳回
          </el-button>
          <span v-if="![1, 2, 3].includes(row.status)"> -- </span>
        </div>
      </template>
    </ProTable>

    <el-dialog
      width="30%"
      v-model="verifyVisible"
      :title="dialogEnum.title"
      center
    >
      <div class="flx-align-center mt10">
        <div class="label mr20">{{ dialogEnum.label }}</div>
        <el-input
          v-if="verifyParams.status"
          v-model="verifyParams.verifyRemark"
          type="textarea"
          placeholder="请输入"
          autosize
        />
        <div style="width: calc(100% - 100px)" v-else>
          <UploadFile
            v-model:fileUrl="verifyParams.uploadImg"
            :fileType="fileType"
          >
            <template #tip>
              <div class="tac">请上传.pdf 标准格式文件，大小不超过5MB</div>
            </template>
          </UploadFile>
        </div>
      </div>
      <template #footer>
        <el-button type="primary" @click="confirm">
          {{ dialogEnum.confirmText }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts" name="OrderContract">
import {
  contractPageApi,
  uploadContractApi,
  verifyContractApi,
} from "@/api/financialCenter";
import { useTable } from "@/hooks/useTable";
import { enumType } from "@/utils/Enum";
import { enumTag } from "@/utils/enumTag";

const {
  tableData,
  pageData,
  searchParam,
  refreshFn,
  getList,
  searchFn,
  resetFn,
} = useTable({ api: contractPageApi });
const columns: ColumnProps[] = [
  { prop: "contractNo", label: "合同编号", width: 170 },
  { prop: "type", label: "签署介质", width: 100, slot: true },
  {
    prop: "userType",
    label: "用户类型",
    width: 100,
    value: (row) => enumType("creditContractUserTypeEnum", row.userType),
  },
  { prop: "status", label: "状态", slot: true, width: 130 },
  { prop: "clientName", label: "甲方名称", width: 183 },
  { prop: "clientContactPerson", label: "甲方联系人", width: 120 },
  { prop: "clientContactPhone", label: "甲方联系方式", width: 120 },
  { prop: "clientContactAddress", label: "甲方地址", width: 200 },
  { prop: "completeTime", label: "完成时间", width: 170 },
  { prop: "customerName", label: "用户账号", width: 120 },
  { prop: "customerPhone", label: "用户电话", width: 120 },
  { prop: "remark", label: "备注", width: 100, fixed: "right" },
  { prop: "operation", label: "操作", slot: true, width: 150, fixed: "right" },
];

// 纸质合同
const verifyParams = ref<TKeyValue>({});
const verifyVisible = ref(false);
const fileType = ["application/pdf"];

const verifyAPIFn = async () => {
  await verifyContractApi(verifyParams.value);
  ElMessage.success("操作成功");
  getList();
};
type TVerifyEnum = 0 | 3 | 5 | 6;
// 审核
const verify = async (id: string, status: TVerifyEnum) => {
  verifyParams.value.id = id;
  if (status !== 0) verifyParams.value.status = status;
  if (status === 3) return verifyAPIFn();
  verifyVisible.value = true;
};

// 上传扫描件
const configUpload = async () => {
  if (!verifyParams.value.uploadImg) return ElMessage.error("请上传签署扫描件");
  await uploadContractApi(verifyParams.value);
  getList();
  ElMessage.success("操作成功");
  verifyVisible.value = false;
};

// 驳回
const rejectFn = async () => {
  if (!verifyParams.value.verifyRemark)
    return ElMessage.error("请输入审核意见");
  verifyAPIFn();
  verifyVisible.value = false;
};
const dialogEnum = computed(() => {
  const isReject = !!verifyParams.value.status;
  return {
    title: isReject ? "驳回备注" : "上传扫描件",
    label: isReject ? "备注" : "签署扫描件",
    confirmText: isReject ? "确认驳回" : "确认上传",
    confirmFn: isReject ? rejectFn : configUpload,
  };
});

watch(
  () => verifyVisible.value,
  (val) => {
    if (!val) verifyParams.value = {};
  }
);
const confirm = () => dialogEnum.value.confirmFn();
</script>
<style lang="scss" scoped>
.label {
  flex-shrink: 0;
}
</style>
