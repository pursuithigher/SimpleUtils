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

import com.dzbook.filedownloader.connection.FileDownloadConnection;
import com.dzbook.filedownloader.util.FileDownloadProperties;
import com.dzbook.filedownloader.util.FileDownloadUtils;

import java.net.ProtocolException;

/**
 * The connection profile for {@link com.dzbook.filedownloader.download.ConnectTask}.
 */
public class ConnectionProfile {

    static final int RANGE_INFINITE = -1;

    final long startOffset;
    final long currentOffset;
    final long endOffset;
    final long contentLength;

    private final boolean isForceNoRange;

    private final boolean isTrialConnect;

    /**
     * This construct is just for build trial connection profile.
     */
    private ConnectionProfile() {
        this.startOffset = 0;
        this.currentOffset = 0;
        this.endOffset = 0;
        this.contentLength = 0;

        this.isForceNoRange = false;
        this.isTrialConnect = true;
    }

    private ConnectionProfile(long startOffset, long currentOffset, long endOffset,
                              long contentLength) {
        this(startOffset, currentOffset, endOffset, contentLength, false);
    }

    private ConnectionProfile(long startOffset, long currentOffset, long endOffset,
                              long contentLength,
                              boolean isForceNoRange) {
        if ((startOffset != 0 || endOffset != 0) && isForceNoRange) {
            throw new IllegalArgumentException();
        }

        this.startOffset = startOffset;
        this.currentOffset = currentOffset;
        this.endOffset = endOffset;
        this.contentLength = contentLength;
        this.isForceNoRange = isForceNoRange;
        this.isTrialConnect = false;
    }

    /**
     * 构造
     *
     * @param connection 连接
     * @throws ProtocolException 异常
     */
    public void processProfile(FileDownloadConnection connection) throws ProtocolException {
        if (isForceNoRange) {
            return;
        }

        if (isTrialConnect && FileDownloadProperties.getImpl().trialConnectionHeadMethod) {
            connection.setRequestMethod("HEAD");
        }

        final String range;
        if (endOffset == RANGE_INFINITE) {
            range = FileDownloadUtils.formatString("bytes=%d-", currentOffset);
        } else {
            range = FileDownloadUtils
                    .formatString("bytes=%d-%d", currentOffset, endOffset);
        }
        connection.addHeader("Range", range);
    }

    @Override
    public String toString() {
        return FileDownloadUtils.formatString("range[%d, %d) current offset[%d]",
                startOffset, endOffset, currentOffset);
    }

    /**
     * build模式
     */
    public static class ConnectionProfileBuild {

        /**
         * 创两构造
         *
         * @return 连接实例
         */
        public static ConnectionProfile buildTrialConnectionProfile() {
            return new ConnectionProfile();
        }

        /**
         * 构造
         *
         * @return 连接实例
         */
        public static ConnectionProfile buildTrialConnectionProfileNoRange() {
            return new ConnectionProfile(0, 0, 0, 0, true);
        }

        /**
         * 构造
         *
         * @param contentLength 长度
         * @return 连接实例
         */
        public static ConnectionProfile buildBeginToEndConnectionProfile(long contentLength) {
            return new ConnectionProfile(0, 0, RANGE_INFINITE, contentLength);
        }

        /**
         * 构造
         *
         * @param startOffset   开始位置
         * @param currentOffset 当前位置
         * @param contentLength 内容长度
         * @return 连接实例
         */
        public static ConnectionProfile buildToEndConnectionProfile(long startOffset,
                                                                    long currentOffset,
                                                                    long contentLength) {
            return new ConnectionProfile(startOffset, currentOffset, RANGE_INFINITE, contentLength);
        }

        /**
         * 构造
         *
         * @param startOffset   开始长度
         * @param currentOffset 当前长度
         * @param endOffset     最后长度
         * @param contentLength 内容长度
         * @return 连接实例
         */
        public static ConnectionProfile buildConnectionProfile(long startOffset,
                                                               long currentOffset,
                                                               long endOffset,
                                                               long contentLength) {
            return new ConnectionProfile(startOffset, currentOffset, endOffset, contentLength);
        }
    }
}
