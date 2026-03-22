# Backend Foundation Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Initialize Spring Boot project with configuration, common utilities, base classes, and enums

**Architecture:** Standard Spring Boot 3.2.5 + MyBatis-Plus layered architecture with JPA-style entities

**Tech Stack:** Spring Boot 3.2.5, MyBatis-Plus 3.5.5, Gradle 8.x, Java 21, MySQL 8.0+

---

## Project Structure Overview

```
uu-backend/
├── build.gradle
├── src/main/java/com/uu/
│   ├── UuApplication.java
│   ├── config/
│   │   ├── MybatisPlusConfig.java
│   │   ├── WebConfig.java
│   │   └── WechatConfig.java
│   ├── annotation/
│   │   └── DevOpsAuth.java
│   ├── interceptor/
│   │   └── DevOpsAuthInterceptor.java
│   ├── exception/
│   │   ├── BusinessException.java
│   │   └── GlobalExceptionHandler.java
│   ├── dto/
│   │   ├── request/
│   │   └── response/
│   ├── enums/
│   ├── util/
│   │   ├── JwtUtil.java
│   │   ├── SnowflakeIdGenerator.java
│   │   └── ValidatorUtil.java
├── src/main/resources/
│   ├── application.yml
│   ├── logback-spring.xml
│   └── uploads/
│       └── avatar/
└── src/test/java/com/uu/
```

---

### Task 1: Project Initialization

**Files:**
- Create: `build.gradle`
- Create: `settings.gradle`
- Create: `src/main/java/com/uu/UuApplication.java`

- [ ] **Step 1: Create build.gradle with dependencies**

```gradle
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.2.5'
    id 'io.spring.dependency-management' version '1.1.5'
}

group = 'com.uu'
version = '1.0.0'
sourceCompatibility = '21'

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'org.springframework.boot:spring-boot-starter-aop'

    // MyBatis-Plus
    implementation 'com.baomidou:mybatis-plus-boot-starter:3.5.5'
    implementation 'com.baomidou:mybatis-plus-extension:3.5.5'

    // Database
    implementation 'com.mysql:mysql-connector-j'
    implementation 'com.zaxxer:HikariCP'

    // JWT
    implementation 'io.jsonwebtoken:jjwt-api:0.12.3'
    runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.12.3'
    runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.12.3'

    // WeChat SDK
    implementation 'com.github.binarywang:weixin-java-miniapp:4.6.0'
    implementation 'com.github.binarywang:weixin-java-pay:4.6.0'

    // Tools
    implementation 'org.apache.commons:commons-lang3'
    implementation 'cn.hutool:hutool-all:5.8.24'

    // Lombok
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'

    // Test
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.mockito:mockito-core'
}

test {
    useJUnitPlatform()
}
```

- [ ] **Step 2: Create settings.gradle**

```gradle
rootProject.name = 'uu-backend'
```

- [ ] **Step 3: Create main application class**

```java
package com.uu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UuApplication {
    public static void main(String[] args) {
        SpringApplication.run(UuApplication.class, args);
    }
}
```

- [ ] **Step 4: Test application starts**

Run: `./gradlew bootRun`
Expected: Application starts without errors, listening on port 8080

- [ ] **Step 5: Commit**

```bash
git add build.gradle settings.gradle src/main/java/com/uu/UuApplication.java
git commit -m "feat: initialize Spring Boot project with Gradle"
```

---

### Task 2: Application Configuration

**Files:**
- Create: `src/main/resources/application.yml`
- Create: `src/main/resources/logback-spring.xml`

- [ ] **Step 1: Create application.yml**

