package hw.sdk.net.bean.type;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import hw.sdk.net.bean.BeanBookInfo;
import hw.sdk.net.bean.HwPublicBean;

/**
 * 分类二级页面的bean
 *
 * @author winzows on 2018/4/13
 */

public class BeanMainTypeDetail extends HwPublicBean<BeanMainTypeDetail> {

    /**
     * 排序的list
     */
    public ArrayList<BeanSortMark> sortMarkList;
    /**
     * 状态排序
     */
    public ArrayList<BeanStatusMark> statusMarkList;
    /**
     * 目录
     */
    public ArrayList<BeanCategoryMark> categoryMarkList;
    /**
     * 书籍表
     */
    public ArrayList<BeanBookInfo> bookInfoList;

    @Override
    public BeanMainTypeDetail parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);

        if (isSuccess()) {
            JSONObject data = jsonObj.optJSONObject("data");
            if (data != null) {
                parseSortMark(data.optJSONArray("sortMark"));
                parseStatusMark(data.optJSONArray("statusMark"));
                parseCategoryMark(data.optJSONArray("categoryMark"));
                parseBookList(data.optJSONArray("bookList"));
            }
        }

        return this;
    }

    private void parseBookList(JSONArray bookListArray) {
        if (bookListArray != null && bookListArray.length() > 0) {
            bookInfoList = new ArrayList<>();
            for (int i = 0; i < bookListArray.length(); i++) {
                JSONObject obj = bookListArray.optJSONObject(i);
                if (obj != null) {
                    BeanBookInfo bookInfo = new BeanBookInfo().parseJSON(obj);
                    bookInfoList.add(bookInfo);
                }
            }
        }
    }

    private void parseCategoryMark(JSONArray categoryMarkArray) {
        if (categoryMarkArray != null && categoryMarkArray.length() > 0) {
            categoryMarkList = new ArrayList<>();
            for (int i = 0; i < categoryMarkArray.length(); i++) {
                JSONObject obj = categoryMarkArray.optJSONObject(i);
                if (obj != null) {
                    BeanCategoryMark categoryMark = new BeanCategoryMark().parseJSON(obj);
                    categoryMarkList.add(categoryMark);
                }
            }
        }
    }

    private void parseStatusMark(JSONArray statusMarkArray) {
        if (statusMarkArray != null && statusMarkArray.length() > 0) {
            statusMarkList = new ArrayList<>();
            for (int i = 0; i < statusMarkArray.length(); i++) {
                JSONObject obj = statusMarkArray.optJSONObject(i);
                if (obj != null) {
                    BeanStatusMark statusMark = new BeanStatusMark().parseJSON(obj);
                    statusMarkList.add(statusMark);
                }
            }
        }
    }

    private void parseSortMark(JSONArray sortMarkArray) {
        if (sortMarkArray != null && sortMarkArray.length() > 0) {
            sortMarkList = new ArrayList<>();
            for (int i = 0; i < sortMarkArray.length(); i++) {
                JSONObject obj = sortMarkArray.optJSONObject(i);
                if (obj != null) {
                    BeanSortMark sortMark = new BeanSortMark().parseJSON(obj);
                    sortMarkList.add(sortMark);
                }
            }
        }
    }

    /**
     * 检查数据
     *
     * @return true false
     */
    public boolean checkSortListData() {
        return sortMarkList != null && sortMarkList.size() > 0;
    }

    /**
     * 检查数据
     *
     * @return true false
     */
    public boolean checkStatusListData() {
        return statusMarkList != null && statusMarkList.size() > 0;
    }

    /**
     * 检查数据
     *
     * @return true false
     */
    public boolean checkCategoryListData() {
        return categoryMarkList != null && categoryMarkList.size() > 0;
    }

    /**
     * 检查数据
     *
     * @return true false
     */
    public boolean checkBookInfoList() {
        return bookInfoList != null && bookInfoList.size() > 0;
    }

    /**
     * 检查数据
     *
     * @return true false
     */
    public boolean checkTopViewData() {
        return checkStatusListData() || checkCategoryListData() || checkSortListData();
    }

    /**
     * 请求服务器的bean
     */
    public static class TypeFilterBean {
        private String sort = "1";
        private String tid = "";
        private String cid = "";
        private String status = "";

        public String getSort() {
            return sort;
        }

        public void setSort(String sort) {
            this.sort = sort;
        }

        public String getTid() {
            return tid;
        }

        public void setTid(String tid) {
            this.tid = tid;
        }

        public String getCid() {
            return cid;
        }

        public void setCid(String cid) {
            this.cid = cid;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

    }
}
