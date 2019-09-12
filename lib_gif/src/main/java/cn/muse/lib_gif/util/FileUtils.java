package cn.muse.lib_gif.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

import cn.muse.lib_gif.R;

/**
 * @author: wanshi
 * created on: 2019-09-10 16:53
 * description:
 */
public class FileUtils {

    /**
     * 截取文件的文件名
     *
     * @param filePath
     * @param isContainExt 是否包含拓展名
     * @return eg. "123.gif"  "123"（不包含拓展名）
     */
    public static String getFileName(String filePath, boolean isContainExt) {
        String fileName = "";
        fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        if (!isContainExt) {
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
        }
        return fileName;
    }

    public static String getFileName(String filePath) {
        return getFileName(filePath, true);
    }

    /**
     * 获取缓存路径(应用内缓存路径可用时优先使用)
     *
     * @param context
     * @return
     */
    public static String getAppCacheDir(Context context) {
        if (context.getExternalCacheDir() != null) {
            return context.getExternalCacheDir().getAbsolutePath() + "/";
        }
        return context.getCacheDir().getAbsolutePath() + "/";
    }

    /**
     * 获取外部存储路径
     *
     * @param context
     * @return
     */
    public static String getFileDir(Context context) {
        File externalDir = Environment.getExternalStorageDirectory();
        if (externalDir.canWrite()) {
            String dirPath = externalDir + "/GifFun/";
            File dir = new File(dirPath);
            if (!dir.exists()) {
                boolean isSuccess = dir.mkdirs();
                if (isSuccess) {
                    return dirPath;
                }
            } else {
                return dirPath;
            }
        }
        return "";
    }

    /**
     * 保存位图到缓存
     *
     * @param context
     * @param bitmap
     * @param fileName
     * @return 若成功则返回保存的路径，失败则返回空串
     */
    public static String saveBitmapToCache(Context context, Bitmap bitmap, String fileName) {
        if (bitmap == null) {
            return "";
        }
        String result = "";
        File outputDir = new File(getAppCacheDir(context) + "gif/");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        String outputPath = outputDir + fileName + ".jpg";
        File file = new File(outputPath);
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            result = file.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * 把GIF字节流写入文件
     *
     * @param context
     * @param outputStream
     * @return
     */
    public static String saveStreamToCache(Context context, @NotNull ByteArrayOutputStream outputStream) {
        String result = "";
        File outputDir = new File(getAppCacheDir(context) + "gif/");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        String outputPath = outputDir.getAbsolutePath() + "/" + System.currentTimeMillis() + ".gif";
        File file = new File(outputPath);
        OutputStream fileStream = null;
        try {
            fileStream = new FileOutputStream(file);
            outputStream.writeTo(fileStream);
            result = file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileStream != null) {
                    fileStream.close();
                }
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 保存图片并通知系统
     *
     * @param context
     * @param path
     * @return
     */
    public static boolean saveImageWithPath(Context context, @NotNull String path) {
        File file = new File(path);
        if (file.exists()) {
            String savePath = getFileDir(context) + getFileName(path);
            boolean result = copyFile(path, savePath);
            if (result) {
                sendActionToGallery(context, savePath);
            }
            return result;
        }
        return false;
    }

    /**
     * 复制文件
     *
     * @param oldPath
     * @param newPath
     * @return
     */
    private static boolean copyFile(String oldPath, String newPath) {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(oldPath).getChannel();
            outputChannel = new FileOutputStream(newPath).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputChannel != null) {
                    inputChannel.close();
                }
                if (outputChannel != null) {
                    outputChannel.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 通知系统媒体文件更新
     *
     * @param context
     * @param path
     */
    private static void sendActionToGallery(Context context, String path) {
        File file = new File(path);
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    @NotNull
    public static String getFileType(String path) {
        return path.substring(path.lastIndexOf(".") + 1).toLowerCase();
    }
}