```yaml
server:
  port: 8080
  servlet:
    context-path: /v1

spring:
  application:
    name: uu-backend

  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/uu_db?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: password
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000

  jackson:
    default-property-inclusion: non_null
    time-zone: GMT+8
    date-format: yyyy-MM-dd HH:mm:ss
    serialization:
      write-dates-as-timestamps: false

mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
    map-underscore-to-camel-case: true
  global-config:
    db-config:
      id-type: assign_id
      logic-delete-field: deleted
      logic-not-delete-value: 1
      logic-delete-value: 0

# JWT配置
jwt:
  secret: uu-secret-key-very-long-random-string-at-least-256-bits
  expiration: 604800000  # 7天（毫秒）

# 微信小程序配置
wechat:
  miniapp:
    appid: your-miniapp-appid
    secret: your-miniapp-secret
  pay:
    appid: your-pay-appid
    mch-id: your-mch-id
    api-key: your-api-key
    api-v3-key: your-api-v3-key
    cert-path: /path/to/cert
    notify-url: https://api.uu.com/v1/payment/callback

# DevOps配置
devops:
  auth:
    user: zhangsan
    secretKey: 123456

# 文件上传配置
file:
  upload:
    base-path: uploads
    avatar-path: uploads/avatar
    max-size: 5242880  # 5MB

# 日志配置
logging:
  level:
    com.uu: debug
    com.baomidou.mybatisplus: debug
```

- [ ] **Step 2: Create logback-spring.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <springProperty scope="context" name="APP_NAME" source="spring.application.name"/>
    <property name="LOG_PATH" value="logs"/>

    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n</pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>

    <appender name="FILE_INFO" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_PATH}/${APP_NAME}-info.log</file>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n</pattern>
            <charset>UTF-8</charset>
        </encoder>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${LOG_PATH}/${APP_NAME}-info.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
    </appender>

    <appender name="FILE_ERROR" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_PATH}/${APP_NAME}-error.log</file>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n</pattern>
            <charset>UTF-8</charset>
        </encoder>
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>ERROR</level>
        </filter>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${LOG_PATH}/${APP_NAME}-error.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>90</maxHistory>
        </rollingPolicy>
    </appender>

    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE_INFO"/>
        <appender-ref ref="FILE_ERROR"/>
    </root>
</configuration>
```

- [ ] **Step 3: Commit**

```bash
git add src/main/resources/application.yml src/main/resources/logback-spring.xml
git commit -m "feat: add application configuration and logging"
```

---

### Task 3: MyBatis-Plus Configuration

**Files:**
- Create: `src/main/java/com/uu/config/MybatisPlusConfig.java`
- Create: `src/main/java/com/uu/config/WebConfig.java`

- [ ] **Step 1: Create MybatisPlusConfig**

```java
package com.uu.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockCharsetInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.uu.mapper")
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 乐观锁
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        // 分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        // 字符编码拦截器
        interceptor.addInnerInterceptor(new BlockCharsetInnerInterceptor());
        return interceptor;
    }
}
```

- [ ] **Step 2: Create WebConfig with DevOps interceptor**

```java
package com.uu.config;

import com.uu.interceptor.DevOpsAuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private DevOpsAuthInterceptor devOpsAuthInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(devOpsAuthInterceptor)
                .addPathPatterns("/devops/**");
    }
}
```

- [ ] **Step 3: Commit**

```bash
git add src/main/java/com/uu/config/MybatisPlusConfig.java src/main/java/com/uu/config/WebConfig.java
git commit -m "feat: configure MyBatis-Plus and Web MVC"
```

---

### Task 4: DevOps Authentication

**Files:**
- Create: `src/main/java/com/uu/annotation/DevOpsAuth.java`
- Create: `src/main/java/com/uu/interceptor/DevOpsAuthInterceptor.java`

- [ ] **Step 1: Create DevOpsAuth annotation**

```java
package com.uu.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DevOpsAuth {
}
```

- [ ] **Step 2: Create DevOpsAuthInterceptor**

```java
package com.uu.interceptor;

import com.uu.annotation.DevOpsAuth;
import com.uu.exception.BusinessException;
import com.uu.enums.ErrorCodeEnum;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class DevOpsAuthInterceptor implements HandlerInterceptor {

    @Value("${devops.auth.secretKey}")
    private String configSecretKey;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            DevOpsAuth annotation = handlerMethod.getMethodAnnotation(DevOpsAuth.class);

            if (annotation != null) {
                String secretKey = request.getHeader("X-DevOps-Secret-Key");

                if (secretKey == null || !secretKey.equals(configSecretKey)) {
                    throw new BusinessException(ErrorCodeEnum.FORBIDDEN);
                }
            }
        }
        return true;
    }
}
```

- [ ] **Step 3: Commit**

```bash
git add src/main/java/com/uu/annotation/DevOpsAuth.java src/main/java/com/uu/interceptor/DevOpsAuthInterceptor.java
git commit -m "feat: add DevOps authentication annotation and interceptor"
```

---

### Task 5: Exception Handling

**Files:**
- Create: `src/main/java/com/uu/exception/BusinessException.java`
- Create: `src/main/java/com/uu/exception/GlobalExceptionHandler.java`

- [ ] **Step 1: Create BusinessException**

```java
package com.uu.exception;

