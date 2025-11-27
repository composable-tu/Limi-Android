package personal.limi.logic.rules

import io.ktor.http.URLBuilder
import io.ktor.http.Url
import personal.limi.utils.getRedirectsUrl

class UnsupportedURLException(message: String) : Exception(message)

val bilibiliTargetHost = arrayOf("b23.tv")

suspend fun processBilibiliUrl(url: Url): String {
    if (url.host.lowercase() !in bilibiliTargetHost) throw UnsupportedURLException("与 bilibiliTargetHost 链接不匹配")
    val redirectsUrl = getRedirectsUrl(url)
    if (redirectsUrl.host.lowercase() != "www.bilibili.com" && redirectsUrl.host.lowercase() != "bilibili.com") throw UnsupportedURLException(
        "重定向链接与 bilibili.com 链接不匹配"
    )
    val finalUrl = URLBuilder(redirectsUrl).apply { parameters.clear() }.build()
    return finalUrl.toString()
}