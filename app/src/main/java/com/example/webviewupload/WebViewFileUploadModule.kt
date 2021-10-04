package com.example.webviewupload

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.TextUtils
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class WebViewFileUploadModule {

    private var filePathCallback: ValueCallback<Array<Uri>>? = null
    private var imageFilePath: String? = null
    private var videoFilePath: String? = null

    fun getChooserIntent(
        context: Context,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: WebChromeClient.FileChooserParams?
    ): Intent {
        this.filePathCallback?.onReceiveValue(null)
        this.filePathCallback = filePathCallback

        val uploadType = getMimeTypesFromAcceptTypes(fileChooserParams?.acceptTypes)

        var imageIntent: Intent? = null
        var videoIntent: Intent? = null

        uploadType.forEach {
            if ((it.contains("jpg") || it.contains("image/*")) && imageIntent == null) {
                imageIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            } else if ((it.contains("mp4") || it.contains("video/*"))&& videoIntent == null) {
                videoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            }
        }

        var imageFile: File? = null
        if (imageIntent?.resolveActivity(context.packageManager) != null) {
            try {
                imageFile = createTempFile(".jpg", context.cacheDir)
            } catch (ex: Exception) {
                // handle your exception
            }

            if (imageFile != null) {
                imageFilePath = "file:" + imageFile.absolutePath
                imageIntent?.putExtra(MediaStore.EXTRA_OUTPUT, getUriFromFile(context, imageFile))
            }
        }

        var videoFile: File? = null
        if (videoIntent?.resolveActivity(context.packageManager) != null) {
            try {
                videoFile = createTempFile(".mp4", context.cacheDir)
            } catch (ex: Exception) {
            }

            if (videoFile != null) {
                videoFilePath = "file:" + videoFile.absolutePath
                videoIntent?.putExtra(MediaStore.EXTRA_OUTPUT, getUriFromFile(context, videoFile))
            }
        }

        val contentSelectionIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(Intent.EXTRA_MIME_TYPES, uploadType)
        }

        return Intent(Intent.ACTION_CHOOSER).apply {
            putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)

            mutableListOf<Intent>().also { list ->
                arrayOf(imageIntent, videoIntent).forEach { intent ->
                    intent?.also { list.add(it) }
                }

                if (list.size > 0) {
                    putExtra(Intent.EXTRA_INITIAL_INTENTS, list.toTypedArray())
                }
            }
        }
    }

    fun handleResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == WebViewActivity.WEB_VIEW_FILE_UPLOAD_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            val results = getResultUri(data)?.let { arrayOf(it) }
            if (results != null) {
                filePathCallback?.onReceiveValue(results)
            }
        } else {
            filePathCallback?.onReceiveValue(null)
        }
        filePathCallback = null
    }

    private fun getResultUri(data: Intent?): Uri? {
        var result: Uri? = null

        if (TextUtils.isEmpty(data?.dataString)) {
            arrayOf(imageFilePath, videoFilePath).forEach { path ->
                if (path != null) {
                    Uri.parse(path).also { uri ->
                        if (File(uri.path.toString()).totalSpace > 0) {
                            result = uri
                            return@forEach
                        }
                    }
                }
            }
        } else {
            val filePath = data?.dataString
            result = Uri.parse(filePath)
        }

        return result
    }

    private fun getMimeTypesFromAcceptTypes(acceptTypes: Array<String>?): Array<String> {
        return (acceptTypes
            ?.mapNotNull { acceptType ->
                when {
                    // 확장자, MIME Type 경우를 구분시킴
                    acceptType.startsWith(".") -> MimeTypeMap.getSingleton().getMimeTypeFromExtension(acceptType.substring(1))
                    else -> acceptType
                }
            }
            // acceptType 을 지정하지 경우를 필터링
            ?.filter { it.isNotBlank() }
            ?.takeIf { it.isNotEmpty() }
        // acceptType 타입이 없을 경우 모든 MIME 타입으로 설정
            ?: listOf("*/*"))
            .toTypedArray()
    }

    private fun createTempFile(extension: String, rootDirectory: File): File? {
        return try {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val filename = "${timestamp}_"

            File.createTempFile(filename, extension, rootDirectory)
        } catch (ex: Exception) {
            null
        }
    }

    private fun getUriFromFile(context: Context, file: File): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        } else {
            Uri.fromFile(file)
        }
    }
}
