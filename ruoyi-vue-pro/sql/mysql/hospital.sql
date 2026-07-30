-- =============================================
-- 医院病人数据管理系统 - MySQL 建表脚本
-- 兼容 ruoyi-vue-pro 框架的 BaseDO 基类字段
-- =============================================

-- 1. 科室表
CREATE TABLE IF NOT EXISTS hospital_department (
    id          BIGINT          PRIMARY KEY AUTO_INCREMENT,
    dept_name   VARCHAR(50)     NOT NULL,
    phone       VARCHAR(20),
    manager     VARCHAR(50),
    location    VARCHAR(100),
    description TEXT,
    creator     VARCHAR(64)     DEFAULT '',
    create_time TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    updater     VARCHAR(64)     DEFAULT '',
    update_time TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    deleted     SMALLINT        DEFAULT 0,
    tenant_id   BIGINT          DEFAULT 0
);


-- 2. 医生信息表
CREATE TABLE IF NOT EXISTS hospital_doctor (
    id          BIGINT          PRIMARY KEY AUTO_INCREMENT,
    user_id     BIGINT,
    dept_id     BIGINT,
    name        VARCHAR(50)     NOT NULL,
    gender      VARCHAR(10),
    age         INT,
    title       VARCHAR(50),
    license_no  VARCHAR(50),
    phone       VARCHAR(20),
    email       VARCHAR(100),
    creator     VARCHAR(64)     DEFAULT '',
    create_time TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    updater     VARCHAR(64)     DEFAULT '',
    update_time TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    deleted     SMALLINT        DEFAULT 0,
    tenant_id   BIGINT          DEFAULT 0
);


-- 3. 病人信息表
CREATE TABLE IF NOT EXISTS hospital_patient (
    id                  BIGINT          PRIMARY KEY AUTO_INCREMENT,
    user_id             BIGINT,
    name                VARCHAR(50)     NOT NULL,
    gender              VARCHAR(10),
    birth_date          DATE,
    id_card             VARCHAR(18),
    phone               VARCHAR(20),
    address             VARCHAR(200),
    region              VARCHAR(50),
    emergency_contact   VARCHAR(50),
    emergency_phone     VARCHAR(20),
    insurance_no        VARCHAR(50),
    insurance_type      VARCHAR(20),
    medical_history     TEXT,
    admission_date      DATE,
    creator             VARCHAR(64)     DEFAULT '',
    create_time         TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    updater             VARCHAR(64)     DEFAULT '',
    update_time         TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    deleted             SMALLINT        DEFAULT 0,
    tenant_id           BIGINT          DEFAULT 0
);


-- 4. 病房表
CREATE TABLE IF NOT EXISTS hospital_ward (
    id          BIGINT          PRIMARY KEY AUTO_INCREMENT,
    dept_id     BIGINT,
    ward_no     VARCHAR(20)     NOT NULL,
    type        VARCHAR(20),
    capacity    INT             DEFAULT 0,
    used_beds   INT             DEFAULT 0,
    status      INT             DEFAULT 1,
    description VARCHAR(200),
    creator     VARCHAR(64)     DEFAULT '',
    create_time TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    updater     VARCHAR(64)     DEFAULT '',
    update_time TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    deleted     SMALLINT        DEFAULT 0,
    tenant_id   BIGINT          DEFAULT 0
);


-- 5. 床位表
CREATE TABLE IF NOT EXISTS hospital_bed (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT,
    ward_id         BIGINT,
    bed_no          VARCHAR(20)     NOT NULL,
    status          VARCHAR(20)     DEFAULT '空闲',
    patient_id      BIGINT,
    admission_time  TIMESTAMP,
    creator         VARCHAR(64)     DEFAULT '',
    create_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    updater         VARCHAR(64)     DEFAULT '',
    update_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    deleted         SMALLINT        DEFAULT 0,
    tenant_id       BIGINT          DEFAULT 0
);


