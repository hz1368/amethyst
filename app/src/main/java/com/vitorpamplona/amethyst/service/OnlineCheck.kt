/**
 * Copyright (c) 2023 Vitor Pamplona
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the
 * Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN
 * AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.vitorpamplona.amethyst.service

import android.util.Log
import android.util.LruCache
import androidx.compose.runtime.Immutable
import com.vitorpamplona.amethyst.BuildConfig
import okhttp3.Request

@Immutable data class OnlineCheckResult(val timeInMs: Long, val online: Boolean)

object OnlineChecker {
    val checkOnlineCache = LruCache<String, OnlineCheckResult>(100)
    val fiveMinutes = 1000 * 60 * 5

    fun isOnlineCached(url: String?): Boolean {
        if (url.isNullOrBlank()) return false
        if ((checkOnlineCache.get(url)?.timeInMs ?: 0) > System.currentTimeMillis() - fiveMinutes) {
            return checkOnlineCache.get(url).online
        }
        return false
    }

    fun isOnline(url: String?): Boolean {
        checkNotInMainThread()

        if (url.isNullOrBlank()) return false
        if ((checkOnlineCache.get(url)?.timeInMs ?: 0) > System.currentTimeMillis() - fiveMinutes) {
            return checkOnlineCache.get(url).online
        }

        Log.d("OnlineChecker", "isOnline $url")

        return try {
            val request =
                Request.Builder()
                    .header("User-Agent", "Amethyst/${BuildConfig.VERSION_NAME}")
                    .url(url)
                    .get()
                    .build()

            val result =
                HttpClient.getHttpClient().newCall(request).execute().use {
                    checkNotInMainThread()
                    it.isSuccessful
                }
            checkOnlineCache.put(url, OnlineCheckResult(System.currentTimeMillis(), result))
            result
        } catch (e: Exception) {
            checkOnlineCache.put(url, OnlineCheckResult(System.currentTimeMillis(), false))
            Log.e("LiveActivities", "Failed to check streaming url $url", e)
            false
        }
    }
}
