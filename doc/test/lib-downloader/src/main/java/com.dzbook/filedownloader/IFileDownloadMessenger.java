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

import com.dzbook.filedownloader.download.DownloadStatusCallback;
import com.dzbook.filedownloader.message.MessageSnapshot;
import com.dzbook.filedownloader.util.FileDownloadHelper;

/**
 * 文件下载信息
 *
 * @see com.dzbook.filedownloader.model.FileDownloadStatus
 */
interface IFileDownloadMessenger {

    /**
     * The task is just received to handle.
     * <p/>
     * FileDownloader accept the task.
     *
     * @return Whether allow it to begin.
     */
    boolean notifyBegin();

    /**
     * The task is pending.
     * <p/>
     * enqueue, and pending, waiting.
     *
     * @see com.dzbook.filedownloader.services.FileDownloadThreadPool
     */
    void notifyPending(MessageSnapshot snapshot);

    /**
     * The download runnable of the task has STARTED running.
     * <p/>
     * Finish pending, and start download runnable.
     *
     * @see DownloadStatusCallback#onStartThread()
     */
    void notifyStarted(MessageSnapshot snapshot);

    /**
     * The task is running.
     * <p/>
     * Already CONNECTED to the server, and received the Http-response.
     *
     * @see DownloadStatusCallback#onConnected(boolean, long, String, String)
     */
    void notifyConnected(MessageSnapshot snapshot);

    /**
     * The task is running.
     * <p/>
     * Fetching datum, and write to local disk.
     *
     * @see DownloadStatusCallback#onProgress(long)
     */
    void notifyProgress(MessageSnapshot snapshot);

    /**
     * The task is running.
     * <p/>
     * Already completed download, and block the current thread to do something, such as unzip,etc.
     *
     * @see DownloadStatusCallback#onCompletedDirectly()
     */
    void notifyBlockComplete(MessageSnapshot snapshot);

    /**
     * The task over.
     * <p/>
     * Occur a exception when downloading, but has retry
     * chance {@link com.dzbook.filedownloader.BaseDownloadTask#setAutoRetryTimes(int)}, so retry(re-connect,re-download).
     */
    void notifyRetry(MessageSnapshot snapshot);

    /**
     * The task over.
     * <p/>
     * There has already had some same Tasks(Same-URL & Same-SavePath) in Pending-Queue or is
     * running.
     *
     * @see FileDownloadHelper#inspectAndInflowDownloading
     */
    void notifyWarn(MessageSnapshot snapshot);

    /**
     * The task is over.
     * <p/>
     * Occur a exception, but don't has any chance to retry.
     *
     * @see DownloadStatusCallback#onErrorDirectly(Exception)
     * @see com.dzbook.filedownloader.exception.FileDownloadHttpException
     * @see com.dzbook.filedownloader.exception.FileDownloadOutOfSpaceException
     * @see com.dzbook.filedownloader.exception.FileDownloadGiveUpRetryException
     */
    void notifyError(MessageSnapshot snapshot);

    /**
     * The task is over.
     * <p/>
     * Pause manually by {@link com.dzbook.filedownloader.BaseDownloadTask#pause()}.
     *
     * @see com.dzbook.filedownloader.BaseDownloadTask#pause()
     */
    void notifyPaused(MessageSnapshot snapshot);

    /**
     * The task is over.
     * <p/>
     * Achieve complete ceremony.
     *
     * @see DownloadStatusCallback#onCompletedDirectly()
     */
    void notifyCompleted(MessageSnapshot snapshot);

    /**
     * Handover a message to {@link com.dzbook.filedownloader.FileDownloadListener}.
     */
    void handoverMessage();

    /**
     * handoverDirectly
     *
     * @return {@code true} if handover a message to {@link com.dzbook.filedownloader.FileDownloadListener} directly(do not
     * need to post the callback to the main thread).
     * @see com.dzbook.filedownloader.BaseDownloadTask#isSyncCallback()
     */
    boolean handoverDirectly();

    /**
     * reAppointment
     *
     * @param task Re-appointment for this task, when this messenger has already accomplished the
     *             old one.
     */
    void reAppointment(com.dzbook.filedownloader.BaseDownloadTask.IRunningTask task,
                       BaseDownloadTask.LifeCycleCallback callback);

    /**
     * The 'block completed'(status) message will be handover in the non-UI thread and block the
     * 'completed'(status) message.
     *
     * @return {@code true} if the status of the current message is
     * {@link com.dzbook.filedownloader.model.FileDownloadStatus#BLOCK_COMPLETE}.
     */
    boolean isBlockingCompleted();

    /**
     * Discard this messenger.
     * <p>
     * If this messenger is discarded, all messages sent by this messenger or feature messages
     * handled by this messenger will be discard, no longer callback to the target Listener.
     */
    void discard();
}
