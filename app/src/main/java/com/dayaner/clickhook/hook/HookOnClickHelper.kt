package com.dayaner.clickhook.hook

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.recyclerview.widget.RecyclerView
import java.lang.reflect.Field
import java.lang.reflect.Method


/**
 * -------------------------------------
 * 作者：dayaner
 * -------------------------------------
 * 时间：9/3/21 8:07 PM
 * -------------------------------------
 * 描述：点击事件的Hook
 * -------------------------------------
 * 备注：
 * -------------------------------------
 */
class HookOnClickHelper {

    var sHookMethod: Method? = null
    var sHookField: Field? = null
    val mPrivateTagKey = 0
    var mProxyListener: IProxyClickListener? = null

    fun registerClickHook(context: Activity, proxyListener: IProxyClickListener) {
        val rootView = context.window.decorView.rootView
        mProxyListener = proxyListener
        init()
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            hookViews(rootView, 0)
        }
    }

    /**
     * 获取待处理的Method和Field
     */
    @SuppressLint("PrivateApi")
    fun init() {
        if (sHookMethod == null) {
            try {
                val viewClass = Class.forName("android.view.View")
                sHookMethod = viewClass.getDeclaredMethod("getListenerInfo")
                sHookMethod?.isAccessible = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (sHookField == null) {
            try {
                val listenerInfoClass = Class.forName("android.view.View\$ListenerInfo")
                sHookField = listenerInfoClass.getDeclaredField("mOnClickListener")
                sHookField?.isAccessible = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private fun hookViews(view: View, recycledContainerDeep: Int) {
        var recycledContainerDeep = recycledContainerDeep
        if (view.visibility == View.VISIBLE) {
            val forceHook = recycledContainerDeep == 1
            if (view is ViewGroup) {
                val existAncestorRecycle = recycledContainerDeep > 0
                val viewGroup: ViewGroup = view
                if (!(viewGroup is AbsListView || viewGroup is RecyclerView) || existAncestorRecycle) {
                    hookClickListener(view, recycledContainerDeep, forceHook)
                    if (existAncestorRecycle) {
                        recycledContainerDeep++
                    }
                } else {
                    recycledContainerDeep = 1
                }
                val childCount = viewGroup.childCount
                repeat((0..childCount).count() - 1) {
                    val childView = viewGroup.getChildAt(it)
                    hookViews(childView, recycledContainerDeep)
                }
            } else {
                hookClickListener(view, recycledContainerDeep, forceHook)
            }
        }

    }

    private fun hookClickListener(view: View, recycledContainerDeep: Int, forceHook: Boolean) {
        var needHook = forceHook
        if (!needHook) {
            needHook = view.isClickable
            if (needHook && recycledContainerDeep == 0) {
                needHook = view.getTag(mPrivateTagKey) == null
            }
        }
        if (needHook) {
            try {
                sHookMethod?.let { method ->
                    var getListenerInfo: Any = method.invoke(view)
                    sHookField?.let { field ->
                        val baseClickListener = field.get(getListenerInfo) as View.OnClickListener
                        //获取已设置过的监听器
                        mProxyListener?.let { proxyListener ->
                            if (baseClickListener !is IProxyClickListener.WrapClickListener) {
                                field[getListenerInfo] =
                                    IProxyClickListener.WrapClickListener(
                                        baseClickListener,
                                        proxyListener
                                    )
                                view.setTag(mPrivateTagKey, recycledContainerDeep)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}