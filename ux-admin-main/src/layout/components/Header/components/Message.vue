<template>
  <el-popover placement="bottom" :width="310" trigger="click">
    <template #reference>
      <el-badge :value="number" :hidden="!number" class="item">
        <i :class="'iconfont icon-xiaoxi'" class="toolBar-icon"></i>
      </el-badge>
    </template>

    <div class="checkMore flx-align-center">
      <el-button link type="primary" @click="readAllMessage">
        一键已读 <span v-if="!!number"> ( {{ number }}) </span>
      </el-button>
      <el-button
        link
        type="primary"
        @click="handleClick('/operate/notifications')"
      >
        查看全部 <el-icon><ArrowRight /></el-icon>
      </el-button>
    </div>

    <div class="message-list">
      <div
        class="message-item"
        v-for="(item, index) in messageList"
        :key="index"
      >
        <div class="message-content">
          <span class="message-title">{{ item.name }}</span>
          <span class="message-date">{{ item.text }}</span>
        </div>
        <div>
          <el-button
            type="primary"
            link
            @click="handleClick(item.url, item.id)"
          >
            查看 <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
      </div>
      <el-empty v-if="!number" description="暂无未读的消息" />
    </div>
  </el-popover>
</template>

<script setup lang="ts" name="Message">
import { getMessagePageApi, readAllMessageApi, readMessageApi } from "@/api";
import { useKeepAlive } from "@/store";

const number = ref(0);
// 消息类型 1  客户表单   2  购买表单  3 推广申请  4 发票申请  5  合同申请  6 IP库预警 7 自建服务器工单 8 续费通知 9 升级通知
const map = [
  {},
  { name: "客户表单", url: "/productManagement/formDataRecovery" },
  { name: "购买表单", url: "/productManagement/consultPurchase" },
  { name: "推广申请", url: "/promotionManagement/promotionAmbassador" },
  { name: "发票申请", url: "/financialCenter/invoicingManagement" },
  { name: "合同申请", url: "/financialCenter/orderContract" },
  { name: "IP库预警", url: "/productManagement/productList" },
  { name: "自建服务器工单", url: "/order/serverTicket" },
  { name: "续费通知", url: "/productManagement/purchaseOrder" },
  { name: "升级通知", url: "/productManagement/purchaseOrder" },
];
const messageList = ref<TKeyValue[]>([]);
// 跳转路由
const router = useRouter();
const route = useRoute();
const keepAliveStore = useKeepAlive();
// 刷新
const handleClick = (path: string, id?: string) => {
  //   跳转页面并打开
  router.push({ path }).then(() => {
    if (!route.meta.noCache) {
      keepAliveStore.removeKeepAliveName(route.name as string);
      nextTick(() => {
        keepAliveStore.addKeepAliveName(route.name as string);
      });
    }
    // 已读消息
    if (!!id) readMessage(id);
  });
};
const timer = ref<any>(null);

const messageCount = ref(0);

// 已读消息
const readMessage = async (msgId: string) => {
  await readMessageApi(msgId);
  getMessage();
};
// 全部已读
const readAllMessage = async () => {
  if (!number.value) return;
  await readAllMessageApi();
  getMessage();
};

const getMessage = async () => {
  const { data } = await getMessagePageApi({ status: 1, pageSize: 5 });
  const { dataTotal, list } = data;
  //  进行语音播报 几条未读消息   当 messageCount.value 与 dataTotal 不一致时进行播报
  if (+dataTotal > messageCount.value && !!+dataTotal) {
    const msg = new SpeechSynthesisUtterance(
      `您有${dataTotal}条未读消息，请及时处理`
    );
    msg.lang = "zh-CN";
    window.speechSynthesis.speak(msg);
  }
  messageCount.value = +dataTotal;

  number.value = +dataTotal;
  messageList.value = list.map((item: any) => {
    return {
      ...item,
      name: map[item.msgType].name,
      url: map[item.msgType].url,
    };
  });
};
getMessage();
//    轮询 每隔5秒请求一次 获取消息数量
onMounted(() => {
  timer.value = setInterval(() => {
    //   获取消息数量
    getMessage();
  }, 300 * 1000);
});
//  //   组件销毁时清除定时器
onBeforeUnmount(() => clearInterval(timer.value));
</script>
<style lang="scss" scoped>
.message-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 260px;
  line-height: 45px;
}
.checkMore {
  // justify-content: end;
  justify-content: space-between;
}
.message-list {
  display: flex;
  flex-direction: column;
  .message-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 20px 0;
    border-bottom: 1px solid var(--el-border-color-light);

    &:last-child {
      border: none;
    }
    .message-content {
      display: flex;
      flex-direction: column;
      .message-title {
        margin-bottom: 5px;
      }
      .message-date {
        font-size: 12px;
        color: var(--el-text-color-secondary);
      }
    }
  }
}
</style>
