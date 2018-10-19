package hw.sdk.net.bean.store;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.dzbook.lib.utils.ALog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import hw.sdk.HwSdkAppConstant;
import hw.sdk.net.bean.HwPublicBean;

/**
 * 书城页面
 * @author dongdianzhou on 2018/1/12.
 */

public class BeanTempletsInfo extends HwPublicBean<BeanTempletsInfo> implements Parcelable {
    /**
     * Creator
     */
    public static final Creator<BeanTempletsInfo> CREATOR = new Creator<BeanTempletsInfo>() {
        @Override
        public BeanTempletsInfo createFromParcel(Parcel source) {
            return new BeanTempletsInfo(source);
        }

        @Override
        public BeanTempletsInfo[] newArray(int size) {
            return new BeanTempletsInfo[size];
        }
    };
    /**
     * channelId
     */
    public String channelId;
    /**
     * channels
     */
    private BeanTempletInfo channels;

    /**
     * section
     */
    private ArrayList<BeanTempletInfo> section;

    /**
     * whiteUrlList
     */
    public ArrayList<String> whiteUrlList;

    /**
     * 构造方法
     */
    public BeanTempletsInfo() {
    }
    /**
     * 构造方法
     * @param in in
     */
    protected BeanTempletsInfo(Parcel in) {
        this.section = in.createTypedArrayList(BeanTempletInfo.CREATOR);
        this.channelId = in.readString();
        this.channels = in.readParcelable(BeanTempletInfo.class.getClassLoader());
    }

    /**
     * 得到有效的频道
     *
     * @return 得到有效的频道
     */
    public ArrayList<BeanSubTempletInfo> getValidChannels() {
        return channels == null ? null : channels.getValidChannels();
    }
    /**
     * isContainChannel
     *
     * @return isContainChannel
     */
    public boolean isContainChannel() {
        return channels != null && channels.isContainItems();
    }
    /**
     * isContainTemplet
     *
     * @return isContainTemplet
     */
    public boolean isContainTemplet() {
        return section != null && section.size() > 0;
    }

    /**
     * 过滤掉无效的数据(不支持栏目不支持模板的数据过滤掉)
     *
     * @return 过滤掉无效的数据
     */
    public List<BeanTempletInfo> getSection() {
        List<BeanTempletInfo> list = new ArrayList<>();
        if (isContainTemplet()) {
            list.addAll(section);
            for (BeanTempletInfo info : section) {
                if (info != null && info.viewType == TempletMapping.VIEW_TYPE_SCRAP) {
                    list.remove(info);
                }

                //屏蔽限免
//                if (info != null && info.viewType == TempletMapping.VIEW_TYPE_XM0) {
//                    list.remove(info);
//                }

//                //屏蔽TopBanner
//                if (info != null && info.viewType == TempletMapping.VIEW_TYPE_BN0) {
//                    list.remove(info);
//                }
                if (info != null && isCheckItems(info.type)) {
                    if (!info.isContainItems()) {
                        list.remove(info);
                    }
                }
            }
        }
        return list;
    }

    /**
     * 是否选中的
     * @param type type
     * @return isCheckItems
     */
    private boolean isCheckItems(String type) {
        switch (type) {
            case TempletMapping.TYPE_PW:
            case TempletMapping.TYPE_TM:
            case TempletMapping.TYPE_LD:
            case TempletMapping.TYPE_XSLB:
                return false;
            default:
                return true;
        }
    }

    @Override
    public BeanTempletsInfo parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);
        JSONObject data = jsonObj.optJSONObject("data");
        if (data != null) {

            int abKey = data.optInt("abKey", 0);
            if (abKey == 1) {
                HwSdkAppConstant.setIsAbKey(true);
            }
            ALog.dZz("switch abKey:" + abKey);

            channelId = data.optString("channelId");
            JSONArray optJSONArray = data.optJSONArray("section");
            if (optJSONArray != null && optJSONArray.length() > 0) {
                this.section = new ArrayList<>();
                for (int i = 0; i < optJSONArray.length(); i++) {
                    JSONObject jsonObject = optJSONArray.optJSONObject(i);
                    if (jsonObject != null) {
                        BeanTempletInfo beanTempletInfo = new BeanTempletInfo();
                        beanTempletInfo.parseJSON(jsonObject);
                        this.section.add(beanTempletInfo);
                    }
                }
            }
            JSONObject channelObject = data.optJSONObject("channels");
            if (channelObject != null) {
                channels = new BeanTempletInfo();
                channels.parseJSON(channelObject);
            }

            JSONArray whiteUrlListArray = data.optJSONArray("whiteUrlList");
            if (whiteUrlListArray != null) {
                int listSize = whiteUrlListArray.length();
                if (listSize > 0) {
                    this.whiteUrlList = new ArrayList<>();
                    for (int i = 0; i < listSize; i++) {
                        String url = whiteUrlListArray.optString(i);
                        if (!TextUtils.isEmpty(url)) {
                            this.whiteUrlList.add(url.trim());
                        }
                    }
                }
            }

        }
        return this;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.section);
        dest.writeString(this.channelId);
        dest.writeParcelable(this.channels, flags);
    }



}
