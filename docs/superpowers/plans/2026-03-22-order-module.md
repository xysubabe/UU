# 订单模块实施计划

**日期：** 2026-03-22
**目标：** 实现订单的下单、查询、取消功能

## 概述

实现订单管理功能，包括创建订单、获取订单列表、获取进行中订单、获取订单详情、取消订单。

## 接口列表

1. `POST /order/create` - 创建订单
2. `GET /order/list` - 获取订单列表（可选status筛选）
3. `GET /order/ongoing` - 获取进行中订单（首页）
4. `GET /order/detail/{orderId}` - 获取订单详情
5. `PUT /order/cancel/{orderId}` - 取消订单

---

## 任务分解

### Task 1: 创建请求DTO和响应DTO

**文件：**
- 更新: `src/main/java/com/uu/dto/request/OrderCreateRequest.java`
- 创建: `src/main/java/com/uu/dto/response/OrderDetailResponse.java`

**实现要点：**
- OrderCreateRequest: 包含服务类型、地址ID、描述、金额
- OrderDetailResponse: 包含订单详情和订单日志

**验证：** `./gradlew build` 编译成功

---

### Task 2: 创建OrderLogService接口和实现

**文件：**
- 创建: `src/main/java/com/uu/service/OrderLogService.java`
- 创建: `src/main/java/com/uu/service/impl/OrderLogServiceImpl.java`

**实现要点：**
- `createLog(Long orderId, String action, String description)` - 创建订单日志
- `getOrderLogs(Long orderId)` - 获取订单日志列表

**验证：** `./gradlew build` 编译成功

---

### Task 3: 创建OrderService接口和实现

**文件：**
- 创建: `src/main/java/com/uu/service/OrderService.java`
- 创建: `src/main/java/com/uu/service/impl/OrderServiceImpl.java`

**实现要点：**
- `createOrder(Long userId, OrderCreateRequest request)` - 创建订单
- `getOrderList(Long userId, Integer status, Integer page, Integer pageSize)` - 获取订单列表
- `getOngoingOrders(Long userId, Integer page, Integer pageSize)` - 获取进行中订单
- `getOrderDetail(Long userId, Long orderId)` - 获取订单详情
- `cancelOrder(Long userId, Long orderId)` - 取消订单
- `validateOwnership(Long userId, Long orderId)` - 验证订单所有权
- `generateOrderCode(Long orderId)` - 生成订单编号

**业务规则：**
- 订单编号格式：`UU` + 订单ID
- 订单标题：取description前10个字符
- 订单状态默认为待支付(100)
- 只能取消待支付(100)和待接单(200)状态的订单
- 地址验证：验证地址是否属于当前用户

**验证：** `./gradlew build` 编译成功

---

### Task 4: 创建OrderController

**文件：**
- 创建: `src/main/java/com/uu/controller/order/OrderController.java`

**实现要点：**
- `POST /order/create` - 创建订单
- `GET /order/list` - 获取订单列表
- `GET /order/ongoing` - 获取进行中订单
- `GET /order/detail/{orderId}` - 获取订单详情
- `PUT /order/cancel/{orderId}` - 取消订单

**验证：** `./gradlew build` 编译成功

---

### Task 5: 测试验证

完成所有任务后，验证：
1. 可以创建三种服务类型的订单
2. 可以获取订单列表（支持状态筛选）
3. 可以获取进行中订单
4. 可以获取订单详情（含日志）
5. 可以取消待支付和待接单状态的订单
6. 订单标题正确生成
7. 地址验证正确
8. 项目编译成功

---

## 执行顺序

按 Task 1 → Task 2 → Task 3 → Task 4 → Task 5 顺序执行

每个 Task 完成后提交 Git：
```bash
git add .
git commit -m "feat: [中文描述]"
```