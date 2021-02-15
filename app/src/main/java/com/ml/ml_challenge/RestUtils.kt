package com.ml.ml_challenge

import okhttp3.OkHttpClient
import okhttp3.Request

class RestUtils {

    companion object {
        var client: OkHttpClient = OkHttpClient()

        fun get(url: String): String? {
            try {
                val request: Request = Request.Builder()
                    .url(url)
                    .build()

                client.newCall(request).execute().use { response -> return response.body?.string() }

            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }
    }

}