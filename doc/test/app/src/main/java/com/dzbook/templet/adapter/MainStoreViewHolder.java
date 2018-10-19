package com.dzbook.templet.adapter;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.dzbook.mvp.presenter.TempletPresenter;
import com.dzbook.view.PageView.PageState;
import com.dzbook.view.store.Bn0View;
import com.dzbook.view.store.Db0View;
import com.dzbook.view.store.Db1View;
import com.dzbook.view.store.LD0View;
import com.dzbook.view.store.LimitFreeTitleView;
import com.dzbook.view.store.ModuleItemView;
import com.dzbook.view.store.Pw0View;
import com.dzbook.view.store.Pw1View;
import com.dzbook.view.store.SigleBooKViewH;
import com.dzbook.view.store.SigleBooKViewV;
import com.dzbook.view.store.SjMoreTitleView;
import com.dzbook.view.store.Tm0View;
import com.dzbook.view.store.Tm1View;
import com.dzbook.view.store.VipStoreTopView;
import com.dzbook.view.store.Xm0HeaderTitleView;
import com.dzbook.view.store.Xslb0ImageView;

import java.util.ArrayList;

import hw.sdk.net.bean.store.BeanSubTempletInfo;
import hw.sdk.net.bean.store.BeanTempletInfo;
import hw.sdk.net.bean.store.BeanVipInfo;
import hw.sdk.net.bean.store.TempletContant;

/**
 * ViewHolder
 *
 * @author dongdianzhou on 2018/3/20.
 */

public class MainStoreViewHolder extends RecyclerView.ViewHolder {

    private Bn0View bn0View;
    private Db0View db0View;
    private Db1View db1View;
    private Pw0View pw0View;
    private Pw1View pw1View;
    private Tm0View tm0View;
    private Tm1View tm1View;
    private LD0View ld0View;
    private SjMoreTitleView titleView;
    private SigleBooKViewV sigleBooKViewV;
    private SigleBooKViewH sigleBooKViewH;
    private ModuleItemView moduleItemView;

    private LimitFreeTitleView lfTitleView;
    private Xm0HeaderTitleView xm0HeaderTitleView;

    private VipStoreTopView vipStoreTopView;

    private Xslb0ImageView xslb0ImageView;

    private long lastClickTime = 0;

    /**
     * 构造函数
     *
     * @param itemView itemView
     */
    public MainStoreViewHolder(View itemView) {
        super(itemView);
        if (itemView instanceof Bn0View) {
            bn0View = (Bn0View) itemView;
        } else if (itemView instanceof Db0View) {
            db0View = (Db0View) itemView;
        } else if (itemView instanceof Db1View) {
            db1View = (Db1View) itemView;
        } else if (itemView instanceof ModuleItemView) {
            moduleItemView = (ModuleItemView) itemView;
        } else if (itemView instanceof Pw0View) {
            pw0View = (Pw0View) itemView;
        } else if (itemView instanceof Pw1View) {
            pw1View = (Pw1View) itemView;
        } else if (itemView instanceof Tm0View) {
            tm0View = (Tm0View) itemView;
        } else {
            setViews(itemView);
        }
    }

    private void setViews(View itemView) {
        if (itemView instanceof SjMoreTitleView) {
            titleView = (SjMoreTitleView) itemView;
        } else if (itemView instanceof SigleBooKViewV) {
            sigleBooKViewV = (SigleBooKViewV) itemView;
        } else if (itemView instanceof SigleBooKViewH) {
            sigleBooKViewH = (SigleBooKViewH) itemView;
        } else if (itemView instanceof LimitFreeTitleView) {
            lfTitleView = (LimitFreeTitleView) itemView;
        } else if (itemView instanceof Xm0HeaderTitleView) {
            xm0HeaderTitleView = (Xm0HeaderTitleView) itemView;
        } else if (itemView instanceof LD0View) {
            ld0View = (LD0View) itemView;
        } else if (itemView instanceof VipStoreTopView) {
            vipStoreTopView = (VipStoreTopView) itemView;
        } else if (itemView instanceof Tm1View) {
            tm1View = (Tm1View) itemView;
        } else if (itemView instanceof Xslb0ImageView) {
            xslb0ImageView = (Xslb0ImageView) itemView;
        }
    }

