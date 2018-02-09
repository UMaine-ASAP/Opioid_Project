package com.asap.mindfulness

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import kotlinx.android.synthetic.main.activity_web_view.*
import android.content.DialogInterface
import android.support.v4.app.NavUtils
import android.support.v7.app.AlertDialog
import android.webkit.JsResult
import android.webkit.WebChromeClient





class WebViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        web_view.loadUrl(intent.getStringExtra("url"))
        with(web_view.settings) {
            javaScriptEnabled = true
            builtInZoomControls = true
            loadWithOverviewMode = true
            displayZoomControls = false
            useWideViewPort = true
        }

        web_view.webChromeClient = ChromeBrowser()
    }

    private class ChromeBrowser : WebChromeClient() {
        override fun onJsAlert(view: WebView, url: String, message: String, result: JsResult): Boolean {
            val dialog = AlertDialog.Builder(view.context).setMessage(message).setPositiveButton("OK") { dialog, which ->
                //do nothing
            }.create()
            dialog.show()
            result.confirm()
            return true
        }
    }
}
