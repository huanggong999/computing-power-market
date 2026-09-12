// import * as Cookies from "js-cookie";
import Cookies from "js-cookie";

const TokenKey = "Admin-Token";

export const getToken = () => Cookies.get(TokenKey);
// // 单位 小时
// let expires = new Date(Date.now() + expiresTime * 1000 * 60 * 60);
export const setToken = (token: string) => Cookies.set(TokenKey, token);

export const removeToken = () => Cookies.remove(TokenKey);
