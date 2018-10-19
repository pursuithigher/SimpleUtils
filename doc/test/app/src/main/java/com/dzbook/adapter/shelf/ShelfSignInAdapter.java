package com.dzbook.adapter.shelf;

import android.content.Context;
import android.view.ViewGroup;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.dzbook.utils.SpUtil;
import com.dzbook.view.shelf.ShelfSignInView;

import hw.sdk.net.bean.shelf.BeanBookUpdateInfo;

/**
 * Adapter
 */
public class ShelfSignInAdapter extends DelegateAdapter.Adapter<ShelfViewHolder> {

    private Context mContext;
    private BeanBookUpdateInfo updateInfo;

    /**
     * 构造
     *
     * @param context    context
     * @param updateInfo updateInfo
     */
    public ShelfSignInAdapter(Context context, BeanBookUpdateInfo updateInfo) {
        mContext = context;
        this.updateInfo = updateInfo;
    }

    /**
     * 刷新签到状态：签到完成和服务器同步签到完成
     *
     * @param info updateInfo
     */
    public void referenceSignInStatus(BeanBookUpdateInfo info) {
        if (info != null) {
            this.updateInfo = info;
        } else if (this.updateInfo == null) {
            this.updateInfo = new BeanBookUpdateInfo();
        }
        // 良坤建议：每日签到用SP存储的值，并以其为唯一标准。
        this.updateInfo.hasSignIn = SpUtil.getinstance(mContext).hasMarkTodayByKey(SpUtil.SP_USER_SIGN) ? 1 : 0;
        this.notifyDataSetChanged();
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        return new LinearLayoutHelper();
    }

    @Override
    public ShelfViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ShelfViewHolder(new ShelfSignInView(mContext));
    }

    @Override
    public void onBindViewHolder(ShelfViewHolder holder, int position) {
        if (updateInfo != null) {
            holder.bindSignInData(updateInfo);
        }
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        return ShelfConstant.SHELF_VIEW_TYPE_SIGNIN;
    }
}
