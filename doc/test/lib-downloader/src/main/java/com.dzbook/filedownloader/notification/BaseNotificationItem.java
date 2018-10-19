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

import android.app.NotificationManager;
import android.content.Context;

import com.dzbook.filedownloader.model.FileDownloadStatus;
import com.dzbook.filedownloader.util.FileDownloadHelper;

/**
 * An atom notification item which identify with a downloading task, they have the same downloading
 * Id.
 *
 * @see FileDownloadNotificationHelper
 * @see FileDownloadNotificationListener
 */
@SuppressWarnings("WeakerAccess")
public abstract class BaseNotificationItem {

    private int id, sofar, total;
    private String title, desc;

    private int status = FileDownloadStatus.INVALID_STATUS;
    private int lastStatus = FileDownloadStatus.INVALID_STATUS;
    private NotificationManager manager;

    /**
     * 构造
     *
     * @param id    id
     * @param title title
     * @param desc  desc
     */
    public BaseNotificationItem(final int id, final String title, final String desc) {
        this.id = id;

        this.title = title;
        this.desc = desc;
    }

    /**
     * 显示
     *
     * @param isShowProgress isShowProgress
     */
    public void show(boolean isShowProgress) {
        show(isChanged(), getStatus(), isShowProgress);
    }

    /**
     * 显示
     *
     * @param isShowProgress Whether there is a need to show the progress schedule changes
     * @param status         status
     * @param statusChanged  statusChanged
     */
    public abstract void show(boolean statusChanged, int status, boolean isShowProgress);

    /**
     * 更新
     *
     * @param sofa   sofar
     * @param total1 total
     */
    public void update(final int sofa, final int total1) {
        this.sofar = sofa;
        this.total = total1;
        show(true);
    }

    /**
     * 更新status
     *
     * @param stat stat
     */
    public void updateStatus(final int stat) {
        this.status = stat;
    }

    /**
     * 取消
     */
    public void cancel() {
        getManager().cancel(id);
    }


    /**
     * 获取NotificationManager
     *
     * @return NotificationManager
     */
    protected NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) FileDownloadHelper.getAppContext().
                    getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSofar() {
        return sofar;
    }

    public void setSofar(int sofar) {
        this.sofar = sofar;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * 获取status
     *
     * @return int
     */
    public int getStatus() {
        this.lastStatus = status;
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getLastStatus() {
        return lastStatus;
    }

    public boolean isChanged() {
        return this.lastStatus != status;
    }
}