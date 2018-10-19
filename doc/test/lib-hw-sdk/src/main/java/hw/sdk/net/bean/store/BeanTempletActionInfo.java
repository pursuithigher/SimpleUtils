package hw.sdk.net.bean.store;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import hw.sdk.net.bean.HwPublicBean;

/**
 * 书城 Action bean
 * @author dongdianzhou on 2018/1/12.
 */

public class BeanTempletActionInfo extends HwPublicBean<BeanTempletActionInfo> implements Parcelable {

    /**
     * Creator
     */
    public static final Creator<BeanTempletActionInfo> CREATOR = new Creator<BeanTempletActionInfo>() {
        @Override
        public BeanTempletActionInfo createFromParcel(Parcel source) {
            return new BeanTempletActionInfo(source);
        }

        @Override
        public BeanTempletActionInfo[] newArray(int size) {
            return new BeanTempletActionInfo[size];
        }
    };


    /**
     * dataId
     */
    public String dataId;
    /**
     * title
     */
    public String title;
    /**
     * url
     */
    public String url;
    /**
     * type
     */
    public String type;
    /**
     * sucName
     */
    public String sucName;
    /**
     * pType
     */
    public String pType;

    /**
     * 构造器
     */
    public BeanTempletActionInfo() {
    }

    /**
     * 构造器
     * @param  in Parcel
     */
    protected BeanTempletActionInfo(Parcel in) {
        this.dataId = in.readString();
        this.title = in.readString();
        this.url = in.readString();
        this.type = in.readString();
        this.sucName = in.readString();
        this.pType = in.readString();
    }

    @Override
    public BeanTempletActionInfo parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);
        dataId = jsonObj.optString("dataId");
        title = jsonObj.optString("title");
        url = jsonObj.optString("url");
        type = jsonObj.optString("type");
        sucName = jsonObj.optString("sucName");
        //活动类型
        pType = jsonObj.optString("pType");
        return this;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.dataId);
        dest.writeString(this.title);
        dest.writeString(this.url);
        dest.writeString(this.type);
        dest.writeString(this.sucName);
        dest.writeString(this.pType);
    }


}
