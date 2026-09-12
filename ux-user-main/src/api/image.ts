// ! 镜像相关
import request from '@/utils/request'

// 获取镜像列表
export const getImageListApi = (params: any): Res<any> =>
  request.get(`/pc/image/page`, { params })

// 导入镜像
export const importImageApi = (data: any): Res =>
  request.post(`/pc/image/import`, data)

// 我的镜像列表
export const getMyImageListApi = (): Res<any> =>
  request.get(`/pc/image/mine`)

// 获取我的镜像上传命令
export const getMyImagePushCommandApi = (data: { name: string; tag: string }): Res<any> =>
  request.post(`/pc/image/mine/push-command`, data)

// 获取我的镜像下载命令
export const getMyImagePullCommandApi = (params: { repo: string; tag?: string }): Res<any> =>
  request.get(`/pc/image/mine/pull-command`, { params })

// 删除我的镜像
export const deleteMyImageApi = (params: { repo: string; tag?: string }): Res<any> =>
  request.delete(`/pc/image/mine`, { params })

// ! 镜像仓库data
// 获取镜像仓库列表
export const getImageRepositoryListApi = (data: any): Res<any> =>
  request.post(`/pc/image/repository/list`, data)
// 获取镜像详情
export const getImageRepositoryDetailApi = (instanceName?: any): Res<any> =>
  request.get(`/pc/image/repository/detail/${instanceName}`)
// 获取OCI概览信息
export const getOCIOverviewApi = (instancesName: any): Res<any> =>
  request.get(`/pc/image/repository/oci/overview/${instancesName}`)
// 获取OCI概览信息分页
export const getOCIOverviewListApi = (data: any): Res<any> =>
  request.post(`/pc/image/repository/oci/overview/page`, data)
// 设置仓库实例密码
export const setRepositoryPasswordApi = (data: any): Res =>
  request.post(`/pc/image/repository/set/password`, data)
// 获取镜像指令username
export const getRepositoryUsernameApi = (): Res<any> =>
  request.get(`/pc/image/repository/get/image/username`)
// 删除镜像
export const deleteRepositoryImageApi = (data: any): Res =>
  request.post(`/pc/image/repository/delete/image`, data)
// 判断镜像名称是否存在
export const checkRepositoryImageApi = (imageName: any): Res<any> =>
  request.get(`/pc/image/repository/image/name/exist/${imageName}`)
