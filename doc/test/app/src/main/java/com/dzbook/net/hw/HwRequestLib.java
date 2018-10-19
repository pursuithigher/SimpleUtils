package com.dzbook.net.hw;

import android.content.Context;
import android.text.TextUtils;

import com.dzbook.AppConst;
import com.dzbook.bean.BookMarkSyncInfo;
import com.dzbook.database.bean.HttpCacheInfo;
import com.dzbook.lib.utils.ALog;
import com.dzbook.lib.utils.SDCardUtil;
import com.dzbook.utils.DBUtils;
import com.dzbook.utils.FileUtils;
import com.dzbook.utils.SpUtil;
import com.dzpay.recharge.netbean.LoadChaptersCommonBeanInfo;
import com.dzpay.recharge.netbean.RechargeListBeanInfo;

import org.json.JSONObject;

import java.util.ArrayList;

import hw.sdk.net.bean.ActivityCenterBean;
import hw.sdk.net.bean.BeanChapterCatalog;
import hw.sdk.net.bean.BeanCidUpload;
import hw.sdk.net.bean.BeanLoginVerifyCode;
import hw.sdk.net.bean.BeanRankTopResBeanInfo;
import hw.sdk.net.bean.BeanSwitchPhoneNum;
import hw.sdk.net.bean.FastOpenBook;
import hw.sdk.net.bean.OldUserFlagBean;
import hw.sdk.net.bean.bookDetail.BeanBookDetail;
import hw.sdk.net.bean.bookDetail.BeanCommentAction;
import hw.sdk.net.bean.bookDetail.BeanCommentCheck;
import hw.sdk.net.bean.bookDetail.BeanCommentMore;
import hw.sdk.net.bean.bookDetail.BeanCommentResult;
import hw.sdk.net.bean.cloudshelf.BeanCloudShelfPageListInfo;
import hw.sdk.net.bean.cloudshelf.BeanSingleBookReadProgressInfo;
import hw.sdk.net.bean.consume.ConsumeBookSumBeanInfo;
import hw.sdk.net.bean.consume.ConsumeSecondBeanInfo;
import hw.sdk.net.bean.consume.ConsumeThirdBeanInfo;
import hw.sdk.net.bean.gift.GiftListBeanInfo;
import hw.sdk.net.bean.reader.BeanBookRecomment;
import hw.sdk.net.bean.reader.MissContentBeanInfo;
import hw.sdk.net.bean.reader.MoreRecommendBook;
import hw.sdk.net.bean.record.RechargeRecordBeanInfo;
import hw.sdk.net.bean.register.DeviceActivationBeanInfo;
import hw.sdk.net.bean.register.RegisterBeanInfo;
import hw.sdk.net.bean.register.UserInfoBeanInfo;
import hw.sdk.net.bean.seach.BeanSearch;
import hw.sdk.net.bean.seach.BeanSearchHot;
import hw.sdk.net.bean.seach.BeanSuggest;
import hw.sdk.net.bean.shelf.BeanBookUpdateInfo;
import hw.sdk.net.bean.shelf.BeanBuiltInBookListInfo;
import hw.sdk.net.bean.shelf.BeanShelfBookItem;
import hw.sdk.net.bean.store.BeanGetBookInfo;
import hw.sdk.net.bean.store.BeanTempletsInfo;
import hw.sdk.net.bean.task.FinishTask;
import hw.sdk.net.bean.task.ShareKd;
import hw.sdk.net.bean.tms.BeanTmsRespQuery;
import hw.sdk.net.bean.tms.BeanTmsRespSign;
import hw.sdk.net.bean.tts.Plugins;
import hw.sdk.net.bean.type.BeanMainType;
import hw.sdk.net.bean.type.BeanMainTypeDetail;
import hw.sdk.net.bean.vip.VipAutoRenewStatus;
import hw.sdk.net.bean.vip.VipBeanInfo;
import hw.sdk.net.bean.vip.VipCancelAutoRenewBeanInfo;
import hw.sdk.net.bean.vip.VipContinueOpenHisBeanInfo;
import hw.sdk.net.bean.vip.VipWellInfo;
import hw.sdk.net.bean.vouchers.VouchersListBeanInfo;

