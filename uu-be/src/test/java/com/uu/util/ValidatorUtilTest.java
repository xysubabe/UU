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
        assertTrue(id2 > id1);
        assertNotNull(generator.nextIdAsString());
    }
}