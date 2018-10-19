package com.dzbook.templet.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.dzbook.mvp.presenter.TempletPresenter;
import com.dzbook.view.PageView.PageState;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import hw.sdk.net.bean.store.BeanSubTempletInfo;
import hw.sdk.net.bean.store.BeanTempletInfo;
import hw.sdk.net.bean.store.TempletMapping;

/**
 * Adapter
 *
 * @author dongdianzhou on 2018/3/15.
 */

public class DzDelegateAdapter extends DelegateAdapter {
    private PageState mState = PageState.Loadable;

    private Context mContext;
    private Fragment mFragment;
    private TempletPresenter mPresenter;
    private int firstPageNum = 0;
    private List<BeanTempletInfo> list;
    private Ld0Adapter ld0Adapter;

    private Vpt0Adapter vpt0Adapter;

    /**
     * 是否有banner和分类
     * 0:有banner和分类
     * 1：有banner无分类
     * 2：有分类无banner
     * 3：都没有
     */
    private int hasFlOrBannerTab;

    /**
     * 构造函数
     *
     * @param layoutManager layoutManager
     */
    public DzDelegateAdapter(VirtualLayoutManager layoutManager) {
        this(layoutManager, false);
    }

    /**
     * 构造函数
     *
     * @param layoutManager      layoutManager
     * @param hasConsistItemType hasConsistItemType
     */
    public DzDelegateAdapter(VirtualLayoutManager layoutManager, boolean hasConsistItemType) {
        this(layoutManager, hasConsistItemType, null, null, null);
    }


    /**
     * 构造函数
     *
     * @param layoutManager      layoutManager
     * @param hasConsistItemType hasConsistItemType
     * @param context            context
     * @param fragment           fragment
     * @param templetPresenter   templetPresenter
     */
    public DzDelegateAdapter(VirtualLayoutManager layoutManager, boolean hasConsistItemType, Context context, Fragment fragment, TempletPresenter templetPresenter) {
        super(layoutManager, hasConsistItemType);
        mContext = context;
        mFragment = fragment;
        mPresenter = templetPresenter;
        list = new ArrayList<>();
    }

    public int getFirstPageNum() {
        return firstPageNum;
    }

