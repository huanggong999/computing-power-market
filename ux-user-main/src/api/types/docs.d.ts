export interface IDocsDetail {
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
   * 文档简介
   */
  intro?: string
  /**
   * 文档介绍
   */
  introduce?: string
  /**
   * 文档名称
   */
  name: string
  /**
   * 序号
   */
  sort?: number
  /**
   * 状态
   */
  status?: Status
  /**
   * 分类id
   */
  typeId?: number
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
  [property: string]: any
}
/**
 * 状态
 */
export enum Status {
  Deactivate = '停用',
  Ok = '正常',
}
