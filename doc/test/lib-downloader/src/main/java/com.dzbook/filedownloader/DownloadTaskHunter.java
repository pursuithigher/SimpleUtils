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

import com.dzbook.filedownloader.message.MessageSnapshot;
import com.dzbook.filedownloader.message.MessageSnapshotTaker;
import com.dzbook.filedownloader.model.FileDownloadHeader;
import com.dzbook.filedownloader.model.FileDownloadStatus;
import com.dzbook.filedownloader.util.FileDownloadHelper;
import com.dzbook.filedownloader.util.FileDownloadLog;
import com.dzbook.filedownloader.util.FileDownloadUtils;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;

/**
 * The download task hunter.
 */

public class DownloadTaskHunter implements com.dzbook.filedownloader.ITaskHunter, com.dzbook.filedownloader.ITaskHunter.IStarter, com.dzbook.filedownloader.ITaskHunter.IMessageHandler, com.dzbook.filedownloader.BaseDownloadTask.LifeCycleCallback {
    private IFileDownloadMessenger mMessenger;

    private final Object mPauseLock;
    private final ICaptureTask mTask;
    private volatile byte mStatus = FileDownloadStatus.INVALID_STATUS;
    private Throwable mThrowable = null;

    private final com.dzbook.filedownloader.IDownloadSpeed.Monitor mSpeedMonitor;
    private final IDownloadSpeed.Lookup mSpeedLookup;

    private long mSoFarBytes;
    private long mTotalBytes;

    private int mRetryingTimes;

    private boolean mIsLargeFile;

    private boolean mIsResuming;
    private String mEtag;

    private boolean mIsReusedOldFile = false;

    DownloadTaskHunter(ICaptureTask task, Object pauseLock) {
        mPauseLock = pauseLock;
        mTask = task;
        final DownloadSpeedMonitor monitor = new DownloadSpeedMonitor();
        mSpeedMonitor = monitor;
        mSpeedLookup = monitor;
        mMessenger = new FileDownloadMessenger(task.getRunningTask(), this);
    }


    @Override
    public boolean updateKeepAhead(MessageSnapshot snapshot) {
        if (!FileDownloadStatus.isKeepAhead(getStatus(), snapshot.getStatus())) {
            if (FileDownloadLog.NEED_LOG) {
                FileDownloadLog.d(this, "can't update mStatus change by keep ahead, %d, but the" + " current mStatus is %d, %d", mStatus, getStatus(), getId());
            }
            return false;
        }

        update(snapshot);
        return true;
    }

    @Override
    public boolean updateKeepFlow(MessageSnapshot snapshot) {
        final int currentStatus = getStatus();
        final int nextStatus = snapshot.getStatus();

        if (FileDownloadStatus.PAUSED == currentStatus && FileDownloadStatus.isIng(nextStatus)) {
            if (FileDownloadLog.NEED_LOG) {
                /**
                 * Occur such situation, must be the running-mStatus waiting for turning up in flow
                 * thread pool(or binder thread) when there is someone invoked the {@link #pause()}.
                 *
                 * High concurrent cause.
                 */
                FileDownloadLog.d(this, "High concurrent cause, callback PENDING, but has already" + " be paused %d", getId());
            }
            return true;
        }

        if (!FileDownloadStatus.isKeepFlow(currentStatus, nextStatus)) {
            if (FileDownloadLog.NEED_LOG) {
                FileDownloadLog.d(this, "can't update mStatus change by keep flow, %d, but the" + " current mStatus is %d, %d", mStatus, getStatus(), getId());
            }

            return false;
        }

        update(snapshot);
        return true;
    }

    @Override
    public boolean updateMoreLikelyCompleted(MessageSnapshot snapshot) {
        if (!FileDownloadStatus.isMoreLikelyCompleted(mTask.getRunningTask().getOrigin())) {
            return false;
        }

        update(snapshot);
        return true;
    }

    @Override
    public boolean updateSameFilePathTaskRunning(MessageSnapshot snapshot) {
        if (!mTask.getRunningTask().getOrigin().isPathAsDirectory()) {
            return false;
        }

        if (snapshot.getStatus() != FileDownloadStatus.WARN || getStatus() != FileDownloadStatus.CONNECTED) {
            return false;
        }

        update(snapshot);
        return true;
    }

    @Override
    public IFileDownloadMessenger getMessenger() {
        return mMessenger;
    }

    @Override
    public MessageSnapshot prepareErrorMessage(Throwable cause) {
        mStatus = FileDownloadStatus.ERROR;
        mThrowable = cause;
        return MessageSnapshotTaker.catchException(getId(), getSofarBytes(), cause);
    }

