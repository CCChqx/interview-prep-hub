# StudyHub · 秋招备战系统

> 面向自身备考的刷题复习平台 —— 集成 **SM-2 间隔重复算法**、**缓存三防**、**冷热数据分层**，打通「刷题 → 复习」完整学习闭环。

![Java](https://img.shields.io/badge/Java-17-orange)
![SpringBoot](https://img.shields.io/badge/SpringBoot-3.5.16-brightgreen)
![MyBatis-Plus](https://img.shields.io/badge/MyBatis--Plus-3.5.9-blue)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)
![Redis](https://img.shields.io/badge/Redis-7-red)
![License](https://img.shields.io/badge/license-MIT-lightgrey)

---

## 📖 项目简介

备战秋招时，八股资料散落在各个笔记里，**复习进度全靠感觉**——不知道哪些背过、哪些忘了、什么时候该复习。

StudyHub 用**间隔重复算法**解决这个问题：把知识点做成"主动回忆卡片"，按遗忘曲线自动调度复习节奏，并记录完整的学习数据。**从需求、设计到开发、部署，全部独立完成，且已实际自用。**

---

## ✨ 核心特性

### 1️⃣ 刷题 → 复习 学习闭环（SM-2 间隔重复算法）
- **主动回忆卡片**：出题只返回题干（答案遮盖）→ 思考 → 翻看答案 → 按掌握度自评（0-5）
- **SM-2 算法**：EF 难度系数（1.3-2.5）随打分动态调整；复习间隔按 `1→2→4→10→20→55→80` 指数递增（**180 天上限**防溢出）；连续高分自动"毕业"
- **知识点状态流转**：未学 → 学习中 → 已掌握 → 毕业（非法流转拦截，抛业务异常）
- **个性化**：首次打分即参与 EF 计算（难点多练、简单快过）

### 2️⃣ 缓存防护（Cache-Aside + 三大问题）
- **Cache-Aside**：读走缓存、写清缓存（**先更新数据库再删缓存**保证一致性）
- **穿透**：空值标记缓存（`__NULL__`）
- **击穿**：SETNX 互斥锁（只让一个线程查库回填）
- **雪崩**：TTL 随机打散
- **压测验证**：接口平均响应时间 **14ms → 7ms**

### 3️⃣ JWT 双 Token 认证（无感续签）
- **access（30min）+ refresh（7天）分离**：access 访问接口、refresh 换新 access
- **payload 加 type 标记**：拦截器校验类型，**防 refresh 冒充 access**
- **refresh 存 Redis**（TTL 动态取剩余有效期）：支持主动失效 / 登出
- **刷新接口完整校验**：签名 → 类型 → Redis 有效性

### 4️⃣ 接口幂等（自定义注解 + AOP）
- `@Idempotent` 注解 + 切面：Redis SETNX 原子操作（首次放行 / 重复拦截）
- key 设计：业务标识 + 方法 + 参数哈希
- **三重容错**：TTL 防重窗口、**Redis 异常降级放行**、**业务失败删 key**（允许立即重试）

### 5️⃣ 可观测性（AOP 耗时统计 + traceId 全链路）
- **AOP `@Around`**：统计每个接口耗时
- **TraceIdFilter + MDC**：为每个请求生成唯一 traceId，贯穿全链路日志（按 traceId 一键串联）

### 6️⃣ 冷热数据分层（Redis + MySQL）
- **热数据**：打卡走 Redis ZSet（日期成员天然幂等）、学习时长/题数走 INCRBY（原子计数）
- **归档**：定时任务将昨日数据 upsert 到 MySQL（唯一键防重）
- **补偿**：服务启动补偿逻辑，兜底"定时任务漏执行"

### 7️⃣ 批量导入（线程池并发）
- 自定义 `ThreadPoolExecutor`（7 参数 + CallerRunsPolicy 拒绝策略）
- 分批处理 + `CountDownLatch` 等待 + `AtomicInteger` 线程安全计数
- title 幂等去重，`saveBatch` 批量插入 + `rewriteBatchedStatements` 优化

---

## 🛠 技术栈

| 分类 | 技术 |
|---|---|
| 语言/运行时 | JDK 17 |
| 框架 | Spring Boot 3.5.16（Web / Validation / AOP / Data-Redis） |
| 持久层 | MyBatis-Plus 3.5.9 + MySQL 8.0 |
| 缓存 | Redis（StringRedisTemplate） |
| 鉴权 | JJWT 0.12.6（HS 签名 + 双 Token） |
| 接口文档 | Knife4j 4.6.0（OpenAPI3） |
| 工具 | Lombok、Maven、Git、Postman/Apifox |
| 核心算法 | SM-2 间隔重复（纯 Java 实现，与框架解耦） |

---

## 🏗 模块结构

```
┌─────────────────────────────────────────────┐
│  ① 八股知识库（分类 + 知识点 CRUD / 分页）      │
│  ② 学习统计（打卡 / 时长题数 / 归档 / 分层）    │
│  ③ SM-2 复习算法（间隔调度 / 毕业机制）         │
│  ④ 刷题闭环（主动回忆卡片 / 状态机 / 事务）     │
│  ⑤ 鉴权（JWT 双 Token）                       │
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
git clone https://github.com/<your-username>/interview-prep-hub.git
cd interview-prep-hub

# 2. 建库建表（执行建库脚本）
mysql -uroot -p < studyhub建库脚本.sql

# 3. 配置 application.yml
#    - datasource.password：数据库密码（或配置环境变量 DB_PASSWORD）
#    - redis.host / password：Redis 地址

# 4. 启动
mvn spring-boot:run
```


### 使用流程
```
① POST /api/auth/login          登录（拿 access + refresh）
② GET  /api/practice/next       出题（只给题干）
③ GET  /api/practice/{id}/answer   看答案
④ POST /api/practice/answer     自评打分 → 自动纳入复习计划
⑤ GET  /api/review/dueList      查看今日该复习的知识点
```

---

## 📂 项目结构

```
src/main/java/com/studyhub/
├── controller/     控制层
├── service/        业务层（+ impl）
├── mapper/         数据访问层
├── entity/         数据库实体
├── dto/            请求参数对象
├── aspect/         切面（耗时统计 / 幂等）
├── filter/         过滤器（traceId）
├── annotation/     自定义注解（@Idempotent）
├── config/         配置（拦截器 / MyBatis-Plus / Knife4j）
├── util/           工具（JwtUtil / SM2Calculator）
├── exception/      异常体系（全局异常处理）
└── common/         通用返回（Result）
```

---

## 🔍 核心设计说明（技术看点）

| 设计点 | 说明 |
|---|---|
| **SM-2 算法解耦** | 算法封装为纯 Java 工具类（`SM2Calculator`），与框架无关，可单测 |
| **缓存一致性** | 采用"先更新 DB 再删缓存"（而非先删再更）——避免"删缓存后、更库前"被读请求回填旧值导致永久脏数据 |
| **幂等降级策略** | Redis 异常时**降级放行**（防重不能拖垮业务）；业务失败**删 key**（允许立即重试） |
| **双 Token 类型隔离** | payload 的 `type` 字段 + 拦截器校验，防止 refresh token 越权访问业务接口 |
| **事务与并发** | 批量导入用线程池并行（**注意：`@Transactional` 按线程生效，管不到子线程**）；刷题自评用 `@Transactional` 保证多表一致 |
| **分组校验** | `Add` / `Default` 分组，解决"PUT 部分更新被全量校验误杀" |

---

## 📈 性能数据

| 指标 | 优化前 | 优化后 |
|---|---|---|
| 分页接口平均响应时间 | 14ms | **7ms**（Cache-Aside） |

---

## 📌 后续规划

- [ ] 会员充值 + AI 问答（RAG 检索自己的笔记 + 检索评测）
- [ ] 分布式锁（Redisson）+ 接口限流
- [ ] Docker 容器化部署 + Prometheus/Grafana 监控

---

## 👤 作者

- **<CCChqx>** ｜ 2027 届 · Java 后端
- 说明：本项目为解决自身备考痛点而独立开发，持续迭代中，目前项目只有后端，欢迎大家指导。

---

> ⭐ 如果这个项目对你有帮助，欢迎 Star～
