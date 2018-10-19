package hw.sdk;

/**
 * HwSdkAppConstant
 * @author lizz 2018/4/30.
 */

public class HwSdkAppConstant {

    /**
     * RSA_KEY
     */
    public static final String RSA_KEY = "W25gdluokG/RzNvQn4+W/XfTryQjr7RpXm1VxCIrCBvYWNU2KrSYV4XUtL+B5ERNj6In6AOrOAif"
            +
            "uVITy5cQQQeoD+AT4YKKMBkQfO2gnZzqb8+ox130e+3K/mufoqJPZeyrCQKBgC2fobjwhQvYwYY+"
            +
            "DIUharri+rYrBRYTDbJYnh/PNOaw1CmHwXJt5PEDcml3+NlIMn58I1X2U/hpDrAIl3MlxpZBkVYF"
            +
            "I8LmlOeR7ereTddN59ZOE4jY/OnCfqA480Jf+FKfoMHby5lPO5OOLaAfjtae1FhrmpUe3EfIx9wV"
            +
            "uhKBAoGBAPFzHKQZbGhkqmyPW2ctTEIWLdUHyO37fm8dj1WjN4wjRAI4ohNiKQJRh3QE11E1PzBT"
            +
            "l9lZVWT8QtEsSjnrA/tpGr378fcUT7WGBgTmBRaAnv1P1n/Tp0TSvh5XpIhhMuxcitIgrhYMIG3G"
            +
            "bP9JNAarxO/qPW6Gi0xWaF7il7Or";

    /**
     * 是否AB key
     */
    private static volatile  boolean isAbKey = false;

    /**
     * add by lizz
     * 是否服务器接口返回token失效需要重新同步token
     */
    private static boolean isAppTokenInvalidNeedRetrySys = false;

    /**
     * add by lizz
     * 进入应用同步token状态
     * false：需要同步
     * true：已经同步成功过，不再需要同步
     */
    private static boolean startAppSynTokenStatus = true;

    public static void setIsAbKey(boolean isAbKey) {
        HwSdkAppConstant.isAbKey = isAbKey;
    }

    public static boolean isAbKey() {
        return isAbKey;
    }

    public static boolean isStartAppSynTokenStatus() {
        return startAppSynTokenStatus;
    }

    public static void setStartAppSynTokenStatus(boolean startAppSynTokenStatus) {
        HwSdkAppConstant.startAppSynTokenStatus = startAppSynTokenStatus;
    }



    public static boolean isIsAppTokenInvalidNeedRetrySys() {
        return isAppTokenInvalidNeedRetrySys;
    }

    public static void setIsAppTokenInvalidNeedRetrySys(boolean isAppTokenInvalidNeedRetrySys) {
        HwSdkAppConstant.isAppTokenInvalidNeedRetrySys = isAppTokenInvalidNeedRetrySys;
    }


}
