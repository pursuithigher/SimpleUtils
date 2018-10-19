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

import com.dzbook.filedownloader.model.FileDownloadStatus;

/**
 * A message snapshot for large file(the length is more than or equal to 2G).
 *
 * @see SmallMessageSnapshot
 * @see com.dzbook.filedownloader.message.BlockCompleteMessage
 */
public abstract class LargeMessageSnapshot extends com.dzbook.filedownloader.message.MessageSnapshot {

    LargeMessageSnapshot(int id) {
        super(id);
        isLargeFile = true;
    }

    LargeMessageSnapshot(Parcel in) {
        super(in);
    }

    @Override
    public int getSmallSofarBytes() {
        if (getLargeSofarBytes() > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }

        return (int) getLargeSofarBytes();
    }

    @Override
    public int getSmallTotalBytes() {
        if (getLargeTotalBytes() > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }

        return (int) getLargeTotalBytes();
    }

    /**
     * Pending Snapshot
     */
    public static class PendingMessageSnapshot extends LargeMessageSnapshot {

        private final long sofarBytes, totalBytes;

        PendingMessageSnapshot(Parcel in) {
            super(in);
            this.sofarBytes = in.readLong();
            this.totalBytes = in.readLong();
        }

        PendingMessageSnapshot(PendingMessageSnapshot snapshot) {
            this(snapshot.getId(), snapshot.getLargeSofarBytes(), snapshot.getLargeTotalBytes());
        }

        PendingMessageSnapshot(int id, long sofarBytes, long totalBytes) {
            super(id);
            this.sofarBytes = sofarBytes;
            this.totalBytes = totalBytes;
        }

        @Override
        public byte getStatus() {
            return FileDownloadStatus.PENDING;
        }

        @Override
        public long getLargeSofarBytes() {
            return sofarBytes;
        }

        @Override
        public long getLargeTotalBytes() {
            return totalBytes;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeLong(this.sofarBytes);
            dest.writeLong(this.totalBytes);
        }

    }

    /**
     * Connected Snapshot
     */
    public static class ConnectedMessageSnapshot extends LargeMessageSnapshot {
        private final boolean resuming;
        private final long totalBytes;
        private final String etag;
        private final String fileName;

        ConnectedMessageSnapshot(Parcel in) {
            super(in);
            this.resuming = in.readByte() != 0;
            this.totalBytes = in.readLong();
            this.etag = in.readString();
            this.fileName = in.readString();
        }

        ConnectedMessageSnapshot(int id, boolean resuming, long totalBytes,
                                 String etag, String fileName) {
            super(id);
            this.resuming = resuming;
            this.totalBytes = totalBytes;
            this.etag = etag;
            this.fileName = fileName;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeByte(resuming ? (byte) 1 : (byte) 0);
            dest.writeLong(this.totalBytes);
            dest.writeString(this.etag);
            dest.writeString(this.fileName);
        }

        @Override
        public String getFileName() {
            return fileName;
        }

        @Override
        public byte getStatus() {
            return FileDownloadStatus.CONNECTED;
        }

        @Override
        public boolean isResuming() {
            return resuming;
        }

        @Override
        public long getLargeTotalBytes() {
            return totalBytes;
        }

        @Override
        public String getEtag() {
            return etag;
        }
    }

    /**
     * Progress Snapshot
     */
    public static class ProgressMessageSnapshot extends LargeMessageSnapshot {
        private final long sofarBytes;

        ProgressMessageSnapshot(Parcel in) {
            super(in);
            this.sofarBytes = in.readLong();
        }

        ProgressMessageSnapshot(int id, long sofarBytes) {
            super(id);
            this.sofarBytes = sofarBytes;
        }

        @Override
        public byte getStatus() {
            return FileDownloadStatus.PROGRESS;
        }

        @Override
        public long getLargeSofarBytes() {
            return sofarBytes;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeLong(this.sofarBytes);
        }

    }

    /**
     * Completed Snapshot
     */
    public static class CompletedFlowDirectlySnapshot extends CompletedSnapshot implements
            com.dzbook.filedownloader.message.IFlowDirectly {

        CompletedFlowDirectlySnapshot(int id, boolean reusedDownloadedFile,
                                      long totalBytes) {
            super(id, reusedDownloadedFile, totalBytes);
        }

        CompletedFlowDirectlySnapshot(Parcel in) {
            super(in);
        }
    }

