package com.dzbook.lib.net;

/**
 * 监听
 * Created by wxliao on 17/7/10.
 */

public interface HttpListener {
    /**
     * 准备
     *
     * @param hostname host
     * @param ip       地址
     */
    void onDnsPrepare(String hostname, String ip);
}
