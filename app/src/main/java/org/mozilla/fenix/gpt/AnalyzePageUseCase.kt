/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.gpt

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mozilla.components.concept.fetch.Client
import mozilla.components.concept.fetch.MutableHeaders
import mozilla.components.concept.fetch.Request
import org.json.JSONObject
import org.mozilla.fenix.BuildConfig

class AnalyzePageUseCase(
    private val client: Client,
) {

    private val completionsUrl = "https://api.openai.com/v1/completions"
    private val apiKey = BuildConfig.CHAT_GPT_API_KEY

    suspend operator fun invoke(pageUrl: String): String = withContext(Dispatchers.IO) {
        Result.runCatching {
            val request = Request(
                url = completionsUrl,
                method = Request.Method.POST,
                headers = MutableHeaders(
                    "Content-Type" to "application/json",
                    "Authorization" to "Bearer $apiKey",
                ),
                body = createBody(pageUrl)
            )
            val response = client.fetch(request)
            response.body.useBufferedReader {
                val json = it.readText()
                Log.e("AnalyzePageUseCase", "$json")
                JSONObject(json).parseAsGtpAnswer()
            }
        }.getOrElse { throwable ->
            Log.e("AnalyzePageUseCase", "${throwable.message}")
            throwable.message.orEmpty()
        }
    }

    private fun createBody(pageUrl: String) =
        Request.Body.fromString(
            "{" +
                    "\"model\":\"text-davinci-003\"," +
                    "\"max_tokens\":10," +
                    "\"temperature\":0," +
                    "\"prompt\":\"make a summary of this page $pageUrl\"" +
                    "}",
        )

    private fun JSONObject.parseAsGtpAnswer(): String =
        with(getJSONArray("choices")) {
            getJSONObject(0).getString("text")
        }

}
