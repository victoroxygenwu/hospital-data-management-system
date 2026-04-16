-- =============================================
-- 医院病人数据管理系统 - OpenGauss 建表脚本
-- =============================================

-- 1. 科室表
DROP TABLE IF EXISTS hospital_department;
CREATE TABLE hospital_department
(
    id          int8         NOT NULL,
    dept_name   varchar(50)  NOT NULL,
    phone       varchar(20)  NULL     DEFAULT NULL,
    manager     varchar(50)  NULL     DEFAULT NULL,
    location    varchar(100) NULL     DEFAULT NULL,
    description text         NULL     DEFAULT NULL,
    creator     varchar(64)  NULL     DEFAULT '',
    create_time timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater     varchar(64)  NULL     DEFAULT '',
    update_time timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted     int2         NOT NULL DEFAULT 0,
    tenant_id   int8         NOT NULL DEFAULT 0
);

ALTER TABLE hospital_department
    ADD CONSTRAINT pk_hospital_department PRIMARY KEY (id);

COMMENT ON COLUMN hospital_department.id IS '科室ID';
COMMENT ON COLUMN hospital_department.dept_name IS '科室名称';
COMMENT ON COLUMN hospital_department.phone IS '科室联系电话';
COMMENT ON COLUMN hospital_department.manager IS '科室主任';
COMMENT ON COLUMN hospital_department.location IS '科室位置';
COMMENT ON COLUMN hospital_department.description IS '科室描述';
COMMENT ON COLUMN hospital_department.creator IS '创建者';
COMMENT ON COLUMN hospital_department.create_time IS '创建时间';
COMMENT ON COLUMN hospital_department.updater IS '更新者';
COMMENT ON COLUMN hospital_department.update_time IS '更新时间';
COMMENT ON COLUMN hospital_department.deleted IS '是否删除';
COMMENT ON COLUMN hospital_department.tenant_id IS '租户编号';
COMMENT ON TABLE hospital_department IS '科室表';

DROP SEQUENCE IF EXISTS hospital_department_seq;
CREATE SEQUENCE hospital_department_seq START 1;

-- 2. 医生信息表
DROP TABLE IF EXISTS hospital_doctor;
CREATE TABLE hospital_doctor
(
    id          int8         NOT NULL,
    user_id     int8         NULL     DEFAULT NULL,
    dept_id     int8         NULL     DEFAULT NULL,
    name        varchar(50)  NOT NULL,
    gender      varchar(10)  NULL     DEFAULT NULL,
    age         int4         NULL     DEFAULT NULL,
    title       varchar(50)  NULL     DEFAULT NULL,
    license_no  varchar(50)  NULL     DEFAULT NULL,
    phone       varchar(20)  NULL     DEFAULT NULL,
    email       varchar(100) NULL     DEFAULT NULL,
    creator     varchar(64)  NULL     DEFAULT '',
    create_time timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater     varchar(64)  NULL     DEFAULT '',
    update_time timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted     int2         NOT NULL DEFAULT 0,
    tenant_id   int8         NOT NULL DEFAULT 0
);

ALTER TABLE hospital_doctor
    ADD CONSTRAINT pk_hospital_doctor PRIMARY KEY (id);

COMMENT ON COLUMN hospital_doctor.id IS '医生ID';
COMMENT ON COLUMN hospital_doctor.user_id IS '关联系统用户ID';
COMMENT ON COLUMN hospital_doctor.dept_id IS '关联科室ID';
COMMENT ON COLUMN hospital_doctor.name IS '医生姓名';
COMMENT ON COLUMN hospital_doctor.gender IS '性别';
COMMENT ON COLUMN hospital_doctor.age IS '年龄';
COMMENT ON COLUMN hospital_doctor.title IS '职称';
COMMENT ON COLUMN hospital_doctor.license_no IS '执业医师证编号';
COMMENT ON COLUMN hospital_doctor.phone IS '联系电话';
COMMENT ON COLUMN hospital_doctor.email IS '邮箱';
COMMENT ON TABLE hospital_doctor IS '医生信息表';

DROP SEQUENCE IF EXISTS hospital_doctor_seq;
CREATE SEQUENCE hospital_doctor_seq START 1;

