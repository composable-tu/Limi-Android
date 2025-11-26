package personal.limi.ui.share_panel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import personal.limi.utils.extractUrlList

class SharePanelViewModel: ViewModel() {
    var isProcessing by mutableStateOf(false)
        private set

    var processedText by mutableStateOf<String?>(null)
        private set

    var isInitialized by mutableStateOf(false)
        private set

    var isEmpty by mutableStateOf(false)
        private set

    var isError by mutableStateOf(false)
        private set

    fun initializeWithText(text: String) {
        processText(text)
    }

    private fun processText(text: String) {
        if (text.isBlank()) {
            processedText = ""
            isEmpty = true
            return
        }

        isProcessing = true
        viewModelScope.launch {
            try {
                val urls = extractUrlList(text)
                processedText = if (urls.isEmpty()) {
                    "未找到链接"
                } else {
                    urls.joinToString("\n")
                }
            } catch (e: Exception) {
                processedText = "处理出错: ${e.message}"
            } finally {
                isProcessing = false
            }
        }
    }
}