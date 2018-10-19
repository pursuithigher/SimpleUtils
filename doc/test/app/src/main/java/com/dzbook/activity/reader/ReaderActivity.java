package com.dzbook.activity.reader;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.RectF;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.dzbook.activity.person.PersonPluginActivity;
import com.dzbook.database.bean.CatalogInfo;
import com.dzbook.database.bean.PluginInfo;
import com.dzbook.event.EventBusUtils;
import com.dzbook.event.EventConstant;
import com.dzbook.event.EventMessage;
import com.dzbook.lib.utils.ALog;
import com.dzbook.loader.BookLoader;
import com.dzbook.mvp.UI.ReaderUI;
import com.dzbook.mvp.presenter.ReaderPresenter;
import com.dzbook.r.c.AkDocInfo;
import com.dzbook.r.c.AkReaderView;
import com.dzbook.r.c.ReaderListener;
import com.dzbook.r.c.SettingManager;
import com.dzbook.r.model.DzChar;
import com.dzbook.r.model.VoiceLine;
import com.dzbook.r.voice.ReaderVoiceHelper;
import com.dzbook.utils.ShareUtils;
import com.dzbook.utils.SpUtil;
import com.dzbook.utils.ThirdPartyLog;
import com.dzbook.view.common.dialog.CustomHintDialog;
import com.dzbook.view.common.dialog.base.CustomDialogBusiness;
import com.dzbook.view.photoview.drag.DragPhotoDecorView;
import com.dzbook.view.reader.ReaderNewPanel;
import com.ishugui.R;

import java.util.ArrayList;

import hw.sdk.net.bean.cloudshelf.BeanSingleBookReadProgressInfo;
import hw.sdk.net.bean.cloudshelf.BeanSingleBookReadProgressTipInfo;

/**
 * 阅读器
 *
 * @author zhenglk
 */
public class ReaderActivity extends BaseReaderActivity implements ReaderUI {

    /**
     * tag
     */
    public static final String TAG = "ReaderActivity";

    private FrameLayout layoutRoot;

    private AkReaderView readerView;

    private LinearLayout layoutBookView;

    private ReaderPresenter mPresenter;

    private ReaderNewPanel readerNewPanel;

    private PhoneStateReceiver phoneStateReceiver = new PhoneStateReceiver();

    private DragPhotoDecorView dragView;

    private CustomHintDialog cloudShelfJumpDialog, pluginDialog;

    /**
     * load
     *
     * @param context  context
     * @param docInfo  docInfo
     * @param openFrom openFrom
     */
    public static void launch(Context context, AkDocInfo docInfo, String openFrom) {
        Intent intent = new Intent(context, ReaderActivity.class);
        intent.putExtra("docInfo", docInfo);
        if (!TextUtils.isEmpty(openFrom)) {
            intent.putExtra("openFrom", openFrom);
        }
        context.startActivity(intent);
    }

    /**
     * load for result
     *
     * @param activity    activity
     * @param docInfo     docInfo
     * @param openFrom    openFrom
     * @param requestCode requestCode
     */
    public static void launchForResult(Activity activity, AkDocInfo docInfo, String openFrom, int requestCode) {
        Intent intent = new Intent(activity, ReaderActivity.class);
        intent.putExtra("docInfo", docInfo);
        if (!TextUtils.isEmpty(openFrom)) {
            intent.putExtra("openFrom", openFrom);
        }
        activity.startActivityForResult(intent, requestCode);
    }


    @Override
    public String getTagName() {
        return TAG;
    }

    /**
     * 手机状态监听
     */
    private class PhoneStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                if (mPresenter != null && mPresenter.isVoiceMode()) {
                    if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
                        ReaderVoiceHelper.getInstance().pause();
                    }

