<template>
  <div class="page">
    <div class="tip-card flx-align-center" @click="serviceDialog = true">
      <el-icon color="#3972FD"><QuestionFilled /></el-icon
      >推广有疑问？可点击添加管理员微信咨询
    </div>
    <el-dialog title="客服二维码" v-model="serviceDialog" width="200">
      <div class="service flx-center">
        <img class="service" :src="config.qrCode" />
      </div>
    </el-dialog>
    <div class="card">
      <div class="invite-url flx-align-center">
        推广链接:<span class="url">{{ personalInfo.link }}</span
        ><el-tag @click="copyText(personalInfo.link)">复制</el-tag>
      </div>
      <div class="section-bar flx-align-center">
        <el-form :inline="true" :model="searchForm" class="flx-align-center">
          <el-form-item label="客户类型">
            <el-select v-model="searchForm.level" clearable>
              <el-option
                v-for="item in typeOption"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="关联日期">
            <el-date-picker
              v-model="pickerValue"
              is-range
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              clearable
            />
          </el-form-item>

          <el-form-item>
            <div class="btns">
              <el-button type="primary" @click="searchFunc">查询</el-button>
              <el-button @click="initParams">重置</el-button>
              <!-- <el-button type="primary" plain>下载</el-button> -->
            </div>
          </el-form-item>
          <el-form-item class="data">
            <div class="flx-align-center" style="gap: 32px">
              <div class="item">
                推广客户数:
                <span>{{ personalInfo.extendCustomerCount }}个</span>
              </div>
              <div class="item">
                成交客户数: <span>{{ personalInfo.cjCustomerCount }}个</span>
              </div>
            </div>
          </el-form-item>
        </el-form>
      </div>
      <ProTable
        :isPage="false"
        :IsRefresh="false"
        :columns="columns"
        :tableData="tableData"
      >
        <template #info="row">
          <div>{{ row.userName }}</div>
          <div>{{ row.userPhone }}</div>
        </template>
        <template #level="row">
          <div v-if="row.level == 1">新用户</div>
          <div v-if="row.level == 2">激活用户</div>
          <div v-if="row.level == 3">老用户</div>
        </template>
      </ProTable>
      <Pagination
        :page-data="pageData"
        :PageChange="getList"
        :pageSizes="[10, 15, 20, 30]"
      />
    </div>
  </div>
</template>

<script setup lang="ts" name="Console">
import {
  getExtendConfigApi,
  getExtendCustomerListApi,
  getExtendInfoApi,
} from "@/api/partner";
import { useTable } from "@/hooks/useTable";
import { copyText } from "@/utils";
// 客服二维码弹窗
const serviceDialog = ref(false);
const config = ref({
  qrCode: "https://www.baidu.com",
});
const personalInfo = ref<any>({
  link: "",
  extendCustomerCount: 0,
  cjCustomerCount: 0,
});
// 等级选项
const typeOption = [
  {
    label: "新用户",
    value: 1,
  },
  {
    label: "激活用户",
    value: 2,
  },
  {
    label: "老用户",
    value: 3,
  },
];
// 搜索表单
const searchForm = ref<any>({
  level: "",
  startTime: "",
  endTime: "",
});
const pickerValue = ref([]);
// 表格数据
const columns: ColumnProps[] = [
  { label: "用户ID/key", prop: "userId" },
  { label: "用户信息", prop: "info", slot: true },

  { label: "用户类型", prop: "level", slot: true },
  {
    label: "累计佣金(元)",
    prop: "totalCommission",
  },
  {
    label: "关联时间",
    prop: "createTime",
  },
];
const { getList, tableData, pageData, searchFn, searchParam } = useTable({
  requestApi: getExtendCustomerListApi,
  requestAuto: false,
});
const initParams = () => {
  searchParam.value = {};
  searchForm.value = {};
  pickerValue.value = [];
  searchFn();
  pageData.value.pageNo = 1;
};
const searchFunc = () => {
  if (pickerValue.value) {
    searchForm.value.startTime = pickerValue.value[0];
    searchForm.value.endTime = pickerValue.value[1];
  }
  searchParam.value = searchForm.value;
  searchFn();
};
const initPage = () => {
  getExtendConfigApi().then((res: any) => {
    config.value = res.data;
  });
  getExtendInfoApi().then((res: any) => {
    personalInfo.value = res.data;
  });
  getList();
};
initPage();
</script>
<style lang="scss" scoped>
.page {
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 20px;
  position: relative;
  .tip-card {
    gap: 8px;
    height: 60px;
    background: linear-gradient(86deg, #e6eeff 0%, rgba(255, 255, 255, 0) 100%);
    border-radius: 10px 10px 10px 10px;
    border: 1px solid #3972fd;
    font-weight: 400;
    font-size: 18px;
    color: #000;
    padding-left: 15px;
    &:hover {
      cursor: pointer;
    }
  }
  .service {
    widows: 200px;
    height: 200px;
  }
  .card {
    padding: 30px 24px;
    flex: 1;
    .invite-url {
      font-weight: 400;
      font-size: 16px;
      gap: 10px;
      .url {
        font-weight: 400;
        font-size: 16px;
        color: #3972fd;
      }
      .el-tag {
        &:hover {
          cursor: pointer;
        }
      }
    }
    .section-bar {
      margin-top: 30px;
      .el-form {
        width: 100%;
        .el-select {
          width: 200px;
        }
      }
      .data {
        margin-left: auto;
      }
    }
  }
}
</style>
