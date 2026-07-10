# 🍽️ 一日三餐热量统计

一个简易的全栈 Web 应用，帮助用户记录每日三餐的食物热量，统计摄入总量，并根据性别和年龄给出个性化饮食建议。

## ✨ 功能

- **热量概览** — 每日总热量、三餐分布、营养素占比、饮食建议
- **快速记录** — 选择餐别 → 搜索食物 → 填份量 → 一键保存
- **食物库** — 预置 32 种常见食物，支持搜索、分类筛选、新增、编辑、删除
- **用户系统** — 注册/登录，录入性别年龄，数据按用户隔离
- **个性化建议** — 根据性别和年龄计算推荐热量，给出针对性健康提示

## 🛠 技术栈

| 层级 | 技术 |
|------|------|
| 后端框架 | Spring Boot 2.7.18 |
| ORM | MyBatis-Plus 3.5.5 |
| 数据库 | MySQL 8.0 |
| 前端框架 | Vue 3 + Vite |
| UI 组件库 | Element Plus |
| JDK | Java 8 |

## 🚀 快速启动

### 1. 初始化数据库

确保 MySQL 已运行，执行建表脚本：

```bash
mysql -u root -p < backend/src/main/resources/db/schema.sql
mysql -u root -p < backend/src/main/resources/db/update_v2.sql
```

### 2. 启动后端

```bash
cd backend

# macOS / Linux
MYSQL_PASSWORD=你的密码 mvn spring-boot:run

# Windows (CMD)
set MYSQL_PASSWORD=你的密码 && mvn spring-boot:run
```

后端启动后访问：`http://localhost:8080`

停止后端：`Ctrl + C`

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端启动后访问：`http://localhost:3000`

停止前端：`Ctrl + C`

## ⚙️ 配置说明

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| `server.port` | 8080 | 后端端口 |
| `spring.datasource.url` | `localhost:3306/calorie_tracker` | 数据库连接 |
| `spring.datasource.username` | root | 数据库用户名 |
| `MYSQL_PASSWORD` | 环境变量 | 数据库密码，启动时传入 |

> ⚠️ 密码通过环境变量 `MYSQL_PASSWORD` 传入，不写入配置文件，防止泄漏。

前端端口和代理配置在 `frontend/vite.config.ts` 中修改。

## 📁 项目结构

```
calorie-tracker/
├── README.md
├── 需求文档.md
├── backend/                         # Spring Boot 后端
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/calorie/
│       │   ├── CalorieApplication.java
│       │   ├── config/              # Web 配置 + 登录拦截
│       │   ├── controller/          # REST 接口
│       │   ├── entity/              # 实体类
│       │   ├── mapper/              # MyBatis-Plus Mapper
│       │   └── service/             # 业务逻辑
│       └── resources/
│           ├── application.yml
│           └── db/                  # 建表脚本
└── frontend/                        # Vue 3 前端
    ├── package.json
    ├── vite.config.ts
    └── src/
        ├── api/                     # 接口封装
        ├── router/                  # 路由
        ├── stores/                  # Pinia 状态管理
        ├── types/                   # TypeScript 类型
        └── views/                   # 页面组件
```

## 📝 饮食建议参考

- 成年男性日均推荐 **2250 kcal**，女性 **1800 kcal**
- 根据年龄微调（青少年 80%、老年人 85%）
- 女性 45 岁以上提示补钙，老年人提示少食多餐
