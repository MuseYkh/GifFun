package cn.muse.lib_gif.util

import android.util.Log

/**
 * @author: wanshi
 * created on: 2019-09-11 15:12
 * description:
 */
internal class TaskTime {

    private var start = 0L

    init {
        start = System.currentTimeMillis()
    }

    fun release(name: String) {
        val time = System.currentTimeMillis() - start
        val result = "方法: $name  耗时 $time 毫秒"
        Log.d("GifFun", result)
    }
}
