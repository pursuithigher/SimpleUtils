package hw.sdk.net.bean.store;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import hw.sdk.net.bean.HwPublicBean;

/**
 * 书城
 *
 * @author dongdianzhou on 2018/1/12.
 */

public class BeanTempletInfo extends HwPublicBean<BeanTempletInfo> implements Parcelable {

    /**
     * Creator
     */
    public static final Creator<BeanTempletInfo> CREATOR = new Creator<BeanTempletInfo>() {
        @Override
        public BeanTempletInfo createFromParcel(Parcel source) {
            return new BeanTempletInfo(source);
        }

        @Override
        public BeanTempletInfo[] newArray(int size) {
            return new BeanTempletInfo[size];
        }
    };

    /**
     * 当前版本支持的分类处理类型：兼容老版本，不能处理的会移除掉
     */
    private static final  List<String> CURRENTSUPPORTFLTYPELIST = new ArrayList<>();

    /**
     * 当前版本支持的频道处理类型：兼容老版本，不能处理的会移除掉
     */
    private static final  List<String> CURRENTSUPPORTPDTYPELIST = new ArrayList<>();

    /**
     * 当前版本支持的banner处理类型：兼容老版本，不能处理的会移除掉
     */
    private static final  List<String> CURRENTSUPPORTBNTYPELIST = new ArrayList<>();

    /**
     * 当前版本支持的ZT处理类型：兼容老版本，不能处理的会移除掉
     */
    private static final  List<String> CURRENTSUPPORTZTTYPELIST = new ArrayList<>();
    /**
     * id
     */
    public String id;
    /**
     * title
     */
    public String title;
    /**
     * template
     */
    public String template;
    /**
     * type
     */
    public String type;
    /**
     * img
     */
    public String img;
    /**
     * counter
     */
    public long counter;
    /**
     * action
     */
    public BeanTempletActionInfo action;
    /**
     * items
     */
    public ArrayList<BeanSubTempletInfo> items;

    /**
     * 本地字段：仅用于限免的数据传递
     */
    public String tabId;

    /**
     * adapter的view类型（通过type和template转化而来，转化规则参考TempletMapping）
     */
    public int viewType = -10;


    static {

        //书城频道支持处理的类型 1：native 2：url 3：排行榜4：分类5：vip6：限免
        CURRENTSUPPORTPDTYPELIST.add("1");
        CURRENTSUPPORTPDTYPELIST.add("2");
        CURRENTSUPPORTPDTYPELIST.add("3");
        CURRENTSUPPORTPDTYPELIST.add("4");
        CURRENTSUPPORTPDTYPELIST.add("5");
        CURRENTSUPPORTPDTYPELIST.add("6");

        //书城列表banner支持处理的类型：1： native 2：url
        CURRENTSUPPORTBNTYPELIST.add("1");
        CURRENTSUPPORTBNTYPELIST.add("2");
        //新增
        CURRENTSUPPORTFLTYPELIST.add("3");
        CURRENTSUPPORTFLTYPELIST.add("4");
        CURRENTSUPPORTFLTYPELIST.add("5");
        CURRENTSUPPORTFLTYPELIST.add("6");
        CURRENTSUPPORTFLTYPELIST.add("7");

        //书城分类 1：native 2：url 3：排行榜4：分类5：vip6：限免 7：通用拓展页
        CURRENTSUPPORTFLTYPELIST.add("1");
        CURRENTSUPPORTFLTYPELIST.add("2");
        CURRENTSUPPORTFLTYPELIST.add("3");
        CURRENTSUPPORTFLTYPELIST.add("4");
        CURRENTSUPPORTFLTYPELIST.add("5");
        CURRENTSUPPORTFLTYPELIST.add("6");
        CURRENTSUPPORTFLTYPELIST.add("7");

        //主题
        CURRENTSUPPORTZTTYPELIST.add("1");
        CURRENTSUPPORTZTTYPELIST.add("2");
    }

    /**
     * 构造器
     */
    public BeanTempletInfo() {
    }
    /**
     * 构造器
     * @param in Parcel in
     */
    protected BeanTempletInfo(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.template = in.readString();
        this.type = in.readString();
        this.counter = in.readLong();
        this.action = in.readParcelable(BeanTempletActionInfo.class.getClassLoader());
        this.items = in.createTypedArrayList(BeanSubTempletInfo.CREATOR);
        this.tabId = in.readString();
        this.viewType = in.readInt();
    }

    /**
     * isContainItems
     * @return isContainItems
     */
    public boolean isContainItems() {
        switch (viewType) {
            //仅支持书籍+url，其他过滤掉
            case TempletMapping.VIEW_TYPE_BN0:
            case TempletMapping.VIEW_TYPE_FL0:
                return isEmptyOnSupport(CURRENTSUPPORTFLTYPELIST);
            case TempletMapping.VIEW_TYPE_PD0:
                return isEmptyOnSupport(CURRENTSUPPORTPDTYPELIST);
            case TempletMapping.VIEW_TYPE_XM0:
            case TempletMapping.VIEW_TYPE_SJ0:
            case TempletMapping.VIEW_TYPE_SJ3:
            default:
                return items != null && items.size() > 0;
        }
    }
    /**
     * isEmptyOnSupport
     * @return isEmptyOnSupport
     */
    private boolean isEmptyOnSupport(List<String> currentSupportZtTypeList) {
        if (items != null && items.size() > 0) {
            List<BeanSubTempletInfo> unSupportSubList = new ArrayList<>();
            for (BeanSubTempletInfo sub : items) {
                if (sub != null && !currentSupportZtTypeList.contains(sub.type)) {
                    unSupportSubList.add(sub);
                }
            }
            if (unSupportSubList.size() > 0) {
                items.removeAll(unSupportSubList);
            }
        }
        return items != null && items.size() > 0;
    }

    @Override
    public BeanTempletInfo parseJSON(JSONObject jsonObj) {
        if (jsonObj == null) {
            return null;
        }
        id = jsonObj.optString("id");
        img = jsonObj.optString("img");
        title = jsonObj.optString("title");
        template = jsonObj.optString("template");
        type = jsonObj.optString("type");
        viewType = TempletMapping.getViewType(type, template);
        JSONObject act = jsonObj.optJSONObject("action");
        if (act != null) {
            action = new BeanTempletActionInfo();
            action.parseJSON(act);
        }
        counter = jsonObj.optLong("counter");
        if (counter > 0) {
            counter = System.currentTimeMillis() + counter * 1000;
        }
        JSONArray itemsObject = jsonObj.optJSONArray("items");
        if (itemsObject != null && itemsObject.length() > 0) {
            items = new ArrayList<>();
            for (int i = 0; i < itemsObject.length(); i++) {
                JSONObject sub = itemsObject.optJSONObject(i);
                if (sub != null) {
                    BeanSubTempletInfo beanSubTempletInfo = new BeanSubTempletInfo();
                    beanSubTempletInfo.parseJSON(sub);
                    items.add(beanSubTempletInfo);
                }
            }
        }
        return this;
    }


    /**
     * 得到有效的频道列表
     *
     * @return 得到有效的频道列表
     */
    public ArrayList<BeanSubTempletInfo> getValidChannels() {
        return items;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.template);
        dest.writeString(this.type);
        dest.writeLong(this.counter);
        dest.writeParcelable(this.action, flags);
        dest.writeTypedList(this.items);
        dest.writeString(this.tabId);
        dest.writeInt(this.viewType);
    }




}
