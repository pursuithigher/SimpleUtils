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

package com.dzbook.filedownloader.model;

import com.dzbook.filedownloader.BaseDownloadTask;

/**
 * checkstyle
 */
@SuppressWarnings({"checkstyle:linelength", "checkstyle:constantname"})
/**
 * The downloading status.
 *
 * @see com.dzbook.filedownloader.IFileDownloadMessenger
 * @see <a href="https://raw.githubusercontent.com/lingochamp/FileDownloader/master/art/filedownloadlistener_callback_flow.png">Callback-Flow</a>
 */
public class FileDownloadStatus {
    // [-2^7, 2^7 -1]
    // by very beginning
    /**
     * When the task on {@code TO_LAUNCH_POOL} status, it means that the task is just into the
     * LaunchPool and is scheduled for launch.
     * <p>
     * The task is scheduled for launch and it isn't on the FileDownloadService yet.
     */
    public static final byte TO_LAUNCH_POOL = 10;
    /**
     * When the task on {@code TO_FILE_DOWNLOAD_SERVICE} status, it means that the task is just post to
     * the FileDownloadService.
     * <p>
     * The task is posting to the FileDownloadService and after this status, this task can start.
     */
    public static final byte TO_FILE_DOWNLOAD_SERVICE = 11;

    // by FileDownloadService
    /**
     * When the task on {@code PENDING} status, it means that the task is in the list on the
     * FileDownloadService and just waiting for start.
     * <p>
     * The task is waiting on the FileDownloadService.
     * <p>
     * The count of downloading simultaneously, you can configure in filedownloader.properties.
     */
    public static final byte PENDING = 1;
    /**
     * When the task on {@code STARTED} status, it means that the network access thread of
     * downloading this task is STARTED.
     * <p>
     * The task is downloading on the FileDownloadService.
     */
    public static final byte STARTED = 6;
    /**
     * When the task on {@code CONNECTED} status, it means that the task is successfully CONNECTED
     * to the back-end.
     * <p>
     * The task is downloading on the FileDownloadService.
     */
    public static final byte CONNECTED = 2;
    /**
     * When the task on {@code PROGRESS} status, it means that the task is fetching data from the
     * back-end.
     * <p>
     * The task is downloading on the FileDownloadService.
     */
    public static final byte PROGRESS = 3;
    /**
     * When the task on {@code blockComplete} status, it means that the task has been completed
     * downloading successfully.
     * <p>
     * The task is completed downloading successfully and the action-flow is blocked for doing
     * something before callback completed method.
     */
    public static final byte BLOCK_COMPLETE = 4;
    /**
     * When the task on {@code retry} status, it means that the task must occur some error, but
     * there is a valid chance to retry, so the task is retry to download again.
     * <p>
     * The task is restarting on the FileDownloadService.
     */
    public static final byte RETRY = 5;

    /**
     * When the task on {@code error} status, it means that the task must occur some error and there
     * isn't any valid chance to retry, so the task is finished with error.
     * <p>
     * The task is finished with an error.
     */
    public static final byte ERROR = -1;
    /**
     * When the task on {@code paused} status, it means that the task is paused manually.
     * <p>
     * The task is finished with the pause action.
     */
    public static final byte PAUSED = -2;
    /**
     * When the task on {@code completed} status, it means that the task is completed downloading
     * successfully.
     * <p>
     * The task is finished with completed downloading successfully.
     */
    public static final byte COMPLETED = -3;
    /**
     * When the task on {@code warn} status, it means that there is another same task(same url,
     * same path to store content) is running.
     * <p>
     * The task is finished with the warn status.
     */
    public static final byte WARN = -4;

    /**
     * When the task on {@code INVALID_STATUS} status, it means that the task is IDLE.
     * <p>
     * The task is clear and it isn't launched.
     */
    public static final byte INVALID_STATUS = 0;

    /**
     * isOver
     * @param status 状态
     * @return boolean
     */
    public static boolean isOver(final int status) {
        return status < 0;
    }

    /**
     * isIng
     * @param status status
     * @return boolean
     */
    public static boolean isIng(final int status) {
        return status > 0;
    }

