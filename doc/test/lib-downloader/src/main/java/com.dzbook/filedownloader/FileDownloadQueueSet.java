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

import java.util.ArrayList;
import java.util.List;

/**
 * The helper for start and config the task queue simply and quickly.
 *
 * @see com.dzbook.filedownloader.FileDownloader#start(com.dzbook.filedownloader.FileDownloadListener, boolean)
 */
@SuppressWarnings({"SameParameterValue", "WeakerAccess"})
public class FileDownloadQueueSet {

    private com.dzbook.filedownloader.FileDownloadListener target;
    private boolean isSerial;


    private List<com.dzbook.filedownloader.BaseDownloadTask.FinishListener> taskFinishListenerList;
    private Integer autoRetryTimes;
    private Boolean syncCallback;
    private Boolean isForceReDownload;
    private Boolean isWifiRequired;
    private Integer callbackProgressTimes;
    private Integer callbackProgressMinIntervalMillis;
    private Object tag;
    private String directory;

    private com.dzbook.filedownloader.BaseDownloadTask[] tasks;

    /**
     * FileDownloadQueueSet
     *
     * @param target The download listener will be set to all tasks in this queue set.
     */
    public FileDownloadQueueSet(com.dzbook.filedownloader.FileDownloadListener target) {
        if (target == null) {
            throw new IllegalArgumentException(
                    "create FileDownloadQueueSet must with valid target!");
        }
        this.target = target;
    }

    /**
     * Form a queue with same {@link #target} and will {@link #start()} in parallel.
     *
     * @param aTasks 任务数组
     * @return FileDownloadQueueSet
     */
    public FileDownloadQueueSet downloadTogether(com.dzbook.filedownloader.BaseDownloadTask... aTasks) {
        this.isSerial = false;
        this.tasks = aTasks;

        return this;

    }

    /**
     * Form a queue with same {@link #target} and will {@link #start()} in parallel.
     *
     * @param aTasks 任务数组
     * @return FileDownloadQueueSet
     */
    public FileDownloadQueueSet downloadTogether(List<com.dzbook.filedownloader.BaseDownloadTask> aTasks) {
        this.isSerial = false;
        this.tasks = new com.dzbook.filedownloader.BaseDownloadTask[aTasks.size()];
        aTasks.toArray(this.tasks);

        return this;

    }

    /**
     * Form a queue with same {@link #target} and will {@link #start()} linearly.
     *
     * @param aTasks 多任务
     * @return FileDownloadQueueSet
     */
    public FileDownloadQueueSet downloadSequentially(com.dzbook.filedownloader.BaseDownloadTask... aTasks) {
        this.isSerial = true;
        this.tasks = aTasks;

        return this;
    }

    /**
     * Form a queue with same {@link #target} and will {@link #start()} linearly.
     *
     * @param aTasks 多任务
     * @return FileDownloadQueueSet
     */
    public FileDownloadQueueSet downloadSequentially(List<com.dzbook.filedownloader.BaseDownloadTask> aTasks) {
        this.isSerial = true;
        this.tasks = new com.dzbook.filedownloader.BaseDownloadTask[aTasks.size()];
        aTasks.toArray(this.tasks);

        return this;
    }

    /**
     * Before starting downloading tasks in this queue-set, we will try to
     * {@link com.dzbook.filedownloader.BaseDownloadTask#reuse} tasks first.
     */
    public void reuseAndStart() {
        for (com.dzbook.filedownloader.BaseDownloadTask task : tasks) {
            task.reuse();
        }
        start();
    }

    /**
     * Start tasks in a queue.
     *
     * @see #downloadSequentially(com.dzbook.filedownloader.BaseDownloadTask...)
     * @see #downloadSequentially(List)
     * @see #downloadTogether(com.dzbook.filedownloader.BaseDownloadTask...)
     * @see #downloadTogether(List)
     */
    public void start() {
        for (com.dzbook.filedownloader.BaseDownloadTask task : tasks) {
            task.setListener(target);

            if (autoRetryTimes != null) {
                task.setAutoRetryTimes(autoRetryTimes);
            }

            if (syncCallback != null) {
                task.setSyncCallback(syncCallback);
            }

            if (isForceReDownload != null) {
                task.setForceReDownload(isForceReDownload);
            }

            if (callbackProgressTimes != null) {
                task.setCallbackProgressTimes(callbackProgressTimes);
            }

            if (callbackProgressMinIntervalMillis != null) {
                task.setCallbackProgressMinInterval(callbackProgressMinIntervalMillis);
            }

            if (tag != null) {
                task.setTag(tag);
            }

            if (taskFinishListenerList != null) {
                for (com.dzbook.filedownloader.BaseDownloadTask.FinishListener finishListener : taskFinishListenerList) {
                    task.addFinishListener(finishListener);
                }
            }

            if (this.directory != null) {
                task.setPath(this.directory, true);
            }

            if (this.isWifiRequired != null) {
                task.setWifiRequired(this.isWifiRequired);
            }

            task.asInQueueTask().enqueue();
        }

        FileDownloader.getImpl().start(target, isSerial);
    }

