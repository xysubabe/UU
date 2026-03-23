# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

# 通用规范

## 1. 称呼规范
每次回复时都必须称呼用户为"苏"

## 2. 语言设置
- 默认使用中文回复
- 项目级别中记录的CLAUDE.md、项目中用户自己创建的agents的[agent-name].md文档、项目中用户自己创建的skills的skill.md以及项目中创建的业务相关的md文档，都使用中文来记录
- 代码注释使用中文

## 3. AI 协作协议
- 不确定就问，不要猜
- 修改后说明改了什么、为什么改
- 交付完整实现，不做简化版
- 不遗留 TODO

## 4. 进度可视化

所有任务执行时必须提供进度反馈，避免用户以为卡死：

- **批量操作** ：显示当前处理项，如 `[1/50] processing file.py`
- **长时间任务** ：定期输出进度，如 `✓ 已完成 50%`
- **任务跟踪** ：使用 TodoWrite 工具跟踪，实时更新状态

## 5. 禁止行为

- 长时间沉默不输出任何内容
- 只在最后给出结果，中间无反馈
- 批量操作时不显示当前处理项

# 业务规范
1. 项目中必须以业务需求文档[text](docs/superpowers/specs/2026-03-19-uu-design.md)为基础，业务需求文档中提及的功能必须在项目中实现，没有提及的功能不能在项目中实现。项目中必须符合业务需求文档中的功能描述，不能超出业务需求文档的范围。
2. 项目执行过程中采用 `superpowers` 工作注来进行管理项目。

## 项目概述

UU 跑腿是一款微信小程序应用，提供帮买、帮送、帮排队三种跑腿服务。

**技术栈：**
- 前端：微信小程序原生开发 (WXML + WXSS + JS/TS)
- 后端：Spring Boot 3.2.5 + MyBatis-Plus + Java 21
- 构建工具：Gradle 8.x
- 数据库：MySQL 8.0+
- 认证：微信小程序登录 + JWT
- 支付：微信支付 + Mock接口

**项目结构：**
```
uu-be/
├── src/main/java/com/uu/
│   ├── UuApplication.java              # 启动类
│   ├── annotation/                     # 注解（如 @DevOpsAuth 运维权限）
│   ├── config/                         # 配置类
│   ├── interceptor/                    # 拦截器
│   ├── controller/                     # 控制器层（auth、order、address、payment、user、devops）
│   ├── service/                        # 服务层及实现
│   ├── mapper/                         # 数据访问层
│   ├── entity/                         # 数据库实体
│   ├── dto/                            # 数据传输对象（request/response）
│   ├── enums/                          # 枚举类
│   ├── exception/                      # 异常处理
│   └── util/                           # 工具类
└── src/main/resources/
    ├── application.yml                 # 配置文件
    └── uploads/avatar/                 # 用户头像目录
```

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

### 拦截器机制
- `LoginInterceptor`：校验业务接口的 JWT Token（除 auth、file、region 接口外）
- `DevOpsAuthInterceptor`：校验运维接口的 `X-DevOps-Secret-Key` 请求头

### 数据库设计规范
- 表名：单数形式，t_ 前缀
- 所有表和字段添加中文注释
- 所有表包含 `schema_version` 字段用于乐观锁（使用 `@Version` 注解）
- 时间字段：`create_at`、`update_at` 使用 LocalDateTime 类型，通过 `@TableField(fill = FieldFill.INSERT/INSERT_UPDATE)` 自动填充
- 主键：使用雪花算法生成（使用 `@TableId(type = IdType.ASSIGN_ID)`）

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

## 运维接口

运维接口需要特殊的权限验证，通过 `@DevOpsAuth` 注解标记。

### 运维权限配置

在 `application.yml` 中配置运维密钥：
```yaml
devops:
  auth:
    user: zhangsan
    secretKey: 123456
```

### 运维接口调用

所有运维接口请求头必须包含：
```
X-DevOps-Secret-Key: 123456
```

### 可用运维接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/devops/mock-pay` | POST | Mock支付（模拟支付成功） |
| `/devops/worker/receive_order` | POST | 订单接单 |
| `/devops/worker/finish_order` | POST | 订单完成 |

### Mock支付说明

- 仅用于测试环境模拟支付流程
- 幂等性：订单状态非"待支付"时返回提示，不执行操作
- 支付成功后订单状态更新为"待接单"(200)