/**
 * 封装了 解析和网络请求
 *
 * @author winzows 2018/4/13
 */

public class HwRequestLib {

    private static volatile HwRequestLib mRequestLib;
    private HwRequest mRequest;

    private HwRequestLib() {
        mRequest = new HwRequest();
    }

    /**
     * 获取HwRequestLib实例
     *
     * @return 实例
     */
    public static HwRequestLib getInstance() {
        if (mRequestLib == null) {
            synchronized (HwRequestLib.class) {
                if (mRequestLib == null) {
                    mRequestLib = new HwRequestLib();
                }
            }
        }
        return mRequestLib;
    }

    /**
     * 获取HwRequest
     *
     * @return HwRequest
     */
    public HwRequest getmRequest() {
        return mRequest;
    }

    /**
     * flog
     *
     * @param msg msg
     */
    public static void flog(String msg) {
        ALog.f(SDCardUtil.getInstance().getSDCardAndroidRootDir() + "/" + FileUtils.APP_LOG_DIR_PATH + "_log.txt", msg);
    }

    /**
     * 分类 一级页面
     *
     * @return BeanMainType
     * @throws Exception 异常
     */
    public BeanMainType getMainTypeIndex() throws Exception {
        String json = mRequest.getMainTypeIndex();
        return new BeanMainType().parseJSON(new JSONObject(json));
    }

    /**
     * 分类 二级页面
     *
     * @param status    status
     * @param flag      flag
     * @param cid       cid
     * @param pageIndex pageIndex
     * @param pageSize  pageSize
     * @param sort      sort
     * @param tid       tid
     * @return BeanMainTypeDetail
     * @throws Exception 异常
     */
    public BeanMainTypeDetail getMainTypeDetailData(String sort, String tid, String status, String cid, String flag, String pageIndex, String pageSize) throws Exception {
        String json = mRequest.getMainTypeDetailData(sort, tid, status, cid, flag, pageIndex, pageSize);
        return new BeanMainTypeDetail().parseJSON(new JSONObject(json));
    }

    /**
     * 发起注册请求获取userId和token
     *
     * @param hwUid         hwUid
     * @param avatar        avatar
     * @param hwAccessToken hwAccessToken
     * @param hwOpenId      hwOpenId
     * @param nickName      nickName
     * @param utdid         utdid
     * @return RegisterBeanInfo
     * @throws Exception 异常
     */
    public RegisterBeanInfo launchRegisterRequest(String hwUid, String hwAccessToken, String hwOpenId, String avatar, String nickName, String utdid) throws Exception {
        String json = mRequest.launchRegisterRequest(hwUid, hwAccessToken, hwOpenId, avatar, nickName, utdid);
        return new RegisterBeanInfo().parseJSON(new JSONObject(json));
    }

    /**
     * 设备激活
     *
     * @param utdId utdId
     * @return DeviceActivationBeanInfo
     * @throws Exception 异常
     */
    public DeviceActivationBeanInfo launchDeviceActivationRequest(String utdId) throws Exception {
        String json = mRequest.launchDeviceActivationRequest(utdId);
        return new DeviceActivationBeanInfo().parseJSON(new JSONObject(json));
    }

    /**
     * 内置书接口
     *
     * @return BeanBuiltInBookListInfo
     * @throws Exception 异常
     */
    public BeanBuiltInBookListInfo buildInBooK() throws Exception {
        String json = mRequest.buildInBooK();
        return new BeanBuiltInBookListInfo().parseJSON(new JSONObject(json));
    }

    /**
     * 书架书籍更新接口
     *
     * @param sex            :偏好
     * @param makeUpFunction ：请求功能
     * @param books          ：书籍列表          json（[{"bookId":"11000007217","chapterId": ""},{"bookId":"11000008404","chapterId": ""},{"bookId":"11000079808","chapterId": ""}]）
     * @return BeanBookUpdateInfo
     * @throws Exception 异常
     */
    public BeanBookUpdateInfo shelfBookUpdate(String sex, String makeUpFunction, ArrayList<BeanShelfBookItem> books) throws Exception {
        String json = mRequest.shelfBookUpdate(sex, makeUpFunction, books);
        return new BeanBookUpdateInfo().parseJSON(new JSONObject(json));
    }

