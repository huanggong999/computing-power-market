# 项目上下文

## 项目概述

**AI智算项目门户端** — 逸云智算平台 PC 端门户网站。基于 Vue 3 + TypeScript + Element Plus 构建，提供云资源浏览、算力市场、模型市场、应用市场、云服务器管理、容器服务、账单查询、订单管理、活动中心、推广分销等面向用户的功能。

## 技术栈

| 类别 | 技术 |
|------|------|
| 框架 | Vue 3.4.37、TypeScript 5.5.3 |
| 构建工具 | Vite 5.4.1 |
| UI 组件库 | Element Plus 2.8.1 |
| 状态管理 | Pinia 2 + pinia-plugin-persistedstate |
| 路由 | Vue Router 4（Hash 模式） |
| HTTP 客户端 | Axios |
| 国际化 | vue-i18n 10 |
| 图表 | echarts 5.5.1 |
| Markdown 预览 | @kangc/v-md-editor 2.3.18 |
| 表单设计 | @form-create/element-ui 3.2.17 |
| 样式 | Sass/SCSS |
| 适配 | postcss-px-to-viewport（设计稿宽度 1980px） |

## 目录结构

```
src/
├── view/              # 页面视图
├── components/        # 公共组件
├── layout/            # 布局系统
├── routes/            # 路由配置
├── store/             # Pinia 状态管理
├── api/               # API 接口
├── utils/             # 工具函数
├── languages/         # 国际化语言包
├── hooks/             # 组合式函数
├── directives/        # 自定义指令
├── config/            # 全局常量
├── styles/            # 全局样式
├── types/             # 全局类型定义
└── assets/            # 静态资源
```

## 路由架构

- **静态路由**（`routes/index.ts`）：首页容器、登录页、文档页、404
- **动态路由**（`routes/dynamicRouter.ts`）：根据后端菜单权限动态生成
- 门户页面使用 `Home` 布局，控制台页面使用 `layout` 布局

## 适配方案

项目使用 **postcss-px-to-viewport** 进行响应式适配：
- 设计稿宽度：1980px
- 转换单位：vw
- 转换精度：6 位小数
- 排除类名：`ignore-` 前缀的类名不转换

## 环境配置

| 文件 | 说明 |
|------|------|
| `.env.development` | 开发环境配置 |
| `.env.production` | 生产环境配置 |
| `.env` | 通用配置 |

关键环境变量：
- `VITE_APP_BASE_API` — API 基础路径，默认 `/api`
- `VITE_PROXY_URL` — 开发代理目标地址
