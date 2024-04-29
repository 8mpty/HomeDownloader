package com.empty.homedownloader.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File

class DownloadFile(private val context: Context, private val fileName: String) {
    fun show() {
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Downloaded File")
        builder.setMessage("Filename: $fileName")
        builder.setPositiveButton("Install") { _, _ ->
            installFile(file)
        }
        builder.setNegativeButton("Delete") { _, _ ->
            deleteFile(file)
        }
        builder.setNeutralButton("Back") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun installFile(file: File) {
        if (file.exists()) {
            val apkUri = FileProvider.getUriForFile(
                context,
                "com.empty.homedownloader.fileprovider",
                file
            )

            val installIntent = Intent(Intent.ACTION_VIEW)
            installIntent.setDataAndType(apkUri, "application/vnd.android.package-archive")
            installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            try {
                context.startActivity(installIntent)
            } catch (e: Exception) {
                Log.e("InstallFile", "Error starting installation intent: ${e.message}")
                Toast.makeText(context, "Error starting installation: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.e("InstallFile", "APK file does not exist")
            Toast.makeText(context, "APK file not found", Toast.LENGTH_SHORT).show()
        }
    }


    private fun finishActivity() {
        if (context is Activity) {
            context.finish()
        }
    }



    private fun deleteFile(file: File) {
        file.delete()
    }
}