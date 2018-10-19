package com.dzbook.push;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * 推送消息封装bean
 *
 * @author lizhongzhong 2014-1-24
 */
public class BeanCloudyNotify implements Serializable {
    /**
     * 消息类型A代表新书推荐消息，B代表营销信息，C代表章节更新消息
     */
    private String type;
    /**
     * 书籍ID或营销信息网址或空字符串
     */
    private String identity;
    /**
     * 消息内容
     */
    private String content;
    /**
     * 通知标题title
     */
    private String notiTitle;

    /**
     * 消息id：第五个参数2017-10-28 11:53:55：追更书籍
     */
    private String messageId;

    /**
     * action扩展参数
     */
    private String actionParam;

    /**
     * 是否是系统推送：根据推送数据的格式进行判断
     * $符是个推后台推送json是系统推送默认系统推送
     */
    private String isSystemPush = "1";

    private String bno = "";

    /**
     * 构造
     */
    public BeanCloudyNotify() {

    }

    /**
     * 构造
     *
     * @param msgs msgs
     */
    public BeanCloudyNotify(String[] msgs) {
        isSystemPush = "2";
        if (msgs.length == 4) {
            this.type = msgs[0];
            this.identity = msgs[1];
            this.content = msgs[2];
            this.notiTitle = msgs[3];
        } else if (msgs.length == 5) {
            this.type = msgs[0];
            this.identity = msgs[1];
            this.content = msgs[2];
            this.notiTitle = msgs[3];
            this.messageId = msgs[4];
        } else {
            this.type = msgs[0];
            this.identity = msgs[1];
            this.content = msgs[2];
        }
    }

    public String getIsSystemPush() {
        return isSystemPush;
    }

    public String getBno() {
        return bno;
    }

    /**
     * 解析
     *
     * @param jsonObject jsonObject
     * @return BeanCloudyNotify
     */
    public BeanCloudyNotify parse(JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }
        isSystemPush = "1";
        this.type = jsonObject.optString("action");
        this.identity = jsonObject.optString("notiid");
        this.notiTitle = jsonObject.optString("notititle");
        this.content = jsonObject.optString("noticontent");
        this.messageId = jsonObject.optString("messageid");
        this.actionParam = jsonObject.optString("actionparam");
        this.bno = jsonObject.optString("bno");
        return this;
    }

    @Override
    public String toString() {
        return "BeanCloudyNotify{" + "type='" + type + '\'' + ", identity='" + identity + '\'' + ", content='" + content + '\'' + ", notiTitle='" + notiTitle + '\'' + ", messageId='" + messageId + '\'' + ", actionParam='" + actionParam + '\'' + ", isSystemPush='" + isSystemPush + '\'' + '}';
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNotiTitle() {
        return notiTitle;
    }

    public void setNotiTitle(String notiTitle) {
        this.notiTitle = notiTitle;
    }

}
