package hw.sdk.net.bean.store;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import hw.sdk.net.bean.HwPublicBean;

/**
 * 书城bean
 * @author dongdianzhou on 2018/1/12.
 */

public class BeanSubTempletInfo extends HwPublicBean<BeanSubTempletInfo> implements Parcelable {
    /**
     * Creator
     */
    public static final Creator<BeanSubTempletInfo> CREATOR = new Creator<BeanSubTempletInfo>() {
        @Override
        public BeanSubTempletInfo createFromParcel(Parcel source) {
            return new BeanSubTempletInfo(source);
        }

        @Override
        public BeanSubTempletInfo[] newArray(int size) {
            return new BeanSubTempletInfo[size];
        }
    };
    /**
     * itemMark
     */
    public String itemMark;
    /**
     * id
     */
    public String id;
    /**
     * title
     */
    public String title;
    /**
     * type
     */
    public String type;
    /**
     * subTitle
     */
    public String subTitle;
    /**
     * subScript
     */
    public String subScript;
    /**
     * actionUrl
     */
    public String actionUrl;
    /**
     * isFix
     */
    public int isFix;//1：固话2：置顶3：正常
    /**
     * imgUrl
     */
    public ArrayList<String> imgUrl;
    /**
     * bannerInfo
     */
    public BeanBannerInfo bannerInfo;
    /**
     * vipInfo
     */
    public BeanVipInfo vipInfo;
    /**
     * action
     */
    public BeanTempletActionInfo action;
    /**
     * items
     */
    public ArrayList<BeanSubTempletInfo> items;
    /**
     * author
     */
    public String author;
    /**
     * desc
     */
    public String desc;
    /**
     * bookMarks
     */
    public ArrayList<String> bookMarks;
    /**
     * counter
     */
    public long counter;
    /**
     * warn
     */
    public String warn;
    /**
     * delLine
     */
    public String delLine;
    /**
     * limit
     */
    public int limit;
    /**
     * hasGot
     */
    public int hasGot;
    /**
     * advert
     */
    public String advert = "false";

    /**
     * 本地字段：用于xm0的tab title是否选中效果
     */
    public boolean isXm0Selected = false;

    /**
     * 构造器
     */
    public BeanSubTempletInfo() {
    }
    /**
     * 构造器
     * @param in Parcel
     */
    protected BeanSubTempletInfo(Parcel in) {
        this.itemMark = in.readString();
        this.id = in.readString();
        this.title = in.readString();
        this.type = in.readString();
        this.subTitle = in.readString();
        this.subScript = in.readString();
        this.actionUrl = in.readString();
        this.isFix = in.readInt();
        this.imgUrl = in.createStringArrayList();
        this.action = in.readParcelable(BeanTempletActionInfo.class.getClassLoader());
        this.items = in.createTypedArrayList(BeanSubTempletInfo.CREATOR);
        this.author = in.readString();
        this.desc = in.readString();
        this.bookMarks = in.createStringArrayList();
        this.counter = in.readLong();
        this.warn = in.readString();
        this.delLine = in.readString();
        this.limit = in.readInt();
        this.hasGot = in.readInt();
        this.advert = in.readString();
        this.isXm0Selected = in.readByte() != 0;
    }

    public boolean isContainItems() {
        return items != null && items.size() > 0;
    }

    public boolean isAd() {
        return !TextUtils.isEmpty(advert) && "true".equals(advert);
    }


    @Override
    public BeanSubTempletInfo parseJSON(JSONObject jsonObj) {

        super.parseJSON(jsonObj);

        itemMark = jsonObj.optString("itemMark");
        id = jsonObj.optString("id");
        title = jsonObj.optString("title");
        type = jsonObj.optString("type");
        subTitle = jsonObj.optString("subTitle");
        subScript = jsonObj.optString("subScript");
        actionUrl = jsonObj.optString("actionUrl");
        isFix = jsonObj.optInt("isFix");
        advert = jsonObj.optString("advert");
        JSONArray urls = jsonObj.optJSONArray("imgUrl");
        if (urls != null && urls.length() > 0) {
            imgUrl = new ArrayList<>();
            for (int i = 0; i < urls.length(); i++) {
                imgUrl.add(urls.optString(i));
            }
        }
        JSONObject act = jsonObj.optJSONObject("action");
        if (act != null) {
            action = new BeanTempletActionInfo();
            action.parseJSON(act);
        }
        JSONObject banner = jsonObj.optJSONObject("bannerInfo");
        if (banner != null) {
            bannerInfo = new BeanBannerInfo();
            bannerInfo.parseJSON(banner);
        }
        JSONObject vip = jsonObj.optJSONObject("vipInfo");
        if (vip != null) {
            vipInfo = new BeanVipInfo();
            vipInfo.parseJSON(vip);
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
        author = jsonObj.optString("author");
        desc = jsonObj.optString("desc");
        JSONArray jsonArray = jsonObj.optJSONArray("bookMarks");
        if (jsonArray != null && jsonArray.length() > 0) {
            this.bookMarks = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                this.bookMarks.add(jsonArray.optString(i));
            }
        }
        counter = jsonObj.optLong("counter");
        warn = jsonObj.optString("warn");
        delLine = jsonObj.optString("delLine");
        limit = jsonObj.optInt("limit");
        hasGot = jsonObj.optInt("hasGot");
        return this;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.itemMark);
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.type);
        dest.writeString(this.subTitle);
        dest.writeString(this.subScript);
        dest.writeString(this.actionUrl);
        dest.writeInt(this.isFix);
        dest.writeStringList(this.imgUrl);
        dest.writeParcelable(this.action, flags);
        dest.writeTypedList(this.items);
        dest.writeString(this.author);
        dest.writeString(this.desc);
        dest.writeStringList(this.bookMarks);
        dest.writeLong(this.counter);
        dest.writeString(this.warn);
        dest.writeString(this.delLine);
        dest.writeInt(this.limit);
        dest.writeInt(this.hasGot);
        dest.writeString(this.advert);
        dest.writeByte(this.isXm0Selected ? (byte) 1 : (byte) 0);
    }



}
