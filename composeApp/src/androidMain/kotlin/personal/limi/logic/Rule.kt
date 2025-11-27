package personal.limi.logic

import io.ktor.http.Url
import personal.limi.logic.rules.bilibili.bilibiliNoParamsTargetHost
import personal.limi.logic.rules.bilibili.bilibiliRedirectTargetHost
import personal.limi.logic.rules.bilibili.processBilibiliNoParamsUrl
import personal.limi.logic.rules.bilibili.processBilibiliRedirectUrl

suspend fun processUrl(urlString: String): String {
    val url = Url(urlString)
    var finalUrl: String? = null
    when {
        url.host.lowercase() in bilibiliRedirectTargetHost -> finalUrl = processBilibiliRedirectUrl(url)
        url.host.lowercase() in bilibiliNoParamsTargetHost -> finalUrl = processBilibiliNoParamsUrl(url)
    }
    return finalUrl?.ifEmpty { urlString } ?: urlString
}
