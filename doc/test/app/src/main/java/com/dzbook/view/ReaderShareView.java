package com.dzbook.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dzbook.database.bean.BookInfo;
import com.dzbook.lib.net.DzSchedulers;
import com.dzbook.lib.utils.ALog;
import com.dzbook.utils.GlideImageLoadUtils;
import com.dzbook.utils.ImageUtils;
import com.ishugui.R;

/**
 * 阅读分享
 *
 * @author dongdianzhou on 2017/12/12.
 */

public class ReaderShareView extends LinearLayout {

    private TextView mTextViewContent;
    private ImageView mImageView;
    private TextView mTextViewTitle;
    private Bitmap bitmap;
    private byte[] bytes;

    /**
     * 构造
     *
     * @param context context
     */
    public ReaderShareView(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public ReaderShareView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
        initData();
        setListener();
    }

    private void setListener() {
    }

    private void initData() {

    }

    private void initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_reader_share, this);
        mTextViewContent = view.findViewById(R.id.share_content);
        mTextViewTitle = view.findViewById(R.id.share_title);
        mImageView = view.findViewById(R.id.share_icon);
    }

    /**
     * 绑定数据
     *
     * @param shareContent shareContent
     * @param bookInfo     bookInfo
     */
    public void bindData(String shareContent, BookInfo bookInfo) {
        if (!TextUtils.isEmpty(bookInfo.coverurl) && URLUtil.isNetworkUrl(bookInfo.coverurl)) {
            GlideImageLoadUtils.getInstanse().glideImageLoadFromUrl((Activity) getContext(), mImageView, bookInfo.coverurl);
        }
        mTextViewTitle.setText(bookInfo.author + "《" + bookInfo.bookname + "》");
        mTextViewContent.setText(shareContent);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    /**
     * 初始数据
     */
    public void initShareData() {
        DzSchedulers.childDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    ReaderShareView.this.bytes = ImageUtils.compressBitmap((Activity) getContext(), createShareBitmap(), 30, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 200);
    }

    /**
     * 分享监听
     */
    public interface ShareViewListener {
        /**
         * 分享文本
         *
         * @param bytes bytes
         */
        void shareBytes(byte[] bytes);
    }

    /**
     * 设置分享监听
     *
     * @param shareViewListener shareViewListener
     */
    public void setShareViewListener(final ShareViewListener shareViewListener) {
        DzSchedulers.child(new Runnable() {
            @Override
            public void run() {
                if (null != shareViewListener) {
                    long time = System.currentTimeMillis();
                    final long num = 2000L;
                    while (null == ReaderShareView.this.bytes && System.currentTimeMillis() - time < num) {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    DzSchedulers.main(new Runnable() {
                        @Override
                        public void run() {
                            shareViewListener.shareBytes(ReaderShareView.this.bytes);
                        }
                    });
                }
            }
        });
    }

    /**
     * 获取分享bitmap
     *
     * @return bitmap
     */
    public Bitmap getShareBitmap() {
        if (null == bitmap) {
            bitmap = createShareBitmap();
        }
        return bitmap;
    }

    /**
     * 生成bitmap
     *
     * @return bitmap
     */
    public Bitmap createShareBitmap() {
        long currentTimeMillis = System.currentTimeMillis();
        Bitmap shareBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(shareBitmap);
        draw(canvas);
        ALog.cmtDebug("createShareBitmap:" + (System.currentTimeMillis() - currentTimeMillis));
        return shareBitmap;
    }
}