    @SuppressWarnings("checkstyle:emptyblock")
    private void update(final MessageSnapshot snapshot) {
        final com.dzbook.filedownloader.BaseDownloadTask task = mTask.getRunningTask().getOrigin();
        final byte status = snapshot.getStatus();

        this.mStatus = status;
        this.mIsLargeFile = snapshot.isLargeFile();

        switch (status) {
            case FileDownloadStatus.PENDING:
                handlePending(snapshot);
                break;
            case FileDownloadStatus.STARTED:
                // notify
                mMessenger.notifyStarted(snapshot);
                break;
            case FileDownloadStatus.CONNECTED:
                handleConnected(snapshot, task);
                break;
            case FileDownloadStatus.PROGRESS:
                handleProgress(snapshot);
                break;
            //            case FileDownloadStatus.blockComplete:
            /**
             * Handled by {@link com.dzbook.filedownloader.FileDownloadList#removeByCompleted(com.dzbook.filedownloader.BaseDownloadTask)}
             */
            //                break;
            case FileDownloadStatus.RETRY:
                handleRetry(snapshot);
                break;
            case FileDownloadStatus.ERROR:
                handleError(snapshot);

                break;
            case FileDownloadStatus.PAUSED:
                /**
                 * Handled by {@link #pause()}
                 */
                break;
            case FileDownloadStatus.COMPLETED:
                handleCompleted(snapshot);

                break;
            case FileDownloadStatus.WARN:
                handleWarn(snapshot, task);
                break;
            default:
                // ignored
        }
    }

    private void handleWarn(MessageSnapshot snapshot, BaseDownloadTask task) {
        mSpeedMonitor.reset();

        final int sameIdTaskCount = FileDownloadList.getImpl().count(task.getId());

        final int sameStoreTaskCount;
        // generate same task id.
        if (sameIdTaskCount <= 1 && task.isPathAsDirectory()) {
            sameStoreTaskCount = FileDownloadList.getImpl().count(FileDownloadUtils.
                    generateId(task.getUrl(), task.getTargetFilePath()));
        } else {
            sameStoreTaskCount = 0;
        }

        if (sameIdTaskCount + sameStoreTaskCount <= 1) {
            // 1. this PROGRESS kill by sys and relive,
            // for add at least one mListener
            // or 2. pre downloading task has already completed/error/paused
            // request mStatus
            final int currentStatus = FileDownloadServiceProxy.getImpl().
                    getStatus(task.getId());
            FileDownloadLog.w(this, "warn, but no mListener to receive, " + "switch to PENDING %d %d", task.getId(), currentStatus);

            //noinspection StatementWithEmptyBody
            if (FileDownloadStatus.isIng(currentStatus)) {
                // ing, has callbacks
                // keep and wait callback

                this.mStatus = FileDownloadStatus.PENDING;
                this.mTotalBytes = snapshot.getLargeTotalBytes();
                this.mSoFarBytes = snapshot.getLargeSofarBytes();

                mSpeedMonitor.start(mSoFarBytes);

                mMessenger.
                        notifyPending(((MessageSnapshot.IWarnMessageSnapshot) snapshot).
                                turnToPending());
                return;
            }

        }

        // to FileDownloadList
        FileDownloadList.getImpl().remove(mTask.getRunningTask(), snapshot);
    }

    private void handleCompleted(MessageSnapshot snapshot) {
        this.mIsReusedOldFile = snapshot.isReusedDownloadedFile();
        // only carry total data back
        this.mSoFarBytes = snapshot.getLargeTotalBytes();
        this.mTotalBytes = snapshot.getLargeTotalBytes();

        // to FileDownloadList
        FileDownloadList.getImpl().remove(mTask.getRunningTask(), snapshot);
    }

    private void handleError(MessageSnapshot snapshot) {
        this.mThrowable = snapshot.getThrowable();
        this.mSoFarBytes = snapshot.getLargeSofarBytes();

        // to FileDownloadList
        FileDownloadList.getImpl().remove(mTask.getRunningTask(), snapshot);
    }

    private void handleRetry(MessageSnapshot snapshot) {
        this.mSoFarBytes = snapshot.getLargeSofarBytes();
        this.mThrowable = snapshot.getThrowable();
        mRetryingTimes = snapshot.getRetryingTimes();

        mSpeedMonitor.reset();

        // notify
        mMessenger.notifyRetry(snapshot);
    }

