package dev.menthamc.harebell.util

object ManifestApplier {
    fun getManifest(property: String): String? {
        try {
            val manifest = this::class.java.classLoader.getResources("META-INF/MANIFEST.MF")
            while (manifest.hasMoreElements()) {
                val url = manifest.nextElement()
                val inputStream = url.openStream()
                val properties = java.util.Properties()
                properties.load(inputStream)
                val value = properties.getProperty(property)
                if (value != null) {
                    return value
                }
            }
        } catch (_: Exception) {
        }
        return null
    }
}