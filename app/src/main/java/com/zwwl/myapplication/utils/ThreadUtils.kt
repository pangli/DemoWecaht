package com.zwwl.myapplication.utils

import java.lang.Exception

/**
 *
 * @Description: 类作用描述
 * @Author: ltt
 * @CreateDate: 2023/4/11 15:19
 */
object ThreadUtils
fun sleep(time: Long) {
    try {
        Thread.sleep(time)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}