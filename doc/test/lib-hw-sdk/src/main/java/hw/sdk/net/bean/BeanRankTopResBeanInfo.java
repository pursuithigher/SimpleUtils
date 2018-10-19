package hw.sdk.net.bean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 书城-排行封装bean
 *
 * @author lizhongzhong 2018-03-08
 */
public class BeanRankTopResBeanInfo extends HwPublicBean<BeanRankTopResBeanInfo> {

    /**
     * 书籍排行榜
     */
    public List<RandTopBean> rankTopResBean;

    /**
     * 书籍list
     */
    public List<BeanBookInfo> rankBooks;

    /**
     * 是否存在更多 0：否 1：是（控制的是rank_book字段有无更多数据）
     */
    public int isMore;

    @Override
    public BeanRankTopResBeanInfo parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);
        if (jsonObj == null) {
            return null;
        }
        if (isSuccess()) {
            JSONObject jsonPri = jsonObj.optJSONObject("data");

            isMore = jsonPri.optInt("isMore", 0);
            JSONArray array = jsonPri.optJSONArray("rankData");
            if (array != null) {
                rankTopResBean = new ArrayList<RandTopBean>();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject item = array.optJSONObject(i);
                    if (item != null) {
                        rankTopResBean.add(new RandTopBean().parseJSON(item));
                    }
                }
            }

            JSONArray jsonArray1 = jsonPri.optJSONArray("rankBook");
            if (jsonArray1 != null) {
                rankBooks = new ArrayList<>();
                for (int i = 0; i < jsonArray1.length(); i++) {
                    JSONObject item = jsonArray1.optJSONObject(i);
                    if (item != null) {
                        rankBooks.add(new BeanBookInfo().parseJSON(item));
                    }
                }
            }
        }

        return this;
    }

    public boolean isMoreData() {
        return isMore == 1;
    }

    /**
     * 排行榜内容bean
     */
    public static class RandTopBean extends HwPublicBean<RandTopBean> {

        /**
         * 一级id
         */
        public String id;

        /**
         * 名称
         */
        public String name;

        /**
         * 二级排行榜的bean
         */
        public List<RandSecondBean> rankSecondResBeans;

        @Override
        public RandTopBean parseJSON(JSONObject jsonObj) {
            super.parseJSON(jsonObj);

            if (jsonObj == null) {
                return null;
            }

            id = jsonObj.optString("id");
            name = jsonObj.optString("name");

            JSONArray jsonArray1 = jsonObj.optJSONArray("subList");
            if (jsonArray1 != null) {
                rankSecondResBeans = new ArrayList<RandSecondBean>();
                for (int i = 0; i < jsonArray1.length(); i++) {
                    JSONObject item = jsonArray1.optJSONObject(i);
                    if (item != null) {
                        rankSecondResBeans.add(new RandSecondBean(id).parseJSON(item));
                    }
                }
            }

            return this;
        }
    }

    /**
     * 二级排行榜
     */
    public static class RandSecondBean extends HwPublicBean<RandSecondBean> {

        /**
         * 一级的id
         */
        public String firstId;

        /**
         * 二级排行id
         */
        public String id;

        /**
         * 名称
         */
        public String name;

        /**
         * 构造
         * @param firstId 一级的id
         */
        public RandSecondBean(String firstId) {
            this.firstId = firstId;
        }

        @Override
        public RandSecondBean parseJSON(JSONObject jsonObj) {
            super.parseJSON(jsonObj);

            if (jsonObj == null) {
                return null;
            }

            id = jsonObj.optString("id");
            name = jsonObj.optString("name");

            return this;
        }

    }
}
