package com.example.salistapp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class HistoryActivity : AppCompatActivity() {
    private lateinit var historyListView: ListView
    private val historyArticles = ArrayList<Article>() // 履歴リスト

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        historyListView = findViewById(R.id.history_list_view)

        // ダミーデータ (必要なら削除)
        historyArticles.add(Article("履歴記事1", "https://example.com/3", "Qiita"))
        historyArticles.add(Article("履歴記事2", "https://example.com/4", "Zenn"))

        // ListView にデータをセット
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, historyArticles.map { it.getTitle() })
        historyListView.adapter = adapter
    }
}
