package hw.sdk.net.bean;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

import hw.sdk.HwSdkAppConstant;

/**
 *  HwPublicBean
 * @author winzows  2018/4/13
 * @param <T> 泛型
 */

public class HwPublicBean<T extends HwPublicBean> implements Serializable {

    /**
     *处理状态码0成功其他操作失败错误码 默认是-1
     */
    private int retCode = -1;

    private int isExpire = -1;

    /**
     * 处理失败错误信息成功时无错误信息
     */
    private String retMsg;

    /**
     * 将json对象转化为Bean实例
     *
     * @param jsonObj json
     * @return  T
     */
    public T parseJSON(JSONObject jsonObj) {
        if (jsonObj == null) {
            return null;
        }

        retCode = jsonObj.optInt("retCode", -1);
        isExpire = jsonObj.optInt("isExpire", -1);
        retMsg = jsonObj.optString("retMsg");

        if (isTokenExpire()) {
            HwSdkAppConstant.setIsAppTokenInvalidNeedRetrySys(true);
        }

        return (T) this;
    }

    /**
     * 接口响应  是否成功
     *
     * @return true false
     */
    public boolean isSuccess() {
        return retCode == 0;
    }

    /**
     * 返回响应消息
     *
     * @return 消息
     */
    public String getRetMsg() {
        return retMsg;
    }

    /**
     * 返回错误码
     *
     * @return 返回错误码
     */
    public int getRetCode() {
        return retCode;
    }

    /**
     * 是否需要登录
     *
     * @return 是否需要登录
     */
    private boolean isNeedLogin() {
        return retCode == 6;
    }

    /**
     * 是否token过期
     *
     * @return 是否token过期
     */
    public boolean isTokenExpire() {
        return isExpire == 1;
    }

    /**
     * 是否token失效，或者需要登录
     * 外部调用此方法后做相应处理，
     * 外部应判断是需要 弹出界面登录 或者 后台隐式登录
     *
     * @return 是否token失效
     */
    public boolean isTokenExpireOrNeedLogin() {
        return isTokenExpire() || isNeedLogin();
    }

    /**
     * 是不是空的
     * @param list 集合
     * @return  是不是空的
     */
    public boolean isEmpty(List list) {
        return null == list || list.size() <= 0;
    }
}
