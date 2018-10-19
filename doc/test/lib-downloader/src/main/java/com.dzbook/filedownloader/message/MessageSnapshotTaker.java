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

package com.dzbook.filedownloader.message;

import com.dzbook.filedownloader.BaseDownloadTask;
import com.dzbook.filedownloader.download.DownloadStatusCallback;
import com.dzbook.filedownloader.model.FileDownloadModel;
import com.dzbook.filedownloader.model.FileDownloadStatus;
import com.dzbook.filedownloader.util.FileDownloadLog;
import com.dzbook.filedownloader.util.FileDownloadUtils;

import java.io.File;

/**
 * The factory for taking message snapshots.
 */
public class MessageSnapshotTaker {

    /**
     * take方法
     *
     * @param status 状态
     * @param model  model
     * @return MessageSnapshot 实例
     */
    public static com.dzbook.filedownloader.message.MessageSnapshot take(byte status, FileDownloadModel model) {
        return take(status, model, null);
    }

    /**
     * catchCanReusedOldFile
     *
     * @param id           id
     * @param oldFile      旧地址
     * @param flowDirectly 目录
     * @return MessageSnapshot 实例
     */
    public static com.dzbook.filedownloader.message.MessageSnapshot catchCanReusedOldFile(int id, File oldFile,
                                                                                          boolean flowDirectly) {
        final long totalBytes = oldFile.length();
        if (totalBytes > Integer.MAX_VALUE) {
            if (flowDirectly) {
                return new com.dzbook.filedownloader.message.LargeMessageSnapshot.CompletedFlowDirectlySnapshot(id, true, totalBytes);
            } else {
                return new com.dzbook.filedownloader.message.LargeMessageSnapshot.CompletedSnapshot(id, true, totalBytes);
            }
        } else {
            if (flowDirectly) {
                return new SmallMessageSnapshot.CompletedFlowDirectlySnapshot(id, true,
                        (int) totalBytes);
            } else {
                return new SmallMessageSnapshot.CompletedSnapshot(id, true, (int) totalBytes);
            }
        }
    }

    /**
     * 警告
     *
     * @param id           id
     * @param sofar        sofar
     * @param total        total
     * @param flowDirectly flowDirectly
     * @return MessageSnapshot实例
     */
    public static com.dzbook.filedownloader.message.MessageSnapshot catchWarn(int id, long sofar, long total, boolean flowDirectly) {
        if (total > Integer.MAX_VALUE) {
            if (flowDirectly) {
                return new com.dzbook.filedownloader.message.LargeMessageSnapshot.WarnFlowDirectlySnapshot(id, sofar, total);
            } else {
                return new com.dzbook.filedownloader.message.LargeMessageSnapshot.WarnMessageSnapshot(id, sofar, total);
            }
        } else {
            if (flowDirectly) {
                return new SmallMessageSnapshot.WarnFlowDirectlySnapshot(id, (int) sofar,
                        (int) total);
            } else {
                return new SmallMessageSnapshot.WarnMessageSnapshot(id, (int) sofar, (int) total);
            }
        }
    }

    /**
     * catch异常
     *
     * @param id    id
     * @param sofar sofar
     * @param error error
     * @return MessageSnapshot 实例
     */
    public static com.dzbook.filedownloader.message.MessageSnapshot catchException(int id, long sofar, Throwable error) {
        if (sofar > Integer.MAX_VALUE) {
            return new com.dzbook.filedownloader.message.LargeMessageSnapshot.ErrorMessageSnapshot(id, sofar, error);
        } else {
            return new SmallMessageSnapshot.ErrorMessageSnapshot(id, (int) sofar, error);
        }
    }

    /**
     * catchPause
     *
     * @param task 任务
     * @return MessageSnapshot 实例
     */
    public static com.dzbook.filedownloader.message.MessageSnapshot catchPause(BaseDownloadTask task) {
        if (task.isLargeFile()) {
            return new com.dzbook.filedownloader.message.LargeMessageSnapshot.PausedSnapshot(task.getId(),
                    task.getLargeFileSoFarBytes(), task.getLargeFileTotalBytes());
        } else {
            return new SmallMessageSnapshot.PausedSnapshot(task.getId(),
                    task.getSmallFileSoFarBytes(), task.getSmallFileTotalBytes());
        }
    }

    /**
     * takeBlockCompleted
     *
     * @param snapshot snapshot
     * @return MessageSnapshot实例
     */
    public static com.dzbook.filedownloader.message.MessageSnapshot takeBlockCompleted(com.dzbook.filedownloader.message.MessageSnapshot snapshot) {
        if (snapshot.getStatus() != FileDownloadStatus.COMPLETED) {
            throw new IllegalStateException(
                    FileDownloadUtils.formatString("take block completed snapshot, must has "
                                    + "already be completed. %d %d",
                            snapshot.getId(), snapshot.getStatus()));
        }

        return new com.dzbook.filedownloader.message.BlockCompleteMessage.BlockCompleteMessageImpl(snapshot);
    }

