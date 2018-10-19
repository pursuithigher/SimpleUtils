package hw.sdk.net.bean;

/**
 * 一级导航栏的bean
 * @author caimantang on 2018/5/8.
 */

public class BeanTabLog {
    /**
     * 一级导航栏ID
     */
    private String tabId;
    /**
     * tabName一级导航栏名称
     */
    private String tabName;
    /**
     * tabPos一级导航栏位置顺序
     */
    private String tabPos;

    /**
     * 构造器
     * @param tabId id
     * @param tabName name
     * @param tabPos pos
     */
    public BeanTabLog(String tabId, String tabName, String tabPos) {
        this.tabId = tabId;
        this.tabName = tabName;
        this.tabPos = tabPos;
    }

    public String getTabId() {
        return tabId;
    }

    public String getTabName() {
        return tabName;
    }

    public String getTabPos() {
        return tabPos;
    }


}