                    if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state)) {
                        ReaderVoiceHelper.getInstance().pause();
                    }

                    if (TelephonyManager.EXTRA_STATE_IDLE.equals(state)) {
                        ReaderVoiceHelper.getInstance().resume();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            AkDocInfo saveDoc = savedInstanceState.getParcelable("saveDoc");
            if (saveDoc != null && getIntent() != null) {
                getIntent().putExtra("docInfo", saveDoc);
            }
        }

        applyFullscreen(0);

        setContentView(R.layout.ac_reader);


        SpUtil spUtil = SpUtil.getinstance(this);
        spUtil.setBoolean(SpUtil.FIRST_DIRECTOPEN, false);

        IntentFilter intentFilter = new IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        registerReceiver(phoneStateReceiver, intentFilter);
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        dissMissDialog();
        boolean result = mPresenter.processIntent(intent);
        if (!result) {
            finish();
        }
        mPresenter.setBookInfoReadFrom();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mPresenter == null) {
            return;
        }
        AkDocInfo akDocInfo = mPresenter.refreshDocument();
        if (akDocInfo != null) {
            outState.putParcelable("saveDoc", akDocInfo);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPresenter != null) {
            mPresenter.stopRecordVoiceReadTime();
            if (mPresenter.isAutoRead()) {
                finishAutoRead();
//                ToastAlone.showShort(R.string.toast_quit_auto_read);
            }

            // 是否显示系统通知栏控制
            // 修改by lizhongzhong 锁屏状态下菜单显示出来状态栏也要显示出来
            if (!mPresenter.isMenuShow) {
                applyFullscreen(0);
            } else {
                showMenuPanel();
            }

            mPresenter.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPresenter.isAutoRead()) {
            finishAutoRead();
//            ToastAlone.showShort(R.string.toast_quit_auto_read);
        }
        mPresenter.onPause();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mPresenter != null) {
            mPresenter.startRecordVoiceReadTime();
        }
    }

    @Override
    protected void onDestroy() {
        //取消所有离线缓存任务
        BookLoader.getInstance().clearLoadQueue();
        EventBusUtils.sendMessage(EventConstant.CLOSEBOOK_REQUEST_CODE, EventConstant.TYPE_MAINSHELFFRAGMENT, null);

        super.onDestroy();

        if (mPresenter != null) {
            mPresenter.destroy();
        }

        if (cloudShelfJumpDialog != null && cloudShelfJumpDialog.isShow()) {
            cloudShelfJumpDialog.dismiss();
            cloudShelfJumpDialog = null;
        }

        if (pluginDialog != null && pluginDialog.isShow()) {
            pluginDialog.dismiss();
            pluginDialog = null;
        }

        unregisterReceiver(phoneStateReceiver);
    }

    @Override
    protected void initView() {
        layoutRoot = findViewById(R.id.layout_root);
        layoutBookView = findViewById(R.id.layout_bookview);

        readerView = new AkReaderView(this, null);
        readerView.setId(R.id.reader_view);
        readerView.setPageCurlCacheEnable(false);
        layoutBookView.addView(readerView);

        readerNewPanel = findViewById(R.id.readerNewPanel);
    }


    @Override
    protected void initData() {
        mPresenter = new ReaderPresenter(this);
        mPresenter.initConfig();

        Intent intent = getIntent();
        boolean result = mPresenter.processIntent(intent);
        if (!result) {
            finish();
            return;
        }
        mPresenter.processChapter();
        showUserGuideIfNeed(layoutRoot);
    }

    @Override
    protected void setListener() {
        readerView.setReaderListener(new MyReaderListener());
    }


    @Override
    protected boolean isCustomPv() {
        return true;
    }

