package com.dzbook.activity.reader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dzbook.BaseLoadActivity;
import com.dzbook.adapter.SubTabReaderCatalogAdapter;
import com.dzbook.database.bean.BookInfo;
import com.dzbook.database.bean.BookMarkNew;
import com.dzbook.database.bean.CatalogInfo;
import com.dzbook.dialog.common.CatalogSelectPopWindow;
import com.dzbook.event.EventBusUtils;
import com.dzbook.event.type.BookMarkEvent;
import com.dzbook.event.type.BookNoteEvent;
import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.mvp.UI.ReaderCatalogUI;
import com.dzbook.mvp.presenter.ReaderCatalogPresenter;
import com.dzbook.r.c.AkDocInfo;
import com.dzbook.r.util.HwUtils;
import com.dzbook.service.SyncBookMarkService;
import com.dzbook.templet.ReaderChapterFragment;
import com.dzbook.templet.ReaderMarkFragment;
import com.dzbook.templet.ReaderNoteFragment;
import com.dzbook.utils.DBUtils;
import com.dzbook.utils.ListUtils;
import com.dzbook.utils.ThirdPartyLog;
import com.dzbook.view.DianZhongCommonTitle;
import com.dzbook.view.common.dialog.CustomHintDialog;
import com.dzbook.view.common.dialog.CustomTxtMoreDialog;
import com.dzbook.view.common.dialog.base.CustomDialogBusiness;
import com.ishugui.R;
import com.iss.app.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import huawei.widget.HwSubTabWidget;
import hw.sdk.net.bean.BeanBlock;

/**
 * 阅读目录
 *
 * @author gavin
 */
public class ReaderCatalogActivity extends BaseLoadActivity implements View.OnClickListener, ReaderCatalogUI {
    /**
     * tag
     */
    public static final String TAG = "ReaderCatalogActivity";
    private DianZhongCommonTitle mCommonTitle;
    private CatalogSelectPopWindow mCatalogSelectPopWindow;
    private ViewPager viewPager;
    private LinearLayout layoutRoot;
    private ProgressBar progressbarScanCatalog;
    private ReaderCatalogPresenter mPresenter;

    private int mOrientation;
    private HwSubTabWidget mHwSubTabWidget;
    private SubTabReaderCatalogAdapter mSubTabReaderCatalogAdapter;

    @Override
    protected boolean isNoFragmentCache() {
        return true;
    }

    /**
     * 启动
     *
     * @param context     context
     * @param orientation orientation
     * @param docInfo     docInfo
     */
    public static void launch(Activity context, int orientation, AkDocInfo docInfo) {
        Intent intent = new Intent(context, ReaderCatalogActivity.class);
        intent.putExtra("docInfo", docInfo);
        intent.putExtra("orientation", orientation);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
    }

