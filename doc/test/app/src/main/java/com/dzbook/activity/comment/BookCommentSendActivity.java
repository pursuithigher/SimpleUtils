package com.dzbook.activity.comment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dzbook.BaseTransparencyLoadActivity;
import com.dzbook.DzConstant;
import com.dzbook.event.EventBusUtils;
import com.dzbook.event.EventConstant;
import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.lib.utils.StringUtil;
import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.mvp.UI.BookCommentSendUI;
import com.dzbook.mvp.presenter.BookCommentSendPresenter;
import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.utils.MathUtils;
import com.dzbook.view.DianZhongCommonTitle;
import com.dzbook.view.comment.CommentRatingBarView;
import com.ishugui.R;
import com.iss.app.BaseActivity;
import com.iss.view.common.ToastAlone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import hw.sdk.net.bean.bookDetail.BeanCommentInfo;

/**
 * 书评编辑
 *
 * @author Winzows on 2017/11/30.
 */
@SuppressLint("SetTextI18n")
public class BookCommentSendActivity extends BaseTransparencyLoadActivity implements BookCommentSendUI {

    /**
     * TYPE_EDIT = 0x002
     */
    public static final int TYPE_EDIT = 0x002;
    /**
     * TYPE_SEND = 0x001
     */
    public static final int TYPE_SEND = 0x001;
    private static final String TAG = "BookCommentSendActivity";
    private static final String TAG_SCORE = "tag_score";
    private static final String TAG_CONTENT = "tag_content";
    private static final String TAG_COMMENT_ID = "tag_comment_id";
    private static final String TAG_COMMENT_TYPE = "tag_comment_type";
    private static final float RIGHT_ALPHE = 0.4f;

    private TextView textViewTitleNum, tvShowScore, tvLimit;
    private EditText editText;
    private CommentRatingBarView ratingBar;
    private float commentScore = 10;
    private String mBookId;

    private BookCommentSendPresenter presenter;
    private String bookName;
    private String commentId;
    private int type = -1;
    private RelativeLayout rlCommentLayout;

    private boolean isRedBg;

    private DianZhongCommonTitle mTitleView;
    private LinkedList<String> list;
    private int editMaxHeight = 0;
    private int editMinHeight = 0;
    private Window mWindow;
    private int mDecorViewVisibleHeight;
    private View mDecorView;
    private int toggleMinHeight;

