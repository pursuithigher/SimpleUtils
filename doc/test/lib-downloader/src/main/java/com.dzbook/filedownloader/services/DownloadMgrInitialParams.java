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

package com.dzbook.filedownloader.services;

import com.dzbook.filedownloader.connection.DefaultConnectionCountAdapter;
import com.dzbook.filedownloader.connection.FileDownloadUrlConnection;
import com.dzbook.filedownloader.database.FileDownloadDatabase;
import com.dzbook.filedownloader.database.RemitDatabase;
import com.dzbook.filedownloader.model.FileDownloadModel;
import com.dzbook.filedownloader.stream.FileDownloadOutputStream;
import com.dzbook.filedownloader.stream.FileDownloadRandomAccessFile;
import com.dzbook.filedownloader.util.FileDownloadHelper;
import com.dzbook.filedownloader.util.FileDownloadLog;
import com.dzbook.filedownloader.util.FileDownloadProperties;
import com.dzbook.filedownloader.util.FileDownloadUtils;

/**
 * Params in this class is used in the downloading manager, and would be used for initialize the
 * download manager in the process the downloader service settled on.
 */
public class DownloadMgrInitialParams {

    private final InitCustomMaker mMaker;

    /**
     * 构造
     */
    public DownloadMgrInitialParams() {
        mMaker = null;
    }

    /**
     * 构造
     * @param maker maker
     */
    public DownloadMgrInitialParams(InitCustomMaker maker) {
        this.mMaker = maker;
    }

    /**
     * 获取最大联网个数
     * @return int
     */
    public int getMaxNetworkThreadCount() {
        if (mMaker == null) {
            return getDefaultMaxNetworkThreadCount();
        }

        final Integer customizeMaxNetworkThreadCount = mMaker.mMaxNetworkThreadCount;

        if (customizeMaxNetworkThreadCount != null) {
            if (FileDownloadLog.NEED_LOG) {
                FileDownloadLog.d(this, "initial FileDownloader manager with the customize "
                        + "maxNetworkThreadCount: %d", customizeMaxNetworkThreadCount);
            }

            return FileDownloadProperties
                    .getValidNetworkThreadCount(customizeMaxNetworkThreadCount);
        } else {
            return getDefaultMaxNetworkThreadCount();
        }

    }

    /**
     * 创建数据库
     * @return FileDownloadDatabase
     */
    public FileDownloadDatabase createDatabase() {
        if (mMaker == null || mMaker.mDatabaseCustomMaker == null) {
            return createDefaultDatabase();
        }
        final FileDownloadDatabase customDatabase = mMaker.mDatabaseCustomMaker.customMake();

        if (customDatabase != null) {
            if (FileDownloadLog.NEED_LOG) {
                FileDownloadLog.d(this, "initial FileDownloader manager with the customize "
                        + "database: %s", customDatabase);
            }
            return customDatabase;
        } else {
            return createDefaultDatabase();
        }
    }

    /**
     * 创建creater
     * @return OutputStreamCreator
     */
    public FileDownloadHelper.OutputStreamCreator createOutputStreamCreator() {
        if (mMaker == null) {
            return createDefaultOutputStreamCreator();
        }

        final FileDownloadHelper.OutputStreamCreator outputStreamCreator =
                mMaker.mOutputStreamCreator;
        if (outputStreamCreator != null) {
            if (FileDownloadLog.NEED_LOG) {
                FileDownloadLog.d(this, "initial FileDownloader manager with the customize "
                        + "output stream: %s", outputStreamCreator);
            }
            return outputStreamCreator;
        } else {
            return createDefaultOutputStreamCreator();
        }
    }

