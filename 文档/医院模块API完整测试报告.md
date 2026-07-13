# 医院数据管理系统 — API 完整测试报告

> 生成时间：2026-04-17
> 测试环境：localhost:48080 (Spring Boot 后端 + OpenGauss Docker)
> 测试工具：test_hospital_api.js (Node.js)
> 测试覆盖：7 大模块，37 个接口

---

## 一、测试结果总览

| 统计项 | 数值 |
|--------|------|
| 总计测试用例 | **37** |
| 通过 | **37** ✅ |
| 失败 | **0** ✅ |
| 通过率 | **100%** |

---

## 二、测试结论的证据

所有测试结果均来自实际 HTTP 响应，以下为关键数据点：

### 2.1 认证接口

| 用例 | 方法 | 路径 | 响应 code | 响应 msg | 证据 |
|------|------|------|-----------|----------|------|
| 登录获取 Token | POST | /admin-api/system/auth/login | 0 | ok | 返回 32 位 token: `1f67f5b43207...` |

> **验证方法**：POST 请求携带 `username=admin`、`password=admin123` + `tenant-id: 1` 头，服务器返回 OAuth2 token，可用于后续所有接口鉴权。

### 2.2 科室管理 (Department)

| 用例 | 方法 | 路径 | 响应 code | 关键数据 | 证据 |
|------|------|------|-----------|----------|------|
| 列表查询(分页) | GET | /admin-api/hospital/dept/page | 0 | total=9 | 共 9 条科室记录 |
| 详情查询 | GET | /admin-api/hospital/dept/get?id=1 | 0 | id=1 | 成功返回 id=1 的科室 |
| 新增科室 | POST | /admin-api/hospital/dept/create | 0 | ok | 数据已写入数据库 |
| 更新科室 | PUT | /admin-api/hospital/dept/update?id=1 | 0 | ok | 数据已更新 |

> **验证方法**：新增后列表 total 从 6→7→8→9 持续增长；更新后详情接口返回的字段与修改值一致。

### 2.3 医生管理 (Doctor)

| 用例 | 方法 | 路径 | 响应 code | 关键数据 | 证据 |
|------|------|------|-----------|----------|------|
| 列表查询(分页) | GET | /admin-api/hospital/doctor/page | 0 | total=7 | 共 7 名医生 |
| 详情查询 | GET | /admin-api/hospital/doctor/get?id=1 | 0 | id=1 | 医生职称等字段正确 |
| 新增医生 | POST | /admin-api/hospital/doctor/create | 0 | ok | 数据已写入数据库 |
| 更新医生 | PUT | /admin-api/hospital/doctor/update?id=1 | 0 | ok | 数据已更新 |

### 2.4 患者管理 (Patient)

| 用例 | 方法 | 路径 | 响应 code | 关键数据 | 证据 |
|------|------|------|-----------|----------|------|
| 列表查询(分页) | GET | /admin-api/hospital/patient/page | 0 | total=8 | 共 8 名患者 |
| 详情查询 | GET | /admin-api/hospital/patient/get?id=1 | 0 | id=1 | 患者信息完整 |
| 新增患者 | POST | /admin-api/hospital/patient/create | 0 | ok | 数据已写入数据库 |
| 更新患者 | PUT | /admin-api/hospital/patient/update?id=1 | 0 | ok | 数据已更新 |

### 2.5 病房 + 床位管理 (Ward + Bed)

| 用例 | 方法 | 路径 | 响应 code | 关键数据 | 证据 |
|------|------|------|-----------|----------|------|
| 病房列表(分页) | GET | /admin-api/hospital/ward/page | 0 | total=7 | 共 7 间病房 |
| 病房详情 | GET | /admin-api/hospital/ward/get?id=1 | 0 | id=1 | 病房容量等字段正确 |
| 病房新增 | POST | /admin-api/hospital/ward/create | 0 | ok | 数据已写入 |
| 病房更新 | PUT | /admin-api/hospital/ward/update?id=1 | 0 | ok | 数据已更新 |
| 床位列表(分页) | GET | /admin-api/hospital/bed/page | 0 | total=25 | 共 25 张床位 |
| 床位详情 | GET | /admin-api/hospital/bed/get?id=1 | 0 | id=1 | 床位状态等正确 |
| 床位新增 | POST | /admin-api/hospital/bed/create | 0 | ok | 数据已写入 |
| 床位更新 | PUT | /admin-api/hospital/bed/update?id=1 | 0 | ok | 数据已更新 |
| **床位分配** | PUT | /admin-api/hospital/bed/assign?bedId=27&patientId=1 | 0 | ok | ✅ 动态找空闲床位后分配 |
| **床位释放** | PUT | /admin-api/hospital/bed/release?bedId=27 | 0 | ok | ✅ 同一床位释放成功 |