-- 6. 就诊记录表
CREATE TABLE IF NOT EXISTS hospital_visit (
    id          BIGINT          PRIMARY KEY AUTO_INCREMENT,
    patient_id  BIGINT,
    doctor_id   BIGINT,
    dept_id     BIGINT,
    visit_date  TIMESTAMP,
    reason      VARCHAR(500),
    diagnosis   VARCHAR(500),
    notes       TEXT,
    status      VARCHAR(20)     DEFAULT '待就诊',
    creator     VARCHAR(64)     DEFAULT '',
    create_time TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    updater     VARCHAR(64)     DEFAULT '',
    update_time TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    deleted     SMALLINT        DEFAULT 0,
    tenant_id   BIGINT          DEFAULT 0
);


-- 7. 药品表
CREATE TABLE IF NOT EXISTS hospital_medicine (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT,
    name            VARCHAR(100)    NOT NULL,
    specification   VARCHAR(100),
    unit            VARCHAR(20),
    price           DECIMAL(10,2)   DEFAULT 0,
    stock           INT             DEFAULT 0,
    manufacturer    VARCHAR(100),
    expiry_date     DATE,
    creator         VARCHAR(64)     DEFAULT '',
    create_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    updater         VARCHAR(64)     DEFAULT '',
    update_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    deleted         SMALLINT        DEFAULT 0,
    tenant_id       BIGINT          DEFAULT 0
);


-- 8. 处方表
CREATE TABLE IF NOT EXISTS hospital_prescription (
    id          BIGINT          PRIMARY KEY AUTO_INCREMENT,
    visit_id    BIGINT,
    doctor_id   BIGINT,
    status      VARCHAR(20)     DEFAULT '未发药',
    notes       VARCHAR(500),
    creator     VARCHAR(64)     DEFAULT '',
    create_time TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    updater     VARCHAR(64)     DEFAULT '',
    update_time TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    deleted     SMALLINT        DEFAULT 0,
    tenant_id   BIGINT          DEFAULT 0
);


-- 9. 处方明细表
CREATE TABLE IF NOT EXISTS hospital_prescription_item (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT,
    prescription_id BIGINT,
    medicine_id     BIGINT,
    quantity        INT             DEFAULT 1,
    price           DECIMAL(10,2)   DEFAULT 0,
    instructions    VARCHAR(200),
    creator         VARCHAR(64)     DEFAULT '',
    create_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    updater         VARCHAR(64)     DEFAULT '',
    update_time     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    deleted         SMALLINT        DEFAULT 0,
    tenant_id       BIGINT          DEFAULT 0
);


-- 10. 账单表
CREATE TABLE IF NOT EXISTS hospital_bill (
    id          BIGINT          PRIMARY KEY AUTO_INCREMENT,
    visit_id    BIGINT,
    patient_id  BIGINT,
    total_amount DECIMAL(10,2)  DEFAULT 0,
    pay_amount  DECIMAL(10,2)   DEFAULT 0,
    pay_time    TIMESTAMP,
    pay_method  VARCHAR(20),
    status      VARCHAR(20)     DEFAULT '未支付',
    creator     VARCHAR(64)     DEFAULT '',
    create_time TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    updater     VARCHAR(64)     DEFAULT '',
    update_time TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    deleted     SMALLINT        DEFAULT 0,
    tenant_id   BIGINT          DEFAULT 0
);


-- 创建序列（OpenGauss 用序列来生成自增主键）

-- 创建索引
CREATE INDEX idx_doctor_dept_id ON hospital_doctor(dept_id);
CREATE INDEX idx_ward_dept_id ON hospital_ward(dept_id);
CREATE INDEX idx_bed_ward_id ON hospital_bed(ward_id);
CREATE INDEX idx_visit_patient_id ON hospital_visit(patient_id);
CREATE INDEX idx_visit_doctor_id ON hospital_visit(doctor_id);
CREATE INDEX idx_prescription_visit_id ON hospital_prescription(visit_id);
CREATE INDEX idx_prescription_item_prescription_id ON hospital_prescription_item(prescription_id);
CREATE INDEX idx_bill_visit_id ON hospital_bill(visit_id);
CREATE INDEX idx_bill_patient_id ON hospital_bill(patient_id);
