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

import android.util.Log;

import com.dzbook.filedownloader.message.MessageSnapshotFlow;
import com.dzbook.filedownloader.util.FileDownloadExecutors;
import com.dzbook.filedownloader.util.FileDownloadLog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import static com.dzbook.filedownloader.FileDownloader.TAG;

/**
 * The global launcher for launching tasks.
 */
class FileDownloadTaskLauncher {

    /**
     * HolderClass
     */
    private static class HolderClass {
        private static final FileDownloadTaskLauncher INSTANCE = new FileDownloadTaskLauncher();

        static {
            // We add the message receiver to the message snapshot flow central, when there is a
            // task request to launch.
            MessageSnapshotFlow.getImpl().setReceiver(new com.dzbook.filedownloader.MessageSnapshotGate());
        }
    }

    private final LaunchTaskPool mLaunchTaskPool = new LaunchTaskPool();

    public static FileDownloadTaskLauncher getImpl() {
        return HolderClass.INSTANCE;
    }

    synchronized void launch(final com.dzbook.filedownloader.ITaskHunter.IStarter taskStarter) {
        mLaunchTaskPool.asyncExecute(taskStarter);
    }

    synchronized void expireAll() {
        mLaunchTaskPool.expireAll();
    }

    synchronized void expire(final com.dzbook.filedownloader.ITaskHunter.IStarter taskStarter) {
        mLaunchTaskPool.expire(taskStarter);
    }

    synchronized void expire(final com.dzbook.filedownloader.FileDownloadListener lis) {
        mLaunchTaskPool.expire(lis);
    }

    /**
     * LaunchTaskPool
     */
    private static class LaunchTaskPool {

        private ThreadPoolExecutor mPool;

        /**
         * the queue to use for holding tasks before they are
         * executed.  This queue will hold only the {@code Runnable}
         * tasks submitted by the {@code execute} method.
         */
        private LinkedBlockingQueue<Runnable> mWorkQueue;

        LaunchTaskPool() {
            init();
        }

        public void asyncExecute(final com.dzbook.filedownloader.ITaskHunter.IStarter taskStarter) {
            mPool.execute(new LaunchTaskRunnable(taskStarter));
        }

        public void expire(com.dzbook.filedownloader.ITaskHunter.IStarter starter) {
            /**
             * @see LaunchTaskRunnable#equals(Object)
             */
            //noinspection SuspiciousMethodCall
            if (starter instanceof Runnable) {
                Log.d(TAG, "remove=" + mWorkQueue.remove(starter));
            }
        }

        public void expire(final com.dzbook.filedownloader.FileDownloadListener listener) {
            if (listener == null) {
                FileDownloadLog.w(this, "want to expire by listener, but the listener provided is" + " null");
                return;
            }

            List<Runnable> needPauseList = new ArrayList<>();
            for (Runnable runnable : mWorkQueue) {
                final LaunchTaskRunnable launchTaskRunnable = (LaunchTaskRunnable) runnable;
                if (launchTaskRunnable.isSameListener(listener)) {
                    launchTaskRunnable.expire();
                    needPauseList.add(runnable);
                }
            }

            if (needPauseList.isEmpty()) {
                return;
            }

            if (FileDownloadLog.NEED_LOG) {
                FileDownloadLog.d(this, "expire %d tasks with listener[%s]", needPauseList.size(), listener);
            }

            for (Runnable runnable : needPauseList) {
                mPool.remove(runnable);
            }
        }

        public void expireAll() {
            if (FileDownloadLog.NEED_LOG) {
                FileDownloadLog.d(this, "expire %d tasks", mWorkQueue.size());
            }

            mPool.shutdownNow();
            init();
        }

        private void init() {
            mWorkQueue = new LinkedBlockingQueue<>();
            mPool = FileDownloadExecutors.newDefaultThreadPool(3, mWorkQueue, "LauncherTask");
        }

    }

    /**
     * Runnable
     */
    private static class LaunchTaskRunnable implements Runnable {
        private final com.dzbook.filedownloader.ITaskHunter.IStarter mTaskStarter;
        private boolean mExpired;

        LaunchTaskRunnable(final ITaskHunter.IStarter taskStarter) {
            this.mTaskStarter = taskStarter;
            this.mExpired = false;
        }

        @Override
        public void run() {
            if (mExpired) {
                return;
            }

            mTaskStarter.start();
        }

        public boolean isSameListener(final FileDownloadListener listener) {
            return mTaskStarter != null && mTaskStarter.equalListener(listener);
        }

        @SuppressWarnings("checkstyle:equalshashcode")
        @Override
        public boolean equals(Object obj) {
            return super.equals(obj) || obj == mTaskStarter;
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        public void expire() {
            this.mExpired = true;
        }
    }
}
