-- =============================================
-- 模块一：AI 增强医疗知识图谱 — 4 张业务表 DDL
-- MySQL 8.0
-- 执行前请确认已导入 hospital.sql（基础 10 张表）
-- =============================================

SET NAMES utf8mb4;

-- 若你的库名不是 hospital，请修改下一行
-- ---------- 1) 疾病字典 ----------
CREATE TABLE IF NOT EXISTS hospital_disease (
    id               BIGINT          PRIMARY KEY AUTO_INCREMENT COMMENT '疾病ID',
    name             VARCHAR(100)    NOT NULL COMMENT '疾病名称',
    icd_code         VARCHAR(20)     DEFAULT NULL COMMENT 'ICD-10编码',
    category         VARCHAR(50)     DEFAULT NULL COMMENT '分类',
    dept_id          BIGINT          DEFAULT NULL COMMENT '所属科室ID',
    description      TEXT            COMMENT '疾病简介',
    typical_symptoms TEXT            COMMENT '典型症状文本',
    is_common        SMALLINT        DEFAULT 0 COMMENT '是否常见病 1=是 0=否',
    creator          VARCHAR(64)     DEFAULT '',
    create_time      DATETIME        DEFAULT CURRENT_TIMESTAMP,
    updater          VARCHAR(64)     DEFAULT '',
    update_time      DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted          BIT(1)          DEFAULT b'0',
    tenant_id        BIGINT          DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='疾病字典';

-- ---------- 2) 症状字典 ----------
CREATE TABLE IF NOT EXISTS hospital_symptom (
    id          BIGINT          PRIMARY KEY AUTO_INCREMENT COMMENT '症状ID',
    name        VARCHAR(100)    NOT NULL COMMENT '症状名称',
    location    VARCHAR(50)     DEFAULT NULL COMMENT '部位',
    type        VARCHAR(50)     DEFAULT NULL COMMENT '症状类型',
    description VARCHAR(500)    DEFAULT NULL COMMENT '症状描述',
    creator     VARCHAR(64)     DEFAULT '',
    create_time DATETIME        DEFAULT CURRENT_TIMESTAMP,
    updater     VARCHAR(64)     DEFAULT '',
    update_time DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted     BIT(1)          DEFAULT b'0',
    tenant_id   BIGINT          DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='症状字典';

-- ---------- 3) 疾病-症状关联 ----------
CREATE TABLE IF NOT EXISTS hospital_disease_symptom (
    id          BIGINT          PRIMARY KEY AUTO_INCREMENT COMMENT '关联ID',
    disease_id  BIGINT          NOT NULL COMMENT '疾病ID',
    symptom_id  BIGINT          NOT NULL COMMENT '症状ID',
    strength    TINYINT         DEFAULT 1 COMMENT '1=主要 2=次要 3=偶见',
    reference   VARCHAR(500)    DEFAULT NULL COMMENT '文献/指南依据',
    creator     VARCHAR(64)     DEFAULT '',
    create_time DATETIME        DEFAULT CURRENT_TIMESTAMP,
    updater     VARCHAR(64)     DEFAULT '',
    update_time DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted     BIT(1)          DEFAULT b'0',
    tenant_id   BIGINT          DEFAULT 0,
    INDEX idx_ds_disease (disease_id),
    INDEX idx_ds_symptom (symptom_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='疾病-症状关联';

-- ---------- 4) 疾病-药品关联 ----------
CREATE TABLE IF NOT EXISTS hospital_disease_medicine (
    id          BIGINT          PRIMARY KEY AUTO_INCREMENT COMMENT '关联ID',
    disease_id  BIGINT          NOT NULL COMMENT '疾病ID',
    medicine_id BIGINT          NOT NULL COMMENT '药品ID',
    usage_type  TINYINT         DEFAULT 1 COMMENT '1=首选 2=备选 3=辅助',
    creator     VARCHAR(64)     DEFAULT '',
    create_time DATETIME        DEFAULT CURRENT_TIMESTAMP,
    updater     VARCHAR(64)     DEFAULT '',
    update_time DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted     BIT(1)          DEFAULT b'0',
    tenant_id   BIGINT          DEFAULT 0,
    INDEX idx_dm_disease (disease_id),
    INDEX idx_dm_medicine (medicine_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='疾病-药品关联';