    /**
     * createConnectionCreator
     * @return ConnectionCreator
     */
    public FileDownloadHelper.ConnectionCreator createConnectionCreator() {
        if (mMaker == null) {
            return createDefaultConnectionCreator();
        }

        final FileDownloadHelper.ConnectionCreator connectionCreator = mMaker.mConnectionCreator;

        if (connectionCreator != null) {
            if (FileDownloadLog.NEED_LOG) {
                FileDownloadLog.d(this, "initial FileDownloader manager with the customize "
                        + "connection creator: %s", connectionCreator);
            }
            return connectionCreator;
        } else {
            return createDefaultConnectionCreator();
        }
    }

    /**
     * createConnectionCountAdapter
     * @return ConnectionCountAdapter
     */
    public FileDownloadHelper.ConnectionCountAdapter createConnectionCountAdapter() {
        if (mMaker == null) {
            return createDefaultConnectionCountAdapter();
        }

        final FileDownloadHelper.ConnectionCountAdapter adapter = mMaker.mConnectionCountAdapter;
        if (adapter != null) {
            if (FileDownloadLog.NEED_LOG) {
                FileDownloadLog.d(this, "initial FileDownloader manager with the customize "
                        + "connection count adapter: %s", adapter);
            }
            return adapter;
        } else {
            return createDefaultConnectionCountAdapter();
        }
    }

    /**
     * createIdGenerator
     * @return IdGenerator
     */
    public FileDownloadHelper.IdGenerator createIdGenerator() {
        if (mMaker == null) {
            return createDefaultIdGenerator();
        }

        final FileDownloadHelper.IdGenerator idGenerator = mMaker.mIdGenerator;
        if (idGenerator != null) {
            if (FileDownloadLog.NEED_LOG) {
                FileDownloadLog.d(this, "initial FileDownloader manager with the customize "
                        + "id generator: %s", idGenerator);
            }

            return idGenerator;
        } else {
            return createDefaultIdGenerator();
        }
    }

    private FileDownloadHelper.IdGenerator createDefaultIdGenerator() {
        return new com.dzbook.filedownloader.services.DefaultIdGenerator();
    }

    private int getDefaultMaxNetworkThreadCount() {
        return FileDownloadProperties.getImpl().downloadMaxNetworkThreadCount;
    }

    private FileDownloadDatabase createDefaultDatabase() {
        return new RemitDatabase();
    }

    private FileDownloadHelper.OutputStreamCreator createDefaultOutputStreamCreator() {
        return new FileDownloadRandomAccessFile.Creator();
    }

    private FileDownloadHelper.ConnectionCreator createDefaultConnectionCreator() {
        return new FileDownloadUrlConnection.Creator();
    }

    private FileDownloadHelper.ConnectionCountAdapter createDefaultConnectionCountAdapter() {
        return new DefaultConnectionCountAdapter();
    }

    /**
     * InitCustomMaker
     */
    public static class InitCustomMaker {
        FileDownloadHelper.DatabaseCustomMaker mDatabaseCustomMaker;
        Integer mMaxNetworkThreadCount;
        FileDownloadHelper.OutputStreamCreator mOutputStreamCreator;
        FileDownloadHelper.ConnectionCreator mConnectionCreator;
        FileDownloadHelper.ConnectionCountAdapter mConnectionCountAdapter;
        FileDownloadHelper.IdGenerator mIdGenerator;

        /**
         * customize the id generator.
         *
         * @param idGenerator the id generator used for generating download identify manually.
         * @return InitCustomMaker
         */
        public InitCustomMaker idGenerator(FileDownloadHelper.IdGenerator idGenerator) {
            this.mIdGenerator = idGenerator;
            return this;
        }

        /**
         * customize the connection count adapter.
         *
         * @param adapter the adapter used for determine how many connection will be used to
         *                downloading the target task.
         * @return the connection count adapter.
         */
        public InitCustomMaker connectionCountAdapter(
                FileDownloadHelper.ConnectionCountAdapter adapter) {
            this.mConnectionCountAdapter = adapter;
            return this;
        }

