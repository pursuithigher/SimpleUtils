package com.dzbook.activity.comment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.dzbook.BaseTransparencyLoadActivity;
import com.dzbook.DzConstant;
import com.dzbook.activity.detail.BookCommentAdapter;
import com.dzbook.event.EventConstant;
import com.dzbook.event.EventMessage;
import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.mvp.UI.BookCommentPersonCenterUI;
import com.dzbook.mvp.presenter.BookCommentPersonCenterPresenter;
import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.utils.ListUtils;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.view.DianZhongCommonTitle;
import com.dzbook.view.PullLoadMoreRecycleLayout;
import com.dzbook.view.comment.CommentBaseView;
import com.dzbook.view.common.NetErrorTopView;
import com.dzbook.view.common.StatusView;
import com.dzbook.view.store.Pw1View;
import com.ishugui.R;
import com.iss.app.BaseActivity;

import java.io.Serializable;
import java.util.HashMap;

import hw.sdk.net.bean.bookDetail.BeanCommentInfo;
import hw.sdk.net.bean.bookDetail.BeanCommentMore;

import static com.dzbook.activity.comment.BookCommentItemDetailActivity.TAG_COMMENT_INFO;

/**
 * 点评中心
 *
 * @author Winzows on 2017/12/8.
 */

public class BookCommentPersonCenterActivity extends BaseTransparencyLoadActivity implements BookCommentPersonCenterUI {

    /**
     * tag
     */
    public static final String TAG = "BookCommentPersonCenterActivity";
    private StatusView statusView;
    private PullLoadMoreRecycleLayout pullLoadMoreRecyclerView;
    //    private DianzhongDefaultView defaultview_nonet, defaultviewRechargeEmpty;
    private BookCommentAdapter bookCommentAdapter;
    private BookCommentPersonCenterPresenter personCenterPresenter;

    private NetErrorTopView netErrorTopView;
    private LinearLayout netErrorTopLayout;
    private DianZhongCommonTitle mTitleView;
    private Pw1View pw1View;
    private boolean isShowTips;


    /**
     * 打开
     *
     * @param context context
     */
    public static void launch(Context context) {
        context.startActivity(new Intent(context, BookCommentPersonCenterActivity.class));
        BaseActivity.showActivity(context);
    }

