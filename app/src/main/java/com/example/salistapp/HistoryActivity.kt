package com.example.salistapp

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.ActionBarDrawerToggle

class HistoryActivity : AppCompatActivity() {
    private lateinit var historyListView: ListView
    private val historyArticles = ArrayList<Article>() // 履歴リスト
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        // DrawerLayout と NavigationView の設定
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)

        // Toolbar 設定
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // ActionBarDrawerToggle の設定
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.open_drawer, R.string.close_drawer
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // NavigationView のアイテムクリックリスナー
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    // ホーム画面に遷移
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_favorite -> {
                    // お気に入り画面に遷移
                    val intent = Intent(this, FavoriteActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_log -> {
                    // 履歴画面に遷移（現在の画面なので何もしない）
                    drawerLayout.closeDrawers()
                }
            }
            true
        }

        // 履歴の表示
        historyListView = findViewById(R.id.history_list_view)

        // ダミーデータ (必要なら削除)
        historyArticles.add(Article("履歴記事1", "https://example.com/3", "Qiita"))
        historyArticles.add(Article("履歴記事2", "https://example.com/4", "Zenn"))

        // ListView にデータをセット
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, historyArticles.map { it.getTitle() })
        historyListView.adapter = adapter

        // タイトル、件数などの設定
        val historyTitle: TextView = findViewById(R.id.history_title)
        val historyCount: TextView = findViewById(R.id.history_count)

        historyTitle.text = "履歴一覧"
        historyCount.text = "${historyArticles.size}件"
    }
}
