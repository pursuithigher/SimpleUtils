package hw.sdk.net.bean;

import android.webkit.URLUtil;

import org.json.JSONObject;

/**
 * 获取是否用户在其它华为阅读存在资产
 *
 * @author winzows
 */
public class OldUserFlagBean extends HwPublicBean<OldUserFlagBean> {


    /**
     * 0:没有 1：有	是否掌阅华为阅读有资产
     */
    private int isZyHasAssets;
    /**
     * 0:没有 1：有	是否书旗华为阅读有资产
     */
    private int isSqHasAssets;

    /**
     * 掌阅H5 url，如果isZyHasAssets为0则可为空
     */
    public String zyH5Url;

    /**
     * 书旗H5 url，如果isSqHasAssets为0则可为空
     */
    public String sQH5Url;

    @Override
    public OldUserFlagBean parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);

        if (isSuccess()) {
            JSONObject dataJsonObj = jsonObj.optJSONObject("data");
            if (dataJsonObj != null) {
                isZyHasAssets = dataJsonObj.optInt("isZyHasAssets");
                isSqHasAssets = dataJsonObj.optInt("isSqHasAssets");

                zyH5Url = dataJsonObj.optString("zyH5Url");
                sQH5Url = dataJsonObj.optString("sQH5Url");
            }
        }

        return this;
    }

    @Override
    public String toString() {
        return "OldUserFlagBean{"
                +
                ", isZyHasAssets='"
                + isZyHasAssets
                + '\''
                +
                ", isSqHasAssets='"
                + isSqHasAssets
                + '\''
                +
                ", zyH5Url='"
                + zyH5Url
                + '\''
                +
                ", sQH5Url='"
                + sQH5Url
                + '}';
    }


    /**
     * 是否掌阅存在资产
     *
     * @return
     */
    public boolean isZyHasAssets() {
        return isZyHasAssets == 1;
    }

    /**
     * 是否在书旗存在资产
     *
     * @return
     */
    public boolean isSqHasAssets() {
        return isSqHasAssets == 1;
    }

}
