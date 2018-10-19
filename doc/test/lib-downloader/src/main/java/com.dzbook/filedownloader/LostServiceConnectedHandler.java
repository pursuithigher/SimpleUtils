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

package com.dzbook.filedownloader;

import com.dzbook.filedownloader.event.DownloadServiceConnectChangedEvent;
import com.dzbook.filedownloader.util.FileDownloadHelper;
import com.dzbook.filedownloader.util.FileDownloadLog;

import java.util.ArrayList;
import java.util.List;

/**
 * The handler for handing the case of the connect with the downloader service is lost when tasks is
 * running.
 */

public class LostServiceConnectedHandler extends com.dzbook.filedownloader.FileDownloadConnectListener implements
        ILostServiceConnectedHandler {

    private final ArrayList<com.dzbook.filedownloader.BaseDownloadTask.IRunningTask> mWaitingList = new ArrayList<>();

    @Override
    public void connected() {
        final com.dzbook.filedownloader.IQueuesHandler queueHandler = com.dzbook.filedownloader.FileDownloader.getImpl().getQueuesHandler();
        final List<com.dzbook.filedownloader.BaseDownloadTask.IRunningTask> copyWaitingList;

        if (FileDownloadLog.NEED_LOG) {
            FileDownloadLog.d(this, "The downloader service is CONNECTED.");
        }

        synchronized (mWaitingList) {
            //noinspection unchecked
            copyWaitingList = (List<com.dzbook.filedownloader.BaseDownloadTask.IRunningTask>) mWaitingList.clone();
            mWaitingList.clear();

            final List<Integer> wakeupSerialQueueKeyList =
                    new ArrayList<>(queueHandler.serialQueueSize());

            for (com.dzbook.filedownloader.BaseDownloadTask.IRunningTask task : copyWaitingList) {
                final int attachKey = task.getAttachKey();
                if (queueHandler.contain(attachKey)) {
                    task.getOrigin().asInQueueTask().enqueue();

                    if (!wakeupSerialQueueKeyList.contains(attachKey)) {
                        wakeupSerialQueueKeyList.add(attachKey);
                    }

                    continue;
                }

                task.startTaskByRescue();
            }

            queueHandler.unFreezeSerialQueues(wakeupSerialQueueKeyList);
        }
    }

    @Override
    public void disconnected() {

        if (getConnectStatus() == DownloadServiceConnectChangedEvent.ConnectStatus.lost) {

            final IQueuesHandler queueHandler = com.dzbook.filedownloader.FileDownloader.getImpl().getQueuesHandler();
            // lost the connection to the service
            if (FileDownloadLog.NEED_LOG) {
                FileDownloadLog.d(this, "lost the connection to the "
                                + "file download service, and current active task size is %d",
                        com.dzbook.filedownloader.FileDownloadList.getImpl().size());
            }

            if (com.dzbook.filedownloader.FileDownloadList.getImpl().size() > 0) {
                synchronized (mWaitingList) {
                    com.dzbook.filedownloader.FileDownloadList.getImpl().divertAndIgnoreDuplicate(mWaitingList);
                    for (com.dzbook.filedownloader.BaseDownloadTask.IRunningTask task : mWaitingList) {
                        task.free();
                    }

                    queueHandler.freezeAllSerialQueues();
                }

                // start service during the app is in background, the IllegalStateException may be
                // thrown, but just ignore it is fun.
                try {
                    com.dzbook.filedownloader.FileDownloader.getImpl().bindService();
                } catch (IllegalStateException ignored) {
                    FileDownloadLog.w(this, "restart service failed, you may need to "
                            + "restart downloading manually when the app comes back to foreground");
                }
            }
        } else {

            if (com.dzbook.filedownloader.FileDownloadList.getImpl().size() > 0) {
                FileDownloadLog.w(this, "file download service has be unbound"
                                + " but the size of active tasks are not empty %d ",
                        FileDownloadList.getImpl().size());
            }
        }
    }

    @Override
    public boolean isInWaitingList(com.dzbook.filedownloader.BaseDownloadTask.IRunningTask task) {
        return !mWaitingList.isEmpty() && mWaitingList.contains(task);
    }

    @Override
    public void taskWorkFine(com.dzbook.filedownloader.BaseDownloadTask.IRunningTask task) {
        if (!mWaitingList.isEmpty()) {
            synchronized (mWaitingList) {
                mWaitingList.remove(task);
            }
        }
    }

    @Override
    public boolean dispatchTaskStart(BaseDownloadTask.IRunningTask task) {
        if (!com.dzbook.filedownloader.FileDownloader.getImpl().isServiceConnected()) {
            synchronized (mWaitingList) {
                if (!FileDownloader.getImpl().isServiceConnected()) {
                    if (FileDownloadLog.NEED_LOG) {
                        FileDownloadLog.d(this, "Waiting for connecting with the downloader "
                                + "service... %d", task.getOrigin().getId());
                    }
                    FileDownloadServiceProxy.getImpl().
                            bindStartByContext(FileDownloadHelper.getAppContext());
                    if (!mWaitingList.contains(task)) {
                        task.free();
                        mWaitingList.add(task);
                    }
                    return true;
                }
            }
        }

        taskWorkFine(task);

        return false;
    }
}
