import {
  showFullScreenLoading,
  tryHideFullScreenLoading,
} from "@/config/serviceLoading";
import axios, { AxiosRequestConfig, InternalAxiosRequestConfig } from "axios";
import { getToken, removeToken } from "./auth";
import router from "@/router";
import { LOGIN_URL } from "@/config";

axios.defaults.headers["Content-Type"] = "application/json; charset=utf-8";
export interface CustomAxiosRequestConfig extends InternalAxiosRequestConfig {
  // export interface CustomAxiosRequestConfig extends AxiosRequestConfig {
  noLoading?: boolean;
}
export interface NoLoadingType extends AxiosRequestConfig {
  noLoading?: boolean;
}
const request = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API,
  timeout: 10 * 1000, // 请求超时时间 10s
});

// 请求拦截
request.interceptors.request.use(
  (config: CustomAxiosRequestConfig) => {
    const isToken = (config.headers || {}).isToken === false;
    config.noLoading || showFullScreenLoading();
    if (getToken() && !isToken)
      config.headers["Authorization"] = "Bearer " + getToken();
    return config;
  },
  (err) => Promise.reject(err)
);
// 响应拦截

request.interceptors.response.use(
  (res) => {
    const code = res.data.code || 200;
    const msg = res.data.msg;
    tryHideFullScreenLoading();
    if (code === 200) return res.data;
    if (code === 401) {
      ElMessage.error("登陆已过期，请重新登录");
      removeToken();
      location.reload();
      return router.push(LOGIN_URL);
    }
    // 特殊状态码处理 👆
    const errCodeArr = [400, 403, 404, 500, 1001, 1002];
    if (errCodeArr.includes(code))
      return ElMessage.error(msg), Promise.reject(new Error(msg));

    return ElNotification.error({ title: msg }), res.data;
  },
  (err) => {
    tryHideFullScreenLoading();
    const msg =
      err?.response?.data?.msg ||
      err?.response?.data?.message ||
      err?.message ||
      "请求失败";
    ElMessage.error(msg);
    return Promise.reject(new Error(msg));
  }
);

export default request;
