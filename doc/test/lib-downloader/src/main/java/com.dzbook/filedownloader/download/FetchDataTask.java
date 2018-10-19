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

import android.os.SystemClock;

import com.dzbook.filedownloader.connection.FileDownloadConnection;
import com.dzbook.filedownloader.database.FileDownloadDatabase;
import com.dzbook.filedownloader.exception.FileDownloadGiveUpRetryException;
import com.dzbook.filedownloader.exception.FileDownloadNetworkPolicyException;
import com.dzbook.filedownloader.stream.FileDownloadOutputStream;
import com.dzbook.filedownloader.util.FileDownloadLog;
import com.dzbook.filedownloader.util.FileDownloadUtils;

import java.io.IOException;
import java.io.InputStream;

import static com.dzbook.filedownloader.model.FileDownloadModel.TOTAL_VALUE_IN_CHUNKED_RESOURCE;

/**
 * Fetch data from the provided connection.
 */
public class FetchDataTask {

    static final int BUFFER_SIZE = 1024 * 4;
    long currentOffset;

    long startTimestamp;
    boolean bufferPersistToDevice;
    private final ProcessCallback callback;

    private final int downloadId;
    private final int connectionIndex;
    private final DownloadRunnable hostRunnable;
    private final FileDownloadConnection connection;
    private final boolean isWifiRequired;

    private final long startOffset;
    private final long endOffset;
    private final long contentLength;

    private final String path;

    private FileDownloadOutputStream outputStream;

    private volatile boolean paused;

    private final FileDownloadDatabase database;
    private volatile long lastSyncBytes = 0;
    private volatile long lastSyncTimestamp = 0;


    private FetchDataTask(FileDownloadConnection connection, ConnectionProfile connectionProfile,
                          DownloadRunnable host, int id, int connectionIndex,
                          boolean isWifiRequired, ProcessCallback callback, String path) {
        this.callback = callback;
        this.path = path;
        this.connection = connection;
        this.isWifiRequired = isWifiRequired;
        this.hostRunnable = host;
        this.connectionIndex = connectionIndex;
        this.downloadId = id;
        this.database = CustomComponentHolder.getImpl().getDatabaseInstance();

        startOffset = connectionProfile.startOffset;
        endOffset = connectionProfile.endOffset;
        currentOffset = connectionProfile.currentOffset;
        contentLength = connectionProfile.contentLength;
    }


    /**
     * 暂停
     */
    public void pause() {
        paused = true;
    }

    /**
     * 运行
     *
     * @throws Exception                        异常
     * @throws IllegalArgumentException         异常
     * @throws FileDownloadGiveUpRetryException 异常
     */
    public void run() throws Exception, IllegalArgumentException,
            FileDownloadGiveUpRetryException {

        if (paused) {
            return;
        }

        long aContentLength = checkContentLength();

        ifConentLengthCanuse(aContentLength);

        final long fetchBeginOffset = currentOffset;
        // start fetch
        InputStream inputStream = null;
        FileDownloadOutputStream aOutputStream = null;

        try {
            final boolean isSupportSeek = CustomComponentHolder.getImpl().isSupportSeek();
            if (hostRunnable != null && !isSupportSeek) {
                throw new IllegalAccessException(
                        "can't using multi-download when the output stream can't support seek");
            }

            this.outputStream = aOutputStream = FileDownloadUtils.createOutputStream(path);
            if (isSupportSeek) {
                aOutputStream.seek(currentOffset);
            }

            inputStream = connection.getInputStream();

            byte[] buff = new byte[BUFFER_SIZE];

            if (paused) {
                return;
            }

            do {
                int byteCount = inputStream.read(buff);
                if (byteCount == -1) {
                    break;
                }

                aOutputStream.write(buff, 0, byteCount);

                currentOffset += byteCount;

                // callback PROGRESS
                callback.onProgress(byteCount);

                checkAndSync();

                // check status
                if (paused) {
                    return;
                }

                if (isWifiRequired && FileDownloadUtils.isNetworkNotOnWifiType()) {
                    throw new FileDownloadNetworkPolicyException();
                }

            } while (true);

        } finally {

            closeStream(inputStream, aOutputStream);

        }

        handleFetchedLength(aContentLength, fetchBeginOffset);

        // callback completed
        callback.onCompleted(hostRunnable, startOffset, endOffset);
    }


    private void handleFetchedLength(long aContentLength, long fetchBeginOffset) {
        final long fetchedLength = currentOffset - fetchBeginOffset;
        if (aContentLength != TOTAL_VALUE_IN_CHUNKED_RESOURCE && aContentLength != fetchedLength) {
            throw new FileDownloadGiveUpRetryException(
                    FileDownloadUtils.formatString("fetched length[%d] != content length[%d],"
                                    + " range[%d, %d) offset[%d] fetch begin offset[%d]",
                            fetchedLength, aContentLength,
                            startOffset, endOffset, currentOffset, fetchBeginOffset));
        }
    }


