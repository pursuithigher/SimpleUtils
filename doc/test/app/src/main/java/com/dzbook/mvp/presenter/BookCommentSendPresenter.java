package com.dzbook.mvp.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.dzbook.activity.CenterDetailActivity;
import com.dzbook.activity.comment.BookCommentSendActivity;
import com.dzbook.activity.hw.RealNameAuthActivity;
import com.dzbook.lib.utils.ALog;
import com.dzbook.lib.utils.CompatUtils;
import com.dzbook.mvp.BasePresenter;
import com.dzbook.mvp.DialogConstants;
import com.dzbook.mvp.UI.BookCommentSendUI;
import com.dzbook.net.hw.HwRequestLib;
import com.dzbook.utils.NetworkUtils;
import com.dzbook.utils.hw.CustomLinkMovementMethod;
import com.dzbook.utils.hw.LoginCheckUtils;
import com.dzbook.utils.hw.LoginUtils;
import com.dzbook.view.common.dialog.CustomHintDialog;
import com.dzbook.view.common.dialog.base.CustomDialogBusiness;
import com.ishugui.R;
import com.iss.app.BaseActivity;
import com.iss.view.common.ToastAlone;

import hw.sdk.net.bean.bookDetail.BeanCommentCheck;
import hw.sdk.net.bean.bookDetail.BeanCommentResult;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * BookCommentSendPresenter
 *
 * @author Winzows on 2017/12/6.
 */

public class BookCommentSendPresenter extends BasePresenter {

    private static final String REALNAME_URL = "http://www.cac.gov.cn/2017-09/07/m_1121623889.htm";
    private static final long MAX_CLICK_WAIT_TIME = 500;
    private LoginCheckUtils loginCheckUtils = null;
    private CustomHintDialog dialog;
    private BookCommentSendUI mUI;
    private long lastSendTime = 0;

    /**
     * 构造
     *
     * @param mUI ui
     */
    public BookCommentSendPresenter(BookCommentSendUI mUI) {
        this.mUI = mUI;
    }

    /**
     * 发表评论
     *
     * @param bookId    bookId
     * @param content   content
     * @param score     score
     * @param bookName  bookName
     * @param type      type
     * @param commentId commentId
     */
    public void sendComment(final String bookId, final String content, final int score, final String bookName, final int type, final String commentId) {
        sendComment(bookId, content, score, bookName, type, commentId, false);
    }