    /**
     * 网络请求书城数据
     *
     * @param channelId   ：频道id
     * @param readPref    ：偏好
     * @param context     context
     * @param channelType channelType
     * @return BeanTempletsInfo
     * @throws Exception 异常
     */
    public BeanTempletsInfo getStorePageDataFromNet(Context context, String channelId, String readPref, String channelType) throws Exception {
        String json = mRequest.getStoreDataFromNet(readPref, channelId, channelType);
        BeanTempletsInfo beanTempletsInfo = new BeanTempletsInfo().parseJSON(new JSONObject(json));
        if (beanTempletsInfo != null && beanTempletsInfo.isSuccess()) {
            HttpCacheInfo httpCacheInfo = new HttpCacheInfo();
            httpCacheInfo.url = RequestCall.STORE_DATA_URL + channelId;
            httpCacheInfo.response = json;
            httpCacheInfo.gmt_create = System.currentTimeMillis() + "";
            DBUtils.updateOrInsertHttpCacheInfo(context, httpCacheInfo);
        }
        return beanTempletsInfo;
    }

    /**
     * 网络请求书城二级数据
     *
     * @param id       :栏目id
     * @param tabId    ：频道id
     * @param readPref ：偏好
     * @return BeanTempletsInfo
     * @throws Exception 异常
     */
    public BeanTempletsInfo getStoreTwoPageDataFromNet(String id, String tabId, String readPref) throws Exception {
        String json = mRequest.getStoreTwoPageDataFromNet(readPref, id, tabId);
        return new BeanTempletsInfo().parseJSON(new JSONObject(json));
    }

    /**
     * 书城更多接口数据
     *
     * @param url :接口请求url
     * @return BeanTempletsInfo
     * @throws Exception 异常
     */
    public BeanTempletsInfo getStoreMoreDataFromNet(String url) throws Exception {
        String json = mRequest.getStoreMoreDataFromNet(url);
        return new BeanTempletsInfo().parseJSON(new JSONObject(json));
    }

    /**
     * 搜索
     *
     * @param keyWord    关键词
     * @param index      第几页
     * @param size       一页最大展示
     * @param searchType 搜索类型
     * @return BeanSearch
     * @throws Exception 异常
     */
    public BeanSearch searchRequest(String keyWord, int index, int size, String searchType) throws Exception {
        String json = mRequest.searchRequest(keyWord, index, size, searchType);
        return new BeanSearch().parseJSON(new JSONObject(json));
    }

    /**
     * 联想词搜索
     *
     * @param keyWord keyWord
     * @return BeanSuggest
     * @throws Exception 异常
     */
    public BeanSuggest searchSuggestRequest(String keyWord) throws Exception {
        String json = mRequest.searchSuggestRequest(keyWord);
        return new BeanSuggest().parseJSON(new JSONObject(json));
    }

    /**
     * 更多推荐书籍
     *
     * @param bookId   bookId
     * @param page     page
     * @param pageSize pageSize
     * @param type     type
     * @return MoreRecommendBook
     * @throws Exception 异常
     */
    public MoreRecommendBook moreRecommendBooks(String bookId, int page, int pageSize, int type) throws Exception {
        String json = mRequest.moreRecommendBooks(bookId, page, pageSize, type);
        return new MoreRecommendBook().parseJSON(new JSONObject(json));
    }

    /**
     * shareKd
     *
     * @param type type
     * @return shareKd
     * @throws Exception 异常
     */
    public ShareKd shareKd(int type) throws Exception {
        String json = mRequest.shareKd(type);
        return new ShareKd().parseJSON(new JSONObject(json));
    }

    /**
     * 搜索页热词
     *
     * @param sex sex
     * @return BeanSearchHot
     * @throws Exception 异常
     */
    public BeanSearchHot searchHotRequest(int sex) throws Exception {
        String json = mRequest.searchHotRequest(sex);
        BeanSearchHot beanSearchHot = new BeanSearchHot().parseJSON(new JSONObject(json));
        if (null != beanSearchHot && beanSearchHot.isSuccess()) {
            HttpCacheInfo httpCacheInfo = new HttpCacheInfo();
            httpCacheInfo.url = RequestCall.SEARCH_HOT_CALL;
            httpCacheInfo.response = json;
            httpCacheInfo.gmt_create = System.currentTimeMillis() + "";
            DBUtils.updateOrInsertHttpCacheInfo(AppConst.getApp(), httpCacheInfo);
        }
        return beanSearchHot;
    }