-- 3. 病人信息表
DROP TABLE IF EXISTS hospital_patient;
CREATE TABLE hospital_patient
(
    id                int8         NOT NULL,
    user_id           int8         NULL     DEFAULT NULL,
    name              varchar(50)  NOT NULL,
    gender            varchar(10)  NULL     DEFAULT NULL,
    birth_date        date         NULL     DEFAULT NULL,
    id_card           varchar(18)  NULL     DEFAULT NULL,
    phone             varchar(20)  NULL     DEFAULT NULL,
    address           varchar(200) NULL     DEFAULT NULL,
    emergency_contact varchar(50)  NULL     DEFAULT NULL,
    emergency_phone   varchar(20)  NULL     DEFAULT NULL,
    insurance_no      varchar(50)  NULL     DEFAULT NULL,
    medical_history   text         NULL     DEFAULT NULL,
    admission_date    date         NULL     DEFAULT NULL,
    creator           varchar(64)  NULL     DEFAULT '',
    create_time       timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater           varchar(64)  NULL     DEFAULT '',
    update_time       timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted           int2         NOT NULL DEFAULT 0,
    tenant_id         int8         NOT NULL DEFAULT 0
);

ALTER TABLE hospital_patient
    ADD CONSTRAINT pk_hospital_patient PRIMARY KEY (id);

COMMENT ON COLUMN hospital_patient.id IS '患者ID';
COMMENT ON COLUMN hospital_patient.user_id IS '关联系统用户ID';
COMMENT ON COLUMN hospital_patient.name IS '患者姓名';
COMMENT ON COLUMN hospital_patient.gender IS '性别';
COMMENT ON COLUMN hospital_patient.birth_date IS '出生日期';
COMMENT ON COLUMN hospital_patient.id_card IS '身份证号';
COMMENT ON COLUMN hospital_patient.phone IS '联系电话';
COMMENT ON COLUMN hospital_patient.address IS '家庭住址';
COMMENT ON COLUMN hospital_patient.emergency_contact IS '紧急联系人姓名';
COMMENT ON COLUMN hospital_patient.emergency_phone IS '紧急联系人电话';
COMMENT ON COLUMN hospital_patient.insurance_no IS '医保卡号';
COMMENT ON COLUMN hospital_patient.medical_history IS '既往病史';
COMMENT ON COLUMN hospital_patient.admission_date IS '最近入院时间';
COMMENT ON TABLE hospital_patient IS '病人信息表';

DROP SEQUENCE IF EXISTS hospital_patient_seq;
CREATE SEQUENCE hospital_patient_seq START 1;

-- 4. 病房表
DROP TABLE IF EXISTS hospital_ward;
CREATE TABLE hospital_ward
(
    id          int8         NOT NULL,
    dept_id     int8         NULL     DEFAULT NULL,
    ward_no     varchar(20)  NULL     DEFAULT NULL,
    type        varchar(20)  NULL     DEFAULT NULL,
    capacity    int4         NOT NULL,
    used_beds   int4         NOT NULL DEFAULT 0,
    status      int2         NOT NULL DEFAULT 1,
    description varchar(200) NULL     DEFAULT NULL,
    creator     varchar(64)  NULL     DEFAULT '',
    create_time timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater     varchar(64)  NULL     DEFAULT '',
    update_time timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted     int2         NOT NULL DEFAULT 0,
    tenant_id   int8         NOT NULL DEFAULT 0
);

ALTER TABLE hospital_ward
    ADD CONSTRAINT pk_hospital_ward PRIMARY KEY (id);

COMMENT ON COLUMN hospital_ward.id IS '病房ID';
COMMENT ON COLUMN hospital_ward.dept_id IS '所属科室ID';
COMMENT ON COLUMN hospital_ward.ward_no IS '病房编号';
COMMENT ON COLUMN hospital_ward.type IS '病房类型：普通/ICU/VIP';
COMMENT ON COLUMN hospital_ward.capacity IS '病房总床位数';
COMMENT ON COLUMN hospital_ward.used_beds IS '已使用床位数';
COMMENT ON COLUMN hospital_ward.status IS '病房状态：1可用/0停用';
COMMENT ON COLUMN hospital_ward.description IS '描述';
COMMENT ON TABLE hospital_ward IS '病房表';

DROP SEQUENCE IF EXISTS hospital_ward_seq;
CREATE SEQUENCE hospital_ward_seq START 1;

-- 5. 床位表
DROP TABLE IF EXISTS hospital_bed;
CREATE TABLE hospital_bed
(
    id             int8        NOT NULL,
    ward_id        int8        NULL     DEFAULT NULL,
    bed_no         varchar(10) NULL     DEFAULT NULL,
    status         varchar(20) NOT NULL,
    patient_id     int8        NULL     DEFAULT NULL,
    admission_time timestamp   NULL     DEFAULT NULL,
    creator        varchar(64) NULL     DEFAULT '',
    create_time    timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater        varchar(64) NULL     DEFAULT '',
    update_time    timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted        int2        NOT NULL DEFAULT 0,
    tenant_id      int8        NOT NULL DEFAULT 0
);

ALTER TABLE hospital_bed
    ADD CONSTRAINT pk_hospital_bed PRIMARY KEY (id);

