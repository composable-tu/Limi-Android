package personal.limi.logic.rules.bilibili

import io.ktor.http.Url
import personal.limi.utils.getRedirectsUrl

class UnsupportedURLException(message: String) : Exception(message)

// 对外暴露的目标处理 Host
val bilibiliRedirectTargetHost = arrayOf("b23.tv")

// 最终允许的 Host
private val bilibiliRedirectAllowedHost = arrayOf("www.bilibili.com", "bilibili.com", "m.bilibili.com")

suspend fun processBilibiliRedirectUrl(url: Url): String {
    if (url.host.lowercase() !in bilibiliRedirectTargetHost) throw UnsupportedURLException("与 bilibiliRedirectTargetHost 链接不匹配")
    val redirectsUrl = getRedirectsUrl(url)
    if (redirectsUrl.host.lowercase() !in bilibiliRedirectAllowedHost) throw UnsupportedURLException(
        "重定向链接 ${redirectsUrl.host} 与 bilibili.com 链接不匹配"
    )
    return processBilibiliNoParamsUrl(redirectsUrl)
}