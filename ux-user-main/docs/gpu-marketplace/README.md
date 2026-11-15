# GPU算力市场

一个基于 Vue3 + Ant Design Vue 的 GPU 算力租赁平台前端，类似 AutoDL。

## 功能特性

- 算力市场：浏览和筛选 GPU 实例
- 控制台：管理运行中的实例
- 租用流程：完整的租用、支付流程
- 用户中心：账户管理、充值

## 技术栈

- Vue 3
- Ant Design Vue 4
- Pinia
- Vue Router 4
- Vite

## 快速开始

```bash
# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 构建生产版本
npm run build
```

## 项目结构

```
gpu-marketplace/
├── src/
│   ├── components/      # 组件
│   │   ├── layout/     # 布局组件
│   │   └── market/     # 市场相关组件
│   ├── stores/         # Pinia 状态管理
│   ├── views/          # 页面视图
│   ├── router/         # 路由配置
│   └── main.js         # 入口文件
├── public/             # 静态资源
├── package.json
├── vite.config.js
└── README.md
```

## License

MIT
