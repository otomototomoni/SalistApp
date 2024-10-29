package com.example.salistapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {
    var articles = ArrayList<Atricle>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onStart() {
        super.onStart()

        fetchArticle("https://qiita.com/api/v2/items") { response, error ->
            if (response != null) {
                // `response`をJSON配列としてパース
                val jsonArray = JSONArray(response)

                for (i in 0 until jsonArray.length()) {
                    val articleJsonObject: JSONObject = jsonArray.getJSONObject(i)
                    val title = articleJsonObject.getString("title")
                    val url = articleJsonObject.getString("url")
                    val article = Atricle(title, url);
                    articles.add(article);
                }

            } else {
                println("No response received")
            }
            println(articles)
        }
    }

    fun fetchArticle(url: String, callback: (String?, Exception?) -> Unit) {
        val apiClient = ApiClient()

        apiClient.sendGetRequest(url) { response, error ->
            if (error != null) {
                println("Error: ${error.message}")
                callback(null, error)
            } else {
                println("Response: $response")
                callback(response, null)
            }
        }
    }
}

class ApiClient {
    // OkHttpClientのインスタンスを作成
    private val client = OkHttpClient()

    // GETリクエストを送信する関数
    fun sendGetRequest(url: String, callback: (String?, Exception?) -> Unit) {
        // リクエストの作成
        val request = Request.Builder()
            .url(url)
            .build()

        // 非同期でリクエストを実行
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // エラー時の処理
                callback(null, e)
            }

            override fun onResponse(call: Call, response: Response) {
                // レスポンスが成功したときの処理
                if (response.isSuccessful) {
                    response.body?.string()?.let { responseBody ->
                        callback(responseBody, null)
                    } ?: callback(null, IOException("Empty response"))
                } else {
                    callback(null, IOException("Unexpected code $response"))
                }
            }
        })
    }
}

class Atricle(title: String, url: String) {
    private var title = "";
    private var url = "";

    init {
        this.title = title;
        this.url = url;
    }

    fun getTitle(): String {
        return this.title;
    }
    fun getUrl(): String {
        return this.url;
    }
}