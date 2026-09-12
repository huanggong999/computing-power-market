/**
 * IModelItem
 */
export interface IModelItem {
  /**`
   * 封面
   */
  cover: string
  /**
   * 创建者
   */
  createBy?: string
  /**
   * 创建者ID
   */
  createById?: number
  /**
   * 创建时间
   */
  createTime?: Date
  /**
   * 删除标志（0代表存在 2代表删除）
   */
  delFlag?: number
  /**
   * 主键
   */
  id?: number
  /**
   * 简介
   */
  intro: string
  /**
   * 介绍
   */
  introduce?: string
  /**
   * 名称
   */
  name: string
  /**
   * 发布者头像
   */
  publishUserAvatar: string
  /**
   * 发布者名称
   */
  publishUserName: string
  /**
   * 序号
   */
  sort: number
  /**
   * 状态
   */
  status: string
  /**
   * 标签
   */
  tags?: string
  /**
   * 类型（1 模型， 2 数据）
   */
  type?: number
  /**
   * 更新者
   */
  updateBy?: string
  /**
   * 更新者Id
   */
  updateById?: number
  /**
   * 更新时间
   */
  updateTime?: Date
  [property: string]: any
}
/**
 * IAppItem
 */
export interface IAppItem {
  /**
   * 创建者
   */
  createBy?: string
  /**
   * 创建者ID
   */
  createById?: number
  /**
   * 创建时间
   */
  createTime: Date
  /**
   * 删除标志（0代表存在 2代表删除）
   */
  delFlag?: number
  /**
   * 主键
   */
  id?: number
  /**
   * 应用简介
   */
  intro: string
  /**
   * 应用介绍
   */
  introduce?: string
  /**
   * 镜像大小
   */
  mirrorSize?: string
  /**
   * 应用名称
   */
  name: string
  /**
   * 发布者头像
   */
  publishUserAvatar: string
  /**
   * 发布者名称
   */
  publishUserName: string
  /**
   * 序号
   */
  sort: number
  /**
   * 状态
   */
  status: string
  /**
   * 应用标签
   */
  tags?: string
  /**
   * 分类id
   */
  typeId: number
  /**
   * 分类名称
   */
  typeName?: string
  /**
   * 分类列表
   */
  typeTypes?: number[]
  /**
   * 更新者
   */
  updateBy?: string
  /**
   * 更新者Id
   */
  updateById?: number
  /**
   * 更新时间
   */
  updateTime?: Date
  /**
   * 应用版本
   */
  version?: string
  [property: string]: any
}
/**
 * IDataItem
 */
export interface IDataItem {
  /**
   * 封面
   */
  cover: string
  /**
   * 创建者
   */
  createBy?: string
  /**
   * 创建者ID
   */
  createById?: number
  /**
   * 创建时间
   */
  createTime?: Date
  /**
   * 删除标志（0代表存在 2代表删除）
   */
  delFlag?: number
  /**
   * 主键
   */
  id?: number
  /**
   * 简介
   */
  intro: string
  /**
   * 介绍
   */
  introduce?: string
  /**
   * 名称
   */
  name: string
  /**
   * 发布者头像
   */
  publishUserAvatar: string
  /**
   * 发布者名称
   */
  publishUserName: string
  /**
   * 序号
   */
  sort: number
  /**
   * 状态
   */
  status: string
  /**
   * 标签
   */
  tags?: string
  /**
   * 类型（1 模型， 2 数据）
   */
  type?: number
  /**
   * 更新者
   */
  updateBy?: string
  /**
   * 更新者Id
   */
  updateById?: number
  /**
   * 更新时间
   */
  updateTime?: Date
  [property: string]: any
}
