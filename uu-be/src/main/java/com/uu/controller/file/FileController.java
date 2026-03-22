package com.uu.controller.file;

import com.uu.dto.response.ApiResponse;
import com.uu.dto.response.IdStringResponse;
import com.uu.entity.User;
import com.uu.interceptor.LoginInterceptor;
import com.uu.service.UserService;
import com.uu.util.FileUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * 文件控制器
 */
@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileUtil fileUtil;

    @Autowired
    private UserService userService;

    /**
     * 默认头像路径
     */
    @Value("${file.upload.avatar-path:uploads/avatar}")
    private String avatarPath;

    private static final String DEFAULT_AVATAR = "default-avatar.png";

    /**
     * 获取用户头像（公开访问）
     */
    @GetMapping("/avatar/{userId}")
    public void getAvatar(@PathVariable String userId, HttpServletResponse response) {
        try {
            // 查询用户获取头像路径
            User user = userService.getById(Long.parseLong(userId));
            String avatarUrl = user.getAvatarUrl();

            // 设置响应头
            response.setContentType(MediaType.IMAGE_JPEG_VALUE);

            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                // 返回用户头像
                InputStream inputStream = fileUtil.readFile(avatarUrl);
                if (inputStream != null) {
                    // 根据文件扩展名设置正确的Content-Type
                    if (avatarUrl.toLowerCase().endsWith(".png")) {
                        response.setContentType(MediaType.IMAGE_PNG_VALUE);
                    }
                    inputStream.transferTo(response.getOutputStream());
                    log.info("返回用户头像: userId={}, avatarUrl={}", userId, avatarUrl);
                    return;
                }
            }

            // 返回默认头像
            returnDefaultAvatar(response);
        } catch (Exception e) {
            log.error("获取头像失败: userId={}", userId, e);
            returnDefaultAvatar(response);
        }
    }

    /**
     * 更新用户头像
     */
    @PostMapping("/user/avatar/update")
    public ApiResponse<IdStringResponse> updateAvatar(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        Long userId = LoginInterceptor.getUserId(request);
        log.info("更新用户头像: userId={}, fileName={}", userId, file.getOriginalFilename());

        // 上传文件
        String avatarUrl = fileUtil.uploadAvatar(file, userId);

        // 更新用户头像URL
        userService.updateAvatar(userId, avatarUrl);

        // 返回结果
        IdStringResponse response = IdStringResponse.of(userId);
        return ApiResponse.success(response);
    }

    /**
     * 返回默认头像
     */
    private void returnDefaultAvatar(HttpServletResponse response) {
        try {
            ClassPathResource resource = new ClassPathResource(DEFAULT_AVATAR);
            if (resource.exists()) {
                response.setContentType(MediaType.IMAGE_JPEG_VALUE);
                resource.getInputStream().transferTo(response.getOutputStream());
            } else {
                // 如果默认头像不存在，返回空
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (IOException e) {
            log.error("返回默认头像失败", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}