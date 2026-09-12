// ! 区域相关
import request from '@/utils/request'

// 获取区域列表
export const getEnvConfigApi = (config: string): Res<any> =>
  request.get(`/pc/config/getConfig/${config}`)

// 获取数据盘基本配置
export const getDataConfigApi = (region: string): Res<any> =>
  request.get(`/pc/volume/getList`, { params: { region } })
