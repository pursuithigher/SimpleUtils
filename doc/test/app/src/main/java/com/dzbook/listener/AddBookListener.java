package com.dzbook.listener;

/**
 * 添加图书监听
 *
 * @author caimantang on 2018/4/23.
 */
public interface AddBookListener {
    /**
     * 成功
     */
    void success();

    /**
     * 失败
     */
    void fail();
}
