package cn.muse.lib_gif.model

/**
 * @author: wanshi
 * created on: 2019-09-11 16:24
 * description:
 */
internal data class ResFrame(var delay: Int, var path: String) : Comparable<String> {
    override fun compareTo(other: String): Int {
        return other.compareTo(path)
    }
}