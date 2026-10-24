package net.perfect.tea.pokelauncher

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.perfect.tea.pokelauncher.ui.theme.PokeLauncherTheme
import net.perfect.tea.pokelauncher.GameFile.installApk
import java.io.File

class MainActivity : ComponentActivity() {

    private val pickFile = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            val file = uriToFile(uri)
            if (file != null) {
                _selectedApk.value = file
            }
        }
    }

    val _selectedApk = mutableStateOf<File?>(null)
    val selectedApk get() = _selectedApk.value

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PokeLauncherTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LauncherScreen(
                        modifier = Modifier.padding(innerPadding),
                        onAddFile = {
                            pickFile.launch(arrayOf("application/vnd.android.package-archive"))
                        },
                        onInstall = { apk ->
                            val packageName = packageManager
                                .getPackageArchiveInfo(apk.absolutePath, 0)
                                ?.packageName
                            if (packageName != null) {
                                installApk(this, apk, packageName)
                            }
                        }
                    )
                }
            }
        }
    }

    private fun uriToFile(uri: Uri): File? {
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                val name = cursor.getString(nameIndex)
                val dir = File(filesDir, "apks")
                dir.mkdirs()
                val file = File(dir, name)
                contentResolver.openInputStream(uri)?.use { input ->
                    file.outputStream().use { output -> input.copyTo(output) }
                }
                return file
            }
        }
        return null
    }
}

@Composable
fun LauncherScreen(
    modifier: Modifier = Modifier,
    onAddFile: () -> Unit,
    onInstall: (File) -> Unit
) {
    val selectedApk = remember { mutableStateOf<File?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = onAddFile,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add APK")
        }

        selectedApk.value?.let { file ->
            Text(
                text = file.name,
                modifier = Modifier.padding(top = 16.dp),
                style = MaterialTheme.typography.bodyMedium
            )

            Button(
                onClick = { onInstall(file) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("Install & Launch")
            }
        } ?: run {
            Text(
                text = "No file selected",
                modifier = Modifier.padding(top = 16.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}