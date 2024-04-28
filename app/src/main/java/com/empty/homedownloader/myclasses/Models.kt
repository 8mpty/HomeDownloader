package com.empty.homedownloader.myclasses

class Models{
    class MainData(
        val allData: List<Data>
    )

    class Data(
        val id: Int, // Incase
        val name: String, // Main uploaded name/title
        val prerelease: Boolean, // get pre-release info
        val assets: List<Assets>
    )

    class Assets(
        val id: Int?, // Incase
        val name: String, // For renaming the filename
        val updated_at: String,
        val browser_download_url: String // The actual url to download from
    )
}
