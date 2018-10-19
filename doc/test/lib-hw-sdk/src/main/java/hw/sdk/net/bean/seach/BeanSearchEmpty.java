package hw.sdk.net.bean.seach;

/**
 * 搜索结果为空
 * @author caimantang on 2018/4/23.
 */
public class BeanSearchEmpty {
    /**
     * msg
     */
    private String msg;
    //1.header 2.moreBook
    /**
     * 类型
     */
    private int type;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
