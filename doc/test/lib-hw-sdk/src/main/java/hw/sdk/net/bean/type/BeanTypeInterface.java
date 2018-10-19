package hw.sdk.net.bean.type;

/**
 * 实现统一接口
 * @author winzows on  2018/4/30
 */

public interface BeanTypeInterface {
    /**
     * 获取title
     *
     * @return title
     */
    String getTitle();

    /**
     * 获取 markId
     *
     * @return 就是请求的id
     */
    String getMarkId();

    /**
     * 获取tyoe
     *
     * @return 类型
     */
    String getType();
}
