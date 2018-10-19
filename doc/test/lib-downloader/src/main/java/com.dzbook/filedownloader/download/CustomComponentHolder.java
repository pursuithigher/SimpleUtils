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
import com.dzbook.filedownloader.database.FileDownloadDatabase;
import com.dzbook.filedownloader.model.FileDownloadModel;
import com.dzbook.filedownloader.model.FileDownloadStatus;
import com.dzbook.filedownloader.services.DownloadMgrInitialParams;
import com.dzbook.filedownloader.stream.FileDownloadOutputStream;
import com.dzbook.filedownloader.util.FileDownloadHelper;
import com.dzbook.filedownloader.util.FileDownloadLog;
import com.dzbook.filedownloader.util.FileDownloadUtils;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 * The holder for supported custom components.
 */
public class CustomComponentHolder {
    static long startTimestamp = 0;

    private static volatile DownloadMgrInitialParams initialParams;

    private static volatile FileDownloadHelper.ConnectionCountAdapter connectionCountAdapter;
    private static volatile FileDownloadHelper.ConnectionCreator connectionCreator;
    private static volatile FileDownloadHelper.OutputStreamCreator outputStreamCreator;
    private static volatile FileDownloadDatabase database;
    private static volatile FileDownloadHelper.IdGenerator idGenerator;

    /**
     * 加载
     */
    private static final class LazyLoader {
        private static final CustomComponentHolder INSTANCE = new CustomComponentHolder();
    }

    public static CustomComponentHolder getImpl() {
        return LazyLoader.INSTANCE;
    }

    /**
     * 构造
     *
     * @param initCustomMaker 构造参数
     */
    public void setInitCustomMaker(DownloadMgrInitialParams.InitCustomMaker initCustomMaker) {
        synchronized (this) {
            staticInit(initCustomMaker);
        }
    }

    private void staticInit(DownloadMgrInitialParams.InitCustomMaker initCustomMaker) {
        initialParams = new DownloadMgrInitialParams(initCustomMaker);
        connectionCreator = null;
        outputStreamCreator = null;
        database = null;
        idGenerator = null;
    }

    /**
     * 创建连接
     *
     * @param url 连接url
     * @return 连接实例
     * @throws IOException io异常
     */
    public FileDownloadConnection createConnection(String url) throws IOException {
        return getConnectionCreator().create(url);
    }

    /**
     * 构造输出
     *
     * @param file 文件路径
     * @return 返回输出实例
     * @throws IOException io异常
     */
    public FileDownloadOutputStream createOutputStream(File file) throws IOException {
        return getOutputStreamCreator().create(file);
    }

    /**
     * 文件下载调度器
     *
     * @return 返回调度器
     */
    public FileDownloadHelper.IdGenerator getIdGeneratorInstance() {
        if (idGenerator != null) {
            return idGenerator;
        }

        synchronized (this) {
            if (idGenerator == null) {
                idGenerator = getDownloadMgrInitialParams().createIdGenerator();
            }
        }

        return idGenerator;
    }

    /**
     * 下载数据库
     *
     * @return 数据库库实例
     */
    public FileDownloadDatabase getDatabaseInstance() {
        if (database != null) {
            return database;
        }

        synchronized (this) {
            if (database == null) {
                FileDownloadDatabase db = getDownloadMgrInitialParams().createDatabase();
                maintainDatabase(db.maintainer());
                database = db;
            }
        }

        return database;
    }

    public int getMaxNetworkThreadCount() {
        return getDownloadMgrInitialParams().getMaxNetworkThreadCount();
    }

    public boolean isSupportSeek() {
        return getOutputStreamCreator().supportSeek();
    }

    /**
     * 连接个数
     *
     * @param downloadId  连接id
     * @param url         连接url
     * @param path        连接路径
     * @param totalLength 总长度
     * @return 返回连接个数
     */
    public int determineConnectionCount(int downloadId, String url, String path, long totalLength) {
        return getConnectionCountAdapter()
                .determineConnectionCount(downloadId, url, path, totalLength);
    }

    private FileDownloadHelper.ConnectionCountAdapter getConnectionCountAdapter() {
        if (connectionCountAdapter != null) {
            return connectionCountAdapter;
        }

        synchronized (this) {
            if (connectionCountAdapter == null) {
                connectionCountAdapter = getDownloadMgrInitialParams()
                        .createConnectionCountAdapter();
            }
        }

        return connectionCountAdapter;
    }

    private FileDownloadHelper.ConnectionCreator getConnectionCreator() {
        if (connectionCreator != null) {
            return connectionCreator;
        }

        synchronized (this) {
            if (connectionCreator == null) {
                connectionCreator = getDownloadMgrInitialParams().createConnectionCreator();
            }
        }

        return connectionCreator;
    }

    private FileDownloadHelper.OutputStreamCreator getOutputStreamCreator() {
        if (outputStreamCreator != null) {
            return outputStreamCreator;
        }

        synchronized (this) {
            if (outputStreamCreator == null) {
                outputStreamCreator = getDownloadMgrInitialParams().createOutputStreamCreator();
            }
        }

        return outputStreamCreator;
    }

