package com.focusguard.app.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.focusguard.app.domain.model.InstalledApp
import com.focusguard.app.utils.TimeUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppLimitsScreen(viewModel: AppsViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedAppForLimit by remember { mutableStateOf<InstalledApp?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "App Limits",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Search Bar
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = { viewModel.onSearchQueryChanged(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            placeholder = { Text("Search installed applications...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(uiState.filteredApps, key = { it.packageName }) { installedApp ->
                    val rule = uiState.rulesMap[installedApp.packageName]
                    val limitMinutes = rule?.dailyLimitMinutes ?: 0

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = installedApp.appName,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = installedApp.packageName,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (limitMinutes > 0) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.padding(end = 8.dp)
                                ) {
                                    Text(
                                        text = if (limitMinutes > 0) TimeUtils.formatMinutes(limitMinutes) else "No Limit",
                                        style = MaterialTheme.typography.labelMedium,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        color = if (limitMinutes > 0) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                IconButton(onClick = { selectedAppForLimit = installedApp }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit Limit")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Set Limit Dialog
    selectedAppForLimit?.let { appItem ->
        val currentRule = uiState.rulesMap[appItem.packageName]
        val initialMins = currentRule?.dailyLimitMinutes ?: 30
        var limitInput by remember { mutableStateOf(if (initialMins > 0) initialMins.toString() else "30") }
        var isDistracting by remember { mutableStateOf(currentRule?.isDistractingApp ?: true) }

        AlertDialog(
            onDismissRequest = { selectedAppForLimit = null },
            title = { Text("Configure Rule for ${appItem.appName}") },
            text = {
                Column {
                    Text("Enter maximum allowed daily usage in minutes:")
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = limitInput,
                        onValueChange = { limitInput = it.filter { char -> char.isDigit() } },
                        label = { Text("Minutes per day (0 = No limit)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = isDistracting,
                            onCheckedChange = { isDistracting = it }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Block during Focus Mode")
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val mins = limitInput.toIntOrNull() ?: 0
                        viewModel.saveAppRule(appItem.packageName, appItem.appName, mins, isDistracting)
                        selectedAppForLimit = null
                    }
                ) {
                    Text("Save Limit")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedAppForLimit = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