    private void handleProgress(MessageSnapshot snapshot) {
        this.mSoFarBytes = snapshot.getLargeSofarBytes();
        mSpeedMonitor.update(snapshot.getLargeSofarBytes());

        // notify
        mMessenger.notifyProgress(snapshot);
    }

    private void handleConnected(MessageSnapshot snapshot, BaseDownloadTask task) {
        this.mTotalBytes = snapshot.getLargeTotalBytes();
        this.mIsResuming = snapshot.isResuming();
        this.mEtag = snapshot.getEtag();

        final String filename = snapshot.getFileName();
        if (filename != null) {
            if (task.getFilename() != null) {
                FileDownloadLog.w(this, "already has mFilename[%s], but assign mFilename[%s] again", task.getFilename(), filename);
            }
            mTask.setFileName(filename);
        }

        mSpeedMonitor.start(mSoFarBytes);

        // notify
        mMessenger.notifyConnected(snapshot);
    }

    private void handlePending(MessageSnapshot snapshot) {
        this.mSoFarBytes = snapshot.getLargeSofarBytes();
        this.mTotalBytes = snapshot.getLargeTotalBytes();

        // notify
        mMessenger.notifyPending(snapshot);
    }

    @Override
    public void onBegin() {
        if (FileDownloadMonitor.isValid()) {
            FileDownloadMonitor.getMonitor().onTaskBegin(mTask.getRunningTask().getOrigin());
        }

        if (FileDownloadLog.NEED_LOG) {
            FileDownloadLog.v(this, "filedownloader:lifecycle:start %s by %d ", toString(), getStatus());
        }
    }

    @Override
    public void onIng() {
        if (FileDownloadMonitor.isValid() && getStatus() == FileDownloadStatus.STARTED) {
            FileDownloadMonitor.getMonitor().onTaskStarted(mTask.getRunningTask().getOrigin());
        }
    }

    @Override
    public void onOver() {
        final com.dzbook.filedownloader.BaseDownloadTask origin = mTask.getRunningTask().getOrigin();

        if (FileDownloadMonitor.isValid()) {
            FileDownloadMonitor.getMonitor().onTaskOver(origin);
        }

        if (FileDownloadLog.NEED_LOG) {
            FileDownloadLog.v(this, "filedownloader:lifecycle:over %s by %d ", toString(), getStatus());
        }

        mSpeedMonitor.end(this.mSoFarBytes);
        if (mTask.getFinishListenerList() != null) {
            @SuppressWarnings("unchecked") final ArrayList<com.dzbook.filedownloader.BaseDownloadTask.FinishListener> listenersCopy = (ArrayList<com.dzbook.filedownloader.BaseDownloadTask.FinishListener>) mTask.getFinishListenerList().clone();
            final int numListeners = listenersCopy.size();
            for (int i = 0; i < numListeners; ++i) {
                listenersCopy.get(i).over(origin);
            }
        }

        com.dzbook.filedownloader.FileDownloader.getImpl().getLostConnectedHandler().taskWorkFine(mTask.getRunningTask());
    }

    /**
     * 接口
     */
    interface ICaptureTask {


        FileDownloadHeader getHeader();

        com.dzbook.filedownloader.BaseDownloadTask.IRunningTask getRunningTask();

        void setFileName(String fileName);

        ArrayList<com.dzbook.filedownloader.BaseDownloadTask.FinishListener> getFinishListenerList();

    }

    @Override
    public void intoLaunchPool() {
        synchronized (mPauseLock) {
            if (mStatus != FileDownloadStatus.INVALID_STATUS) {
                FileDownloadLog.w(this, "High concurrent cause, this task %d will not input " + "to launch pool, because of the status isn't idle : %d", getId(), mStatus);
                return;
            }

            mStatus = FileDownloadStatus.TO_LAUNCH_POOL;
        }

        final com.dzbook.filedownloader.BaseDownloadTask.IRunningTask runningTask = mTask.getRunningTask();
        final com.dzbook.filedownloader.BaseDownloadTask origin = runningTask.getOrigin();

        if (FileDownloadMonitor.isValid()) {
            FileDownloadMonitor.getMonitor().onRequestStart(origin);
        }

        if (FileDownloadLog.NEED_LOG) {
            FileDownloadLog.v(this, "call start " + "Url[%s], Path[%s] Listener[%s], Tag[%s]", origin.getUrl(), origin.getPath(), origin.getListener(), origin.getTag());
        }

        boolean ready = true;

        try {
            prepare();
        } catch (Throwable e) {
            ready = false;

            com.dzbook.filedownloader.FileDownloadList.getImpl().add(runningTask);
            com.dzbook.filedownloader.FileDownloadList.getImpl().remove(runningTask, prepareErrorMessage(e));
        }

        if (ready) {
            FileDownloadTaskLauncher.getImpl().launch(this);
        }

        if (FileDownloadLog.NEED_LOG) {
            FileDownloadLog.v(this, "the task[%d] has been into the launch pool.", getId());
        }
    }

