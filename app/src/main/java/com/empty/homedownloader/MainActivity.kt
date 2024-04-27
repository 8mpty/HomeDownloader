package com.empty.homedownloader

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.empty.homedownloader.utils.AppVersionChecker
import com.empty.homedownloader.utils.FetchHome
import com.empty.homedownloader.utils.FetchMY
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var rv: RecyclerView
    private lateinit var fetchHomeJob: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rv = findViewById(R.id.mainRV)
        rv.layoutManager = LinearLayoutManager(this)
        val appCheck = AppVersionChecker(this)
        val fetchRequest = FetchMY(rv)
        val fetchHome = FetchHome()

        val appVersion = BuildConfig.VERSION_NAME
        fetchHomeJob = CoroutineScope(Dispatchers.Main).launch {
            try {
                val targetVersion = fetchHome.fetchHomeRequest(BuildConfig.HOME_URL)
                appCheck.run(appVersion, targetVersion)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fetchRequest.fetchRequest(BuildConfig.MY_URL)
    }

    override fun onDestroy() {
        super.onDestroy()
        fetchHomeJob.cancel()
    }
}