    /**
     * isKeepAhead
     * @param status  状态
     * @param nextStatus 下一个状态
     * @return boolean
     */
    public static boolean isKeepAhead(final int status, final int nextStatus) {
        if (checkError(status, nextStatus)) {
            return false;
        }

        switch (status) {
            case PENDING:
                return handleKeepPending(nextStatus);
            case STARTED:
                return handleKeepStart(nextStatus);

            case CONNECTED:
                return handleKeepConnected(nextStatus);
            case PROGRESS:
                return handleKeepProgress(nextStatus);

            case RETRY:
                return handleKeepRetry(nextStatus);

            default:
                return true;
        }

    }

    private static boolean handleKeepRetry(int nextStatus) {
        switch (nextStatus) {
            case PENDING:
            case STARTED:
                return false;
            default:
                return true;
        }
    }

    private static boolean handleKeepProgress(int nextStatus) {
        switch (nextStatus) {
            case INVALID_STATUS:
            case PENDING:
            case STARTED:
            case CONNECTED:
                return false;
            default:
                return true;
        }
    }

    private static boolean handleKeepConnected(int nextStatus) {
        switch (nextStatus) {
            case INVALID_STATUS:
            case PENDING:
            case STARTED:
                return false;
            default:
                return true;
        }
    }

    private static boolean handleKeepStart(int nextStatus) {
        switch (nextStatus) {
            case INVALID_STATUS:
            case PENDING:
                return false;
            default:
                return true;
        }
    }

    private static boolean handleKeepPending(int nextStatus) {
        switch (nextStatus) {
            case INVALID_STATUS:
                return false;
            default:
                return true;
        }
    }

    private static boolean checkError(int status, int nextStatus) {
        if (status != PROGRESS && status != RETRY && status == nextStatus) {
            return true;
        }

        if (isOver(status)) {
            return true;
        }

        if (status >= PENDING && status <= STARTED /** in FileDownloadService **/
                && nextStatus >= TO_LAUNCH_POOL && nextStatus <= TO_FILE_DOWNLOAD_SERVICE) {
            return true;
        }
        return false;
    }

    /**
     * isKeepFlow
     * @param status 状态
     * @param nextStatus nextStatus
     * @return boolean
     */
    @SuppressWarnings("checkstyle:avoidnestedblocks")
    public static boolean isKeepFlow(final int status, final int nextStatus) {
        boolean isError = checkNetWorkError(status, nextStatus);
        if (isError) {
            return true;
        }
        ;

        switch (status) {
            case INVALID_STATUS: {
                return handleStatusInvalid(nextStatus);
            }
            case TO_LAUNCH_POOL:
                return handleTofileDowload(nextStatus);
            case TO_FILE_DOWNLOAD_SERVICE:
                return handleNextStatus(nextStatus);
            case PENDING:
                return handlePending(nextStatus);
            case RETRY:
            case STARTED:
                return handleStart(nextStatus);
            case CONNECTED:
            case PROGRESS:
                return handleProgress(nextStatus);
            default:
                return false;
        }

    }

    private static boolean handleProgress(int nextStatus) {
        switch (nextStatus) {
            case PROGRESS:
            case COMPLETED:
            case RETRY:
                return true;
            default:
                return false;
        }
    }

    private static boolean handleStart(int nextStatus) {
        switch (nextStatus) {
            case RETRY:
            case CONNECTED:
                return true;
            default:
                return false;
        }
    }

    private static boolean handlePending(int nextStatus) {
        switch (nextStatus) {
            case STARTED:
                return true;
            default:
                return false;
        }
    }

    private static boolean handleNextStatus(int nextStatus) {
        switch (nextStatus) {
            case PENDING:
            case WARN:
            case COMPLETED:
                return true;
            default:
                return false;
        }
    }

    private static boolean handleTofileDowload(int nextStatus) {
        switch (nextStatus) {
            case TO_FILE_DOWNLOAD_SERVICE:
                return true;
            default:
                return false;
        }
    }

    private static boolean handleStatusInvalid(int nextStatus) {
        switch (nextStatus) {
            case TO_LAUNCH_POOL:
                return true;
            default:
                return false;
        }
    }

    private static Boolean checkNetWorkError(int status, int nextStatus) {
        return !(status != PROGRESS && status != RETRY && status == nextStatus) && !isOver(status) && (nextStatus == PAUSED || nextStatus == ERROR);

    }

    /**
     * isMoreLikelyCompleted
     * @param task 任务
     * @return boolean
     */
    public static boolean isMoreLikelyCompleted(BaseDownloadTask task) {
        return task.getStatus() == INVALID_STATUS || task.getStatus() == PROGRESS;
    }
}