    /**
     * setDirectory
     *
     * @param aDirectory Set the {@code directory} to store files in this queue.
     *                   All tasks in this queue will be invoked
     *                   {@link com.dzbook.filedownloader.BaseDownloadTask#setPath(String, boolean)} with params:
     *                   ({@code directory}, {@code true}).
     * @return FileDownloadQueueSet
     */
    public FileDownloadQueueSet setDirectory(String aDirectory) {
        this.directory = aDirectory;
        return this;
    }

    /**
     * setAutoRetryTimes
     *
     * @param aAutoRetryTimes aAutoRetryTimes
     * @return FileDownloadQueueSet
     * @see com.dzbook.filedownloader.BaseDownloadTask#setAutoRetryTimes(int)
     */
    public FileDownloadQueueSet setAutoRetryTimes(int aAutoRetryTimes) {
        this.autoRetryTimes = aAutoRetryTimes;
        return this;
    }

    /**
     * setSyncCallback
     *
     * @param aSyncCallback aSyncCallback
     * @return FileDownloadQueueSet
     * @see com.dzbook.filedownloader.BaseDownloadTask#setSyncCallback(boolean)
     */
    public FileDownloadQueueSet setSyncCallback(final boolean aSyncCallback) {
        this.syncCallback = aSyncCallback;
        return this;
    }

    /**
     * setForceReDownload
     *
     * @param aIsForceReDownload aIsForceReDownload
     * @return FileDownloadQueueSet
     * @see com.dzbook.filedownloader.BaseDownloadTask#setForceReDownload(boolean)
     */
    public FileDownloadQueueSet setForceReDownload(final boolean aIsForceReDownload) {
        this.isForceReDownload = aIsForceReDownload;
        return this;
    }

    /**
     * setCallbackProgressTimes
     *
     * @param aCallbackProgressTimes aCallbackProgressTimes
     * @return FileDownloadQueueSet
     * @see com.dzbook.filedownloader.BaseDownloadTask#setCallbackProgressTimes(int)
     */
    public FileDownloadQueueSet setCallbackProgressTimes(final int aCallbackProgressTimes) {
        this.callbackProgressTimes = aCallbackProgressTimes;
        return this;
    }

    /**
     * setCallbackProgressMinInterval
     *
     * @param minIntervalMillis minIntervalMillis
     * @return FileDownloadQueueSet
     * @see com.dzbook.filedownloader.BaseDownloadTask#setCallbackProgressMinInterval(int)
     */
    public FileDownloadQueueSet setCallbackProgressMinInterval(int minIntervalMillis) {
        this.callbackProgressMinIntervalMillis = minIntervalMillis;
        return this;
    }

    /**
     * ignoreEachTaskInternalProgress
     *
     * @return FileDownloadQueueSet
     * @see com.dzbook.filedownloader.BaseDownloadTask#setCallbackProgressIgnored()
     */
    public FileDownloadQueueSet ignoreEachTaskInternalProgress() {
        setCallbackProgressTimes(-1);
        return this;
    }

    /**
     * disableCallbackProgressTimes
     *
     * @return FileDownloadQueueSet
     * @see com.dzbook.filedownloader.BaseDownloadTask#setCallbackProgressTimes(int)
     */
    public FileDownloadQueueSet disableCallbackProgressTimes() {
        return setCallbackProgressTimes(0);
    }

    /**
     * setTag
     *
     * @param aTag tag
     * @return FileDownloadQueueSet
     * @see com.dzbook.filedownloader.BaseDownloadTask#setTag(Object)
     */
    public FileDownloadQueueSet setTag(final Object aTag) {
        this.tag = aTag;
        return this;
    }

    /**
     * addTaskFinishListener
     *
     * @param finishListener finishListener
     * @return FileDownloadQueueSet
     * @see com.dzbook.filedownloader.BaseDownloadTask#addFinishListener(com.dzbook.filedownloader.BaseDownloadTask.FinishListener)
     */
    public FileDownloadQueueSet addTaskFinishListener(
            final com.dzbook.filedownloader.BaseDownloadTask.FinishListener finishListener) {
        if (this.taskFinishListenerList == null) {
            this.taskFinishListenerList = new ArrayList<>();
        }

        this.taskFinishListenerList.add(finishListener);
        return this;
    }

    /**
     * setWifiRequired
     *
     * @param aIsWifiRequired aIsWifiRequired
     * @return FileDownloadQueueSet
     * @see BaseDownloadTask#setWifiRequired(boolean)
     */
    public FileDownloadQueueSet setWifiRequired(boolean aIsWifiRequired) {
        this.isWifiRequired = aIsWifiRequired;
        return this;
    }

}
