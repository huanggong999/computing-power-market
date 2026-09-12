<template>
  <div class="coupon-card" v-if="cardType === 'normal'">
    <div class="title">{{ name }}</div>
    <div class="content">
      {{ couponRemark }}
    </div>
    <div class="price">
      ￥<span class="number">{{ deductionAmount }}</span>
    </div>
    <div class="footer">
      <div class="date">有效期： {{ useTimeStart }} 至 {{ useTimeEnd }}</div>
      <div v-if="showBtn">
        <el-button
          v-if="!customerReceive"
          class="get"
          @click="receive(customerId)"
          >立即领取</el-button
        >
        <el-button v-else class="have" :disabled="true">已领取</el-button>
      </div>
    </div>
  </div>
  <div
    class="large-card"
    v-else-if="cardType === 'large'"
    :class="[selected ? 'selected' : '']"
  >
    <!-- <div class="item flex mb20" v-for="(item, index) in tableData" :key="index"> -->
    <div class="top flx-justify-between">
      <div class="title">{{ enumType('couponType', type) }}</div>
      <div class="price"><span class="unit">￥</span>{{ deductionAmount }}</div>
    </div>
    <div class="flex">
      <div class="left mr20">使用说明</div>
      <div class="right">{{ couponRemark }}</div>
    </div>

    <div class="flx-justify-between btn">
      <div class="flx-center">
        <div class="time-left mr10">有效期:</div>
        <div class="time">
          {{ `${useTimeStart}~${useTimeEnd} ` }}
        </div>
      </div>

      <el-button
        v-if="showBtn"
        :disabled="customerReceive"
        type="primary"
        round
        @click="() => toPage('/console')"
      >
        立即使用
      </el-button>
    </div>
  </div>
  <div class="small-card" v-else-if="cardType === 'small'">
    <div class="top flx-align-center">
      <div class="left flx-center">
        <span class="icon">￥</span>{{ deductionAmount }}
      </div>
      <div class="right">
        <div class="name">{{ enumType('couponType', type) }}</div>
        <div class="time">{{ `${useTimeStart}~${useTimeEnd} ` }}</div>
      </div>
      <!-- 根据需求 -->

      <div class="receive" v-if="btnType === 'receive'">
        <div class="yes"></div>
        <div class="no"></div>
      </div>
      <div class="select" v-else-if="btnType === 'select'">
        <img v-if="!selected" src="@/assets/icon/coupon-no-select.png" />
        <img v-else src="@/assets/icon/coupon-select.png" />
      </div>
    </div>
    <div class="bottom flx-align-center">
      <div class="text">{{ couponRemark }}</div>
    </div>
  </div>
</template>

<script setup lang="ts" name="CouponCard">
import { receiveCouponAPI } from '@/api/accountManagement'

