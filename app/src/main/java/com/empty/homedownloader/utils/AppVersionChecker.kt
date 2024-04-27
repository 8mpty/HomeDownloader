package com.empty.homedownloader.utils

import android.content.Context
import android.widget.Toast

class AppVersionChecker(private val context : Context) {

    fun run(appVer: String, targetVer: String){
        val comparisonResult = isVersionGreaterThan(appVer, targetVer)
        if (comparisonResult > 0) {
            Toast.makeText(context, "WHAT THE. HOWWWW??", Toast.LENGTH_LONG).show()
        } else if (comparisonResult < 0) {
            Toast.makeText(context, """
                Current App Version: ${appVer}
                Latest App Version: ${targetVer}
                Please Update The App!
            """.trimIndent(), Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "App is up to date", Toast.LENGTH_SHORT).show()
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
}