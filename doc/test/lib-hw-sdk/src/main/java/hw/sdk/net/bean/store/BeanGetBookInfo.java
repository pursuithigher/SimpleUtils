package hw.sdk.net.bean.store;

import android.text.TextUtils;

import org.json.JSONObject;

import java.util.ArrayList;

import hw.sdk.net.bean.BeanBookInfo;
import hw.sdk.net.bean.BeanSingleBookInfo;
import hw.sdk.net.bean.HwPublicBean;
import hw.sdk.utils.JsonUtils;

/**
 * 打包定价
 *
 * @author winzows
 */
public class BeanGetBookInfo extends HwPublicBean<BeanGetBookInfo> {

    private  static final String GET_BOOK_SUCCESS = "5";
    /**
     * 价格
     */
    public String price;
    /**
     * balance
     */
    public String balance;
    /**
     * 数量
     */
    public String remainSum;
    /**
     * title
     */
    public String title;

    /**
     * 状态
     */
    public String status;
    /**
     * message
     */
    public String message;
    /**
     * 书籍列表
     */
    public ArrayList<BeanSingleBookInfo> books;

    @Override
    public BeanGetBookInfo parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);
        JSONObject data = null;
        if (isSuccess() && null != (data = jsonObj.optJSONObject("data"))) {
            status = data.optString("status");
            message = data.optString("message");
            price = data.optString("price");
            balance = data.optString("balance");
            remainSum = data.optString("remainSum");
            title = data.optString("title");
            books = JsonUtils.getSingleBookList(data.optJSONArray("books"));
        }
        return this;
    }


    public boolean isGetSuccess() {
        return !TextUtils.isEmpty(status) && GET_BOOK_SUCCESS.equals(status);
    }

    public boolean isContainItems() {
        return books != null && books.size() > 0;
    }

    /**
     * 获取书籍名字
     *
     * @return name
     */
    public String getBookNames() {
        StringBuffer bookNames = new StringBuffer();
        if (isContainItems()) {
            for (int i = 0; i < books.size(); i++) {
                BeanBookInfo book = books.get(i).bookInfo;
                if (null != book) {
                    bookNames.append("《" + book.bookName + "》");
                }
            }
        }
        return bookNames.toString();
    }
}
