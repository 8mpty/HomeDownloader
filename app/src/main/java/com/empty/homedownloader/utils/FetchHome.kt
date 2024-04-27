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

    suspend fun fetchHomeRequest(api: String): String {
        return withContext(Dispatchers.IO) {
            val client = OkHttpClient()
            val request = Request.Builder().url(api).build()

            val response = client.newCall(request).execute()

            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            val body = response.body?.string()
            Log.d("RESPONSE", body.toString())

            val gson = GsonBuilder().create()
            val dataArray = gson.fromJson(body, Array<Models.Data>::class.java)

            if (dataArray.isNotEmpty()) {
                dataArray[0].name
            } else {
                Log.e("FETCH_HOME", "Empty or invalid response")
                ""
            }
        }
    }
}