    private DownloadMgrInitialParams getDownloadMgrInitialParams() {
        if (initialParams != null) {
            return initialParams;
        }

        synchronized (this) {
            if (initialParams == null) {
                initialParams = new DownloadMgrInitialParams();
            }
        }

        return initialParams;
    }

    private static void maintainDatabase(FileDownloadDatabase.Maintainer maintainer) {
        final Iterator<FileDownloadModel> iterator = maintainer.iterator();
        long refreshDataCount = 0;
        long removedDataCount = 0;
        long resetIdCount = 0;
        final FileDownloadHelper.IdGenerator aidGenerator = getImpl().getIdGeneratorInstance();

        startTimestamp = System.currentTimeMillis();
        try {
            while (iterator.hasNext()) {
                boolean isInvalid = false;
                final FileDownloadModel model = iterator.next();
                do {
                    getModel(model);
                    final String targetFilePath = model.getTargetFilePath();
                    if (targetFilePath == null) {
                        // no target file path, can't used to resume from breakpoint.
                        isInvalid = true;
                        break;
                    }

                    final File targetFile = new File(targetFilePath);
                    // consider check in new thread, but SQLite lock | file lock aways effect, so
                    // sync
                    if (model.getStatus() == FileDownloadStatus.PAUSED
                            && FileDownloadUtils.isBreakpointAvailable(model.getId(), model,
                            model.getPath(), null)) {
                        // can be reused in the old mechanism(no-temp-file).

                        handleTempFile(model, targetFile);
                    }

                    /**
                     * Remove {@code model} from DB if it can't used for judging whether the
                     * old-downloaded file is valid for reused & it can't used for resuming from
                     * BREAKPOINT, In other words, {@code model} is no use anymore for
                     * FileDownloader.
                     */
                    if (model.getStatus() == FileDownloadStatus.PENDING && model.getSoFar() <= 0) {
                        // This model is redundant.
                        isInvalid = true;
                        break;
                    }

                    if (!FileDownloadUtils.isBreakpointAvailable(model.getId(), model)) {
                        // It can't used to resuming from breakpoint.
                        isInvalid = true;
                        break;
                    }

                    if (targetFile.exists()) {
                        // It has already completed downloading.
                        isInvalid = true;
                        break;
                    }

                } while (false);


                if (isInvalid) {
                    iterator.remove();
                    maintainer.onRemovedInvalidData(model);
                    removedDataCount++;
                } else {
                    resetIdCount = getResetIdCount(maintainer, resetIdCount, aidGenerator, model);

                    maintainer.onRefreshedValidData(model);
                    refreshDataCount++;
                }
            }

        } finally {
            handleFinally(maintainer, refreshDataCount, removedDataCount, resetIdCount);
        }
    }

    private static void getModel(FileDownloadModel model) {
        if (model.getStatus() == FileDownloadStatus.PROGRESS
                || model.getStatus() == FileDownloadStatus.CONNECTED
                || model.getStatus() == FileDownloadStatus.ERROR
                || (model.getStatus() == FileDownloadStatus.PENDING && model
                .getSoFar() > 0)
                ) {
            // Ensure can be covered by RESUME FROM BREAKPOINT.
            model.setStatus(FileDownloadStatus.PAUSED);
        }
    }

    private static void handleFinally(FileDownloadDatabase.Maintainer maintainer, long refreshDataCount, long removedDataCount, long resetIdCount) {
        FileDownloadUtils.markConverted(FileDownloadHelper.getAppContext());
        maintainer.onFinishMaintain();
        // 566 data consumes about 140ms
        if (FileDownloadLog.NEED_LOG) {
            FileDownloadLog.d(FileDownloadDatabase.class,
                    "refreshed data count: %d , delete data count: %d, reset id count:"
                            + " %d. consume %d",
                    refreshDataCount, removedDataCount, resetIdCount,
                    System.currentTimeMillis() - startTimestamp);
        }
    }

    private static long getResetIdCount(FileDownloadDatabase.Maintainer maintainer, long resetIdCount, FileDownloadHelper.IdGenerator aidGenerator, FileDownloadModel model) {
        final int oldId = model.getId();
        final int newId = aidGenerator.transOldId(oldId, model.getUrl(), model.getPath(),
                model.isPathAsDirectory());
        if (newId != oldId) {
            if (FileDownloadLog.NEED_LOG) {
                FileDownloadLog.d(FileDownloadDatabase.class,
                        "the id is changed on restoring from db:"
                                + " old[%d] -> new[%d]",
                        oldId, newId);
            }
            model.setId(newId);
            maintainer.changeFileDownloadModelId(oldId, model);
            resetIdCount++;
        }
        return resetIdCount;
    }

    private static void handleTempFile(FileDownloadModel model, File targetFile) {
        final File tempFile = new File(model.getTempFilePath());

        if (!tempFile.exists() && targetFile.exists()) {
            final boolean successRename = targetFile.renameTo(tempFile);
            if (FileDownloadLog.NEED_LOG) {
                FileDownloadLog.d(FileDownloadDatabase.class,
                        "resume from the old no-temp-file architecture "
                                + "[%B], [%s]->[%s]",
                        successRename, targetFile.getPath(), tempFile.getPath());

            }
        }
    }
}
