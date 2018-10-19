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

package com.dzbook.filedownloader.notification;

import com.dzbook.filedownloader.BaseDownloadTask;
import com.dzbook.filedownloader.FileDownloadList;
import com.dzbook.filedownloader.FileDownloadListener;

/**
 * The listener of the notification with the task.
 *
 * @see com.dzbook.filedownloader.notification.FileDownloadNotificationHelper
 * @see com.dzbook.filedownloader.notification.BaseNotificationItem
 */
@SuppressWarnings({"WeakerAccess", "UnusedParameters"})
public abstract class FileDownloadNotificationListener extends FileDownloadListener {
    private final com.dzbook.filedownloader.notification.FileDownloadNotificationHelper helper;

    /**
     * FileDownloadNotificationListener
     * @param helper helper
     */
    public FileDownloadNotificationListener(com.dzbook.filedownloader.notification.FileDownloadNotificationHelper helper) {
        if (helper == null) {
            throw new IllegalArgumentException("helper must not be null!");
        }
        this.helper = helper;
    }

    public com.dzbook.filedownloader.notification.FileDownloadNotificationHelper getHelper() {
        return helper;
    }

    /**
     * addNotificationItem
     * @param downloadId downloadId
     */
    public void addNotificationItem(int downloadId) {
        if (downloadId == 0) {
            return;
        }

        BaseDownloadTask.IRunningTask task = FileDownloadList.getImpl().get(downloadId);
        if (task != null) {
            addNotificationItem(task.getOrigin());
        }
    }

    /**
     * addNotificationItem
     * @param task 任务
     */
    public void addNotificationItem(BaseDownloadTask task) {
        if (disableNotification(task)) {
            return;
        }

        final com.dzbook.filedownloader.notification.BaseNotificationItem n = create(task);
        if (n != null) {
            //noinspection unchecked
            this.helper.add(n);
        }
    }

    /**
     * The notification item with the {@code task} is told to destroy.
     *
     * @param task The task used to identify the will be destroyed notification item.
     */
    public void destroyNotification(BaseDownloadTask task) {
        if (disableNotification(task)) {
            return;
        }

        this.helper.showIndeterminate(task.getId(), task.getStatus());

        final com.dzbook.filedownloader.notification.BaseNotificationItem n = this.helper.
                remove(task.getId());
        if (!interceptCancel(task, n) && n != null) {
            n.cancel();
        }
    }

    /**
     * showIndeterminate
     * @param task 任务
     */
    public void showIndeterminate(BaseDownloadTask task) {
        if (disableNotification(task)) {
            return;
        }

        this.helper.showIndeterminate(task.getId(), task.getStatus());
    }

    /**
     * 展示progress
     * @param task 任务
     * @param soFarBytes soFarBytes
     * @param totalBytes totalBytes
     */
    public void showProgress(BaseDownloadTask task, int soFarBytes,
                             int totalBytes) {
        if (disableNotification(task)) {
            return;
        }

        this.helper.showProgress(task.getId(), task.getSmallFileSoFarBytes(),
                task.getSmallFileTotalBytes());
    }

    /**
     * BaseNotificationItem
     * @param task The task used to bind with the will be created notification item.
     * @return The notification item is related with the {@code task}.
     */
    protected abstract com.dzbook.filedownloader.notification.BaseNotificationItem create(BaseDownloadTask task);

    /**
     * interceptCancel
     * @param task             The task.
     * @param notificationItem The notification item.
     * @return {@code true} if you want to survive the notification item, and we will don't  cancel
     * the relate notification from the notification panel when the relate task is finished,
     * {@code false} otherwise.
     * <p>
     * <strong>Default:</strong> {@code false}
     * @see #destroyNotification(BaseDownloadTask)
     */
    protected boolean interceptCancel(BaseDownloadTask task,
                                      BaseNotificationItem notificationItem) {
        return false;
    }

    /**
     * disableNotification
     * @param task The task.
     * @return {@code true} if you want to disable the internal notification lifecycle, and in this
     * case all method about the notification will be invalid, {@code false} otherwise.
     * <p>
     * <strong>Default:</strong> {@code false}
     */
    protected boolean disableNotification(final BaseDownloadTask task) {
        return false;
    }

    @Override
    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        addNotificationItem(task);
        showIndeterminate(task);
    }

    @Override
    protected void started(BaseDownloadTask task) {
        super.started(task);
        showIndeterminate(task);
    }

    @Override
    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        showProgress(task, soFarBytes, totalBytes);
    }

    @Override
    protected void retry(BaseDownloadTask task, Throwable ex, int retryingTimes, int soFarBytes) {
        super.retry(task, ex, retryingTimes, soFarBytes);
        showIndeterminate(task);
    }

    @Override
    protected void blockComplete(BaseDownloadTask task) {
    }

    @Override
    protected void completed(BaseDownloadTask task) {
        destroyNotification(task);
    }

    @Override
    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        destroyNotification(task);
    }

    @Override
    protected void error(BaseDownloadTask task, Throwable e) {
        destroyNotification(task);
    }

    @Override
    protected void warn(BaseDownloadTask task) {
        // ignore
        // do not handle the case of the same URL and path task(the same download id), which
        // will share the same notification item
//        if (!disableNotification(task)) {
//            destroyNotification(task);
//        }
    }
}
