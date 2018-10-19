package hw.sdk.net.bean.store;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.HashMap;

/**
 * TempletMapping
 * @author dongdianzhou on 2018/1/10.
 * 模板到view类型的映射
 */

public class TempletMapping {
    /**
     * TYPE_PD
     */
    public static final String TYPE_PD = "pd";
    /**
     * TYPE_BN
     */
    public static final String TYPE_BN = "bn";
    /**
     * TYPE_FL
     */
    public static final String TYPE_FL = "fl";
    /**
     * TYPE_XM
     */
    public static final String TYPE_XM = "xm";
    /**
     * TYPE_SJ
     */
    public static final String TYPE_SJ = "sj";
    /**
     * TYPE_PW
     */
    public static final String TYPE_PW = "pw";
    /**
     * TYPE_TM
     */
    public static final String TYPE_TM = "tm";
    /**
     * TYPE_DB
     */
    public static final String TYPE_DB = "db";
    /**
     * TYPE_LD
     */
    public static final String TYPE_LD = "ld";
    /**
     * TYPE_VPT
     */
    public static final String TYPE_VPT = "vpt";
    /**
     * TYPE_XSLB
     */
    public static final String TYPE_XSLB = "xslb";
    /**
     * PD0
     */
    public static final String PD0 = "pd0";
    /**
     * PD1
     */
    public static final String PD1 = "pd1";
    /**
     * BN0
     */
    public static final String BN0 = "bn0";
    /**
     * FL0
     */
    public static final String FL0 = "fl0";
    /**
     * XM0
     */
    public static final String XM0 = "xm0";
    /**
     * SJ0
     */
    public static final String SJ0 = "sj0";
    /**
     * SJ3
     */
    public static final String SJ3 = "sj3";
    /**
     * PW1
     */

    public static final String PW1 = "pw1";
    /**
     * TM0
     */
    public static final String TM0 = "tm0";
    /**
     * TM1
     */
    public static final String TM1 = "tm1";
    /**
     * DB0
     */
    public static final String DB0 = "db0";
    /**
     * DB1
     */
    public static final String DB1 = "db1";
    /**
     * LD0
     */
    public static final String LD0 = "ld0";
    /**
     * VPT0
     */
    public static final String VPT0 = "vpt0";
    /**
     * XSLB0
     */
    public static final String XSLB0 = "xslb0";
    /**
     * VIEW_TYPE_PD0
     */
    public static final int VIEW_TYPE_PD0 = 1;
    /**
     * VIEW_TYPE_PD1
     */
    public static final int VIEW_TYPE_PD1 = 2;
    /**
     * VIEW_TYPE_BN0
     */
    public static final int VIEW_TYPE_BN0 = 3;
    /**
     * VIEW_TYPE_FL0
     */
    public static final int VIEW_TYPE_FL0 = 5;
    /**
     * VIEW_TYPE_XM0
     */
    public static final int VIEW_TYPE_XM0 = 9;
    /**
     * VIEW_TYPE_SJ0
     */
    public static final int VIEW_TYPE_SJ0 = 10;
    /**
     * VIEW_TYPE_SJ3
     */
    public static final int VIEW_TYPE_SJ3 = 13;
    /**
     * VIEW_TYPE_PW1
     */
    public static final int VIEW_TYPE_PW1 = 15;
    /**
     * VIEW_TYPE_TM0
     */
    public static final int VIEW_TYPE_TM0 = 17;
    /**
     * VIEW_TYPE_DB0
     */
    public static final int VIEW_TYPE_DB0 = 18;
    /**
     * VIEW_TYPE_DB1
     */
    public static final int VIEW_TYPE_DB1 = 19;
    /**
     * VIEW_TYPE_LD0
     */
    public static final int VIEW_TYPE_LD0 = 20;
    /**
     * VIEW_TYPE_SCRAP
     */
    public static final int VIEW_TYPE_SCRAP = 22; //废弃
    /**
     * VIEW_TYPE_VPT0
     */
    public static final int VIEW_TYPE_VPT0 = 23;
    /**
     * VIEW_TYPE_TM1
     */
    public static final int VIEW_TYPE_TM1 = 24;
    /**
     * VIEW_TYPE_XSLB0
     */
    public static final int VIEW_TYPE_XSLB0 = 25;

