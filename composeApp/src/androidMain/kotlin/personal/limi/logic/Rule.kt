package personal.limi.logic

import io.ktor.http.Url
import personal.limi.data.rules.RuleConfig
import personal.limi.logic.rules.bilibili.bilibiliNoParamsTargetHost
import personal.limi.logic.rules.bilibili.bilibiliRedirectTargetHost
import personal.limi.logic.rules.bilibili.processBilibiliNoParamsUrl
import personal.limi.logic.rules.bilibili.processBilibiliRedirectUrl
import personal.limi.logic.rules.common.processCommonAdsParams
import personal.limi.logic.rules.common.processUTMParams
import personal.limi.logic.rules.common.processUTMParamsEnhanced

suspend fun processUrl(urlString: String): String {

    val ruleConfig = RuleConfig( // TODO: 版本迭代后需要删除这行代码
        commonAdsParams = true, UTMParams = true, UTMParamsEnhanced = false, bilibili = true
    )

    var finalUrl = Url(urlString)

    // 第一层：特定 Host 匹配处理
    finalUrl = when {
        ruleConfig.bilibili && finalUrl.host.lowercase() in bilibiliRedirectTargetHost -> processBilibiliRedirectUrl(
            finalUrl
        )

        ruleConfig.bilibili && finalUrl.host.lowercase() in bilibiliNoParamsTargetHost -> processBilibiliNoParamsUrl(
            finalUrl
        )

        else -> finalUrl
    }

    // 第二层：通用参数去除处理
    if (ruleConfig.commonAdsParams) finalUrl = processCommonAdsParams(finalUrl)
    if (ruleConfig.UTMParams) finalUrl = processUTMParams(finalUrl)
    if (ruleConfig.UTMParamsEnhanced) finalUrl = processUTMParamsEnhanced(finalUrl)

    val finalUrlString = finalUrl.toString()
    return finalUrlString.ifEmpty { urlString }
}
