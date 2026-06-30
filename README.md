# 🧊 冰箱销售电商平台

基于 **Spring Boot 3.2 + Vue 3 + MyBatis-Plus** 的全栈电商系统，实现冰箱商品的在线浏览、购物车、下单、支付、评价等完整购物流程，以及后台商品/订单/用户/权限/数据统计管理。

---

## 📋 项目概述

| 项目 | 说明 |
|------|------|
| 项目名称 | 冰箱销售网站的设计与实现 |
| 技术架构 | 前后端分离 |
| 后端框架 | Spring Boot 3.2.5 + MyBatis-Plus |
| 前端框架 | Vue 3.4 + Element Plus |
| 数据库 | MySQL 8.0 |
| 认证方式 | JWT + BCrypt |

---

## 🛠️ 技术栈

### 后端

- **Spring Boot 3.2.5** — 核心框架
- **MyBatis-Plus** — ORM 持久层
- **MySQL 8.0** — 关系型数据库
- **JWT (jjwt)** — 用户认证与鉴权
- **BCrypt (spring-security-crypto)** — 密码加密
- **Spring Cache + Caffeine** — 本地缓存
- **SpringDoc OpenAPI** — API 文档
- **Hutool** — 工具类库
- **Lombok** — 代码简化

### 前端

- **Vue 3.4** — 渐进式前端框架
- **Vite** — 构建工具
- **Vue Router** — 路由管理
- **Pinia** — 状态管理
- **Element Plus** — UI 组件库
- **Axios** — HTTP 请求
- **ECharts** — 数据可视化

---

## 📁 项目结构

```
fridge-sales-platform/
├── fridge-sales-backend/          # 后端 (Spring Boot)
│   ├── src/main/java/com/fridge/sales/
│   │   ├── annotation/            # 自定义注解
│   │   ├── aspect/                # AOP 切面
│   │   ├── common/                # 通用类（Result、ResultCode、PageResult）
│   │   ├── config/                # 配置类（CORS、缓存、Swagger、MybatisPlus）
│   │   ├── controller/            # 控制器层
│   │   ├── dto/                   # 数据传输对象
│   │   ├── entity/                # 实体类
│   │   ├── interceptor/           # 拦截器（JWT、Admin权限）
│   │   ├── mapper/                # 数据访问层
│   │   ├── service/               # 业务逻辑层
│   │   │   └── impl/              # 业务实现
│   │   └── utils/                 # 工具类（JwtUtil）
│   └── src/main/resources/
│       ├── application.yml        # 配置文件（环境变量）
│       └── mapper/                # MyBatis XML 映射
│
├── fridge-sales-frontend/         # 前端 (Vue 3)
│   ├── src/
│   │   ├── api/                   # API 请求模块
│   │   ├── assets/                # 静态资源
│   │   ├── components/            # 公共组件
│   │   ├── composables/           # 组合式函数
│   │   ├── layout/                # 布局组件
│   │   ├── router/                # 路由配置
│   │   ├── stores/                # Pinia 状态管理
│   │   ├── utils/                 # 工具函数
│   │   └── views/                 # 页面视图
│   │       ├── user/              # 用户端页面
│   │       └── admin/             # 管理端页面
│   └── vite.config.js             # Vite 构建配置
│
└── database/
    └── complete_init.sql          # 数据库初始化脚本
```

---

## ✨ 功能模块

### 用户端

| 模块 | 功能 |
|------|------|
| 首页 | 商品推荐、分类导航、搜索 |
| 商品浏览 | 分类筛选、品牌筛选、排序、分页 |
| 商品详情 | 规格参数、收藏、加购、评价列表 |
| 购物车 | 增删改查、数量调整、结算 |
| 订单 | 创建订单、模拟支付、订单状态流转 |
| 收藏 | 收藏/取消收藏、收藏列表 |
| 收货地址 | 增删改查、默认地址设置 |
| 评价 | 订单评价、评分、评价列表 |
| 个人中心 | 信息修改、密码修改、浏览历史 |

### 管理端

| 模块 | 功能 |
|------|------|
| 数据概览 | 销售额统计、订单统计、用户统计 |
| 商品管理 | 商品 CRUD、上下架、库存管理 |
| 分类管理 | 分类 CRUD、排序 |
| 品牌管理 | 品牌 CRUD |
| 订单管理 | 订单列表、发货、状态管理 |
| 用户管理 | 用户列表、禁用/启用 |
| 评价管理 | 评价审核、隐藏/显示 |
| 权限管理 | 角色/菜单/权限分配 |
| 数据统计 | 销售趋势、分类销售、热销商品 |

---

## 🗄️ 数据库设计

共 **14** 张核心数据表：

| 表名 | 说明 |
|------|------|
| sys_user | 用户表 |
| sys_role | 角色表 |
| sys_permission | 权限/菜单表 |
| sys_user_role | 用户-角色关联表 |
| sys_role_permission | 角色-权限关联表 |
| sys_oper_log | 操作日志表 |
| category | 商品分类表 |
| brand | 品牌表 |
| product | 商品表 |
| cart | 购物车表 |
| orders | 订单表 |
| order_item | 订单详情表 |
| favorite | 收藏表 |
| address | 收货地址表 |
| review | 评价表 |

---

## 🚀 快速开始

### 环境要求

- JDK 17+
- Node.js 16+
- MySQL 8.0+
- Maven 3.8+

### 1. 初始化数据库

```bash
mysql -u root -p < database/complete_init.sql
```

### 2. 配置后端

编辑 `fridge-sales-backend/src/main/resources/application.yml`，或通过环境变量配置：

```bash
export DB_USERNAME=root
export DB_PASSWORD=your_password
export JWT_SECRET=your_jwt_secret_key
```

### 3. 启动后端

```bash
cd fridge-sales-backend
mvn spring-boot:run
```

后端服务启动在 `http://localhost:8080/api`

### 4. 启动前端

```bash
cd fridge-sales-frontend
npm install
npm run dev
```

前端服务启动在 `http://localhost:5173`

### 5. 访问系统

| 入口 | 地址 |
|------|------|
| 用户端 | http://localhost:5173 |
| 管理端 | http://localhost:5173/admin |

### 6. 默认账号

| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 管理员 | admin | 123456 | 数据库初始化自带，首次登录后请修改 |
| 普通用户 | zhangsan | 123456 | 测试账号 |
| 普通用户 | lisi | 123456 | 测试账号 |

> 密码使用 BCrypt 加密存储。管理员可在后台重置用户密码，重置后默认密码为 `123456`。

---

## 🔐 安全设计

- **JWT 认证**：用户登录后签发 Token，请求头携带 `Authorization: Bearer <token>`
- **BCrypt 加密**：用户密码使用 BCrypt 算法加密存储
- **权限拦截**：管理端接口通过 `AdminInterceptor` 校验管理员角色
- **环境变量**：数据库密码、JWT 密钥等敏感信息通过环境变量注入，不硬编码
- **CORS 配置**：跨域请求白名单控制

---

## 📊 订单状态流转

```
待付款(0) → 待发货(1) → 待收货(2) → 已完成(3)
   ↓
已取消(4)
```

---

## 📄 License

本项目仅供学习交流使用。