    private static HashMap<String, Integer> mapForType, mapForTemplate;


    /**
     * 通过类型和模板得到view的类型
     * 模板优先：类型其次：两者姐不支持废弃
     *
     * @param type TYPE_SJ
     * @param template TYPE_SJ
     * @return 通过类型和模板得到view的类型
     */
    public static int getViewType(String type, String template) {
        if (TextUtils.isEmpty(type) && TextUtils.isEmpty(template)) {
            return VIEW_TYPE_SCRAP;
        }
        Integer result = getViewTypeByTemplate(template);
        if (result != null) {
            return result;
        }
        result = getViewTypeByType(type);
        if (result != null) {
            return result;
        }
        return VIEW_TYPE_SCRAP;
    }

    @Nullable
    private static Integer getViewTypeByType(String type) {
        if (!TextUtils.isEmpty(type)) {
            if (null == mapForType) {
                synchronized (TempletMapping.class) {
                    if (null == mapForType) {
                        HashMap<String, Integer> map = new HashMap<>();
                        map.put(TYPE_PD, VIEW_TYPE_PD0);
                        map.put(TYPE_BN, VIEW_TYPE_BN0);
                        map.put(TYPE_FL, VIEW_TYPE_FL0);
                        map.put(TYPE_XM, VIEW_TYPE_XM0);
                        map.put(TYPE_SJ, VIEW_TYPE_SJ0);
                        map.put(TYPE_PW, VIEW_TYPE_PW1);
                        map.put(TYPE_TM, VIEW_TYPE_TM0);
                        map.put(TYPE_DB, VIEW_TYPE_DB0);
                        map.put(TYPE_LD, VIEW_TYPE_LD0);
                        map.put(TYPE_VPT, VIEW_TYPE_VPT0);
                        map.put(TYPE_XSLB, VIEW_TYPE_XSLB0);
                        mapForType = map;
                    }
                }
            }
            if (mapForType.containsKey(type)) {
                return mapForType.get(type);
            }
        }
        return null;
    }

    @Nullable
    private static Integer getViewTypeByTemplate(String template) {
        if (!TextUtils.isEmpty(template)) {
            if (null == mapForTemplate) {
                synchronized (TempletMapping.class) {
                    if (null == mapForTemplate) {
                        HashMap<String, Integer> map = new HashMap<>();
                        map.put(PD0, VIEW_TYPE_PD0);
                        map.put(PD1, VIEW_TYPE_PD1);
                        map.put(BN0, VIEW_TYPE_BN0);
                        map.put(FL0, VIEW_TYPE_FL0);
                        map.put(XM0, VIEW_TYPE_XM0);
                        map.put(SJ0, VIEW_TYPE_SJ0);
                        map.put(SJ3, VIEW_TYPE_SJ3);
//                        map.put(PW0, VIEW_TYPE_PW0);
                        map.put(PW1, VIEW_TYPE_PW1);
                        map.put(TM0, VIEW_TYPE_TM0);
                        map.put(TM1, VIEW_TYPE_TM1);
                        map.put(DB0, VIEW_TYPE_DB0);
                        map.put(DB1, VIEW_TYPE_DB1);
                        map.put(LD0, VIEW_TYPE_LD0);
                        map.put(VPT0, VIEW_TYPE_VPT0);
                        map.put(XSLB0, VIEW_TYPE_XSLB0);
                        mapForTemplate = map;
                    }
                }
            }
            if (mapForTemplate.containsKey(template)) {
                return mapForTemplate.get(template);
            }
        }
        return null;
    }
}
