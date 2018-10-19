package com.dzbook.view.comment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dzbook.DzConstant;
import com.dzbook.activity.comment.BookCommentItemDetailActivity;
import com.dzbook.activity.comment.BookCommentMoreActivity;
import com.dzbook.activity.comment.BookCommentSendActivity;
import com.dzbook.activity.detail.BookCommentAdapter;
import com.dzbook.activity.detail.BookDetailActivity;
import com.dzbook.event.EventBusUtils;
import com.dzbook.event.EventConstant;
import com.dzbook.event.EventMessage;
import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.mvp.UI.BookCommentSendUI;
import com.dzbook.mvp.presenter.BookCommentSendPresenter;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.TypefaceUtils;
import com.ishugui.R;
import com.iss.app.BaseActivity;
import com.iss.view.common.ToastAlone;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import hw.sdk.net.bean.bookDetail.BeanCommentInfo;

import static com.dzbook.activity.comment.BookCommentItemDetailActivity.TAG_COMMENT_INFO;

/**
 * 书籍详情页的评论详情
 *
 * @author Winzows on 2017/12/6.
 */

public class CommentBookDetailView extends RelativeLayout implements View.OnClickListener, BookCommentSendUI {
    private static final String TAG = "CommentBookDetailView";
    private Context context;
    private TextView tvSendcomment, tvMorecomment;
    private RecyclerView recyclerView;
    private RelativeLayout rlCommentEmpty;
    private BookCommentAdapter commentAdapter;
    private String bookId, bookName, author, coverWap;

    private BookCommentSendPresenter sendPresenter;

    /**
     * 构造
     *
     * @param context context
     */
    public CommentBookDetailView(Context context) {
        super(context);
        this.context = context;
        initView();
        initListener();
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public CommentBookDetailView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
        initListener();
    }

