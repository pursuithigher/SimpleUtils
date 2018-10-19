package com.dzbook.view.reader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ToggleButton;

import com.dzbook.activity.comment.BookCommentMoreActivity;
import com.dzbook.activity.detail.BookDetailActivity;
import com.dzbook.activity.reader.ReaderActivity;
import com.dzbook.database.bean.BookInfo;
import com.dzbook.database.bean.BookMarkNew;
import com.dzbook.mvp.presenter.ReaderPresenter;
import com.dzbook.r.c.AkDocInfo;
import com.dzbook.r.util.ConvertUtils;
import com.dzbook.r.util.HwUtils;
import com.dzbook.service.SyncBookMarkService;
import com.dzbook.utils.DBUtils;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.ShareUtils;
import com.dzbook.utils.ThirdPartyLog;
import com.dzbook.utils.hw.LoginUtils;
import com.ishugui.R;
import com.iss.app.BaseActivity;
import com.iss.view.common.ToastAlone;

/**
 * ReaderNewTitle
 *
 * @author Created by wxliao on 18/4/21.
 */

public class ReaderNewTitle extends LinearLayout implements View.OnClickListener {
    private ImageView imageviewMore;
    private ImageView imageviewDownload;
    private ImageView imageviewComment;
    private PopupWindow popupWindow;
    private ToggleButton togglebuttonMark;

    /**
     * 构造
     *
     * @param context context
     */
    public ReaderNewTitle(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public ReaderNewTitle(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_reader_new_title, this, true);
        boolean isInMultiWindow = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            isInMultiWindow = ((Activity) getContext()).isInMultiWindowMode();
        }
        if (!isInMultiWindow) {
            int height = HwUtils.getStatusBarHeight(context);
            setPadding(0, height, 0, 0);
        }

