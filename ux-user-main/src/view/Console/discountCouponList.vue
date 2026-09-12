/** * 优惠券列表 */
<template>
  <div class="card position-relative">
    <div class="coupon-box flex">
      <div
        class="item flex mb20"
        v-for="(item, index) in tableData"
        :key="index"
      >
        <div class="top flx-justify-between">
          <div class="title">{{ item.name }}</div>
          <div class="price">
            <span class="unit">￥</span>{{ item.deductionAmount }}
          </div>
        </div>
        <div class="flex">
          <div class="left mr20">使用说明</div>
          <div class="right">{{ item.couponRemark }}</div>
        </div>

        <div class="flx-justify-between btn">
          <div class="flx-center">
            <div class="time-left mr10">有效期:</div>
            <div class="time">
              {{ `${item.useTimeStart}~${item.useTimeEnd} ` }}
            </div>
          </div>

          <el-button
            :disabled="item.customerReceive"
            type="primary"
            round
            @click="() => toPage('/console')"
          >
            立即使用
          </el-button>
        </div>
      </div>
    </div>
    <MyEmpty description="暂无优惠券" v-if="!tableData.length" />
    <Pagination
      :page-data="pageData"
      :PageChange="getList"
      :pageSizes="[9, 18, 56, 118]"
    />
  </div>
</template>

<script setup lang="ts" name="DiscountCouponList">
import { getUserCouponListAPI } from '@/api/user'
import { useTable } from '@/hooks/useTable'
import { toPage } from '@/utils'
import { enumType } from '@/utils/Enum'
const { tableData, pageData, getList } = useTable({
  requestApi: getUserCouponListAPI,
  requestAuto: false,
})
pageData.value.pageSize = 9
getList()
</script>
<style lang="scss" scoped>
.card {
  height: calc(100vh - 100px - 100px);
}
.coupon-box {
  overflow: auto;
  flex-wrap: wrap;
  .item {
    width: 540px;
    height: 290px;
    margin-right: 20px;
    background: #ffffff;
    box-shadow: 0px 0px 16px 1px rgba(196, 213, 255, 0.5);
    border-top: 8px solid #3972fd;
    border-radius: 10px;
    padding: 32px 24px 28px 20px;
    flex-direction: column;
    justify-content: space-between;
    &:nth-child(3n) {
      margin-right: 0;
    }
    .top {
      .title {
        font-size: 28px;
      }
      .price {
        color: #fba201;
        font-size: 34px;
        .unit {
          font-size: 18px;
        }
      }
    }
    .time {
      font-size: 16px;
    }
    .time-left {
      width: 60px;
      font-size: 18px;
      color: #83889d;
    }
    .left {
      width: 90px;
      font-size: 18px;
      color: #83889d;
      flex-shrink: 0;
    }
  }
}
</style>
