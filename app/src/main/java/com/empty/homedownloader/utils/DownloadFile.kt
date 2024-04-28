package com.empty.homedownloader.utils

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Environment
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
        val uri = FileProvider.getUriForFile(
            context,
            "com.empty.homedownloader.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/vnd.android.package-archive")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(intent)
    }


    private fun deleteFile(file: File) {
        file.delete()
    }
}