    @Override
    public boolean pause() {
        if (FileDownloadStatus.isOver(getStatus())) {
            if (FileDownloadLog.NEED_LOG) {
                /**
                 * The over-mStatus call-backed and set the over-mStatus to this task between here
                 * area and remove from the {@link com.dzbook.filedownloader.FileDownloadList}.
                 *
                 * High concurrent cause.
                 */
                FileDownloadLog.d(this, "High concurrent cause, Already is over, can't pause " + "again, %d %d", getStatus(), mTask.getRunningTask().getOrigin().getId());
            }
            return false;
        }
        this.mStatus = FileDownloadStatus.PAUSED;

        final com.dzbook.filedownloader.BaseDownloadTask.IRunningTask runningTask = mTask.getRunningTask();
        final com.dzbook.filedownloader.BaseDownloadTask origin = runningTask.getOrigin();

        FileDownloadTaskLauncher.getImpl().expire(this);
        if (FileDownloadLog.NEED_LOG) {
            FileDownloadLog.v(this, "the task[%d] has been expired from the launch pool.", getId());
        }

        if (!com.dzbook.filedownloader.FileDownloader.getImpl().isServiceConnected()) {
            if (FileDownloadLog.NEED_LOG) {
                FileDownloadLog.d(this, "request pause the task[%d] to the download service," + " but the download service isn't CONNECTED yet.", origin.getId());
            }
        } else {
            FileDownloadServiceProxy.getImpl().pause(origin.getId());
        }

        // For make sure already added event mListener for receive paused event
        com.dzbook.filedownloader.FileDownloadList.getImpl().add(runningTask);
        com.dzbook.filedownloader.FileDownloadList.getImpl().remove(runningTask, MessageSnapshotTaker.catchPause(origin));

        com.dzbook.filedownloader.FileDownloader.getImpl().getLostConnectedHandler().taskWorkFine(runningTask);

        return true;
    }

    @Override
    public byte getStatus() {
        return mStatus;
    }

    @Override
    public void reset() {
        mThrowable = null;

        mEtag = null;
        mIsResuming = false;
        mRetryingTimes = 0;
        mIsReusedOldFile = false;
        mIsLargeFile = false;

        mSoFarBytes = 0;
        mTotalBytes = 0;

        mSpeedMonitor.reset();

        if (FileDownloadStatus.isOver(mStatus)) {
            mMessenger.discard();
            mMessenger = new FileDownloadMessenger(mTask.getRunningTask(), this);
        } else {
            mMessenger.reAppointment(mTask.getRunningTask(), this);
        }

        mStatus = FileDownloadStatus.INVALID_STATUS;
    }

    @Override
    public void setMinIntervalUpdateSpeed(int minIntervalUpdateSpeed) {
        mSpeedLookup.setMinIntervalUpdateSpeed(minIntervalUpdateSpeed);
    }

    @Override
    public int getSpeed() {
        return mSpeedLookup.getSpeed();
    }

    @Override
    public long getSofarBytes() {
        return mSoFarBytes;
    }

    @Override
    public long getTotalBytes() {
        return mTotalBytes;
    }

    @Override
    public Throwable getErrorCause() {
        return mThrowable;
    }

    @Override
    public int getRetryingTimes() {
        return mRetryingTimes;
    }

    @Override
    public boolean isReusedOldFile() {
        return mIsReusedOldFile;
    }

    @Override
    public boolean isResuming() {
        return mIsResuming;
    }

    @Override
    public String getEtag() {
        return mEtag;
    }

    @Override
    public boolean isLargeFile() {
        return mIsLargeFile;
    }

    @Override
    public void free() {
        if (FileDownloadLog.NEED_LOG) {
            FileDownloadLog.d(this, "free the task %d, when the status is %d", getId(), mStatus);
        }
        mStatus = FileDownloadStatus.INVALID_STATUS;
    }

