# StudyHub · 学习复习平台

> 面向长周期、结构化知识备考者的复习调度平台。
> 当前以 Java 面试知识库作为首个内容模板，支持知识点管理、学习统计、SM-2 间隔复习和 JWT 双 Token 认证。

![Java](https://img.shields.io/badge/Java-17-orange)
![SpringBoot](https://img.shields.io/badge/SpringBoot-3.5.16-brightgreen)
![MyBatis-Plus](https://img.shields.io/badge/MyBatis--Plus-3.5.9-blue)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)
![Redis](https://img.shields.io/badge/Redis-7-red)
![License](https://img.shields.io/badge/license-MIT-lightgrey)

---

## 📖 项目简介

备考场景中，知识内容经常散落在笔记、文档和题库里，复习进度依赖感觉，容易出现“背过但忘了、错了还会再错、学习过程没有数据”的问题。

StudyHub 将知识点转化为可调度的复习对象，通过间隔重复算法安排复习节奏，并结合学习统计和认证能力，逐步演进为面向多用户、多领域的内容复习平台。项目当前已实际用于个人备考，并持续进行用户隔离与领域模型重构。

| 文档 | 地址 |
|---|---|
| Knife4j 接口文档 | http://localhost:8080/doc.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |

---

## 🚧 当前进度（2026-09-23）

| 模块 | 状态 | 说明 |
|---|---|---|
| 知识点与分类 | ✅ 已完成 | CRUD、分页、组合筛选、统一返回、全局异常、分组校验 |
| 缓存优化 | ✅ 已完成 | 知识点详情和分页查询使用 Cache-Aside，包含空值缓存、SETNX 和随机 TTL |
| SM-2 复习 | ✅ 已完成 | 评分驱动复习间隔，支持复习上限和毕业状态 |
| Redis 学习统计 | ✅ 已完成 | ZSet 打卡、String 计数、MySQL 日维度归档 |
| JWT 双 Token | ✅ 已完成 | access + refresh 分离，type 校验，refresh 存储 Redis |
| 注册与 BCrypt | ✅ 已完成 | `POST /api/auth/register`、用户查重、BCrypt 密码哈希和 UserVO 返回已闭环 |
| 登录与 Token | ✅ 已完成 | 数据库登录、JWT access/refresh、Redis refresh 存储、refresh 和 logout 已闭环 |
| 当前用户接口 | 🟡 进行中 | 认证上下文已具备，`GET /api/users/me` 待完成 |
| 用户数据隔离 | 🟡 进行中 | `user` 表和 `study_record.user_id` 已落库，Checkin 和统计查询仍待按 userId 改造 |
| 复习状态拆分 | 📌 规划中 | 计划拆分为 `review_state + review_log`，支持状态重算和算法版本化 |
| 导入任务化 | 📌 规划中 | 当前已有线程池批量导入，后续改为任务表 + Worker 的异步流程 |

---

## ✨ 核心特性

### 1️⃣ 刷题 → 复习学习闭环（SM-2 间隔重复算法）

- **主动回忆卡片**：出题只返回题干，用户思考后查看答案并自评 0-5 分。
- **SM-2 调度**：根据 EF 难度系数和评分动态调整下一次复习时间。
- **复习间隔控制**：设置最长复习间隔，避免间隔无限增长，并配合 `mastered` 毕业机制。
- **状态流转**：知识点从学习中进行到已掌握，再由复习评分驱动后续调度。

### 2️⃣ 缓存防护（Cache-Aside）

- **Cache-Aside**：读走缓存，写后删除缓存，降低缓存与数据库的不一致窗口。
- **缓存穿透**：空值标记缓存，避免无效请求持续访问数据库。
- **缓存击穿**：SETNX 互斥锁，只允许一个线程查库回填。
- **缓存雪崩**：TTL 随机打散，降低大量缓存同时失效的风险。
- **性能数据**：分页接口平均响应时间由 14ms 优化至 7ms。

### 3️⃣ 用户与认证

- **真实用户表**：使用 `user` 表管理账号、密码哈希、状态和登录时间。
- **BCrypt 密码哈希**：注册时使用 `PasswordEncoder.encode()`，登录时通过 `matches()` 校验，数据库不存明文密码。
- **JWT access + refresh 双 Token**：access 用于访问业务接口，refresh 用于换取新的 access token。
- **Token 类型隔离**：payload 中使用 `type` 区分 access 和 refresh，防止 refresh token 冒充 access token。
- **Refresh Token 存储**：refresh token 存入 Redis，TTL 根据 token 剩余有效期设置，支持登出时主动失效。

### 4️⃣ 批量导入幂等

- 当前 `@Idempotent` 主要用于批量导入接口。
- 基于 Redis SETNX 拦截重复请求，并设置 TTL 防重窗口。
- Redis 异常时降级放行，业务失败时删除 key，允许立即重试。

### 5️⃣ 可观测性

- **AOP 耗时统计**：统计每个接口的执行耗时。
- **TraceId + MDC**：为每个请求生成唯一 traceId，贯穿日志链路，便于异常定位。

### 6️⃣ Redis + MySQL 冷热数据分层

- **热数据**：打卡使用 Redis ZSet，学习时长和题数使用 Redis String 计数。
- **历史归档**：定时任务将昨日数据归档至 MySQL 日粒度统计表。
- **幂等写入**：`study_record` 使用 `(user_id, record_date)` 唯一索引防止重复归档。
- **当前重构方向**：统计 Key 和查询条件需要从硬编码 userId 迁移到登录用户上下文。

### 7️⃣ 批量导入

- 使用自定义 `ThreadPoolExecutor`、分批处理和计数聚合。
- 基于标题做基础去重，使用 `saveBatch` 和 `rewriteBatchedStatements` 优化批量写入。
- 当前版本仍是同步请求链路，后续计划改为 `import_job + import_row + Worker` 的异步导入任务。

---

## 🛠 技术栈

| 分类 | 技术 |
|---|---|
| 语言/运行时 | JDK 17 |
| 框架 | Spring Boot 3.5.16（Web / Validation / AOP / Data-Redis） |
| 持久层 | MyBatis-Plus 3.5.9 + MySQL 8.0 |
| 缓存 | Redis 7（StringRedisTemplate） |
| 鉴权 | JJWT 0.12.6 + Spring Security Crypto（BCrypt） |
| 接口文档 | Knife4j 4.6.0（OpenAPI3） |
| 工具 | Lombok、Maven、Git、Postman/Apifox |
| 核心算法 | SM-2 间隔重复（纯 Java 实现，与框架解耦） |

---

## 🏗 模块结构

```text
┌─────────────────────────────────────────────┐
│  ① 内容管理（分类 + 知识点 CRUD / 分页）       │
│  ② 学习统计（打卡 / 时长题数 / 归档 / 分层）    │
│  ③ SM-2 复习调度（间隔 / 评分 / 毕业）         │
│  ④ 刷题闭环（主动回忆卡片 / 状态更新）         │
│  ⑤ 用户认证（注册 / 登录 / JWT / 刷新 / 登出）  │
│  ⑥ 横切能力（缓存 / 幂等 / AOP / traceId）      │
└─────────────────────────────────────────────┘
```

---

## 🚀 快速开始

### 环境要求

- JDK 17、Maven 3.9+
- MySQL 8.0、Redis 7

### 步骤

```bash
# 1. 克隆项目
git clone https://github.com/CCChqx/interview-prep-hub.git
cd interview-prep-hub

# 2. 建库建表
mysql -uroot -p < studyhub建库脚本.sql

# 3. 配置 application.yml
#    - datasource.password：数据库密码
#    - redis.host / password：Redis 地址
#    - jwt.secret：JWT 密钥

# 4. 启动
mvn spring-boot:run
```

### 接口文档

项目启动后可通过以下地址查看和调试接口：

| 文档 | 地址 |
|---|---|
| Knife4j 接口文档 | http://localhost:8080/doc.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |

> 如果修改了 `server.port`，请将地址中的 `8080` 替换为实际端口。

### 当前使用流程

```text
① POST /api/auth/login          登录，获取 access + refresh
② GET  /api/practice/next       获取下一个练习知识点
③ GET  /api/practice/{id}/answer 查看答案
④ POST /api/practice/answer     提交自评评分
⑤ GET  /api/review/dueList       查看今日待复习知识点
```

> 注册、登录、刷新和登出接口已经完成基础链路；`GET /api/users/me` 和 Checkin/统计的 userId 隔离仍是下一步工作。

---

## 📂 项目结构

```text
src/main/java/com/studyhub/
├── controller/     控制层
├── converter/      对象转换（Entity -> VO）
├── service/        业务层（+ impl）
├── mapper/         数据访问层
├── pojo/
│   ├── entity/     数据库实体
│   ├── dto/        请求参数对象
│   └── vo/         响应对象
├── aspect/         切面（耗时统计 / 幂等）
├── filter/         过滤器（traceId）
├── annotation/     自定义注解（@Idempotent）
├── config/         配置（拦截器 / MyBatis-Plus / Knife4j / PasswordEncoder）
├── util/           工具（JwtUtil / SM2Calculator）
├── exception/      异常体系（全局异常处理）
└── common/         通用返回（Result）
```

---

## 🔍 核心设计说明

| 设计点 | 说明 |
|---|---|
| **SM-2 算法解耦** | 算法封装为纯 Java 工具类 `SM2Calculator`，与框架解耦，便于单元测试 |
| **缓存一致性** | 采用 Cache-Aside，写后删除缓存并设置随机 TTL，降低旧数据长期驻留的风险 |
| **密码安全** | 使用 BCrypt 单向哈希，数据库只保存 `password_hash`，JWT 不包含密码或哈希 |
| **Token 类型隔离** | 使用 `type=access/refresh`，拦截器只接受 access token，refresh 只能用于换取新 access token |
| **认证职责分层** | Controller 负责 HTTP 适配，AuthService 负责认证编排，JwtUtil 负责签名与解析 |
| **显式对象转换** | 使用 `UserConverter` 控制 Entity 到 VO 的字段映射，避免敏感字段泄露并便于测试 |
| **批量导入** | 当前为同步线程池导入，后续演进为异步任务和断点续跑 |

---

## 📈 性能数据

| 指标 | 优化前 | 优化后 |
|---|---|---|
| 分页接口平均响应时间 | 14ms | **7ms**（Cache-Aside） |

---

## 📌 当前规划

- [x] 建立 `user` 表并引入 BCrypt 密码哈希
- [x] 将 `study_record` 增加 `user_id` 并建立 `(user_id, record_date)` 唯一索引
- [x] 实现注册 Service 和对象转换
- [x] 实现数据库登录和 JWT 双 Token
- [x] 补回并验证 `POST /api/auth/register`
- [ ] 完成 `GET /api/users/me`
- [x] 将登录、刷新、登出逻辑统一收口到 AuthService
- [ ] 将打卡和学习统计的 Redis Key、数据库查询全部改为用户维度
- [ ] 完成两个用户的交叉隔离测试（下一阶段）

## 📌 后续规划

- [ ] `review_state + review_log`：将复习当前状态与不可变事件拆分
- [ ] 乐观锁和请求幂等：防止并发复习评分丢失更新
- [ ] 导入任务化：`import_job + import_row + Worker`
- [ ] Prometheus + Grafana 监控和 Docker 部署
- [ ] 会员充值、AI 问答/RAG 等扩展功能

---

## 👤 作者

- **CCChqx** ｜ 2027 届 · Java 后端
- 说明：本项目为解决自身备考痛点而独立开发，持续迭代中，目前项目只有后端，欢迎大家指导。

---

> ⭐ 如果这个项目对你有帮助，欢迎 Star～