    /**
     * 签署协议
     *
     * @param signInfoJson 签名列表
     * @param token        用户的accessToken
     * @return 签署协议后 返回的bean
     * @throws Exception 异常
     */
    public BeanTmsRespSign signAgreement(String signInfoJson, String token) throws Exception {
        String json = mRequest.signOrQueryAgreement(signInfoJson, token, 1);
        ALog.dWz("signAgreement " + json);
        return new BeanTmsRespSign().parseJSON(new JSONObject(json));
    }

    /**
     * 查询协议
     *
     * @param signInfoJson 查询之前签署过的协议
     * @param token        用户的accessToken
     * @return 签署协议后 返回的bean
     * @throws Exception 异常
     */
    public BeanTmsRespQuery queryAgreement(String signInfoJson, String token) throws Exception {
        String json = mRequest.signOrQueryAgreement(signInfoJson, token, 2);
        ALog.dWz("queryAgreement " + json);
        return new BeanTmsRespQuery().parseJSON(new JSONObject(json));
    }

    /**
     * 如果用户存在资产，则返回阅读H5页面
     *
     * @param uid userId
     * @return 用户是否存在资产的bean
     * @throws Exception e
     */
    public OldUserFlagBean getOldUserFlagBean(String uid) throws Exception {
        String json = mRequest.getOldUserAssertRequest(uid);
        ALog.dWz("getOldUserAssertRequest " + json);
        return new OldUserFlagBean().parseJSON(new JSONObject(json));
    }


    /**
     * 图书详情
     *
     * @param bookId bookId
     * @return BeanBookDetail
     * @throws Exception 异常
     */
    public BeanBookDetail bookdetailRequest(String bookId) throws Exception {
        String json = mRequest.bookDetailRequest(bookId);
        return new BeanBookDetail().parseJSON(new JSONObject(json));
    }

    /**
     * 书籍点赞
     *
     * @param bookId bookId
     * @return String
     * @throws Exception 异常
     */
    public String bookPraiseRequest(String bookId) throws Exception {
        return mRequest.bookPraiseRequest(bookId);
    }

    /**
     * 完成任务
     *
     * @param action       1-分享；4-加入书架；12-阅读时长；15-夜间模式；16-安装任务
     * @param readDuration 阅读时长 action = 12 时，必须
     * @return FinishTask
     * @throws Exception 异常
     */
    public FinishTask finishTask(String action, int readDuration) throws Exception {
        //无token不发起
        if (TextUtils.isEmpty(SpUtil.getinstance(AppConst.getApp()).getAppToken())) {
            return null;
        }

        String json = mRequest.finishTask(action, readDuration);
        return new FinishTask().parseJSON(new JSONObject(json));
    }

    /**
     * 全部评论
     *
     * @param bookId    bookId
     * @param pageIndex pageIndex
     * @param pageSize  pageSize
     * @return BeanCommentMore
     * @throws Exception 异常
     */
    public BeanCommentMore moreCommentRequest(String bookId, int pageIndex, int pageSize) throws Exception {
        String json = mRequest.moreCommentRequest(bookId, pageIndex, pageSize);
        return new BeanCommentMore().parseJSON(new JSONObject(json));
    }

    /**
     * 用户评论 个人中心
     *
     * @param pageIndex pageIndex
     * @param pageSize  pageSize
     * @return BeanCommentMore
     * @throws Exception 异常
     */
    public BeanCommentMore userCommentRequest(int pageIndex, int pageSize) throws Exception {
        String json = mRequest.userCommentRequest(pageIndex, pageSize);
        return new BeanCommentMore().parseJSON(new JSONObject(json));
    }

    /**
     * 检查评论
     *
     * @param bookId bookId
     * @return BeanCommentCheck
     * @throws Exception 异常
     */
    public BeanCommentCheck checkCommentRequest(String bookId) throws Exception {
        String json = mRequest.checkCommentRequest(bookId);
        return new BeanCommentCheck().parseJSON(new JSONObject(json));
    }

