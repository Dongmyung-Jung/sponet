package com.sponet.sponet

//import android.icu.text.LocaleDisplayNames
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var mProgressBar: ProgressBar
    private var backBtnTime: Long = 0

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.SplashTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView1)
        mProgressBar = findViewById(R.id.progress1)

        webView.apply {
            this.webViewClient = WebViewClientClass()

            webChromeClient = object : WebChromeClient() {
                override fun onCreateWindow(view: WebView?, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message?): Boolean {
                    val newWebView = WebView(this@MainActivity).apply{
                        webViewClient = WebViewClient()
                        settings.javaScriptEnabled = true
                    }

                    val dialog = Dialog(this@MainActivity).apply {
                        setContentView(newWebView)
                        window!!.attributes.width = ViewGroup.LayoutParams.MATCH_PARENT
                        window!!.attributes.height = ViewGroup.LayoutParams.MATCH_PARENT
                        show()
                    }

                    newWebView.webChromeClient = object : WebChromeClient() {
                        override fun onCloseWindow(window: WebView?) {
                            dialog.dismiss()
                        }
                    }

                    (resultMsg?.obj as WebView.WebViewTransport).webView = newWebView
                    resultMsg.sendToTarget()
                    return true
                }
            }

            settings.javaScriptEnabled = true
            settings.setSupportMultipleWindows(true)
            settings.javaScriptCanOpenWindowsAutomatically = true
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            //Zoom Control
            settings.setSupportZoom(true)
            settings.displayZoomControls = false
            settings.builtInZoomControls = true

            //Enable and Setup Web view Cache
            settings.cacheMode = WebSettings.LOAD_NO_CACHE
            settings.domStorageEnabled = true

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                settings.safeBrowsingEnabled = true
            }
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                settings.mediaPlaybackRequiresUserGesture = true
            }

            settings.allowFileAccess = true
            //settings.loadsImagesAutomatically = true

            fitsSystemWindows = true
        }

        val url = "http://sponet.co.kr/BM/m"
        webView.loadUrl(url)

    }

    override fun onBackPressed() {
        val curTime = System.currentTimeMillis()
        val gapTime = curTime - backBtnTime
        if (webView.canGoBack()) {
            webView.goBack()
        } else if (0 <= gapTime && 2000 >= gapTime) {
            super.onBackPressed()
        } else {
            backBtnTime = curTime
            Toast.makeText(this, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
        }
    }

    inner class WebViewClientClass : WebViewClient() {
        @Deprecated("Deprecated in Java")
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            mProgressBar.visibility = ProgressBar.VISIBLE
            webView.visibility = View.INVISIBLE
        }

        override fun onPageCommitVisible(view: WebView, url: String) {
            super.onPageCommitVisible(view, url)
            mProgressBar.visibility = ProgressBar.GONE
            webView.visibility = View.VISIBLE
        }

        override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler, error: SslError) {

            handler.proceed(); // SSL 에러가 발생해도 계속 진행!

            /*
            var builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this@MainActivity)
            var message = "SSL Certificate Error"
            when(error.primaryError) {
                SslError.SSL_UNTRUSTED -> message = "The certificate authority is not trusted."
                SslError.SSL_EXPIRED -> message = "The certificate has expired."
                SslError.SSL_IDMISMATCH -> message = "The certificate Hostname missmatch."
                SslError.SSL_NOTYETVALID -> message = "The certificate is not yet valid."
            }

            message += "Do you want to continue anyway?"
            builder.setTitle("SSL Certificate Error")
            builder.setMessage(message)
            builder.setPositiveButton("continue", DialogInterface.OnClickListener { _, _ -> handler.proceed()})
            builder.setNegativeButton("cancel", DialogInterface.OnClickListener { _, _ -> handler.cancel()})

            val dialog: android.app.AlertDialog? = builder.create()
            dialog?.show()
             */
        }
    }
}


