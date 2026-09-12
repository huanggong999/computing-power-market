<template>
  <div class="model-page">
    <div class="left">
      <div class="title mb14">历史对话</div>
      <el-button
        type="primary"
        :icon="CirclePlus"
        plain
        class="mb20"
        @click="createDialogue"
        >创建新对话</el-button
      >
      <div class="dialogue-list">
        <div
          :class="[
            'dialogue-item',
            activeDialogue === item.id ? 'active-dialogue' : '',
          ]"
          v-for="item in dialogueHistoryList"
          :key="item.id"
          @click="openDetail(item)"
        >
          <img class="dialogue-icon" src="../../assets/icon/message-icon.png" />
          <div class="dialogue-info">
            <div class="title">{{ item.name }}</div>
            <div class="remark">逸云数智AI</div>
          </div>
          <el-popconfirm
            title="确认删除该会话吗?"
            confirm-button-text="确认"
            cancel-button-text="取消"
            @confirm="deleteDialogue(item.id)"
          >
            <template #reference>
              <el-icon
                :style="
                  activeDialogue === item.id ? 'color: white' : 'color:#3972fd'
                "
                @click.stop
                ><CircleCloseFilled
              /></el-icon>
            </template>
          </el-popconfirm>

          <!-- <img class="dialogue-icon" src="../../assets/icon/more-icon.png" /> -->
        </div>
      </div>
    </div>
    <div class="right flx-center">
      <div class="header flx-center">
        <div class="name flx-center">逸云数智AI</div>
      </div>
      <div class="content" ref="content">
        <div class="tip-card">
          <div class="welcome">你好,我是逸云数智AI</div>
          <div class="try">
            您可以试着问我
            <div class="try-btns">
              <div class="try-btn" @click="msg = '写个故事？'">写个故事？</div>
              <div class="try-btn" @click="msg = '世界第一高山是什么？'">
                世界第一高山是什么？
              </div>
              <div class="try-btn" @click="msg = '年终总结怎么写？'">
                年终总结怎么写？
              </div>
            </div>
          </div>
        </div>
        <div class="info-list">
          <div class="info" v-for="item in messageList" :key="item.id">
            <div class="user">
              <div class="font">{{ item.userValue }}</div>
            </div>
            <v-md-preview :text="item.aiValue" height="400"></v-md-preview>
          </div>
        </div>
      </div>
      <div class="input-card flx-align-center">
        <el-input
          v-model="msg"
          :disabled="!flagFinish"
          @keydown.enter="sendHandle"
        >
          <template #suffix>
            <img src="../../assets/icon/input-icon.png" @click="sendHandle" />
          </template>
        </el-input>
      </div>
    </div>
    <div class="bg-div"></div>
  </div>
</template>

<script setup lang="ts" name="aiModel">
import { CirclePlus } from '@element-plus/icons-vue'
import {
  createNewAiApi,
  deleteAiApi,
  getAiListApi,
  getAiMessageListApi,
  getAiReturnMsgApi,
  sendAiMessageApi,
} from '@/api/ai'
import { ElMessage } from 'element-plus'
// 对话列表
const msg = ref('')
const isNew = ref(false)
const dialogueHistoryList = ref<any>([])
const activeDialogue = ref<any>(null)
const content = ref()
const openDetail = (item: any) => {
  if (!flagFinish.value) return
  activeDialogue.value = item.id
}
// 根据不同的对话id获取对话详情
const messageList = ref<any>([])
watch(
  () => activeDialogue.value,
  (newVal) => {
    if (newVal) {
      messageList.value.length = 0
      getAiMessageListApi({
        dialogueId: newVal,
        pageNo: 1,
        pageSize: 100,
      }).then((res) => {
        if (res.data) {
          res.data.list.forEach((item: any) => {
            messageList.value.push(item)
          })
        }
      })
    }
  }
)
// 新增对话(将isNew设置为true,防止过多的新对话)
const createDialogue = () => {
  if (isNew.value || !flagFinish.value) return
  createNewAiApi().then(() => {
    isNew.value = true
    initPage()
  })
}
// 发送信息
const flagId = ref('')
const flagFinish = ref(true)
// 每半秒定时请求接口
const intervalId = ref<any>()

const sendHandle = () => {
  if (msg.value.trim() === '') {
    ElMessage.warning('请输入信息')
    return
  }
  // 将isNew设置为false
  isNew.value = false
  if (!flagFinish.value) {
    return
  }
  sendAiMessageApi({
    dialogueId: activeDialogue.value,
    message: msg.value,
  })
    .then((res) => {
      messageList.value.unshift({
        userValue: msg.value,
        aiValue: '',
      })
      flagId.value = res.data
      intervalId.value = setInterval(() => {
        getAiReturnMsgApi({ id: flagId.value }).then((res) => {
          if (res.data) {
            messageList.value.shift()
            messageList.value.unshift(res.data)
          }
          flagFinish.value = res.data.finish
          content.value.scrollTop = content.value.scrollHeight
          if (res.data.finish) {
            clearInterval(intervalId.value)
          }
        })
      }, 200)
    })
    .finally(() => {
      msg.value = ''
    })
}
const deleteDialogue = (id: any) => {
  deleteAiApi(id).then(() => {
    initPage()
  })
}
// 初始化页面数据
const initPage = () => {
  getAiListApi({ pageNo: 1, pageSize: 10 }).then((res) => {
    dialogueHistoryList.value = res.data.list
    activeDialogue.value = res.data.list[0].id
  })
}
initPage()
</script>
<style lang="scss" scoped>
@import './index.scss';
</style>
