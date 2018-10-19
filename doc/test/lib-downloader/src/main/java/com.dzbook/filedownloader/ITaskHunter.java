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

import com.dzbook.filedownloader.message.MessageSnapshot;

/**
 * The downloading task hunter.
 */

public interface ITaskHunter extends IDownloadSpeed.Lookup {
    /**
     * Enter into the launch pool.
     *
     * @see FileDownloadTaskLauncher
     */
    void intoLaunchPool();

    /**
     * 暂停
     *
     * @return Whether pause the task successfully.
     */
    boolean pause();

    /**
     * 获取状态
     *
     * @return the status.
     * @see com.dzbook.filedownloader.model.FileDownloadStatus
     */
    byte getStatus();

    /**
     * Reset the hunter.
     */
    void reset();

    /**
     * 获取字节
     *
     * @return The so far downloaded bytes.
     */
    long getSofarBytes();

    /**
     * 获取字节
     *
     * @return The total bytes.
     */
    long getTotalBytes();

    /**
     * 获取错误
     *
     * @return {@code Null} if has didn't occurred any error yet.
     */
    Throwable getErrorCause();

    /**
     * 获取时间
     *
     * @return The currently retrying times.
     */
    int getRetryingTimes();

    /**
     * 获取文件
     *
     * @return {@code true} if didn't real start downloading but the old-file with target-path is
     * exist, and just reuse it. {@code false} otherwise.
     */
    boolean isReusedOldFile();

    /**
     * 正在运行判断
     *
     * @return {@code true} if the currently downloading is the downloading resuming from the
     * breakpoint. {@code false} downloading from the beginning.
     */
    boolean isResuming();

    /**
     * 获取etag
     *
     * @return The Etag from the response's header.
     */
    String getEtag();

    /**
     * 是否大文件
     *
     * @return {@code true} if the file length is large than 1.99G, {@code false} otherwise.
     */
    boolean isLargeFile();

    /**
     * Free the current hunter.
     */
    void free();

    /**
     * IStarter
     *
     * @see FileDownloadTaskLauncher
     * <p>
     * The starter for the downloading task.
     */
    interface IStarter {
        /**
         * Start the task in the launcher thread.
         */
        void start();

        /**
         * equalListener
         *
         * @param listener The downloading listener.
         * @return {@code true} if {@code listener} equal to the listener of the current task.
         */
        boolean equalListener(FileDownloadListener listener);
    }

    /**
     * The message handler for a task.
     */
    interface IMessageHandler {

        /**
         * Try to dispatch the {@code snapshot} with the keep ahead policy.
         *
         * @param snapshot the received message snapshot.
         * @return {@code true} the message has been dispatched successfully.
         */
        boolean updateKeepAhead(final MessageSnapshot snapshot);

        /**
         * Try to dispatch the {@code snapshot} with the keep right flow policy.
         *
         * @param snapshot the received message snapshot.
         * @return {@code true} the message has been dispatched successfully.
         */
        boolean updateKeepFlow(final MessageSnapshot snapshot);

        /**
         * Try to dispatch the {@code snapshot} with the more likely completed policy.
         * <p>
         * The more likely completed policy: in some case the snapshot more likely to a waiting to
         * complete task.
         *
         * @param snapshot the received message snapshot.
         * @return {@code true} the message has been dispatched successfully.
         */
        boolean updateMoreLikelyCompleted(final MessageSnapshot snapshot);

        /**
         * Try to dispatch the {@code snapshot} with the same file path policy.
         * <p>
         * The same file path policy: when the path provided by the user is a directory, we find the
         * filename until the response from the service has been received, in this case when we find
         * the target file path maybe can equal to another running task, so in this case, this task
         * need callback a warn message.
         *
         * @param snapshot the received message snapshot.
         * @return {@code true} the message has been dispatched successfully.
         */
        boolean updateSameFilePathTaskRunning(final MessageSnapshot snapshot);

        /**
         * 获取错误信息
         *
         * @return The messenger for the message handler.
         */
        IFileDownloadMessenger getMessenger();

        /**
         * 解析错误码
         *
         * @param cause The cause of occurred exception.
         * @return This message snapshot capture from the {@code cause}.
         */
        MessageSnapshot prepareErrorMessage(Throwable cause);
    }
}
