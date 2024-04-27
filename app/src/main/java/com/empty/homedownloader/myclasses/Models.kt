package com.empty.homedownloader.myclasses

class ParentFeed(
    val allData: List<Data>
)

class Data(
    val id: Int, // Incase
    val name: String, // Main uploaded name/title
    val assets: List<Assets>
)

class Assets(
    val id: Int, // Incase
    val name: String, // For renaming the filename
    val browser_download_url: String // The actual url to download from
)

