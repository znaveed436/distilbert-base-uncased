data class AppInfo(
    val packageName: String,
    val label: String,
    val hooks: List<HookSuggestion>
)

data class HookSuggestion(
    val name: String,
    val description: String,
    val type: String
)