    /**
     * 评论点赞 举报 删除
     *
     * @param type      type
     * @param bookId    bookId
     * @param commentId commentId
     * @return BeanCommentAction
     * @throws Exception 异常
     */
    public BeanCommentAction commentActionRequest(int type, String bookId, String commentId) throws Exception {
        String json = mRequest.commentActionRequest(type, bookId, commentId);
        return new BeanCommentAction().parseJSON(new JSONObject(json));
    }

    /**
     * 发表评论
     *
     * @param bookId    bookId
     * @param content   content
     * @param score     打分
     * @param bookName  bookName
     * @param type      1 发表新评论 2 编辑评论
     * @param commentId type=2 时必须
     * @return BeanCommentResult
     * @throws Exception 异常
     */
    public BeanCommentResult sendCommentRequest(String bookId, String content, int score, String bookName, int type, String commentId) throws Exception {
        String json = mRequest.sendCommentRequest(bookId, content, score, bookName, type, commentId);
        return new BeanCommentResult().parseJSON(new JSONObject(json));
    }

    /**
     * 获取章节目录
     *
     * @param bookId         bookId
     * @param startChapterId startChapterId
     * @param chapterNum     chapterNum
     * @param endChapterId   endChapterId
     * @param needBlockList  needBlockList
     * @return BeanChapterCatalog
     * @throws Exception 异常
     */
    public BeanChapterCatalog chapterCatalog(String bookId, String startChapterId, String chapterNum, String endChapterId, String needBlockList) throws Exception {
        String json = mRequest.chapterCatalog(bookId, startChapterId, chapterNum, endChapterId, needBlockList);
        return new BeanChapterCatalog().parseJSON(new JSONObject(json));
    }

    /**
     * 终章推荐
     *
     * @param bookId bookId
     * @return BeanBookRecomment
     * @throws Exception 异常
     */
    public BeanBookRecomment bookRecommentRequest(String bookId) throws Exception {
        String json = mRequest.bookRecommendRequest(bookId);
        return new BeanBookRecomment().parseJSON(new JSONObject(json));
    }

    /**
     * 快速打开书籍
     *
     * @param bookId    bookId
     * @param chapterId chapterId
     * @return FastOpenBook
     * @throws Exception 异常
     */
    public FastOpenBook fastOpenBookRequest(String bookId, String chapterId) throws Exception {
        String json = mRequest.fastOpenBookRequest(bookId, chapterId);
        return new FastOpenBook().parseJSON(new JSONObject(json));
    }

    /**
     * 订购-后台多章加载
     *
     * @param bookId     书籍id
     * @param chapterIds 预加载的章节列表
     * @param autoPay    是否直接购买：1，否；2，是
     * @return LoadChaptersCommonBeanInfo
     * @throws Exception 异常
     */
    public LoadChaptersCommonBeanInfo preloadLotChapterBeanInfo(String bookId, ArrayList<String> chapterIds, String autoPay) throws Exception {
        String json = mRequest.preloadLotChapterRequest(bookId, chapterIds, autoPay);
        return new LoadChaptersCommonBeanInfo().parseJSON(new JSONObject(json));
    }

    /**
     * 订购-加载已经订购章节
     *
     * @param bookId    书籍id
     * @param chapterId 章节id，当前在读章节之后第一个无内容章节id
     * @return LoadChaptersCommonBeanInfo
     * @throws Exception 异常
     */
    public LoadChaptersCommonBeanInfo loadAlreadyOrderChapterBeanInfo(String bookId, String chapterId) throws Exception {
        String json = mRequest.loadAlreadyOrderChapterRequest(bookId, chapterId);
        return new LoadChaptersCommonBeanInfo().parseJSON(new JSONObject(json));
    }

    /**
     * 领书接口（限免/限价）
     *
     * @param productId ：商品id
     * @param bookId    ：书籍id
     * @param type      ：领书类型
     * @return BeanGetBookInfo
     * @throws Exception 异常
     */
    public BeanGetBookInfo getBookInfoFromNet(String productId, String bookId, String type) throws Exception {
        String json = mRequest.getBookInfoFromNet(productId, bookId, type);
        return new BeanGetBookInfo().parseJSON(new JSONObject(json));
    }

