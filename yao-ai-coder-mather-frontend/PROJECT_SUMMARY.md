# yao-ai-coder-mather-frontend 项目总结

## 一、项目概述

`yao-ai-coder-mather-frontend` 是 **yao-ai-code-mother**（零代码生成平台）的前端子项目，采用 Vue 3 + TypeScript + Vite 技术栈构建，与后端 Spring Boot 服务通过 REST API 进行通信。

| 属性 | 说明 |
|------|------|
| 项目名称 | yao-ai-coder-mather-frontend |
| 版本 | 0.0.0 |
| 页面标题 | 零代码生成平台 |
| 品牌展示名 | 编程导航 |
| 后端服务 | yao-ai-code-mother（端口 8125，上下文路径 `/api`） |
| 开发状态 | 早期脚手架阶段，核心页面与业务功能尚未实现 |

> **说明**：前端目录名中存在拼写差异（`mather` vs `mother`），与后端仓库名 `yao-ai-code-mother` 不一致。

---

## 二、技术栈

### 核心依赖

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue | ^3.5.17 | 前端框架 |
| TypeScript | ~5.8.0 | 类型安全 |
| Vite | ^7.0.0 | 构建工具与开发服务器 |
| Vue Router | ^4.5.1 | 路由管理 |
| Pinia | ^3.0.3 | 状态管理 |
| Ant Design Vue | ^4.2.6 | UI 组件库 |
| Axios | ^1.15.1 | HTTP 请求 |

### 开发工具

| 工具 | 用途 |
|------|------|
| vue-tsc | Vue 单文件组件类型检查 |
| ESLint + Prettier | 代码规范与格式化 |
| @umijs/openapi | 根据 OpenAPI 文档自动生成 API 客户端代码 |
| vite-plugin-vue-devtools | Vue 开发调试工具 |

---

## 三、项目结构

```
yao-ai-coder-mather-frontend/
├── index.html                  # 入口 HTML（标题：零代码生成平台）
├── package.json                # 依赖与脚本配置
├── vite.config.ts              # Vite 配置（路径别名 @ -> src）
├── tsconfig.json               # TypeScript 配置（项目引用模式）
├── openapi2ts.config.ts        # OpenAPI 代码生成配置
├── eslint.config.ts            # ESLint 配置
├── .prettierrc.json            # Prettier 配置
└── src/
    ├── main.ts                 # 应用入口（注册 Pinia、Router、Ant Design Vue）
    ├── App.vue                 # 根组件（挂载 BasicLayout）
    ├── request.ts              # Axios 实例与拦截器
    ├── router/
    │   └── index.ts            # 路由定义
    ├── layouts/
    │   └── BasicLayout.vue     # 基础布局（Header + Content + Footer）
    ├── components/
    │   ├── GlobalHeader.vue    # 全局顶栏（Logo、导航菜单、登录按钮）
    │   ├── GlobalFooter.vue    # 全局页脚
    │   └── icons/              # 图标组件（Vue 脚手架默认）
    ├── pages/
    │   ├── HomePage.vue        # 首页（当前为空）
    │   └── AboutPage.vue       # 关于页
    ├── api/                    # 自动生成的 API 客户端
    │   ├── index.ts
    │   ├── healthController.ts
    │   └── typings.d.ts
    ├── stores/
    │   └── counter.ts          # Pinia 示例 Store（未实际使用）
    └── assets/
        └── logo.svg            # 站点 Logo
```

---

## 四、架构设计

### 4.1 应用启动流程

```
index.html
    └── main.ts
          ├── createPinia()      → 状态管理
          ├── createRouter()     → 路由
          ├── Ant Design Vue     → UI 组件库
          └── mount('#app')
                └── App.vue
                      └── BasicLayout.vue
                            ├── GlobalHeader（顶栏）
                            ├── <router-view>（页面内容区）
                            └── GlobalFooter（页脚）
```

### 4.2 路由配置

| 路径 | 名称 | 组件 | 加载方式 |
|------|------|------|----------|
| `/` | home | HomePage.vue | 同步加载 |
| `/about` | about | AboutPage.vue | 懒加载（代码分割） |

### 4.3 布局体系

采用 **Ant Design Vue Layout** 实现经典三段式布局：

- **Header**：固定高度 64px，包含 Logo、水平导航菜单、登录按钮
- **Content**：灰色背景（`#f0f2f5`），内边距 24px，承载路由页面
- **Footer**：居中展示版权信息

响应式适配：768px 以下隐藏导航菜单、缩小内边距。

### 4.4 HTTP 请求层

`src/request.ts` 封装了统一的 Axios 实例：

```typescript
baseURL: 'http://localhost:8125/api'
timeout: 60000
withCredentials: true
```

**响应拦截器逻辑**：
- 当后端返回 `code === 40100`（未登录）时，弹出「请先登录」提示
- 排除 `user/get/login` 请求和已在登录页的情况
- 自动跳转至 `/user/login?redirect=当前页面 URL`

