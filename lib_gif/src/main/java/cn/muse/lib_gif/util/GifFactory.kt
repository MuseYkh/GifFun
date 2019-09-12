package cn.muse.lib_gif.util

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import cn.muse.ffmpegutil.FFmpegUtils
import cn.muse.lib_gif.GlideApp
import cn.muse.lib_gif.model.ResFrame
import cn.muse.lib_gif.decoder.AnimatedGifEncoder
import com.bumptech.glide.gifdecoder.GifDecoder
import com.bumptech.glide.load.resource.gif.GifDrawable
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.Collections.reverse
import java.util.concurrent.Executors

/**
 * @author: wanshi
 * created on: 2019-09-11 11:21
 * description:
 */
object GifFactory {

    private val mainHandler by lazy { Handler(Looper.getMainLooper()) }

    /**
     * 将视频转成GIF
     */
    fun videoToGif(context: Context, videoPath: String, onConvertListener: OnConvertListener?) {
        val outputPath = FileUtils.getAppCacheDir(context) + FileUtils.getFileName(videoPath, false) + ".gif"
        val commands = arrayOf("ffmpeg", "-y", "-i", videoPath, "-b", "400k", "-r", "15", "-vf", "fps=15,scale=270:-1", "-y", "-f", "gif", outputPath)
        val runnable = Runnable {
            val time = TaskTime()
            val result = FFmpegUtils.runCommands(commands)
            time.release("videoToGif")
            onConvertListener?.let {
                mainHandler.post {
                    if (result == 0) {
                        onConvertListener.onConvertSuccess(outputPath)
                    } else {
                        onConvertListener.onConvertFail()
                    }
                    onConvertListener.onConvertFinish()
                }
            }
        }
        val executor = Executors.newSingleThreadExecutor()
        executor.execute(runnable)
    }

    /**
     * GIF倒序
     */
    fun reverseGif(context: Context, originPath: String, onConvertListener: OnConvertListener?) {
        val runnable = Runnable {
            val taskTime = TaskTime()
            val drawable = GlideApp.with(context).asGif().load(originPath).submit().get()
            val frames = getResourceFrames(context, drawable)
            reverse(frames)
            val outputPath = generateGifByFrame(context, frames)
            onConvertListener?.let {
                mainHandler.post {
                    if (outputPath.isBlank()) {
                        onConvertListener.onConvertFail()
                    } else {
                        onConvertListener.onConvertSuccess(outputPath)
                    }
                    onConvertListener.onConvertFinish()
                }
            }
            taskTime.release("reverseGif")
        }
        val executor = Executors.newSingleThreadExecutor()
        executor.execute(runnable)
    }

    /**
     * 根据帧来生成GIF
     */
    private fun generateGifByFrame(context: Context, frames: List<ResFrame>): String {
        val time = TaskTime()
        val os = ByteArrayOutputStream()
        val encoder = AnimatedGifEncoder()
        encoder.start(os)
        encoder.setRepeat(0)
        for (value in frames) {
            val bitmap = BitmapFactory.decodeFile(value.path)
            encoder.setDelay(value.delay)
            encoder.addFrame(bitmap)
        }
        encoder.finish()

        val path = FileUtils.saveStreamToCache(context, os)
        time.release("generateGifByFrame")
        return path
    }

    /**
     * 获取GIF的每一帧
     */
    private fun getResourceFrames(context: Context, drawable: GifDrawable): List<ResFrame> {
        val time = TaskTime()
        val frames = ArrayList<ResFrame>()
        val decoder = getGifDecoder(drawable)
        decoder?.let {
            for (i in 0..drawable.frameCount) {
                val bitmap = decoder.nextFrame
                val path = FileUtils.saveBitmapToCache(context, bitmap, "pic_$i")
                val frame = ResFrame(decoder.getDelay(i), path)
                frames.add(frame)
                decoder.advance()
            }
        }
        time.release("getResourceFrames")
        return frames
    }

    /**
     * 获取GIF解码器
     */
    private fun getGifDecoder(resource: GifDrawable): GifDecoder? {
        var decoder: GifDecoder? = null
        val state = resource.constantState
        try {
            val field = state?.javaClass?.getDeclaredField("frameLoader")
            field?.isAccessible = true
            val loader = field?.get(state)
            val loaderField = loader?.javaClass?.getDeclaredField("gifDecoder")
            loaderField?.isAccessible = true
            val coder = loaderField?.get(loader)
            if (coder is GifDecoder) {
                decoder = coder
            }
        } catch (exception: ReflectiveOperationException) {
            exception.printStackTrace()
        }
        return decoder
    }

    interface OnConvertListener {
        fun onConvertSuccess(outputPath: String)

        fun onConvertFail()

        fun onConvertFinish() {

        }
    }
}