import com.uu.enums.ErrorCodeEnum;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final int code;
    private final String message;

    public BusinessException(ErrorCodeEnum errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BusinessException(ErrorCodeEnum errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
        this.message = message;
    }
}
```

- [ ] **Step 2: Create GlobalExceptionHandler**

```java
package com.uu.exception;

import com.baomidou.mybatisplus.core.exceptions.OptimisticLockerException;
import com.uu.dto.response.ApiResponse;
import com.uu.enums.ErrorCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<?>> handleBusinessException(BusinessException e) {
        log.warn("Business exception: code={}, message={}", e.getCode(), e.getMessage());
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.error(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler({BindException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<ApiResponse<?>> handleValidationException(Exception e) {
        String message;
        if (e instanceof BindException) {
            BindException ex = (BindException) e;
            message = ex.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
        } else {
            MethodArgumentNotValidException ex = (MethodArgumentNotValidException) e;
            message = ex.getBindingResult().getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
        }
        log.warn("Validation exception: {}", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ErrorCodeEnum.INVALID_PARAMS.getCode(), message));
    }

    @ExceptionHandler(OptimisticLockerException.class)
    public ResponseEntity<ApiResponse<?>> handleOptimisticLockException(OptimisticLockerException e) {
        log.warn("Optimistic lock exception: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.error(ErrorCodeEnum.INVALID_PARAMS.getCode(), "数据已更新，请刷新后重试"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {
        log.error("Unexpected exception", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(ErrorCodeEnum.UNKNOWN_ERROR.getCode(), "系统错误，请稍后重试"));
    }
}
```

- [ ] **Step 3: Create API Response DTO**

```java
package com.uu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private int code;
    private String message;
    private T data;
    private long timestamp;

    public ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(0, "成功", data);
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }
}
```

- [ ] **Step 4: Commit**

```bash
git add src/main/java/com/uu/exception/ src/main/java/com/uu/dto/response/ApiResponse.java
git commit -m "feat: add exception handling and API response"
```

---

### Task 6: Enum Classes

**Files:**
- Create: `src/main/java/com/uu/enums/ErrorCodeEnum.java`
- Create: `src/main/java/com/uu/enums/ServiceTypeEnum.java`
- Create: `src/main/java/com/uu/enums/OrderStatusEnum.java`
- Create: `src/main/java/com/uu/enums/PaymentStatusEnum.java`
- Create: `src/main/java/com/uu/enums/UserStatusEnum.java`
- Create: `src/main/java/com/uu/enums/YesNoStatusEnum.java`

- [ ] **Step 1: Create ErrorCodeEnum**

```java
package com.uu.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCodeEnum {
    // 通用错误 (1xxx)
    SUCCESS(0, "成功"),
    UNKNOWN_ERROR(1000, "未知错误"),
    INVALID_PARAMS(1001, "参数错误"),
    UNAUTHORIZED(1002, "未授权"),
    FORBIDDEN(1003, "禁止访问"),
    NOT_FOUND(1004, "资源不存在"),

    // 用户错误 (2xxx)
    USER_NOT_FOUND(2001, "用户不存在"),
    USER_DISABLED(2002, "用户已被禁用"),

    // 订单错误 (3xxx)
    ORDER_NOT_FOUND(3001, "订单不存在"),
    ORDER_STATUS_ERROR(3002, "订单状态错误"),
    ORDER_ALREADY_PAID(3003, "订单已支付"),
    ORDER_CANCELLED(3004, "订单已取消"),
    ORDER_CANNOT_CANCEL(3005, "订单不可取消"),

    // 地址错误 (4xxx)
    ADDRESS_NOT_FOUND(4001, "地址不存在"),
    ADDRESS_LIMIT_EXCEEDED(4002, "地址数量超限"),

    // 支付错误 (5xxx)
    PAYMENT_FAILED(5001, "支付失败"),
    PAYMENT_CALLBACK_FAILED(5002, "支付回调失败"),
    PAYMENT_AMOUNT_MISMATCH(5003, "支付金额不匹配"),

    // 微信接口错误 (6xxx)
    WECHAT_LOGIN_FAILED(6001, "微信登录失败"),
    WECHAT_PAY_FAILED(6002, "微信支付失败");

    private final int code;
    private final String message;
}
```

- [ ] **Step 2: Create ServiceTypeEnum**

```java
package com.uu.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ServiceTypeEnum {
    HELP_BUY(1, "帮买"),
    HELP_SEND(2, "帮送"),
    HELP_QUEUE(3, "帮排队");

    private final Integer code;
    private final String desc;

    public static ServiceTypeEnum getByCode(Integer code) {
        if (code == null) return null;
        for (ServiceTypeEnum serviceType : values()) {
            if (serviceType.getCode().equals(code)) {
                return serviceType;
            }
        }
        return null;
    }

    public static boolean isValid(Integer code) {
        return getByCode(code) != null;
    }
}
```

- [ ] **Step 3: Create OrderStatusEnum**

```java
package com.uu.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatusEnum {
    PENDING_PAYMENT(100, "待支付"),
    PENDING_ACCEPT(200, "待接单"),
    IN_PROGRESS(300, "进行中"),
    COMPLETED(600, "已完成"),
    CANCELLED(999, "已取消");

    private final Integer code;
    private final String desc;

    public static OrderStatusEnum getByCode(Integer code) {
        if (code == null) return null;
        for (OrderStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    public static boolean isValid(Integer code) {
        return getByCode(code) != null;
    }

    public boolean canCancel() {
        return this == PENDING_PAYMENT || this == PENDING_ACCEPT;
    }
}
```

- [ ] **Step 4: Create PaymentStatusEnum**

```java
package com.uu.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentStatusEnum {
    PENDING(0, "待支付"),
    PAID(1, "已支付"),
    REFUNDED(2, "已退款");

    private final Integer code;
    private final String desc;

    public static PaymentStatusEnum getByCode(Integer code) {
        if (code == null) return null;
        for (PaymentStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    public static boolean isValid(Integer code) {
        return getByCode(code) != null;
    }
}
```

- [ ] **Step 5: Create UserStatusEnum**

```java
package com.uu.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserStatusEnum {
    NORMAL(1, "正常"),
    DISABLED(0, "禁用");

    private final Integer code;
    private final String desc;

    public static UserStatusEnum getByCode(Integer code) {
        if (code == null) return null;
        for (UserStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    public static boolean isValid(Integer code) {
        return getByCode(code) != null;
    }
}
```

- [ ] **Step 6: Create YesNoStatusEnum**

```java
package com.uu.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum YesNoStatusEnum {
    NO(0, "否"),
    YES(1, "是");

    private final Integer code;
    private final String desc;

    public static YesNoStatusEnum getByCode(Integer code) {
        if (code == null) return NO;
        for (YesNoStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return NO;
    }

    public static boolean isValid(Integer code) {
        return getByCode(code) != null;
    }

    public Boolean toBoolean() {
        return this == YES;
    }

    public static YesNoStatusEnum fromBoolean(Boolean value) {
        if (value == null) return NO;
        return value ? YES : NO;
    }
}
```

- [ ] **Step 7: Commit**

```bash
git add src/main/java/com/uu/enums/
git commit -m "feat: add enum classes for business types and error codes"
```

---

### Task 7: Utility Classes

**Files:**
- Create: `src/main/java/com/uu/util/JwtUtil.java`
- Create: `src/main/java/com/uu/util/SnowflakeIdGenerator.java`
- Create: `src/main/java/com/uu/util/ValidatorUtil.java`

- [ ] **Step 1: Create JwtUtil**

```java
package com.uu.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Long userId, String nickname) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("nickname", nickname)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getPayload();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Long getExpiration() {
        return expiration;
    }
}
```

- [ ] **Step 2: Create SnowflakeIdGenerator**

```java
package com.uu.util;

import org.springframework.stereotype.Component;

@Component
public class SnowflakeIdGenerator {

    private static final long EPOCH = 1710000000000L;
    private static final long WORKER_ID_BITS = 5L;
    private static final long DATACENTER_ID_BITS = 5L;
    private static final long SEQUENCE_BITS = 12L;

    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);

    private long workerId;
    private long datacenterId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    public SnowflakeIdGenerator() {
        this(1L, 1L);
    }

    public SnowflakeIdGenerator(long workerId, long datacenterId) {
        if (workerId < 0 || workerId > MAX_WORKER_ID) {
            throw new IllegalArgumentException("Worker ID out of range");
        }
        if (datacenterId < 0 || datacenterId > MAX_DATACENTER_ID) {
            throw new IllegalArgumentException("Datacenter ID out of range");
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    public synchronized long nextId() {
        long timestamp = System.currentTimeMillis();

        if (timestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards");
        }

        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - EPOCH) << (WORKER_ID_BITS + DATACENTER_ID_BITS + SEQUENCE_BITS))
                | (datacenterId << (WORKER_ID_BITS + SEQUENCE_BITS))
                | (workerId << SEQUENCE_BITS)
                | sequence;
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }
}
```

- [ ] **Step 3: Create ValidatorUtil**

```java
package com.uu.util;

import com.uu.exception.BusinessException;
import com.uu.enums.ErrorCodeEnum;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public class ValidatorUtil {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private static final int MIN_AMOUNT = 5;
    private static final int MAX_AMOUNT = 9999;

    public static void validatePhone(String phone) {
        if (StringUtils.isBlank(phone)) {
            throw new BusinessException(ErrorCodeEnum.INVALID_PARAMS, "手机号不能为空");
        }
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new BusinessException(ErrorCodeEnum.INVALID_PARAMS, "手机号格式不正确");
        }
    }

    public static void validateAmount(Integer amount) {
        if (amount == null) {
            throw new BusinessException(ErrorCodeEnum.INVALID_PARAMS, "跑腿费用不能为空");
        }
        if (amount < MIN_AMOUNT) {
            throw new BusinessException(ErrorCodeEnum.INVALID_PARAMS, "跑腿费用最小为" + MIN_AMOUNT + "元");
        }
        if (amount > MAX_AMOUNT) {
            throw new BusinessException(ErrorCodeEnum.INVALID_PARAMS, "跑腿费用最大为" + MAX_AMOUNT + "元");
        }
    }

    public static void validateStringLength(String value, int maxLength, String fieldName) {
        if (StringUtils.isNotBlank(value) && value.length() > maxLength) {
            throw new BusinessException(ErrorCodeEnum.INVALID_PARAMS,
                fieldName + "不能超过" + maxLength + "个字符");
        }
    }

    public static void validateRequired(String value, String fieldName) {
        if (StringUtils.isBlank(value)) {
            throw new BusinessException(ErrorCodeEnum.INVALID_PARAMS, fieldName + "不能为空");
        }
    }

    public static void validateSnowflakeId(String id, String fieldName) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCodeEnum.INVALID_PARAMS, fieldName + "不能为空");
        }
        try {
            Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new BusinessException(ErrorCodeEnum.INVALID_PARAMS, fieldName + "格式不正确");
        }
    }
}
```

- [ ] **Step 4: Write unit tests for ValidatorUtil**

```java
package com.uu.util;

import com.uu.exception.BusinessException;
import com.uu.enums.ErrorCodeEnum;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ValidatorUtilTest {

    @Test
    void testValidatePhone_valid() {
        assertDoesNotThrow(() -> ValidatorUtil.validatePhone("13800138000"));
    }

    @Test
    void testValidatePhone_invalid() {
        BusinessException ex = assertThrows(BusinessException.class,
            () -> ValidatorUtil.validatePhone("12345"));
        assertEquals(ErrorCodeEnum.INVALID_PARAMS.getCode(), ex.getCode());
    }

    @Test
    void testValidateAmount_valid() {
        assertDoesNotThrow(() -> ValidatorUtil.validateAmount(100));
    }

    @Test
    void testValidateAmount_tooSmall() {
        BusinessException ex = assertThrows(BusinessException.class,
            () -> ValidatorUtil.validateAmount(3));
        assertEquals(ErrorCodeEnum.INVALID_PARAMS.getCode(), ex.getCode());
    }

    @Test
    void testValidateAmount_tooLarge() {
        BusinessException ex = assertThrows(BusinessException.class,
            () -> ValidatorUtil.validateAmount(10000));
        assertEquals(ErrorCodeEnum.INVALID_PARAMS.getCode(), ex.getCode());
    }

    @Test
    void testSnowflakeIdGenerator() {
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator();
        long id1 = generator.nextId();
        long id2 = generator.nextId();
        assertTrue(id1 > 0);
        assertTrue(id2 > id1);
    }
}
```

- [ ] **Step 5: Run tests**

Run: `./gradlew test --tests com.uu.util.ValidatorUtilTest`
Expected: All tests pass

- [ ] **Step 6: Commit**

```bash
git add src/main/java/com/uu/util/ src/test/java/com/uu/util/
git commit -m "feat: add utility classes for JWT, ID generation, and validation"
```

---

### Task 8: Response DTOs Base

**Files:**
- Create: `src/main/java/com/uu/dto/response/IdResponse.java`
- Create: `src/main/java/com/uu/dto/response/IdStringResponse.java`

- [ ] **Step 1: Create IdResponse for Long IDs (internal use)**

```java
package com.uu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdResponse {
    private Long id;

    public static IdResponse of(Long id) {
        return new IdResponse(id);
    }
}
```

- [ ] **Step 2: Create IdStringResponse for API responses**

```java
package com.uu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdStringResponse {
    private String id;

    public static IdStringResponse of(Long id) {
        return new IdStringResponse(id != null ? id.toString() : null);
    }
}
```

- [ ] **Step 3: Commit**

```bash
git add src/main/java/com/uu/dto/response/
git commit -m "feat: add common response DTOs"
```

---

### Task 9: JWT Configuration

**Files:**
- Create: `src/main/java/com/uu/config/WechatConfig.java`

- [ ] **Step 1: Create WechatConfig**

```java
package com.uu.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "wechat")
public class WechatConfig {

