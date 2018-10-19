package com.dzbook.bean;

import android.text.TextUtils;

import org.json.JSONObject;

import hw.sdk.net.bean.HwPublicBean;

/**
 * ShareBeanInfo
 * @author lizhongzhong 2017/12/9.
 */

public class ShareBeanInfo extends HwPublicBean {

    /**
     * 微信朋友圈
     */
    public ShareBean wxPyqBean;

    /**
     * 微信好友
     */
    public ShareBean wxHyBean;

    /**
     * QQ好友
     */
    public ShareBean qqHyBean;

    /**
     * QQ空间
     */
    public ShareBean qqKJBean;

    /**
     * 微博
     */
    public ShareBean weiBoBean;

    /**
     * 替身模式  1是替身模式  其他的是sdk模式 默认是sdk模式
     */
    public int style = -1;

    @Override
    public ShareBeanInfo parseJSON(JSONObject jsonObj) {

        if (jsonObj == null) {
            return null;
        }

        wxPyqBean = new ShareBean().parseShareItem(jsonObj.optJSONObject("wxPyq"));
        wxHyBean = new ShareBean().parseShareItem(jsonObj.optJSONObject("wxHy"));
        qqHyBean = new ShareBean().parseShareItem(jsonObj.optJSONObject("qqHy"));
        qqKJBean = new ShareBean().parseShareItem(jsonObj.optJSONObject("qqKJ"));
        weiBoBean = new ShareBean().parseShareItem(jsonObj.optJSONObject("weiBo"));

        style = jsonObj.optInt("style", -1);

        return this;
    }

    /**
     * 为了 书籍详情分享统一
     *
     * @param title     title
     * @param introduce introduce
     * @param shareUrl  shareUrl
     * @param imageUrl  imageUrl
     */
    public void setShareParam(String title, String introduce, String shareUrl, String imageUrl) {
        wxPyqBean = new ShareBean(title, introduce, shareUrl, imageUrl);
        wxHyBean = new ShareBean(title, introduce, shareUrl, imageUrl);
        qqHyBean = new ShareBean(title, introduce, shareUrl, imageUrl);
        qqKJBean = new ShareBean(title, introduce, shareUrl, imageUrl);
        weiBoBean = new ShareBean(title, introduce, shareUrl, imageUrl);
    }

    @Override
    public String toString() {
        return "微信朋友圈：" + wxPyqBean.toString() + ",微信好友：" + wxHyBean.toString() + ",QQ空间：" + qqKJBean.toString() + ",qq好友：" + qqHyBean;
    }

    /**
     * 是否可以展示
     *
     * @param bean bean
     * @return boolean
     */
    public static boolean isShow(ShareBean bean) {
        return null != bean && !TextUtils.isEmpty(bean.des) && !TextUtils.isEmpty(bean.img) && !TextUtils.isEmpty(bean.title) && !TextUtils.isEmpty(bean.url);
    }

    /**
     * ShareBean
     */
    public static class ShareBean extends HwPublicBean {
        /**
         * 分享title
         */
        public String title;

        /**
         * 分享描述"
         */
        public String des;

        /**
         * 分享url
         */
        public String url;

        /**
         *img
         */
        public String img;

        ShareBean() {

        }

        /**
         * ShareBean
         * @param title title
         * @param des  des
         * @param url url
         * @param img img
         */
        public ShareBean(String title, String des, String url, String img) {
            this.title = title;
            this.des = des;
            this.url = url;
            this.img = img;
        }

        /**
         * 解析分享内容
         *
         * @param jsonObj
         * @return
         */
        ShareBean parseShareItem(JSONObject jsonObj) {
            if (jsonObj == null) {
                return this;
            }
            url = jsonObj.optString("url");
            title = jsonObj.optString("title");
            des = jsonObj.optString("des");
            img = jsonObj.optString("img");
            return this;
        }

        @Override
        public String toString() {
            return "(url" + url + ",title:" + title + ",des:" + des + ",img:" + img + ")";
        }
    }
}
