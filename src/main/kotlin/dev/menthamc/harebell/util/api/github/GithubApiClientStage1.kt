package dev.menthamc.harebell.util.api.github

import dev.menthamc.harebell.data.RepoTarget
import kotlinx.serialization.Serializable
import java.net.URI
import java.net.http.HttpResponse
import java.time.Duration

class GithubApiClientStage1(
    private val api: GithubApiClient,
    private val repoTarget: RepoTarget
) {
    fun fetchBranches(): List<BranchInfo> {
        val apiUrl = "https://api.github.com/repos/${repoTarget.owner}/${repoTarget.repo}/branches"
        val request = api.createBaseRequestBuilder()
            .uri(URI.create(apiUrl))
            .timeout(Duration.ofSeconds(30))
            .GET()
            .build()

        val response = api.getClient().send(request, HttpResponse.BodyHandlers.ofString())

        if (response.statusCode() == 200) {
            val responseBody = response.body()
            val branches = api.getJson().decodeFromString<List<BranchInfo>>(responseBody)
            return branches
        } else {
            return emptyList()
        }
    }

    fun getDefaultBranchName(defaultBranchName: String?): String? {
        if (defaultBranchName != null) {
            return defaultBranchName
        }

        val repoApiUrl = "https://api.github.com/repos/${repoTarget.owner}/${repoTarget.repo}"
        val request = api.createBaseRequestBuilder()
            .uri(URI.create(repoApiUrl))
            .timeout(Duration.ofSeconds(30))
            .GET()
            .build()

        val response = api.getClient().send(request, HttpResponse.BodyHandlers.ofString())

        if (response.statusCode() == 200) {
            val responseBody = response.body()
            val repoInfo = api.getJson().decodeFromString<RepoInfo>(responseBody)
            return repoInfo.defaultBranch
        }
        return null
    }

    @Serializable
    data class BranchInfo(
        val name: String,
        val commit: CommitInfo? = null
    )

    @Serializable
    data class CommitInfo(
        val sha: String? = null,
        val url: String? = null
    )

    @Serializable
    data class RepoInfo(
        val default_branch: String
    ) {
        val defaultBranch: String
            get() = default_branch
    }
}