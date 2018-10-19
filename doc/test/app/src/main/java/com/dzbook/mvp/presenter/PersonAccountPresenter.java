package com.dzbook.mvp.presenter;

/**
 * PersonAccountPresenter
 * @author dongdianzhou on 2017/4/6.
 */
public interface PersonAccountPresenter {

    /**
     * 充值
     */
    void dzRechargePay();

    /**
     * 充值记录
     */
    void intentToRechargeRecord();

    /**
     * 有效期
     */
    void intentToVouchersList();

    /**
     * 消费记录
     */
    void intentToConsumeRecordActivity();
}
