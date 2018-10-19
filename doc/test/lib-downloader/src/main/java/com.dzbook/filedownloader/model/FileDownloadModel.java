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

package com.dzbook.filedownloader.model;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.dzbook.filedownloader.BaseDownloadTask;
import com.dzbook.filedownloader.database.FileDownloadDatabase;
import com.dzbook.filedownloader.util.FileDownloadUtils;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static com.dzbook.filedownloader.FileDownloader.TAG;

/**
 * The model of the downloading task will be used in the filedownloader database.
 *
 * @see FileDownloadDatabase
 */
@SuppressWarnings("WeakerAccess")
public class FileDownloadModel implements Parcelable {

    /**
     * 构造器
     */
    public static final Creator<FileDownloadModel> CREATOR =
            new Creator<FileDownloadModel>() {
                @Override
                public FileDownloadModel createFromParcel(Parcel source) {
                    return new FileDownloadModel(source);
                }

                @Override
                public FileDownloadModel[] newArray(int size) {
                    return new FileDownloadModel[size];
                }
            };

    /**
     * TOTAL_VALUE_IN_CHUNKED_RESOURCE
     */
    public static final int TOTAL_VALUE_IN_CHUNKED_RESOURCE = -1;
    /**
     * DEFAULT_CALLBACK_PROGRESS_TIMES
     */
    public static final int DEFAULT_CALLBACK_PROGRESS_TIMES = 100;
    /**
     * ID
     */
    public static final String ID = "_id";
    /**
     * URL
     */
    public static final String URL = "url";
    /**
     * PATH
     */
    public static final String PATH = "path";
    /**
     * PATH_AS_DIRECTORY
     */
    public static final String PATH_AS_DIRECTORY = "pathAsDirectory";
    /**
     * FILENAME
     */
    public static final String FILENAME = "filename";
    /**
     * STATUS
     */
    public static final String STATUS = "status";
    /**
     * SOFAR
     */
    public static final String SOFAR = "sofar";
    /**
     * TOTAL
     */
    public static final String TOTAL = "total";
    /**
     * ERR_MSG
     */
    public static final String ERR_MSG = "errMsg";
    /**
     * ETAG
     */
    public static final String ETAG = "etag";
    /**
     * CONNECTION_COUNT
     */
    public static final String CONNECTION_COUNT = "connectionCount";

    // download id
    private int id;
    // download url
    private String url;
    // save path
    private String path;
    private boolean pathAsDirectory;
    private String filename;
    private final AtomicInteger status;
    private final AtomicLong soFar;
    private long total;
    private String errMsg;
    // header
    private String eTag;
    private int connectionCount;
    private boolean isLargeFile;

    /**
     * 构造
     */
    public FileDownloadModel() {
        this.soFar = new AtomicLong();
        this.status = new AtomicInteger();
    }

