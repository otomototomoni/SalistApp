package com.example.salistapp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class FavoriteActivity : AppCompatActivity() {
    private lateinit var favoriteListView: ListView
    private val favoriteArticles = ArrayList<Article>() // お気に入りリスト

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)

        favoriteListView = findViewById(R.id.favorite_list_view)

        // ダミーデータ (必要なら削除)
        favoriteArticles.add(Article("お気に入り記事1", "https://example.com/1", "Qiita"))
        favoriteArticles.add(Article("お気に入り記事2", "https://example.com/2", "Zenn"))

        // ListView にデータをセット
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, favoriteArticles.map { it.getTitle() })
        favoriteListView.adapter = adapter
    }
}
