package com.dayaner.clickhook

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.dayaner.clickhook.hook.HookOnClickHelper
import com.dayaner.clickhook.hook.IProxyClickListener


class MainActivity : AppCompatActivity(), IProxyClickListener {

    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        HookOnClickHelper().registerClickHook(this, this)
        initView()
    }

    private fun initView() {
        findViewById<AppCompatButton>(R.id.btn_click).setOnClickListener {
            Log.i(TAG, "initView: 正常点击")
        }
    }

    override fun onProxyClick(wrap: IProxyClickListener.WrapClickListener, v: View): Boolean {
        Log.i(TAG, "onProxyClick: 点击拦截")
        return false
    }
}