    private void closeStream(InputStream inputStream, FileDownloadOutputStream aOutputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            if (aOutputStream != null) {
                sync();
            }
        } finally {
            if (aOutputStream != null) {
                try {
                    aOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void ifConentLengthCanuse(long aContentLength) {
        if (this.contentLength > 0 && aContentLength != this.contentLength) {
            final String range;
            if (endOffset == ConnectionProfile.RANGE_INFINITE) {
                range = FileDownloadUtils.formatString("range[%d-)", currentOffset);
            } else {
                range = FileDownloadUtils.formatString("range[%d-%d)", currentOffset, endOffset);
            }
            throw new FileDownloadGiveUpRetryException(FileDownloadUtils.
                    formatString("require %s with contentLength(%d), but the "
                                    + "backend response contentLength is %d on "
                                    + "downloadId[%d]-connectionIndex[%d], please ask your backend "
                                    + "dev to fix such problem.",
                            range, this.contentLength, aContentLength, downloadId, connectionIndex));
        }
    }

    private long checkContentLength() {
        long aContentLength = FileDownloadUtils.findContentLength(connectionIndex, connection);
        if (aContentLength == TOTAL_VALUE_IN_CHUNKED_RESOURCE) {
            aContentLength = FileDownloadUtils.findContentLengthFromContentRange(connection);
        }
        if (aContentLength == 0) {
            throw new FileDownloadGiveUpRetryException(FileDownloadUtils.
                    formatString(
                            "there isn't any content need to download on %d-%d with the "
                                    + "content-length is 0",
                            downloadId, connectionIndex));
        }
        return aContentLength;
    }

    private void checkAndSync() {
        final long now = SystemClock.elapsedRealtime();
        final long bytesDelta = currentOffset - lastSyncBytes;
        final long timestampDelta = now - lastSyncTimestamp;

        if (FileDownloadUtils.isNeedSync(bytesDelta, timestampDelta)) {
            sync();

            lastSyncBytes = currentOffset;
            lastSyncTimestamp = now;
        }
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    private void sync() {
        setStartTimestamp(SystemClock.uptimeMillis());
        bufferPersistToDevice = false;
        try {
            outputStream.flushAndSync();
            bufferPersistToDevice = true;
        } catch (IOException e) {
            bufferPersistToDevice = false;
            if (FileDownloadLog.NEED_LOG) {
                FileDownloadLog.d(this, "Because of the system cannot guarantee that all "
                        + "the buffers have been synchronized with physical media, or write to file"
                        + "failed, we just not flushAndSync process to database too %s", e);
            }
        }

        if (bufferPersistToDevice) {
            final boolean isBelongMultiConnection = connectionIndex >= 0;
            if (isBelongMultiConnection) {
                // only need update the connection table.
                database.updateConnectionModel(downloadId, connectionIndex, currentOffset);
            } else {
                // only need update the filedownloader table.
                callback.syncProgressFromCache();
            }

            if (FileDownloadLog.NEED_LOG) {
                FileDownloadLog
                        .d(this, "require flushAndSync id[%d] index[%d] offset[%d], consume[%d]",
                                downloadId, connectionIndex, currentOffset,
                                SystemClock.uptimeMillis() - getStartTimestamp());
            }
        }
    }

    /**
     * build构造
     */
    public static class Builder {
        DownloadRunnable downloadRunnable;
        FileDownloadConnection connection;
        ConnectionProfile connectionProfile;
        ProcessCallback callback;
        String path;
        Boolean isWifiRequired;
        Integer connectionIndex;
        Integer downloadId;

        /**
         * 设置连接
         *
         * @param aConnection 连接
         * @return build构造
         */
        public Builder setConnection(FileDownloadConnection aConnection) {
            this.connection = aConnection;
            return this;
        }

        /**
         * 设置profile
         *
         * @param aConnectionProfile profile
         * @return build构造
         */
        public Builder setConnectionProfile(ConnectionProfile aConnectionProfile) {
            this.connectionProfile = aConnectionProfile;
            return this;
        }

        /**
         * 设置回调
         *
         * @param aCallback 回调
         * @return build构造
         */
        public Builder setCallback(ProcessCallback aCallback) {
            this.callback = aCallback;
            return this;
        }

        /**
         * 设置路径
         *
         * @param aPath 路径
         * @return build构造
         */
        public Builder setPath(String aPath) {
            this.path = aPath;
            return this;
        }

        /**
         * 设置wifi
         *
         * @param wifiRequired wifi
         * @return build构造
         */
        public Builder setWifiRequired(boolean wifiRequired) {
            isWifiRequired = wifiRequired;
            return this;
        }

        /**
         * 设置host
         *
         * @param aDownloadRunnable 下载runnable
         * @return build构造
         */
        public Builder setHost(DownloadRunnable aDownloadRunnable) {
            this.downloadRunnable = aDownloadRunnable;
            return this;
        }

        /**
         * 设置连接位序
         *
         * @param aConnectionIndex connectionIndex
         * @return build构造
         */
        public Builder setConnectionIndex(int aConnectionIndex) {
            this.connectionIndex = aConnectionIndex;
            return this;
        }

        /**
         * 设置下载id
         *
         * @param aDownloadId downloadId
         * @return build构造
         */
        public Builder setDownloadId(int aDownloadId) {
            this.downloadId = aDownloadId;
            return this;
        }

        /**
         * build 构造
         *
         * @return build
         * @throws IllegalArgumentException 参数异常
         */
        public FetchDataTask build() throws IllegalArgumentException {
            boolean result = isWifiRequired == null || connection == null || connectionProfile == null;
            if (result || callback == null || path == null || downloadId == null
                    || connectionIndex == null) {
                throw new IllegalArgumentException();
            }

            return new FetchDataTask(connection, connectionProfile, downloadRunnable,
                    downloadId, connectionIndex,
                    isWifiRequired, callback, path);
        }

    }
}
