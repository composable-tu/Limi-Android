package personal.limi.logic

import io.ktor.http.Url
import personal.limi.data.rules.RuleConfig
import personal.limi.logic.rules.bilibili.bilibiliNoParamsTargetHost
import personal.limi.logic.rules.bilibili.bilibiliRedirectTargetHost
import personal.limi.logic.rules.bilibili.processBilibiliNoParamsUrl
import personal.limi.logic.rules.bilibili.processBilibiliRedirectUrl
import personal.limi.logic.rules.common.processCommonParams
import personal.limi.logic.rules.common.processUTMParams
import personal.limi.logic.rules.common.processUTMParamsEnhanced
import personal.limi.utils.datastore.DataStorePreferences

object RuleIds {
    const val BILIBILI = "bilibili_rules"
    const val COMMON_PARAMS = "common_params_rules"
    const val UTM_PARAMS = "utm_params_rules"
    const val UTM_PARAMS_ENHANCED = "utm_params_enhanced_rules"
}

suspend fun processUrl(urlString: String): String {

    val ruleConfig = RuleConfig(
        commonParams = DataStorePreferences.getBoolean(RuleIds.COMMON_PARAMS, true),
        UTMParams = DataStorePreferences.getBoolean(RuleIds.UTM_PARAMS, true),
        UTMParamsEnhanced = DataStorePreferences.getBoolean(RuleIds.UTM_PARAMS_ENHANCED, false),
        bilibili = DataStorePreferences.getBoolean(RuleIds.BILIBILI, true)
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
    if (ruleConfig.commonParams) finalUrl = processCommonParams(finalUrl)
    if (ruleConfig.UTMParams) finalUrl = processUTMParams(finalUrl)
    if (ruleConfig.UTMParamsEnhanced) finalUrl = processUTMParamsEnhanced(finalUrl)

    val finalUrlString = finalUrl.toString()
    return finalUrlString.ifEmpty { urlString }
}
