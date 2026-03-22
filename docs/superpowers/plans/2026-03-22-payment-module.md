# 支付模块实施计划

**日期：** 2026-03-22
**目标：** 实现支付创建、支付回调、Mock支付功能

## 概述

实现支付管理功能，包括创建支付、支付回调、Mock支付（运维专用）。

## 接口列表

1. `POST /payment/create` - 创建支付
2. `POST /payment/callback` - 支付回调
3. `POST /payment/mock-pay` - Mock支付（运维专用）

---

## 任务分解

### Task 1: 创建请求DTO和响应DTO

**文件：**
- 创建: `src/main/java/com/uu/dto/request/PaymentCreateRequest.java`
- 创建: `src/main/java/com/uu/dto/response/PaymentResponse.java`

**实现要点：**
- PaymentCreateRequest: 包含订单ID、支付金额
- PaymentResponse: 包含支付ID、支付参数

**验证：** `gradle build` 编译成功

---

### Task 2: 创建PaymentService接口和实现

**文件：**
- 创建: `src/main/java/com/uu/service/PaymentService.java`
- 创建: `src/main/java/com/uu/service/impl/PaymentServiceImpl.java`

**实现要点：**
- `createPayment(Long userId, PaymentCreateRequest request)` - 创建支付
- `handleCallback(String transactionId, String orderCode, String amount)` - 处理支付回调
- `mockPay(Long userId, Long orderId)` - Mock支付（运维专用）

**业务规则：**
- 验证订单状态是否为"待支付"(100)
- 支付金额必须与订单表的 amount 字段一致
- 支付成功后更新订单状态为"待接单"(200)
- 支付成功后记录"已支付"日志
- Mock支付只有订单状态为"待支付"(100)时才允许执行

**验证：** `gradle build` 编译成功

---

### Task 3: 创建PaymentController

**文件：**
- 创建: `src/main/java/com/uu/controller/payment/PaymentController.java`

**实现要点：**
- `POST /payment/create` - 创建支付
- `POST /payment/callback` - 支付回调
- `POST /payment/mock-pay` - Mock支付

**验证：** `gradle build` 编译成功

---

### Task 4: 测试验证

完成所有任务后，验证：
1. 可以创建支付
2. 支付金额与订单金额一致时才允许支付
3. 只有待支付状态可以创建支付
4. 支付成功后订单状态更新为待接单
5. Mock支付功能正常
6. 项目编译成功

---

## 执行顺序

按 Task 1 → Task 2 → Task 3 → Task 4 顺序执行

每个 Task 完成后提交 Git：
```bash
git add .
git commit -m "feat: [中文描述]"
```