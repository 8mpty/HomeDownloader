package com.empty.homedownloader.utils

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.empty.homedownloader.BuildConfig
import com.empty.homedownloader.adapters.MYRVAdapter
import com.empty.homedownloader.myclasses.Models
import com.google.gson.GsonBuilder
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class FetchMY(private val rv: RecyclerView) {
    private val mainHandler = Handler(Looper.getMainLooper())

    fun fetchRequest(api : String){
        val request = Request.Builder().url(api).build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                Log.d("RESPONSE", body.toString())

                val gson = GsonBuilder().create()
                val dataArray = gson.fromJson(body, Array<Models.Data>::class.java)
                val mainData = Models.MainData(dataArray.toList())

                mainHandler.post {
                    rv.adapter = MYRVAdapter(mainData)
                }
            }
        })
    }
}