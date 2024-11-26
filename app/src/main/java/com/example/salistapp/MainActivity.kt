package com.example.salistapp

import android.os.Bundle
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.android.material.navigation.NavigationView
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var listView: ListView
    private var articles = ArrayList<Article>()
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        listView = findViewById(R.id.list_view)

        // ここからナビゲーションメニューの設定を追加
        val drawerLayout: DrawerLayout = findViewById(R.id.main) // DrawerLayout の ID を取得
        val navigationView: NavigationView = findViewById(R.id.nav_view) // NavigationView の ID を取得

        // ActionBarDrawerToggle を設定
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, findViewById(R.id.toolbar),
            R.string.open_drawer, R.string.close_drawer
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // NavigationView のメニュー項目クリックイベント
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> println("ホームが選択されました")
                R.id.nav_favorite -> {
                    println("お気に入りが選択されました")
                    startActivity(Intent(this, FavoriteActivity::class.java)) // お気に入りページ
                }
                R.id.nav_log -> {
                    startActivity(Intent(this, HistoryActivity::class.java)) // 履歴ページ
                }
            }
            drawerLayout.closeDrawers()
            true
        }

        //検索機能----------------------------------------------------------------------------

        //EditText　検索ボタンの取得
        val searchEditText = findViewById<EditText>(R.id.search_bar)

        //EditTextが変更されたかどうかを監視する。
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val searchText = s.toString()

                //一致するTitle、URL、Mediaを取得して filteredArticlesにリストとして（？）格納
                val filteredArticles = articles.filter { article ->
                    article.getTitle().contains(searchText, ignoreCase = true) ||
                            article.getUrl().contains(searchText, ignoreCase = true) ||
                                    article.getMedia().contains(searchText, ignoreCase = true)
                }

                //デバッグ用のログ
                Log.d("FilteredArticles", filteredArticles.toString())

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        }
        )

        //検索機能終わり--------------------------------------------------------------------------

        articles.clear()
        fetchQiitaArticle("")
        fetchZennArticle("")
    }

    override fun onStart() {
        super.onStart()
    }

    fun updateArticleList() {
        val adapter = object : ArrayAdapter<Article>(this, R.layout.item_article, articles) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val article = getItem(position)!!
                val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_article, parent, false)

                // アイコン、タイトル、サイト名を設定
                val iconView: ImageView = view.findViewById(R.id.article_icon)
                val mediaView: TextView = view.findViewById(R.id.article_media)
                val titleView: TextView = view.findViewById(R.id.article_title)
                val favoriteButton: ImageButton = view.findViewById(R.id.favorite_button)

                // アイコンを設定
                val iconResId = when (article.getMedia()) {
                    "Qiita" -> R.drawable.qiita_icon // Qiita用アイコン
                    "Zenn" -> R.drawable.zenn_icon   // Zenn用アイコン
                    else -> R.drawable.zenn_icon  // デフォルトアイコン
                }
                iconView.setImageResource(iconResId)

                mediaView.text = article.getMedia()
                titleView.text = article.getTitle()

                // viewのクリック
                view.setOnClickListener {
                    val intent = Intent(this@MainActivity, WebViewPage::class.java)
                    intent.putExtra("url", article.getUrl())
                    startActivity(intent)

                }
                // お気に入りボタンのクリックイベント
                favoriteButton.setOnClickListener {
                    // ここでお気に入り処理を追加
                    println("favorite")
                }

                return view
            }
        }

        runOnUiThread {
            listView.adapter = adapter
        }
    }

    fun fetchQiitaArticle(keyword: String = "") {
        val apiURL = if (keyword.isNotEmpty()) "https://qiita.com/api/v2/items?query=$keyword" else "https://qiita.com/api/v2/items"
        fetchArticle(apiURL)
    }

    fun fetchZennArticle(keyword: String = "") {
        val apiURL = if (keyword.isNotEmpty()) "https://zenn.dev/api/search?source=articles&q=$keyword" else "https://zenn.dev/api/articles"
        fetchArticle(apiURL)
    }

    fun fetchArticle(url: String) {
        val apiClient = ApiClient()
        apiClient.sendGetRequest(url) { response, error ->
            if (response != null) {
                // QiitaまたはZennの記事のレスポンスを処理
                if (url.contains("qiita")) {
                    val jsonArray = JSONArray(response)
                    for (i in 0 until jsonArray.length()) {
                        val articleJsonObject: JSONObject = jsonArray.getJSONObject(i)
                        val title = articleJsonObject.getString("title")
                        val url = articleJsonObject.getString("url")
                        val article = Article(title, url, "Qiita")
                        articles.add(article)
                    }
                } else if (url.contains("zenn")) {
                    val jsonObject = JSONObject(response)
                    val articlesArray = jsonObject.getJSONArray("articles")
                    for (i in 0 until articlesArray.length()) {
                        val articleJsonObject: JSONObject = articlesArray.getJSONObject(i)
                        val title = articleJsonObject.getString("title")
                        val url = "https://zenn.dev" + articleJsonObject.getString("path")
                        val article = Article(title, url, "Zenn")
                        articles.add(article)
                    }
                }

                articles.shuffle()
                updateArticleList()
            } else {
                println("No response received")
            }
        }
    }
}

class ApiClient {
    // OkHttpClientのインスタンスを作成
    private val client = OkHttpClient()

    // GETリクエストを送信する関数
    fun sendGetRequest(url: String, callback: (String?, Exception?) -> Unit) {
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null, e)
            }

            override fun onResponse(call: Call, response: Response) {
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

class Article(private val title: String, private val url: String, private val media: String) {
    fun getTitle(): String = title
    fun getUrl(): String = url
    fun getMedia(): String = media
}
