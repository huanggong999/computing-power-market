interface IMenuParams {
  parentId?: number;
}

interface IMenuList {
  path: string; // 菜单路径
  name: string; // 菜单别名
  component: string; // 视图文件路径
  redirect?: string; // 重定向地址
  meta: IMeta; // 菜单信息
  children?: IMenuList[]; // 子菜单
  hidden?: boolean; // 是否隐藏
}
interface IMeta {
  icon: string; // 菜单图标
  title: string; // 菜单标题
  hideMenu: boolean; // 是否隐藏
  activeMenu?: string; // 高亮菜单
  isLink?: boolean; // 是否外链
  isAffix?: boolean; // 是否固定在 tabs nav
  isFull?: boolean; // 是否全屏(示例：数据大屏页面)
  noCache: boolean; // 是否缓存
}