//    public static void finishThisActivity() {
//        EventBusUtils.sendMessage(EventConstant.CODE_FINISH_READER);
//    }

    @Override
    public void showCloudProgressDialog(final BeanSingleBookReadProgressInfo beanInfo) {
        if (cloudShelfJumpDialog == null) {
            cloudShelfJumpDialog = new CustomHintDialog(this);
        }
        StringBuilder sb = new StringBuilder();
        for (BeanSingleBookReadProgressTipInfo bean : beanInfo.tips) {
            if (bean != null && !TextUtils.isEmpty(bean.tip)) {
                sb.append(bean.tip);
                sb.append(" ");
            }
        }
        cloudShelfJumpDialog.setDesc(sb.toString());
        cloudShelfJumpDialog.setCheckListener(new CustomDialogBusiness.OnCheckListener() {
            @Override
            public void clickConfirm(Object object) {
                ThirdPartyLog.onEvent(ReaderActivity.this, ThirdPartyLog.READER_PROGRESS_CONFIRM_SU);
                mPresenter.showCloudChapter(beanInfo, true);
            }

            @Override
            public void clickCancel() {
                ThirdPartyLog.onEvent(ReaderActivity.this, ThirdPartyLog.READER_PROGRESS_CANCEL_SU);
            }
        });

        cloudShelfJumpDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                hideMenuPanel(false);
                applyFullscreen(0);
            }
        });
        cloudShelfJumpDialog.show();
        ThirdPartyLog.onEvent(ReaderActivity.this, ThirdPartyLog.READER_PROGRESS_SHOW_SU);
    }


    @Override
    public void loadDocument(AkDocInfo doc) {
        readerView.loadDocument(doc);
    }


    @Override
    public AkDocInfo getDocument() {
        try {
            return readerView.getDocument();
        } catch (Exception e) {
            ALog.printStackTrace(e);
        }
        return null;
    }

    @Override
    public void startAutoRead(int animIndex, int speed, boolean hideMenuPanel) {
        if (hideMenuPanel) {
            hideMenuPanel(false);
            //设置自动阅读
            readerView.setAnimStyle(animIndex);
            readerView.setSpeed(speed);
            readerView.update(true);
            readerView.invalidate();
        } else {
            readerView.resume();
            readerView.setSpeed(speed);
            readerView.update(true);
            readerView.invalidate();
        }
        setMenuState(ReaderNewPanel.STATE_AUTO_READ);
        mPresenter.startAutoRead();

        //开启自动阅读，保持屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void finishAutoRead() {
        //UI处理
        hideMenuPanel(true);
        setMenuState(ReaderNewPanel.STATE_MAIN);
        mPresenter.finishAutoRead();

        readerView.stop();

        int index = SettingManager.getInstance(this).getAnimStyleIndex();
        applyAnim(index);

        //结束自动阅读，取消屏幕常亮
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


    @Override
    public void showMenuPanel() {
        mPresenter.isMenuShow = true;
        readerNewPanel.showMainMenu();
        readerView.pause();
    }

    @Override
    public void hideMenuPanel(boolean resetState) {
        mPresenter.isMenuShow = false;
        readerNewPanel.hideMainMenu(resetState);
        readerView.resume();
    }

    @Override
    public int getMenuState() {
        return readerNewPanel.getState();
    }

    @Override
    public void turnChapter(CatalogInfo chapter, boolean isNext, String partFrom) {
        mPresenter.checkAndLoadChapter(chapter, isNext, partFrom);
    }

    @Override
    public AkReaderView getReader() {
        return readerView;
    }

    @Override
    public void showPluginDialog() {

        if (pluginDialog == null) {
            pluginDialog = new CustomHintDialog(this);
        }

        pluginDialog.setDesc(getString(R.string.dialog_voice_install_tips));
        pluginDialog.setCheckListener(new CustomDialogBusiness.OnCheckListener() {
            @Override
            public void clickConfirm(Object object) {
                PersonPluginActivity.launch(ReaderActivity.this, PluginInfo.TTS_NAME);
            }

            @Override
            public void clickCancel() {

            }
        });
        pluginDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                hideMenuPanel(false);
                applyFullscreen(0);
            }
        });
        pluginDialog.show();
    }

    @Override
    public void setMenuState(int state) {
        readerNewPanel.setState(state);
    }

    @Override
    public ReaderPresenter getPresenter() {
        return mPresenter;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP && readerView != null && !readerView.isInVoiceMode()) {
            return keyTurnPrePage();
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN && readerView != null && !readerView.isInVoiceMode()) {
            return keyTurnNextPage();
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            return keyMenuToggle();
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (dragView != null) {
                exitDragPhoto();
                return false;
            }
            return keyBackKey();
        }
        return false;
    }

    private boolean keyTurnPrePage() {
        if (readerView.isInSelectMode()) {
            readerView.clearSelect();
            return true;
        }
        if (SpUtil.getinstance(this).getVolumeKeyCode() == 1 && !mPresenter.isMenuShow) {
            readerView.turnPrePage();
            return true;
        }
        return false;
    }

    private boolean keyTurnNextPage() {
        if (readerView.isInSelectMode()) {
            readerView.clearSelect();
            return true;
        }
        if (SpUtil.getinstance(this).getVolumeKeyCode() == 1 && !mPresenter.isMenuShow) {
            readerView.turnNextPage();
            return true;
        }
        return false;
    }

    private boolean keyMenuToggle() {
        if (readerView != null && readerView.isInSelectMode()) {
            readerView.clearSelect();
            return true;
        }
        if (mPresenter.isMenuShow) {
            hideMenuPanel(false);
        } else {
            showMenuPanel();
        }
        return true;
    }

    private boolean keyBackKey() {
        if (readerView != null && readerView.isInSelectMode()) {
            readerView.clearSelect();
            return true;
        }
        mPresenter.onBackPress(false);
        return true;
    }

    @Override
    public ReaderActivity getHostActivity() {
        return this;
    }

    @Override
    public void onEventMainThread(EventMessage event) {
        super.onEventMainThread(event);
        if (event.getRequestCode() == EventConstant.CODE_FINISH_READER) {
            finish();
        }
    }

    private void showDragPhoto(int rootWidth, int rootHeight, int left, int top, int right, int bottom, String url) {
        if (dragView == null) {
            dragView = new DragPhotoDecorView(this);
            dragView.start(rootWidth, rootHeight, left, top, right, bottom, url);
        }
        ((ViewGroup) this.getWindow().getDecorView()).removeView(dragView);
        ((ViewGroup) this.getWindow().getDecorView()).addView(dragView);
        dragView.setExitListener(new DragPhotoDecorView.ExitListener() {
            @Override
            public void exitEvent() {
                exitDragPhoto();
            }
        });
    }

    private void exitDragPhoto() {
        if (dragView != null) {
            ((ViewGroup) ReaderActivity.this.getWindow().getDecorView()).removeView(dragView);
            dragView.destroyDragView();
            dragView = null;
        }
    }

    /**
     * 阅读章节监听
     */
    private class MyReaderListener implements ReaderListener {
        @Override
        public void onChapterStart(AkDocInfo doc, boolean isBookStart, boolean autoSerial) {
            ALog.dZz("ReaderActivity:onChapterStart");
            mPresenter.onChapterStart(doc, isBookStart, autoSerial);
        }

        @Override
        public void onChapterEnd(AkDocInfo doc, boolean isBookEnd, boolean autoSerial) {
            ALog.dZz("ReaderActivity:onChapterEnd");
            mPresenter.onChapterEnd(doc, isBookEnd, autoSerial);
        }

        @Override
        public void onTurnPrePage(AkDocInfo doc) {
            ALog.dZz("ReaderActivity:onTurnPrePage");
            mPresenter.refreshDocument();
        }

        @Override
        public void onTurnNextPage(AkDocInfo doc) {
            ALog.dZz("ReaderActivity:onTurnNextPage");
            mPresenter.refreshDocument();
            int state = ReaderVoiceHelper.getInstance().getSdkState();
            if (state == ReaderVoiceHelper.STATE_READY) {
                VoiceLine lastLine = ReaderVoiceHelper.getInstance().getLastLine();
                ArrayList<DzChar> list = readerView.getPageTextForVoice(lastLine.getLastChar(), false);
                ReaderVoiceHelper.getInstance().addChar(list);
            }
        }

        @Override
        public AkDocInfo getNextDocInfo() {
            ALog.dZz("ReaderActivity:getNextDocInfo");
            return mPresenter.getNextDocInfo();
        }

        @Override
        public AkDocInfo getPreDocInfo() {
            ALog.dZz("ReaderActivity:getPreDocInfo");
            return mPresenter.getPreDocInfo();
        }

        @Override
        public void onOpenBook() {
            ALog.dZz("ReaderActivity:onOpenBook");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isFinishing()) {
                        return;
                    }
                    readerNewPanel.refreshData();
                    mPresenter.onOpenBook();
                }
            });

        }

        @Override
        public void onError() {
//                showMessage("阅读器错误：" + response.code);
        }

        @Override
        public void onMenuAreaClick() {
            showMenuPanel();
        }

        @Override
        public void onImageAreaClick(String imagePath, RectF rectF) {
            showDragPhoto(layoutRoot.getMeasuredWidth(), layoutRoot.getMeasuredHeight(), (int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom, imagePath);
        }

        @Override
        public boolean onPopClick(AkDocInfo docInfo, String showText, String noteText, long startPos, long endPos, int action) {
            return mPresenter.handleReaderPopClick(docInfo, showText, noteText, startPos, endPos, action);
        }

        @Override
        public boolean getShareSupport() {
            return ShareUtils.isSupportShare();
        }
    }


    /**
     * 底部导航栏 沉浸色值
     *
     * @return color
     */
    @Override
    public int getNavigationBarColor() {
        return R.color.transparent;
    }


    /**
     * 二级页面都是灰色 一级页面都是白色
     * 状态栏 沉浸色值
     *
     * @return color
     */
    @Override
    public int getStatusColor() {
        return R.color.transparent;
    }

}
