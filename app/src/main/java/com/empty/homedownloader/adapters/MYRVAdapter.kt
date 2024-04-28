package com.empty.homedownloader.adapters

import android.content.Context
import android.graphics.Color
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.empty.homedownloader.myclasses.Models
import com.empty.homedownloader.R
import com.empty.homedownloader.utils.DownloadFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class MYRVAdapter(parentFeed: Models.MainData, private val context: Context): RecyclerView.Adapter<MYRVAdapter.CustomVH>() {

    private val prereleaseItems: List<Models.Data> = parentFeed.allData.filter { it.prerelease }

    inner class CustomVH(itemView: View) : RecyclerView.ViewHolder(itemView){
        val itemTitle : TextView = itemView.findViewById(R.id.itemTitle)
        val itemUrl : TextView = itemView.findViewById(R.id.itemUrl)
        val itemFilename : TextView = itemView.findViewById(R.id.itemFilename)
        val itemUploaded : TextView = itemView.findViewById(R.id.itemUploaded)
        val itemRow : LinearLayout = itemView.findViewById(R.id.customLL)
        val downloadProgressBar: ProgressBar = itemView.findViewById(R.id.downloadProgressBar)

        var downloadProgress: Int = 0
    }

    private fun downloadFile(fileUrl: String, fileName: String, holder: CustomVH) {
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
                    val totalBytes = response.body?.contentLength() ?: 0

                    inputStream?.use { input ->
                        outputFile.outputStream().use { output ->
                            var bytesRead: Int
                            val buffer = ByteArray(8192)
                            var totalBytesRead = 0
                            while (input.read(buffer).also { bytesRead = it } != -1) {
                                output.write(buffer, 0, bytesRead)
                                totalBytesRead += bytesRead
                                val progress = (totalBytesRead * 100 / totalBytes).toInt()
                                withContext(Dispatchers.Main) {
                                    holder.downloadProgress = progress
                                    holder.downloadProgressBar.progress = progress
                                }
                            }
                        }
                    }

                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "File downloaded: $fileName", Toast.LENGTH_SHORT).show()
                        holder.downloadProgressBar.progress = 100
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

    override fun getItemCount(): Int {
        return prereleaseItems.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomVH {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemRow = layoutInflater.inflate(R.layout.custom_app_list, parent, false)
        return CustomVH(itemRow)
    }

    override fun onBindViewHolder(holder: CustomVH, position: Int) {
        val data = prereleaseItems[position]
        val assetData = data.assets.firstOrNull()
        if(data.prerelease) {
            if (position == 0) {
                val first = data.name + " <--- [ LATEST (Click This) ]"
                holder.itemTitle.text = first
                holder.itemTitle.setTextColor(Color.parseColor("#03a56a"))
            } else {
                holder.itemTitle.text = data.name
                holder.itemTitle.setTextColor(Color.parseColor("#FFFFFF"))
            }

            val oriupdate = Instant.parse(assetData?.updated_at)
            val upformat = DateTimeFormatter.ofPattern("MMM d, yyyy, @h:mm a")
            val newupdate = upformat.format(oriupdate.atZone(ZoneId.systemDefault()))


            val filename = "Filename: " + assetData?.name
            val uploaded = "Uploaded at: $newupdate"
            holder.itemFilename.text = filename
            holder.itemUploaded.text = uploaded
            holder.itemUrl.text = assetData?.browser_download_url ?: "INVALID APP URL"

            val row = holder.itemRow.context

            holder.itemRow.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    holder.itemRow.setBackgroundColor(Color.parseColor("#808080"))
                } else {
                    holder.itemRow.setBackgroundColor(Color.TRANSPARENT)
                }
            }

            holder.itemRow.setOnClickListener {
                val assetData = data.assets.firstOrNull()
                if (assetData != null) {
                    val fileUrl = assetData.browser_download_url
                    val fileName = assetData.name
                    val downloadFile = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        fileName
                    )
                    if (downloadFile.exists()) {
                        holder.downloadProgressBar.visibility = View.VISIBLE
                        holder.downloadProgressBar.progress = 100
                        DownloadFile(context, fileName).show()
                    } else {
                        downloadFile(fileUrl, fileName, holder)
                        Toast.makeText(
                            row, """
                            Downloading: $fileName
                            Please wait....
                         """.trimIndent(), Toast.LENGTH_SHORT
                        ).show()
                        holder.downloadProgressBar.visibility = View.VISIBLE
                    }
                } else {
                    Toast.makeText(row, "Invalid asset data", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            // If the current item has prerelease = false, hide the view holder
            holder.itemView.visibility = View.GONE
        }
    }
}