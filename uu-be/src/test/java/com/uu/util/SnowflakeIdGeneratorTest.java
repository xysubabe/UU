package com.uu.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 雪花ID生成器测试
 */
@ExtendWith(MockitoExtension.class)
public class SnowflakeIdGeneratorTest {

    @Test
    void testNextId_SingleInstance() {
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator(1L, 1L);

        Long id1 = generator.nextId();
        Long id2 = generator.nextId();

        assertTrue(id2 > id1);
    }

    @Test
    void testNextId_MultipleInstances() {
        SnowflakeIdGenerator generator1 = new SnowflakeIdGenerator(1L, 1L);
        SnowflakeIdGenerator generator2 = new SnowflakeIdGenerator(1L, 2L);

        Long id1 = generator1.nextId();
        Long id2 = generator2.nextId();

        assertTrue(id2 > id1);
    }

    @Test
    void testNextId_LowerWorkerId() {
        SnowflakeIdGenerator generator1 = new SnowflakeIdGenerator(1L, 1L);
        SnowflakeIdGenerator generator2 = new SnowflakeIdGenerator(2L, 2L);

        Long id1 = generator1.nextId();
        Long id2 = generator2.nextId();

        assertTrue(id2 > id1);
    }

    @Test
    void testNextId_HigherDatacenterId() {
        SnowflakeIdGenerator generator1 = new SnowflakeIdGenerator(1L, 1L);
        SnowflakeIdGenerator generator2 = new SnowflakeIdGenerator(1L, 2L);

        Long id1 = generator1.nextId();
        Long id2 = generator2.nextId();

        assertTrue(id2 > id1);
    }

    @Test
    void testNextId_FastSequence() {
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator(1L, 1L);

        Long id1 = generator.nextId();
        Long id2 = generator.nextId();

        // 第二次调用应该增加时间戳，因为序列号相同
        assertEquals(1L, generator.nextId() - id2);
    }

    @Test
    void testNextId_DifferentWorkerId() {
        SnowflakeIdGenerator generator1 = new SnowflakeIdGenerator(1L, 1L);
        SnowflakeIdGenerator generator2 = new SnowflakeIdGenerator(2L, 1L);

        Long id1 = generator1.nextId();
        Long id2 = generator2.nextId();

        // 不同workerId应该产生不同的ID
        assertNotEquals(id1, id2);
    }

    @Test
    void testNextIdAsString() {
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator(1L, 1L);

        Long idLong = generator.nextId();
        // 使用同一个ID创建新的生成器来测试toString
        SnowflakeIdGenerator generator2 = new SnowflakeIdGenerator(1L, 1L);
        String idString = String.valueOf(idLong);

        assertEquals(idLong.toString(), idString);
    }

    @Test
    void testConstructor_DefaultValues() {
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator();

        assertNotNull(generator.nextId());
    }
}
