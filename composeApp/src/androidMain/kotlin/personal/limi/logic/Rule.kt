package personal.limi.logic

import io.ktor.http.Url
import personal.limi.logic.rules.bilibili.bilibiliNoParamsTargetHost
import personal.limi.logic.rules.bilibili.bilibiliRedirectTargetHost
import personal.limi.logic.rules.bilibili.processBilibiliNoParamsUrl
import personal.limi.logic.rules.bilibili.processBilibiliRedirectUrl
import personal.limi.logic.rules.common.processCommonAdsParams
import personal.limi.logic.rules.common.processUTMParams

suspend fun processUrl(urlString: String): String {
    var finalUrl = Url(urlString)

    // TODO 第一层：特定网页重定向处理

    // 第二层：特定 Host 匹配处理
    finalUrl = when {
        finalUrl.host.lowercase() in bilibiliRedirectTargetHost -> processBilibiliRedirectUrl(
            finalUrl
        )

        finalUrl.host.lowercase() in bilibiliNoParamsTargetHost -> processBilibiliNoParamsUrl(
            finalUrl
        )

        else -> finalUrl
    }

    // 第三层：通用参数去除处理
    finalUrl = processCommonAdsParams(finalUrl)
    finalUrl = processUTMParams(finalUrl)

    val finalUrlString = finalUrl.toString()
    return finalUrlString.ifEmpty { urlString }
}
