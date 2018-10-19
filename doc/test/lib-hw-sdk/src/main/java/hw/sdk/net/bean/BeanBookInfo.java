package hw.sdk.net.bean;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import hw.sdk.utils.JsonUtils;

/**
 * 书籍bean
 *
 * @author dongdz
 */
public class BeanBookInfo extends HwPublicBean<BeanBookInfo> {

    /**
     * 书籍在列表中的顺序，本地加的，用于打log
     */
    public int index;

    /**
     * 收费方式：0：章 1：本
     */
    public String unit;
    /**
     * 价格
     */
    public String price;
    /**
     * 支付提示
     */
    public String payTips;
    /**
     * 书籍状态：
     * 1：常态
     * 2：限免
     * 3：vip用户限免
     * 4：下架
     * 5：删除
     */
    public int control;
    /**
     * 书籍封面
     */
    public String coverWap;
    /**
     * 书籍名字
     */
    public String bookName;
    /**
     * 书籍id
     */
    public String bookId;
    /**
     * 书籍描述
     */
    public String introduction;
    /**
     * 书籍作者
     */
    public String author;
    /**
     * 单位
     */
    public String amount;
    /**
     * 点赞数
     */
    public int praiseNum;
    /**
     * 总共字数
     */
    public String totalWordSize;
    /**
     * 总共章节数
     */
    public String totalChapterNum;
    /**
     * 点击数
     */
    public String clickNum;
    /**
     * 书籍状态
     */
    public String statusShow;
    /**
     * 书籍评分
     */
    public String score;
    /**
     * 评论数
     */
    public String commentNum;
    /**
     * vip提示语
     */
    public String vipTips;
    /**
     * 书籍版权声明
     */
    public String bookCopyright;
    /**
     * 书籍版权声明
     */
    public String bookDisclaimer;
    /**
     * vip是否可点击：0：否 1：是
     */
    public int vipClickable;
    /**
     * 是否是vip书籍：0：否1：是
     */
    public int isVipBook;
    /**
     * 用户是否vip用户：0：否1：是
     */
    public int isVip;
    /**
     * 书籍标签
     */
    public ArrayList<String> tagList;
    /**
     * 今天是否点赞过：0否1：是
     */
    public int isPraise;

    /**
     * 书籍分享url
     */
    public String shareUrl;
    /**
     * 是否有章节更新：0无1：有
     */
    public int newChapter;


    /**
     * 书籍版权声明
     */
    public String cp;

    /**
     * 1：支持 0：不支持
     */
    public int isSupportH;

    /**
     * 最近阅读时间：仅云书架下行：暂无使用
     */
    public long timeTips;

    /**
     * 章节列表
     */
    public List<BeanChapterInfo> contentList;
    /**
     * 是否完本
     * 0连载 1完本
     */
    public int status;
    /**
     * 书籍划线价格
     */
    public String oldPrice;
    /**
     * 书籍当前价格
     */
    public String currentPrice;

    @Override

    public BeanBookInfo parseJSON(JSONObject jsonObj) {
        super.parseJSON(jsonObj);
        if (null != jsonObj) {
            this.bookId = jsonObj.optString("bookId");
            this.bookName = jsonObj.optString("bookName");
            this.introduction = jsonObj.optString("introduction");
            this.author = jsonObj.optString("author");
            this.coverWap = jsonObj.optString("coverWap");
            this.price = jsonObj.optString("price");
            this.totalChapterNum = jsonObj.optString("totalChapterNum");
            this.praiseNum = jsonObj.optInt("praiseNum");
            this.unit = jsonObj.optString("unit");
            this.clickNum = jsonObj.optString("clickNum");
            this.control = jsonObj.optInt("control");
            this.statusShow = jsonObj.optString("statusShow");
            this.status = jsonObj.optInt("status", -1);
            this.totalWordSize = jsonObj.optString("totalWordSize");
            this.payTips = jsonObj.optString("payTips");
            this.amount = jsonObj.optString("amount");
            this.oldPrice = jsonObj.optString("oldPrice");
            this.currentPrice = jsonObj.optString("currentPrice");
            this.bookCopyright = jsonObj.optString("copyright");
            this.bookDisclaimer = jsonObj.optString("disclaimer");
            this.score = jsonObj.optString("score");
            this.vipTips = jsonObj.optString("vipTips");
            this.vipClickable = jsonObj.optInt("vipClickable", 0);
            this.isVipBook = jsonObj.optInt("isVipBook");
            this.isVip = jsonObj.optInt("isVip");
            this.timeTips = jsonObj.optLong("timeTips");
            this.cp = jsonObj.optString("cp");
            this.isSupportH = jsonObj.optInt("isSupportH");
            JSONArray jsonArray = jsonObj.optJSONArray("tagList");
            if (null != jsonArray && jsonArray.length() > 0) {
                tagList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        Object o = jsonArray.get(i);
                        if (null != o && o instanceof String) {
                            this.tagList.add((String) o);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            this.isPraise = jsonObj.optInt("praise");
            this.shareUrl = jsonObj.optString("shareUrl");
            this.newChapter = jsonObj.optInt("newChapter");
            this.commentNum = jsonObj.optString("commentNum");

            JSONArray contentListArray = jsonObj.optJSONArray("contentList");
            contentList = JsonUtils.getChapterList(contentListArray);
        }
        return this;
    }

    @Override
    public String toString() {
        return "BeanBookInfo{" + "unit='" + unit + '\'' + ", price='" + price + '\'' + ", payTips='" + payTips + '\'' + ", control=" + control + ", coverWap='" + coverWap + '\'' + ", bookName='" + bookName + '\'' + ", bookId='" + bookId + '\'' + ", introduction='" + introduction + '\'' + ", author='" + author + '\'' + ", amount='" + amount + '\'' + ", praiseNum=" + praiseNum + ", totalWordSize='" + totalWordSize + '\'' + ", totalChapterNum='" + totalChapterNum + '\'' + ", clickNum='" + clickNum + '\'' + ", statusShow='" + statusShow + '\'' + ", score='" + score + '\'' + ", commentNum='" + commentNum + '\'' + ", vipTips='" + vipTips + '\'' + ", vipClickable=" + vipClickable + ", isVipBook=" + isVipBook + ", vip=" + isVip + ", tagList=" + tagList + ", praise=" + isPraise + ", shareUrl='" + shareUrl + '\'' + ", newChapter=" + newChapter + ", isSupportH=" + isSupportH + '}';
    }

    /**
     * 是否下架
     *
     * @return 是否下架
     */
    public boolean isUndercarriage() {
        return control == 4;
    }

    /**
     * 是否删除
     *
     * @return 是否删除
     */
    public boolean isDelete() {
        return control == 5;
    }

    /**
     * 是否删除或下架
     *
     * @return 是否删除或下架
     */
    public boolean isDeleteOrUndercarriage() {
        return control == 4 || control == 5;
    }

    /**
     * 是否单本书
     *
     * @return  是否单本书
     **/
    public boolean isSingleBook() {
        return TextUtils.equals(unit, "1");
    }

}
