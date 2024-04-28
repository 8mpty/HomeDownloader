package com.empty.homedownloader

import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
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
    private val REQUEST_PERMISSION = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rv = findViewById(R.id.mainRV)
        rv.layoutManager = LinearLayoutManager(this)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_PERMISSION
                )
            }
        }

        val appCheck = AppVersionChecker(this)
        val fetchRequest = FetchMY(this,rv)
        val fetchHome = FetchHome()

        val appVersion = BuildConfig.VERSION_NAME
        fetchHomeJob = CoroutineScope(Dispatchers.Main).launch {
            try {
                val targetVersion = fetchHome.fetchHomeRequest(BuildConfig.MY_URL)
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with downloading
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
