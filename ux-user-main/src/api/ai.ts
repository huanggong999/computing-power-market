// ! ai
import request from '@/utils/request'

// 新建对话
export const createNewAiApi = () => request.get('/pc/ai/add')

// 对话列表分页
export const getAiListApi = (params: any) =>
  request.get('/pc/ai/page', { params })

// 消息列表分页
export const getAiMessageListApi = (params: any) =>
  request.get('/pc/ai/page-msg', { params })

// 发送消息
// export const sendAiMessageApi = (data: any) =>
//   request.post('/pc/ai/send-msg', data)

// 发送消息
export const sendAiMessageApi = (data: any) =>
  request.post('/pc/ai/send-msg-refresh', data)

// 获取返回消息
export const getAiReturnMsgApi = (params: any) =>
  request.get('/pc/ai/get-send-msg', { params })
// 获取返回消息
export const deleteAiApi = (id: any) =>
  request.get('/pc/ai/delete', { params: { dialogueId: id } })
