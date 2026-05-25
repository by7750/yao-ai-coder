# yao-ai-coder-mather-frontend

面向 **yao-ai-coder** 生态的前端应用（页面标题为「零代码生成平台」，界面品牌为「编程导航」）。基于 Vue 3 与 Vite，使用 Ant Design Vue 搭建布局与组件，通过 Axios 调用后端 REST API，并支持从 OpenAPI 文档自动生成 TypeScript 请求代码。

## 技术栈

| 类别 | 技术 |
|------|------|
| 框架 | Vue 3、TypeScript |
| 构建 | Vite 7 |
| 路由 | Vue Router 4 |
| 状态 | Pinia 3 |
| UI | Ant Design Vue 4 |
| HTTP | Axios（统一实例与拦截器） |
| 代码生成 | `@umijs/openapi`（`openapi2ts`） |

## 功能概览

- **布局**：顶栏（Logo、横向菜单、登录入口占位）、内容区、`router-view`、页脚。
- **路由**：`/` 首页（当前为主内容占位）、`/about` 关于页。
- **接口**：启动时在根组件调用 `healthCheck`，用于连通性演示；更多接口可通过 OpenAPI 生成后写入 `src/api/`。
- **鉴权**：响应体 `code === 40100` 时提示登录并跳转 `/user/login?redirect=...`（需后端与同域或 Cookie 策略配合；请求默认 `withCredentials: true`）。

## 环境要求

- **Node.js**：建议 22.x（与 `@tsconfig/node22` 一致）。
- **后端**：默认假定 API 根地址为 `http://localhost:8125/api`，且提供 Swagger/OpenAPI 文档（见下文代码生成）。

## 快速开始

```sh
npm install
npm run dev
```

本地开发服务器由 Vite 启动（默认端口以终端输出为准）。

### 常用脚本

| 命令 | 说明 |
|------|------|
| `npm run dev` | 开发模式，热更新 |
| `npm run build` | 类型检查 + 生产构建 |
| `npm run preview` | 预览构建产物 |
| `npm run type-check` | 仅运行 `vue-tsc` |
| `npm run lint` | ESLint 检查并尝试自动修复 |
| `npm run format` | Prettier 格式化 `src/` |
| `npm run openapi2ts` | 根据 OpenAPI 生成/更新 `src/api` 与类型 |

生产构建前建议执行 `npm run build`，确保类型与打包均通过。

## 后端与 OpenAPI 代码生成

HTTP 客户端集中在 [`src/request.ts`](src/request.ts)：`baseURL` 指向 `http://localhost:8125/api`。若后端地址或路径变更，请同步修改该文件。

代码生成配置见 [`openapi2ts.config.ts`](openapi2ts.config.ts)：

- `schemaPath`：`http://localhost:8125/api/v3/api-docs`（SpringDoc 常见路径，以后端实际为准）。
- `requestLibPath`：使用项目内的 `@/request` 封装。

**生成步骤**：先启动后端并确保文档地址可访问，再执行：

```sh
npm run openapi2ts
```

生成文件会写入 `src/api/`（如 `healthController.ts`、`typings.d.ts`、`index.ts` 等），请勿手工改动生成段落时可优先重新生成。

## 目录结构（简要）

```
src/
├── api/              # OpenAPI 生成的接口与类型
├── assets/           # 静态资源
├── components/       # 全局组件（页眉、页脚、图标等）
├── layouts/          # 页面布局（BasicLayout）
├── pages/            # 路由页面
├── router/           # 路由定义
├── stores/           # Pinia 仓库（含示例 counter）
├── App.vue
├── main.ts
└── request.ts        # Axios 实例与拦截器
```

## 路径别名

`@` 指向 `src/`（见 [`vite.config.ts`](vite.config.ts)）。

## 推荐开发工具

- [VS Code](https://code.visualstudio.com/) + [Vue - Official (Volar)](https://marketplace.visualstudio.com/items?itemName=Vue.volar)
- `.vue` 类型检查使用 `vue-tsc`，而非单独的 `tsc` 处理 SFC

## 参考链接

- [Vite 配置说明](https://vite.dev/config/)
- [Vue 3 文档](https://vuejs.org/)
- [Ant Design Vue](https://antdv.com/)