    /**
     * CompletedSnapshot
     */
    public static class CompletedSnapshot extends LargeMessageSnapshot {
        private final boolean reusedDownloadedFile;
        private final long totalBytes;

        CompletedSnapshot(Parcel in) {
            super(in);
            this.reusedDownloadedFile = in.readByte() != 0;
            this.totalBytes = in.readLong();
        }

        CompletedSnapshot(int id, boolean reusedDownloadedFile,
                          long totalBytes) {
            super(id);
            this.reusedDownloadedFile = reusedDownloadedFile;
            this.totalBytes = totalBytes;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeByte(reusedDownloadedFile ? (byte) 1 : (byte) 0);
            dest.writeLong(this.totalBytes);
        }

        @Override
        public byte getStatus() {
            return FileDownloadStatus.COMPLETED;
        }

        @Override
        public long getLargeTotalBytes() {
            return totalBytes;
        }

        @Override
        public boolean isReusedDownloadedFile() {
            return reusedDownloadedFile;
        }
    }

    /**
     * Error Snapshot
     */
    public static class ErrorMessageSnapshot extends LargeMessageSnapshot {
        private final long sofarBytes;
        private final Throwable throwable;

        ErrorMessageSnapshot(Parcel in) {
            super(in);
            this.sofarBytes = in.readLong();
            this.throwable = (Throwable) in.readSerializable();
        }

        ErrorMessageSnapshot(int id, long sofarBytes, Throwable throwable) {
            super(id);
            this.sofarBytes = sofarBytes;
            this.throwable = throwable;
        }

        @Override
        public long getLargeSofarBytes() {
            return sofarBytes;
        }

        @Override
        public byte getStatus() {
            return FileDownloadStatus.ERROR;
        }

        @Override
        public Throwable getThrowable() {
            return throwable;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeLong(this.sofarBytes);
            dest.writeSerializable(this.throwable);
        }

    }

    /**
     * Retry Snapshot
     */
    public static class RetryMessageSnapshot extends ErrorMessageSnapshot {
        private final int retryingTimes;

        RetryMessageSnapshot(Parcel in) {
            super(in);
            this.retryingTimes = in.readInt();
        }

        RetryMessageSnapshot(int id, long sofarBytes, Throwable throwable,
                             int retryingTimes) {
            super(id, sofarBytes, throwable);
            this.retryingTimes = retryingTimes;
        }

        @Override
        public int getRetryingTimes() {
            return retryingTimes;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(this.retryingTimes);
        }


        @Override
        public byte getStatus() {
            return FileDownloadStatus.RETRY;
        }
    }

    /**
     * Warn Snapshot
     */
    public static class WarnFlowDirectlySnapshot extends WarnMessageSnapshot implements
            IFlowDirectly {

        WarnFlowDirectlySnapshot(int id, long sofarBytes, long totalBytes) {
            super(id, sofarBytes, totalBytes);
        }

        WarnFlowDirectlySnapshot(Parcel in) {
            super(in);
        }
    }

    /**
     * 警告信息
     */
    public static class WarnMessageSnapshot extends PendingMessageSnapshot implements
            IWarnMessageSnapshot {

        WarnMessageSnapshot(int id, long sofarBytes, long totalBytes) {
            super(id, sofarBytes, totalBytes);
        }

        WarnMessageSnapshot(Parcel in) {
            super(in);
        }

        @Override
        public MessageSnapshot turnToPending() {
            return new PendingMessageSnapshot(this);
        }

        @Override
        public byte getStatus() {
            return FileDownloadStatus.WARN;
        }
    }

    /**
     * Paused Snapshot
     */
    public static class PausedSnapshot extends PendingMessageSnapshot {
        PausedSnapshot(int id, long sofarBytes, long totalBytes) {
            super(id, sofarBytes, totalBytes);
        }

        @Override
        public byte getStatus() {
            return FileDownloadStatus.PAUSED;
        }
    }
}
