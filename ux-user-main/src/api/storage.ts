// ! 对象存储(桶)相关接口
import request from '@/utils/request'

// 获取桶列表
export const getBucketListAPI = (params: any) =>
  request.get('/pc/bucket/page', { params })
// 创建桶
export const createBucketAPI = (data: any) =>
  request.post('/pc/bucket/create', data)
// 删除桶
export const deleteBucketAPI = (name: any) =>
  request.delete(`/pc/bucket/delete/${name}`)
// 获取桶详情
export const getBucketDetailAPI = (name: any) =>
  request.get(`/pc/bucket/detail/${name}`)
// 查询桶名是否存在
export const checkBucketExistAPI = (name: any) =>
  request.get(`/pc/bucket/check/${name}`)

// ! 对象存储文件相关接口
// 获取桶下文件列表
export const getFileListByIdAPI = (data: any) =>
  request.get('/pc/ossfile/page', { params: data })
// 获取桶下所有文件夹
export const getAllFolderByIdApi = (id: any) =>
  request.get(`/pc/ossfile/getAll`, { params: { id } })
// 新建文件夹
export const createFolderAPI = (data: any) =>
  request.post('/pc/ossfile/addFolder', data)
// 上传文件

export const uploadFileAPI = (data: FormData, params: any): Res<string> =>
  request.post('/pc/ossfile/addFile', data, {
    headers: {
      ['Content-Type']: 'multipart/form-data',
    },
    params,
  })
// 删除文件
export const deleteFileAPI = (id: any) =>
  request.get(`/pc/ossfile/delete`, { params: { id } })
// 移动文件
export const moveFileApi = (data: any) => request.post(`/pc/ossfile/move`, data)
// 删除文件
export const copyFileApi = (data: any) => request.post(`/pc/ossfile/copy`, data)
