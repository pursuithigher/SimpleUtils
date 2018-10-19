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
package com.dzbook.filedownloader.util;

import android.app.Notification;

import com.dzbook.filedownloader.FileDownloader;
import com.dzbook.filedownloader.model.FileDownloadStatus;

/**
 * The helper for handling the case of requesting do something in the downloader service but the
 * downloader service isn't CONNECTED yet.
 *
 * @see FileDownloader#insureServiceBind()
 * @see FileDownloader#insureServiceBindAsync()
 */
public class DownloadServiceNotConnectedHelper {

    private static final String CAUSE = ", but the download service isn't CONNECTED yet.";
    private static final String TIPS = "\nYou can use FileDownloader#isServiceConnected() to check"
            + " whether the service has been CONNECTED, \nbesides you can use following functions"
            + " easier to control your code invoke after the service has been CONNECTED: \n"
            + "1. FileDownloader#bindService(Runnable)\n"
            + "2. FileDownloader#insureServiceBind()\n"
            + "3. FileDownloader#insureServiceBindAsync()";

    /**
     * start
     *
     * @param url             url
     * @param path            path
     * @param pathAsDirectory pathAsDirectory
     * @return boolean
     */
    public static boolean start(final String url, final String path,
                                final boolean pathAsDirectory) {
        log("request start the task([%s], [%s], [%B]) in the download service", url, path,
                pathAsDirectory);
        return false;
    }

    /**
     * pause
     *
     * @param id id
     * @return boolean
     */
    public static boolean pause(final int id) {
        log("request pause the task[%d] in the download service", id);
        return false;
    }

    /**
     * isDownloading
     *
     * @param url  url
     * @param path path
     * @return boolean
     */
    public static boolean isDownloading(final String url, final String path) {
        log("request check the task([%s], [%s]) is downloading in the download service", url, path);
        return false;
    }

    /**
     * getSofar
     *
     * @param id id
     * @return long
     */
    public static long getSofar(final int id) {
        log("request get the downloaded so far byte for the task[%d] in the download service", id);
        return 0;
    }

    /**
     * getTotal
     *
     * @param id id
     * @return long
     */
    public static long getTotal(final int id) {
        log("request get the total byte for the task[%d] in the download service", id);
        return 0;
    }

    /**
     * getStatus
     *
     * @param id id
     * @return byte
     */
    public static byte getStatus(final int id) {
        log("request get the status for the task[%d] in the download service", id);
        return FileDownloadStatus.INVALID_STATUS;
    }

    /**
     * pauseAllTasks
     */
    public static void pauseAllTasks() {
        log("request pause all tasks in the download service");
    }

    /**
     * isIdle
     *
     * @return boolean
     */
    public static boolean isIdle() {
        log("request check the download service is idle");
        return true;
    }

    /**
     * startForeground
     *
     * @param notificationId notificationId
     * @param notification   notification
     */
    public static void startForeground(int notificationId, Notification notification) {
        log("request set the download service as the foreground service([%d],[%s]),",
                notificationId, notification);
    }

    /**
     * stopForeground
     *
     * @param removeNotification removeNotification
     */
    public static void stopForeground(boolean removeNotification) {
        log("request cancel the foreground status[%B] for the download service",
                removeNotification);
    }

    /**
     * setMaxNetworkThreadCount
     *
     * @param count count
     * @return boolean
     */
    public static boolean setMaxNetworkThreadCount(int count) {
        log("request set the max network thread count[%d] in the download service", count);
        return false;
    }

    /**
     * clearTaskData
     *
     * @param id id
     * @return boolean
     */
    public static boolean clearTaskData(int id) {
        log("request clear the task[%d] data in the database", id);
        return false;
    }

    /**
     * clearAllTaskData
     *
     * @return boolean
     */
    public static boolean clearAllTaskData() {
        log("request clear all tasks data in the database");
        return false;
    }

    private static void log(String message, Object... args) {
        com.dzbook.filedownloader.util.FileDownloadLog.w(DownloadServiceNotConnectedHelper.class, message + CAUSE + TIPS, args);
    }

}