        setOrientation(VERTICAL);
        findViewById(R.id.imageView_back).setOnClickListener(this);
        imageviewDownload = findViewById(R.id.imageView_download);
        imageviewDownload.setOnClickListener(this);
        imageviewComment = findViewById(R.id.imageView_comment);
        imageviewComment.setOnClickListener(this);
        imageviewMore = findViewById(R.id.imageView_more);
        imageviewMore.setOnClickListener(this);
    }

    /**
     * 获取Presenter
     *
     * @return Presenter
     */
    public ReaderPresenter getPresenter() {
        ReaderActivity activity = (ReaderActivity) getContext();
        return activity.getPresenter();
    }

    private void finish() {
        ReaderPresenter presenter = getPresenter();
        if (presenter == null) {
            return;
        }
        presenter.onBackPress(true);
    }

    private void download() {
        ((ReaderActivity) getContext()).hideMenuPanel(true);
        ReaderPresenter presenter = getPresenter();
        if (presenter == null) {
            return;
        }
        AkDocInfo docInfo = presenter.refreshDocument();

        final BookInfo bookInfo = DBUtils.findByBookId(getContext(), docInfo.bookId);
        if (bookInfo.bookfrom == 2) {
            ToastAlone.showShort(R.string.reader_download_local);
            return;
        }

        presenter.download(docInfo.bookId, docInfo.chapterId);
    }

    private void comment() {
        ReaderPresenter presenter = getPresenter();
        if (presenter == null) {
            return;
        }
        AkDocInfo docInfo = presenter.refreshDocument();
        if (docInfo != null && !TextUtils.isEmpty(docInfo.bookId)) {
            BookInfo bookInfo = DBUtils.findByBookId(getContext(), docInfo.bookId);
            if (null != bookInfo) {
                BookCommentMoreActivity.launch(getContext(), bookInfo.bookid, bookInfo.bookname, bookInfo.author, bookInfo.coverurl);
            }
        }
    }


    private void more() {
        showPopup();

        ReaderPresenter presenter = getPresenter();
        if (presenter == null) {
            return;
        }
        AkDocInfo docInfo = presenter.refreshDocument();
        boolean isMarked = false;
        BookMarkNew bookMarkNew = BookMarkNew.createBookMark(getContext(), docInfo);
        if (bookMarkNew != null) {
            isMarked = BookMarkNew.isMarked(getContext(), bookMarkNew);
        }
        togglebuttonMark.setChecked(isMarked);
    }

    //    private boolean isMarked(AkDocInfo docInfo) {
    //        if (null == docInfo) {
    //            return false;
    //        }
    //        BookMark bean = new BookMark();
    //        bean.path = docInfo.path;
    //        bean.startPos = docInfo.currentPos;
    //        return DBUtils.isMarked(getContext(), bean);
    //    }

    //    private BookMarkNew createBookMark(AkDocInfo docInfo) {
    //        if (docInfo == null) {
    //            return null;
    //        }
    //        BookMarkNew beanNew = new BookMarkNew();
    //        beanNew.type = BookMarkNew.TYPE_MARK;
    //        beanNew.userId = SpUtil.getinstance(getContext()).getUserID();
    //
    //        beanNew.bookId = docInfo.bookId;
    //        beanNew.bookName = docInfo.bookName;
    //        beanNew.chapterId = docInfo.chapterId;
    //        beanNew.chapterName = docInfo.chapterName;
    //
    //        beanNew.startPos = docInfo.currentPos;
    //        beanNew.endPos = 0;
    //        beanNew.percent = getPercentStr(docInfo.percent / 100);
    //        if (null != docInfo.pageText) {
    //            beanNew.showText = docInfo.pageText.length() > 50 ? docInfo.pageText.substring(0, 50) : docInfo.pageText;
    //        }
    //        beanNew.noteText = "";
    //        beanNew.updateTime = System.currentTimeMillis();
    //        return beanNew;
    //    }

    private void showPopup() {
        if (popupWindow == null) {
            View contentView = LayoutInflater.from(getContext()).inflate(R.layout.popup_reader_more, null);

            popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            popupWindow.setBackgroundDrawable(new ColorDrawable());
            popupWindow.setOutsideTouchable(true);
            popupWindow.setAnimationStyle(R.style.PopupReaderAnimation);
            togglebuttonMark = contentView.findViewById(R.id.toggleButton_mark);
            View textViewBookDetail = contentView.findViewById(R.id.textView_bookDetail);
            View textViewShareBook = contentView.findViewById(R.id.textView_shareBook);
            View shareLine = contentView.findViewById(R.id.share_line);
            View detailLine = contentView.findViewById(R.id.detail_line);
            textViewShareBook.setOnClickListener(this);
            contentView.findViewById(R.id.textView_bookDetail).setOnClickListener(this);
            togglebuttonMark.setOnClickListener(this);
            if (ShareUtils.isSupportShare()) {
                textViewShareBook.setVisibility(VISIBLE);
                shareLine.setVisibility(VISIBLE);
            } else {
                textViewShareBook.setVisibility(GONE);
                shareLine.setVisibility(GONE);
            }

            AkDocInfo docInfo = getAkDocInfo();
            if (docInfo != null && docInfo.isStoreBook) {
                textViewBookDetail.setVisibility(View.VISIBLE);
                detailLine.setVisibility(View.VISIBLE);
            } else {
                textViewBookDetail.setVisibility(View.GONE);
                detailLine.setVisibility(View.GONE);
            }

            if (textViewShareBook.getVisibility() == GONE && textViewBookDetail.getVisibility() == GONE) {
                togglebuttonMark.setBackgroundDrawable(getResources().getDrawable(R.drawable.item_read_selector_round_rect));
            }
        }
        int[] location = new int[2];
        getLocationInWindow(location);
        float num1 = 16;
        float num2 = 4;
        int rightPadding = ConvertUtils.dp2px(getContext(), num1);
        int topPadding = ConvertUtils.dp2px(getContext(), num2);
        popupWindow.showAtLocation(this, Gravity.TOP | Gravity.RIGHT, rightPadding, location[1] + getHeight() + topPadding);
    }

    private void hidePopup() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    private void shareBook() {
        hidePopup();
        ((ReaderActivity) getContext()).hideMenuPanel(true);
        ReaderPresenter presenter = getPresenter();
        if (presenter == null) {
            return;
        }
        AkDocInfo docInfo = presenter.refreshDocument();
        if (docInfo == null || TextUtils.isEmpty(docInfo.bookId)) {
            return;
        }
        ShareUtils.gotoShare((ReaderActivity) getContext(), docInfo.bookId, ShareUtils.DIALOG_SHOW_FROM_READER_SHARE);
    }

    private void bookDetail() {
        hidePopup();
        ((ReaderActivity) getContext()).hideMenuPanel(true);

        ReaderPresenter presenter = getPresenter();
        if (presenter == null) {
            return;
        }
        AkDocInfo docInfo = presenter.refreshDocument();
        if (docInfo == null) {
            return;
        }
        ThirdPartyLog.onEvent(getContext(), ThirdPartyLog.READ_DETAIL);
        ThirdPartyLog.onEventValueOldClick(getContext(), ThirdPartyLog.READER_UMENG_ID, ThirdPartyLog.BOOKDETAIL_VALUE, 1);


        Intent intent = new Intent();
        intent.setClass(getContext(), BookDetailActivity.class);
        intent.putExtra("bookId", docInfo.bookId);
        getContext().startActivity(intent);
        BaseActivity.showActivity(getContext());
    }

    private void resetMark() {
        hidePopup();
        ReaderPresenter presenter = getPresenter();
        if (presenter == null) {
            return;
        }
        AkDocInfo docInfo = presenter.refreshDocument();
        if (docInfo == null) {
            return;
        }

        BookMarkNew bean = BookMarkNew.createBookMark(getContext(), docInfo);
        if (bean == null) {
            return;
        }


        boolean isChecked = togglebuttonMark.isChecked();
        if (isChecked) {
            BookMarkNew.addBookMark(getContext(), bean);
            ThirdPartyLog.onEventValueOldClick(getContext(), ThirdPartyLog.READER_UMENG_ID, ThirdPartyLog.ADD_BOOKMARK_VALUE, 1);
            ToastAlone.showShort(R.string.toast_add_bookmark_success);

            SyncBookMarkService.launch(getContext());
        } else {
            BookMarkNew.deleteBookMark(getContext(), bean, false);
            ThirdPartyLog.onEventValueOldClick(getContext(), ThirdPartyLog.READER_UMENG_ID, ThirdPartyLog.DELETE_BOOKMARK_VALUE, 1);
            ToastAlone.showShort(R.string.toast_delete_bookmark_success);

            SyncBookMarkService.launch(getContext());
        }
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.imageView_back) {
            finish();
        } else if (id == R.id.imageView_download) {
            if (!NetworkUtils.getInstance().checkNet()) {
                if (getContext() instanceof BaseActivity) {
                    ((BaseActivity) getContext()).showNotNetDialog();
                }
            } else {
                LoginUtils.getInstance().forceLoginCheck(getContext(), new LoginUtils.LoginCheckListener() {
                    @Override
                    public void loginComplete() {
                        download();
                    }
                });
            }
        } else if (id == R.id.imageView_comment) {
            comment();
        } else if (id == R.id.imageView_more) {
            more();
        } else if (id == R.id.textView_shareBook) {
            shareBook();
        } else if (id == R.id.textView_bookDetail) {
            bookDetail();
        } else if (id == R.id.toggleButton_mark) {
            resetMark();
        }
    }

    private AkDocInfo getAkDocInfo() {
        ReaderPresenter presenter = getPresenter();
        if (presenter == null) {
            return null;
        }
        return presenter.refreshDocument();
    }

    /**
     * 刷新
     *
     * @param docInfo docInfo
     */
    public void refresh(AkDocInfo docInfo) {
        if (docInfo.isStoreBook) {
            imageviewDownload.setVisibility(View.VISIBLE);
            imageviewComment.setVisibility(View.VISIBLE);
        } else {
            imageviewDownload.setVisibility(View.INVISIBLE);
            imageviewComment.setVisibility(View.INVISIBLE);
        }
    }
}