    /**
     * 发表评论
     *
     * @param bookId              bookId
     * @param content             content
     * @param score               score
     * @param bookName            bookName
     * @param type                type
     * @param commentId           commentId
     * @param isTokenInvalidRetry isTokenInvalidRetry
     */
    private void sendComment(final String bookId, final String content, final int score, final String bookName, final int type, final String commentId, final boolean isTokenInvalidRetry) {
        Disposable disposable = Observable.create(new ObservableOnSubscribe<BeanCommentResult>() {
            @Override
            public void subscribe(ObservableEmitter<BeanCommentResult> e) {
                try {
                    BeanCommentResult beanCommentResult = HwRequestLib.getInstance().sendCommentRequest(bookId, content, score, bookName, type, commentId);
                    e.onNext(beanCommentResult);
                    e.onComplete();
                } catch (Exception ex) {
                    ALog.printStackTrace(ex);
                    e.onError(ex);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<BeanCommentResult>() {
            @Override
            public void onNext(BeanCommentResult value) {
                if (value != null) {
                    if (value.isSuccess()) {
                        if (value.status == 1) {
                            ToastAlone.showShort(R.string.toast_comment_commit_success);
                            mUI.notifyBookDetailRefresh(value.commentList, bookId);
                            mUI.getActivity().finish();
                            return;
                        }
                    }

                    if (!isTokenInvalidRetry && value.isTokenExpireOrNeedLogin()) {
                        tokenInvalidRetry(bookId, content, score, bookName, type, commentId, value.tip);
                        return;
                    }

                    ToastAlone.showShort(value.tip);
                } else {
                    ToastAlone.showShort(R.string.comment_commit_error);
                }
            }

            @Override
            public void onError(Throwable e) {
                mUI.dissMissDialog();
                mUI.isShowNotNetDialog();
            }

            @Override
            public void onComplete() {
                mUI.dissMissDialog();
            }

            @Override
            protected void onStart() {
                super.onStart();
                mUI.showDialogByType(DialogConstants.TYPE_GET_DATA);
            }
        });

        composite.addAndDisposeOldByKey("sendComment" + bookId + type, disposable);
    }

    /**
     * destroy
     */
    public void destroy() {
        composite.disposeAll();
        if (loginCheckUtils != null) {
            loginCheckUtils.resetAgainObtainListener();
            loginCheckUtils.disHuaWeiConnect();
        }
    }

    /**
     * 校验用户评论状态
     *
     * @param context  context
     * @param bookId   bookId
     * @param bookName bookName
     * @param type     是发送 还是二次编辑
     */
    public void checkCommentStatus(final Activity context, @NonNull final String bookId, final String bookName, final int type) {
        checkCommentStatus(context, bookId, bookName, type, false);
    }

    /**
     * 校验用户评论状态
     *
     * @param context  context
     * @param bookId   bookId
     * @param bookName bookName
     * @param type     是发送 还是二次编辑
     */
    private void checkCommentStatus(final Activity context, @NonNull final String bookId, final String bookName, final int type, final boolean isTokenInvalidRetry) {
        //不能连续点两次。
        if (System.currentTimeMillis() - lastSendTime < MAX_CLICK_WAIT_TIME) {
            return;
        }
        lastSendTime = System.currentTimeMillis();
        if (!NetworkUtils.getInstance().checkNet()) {
            if (context instanceof BaseActivity) {
                ((BaseActivity) context).showNotNetDialog();
            }
        } else {
            //校验登录状态
            LoginUtils.getInstance().forceLoginCheck(context, new MyLoginCheckListener(bookId, context, bookName, type, isTokenInvalidRetry));
        }
    }


    private void requestAuth(final Context context) {
        if (dialog == null) {
            dialog = new CustomHintDialog((Activity) context);
        }
        if (dialog.isShow()) {
            return;
        }
        dialog.setConfirmTxt(context.getString(R.string.dialog_real_name));
        dialog.setCancelTxt(context.getString(R.string.cancel));
        dialog.setDesc(getRealNameTxt(context));
        CustomLinkMovementMethod instance = CustomLinkMovementMethod.getInstance();
        dialog.getTextViewMessage().setMovementMethod(instance);
        dialog.getTextViewMessage().setHighlightColor(Color.TRANSPARENT);

        dialog.setCheckListener(new CustomDialogBusiness.OnCheckListener() {
            @Override
            public void clickConfirm(Object object) {
                RealNameAuthActivity.launch(context);
            }

            @Override
            public void clickCancel() {

            }
        });

        dialog.show();
    }


    private CharSequence getRealNameTxt(final Context context) {
        final Resources resources = context.getResources();
        String content1 = resources.getString(R.string.real_name_part1);
        String content2 = resources.getString(R.string.real_name_part2);
        String content3 = resources.getString(R.string.real_name_part3);
        SpannableString s = new SpannableString(content1 + content2 + content3);
        final int color = CompatUtils.getColor(context, R.color.color_100_CD2325);
        s.setSpan(new ForegroundColorSpan(color), content1.length(), content1.length() + content2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        s.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                CenterDetailActivity.show(context, REALNAME_URL, context.getString(R.string.real_name_part4));
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(color);
                ds.setUnderlineText(false);
            }
        }, content1.length(), content1.length() + content2.length(), 1);

        return s;
    }

    /**
     * 隐藏软键盘
     *
     * @param view view
     */
    public void hideInput(View view) {
        try {
            InputMethodManager imm = (InputMethodManager) mUI.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                boolean isOpen = imm.isActive();
                if (isOpen) {
                    (((InputMethodManager) mUI.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))).hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        } catch (Exception ignore) {
        }
    }


    /**
     * 只重试一次
     */
    private void tokenInvalidRetry(final String bookId, final String content, final int score, final String bookName, final int type, final String commendId, final String tips) {
        if (loginCheckUtils == null) {
            loginCheckUtils = LoginCheckUtils.getInstance();
        }
        loginCheckUtils.againObtainAppToken((Activity) mUI.getContext(), new LoginUtils.LoginCheckListenerSub() {
            @Override
            public void loginFail() {
                ToastAlone.showShort(tips);
            }

            @Override
            public void loginComplete() {
                sendComment(bookId, content, score, bookName, type, commendId, true);
            }
        });
    }


    /**
     * 只重试一次
     */
    private void commentCheckTokenInvalidRetry(final Activity context, @NonNull final String bookId, final String bookName, final int type, final String tips) {
        if (loginCheckUtils == null) {
            loginCheckUtils = LoginCheckUtils.getInstance();
        }
        loginCheckUtils.againObtainAppToken(context, new LoginUtils.LoginCheckListenerSub() {
            @Override
            public void loginFail() {
                ToastAlone.showShort(tips);
            }

            @Override
            public void loginComplete() {
                checkCommentStatus(context, bookId, bookName, type, true);
            }
        });
    }

    /**
     * 评论登录检查
     */
    private class MyLoginCheckListener implements LoginUtils.LoginCheckListener {
        private final String bookId;
        private final Activity context;
        private final String bookName;
        private final int type;
        private final boolean isTokenInvalidRetry;

        MyLoginCheckListener(String bookId, Activity context, String bookName, int type, boolean isTokenInvalidRetry) {
            this.bookId = bookId;
            this.context = context;
            this.bookName = bookName;
            this.type = type;
            this.isTokenInvalidRetry = isTokenInvalidRetry;
        }

        @Override
        public void loginComplete() {
            Disposable disposable = Observable.create(new ObservableOnSubscribe<BeanCommentCheck>() {
                @Override
                public void subscribe(ObservableEmitter<BeanCommentCheck> e) {
                    try {
                        BeanCommentCheck beanCommentCheck = HwRequestLib.getInstance().checkCommentRequest(bookId);
                        e.onNext(beanCommentCheck);
                        e.onComplete();
                    } catch (Exception ex) {
                        ALog.printStackTrace(ex);
                        e.onError(ex);
                    }
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<BeanCommentCheck>() {
                @Override
                public void onNext(BeanCommentCheck value) {
                    if (value != null) {
                        if (value.isSuccess()) {
                            if (value.isCommentCheckPass()) {
                                BookCommentSendActivity.launch(context, bookId, value.content, (float) value.score, bookName, null, type);
                            } else if (value.isCommentNeedRealNameVerified()) {
                                requestAuth(context);
                            } else {
                                ToastAlone.showShort(value.tip);
                            }
                        } else {
                            if (!isTokenInvalidRetry && value.isTokenExpireOrNeedLogin()) {
                                commentCheckTokenInvalidRetry(context, bookId, bookName, type, value.tip);
                                return;
                            }
                            ToastAlone.showShort(value.tip);
                        }
                    } else {
                        ToastAlone.showShort(R.string.comment_send_comment_error);
                    }
                }

                @Override
                public void onError(Throwable e) {
                    mUI.isShowNotNetDialog();
                    if (context instanceof BaseActivity) {
                        ((BaseActivity) context).dissMissDialog();
                    }
                }

                @Override
                public void onComplete() {
                    if (context instanceof BaseActivity) {
                        ((BaseActivity) context).dissMissDialog();
                    }
                }

                @Override
                protected void onStart() {
                    super.onStart();
                    ((BaseActivity) context).showDialogByType(DialogConstants.TYPE_GET_DATA);
                }
            });
            composite.addAndDisposeOldByKey("checkCommentStatus", disposable);
        }
    }
}
