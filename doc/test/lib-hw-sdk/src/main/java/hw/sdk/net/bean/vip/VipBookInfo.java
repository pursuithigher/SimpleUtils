package hw.sdk.net.bean.vip;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import hw.sdk.net.bean.HwPublicBean;

/**
 * vip 书籍info
 *
 * @author gavin
 */
public class VipBookInfo extends HwPublicBean<VipBookInfo> {

    /**
     * titlebean
     */
    public TitleBean titleBean;
    /**
     * 书籍bean
     */
    public List<BookBean> beanBookInfoList;

    @Override
    public VipBookInfo parseJSON(JSONObject jsonObj) {
        if (jsonObj == null) {
            return null;
        }
//        String string = jsonObj.toString();
//        showLog(string);

        titleBean = new TitleBean().parseJSON(jsonObj);

        JSONArray array = jsonObj.optJSONArray("items");
        if (array != null && array.length() > 0) {
            beanBookInfoList = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                BookBean bookBean = new BookBean().parseJSON(array.optJSONObject(i));
                beanBookInfoList.add(bookBean);
            }

        }
        return this;
    }

    /**
     * TitleBean
     */
    public static class TitleBean extends HwPublicBean<TitleBean> {

        /**
         * title
         */
        public String title;
        /**
         * 类型
         */
        public String type;
        /**
         * template
         */
        public String template;
        /**
         * actionTitle
         */
        public String actionTitle;
        /**
         * actionType
         */
        public String actionType;
        /**
         * actionDataId
         */
        public String actionDataId;

        @Override
        public TitleBean parseJSON(JSONObject jsonObj) {

            if (jsonObj == null) {
                return null;
            }

            title = jsonObj.optString("title");
            type = jsonObj.optString("type");
            template = jsonObj.optString("template");

            JSONObject jo = jsonObj.optJSONObject("action");
            if (jo != null) {
                actionTitle = jo.optString("title");
                actionType = jo.optString("type");
                actionDataId = jo.optString("dataId");
            }

            return this;
        }

    }

    /**
     * BookBean
     */
    public static class BookBean extends HwPublicBean<BookBean> {

        /**
         * title
         */
        public String title;
        /**
         * id
         */
        public String id;
        /**
         * imgUrl
         */
        public String imgUrl;
        /**
         * author
         */
        public String author;

        @Override
        public BookBean parseJSON(JSONObject jsonObj) {
            super.parseJSON(jsonObj);

            if (jsonObj == null) {
                return null;
            }

            title = jsonObj.optString("title");
            id = jsonObj.optString("id");
            author = jsonObj.optString("author");
            JSONArray ja = jsonObj.optJSONArray("imgUrl");
            if (ja != null && ja.length() > 0) {

                imgUrl = ja.optString(0);
            }

//            JSONObject jo = jsonObj.optJSONObject("action");
//            if (jo != null) {
//                actionTitle = jo.optString("title");
//                actionType = jo.optString("type");
//                actionDataId = jo.optString("dataId");
//            }
            return this;
        }

    }
}