    @Override
    public String getTagName() {
        return TAG;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_person_center);
    }

    @Override
    protected void initView() {
        netErrorTopLayout = findViewById(R.id.net_error_layout_view);
        statusView = findViewById(R.id.statusView);
        mTitleView = findViewById(R.id.commontitle);
        pullLoadMoreRecyclerView = findViewById(R.id.pullLoadMoreRecyclerView);
        pullLoadMoreRecyclerView.setLinearLayout();
        bookCommentAdapter = new BookCommentAdapter(getContext(), CommentBaseView.TYPE_ITEM_PERSON_CENTER, TAG);
        personCenterPresenter = new BookCommentPersonCenterPresenter(this);
        pw1View = new Pw1View(this);
    }

    @Override
    protected void initData() {
        statusView.showLoading();
        pullLoadMoreRecyclerView.setAdapter(bookCommentAdapter);
        request();
    }


    @Override
    protected void setListener() {
        pullLoadMoreRecyclerView.setOnPullLoadMoreListener(new PullLoadMoreRecycleLayout.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                initNetErrorStatus();
                if (NetworkUtils.getInstance().checkNet()) {
                    personCenterPresenter.requestData(BookCommentAdapter.LOAD_TYPE_REFRESH);
                    if (isShowTips) {
                        pullLoadMoreRecyclerView.removeFooterView(pw1View);
                        isShowTips = false;
                    }
                }
            }

            @Override
            public void onLoadMore() {
                initNetErrorStatus();
                if (NetworkUtils.getInstance().checkNet()) {
                    personCenterPresenter.requestData(BookCommentAdapter.LOAD_TYPE_LOADMORE);
                } else {
                    pullLoadMoreRecyclerView.setPullLoadMoreCompleted();
                }
            }
        });
        statusView.setNetErrorClickListener(new StatusView.NetErrorClickListener() {
            @Override
            public void onNetErrorEvent(View v) {
                request();
            }
        });
        mTitleView.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        bookCommentAdapter.setOnItemClickListener(new BookCommentAdapter.OnItemClickListener() {
            @Override
            public void onClick(BeanCommentInfo info) {
                Intent mIntent = new Intent(getContext(), BookCommentItemDetailActivity.class);
                mIntent.putExtra(TAG_COMMENT_INFO, info);
                mIntent.putExtra(DzConstant.BOOK_NAME, info.bookName);
                mIntent.putExtra(DzConstant.PAGE_TYPE, CommentBaseView.TYPE_ITEM_MY_COMMENT_DETAIL);
                startActivity(mIntent);
                BaseActivity.showActivity(getActivity());

                HashMap<String, String> map = new HashMap<>();
                map.put(LogConstants.KEY_RECHARGE_BID, info.bookId);
                map.put("book_name", DzConstant.BOOK_NAME);
                DzLog.getInstance().logClick(LogConstants.MODULE_WDDP, LogConstants.ZONE_PLXQ, "", map, null);
            }
        });
    }

    @Override
    public void fillData(BeanCommentMore value, int dataType) {
        if (null != value && !ListUtils.isEmpty(value.commentList)) {
            bookCommentAdapter.fillData(value.commentList, dataType);
            pullLoadMoreRecyclerView.setVisibility(View.VISIBLE);
        } else {
            onError();
        }
    }

    @Override
    public void onError() {
        pullLoadMoreRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                if (!NetworkUtils.getInstance().checkNet() && bookCommentAdapter != null && bookCommentAdapter.getItemCount() > 0) {
                    initNetErrorStatus();
                    pullLoadMoreRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    statusView.showNetError();
                    pullLoadMoreRecyclerView.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void showView() {
        pullLoadMoreRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                statusView.showSuccess();
                pullLoadMoreRecyclerView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void showEmpty() {
        pullLoadMoreRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                statusView.showEmpty(getContext().getResources().getString(R.string.hua_wei_no_comment));
                pullLoadMoreRecyclerView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void noMore() {
        pullLoadMoreRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                pullLoadMoreRecyclerView.setPullLoadMoreCompleted();
                pullLoadMoreRecyclerView.setHasMore(false);
                if (!isShowTips) {
                    pullLoadMoreRecyclerView.addFooterView(pw1View);
                    isShowTips = true;
                }
                //                ToastAlone.showShort(R.string.no_more_data);
            }
        });
    }

    @Override
    public void stopLoad() {
        pullLoadMoreRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                pullLoadMoreRecyclerView.setPullLoadMoreCompleted();
            }
        });
    }

    private void request() {
        if (NetworkUtils.getInstance().checkNet()) {
            statusView.showLoading();
            pullLoadMoreRecyclerView.setVisibility(View.GONE);
            personCenterPresenter.requestData(BookCommentAdapter.LOAD_TYPE_DEFAULT);
        } else {
            onError();
        }
    }

    @Override
    public void onEventMainThread(EventMessage event) {
        super.onEventMainThread(event);
        int requestCode = event.getRequestCode();
        Bundle bundle = event.getBundle();

        switch (requestCode) {
            case EventConstant.CODE_DELETE_BOOK_COMMENT:
                if (bundle != null) {
                    String commentId = bundle.getString("comment_id");
                    if (!TextUtils.isEmpty(commentId)) {
                        bookCommentAdapter.deleteItemByCommentId(commentId);
                    }
                }
                break;
            case EventConstant.CODE_COMMENT_BOOKDETAIL_SEND_SUCCESS:
                request();
                break;
            case EventConstant.CODE_DELETE_BOOK_IS_EMPTY:
                showEmpty();
                break;
            case EventConstant.CODE_PARISE_BOOK_COMMENT:
            case EventConstant.CODE_CANCEL_PARISE_BOOK_COMMENT:
                Serializable serializable;
                if (bundle != null && null != (serializable = bundle.getSerializable("commentInfo"))) {
                    if (serializable instanceof BeanCommentInfo) {
                        bookCommentAdapter.refreshComment((BeanCommentInfo) serializable);
                    }
                }
                break;
            default:
                break;
        }
    }


    @Override
    public void initNetErrorStatus() {
        if (!NetworkUtils.getInstance().checkNet() && bookCommentAdapter != null && bookCommentAdapter.getItemCount() > 0) {
            initNetView();
        } else {
            destoryNetView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (personCenterPresenter != null) {
            personCenterPresenter.destroy();
        }
    }

    private void initNetView() {
        if (netErrorTopView == null) {
            netErrorTopView = new NetErrorTopView(getContext());
            netErrorTopLayout.addView(netErrorTopView, 0, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DimensionPixelUtil.dip2px(getContext(), 48)));
        }
    }

    private void destoryNetView() {
        if (netErrorTopView != null) {
            netErrorTopLayout.removeView(netErrorTopView);
            netErrorTopView = null;
        }
    }
}
