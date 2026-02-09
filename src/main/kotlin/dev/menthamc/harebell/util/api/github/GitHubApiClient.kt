package dev.menthamc.harebell.util.api.github

import dev.menthamc.harebell.util.ManifestApplier
import kotlinx.serialization.json.Json
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.time.Duration

class GithubApiClient(
    private val authToken: String? = System.getenv("GITHUB_TOKEN") // for debug use
        ?: System.getenv("token")
) {
    private val userAgent = "Harebell/${ManifestApplier.getManifest("Implementation-Version") ?: "unknown"}"
    private val httpClient = HttpClient.newBuilder()
        .followRedirects(HttpClient.Redirect.NORMAL)
        .connectTimeout(Duration.ofSeconds(3))
        .build()

    private val json = Json { ignoreUnknownKeys = true }

    fun createBaseRequestBuilder(download: Boolean = false): HttpRequest.Builder {
        val builder = HttpRequest.newBuilder()
            .header("User-Agent", userAgent)
        if (download) {
            builder.header("Accept", "application/octet-stream")
        } else {
            builder.header("Accept", "application/vnd.github.v3+json")
        }

        authToken?.let { token ->
            builder.header("Authorization", "Bearer $token")
        }

        return builder
    }

    fun getClient(): HttpClient {
        return httpClient
    }

    fun getJson(): Json {
        return json
    }
}