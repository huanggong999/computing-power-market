<template>
  <div class="server-card">
    <div class="inner">
      <div class="title">{{ title }}</div>
      <div class="content">{{ content }}</div>
      <div class="info">实例规格: {{ cpuNumber }}核{{ memorySize }}G</div>
      <div class="info">GPU型号: {{ gpuModel }}</div>
      <!-- <div class="info">带宽:{{ bandwidth }}</div> -->
      <div class="footer">
        <div class="price">
          <span
            class="number"
            :style="{
              color: props.payPriceColor ? props.payPriceColor : '#000',
            }"
            >{{ payPriceText }}</span
          >
        </div>
        <el-button
          type="primary"
          @click="create"
          :disabled="!props.zoneList && props.productType == 1"
          >创建实例</el-button
        >
      </div>
      <div class="circle"></div>
    </div>
    <div class="status" v-if="!props.zoneList && props.productType == 1">
      已售罄
    </div>
  </div>
</template>

<script setup lang="ts" name="ServerCard">
import { toPage } from "@/utils";
import { getToken } from "@/utils/auth";
import { ElMessage } from "element-plus";
interface IServerProps {
  id: number;
  title: string;
  content: string;
  specification: string;
  cpuNumber: string | number;
  memorySize: string | number;
  bandwidth: string;
  price: string;
  gpuModel?: string;
  payPriceText?: string;
  payPriceColor?: string;
  // 产品类型（1外部资源 2自建）
  productType?: number;
  // 地域
  regionsZones?: string;
  zoneList: TKeyValue[];
}
const props = defineProps<IServerProps>();
const create = () => {
  const Token = getToken();
  if (!Token) {
    ElMessage.warning("请先登录~");
    toPage("/login");
  } else {
    if (!props.zoneList && props.productType == 1)
      return ElMessage.warning("已售罄");
    if (props.productType) {
      toPage(
        `/cloud/createInstance?ecsId=${props.id}&productType=${props.productType}&regionsZones=${props.regionsZones}`
      );
      return;
    }
    toPage(`/cloud/createInstance?ecsId=${props.id}`);
  }
};
</script>
<style lang="scss" scoped>
.server-card {
  position: relative;
  width: 342px;
  height: 450px;
  border: solid 4px transparent;
  border-radius: 10px;
  background-image: linear-gradient(#fff, #fff),
    linear-gradient(to bottom right, #3972fd, #f1e452);
  background-origin: border-box;
  background-clip: content-box, border-box;

  .status {
    position: absolute;
    right: 20px;
    top: 20px;
    color: red;
  }

  &:hover {
    background: rgb(57, 114, 253);

    .inner {
      .title {
        color: #fff;
      }

      .content {
        color: #fff;
      }

      .info {
        color: #fff;
      }

      .footer {
        .price {
          .number {
            color: #fff;
          }

          color: #fff;
        }

        .el-button {
          background: #ffffff;
          border-radius: 26px 26px 26px 26px;
          border: #fff;
          color: #3972fd;
        }
      }
    }
  }

  .inner {
    height: 100%;
    padding: 49px 30px 30px;
    display: grid;

    .circle {
      position: absolute;
      top: 53%;
      left: 45%;
      width: 180px;
      height: 233px;
      background: linear-gradient(88deg, #f1e452 0%, #79fb9e 50%, #004aff 100%);
      opacity: 0.52;
      filter: blur(50px);
      overflow: hidden;
    }

    .title {
      font-weight: bold;
      font-size: 24px;
      color: #000000;
      margin-bottom: 32px;
    }

    .content {
      font-weight: 400;
      font-size: 16px;
      color: #353535;
      margin-bottom: 59px;
    }

    .info {
      font-weight: bold;
      font-size: 16px;
      color: #000000;
      margin-top: 16px;
    }

    .footer {
      margin-top: auto;
      display: flex;
      justify-content: space-between;
      align-items: center;

      .price {
        font-weight: 400;
        font-size: 16px;
        color: #3972fd;

        .number {
          font-weight: bold;
          font-size: 24px;
          color: #3972fd;
        }
      }

      .el-button {
        width: 124px;
        height: 52px;
        z-index: 2;
        border-radius: 26px 26px 26px 26px;
      }
    }
  }
}
</style>
