package dev.menthamc.harebell

enum class Language(val code: String) {
    ZH_CN("zh-CN"),
    EN("en");

    companion object {
        fun fromCode(code: String?): Language? {
            if (code.isNullOrBlank()) return null
            val normalized = code.trim().lowercase().replace('_', '-')
            return when (normalized) {
                "zh", "zh-cn", "zh-hans", "cn", "chinese", "中文" -> ZH_CN
                "en", "en-us", "english" -> EN
                else -> null
            }
        }
    }
}

fun tr(language: Language, zh: String, en: String): String =
    if (language == Language.ZH_CN) zh else en
