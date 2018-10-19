package com.dzbook.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.dzbook.adapter.ShareRecyclerViewAdapter;
import com.dzbook.bean.ShareBean;
import com.dzbook.bean.ShareBeanInfo;
import com.dzbook.database.bean.BookInfo;
import com.dzbook.event.EventConstant;
import com.dzbook.event.EventMessage;
import com.dzbook.lib.net.DzSchedulers;
import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.lib.utils.SDCardUtil;
import com.dzbook.mvp.DialogConstants;
import com.dzbook.utils.DBUtils;
import com.dzbook.utils.FileUtils;
import com.dzbook.utils.GlideImageLoadUtils;
import com.dzbook.utils.ImageUtils;
import com.dzbook.utils.ShareUtils;
import com.dzbook.view.DianZhongCommonTitle;
import com.dzbook.view.ReaderShareView;
import com.ishugui.R;
import com.iss.app.BaseActivity;
import com.sina.weibo.sdk.share.WbShareHandler;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.Tencent;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * 分享
 *
 * @author wangjianchen
 */
public class ShareActivity extends BaseActivity {

    private static final String SHARE_BEANINFO = "ShareBeanInfo";
    private static final String FROM = "from";
    private static final String IS_SHOW_CENTER = "isShowCenter";
    private static final String BOOK_ID = "book_id";
    private static final String SHARE_CONTENT = "share_content";
    private ArrayList<ShareBean> datas;
    private RecyclerView recyclerView;
    private ShareBeanInfo beanInfo;
    private ShareBeanInfo.ShareBean shareBean = null;
    private int flag = -1;
    private ReaderShareView mShareView;
    private boolean isShowCenter;
    private String bookId;
    private ShareRecyclerViewAdapter adapter;
    private String shareContent;
    private View fraMain;
    private DianZhongCommonTitle commontitle;
    private View viewBack;
    private WbShareHandler wbShareHandler;

    private long[] mHits = new long[2];

    /**
     * 获取 WbShareHandler
     *
     * @return WbShareHandler
     */
    public WbShareHandler getWbShareHandler() {
        if (null == wbShareHandler) {
            wbShareHandler = new WbShareHandler(this);
        }
        return wbShareHandler;
    }

