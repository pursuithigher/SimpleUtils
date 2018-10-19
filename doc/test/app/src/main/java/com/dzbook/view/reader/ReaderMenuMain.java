package com.dzbook.view.reader;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.dzbook.activity.reader.ReaderActivity;
import com.dzbook.activity.reader.ReaderCatalogActivity;
import com.dzbook.database.bean.CatalogInfo;
import com.dzbook.log.DzLog;
import com.dzbook.log.LogConstants;
import com.dzbook.model.UserGrow;
import com.dzbook.mvp.presenter.ReaderPresenter;
import com.dzbook.r.c.AkDocInfo;
import com.dzbook.r.c.SettingManager;
import com.dzbook.utils.DBUtils;
import com.dzbook.utils.DimensionPixelUtil;
import com.ishugui.R;
import com.iss.view.common.ToastAlone;

import java.text.DecimalFormat;
import java.util.HashMap;

/**
 * 阅读器主菜单
 *
 * @author wxliao on 18/4/18.
 */
public class ReaderMenuMain extends RelativeLayout implements View.OnClickListener, Menuable {

    private static final String FIRST_PAGE = "已经是第一章";
    private static final String LAST_PAGE = "已经是最后一章";
    private static final int PRG_MAX = 10000;
    private static final float HALF = 0.5f;
    private static final float HUNDRED = 100f;
    /**
     * 点击时间间隔最大等待1秒
     */
    private static final int MAX_CLICK_INTERVAL_TIME = 500;

    private final DecimalFormat df = new DecimalFormat("##0.00%");

    private ReaderNewTitle readerTitleView;
    private LinearLayout layoutToolBar;
    private ToggleButton toggleButtonMode;
    private SeekBar seekBarReadProgress;
    private TextView textViewPercent;
    private boolean isUpdateIcon = false;
    private volatile boolean isAnimationIng = false;


    private long lastClickTime = 0;

    /**
     * 构造
     *
     * @param context context
     */
    public ReaderMenuMain(Context context) {
        this(context, null);
    }

    /**
     * 构造
     *
     * @param context context
     * @param attrs   attrs
     */
    public ReaderMenuMain(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_reader_menu_main, this, true);
        readerTitleView = findViewById(R.id.readerTitleView);
        layoutToolBar = findViewById(R.id.layout_toolBar);
        toggleButtonMode = findViewById(R.id.toggleButton_mode);
        textViewPercent = findViewById(R.id.textView_percent);
        seekBarReadProgress = findViewById(R.id.seekBar_readProgress);

        findViewById(R.id.menu_chapter).setOnClickListener(this);
        findViewById(R.id.menu_brightness).setOnClickListener(this);
        findViewById(R.id.menu_voice).setOnClickListener(this);
        findViewById(R.id.menu_setting).setOnClickListener(this);
        findViewById(R.id.textView_preChapter).setOnClickListener(this);
        findViewById(R.id.textView_nextChapter).setOnClickListener(this);
        toggleButtonMode.setOnClickListener(this);

        seekBarReadProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                float percent = seekBar.getProgress() * HUNDRED / PRG_MAX;
                ReaderActivity activity = (ReaderActivity) getContext();
                activity.applyProgress(percent);
                refreshData();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    float percent = (float) seekBar.getProgress() / (float) PRG_MAX;
                    textViewPercent.setText(getPercentStr(percent));
                }
            }
        });
    }

    /**
     * 打开
     */
    public void show() {
        readerTitleView.setTranslationY(-readerTitleView.getMeasuredHeight());
        readerTitleView.animate().translationY(0);

        layoutToolBar.setTranslationY(layoutToolBar.getMeasuredHeight());
        layoutToolBar.animate().translationY(0).setListener(null);

        toggleButtonMode.setScaleX(0f);
        toggleButtonMode.setScaleY(0f);
        toggleButtonMode.animate().scaleX(1f).scaleY(1f);

        layoutToolBar.bringToFront();
        refreshData();
    }

    @Override
    public void refreshData() {
        seekBarReadProgress.setMax(PRG_MAX);
        ReaderActivity activity = (ReaderActivity) getContext();
        AkDocInfo docInfo = activity.getDocument();
        if (docInfo == null) {
            return;
        }
        int currentPercent = (int) (docInfo.percent * 100);
        if (currentPercent > PRG_MAX) {
            currentPercent = PRG_MAX;
        }
        seekBarReadProgress.setProgress(currentPercent);
        textViewPercent.setText(getPercentStr(docInfo.percent / 100));

        boolean isNight = SettingManager.getInstance(getContext()).getReaderNightMode();
        setReaderModeChecked(isNight);
        readerTitleView.refresh(docInfo);
    }

    /**
     * 关闭
     *
     * @param runnable 回调
     */
    public void hide(final Runnable runnable) {
        readerTitleView.setTranslationY(0);
        readerTitleView.animate().translationY(-readerTitleView.getMeasuredHeight());

        layoutToolBar.setTranslationY(0);
        layoutToolBar.animate().translationY(layoutToolBar.getMeasuredHeight()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                runnable.run();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        toggleButtonMode.setScaleX(1f);
        toggleButtonMode.setScaleY(1f);
        toggleButtonMode.animate().scaleX(0f).scaleY(0f);
    }

    private void startVoice() {
        ReaderActivity activity = (ReaderActivity) getContext();
        ReaderPresenter presenter = activity.getPresenter();
        if (presenter == null) {
            return;
        }
        AkDocInfo docInfo = presenter.refreshDocument();
        if (docInfo != null) {
            HashMap<String, String> paramsMap = new HashMap<>();
            paramsMap.put("action_type", "action_tts");
            paramsMap.put("cid", docInfo.chapterId);
            DzLog.getInstance().logClick(LogConstants.MODULE_YDQ, LogConstants.ZONE_YDQ_YDCZ, docInfo.bookId, paramsMap, null);
            presenter.startVoice();
        }
    }

    private void startCatalog() {
        ReaderActivity activity = (ReaderActivity) getContext();
        ReaderCatalogActivity.launch(activity, activity.getRequestedOrientation(), activity.getDocument());
        activity.hideMenuPanel(true);
    }

    private void showBrightness() {
        ReaderActivity activity = (ReaderActivity) getContext();
        activity.setMenuState(ReaderNewPanel.STATE_BRIGHTNESS);
        activity.showMenuPanel();
    }

    private void showMoreSetting() {
        ReaderActivity activity = (ReaderActivity) getContext();
        activity.setMenuState(ReaderNewPanel.STATE_SETTING);
        activity.showMenuPanel();
    }

    private void onNextChapterClick() {
        ReaderActivity activity = (ReaderActivity) getContext();
        ReaderPresenter presenter = activity.getPresenter();
        if (presenter == null) {
            return;
        }
        AkDocInfo currentDoc = activity.getDocument();
        if (currentDoc == null) {
            return;
        }
        CatalogInfo nextChapter = DBUtils.getNextCatalog(activity, currentDoc.bookId, currentDoc.chapterId);
        if (nextChapter != null) {
            activity.turnChapter(nextChapter, true, LogConstants.ORDER_SOURCE_FROM_VALUE_7);
//            activity.hideMenuPanel(true);
        } else {
//            activity.hideMenuPanel(true);
            presenter.launchChaseRecommendBooks(currentDoc.chapterId);
        }
    }

    private void onPreChapterClick() {
        ReaderActivity activity = (ReaderActivity) getContext();
        AkDocInfo currentDoc = activity.getDocument();
        if (currentDoc == null) {
            return;
        }
        CatalogInfo preChapter = DBUtils.getPreCatalog(activity, currentDoc.bookId, currentDoc.chapterId);
        if (preChapter != null) {
            activity.turnChapter(preChapter, true, LogConstants.ORDER_SOURCE_FROM_VALUE_7);
//            activity.hideMenuPanel(true);
        } else {
//            activity.hideMenuPanel(true);
            ToastAlone.showShort(FIRST_PAGE);
        }
    }


    private String getPercentStr(float percent) {
        return df.format(percent);
    }

    private void setReaderModeChecked(boolean checked) {
        toggleButtonMode.setChecked(checked);
        if (checked) {
            toggleButtonMode.setBackgroundResource(R.drawable.hw_reader_menu_light);
        } else {
            toggleButtonMode.setBackgroundResource(R.drawable.hw_reader_menu_night);
        }
    }


    private synchronized void onModeClick() {
        if (isAnimationIng) {
            return;
        }
        isAnimationIng = true;
        final ReaderActivity activity = (ReaderActivity) getContext();
        boolean isChecked = toggleButtonMode.isChecked();

        final float radus = DimensionPixelUtil.dip2px(getContext(), 44);
        final float startX = (int) toggleButtonMode.getX();
        final float startY = (int) toggleButtonMode.getY();
        final float endY = startY + radus;
        if (isChecked) {
            UserGrow.userGrowOnceToday(activity, UserGrow.USER_GROW_NIGHT_READER);
            SettingManager.getInstance(getContext()).setReaderNightMode(true);
            activity.applyColorStyle(SettingManager.NIGHT_MODE_COLOR_INDEX);
            isUpdateIcon = false;
            setAnimator(radus, startX, startY, endY, R.drawable.hw_reader_menu_light);
        } else {
            SettingManager.getInstance(getContext()).setReaderNightMode(false);
            int index = SettingManager.getInstance(getContext()).getColorStyleIndex();
            activity.applyColorStyle(index);
            isUpdateIcon = false;
            setAnimator(radus, startX, startY, endY, R.drawable.hw_reader_menu_night);
        }
    }


    private void setAnimator(final float radus, final float startX, final float startY, final float endY, final int imgSrc) {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float duraction = (Float) valueAnimator.getAnimatedValue();
                float currentCenterX;
                float currentCenterY;
                float currentRadians;
                if (duraction < HALF) {
                    currentRadians = 360 - 90 * duraction * 2;
                    currentCenterX = (float) (Math.sin(Math.toRadians(currentRadians)) * radus);
                    currentCenterY = (float) (-Math.cos(Math.toRadians(currentRadians)) * radus);
                    toggleButtonMode.setX(startX + currentCenterX);
                    toggleButtonMode.setY(startY + currentCenterY + radus);
                    toggleButtonMode.setAlpha(1 - duraction * 2);
                } else {
                    if (!isUpdateIcon) {
                        isUpdateIcon = true;
                        toggleButtonMode.setBackgroundResource(imgSrc);
                    }
                    currentRadians = 90 - 90 * (duraction - HALF) * 2;
                    currentCenterX = (float) (Math.sin(Math.toRadians(currentRadians)) * radus);
                    currentCenterY = (float) (Math.cos(Math.toRadians(currentRadians)) * radus);
                    toggleButtonMode.setX(startX + currentCenterX);
                    toggleButtonMode.setY(endY - currentCenterY);
                    toggleButtonMode.setAlpha((duraction - HALF) * 2);
                }
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimationIng = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animator.setDuration(300);
        animator.start();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.menu_chapter) {
            startCatalog();
        } else if (id == R.id.menu_voice) {
            startVoice();
        } else if (id == R.id.menu_brightness) {
            showBrightness();
        } else if (id == R.id.menu_setting) {
            showMoreSetting();
        } else if (id == R.id.textView_preChapter) {
            long thisClickTime = System.currentTimeMillis();
            if (thisClickTime - lastClickTime < MAX_CLICK_INTERVAL_TIME) {
                return;
            }
            lastClickTime = thisClickTime;
            onPreChapterClick();
        } else if (id == R.id.textView_nextChapter) {
            long thisClickTime = System.currentTimeMillis();
            if (thisClickTime - lastClickTime < MAX_CLICK_INTERVAL_TIME) {
                return;
            }
            lastClickTime = thisClickTime;
            onNextChapterClick();
        } else if (id == R.id.toggleButton_mode) {
            onModeClick();
        }
    }
}
