package com.dzbook.view.comment;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.dzbook.activity.comment.BookCommentItemDetailActivity;
import com.dzbook.activity.comment.BookCommentMoreActivity;
import com.dzbook.activity.comment.BookCommentPersonCenterActivity;
import com.dzbook.activity.comment.BookCommentSendActivity;
import com.dzbook.activity.detail.BookDetailActivity;
import com.dzbook.activity.reader.BasePopupWindow;
import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.lib.utils.StringUtil;
import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.utils.DimensionPixelUtil;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.SpUtil;
import com.dzbook.utils.hw.LoginUtils;
import com.ishugui.R;
import com.iss.app.BaseActivity;

import java.util.HashMap;

import hw.sdk.net.bean.bookDetail.BeanCommentInfo;


/**
 * CommentPopWindow
 *
 * @author Winzows on 2017/12/05.
 */

public class CommentPopWindow extends BasePopupWindow implements View.OnClickListener {

    private Context context;

    private TextView tvEdit, tvDelete;

    private BeanCommentInfo info;

    private View line1;

    private CommentBaseView itemView;

    private String from;

    private String module;

    /**
     * 构造
     *
     * @param context  context
     * @param info     info
     * @param itemView itemView
     * @param from     from
     */
    public CommentPopWindow(Context context, BeanCommentInfo info, CommentBaseView itemView, String from) {
        super(context);
        this.context = context;
        this.info = info;
        this.itemView = itemView;
        this.from = from;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setContentView(inflater.inflate(R.layout.dialog_comment_popwindow, null));
        setOutsideTouchable(true);
        module = LogConstants.MODULE_QBPL;
        switch (from) {
            case BookDetailActivity.TAG:
                module = LogConstants.MODULE_SJXQ;
                break;
            case BookCommentMoreActivity.TAG:
                module = LogConstants.MODULE_QBPL;
                break;
            case BookCommentItemDetailActivity.TAG:
                module = LogConstants.MODULE_PLXQ;
                break;
            case BookCommentPersonCenterActivity.TAG:
                module = LogConstants.MODULE_WDDP;
                break;
            default:
                module = "";
                break;

        }
    }

    @Override
    protected void initView(View view) {
        tvEdit = view.findViewById(R.id.tv_edit);
        tvDelete = view.findViewById(R.id.tv_delete);
        line1 = view.findViewById(R.id.line1);
    }

    @Override
    protected void initData(View view) {
        if (TextUtils.equals(SpUtil.getinstance(context).getUserID(), info.uId)) {
            tvDelete.setVisibility(View.VISIBLE);
            tvEdit.setVisibility(View.VISIBLE);
            setHeight(DimensionPixelUtil.dip2px(context, 75));
        } else {
            tvDelete.setVisibility(View.GONE);
            tvEdit.setVisibility(View.GONE);
            line1.setVisibility(View.GONE);
            setHeight(DimensionPixelUtil.dip2px(context, 43));
        }
        setWidth(DimensionPixelUtil.dip2px(context, 80));
        int color = CompatUtils.getColor(context, android.R.color.transparent);
        this.setBackgroundDrawable(new ColorDrawable(color));
    }

    @Override
    protected void setListener(View view) {
        tvEdit.setOnClickListener(this);
        tvDelete.setOnClickListener(this);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        super.showAsDropDown(anchor, xoff, yoff);
        int color = CompatUtils.getColor(context, android.R.color.transparent);
        this.setBackgroundDrawable(new ColorDrawable(color));
    }

    @Override
    public void onClick(View view) {
        if (view != null) {
            int id = view.getId();
            switch (id) {
                case R.id.tv_edit:
                    dealClickEdit();
                    break;
                case R.id.tv_delete:
                    dealClickDelete();
                    break;
                default:
                    break;
            }

            dismiss();
        }
    }

    private void dealClickEdit() {
        Boolean accountLoginStatus = SpUtil.getinstance(context).getAccountLoginStatus();
        if (!NetworkUtils.getInstance().checkNet()) {
            if (context instanceof BaseActivity) {
                ((BaseActivity) context).showNotNetDialog();
            }
        } else {
            LoginUtils.getInstance().forceLoginCheck(context, new LoginUtils.LoginCheckListener() {
                @Override
                public void loginComplete() {
                    BookCommentSendActivity.launch(context, info.bookId, info.content, info.score,
                            TextUtils.isEmpty(info.bookName) ? itemView.getBookName() : info.bookName,
                            info.commentId, BookCommentSendActivity.TYPE_EDIT);
                    if (context instanceof BookCommentItemDetailActivity) {
                        ((BookCommentItemDetailActivity) context).finish();
                    }
                }
            });
        }
        if (!StringUtil.isEmpty(from, module)) {
            HashMap<String, String> map = new HashMap<>();
            map.put(LogConstants.KEY_RECHARGE_BID, info.bookId);
            map.put("book_name", info.bookName);
            map.put("is_login", accountLoginStatus + "");
            map.put("type", "2");
            DzLog.getInstance().logClick(module, LogConstants.ZONE_PLBJ, "", map, null);
        }
    }

    private void dealClickDelete() {
        if (!NetworkUtils.getInstance().checkNet()) {
            if (context instanceof BaseActivity) {
                ((BaseActivity) context).showNotNetDialog();
            }
        } else {
            LoginUtils.getInstance().forceLoginCheck(context, new LoginUtils.LoginCheckListener() {
                @Override
                public void loginComplete() {
                    CommentItemView.operationComment(context, info, CommentItemView.TYPE_DELETE);
                }
            });
        }
        if (!StringUtil.isEmpty(from, module)) {
            HashMap<String, String> map = new HashMap<>();
            map.put(LogConstants.KEY_RECHARGE_BID, info.bookId);
            map.put("book_name", info.bookName);
            DzLog.getInstance().logClick(module, LogConstants.ZONE_PLSC, "", map, null);
        }
    }
}
