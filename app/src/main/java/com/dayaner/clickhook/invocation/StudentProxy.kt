package com.dayaner.clickhook.invocation

import android.util.Log
import com.dayaner.clickhook.MainActivity
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy


/**
 * -------------------------------------
 * 作者：dayaner
 * -------------------------------------
 * 时间：9/6/21 12:30 PM
 * -------------------------------------
 * 描述：学生代理类
 * -------------------------------------
 * 备注：
 * -------------------------------------
 */
class StudentProxy(private val objStudent: Any) : InvocationHandler {

    fun getProxyInstance(): Any = Proxy.newProxyInstance(
        objStudent::class.java.classLoader,
        objStudent::class.java.interfaces,
        this
    )

    override fun invoke(proxy: Any?, method: Method?, args: Array<Any?>): Any?{
        if (method?.name == "name") {
            //拦截处理增强业务
            Log.i(MainActivity.TAG, "StudentProxy invoke: " + (args[0] ?: "null"))

        }
        //调用自身
        return method?.invoke(objStudent, *args)
    }
}