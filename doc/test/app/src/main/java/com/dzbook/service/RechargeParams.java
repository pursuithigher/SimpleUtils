package com.dzbook.service;

import com.dzbook.database.bean.BookInfo;

/**
 * 将充值需要的参数 封装成一个类中
 *
 * @author lizhongzhong 2015/9/11.
 */
public class RechargeParams {

    /**
     * SINGLE
     */
    public static final String READACTION_SINGLE = "1";

    /**
     * 是否来源于阅读器 true(是) false(否)
     */
    public boolean isReader;

    /**
     * 来源区域
     */
    public String partFrom;

    /**
     * 触发操作 1：单章 4:批量下载
     */
    private String readAction;

    /**
     * 操作来源详情
     * {@see com.dzbook.log.SourceFrom}
     */
    private String operateFrom;

    /**
     * 构造
     *
     * @param readAction readAction
     * @param bookInfo   bookInfo
     */
    public RechargeParams(String readAction, BookInfo bookInfo) {
        this.readAction = readAction;
    }

    public String getOperateFrom() {
        return operateFrom;
    }

    public void setOperateFrom(String operateFrom) {
        this.operateFrom = operateFrom;
    }

    public String getPartFrom() {
        return partFrom;
    }

    public void setPartFrom(String partFrom) {
        this.partFrom = partFrom;
    }

    public String getReadAction() {
        return readAction;
    }

    public boolean isReader() {
        return isReader;
    }

    public void setReader(boolean reader) {
        isReader = reader;
    }
}