    /**
     * 发送短信验证码
     *
     * @param phoneNum phoneNum
     * @return BeanLoginVerifyCode
     * @throws Exception 异常
     */
    public BeanLoginVerifyCode getVerifyByPhoneRequest(String phoneNum) throws Exception {
        String json = mRequest.getVerifyByPhoneRequest(phoneNum);
        return new BeanLoginVerifyCode().parseJSON(new JSONObject(json));
    }

    /**
     * 绑定手机号码
     *
     * @param phoneNum   phoneNum
     * @param type       type
     * @param verifyCode verifyCode
     * @return BeanLoginVerifyCode
     * @throws Exception 异常
     */
    public BeanLoginVerifyCode bindVerifyByPhoneRequest(int type, String phoneNum, String verifyCode) throws Exception {
        String json = mRequest.sendVerifyByPhoneRequest(type, phoneNum, verifyCode);
        return new BeanLoginVerifyCode().parseJSON(new JSONObject(json));
    }

    /**
     * 获取之前实名认证 绑定的手机号信息
     *
     * @return BeanSwitchPhoneNum
     * @throws Exception 异常
     */
    public BeanSwitchPhoneNum getSwitchPhoneNumInfo() throws Exception {
        String json = mRequest.getSwitchPhoneNumInfo();
        return new BeanSwitchPhoneNum().parseJSON(new JSONObject(json));
    }

    /**
     * 登录后同步云书架
     *
     * @param bookIds bookIds
     * @return String
     * @throws Exception 异常
     */
    public String cloudShelfLoginSync(String bookIds) throws Exception {
        String json = mRequest.cloudShelfLoginSync(bookIds);
        return json;
    }

    /**
     * 获取云书架同步后的数据详情列表
     *
     * @param bookIds bookIds
     * @return BeanGetBookInfo
     * @throws Exception 异常
     */
    public BeanGetBookInfo getCloudShelfBookDetail(ArrayList<String> bookIds) throws Exception {
        String json = mRequest.getCloudShelfBookDetail(bookIds);
        return new BeanGetBookInfo().parseJSON(new JSONObject(json));
    }

    /**
     * 云书架：同步书籍的阅读进度
     *
     * @param bookId     :书籍id
     * @param chapterId  ：章节id
     * @param operateDur ：阅读时间
     * @throws Exception 异常
     */
    public void syncBookReadProgress(String bookId, String chapterId, String operateDur) throws Exception {
        mRequest.syncBookReadProgress(bookId, chapterId, operateDur);
    }

    /**
     * 云书架：同步远程书籍的阅读进度
     *
     * @param bookId    ：书籍id
     * @param chapterId ：章节id
     * @return BeanSingleBookReadProgressInfo
     * @throws Exception 异常
     */
    public BeanSingleBookReadProgressInfo syncBookReadProgressFromNet(String bookId, String chapterId) throws Exception {
        String json = mRequest.syncBookReadProgressFromNet(bookId, chapterId);
        return new BeanSingleBookReadProgressInfo().parseJSON(new JSONObject(json));
    }

    /**
     * 云书架：页面数据获取
     *
     * @param page         ：页码
     * @param size         ：数量
     * @param lastItemTime lastItemTime
     * @return BeanCloudShelfPageListInfo
     * @throws Exception 异常
     */
    public BeanCloudShelfPageListInfo getCloudShelfPageList(String page, String size, String lastItemTime) throws Exception {
        String json = mRequest.getBeanCloudShelfPageList(page, size, lastItemTime);
        return new BeanCloudShelfPageListInfo().parseJSON(new JSONObject(json));
    }

    /**
     * 云书架：删除书籍
     *
     * @param bookIds ：删除书籍id
     * @param size    ：数量
     * @return BeanCloudShelfPageListInfo
     * @throws Exception 异常
     */
    public BeanCloudShelfPageListInfo deleteCloudShelfData(String bookIds, String size) throws Exception {
        String json = mRequest.deleteCloudShelfData(bookIds, size);
        return new BeanCloudShelfPageListInfo().parseJSON(new JSONObject(json));
    }

    /**
     * 获取Tts插件
     *
     * @return Plugins
     * @throws Exception 异常
     */
    public Plugins getPluginInfo() throws Exception {
        String json = mRequest.getPluginInfo();
        return new Plugins().parseJSON(new JSONObject(json));
    }