COMMENT ON COLUMN hospital_bed.id IS '床位ID';
COMMENT ON COLUMN hospital_bed.ward_id IS '所属病房ID';
COMMENT ON COLUMN hospital_bed.bed_no IS '床位号';
COMMENT ON COLUMN hospital_bed.status IS '状态：空闲/已占用/维护中';
COMMENT ON COLUMN hospital_bed.patient_id IS '当前入住的患者ID';
COMMENT ON COLUMN hospital_bed.admission_time IS '入住时间';
COMMENT ON TABLE hospital_bed IS '床位表';

DROP SEQUENCE IF EXISTS hospital_bed_seq;
CREATE SEQUENCE hospital_bed_seq START 1;

-- 6. 就诊记录表
DROP TABLE IF EXISTS hospital_visit;
CREATE TABLE hospital_visit
(
    id          int8         NOT NULL,
    patient_id  int8         NULL     DEFAULT NULL,
    doctor_id   int8         NULL     DEFAULT NULL,
    dept_id     int8         NULL     DEFAULT NULL,
    visit_date  timestamp    NOT NULL,
    reason      varchar(200) NULL     DEFAULT NULL,
    diagnosis   text         NULL     DEFAULT NULL,
    notes       text         NULL     DEFAULT NULL,
    status      varchar(20)  NOT NULL,
    creator     varchar(64)  NULL     DEFAULT '',
    create_time timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater     varchar(64)  NULL     DEFAULT '',
    update_time timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted     int2         NOT NULL DEFAULT 0,
    tenant_id   int8         NOT NULL DEFAULT 0
);

ALTER TABLE hospital_visit
    ADD CONSTRAINT pk_hospital_visit PRIMARY KEY (id);

COMMENT ON COLUMN hospital_visit.id IS '就诊ID';
COMMENT ON COLUMN hospital_visit.patient_id IS '患者ID';
COMMENT ON COLUMN hospital_visit.doctor_id IS '主治医生ID';
COMMENT ON COLUMN hospital_visit.dept_id IS '就诊科室ID';
COMMENT ON COLUMN hospital_visit.visit_date IS '就诊时间';
COMMENT ON COLUMN hospital_visit.reason IS '就诊原因';
COMMENT ON COLUMN hospital_visit.diagnosis IS '诊断结果';
COMMENT ON COLUMN hospital_visit.notes IS '医生备注';
COMMENT ON COLUMN hospital_visit.status IS '状态：待就诊/就诊中/已完成/已取消';
COMMENT ON TABLE hospital_visit IS '就诊记录表';

DROP SEQUENCE IF EXISTS hospital_visit_seq;
CREATE SEQUENCE hospital_visit_seq START 1;

-- 7. 药品信息表
DROP TABLE IF EXISTS hospital_medicine;
CREATE TABLE hospital_medicine
(
    id            int8          NOT NULL,
    name          varchar(100)  NOT NULL,
    specification varchar(50)   NULL     DEFAULT NULL,
    unit          varchar(20)   NULL     DEFAULT NULL,
    price         numeric(10,2) NOT NULL,
    stock         int4          NOT NULL DEFAULT 0,
    manufacturer  varchar(100)  NULL     DEFAULT NULL,
    expiry_date   date          NULL     DEFAULT NULL,
    creator       varchar(64)   NULL     DEFAULT '',
    create_time   timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater       varchar(64)   NULL     DEFAULT '',
    update_time   timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted       int2          NOT NULL DEFAULT 0,
    tenant_id     int8          NOT NULL DEFAULT 0
);

ALTER TABLE hospital_medicine
    ADD CONSTRAINT pk_hospital_medicine PRIMARY KEY (id);

COMMENT ON COLUMN hospital_medicine.id IS '药品ID';
COMMENT ON COLUMN hospital_medicine.name IS '药品名称';
COMMENT ON COLUMN hospital_medicine.specification IS '规格';
COMMENT ON COLUMN hospital_medicine.unit IS '单位：盒/片/瓶';
COMMENT ON COLUMN hospital_medicine.price IS '单价';
COMMENT ON COLUMN hospital_medicine.stock IS '库存数量';
COMMENT ON COLUMN hospital_medicine.manufacturer IS '生产厂家';
COMMENT ON COLUMN hospital_medicine.expiry_date IS '有效期';
COMMENT ON TABLE hospital_medicine IS '药品信息表';

DROP SEQUENCE IF EXISTS hospital_medicine_seq;
CREATE SEQUENCE hospital_medicine_seq START 1;

