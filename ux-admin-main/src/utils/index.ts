/**
 * @description 获取当前时间对应的提示语
 * @returns {String} 当前时间对应的提示语
 */
export const getTimeState = () => {
  let timeNow = new Date();
  let hours = timeNow.getHours();
  if (hours >= 6 && hours <= 10) return `早上好 ⛅`;
  if (hours >= 10 && hours <= 14) return `中午好 🌞`;
  if (hours >= 14 && hours <= 18) return `下午好 🌞`;
  if (hours >= 18 && hours <= 24) return `晚上好 🌛`;
  if (hours >= 0 && hours <= 6) return `凌晨好 🌛`;
};

/**
 * @description 使用递归过滤出需要渲染在左侧菜单的列表 (需剔除 meta.hideMenu == true 的菜单)
 *   */
export const getShowMenuList = (menuList: IMenuList[]) => {
  let newMenuList: IMenuList[] = JSON.parse(JSON.stringify(menuList));
  return newMenuList.filter((item: IMenuList) => {
    item.children?.length && (item.children = getShowMenuList(item.children));
    return !item.hidden;
  });
};
/**
 * @description 路由 path 添加 '/'
 *  */
export const addPath = (menuList: IMenuList[]) => {
  let newMenuList: any[] = JSON.parse(JSON.stringify(menuList));
  const normalizePath = (item: IMenuList, parentPath = "") => {
    item.path = `${parentPath}/${item.path}`.replace(/\/+/g, "/");
    if (item.children && item.children.length > 0) {
      item.children.forEach((el) => normalizePath(el, item.path));
    }
  };
  return newMenuList.filter((item: IMenuList) => {
    normalizePath(item);
    return item;
  });
};

/**
 * @description 使用递归扁平化菜单，方便添加动态路由
 * @param {Array} menuList 菜单列表
 * @returns {Array}
 */
export const getFlatMenuList = (menuList: IMenuList[]): IMenuList[] => {
  let newMenuList: IMenuList[] = JSON.parse(JSON.stringify(menuList));
  return newMenuList.flatMap((item) => [
    item,
    ...(item.children ? getFlatMenuList(item.children) : []),
  ]);
};
/**
 * @description 使用递归找出所有面包屑存储到 pinia 中
 * @param {Array} menuList 菜单列表
 * @param {Array} parent 父级菜单
 * @param {Object} result 处理后的结果
 * @returns {Object}
 */
export const getAllBreadcrumbList = (
  menuList: IMenuList[],
  parent = [],
  result: TKeyValue = {}
) => {
  for (const item of menuList) {
    result[item.path] = [...parent, item];
    if (item.children)
      getAllBreadcrumbList(item.children, result[item.path], result);
  }
  return result;
};

/**
 * @description: 递归处理缓存路由名称
 * @param {Array} menuList 菜单列表
 * @param {Array} keepAliveNameArr 缓存路由名称数组
 * @returns {Array}
 */
export const getKeepAliveRouterName = (
  menuList: IMenuList[],
  keepAliveNameArr: string[] = []
) => {
  menuList.forEach((el) => {
    !el.meta.noCache && el.name && keepAliveNameArr.push(el.name);
    el.children && getKeepAliveRouterName(el.children, keepAliveNameArr);
  });
  return keepAliveNameArr;
};

/**
 * @description 判断数据类型
 * @param {*} val 需要判断类型的数据
 * @returns {String}
 */
export const isType = (val: any) => {
  if (val === null) return "null";
  if (typeof val !== "object") return typeof val;
  else
    return Object.prototype.toString.call(val).slice(8, -1).toLocaleLowerCase();
};
/**
 * @description 浏览器自带API 生成UUID
 * @returns {String}
 */
// export const generateUUID = () => window.crypto.randomUUID();
export const generateUUID = () => {
  let uuid = "";
  for (let i = 0; i < 32; i++) {
    let random = (Math.random() * 16) | 0;
    if (i === 8 || i === 12 || i === 16 || i === 20) uuid += "-";
    uuid += (i === 12 ? 4 : i === 16 ? (random & 3) | 8 : random).toString(16);
  }
  return uuid;
};
