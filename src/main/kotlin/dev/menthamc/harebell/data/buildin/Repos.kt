package dev.menthamc.harebell.data.buildin

import dev.menthamc.harebell.data.RepoTarget

enum class Repos(val repoTarget: RepoTarget) {
    Mint(RepoTarget("MenthaMC", "Mint")),
    Luminol(RepoTarget("LuminolMC", "Luminol")),
    LightingLuminol(RepoTarget("LuminolMC", "LightingLuminol")),
    Lophine(RepoTarget("LuminolMC", "Lophine")),
    Leaves(RepoTarget("LeavesMC", "Leaves")),
    Leaf(RepoTarget("Winds-Studio", "Leaf")),
}