package com.zjut.wristband.util

enum class WCode(val num: Int, val text: String) {
    OK(0, "成功"),
    UnDefinedError(1, "未定义错误"),
    NetworkError(2, "网络连接错误"),
    JsonParseError(3, "Json解析错误"),
    ServerError(4, "服务器错误"),
    AccountError(5, "用户名或密码错误")
}