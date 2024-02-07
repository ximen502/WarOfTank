package game.lib

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * @Class ExtSring
 * @Description 增加一个正则表达式获取方法
 * @Author xsc
 * @Date 2024/2/7 下午12:25
 * @Version 1.0
 */
fun String.findStr(input: String?, regex: String?): String {
    val pattern: Pattern = Pattern.compile(regex)
    val matcher: Matcher = pattern.matcher(input)
    while (matcher.find()) {
        return matcher.group()
    }
    return ""
}