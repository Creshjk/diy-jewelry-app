# DIY饰界 —— AI 首饰设计 App

一个面向普通用户的 AI 首饰设计辅助应用：输入关键词即可生成首饰设计图，并完成保存、发布、互动的完整闭环。

## 功能

### 移动端（uni-app）

- 登录 / 注册
- AI 设计生成（文生图 / 图生图）
- 首饰盒：设计历史管理
- 发现广场：作品发布与点赞
- 教程与材料推荐
- 个人中心

### 管理后台（Vue 3 + Element Plus）

- 管理员登录
- 数据看板
- 用户管理
- 设计记录管理
- 作品管理
- 教程 / 材料管理

## 技术栈

| 层 | 技术 |
|---|---|
| 移动端 | uni-app |
| 管理后台 | Vue 3 + Vite + Element Plus |
| 后端 | Spring Boot + MyBatis-Plus |
| 数据库 | MySQL |
| 大模型 | 火山方舟（豆包）文生图 / 图生图 + 文本生成 |

## 目录结构

```
├── admin-web/        管理后台（Vue 3 + Element Plus）
├── backend/          Spring Boot 后端服务
├── mobile-uniapp/    移动端（uni-app）
└── sql/              数据库建表脚本
```

## 核心设计

- **品类自动识别**：扫描用户提示词（中英文关键词），识别项链、耳环、戒指、手链、头饰、脚链六类首饰。
- **差异化提示词模板**：按品类拼接不同画面描述，并加入品类互斥约束，避免生成跑偏。
- **统一全局约束**：电商主图风格、纯净背景、聚焦首饰本体。
- **回退机制**：大模型调用失败时回退示意图与提示语，不向用户暴露底层错误。

## 数据库

10 张业务表：`app_user`、`admin_user`、`ai_design_record`、`gallery_post`、`gallery_like`、`tutorial`、`material_recommendation`、`ai_prompt_recommendation`、`operation_log`、`login_log`。

建表脚本见 `sql/diy_jewelry.sql`。

## 本地运行

### 1. 数据库

```sql
CREATE DATABASE diy_jewelry CHARACTER SET utf8mb4;
-- 导入 sql/diy_jewelry.sql
```

### 2. 后端

```bash
cd backend
mvn spring-boot:run
```

### 3. 管理后台

```bash
cd admin-web
npm install
npm run dev
```

### 4. 移动端

用 HBuilderX 打开 `mobile-uniapp`，运行到浏览器或真机。

## 环境变量

| 变量 | 说明 |
|---|---|
| `ARK_API_KEY` | 火山方舟文本模型 API Key |
| `ARK_IMAGE_API_KEY` | 火山方舟图像模型 API Key |
| `DB_PASSWORD` | 数据库密码（默认 123456） |

> 不要将真实 Key 提交到仓库。本地可将真实值放在 `application-local.yml`（已在 .gitignore 中），需以 `local` profile 启动。

## 开发中遇到的问题

- uni-app 页面兼容问题导致白屏：回退为更稳定的写法。
- MyBatis-Plus 分页 total 恒为 0：补充分页拦截器。
- 大模型返回图片 URL 过长：数据库字段由 VARCHAR 扩容为 TEXT。

## 说明

个人毕业设计项目，代码仅供学习参考。
