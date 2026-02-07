package dev.menthamc.harebell.data

import dev.menthamc.harebell.Language
import dev.menthamc.harebell.tr
import dev.menthamc.harebell.util.api.github.GithubApiClientStage1
import kotlin.math.ceil

class BranchInit(
    private val language: Language,
    private val api: GithubApiClientStage1
) {
    private var allBranches: List<GithubApiClientStage1.BranchInfo>? = null
    private var defaultBranchName: String? = null

    private fun msg(zh: String, en: String) = tr(language, zh, en)

    fun init(): String {
        println(msg("正在获取分支列表...", "Fetching branch list..."))
        val branches = getAllBranches()
        if (branches.isEmpty()) {
            println(msg("无法获取分支列表或仓库没有分支", "Unable to fetch branch list or repository has no branches"))
            return "unknown"
        }

        var defaultBranch = api.getDefaultBranchName(defaultBranchName)
        if (defaultBranch == null) {
            defaultBranch = findFallbackDefaultBranch(branches)
        }

        var currentPage = 0
        val pageSize = 9

        while (true) {
            val totalPages = ceil(branches.size.toDouble() / pageSize).toInt()

            displayPage(branches, currentPage, pageSize, defaultBranch, totalPages)

            print(
                msg(
                    "请选择分支编号，输入 'n' 下一页，'p' 上一页，直接回车选择默认分支 [$defaultBranch]: ",
                    "Select branch number, 'n' for next page, 'p' for previous page, press Enter for default [$defaultBranch]: "
                )
            )

            val input = readlnOrNull()?.trim()

            when {
                input.isNullOrEmpty() -> return defaultBranch
                input.equals("n", ignoreCase = true) -> {
                    if (currentPage < totalPages - 1) currentPage++ else {
                        println(msg("已经是最后一页", "Already on the last page"))
                    }
                }

                input.equals("p", ignoreCase = true) -> {
                    if (currentPage > 0) currentPage-- else {
                        println(msg("已经是第一页", "Already on the first page"))
                    }
                }

                input.toIntOrNull() != null -> {
                    val selectedNum = input.toInt()

                    // 获取当前页面显示的分支列表（与显示逻辑保持一致）
                    val displayedBranches =
                        getCurrentPageDisplayBranches(branches, currentPage, pageSize, defaultBranch)

                    val branchIndex = selectedNum - 1
                    if (branchIndex in displayedBranches.indices) {
                        return displayedBranches[branchIndex]
                    } else {
                        println(msg("无效的分支编号", "Invalid branch number"))
                    }
                }

                else -> println(msg("无效输入", "Invalid input"))
            }
        }
    }

    private fun getAllBranches(): List<GithubApiClientStage1.BranchInfo> {
        if (allBranches != null) {
            return allBranches!!
        }

        val branches = api.fetchBranches()
        allBranches = branches
        return branches
    }

    private fun findFallbackDefaultBranch(branches: List<GithubApiClientStage1.BranchInfo>): String {
        return branches.find { it.name.equals("main", ignoreCase = true) }?.name
            ?: branches.find { it.name.equals("master", ignoreCase = true) }?.name
            ?: branches.firstOrNull()?.name
            ?: "unknown"
    }

    private fun displayPage(
        branches: List<GithubApiClientStage1.BranchInfo>,
        currentPage: Int,
        pageSize: Int,
        defaultBranch: String,
        totalPages: Int
    ) {
        println(
            msg(
                "\n=== 分支列表 - 第 ${currentPage + 1} 页 (共 $totalPages 页) ===",
                "\n=== Branch List - Page ${currentPage + 1} of $totalPages ==="
            )
        )

        val displayedBranches = getCurrentPageDisplayBranches(branches, currentPage, pageSize, defaultBranch)

        displayedBranches.forEachIndexed { index, branch ->
            val isDefaultBranch = currentPage == 0 &&
                    branch != "latest" &&
                    branches.indexOfFirst { it.name == branch } == branches.indexOfFirst { it.name == defaultBranch }
            val prefix = if (isDefaultBranch) " (默认分支)" else ""
            println("${index + 1}. ${branch}${prefix}")
        }
    }

    private fun getCurrentPageDisplayBranches(
        branches: List<GithubApiClientStage1.BranchInfo>,
        currentPage: Int,
        pageSize: Int,
        defaultBranch: String
    ): List<String> {
        val defaultBranchInfo = branches.find { it.name == defaultBranch }
        return if (currentPage == 0) {
            val otherBranches = branches.filter { it.name != defaultBranch }.map { it.name }

            if (defaultBranchInfo != null) {
                val result = mutableListOf("latest", defaultBranchInfo.name)
                result.addAll(otherBranches.take(pageSize - 2))
                result
            } else {
                val result = mutableListOf("latest")
                result.addAll(otherBranches.take(pageSize - 1))
                result
            }
        } else {
            val otherBranches = branches.filter { it.name != defaultBranch }.map { it.name }
            val startIndex =
                (currentPage - 1) * pageSize + if (currentPage > 1) pageSize - (if (defaultBranchInfo != null) 2 else 1) else 0
            val endIndex = (startIndex + pageSize).coerceAtMost(otherBranches.size)

            if (startIndex < otherBranches.size) {
                otherBranches.subList(startIndex, endIndex)
            } else {
                emptyList()
            }
        }
    }
}