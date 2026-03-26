package com.example.reelplex.Models


data class ReelResponse(
    val status: String,
    val data: ReelData
)

data class ReelData(
    val url: String,
    val thumbnail: String?,
    val caption: String?
)
