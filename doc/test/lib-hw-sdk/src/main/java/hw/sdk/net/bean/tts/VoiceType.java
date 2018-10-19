package hw.sdk.net.bean.tts;

import java.io.Serializable;

/**
 * 声音类型
 * @author wxliao on 18/4/25.
 */

public class VoiceType implements Serializable {
    /**
     * 序列号
     */
    public int index;
    /**
     * 文件名字
     */
    public String fileName;

    /**
     * 构造器
     * @param index index
     * @param fileName 文件名
     */
    public VoiceType(int index, String fileName) {
        this.index = index;
        this.fileName = fileName;
    }
}
