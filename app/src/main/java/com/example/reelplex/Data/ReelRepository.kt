package com.example.reelplex.Data

import com.example.reelplex.Models.ReelData

class ReelRepository(private val api: ReelApi) {

    suspend fun fetchReel(url: String): Result<ReelData> {
        return try {

            val response = api.getReel(
                url = url,
                apiKey = "", // Add your api key here
                host = "" // Add your web host here if have one
            )

            if (response.isSuccessful) {

                val body = response.body()

                if (body != null && body.status == "success") {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception("API failed"))
                }

            } else {
                Result.failure(Exception("HTTP ${response.code()}"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}