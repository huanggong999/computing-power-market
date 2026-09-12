// import {
//   showFullScreenLoading,
//   tryHideFullScreenLoading,
// } from "@/config/serviceLoading";
import axios, { AxiosRequestConfig, InternalAxiosRequestConfig } from 'axios'
// import { getToken, removeToken } from "./auth";

import router from '@/routes'
import { LOGIN_URL } from '@/config'
import { getToken, removeToken } from './auth'
import { useUserInfo } from '@/store'

// const user = useUserInfo();

axios.defaults.headers['Content-Type'] = 'application/json; charset=utf-8'
export interface CustomAxiosRequestConfig extends InternalAxiosRequestConfig {
  // export interface CustomAxiosRequestConfig extends AxiosRequestConfig {
  noLoading?: boolean
  suppressErrorMessage?: boolean
  /** 公开页面的可选接口失败时，不要清除登录态或强制跳转登录页。 */
  skipAuthRedirect?: boolean
}
export interface NoLoadingType extends AxiosRequestConfig {
  noLoading?: boolean
  suppressErrorMessage?: boolean
  skipAuthRedirect?: boolean
}

const request = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API,
  // baseURL: "http://192.168.1.15:11006",
  timeout: 60 * 1000, // 请求超时时间 60s
})

const getErrorMessage = (data: any) => {
  if (data?.detail) return String(data.detail)

  const rawMessage = data?.msg ?? data?.message ?? data
  if (rawMessage?.detail) return String(rawMessage.detail)

  const message = String(rawMessage || '')
  try {
    const parsed = JSON.parse(message)
    if (parsed?.detail) return String(parsed.detail)
    if (parsed?.msg) return String(parsed.msg)
    if (parsed?.message) return String(parsed.message)
  } catch {}

  const detailMessage = message.match(/"detail"\s*:\s*"([^"]+)"/)
  if (detailMessage?.[1]) return detailMessage[1]

  const resourceMessage = message.match(/资源不足，无法分配\s*\d+\s*张\s*[^"'，。；;]+GPU/)
  return resourceMessage?.[0] || message || '操作失败'
}

// 请求拦截
request.interceptors.request.use(
  (config: CustomAxiosRequestConfig) => {
    const isToken = (config.headers || {}).isToken === false

    // config.noLoading || showFullScreenLoading();
    if (getToken() && !isToken)
      config.headers['Authorization'] = 'Bearer ' + getToken()
    return config
  },
  (err) => Promise.reject(err)
)
// 响应拦截

request.interceptors.response.use(
  (res) => {
    const code = res.data.code || 200
    const msg = getErrorMessage(res.data)
    const config = res.config as CustomAxiosRequestConfig
    // tryHideFullScreenLoading();
    const user = useUserInfo()
    if (code === 200) return res.data
    if (code === 401) {
      if (config.skipAuthRedirect) {
        return Promise.reject(new Error(msg || '登录态无效'))
      }
      ElMessage.error('登陆已过期，请重新登录')
      user.token = ''
      removeToken()

      return router.push(LOGIN_URL)
    }
    // 特殊状态码处理 👆
    const errCodeArr = [403, 500, 501, 1001, 5000]
    if (errCodeArr.includes(code)) {
      if (config.suppressErrorMessage) return Promise.reject(new Error(msg))
      return ElMessage.error(msg), Promise.reject(new Error(msg))
    }
    if (!config.suppressErrorMessage) ElNotification.error({ title: msg })
    return res.data
  },
  (err) => Promise.reject(err)
)

export default request
