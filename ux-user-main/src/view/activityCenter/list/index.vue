<template>
  <div class="activity-page">
    <div class="page-title mb20">最近活动</div>
    <div class="activity-box position-relative">
      <div class="activity-card" v-for="item in activityList" :key="item.id">
        <img class="activity-img" :src="item.picture" />
        <div class="activity-info">
          <div class="title">{{ item.name }}</div>
          <div class="url-line flx-align-center" v-if="item.link">
            <div class="url">{{ item.link }}</div>
            <div class="copy" @click="copyText(item.link)">复制</div>
          </div>
        </div>
        <div class="activity-footer flx-center">
          <div class="btn flx-center" @click="checkActivity(item)">
            <el-icon><View /></el-icon>查看活动
          </div>
          <div class="btn flx-center" @click="checkParticipation(item)">
            <el-icon><Document /></el-icon>参与情况
          </div>
        </div>
      </div>
      <el-dialog
        title="参与情况"
        v-model="conditionDialog"
        style="width: 950px"
      >
        <TipText class="fwb mt15 mb15" content="基础信息" fontSize="16" />
        <div class="card info-card mb15">
          <el-table
            :data="tableData"
            style="width: 100%"
            :cell-style="{
              textAlign: 'center',
              fontSize: '13px',
            }"
            :header-cell-style="{
              textAlign: 'center',
              fontSize: '13px',
            }"
          >
            <el-table-column type="index" width="50" />
            <el-table-column
              prop="invitationTime"
              label="邀请时间"
              width="180"
            />
            <el-table-column prop="registerUserId" label="注册用户ID/Key" />
            <el-table-column prop="registerUserName" label="注册用户信息">
              <template #default="{ row }">
                <div>
                  {{ row.registerUsername }}
                </div>
                <div>
                  {{ row.registerUserPhone }}
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="orderNo" label="关联订单号" width="180" />
          </el-table>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script setup lang="ts" name="VoucherCollectionCenter">
import { copyText, toPage } from '@/utils/index'
import { getActivityListApi, getActivityRecordApi } from '@/api/activity'
import { getToken } from '@/utils/auth'
import { ElMessage } from 'element-plus'

// 活动列表
const activityList = ref<any>([])

// 查看活动详情
const checkActivity = (item: any) => {
  toPage(`/activityCenter/detail?id=${item.id}`)
}
// 活动参与弹窗
const conditionDialog = ref(false)
// 查看活动参与情况
const tableData = ref<any>([])
const checkParticipation = (item: any) => {
  // 查询用户是否登录
  if (getToken()) {
    getActivityRecordApi({ activeId: item.id }).then((res) => {
      tableData.value = res.data.list
    })
    conditionDialog.value = true
  } else {
    ElMessage.warning('账号未登录，正在为您跳转登录页...')
    toPage('/login')
  }
}

// 初始化页面
const initPage = () => {
  getActivityListApi().then((res) => {
    activityList.value = res.data
  })
}
initPage()
</script>
<style lang="scss" scoped>
.activity-page {
  background: url('@/assets/images/app-top.png') no-repeat;

  padding: 150px 225px 50px 225px;
  margin-bottom: -50px;
  min-height: 100vh;
  .page-title {
    font-weight: bold;
    font-size: 24px;
    color: #000000;
  }
  .activity-box {
    height: 100%;
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 22px;
    .activity-card {
      width: 470px;
      height: 371px;
      border-radius: 10px 10px 10px 10px;
      border: 1px solid #d9d9d9;
      display: flex;
      flex-direction: column;
      .activity-img {
        width: 100%;
        height: 238px;
        background: #d9d9d9;
        border-radius: 10px 10px 0px 0px;
      }
      .activity-info {
        flex: 1;
        display: flex;
        flex-direction: column;
        justify-content: space-evenly;
        padding: 0px 12px;
        .title {
          width: 100%;
          font-weight: 500;
          font-size: 18px;
          color: #333333;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
        }
        .url-line {
          width: 350px;
          .url {
            width: 90%;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
            font-weight: 400;
            font-size: 16px;
            color: #83889d;
          }
          .copy {
            font-weight: 400;
            font-size: 16px;
            color: #3972fd;
            &:hover {
              cursor: pointer;
            }
          }
        }
      }
      .activity-footer {
        width: 100%;
        height: 42px;
        background: #3972fd;
        border-radius: 0px 0px 10px 10px;
        .btn {
          width: 50%;
          font-weight: 500;
          font-size: 16px;
          color: #ffffff;
          &:hover {
            cursor: pointer;
          }
          :nth-child(1) {
            border-right: 0;
            margin-right: 3px;
            font-size: 18px;
          }
        }
        :nth-child(1) {
          border-right: 3px solid #fff;
        }
      }
    }
    .info-card {
      width: 1190px;
      height: 421px;
      padding: 0;
      /* background: linear-gradient(180deg, #fff9ee 0%, #ffffff 100%); */
      box-shadow: 0px 0px 16px 1px rgba(181, 181, 181, 0.1);
      border-radius: 8px 8px 8px 8px;
      overflow: auto;
    }
    .content-card {
      .content {
        background: #f7f8fb;
        border-radius: 8px 8px 8px 8px;
      }
    }
  }
}
</style>
