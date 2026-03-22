-- UU 跑腿数据库表结构初始化脚本
-- 日期: 2026-03-22

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS uu_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE uu_db;

-- ==================== 用户表 ====================
CREATE TABLE t_user (
    id BIGINT PRIMARY KEY COMMENT '用户ID（雪花算法生成）',
    openid VARCHAR(100) UNIQUE NOT NULL COMMENT '微信openid',
    unionid VARCHAR(100) COMMENT '微信unionid',
    nickname VARCHAR(50) COMMENT '昵称',
    avatar_url VARCHAR(500) COMMENT '头像URL（存储相对路径）',
    phone VARCHAR(20) COMMENT '手机号',
    status TINYINT DEFAULT 1 COMMENT '状态：1正常 0禁用',
    schema_version INT DEFAULT 1 COMMENT '乐观锁版本号',
    create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_openid (openid) COMMENT 'openid索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ==================== 地址表 ====================
CREATE TABLE t_address (
    id BIGINT PRIMARY KEY COMMENT '地址ID（雪花算法生成）',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    contact_name VARCHAR(50) NOT NULL COMMENT '联系人姓名',
    contact_phone VARCHAR(20) NOT NULL COMMENT '联系人电话',
    province VARCHAR(50) NOT NULL COMMENT '省份',
    city VARCHAR(50) NOT NULL COMMENT '城市',
    district VARCHAR(50) NOT NULL COMMENT '区/县',
    province_code VARCHAR(20) NOT NULL COMMENT '省份编码',
    city_code VARCHAR(20) NOT NULL COMMENT '城市编码',
    district_code VARCHAR(20) NOT NULL COMMENT '区/县编码',
    detail_address VARCHAR(200) NOT NULL COMMENT '详细地址',
    is_default TINYINT DEFAULT 0 COMMENT '是否默认地址：1是 0否',
    status TINYINT DEFAULT 1 COMMENT '状态：1启用 0删除',
    schema_version INT DEFAULT 1 COMMENT '乐观锁版本号',
    create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id) COMMENT '用户ID索引',
    INDEX idx_user_default (user_id, is_default) COMMENT '查询默认地址索引',
    INDEX idx_user_status (user_id, status) COMMENT '用户地址查询索引',
    FOREIGN KEY (user_id) REFERENCES t_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='地址表';

-- ==================== 订单表 ====================
CREATE TABLE t_order (
    id BIGINT PRIMARY KEY COMMENT '订单ID（雪花算法生成）',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    order_code VARCHAR(32) UNIQUE NOT NULL COMMENT '订单编号',
    service_type TINYINT NOT NULL COMMENT '服务类型：1帮买 2帮送 3帮排队',
    status TINYINT NOT NULL DEFAULT 100 COMMENT '订单状态：100待支付 200待接单 300进行中 600已完成 999已取消',
    title VARCHAR(100) COMMENT '订单标题',
    description VARCHAR(200) COMMENT '订单描述',
    start_address VARCHAR(200) COMMENT '起点地址',
    end_address VARCHAR(200) COMMENT '终点地址',
    end_contact_name VARCHAR(50) COMMENT '终点联系人姓名',
    end_contact_phone VARCHAR(20) COMMENT '终点联系人电话',
    amount DECIMAL(10,0) COMMENT '订单总金额',
    delivery_fee DECIMAL(10,0) COMMENT '跑腿费',
    payment_id BIGINT COMMENT '支付ID',
    schema_version INT DEFAULT 1 COMMENT '乐观锁版本号',
    create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id_create (user_id, create_at) COMMENT '用户订单列表查询索引',
    INDEX idx_status (status) COMMENT '状态索引',
    INDEX idx_order_code (order_code) COMMENT '订单编号索引',
    FOREIGN KEY (user_id) REFERENCES t_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- ==================== 订单日志表 ====================
CREATE TABLE t_order_log (
    id BIGINT PRIMARY KEY COMMENT '日志ID（雪花算法生成）',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    action VARCHAR(50) NOT NULL COMMENT '操作类型',
    description VARCHAR(200) COMMENT '操作描述',
    schema_version INT DEFAULT 1 COMMENT '乐观锁版本号',
    create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_order_id (order_id) COMMENT '订单ID索引',
    INDEX idx_order_create (order_id, create_at) COMMENT '按时间查询日志索引',
    FOREIGN KEY (order_id) REFERENCES t_order(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单日志表';

-- ==================== 支付表 ====================
CREATE TABLE t_payment (
    id BIGINT PRIMARY KEY COMMENT '支付ID（雪花算法生成）',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    order_code VARCHAR(32) NOT NULL COMMENT '订单编号',
    amount DECIMAL(10,0) NOT NULL COMMENT '支付金额',
    transaction_id VARCHAR(100) COMMENT '第三方交易ID',
    status TINYINT DEFAULT 0 COMMENT '支付状态：0待支付 1已支付 2已退款',
    schema_version INT DEFAULT 1 COMMENT '乐观锁版本号',
    create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_order_id (order_id) COMMENT '订单ID索引',
    INDEX idx_order_code (order_code) COMMENT '订单编号索引',
    FOREIGN KEY (order_id) REFERENCES t_order(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='支付表';

-- ==================== Mock支付表 ====================
CREATE TABLE t_mock_payment (
    id BIGINT PRIMARY KEY COMMENT 'Mock支付ID（雪花算法生成）',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    order_code VARCHAR(32) NOT NULL COMMENT '订单编号',
    amount DECIMAL(10,0) NOT NULL COMMENT 'Mock支付金额',
    status TINYINT DEFAULT 0 COMMENT '状态：0待支付 1已支付',
    mock_result JSON COMMENT 'Mock结果',
    schema_version INT DEFAULT 1 COMMENT '乐观锁版本号',
    create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_order_id (order_id) COMMENT '订单ID索引',
    INDEX idx_order_code (order_code) COMMENT '订单编号索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Mock支付表';

-- ==================== 省市区表 ====================
CREATE TABLE t_region (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '地区ID（数据库自增）',
    code VARCHAR(20) UNIQUE NOT NULL COMMENT '地区编码',
    name VARCHAR(50) NOT NULL COMMENT '地区名称',
    level TINYINT NOT NULL COMMENT '层级：1省 2市 3区县',
    parent_code VARCHAR(20) COMMENT '父级地区编码',
    schema_version INT DEFAULT 1 COMMENT '乐观锁版本号',
    create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_parent_code (parent_code) COMMENT '父级编码索引',
    INDEX idx_level (level) COMMENT '层级索引',
    INDEX idx_parent_level (parent_code, level) COMMENT '父级编码和层级联合索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='省市区表';