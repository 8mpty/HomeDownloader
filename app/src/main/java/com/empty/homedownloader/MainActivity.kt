package com.empty.homedownloader

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.empty.homedownloader.utils.AppVersionChecker
import com.empty.homedownloader.utils.FetchMY

class MainActivity : AppCompatActivity() {
    private lateinit var rv: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rv = findViewById(R.id.mainRV)
        rv.layoutManager = LinearLayoutManager(this)
        val appCheck = AppVersionChecker(this)
        val fetchRequest = FetchMY(rv)

        val appVersion = BuildConfig.VERSION_NAME
        val targetVersion = "1.0" // For testing
        appCheck.run(appVersion, targetVersion)

        fetchRequest.fetchRequest()
    }
}
