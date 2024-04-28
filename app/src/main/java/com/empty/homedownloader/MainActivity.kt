package com.empty.homedownloader

import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
    private var appCheck : AppVersionChecker? = null
    private var appVersion : String = ""
    private var info : List<String> = listOf()

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

        appCheck = AppVersionChecker(this)
        val fetchRequest = FetchMY(this,rv)
        val fetchHome = FetchHome()

        appVersion = BuildConfig.VERSION_NAME
        fetchHomeJob = CoroutineScope(Dispatchers.Main).launch {
            try {
                info = fetchHome.fetchHomeRequest(BuildConfig.MY_URL)
                checkApp(appCheck!!, appVersion, info)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fetchRequest.fetchRequest(BuildConfig.MY_URL)
    }

    private fun checkApp(appCheck : AppVersionChecker  ,appVer : String, info : List<String>){
        appCheck.run(appVer, info)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.custom_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.menuAppVer ->{
                checkApp(appCheck!!,appVersion,info)
                return true
            } else -> super.onOptionsItemSelected(item)
        }
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
