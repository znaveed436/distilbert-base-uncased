object HookGenerator {
    fun generateHook(pkg: String, hookType: String): String {
        return when (hookType) {
            "Remove Ads" -> adBlockTemplate(pkg)
            "Screenshot Bypass" -> screenshotTemplate(pkg)
            else -> basicHook(pkg)
        }
    }

    private fun adBlockTemplate(pkg: String) = """
        loadApp("$pkg") {
            "com.google.android.gms.ads.AdView::class.method { name = "loadAd" }.hook { replace { } }
        }
    """.trimIndent()
    
    private fun screenshotTemplate(pkg: String) = """
        loadApp("$pkg") {
            "android.media.projection.MediaProjectionManager::class.method { name = "checkPermission" }.hook { replace { result = true } }
        }
    """.trimIndent()
    
    private fun basicHook(pkg: String) = """
        loadApp("$pkg") {
            "android.app.Activity::class.method { name = "onCreate" }.hook {
                before { loggerI("Hooked ${it.thisObject.javaClass.simpleName}") }
            }
        }
    """.trimIndent()
}