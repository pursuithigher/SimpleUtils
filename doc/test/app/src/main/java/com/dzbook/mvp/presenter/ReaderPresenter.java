package com.dzbook.mvp.presenter;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.MainThread;
import android.text.TextUtils;

import com.dzbook.AppConst;
import com.dzbook.activity.ShareActivity;
import com.dzbook.activity.detail.BookDetailActivity;
import com.dzbook.activity.detail.BookDetailChapterActivity;
import com.dzbook.activity.reader.MissingContentActivity;
import com.dzbook.activity.reader.ReaderActivity;
import com.dzbook.activity.reader.ReaderNoteActivity;
import com.dzbook.activity.reader.ReaderUtils;
import com.dzbook.database.bean.BookInfo;
import com.dzbook.database.bean.BookMarkNew;
import com.dzbook.database.bean.CatalogInfo;
import com.dzbook.database.bean.PluginInfo;
import com.dzbook.event.EventBusUtils;
import com.dzbook.event.EventConstant;
import com.dzbook.event.EventMessage;
import com.dzbook.event.type.BookNoteEvent;
import com.dzbook.fragment.main.MainShelfFragment;
import com.dzbook.lib.net.DzSchedulers;
import com.dzbook.lib.rx.CompositeDisposable;
import com.dzbook.lib.utils.ALog;
import com.dzbook.lib.utils.SDCardUtil;
import com.dzbook.lib.utils.UtilTimeOffset;
import com.dzbook.loader.BookLoader;
import com.dzbook.loader.LoadResult;
import com.dzbook.log.DzLog;
import com.dzbook.log.DzLogMap;
import com.dzbook.log.LogConstants;
import com.dzbook.model.ModelAction;
import com.dzbook.model.UserGrow;
import com.dzbook.mvp.BasePresenter;
import com.dzbook.mvp.DialogConstants;
import com.dzbook.mvp.UI.ReaderUI;
import com.dzbook.net.hw.HwRequestLib;
import com.dzbook.r.c.AkDocInfo;
import com.dzbook.r.c.AkReaderView;
import com.dzbook.r.c.DzThread;
import com.dzbook.r.c.ReaderPopWindow;
import com.dzbook.r.c.SettingManager;
import com.dzbook.r.model.DzChar;
import com.dzbook.r.model.DzLine;
import com.dzbook.r.model.VoiceInfo;
import com.dzbook.r.model.VoiceLine;
import com.dzbook.r.util.HwUtils;
import com.dzbook.r.voice.ReaderVoiceHelper;
import com.dzbook.r.voice.ReaderVoiceListener;
import com.dzbook.service.CheckBookshelfUpdateRunnable;
import com.dzbook.service.MarketDao;
import com.dzbook.service.RechargeParams;
import com.dzbook.service.SyncBookMarkService;
import com.dzbook.utils.ClipboardUtils;
import com.dzbook.utils.DBUtils;
import com.dzbook.utils.FileUtils;
import com.dzbook.utils.HwLog;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.ScreenUtils;
import com.dzbook.utils.SpUtil;
import com.dzbook.utils.ThirdPartyLog;
import com.dzbook.utils.TurnPageUtils;
import com.dzbook.utils.WhiteListWorker;
import com.dzbook.utils.hw.LoginUtils;
import com.dzbook.view.common.dialog.CustomHintDialog;
import com.dzbook.view.common.dialog.base.CustomDialogBusiness;
import com.dzbook.view.reader.ReaderNewPanel;
import com.ishugui.R;
import com.iss.app.BaseActivity;
import com.iss.view.common.ToastAlone;

import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import hw.sdk.net.bean.cloudshelf.BeanSingleBookReadProgressInfo;
import hw.sdk.net.bean.tts.PluginTtsInfo;
import hw.sdk.net.bean.tts.Plugins;
import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableObserver;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * ReaderPresenter
 *
 * @author lizhongzhong 2017/8/17.
 */
public class ReaderPresenter extends BasePresenter implements AudioManager.OnAudioFocusChangeListener {
    /**
     * 从菜单关闭
     */
    public static final int VFT_FROM_MENU = 0x01;
    private static final int VFT_FROM_BACK = 0x02;
    private static final int VFT_FROM_TIMER = 0x03;
    private static final int VFT_FROM_FOCUS = 0x04;
    private static final int VFT_NO_MORE = 0x05;


    private static final int MODE_NORMAL = 0x00;
    private static final int MODE_AUTO = 0x01;
    private static final int MODE_VOICE = 0x02;
    private static final int MESSAGE_STOP_VOICE = 0x01;


    /**
     * 定时器 总的次数
     */
    private static final int COUNT_TIME = Short.MAX_VALUE;
    /**
     * 菜单是否显示
     */
    public boolean isMenuShow;
    /**
     * 定时器的实现
     */
    public Disposable mDisposable;

    final DecimalFormat df = new DecimalFormat("##0.00%");

    private int readerMode;
    private CompositeDisposable composite = new CompositeDisposable();
    private ReaderUI mUI;
    private String prev;

    private AkDocInfo mDoc;
    private long startTtsTime;
    private BookInfo mBookInfo;
    private long lastDetailTime;
    private String lastPvChapterId;
    private boolean isCheckNetProgress = false;
    //加载阅读进度跳转  为了控制等待时间
    private long loadChapterProgressTime = 0;
    private HashMap<String, String> logMap;
    private int voiceReadScheduleTime = 5 * 60 * 1000;