### 4.5 API 代码生成

通过 `@umijs/openapi` 从后端 Swagger 文档自动生成 TypeScript API 客户端：

```typescript
// openapi2ts.config.ts
{
  requestLibPath: "import request from '@/request'",
  schemaPath: 'http://localhost:8125/api/v3/api-docs',
  serversPath: './src',
}
```

执行 `npm run openapi2ts` 即可重新生成。当前已生成的接口：

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| healthCheck | GET | `/healthCheck` | 健康检查 |

---

## 五、页面与组件说明

### GlobalHeader（全局顶栏）

- 左侧：Logo + 站点标题「编程导航」
- 中间：水平导航菜单（首页、关于），通过 `router.afterEach` 同步选中状态
- 右侧：登录按钮（当前仅为 UI 占位，未绑定逻辑）

### HomePage（首页）

当前为空页面，`<main>` 标签内无内容，待后续实现核心业务功能。

### AboutPage（关于页）

展示项目介绍信息：
- 项目名称：编程导航
- 定位：编程学习导航网站，帮助开发者找到优质学习资源和工具
- 作者：程序员小y
- 联系方式：邮箱与 GitHub（占位信息）

### GlobalFooter（页脚）

展示「原创项目 by 程序员小y」。

---

## 六、后端服务概览

前端对接的后端项目 `yao-ai-code-mother` 位于同级目录，主要信息如下：

| 属性 | 值 |
|------|-----|
| 框架 | Spring Boot 3.5.9 |
| Java 版本 | 21 |
| 服务端口 | 8125 |
| 上下文路径 | `/api` |
| API 文档 | Knife4j（`/api/doc.html`） |
| OpenAPI | `/api/v3/api-docs` |

### 后端技术依赖

- Spring Boot Web
- Hutool 工具库
- Knife4j（Swagger UI）
- MySQL 连接器（已引入，尚未使用）
- Lombok

### 后端通用模块

| 模块 | 说明 |
|------|------|
| `BaseResponse<T>` | 统一响应体（code、data、message） |
| `ResultUtils` | 响应构建工具 |
| `GlobalExceptionHandler` | 全局异常处理 |
| `BusinessException` / `ErrorCode` | 业务异常体系 |
| `CorsConfig` | 跨域配置（允许所有来源、携带 Cookie） |
| `PageRequest` / `DeleteRequest` | 通用请求 DTO |

### 已实现接口

```
GET /api/healthCheck → { code: 0, data: "ok", message: "" }
```

---

## 七、开发与构建

### 环境要求

- Node.js（建议 22.x，与 `@tsconfig/node22` 对应）
- 后端服务运行在 `http://localhost:8125`

### 常用命令

```bash
# 安装依赖
npm install

# 启动开发服务器（支持局域网访问）
npm run dev

# 类型检查 + 生产构建
npm run build

# 预览生产构建产物
npm run preview

# 代码检查与自动修复
npm run lint

# 代码格式化
npm run format

# 根据后端 OpenAPI 文档重新生成 API 客户端
npm run openapi2ts
```

### 推荐 IDE

VS Code + [Volar](https://marketplace.visualstudio.com/items?itemName=Vue.volar) 插件（需禁用 Vetur）。

---

## 八、当前完成度与待办

### 已完成

- [x] Vue 3 + Vite + TypeScript 项目脚手架
- [x] Ant Design Vue UI 框架集成
- [x] 基础布局（Header / Content / Footer）
- [x] 路由配置（首页、关于页）
- [x] Axios 请求封装与登录拦截逻辑
- [x] OpenAPI 自动代码生成流程
- [x] 健康检查 API 对接
- [x] 跨域与 Cookie 支持（前后端均已配置）
- [x] ESLint + Prettier 代码规范

### 待实现

- [ ] 首页核心业务功能（零代码生成 / 编程导航）
- [ ] 用户登录/注册页面与逻辑
- [ ] 登录按钮功能绑定
- [ ] 移除或替换脚手架默认的 counter Store
- [ ] 环境变量配置（API 地址等硬编码问题）
- [ ] 移动端导航体验优化（当前小屏直接隐藏菜单）

---

## 九、关键配置速查

### Vite 路径别名

```typescript
'@' → './src'
```

### API 基础地址

```
http://localhost:8125/api
```

### 未登录错误码

```
40100 → 跳转 /user/login
```

---

## 十、总结

本项目是一个处于**早期开发阶段**的前端应用，已完成基础工程化搭建和 UI 框架集成，具备与后端 Spring Boot 服务通信的能力。项目定位为「零代码生成平台」的前端界面，当前 UI 品牌展示为「编程导航」。

整体架构清晰，采用了业界主流的 Vue 3 生态技术栈，并通过 OpenAPI 实现了前后端 API 的类型安全对接。下一步需要重点实现首页业务功能、用户认证体系，以及完善各页面的实际内容。
