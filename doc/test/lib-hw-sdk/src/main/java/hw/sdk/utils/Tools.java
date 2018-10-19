package hw.sdk.utils;

import android.content.Context;
import android.util.Log;

/**
 * 工具类
 * @author winzows
 */
public class Tools {

    /**
     * 日志标签
     */
    private static final String TAG = "Tools";
    private static final int ONE_SECOND = 1000;

    private static final int ONE_MINUTE = 60 * ONE_SECOND;
    // 超过此尺寸的设备当做是平板
    private static final double TABLET_MIN_WIDTH = 6.9;

    /**
     * 根据尺寸判断是否为平板，请应用不要参考样例，使用自己的方式实现
     *
     * @param context 上线文环境
     * @return true：平板，false：手机
     */
    public static boolean isTablet(Context context) {
        double screenSize = UiHelper.getScreenInch(context);
        return screenSize > TABLET_MIN_WIDTH;
    }

    /**
     * 是否是横屏
     *
     * @param context 上下文
     * @return 是否是横屏
     * @see [类、类#方法、类#成员]
     */
    public static boolean isLandscape(Context context) {
        if (null == context) {
            return true;
        }
        android.content.res.Configuration config = context.getResources().getConfiguration();
        if (config.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) {
            Log.i(TAG, "landscape");
            return true;
        }
        Log.i(TAG, "portrait");
        return false;
    }

}
