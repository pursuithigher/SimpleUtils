package com.dzbook.utils;

import android.content.Context;
import android.content.Intent;

import com.dzbook.activity.reader.ChaseRecommendActivity;
import com.dzbook.mvp.presenter.ChaseRecommendPresenter;
import com.ishugui.R;
import com.iss.app.BaseActivity;
import com.iss.view.common.ToastAlone;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * intent 页面跳转
 *
 * @author caimantang on 2018/4/17.
 */

public class TurnPageUtils {
    /**
     * 跳转至推荐
     * 1.终章
     * 2.下架
     *
     * @param context       context
     * @param bookId        bookId
     * @param bookName      bookName
     * @param bookstatus    bookstatus
     * @param lastChapterId lastChapterId
     * @param bookFrom      bookFrom
     */
    public static void toRecommentPage(final Context context, final String bookId, final String bookName, final int bookstatus, final String lastChapterId, int bookFrom) {

        if (bookFrom == 2) {
            //本地书没有终章推荐
            ToastAlone.showShort(R.string.str_last_page);
        } else {
            AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(context, ChaseRecommendActivity.class);
                    intent.putExtra(ChaseRecommendPresenter.CHASE_RECOMMEND_BOOK_ID, bookId);
                    intent.putExtra(ChaseRecommendPresenter.CHASE_RECOMMEND_BOOK_NAME, bookName);
                    intent.putExtra(ChaseRecommendPresenter.CHASE_RECOMMEND_BOOK_STATUS, bookstatus);
                    intent.putExtra(ChaseRecommendPresenter.CHASE_RECOMMEND_LAST_CHPTERID, lastChapterId);
                    context.startActivity(intent);
                    BaseActivity.showActivity(context);
                }
            });
        }
    }
}
