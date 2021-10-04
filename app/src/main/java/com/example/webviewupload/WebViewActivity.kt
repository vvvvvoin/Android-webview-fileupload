package com.example.webviewupload

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import com.example.webviewupload.databinding.ActivityWebViewBinding

class WebViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWebViewBinding

    private val webViewFileUploadModule = WebViewFileUploadModule()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val url = intent.getStringExtra(MainActivity.YOUR_URL) ?: "https://pasteboard.co/"

        binding.webView.apply {
            settings.apply {
                javaScriptEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
                setSupportZoom(true)
                builtInZoomControls = true
                displayZoomControls = false
                cacheMode = WebSettings.LOAD_NO_CACHE
                domStorageEnabled = true

                allowFileAccess = true
            }

            webChromeClient = object : WebChromeClient() {

                override fun onCreateWindow(
                    view: WebView?,
                    isDialog: Boolean,
                    isUserGesture: Boolean,
                    resultMsg: Message?
                ): Boolean {
                    // intercept new window
                    val transport = resultMsg?.obj as? WebView.WebViewTransport
                    val newWebView = WebView(this@WebViewActivity)

                    view?.addView(newWebView)
                    transport?.webView = newWebView
                    resultMsg?.sendToTarget()

                    newWebView.webViewClient = object : WebViewClient() {
                        // handle url navigating and create new activity
                        override fun shouldOverrideUrlLoading(
                            view: WebView,
                            url: String
                        ): Boolean {
                            return when {
                                url.startsWith("tel:") -> {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse(url))
                                    startActivity(intent)
                                    true
                                }
                                url.startsWith("mailto:") -> {
                                    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse(url))
                                    startActivity(intent)
                                    true
                                }
                                else -> {
                                    Intent(view.context, WebViewActivity::class.java)
                                        .apply { putExtra(MainActivity.YOUR_URL, url) }
                                        .also { intent -> startActivity(intent) }
                                    true
                                }
                            }
                        }
                    }

                    return true
                }


                override fun onShowFileChooser(
                    webView: WebView?,
                    filePathCallback: ValueCallback<Array<Uri>>?,
                    fileChooserParams: FileChooserParams?
                ): Boolean {
                    startActivityForResult(
                        webViewFileUploadModule.getChooserIntent(
                            context,
                            filePathCallback,
                            fileChooserParams
                        ),
                        WEB_VIEW_FILE_UPLOAD_REQUEST_CODE
                    )

                    return true
                }
            }

            loadUrl(url)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        webViewFileUploadModule.handleResult(requestCode, resultCode, data)

        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        const val WEB_VIEW_FILE_UPLOAD_REQUEST_CODE = 9999
    }
}
