package com.dzbook.activity.comment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dzbook.BaseTransparencyLoadActivity;
import com.dzbook.DzConstant;
import com.dzbook.activity.detail.BookCommentAdapter;
import com.dzbook.event.EventConstant;
import com.dzbook.event.EventMessage;
import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.mvp.UI.BookCommentDetailUI;
import com.dzbook.mvp.UI.BookCommentSendUI;
import com.dzbook.mvp.presenter.BookCommentPresenter;
import com.dzbook.mvp.presenter.BookCommentSendPresenter;
import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.utils.GlideImageLoadUtils;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.TypefaceUtils;
import com.dzbook.view.DianZhongCommonTitle;
import com.dzbook.view.PullLoadMoreRecycleLayout;
import com.dzbook.view.bookdetail.CommentEmpty;
import com.dzbook.view.comment.CommentBaseView;
import com.dzbook.view.common.NetErrorTopView;
import com.dzbook.view.common.StatusView;
import com.dzbook.view.store.Pw1View;
import com.ishugui.R;
import com.iss.app.BaseActivity;
import com.iss.view.common.ToastAlone;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import hw.sdk.net.bean.bookDetail.BeanCommentInfo;
import hw.sdk.net.bean.bookDetail.BeanCommentMore;

import static com.dzbook.activity.comment.BookCommentItemDetailActivity.TAG_COMMENT_INFO;

/**
 * 评论详情
 *
 * @author Winzows on 2017/11/27.
 */
public class BookCommentMoreActivity extends BaseTransparencyLoadActivity implements BookCommentDetailUI, BookCommentSendUI, View.OnClickListener {
    /**
     * tag
     */
    public static final String TAG = "BookCommentMoreActivity";
    private Intent mIntent;
    private ImageView ivCover;
    private PullLoadMoreRecycleLayout mPullLoadMoreLayout;
    private BookCommentAdapter commentAdapter;
    private BookCommentPresenter presenter;
    private BookCommentSendPresenter commentSendPresenter;
    private String mBookId;
    private String mBookName;
    private View loadMorePb;
    private StatusView statusView;
    private View reBook;
    private TextView tvBookName, tvAuthor, tvSendComment;
    private NetErrorTopView netErrorTopView;
    private LinearLayout netErrorTopLayout;

