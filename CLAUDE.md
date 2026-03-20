# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

UU 跑腿是一款微信小程序应用，提供帮买、帮送、帮排队三种跑腿服务。

**技术栈：**
- 前端：微信小程序原生开发 (WXML + WXSS + JS/TS)
- 后端：Spring Boot + MyBatis-Plus + Java 17+
- 构建工具：Gradle 8.x
- 数据库：MySQL 8.0+
- 认证：微信小程序登录 + JWT
- 支付：微信支付 + Mock接口

## 常用命令

```bash
# 构建项目
./gradlew build

# 运行项目
./gradlew bootRun

# 运行测试
./gradlew test

# 运行单个测试类
./gradlew test --tests ClassName

# 代码检查
./gradlew check
```

## 技术架构

### 后端架构
- Controller 层：处理 HTTP 请求，参数验证
- Service 层：业务逻辑处理
- Mapper 层：数据访问 (MyBatis-Plus)
- DTO 层：数据传输对象 (Request/Response)
- Entity 层：数据库实体映射

### 数据库设计规范
- 表名：单数形式，t_ 前缀
- 所有表和字段添加中文注释
- 所有表包含 `schema_version` 字段用于乐观锁
- 时间字段：`create_at`、`update_at` 使用 LocalDateTime 类型

### 关键枚举
- 服务类型：1-帮买、2-帮送、3-帮排队
- 订单状态：100-待支付、200-待接单、300-进行中、600-已完成、999-已取消
- 支付状态：0-待支付、1-已支付、2-已退款

## 重要业务规则

### 订单规则
- 费用由用户自行输入，系统不做计算
- 订单编号格式：`UU + 订单ID`
- 状态流转：待支付 → 待接单 → 进行中 → 已完成 / 已取消
- 待支付、待接单状态可取消，进行中及以后不可取消
- 订单描述最大 200 字符，为非必填字段

### 地址规则
- 每个用户最多保存 3 个地址
- 同一用户有且仅有一个默认地址
- 纯手写方式，不联动地图API
- 联系人电话 11 位数字

### 数据隔离
- 所有业务接口基于登录用户 ID 进行数据过滤
- 验证数据所有权，防止越权访问

## API 接口规范

**基础 URL：** `https://api.uu.com/v1`

**请求头：**
```
Content-Type: application/json
Authorization: Bearer {jwt_token}
```

**响应格式：**
```json
{
  "code": 0,
  "message": "成功",
  "data": {},
  "timestamp": 1710838800000
}
```

**登录校验：**
- 认证接口（如 `/auth/wechat-login`）：不需要登录校验
- 文件接口（如 `/file/avatar/{userId}`）：公开访问
- 其他业务接口：需要登录校验

## 错误处理

**错误码体系：**
- 1xxx：通用错误 (1000-未知错误、1001-参数错误、1002-未授权、1003-禁止访问、1004-资源不存在)
- 2xxx：用户错误 (2001-用户不存在、2002-用户已被禁用)
- 3xxx：订单错误 (3001-订单不存在、3002-订单状态错误、3005-订单不可取消)
- 4xxx：地址错误 (4001-地址不存在、4002-地址数量超限)
- 5xxx：支付错误 (5001-支付失败)

## 下单表单必填规则

**必填字段：**
- 帮买订单：购买地址、收货地址、跑腿费用
- 帮送订单：取件地址、送达地址、跑腿费用
- 帮排队订单：排队地址、跑腿费用

**非必填字段：**
- 帮买订单：购买清单
- 帮送订单：物品描述
- 帮排队订单：排队事项

## 乐观锁使用

所有表包含 `schema_version` 字段，使用 `@Version` 注解进行乐观锁控制。更新时自动检查版本号，冲突时抛出 `OptimisticLockerException`。

## 文档位置

完整设计文档位于：`docs/superpowers/specs/2026-03-19-uu-design.md`