    /**
     * 构造
     *
     * @param in 路径
     */
    protected FileDownloadModel(Parcel in) {
        this.id = in.readInt();
        this.url = in.readString();
        this.path = in.readString();
        this.pathAsDirectory = in.readByte() != 0;
        this.filename = in.readString();
        this.status = new AtomicInteger(in.readByte());
        this.soFar = new AtomicLong(in.readLong());
        this.total = in.readLong();
        this.errMsg = in.readString();
        this.eTag = in.readString();
        this.connectionCount = in.readInt();
        this.isLargeFile = in.readByte() != 0;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 设置路径
     *
     * @param aPath            path
     * @param aPathAsDirectory pathAsDirectory
     */
    public void setPath(String aPath, boolean aPathAsDirectory) {
        this.path = aPath;
        this.pathAsDirectory = aPathAsDirectory;
    }

    /**
     * 设置状态
     *
     * @param status 状态
     */
    public void setStatus(byte status) {
        this.status.set(status);
    }

    /**
     * setSoFar
     *
     * @param soFar soFar
     */
    public void setSoFar(long soFar) {
        this.soFar.set(soFar);
    }

    /**
     * increaseSoFar
     *
     * @param increaseBytes increaseBytes
     */
    public void increaseSoFar(long increaseBytes) {
        this.soFar.addAndGet(increaseBytes);
    }

    /**
     * 设置总数
     *
     * @param total 数量
     */
    public void setTotal(long total) {
        this.isLargeFile = total > Integer.MAX_VALUE;
        this.total = total;
    }

    public int getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    /**
     * Get the path user set from {@link BaseDownloadTask#setPath(String)}
     *
     * @return the path user set from {@link BaseDownloadTask#setPath(String)}
     * @see #getTargetFilePath()
     */
    public String getPath() {
        return path;
    }

    /**
     * Get the finally target file path is used for store the download file.
     * <p/>
     * This path is composited with {@link #path}、{@link #pathAsDirectory}、{@link #filename}.
     * <p/>
     * Why {@link #getPath()} may be not equal to getTargetFilePath()? this case only occurred
     * when the {@link #isPathAsDirectory()} is {@code true}, on this scenario the
     * {@link #getPath()} is directory, and the getTargetFilePath() is 'directory + "/" + filename'.
     *
     * @return the finally target file path.
     */
    public String getTargetFilePath() {
        return FileDownloadUtils.getTargetFilePath(getPath(), isPathAsDirectory(), getFilename());
    }

    /**
     * 返回文件路径
     *
     * @return 路径
     */
    public String getTempFilePath() {
        if (getTargetFilePath() == null) {
            return null;
        }
        return FileDownloadUtils.getTempPath(getTargetFilePath());
    }

    public byte getStatus() {
        return (byte) status.get();
    }

    public long getSoFar() {
        return soFar.get();
    }

    public long getTotal() {
        return total;
    }

    public boolean isChunked() {
        return total == TOTAL_VALUE_IN_CHUNKED_RESOURCE;
    }

    public String getETag() {
        return eTag;
    }

    public void setETag(String aETag) {
        this.eTag = aETag;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public boolean isPathAsDirectory() {
        return pathAsDirectory;
    }

    public String getFilename() {
        return filename;
    }

    public void setConnectionCount(int connectionCount) {
        this.connectionCount = connectionCount;
    }

    public int getConnectionCount() {
        return connectionCount;
    }

    /**
     * reset the connection count to default value: 1.
     */
    public void resetConnectionCount() {
        this.connectionCount = 1;
    }

    /**
     * 参数
     *
     * @return ContentValues 实例
     */
    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(ID, getId());
        cv.put(URL, getUrl());
        cv.put(PATH, getPath());
        cv.put(STATUS, getStatus());
        cv.put(SOFAR, getSoFar());
        cv.put(TOTAL, getTotal());
        cv.put(ERR_MSG, getErrMsg());
        cv.put(ETAG, getETag());
        cv.put(CONNECTION_COUNT, getConnectionCount());
        cv.put(PATH_AS_DIRECTORY, isPathAsDirectory());
        if (isPathAsDirectory() && getFilename() != null) {
            cv.put(FILENAME, getFilename());
        }

        return cv;
    }

    public boolean isLargeFile() {
        return isLargeFile;
    }

    /**
     * 删除任务文件
     */
    public void deleteTaskFiles() {
        deleteTempFile();
        deleteTargetFile();
    }

    /**
     * 删除临时文件
     */
    public void deleteTempFile() {
        final String tempFilePath = getTempFilePath();

        if (tempFilePath != null) {
            final File tempFile = new File(tempFilePath);
            if (tempFile.exists()) {
                //noinspection ResultOfMethodCallIgnored
                boolean delete = tempFile.delete();
                Log.d(TAG, "delete = " + delete);
            }
        }
    }

    /**
     * 删除临时文件
     */
    public void deleteTargetFile() {
        final String targetFilePath = getTargetFilePath();
        if (targetFilePath != null) {
            final File targetFile = new File(targetFilePath);
            if (targetFile.exists()) {
                //noinspection ResultOfMethodCallIgnored
                boolean delete = targetFile.delete();
                Log.d(TAG, "delete = " + delete);
            }
        }
    }

    @Override
    public String toString() {
        return FileDownloadUtils.formatString("id[%d], url[%s], path[%s], status[%d], sofar[%s],"
                        + " total[%d], etag[%s], %s",
                id, url, path, status.get(), soFar, total, eTag,
                super.toString());
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.url);
        dest.writeString(this.path);
        dest.writeByte(this.pathAsDirectory ? (byte) 1 : (byte) 0);
        dest.writeString(this.filename);
        dest.writeByte((byte) this.status.get());
        dest.writeLong(this.soFar.get());
        dest.writeLong(this.total);
        dest.writeString(this.errMsg);
        dest.writeString(this.eTag);
        dest.writeInt(this.connectionCount);
        dest.writeByte(this.isLargeFile ? (byte) 1 : (byte) 0);
    }
}
