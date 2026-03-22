# 数据库表设计和初始化实施计划

**日期：** 2026-03-22
**目标：** 创建所有业务数据表并初始化必要数据

## 概述

基于设计文档 `2026-03-19-uu-design.md`，创建7个数据表并初始化省市区数据。

## 数据表列表

1. t_user - 用户表
2. t_address - 地址表
3. t_order - 订单表
4. t_order_log - 订单日志表
5. t_payment - 支付表
6. t_mock_payment - Mock支付表
7. t_region - 省市区表（含初始化数据）

---

## 任务分解

### Task 1: 创建数据库脚本目录和初始化SQL文件

**文件：**
- 创建: `uu-be/src/main/resources/sql/schema.sql` - 表结构定义
- 创建: `uu-be/src/main/resources/sql/data/region_init.sql` - 省市区初始化数据

**步骤：**
1. 创建 `sql` 和 `sql/data` 目录
2. 创建 `schema.sql` 包含所有表定义
3. 创建 `region_init.sql` 包含省市区数据（31省、300+市、2800+区县）

**验证：** 文件创建成功，SQL语法正确

---

### Task 2: 创建实体类 (Entity)

**文件：**
- 创建: `src/main/java/com/uu/entity/User.java`
- 创建: `src/main/java/com/uu/entity/Address.java`
- 创建: `src/main/java/com/uu/entity/Order.java`
- 创建: `src/main/java/com/uu/entity/OrderLog.java`
- 创建: `src/main/java/com/uu/entity/Payment.java`
- 创建: `src/main/java/com/uu/entity/MockPayment.java`
- 创建: `src/main/java/com/uu/entity/Region.java`

**实现要点：**
- 使用 MyBatis-Plus 注解: `@TableName`, `@TableId`, `@TableField`
- 主键使用 `@TableId(type = IdType.ASSIGN_ID)` 雪花算法
- 乐观锁使用 `@Version` 注解在 `schema_version` 字段
- 时间字段使用 `LocalDateTime` 类型
- 枚举字段在实体中定义，由 MyBatis-Plus 自动转换

**验证：** `./gradlew build` 编译成功

---

### Task 3: 创建Mapper接口

**文件：**
- 创建: `src/main/java/com/uu/mapper/UserMapper.java`
- 创建: `src/main/java/com/uu/mapper/AddressMapper.java`
- 创建: `src/main/java/com/uu/mapper/OrderMapper.java`
- 创建: `src/main/java/com/uu/mapper/OrderLogMapper.java`
- 创建: `src/main/java/com/uu/mapper/PaymentMapper.java`
- 创建: `src/main/java/com/uu/mapper/MockPaymentMapper.java`
- 创建: `src/main/java/com/uu/mapper/RegionMapper.java`

**实现要点：**
- 继承 `BaseMapper<T>`
- 使用 `@Mapper` 注解（或通过 `@MapperScan` 扫描）

**验证：** `./gradlew build` 编译成功

---

### Task 4: 创建DTO类

**请求DTO (request):**
- 创建: `src/main/java/com/uu/dto/request/OrderCreateRequest.java`
- 创建: `src/main/java/com/uu/dto/request/AddressCreateRequest.java`
- 创建: `src/main/java/com/uu/dto/request/AddressUpdateRequest.java`

**响应DTO (response):**
- 创建: `src/main/java/com/uu/dto/response/UserResponse.java`
- 创建: `src/main/java/com/uu/dto/response/AddressResponse.java`
- 创建: `src/main/java/com/uu/dto/response/OrderResponse.java`
- 创建: `src/main/java/com/uu/dto/response/RegionResponse.java`
- 创建: `src/main/java/com/uu/dto/response/OrderListResponse.java`

**验证：** `./gradlew build` 编译成功

---

### Task 5: 执行数据库初始化

**步骤：**
1. 确保MySQL服务运行
2. 创建数据库 `uu_db` (如果不存在)
3. 执行 `schema.sql` 创建表结构
4. 执行 `region_init.sql` 初始化省市区数据

**命令：**
```bash
mysql -u root -p < uu-be/src/main/resources/sql/schema.sql
mysql -u root -p uu_db < uu-be/src/main/resources/sql/data/region_init.sql
```

**验证：**
```sql
-- 检查表是否创建成功
SHOW TABLES;

-- 检查省市区数据
SELECT COUNT(*) FROM t_region WHERE level = 1; -- 应为31
SELECT COUNT(*) FROM t_region WHERE level = 2; -- 应为300+
SELECT COUNT(*) FROM t_region WHERE level = 3; -- 应为2800+
```

---

## 执行顺序

按 Task 1 → Task 2 → Task 3 → Task 4 → Task 5 顺序执行

每个 Task 完成后提交 Git：
```bash
git add .
git commit -m "feat: [中文描述]"
```

---

## 测试验证

完成所有任务后，验证：
1. 所有表结构正确
2. 省市区数据完整
3. 项目编译成功
4. 应用可以正常启动（需配置数据库连接）