# 智能医院管理系统

基于 [RuoYi-vue-pro](https://github.com/YunaiV/ruoyi-vue-pro) 的医院数据管理系统，数据库原理课程设计项目。

## 项目简介

本项目围绕"患者就诊"这条主线，模拟中小型医院的门诊与住院管理流程，覆盖了从挂号、医生接诊、开处方、药品管理到收费结算的完整业务闭环。系统采用前后端分离架构，数据库设计遵循第三范式（3NF），包含完整的 ER 模型和 20+ 张数据表。

## 功能模块

| 模块 | 功能 |
|------|------|
| **用户管理** | 管理员/医生/患者三种角色，菜单级 + 数据级权限隔离 |
| **科室管理** | 科室增删改查，关联医生和病房 |
| **医生管理** | 医生信息维护、归属科室 |
| **患者管理** | 患者档案、就诊历史 |
| **病房床位** | 病房容量管理、床位分配与释放（含并发控制） |
| **就诊记录** | 门诊挂号、医生接诊、电子病历 |
| **处方管理** | 开处方、处方明细、发药状态跟踪 |
| **药品管理** | 药品库存管理、有效期监控 |
| **收费结算** | 账单生成、支付（含防重复支付） |
| **数据统计** | 病房使用率、医生工作量、药品库存预警 |

## 技术栈

| 层级 | 技术 |
|------|------|
| 前端 | Vue 3 + Element Plus + yudao-ui-admin-vue3 |
| 后端 | Spring Boot 2.7 + MyBatis-Plus + RuoYi-vue-pro |
| 数据库 | OpenGauss 5.0 / MySQL 8.0 |
| 缓存 | Redis |

## 项目结构

```
hospital-data-management-system/
├── ruoyi-vue-pro/                     # 后端
│   └── yudao-module-hospital/         # 医院业务模块
│       └── src/main/java/.../hospital/
│           ├── controller/            # API 接口层
│           ├── service/               # 业务逻辑层
│           ├── dal/mysql/             # 数据访问层（Mapper）
│           ├── dal/dataobject/        # 数据对象（DO）
│           ├── enums/                 # 枚举常量
│           └── framework/security/    # 角色权限控制
├── yudao-ui-admin-vue3/               # 前端（Vue 3）
├── sql/                               # 数据库初始化脚本
│   ├── mysql/                         # MySQL 建表 & 初始数据
│   └── opengauss/                     # OpenGauss 建表 & 初始数据
└── 文档/                              # 项目文档
    ├── 智能医院管理系统-总体设计文档.md
    ├── 系统扩展功能设计文档.md
    ├── 启动与测试指南.md
    └── 项目设计展示.html
```

## 文档索引

| 文档 | 说明 |
|------|------|
| [总体设计文档](文档/智能医院管理系统-总体设计文档.md) | 系统架构、ER 图、表结构、模块设计 |
| [扩展功能设计](文档/系统扩展功能设计文档.md) | 知识图谱、AI 辅助诊疗等扩展功能设计 |
| [启动与测试指南](文档/启动与测试指南.md) | 环境搭建、项目启动、本地开发配置 |
| [设计展示](文档/项目设计展示.html) | 项目设计概览的 HTML 展示页面 |

## 快速开始

> 详见 [启动与测试指南](文档/启动与测试指南.md)

**环境要求**：JDK 21 (LTS，兼容 8+) / Maven 3.8+ / Docker（OpenGauss + Redis 或本地 MySQL）/ Node 16+

```bash
# 1. 启动数据库和缓存
docker-compose up -d

# 2. 导入数据库脚本（建表 + 初始数据）
# 在 OpenGauss 中执行 sql/opengauss/ 下的 SQL 文件

# 3. 启动后端（默认端口 48080）
cd ruoyi-vue-pro
mvn clean install -pl yudao-module-hospital
# 在 IDE 中运行 YudaoServerApplication

# 4. 启动前端（默认端口 80）
cd yudao-ui-admin-vue3
npm install && npm run dev
```

**默认登录**：用户名 `admin`，密码 `admin123`，租户 ID `1`

测试账号：

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |
| 医生 | doctor001 | 123456 |
| 患者 | patient001 | 123456 |

## 关于

- **课程**：数据库原理课程设计
- **数据库**：OpenGauss 5.0（华为，兼容 PostgreSQL 语法）
- **设计理念**：遵循第三范式（3NF），20+ 张数据表，完整的 ER 模型，角色级权限隔离
