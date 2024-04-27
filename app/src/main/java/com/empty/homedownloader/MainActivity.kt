package com.empty.homedownloader

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.empty.homedownloader.myclasses.Data
import com.empty.homedownloader.myclasses.ParentFeed
import com.empty.homedownloader.adapters.MY_RVAdapter
import com.empty.homedownloader.utils.AppVersionChecker
import com.google.gson.GsonBuilder
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var rv: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rv = findViewById(R.id.mainRV)
        rv.layoutManager = LinearLayoutManager(this)
        val appCheck = AppVersionChecker(this)

        fetchRequest()

        val appVersion = BuildConfig.VERSION_NAME
        val targetVersion = "1.0" // For testing
        appCheck.run(appVersion, targetVersion)
    }

    private fun fetchRequest(){
        val apiURL = BuildConfig.API_URL

        val request = Request.Builder().url(apiURL).build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                Log.d("RESPONSE", body.toString())

                val gson = GsonBuilder().create()
                val dataArray = gson.fromJson(body, Array<Data>::class.java)
                val parentFeed = ParentFeed(dataArray.toList())

                runOnUiThread {
                    rv.adapter = MY_RVAdapter(parentFeed)
                }
            }
        })
    }
}
