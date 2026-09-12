import Cookies from 'js-cookie'

const TokenKey = 'token'

export const getToken = () => Cookies.get(TokenKey)

export const setToken = (token: string) => Cookies.set(TokenKey, token)

export const setUnionId = (unionId: string) => Cookies.set('unionId', unionId)

export const getUnionId = () => Cookies.get('unionId')

export const setOpenId = (openId: string) => Cookies.set('openId', openId)

export const getOpenId = () => Cookies.get('openId')

export const removeUnionId = () => Cookies.remove('unionId')

export const removeOpenId = () => Cookies.remove('openId')

export const removeToken = () => Cookies.remove(TokenKey)

// 检查是否实名