    /**
     * 获取用户信息
     *
     * @return UserInfoBeanInfo
     * @throws Exception 异常
     */
    public UserInfoBeanInfo getUserInfo() throws Exception {
        String json = mRequest.getUserInfoRequest();
        return new UserInfoBeanInfo().parseJSON(new JSONObject(json));
    }

    /**
     * 获取兑换礼品结果
     *
     * @param code code
     * @return BeanGetBookInfo
     * @throws Exception 异常
     */
    public BeanGetBookInfo getGiftExchange(String code) throws Exception {
        String json = mRequest.getGiftExchangeRequest(code);
        return new BeanGetBookInfo().parseJSON(new JSONObject(json));
    }


    /**
     * 领取缺失内容奖励接口
     *
     * @param bookId    书籍id
     * @param chapterId 章节id，
     * @return MissContentBeanInfo
     * @throws Exception 异常
     */
    public MissContentBeanInfo receiveMissContentAwardBeanInfo(String bookId, String chapterId) throws Exception {
        String json = mRequest.receiveMissContentAwardRequest(bookId, chapterId);
        return new MissContentBeanInfo().parseJSON(new JSONObject(json));
    }


    /**
     * 获取充值记录
     *
     * @param index    默认index为1，第几页
     * @param totalNum 每页返回条数
     * @return RechargeRecordBeanInfo
     * @throws Exception 异常
     */
    public RechargeRecordBeanInfo getRechargeRecordInfo(String index, String totalNum) throws Exception {
        String json = mRequest.getRechargeRecordRequest(index, totalNum);
        return new RechargeRecordBeanInfo().parseJSON(new JSONObject(json));
    }

    /**
     * 获取代金券列表
     *
     * @param index 默认index为1，第几页
     * @return VouchersListBeanInfo
     * @throws Exception 异常
     */
    public VouchersListBeanInfo getVouchersListInfo(String index) throws Exception {
        String json = mRequest.getVouchersListRequest(index);
        return new VouchersListBeanInfo().parseJSON(new JSONObject(json));
    }

    /**
     * 获取礼品列表
     *
     * @param index index
     * @return GiftListBeanInfo
     * @throws Exception 异常
     */
    public GiftListBeanInfo getGiftListInfo(String index) throws Exception {
        String json = mRequest.getGiftListRequest(index);
        return new GiftListBeanInfo().parseJSON(new JSONObject(json));
    }

    /**
     * 获取书籍消费记录汇总
     *
     * @param index    默认index为1，第几页
     * @param totalNum 每页返回条数
     * @return ConsumeBookSumBeanInfo
     * @throws Exception 异常
     */
    public ConsumeBookSumBeanInfo getBookConsumeSummaryInfo(String index, String totalNum) throws Exception {
        String json = mRequest.getBookConsumeSummaryRequest(index, totalNum);
        return new ConsumeBookSumBeanInfo().parseJSON(new JSONObject(json));
    }

    /**
     * 获取消费记录二级（活动，书籍，vip）
     *
     * @param index    默认index为1，第几页
     * @param totalNum 每页返回条数
     * @param type     type
     * @param nextId   nextId
     * @return ConsumeSecondBeanInfo
     * @throws Exception 异常
     */
    public ConsumeSecondBeanInfo getConsumeSecondInfo(String type, String nextId, String index, String totalNum) throws Exception {
        String json = mRequest.getConsumeSecondRequest(type, nextId, index, totalNum);
        return new ConsumeSecondBeanInfo().parseJSON(new JSONObject(json));
    }

    /**
     * 获取消费记录二级（活动，书籍，vip）
     *
     * @param index     默认index为1，第几页
     * @param totalNum  每页返回条数
     * @param bookId    bookId
     * @param consumeId consumeId
     * @return ConsumeThirdBeanInfo
     * @throws Exception 异常
     */
    public ConsumeThirdBeanInfo getConsumeThirdInfo(String consumeId, String bookId, String index, String totalNum) throws Exception {
        String json = mRequest.getConsumeThirdRequest(consumeId, bookId, index, totalNum);
        return new ConsumeThirdBeanInfo().parseJSON(new JSONObject(json));
    }