    private CustomHintDialog addBookShelfDialog;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MESSAGE_STOP_VOICE) {
                finishVoice(VFT_FROM_TIMER);
            }
        }
    };

    private ReaderVoiceListener voiceListener = new ReaderVoiceListener() {
        @Override
        public void onSpeechStart(String s) {
            super.onSpeechStart(s);
        }

        @Override
        public void onSpeechProgressChanged(String s, int i) {
            super.onSpeechProgressChanged(s, i);
            VoiceLine voiceLine = ReaderVoiceHelper.getInstance().getByKey(s);
            if (voiceLine != null) {
                mUI.getReader().setCurrentVoiceLine(voiceLine, i);
            }
        }

        @Override
        public void onSpeechFinish(String s) {
            super.onSpeechFinish(s);
            ReaderVoiceHelper.getInstance().removeByKey(s);

            if (ReaderVoiceHelper.getInstance().isMapEmpty()) {
                mUI.showMessage("语音朗读结束");
                finishVoice(VFT_NO_MORE);
            }
        }
    };

    /**
     * 构造
     *
     * @param mUI mUI
     */
    public ReaderPresenter(ReaderUI mUI) {
        this.mUI = mUI;
        EventBusUtils.register(this);
    }


    /**
     * 主线程操作
     *
     * @param event event
     */
    public void onEventMainThread(BookNoteEvent event) {
        if (event == null || event.getBookNote() == null) {
            return;
        }
        BookMarkNew bookNote = event.getBookNote();
        switch (event.getType()) {
            case BookNoteEvent.TYPE_ADD:
                mUI.getReader().addDzLine(new DzLine(bookNote.startPos, bookNote.endPos, bookNote.showText, bookNote.noteText));
                break;
            case BookNoteEvent.TYPE_DELETE:
                mUI.getReader().deleteDzLine(new DzLine(bookNote.startPos, bookNote.endPos, bookNote.showText, bookNote.noteText));
                break;
            case BookNoteEvent.TYPE_CLEAR:
                mUI.getReader().clearDzLine();
                break;
            default:
                break;
        }
    }

    /**
     * 主线程操作
     *
     * @param event event
     */
    public void onEventMainThread(EventMessage event) {
        int requestCode = event.getRequestCode();
        String type = event.getType();

        if (requestCode == EventConstant.CODE_ADD_BOOK_FROM_H5 && TextUtils.equals(type, EventConstant.TYPE_ADD_BOOK_FROM_H5)) {
            if (mDoc != null && !TextUtils.isEmpty(mDoc.bookId)) {
                BookInfo bookInfo = new BookInfo();
                bookInfo.bookid = mDoc.bookId;
                bookInfo.isAddBook = 2;
                bookInfo.time = System.currentTimeMillis() + "";
                DBUtils.updateBook(mUI.getContext(), bookInfo);
            }
        }

    }


    /**
     * onResume
     */
    public void onResume() {
        UserGrow.userGrowByReadNew(UserGrow.EnumUserGrowAction.RESUME, getBookId(), getBookName(), getPrev());
    }

    private String getBookId() {
        if (mDoc != null && !TextUtils.isEmpty(mDoc.bookId)) {
            return mDoc.bookId;
        }
        return "";

    }

    private String getBookName() {
        if (mDoc != null && !TextUtils.isEmpty(mDoc.bookName)) {
            return mDoc.bookName;
        }
        return "";

    }

    /**
     * onPause
     */
    public void onPause() {
        UserGrow.userGrowByReadNew(UserGrow.EnumUserGrowAction.PAUSE, getBookId(), getBookName(), getPrev());

        refreshDocument();
        if (mDoc != null) {
            BookInfo bookInfo = new BookInfo();
            bookInfo.bookid = mDoc.bookId;
            bookInfo.currentCatalogId = mDoc.chapterId;
            bookInfo.time = System.currentTimeMillis() + "";
            DBUtils.updateBook(mUI.getContext(), bookInfo);

            CatalogInfo catalogInfo = new CatalogInfo(mDoc.bookId, mDoc.chapterId);
            catalogInfo.currentPos = mDoc.currentPos;
            DBUtils.updateCatalog(mUI.getContext(), catalogInfo);
        }
    }

    /**
     * destroy
     */
    public void destroy() {
        if (readerMode == MODE_AUTO) {
            mUI.finishAutoRead();
            ToastAlone.showShort("您已退出自动阅读模式");
        }

        if (readerMode == MODE_VOICE) {
            finishVoice(VFT_FROM_BACK);
            ToastAlone.showShort("您已退出语音朗读模式");
        }
        composite.disposeAll();

        handler.removeMessages(MESSAGE_STOP_VOICE);
        EventBusUtils.unregister(this);

        if (addBookShelfDialog != null && addBookShelfDialog.isShow()) {
            addBookShelfDialog.dismiss();
            addBookShelfDialog = null;
        }

        //明确需要删除的图书，和未读的下架图书，可以在这里删除掉了。
        if (null != mDoc && !TextUtils.isEmpty(mDoc.bookId)) {
            MarketDao.deleteSomeBook(mUI.getContext(), mDoc.bookId);
        }

        if (mBookInfo != null && !TextUtils.isEmpty(mBookInfo.bookid) && !TextUtils.isEmpty(mBookInfo.bookname)) {
            HwLog.reader(mBookInfo, "", String.valueOf(System.currentTimeMillis() / 1000), "", "", getPrev());
        }
    }

    private String getPrev() {
        if (MainShelfFragment.TAG.equals(prev)) {
            return "2";
        } else if (BookDetailActivity.TAG.equals(prev) || (BookDetailChapterActivity.TAG).equals(prev)) {
            return "1";
        } else {
            return "3";
        }
    }

    /**
     * 返回监听
     *
     * @param finishActivity 是否销毁activity
     */
    public void onBackPress(boolean finishActivity) {

        if (readerMode == MODE_AUTO) {
            mUI.finishAutoRead();
            if (!finishActivity) {
                return;
            }
        }

        if (readerMode == MODE_VOICE) {
            finishVoice(VFT_FROM_BACK);
            ToastAlone.showShort("您已退出语音朗读模式");
            if (!finishActivity) {
                return;
            }
        }

        if (isMenuShow) {
            int state = mUI.getMenuState();
            mUI.hideMenuPanel(true);
            boolean hasNavigationBar = HwUtils.hasNavigationBar(mUI.getContext());
            boolean isFirstMenu = state == ReaderNewPanel.STATE_MAIN;
            if (!finishActivity && (!isFirstMenu || !hasNavigationBar)) {
                return;
            }
        }

        refreshDocument();
        if (mDoc != null) {
            final BookInfo bookInfo = DBUtils.findByBookId(mUI.getContext(), mDoc.bookId);
            if (bookInfo != null && bookInfo.isAddBook == 1) {
                if (addBookShelfDialog == null) {
                    addBookShelfDialog = new CustomHintDialog(mUI.getContext(), false);
                }
                String title = String.format(mUI.getContext().getString(R.string.reader_add_book_shelf), bookInfo.bookname);
                addBookShelfDialog.setTitle(title);
                addBookShelfDialog.setDesc(mUI.getContext().getString(R.string.dialog_join_bookshelf_hint));
                addBookShelfDialog.setCheckListener(new CustomDialogBusiness.OnCheckListener() {
                    @Override
                    public void clickConfirm(Object object) {
                        DzSchedulers.child(new Runnable() {
                            @Override
                            public void run() {
                                final BookInfo bookInfo = new BookInfo();
                                bookInfo.bookid = mBookInfo.bookid;
                                bookInfo.isAddBook = 2;
                                DBUtils.updateBook(mUI.getContext(), bookInfo);
                            }
                        });

                        // 加入书架，同步成长值
                        UserGrow.userGrowOnceToday(mUI.getContext(), UserGrow.USER_GROW_ADD_BOOK);
                        ModelAction.checkElseGoHome(mUI.getHostActivity());
                        mUI.getHostActivity().finish();

                        DzLog.getInstance().logClick(LogConstants.MODULE_YDQ, LogConstants.ZONE_YDQ_JRSJ_QD, mBookInfo.bookid, logMap, null);
                    }

                    @Override
                    public void clickCancel() {
                        DzSchedulers.child(new Runnable() {
                            @Override
                            public void run() {
                                DBUtils.deleteBook(mUI.getContext(), mBookInfo);
                                DBUtils.deleteCatalogByBoodId(mUI.getContext(), mBookInfo.bookid);
                            }
                        });
                        DzLog.getInstance().logClick(LogConstants.MODULE_YDQ, LogConstants.ZONE_YDQ_JRSJ_QX, mBookInfo.bookid, null, null);
                        ModelAction.checkElseGoHome(mUI.getHostActivity());
                        mUI.getHostActivity().finish();

                    }
                });
                addBookShelfDialog.show();
            } else {
                ModelAction.checkElseGoHome(mUI.getHostActivity());
                mUI.getHostActivity().finish();
            }
            //上传书籍阅读进度 只有网络书籍才上传 帐号登录之后才上传
            // FIXMEDongdz: 2018/4/23 优化此段代码：放到intentservices中去
            if (bookInfo != null && bookInfo.bookfrom == 1 && LoginUtils.getInstance().checkLoginStatus(mUI.getContext())) {
                cloudSyncBookProcess(bookInfo);
            }

        } else {
            ModelAction.checkElseGoHome(mUI.getHostActivity());
            mUI.getHostActivity().finish();
        }
    }

    /**
     * 上传图书阅读进度
     *
     * @param bookInfo 图书信息
     */
    private void cloudSyncBookProcess(final BookInfo bookInfo) {
        Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter e) throws Exception {
                long operateDur = System.currentTimeMillis() - Long.parseLong(bookInfo.time);
                HwRequestLib.getInstance().syncBookReadProgress(bookInfo.bookid, bookInfo.currentCatalogId, operateDur + "");
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
                composite.addAndDisposeOldByKey("syncCloudShelf", d);
            }

            @Override
            public void onComplete() {
            }

            @Override
            public void onError(Throwable e) {
            }
        });

    }

    /**
     * 刷新文档
     *
     * @return AkDocInfo
     */
    public AkDocInfo refreshDocument() {
        mDoc = mUI.getDocument();
        return mDoc;
    }

    /**
     * 处理弹窗
     *
     * @param docInfo  docInfo
     * @param showText showText
     * @param noteText noteText
     * @param startPos startPos
     * @param endPos   endPos
     * @param action   action
     * @return boolean
     */
    public boolean handleReaderPopClick(AkDocInfo docInfo, String showText, String noteText, long startPos, long endPos, int action) {
        BookMarkNew bookNote = BookMarkNew.createBookNote(mUI.getContext(), docInfo, startPos, endPos, showText, noteText);
        if (action == ReaderPopWindow.ACTION_LINE) {
            BookMarkNew.addBookNote(mUI.getContext(), bookNote);
            mUI.getReader().addDzLine(new DzLine(startPos, endPos, showText, noteText));

            SyncBookMarkService.launch(mUI.getContext());

            HashMap<String, String> paramsMap = new HashMap<>();
            paramsMap.put("action_type", "action_line");
            paramsMap.put("cid", bookNote.chapterId);
            DzLog.getInstance().logClick(LogConstants.MODULE_YDQ, LogConstants.ZONE_YDQ_YDCZ, bookNote.bookId, paramsMap, null);
        } else if (action == ReaderPopWindow.ACTION_SHARE) {
            ShareActivity.launch(mUI.getHostActivity(), showText, docInfo.bookId, true);

            HashMap<String, String> paramsMap = new HashMap<>();
            paramsMap.put("action_type", "action_share");
            paramsMap.put("cid", bookNote.chapterId);
            DzLog.getInstance().logClick(LogConstants.MODULE_YDQ, LogConstants.ZONE_YDQ_YDCZ, bookNote.bookId, paramsMap, null);
        } else if (action == ReaderPopWindow.ACTION_COPY) {
            ClipboardUtils.getInstanse().copyText(showText);
            mUI.showMessage(mUI.getContext().getResources().getString(R.string.copy_finish));

            HashMap<String, String> paramsMap = new HashMap<>();
            paramsMap.put("action_type", "action_copy");
            paramsMap.put("cid", bookNote.chapterId);
            DzLog.getInstance().logClick(LogConstants.MODULE_YDQ, LogConstants.ZONE_YDQ_YDCZ, bookNote.bookId, paramsMap, null);
        } else if (action == ReaderPopWindow.ACTION_NOTE) {
            ReaderNoteActivity.launch(mUI.getContext(), mUI.getHostActivity().getRequestedOrientation(), bookNote);

            HashMap<String, String> paramsMap = new HashMap<>();
            paramsMap.put("action_type", "action_note");
            paramsMap.put("cid", bookNote.chapterId);
            DzLog.getInstance().logClick(LogConstants.MODULE_YDQ, LogConstants.ZONE_YDQ_YDCZ, bookNote.bookId, paramsMap, null);
        } else if (action == ReaderPopWindow.ACTION_CLEAR) {
            BookMarkNew.deleteBookNote(mUI.getContext(), bookNote, false);
            mUI.getReader().deleteDzLine(new DzLine(startPos, endPos, showText, noteText));

            SyncBookMarkService.launch(mUI.getContext());

            HashMap<String, String> paramsMap = new HashMap<>();
            paramsMap.put("action_type", "action_clear");
            paramsMap.put("cid", bookNote.chapterId);
            DzLog.getInstance().logClick(LogConstants.MODULE_YDQ, LogConstants.ZONE_YDQ_YDCZ, bookNote.bookId, paramsMap, null);
        }
        return true;
    }

    /**
     * onChapterStart
     *
     * @param doc         doc
     * @param isBookStart isBookStart
     * @param autoSerial  autoSerial
     */
    public void onChapterStart(AkDocInfo doc, boolean isBookStart, boolean autoSerial) {
        if (isBookStart) {
            refreshDocument();
            if (mDoc == null || mBookInfo == null) {
                return;
            }
            CatalogInfo preChapter = DBUtils.getPreCatalog(mUI.getContext(), mDoc.bookId, mDoc.chapterId);
            if (preChapter != null) {
                checkAndLoadChapter(preChapter, false, LogConstants.ORDER_SOURCE_FROM_VALUE_6);
            } else {
                mUI.showMessage(R.string.str_first_page);
            }
        }
    }

    /**
     * onChapterEnd
     *
     * @param doc        doc
     * @param isBookEnd  isBookEnd
     * @param autoSerial autoSerial
     */
    public void onChapterEnd(AkDocInfo doc, boolean isBookEnd, boolean autoSerial) {
        //        setEndBookMark();
        if (isBookEnd) {
            refreshDocument();
            if (mDoc == null || mBookInfo == null) {
                return;
            }
            CatalogInfo nextChapter = DBUtils.getNextCatalog(mUI.getContext(), mDoc.bookId, mDoc.chapterId);
            if (nextChapter != null) {
                checkAndLoadChapter(nextChapter, true, LogConstants.ORDER_SOURCE_FROM_VALUE_6);
            } else {
                if (mBookInfo.isLocalBook()) {
                    //本地书
                    mUI.showMessage(R.string.str_last_page);
                } else if (mBookInfo.bookstatus == 2) {
                    //连载书籍
                    updateCatalog(new Runnable() {
                        @Override
                        public void run() {
                            mUI.dissMissDialog();
                            CatalogInfo nextCatalog = DBUtils.getNextCatalog(mUI.getContext(), mDoc.bookId, mDoc.chapterId);
                            if (null != nextCatalog) {
                                checkAndLoadChapter(nextCatalog, true, LogConstants.ORDER_SOURCE_FROM_VALUE_6);
                            } else {
                                //无新的章节
                                //                                requestFollowBook();
                                launchChaseRecommendBooks(mDoc.chapterId);
                            }
                        }
                    });
                } else { //连载已完结 || 单本
                    //                    requestRecommendBook();
                    launchChaseRecommendBooks(mDoc.chapterId);
                }
            }
        }
    }

    /**
     * getNextDocInfo
     *
     * @return AkDocInfo
     */
    public AkDocInfo getNextDocInfo() {
        refreshDocument();
        CatalogInfo nextChapter = DBUtils.getNextCatalog(mUI.getContext(), mDoc.bookId, mDoc.chapterId);
        BookInfo bookInfo = DBUtils.findByBookId(mUI.getContext(), mDoc.bookId);
        if (nextChapter != null && nextChapter.isAvailable()) {
            if (!ReaderUtils.allowOpenDirect(nextChapter)) {
                if (bookInfo.getLimitConfirmStatus() == 1) {
                    return null;
                }
            }
            AkDocInfo docInfo = ReaderUtils.generateDoc(mUI.getContext(), bookInfo, nextChapter);
            docInfo.currentPos = docInfo.chapterStartPos;
            return docInfo;
        }
        return null;
    }

    /**
     * getPreDocInfo
     *
     * @return AkDocInfo
     */
    public AkDocInfo getPreDocInfo() {
        refreshDocument();
        CatalogInfo preChapter = DBUtils.getPreCatalog(mUI.getContext(), mDoc.bookId, mDoc.chapterId);
        BookInfo bookInfo = DBUtils.findByBookId(mUI.getContext(), mDoc.bookId);
        if (preChapter != null && preChapter.isAvailable()) {
            if (!ReaderUtils.allowOpenDirect(preChapter)) {
                if (bookInfo.getLimitConfirmStatus() == 1) {
                    return null;
                }
            }
            AkDocInfo docInfo = ReaderUtils.generateDoc(mUI.getContext(), bookInfo, preChapter);
            docInfo.currentPos = Long.MAX_VALUE;
            return docInfo;
        }

        return null;
    }

    /**
     * 打开书籍
     */
    public void onOpenBook() {
        refreshDocument();
        if (mDoc == null || mBookInfo == null) {
            ToastAlone.showShort("图书信息为空");
            return;
        }
        if (!TextUtils.isEmpty(mDoc.bookId)) {
            SpUtil.getinstance(AppConst.getApp()).setString(SpUtil.RECENT_READER, mDoc.bookId);
        }

        //打印log
        pvLog(mBookInfo);

        //判断当前章节是否是本书最后一个章节，是则请求章节更新 add by lizhongzhong  //@判断bookInfo非空 add by huzs
        updateCatalog();


        // 更新图书阅读进度。
        mBookInfo.hasRead = 1;
        BookInfo book = new BookInfo();
        book.bookid = mDoc.bookId;
        book.currentCatalogId = mDoc.chapterId;
        book.time = System.currentTimeMillis() + "";
        // 图书已读标记
        book.hasRead = 1;
        DBUtils.updateBook(mUI.getContext(), book);

        // 标记章节已读。
        CatalogInfo chapter = new CatalogInfo(mDoc.bookId, mDoc.chapterId);
        chapter.isread = "0";
        CatalogInfo cif = DBUtils.getCatalog(mUI.getContext(), mDoc.bookId, mDoc.chapterId);
        if (null != cif && TextUtils.isEmpty(cif.dlTime)) {
            chapter.dlTime = UtilTimeOffset.getDateFormatSev();
        }
        DBUtils.updateCatalog(mUI.getContext(), chapter);


        DzSchedulers.child(new Runnable() {
            @Override
            public void run() {
                BookLoader.getInstance().preLoadWithRetry(mUI.getHostActivity(), mBookInfo, mDoc.chapterId);
            }
        });

    }


    /**
     * 打点
     *
     * @param bookInfo bookInfo
     */
    public void pvLog(BookInfo bookInfo) {
        if (!TextUtils.isEmpty(mDoc.bookId) && !TextUtils.isEmpty(mDoc.chapterId) && !TextUtils.equals(mDoc.chapterId, lastPvChapterId)) {
            lastPvChapterId = mDoc.chapterId;

            CatalogInfo catalog = DBUtils.getCatalog(mUI.getContext(), mDoc.bookId, mDoc.chapterId);
            HashMap<String, String> map = new HashMap<>();
            HashMap<String, String> readerMap = DzLogMap.getReaderMap(mUI.getContext(), logMap, bookInfo, catalog);

            String cidNumb = "";
            if (null != readerMap && readerMap.containsKey("cid_numb")) {
                cidNumb = readerMap.get("cid_numb");
            }
            HwLog.reader(bookInfo, "", "", "", cidNumb, getPrev());
            if (null != readerMap) {
                map.putAll(readerMap);
            }
            map.put("bid", mDoc.bookId);
            map.put("cid", mDoc.chapterId);
            DzLog.getInstance().logPv(mUI.getHostActivity(), map, null);
        }
    }


    /**
     * 检查文件
     *
     * @param docInfo docInfo
     * @return boolean
     */
    public boolean checkFile(AkDocInfo docInfo) {
        File file = new File(docInfo.path);
        if (!file.exists()) {
            ToastAlone.showShort(R.string.toast_file_not_exit);
            return false;
        } else if (file.isFile() && file.length() <= 3) {
            // 空文件长度为3
            ToastAlone.showShort(R.string.toast_file_is_empty);
            return false;
        }
        String tempPath = docInfo.path.toLowerCase();
        if (!tempPath.endsWith(".txt") && !tempPath.endsWith(".kf") && !tempPath.endsWith(".xhtml") && !tempPath.endsWith(".html")) {
            ToastAlone.showLong(R.string.toast_unsupport_format);
            return false;
        }
        return true;
    }


    /**
     * 初始化配置信息
     */
    public void initConfig() {
        //处理app根目录文件是否是文件夹
        FileUtils.handleAppFileRootDirectory();
        prev = DzLog.getInstance().getPrev();

        //亮度初始化
        boolean isSystemBrightnessMode = SettingManager.getInstance(mUI.getContext()).getBrightnessSystem();
        if (isSystemBrightnessMode) {
            ScreenUtils.setAppScreenBrightnes(mUI.getHostActivity(), -1);
        } else {
            int progress = SettingManager.getInstance(mUI.getContext()).getBrightnessPercent();
            //根据黑白模式修改屏幕
            ScreenUtils.updateScreenBrightnessMask(mUI.getHostActivity(), progress);
        }
    }


    /**
     * 处理intent
     *
     * @param intent intent
     * @return boolean
     */
    public boolean processIntent(Intent intent) {
        if (intent == null) {
            return false;
        }
        //初始化上上级页面的pv map
        if (null == logMap) {
            logMap = DzLogMap.getPreLastMap();
        }

        mDoc = intent.getParcelableExtra("docInfo");

        if (mDoc == null || TextUtils.isEmpty(mDoc.path) || TextUtils.isEmpty(mDoc.bookId)) {
            ToastAlone.showLong(R.string.toast_file_not_exit);
            return false;
        }

        mBookInfo = DBUtils.findByBookId(mUI.getContext(), mDoc.bookId);

        if (mBookInfo == null) {
            return false;
        }

        HwLog.reader(mBookInfo, String.valueOf(System.currentTimeMillis() / 1000), "", "", "", getPrev());

        boolean checkResult = checkFile(mDoc);
        if (!checkResult) {
            return false;
        }

        if (!TextUtils.isEmpty(mDoc.bookId)) {
            SpUtil.getinstance(mUI.getContext()).setBoolean(SpUtil.FROM_URI_BOOK_OPEN + mDoc.bookId, false);
        }


        //友盟打点
        ThirdPartyLog.onEventValue(mUI.getContext(), "dz_" + ThirdPartyLog.READER_UMENG_ID, null, 1);
        ThirdPartyLog.onEventValueOldClick(mUI.getContext(), "dz_" + ThirdPartyLog.READER_UMENG_ID, null, 1);

        mUI.applyCopyrightImg(null);
        mUI.loadDocument(mDoc);
        return true;
    }

    /**
     * 处理章节
     */
    public void processChapter() {
        if (mBookInfo != null && !mBookInfo.isLocalBook()) {
            DzSchedulers.child(new Runnable() {
                @Override
                public void run() {
                    // 查询此书的所有章节isdownload状态为-1(正在下载)的章节,然后还原为1(未下载)状态
                    ArrayList<CatalogInfo> catalogLodings = DBUtils.getCatalogLoadingByBookId(mUI.getContext(), mDoc.bookId);
                    int counter1 = 0;

                    if (catalogLodings != null && catalogLodings.size() > 0) {

                        for (CatalogInfo info : catalogLodings) {
                            if (info != null) {
                                CatalogInfo catalog = new CatalogInfo(info.bookid, info.catalogid);
                                catalog.isdownload = "1";
                                DBUtils.updateCatalog(mUI.getContext(), catalog);
                                counter1++;
                            }
                        }
                    }

                    // 查询此书的所有章节isdownload状态为0(已下载)的章节,如果章节丢失,还原为1(未下载)状态
                    int counter2 = 0;
                    if (SDCardUtil.getInstance().isSDCardAvailable()) {
                        // 查询此书的所有章节isdownload状态为0(已下载)的章节,如果章节丢失,还原为1(未下载)状态
                        ArrayList<CatalogInfo> catalogLocals = DBUtils.getCategLocalByBookId(mUI.getContext(), mDoc.bookId);

                        if (catalogLocals != null && catalogLocals.size() > 0) {

                            for (CatalogInfo info : catalogLocals) {
                                if (info != null && !info.isAvailable()) {
                                    CatalogInfo catalog = new CatalogInfo(info.bookid, info.catalogid);
                                    catalog.isdownload = "1";
                                    DBUtils.updateCatalog(mUI.getContext(), catalog);
                                    counter2++;
                                }
                            }
                        }
                    }

                    ALog.iZz("图书:" + mDoc.bookName + " " + counter1 + "个下载中章节纠正，" + counter2 + "个已下载章节纠正");

                    //设置书籍来源信息
                    setBookInfoReadFrom();
                }
            });


            cloudUpdateReadProgress(mBookInfo);

            // 打开图书的时候，清除付费意向
            MarketDao.markResetWilling(mUI.getContext(), mBookInfo.bookid);

            checkBookMarkSync();

        }
    }

    private void checkBookMarkSync() {
        //step1：获取userId，如果userId为空，不需要执行同步
        String userId = SpUtil.getinstance(mUI.getContext()).getUserID();
        if (TextUtils.isEmpty(userId)) {
            return;
        }

        //step2: 根据userId获取同步的时间，如果存在同步时间，不需要再同步，后续同步由笔记和书签操作触发
        String syncTime = SpUtil.getinstance(mUI.getContext()).getBookMarkSyncTime(userId);
        if (TextUtils.isEmpty(syncTime)) {
            SyncBookMarkService.launch(mUI.getContext(), true);
        }
    }

    /**
     * 批量下载
     *
     * @param bookId    bookId
     * @param chapterId chapterId
     */
    public void download(final String bookId, final String chapterId) {

        if (!NetworkUtils.getInstance().checkNet()) {
            if (mUI.getContext() instanceof BaseActivity) {
                ((BaseActivity) mUI.getContext()).showNotNetDialog();
            }
            return;
        }

        Disposable disposable = getBulkChaptersObservable(mUI.getHostActivity(), bookId, chapterId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<LoadResult>() {

            @Override
            public void onNext(LoadResult value) {
                mUI.dissMissDialog();
                if (value == null) {
                    ALog.dLwx("LoadResult null");
                    mUI.getHostActivity().showNotNetDialog();
                    return;
                }

                //                if (!value.isCanceled() && !value.isSuccess()) {
                //                    // FIXME: cmt 2018/4/24 章节下载错误 待取数sdk完善
                //                }


                if (value.status == LoadResult.STATUS_NET_WORK_NOT_USE || value.status == LoadResult.STATUS_NET_WORK_NOT_COOL || value.status == LoadResult.STATUS_ERROR && !NetworkUtils.getInstance().checkNet()) {
                    if (!TextUtils.isEmpty(value.getMessage(mUI.getContext())) && mUI.getHostActivity() != null) {
                        mUI.getHostActivity().showNotNetDialog();
                    }
                } else {
                    ReaderUtils.dialogOrToast(mUI.getHostActivity(), value.getMessage(mUI.getContext()), true, bookId);
                }
                //                        mUI.showMessage(value.getMessage(mUI.getContext()));
                ALog.dZz("LoadResult:" + value.status);

            }

            @Override
            public void onError(Throwable e) {
                mUI.dissMissDialog();
                ALog.dZz("load ex:" + e.getMessage());
            }

            @Override
            public void onComplete() {
                ALog.dZz("load onComplete");
            }

            @Override
            protected void onStart() {
                mUI.showDialogByType(DialogConstants.TYPE_GET_DATA);
            }
        });

        composite.addAndDisposeOldByKey("downloadBook", disposable);
    }


    /**
     * 获取批量下载的Observable
     *
     * @return
     */
    private Observable<LoadResult> getBulkChaptersObservable(final BaseActivity activity, final String bookId, final String chapterId) {


        return Observable.create(new ObservableOnSubscribe<LoadResult>() {
            @Override
            public void subscribe(ObservableEmitter<LoadResult> e) {

                final BookInfo bookInfo = DBUtils.findByBookId(activity, bookId);
                if (bookInfo.bookfrom == 2) {
                    e.onNext(new LoadResult(LoadResult.STATUS_ERROR, activity.getString(R.string.toast_native_book_unsupport_cache)));
                    e.onComplete();
                    return;
                }

                CatalogInfo currentCatalog = DBUtils.getCatalog(activity, bookInfo.bookid, chapterId);

                RechargeParams rechargeParams = new RechargeParams("4", bookInfo);
                rechargeParams.setOperateFrom(activity.getName());
                rechargeParams.setPartFrom(LogConstants.ORDER_SOURCE_FROM_VALUE_4);
                rechargeParams.isReader = true;

                LoadResult result;
                CatalogInfo noDownloadCatalog = DBUtils.getFirstNoDownloadCatalog(activity, currentCatalog);
                if (noDownloadCatalog == null) {
                    e.onNext(new LoadResult(LoadResult.STATUS_ERROR, "后续已无可缓存章节"));
                    e.onComplete();
                    return;
                }
                result = BookLoader.getInstance().loadBulkChapters(activity, bookInfo, noDownloadCatalog, rechargeParams);

                e.onNext(result);
                e.onComplete();
            }
        });
    }


    /**
     * 云阅读记录更新
     * 先判断是否帐号登录成功
     * 判定条件 1：没有加入书架，2.首次打开 3：是否从云书架界面跳转过来
     */
    private void cloudUpdateReadProgress(final BookInfo bookInfo) {

        if (!isCheckNetProgress && LoginUtils.getInstance().checkLoginStatus(mUI.getContext())) {
            isCheckNetProgress = true;
            boolean flag = (bookInfo.isAddBook != 2 || bookInfo.hasRead == 2) && !bookInfo.isLocalBook();
            if (flag) {

                Disposable disposable = Observable.create(new ObservableOnSubscribe<BeanSingleBookReadProgressInfo>() {
                    @Override
                    public void subscribe(ObservableEmitter<BeanSingleBookReadProgressInfo> e) throws Exception {
                        BeanSingleBookReadProgressInfo beanInfo = HwRequestLib.getInstance().syncBookReadProgressFromNet(bookInfo.bookid, bookInfo.currentCatalogId);
                        e.onNext(beanInfo);
                        e.onComplete();
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<BeanSingleBookReadProgressInfo>() {
                    @Override
                    public void onNext(BeanSingleBookReadProgressInfo beanInfo) {
                        if (beanInfo != null && beanInfo.isSuccess() && !TextUtils.isEmpty(beanInfo.bookId) && !TextUtils.isEmpty(beanInfo.chapterId) && beanInfo.tips != null && beanInfo.tips.size() > 0) {

                            if (mDoc != null && !TextUtils.equals(mDoc.chapterId, beanInfo.chapterId)) {
                                mUI.showCloudProgressDialog(beanInfo);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

                composite.addAndDisposeOldByKey("cloudUpdateReadProgress", disposable);
            }
        }
    }


    /**
     * 跳转到书籍的云记录
     *
     * @param beanInfo beanInfo
     * @param isRetry  isRetry
     */
    @MainThread
    public void showCloudChapter(final BeanSingleBookReadProgressInfo beanInfo, boolean isRetry) {
        if (mBookInfo == null || beanInfo == null) {
            return;
        }

        mUI.showDialogByType(DialogConstants.TYPE_GET_DATA);

        if (isRetry) {
            loadChapterProgressTime = System.currentTimeMillis();
        }

        CatalogInfo info = DBUtils.getCatalog(mUI.getContext(), mBookInfo.bookid, beanInfo.chapterId);
        if (info != null) {
            mUI.dissMissDialog();
            //直接跳转
            checkAndLoadChapter(info, true, LogConstants.ORDER_SOURCE_FROM_VALUE_6);
            ALog.dZz("" + this.getClass().getSimpleName() + "->>showReadProgressOperate->toReaderChapter");
            return;
        }

        //最多等待6s 然后结束
        if (System.currentTimeMillis() - loadChapterProgressTime > 6000) {
            mUI.dissMissDialog();
            ToastAlone.showShort("进度跳转失败，请手动重试");
            return;
        }

        //查询是否在更新目录 如果在更新目录 则等待  如果没有更新目录 则更新目录后跳转
        mBookInfo = DBUtils.findByBookId(mUI.getContext(), mBookInfo.bookid);
        if (mBookInfo.isUpdate == 3) {
            ALog.dZz("" + this.getClass().getSimpleName() + "->>showReadProgressOperate->mBookInfo.isUpdate==3");
            DzThread.getDefault().postDelayOnMain(new Runnable() {
                @Override
                public void run() {
                    showCloudChapter(beanInfo, false);
                }
            }, 3000);
        } else if (isRetry) {
            CheckBookshelfUpdateRunnable checkRunnable = new CheckBookshelfUpdateRunnable(mUI.getContext(), mBookInfo.bookid);
            DzThread.getDefault().postMainAfterWorker(checkRunnable, new Runnable() {
                @Override
                public void run() {
                    showCloudChapter(beanInfo, false);
                }
            });
        } else {
            mUI.dissMissDialog();
            ToastAlone.showShort("进度跳转失败，请手动重试");
        }
    }

    /**
     * 检查加载章节
     *
     * @param indexChapter indexChapter
     * @param isNext       isNext
     * @param partFrom     partFrom
     */
    public void checkAndLoadChapter(final CatalogInfo indexChapter, final boolean isNext, final String partFrom) {
        if (indexChapter == null) {
            return;
        }

        //停止自动阅读
        if (indexChapter.isAvailable()) {
            showChapter(indexChapter, isNext);
            return;
        }

        Disposable disposable = Observable.create(new ObservableOnSubscribe<LoadResult>() {

            @Override
            public void subscribe(ObservableEmitter<LoadResult> e) {
                RechargeParams rechargeParams = new RechargeParams(RechargeParams.READACTION_SINGLE, mBookInfo);

                rechargeParams.setOperateFrom(mUI.getHostActivity().getName());
                rechargeParams.setPartFrom(partFrom);

                rechargeParams.isReader = true;

                LoadResult result = BookLoader.getInstance().loadOneChapter(mUI.getHostActivity(), mBookInfo, indexChapter, rechargeParams);
                if (result != null) {
                    result.mChapter = indexChapter;
                }
                e.onNext(result);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<LoadResult>() {
            @Override
            public void onNext(LoadResult value) {
                mUI.dissMissDialog();
                if (value.isSuccess()) {
                    CatalogInfo chapter = DBUtils.getCatalog(mUI.getContext(), indexChapter.bookid, indexChapter.catalogid);
                    showChapter(chapter, isNext);
                    return;
                }
                //                if (!value.isCanceled()) {
                //                    // FIXME: cmt 2018/4/24 章节下载错误 待取数sdk完善
                //                }
                if (value.status == LoadResult.STATUS_NET_WORK_NOT_USE || value.status == LoadResult.STATUS_NET_WORK_NOT_COOL || value.status == LoadResult.STATUS_ERROR && !NetworkUtils.getInstance().checkNet()) {
                    if (!TextUtils.isEmpty(value.getMessage(mUI.getContext())) && mUI.getHostActivity() != null) {
                        mUI.getHostActivity().showNotNetDialog();
                    }
                } else {
                    ReaderUtils.dialogOrToast(mUI.getHostActivity(), value.getMessage(mUI.getContext()), true, indexChapter.bookid);
                }
            }

            @Override
            public void onError(Throwable e) {
                mUI.dissMissDialog();
            }

            @Override
            public void onComplete() {
                mUI.dissMissDialog();
            }

            @Override
            protected void onStart() {
                mUI.showDialogByType(DialogConstants.TYPE_GET_DATA);
            }
        });

        composite.addAndDisposeOldByKey("checkAndLoadChapter", disposable);

    }

    /**
     * 展示章节
     *
     * @param indexChapter indexChapter
     * @param isTurnNext   isTurnNext
     */
    public void showChapter(CatalogInfo indexChapter, boolean isTurnNext) {
        if (indexChapter == null || mBookInfo == null) {
            return;
        }

        if (indexChapter.isContentEmptyDeleted()) {
            //2(缺章,未领取) 3(缺章，已领取) //4(删章)
            Intent intent = new Intent(mUI.getContext(), MissingContentActivity.class);
            intent.putExtra(MissingContentPresenter.BOOK_INFO, mBookInfo);
            intent.putExtra(MissingContentPresenter.CHAPTER_INFO, indexChapter);
            mUI.getContext().startActivity(intent);
            return;
        }

        AkDocInfo docInfo = ReaderUtils.generateDoc(mUI.getContext(), mBookInfo, indexChapter);
        if (isTurnNext) {
            docInfo.currentPos = docInfo.chapterStartPos;
        } else {
            docInfo.currentPos = Long.MAX_VALUE;
        }
        docInfo.isStoreBook = mBookInfo.bookfrom == 1;
        Intent intent = new Intent(mUI.getContext(), ReaderActivity.class);
        intent.putExtra("docInfo", docInfo);
        processIntent(intent);
    }


    /**
     * 书城图书，在联网状态下面列举场景会更新目录<br>
     * （1）未读 || 有更新标记的<br>
     * （2）(连载未完结 || 单本内置的图书) && 章节目录最后一章<br>
     * （3）正在更新的图书，等会如果失败再发起更新重试<br>
     */
    private void updateCatalog() {
        if (mDoc == null || mBookInfo == null || mBookInfo.bookfrom == 2 || !NetworkUtils.getInstance().checkNet()) {
            return;
        }

        if (isNeedUpdateCatalogDirect()) {
            DzThread.getDefault().postOnWorker(new CheckBookshelfUpdateRunnable(mUI.getContext(), mBookInfo.bookid));
        } else if (mBookInfo.isUpdate == 3) {
            DzThread.getDefault().postDelayOnWorker(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 3; i++) {
                        if (!NetworkUtils.getInstance().checkNet()) {
                            break;
                        }
                        BookInfo bookInfo = DBUtils.findByBookId(mUI.getContext(), mBookInfo.bookid);
                        if (bookInfo != null && bookInfo.isUpdate == 2) {
                            DzThread.getDefault().postOnWorker(new CheckBookshelfUpdateRunnable(mUI.getContext(), mBookInfo.bookid));
                        } else {
                            try {
                                Thread.sleep(1500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }, 3000);
        }
    }

    private boolean isNeedUpdateCatalogDirect() {
        if (mBookInfo.hasRead == 2 || mBookInfo.isUpdate == 2) {
            return true;
        }
        if (mBookInfo.bookstatus == 2 && mBookInfo.isEnd != 2) {
            CatalogInfo lastCatalog = DBUtils.getLastCatalog(mUI.getContext(), mDoc.bookId);
            return lastCatalog != null && TextUtils.equals(lastCatalog.catalogid, mDoc.chapterId);
        } else if (mBookInfo.bookstatus == 1 && mBookInfo.isdefautbook == 2) {
            CatalogInfo lastCatalog = DBUtils.getLastCatalog(mUI.getContext(), mDoc.bookId);
            return lastCatalog != null && TextUtils.equals(lastCatalog.catalogid, mDoc.chapterId);
        }
        return false;
    }

    private void updateCatalog(Runnable runnable) {
        if (mBookInfo != null) {
            mUI.showDialogByType(DialogConstants.TYPE_GET_DATA);
            DzThread.getDefault().postMainAfterWorker(new CheckBookshelfUpdateRunnable(mUI.getContext(), mBookInfo.bookid), runnable);
        }
    }

    /**
     * 跳转推荐书籍页
     *
     * @param lastChapterID lastChapterID
     */
    public void launchChaseRecommendBooks(String lastChapterID) {

        final long thisTime = System.currentTimeMillis();
        if (thisTime - lastDetailTime > AppConst.MAX_CLICK_INTERVAL_TIME) {
            lastDetailTime = thisTime;
            if (null != mBookInfo) {
                TurnPageUtils.toRecommentPage(mUI.getContext(), mBookInfo.bookid, mBookInfo.bookname, mBookInfo.bookstatus, lastChapterID, mBookInfo.bookfrom);
            }
        }
    }


    private String getPercentStr(float percent) {
        return df.format(percent);
    }


    /**
     * 开始自动朗读模式
     */
    public void startAutoRead() {
        readerMode = MODE_AUTO;
    }

    /**
     * 结束自动朗读模式
     */
    public void finishAutoRead() {
        readerMode = MODE_NORMAL;
    }

    public boolean isAutoRead() {
        return readerMode == MODE_AUTO;
    }


    /**
     * 开始朗读
     */
    public void startVoice() {
        mUI.hideMenuPanel(false);

        PluginInfo pluginInfo = DBUtils.getPlugin(mUI.getContext(), PluginInfo.TTS_NAME);
        PluginTtsInfo ttsInfo = null;
        if (pluginInfo != null) {
            ttsInfo = pluginInfo.getTtsInfo();
        }

        if (ttsInfo == null) {
            mUI.showPluginDialog();
        } else {
            VoiceInfo voiceInfo = new VoiceInfo(ttsInfo.appId, ttsInfo.appKey, ttsInfo.secretKey);

            int voiceSpeed = SettingManager.getInstance(mUI.getContext()).getVoiceSpeed() / 10;
            voiceInfo.voiceSpeed = String.valueOf(voiceSpeed);

            int voicePlusIndex = SettingManager.getInstance(mUI.getContext()).getVoicePlusIndex();
            String type = SettingManager.getVoicePlusValue(voicePlusIndex);
            voiceInfo.voicePlusType = type;

            voiceInfo.baseFilePath = ttsInfo.getBaseFilePath();

            int voiceLocalIndex = SettingManager.getInstance(mUI.getContext()).getVoiceLocalIndex();
            voiceInfo.modelFilePath = ttsInfo.getVoiceFilePath(voiceLocalIndex);

            initTts(voiceInfo);
            updateTtsInfo(ttsInfo);
        }

    }

    private void updateTtsInfo(final PluginTtsInfo ttsInfo) {
        if (ttsInfo == null) {
            return;
        }

        if (System.currentTimeMillis() - ttsInfo.updateTime > 30 * 60 * 1000) {
            Single.create(new SingleOnSubscribe<Plugins>() {

                @Override
                public void subscribe(SingleEmitter<Plugins> e) throws Exception {
                    Plugins plugins = HwRequestLib.getInstance().getPluginInfo();
                    e.onSuccess(plugins);
                }
            }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new SingleObserver<Plugins>() {
                @Override
                public void onSubscribe(Disposable d) {
                    composite.addAndDisposeOldByKey("updateTts", d);
                }

                @Override
                public void onSuccess(Plugins value) {
                    if (value.isSuccess()) {
                        if (value.ttsPlugin != null && value.ttsPlugin.isEnable()) {
                            ttsInfo.appKey = value.ttsPlugin.ttsInfo.appKey;
                            ttsInfo.appId = value.ttsPlugin.ttsInfo.appId;
                            ttsInfo.secretKey = value.ttsPlugin.ttsInfo.secretKey;
                            ttsInfo.updateTime = System.currentTimeMillis();
                            PersonPluginPresenter.updateDbTts(mUI.getContext(), ttsInfo);
                        }
                    }
                }

                @Override
                public void onError(Throwable e) {

                }
            });

        }

    }


    /**
     * 结束朗读
     *
     * @param tag tag
     */
    public void finishVoice(int tag) {
        handler.removeMessages(MESSAGE_STOP_VOICE);
        //UI处理
        mUI.hideMenuPanel(true);
        readerMode = MODE_NORMAL;
        mUI.getReader().setCurrentVoiceLine(null, 0);
        ReaderVoiceHelper.getInstance().stopTts();

        ReaderVoiceHelper.getInstance().abandomAudioFocus(mUI.getContext(), this);

        int animIndex = SettingManager.getInstance(mUI.getContext()).getAnimStyleIndex();
        mUI.applyAnim(animIndex);

        refreshDocument();
        if (mDoc != null && startTtsTime > 0) {
            final float half = 0.5f;
            final float thounds = 1000f;
            int totalTime = (int) (half + (System.currentTimeMillis() - startTtsTime) / thounds);
            int voiceSpeed = SettingManager.getInstance(mUI.getContext()).getVoiceSpeed() / 10;
            int voicePlusIndex = SettingManager.getInstance(mUI.getContext()).getVoicePlusIndex();

            int voiceLocalIndex = SettingManager.getInstance(mUI.getContext()).getVoiceLocalIndex();
            int timeIndex = SettingManager.getInstance(mUI.getContext()).getVoiceTimeIndex();

            HashMap<String, String> paramsMap = new HashMap<>();
            paramsMap.put("bid", mDoc.bookId);
            paramsMap.put("cid", mDoc.chapterId);

            paramsMap.put("totalTime", totalTime + "");
            paramsMap.put("speed", voiceSpeed + "");
            paramsMap.put("voicePlusIndex", voicePlusIndex + "");
            paramsMap.put("voiceLocalIndex", voiceLocalIndex + "");
            paramsMap.put("timeIndex", timeIndex + "");
            paramsMap.put("finishTag", tag + "");
            DzLog.getInstance().logEvent(LogConstants.EVENT_YDQ_TTS_B, paramsMap, null);
            startTtsTime = 0;
        }

        stopRecordVoiceReadTime();
    }

    public boolean isVoiceMode() {
        return readerMode == MODE_VOICE;
    }

    /**
     * 重置朗读速度
     *
     * @param speed speed
     */
    public void resetVoiceSpeed(final String speed) {
        DzThread.getByTag(AkReaderView.THREAD_TAG).postOnWorker(new Runnable() {
            @Override
            public void run() {
                VoiceLine voiceLine = mUI.getReader().getCurrentVoiceLine();
                DzChar dzChar = null;
                if (voiceLine != null) {
                    dzChar = voiceLine.getFirstChar();
                }
                ArrayList<DzChar> list = mUI.getReader().getPageTextForVoice(dzChar, true);

                ReaderVoiceHelper.getInstance().stop();
                ReaderVoiceHelper.getInstance().setVoiceSpeed(speed);

                ReaderVoiceHelper.getInstance().addChar(list);
            }
        });

    }

    /**
     * 重置语音插件朗读类型
     *
     * @param index index
     */
    public void resetVoicePlusType(int index) {
        VoiceLine voiceLine = mUI.getReader().getCurrentVoiceLine();
        DzChar dzChar = null;
        if (voiceLine != null) {
            dzChar = voiceLine.getFirstChar();
        }
        ReaderVoiceHelper.getInstance().stop();
        ReaderVoiceHelper.getInstance().setVoicePlusType(index);
        ArrayList<DzChar> list = mUI.getReader().getPageTextForVoice(dzChar, true);
        ReaderVoiceHelper.getInstance().addChar(list);
    }

    /**
     * 重置离线语音类型
     *
     * @param index index
     */
    public void resetVoiceLocalType(int index) {
        PluginInfo pluginInfo = DBUtils.getPlugin(mUI.getContext(), PluginInfo.TTS_NAME);
        PluginTtsInfo ttsInfo = null;
        if (pluginInfo != null) {
            ttsInfo = pluginInfo.getTtsInfo();
        }

        if (ttsInfo == null) {
            return;
        }

        int voiceLocalIndex = SettingManager.getInstance(mUI.getContext()).getVoiceLocalIndex();
        String filePath = ttsInfo.getVoiceFilePath(voiceLocalIndex);
        if (!TextUtils.isEmpty(filePath)) {
            VoiceLine voiceLine = mUI.getReader().getCurrentVoiceLine();
            DzChar dzChar = null;
            if (voiceLine != null) {
                dzChar = voiceLine.getFirstChar();
            }

            ReaderVoiceHelper.getInstance().stop();
            ReaderVoiceHelper.getInstance().setVoiceLocalType(filePath, ttsInfo.getBaseFilePath());

            ArrayList<DzChar> list = mUI.getReader().getPageTextForVoice(dzChar, true);
            ReaderVoiceHelper.getInstance().addChar(list);
        }
    }

    /**
     * 重置语音阅读时长
     *
     * @param index index
     */
    public void resetVoiceTime(int index) {
        mUI.hideMenuPanel(false);
        handler.removeMessages(MESSAGE_STOP_VOICE);
        int delay = SettingManager.getVoiceTimeValue(index);
        if (delay > 0) {
            mUI.showMessage((delay / (60 * 1000)) + "分钟后自动关闭语音朗读");
            handler.sendEmptyMessageDelayed(MESSAGE_STOP_VOICE, delay);
        }
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        if (focusChange == AudioManager.AUDIOFOCUS_LOSS || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
            //失去焦点之后的操作
            finishVoice(VFT_FROM_FOCUS);
        }
    }


    private void initTts(final VoiceInfo voiceInfo) {
        Disposable disposable = Observable.create(new ObservableOnSubscribe<Integer>() {

            @Override
            public void subscribe(ObservableEmitter<Integer> e) {
                int voiceSdkState = ReaderVoiceHelper.getInstance().getSdkState();
                if (voiceSdkState == ReaderVoiceHelper.STATE_READY) {
                    ReaderVoiceHelper.getInstance().stopTts();
                }
                voiceSdkState = ReaderVoiceHelper.getInstance().getSdkState();
                if (voiceSdkState == ReaderVoiceHelper.STATE_TO_INIT) {
                    ReaderVoiceHelper.getInstance().initTts(mUI.getContext(), voiceInfo, voiceListener);
                }
                e.onNext(ReaderVoiceHelper.getInstance().getSdkState());
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<Integer>() {
            @Override
            public void onNext(Integer value) {
                mUI.dissMissDialog();
                if (value == ReaderVoiceHelper.STATE_READY) {
                    readerMode = MODE_VOICE;
                    mUI.setMenuState(ReaderNewPanel.STATE_VOICE);

                    ReaderVoiceHelper.getInstance().requestAudioFocus(mUI.getContext(), ReaderPresenter.this);
                    mUI.getReader().setAnimStyle(6);
                    ArrayList<DzChar> list = mUI.getReader().getPageTextForVoice(null, false);
                    ReaderVoiceHelper.getInstance().addChar(list);
                    int timeIndex = SettingManager.getInstance(mUI.getContext()).getVoiceTimeIndex();
                    //                            int time = SpUtil.getinstance(mUI.getContext()).getInt(SpUtil.DZ_READER_VOICE_TIME, SpUtil.VOICE_TIME_CLOSE);
                    resetVoiceTime(timeIndex);

                    refreshDocument();
                    if (mDoc != null) {
                        startTtsTime = System.currentTimeMillis();
                        HashMap<String, String> paramsMap = new HashMap<>();
                        paramsMap.put("bid", mDoc.bookId);
                        paramsMap.put("cid", mDoc.chapterId);
                        DzLog.getInstance().logEvent(LogConstants.EVENT_YDQ_TTS_A, paramsMap, null);
                    }

                } else if (value == ReaderVoiceHelper.STATE_TO_INIT) {
                    mUI.showMessage("初始化语音插件失败，请稍后重试");
                }
            }

            @Override
            public void onError(Throwable e) {
                mUI.dissMissDialog();
            }

            @Override
            public void onComplete() {

            }

            @Override
            protected void onStart() {
                super.onStart();
                mUI.showDialogByType(DialogConstants.TYPE_GET_DATA);
            }
        });

        composite.addAndDisposeOldByKey("initTts", disposable);
    }

    /**
     * 记录阅读时长
     */
    public void startRecordVoiceReadTime() {
        try {
            if (isVoiceMode()) {
                if (mDisposable != null) {
                    mDisposable.dispose();
                }

                Observable.interval(0, 5, TimeUnit.MINUTES)
                        //设置总共发送的次数
                        .take(COUNT_TIME + 1).map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) {
                        //aLong从0开始
                        return COUNT_TIME - aLong;
                    }
                }).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(Long value) {
                        try {
                            ALog.dZz("recordVoiceReadTime startRecordVoiceReadTime  shcedule run");

                            SpUtil spUtil = SpUtil.getinstance(AppConst.getApp());
                            long time = spUtil.getLocalReaderDurationTime();
                            if (LoginUtils.getInstance().checkLoginStatus(AppConst.getApp())) {
                                spUtil.setLocalReaderDurationTime(time + voiceReadScheduleTime);
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }

                        ALog.dWz("startRecordVoiceReadTime value:" + value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        ALog.printExceptionWz(e);
                        if (mDisposable != null) {
                            mDisposable.dispose();
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (mDisposable != null) {
                            mDisposable.dispose();
                        }
                    }
                });

                ALog.dZz("recordVoiceReadTime startRecordVoiceReadTime");
            } else {
                stopRecordVoiceReadTime();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止记录阅读时长
     */
    public void stopRecordVoiceReadTime() {
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }

    /**
     * 设置书籍来源信息
     */
    public void setBookInfoReadFrom() {
        JSONObject whiteObj = WhiteListWorker.getWhiteObj();
        if (null != whiteObj && mDoc != null && !TextUtils.isEmpty(mDoc.bookId)) {
            BookInfo mBookInfo1 = new BookInfo();
            mBookInfo1.bookid = mDoc.bookId;
            mBookInfo1.readerFrom = whiteObj.toString();
            DBUtils.updateBook(mUI.getContext(), mBookInfo1);
        }
    }

}
