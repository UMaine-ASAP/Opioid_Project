package com.asap.mindfulness

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView

class WebViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        val webView = R.id.web_view as WebView
//        webView.settings.javaScriptEnabled = true
        webView.loadUrl(intent.getStringExtra("url"))
    }
}