    private DianZhongCommonTitle mTitleView;
    private String bookCover;
    private boolean isShowTips;
    private String bookAuthor;
    private View inflate;
    private CommentEmpty rlCommentEmpty;
    private Pw1View pw1View;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_detail);
    }

    @Override
    protected void initView() {
        netErrorTopLayout = findViewById(R.id.net_error_layout_view);
        mPullLoadMoreLayout = findViewById(R.id.pullLoadMoreRecyclerView);
        loadMorePb = findViewById(R.id.load_more_pb);
        statusView = findViewById(R.id.statusView);
        mPullLoadMoreLayout.setLinearLayout();
        presenter = new BookCommentPresenter(this);
        commentSendPresenter = new BookCommentSendPresenter(this);
        mTitleView = findViewById(R.id.commontitle);
        rlCommentEmpty = findViewById(R.id.rl_comment_empty);
        rlCommentEmpty.setSendPresenter(commentSendPresenter);
        inflate = View.inflate(this, R.layout.view_more_comment_title, null);
        reBook = inflate.findViewById(R.id.re_book);
        ivCover = inflate.findViewById(R.id.iv_cover);
        tvBookName = inflate.findViewById(R.id.tv_bookName);
        tvAuthor = inflate.findViewById(R.id.tv_author);
        tvSendComment = inflate.findViewById(R.id.tv_sendComment);
        TextView tvBookComment = inflate.findViewById(R.id.tv_book_comment);
        pw1View = new Pw1View(this);


        TypefaceUtils.setHwChineseMediumFonts(tvBookName);
        TypefaceUtils.setHwChineseMediumFonts(tvBookComment);
        TypefaceUtils.setHwChineseMediumFonts(tvSendComment);
    }

    private void setBooInfo() {
        if (!TextUtils.isEmpty(bookCover)) {
            GlideImageLoadUtils.getInstanse().glideImageLoadFromUrl(getContext(), ivCover, bookCover, 0);
        }
        if (!TextUtils.isEmpty(mBookName)) {
            tvBookName.setText(mBookName);
        }
        if (!TextUtils.isEmpty(bookAuthor)) {
            tvAuthor.setText(bookAuthor + " " + getResources().getString(R.string.str_verb_writing));
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initData() {
        mIntent = getIntent();
        if (mIntent != null) {
            mBookName = mIntent.getStringExtra(DzConstant.BOOK_NAME);
            mBookId = mIntent.getStringExtra(DzConstant.BOOK_ID);
            bookCover = mIntent.getStringExtra(DzConstant.BOOK_COVER);
            bookAuthor = mIntent.getStringExtra(DzConstant.BOOK_AUTHOR);
            setBooInfo();
            rlCommentEmpty.setBookInfo(bookCover, mBookName, bookAuthor, mBookId);
        }
        commentAdapter = new BookCommentAdapter(this, CommentBaseView.TYPE_ITEM_MORE_COMMENT, TAG);
        mPullLoadMoreLayout.setAdapter(commentAdapter);
        mPullLoadMoreLayout.addHeaderView(inflate);
        commentAdapter.setTitleName(mBookName);
        if (TextUtils.isEmpty(mBookId)) {
            ToastAlone.showLong(R.string.comment_error);
            return;
        }
        request();
    }

    @Override
    protected void setListener() {
        mTitleView.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tvSendComment.setOnClickListener(this);
        statusView.setNetErrorClickListener(new StatusView.NetErrorClickListener() {
            @Override
            public void onNetErrorEvent(View v) {
                statusView.setVisibility(View.GONE);
                request();
            }
        });
        mPullLoadMoreLayout.setOnPullLoadMoreListener(new PullLoadMoreRecycleLayout.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                initNetErrorStatus();
                if (NetworkUtils.getInstance().checkNet()) {
                    presenter.requestData(mBookId, BookCommentAdapter.LOAD_TYPE_REFRESH, false);
                    if (isShowTips) {
                        mPullLoadMoreLayout.removeFooterView(pw1View);
                        isShowTips = false;
                    }
                }
            }

            @Override
            public void onLoadMore() {
                initNetErrorStatus();
                if (NetworkUtils.getInstance().checkNet()) {
                    presenter.requestData(mBookId, BookCommentAdapter.LOAD_TYPE_LOADMORE, false);
                } else {
                    mPullLoadMoreLayout.setPullLoadMoreCompleted();
                }
            }
        });

        commentAdapter.setOnItemClickListener(new BookCommentAdapter.OnItemClickListener() {
            @Override
            public void onClick(BeanCommentInfo info) {
                Intent intent = new Intent(getContext(), BookCommentItemDetailActivity.class);
                intent.putExtra(TAG_COMMENT_INFO, info);
                intent.putExtra(DzConstant.BOOK_NAME, mBookName);
                intent.putExtra(DzConstant.PAGE_TYPE, CommentBaseView.TYPE_ITEM_COMMENT_DETAIL);
                startActivity(intent);
                BaseActivity.showActivity(getActivity());

                HashMap<String, String> map = new HashMap<>();
                map.put(LogConstants.KEY_RECHARGE_BID, mBookId + "");
                map.put("book_name", mBookName + "");
                DzLog.getInstance().logClick(LogConstants.MODULE_QBPL, LogConstants.ZONE_PLXQ, "", map, null);
            }
        });
    }


    private void request() {
        if (NetworkUtils.getInstance().checkNet()) {
            loadMorePb.setVisibility(View.VISIBLE);
            presenter.requestData(mBookId, BookCommentAdapter.LOAD_TYPE_DEFAULT, false);
        } else {
            onError();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void fillData(BeanCommentMore value, int dataType) {
        if (value != null) {
            ArrayList<BeanCommentInfo> bookCommentInfo = value.commentList;
            if (bookCommentInfo != null && bookCommentInfo.size() > 0) {
                commentAdapter.fillData(value.commentList, dataType);
                setBooInfo();
            }
        }
    }

    @Override
    public void onError() {
        mPullLoadMoreLayout.post(new Runnable() {
            @Override
            public void run() {
                if (!NetworkUtils.getInstance().checkNet() && commentAdapter != null && commentAdapter.getItemCount() > 0) {
                    initNetErrorStatus();
                } else {
                    statusView.showNetError();
                    reBook.setVisibility(View.GONE);
                    mPullLoadMoreLayout.setVisibility(View.GONE);
                    loadMorePb.setVisibility(View.INVISIBLE);
                    tvSendComment.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    @Override
    public void showView() {
        mPullLoadMoreLayout.post(new Runnable() {
            @Override
            public void run() {
                loadMorePb.setVisibility(View.GONE);
                rlCommentEmpty.setVisibility(View.GONE);
                statusView.setVisibility(View.GONE);
                reBook.setVisibility(View.VISIBLE);
                mPullLoadMoreLayout.setVisibility(View.VISIBLE);
                tvSendComment.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void showEmpty() {
        mPullLoadMoreLayout.post(new Runnable() {
            @Override
            public void run() {
                reBook.setVisibility(View.GONE);
                loadMorePb.setVisibility(View.GONE);
                rlCommentEmpty.setVisibility(View.VISIBLE);
                tvSendComment.setVisibility(View.VISIBLE);

            }
        });

    }

    @Override
    public void stopLoad() {
        mPullLoadMoreLayout.post(new Runnable() {
            @Override
            public void run() {
                mPullLoadMoreLayout.setRefreshing(false);
                mPullLoadMoreLayout.setPullLoadMoreCompleted();
            }
        });

    }

    @Override
    public void noMore() {
        mPullLoadMoreLayout.post(new Runnable() {
            @Override
            public void run() {
                mPullLoadMoreLayout.setPullLoadMoreCompleted();
                mPullLoadMoreLayout.setHasMore(false);
                if (!isShowTips) {
                    mPullLoadMoreLayout.addFooterView(pw1View);
                    isShowTips = true;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_sendComment:
                commentSendPresenter.checkCommentStatus(BookCommentMoreActivity.this, mBookId, mBookName, BookCommentSendActivity.TYPE_SEND);
                HashMap<String, String> map = new HashMap<>();
                map.put(LogConstants.KEY_RECHARGE_BID, mBookId + "");
                map.put("book_name", mBookName + "");
                map.put("type", "1");
                DzLog.getInstance().logClick(LogConstants.MODULE_QBPL, LogConstants.ZONE_FSPL, "", map, "");
                break;
            default:
                break;

        }
    }

    @Override
    public void onEventMainThread(EventMessage event) {
        super.onEventMainThread(event);
        String type = event.getType();
        int requestCode = event.getRequestCode();
        Bundle bundle = event.getBundle();

        if (TextUtils.equals(EventConstant.TYPE_BOOK_COMMENT, type)) {
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
                case EventConstant.CODE_DELETE_BOOK_IS_EMPTY:
                    showEmpty();
                    break;
                case EventConstant.CODE_COMMENT_BOOKDETAIL_SEND_SUCCESS:
                    request();
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
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.destroy();
        }
    }

    /**
     * 公用launche方法  bookID不为空
     *
     * @param context  context
     * @param bookId   bookId
     * @param bookName bookName
     * @param author   author
     * @param coverWap coverWap
     */
    public static void launch(Context context, @NonNull String bookId, String bookName, String author, String coverWap) {
        Intent intent = new Intent(context, BookCommentMoreActivity.class);
        intent.putExtra(DzConstant.BOOK_NAME, bookName + "");
        intent.putExtra(DzConstant.BOOK_ID, bookId);
        intent.putExtra(DzConstant.BOOK_AUTHOR, author);
        intent.putExtra(DzConstant.BOOK_COVER, coverWap);
        context.startActivity(intent);
        BaseActivity.showActivity(context);
    }

    @Override
    public void notifyBookDetailRefresh(ArrayList<BeanCommentInfo> infoList, String bookId) {

    }

    @Override
    public void isShowNotNetDialog() {
        BookCommentMoreActivity.this.showNotNetDialog();
    }

    private void initNetView() {
        if (netErrorTopView == null) {
            netErrorTopView = new NetErrorTopView(getContext());
            netErrorTopLayout.addView(netErrorTopView, 0, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DimensionPixelUtil.dip2px(getContext(), 48)));
        }
    }

    private void initNetErrorStatus() {
        if (!NetworkUtils.getInstance().checkNet() && commentAdapter != null && commentAdapter.getItemCount() > 0) {
            initNetView();
        } else {
            destoryNetView();
        }
    }


    private void destoryNetView() {
        if (netErrorTopView != null) {
            netErrorTopLayout.removeView(netErrorTopView);
            netErrorTopView = null;
        }
    }

    @Override
    public String getTagName() {
        return TAG;
    }

}
