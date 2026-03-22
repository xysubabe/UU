# 地址模块实施计划

**日期：** 2026-03-22
**目标：** 实现地址管理的增删改查功能

## 概述

实现地址管理功能，包括获取地址列表、获取默认地址、新增地址、更新地址、删除地址。每个用户最多保存3个启用状态的地址，同一用户有且仅有一个默认地址。

## 接口列表

1. `GET /address/list` - 获取地址列表
2. `GET /address/default` - 获取默认地址
3. `POST /address/create` - 新增地址
4. `PUT /address/update` - 更新地址
5. `DELETE /address/delete/{addressId}` - 删除地址

---

## 任务分解

### Task 1: 创建AddressService接口和实现

**文件：**
- 创建: `src/main/java/com/uu/service/AddressService.java`
- 创建: `src/main/java/com/uu/service/impl/AddressServiceImpl.java`

**实现要点：**
- `getAddressList(Long userId)` - 获取地址列表（分页）
- `getDefaultAddress(Long userId)` - 获取默认地址
- `createAddress(Long userId, AddressCreateRequest request)` - 新增地址
- `updateAddress(Long userId, AddressUpdateRequest request)` - 更新地址
- `deleteAddress(Long userId, Long addressId)` - 删除地址（逻辑删除）
- `validateOwnership(Long userId, Long addressId)` - 验证地址所有权

**业务规则：**
- 手机号脱敏：保留前3后4，中间用4个*替代
- 每个用户最多保存3个启用状态的地址
- 同一用户有且仅有一个默认地址
- 设置默认地址时，自动取消其他地址的默认状态
- 删除采用逻辑删除（status = 0）

**验证：** `./gradlew build` 编译成功

---

### Task 2: 创建AddressController

**文件：**
- 创建: `src/main/java/com/uu/controller/address/AddressController.java`

**实现要点：**
- `GET /address/list` - 获取地址列表
- `GET /address/default` - 获取默认地址
- `POST /address/create` - 新增地址
- `PUT /address/update` - 更新地址
- `DELETE /address/delete/{addressId}` - 删除地址

**验证：** `./gradlew build` 编译成功

---

### Task 3: 测试验证

完成所有任务后，验证：
1. 可以获取当前用户的地址列表
2. 可以获取默认地址
3. 新增地址时验证规则正确
4. 更新地址时验证规则正确
5. 删除地址采用逻辑删除
6. 项目编译成功

---

## 执行顺序

按 Task 1 → Task 2 → Task 3 顺序执行

每个 Task 完成后提交 Git：
```bash
git add .
git commit -m "feat: [中文描述]"
```