-- 8. 处方表
DROP TABLE IF EXISTS hospital_prescription;
CREATE TABLE hospital_prescription
(
    id          int8         NOT NULL,
    visit_id    int8         NULL     DEFAULT NULL,
    doctor_id   int8         NULL     DEFAULT NULL,
    create_time timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status      varchar(20)  NOT NULL,
    notes       text         NULL     DEFAULT NULL,
    creator     varchar(64)  NULL     DEFAULT '',
    updater     varchar(64)  NULL     DEFAULT '',
    update_time timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted     int2         NOT NULL DEFAULT 0,
    tenant_id   int8         NOT NULL DEFAULT 0
);

ALTER TABLE hospital_prescription
    ADD CONSTRAINT pk_hospital_prescription PRIMARY KEY (id);

COMMENT ON COLUMN hospital_prescription.id IS '处方ID';
COMMENT ON COLUMN hospital_prescription.visit_id IS '关联的就诊记录ID';
COMMENT ON COLUMN hospital_prescription.doctor_id IS '开方医生ID';
COMMENT ON COLUMN hospital_prescription.create_time IS '开方时间';
COMMENT ON COLUMN hospital_prescription.status IS '状态：未发药/已发药/已过期';
COMMENT ON COLUMN hospital_prescription.notes IS '处方备注';
COMMENT ON TABLE hospital_prescription IS '处方表';

DROP SEQUENCE IF EXISTS hospital_prescription_seq;
CREATE SEQUENCE hospital_prescription_seq START 1;

-- 9. 处方明细表
DROP TABLE IF EXISTS hospital_prescription_item;
CREATE TABLE hospital_prescription_item
(
    id              int8          NOT NULL,
    prescription_id int8          NULL     DEFAULT NULL,
    medicine_id     int8          NULL     DEFAULT NULL,
    quantity        int4          NOT NULL,
    price           numeric(10,2) NOT NULL,
    instructions    varchar(200)  NULL     DEFAULT NULL,
    creator         varchar(64)   NULL     DEFAULT '',
    create_time     timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater         varchar(64)   NULL     DEFAULT '',
    update_time     timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted         int2          NOT NULL DEFAULT 0,
    tenant_id       int8          NOT NULL DEFAULT 0
);

ALTER TABLE hospital_prescription_item
    ADD CONSTRAINT pk_hospital_prescription_item PRIMARY KEY (id);

COMMENT ON COLUMN hospital_prescription_item.id IS '明细ID';
COMMENT ON COLUMN hospital_prescription_item.prescription_id IS '所属处方ID';
COMMENT ON COLUMN hospital_prescription_item.medicine_id IS '药品ID';
COMMENT ON COLUMN hospital_prescription_item.quantity IS '数量';
COMMENT ON COLUMN hospital_prescription_item.price IS '当时的单价';
COMMENT ON COLUMN hospital_prescription_item.instructions IS '用法说明';
COMMENT ON TABLE hospital_prescription_item IS '处方明细表';

DROP SEQUENCE IF EXISTS hospital_prescription_item_seq;
CREATE SEQUENCE hospital_prescription_item_seq START 1;

-- 10. 收费账单表
DROP TABLE IF EXISTS hospital_bill;
CREATE TABLE hospital_bill
(
    id          int8          NOT NULL,
    visit_id    int8          NULL     DEFAULT NULL,
    patient_id  int8          NULL     DEFAULT NULL,
    total_amount numeric(10,2) NOT NULL,
    pay_amount  numeric(10,2) NOT NULL DEFAULT 0,
    pay_time    timestamp     NULL     DEFAULT NULL,
    pay_method  varchar(20)   NULL     DEFAULT NULL,
    status      varchar(20)   NOT NULL,
    creator     varchar(64)   NULL     DEFAULT '',
    create_time timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater     varchar(64)   NULL     DEFAULT '',
    update_time timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted     int2          NOT NULL DEFAULT 0,
    tenant_id   int8          NOT NULL DEFAULT 0
);

ALTER TABLE hospital_bill
    ADD CONSTRAINT pk_hospital_bill PRIMARY KEY (id);

COMMENT ON COLUMN hospital_bill.id IS '账单ID';
COMMENT ON COLUMN hospital_bill.visit_id IS '关联的就诊记录ID';
COMMENT ON COLUMN hospital_bill.patient_id IS '患者ID';
COMMENT ON COLUMN hospital_bill.total_amount IS '总金额';
COMMENT ON COLUMN hospital_bill.pay_amount IS '已支付金额';
COMMENT ON COLUMN hospital_bill.pay_time IS '支付时间';
COMMENT ON COLUMN hospital_bill.pay_method IS '支付方式：现金/医保/微信';
COMMENT ON COLUMN hospital_bill.status IS '状态：未支付/已支付/已退费';
COMMENT ON TABLE hospital_bill IS '收费账单表';

DROP SEQUENCE IF EXISTS hospital_bill_seq;
CREATE SEQUENCE hospital_bill_seq START 1;
