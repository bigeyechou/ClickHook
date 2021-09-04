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
        private val mBaseListener: View.OnClickListener,
        private val mProxyListener: IProxyClickListener
    ) : View.OnClickListener {

        override fun onClick(v: View?) {
            v?.let { view ->
                val handled: Boolean = mProxyListener.onProxyClick(this, view)
                if (handled) {
                    mBaseListener.onClick(view)
                }
            }
        }
    }
}