    /**
     * VIP自动续费状态获取
     *
     * @return VipAutoRenewStatus
     * @throws Exception 异常
     */
    public VipAutoRenewStatus getVipAutoRenewStatusInfo() throws Exception {
        String json = mRequest.getVipAutoRenewStatusRequest();
        return new VipAutoRenewStatus().parseJSON(new JSONObject(json));
    }

    /**
     * 取消VIP自动续费
     *
     * @return VipCancelAutoRenewBeanInfo
     * @throws Exception 异常
     */
    public VipCancelAutoRenewBeanInfo cancelVipAutoRenewInfo() throws Exception {
        String json = mRequest.cancelVipAutoRenewRequest();
        return new VipCancelAutoRenewBeanInfo().parseJSON(new JSONObject(json));
    }

    /**
     * 获取VIP连续开通历史
     *
     * @param index index
     * @return VipContinueOpenHisBeanInfo
     * @throws Exception 异常
     */
    public VipContinueOpenHisBeanInfo getVipContinueOpenHisInfo(String index) throws Exception {
        String json = mRequest.getVipContinueOpenHisRequest(index);
        return new VipContinueOpenHisBeanInfo().parseJSON(new JSONObject(json));
    }

    /**
     * 获取活动列表
     *
     * @return ActivityCenterBean
     * @throws Exception 异常
     */
    public ActivityCenterBean getActivityCenterBean() throws Exception {
        String json = mRequest.getActicityCenterListRequest();
        return new ActivityCenterBean().parseJSON(new JSONObject(json));
    }

    /**
     * 给H5提供签名方法，已经传递客户端公共数据
     *
     * @param data data
     * @param type type
     * @return String
     */
    public String getH5AddSignHeaderData(String data, String type) {
        String json = mRequest.getH5AddSignHeaderData(data, type);
        ALog.dZz("getH5AddSignHeaderData:" + json);
        return json;
    }

    /**
     * 排行榜
     *
     * @param parentId 父级id
     * @param subId    subId
     * @param page     页码
     * @param pageSize 每页多少
     * @return BeanRankTopResBeanInfo
     * @throws Exception 异常
     */
    public BeanRankTopResBeanInfo getBookStoreRankTopData(String parentId, String subId, int page, int pageSize) throws Exception {
        String json = mRequest.getBookStoreRankTopData(parentId, subId, page, pageSize);
        return new BeanRankTopResBeanInfo().parseJSON(new JSONObject(json));
    }

    /**
     * 上传push
     *
     * @param cid 每页多少
     * @return BeanCidUpload
     * @throws Exception 异常
     */
    public BeanCidUpload upLoadCid(String cid) throws Exception {
        String json = mRequest.upLoadCid(cid);
        return new BeanCidUpload().parseJSON(new JSONObject(json));
    }

    /**
     * 获取充值列表信息
     *
     * @return RechargeListBeanInfo
     * @throws Exception 异常
     */
    public RechargeListBeanInfo getRechargeListInfo() throws Exception {
        String json = mRequest.getRechargeListRequest();
        return new RechargeListBeanInfo().parseJSON(new JSONObject(json));
    }


    /**
     * 获取vip页数据
     *
     * @return VipBeanInfo
     * @throws Exception 异常
     */
    public VipBeanInfo getVipInfoBean() throws Exception {
        String json = mRequest.getVipListRequest();
        return new VipBeanInfo().parseJSON(new JSONObject(json));
    }

    /**
     * 获取vip福利
     *
     * @return VipWellInfo
     * @throws Exception 异常
     */
    public VipWellInfo getVipWellInfo() throws Exception {
        String json = mRequest.getVipWellRequest();
        return new VipWellInfo().parseJSON(new JSONObject(json));
    }

    /**
     * 书签笔记同步
     *
     * @param userId   userId
     * @param time     time
     * @param markList markList
     * @return BookMarkSyncInfo
     * @throws Exception 异常
     */
    public BookMarkSyncInfo syncMark(String userId, String time, String markList) throws Exception {
        String json = mRequest.syncMarkRequest(userId, time, markList);
        return new BookMarkSyncInfo().parseJSON(new JSONObject(json));
    }
}
