package com.dzbook.event.type;

/**
 * DownloadEvent
 * @author wxliao on 18/1/17.
 */
public class DownloadEvent {
    /**
     * STATE_PENDING
     */
    public static final int STATE_PENDING = 0x01;
    /**
     * STATE_PROGRESS
     */
    public static final int STATE_PROGRESS = 0x02;
    /**
     * STATE_COMPLETED
     */
    public static final int STATE_COMPLETED = 0x03;
    /**
     * STATE_PAUSED
     */
    public static final int STATE_PAUSED = 0x04;
    /**
     * STATE_ERROR
     */
    public static final int STATE_ERROR = 0x05;
    /**
     * STATE_WARN
     */
    public static final int STATE_WARN = 0x06;

    /**
     * state
     */
    public int state;
    /**
     * downloadSize
     */
    public long downloadSize;
    /**
     * totalSize
     */
    public long totalSize;
    /**
     * downloadUrl
     */
    public String downloadUrl;
    /**
     * savePath
     */
    public String savePath;

    /**
     * DownloadEvent
     * @param state state
     * @param downloadSize downloadSize
     * @param totalSize totalSize
     * @param downloadUrl downloadUrl
     * @param savePath savePath
     */
    public DownloadEvent(int state, long downloadSize, long totalSize, String downloadUrl, String savePath) {
        this.state = state;
        this.downloadSize = downloadSize;
        this.totalSize = totalSize;
        this.downloadUrl = downloadUrl;
        this.savePath = savePath;
    }
}
