package com.empty.homedownloader.utils

import android.app.AlertDialog
import android.content.Context
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import com.empty.homedownloader.adapters.MYRVAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

class AppVersionChecker(private val context : Context) {

    fun run(appVer: String, info: List<String>){
        val version = info[0]
        val comparisonResult = isVersionGreaterThan(appVer, version)
        if (comparisonResult > 0) {
            Toast.makeText(context, "WHAT THE. HOWWWW??", Toast.LENGTH_LONG).show()
        } else if (comparisonResult < 0) {
            Toast.makeText(context, """
                Current App Version: $appVer
                Latest App Version: $version
                Please Update The App!
            """.trimIndent(), Toast.LENGTH_LONG).show()
            showUpdateDialog(info)
        } else {
            Toast.makeText(context, """
                App Version: $appVer
                App is up to date
            """.trimIndent(), Toast.LENGTH_SHORT).show()
        }
    }
    private fun isVersionGreaterThan(currentVersion: String, targetVersion: String): Int {
        val currentVersionParts = currentVersion.split(".").map { it.toInt() }
        val targetVersionParts = targetVersion.split(".").map { it.toInt() }

        for (i in 0 until maxOf(currentVersionParts.size, targetVersionParts.size)) {
            val currentPart = if (i < currentVersionParts.size) currentVersionParts[i] else 0
            val targetPart = if (i < targetVersionParts.size) targetVersionParts[i] else 0

            if (currentPart > targetPart) {
                return 1
            } else if (currentPart < targetPart) {
                return -1
            }
        }
        // If all parts are equal, versions are equal
        return 0
    }
    private fun showUpdateDialog(info : List<String>) {
        val fileName = info[1]
        val fileUrl = info[2]
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Update Available")
        builder.setMessage("A new version of the app is available. Do you want to update?")
        builder.setPositiveButton("Update") { _, _ ->
            updateApp(fileName, fileUrl)
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }
    private fun updateApp(fileName : String, fileUrl : String){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url(fileUrl)
                    .build()

                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val inputStream = response.body?.byteStream()
                    val outputFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)

                    inputStream?.use { input ->
                        outputFile.outputStream().use { output ->
                            var bytesRead: Int
                            val buffer = ByteArray(8192)
                            var totalBytesRead = 0
                            while (input.read(buffer).also { bytesRead = it } != -1) {
                                output.write(buffer, 0, bytesRead)
                                totalBytesRead += bytesRead
                            }
                        }
                    }

                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "File downloaded: $fileName", Toast.LENGTH_SHORT).show()
                        DownloadFile(context, fileName).show()
                    }
                } else {
                    Log.e("8mpty", "Error downloading file: ${response.message}")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Error downloading file: ${response.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("8mpty", "Error downloading file: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error downloading file: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}