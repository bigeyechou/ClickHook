package com.dayaner.clickhook

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.dayaner.clickhook.hook.HookOnClickHelper
import com.dayaner.clickhook.hook.IProxyClickListener
import com.dayaner.clickhook.invocation.StudentInterface
import com.dayaner.clickhook.invocation.StudentProxy
import com.dayaner.clickhook.invocation.UniversityStudent
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Proxy


class MainActivity : AppCompatActivity(), IProxyClickListener {

    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        HookOnClickHelper().registerClickHook(this.window.decorView.rootView, this)
        initView()
    }

    private fun initView() {
        findViewById<AppCompatButton>(R.id.btn_click1).setOnClickListener {
            Log.i(TAG, "initView: 正常点击1")
            invocationText()
        }

        findViewById<AppCompatButton>(R.id.btn_click2).setOnClickListener {
            Log.i(TAG, "initView: 正常点击2")
        }
    }

    /**
     * invocation动态代理 测试
     */
    private fun invocationText() {
        val handler = InvocationHandler { proxy, method, args ->
            if (method.name == "name") {
                Log.i(TAG, "invoke: " + args[0])
            }
            null
        }
        val student: StudentInterface = Proxy.newProxyInstance(
            StudentInterface::class.java.classLoader,
            arrayOf(StudentInterface::class.java),
            handler
        ) as StudentInterface
        student.name("徐泽")

        val university = UniversityStudent()
        val studentProxy = StudentProxy(university)
        val universityStudent: StudentInterface =
            studentProxy.getProxyInstance() as StudentInterface
        universityStudent.name("徐露露")
    }

    override fun onProxyClick(wrap: IProxyClickListener.WrapClickListener, v: View): Boolean {
        Log.i(TAG, "onProxyClick: 点击拦截")
        return false
    }
}