        /**
         * customize the database component.
         * <p>
         * If you don't customize the data component, we use the result of
         * {@link #createDefaultDatabase()} as the default one.
         *
         * @param maker The database is used for storing the {@link FileDownloadModel}.
         *              <p>
         *              The data stored in the database is only used for task resumes from the
         *              breakpoint.
         *              <p>
         *              The task of the data stored in the database must be a task that has not
         *              finished downloading yet, and if the task has finished downloading, its data
         *              will be {@link FileDownloadDatabase#remove(int)} from the database, since
         *              that data is no longer available for resumption of its task pass.
         *  @return InitCustomMaker
         */
        public InitCustomMaker database(FileDownloadHelper.DatabaseCustomMaker maker) {
            this.mDatabaseCustomMaker = maker;
            return this;
        }

        /**
         * Customize the max network thread count.
         * <p>
         * If you don't customize the network thread count, we use the result of
         * {@link #getDefaultMaxNetworkThreadCount()} as the default one.
         *
         * @param maxNetworkThreadCount The maximum count of the network thread, what is the number
         *                              of simultaneous downloads in FileDownloader.
         *                              <p>
         *                              If this value is less than or equal to 0, the value will be
         *                              ignored and use
         *                              {@link FileDownloadProperties#downloadMaxNetworkThreadCount}
         *                              which is defined in filedownloader.properties instead.
         * @return InitCustomMaker
         */
        public InitCustomMaker maxNetworkThreadCount(int maxNetworkThreadCount) {
            if (maxNetworkThreadCount > 0) {
                this.mMaxNetworkThreadCount = maxNetworkThreadCount;
            }
            return this;
        }

        /**
         * Customize the output stream component.
         * <p>
         * If you don't customize the output stream component, we use the result of
         * {@link #createDefaultOutputStreamCreator()} as the default one.
         *
         * @param creator The output stream creator is used for creating
         *                {@link FileDownloadOutputStream} which is used to write the input stream
         *                to the file for downloading.
         * @return InitCustomMaker
         */
        public InitCustomMaker outputStreamCreator(FileDownloadHelper.OutputStreamCreator creator) {
            this.mOutputStreamCreator = creator;
            if (mOutputStreamCreator != null && !mOutputStreamCreator.supportSeek()) {
                if (!FileDownloadProperties.getImpl().fileNonPreAllocation) {
                    throw new IllegalArgumentException(
                            "Since the provided FileDownloadOutputStream "
                                    + "does not support the seek function, if FileDownloader"
                                    + " pre-allocates file size at the beginning of the download,"
                                    + " it will can not be resumed from the breakpoint. If you need"
                                    + " to ensure that the resumption is available, please add and"
                                    + " set the value of 'file.non-pre-allocation' field to 'true'"
                                    + " in the 'filedownloader.properties' file which is in your"
                                    + " application assets folder manually for resolving this "
                                    + "problem.");
                }
            }
            return this;
        }

        /**
         * Customize the connection component.
         * <p>
         * If you don't customize the connection component, we use the result of
         * {@link #createDefaultConnectionCreator()} as the default one.
         *
         * @param creator the connection creator will used for create the connection when start
         *                downloading any task in the FileDownloader.
         * @return InitCustomMaker
         */
        public InitCustomMaker connectionCreator(FileDownloadHelper.ConnectionCreator creator) {
            this.mConnectionCreator = creator;
            return this;
        }

        /**
         * commit
         */
        @SuppressWarnings("EmptyMethod")
        public void commit() {
            // do nothing now.
        }

        @Override
        public String toString() {
            return FileDownloadUtils.formatString("component: database[%s], maxNetworkCount[%s],"
                            + " outputStream[%s], connection[%s], connectionCountAdapter[%s]",
                    mDatabaseCustomMaker, mMaxNetworkThreadCount, mOutputStreamCreator,
                    mConnectionCreator, mConnectionCountAdapter);
        }
    }
}
