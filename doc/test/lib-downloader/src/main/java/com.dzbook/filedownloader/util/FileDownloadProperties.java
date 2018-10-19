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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * You can customize the FileDownloader Engine by add filedownloader.properties file in your assets
 * folder. Example: /demo/src/main/assets/filedownloader.properties
 * <p/>
 * Rules: key=value
 * <p/>
 * Supported keys:
 * <p/>
 * Key {@code http.lenient}
 * Value: {@code true} or {@code false}
 * Default: {@code false}.
 * Such as: http.lenient=false
 * Description:
 * If you occur exception: 'can't know the size of the download file, and its Transfer-Encoding is
 * not Chunked either', but you want to ignore such exception, set true, will deal with it as the
 * case of transfer encoding chunk.
 * If true, will ignore HTTP response header does not has content-length either not chunk transfer
 * encoding.
 * <p/>
 * Key {@code process.non-separate}
 * Value: {@code true} or {@code false}
 * Default: {@code false}.
 * Such as: process.non-separate=false
 * Description:
 * The FileDownloadService runs in the separate process ':filedownloader' as default, if you want
 * to run the FileDownloadService in the main process, just set true.
 * <p/>
 * Key {@code download.min-PROGRESS-step}
 * Value: [0, {@link Integer#MAX_VALUE}]
 * Default: 65536, which follow the value in com.android.providers.downloads.Constants.
 * Such as: download.min-PROGRESS-step=65536
 * Description:
 * The min buffered so far bytes.
 * Used for adjudging whether is time to sync the downloaded so far bytes to database and make sure
 * sync the downloaded buffer to local file.
 * More smaller more frequently, then download more slowly, but will more safer in scene of the
 * process is killed unexpectedly.
 * <p/>
 * Key {@code download.min-PROGRESS-time}
 * Value: [0, {@link Long#MAX_VALUE}]
 * Default: 2000, which follow the value in com.android.providers.downloads.Constants.
 * Such as: download.min-PROGRESS-time=2000
 * Description:
 * The min buffered millisecond.
 * Used for adjudging whether is time to sync the downloaded so far bytes to database and make sure
 * sync the downloaded buffer to local file.
 * More smaller more frequently, then download more slowly, but will more safer in scene of the
 * process is killed unexpectedly.
 * <p/>
 * Key {@code download.max-network-thread-count}
 * Value: [1, 12]
 * Default: 3.
 * Such as: download.max-network-thread-count=3
 * Description:
 * The maximum network thread count for downloading simultaneously.
 * FileDownloader is designed to download 3 files simultaneously as maximum size as default, and the
 * rest of the task is in the FIFO(First In First Out) pending queue.
 * Because the network resource is limited to one device, it means if FileDownloader start
 * downloading tasks unlimited simultaneously, it will be blocked by lack of the network resource,
 * and more useless CPU occupy.
 * The relative efficiency of 3 is higher than others(As Fresco or Picasso do), But for case by case
 * FileDownloader is support to configure for this.
 * Max 12, min 1. If the value more than {@code max} will be replaced with {@code max}; If the value
 * less than {@code min} will be replaced with {@code min}.
 * <p/>
 * Key {@code file.non-pre-allocation}
 * Value: {@code true} or {@code false}
 * Default: {@code false}.
 * Such as: file.non-pre-allocation=false
 * Description:
 * FileDownloader is designed to create the file and pre-allocates the 'content-length' space for it
 * when start downloading.Because FileDownloader want to prevent the space is not enough to store
 * coming data in downloading state as default.
 * <p/>
 * Key {@code broadcast.completed}
 * Value: {@code true} or {@code false}
 * Default: {@code false}.
 * Such as: broadcast.completed=false
 * Description:
 * Whether need to post an broadcast when downloading is completed.
 * This option is very useful when you download something silent on the background on the
 * filedownloader process, and the main process is killed, but you want to do something on the main
 * process when tasks are completed downloading on the filedownloader process, so you can set this
 * one to `true`, then when a task is completed task, you will receive the broadcast, and the main
 * process will be relaunched to handle the broadcast.
 * <p>
 * If you want to receive such broadcast, you also need to register receiver with
 * 'filedownloader.intent.action.completed' action name on 'AndroidManifest.xml'.
 * <p>
 * You can use {@link FileDownloadBroadcastHandler} class to parse the received intent.
 * <p/>
 * Key {code download.trial-connection-head-method}
 * Value: {@code true} or {@code false}
 * Default: {@code false}.
 * Such as download.trial-connection-head-method=false
 * Description:
 * Whether you want the first trial connection with HEAD method to request to backend or not, if
 * this value is true, the first trial connection will with HEAD method instead of GET method and
 * then you will reduce 1 byte cost on the response body, but if the backend can't support HEAD
 * method you will receive 405 response code and failed to download.
 */
public class FileDownloadProperties {

    private static final String KEY_HTTP_LENIENT = "http.lenient";
    private static final String KEY_PROCESS_NON_SEPARATE = "process.non-separate";
    private static final String KEY_DOWNLOAD_MIN_PROGRESS_STEP = "download.min-PROGRESS-step";
    private static final String KEY_DOWNLOAD_MIN_PROGRESS_TIME = "download.min-PROGRESS-time";
    private static final String KEY_DOWNLOAD_MAX_NETWORK_THREAD_COUNT =
            "download.max-network-thread-count";
    private static final String KEY_FILE_NON_PRE_ALLOCATION = "file.non-pre-allocation";
    private static final String KEY_BROADCAST_COMPLETED = "broadcast.completed";
    private static final String KEY_TRIAL_CONNECTION_HEAD_METHOD
            = "download.trial-connection-head-method";
    private static final String TRUE_STRING = "true";
    private static final String FALSE_STRING = "false";
    /**
     * downloadMinProgressStep
     */
    public int downloadMinProgressStep;
    /**
     * downloadMinProgressTime
     */
    public long downloadMinProgressTime;
    /**
     * httpLenient
     */
    public boolean httpLenient;
    /**
     * processNonSeparate
     */
    public boolean processNonSeparate;
    /**
     * downloadMaxNetworkThreadCount
     */
    public int downloadMaxNetworkThreadCount;
    /**
     * fileNonPreAllocation
     */
    public boolean fileNonPreAllocation;
    /**
     * broadcastCompleted
     */
    public boolean broadcastCompleted;
    /**
     * trialConnectionHeadMethod
     */
    public boolean trialConnectionHeadMethod;
    private long start;

    // init properties, normally consume <= 2ms
    private FileDownloadProperties() {
        if (com.dzbook.filedownloader.util.FileDownloadHelper.getAppContext() == null) {
            throw new IllegalStateException("Please invoke the 'FileDownloader#setup' before using "
                    + "FileDownloader. If you want to register some components on FileDownloader "
                    + "please invoke the 'FileDownloader#setupOnApplicationOnCreate' on the "
                    + "'Application#onCreate' first.");
        }
        start = System.currentTimeMillis();
        String aHttpLenient = null;
        String aProcessNonSeparate = null;
        String aDownloadMinProgressStep = null;
        String aDownloadMinProgressTime = null;
        String aDownloadMaxNetworkThreadCount = null;
        String aFileNonPreAllocation = null;
        String aBroadcastCompleted = null;
        String aDownloadTrialConnectionHeadMethod = null;


        Properties p = new Properties();
        InputStream inputStream = null;

        try {
            inputStream = com.dzbook.filedownloader.util.FileDownloadHelper.getAppContext().getAssets().
                    open("filedownloader.properties");
            if (inputStream != null) {
                p.load(inputStream);
                aHttpLenient = p.getProperty(KEY_HTTP_LENIENT);
                aProcessNonSeparate = p.getProperty(KEY_PROCESS_NON_SEPARATE);
                aDownloadMinProgressStep = p.getProperty(KEY_DOWNLOAD_MIN_PROGRESS_STEP);
                aDownloadMinProgressTime = p.getProperty(KEY_DOWNLOAD_MIN_PROGRESS_TIME);
                aDownloadMaxNetworkThreadCount = p
                        .getProperty(KEY_DOWNLOAD_MAX_NETWORK_THREAD_COUNT);
                aFileNonPreAllocation = p.getProperty(KEY_FILE_NON_PRE_ALLOCATION);
                aBroadcastCompleted = p.getProperty(KEY_BROADCAST_COMPLETED);
                aDownloadTrialConnectionHeadMethod = p.getProperty(KEY_TRIAL_CONNECTION_HEAD_METHOD);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        //http.lenient
        handleHttpLenient(aHttpLenient);

        //process.non-separate
        handleProcessNonSeparate(aProcessNonSeparate);

        //download.min-PROGRESS-step
        handleDownloadStep(aDownloadMinProgressStep);

        //download.min-PROGRESS-time
        handleDownloadTime(aDownloadMinProgressTime);

        //download.max-network-thread-count
        handleNetWorkThreadCount(aDownloadMaxNetworkThreadCount);

        // file.non-pre-allocation
        handleFileNonPreAllocation(aFileNonPreAllocation);

        // broadcast.completed
        handleBroadcastCompleted(aBroadcastCompleted);

        // download.trial-connection-head-method
        handleTrialConnectionHeadMethod(aDownloadTrialConnectionHeadMethod);

        if (com.dzbook.filedownloader.util.FileDownloadLog.NEED_LOG) {
            com.dzbook.filedownloader.util.FileDownloadLog.i(FileDownloadProperties.class, "init properties %d\n load properties:"
                            + " %s=%B; %s=%B; %s=%d; %s=%d; %s=%d; %s=%B; %s=%B; %s=%B",
                    System.currentTimeMillis() - start,
                    KEY_HTTP_LENIENT, this.httpLenient,
                    KEY_PROCESS_NON_SEPARATE, this.processNonSeparate,
                    KEY_DOWNLOAD_MIN_PROGRESS_STEP, this.downloadMinProgressStep,
                    KEY_DOWNLOAD_MIN_PROGRESS_TIME, this.downloadMinProgressTime,
                    KEY_DOWNLOAD_MAX_NETWORK_THREAD_COUNT, this.downloadMaxNetworkThreadCount,
                    KEY_FILE_NON_PRE_ALLOCATION, this.fileNonPreAllocation,
                    KEY_BROADCAST_COMPLETED, this.broadcastCompleted,
                    KEY_TRIAL_CONNECTION_HEAD_METHOD, this.trialConnectionHeadMethod);
        }
    }

    /**
     * HolderClass
     */
    public static class HolderClass {
        private static final FileDownloadProperties INSTANCE = new FileDownloadProperties();
    }

    public static FileDownloadProperties getImpl() {
        return HolderClass.INSTANCE;
    }

    private void handleTrialConnectionHeadMethod(String downloadTrialConnectionHeadMethod) {
        if (downloadTrialConnectionHeadMethod != null) {
            if (!downloadTrialConnectionHeadMethod.equals(TRUE_STRING)
                    && !downloadTrialConnectionHeadMethod.equals(FALSE_STRING)) {
                throw new IllegalStateException(
                        FileDownloadUtils.formatString("the value of '%s' must be '%s' or '%s'",
                                KEY_TRIAL_CONNECTION_HEAD_METHOD, TRUE_STRING, FALSE_STRING));
            }
            this.trialConnectionHeadMethod = downloadTrialConnectionHeadMethod.equals(TRUE_STRING);
        } else {
            this.trialConnectionHeadMethod = false;
        }
    }

    private void handleBroadcastCompleted(String aBroadcastCompleted) {
        if (aBroadcastCompleted != null) {
            if (!aBroadcastCompleted.equals(TRUE_STRING)
                    && !aBroadcastCompleted.equals(FALSE_STRING)) {
                throw new IllegalStateException(
                        FileDownloadUtils.formatString("the value of '%s' must be '%s' or '%s'",
                                KEY_BROADCAST_COMPLETED, TRUE_STRING, FALSE_STRING));
            }
            this.broadcastCompleted = aBroadcastCompleted.equals(TRUE_STRING);

        } else {
            this.broadcastCompleted = false;
        }
    }

    private void handleFileNonPreAllocation(String aFileNonPreAllocation) {
        if (aFileNonPreAllocation != null) {
            if (!aFileNonPreAllocation.equals(TRUE_STRING)
                    && !aFileNonPreAllocation.equals(FALSE_STRING)) {
                throw new IllegalStateException(
                        FileDownloadUtils.formatString("the value of '%s' must be '%s' or '%s'",
                                KEY_FILE_NON_PRE_ALLOCATION, TRUE_STRING, FALSE_STRING));
            }
            this.fileNonPreAllocation = aFileNonPreAllocation.equals(TRUE_STRING);
        } else {
            this.fileNonPreAllocation = false;
        }
    }

    private void handleNetWorkThreadCount(String aDownloadMaxNetworkThreadCount) {
        if (aDownloadMaxNetworkThreadCount != null) {
            this.downloadMaxNetworkThreadCount = getValidNetworkThreadCount(
                    Integer.parseInt(aDownloadMaxNetworkThreadCount));
        } else {
            this.downloadMaxNetworkThreadCount = 3;
        }
    }

    private void handleDownloadTime(String aDownloadMinProgressTime) {
        if (aDownloadMinProgressTime != null) {
            long processDownloadMinProgressTime = Long.parseLong(aDownloadMinProgressTime);
            processDownloadMinProgressTime = Math.max(0, processDownloadMinProgressTime);
            this.downloadMinProgressTime = processDownloadMinProgressTime;
        } else {
            this.downloadMinProgressTime = 2000L;
        }
    }

    private void handleDownloadStep(String aDownloadMinProgressStep) {
        if (aDownloadMinProgressStep != null) {
            int processDownloadMinProgressStep = Integer.parseInt(aDownloadMinProgressStep);
            processDownloadMinProgressStep = Math.max(0, processDownloadMinProgressStep);
            this.downloadMinProgressStep = processDownloadMinProgressStep;
        } else {
            this.downloadMinProgressStep = 65536;
        }
    }

    private void handleProcessNonSeparate(String aProcessNonSeparate) {
        if (aProcessNonSeparate != null) {
            if (!aProcessNonSeparate.equals(TRUE_STRING)
                    && !aProcessNonSeparate.equals(FALSE_STRING)) {
                throw new IllegalStateException(
                        FileDownloadUtils.formatString("the value of '%s' must be '%s' or '%s'",
                                KEY_PROCESS_NON_SEPARATE, TRUE_STRING, FALSE_STRING));
            }
            this.processNonSeparate = aProcessNonSeparate.equals(TRUE_STRING);
        } else {
            this.processNonSeparate = false;
        }
    }

    private void handleHttpLenient(String aHttpLenient) {
        if (aHttpLenient != null) {
            if (!aHttpLenient.equals(TRUE_STRING) && !aHttpLenient.equals(FALSE_STRING)) {
                throw new IllegalStateException(
                        FileDownloadUtils.formatString("the value of '%s' must be '%s' or '%s'",
                                KEY_HTTP_LENIENT, TRUE_STRING, FALSE_STRING));
            }
            this.httpLenient = aHttpLenient.equals(TRUE_STRING);
        } else {
            this.httpLenient = false;
        }
    }

    /**
     * getValidNetworkThreadCount
     *
     * @param requireCount requireCount
     * @return int
     */
    public static int getValidNetworkThreadCount(int requireCount) {
        int maxValidNetworkThreadCount = 12;
        int minValidNetworkThreadCount = 1;

        if (requireCount > maxValidNetworkThreadCount) {
            com.dzbook.filedownloader.util.FileDownloadLog.w(FileDownloadProperties.class, "require the count of network thread  "
                            + "is %d, what is more than the max valid count(%d), so adjust to %d "
                            + "auto",
                    requireCount, maxValidNetworkThreadCount, maxValidNetworkThreadCount);
            return maxValidNetworkThreadCount;
        } else if (requireCount < minValidNetworkThreadCount) {
            FileDownloadLog.w(FileDownloadProperties.class, "require the count of network thread  "
                            + "is %d, what is less than the min valid count(%d), so adjust to %d"
                            + " auto",
                    requireCount, minValidNetworkThreadCount, minValidNetworkThreadCount);
            return minValidNetworkThreadCount;
        }

        return requireCount;
    }
}
