package com.empty.homedownloader.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.empty.homedownloader.BuildConfig
import com.empty.homedownloader.adapters.MYRVAdapter
import com.empty.homedownloader.myclasses.Models
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class FetchMY( private val context: Context, private val rv: RecyclerView) {
    private val mainHandler = Handler(Looper.getMainLooper())

    fun fetchRequest(api : String){
        val request = Request.Builder().url(api).build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    Log.d("RESPONSE", body.toString())

                    val gson = GsonBuilder().create()
                    try {
                        val dataArray = gson.fromJson(body, Array<Models.Data>::class.java)
                        val mainData = Models.MainData(dataArray.toList())

                        mainHandler.post {
                            rv.adapter = MYRVAdapter(mainData)
                        }
                    } catch (e: JsonSyntaxException) {
                        e.printStackTrace()
                        mainHandler.post {
                            Toast.makeText(context, "Failed to parse data. Please try again later.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    mainHandler.post {
                        Toast.makeText(context, "API rate limit exceeded. Please try again later.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}