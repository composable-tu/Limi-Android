package personal.limi.ui.share_panel

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import personal.limi.theme.LimiTheme

class SharePanelActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val sharedText: String? = intent.getStringExtra(Intent.EXTRA_TEXT)?.ifBlank { null }

        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(), color = Color.Transparent
            ) {
                LimiTheme {
                    val viewModel: SharePanelViewModel = viewModel()
                    viewModel.initializeWithText(sharedText)
                    SharePanel(viewModel, onActivityClose = { finish() })
                }
            }
        }
    }
}