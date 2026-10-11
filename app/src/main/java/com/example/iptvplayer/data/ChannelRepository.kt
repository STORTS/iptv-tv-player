package com.example.iptvplayer.data

import com.example.iptvplayer.model.Channel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.BufferedReader
import java.util.UUID
import java.util.regex.Pattern

object M3uParser {
    private val LOGO_PATTERN = Pattern.compile("""tvg-logo="([^"]*)"""")
    private val GROUP_PATTERN = Pattern.compile("""group-title="([^"]*)"""")
    private val NAME_PATTERN = Pattern.compile(""",(.*)$""")

    suspend fun parseStream(reader: BufferedReader): List<Channel> = withContext(Dispatchers.Default) {
        val channels = ArrayList<Channel>(10_000)
        var line: String?
        var currentLogo: String? = null
        var currentGroup = "General"
        var currentName = "Unknown Channel"

        while (reader.readLine().also { line = it } != null) {
            val trimmed = line!!.trim()
            if (trimmed.isEmpty()) continue

            if (trimmed.startsWith("#EXTINF:")) {
                // Extract tvg-logo
                val logoMatcher = LOGO_PATTERN.matcher(trimmed)
                currentLogo = if (logoMatcher.find()) logoMatcher.group(1).takeIf { it.isNotBlank() } else null

                // Extract group-title
                val groupMatcher = GROUP_PATTERN.matcher(trimmed)
                currentGroup = if (groupMatcher.find()) {
                    groupMatcher.group(1).trim().ifEmpty { "General" }
                } else "General"

                // Extract Channel Name
                val nameMatcher = NAME_PATTERN.matcher(trimmed)
                currentName = if (nameMatcher.find()) {
                    nameMatcher.group(1).trim().ifEmpty { "Unknown" }
                } else "Unknown"
            } else if (!trimmed.startsWith("#") && (trimmed.startsWith("http://") || trimmed.startsWith("https://"))) {
                channels.add(
                    Channel(
                        id = UUID.randomUUID().toString(),
                        name = currentName,
                        streamUrl = trimmed,
                        logoUrl = currentLogo,
                        group = currentGroup
                    )
                )
                // Reset per-channel state
                currentLogo = null
                currentGroup = "General"
                currentName = "Unknown"
            }
        }
        channels
    }
}

class ChannelRepository(private val client: OkHttpClient = OkHttpClient()) {
    suspend fun fetchPlaylist(url: String): List<Channel> = withContext(Dispatchers.IO) {
        val request = Request.Builder().url(url).build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("Failed to download playlist: HTTP ${response.code}")
            val body = response.body ?: throw Exception("Empty response body")
            body.byteStream().bufferedReader().use { reader ->
                M3uParser.parseStream(reader)
            }
        }
    }
}
