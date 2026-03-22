package com.uu.constants;

/**
 * 订单相关常量
 *
 * @author UU Team
 * @since 1.0.0
 */
public class OrderConstants {

    /**
     * 订单标题最大长度
     */
    public static final int MAX_TITLE_LENGTH = 10;

    /**
     * 默认每页数量
     */
    public static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 最大每页数量
     */
    public static final int MAX_PAGE_SIZE = 100;

    /**
     * 手机号长度
     */
    public static final int PHONE_LENGTH = 11;

    /**
     * 用户最大地址数量
     */
    public static final int MAX_ADDRESS_COUNT = 3;

    private OrderConstants() {
        // 私有构造函数防止实例化
    }
}