    @Override
    public String getTagName() {
        return TAG;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_send);
    }

    @Override
    protected void initView() {
        mWindow = getWindow();
        if (mWindow != null) {
            mWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            mDecorView = mWindow.getDecorView();
        }
        tvLimit = findViewById(R.id.textView_limit);
        editText = findViewById(R.id.edit_text);
        ratingBar = findViewById(R.id.ratingbar);
        textViewTitleNum = findViewById(R.id.textView_titlenum);
        tvShowScore = findViewById(R.id.tv_show_score);
        rlCommentLayout = findViewById(R.id.rl_comment_layout);
        mTitleView = findViewById(R.id.commontitle);
        mTitleView.setImageViewRightOperAlphe(RIGHT_ALPHE, false);
        presenter = new BookCommentSendPresenter(this);
        editMinHeight = DimensionPixelUtil.dip2px(getContext(), 120);
        editMaxHeight = DimensionPixelUtil.dip2px(getContext(), 140);
        toggleMinHeight = editMinHeight;
    }


    @Override
    protected void initData() {
        list = new LinkedList<>();
        list.add(getResources().getString(R.string.first_score));
        list.add(getResources().getString(R.string.second_score));
        list.add(getResources().getString(R.string.third_score));
        list.add(getResources().getString(R.string.four_score));
        list.add(getResources().getString(R.string.five_score));
        Intent getIntent = getIntent();
        if (getIntent != null) {
            bookName = getIntent.getStringExtra(DzConstant.BOOK_NAME);
            commentId = getIntent.getStringExtra(TAG_COMMENT_ID);
            type = getIntent.getIntExtra(TAG_COMMENT_TYPE, -1);
            String content = getIntent.getStringExtra(TAG_CONTENT);
            if (!TextUtils.isEmpty(content)) {
                editText.setText(content);
                textViewTitleNum.setText(Math.min(500, content.length()) + "/500");
            }

            float oldScore = getIntent.getFloatExtra(TAG_SCORE, 0);
            if (oldScore < 0) {
                oldScore = 0;
            }
            if (oldScore > 10) {
                oldScore = 10;
            }

            if (oldScore == 0) {
                ratingBar.setStar(5);
                tvShowScore.setText(getResources().getString(R.string.five_score));
            } else {
                ratingBar.setStar(MathUtils.meg(oldScore / 2));
                commentScore = oldScore;

                int showScore = 0;
                showScore = (int) ((oldScore % 2) == 1 ? (oldScore + 1) : oldScore);
                if (showScore > 10) {
                    showScore = 10;
                }
                if (showScore < 1) {
                    showScore = 1;
                }
                tvShowScore.setText(list.get((showScore / 2) - 1));
            }

            mBookId = getIntent.getStringExtra(DzConstant.BOOK_ID);
        }

    }


    private void sendComment() {
        String content = editText.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
//            ToastAlone.showShort(R.string.comment_not_allow_empty);
            rlCommentLayout.setBackgroundResource(R.drawable.bg_edit_note_full);
            tvLimit.setText(R.string.comment_not_allow_minlength);
            isRedBg = true;
            return;
        }

        content = content.replaceAll("\r", "").replaceAll("\n", "").trim();
        if (TextUtils.isEmpty(content)) {
//            ToastAlone.showShort(R.string.comment_not_allow_empty);
            rlCommentLayout.setBackgroundResource(R.drawable.bg_edit_note_full);
            tvLimit.setText(R.string.comment_not_allow_minlength);
            isRedBg = true;
            return;
        }

        if (StringUtil.containsEmoji(content)) {
            ToastAlone.showLong(R.string.comment_not_allow_emoji);
            return;
        }

        if (content.length() < 5) {
            rlCommentLayout.setBackgroundResource(R.drawable.bg_edit_note_full);
            tvLimit.setText(R.string.comment_not_allow_minlength);
            isRedBg = true;
            return;
        }

        HashMap<String, String> map = new HashMap<>();
        if (type != TYPE_EDIT) {
            map.put("type", "1");
        } else {
            map.put("type", "2");
        }
        int showScore = (int) ((commentScore % 2) == 1 ? (commentScore + 1) : commentScore);
        presenter.sendComment(mBookId, content, showScore, bookName, type != TYPE_EDIT ? 1 : 2, commentId);

        DzLog.getInstance().logClick(LogConstants.MODULE_FSPL, LogConstants.ZONE_TJPL, "", map, null);
    }

    @Override
    protected void setListener() {
        mTitleView.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.hideInput(editText);
                onBackPressed();
            }
        });
        mTitleView.setRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendComment();
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = editText.getText().toString().trim();
                if (!TextUtils.isEmpty(text) && text.length() >= 500) {
                    rlCommentLayout.setBackgroundResource(R.drawable.bg_edit_note_full);
                    textViewTitleNum.setTextColor(CompatUtils.getColor(getContext(), R.color.color_100_FA2A2D));
                    isRedBg = true;
                }
                textViewTitleNum.setText(Math.min(500, text.length()) + "/500");
                if (text.length() > 4) {
                    tvLimit.setText("");
                    if (isRedBg && text.length() < 500) {
                        rlCommentLayout.setBackgroundResource(R.drawable.hw_shape_tag);
                        isRedBg = false;
                        textViewTitleNum.setTextColor(CompatUtils.getColor(getContext(), R.color.color_b3b3b3));
                    }
                }
                if (text.length() > 0) {
                    mTitleView.setImageViewRightOperAlphe(1.0f, true);
                } else {
                    mTitleView.setImageViewRightOperAlphe(RIGHT_ALPHE, false);
                }
            }
        });
        ratingBar.setOnRatingChangeListener(new CommentRatingBarView.OnRatingChangeListener() {
            @Override
            public void onRatingChange(float ratingCount) {
                commentScore = ratingCount * 2;
                int showScore = (int) ((commentScore % 2) == 1 ? (commentScore + 1) : commentScore);
                if (showScore > 10) {
                    showScore = 10;
                } else if (showScore < 1) {
                    showScore = 1;
                }
                tvShowScore.setText(list.get((showScore / 2) - 1));
            }
        });
        if (mDecorView != null) {
            mDecorView.getViewTreeObserver().addOnGlobalLayoutListener(new MyOnGlobalLayoutListener());
        }
    }

    @Override
    public void notifyBookDetailRefresh(ArrayList<BeanCommentInfo> infoList, String bookId) {
        if (infoList != null && infoList.size() > 0 && !TextUtils.isEmpty(bookId)) {
            Bundle mBundle = new Bundle();
            mBundle.putSerializable("commentList", infoList);
            mBundle.putString("bookId", bookId);
            EventBusUtils.sendMessage(EventConstant.CODE_COMMENT_BOOKDETAIL_SEND_SUCCESS, EventConstant.TYPE_BOOK_COMMENT, mBundle);
        } else {
            EventBusUtils.sendMessage(EventConstant.CODE_COMMENT_BOOKDETAIL_SEND_SUCCESS, EventConstant.TYPE_BOOK_COMMENT, null);
        }
    }

    @Override
    public void isShowNotNetDialog() {
        BookCommentSendActivity.this.showNotNetDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        HashMap<String, String> map = new HashMap<>();
        if (type != TYPE_EDIT) {
            map.put("type", "1");
        } else {
            map.put("type", "2");
        }
        DzLog.getInstance().logPv(this.getName(), map, null);
    }

    @Override
    public void finish() {
        presenter.hideInput(editText);
        super.finish();
    }

    @Override
    protected boolean isCustomPv() {
        return true;
    }

    /**
     * 打开
     *
     * @param context   context
     * @param bookId    bookId
     * @param content   content
     * @param score     score
     * @param bookName  bookName
     * @param commentId commentId
     * @param type      type
     */
    public static void launch(Context context, @NonNull String bookId, String content, float score, String bookName, String commentId, int type) {
        Intent intent = new Intent(context, BookCommentSendActivity.class);
        intent.putExtra(DzConstant.BOOK_NAME, bookName);
        intent.putExtra(BookCommentSendActivity.TAG_CONTENT, content);
        intent.putExtra(BookCommentSendActivity.TAG_SCORE, score);
        intent.putExtra(BookCommentSendActivity.TAG_COMMENT_ID, commentId);
        intent.putExtra(BookCommentSendActivity.TAG_COMMENT_TYPE, type);
        intent.putExtra(DzConstant.BOOK_ID, bookId);
        context.startActivity(intent);
        BaseActivity.showActivity(context);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.destroy();
        }
    }

    /**
     * layout 监听
     */
    private class MyOnGlobalLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {
        @Override
        public void onGlobalLayout() {
            //获取当前根视图在屏幕上显示的大小
            Rect r = new Rect();
            mDecorView.getWindowVisibleDisplayFrame(r);

            int visibleHeight = r.height();
            if (mDecorViewVisibleHeight == 0) {
                mDecorViewVisibleHeight = visibleHeight;
                return;
            }

            //根视图显示高度没有变化，可以看作软键盘显示／隐藏状态没有改变
            if (mDecorViewVisibleHeight == visibleHeight) {
                return;
            }

            //根视图显示高度变小超过200，可以看作软键盘显示了
            if (mDecorViewVisibleHeight - visibleHeight > toggleMinHeight) {
                mDecorViewVisibleHeight = visibleHeight;
                ViewGroup.LayoutParams layoutParams = editText.getLayoutParams();
                if (layoutParams != null) {
                    layoutParams.height = editMinHeight;
                    editText.setLayoutParams(layoutParams);
                }
                return;
            }

            //根视图显示高度变大超过200，可以看作软键盘隐藏了
            if (visibleHeight - mDecorViewVisibleHeight > toggleMinHeight) {
                mDecorViewVisibleHeight = visibleHeight;
                ViewGroup.LayoutParams layoutParams = editText.getLayoutParams();
                if (layoutParams != null) {
                    layoutParams.height = editMaxHeight;
                    editText.setLayoutParams(layoutParams);
                }
            }

        }
    }
}