import { toPage } from '@/utils'
import { enumType } from '@/utils/Enum'
const emit = defineEmits(['success'])
interface ICardProps {
  cardType?: string
  type?: string
  name?: string
  title?: string
  couponRemark?: string
  deductionAmount?: number
  useTimeStart?: string
  useTimeEnd?: string
  customerId?: string
  customerReceive?: boolean
  showBtn?: boolean
  selected?: boolean
  btnType?: string // 优惠券场景： 1、 领取页  2、结算页
}
const props = defineProps<ICardProps>()
const receive = async (couponId: string) => {
  receiveCouponAPI(couponId).then((res: any) => {
    if (res.code === 200) {
      ElMessage.success('领取成功')
    } else if (res.code === 401) {
      ElMessage.warning('当前未登录账号，正在为您跳转登录页...')
    } else {
      ElMessage.error('领取失败')
    }
  })
  emit('success')
}
</script>
<style lang="scss" scoped>
.coupon-card {
  position: relative;
  width: 470px;
  height: 195px;
  background-image: url('../../assets/images/couponbg.png');
  background-size: cover;
  /* 或使用 contain 根据需要 */
  background-repeat: no-repeat;
  padding: 20px 20px 14px 20px;
  display: flex;
  flex-direction: column;

  .title {
    font-weight: bold;
    font-size: 22px;
    color: #000;
  }

  .content {
    width: 288px;
    height: 44px;
    margin-top: 12px;
    margin-bottom: 20px;
    font-weight: 400;
    font-size: 16px;
    color: #666666;
  }

  .price {
    position: absolute;
    top: 38px;
    left: 349px;
    font-family: DIN Alternate, DIN Alternate;
    font-weight: bold;
    font-size: 24px;
    color: #3972fd;

    .number {
      font-weight: bold;
      font-size: 44px;
      color: #3972fd;
    }
  }

  .footer {
    width: 430px;
    border-top: 1px solid #dee8ff;
    padding-top: 14px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: auto;

    .date {
      font-weight: 400;
      font-size: 14px;
      color: #666666;
    }

    .get {
      width: 112px;
      height: 41px;
      background: linear-gradient(133deg, #fba201 0%, #ff818c 100%);
      border-radius: 24px 24px 24px 24px;
      font-weight: 400;
      font-size: 16px;
      color: #ffffff;
      text-align: center;
      border: 0;

      &:hover {
        background: linear-gradient(133deg, #fba201 0%, #ff4151 100%);
      }
    }

    .have {
      width: 112px;
      height: 41px;
      background: #cccccc;
      border-radius: 24px 24px 24px 24px;
    }
  }
}

.large-card {
  width: 540px;
  height: 290px;
  margin-right: 20px;
  background: #ffffff;
  box-shadow: 0px 0px 16px 1px rgba(196, 213, 255, 0.5);
  border-top: 8px solid #3972fd;
  border-left: 2px solid #fff;
  border-right: 2px solid #fff;
  border-bottom: 2px solid #fff;
  border-radius: 10px;
  padding: 32px 24px 28px 20px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;

  &:nth-child(3n) {
    margin-right: 0;
  }

  &:hover {
    cursor: pointer;
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

.selected {
  box-shadow: 0px 0px 16px 1px rgba(196, 213, 255, 0.5);
  border-left: 2px solid #3972fd;
  border-right: 2px solid #3972fd;
  border-bottom: 2px solid #3972fd;
}

.small-card {
  width: 367px;
  height: 132px;
  border-top: 8px solid #f4aa2a;
  border-left: 2px solid #fff;
  border-right: 2px solid #fff;
  border-bottom: 2px solid #fff;
  border-radius: 10px;
  background: #ffffff;
  box-shadow: 0px 0px 16px 1px rgba(196, 213, 255, 0.5);
  display: flex;
  flex-direction: column;
  padding: 12px;
  &:hover {
    cursor: pointer;
  }
  .top {
    position: relative;
    gap: 20px;
    border-bottom: 1px solid rgb(232, 232, 232);
    .left {
      color: #ff4151;
      font-weight: bold;
      font-size: 28px;
      padding-bottom: 12px;
      .icon {
        font-size: 16px;
        font-weight: bold;
      }
    }
    .right {
      display: flex;
      flex-direction: column;
      gap: 13px;
      margin-bottom: 10px;

      .name {
        width: 136px;
        height: 28px;
        font-weight: 500;
        font-size: 20px;
        color: #333333;
        line-height: 28px;
        text-align: left;

        overflow: hidden;
        text-overflow: ellipsis; //文本溢出显示省略号
        white-space: nowrap; //文本不会换行
      }
      .time {
        width: 248px;
        height: 20px;
        font-weight: 400;
        font-size: 14px;
        color: #83889d;
        line-height: 20px;
        text-align: left;
        overflow: hidden;
        text-overflow: ellipsis; //文本溢出显示省略号
        white-space: nowrap; //文本不会换行
      }
    }
    .select {
      position: absolute;
      top: 4px;
      left: 300px;
      width: 22px;
      height: 22px;
      img {
        width: 22px;
        height: 22px;
      }
    }
  }
  .bottom {
    padding-top: 12px;
    display: flex;
    align-items: center;
    font-weight: 400;
    font-size: 14px;
    color: #333333;
    text-align: left;
    overflow: hidden;
    text-overflow: ellipsis; //文本溢出显示省略号
    white-space: nowrap; //文本不会换行
  }
}
</style>