    private MiniappConfig miniapp;
    private PayConfig pay;

    @Data
    public static class MiniappConfig {
        private String appid;
        private String secret;
    }

    @Data
    public static class PayConfig {
        private String appid;
        private String mchId;
        private String apiKey;
        private String apiV3Key;
        private String certPath;
        private String notifyUrl;
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add src/main/java/com/uu/config/WechatConfig.java
git commit -m "feat: add WeChat configuration"
```

---

### Task 10: Upload Directory Initialization

**Files:**
- Create: `src/main/resources/.gitkeep`
- Create: `uploads/avatar/.gitkeep`

- [ ] **Step 1: Create .gitkeep files to preserve empty directories**

```bash
mkdir -p src/main/resources/uploads/avatar
touch src/main/resources/.gitkeep
touch uploads/avatar/.gitkeep
```

- [ ] **Step 2: Update .gitignore to exclude uploads except .gitkeep**

```bash
echo "uploads/*" >> .gitignore
echo "!uploads/avatar/.gitkeep" >> .gitignore
```

- [ ] **Step 3: Commit**

```bash
git add .gitignore src/main/resources/.gitkeep uploads/avatar/.gitkeep
git commit -m "feat: initialize upload directories"
```

---

## Testing Strategy

### Unit Tests
- Run: `./gradlew test`
- Expected: All tests pass, 80%+ coverage

### Integration Tests
- Run: `./gradlew bootRun` (in background)
- Test endpoints with curl/Postman
- Check logs in `logs/` directory

### Manual Verification Checklist
- [ ] Application starts without errors
- [ ] Logs are properly written to logs/
- [ ] MyBatis-Plus pagination works
- [ ] Global exception handler returns correct JSON format
- [ ] JWT token generation and parsing works
- [ ] Snowflake ID generation produces unique IDs
- [ ] ValidatorUtil validates all inputs correctly