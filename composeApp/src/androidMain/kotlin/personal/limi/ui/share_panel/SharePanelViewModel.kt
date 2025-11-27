package personal.limi.ui.share_panel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import personal.limi.logic.processUrl
import personal.limi.logic.rules.bilibili.UnsupportedURLException
import personal.limi.utils.extractUrlList

class SharePanelViewModel : ViewModel() {
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
        isError = false

        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            viewModelScope.launch(Dispatchers.Main) {
                isProcessing = false
                isError = true
                processedText = "处理出错: ${exception.message ?: "未知错误"}"
            }
        }

        viewModelScope.launch(exceptionHandler) {
            try {
                val urls = extractUrlList(text)
                if (urls.isEmpty()) processedText = "未找到链接" else {
                    val semaphore = Semaphore(5)

                    val tasks = urls.map { url ->
                        async(Dispatchers.IO) {
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
            } catch (e: CancellationException) {
                // 协程被取消时（例如用户退出屏幕），通常不需要特殊处理，但可以记录。
                // 捕获 CancellationException 是为了避免它被下面的 Exception 捕获，并在 isError 状态中设置不正确的值。
                throw e
            } catch (e: Exception) {
                processedText = "处理出错: ${e.message}"
                isError = true
            } finally {
                isProcessing = false
            }
        }
    }
}