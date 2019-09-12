package cn.muse.ffmpegutil;

/**
 * @author: wanshi
 * created on: 2019-09-09 15:08
 * description:
 */
public class FFmpegUtils {

    static {
        System.loadLibrary("ffmpeg-lib");
    }

    public static native int runCommands(String[] cmd);

    /**
     * 获取当前ffmpeg的版本
     * @return
     */
    public static native String getVersion();

}