    /**
     * 打开分享
     *
     * @param activity     activity
     * @param shareContent shareContent
     * @param bookId       bookId
     * @param isShowCenter isShowCenter
     */
    public static void launch(Activity activity, String shareContent, String bookId, boolean isShowCenter) {
        Intent intent = new Intent();
        intent.setClass(activity, ShareActivity.class);
        intent.putExtra(SHARE_CONTENT, shareContent);
        intent.putExtra(BOOK_ID, bookId);
        intent.putExtra(IS_SHOW_CENTER, isShowCenter);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.anim_activityin, R.anim.anim_activity_out);
    }

    /**
     * 打开分享
     *
     * @param activity      activity
     * @param shareBeanInfo shareBeanInfo
     * @param from          from
     * @param isShowCenter  isShowCenter
     */
    public static void lanuch(Activity activity, ShareBeanInfo shareBeanInfo, int from, boolean isShowCenter) {
        Intent intent = new Intent(activity, ShareActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(SHARE_BEANINFO, shareBeanInfo);
        intent.putExtras(bundle);
        intent.putExtra(FROM, from);
        intent.putExtra(IS_SHOW_CENTER, isShowCenter);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.anim_activityin, R.anim.anim_activity_out);
    }


    @Override
    public String getTagName() {
        return "ShareActivity";
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (null != wbShareHandler) {
            wbShareHandler.doResultIntent(intent, ShareUtils.getWbShareCallback());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_QZONE_SHARE || requestCode == Constants.REQUEST_QQ_SHARE) {
            Tencent.onActivityResultData(requestCode, resultCode, data, ShareUtils.getQqShareListener());
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_horinazal);
    }

    @Override
    protected void initView() {
        Intent intent = getIntent();
        if (null == intent) {
            finish();
            return;
        }
        mShareView = findViewById(R.id.shareview);
        fraMain = findViewById(R.id.fra_main);
        commontitle = findViewById(R.id.commontitle);
        viewBack = findViewById(R.id.view_back);
        recyclerView = findViewById(R.id.arecycler_view);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ShareRecyclerViewAdapter(this);
        recyclerView.setAdapter(adapter);
        isShowCenter = intent.getBooleanExtra(IS_SHOW_CENTER, false);
        mShareView.setVisibility(isShowCenter ? View.VISIBLE : View.GONE);
        if (!isShowCenter) {
            commontitle.setVisibility(View.GONE);
            viewBack.setVisibility(View.VISIBLE);
            fraMain.setBackgroundColor(CompatUtils.getColor(this, R.color.color_30_000000));
            Serializable serializableExtra = intent.getSerializableExtra(SHARE_BEANINFO);
            if (null != serializableExtra && serializableExtra instanceof ShareBeanInfo) {
                this.beanInfo = (ShareBeanInfo) serializableExtra;
            }
//            this.from = intent.getIntExtra(FROM, -1);

        } else {
            commontitle.setVisibility(View.VISIBLE);
            viewBack.setVisibility(View.GONE);
            fraMain.setBackgroundColor(CompatUtils.getColor(this, R.color.color_FFF3F3F3));
            bookId = intent.getStringExtra(BOOK_ID);
            shareContent = intent.getStringExtra(SHARE_CONTENT);
            if (!TextUtils.isEmpty(bookId)) {
                BookInfo bookInfo = DBUtils.findByBookId(getContext(), bookId);
                if (bookInfo != null) {
                    mShareView.bindData(shareContent, bookInfo);
                    mShareView.initShareData();
                } else {
                    finish();
                }
            } else {
                finish();
            }

        }

    }

    @Override
    protected void initData() {
        datas = new ArrayList<>();
        if (isShowCenter) {
            datas.add(new ShareBean("微信", CompatUtils.getDrawable(getContext(), R.drawable.hw_chat), 3));
            datas.add(new ShareBean("朋友圈", CompatUtils.getDrawable(getContext(), R.drawable.hw_qq_zone), 4));
            datas.add(new ShareBean("QQ", CompatUtils.getDrawable(getContext(), R.drawable.hw_qq), 1));
            datas.add(new ShareBean("QQ空间", CompatUtils.getDrawable(getContext(), R.drawable.hw_qq_star), 2));
            datas.add(new ShareBean("新浪微博", CompatUtils.getDrawable(getContext(), R.drawable.hw_wb), 5));
        } else {
            //类型：1.qq好友2.qq空间3.微信好友4.微信朋友圈5.微博
            if (beanInfo != null) {
                if (ShareBeanInfo.isShow(beanInfo.wxHyBean)) {
                    datas.add(new ShareBean("微信", CompatUtils.getDrawable(getContext(), R.drawable.hw_chat), 3));
                }
                if (ShareBeanInfo.isShow(beanInfo.wxPyqBean)) {
                    datas.add(new ShareBean("朋友圈", CompatUtils.getDrawable(getContext(), R.drawable.hw_qq_zone), 4));
                }
                if (ShareBeanInfo.isShow(beanInfo.qqHyBean)) {
                    datas.add(new ShareBean("QQ", CompatUtils.getDrawable(getContext(), R.drawable.hw_qq), 1));
                }
                if (ShareBeanInfo.isShow(beanInfo.qqKJBean)) {
                    datas.add(new ShareBean("QQ空间", CompatUtils.getDrawable(getContext(), R.drawable.hw_qq_star), 2));
                }
                if (ShareBeanInfo.isShow(beanInfo.weiBoBean)) {
                    datas.add(new ShareBean("新浪微博", CompatUtils.getDrawable(getContext(), R.drawable.hw_wb), 5));
                }
            }
        }
        if (datas != null && datas.size() > 0) {
            adapter.setData(datas);
        } else {
            shareFail(-1, true);
        }
    }


    @Override
    protected void setListener() {
        commontitle.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        viewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.anim_activityin, R.anim.anim_activity_out);
            }
        });
        adapter.setOnItemClickListener(new ShareRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onitemclick(View view, ShareBean bean) {
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                if (mHits[1] >= (mHits[0] + 500)) {
                    if (null != bean) {
                        switch (bean.type) {
                            case ShareUtils.QQ_HY:
                                shareQqGoodFriend();
                                break;
                            case ShareUtils.QQ_KJ:
                                shareQq();
                                break;
                            case ShareUtils.WX_HY:
                                shareWeixin(bean.type);
                                break;
                            case ShareUtils.WX_PYQ:
                                shareWeixin(bean.type);
                                break;
                            case ShareUtils.WEI_BO:
                                shareWeiBo();
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        });
    }

    private void shareWeiBo() {
        if (isShowCenter) {
            showDialogByType(DialogConstants.TYPE_GET_DATA);
            mShareView.setShareViewListener(new ReaderShareView.ShareViewListener() {
                @Override
                public void shareBytes(final byte[] bytes) {
                    ShareActivity.this.dissMissDialog();
                    if (null == bytes) {
                        ShareActivity.this.shareFail(ShareUtils.WEI_BO, true);
                    } else {
                        ShareUtils.directWeiBoShareByImg(ShareActivity.this, bytes);
                    }
                }
            });
        } else if (beanInfo != null && beanInfo.weiBoBean != null) {
            ShareUtils.directWeiBoShareByType(this, beanInfo.weiBoBean.url, beanInfo.weiBoBean.title,
                    beanInfo.weiBoBean.des, beanInfo.weiBoBean.img, ShareUtils.SHARE_TO_FRIENDS);
        }

    }

    private void shareQqGoodFriend() {
        if (isShowCenter) {
            shareQQImg(0);
        } else if (beanInfo != null && beanInfo.qqHyBean != null) {
            ShareUtils.directQQShareByType(this, beanInfo.qqHyBean.url, beanInfo.qqHyBean.title,
                    beanInfo.qqHyBean.des, beanInfo.qqHyBean.img, ShareUtils.SHARE_TO_FRIENDS);
        }
    }

    private void shareQQImg(final int qqFlag) {
        DzSchedulers.child(new Runnable() {
            @Override
            public void run() {
                String cacheDirPath = SDCardUtil.getInstance().getSDCardAndroidRootDir() + File.separator + FileUtils.APP_BOOK_IMAGE_CACHE_PATH;
                final String shareUrl = cacheDirPath + "/shareqq.jpg";
                Bitmap bitmap = mShareView.getShareBitmap();
                File file = new File(cacheDirPath);
                if (!file.exists()) {
                    boolean mkdirs = file.mkdirs();
                }
                ImageUtils.saveBitmap(bitmap, shareUrl);
                DzSchedulers.main(new Runnable() {
                    @Override
                    public void run() {
                        if (!TextUtils.isEmpty(shareUrl)) {
                            ShareUtils.directQQShareByImage(ShareActivity.this, shareUrl, shareContent, qqFlag);
                        }

                    }
                });
            }
        });
    }

    private void shareQq() {
        if (isShowCenter) {
            shareQQImg(1);
        } else if (beanInfo != null && beanInfo.qqKJBean != null) {
            ShareUtils.directQQShareByType(this, beanInfo.qqKJBean.url, beanInfo.qqKJBean.title, beanInfo.qqKJBean.des,
                    beanInfo.qqKJBean.img, ShareUtils.SHARE_TO_FRIEND_CIRCLE);
        }
    }

    private void shareWeixin(final int id) {
        if (isShowCenter) {
            showDialogByType(DialogConstants.TYPE_GET_DATA);
            mShareView.setShareViewListener(new ReaderShareView.ShareViewListener() {
                @Override
                public void shareBytes(final byte[] bytes) {
                    ShareActivity.this.dissMissDialog();
                    if (null == bytes) {
                        ShareActivity.this.shareFail(-1, true);
                    } else {
                        ShareUtils.doWechatShareForImg(ShareActivity.this, (ShareUtils.WX_PYQ == id) ? 1 : 0, bytes);
                    }
                }
            });
        } else {
            if (beanInfo == null || beanInfo.wxPyqBean == null || beanInfo.wxHyBean == null) {
                shareFail(-1, true);
                return;
            }
            if (ShareUtils.WX_PYQ == id) {
                flag = 1;
                shareBean = beanInfo.wxPyqBean;

            } else {
                flag = 0;
                shareBean = beanInfo.wxHyBean;
            }
            downloadImgAndShare();
        }
    }

    private void downloadImgAndShare() {
        GlideImageLoadUtils.getInstanse().downloadImageBitmapFromUrl(this, shareBean.img, new GlideImageLoadUtils.DownloadImageListener() {
            @Override
            public void downloadSuccess(Bitmap bitmap) {
                ShareUtils.doWechatShareForWebpage(ShareActivity.this, flag, shareBean.url, shareBean.title, shareBean.des,
                        ImageUtils.compressBitmap(ShareActivity.this, bitmap, 30, false), true, beanInfo.style);
            }

            @Override
            public void downloadSuccess(File resource) {

            }

            @Override
            public void downloadFailed() {
                String imageUrl = "drawable://" + R.drawable.push;
                GlideImageLoadUtils.getInstanse().downloadImageBitmapFromUrl(ShareActivity.this, imageUrl, new GlideImageLoadUtils.DownloadImageListener() {
                    @Override
                    public void downloadSuccess(Bitmap bitmap) {
                        ShareUtils.doWechatShareForWebpage(ShareActivity.this, flag, shareBean.url, shareBean.title, shareBean.des,
                                ImageUtils.compressBitmap(ShareActivity.this, bitmap, 30, false), true, beanInfo.style);
                    }

                    @Override
                    public void downloadSuccess(File resource) {

                    }

                    @Override
                    public void downloadFailed() {

                    }
                }, false);
            }
        }, false);
    }


    @Override
    public void onEventMainThread(EventMessage event) {
        super.onEventMainThread(event);
        int requestCode = event.getRequestCode();
        Bundle bundle = event.getBundle();
        int type = -1;
        if (null != bundle) {
            type = bundle.getInt("type");
        }
        switch (requestCode) {
            case EventConstant.FINISH_SHARE:
                finish();
                break;
            case EventConstant.SHARE_SUCCESS:
                shareSuccess(type, true);
                break;
            case EventConstant.SHARE_CANCEL:
                shareCancel(type, true);
                break;
            case EventConstant.SHARE_FAIL:
                shareFail(type, true);
                break;
            default:
                break;
        }
    }
}