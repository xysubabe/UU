# 用户模块实施计划

**日期：** 2026-03-22
**目标：** 实现用户登录、用户信息管理和头像上传功能

## 概述

实现用户认证、用户信息查询和头像上传功能，包括微信小程序登录、JWT Token生成、登录校验拦截器等。

## 接口列表

1. `POST /auth/wechat-login` - 微信登录
2. `GET /user/info` - 获取用户信息
3. `POST /user/avatar/update` - 更新用户头像
4. `GET /file/avatar/{userId}` - 获取用户头像（公开访问）

---

## 任务分解

### Task 1: 创建登录校验拦截器

**文件：**
- 创建: `src/main/java/com/uu/interceptor/LoginInterceptor.java`
- 更新: `src/main/java/com/uu/config/WebConfig.java`

**实现要点：**
- 从请求头获取 `Authorization: Bearer {token}`
- 使用 JwtUtil 验证 Token
- 验证失败返回 401 状态码
- 验证成功后将 userId 存入请求属性

**排除路径：**
- `/auth/**` - 认证接口
- `/file/**` - 文件接口
- `/region/**` - 省市区接口

**验证：** `./gradlew build` 编译成功

---

### Task 2: 创建请求DTO和响应DTO

**文件：**
- 创建: `src/main/java/com/uu/dto/request/WechatLoginRequest.java`
- 创建: `src/main/java/com/uu/dto/response/LoginResponse.java`

**实现要点：**
- WechatLoginRequest: 包含 code 字段
- LoginResponse: 包含 token 和 userInfo

**验证：** `./gradlew build` 编译成功

---

### Task 3: 创建UserService接口和实现

**文件：**
- 创建: `src/main/java/com/uu/service/UserService.java`
- 创建: `src/main/java/com/uu/service/impl/UserServiceImpl.java`

**实现要点：**
- `wechatLogin(String code)` - 微信登录
  - 调用微信API获取openid
  - 查询用户是否存在，不存在则创建
  - 生成JWT Token
  - 返回登录响应
- `getUserInfo(Long userId)` - 获取用户信息
- `updateAvatar(Long userId, String avatarUrl)` - 更新头像URL

**验证：** `./gradlew build` 编译成功

---

### Task 4: 创建AuthController

**文件：**
- 创建: `src/main/java/com/uu/controller/auth/AuthController.java`

**实现要点：**
- `POST /auth/wechat-login` - 微信登录接口
  - 调用 UserService.wechatLogin()
  - 返回 ApiResponse<LoginResponse>

**验证：** `./gradlew build` 编译成功

---

### Task 5: 创建UserController

**文件：**
- 创建: `src/main/java/com/uu/controller/user/UserController.java`

**实现要点：**
- `GET /user/info` - 获取用户信息
  - 从请求头获取 userId
  - 调用 UserService.getUserInfo()
  - 返回 ApiResponse<UserResponse>

**验证：** `./gradlew build` 编译成功

---

### Task 6: 创建文件上传工具类

**文件：**
- 创建: `src/main/java/com/uu/util/FileUtil.java`

**实现要点：**
- `uploadAvatar(MultipartFile file, Long userId)` - 上传头像
  - 验证文件大小（最大5MB）
  - 验证文件格式（jpg、jpeg、png）
  - 验证文件内容（魔数）
  - 生成文件名：`{userId}_{timestamp}_{random}.{ext}`
  - 存储路径：`uploads/avatar/{年}/{月}/`
  - 返回相对路径

**验证：** `./gradlew build` 编译成功

---

### Task 7: 创建头像上传和访问接口

**文件：**
- 创建: `src/main/java/com/uu/controller/file/FileController.java`

**实现要点：**
- `POST /user/avatar/update` - 更新用户头像
  - 需要登录校验
  - 调用 FileUtil.uploadAvatar()
  - 更新用户 avatarUrl 字段
  - 返回相对路径

- `GET /file/avatar/{userId}` - 获取用户头像
  - 公开访问（不需要登录校验）
  - 查询用户获取 avatarUrl
  - 返回文件流
  - 未设置头像则返回默认头像

**验证：** `./gradlew build` 编译成功

---

## 执行顺序

按 Task 1 → Task 2 → Task 3 → Task 4 → Task 5 → Task 6 → Task 7 顺序执行

每个 Task 完成后提交 Git：
```bash
git add .
git commit -m "feat: [中文描述]"
```

---

## 测试验证

完成所有任务后，验证：
1. 微信登录接口返回正确的 token 和用户信息
2. 获取用户信息接口返回正确的用户数据
3. 头像上传功能正常工作
4. 头像访问接口返回正确的文件
5. 未登录访问需要登录的接口返回 401
6. 项目编译成功