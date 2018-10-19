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

import android.util.SparseArray;

import com.dzbook.filedownloader.model.FileDownloadStatus;

/**
 * The helper for notifications with downloading tasks. You also can think this is the notifications
 * manager.
 *
 * @param <T> 泛型
 * @see com.dzbook.filedownloader.notification.BaseNotificationItem
 * @see FileDownloadNotificationListener
 */
@SuppressWarnings("WeakerAccess")
public class FileDownloadNotificationHelper<T extends com.dzbook.filedownloader.notification.BaseNotificationItem> {

    private final SparseArray<T> notificationArray = new SparseArray<>();

    /**
     * Get {@link com.dzbook.filedownloader.notification.BaseNotificationItem} by the download id.
     *
     * @param id The download id.
     * @return T
     */
    public T get(final int id) {
        return notificationArray.get(id);
    }

    /**
     * 是否包含
     * @param id id
     * @return boolean
     */
    public boolean contains(final int id) {
        return get(id) != null;
    }

    /**
     * Remove the {@link com.dzbook.filedownloader.notification.BaseNotificationItem} by the download id.
     *
     * @param id The download id.
     * @return The removed {@link com.dzbook.filedownloader.notification.BaseNotificationItem}.
     */
    public T remove(final int id) {
        final T n = get(id);
        if (n != null) {
            notificationArray.remove(id);
            return n;
        }

        return null;
    }

    /**
     * Input a {@link com.dzbook.filedownloader.notification.BaseNotificationItem}.
     * @param notification  notification
     */
    public void add(T notification) {
        notificationArray.remove(notification.getId());
        notificationArray.put(notification.getId(), notification);
    }

    /**
     * Show the notification with the exact PROGRESS.
     *
     * @param id    The download id.
     * @param sofar The downloaded bytes so far.
     * @param total The total bytes of this task.
     */
    public void showProgress(final int id, final int sofar, final int total) {
        final T notification = get(id);

        if (notification == null) {
            return;
        }

        notification.updateStatus(FileDownloadStatus.PROGRESS);
        notification.update(sofar, total);
    }

    /**
     * Show the notification with indeterminate PROGRESS.
     *
     * @param id     The download id.
     * @param status {@link FileDownloadStatus}
     */
    public void showIndeterminate(final int id, int status) {
        final com.dzbook.filedownloader.notification.BaseNotificationItem notification = get(id);

        if (notification == null) {
            return;
        }

        notification.updateStatus(status);
        notification.show(false);
    }

    /**
     * Cancel the notification by notification id.
     *
     * @param id The download id.
     */
    public void cancel(final int id) {
        final com.dzbook.filedownloader.notification.BaseNotificationItem notification = remove(id);

        if (notification == null) {
            return;
        }

        notification.cancel();
    }

    /**
     * Clear and cancel all notifications which inside this helper {@link #notificationArray}.
     */
    public void clear() {
        @SuppressWarnings("unchecked") SparseArray<com.dzbook.filedownloader.notification.BaseNotificationItem> cloneArray =
                (SparseArray<com.dzbook.filedownloader.notification.BaseNotificationItem>) notificationArray.clone();
        notificationArray.clear();

        for (int i = 0; i < cloneArray.size(); i++) {
            final com.dzbook.filedownloader.notification.BaseNotificationItem n = cloneArray.get(cloneArray.keyAt(i));
            n.cancel();
        }

    }
}
