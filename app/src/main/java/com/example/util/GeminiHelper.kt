package com.example.util

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiHelper {
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    suspend fun askKhataAi(prompt: String, shopName: String, totalPabo: Double, customerCount: Int): String {
        return withContext(Dispatchers.IO) {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
                return@withContext getSmartOfflineAdvice(prompt, totalPabo)
            }

            try {
                val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"

                val systemText = "আপনি '$shopName' দোকানের জন্য একজন অভিজ্ঞ বাংলা ব্যবসায়ী পরামর্শক ও 'আমার খাতা' এআই সহকারী।" +
                        "দোকানে মোট বাকি কাস্টমার: $customerCount জন, মোট পাওনা বাকি: $totalPabo টাকা।" +
                        "সহজ, স্পষ্ট, এবং মার্জিত বাংলায় ২-৪ বাক্যে উত্তর দিন।"

                val jsonBody = JSONObject().apply {
                    val contents = JSONArray().apply {
                        val contentObj = JSONObject().apply {
                            val parts = JSONArray().apply {
                                val partObj = JSONObject().apply {
                                    put("text", "$systemText\n\nপ্রশ্ন: $prompt")
                                }
                                put(partObj)
                            }
                            put("parts", parts)
                        }
                        put(contentObj)
                    }
                    put("contents", contents)
                }

                val mediaType = "application/json; charset=utf-8".toMediaType()
                val body = jsonBody.toString().toRequestBody(mediaType)
                val request = Request.Builder()
                    .url(url)
                    .post(body)
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string() ?: ""

                if (response.isSuccessful && responseBody.isNotBlank()) {
                    val jsonResp = JSONObject(responseBody)
                    val candidates = jsonResp.optJSONArray("candidates")
                    if (candidates != null && candidates.length() > 0) {
                        val firstCand = candidates.getJSONObject(0)
                        val content = firstCand.optJSONObject("content")
                        val parts = content?.optJSONArray("parts")
                        if (parts != null && parts.length() > 0) {
                            val resText = parts.getJSONObject(0).optString("text", "")
                            if (resText.isNotBlank()) return@withContext resText
                        }
                    }
                }
                getSmartOfflineAdvice(prompt, totalPabo)
            } catch (e: Exception) {
                getSmartOfflineAdvice(prompt, totalPabo)
            }
        }
    }

    private fun getSmartOfflineAdvice(prompt: String, totalPabo: Double): String {
        return when {
            prompt.contains("বার্তা") || prompt.contains("মেসেজ") ->
                "💡 তাগাদা বার্তা টেমপ্লেট:\n\"প্রিয় গ্রাহক, আমার খাতা স্টোরে আপনার বকেয়া হিসাব $totalPabo টাকা। অনুগ্রহ করে সুবিধাজনক সময়ে পরিশোধ করার বিনীত অনুরোধ রইলো। ধন্যবাদ!\""
            prompt.contains("বাকি") || prompt.contains("আদায়") ->
                "💡 বাকি আদায়ের পরামর্শ:\n১. প্রতি সপ্তাহে ছোট ছোট তগাদা দিন, মাসের শেষে না জমিয়ে রাখা ভালো।\n২. আমার খাতা অ্যাপ থেকে প্রতি লেনদেনের পরেই SMS বা হিসাবের ডিজিটাল ছবি পাঠান।"
            else ->
                "💡 কাস্টমার সম্পর্ক টিপস:\n১. নিয়মিত সঠিক হিসাব রাখলে কাস্টমারদের সাথে সম্পর্কের আস্থা বাড়ে।\n২. আমার খাতা অ্যাপের মাধ্যমে অনলাইন বা অফলাইনে সবসময় নির্ভুল ডিজিটাল খাতা আপডেট রাখুন।"
        }
    }
}
