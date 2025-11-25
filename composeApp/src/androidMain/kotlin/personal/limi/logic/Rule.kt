package personal.limi.logic

import io.ktor.http.Url
import personal.limi.logic.rules.bilibiliTargetHost
import personal.limi.logic.rules.processBilibiliUrl

suspend fun processUrl(urlString: String): String {
    val url = Url(urlString)
    var finalUrl = ""
    when {
        url.host.lowercase() in bilibiliTargetHost -> finalUrl = processBilibiliUrl(url)
    }
    return finalUrl.ifEmpty { urlString }
}
