package personal.limi.ui.screen.oss

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.Libs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import limi.shared.generated.resources.Res
import limi.shared.generated.resources.back
import limi.shared.generated.resources.open_source_license
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import personal.limi.ui.OSSLicenseActivity
import personal.limi.ui.components.oss.LicenseItem
import personal.limi.ui.components.oss.ossLazyGroup

@OptIn(
  ExperimentalMaterial3Api::class,
  ExperimentalResourceApi::class,
  ExperimentalMaterial3ExpressiveApi::class,
)
@Composable
fun OSSLicenseMenu(onBack: () -> Unit) {
  var libraries by remember { mutableStateOf<Libs?>(null) }
  LaunchedEffect(Unit) {
    libraries =
      withContext(Dispatchers.Default) {
        val json = Res.readBytes("files/aboutlibraries.json").decodeToString()
        Libs.Builder().withJson(json).build()
      }
  }
  val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
  val listState = rememberLazyListState()
  val layoutDirection = LocalLayoutDirection.current
  val context = LocalContext.current
  Scaffold(
    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    topBar = {
      TopAppBar(
        title = { Text(text = stringResource(Res.string.open_source_license)) },
        scrollBehavior = scrollBehavior,
        colors =
          TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
          ),
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(
              imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
              contentDescription = stringResource(Res.string.back),
            )
          }
        },
      )
    },
  ) { innerPadding ->
    val libs = libraries
    if (libs == null) {
      Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        LoadingIndicator()
      }
    } else {
      LazyColumn(
        state = listState,
        modifier =
          Modifier.fillMaxSize()
            .padding(
              start = innerPadding.calculateStartPadding(layoutDirection),
              end = innerPadding.calculateEndPadding(layoutDirection),
              top = innerPadding.calculateTopPadding(),
            ),
        contentPadding = PaddingValues(vertical = 8.dp),
      ) {
        ossLazyGroup {
          libs.libraries.forEach { library ->
            item { shape ->
              LicenseItem(
                shape = shape,
                library = library,
                onClick = {
                  if (library.licenses.isNotEmpty() || library.website != null) {
                    val intent =
                      Intent(context, OSSLicenseActivity::class.java).apply {
                        putExtra("library", library.uniqueId)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                      }
                    context.startActivity(intent)
                  }
                },
              )
            }
          }
        }
        item {
          Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding()))
        }
      }
    }
  }
}