    private void prepare() throws IOException {
        final com.dzbook.filedownloader.BaseDownloadTask.IRunningTask runningTask = mTask.getRunningTask();
        final com.dzbook.filedownloader.BaseDownloadTask origin = runningTask.getOrigin();

        if (origin.getPath() == null) {
            origin.setPath(FileDownloadUtils.getDefaultSaveFilePath(origin.getUrl()));
            if (FileDownloadLog.NEED_LOG) {
                FileDownloadLog.d(this, "save Path is null to %s", origin.getPath());
            }
        }

        final File dir;
        if (origin.isPathAsDirectory()) {
            dir = new File(origin.getPath());
        } else {
            final String dirString = FileDownloadUtils.getParent(origin.getPath());
            if (dirString == null) {
                throw new InvalidParameterException(FileDownloadUtils.formatString("the provided mPath[%s] is invalid," + " can't find its directory", origin.getPath()));
            }
            dir = new File(dirString);
        }

        if (!dir.exists()) {
            if (!dir.mkdirs() && !dir.exists()) {
                throw new IOException(FileDownloadUtils.
                        formatString("Create parent directory failed, please make sure " + "you have permission to create file or directory " + "on the path: %s", dir.getAbsolutePath()));
            }
        }
    }

    private int getId() {
        return mTask.getRunningTask().getOrigin().getId();
    }

    @SuppressWarnings("checkstyle:emptyblock")
    @Override
    public void start() {
        if (mStatus != FileDownloadStatus.TO_LAUNCH_POOL) {
            FileDownloadLog.w(this, "High concurrent cause, this task %d will not start," + " because the of status isn't TO_LAUNCH_POOL: %d", getId(), mStatus);
            return;
        }

        final com.dzbook.filedownloader.BaseDownloadTask.IRunningTask runningTask = mTask.getRunningTask();
        final BaseDownloadTask origin = runningTask.getOrigin();

        final ILostServiceConnectedHandler lostConnectedHandler = FileDownloader.getImpl().
                getLostConnectedHandler();
        try {

            if (lostConnectedHandler.dispatchTaskStart(runningTask)) {
                return;
            }

            synchronized (mPauseLock) {
                if (mStatus != FileDownloadStatus.TO_LAUNCH_POOL) {
                    FileDownloadLog.w(this, "High concurrent cause, this task %d will not start," + " the status can't assign to TO_FILE_DOWNLOAD_SERVICE, because " + "the status isn't TO_LAUNCH_POOL: %d", getId(), mStatus);
                    return;
                }

                mStatus = FileDownloadStatus.TO_FILE_DOWNLOAD_SERVICE;
            }

            com.dzbook.filedownloader.FileDownloadList.getImpl().add(runningTask);
            if (FileDownloadHelper.inspectAndInflowDownloaded(origin.getId(), origin.getTargetFilePath(), origin.isForceReDownload(), true)) {
                // Will be removed when the complete message is received in #update
                return;
            }

            final boolean succeed = FileDownloadServiceProxy.getImpl().
                    start(origin.getUrl(), origin.getPath(), origin.isPathAsDirectory(), origin.getCallbackProgressTimes(), origin.getCallbackProgressMinInterval(), origin.getAutoRetryTimes(), origin.isForceReDownload(), mTask.getHeader(), origin.isWifiRequired());

            if (mStatus == FileDownloadStatus.PAUSED) {
                FileDownloadLog.w(this, "High concurrent cause, this task %d will be paused," + "because of the status is paused, so the pause action must be " + "applied", getId());
                if (succeed) {
                    FileDownloadServiceProxy.getImpl().pause(getId());
                }
                return;
            }

            if (!succeed) {
                //noinspection StatementWithEmptyBody
                if (!lostConnectedHandler.dispatchTaskStart(runningTask)) {
                    final MessageSnapshot snapshot = prepareErrorMessage(new RuntimeException("Occur Unknown Error, when request to start" + " maybe some problem in binder, maybe the process was killed " + "in unexpected."));

                    if (com.dzbook.filedownloader.FileDownloadList.getImpl().isNotContains(runningTask)) {
                        lostConnectedHandler.taskWorkFine(runningTask);
                        com.dzbook.filedownloader.FileDownloadList.getImpl().add(runningTask);
                    }

                    com.dzbook.filedownloader.FileDownloadList.getImpl().remove(runningTask, snapshot);

                }
            } else {
                lostConnectedHandler.taskWorkFine(runningTask);
            }

        } catch (Throwable e) {
            e.printStackTrace();

            FileDownloadList.getImpl().remove(runningTask, prepareErrorMessage(e));
        }
    }

    @Override
    public boolean equalListener(FileDownloadListener listener) {
        return mTask.getRunningTask().getOrigin().getListener() == listener;
    }
}
