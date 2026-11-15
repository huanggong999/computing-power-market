# AI 代理指导文件

## 项目信息

- **名称**: AI智算门户端 (ux-user-main)
- **技术栈**: Vue 3.4 + TypeScript 5.5 + Vite 5.4 + Element Plus 2.8
- **状态管理**: Pinia 2 + pinia-plugin-persistedstate
- **路由**: Vue Router 4 (Hash 模式)
- **HTTP**: Axios (封装于 src/utils/request.ts)
- **样式**: SCSS + postcss-px-to-viewport (设计稿 1980px)
- **包管理**: npm

## 开发规范

### 编码原则
- 最少代码解决问题，不做推测性实现
- 只触碰必须修改的部分
- 编辑现有代码时遵循现有风格
- 默认不写注释，除非 WHY 非显而易见

### 页面开发
- 页面存放于 `src/view/`，按业务模块组织
- 门户页面（首页、市场等）使用 `Home` 布局
- 控制台页面（云服务器、账单等）使用 `layout` 布局
- 动态路由页面挂载在 `layout` 下

### 组件开发
- 公共组件存放于 `src/components/`，PascalCase 命名
- 使用 `unplugin-vue-components` 自动导入自定义组件
- 使用 `unplugin-auto-import` 自动导入 Vue API
- Element Plus 组件无需手动引入

### API 接口
- 接口文件存放于 `src/api/`，按业务域拆分
- 类型定义存放于 `src/api/types/`
- 统一使用 `request` 实例发起请求

### 样式约定
- 使用 SCSS 编写样式
- px 单位在构建时会自动转为 vw（基于 1980px 设计稿）
- 需要排除转换的类名使用 `ignore-` 前缀

## 常用命令

```bash
npm install    # 安装依赖
npm run dev    # 启动开发服务器
npm run build  # 生产构建
npm run preview # 预览生产构建
```

## 重要约定

- 路由模式：`createWebHashHistory`
- Token 存储：`js-cookie`
- 请求超时：60 秒
- 白名单：`['/aiModel', '/networkProduct', '/networkProductDetail']` 无需登录
- 动态路由：控制台页面通过后端菜单权限动态生成
