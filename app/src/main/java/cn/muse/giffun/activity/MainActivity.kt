package cn.muse.giffun.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import cn.muse.giffun.R
import cn.muse.lib_gif.GlideApp
import cn.muse.lib_gif.util.FileUtils
import cn.muse.lib_gif.util.GifFactory
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.Executors


class MainActivity : BaseActivity() {

    companion object {
        private const val CHOOSE_VIDEO = 100
        private const val CHOOSE_GIF = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setProgressCanOutCancel(false)

        cv_mp4_to_gif.setOnClickListener {
            selectVideo()
        }
        cv_reverse_gif.setOnClickListener {
            selectGif()
        }
    }

    private fun selectGif() {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .isGif(true)
                .isCamera(false)
                .maxSelectNum(1)
                .previewImage(true)
                .synOrAsy(false)
                .forResult(CHOOSE_GIF)
    }

    private fun selectVideo() {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofVideo())
                .maxSelectNum(1)
                .videoMaxSecond(30)
                .previewVideo(true)
                .isCamera(true)
                .synOrAsy(false)
                .forResult(CHOOSE_VIDEO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                CHOOSE_VIDEO -> {
                    val selectList = PictureSelector.obtainMultipleResult(data)
                    if (selectList.size > 0) {
                        val video = selectList[0]
                        val path = video.path
                        if (!TextUtils.isEmpty(path)) {
                            convertToGif(path)
                        }
                    }
                }
                CHOOSE_GIF -> {
                    val selectList = PictureSelector.obtainMultipleResult(data)
                    if (selectList.size > 0) {
                        val media = selectList[0]
                        val path = media.path
                        val fileType = FileUtils.getFileType(path)
                        if (fileType != "gif") {
                            showToast("只能选择GIF图片")
                            return
                        }
                        reverseGif(path)
                    }
                }
            }
        }
    }

    private fun reverseGif(path: String) {
        showProgressDialog()
        GifFactory.reverseGif(this, path, object : GifFactory.OnConvertListener {
            override fun onConvertSuccess(outputPath: String) {
                showToast("转换成功!")
                GlideApp.with(this@MainActivity)
                        .asGif()
                        .load(outputPath)
                        .into(iv_trans_gif)
                btn_save.visibility = View.VISIBLE
                btn_save.setOnClickListener {
                    saveGif(outputPath)
                }
            }
            override fun onConvertFail() {
                showToast("转换失败")
                GlideApp.with(this@MainActivity)
                        .clear(iv_trans_gif)
                btn_save.visibility = View.GONE
            }
            override fun onConvertFinish() {
                hideProgressDialog()
                iv_origin_gif.visibility = View.VISIBLE
                GlideApp.with(this@MainActivity)
                        .asGif()
                        .load(path)
                        .into(iv_origin_gif)
            }
        })
    }

    private fun convertToGif(path: String) {
        showProgressDialog()
        GifFactory.videoToGif(this, path, object : GifFactory.OnConvertListener {
            override fun onConvertSuccess(outputPath: String) {
                showToast("转换成功!")
                iv_origin_gif.visibility = View.GONE
                GlideApp.with(this@MainActivity)
                        .asGif()
                        .load(outputPath)
                        .into(iv_trans_gif)
                btn_save.visibility = View.VISIBLE
                btn_save.setOnClickListener {
                    saveGif(outputPath)
                }
            }
            override fun onConvertFail() {
                showToast("转换失败")
            }
            override fun onConvertFinish() {
                hideProgressDialog()
            }
        })
    }

    private fun saveGif(outputPath: String) {
        val runnable = Runnable {
            val success = FileUtils.saveImageWithPath(this@MainActivity, outputPath)
            runOnUiThread {
                if (success) {
                    showToast("保存成功!")
                } else {
                    showToast("保存失败")
                }
            }
        }
        val executor = Executors.newSingleThreadExecutor()
        executor.execute(runnable)
    }
}
