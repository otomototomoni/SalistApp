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

class FavoriteActivity : AppCompatActivity() {
    private lateinit var favoriteListView: ListView
    private val favoriteArticles = ArrayList<Article>() // お気に入りリスト
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)

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
                    // お気に入り画面に遷移（現在の画面なので何もしない）
                    drawerLayout.closeDrawers()
                }
                R.id.nav_log -> {
                    // 履歴画面に遷移
                    val intent = Intent(this, HistoryActivity::class.java)
                    startActivity(intent)
                }
            }
            true
        }

        // お気に入りの表示
        favoriteListView = findViewById(R.id.favorite_list_view)

        // ダミーデータ (必要なら削除)
        favoriteArticles.add(Article("お気に入り記事1", "https://example.com/1", "Qiita"))
        favoriteArticles.add(Article("お気に入り記事2", "https://example.com/2", "Zenn"))

        // ListView にデータをセット
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, favoriteArticles.map { it.getTitle() })
        favoriteListView.adapter = adapter

        // タイトル、件数などの設定
        val favoriteTitle: TextView = findViewById(R.id.favorite_title)
        val favoriteCount: TextView = findViewById(R.id.favorite_count)

        favoriteTitle.text = "お気に入り一覧"
        favoriteCount.text = "${favoriteArticles.size}件"
    }
}