    /**
     * 添加数据
     *
     * @param infoList infoList
     */
    public void setItems(List<BeanTempletInfo> infoList) {
        if (this.list != null) {
            this.list.clear();
            this.list.addAll(infoList);
            firstPageNum = this.list.size();
            List<Adapter> adapters = covertTempletInfoToVlayoutAdapter(this.list, true);
            setAdapters(adapters);
            if (vpt0Adapter != null) {
                vpt0Adapter.setUseLocalData(true);
                vpt0Adapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 添加数据
     *
     * @param infoList infoList
     */
    public void addItems(List<BeanTempletInfo> infoList) {
        if (this.list != null) {
            if (this.list.size() > 0) {
                for (BeanTempletInfo temp : this.list) {
                    if (temp != null && temp.viewType == TempletMapping.VIEW_TYPE_LD0) {
                        this.list.remove(temp);
                    }
                }
            }
            this.list.addAll(infoList);
            List<Adapter> adapters = covertTempletInfoToVlayoutAdapter(this.list, false);
            setAdapters(adapters);
        }
    }


    /**
     * 设置PageState
     *
     * @param state state
     */
    public void setState(PageState state) {
        mState = state;
        if (ld0Adapter != null) {
            ld0Adapter.setState(state);
            ld0Adapter.notifyDataSetChanged();
        }
    }

    public PageState getState() {
        return mState;
    }


    /**
     * 登录成功和开通vip刷新view
     */
    public void referenceVptView() {
        if (vpt0Adapter != null) {
            vpt0Adapter.setUseLocalData(true);
            vpt0Adapter.notifyDataSetChanged();
        }
    }


    /**
     * 将对象数据转换成为vlayout的adapter
     *
     * @param templetInfos
     * @param isReference
     * @return
     */
    private List<Adapter> covertTempletInfoToVlayoutAdapter(List<BeanTempletInfo> templetInfos, boolean isReference) {
        hasFlOrBannerTab = hasFlOrBannerTab(templetInfos);
        List<DelegateAdapter.Adapter> adapters = new LinkedList<>();
        for (int i = 0; i < templetInfos.size(); i++) {
            BeanTempletInfo tem = templetInfos.get(i);
            if (tem != null) {
                addItemForViewType(isReference, adapters, i, tem);
            }
        }
        return adapters;
    }

    private void addItemForViewType(boolean isReference, List<Adapter> adapters, int i, BeanTempletInfo tem) {
        switch (tem.viewType) {
            case TempletMapping.VIEW_TYPE_BN0:
                adapters.add(new Bn0Adapter(tem, mFragment, mPresenter, isReference, i));
                break;
            case TempletMapping.VIEW_TYPE_FL0:
                adapters.add(new Fl0Adapter(tem, mContext, mFragment, mPresenter, i));
                break;
            case TempletMapping.VIEW_TYPE_XM0:
                Xm0Adapter xm0Adapter = new Xm0Adapter(mContext, mPresenter, i, hasFlOrBannerTab);
                xm0Adapter.addXM0Adapter(adapters, tem);
                break;
            case TempletMapping.VIEW_TYPE_SJ0:
                Sj0Adapter sj0Adapter = new Sj0Adapter(mContext, mPresenter, i, hasFlOrBannerTab);
                sj0Adapter.addSj0Adapter(adapters, tem);
                break;
            case TempletMapping.VIEW_TYPE_SJ3:
                Sj3Adapter sj3Adapter = new Sj3Adapter(mContext, mPresenter, i, hasFlOrBannerTab);
                sj3Adapter.addSj3Adapter(adapters, tem);
                break;
            case TempletMapping.VIEW_TYPE_PW1:
                adapters.add(new Pw1Adapter(tem));
                break;
            case TempletMapping.VIEW_TYPE_VPT0:
                vpt0Adapter = new Vpt0Adapter(tem, mPresenter);
                adapters.add(vpt0Adapter);
                break;
            case TempletMapping.VIEW_TYPE_XSLB0:
                Xhlb0Adapter xhlb0Adapter = new Xhlb0Adapter(mContext, tem, mPresenter);
                adapters.add(xhlb0Adapter);
                break;
            case TempletMapping.VIEW_TYPE_LD0:
                ld0Adapter = new Ld0Adapter(tem);
                ld0Adapter.setState(mState);
                adapters.add(ld0Adapter);
                break;
            case TempletMapping.VIEW_TYPE_TM0:
                adapters.add(new Tm0Adapter(tem, mPresenter));
                break;
            case TempletMapping.VIEW_TYPE_TM1:
                adapters.add(new Tm1Adapter(tem));
                break;
            case TempletMapping.VIEW_TYPE_DB0:
                adapters.add(new Db0Adapter(tem, mFragment, mPresenter, mContext, i));
                break;
            case TempletMapping.VIEW_TYPE_DB1:
                adapters.add(new Db1Adapter(tem, mFragment, mPresenter, mContext, i));
                break;
            default:
                break;
        }
    }

    /**
     * 计算bn和fl：动态计算sj等padding
     *
     * @param templetInfos templetInfos
     * @return int
     */
    private int hasFlOrBannerTab(List<BeanTempletInfo> templetInfos) {
        List<Integer> list1 = new ArrayList<>();
        if (templetInfos != null && templetInfos.size() > 0) {
            for (BeanTempletInfo tem : templetInfos) {
                list1.add(tem.viewType);
            }
        }
        boolean hasBN0 = list1.contains(TempletMapping.VIEW_TYPE_BN0);
        boolean hasFl0 = list1.contains(TempletMapping.VIEW_TYPE_FL0);
        boolean hasVpt0 = list1.contains(TempletMapping.VIEW_TYPE_VPT0);
        boolean hasTm1 = list1.contains(TempletMapping.VIEW_TYPE_TM1);
        if (hasBN0 && hasFl0) {
            return 0;
        } else if (hasBN0 && !hasFl0) {
            return 1;
        } else if (!hasBN0 && hasFl0) {
            return 2;
        } else {
            if (hasVpt0 || hasTm1) {
                return 0;
            } else {
                return 3;
            }
        }
    }

    /**
     * 限免结束
     *
     * @param state 购买状态
     */
    public void limitFreeComplete(String state) {
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                BeanTempletInfo temp = list.get(i);
                //移除倒计时view
                if (temp != null && temp.viewType == TempletMapping.VIEW_TYPE_TM0) {
                    list.remove(temp);
                }
            }
            if (list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    //设置当前模板下的所有限免书籍皆不可领取
                    BeanTempletInfo temp = list.get(i);
                    if (temp != null && temp.viewType == TempletMapping.VIEW_TYPE_DB1) {
                        for (BeanSubTempletInfo sub : temp.items) {
                            if (sub != null && sub.action != null) {
                                sub.action.type = "0";
                                sub.action.title = "已过期";
                            }
                        }
                    }
                }
            }
            List<Adapter> adapters = covertTempletInfoToVlayoutAdapter(this.list, false);
            setAdapters(adapters);
        }
    }

    /**
     * 领取成功：修改对应的状态
     *
     * @param subTempletInfo subTempletInfo
     */
    public void getBookSuccess(BeanSubTempletInfo subTempletInfo) {
        if (list != null && list.size() > 0) {
            for (BeanTempletInfo templetInfo : list) {
                if (templetInfo != null && templetInfo.viewType == TempletMapping.VIEW_TYPE_DB1) {
                    for (BeanSubTempletInfo sub : templetInfo.items) {
                        if (sub != null && sub.action != null && sub.id.equals(subTempletInfo.id)) {
                            sub.hasGot++;
                            sub.action.type = "0";
                            if (sub.hasGot == sub.limit) {
                                sub.action.title = "已领光了";
                            } else {
                                sub.action.title = "已领取";
                            }
                        }
                    }
                }
            }
        }
    }
}
