package com.hook.automation.ui

import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hook.automation.AppInfo
import com.hook.automation.HookSuggestion

@Composable
fun AppScannerScreen(pm: PackageManager) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedApp by remember { mutableStateOf<AppInfo?>(null) }
    
    val apps = remember(searchQuery) {
        (pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .mapNotNull { app ->
                try {
                    val label = pm.getApplicationLabel(app).toString()
                    val pkgName = app.packageName
                    AppInfo(pkgName, label, detectHooksForApp(pkgName))
                } catch (e: Exception) { null }
            }
            .filter { it.label.contains(searchQuery, ignoreCase = true) || it.packageName.contains(searchQuery, ignoreCase = true) }
            .sortedBy { it.label })
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Build,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Hook Automation",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${apps.size} apps available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search apps...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Apps List
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(apps) { app ->
                AppCard(
                    app = app,
                    onClick = { selectedApp = app }
                )
            }
        }
        
        // FAB for refresh
        FloatingActionButton(
            onClick = { /* Refresh apps */ },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.End),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Default.Refresh, contentDescription = "Refresh")
        }
    }
    
    selectedApp?.let { app ->
        HookSelectionDialog(
            app = app,
            onDismiss = { selectedApp = null },
            onHookSelected = { pkg, hook ->
                // Generate hook
                selectedApp = null
            }
        )
    }
}

@Composable
fun AppCard(app: AppInfo, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App Icon Placeholder
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Android,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = app.label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = app.packageName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Badge(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Text("${app.hooks.size}")
            }
        }
    }
}

private fun detectHooksForApp(pkgName: String): List<HookSuggestion> {
    return when {
        pkgName.contains("whatsapp", ignoreCase = true) -> listOf(
            HookSuggestion("Screenshot Bypass", "Disable screenshot blocking", "screenshot"),
            HookSuggestion("Unlimited Forward", "Remove forward limits", "forward"),
            HookSuggestion("Remove Ads", "Block all ads", "ads")
        )
        pkgName.contains("instagram", ignoreCase = true) -> listOf(
            HookSuggestion("Download Media", "Save photos/videos", "download"),
            HookSuggestion("Remove Ads", "Block sponsored content", "ads"),
            HookSuggestion("Story Saver", "Download stories", "stories")
        )
        pkgName.contains("systemui", ignoreCase = true) -> listOf(
            HookSuggestion("Status Bar Mods", "Customize status bar", "statusbar"),
            HookSuggestion("QS Tiles", "Quick settings tweaks", "quicksettings")
        )
        else -> listOf(
            HookSuggestion("Remove Ads", "Block all ads", "ads"),
            HookSuggestion("Bypass Checks", "Disable integrity checks", "bypass"),
            HookSuggestion("UI Mods", "Activity modifications", "ui")
        )
    }
}