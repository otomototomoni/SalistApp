package com.example.salistapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity

class WebViewPage: AppCompatActivity() {
    private var webView: WebView? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        val URL = intent.getStringExtra("url").toString()

        webView = findViewById(R.id.webView)
        webView?.settings?.javaScriptEnabled = true
        webView?.loadUrl(URL)

    }
}