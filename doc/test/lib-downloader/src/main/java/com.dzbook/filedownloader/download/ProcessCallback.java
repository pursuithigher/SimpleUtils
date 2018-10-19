/*
 * Copyright (c) 2015 LingoChamp Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dzbook.filedownloader.download;

/**
 * The process event callbacks.
 */
public interface ProcessCallback {

    /**
     * 进度
     *
     * @param increaseBytes increaseBytes
     */
    void onProgress(long increaseBytes);

    /**
     * 完成
     *
     * @param doneRunnable doneRunnable
     * @param startOffset  startOffset
     * @param endOffset    endOffset
     */
    void onCompleted(DownloadRunnable doneRunnable, long startOffset, long endOffset);

    /**
     * isRetry
     *
     * @param exception 异常
     * @return boolean
     */
    boolean isRetry(Exception exception);

    /**
     * 错误
     *
     * @param exception 异常
     */
    void onError(Exception exception);

    /**
     * retry
     *
     * @param exception 异常
     */
    void onRetry(Exception exception);

    /**
     * 进度从缓存
     */
    void syncProgressFromCache();
}