    /**
     * 构造
     *
     * @param context      context
     * @param attrs        attrs
     * @param defStyleAttr defStyleAttr
     */
    public CommentBookDetailView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
        initListener();
    }

    private void initView() {
        sendPresenter = new BookCommentSendPresenter(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);

        LayoutInflater.from(context).inflate(R.layout.view_comment_bookdetail, this, true);
        tvSendcomment = findViewById(R.id.tv_sendComment);
        tvMorecomment = findViewById(R.id.tv_moreComment);
        TypefaceUtils.setHwChineseMediumFonts(tvMorecomment);
        TextView tvCommentTitle = findViewById(R.id.tv_comment_title);
        recyclerView = findViewById(R.id.recycler_view);
        rlCommentEmpty = findViewById(R.id.rl_comment_empty);
        commentAdapter = new BookCommentAdapter(getContext(), CommentBaseView.TYPE_ITEM_BOOKDETAIL, BookDetailActivity.TAG);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(commentAdapter);

        EventBusUtils.register(this);
        TypefaceUtils.setHwChineseMediumFonts(tvSendcomment);
        TypefaceUtils.setHwChineseMediumFonts(tvCommentTitle);
    }


    private void initListener() {
        tvSendcomment.setOnClickListener(this);
        tvMorecomment.setOnClickListener(this);
        rlCommentEmpty.setOnClickListener(this);
        commentAdapter.setOnItemClickListener(new BookCommentAdapter.OnItemClickListener() {
            @Override
            public void onClick(BeanCommentInfo info) {
                Intent mIntent = new Intent(getContext(), BookCommentItemDetailActivity.class);
                mIntent.putExtra(TAG_COMMENT_INFO, info);
                mIntent.putExtra(DzConstant.BOOK_NAME, bookName);
                mIntent.putExtra(DzConstant.PAGE_TYPE, CommentBaseView.TYPE_ITEM_COMMENT_DETAIL);
                getContext().startActivity(mIntent);
                BaseActivity.showActivity(getContext());

                HashMap<String, String> map = getUploadMap();
                DzLog.getInstance().logClick(LogConstants.MODULE_SJXQ, LogConstants.ZONE_PLXQ, "", map, null);
            }
        });
    }

    private HashMap<String, String> getUploadMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put(LogConstants.KEY_RECHARGE_BID, bookId);
        map.put("book_name", bookName);
        return map;
    }

    /**
     * 设置数据
     *
     * @param dataList  dataList
     * @param bookId1   bookId
     * @param bookName1 bookName
     * @param author1   author
     * @param coverWap1 coverWap
     */
    public void bindData(ArrayList<BeanCommentInfo> dataList, String bookId1, String bookName1, String author1, String coverWap1) {
        if (dataList != null && dataList.size() > 0 && !TextUtils.isEmpty(bookId1)) {
            commentAdapter.fillData(dataList, 1);
            tvMorecomment.setVisibility(VISIBLE);
            recyclerView.setVisibility(VISIBLE);
            rlCommentEmpty.setVisibility(GONE);
            if (dataList.size() == 1) {
                tvMorecomment.setVisibility(GONE);
            }
        } else {
            tvMorecomment.setVisibility(GONE);
            recyclerView.setVisibility(GONE);
            rlCommentEmpty.setVisibility(VISIBLE);
        }
        this.bookId = bookId1;
        this.bookName = bookName1;
        this.author = author1;
        this.coverWap = coverWap1;
        commentAdapter.setTitleName(bookName1);
    }

    @Override
    public String getTagName() {
        return TAG;
    }

    @Override
    public void onClick(View v) {
        if (v != null) {
            switch (v.getId()) {
                case R.id.tv_sendComment:
                case R.id.rl_comment_empty://发送评论
                    if (!NetworkUtils.getInstance().checkNet()) {
                        if (getContext() instanceof BaseActivity) {
                            ((BaseActivity) getContext()).showNotNetDialog();
                        }
                    } else {
                        if (!TextUtils.isEmpty(bookId)) {
                            sendPresenter.checkCommentStatus((Activity) context, bookId, bookName + "", BookCommentSendActivity.TYPE_SEND);
                        } else {
                            ToastAlone.showShort(R.string.comment_send_comment_error);
                        }
                        HashMap<String, String> map = getUploadMap();
                        DzLog.getInstance().logClick(LogConstants.MODULE_SJXQ, LogConstants.ZONE_FSPL, "", map, null);
                    }
                    break;
                case R.id.tv_moreComment:
                    if (!TextUtils.isEmpty(bookId)) {
                        BookCommentMoreActivity.launch(getContext(), bookId, bookName, author, coverWap);
                    } else {
                        ToastAlone.showShort(R.string.comment_error);
                    }

                    HashMap<String, String> map2 = getUploadMap();
                    DzLog.getInstance().logClick(LogConstants.MODULE_SJXQ, LogConstants.ZONE_QBPL, "", map2, null);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBusUtils.unregister(this);
    }

    /**
     * 处理事件
     *
     * @param event event
     */
    public void onEventMainThread(EventMessage event) {
        String type = event.getType();
        int requestCode = event.getRequestCode();
        Bundle bundle = event.getBundle();
        if (TextUtils.equals(EventConstant.TYPE_BOOK_COMMENT, type) || TextUtils.equals(EventConstant.TYPE_BOOK_DETAIL, type)) {
            switch (requestCode) {
                case EventConstant.CODE_DELETE_BOOK_COMMENT:
                    //删除书籍评论
                    if (bundle != null) {
                        String commentId = bundle.getString("comment_id");
                        if (!TextUtils.isEmpty(commentId)) {
                            commentAdapter.deleteItemByCommentId(commentId);
                        }
                    }
                    break;
                case EventConstant.CODE_PARISE_BOOK_COMMENT:
                case EventConstant.CODE_CANCEL_PARISE_BOOK_COMMENT:
                    Serializable serializable;
                    if (bundle != null && null != (serializable = bundle.getSerializable("commentInfo"))) {
                        if (serializable instanceof BeanCommentInfo) {
                            commentAdapter.refreshComment((BeanCommentInfo) serializable);
                        }
                    }
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public Activity getActivity() {
        return null;
    }

    @Override
    public void notifyBookDetailRefresh(ArrayList<BeanCommentInfo> infoList, String id) {

    }

    @Override
    public void isShowNotNetDialog() {
    }

    @Override
    public void showDialogByType(int loadingType) {

    }

    @Override
    public void showDialogByType(int loadingType, CharSequence text) {

    }

    @Override
    public void dissMissDialog() {

    }

    @Override
    public void showMessage(String message) {

    }

    @Override
    public void showMessage(int resId) {

    }

    @Override
    public boolean isNetworkConnected() {
        return false;
    }
}
