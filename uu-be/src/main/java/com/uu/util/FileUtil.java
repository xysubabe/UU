package com.uu.util;

import com.uu.enums.ErrorCodeEnum;
import com.uu.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 文件工具类
 */
@Slf4j
@Component
public class FileUtil {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    // 支持的图片格式
    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList("jpg", "jpeg", "png"));

    // 文件魔数
    private static final Set<String> JPEG_MAGIC_NUMBERS = new HashSet<>(Arrays.asList("FFD8FF"));
    private static final String PNG_MAGIC_NUMBER = "89504E47";

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM");

    @Value("${file.upload.base-path:uploads}")
    private String basePath;

    @Value("${file.upload.avatar-path:uploads/avatar}")
    private String avatarPath;

    /**
     * 上传头像
     */
    public String uploadAvatar(MultipartFile file, Long userId) {
        // 验证文件大小
        validateFileSize(file);

        // 获取文件扩展名
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);

        // 验证文件格式
        validateFileExtension(extension);

        // 验证文件内容
        validateFileContent(file);

        // 生成文件名
        String fileName = generateFileName(userId, extension);

        // 生成存储路径
        String relativePath = generateRelativePath();
        String fullPath = getFullPath(relativePath);

        // 创建目录
        createDirectoryIfNotExists(fullPath);

        // 保存文件
        String filePath = fullPath + File.separator + fileName;
        saveFile(file, filePath);

        // 返回相对路径
        return relativePath + "/" + fileName;
    }

    /**
     * 获取完整路径
     */
    private String getFullPath(String relativePath) {
        return Paths.get(basePath, relativePath).toString();
    }

    /**
     * 生成相对路径
     */
    private String generateRelativePath() {
        LocalDateTime now = LocalDateTime.now();
        return avatarPath + File.separator + DATE_FORMATTER.format(now);
    }

    /**
     * 生成文件名
     */
    private String generateFileName(Long userId, String extension) {
        long timestamp = System.currentTimeMillis();
        SecureRandom random = new SecureRandom();
        int randomSuffix = random.nextInt(1000);
        return userId + "_" + timestamp + "_" + randomSuffix + "." + extension;
    }

    /**
     * 验证文件大小
     */
    private void validateFileSize(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(4001, "文件大小不能超过5MB");
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            throw new BusinessException(4002, "文件名不能为空");
        }
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == filename.length() - 1) {
            throw new BusinessException(4002, "文件格式不支持");
        }
        String extension = filename.substring(dotIndex + 1).toLowerCase();
        return extension;
    }

    /**
     * 验证文件扩展名
     */
    private void validateFileExtension(String extension) {
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BusinessException(ErrorCodeEnum.INVALID_PARAMS, "文件格式不支持，仅支持jpg、jpeg、png");
        }
    }

    /**
     * 验证文件内容
     */
    private void validateFileContent(MultipartFile file) {
        try {
            byte[] bytes = file.getBytes();
            if (bytes.length < 8) {
                throw new BusinessException(4003, "文件内容验证失败");
            }

            String magicNumber = bytesToHex(bytes, 0, 3);

            if (JPEG_MAGIC_NUMBERS.contains(magicNumber)) {
                // JPEG格式
            } else if (magicNumber.equals(PNG_MAGIC_NUMBER)) {
                // PNG格式
            } else {
                throw new BusinessException(4003, "文件内容验证失败");
            }
        } catch (IOException e) {
            log.error("读取文件失败", e);
            throw new BusinessException(4003, "文件内容验证失败");
        }
    }

    /**
     * 字节数组转十六进制字符串
     */
    private String bytesToHex(byte[] bytes, int offset, int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = offset; i < offset + length && i < bytes.length; i++) {
            sb.append(String.format("%02X", bytes[i] & 0xFF));
        }
        return sb.toString();
    }

    /**
     * 创建目录
     */
    private void createDirectoryIfNotExists(String path) {
        try {
            Path directory = Paths.get(path);
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }
        } catch (IOException e) {
            log.error("创建目录失败: {}", path, e);
            throw new BusinessException(4005, "创建目录失败");
        }
    }

    /**
     * 保存文件
     */
    private void saveFile(MultipartFile file, String filePath) {
        try {
            Path path = Paths.get(filePath);
            Files.copy(file.getInputStream(), path);
            log.info("文件保存成功: {}", filePath);
        } catch (IOException e) {
            log.error("保存文件失败: {}", filePath, e);
            throw new BusinessException(4004, "文件保存失败");
        }
    }

    /**
     * 读取文件
     */
    public InputStream readFile(String relativePath) throws IOException {
        String fullPath = Paths.get(basePath, relativePath).toString();
        File file = new File(fullPath);
        if (!file.exists()) {
            return null;
        }
        return new java.io.FileInputStream(file);
    }
}