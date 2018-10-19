/*
 * Copyright (c) 2015 LingoChamp Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dzbook.filedownloader;

import android.app.Notification;
import android.content.Context;

import com.dzbook.filedownloader.model.FileDownloadHeader;

/**
 * The interface to access the FileDownloadService.
 */
public interface IFileDownloadServiceProxy {
    /**
     * 开始
     *
     * @param url                               url
     * @param path                              path
     * @param pathAsDirectory                   pathAsDirectory
     * @param callbackProgressTimes             callbackProgressTimes
     * @param callbackProgressMinIntervalMillis callbackProgressMinIntervalMillis
     * @param autoRetryTimes                    autoRetryTimes
     * @param forceReDownload                   forceReDownload
     * @param header                            header
     * @param isWifiRequired                    isWifiRequired
     * @return boolean
     */
    boolean start(final String url, final String path, final boolean pathAsDirectory,
                  final int callbackProgressTimes,
                  final int callbackProgressMinIntervalMillis,
                  final int autoRetryTimes, boolean forceReDownload,
                  final FileDownloadHeader header, boolean isWifiRequired);

    /**
     * 暂停
     *
     * @param id id
     * @return boolean
     */
    boolean pause(final int id);

    /**
     * 是否正在下载
     *
     * @param url  url
     * @param path path
     * @return boolean
     */
    boolean isDownloading(final String url, final String path);

    /**
     * getSofar
     *
     * @param downloadId 下载id
     * @return long
     */
    long getSofar(final int downloadId);

    /**
     * 获取总数
     *
     * @param downloadId downloadId
     * @return long
     */
    long getTotal(final int downloadId);

    /**
     * 获取状态
     *
     * @param downloadId 下载id
     * @return byte
     */
    byte getStatus(final int downloadId);

    /**
     * pauseAllTasks
     */
    void pauseAllTasks();

    /**
     * isIdle
     *
     * @return boolean
     */
    boolean isIdle();

    /**
     * 是否连接
     *
     * @return boolean
     */
    boolean isConnected();

    /**
     * bindStartByContext
     *
     * @param context 上下文
     */
    void bindStartByContext(final Context context);

    /**
     * bindStartByContext
     *
     * @param context           上下文
     * @param connectedRunnable 连接runnable
     */
    void bindStartByContext(final Context context, final Runnable connectedRunnable);

    /**
     * 解绑
     *
     * @param context 上下文
     */
    void unbindByContext(final Context context);

    /**
     * 开始
     *
     * @param id           id
     * @param notification notification
     */
    void startForeground(int id, Notification notification);

    /**
     * 停止
     *
     * @param removeNotification removeNotification
     */
    void stopForeground(boolean removeNotification);

    /**
     * 设置最大线程数
     *
     * @param count 数据
     * @return booleann
     */
    boolean setMaxNetworkThreadCount(int count);

    /**
     * 清理任务
     *
     * @param id id
     * @return boolean
     */
    boolean clearTaskData(int id);

    /**
     * 清楚所有任务
     */
    void clearAllTaskData();
}
