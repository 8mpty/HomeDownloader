package com.empty.homedownloader.utils

import android.util.Log
import com.empty.homedownloader.myclasses.Models
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class FetchHome {

    suspend fun fetchHomeRequest(api: String): List<String> {
        return withContext(Dispatchers.IO) {
            val client = OkHttpClient()
            val request = Request.Builder().url(api).build()

            val response = client.newCall(request).execute()

            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            val body = response.body?.string()
            Log.d("RESPONSE", body.toString())

            val gson = GsonBuilder().create()
            val dataArray = gson.fromJson(body, Array<Models.Data>::class.java)

            val resultList = mutableListOf<String>()
            if (dataArray.isNotEmpty()) {
                val ver = dataArray[0].name
                val firstData = dataArray[0]
                val assets = firstData.assets
                if (assets.isNotEmpty()) {
                    val fileName = assets[0].name
                    val fileUrl = assets[0].browser_download_url
                    Log.e("8mpty", ver)
                    Log.e("8mpty", fileName)
                    Log.e("8mpty", fileUrl)
                    resultList.add(ver)
                    resultList.add(fileName)
                    resultList.add(fileUrl)
                } else {
                    Log.e("FETCH_HOME", "No assets found for the first data")
                }
            } else {
                Log.e("FETCH_HOME", "Empty or invalid response")
            }
            resultList
        }
    }
}