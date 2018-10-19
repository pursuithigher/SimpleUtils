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

import android.os.Parcel;
import android.os.Parcelable;

import com.dzbook.filedownloader.model.FileDownloadStatus;
import com.dzbook.filedownloader.util.FileDownloadUtils;

/**
 * The message snapshot.
 */
public abstract class MessageSnapshot implements IMessageSnapshot, Parcelable {

    /**
     * 创建常亮
     */
    public static final Creator<MessageSnapshot> CREATOR = new Creator<MessageSnapshot>() {
        @Override
        public MessageSnapshot createFromParcel(Parcel source) {
            boolean largeFile = source.readByte() == 1;
            byte status = source.readByte();
            final MessageSnapshot snapshot;
            switch (status) {
                case FileDownloadStatus.PENDING:
                    snapshot = handlePending(source, largeFile);
                    break;
                case FileDownloadStatus.STARTED:
                    snapshot = new StartedMessageSnapshot(source);
                    break;
                case FileDownloadStatus.CONNECTED:
                    snapshot = handleConnected(source, largeFile);
                    break;
                case FileDownloadStatus.PROGRESS:
                    snapshot = handleProgress(source, largeFile);
                    break;
                case FileDownloadStatus.RETRY:
                    snapshot = handleRetry(source, largeFile);
                    break;
                case FileDownloadStatus.ERROR:
                    snapshot = handleError(source, largeFile);
                    break;
                case FileDownloadStatus.COMPLETED:
                    snapshot = handleComplete(source, largeFile);
                    break;
                case FileDownloadStatus.WARN:
                    snapshot = handleWarn(source, largeFile);
                    break;
                default:
                    snapshot = null;
            }

            if (snapshot != null) {
                snapshot.isLargeFile = largeFile;
            } else {
                throw new IllegalStateException("Can't restore the snapshot because unknown "
                        + "status: " + status);
            }

            return snapshot;
        }

        @Override
        public MessageSnapshot[] newArray(int size) {
            return new MessageSnapshot[size];
        }
    };

    protected boolean isLargeFile;
    private final int id;

    MessageSnapshot(Parcel in) {
        this.id = in.readInt();
    }

    MessageSnapshot(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Throwable getThrowable() {
        throw new NoFieldException("getThrowable", this);
    }

    @Override
    public int getRetryingTimes() {
        throw new NoFieldException("getRetryingTimes", this);
    }

    @Override
    public boolean isResuming() {
        throw new NoFieldException("isResuming", this);
    }

    @Override
    public String getEtag() {
        throw new NoFieldException("getEtag", this);
    }

    @Override
    public long getLargeSofarBytes() {
        throw new NoFieldException("getLargeSofarBytes", this);
    }

    @Override
    public long getLargeTotalBytes() {
        throw new NoFieldException("getLargeTotalBytes", this);
    }

    @Override
    public int getSmallSofarBytes() {
        throw new NoFieldException("getSmallSofarBytes", this);
    }

    @Override
    public int getSmallTotalBytes() {
        throw new NoFieldException("getSmallTotalBytes", this);
    }

    @Override
    public boolean isReusedDownloadedFile() {
        throw new NoFieldException("isReusedDownloadedFile", this);
    }

    @Override
    public String getFileName() {
        throw new NoFieldException("getFileName", this);
    }

    @Override
    public boolean isLargeFile() {
        return isLargeFile;
    }

    /**
     * 警告接口
     */
    public interface IWarnMessageSnapshot {
        /**
         * PENDING
         *
         * @return 实例
         */
        MessageSnapshot turnToPending();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isLargeFile ? 1 : 0));
        dest.writeByte(getStatus());
        // normal
        dest.writeInt(this.id);
    }

    private static MessageSnapshot handleWarn(Parcel source, boolean largeFile) {
        MessageSnapshot snapshot;
        if (largeFile) {
            snapshot = new LargeMessageSnapshot.WarnMessageSnapshot(source);
        } else {
            snapshot = new SmallMessageSnapshot.WarnMessageSnapshot(source);
        }
        return snapshot;
    }

    private static MessageSnapshot handleComplete(Parcel source, boolean largeFile) {
        MessageSnapshot snapshot;
        if (largeFile) {
            snapshot = new LargeMessageSnapshot.CompletedSnapshot(source);
        } else {
            snapshot = new SmallMessageSnapshot.CompletedSnapshot(source);
        }
        return snapshot;
    }

    private static MessageSnapshot handleError(Parcel source, boolean largeFile) {
        MessageSnapshot snapshot;
        if (largeFile) {
            snapshot = new LargeMessageSnapshot.ErrorMessageSnapshot(source);
        } else {
            snapshot = new SmallMessageSnapshot.ErrorMessageSnapshot(source);
        }
        return snapshot;
    }

    private static MessageSnapshot handleRetry(Parcel source, boolean largeFile) {
        MessageSnapshot snapshot;
        if (largeFile) {
            snapshot = new LargeMessageSnapshot.RetryMessageSnapshot(source);
        } else {
            snapshot = new SmallMessageSnapshot.RetryMessageSnapshot(source);
        }
        return snapshot;
    }

    private static MessageSnapshot handleProgress(Parcel source, boolean largeFile) {
        MessageSnapshot snapshot;
        if (largeFile) {
            snapshot = new LargeMessageSnapshot.ProgressMessageSnapshot(source);
        } else {
            snapshot = new SmallMessageSnapshot.ProgressMessageSnapshot(source);
        }
        return snapshot;
    }

    private static MessageSnapshot handleConnected(Parcel source, boolean largeFile) {
        MessageSnapshot snapshot;
        if (largeFile) {
            snapshot = new LargeMessageSnapshot.ConnectedMessageSnapshot(source);
        } else {
            snapshot = new SmallMessageSnapshot.ConnectedMessageSnapshot(source);
        }
        return snapshot;
    }

    private static MessageSnapshot handlePending(Parcel source, boolean largeFile) {
        MessageSnapshot snapshot;
        if (largeFile) {
            snapshot = new LargeMessageSnapshot.PendingMessageSnapshot(source);
        } else {
            snapshot = new SmallMessageSnapshot.PendingMessageSnapshot(source);
        }
        return snapshot;
    }

    /**
     * 没有文件异常
     */
    public static class NoFieldException extends IllegalStateException {
        NoFieldException(String methodName, MessageSnapshot snapshot) {
            super(FileDownloadUtils.formatString("There isn't a field for '%s' in this message"
                            + " %d %d %s",
                    methodName, snapshot.getId(), snapshot.getStatus(),
                    snapshot.getClass().getName()));
        }
    }

    /**
     * Started Snapshot
     */
    public static class StartedMessageSnapshot extends MessageSnapshot {

        StartedMessageSnapshot(int id) {
            super(id);
        }

        StartedMessageSnapshot(Parcel in) {
            super(in);
        }

        @Override
        public byte getStatus() {
            return FileDownloadStatus.STARTED;
        }
    }


}
