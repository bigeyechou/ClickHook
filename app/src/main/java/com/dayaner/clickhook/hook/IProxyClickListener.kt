package com.dayaner.clickhook.hook

import android.view.View

/**
 * -------------------------------------
 * 作者：dayaner
 * -------------------------------------
 * 时间：9/3/21 8:04 PM
 * -------------------------------------
 * 描述：onCLick事件代理
 * -------------------------------------
 * 备注：
 * -------------------------------------
 */
interface IProxyClickListener {

    fun onProxyClick(wrap: WrapClickListener, v: View): Boolean

    class WrapClickListener(
        private val mBaseOnClickListener: View.OnClickListener,
        private val mProxyOnClickListener: IProxyClickListener
    ) : View.OnClickListener {

        override fun onClick(v: View?) {
            v?.let { view ->
                //代理点击事件，返回是否要往下传递
                val handled: Boolean = mProxyOnClickListener.onProxyClick(this, view)
                if (handled) {
                    //本身的onClick事件
                    mBaseOnClickListener.onClick(view)
                }
            }
        }
    }
}