    /**
     * 绑定数据
     *
     * @param templetInfo templetInfo
     * @param isReference isReference
     */
    public void bindBn0Data(BeanTempletInfo templetInfo, boolean isReference) {
        if (bn0View != null) {
            bn0View.bindData(templetInfo, isReference);
        }
    }

    /**
     * 绑定数据
     *
     * @param templetInfo     templetInfo
     * @param templetPosition templetPosition
     * @param position        position
     */
    public void bindDb0Data(BeanTempletInfo templetInfo, int templetPosition, int position) {
        if (db0View != null) {
            db0View.bindData(templetInfo, templetPosition, position);
        }
    }

    /**
     * 绑定数据
     *
     * @param templetInfo     templetInfo
     * @param templetPosition templetPosition
     */
    public void bindDb1Data(BeanTempletInfo templetInfo, int templetPosition) {
        if (db1View != null) {
            db1View.bindData(templetInfo, templetPosition);
        }
    }

    /**
     * 绑定数据
     *
     * @param templetInfo      templetInfo
     * @param subTempletInfo   subTempletInfo
     * @param fragment         fragment
     * @param templetPresenter templetPresenter
     */
    public void bindFl0Data(BeanTempletInfo templetInfo, BeanSubTempletInfo subTempletInfo, Fragment fragment, TempletPresenter templetPresenter) {
        if (moduleItemView != null) {
            moduleItemView.bindData(fragment, templetPresenter, subTempletInfo, templetInfo);
        }
    }

    /**
     * 绑定数据
     *
     * @param templetInfo templetInfo
     */
    public void bindPw0Data(BeanTempletInfo templetInfo) {
        if (pw0View != null) {
            pw0View.bindData(templetInfo);
        }
    }

    /**
     * 绑定数据
     *
     * @param templetInfo templetInfo
     */
    public void bindPw1Data(BeanTempletInfo templetInfo) {
        if (pw1View != null) {
            pw1View.bindData(templetInfo);
        }
    }

    /**
     * 绑定数据
     *
     * @param templetInfo templetInfo
     */
    public void bindTm0Data(BeanTempletInfo templetInfo) {
        if (tm0View != null) {
            tm0View.bindData(templetInfo);
        }
    }