    @Override
    public String getTagName() {
        return TAG;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_catalog);
        setRequestedOrientation(mOrientation);
        setPadding();
    }

    /**
     * 适配横屏模式下的挖孔屏显示
     */
    private void setPadding() {
        if (mOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            int[] notchSize = HwUtils.getNotchSize();
            if (notchSize == null) {
                notchSize = new int[2];
                notchSize[0] = 0;
                notchSize[1] = 0;
            }
            viewPager.setPadding(notchSize[1], 0, 0, 0);
        }
    }

    @Override
    protected void initView() {
        mCommonTitle = findViewById(R.id.commontitle);

        viewPager = findViewById(R.id.viewpager);
        layoutRoot = findViewById(R.id.layout_root);
        progressbarScanCatalog = findViewById(R.id.progressbar_scan_catalog);
        mCatalogSelectPopWindow = new CatalogSelectPopWindow(this);
        mHwSubTabWidget = initializeSubTabs(getContext());
    }

    private HwSubTabWidget initializeSubTabs(Context context) {
        HwSubTabWidget subTabWidget = findViewById(R.id.layout_tab_catalog);
        mSubTabReaderCatalogAdapter = new SubTabReaderCatalogAdapter((FragmentActivity) context, viewPager, subTabWidget);
        return subTabWidget;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void initData() {
        Intent intent = getIntent();
        AkDocInfo mDoc = null;
        if (intent != null) {
            mDoc = intent.getParcelableExtra("docInfo");
            mOrientation = intent.getIntExtra("orientation", -1);
        }
        //这里加载目录列表 需要做判空处理
        if (null == mDoc || TextUtils.isEmpty(mDoc.bookId)) {
            finish();
            return;
        }

        BookInfo bookInfo = DBUtils.findByBookId(this, mDoc.bookId);
        if (bookInfo == null) {
            finish();
            return;
        }

        mPresenter = new ReaderCatalogPresenter(this, mDoc, bookInfo);

        bindData();
    }

    private void bindData() {
        ReaderChapterFragment readerChapterFragment = new ReaderChapterFragment();
        HwSubTabWidget.SubTab readerChapterSubTab = mHwSubTabWidget.newSubTab(getResources().getString(R.string.chapter_book));
        readerChapterFragment.setPresenter(mPresenter);

        ReaderMarkFragment readerMarkFragment = new ReaderMarkFragment();
        HwSubTabWidget.SubTab readerMarkSubTab = mHwSubTabWidget.newSubTab(getResources().getString(R.string.hw_mark));
        readerMarkFragment.setPresenter(mPresenter);

        ReaderNoteFragment readerNoteFragment = new ReaderNoteFragment();
        HwSubTabWidget.SubTab readerNoteSubTab = mHwSubTabWidget.newSubTab(getResources().getString(R.string.hw_note));
        readerNoteFragment.setPresenter(mPresenter);

        mSubTabReaderCatalogAdapter.addSubTab(readerChapterSubTab, readerChapterFragment, null, true);
        mSubTabReaderCatalogAdapter.addSubTab(readerMarkSubTab, readerMarkFragment, null, false);
        mSubTabReaderCatalogAdapter.addSubTab(readerNoteSubTab, readerNoteFragment, null, false);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.destroy();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void showScanProgress(int progress, int max) {
        progressbarScanCatalog.setVisibility(View.VISIBLE);
        progressbarScanCatalog.setMax(max);
        progressbarScanCatalog.setProgress(progress);
    }

    @Override
    public void hideScanProgress() {
        progressbarScanCatalog.setVisibility(View.GONE);
    }

    @Override
    public void intoReaderCatalogInfo(CatalogInfo catalogInfo) {
        ReaderUtils.intoReader(this, catalogInfo, catalogInfo.currentPos);
        finish();
    }

    @Override
    public BaseActivity getHostActivity() {
        return this;
    }

    @Override
    public void setPurchasedButtonStatus(int status, int remainSize, int totalSize) {
        ReaderChapterFragment readerChapterFragment = mSubTabReaderCatalogAdapter.getReaderChapterFragment();
        if (null != readerChapterFragment) {
            readerChapterFragment.setPurchasedButtonStatus(status, remainSize, totalSize);
        }
    }

    @Override
    public void addChapterItem(List<CatalogInfo> list, boolean clear) {
        if (ListUtils.isEmpty(list)) {
            return;
        }
        ReaderChapterFragment readerChapterFragment = mSubTabReaderCatalogAdapter.getReaderChapterFragment();
        if (null != readerChapterFragment) {
            readerChapterFragment.addItem(list, clear);
        }
        List<BeanBlock> blockBeanList = new ArrayList<>();
        int num = list.size() % 50;
        int len = num == 0 ? (list.size() / 50) : list.size() / 50 + 1;
        for (int i = 0; i < len; i++) {
            BeanBlock beanBlock = new BeanBlock();
            beanBlock.startId = list.get(i * 50).catalogid;
            if (i == len - 1) {
                beanBlock.tip = (50 * i + 1) + "-" + list.size() + "章";
                beanBlock.endId = list.get(list.size() - 1).catalogid;
            } else {
                beanBlock.tip = (50 * i + 1) + "-" + 50 * (i + 1) + "章";
                beanBlock.endId = list.get((i + 1) * 50 - 1).catalogid;
            }
            blockBeanList.add(beanBlock);
        }

        if (blockBeanList.size() > 0) {
            mCatalogSelectPopWindow.addItem(blockBeanList);
//            mCatalogSelectPopWindow.setPopupHeight(blockBeanList.size());
        }
    }

    @Override
    public void addBookMarkItem(List<BookMarkNew> list, boolean clear) {
        ReaderMarkFragment readerMarkFragment = mSubTabReaderCatalogAdapter.getReaderMarkFragment();
        if (null != readerMarkFragment) {
            readerMarkFragment.addItem(list, clear);
        }
    }

    @Override
    public void addBookNoteItem(List<BookMarkNew> list, boolean clear) {
        ReaderNoteFragment readerNoteFragment = mSubTabReaderCatalogAdapter.getReaderNoteFragment();
        if (null != readerNoteFragment) {
            readerNoteFragment.addItem(list, clear);
        }
    }

    @Override
    public void setSelectionFromTop(String catalogId) {
        ReaderChapterFragment readerChapterFragment = mSubTabReaderCatalogAdapter.getReaderChapterFragment();
        if (null != readerChapterFragment) {
            readerChapterFragment.setSelectionFromTop(catalogId);
        }
    }

    @Override
    public void refreshChapterView() {
        ReaderChapterFragment readerChapterFragment = mSubTabReaderCatalogAdapter.getReaderChapterFragment();
        if (null != readerChapterFragment) {
            readerChapterFragment.refresh();
        }
    }

    @Override
    public void refreshBookMarkView() {
        mPresenter.getBookMarkTask();
    }

    @Override
    public void refreshBookNoteView() {
        mPresenter.getBookNoteTask();
    }

    @Override
    public void onChapterItemClick(CatalogInfo chapter) {
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("action_type", "click");
        paramsMap.put("cid", chapter.catalogid);
        DzLog.getInstance().logClick(LogConstants.MODULE_YDQML, LogConstants.ZONE_YDQML_ZJ, chapter.bookid, paramsMap, null);
        mPresenter.handleChapterClick(chapter);
    }

    @Override
    public void onBookMarkItemClick(BookMarkNew bookMark) {
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("action_type", "click");
        paramsMap.put("cid", bookMark.chapterId);
        DzLog.getInstance().logClick(LogConstants.MODULE_YDQML, LogConstants.ZONE_YDQML_SQ, bookMark.bookId, paramsMap, null);

        CatalogInfo chapter = DBUtils.getCatalog(this, bookMark.bookId, bookMark.chapterId);
        if (chapter != null) {
            mPresenter.handleChapterClick(chapter, bookMark.startPos);
        } else {
            finish();
            overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
        }
    }


    @Override
    public void onBookMarkItemLongClick(final BookMarkNew bookMark) {
        List<String> list = new ArrayList<>();
        list.add(getResources().getString(R.string.str_delete_book_mark));
        list.add(getResources().getString(R.string.clean_all_book_marks));

        final CustomTxtMoreDialog dialog = new CustomTxtMoreDialog(getContext(), CustomDialogBusiness.STYLE_DIALOG_BOTTOM_CANCEL);
        dialog.setTitle(getResources().getString(R.string.hw_mark));
        dialog.setData(list, -1, true);
        dialog.setCheckListener(new CustomDialogBusiness.OnCheckListener() {
            @Override
            public void clickConfirm(Object object) {
                int index = (int) object;
                switch (index) {
                    case 0:
                        popDeleteOneBookMark(bookMark, dialog);
                        break;
                    case 1:
                        popDeleteAllBookMark(bookMark, dialog);
                        break;
                    default:
                        break;
                }
                dialog.dismiss();
            }

            @Override
            public void clickCancel() {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    private void popDeleteOneBookMark(final BookMarkNew bookMark, final CustomTxtMoreDialog dialog) {
        CustomHintDialog hintDialog = new CustomHintDialog(getContext());
        // 稍后哦确认翻译
        hintDialog.setTitle("是否删除此书签？");
        hintDialog.show();
        hintDialog.setCheckListener(new CustomDialogBusiness.OnCheckListener() {
            @Override
            public void clickConfirm(Object object) {
                HashMap<String, String> paramsMap = new HashMap<>();
                paramsMap.put("action_type", "delete");
                paramsMap.put("cid", bookMark.chapterId);
                DzLog.getInstance().logClick(LogConstants.MODULE_YDQML, LogConstants.ZONE_YDQML_SQ, bookMark.bookId, paramsMap, null);

                BookMarkNew.deleteBookMark(ReaderCatalogActivity.this, bookMark, false);
                EventBusUtils.sendMessage(new BookMarkEvent(BookMarkEvent.TYPE_DELETE, bookMark));

                SyncBookMarkService.launch(ReaderCatalogActivity.this);
            }

            @Override
            public void clickCancel() {
                dialog.dismiss();
            }
        });
    }

    private void popDeleteAllBookMark(final BookMarkNew bookMark, final CustomTxtMoreDialog dialog) {
        CustomHintDialog hintDialog = new CustomHintDialog(getContext());
        // 稍后哦确认翻译
        hintDialog.setTitle("是否删除所有书签？");
        hintDialog.show();
        hintDialog.setCheckListener(new CustomDialogBusiness.OnCheckListener() {
            @Override
            public void clickConfirm(Object object) {
                HashMap<String, String> paramsMap = new HashMap<>();
                paramsMap.put("action_type", "clear");
                paramsMap.put("cid", bookMark.chapterId);
                DzLog.getInstance().logClick(LogConstants.MODULE_YDQML, LogConstants.ZONE_YDQML_SQ, bookMark.bookId, paramsMap, null);

                BookMarkNew.clearBookMark(ReaderCatalogActivity.this, bookMark.bookId);
                EventBusUtils.sendMessage(new BookMarkEvent(BookMarkEvent.TYPE_CLEAR, bookMark));

                SyncBookMarkService.launch(ReaderCatalogActivity.this);
            }

            @Override
            public void clickCancel() {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onBookNoteItemClick(BookMarkNew bookNote) {
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("action_type", "click");
        paramsMap.put("cid", bookNote.chapterId);
        DzLog.getInstance().logClick(LogConstants.MODULE_YDQML, LogConstants.ZONE_YDQML_BJ, bookNote.bookId, paramsMap, null);

        CatalogInfo chapter = DBUtils.getCatalog(this, bookNote.bookId, bookNote.chapterId);

        if (chapter != null) {
            mPresenter.handleChapterClick(chapter, bookNote.startPos - 10);
        } else {
            finish();
            overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
        }
    }

    @Override
    public void onBookNoteItemLongClick(final BookMarkNew bookNote) {
        List<String> list = new ArrayList<>();
        list.add(getResources().getString(R.string.note_amend_idea));
        list.add(getResources().getString(R.string.note_delete_idea));
        list.add(getResources().getString(R.string.note_clean_all_idea));

        final CustomTxtMoreDialog dialog = new CustomTxtMoreDialog(getContext(), CustomDialogBusiness.STYLE_DIALOG_BOTTOM_CANCEL);
        dialog.setTitle(getResources().getString(R.string.hw_note));
        dialog.setData(list, -1, true);
        dialog.setCheckListener(new CustomDialogBusiness.OnCheckListener() {
            @Override
            public void clickConfirm(Object object) {
                int index = (int) object;
                final HashMap<String, String> paramsMap = new HashMap<>();
                CustomHintDialog hintDialog = new CustomHintDialog(getContext());
                switch (index) {
                    case 0:
                        paramsMap.put("action_type", "edit");
                        paramsMap.put("cid", bookNote.chapterId);
                        DzLog.getInstance().logClick(LogConstants.MODULE_YDQML, LogConstants.ZONE_YDQML_BJ, bookNote.bookId, paramsMap, null);
                        ReaderNoteActivity.launch(ReaderCatalogActivity.this, getRequestedOrientation(), bookNote);
                        break;
                    case 1:
                        popDeleteOneIdea(paramsMap, hintDialog, bookNote, dialog);

                        break;
                    case 2:
                        popDeleteAllIdea(paramsMap, hintDialog, bookNote, dialog);
                        break;
                    default:
                        break;
                }
                dialog.dismiss();
            }

            @Override
            public void clickCancel() {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void popDeleteOneIdea(final HashMap<String, String> paramsMap, CustomHintDialog hintDialog, final BookMarkNew bookNote, final CustomTxtMoreDialog dialog) {
        // 稍后哦确认翻译
        hintDialog.setTitle("是否删除此想法？");
        hintDialog.show();
        hintDialog.setCheckListener(new CustomDialogBusiness.OnCheckListener() {
            @Override
            public void clickConfirm(Object object) {
                paramsMap.put("action_type", "delete");
                paramsMap.put("cid", bookNote.chapterId);
                DzLog.getInstance().logClick(LogConstants.MODULE_YDQML, LogConstants.ZONE_YDQML_BJ, bookNote.bookId, paramsMap, null);

                BookMarkNew.deleteBookNote(ReaderCatalogActivity.this, bookNote, false);
                EventBusUtils.sendMessage(new BookNoteEvent(BookNoteEvent.TYPE_DELETE, bookNote));

                SyncBookMarkService.launch(ReaderCatalogActivity.this);
            }

            @Override
            public void clickCancel() {
                dialog.dismiss();
            }
        });
    }

    private void popDeleteAllIdea(final HashMap<String, String> paramsMap, CustomHintDialog hintDialog, final BookMarkNew bookNote, final CustomTxtMoreDialog dialog) {
        hintDialog.setTitle("是否删除所有想法？");
        hintDialog.show();
        hintDialog.setCheckListener(new CustomDialogBusiness.OnCheckListener() {
            @Override
            public void clickConfirm(Object object) {
                paramsMap.put("action_type", "clear");
                paramsMap.put("cid", bookNote.chapterId);
                DzLog.getInstance().logClick(LogConstants.MODULE_YDQML, LogConstants.ZONE_YDQML_BJ, bookNote.bookId, paramsMap, null);

                BookMarkNew.clearBookNote(ReaderCatalogActivity.this, bookNote.bookId);

                SyncBookMarkService.launch(ReaderCatalogActivity.this);

                EventBusUtils.sendMessage(new BookNoteEvent(BookNoteEvent.TYPE_CLEAR, bookNote));
            }

            @Override
            public void clickCancel() {
                dialog.dismiss();
            }
        });
        return;
    }

    /**
     * 显示选择pop
     *
     * @param position position
     * @param textView textView
     */
    public void showSelectPop(int position, TextView textView) {
        if (mCatalogSelectPopWindow == null) {
            mCatalogSelectPopWindow = new CatalogSelectPopWindow(this);
        }
        mCatalogSelectPopWindow.showAsDropDown(textView, 0, 0);
        mCatalogSelectPopWindow.initPosition(position);

    }

    public ReaderCatalogPresenter getPresenter() {
        return mPresenter;
    }


    @Override
    protected void setListener() {
        mCommonTitle.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThirdPartyLog.onEventValueOldClick(ReaderCatalogActivity.this, ThirdPartyLog.READER_UMENG_ID, ThirdPartyLog.CLOSE_VALUE, 1);
                finish();
                overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
            }
        });
        mCatalogSelectPopWindow.setBlockAction(new CatalogSelectPopWindow.BlockAction() {
            @Override
            public void onBlockClick(int position, BeanBlock beanBlock) {
                ReaderChapterFragment readerChapterFragment = mSubTabReaderCatalogAdapter.getReaderChapterFragment();
                if (null != readerChapterFragment) {
                    readerChapterFragment.setBlockClick(position);
                }
            }
        });

        layoutRoot.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v != null) {
            int id = v.getId();
            if (id == R.id.imageView_back) {
                ThirdPartyLog.onEventValueOldClick(this, ThirdPartyLog.READER_UMENG_ID, ThirdPartyLog.CLOSE_VALUE, 1);
                finish();
                overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
            } else if (id == R.id.layout_root) {
                finish();
                overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
            }
        }

    }


    @Override
    protected boolean needImmersionBar() {
        return true;
    }

}