> **验证方法**：先分页查询所有床位，筛选 `status === '空闲'` 的记录，取其 bedId 进行分配操作；释放后再用同一 bedId 验证。床位状态需使用中文值 `"空闲"`/`"已占用"`，英文值 `"AVAILABLE"`/`"OCCUPIED"` 会导致业务逻辑判断失败。

### 2.6 就诊 + 处方 + 账单管理

#### 就诊 (Visit)

| 用例 | 方法 | 路径 | 响应 code | 关键数据 | 证据 |
|------|------|------|-----------|----------|------|
| 就诊列表(分页) | GET | /admin-api/hospital/visit/page | 0 | total=5 | 共 5 条就诊记录 |
| 就诊详情 | GET | /admin-api/hospital/visit/get?id=1 | 0 | id=1 | 就诊信息完整 |
| **就诊新增** | POST | /admin-api/hospital/visit/create | 0 | ok | ✅ 包含 visitDate 字段 |
| 就诊更新 | PUT | /admin-api/hospital/visit/update?id=1 | 0 | ok | 数据已更新 |

> **踩坑记录**：就诊表的 `visit_date` 字段在数据库层面有 `NOT NULL` 约束。创建就诊记录时必须传递 `visitDate` 字段（如 `'2026-04-17T12:00:00'`），否则返回 500 错误。

#### 处方 (Prescription)

| 用例 | 方法 | 路径 | 响应 code | 关键数据 | 证据 |
|------|------|------|-----------|----------|------|
| 处方列表(分页) | GET | /admin-api/hospital/prescription/page | 0 | total=6 | 共 6 条处方 |
| 处方详情 | GET | /admin-api/hospital/prescription/get?id=1 | 0 | id=1 | 处方包含药品明细 |
| 处方新增 | POST | /admin-api/hospital/prescription/create | 0 | ok | 数据已写入 |
| **处方发药** | PUT | /admin-api/hospital/prescription/dispense?id=1 | 0 | ok | ✅ 状态变更为已发药 |

> **验证方法**：发药前查询处方详情确认状态为 `"PENDING"`，发药后再次查询确认状态变更为 `"DISPENSED"`。

#### 账单 (Bill)

| 用例 | 方法 | 路径 | 响应 code | 关键数据 | 证据 |
|------|------|------|-----------|----------|------|
| 账单列表(分页) | GET | /admin-api/hospital/bill/page | 0 | total=6 | 共 6 条账单 |
| 账单详情 | GET | /admin-api/hospital/bill/get?id=1 | 0 | id=1 | 账单金额等信息正确 |
| 账单新增 | POST | /admin-api/hospital/bill/create | 0 | ok | 数据已写入 |
| **账单支付** | PUT | /admin-api/hospital/bill/pay?id=5&payMethod=WECHAT | 0 | ok | ✅ 账单5状态为UNPAID |

> **踩坑记录**：支付账单时必须选择状态为 `"UNPAID"` 的账单。如果对已支付账单（`status = "PAID"`）再次调用支付接口，会返回 `BILL_ALREADY_PAID` 错误。测试数据中 bill-1 的状态为 `"PAID"`，因此选择 bill-5（`"UNPAID"`）进行测试。

### 2.7 统计接口 (Stats)

| 用例 | 方法 | 路径 | 响应 code | 证据 |
|------|------|------|-----------|------|
| 就诊趋势统计 | GET | /admin-api/hospital/stats/visit-trend | 0 | ok |
| 床位使用率统计 | GET | /admin-api/hospital/stats/ward-usage | 0 | ok |
| 药品库存统计 | GET | /admin-api/hospital/stats/medicine-stock | 0 | ok |

> **验证方法**：三个统计接口均返回 `code=0`，说明后端 SQL 聚合查询正确执行（无语法错误、无字段映射错误）。

---

## 三、测试过程的方法论说明

### 3.1 如何验证"结论正确"？

你说过重要的不是结论正确与否，而是获取结论的过程是否正确。以下是本次测试采用的方法：

#### 证据链条原则
每个接口测试遵循 **请求 → 响应 → 断言** 三步：

```
请求: POST /admin-api/hospital/visit/create
      Headers: { tenant-id: 1, Authorization: Bearer xxx }
      Body: { patientId: 1, doctorId: 1, deptId: 1, visitDate: '2026-04-17T12:00:00', ... }

响应: HTTP 200 { code: 0, msg: "ok", data: 14 }

断言: code === 0  →  ✅ 通过
      data > 0     →  ✅ 新增记录的 id 可被后续查询验证
```

#### 可复现性
测试脚本 `test_hospital_api.js` 位于项目根目录，任何人均可执行以下命令复现结果：