    /**
     * 绑定数据
     *
     * @param info             info
     * @param templetPresenter templetPresenter
     * @param clickAction      clickAction
     * @param clickType        clickType
     * @param templetPosition  templetPosition
     */
    public void bindSjMoreTitleData(final BeanTempletInfo info, final TempletPresenter templetPresenter, final int clickAction, final int clickType, final int templetPosition) {
        titleView.bindData(info.title);
        if (info.action == null) {
            titleView.setMoreViewVisible(View.GONE);
        } else {
            titleView.setMoreViewVisible(View.VISIBLE);
        }
        titleView.setMoreClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long currentClickTime = System.currentTimeMillis();
                if (currentClickTime - lastClickTime > TempletContant.COMMON_CLICK_DISTANSE) {
                    if (info != null) {
                        templetPresenter.actionOper(info.action, info.title);
                        templetPresenter.logCommonBookClick(clickAction, clickType, info, "", templetPosition);
                        templetPresenter.logHw(info, null, templetPosition, clickAction, clickType, true);
                    }
                    lastClickTime = currentClickTime;
                }
            }
        });
    }

    /**
     * 绑定数据
     *
     * @param sub              sub
     * @param info             info
     * @param templetPresenter templetPresenter
     * @param isLimitFree      isLimitFree
     * @param clickAction      clickAction
     * @param templetPotion    templetPotion
     */
    public void bindSingleBookViewVData(BeanSubTempletInfo sub, BeanTempletInfo info, TempletPresenter templetPresenter, boolean isLimitFree, int clickAction, int templetPotion) {
        sigleBooKViewV.setTempletPresenter(templetPresenter);
        sigleBooKViewV.bindData(sub, info, isLimitFree, clickAction, templetPotion);
    }

    /**
     * 绑定数据
     *
     * @param sub              sub
     * @param info             info
     * @param templetPresenter templetPresenter
     * @param clickAction      clickAction
     * @param templetPosition  templetPosition
     * @param isVisibleLine    isVisibleLine
     */
    public void bindSingleBookViewHData(BeanSubTempletInfo sub, BeanTempletInfo info, TempletPresenter templetPresenter, int clickAction, int templetPosition, boolean isVisibleLine) {
        sigleBooKViewH.setTempletPresenter(templetPresenter);
        sigleBooKViewH.bindData(info, sub, clickAction, templetPosition, isVisibleLine);
    }

    /**
     * 绑定数据
     *
     * @param templetInfo templetInfo
     */
    public void bindLfTitleView(BeanTempletInfo templetInfo) {
        if (lfTitleView != null) {
            lfTitleView.bindData(templetInfo);
        }
    }

    /**
     * xm0HeaderTitleView设置点击监听
     *
     * @param onClickListener onClickListener
     */
    public void setXm0HtvListener(View.OnClickListener onClickListener) {
        if (xm0HeaderTitleView != null) {
            xm0HeaderTitleView.setOnClickListener(onClickListener);
        }
    }

    /**
     * 绑定数据
     *
     * @param subTempletInfo subTempletInfo
     */
    public void bindLimitHeaderData(BeanSubTempletInfo subTempletInfo) {
        xm0HeaderTitleView.bindData(subTempletInfo);
    }

    /**
     * 绑定数据
     *
     * @param pageState pageState
     */
    public void bindLd0Data(PageState pageState) {
        ld0View.setState(pageState);
    }

    /**
     * 清除图片
     */
    public void clearHImageView() {
        if (sigleBooKViewH != null) {
            sigleBooKViewH.clearImageView();
        }
    }

    /**
     * 清除图片
     */
    public void clearVImageView() {
        if (sigleBooKViewV != null) {
            sigleBooKViewV.clearImageView();
        }
    }

    /**
     * 清除图片
     */
    public void clearDb0ImageView() {
        if (db0View != null) {
            db0View.clearImageView();
        }
    }

    /**
     * 清除图片
     */
    public void clearDb1ImageView() {
        if (db1View != null) {
            db1View.clearImageView();
        }
    }

    /**
     * 绑定数据
     *
     * @param templetInfo    templetInfo
     * @param isUseLocalData isUseLocalData
     */
    public void bindVpt0Data(BeanTempletInfo templetInfo, boolean isUseLocalData) {
        if (isUseLocalData) {
            if (vipStoreTopView != null) {
                vipStoreTopView.bindData();
                return;
            }
        }
        if (templetInfo == null) {
            return;
        }
        ArrayList<BeanSubTempletInfo> beanSubTempletInfos = templetInfo.getValidChannels();
        if (beanSubTempletInfos != null && beanSubTempletInfos.size() > 0) {
            BeanSubTempletInfo subTempletInfo = beanSubTempletInfos.get(0);
            if (subTempletInfo != null) {
                BeanVipInfo vipInfo = subTempletInfo.vipInfo;
                if (vipInfo != null) {
                    if (vipStoreTopView != null) {
                        vipStoreTopView.bindData(vipInfo);
                    }
                }
            }
        }
    }

    /**
     * 绑定数据
     *
     * @param templetInfo templetInfo
     */
    public void bindTm1Data(BeanTempletInfo templetInfo) {
        if (tm1View != null) {
            tm1View.bindData(templetInfo);
        }
    }

    /**
     * 绑定数据
     *
     * @param templetInfo      templetInfo
     * @param templetPresenter templetPresenter
     */
    public void bindXslb0Data(BeanTempletInfo templetInfo, TempletPresenter templetPresenter) {
        if (xslb0ImageView != null) {
            xslb0ImageView.setTempletPresenter(templetPresenter);
            xslb0ImageView.bindData(templetInfo);
        }
    }


}
