/*
 * Copyright (c) 2015 LingoChamp Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dzbook.filedownloader;

import android.app.Notification;
import android.os.Looper;

import com.dzbook.filedownloader.model.FileDownloadStatus;

import java.io.File;

/**
 * The FileDownload synchronous line.
 *
 * @see com.dzbook.filedownloader.FileDownloader#insureServiceBind()
 * @see #wait(ConnectSubscriber)
 */

public class FileDownloadLine {

    /**
     * The {@link com.dzbook.filedownloader.FileDownloader#startForeground(int, Notification)} request.
     *
     * @param id           id
     * @param notification notification
     */
    public void startForeground(final int id, final Notification notification) {
        if (com.dzbook.filedownloader.FileDownloader.getImpl().isServiceConnected()) {
            com.dzbook.filedownloader.FileDownloader.getImpl().startForeground(id, notification);
            return;
        }

        final ConnectSubscriber subscriber = new ConnectSubscriber() {
            @Override
            public void connected() {
                com.dzbook.filedownloader.FileDownloader.getImpl().startForeground(id, notification);
            }

            @Override
            public Object getValue() {
                return null;
            }
        };

        wait(subscriber);
    }

    /**
     * The {@link com.dzbook.filedownloader.FileDownloader#getSoFar(int)} request.
     *
     * @param id id
     * @return long
     */
    public long getSoFar(final int id) {
        if (com.dzbook.filedownloader.FileDownloader.getImpl().isServiceConnected()) {
            return com.dzbook.filedownloader.FileDownloader.getImpl().getSoFar(id);
        }

        final ConnectSubscriber subscriber = new ConnectSubscriber() {
            private long mValue;

            @Override
            public void connected() {
                mValue = com.dzbook.filedownloader.FileDownloader.getImpl().getSoFar(id);
            }

            @Override
            public Object getValue() {
                return mValue;
            }
        };

        wait(subscriber);

        return (long) subscriber.getValue();
    }

    /**
     * The {@link com.dzbook.filedownloader.FileDownloader#getTotal(int)} request.
     *
     * @param id id
     * @return long
     */
    public long getTotal(final int id) {
        if (com.dzbook.filedownloader.FileDownloader.getImpl().isServiceConnected()) {
            return com.dzbook.filedownloader.FileDownloader.getImpl().getTotal(id);
        }

        final ConnectSubscriber subscriber = new ConnectSubscriber() {
            private long mValue;

            @Override
            public void connected() {
                mValue = com.dzbook.filedownloader.FileDownloader.getImpl().getTotal(id);
            }

            @Override
            public Object getValue() {
                return mValue;
            }
        };

        wait(subscriber);

        return (long) subscriber.getValue();
    }

    /**
     * The {@link com.dzbook.filedownloader.FileDownloader#getStatus(int, String)} request.
     *
     * @param id   id
     * @param path path
     * @return byte
     */
    public byte getStatus(final int id, final String path) {
        if (com.dzbook.filedownloader.FileDownloader.getImpl().isServiceConnected()) {
            return com.dzbook.filedownloader.FileDownloader.getImpl().getStatus(id, path);
        }

        if (path != null && new File(path).exists()) {
            return FileDownloadStatus.COMPLETED;
        }

        final ConnectSubscriber subscriber = new ConnectSubscriber() {
            private byte mValue;

            @Override
            public void connected() {
                mValue = com.dzbook.filedownloader.FileDownloader.getImpl().getStatus(id, path);
            }

            @Override
            public Object getValue() {
                return mValue;
            }
        };

        wait(subscriber);

        return (byte) subscriber.getValue();
    }

    private void wait(final ConnectSubscriber subscriber) {
        final ConnectListener connectListener = new ConnectListener(subscriber);

        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (connectListener) {
            com.dzbook.filedownloader.FileDownloader.getImpl().bindService(connectListener);

            if (!connectListener.isFinished()) {

                if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
                    throw new IllegalThreadStateException("Sorry, FileDownloader can not block the "
                            + "main thread, because the system is also  callbacks "
                            + "ServiceConnection#onServiceConnected method in the main thread.");
                }

                try {
                    connectListener.wait(200 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * ConnectListener
     */
    static class ConnectListener implements Runnable {
        private boolean mIsFinished = false;
        private final ConnectSubscriber mSubscriber;

        ConnectListener(ConnectSubscriber subscriber) {
            this.mSubscriber = subscriber;
        }

        public boolean isFinished() {
            return mIsFinished;
        }

        @Override
        public void run() {
            synchronized (this) {
                mSubscriber.connected();
                mIsFinished = true;
                notifyAll();
            }
        }
    }

    /**
     * ConnectSubscriber
     */
    interface ConnectSubscriber {
        void connected();

        Object getValue();
    }
}
