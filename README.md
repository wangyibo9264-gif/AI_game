# AI 谣言小镇

这是一个 AI 辅助的恐怖怪谈调查游戏 MVP。后端负责保存固定案件真相、调查阶段、NPC 知识、线索解锁规则、真相报告和评分；AI 只通过受限适配器生成 NPC 回复和报告复盘，不能修改案件的标准真相。

## 技术栈

- 后端：Java 17、Spring Boot 3、Maven、JPA、Flyway、MySQL 8、WebClient
- 前端：Vue 3、Vite、TypeScript、Pinia、Vue Router、Axios、Vitest
- AI：硅基流动 OpenAI 兼容接口，模型配置为 DeepSeek V4 Pro

## 项目结构

```text
backend/   Spring Boot REST API、Flyway 数据迁移、后端测试
frontend/  Vue 3 前端应用、API client、stores、views、前端测试
docs/      Superpowers 需求规格和实现计划
```

## 环境要求

- JDK 17+
- Maven 3.9+
- Node.js 20+
- npm
- MySQL 8，本地运行后端时需要

## MySQL 初始化

创建名为 `rumor_town` 的数据库，并使用 UTF-8 编码：

```sql
CREATE DATABASE rumor_town CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

后端启动时会自动执行 Flyway 迁移，创建表并导入固定案件数据。

## 环境变量

复制 `.env.example`，按本机环境修改配置。`SILICONFLOW_API_KEY` 可以先留空；没有真实 API key 时，后端会使用确定性的 mock AI 回复，方便本地开发和测试。

```text
RUMOR_TOWN_DB_URL=jdbc:mysql://localhost:3306/rumor_town?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
RUMOR_TOWN_DB_USER=root
RUMOR_TOWN_DB_PASSWORD=root
SILICONFLOW_API_KEY=
SILICONFLOW_BASE_URL=https://api.siliconflow.cn/v1
SILICONFLOW_MODEL=deepseek-ai/DeepSeek-V4-Pro
VITE_API_BASE_URL=http://localhost:8080/api
```

## 启动后端

```powershell
cd backend
mvn test
mvn spring-boot:run
```

运行时默认配置位于 `backend/src/main/resources/application.yml`，优先读取环境变量。

## 启动前端

```powershell
cd frontend
npm install
npm run test
npm run build
npm run dev
```

前端 API 地址默认是 `http://localhost:8080/api`，也可以通过 `VITE_API_BASE_URL` 覆盖。

## MVP 流程

1. 创建游客玩家。
2. 打开案件柜。
3. 选择三起固定案件之一并开始调查。
4. 访问已解锁地点。
5. 向 NPC 提问。
6. 查看保存下来的对话记录。
7. 在线索档案中查看已收集线索。
8. 收集足够关键线索后推进调查阶段。
9. 在第 4 阶段提交真相报告。
10. 查看评分、结局、复盘总结和遗漏点。

## 范围说明

- 案件是固定的，由 Flyway 迁移脚本导入。
- 标准真相和怪谈规则含义只由后端掌控。
- NPC 对话只能揭示已授权、已解锁的线索编码。
- 当前使用游客模式，保留用户表，方便后续扩展登录系统。
- 自定义案件生成、完整登录、多玩家、语音、拖拽式线索板不在当前 MVP 范围内。