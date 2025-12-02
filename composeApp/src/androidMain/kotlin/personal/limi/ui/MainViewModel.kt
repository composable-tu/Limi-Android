package personal.limi.ui

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import personal.limi.ui.share_panel.SharePanelActivity
import personal.limi.utils.startPlayBarcodeScanner

class MainViewModel : ViewModel() {
    fun startSharePanel(context: Context) {
        val intent = Intent(context, SharePanelActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        context.startActivity(intent)
    }

    fun startScanQRCode(context: Context) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val result = withContext(Dispatchers.IO) { startPlayBarcodeScanner(context) }
                if (result.isNotEmpty()) {
                    val intent = Intent(context, SharePanelActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        putExtra(Intent.EXTRA_TEXT, result.joinToString("\n"))
                    }
                    context.startActivity(intent)
                } else throw Exception("扫码结果为空")
            } catch (_: Exception) {

            }
        }
    }
}