package com.example.ai

import com.example.data.local.DateIdeaEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiDateService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    private fun getApiKey(): String {
        return try {
            val field = Class.forName("com.example.BuildConfig").getField("GEMINI_API_KEY")
            (field.get(null) as? String)?.takeIf { it.isNotBlank() && !it.contains("MY_GEMINI_API_KEY") } ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    suspend fun generateDateIdeas(
        interests: String,
        budget: String,
        isVirtual: Boolean
    ): List<DateIdeaEntity> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isBlank()) {
            return@withContext getCuratedFallbackIdeas(interests, budget, isVirtual)
        }

        try {
            val modeStr = if (isVirtual) "Virtual Call / Remote Video Date" else "Upcoming In-Person Visit"
            val prompt = """
                You are MYLY, an expert romantic companion AI for long-distance couples.
                Generate exactly 3 creative, heartwarming, and non-cliché date ideas for an LDR couple.
                Couple interests: $interests
                Budget: $budget
                Mode: $modeStr
                
                Respond ONLY with a valid JSON array of 3 objects with the following keys:
                [
                  {
                    "title": "Date title with emoji",
                    "description": "Engaging 2-sentence description of what to do together",
                    "category": "${if (isVirtual) "Virtual Date" else "Visit Date"}",
                    "estimatedCost": "$budget",
                    "duration": "1 - 2 hours"
                  }
                ]
            """.trimIndent()

            val requestJson = JSONObject().apply {
                val contents = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val parts = JSONArray().apply {
                            put(JSONObject().put("text", prompt))
                        }
                        put("parts", parts)
                    }
                    put(contentObj)
                }
                put("contents", contents)
            }

            // Using gemini-3.5-flash as mandated for text tasks
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val body = requestJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder().url(url).post(body).build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext getCuratedFallbackIdeas(interests, budget, isVirtual)
            }

            val responseBody = response.body?.string() ?: return@withContext getCuratedFallbackIdeas(interests, budget, isVirtual)
            val jsonRoot = JSONObject(responseBody)
            val candidates = jsonRoot.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val firstCandidate = candidates.getJSONObject(0)
                val content = firstCandidate.getJSONObject("content")
                val parts = content.getJSONArray("parts")
                val text = parts.getJSONObject(0).getString("text")

                // Extract JSON array
                val cleanedText = text.trim()
                    .removePrefix("```json")
                    .removePrefix("```")
                    .removeSuffix("```")
                    .trim()

                val jsonArray = JSONArray(cleanedText)
                val results = mutableListOf<DateIdeaEntity>()
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    results.add(
                        DateIdeaEntity(
                            title = obj.getString("title"),
                            description = obj.getString("description"),
                            category = obj.optString("category", if (isVirtual) "Virtual Date" else "Visit Date"),
                            estimatedCost = obj.optString("estimatedCost", budget),
                            duration = obj.optString("duration", "1.5 hours"),
                            isSavedToBucketList = false
                        )
                    )
                }
                if (results.isNotEmpty()) {
                    return@withContext results
                }
            }
            getCuratedFallbackIdeas(interests, budget, isVirtual)
        } catch (e: Exception) {
            getCuratedFallbackIdeas(interests, budget, isVirtual)
        }
    }

    suspend fun generatePersonalizedQuote(
        partnerA: String,
        partnerB: String,
        daysApart: Int,
        mood: String
    ): String = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isBlank()) {
            return@withContext getCuratedFallbackQuote(daysApart, mood)
        }

        try {
            val prompt = "Write a one-sentence $mood quote for long distance couple $partnerA and $partnerB who have been apart for $daysApart days. Warm, playful, and genuine. Maximum 20 words."
            val requestJson = JSONObject().apply {
                val contents = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val parts = JSONArray().apply {
                            put(JSONObject().put("text", prompt))
                        }
                        put("parts", parts)
                    }
                    put(contentObj)
                }
                put("contents", contents)
            }

            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val body = requestJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder().url(url).post(body).build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                if (responseBody != null) {
                    val jsonRoot = JSONObject(responseBody)
                    val candidates = jsonRoot.optJSONArray("candidates")
                    if (candidates != null && candidates.length() > 0) {
                        val text = candidates.getJSONObject(0)
                            .getJSONObject("content")
                            .getJSONArray("parts")
                            .getJSONObject(0)
                            .getString("text")
                            .trim()
                            .replace("\"", "")
                        if (text.isNotBlank()) return@withContext text
                    }
                }
            }
            getCuratedFallbackQuote(daysApart, mood)
        } catch (e: Exception) {
            getCuratedFallbackQuote(daysApart, mood)
        }
    }

    fun getCuratedFallbackIdeas(
        interests: String,
        budget: String,
        isVirtual: Boolean
    ): List<DateIdeaEntity> {
        val virtualPool = listOf(
            DateIdeaEntity(
                title = "🍝 Synced Pasta & Candlelight Call",
                description = "Both order or cook the same simple creamy fettuccine recipe, set a warm candlelight by your phones, and dine together over FaceTime.",
                category = "Virtual Cooking",
                estimatedCost = "$",
                duration = "1.5 hours"
            ),
            DateIdeaEntity(
                title = "🌙 Same Sky Moon Gaze & Voice Memo",
                description = "Step outside at the exact same moment, gaze at the same moon glowing over both your cities, and record a 2-minute live audio note describing what you see.",
                category = "Mindful Connection",
                estimatedCost = "Free",
                duration = "30 mins"
            ),
            DateIdeaEntity(
                title = "🎨 Mystery Drawing & Soundtrack Stream",
                description = "Put on a synced ambient lofi playlist on Spotify, hop on video, and draw funny caricatures of each other's day without showing until the countdown ends!",
                category = "Creative Fun",
                estimatedCost = "Free",
                duration = "1 hour"
            ),
            DateIdeaEntity(
                title = "🍿 Teleparty Film Night & Popcorn Swap",
                description = "Sync a Studio Ghibli or cozy mystery film using Teleparty or watch-together mode, with matching cinema snacks mailed in advance.",
                category = "Virtual Cinema",
                estimatedCost = "Free",
                duration = "2 hours"
            ),
            DateIdeaEntity(
                title = "🏛️ Loungewear Louvre Virtual Tour",
                description = "Tour the 360-degree online exhibits of the Musée du Louvre or British Museum together, picking your favorite historic portraits.",
                category = "Culture & Travel",
                estimatedCost = "Free",
                duration = "1 hour"
            )
        )

        val visitPool = listOf(
            DateIdeaEntity(
                title = "🌅 Dawn Sunrise Picnic at the Observatory",
                description = "Pack warm croissants and fresh strawberries in a basket, wake up early together, and watch the golden morning light bathe the city skyline.",
                category = "In-Person Visit",
                estimatedCost = "$$",
                duration = "2.5 hours"
            ),
            DateIdeaEntity(
                title = "🚲 Hidden Alley Bicycle & Gelato Hunt",
                description = "Rent tandem or city cruiser bicycles, explore historic cobblestone alleys, and sample artisanal pistachio and dark chocolate gelato.",
                category = "City Adventure",
                estimatedCost = "$$",
                duration = "3 hours"
            ),
            DateIdeaEntity(
                title = "📸 Disposable Film Camera Walk",
                description = "Buy one vintage 27-exposure disposable camera each, spend the afternoon taking candid snapshots of each other, and develop them together.",
                category = "Memories",
                estimatedCost = "$",
                duration = "2 hours"
            )
        )

        val pool = if (isVirtual) virtualPool else visitPool
        return pool.shuffled().take(3)
    }

    private fun getCuratedFallbackQuote(daysApart: Int, mood: String): String {
        return when (mood.lowercase()) {
            "funny" -> "Distance makes the heart grow fonder, but our phone battery definitely begs to differ! 🔋"
            "deep" -> "No amount of physical space can unweave what two souls chose to tie together under the same sky. 🌌"
            "cheesy" -> "Are you a magician? Because even 9,000 kilometers away, you still make my heart skip every single time! 🧀"
            else -> "Day $daysApart apart, and every single second is proof that love doesn't measure distance in miles. 💕"
        }
    }
}
