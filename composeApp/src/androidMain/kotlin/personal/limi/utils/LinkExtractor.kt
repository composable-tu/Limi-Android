package personal.limi.utils

import io.ktor.http.Url

private const val URL_PATTERN =
    """(https?://)?(www\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)"""

private val DefaultUrlRegex = Regex(URL_PATTERN)
private val regex: Regex = DefaultUrlRegex

/**
 * 提取文本中的链接
 *
 * @param text 待处理的文本
 * @param validateUrls 是否验证链接
 * @param truncateNonAscii 是否截断非 ASCII 字符
 */
fun extractUrlList(
    text: String, validateUrls: Boolean = true, truncateNonAscii: Boolean = false
): List<String> {
    // 预先将非 ASCII 字符替换为空格
    var preProcessedText = if (truncateNonAscii) text.map { char ->
        if (char.isAscii()) char else ' '
    }.joinToString("") else text

    // 将常见的非URL字符替换为空格
    preProcessedText = preProcessedText.map { char ->
        if (char in listOf('<', '>', '(', ')', '[', ']', '{', '}')) ' ' else char
    }.joinToString("")

    var extractedLinks = regex.findAll(preProcessedText).map { it.value.trim() } // 提取匹配到的值并去除可能的空白
        .filter { it.isNotEmpty() }.toList()

    extractedLinks = extractedLinks.map(::removeTrailingDotFromHost)

    if (validateUrls) extractedLinks = extractedLinks.filter(::isValidUrl)
    if (!truncateNonAscii) return extractedLinks

    // 如果开启了非 ASCII 截断
    return extractedLinks.distinct() // 确保截断后相同的链接只出现一次
}


/**
 * 验证URL是否能被正确解析
 */
private fun isValidUrl(url: String): Boolean {
    return try {
        val formattedUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) {
            "http://$url"
        } else url
        Url(formattedUrl)
        true
    } catch (_: Exception) {
        false
    }
}

/**
 * 移除链接主机名末尾的点
 */
private fun removeTrailingDotFromHost(url: String): String {
    return try {
        // 如果URL没有协议，临时添加一个以便正确解析
        val formattedUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) {
            "http://$url"
        } else url

        val parsedUrl = Url(formattedUrl)
        val host = parsedUrl.host

        // 如果主机名以点结尾，则移除它
        if (host.endsWith(".")) {
            val cleanedHost = host.dropLast(1)
            // 重新构建URL，替换清理后的主机名
            url.replaceFirst(host, cleanedHost)
        } else url
    } catch (_: Exception) {
        // 如果解析失败，返回原始URL
        url
    }
}