    /**
     * 添加
     *
     * @param status        状态
     * @param model         model
     * @param processParams 进程参数
     * @return MessageSnapshot 实例
     */
    public static com.dzbook.filedownloader.message.MessageSnapshot take(byte status, FileDownloadModel model,
                                                                         DownloadStatusCallback.ProcessParams processParams) {
        final com.dzbook.filedownloader.message.MessageSnapshot snapShot;
        final int id = model.getId();
        if (status == FileDownloadStatus.WARN) {
            throw new IllegalStateException(FileDownloadUtils.
                    formatString("please use #catchWarn instead %d", id));
        }

        switch (status) {
            case FileDownloadStatus.PENDING:
                snapShot = handlePending(model, id);
                break;
            case FileDownloadStatus.STARTED:
                snapShot = new MessageSnapshot.StartedMessageSnapshot(id);
                break;
            case FileDownloadStatus.CONNECTED:
                snapShot = handleConnected(model, processParams, id);
                break;
            case FileDownloadStatus.PROGRESS:
                snapShot = handleProgress(model, id);
                break;
            case FileDownloadStatus.COMPLETED:
                snapShot = handleComplered(model, id);
                break;
            case FileDownloadStatus.RETRY:
                snapShot = handleRetry(model, processParams, id);
                break;
            case FileDownloadStatus.ERROR:
                snapShot = handleError(model, processParams, id);
                break;
            default:
                // deal with as error.
                snapShot = handleDefault(status, model, processParams, id);
                break;
        }

        return snapShot;
    }

    private static MessageSnapshot handleDefault(byte status, FileDownloadModel model, DownloadStatusCallback.ProcessParams processParams, int id) {
        MessageSnapshot snapShot;
        final String message = FileDownloadUtils.
                formatString(
                        "it can't takes a snapshot for the task(%s) when its status is %d,",
                        model, status);

        FileDownloadLog.w(MessageSnapshotTaker.class,
                "it can't takes a snapshot for the task(%s) when its status is %d,", model,
                status);

        final Throwable throwable;
        if (processParams.getException() != null) {
            throwable = new IllegalStateException(message, processParams.getException());
        } else {
            throwable = new IllegalStateException(message);
        }

        if (model.isLargeFile()) {
            snapShot = new LargeMessageSnapshot.ErrorMessageSnapshot(id,
                    model.getSoFar(), throwable);
        } else {
            snapShot = new SmallMessageSnapshot.ErrorMessageSnapshot(id,
                    (int) model.getSoFar(), throwable);
        }
        return snapShot;
    }

    private static MessageSnapshot handleError(FileDownloadModel model, DownloadStatusCallback.ProcessParams processParams, int id) {
        MessageSnapshot snapShot;
        if (model.isLargeFile()) {
            snapShot = new LargeMessageSnapshot.ErrorMessageSnapshot(id,
                    model.getSoFar(), processParams.getException());
        } else {
            snapShot = new SmallMessageSnapshot.ErrorMessageSnapshot(id,
                    (int) model.getSoFar(), processParams.getException());
        }
        return snapShot;
    }

    private static MessageSnapshot handleRetry(FileDownloadModel model, DownloadStatusCallback.ProcessParams processParams, int id) {
        MessageSnapshot snapShot;
        if (model.isLargeFile()) {
            snapShot = new LargeMessageSnapshot.RetryMessageSnapshot(id,
                    model.getSoFar(), processParams.getException(),
                    processParams.getRetryingTimes());
        } else {
            snapShot = new SmallMessageSnapshot.RetryMessageSnapshot(id,
                    (int) model.getSoFar(), processParams.getException(),
                    processParams.getRetryingTimes());
        }
        return snapShot;
    }

    private static MessageSnapshot handleComplered(FileDownloadModel model, int id) {
        MessageSnapshot snapShot;
        if (model.isLargeFile()) {
            snapShot = new LargeMessageSnapshot.
                    CompletedSnapshot(id, false, model.getTotal());
        } else {
            snapShot = new SmallMessageSnapshot.
                    CompletedSnapshot(id, false, (int) model.getTotal());
        }
        return snapShot;
    }

    private static MessageSnapshot handleProgress(FileDownloadModel model, int id) {
        MessageSnapshot snapShot;
        if (model.isLargeFile()) {
            snapShot = new LargeMessageSnapshot.
                    ProgressMessageSnapshot(id, model.getSoFar());
        } else {
            snapShot = new SmallMessageSnapshot.
                    ProgressMessageSnapshot(id, (int) model.getSoFar());
        }
        return snapShot;
    }

    private static MessageSnapshot handleConnected(FileDownloadModel model, DownloadStatusCallback.ProcessParams processParams, int id) {
        MessageSnapshot snapShot;
        final String filename = model.isPathAsDirectory() ? model.getFilename() : null;
        if (model.isLargeFile()) {
            snapShot = new LargeMessageSnapshot.ConnectedMessageSnapshot(id,
                    processParams.isResuming(), model.getTotal(), model.getETag(),
                    filename);
        } else {
            snapShot = new SmallMessageSnapshot.ConnectedMessageSnapshot(id,
                    processParams.isResuming(), (int) model.getTotal(), model.getETag(),
                    filename);
        }
        return snapShot;
    }

    private static MessageSnapshot handlePending(FileDownloadModel model, int id) {
        MessageSnapshot snapShot;
        if (model.isLargeFile()) {
            snapShot = new LargeMessageSnapshot.PendingMessageSnapshot(id,
                    model.getSoFar(), model.getTotal());
        } else {
            snapShot = new SmallMessageSnapshot.PendingMessageSnapshot(id,
                    (int) model.getSoFar(), (int) model.getTotal());
        }
        return snapShot;
    }
}
