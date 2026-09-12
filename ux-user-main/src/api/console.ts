// ! 控制台
import request from '@/utils/request'

// | /pc/console/home { 获取控制台主页数据 }
export const getHomeDataApi = (): Res => request.get('/pc/console/home')

// | /pc/console/getSourceInfo { 获取资源概览页数据 }
export const getResourceDataApi = (): Res =>
  request.get('/pc/console/getSourceInfo')