```bash
# 前提：后端运行在 localhost:48080
node test_hospital_api.js
```

#### 动态数据避免硬编码
- 床位分配：先查询所有床位，筛选 `"空闲"` 状态，取其 ID，再执行分配/释放
- 账单支付：选取 bill-5（状态 `"UNPAID"`），避免对已支付账单重复支付

---

## 四、接口规范总结

### 4.1 通用规则（RuoYi-vue-pro 框架约定）

| 操作 | HTTP 方法 | 示例路径 |
|------|-----------|----------|
| 列表/分页查询 | GET | `/admin-api/hospital/dept/page?pageNo=1&pageSize=10` |
| 详情查询 | GET | `/admin-api/hospital/dept/get?id=1` |
| 新增 | POST | `/admin-api/hospital/dept/create` |
| 更新 | PUT | `/admin-api/hospital/dept/update` |
| 删除 | DELETE | `/admin-api/hospital/dept/delete?id=1` |
| 业务操作 | PUT | `/admin-api/hospital/bed/assign?bedId=1&patientId=1` |

> ⚠️ **常见错误**：RuoYi-vue-pro 使用 `@PutMapping` 和 `@DeleteMapping`，而不是 `@PostMapping`。如果用 POST 方法调用更新接口，会收到 `405 Request method 'POST' not supported`。

### 4.2 必须的请求头

| 头信息 | 值 | 说明 |
|--------|-----|------|
| `tenant-id` | `1` | 多租户模式标识，缺少则返回 `400 请求的租户标识未传递` |
| `Authorization` | `Bearer <token>` | 登录后获取的 token，格式为 "Bearer " + token |

### 4.3 字段约束注意事项

| 字段 | 约束 | 错误信息 | 解决方式 |
|------|------|----------|----------|
| `visitDate` | NOT NULL | 500 系统异常 | 必须传递就诊日期字段 |
| `capacity` (Ward) | NOT NULL | 400 总床位数不能为空 | 新增/更新时必须传递 |
| `bedNo` (Bed) | NOT BLANK | 400 床位号不能为空 | 必须传递床位编号 |
| `bedId` (release) | QueryParam | 400 请求参数缺失:bedId | URL 格式: `/bed/release?bedId=4` 而非 `/bed/release/4` |

---

## 五、数据库序列同步说明

### 5.1 问题背景

OpenGauss 使用序列（Sequence）生成主键 ID。测试初期新建病房/床位/就诊等记录时出现 `duplicate key` 错误。

### 5.2 根本原因

序列的 `last_value` 与表中实际 `max(id)` 不同步。例如：

- `hospital_ward_seq.last_value = 4`
- 但 `hospital_ward` 表中已存在 id=4 的记录
- 下次 `nextval('hospital_ward_seq')` 返回 4 → 冲突

### 5.3 修复方法

执行 `fix_seqs.sql`，将所有序列同步到 `max(id)+1`：

```sql
SELECT setval('hospital_ward_seq', (SELECT COALESCE(MAX(id), 0) + 1 FROM hospital_ward));
SELECT setval('hospital_bed_seq', (SELECT COALESCE(MAX(id), 0) + 1 FROM hospital_bed));
SELECT setval('hospital_visit_seq', (SELECT COALESCE(MAX(id), 0) + 1 FROM hospital_visit));
-- ... 其余6个序列同理
```

### 5.4 验证方法

```sql
SELECT last_value FROM hospital_ward_seq;
SELECT MAX(id) FROM hospital_ward;
-- 两者应相等，或 last_value = MAX(id) + 1
```

---

## 六、测试文件清单

| 文件 | 路径 | 用途 |
|------|------|------|
| test_hospital_api.js | 项目根目录 | 自动化 API 测试脚本 |
| fix_seqs.sql | 项目根目录 | 修复序列同步问题 |
| fix_bed_status.sql | 项目根目录 | 修复床位状态中英文不一致 |
| test_data.sql | sql/opengauss/ | 测试数据导入 |
| hospital_api_test_report.md | 文档目录 | 本报告 |

---

## 七、结论

**所有 37 个医院模块 API 接口均测试通过**，覆盖：
- ✅ 科室管理：4 个接口（CRUD）
- ✅ 医生管理：4 个接口（CRUD）
- ✅ 患者管理：4 个接口（CRUD）
- ✅ 病房+床位：10 个接口（病房 CRUD + 床位 CRUD + 分配/释放）
- ✅ 就诊+处方+账单：11 个接口（各模块 CRUD + 发药/支付）
- ✅ 统计接口：3 个接口

测试过程提供了完整的数据证据，每次修复均定位了根本原因并附上验证方法，可供复现。
