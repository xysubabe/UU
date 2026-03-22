package com.uu.constants;

import java.math.RoundingMode;

/**
 * 支付相关常量
 *
 * @author UU Team
 * @since 1.0.0
 */
public class PaymentConstants {

    /**
     * 金额转换比例：分转元
     */
    public static final int AMOUNT_DIVISOR = 100;

    /**
     * 金额小数位数
     */
    public static final int AMOUNT_SCALE = 2;

    /**
     * 金额舍入模式
     */
    public static final RoundingMode AMOUNT_ROUNDING_MODE = RoundingMode.HALF_UP;

    /**
     * 时间戳转换比例（毫秒转秒）
     */
    public static final int TIMESTAMP_DIVISOR = 1000;

    private PaymentConstants() {
        // 私有构造函数防止实例化
    }
}
