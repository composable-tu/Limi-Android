package personal.limi.ui.share_panel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import personal.limi.logic.processUrl
import personal.limi.utils.extractUrlList

class SharePanelViewModel: ViewModel() {
    var isProcessing by mutableStateOf(false)
        private set

    var processedText by mutableStateOf<String?>(null)
        private set

    var originalText by mutableStateOf<String?>("")
        private set

    var isEmpty by mutableStateOf(false)
        private set

    var isError by mutableStateOf(false)
        private set

    fun initializeWithText(text: String?) {
        originalText = text
        processText(text)
    }

    private fun processText(text: String?) {
        if (text.isNullOrBlank()) {
            processedText = ""
            isEmpty = true
            return
        }

        isProcessing = true
        viewModelScope.launch {
            try {
                val urls = extractUrlList(text)
                if (urls.isEmpty()) processedText = "未找到链接" else {
                    val semaphore = Semaphore(5)

                    val tasks = urls.map { url ->
                        async {
                            semaphore.withPermit {
                                val processedResult = processUrl(url)
                                url to processedResult
                            }
                        }
                    }

                    val processedUrls = awaitAll(*tasks.toTypedArray()).toMap()

                    var resultText = text
                    if (processedUrls.isNotEmpty()) processedUrls.forEach { (originalUrl, processedUrl) ->
                        resultText = resultText?.replace(originalUrl, processedUrl)
                    }
                    processedText = resultText
                }
            } catch (e: Exception) {
                processedText = "处理出错: ${e.message}"
                isError = true
            } finally {
                isProcessing = false
            }
        }
    }
}