-- =============================================
-- 医院病人数据管理系统 - OpenGauss 建表脚本
-- 兼容 ruoyi-vue-pro 框架的 BaseDO 基类字段
-- =============================================

-- 1. 科室表
CREATE TABLE IF NOT EXISTS hospital_department (
    id          BIGINT          PRIMARY KEY,
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

COMMENT ON TABLE  hospital_department              IS '科室表';
COMMENT ON COLUMN hospital_department.id            IS '科室ID';
COMMENT ON COLUMN hospital_department.dept_name     IS '科室名称';
COMMENT ON COLUMN hospital_department.phone         IS '科室联系电话';
COMMENT ON COLUMN hospital_department.manager       IS '科室主任';
COMMENT ON COLUMN hospital_department.location      IS '科室位置';
COMMENT ON COLUMN hospital_department.description   IS '科室描述';

-- 2. 医生信息表
CREATE TABLE IF NOT EXISTS hospital_doctor (
    id          BIGINT          PRIMARY KEY,
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

COMMENT ON TABLE  hospital_doctor              IS '医生信息表';
COMMENT ON COLUMN hospital_doctor.id            IS '医生ID';
COMMENT ON COLUMN hospital_doctor.user_id       IS '关联系统用户ID';
COMMENT ON COLUMN hospital_doctor.dept_id       IS '关联科室ID';
COMMENT ON COLUMN hospital_doctor.name          IS '医生姓名';
COMMENT ON COLUMN hospital_doctor.gender        IS '性别';
COMMENT ON COLUMN hospital_doctor.age           IS '年龄';
COMMENT ON COLUMN hospital_doctor.title         IS '职称';
COMMENT ON COLUMN hospital_doctor.license_no    IS '执业医师证编号';
COMMENT ON COLUMN hospital_doctor.phone         IS '联系电话';
COMMENT ON COLUMN hospital_doctor.email         IS '邮箱';

-- 3. 病人信息表
CREATE TABLE IF NOT EXISTS hospital_patient (
    id                  BIGINT          PRIMARY KEY,
    user_id             BIGINT,
    name                VARCHAR(50)     NOT NULL,
    gender              VARCHAR(10),
    birth_date          DATE,
    id_card             VARCHAR(18),
    phone               VARCHAR(20),
    address             VARCHAR(200),
    emergency_contact   VARCHAR(50),
    emergency_phone     VARCHAR(20),
    insurance_no        VARCHAR(50),
    medical_history     TEXT,
    admission_date      DATE,
    creator             VARCHAR(64)     DEFAULT '',
    create_time         TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    updater             VARCHAR(64)     DEFAULT '',
    update_time         TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    deleted             SMALLINT        DEFAULT 0,
    tenant_id           BIGINT          DEFAULT 0
);

COMMENT ON TABLE  hospital_patient                  IS '病人信息表';
COMMENT ON COLUMN hospital_patient.id                IS '患者ID';
COMMENT ON COLUMN hospital_patient.user_id           IS '关联系统用户ID';
COMMENT ON COLUMN hospital_patient.name              IS '患者姓名';
COMMENT ON COLUMN hospital_patient.gender            IS '性别';
COMMENT ON COLUMN hospital_patient.birth_date        IS '出生日期';
COMMENT ON COLUMN hospital_patient.id_card           IS '身份证号';
COMMENT ON COLUMN hospital_patient.phone             IS '联系电话';
COMMENT ON COLUMN hospital_patient.address           IS '家庭住址';
COMMENT ON COLUMN hospital_patient.emergency_contact IS '紧急联系人';
COMMENT ON COLUMN hospital_patient.emergency_phone   IS '紧急联系电话';
COMMENT ON COLUMN hospital_patient.insurance_no      IS '医保号';
COMMENT ON COLUMN hospital_patient.medical_history   IS '既往病史';
COMMENT ON COLUMN hospital_patient.admission_date    IS '入院日期';

-- 4. 病房表
CREATE TABLE IF NOT EXISTS hospital_ward (
    id          BIGINT          PRIMARY KEY,
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

COMMENT ON TABLE  hospital_ward              IS '病房表';
COMMENT ON COLUMN hospital_ward.id            IS '病房ID';
COMMENT ON COLUMN hospital_ward.dept_id       IS '所属科室ID';
COMMENT ON COLUMN hospital_ward.ward_no       IS '病房编号';
COMMENT ON COLUMN hospital_ward.type          IS '病房类型：普通/ICU/VIP';
COMMENT ON COLUMN hospital_ward.capacity      IS '总床位数';
COMMENT ON COLUMN hospital_ward.used_beds     IS '已使用床位数';
COMMENT ON COLUMN hospital_ward.status        IS '状态：1可用/0停用';
COMMENT ON COLUMN hospital_ward.description   IS '描述';

-- 5. 床位表
CREATE TABLE IF NOT EXISTS hospital_bed (
    id              BIGINT          PRIMARY KEY,
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

COMMENT ON TABLE  hospital_bed              IS '床位表';
COMMENT ON COLUMN hospital_bed.id            IS '床位ID';
COMMENT ON COLUMN hospital_bed.ward_id       IS '所属病房ID';
COMMENT ON COLUMN hospital_bed.bed_no        IS '床位号';
COMMENT ON COLUMN hospital_bed.status        IS '状态：空闲/已占用/维护中';
COMMENT ON COLUMN hospital_bed.patient_id    IS '当前入住患者ID';
COMMENT ON COLUMN hospital_bed.admission_time IS '入住时间';

-- 6. 就诊记录表
CREATE TABLE IF NOT EXISTS hospital_visit (
    id          BIGINT          PRIMARY KEY,
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

COMMENT ON TABLE  hospital_visit              IS '就诊记录表';
COMMENT ON COLUMN hospital_visit.id            IS '就诊ID';
COMMENT ON COLUMN hospital_visit.patient_id    IS '患者ID';
COMMENT ON COLUMN hospital_visit.doctor_id     IS '主治医生ID';
COMMENT ON COLUMN hospital_visit.dept_id       IS '就诊科室ID';
COMMENT ON COLUMN hospital_visit.visit_date    IS '就诊时间';
COMMENT ON COLUMN hospital_visit.reason        IS '就诊原因';
COMMENT ON COLUMN hospital_visit.diagnosis     IS '诊断结果';
COMMENT ON COLUMN hospital_visit.notes         IS '医生备注';
COMMENT ON COLUMN hospital_visit.status        IS '状态：待就诊/就诊中/已完成/已取消';

-- 7. 药品表
CREATE TABLE IF NOT EXISTS hospital_medicine (
    id              BIGINT          PRIMARY KEY,
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

COMMENT ON TABLE  hospital_medicine              IS '药品表';
COMMENT ON COLUMN hospital_medicine.id            IS '药品ID';
COMMENT ON COLUMN hospital_medicine.name          IS '药品名称';
COMMENT ON COLUMN hospital_medicine.specification IS '规格';
COMMENT ON COLUMN hospital_medicine.unit          IS '单位：盒/片/瓶';
COMMENT ON COLUMN hospital_medicine.price         IS '单价';
COMMENT ON COLUMN hospital_medicine.stock         IS '库存数量';
COMMENT ON COLUMN hospital_medicine.manufacturer  IS '生产厂家';
COMMENT ON COLUMN hospital_medicine.expiry_date   IS '有效期';

-- 8. 处方表
CREATE TABLE IF NOT EXISTS hospital_prescription (
    id          BIGINT          PRIMARY KEY,
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

COMMENT ON TABLE  hospital_prescription              IS '处方表';
COMMENT ON COLUMN hospital_prescription.id            IS '处方ID';
COMMENT ON COLUMN hospital_prescription.visit_id      IS '关联就诊记录ID';
COMMENT ON COLUMN hospital_prescription.doctor_id     IS '开方医生ID';
COMMENT ON COLUMN hospital_prescription.status        IS '状态：未发药/已发药/已过期';
COMMENT ON COLUMN hospital_prescription.notes         IS '处方备注';

-- 9. 处方明细表
CREATE TABLE IF NOT EXISTS hospital_prescription_item (
    id              BIGINT          PRIMARY KEY,
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

COMMENT ON TABLE  hospital_prescription_item              IS '处方明细表';
COMMENT ON COLUMN hospital_prescription_item.id            IS '明细ID';
COMMENT ON COLUMN hospital_prescription_item.prescription_id IS '关联处方ID';
COMMENT ON COLUMN hospital_prescription_item.medicine_id     IS '药品ID';
COMMENT ON COLUMN hospital_prescription_item.quantity        IS '数量';
COMMENT ON COLUMN hospital_prescription_item.price           IS '当时单价';
COMMENT ON COLUMN hospital_prescription_item.instructions    IS '用法说明';

-- 10. 账单表
CREATE TABLE IF NOT EXISTS hospital_bill (
    id          BIGINT          PRIMARY KEY,
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

COMMENT ON TABLE  hospital_bill              IS '账单表';
COMMENT ON COLUMN hospital_bill.id            IS '账单ID';
COMMENT ON COLUMN hospital_bill.visit_id      IS '关联就诊记录ID';
COMMENT ON COLUMN hospital_bill.patient_id    IS '患者ID';
COMMENT ON COLUMN hospital_bill.total_amount  IS '总金额';
COMMENT ON COLUMN hospital_bill.pay_amount    IS '已支付金额';
COMMENT ON COLUMN hospital_bill.pay_time      IS '支付时间';
COMMENT ON COLUMN hospital_bill.pay_method    IS '支付方式：现金/医保/微信';
COMMENT ON COLUMN hospital_bill.status        IS '状态：未支付/已支付/已退费';

-- 创建序列（OpenGauss 用序列来生成自增主键）
CREATE SEQUENCE IF NOT EXISTS hospital_department_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS hospital_doctor_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS hospital_patient_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS hospital_ward_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS hospital_bed_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS hospital_visit_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS hospital_medicine_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS hospital_prescription_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS hospital_prescription_item_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS hospital_bill_seq START WITH 1 INCREMENT BY 1;

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_doctor_dept_id ON hospital_doctor(dept_id);
CREATE INDEX IF NOT EXISTS idx_ward_dept_id ON hospital_ward(dept_id);
CREATE INDEX IF NOT EXISTS idx_bed_ward_id ON hospital_bed(ward_id);
CREATE INDEX IF NOT EXISTS idx_visit_patient_id ON hospital_visit(patient_id);
CREATE INDEX IF NOT EXISTS idx_visit_doctor_id ON hospital_visit(doctor_id);
CREATE INDEX IF NOT EXISTS idx_prescription_visit_id ON hospital_prescription(visit_id);
CREATE INDEX IF NOT EXISTS idx_prescription_item_prescription_id ON hospital_prescription_item(prescription_id);
CREATE INDEX IF NOT EXISTS idx_bill_visit_id ON hospital_bill(visit_id);
CREATE INDEX IF NOT EXISTS idx_bill_patient_id ON hospital_bill(patient_id);
