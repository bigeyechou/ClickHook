package com.dayaner.clickhook.hook

import android.annotation.SuppressLint
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

    /**
     * 需要hook的方法
     */
    var sHookMethod: Method? = null

    /**
     * 需要hook的对象
     */
    var sHookField: Field? = null

    var mProxyListener: IProxyClickListener? = null

    /**
     * 防止重复设置
     */
    var mPrivateTagKey = System.identityHashCode(this)

    /**
     * 注册点击事件的hook
     * Activity : 根据Activity来获取页面的根View
     * IProxyClickListener : 代理点击事件
     */
    fun registerClickHook(rootView: View, proxyListener: IProxyClickListener) {
        mProxyListener = proxyListener
        //初始化hook
        initHooks()
        mPrivateTagKey = mPrivateTagKey or (0xFFFF shl 24)
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            hookViews(rootView, 0)
        }
    }

    /**
     * 获取View.class 中的点击事件的 Method和Field
     */
    @SuppressLint("PrivateApi")
    fun initHooks() {
        //得到ListenerInfo函数
        if (sHookMethod == null) {
            try {
                val viewClass = Class.forName("android.view.View")
                sHookMethod = viewClass.getDeclaredMethod("getListenerInfo")
                sHookMethod?.isAccessible = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        //得到原始的OnClickListener事件对象
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

    /**
     * hook view ，recycledContainerDeep ：记录列表容器的深度
     */
    private fun hookViews(view: View, recycledContainerDeep: Int) {
        if (view.visibility == View.VISIBLE) {
            var recycledContainerDeep = recycledContainerDeep
            val forceHook: Boolean = recycledContainerDeep == 1
            if (view is ViewGroup) {
                val existAncestorRecycle: Boolean = recycledContainerDeep > 0
                val viewGroup: ViewGroup = view
                //不属于ListView 或者 是ListView深度大于0，继续往下走
                if (!(viewGroup is AbsListView || viewGroup is RecyclerView) || existAncestorRecycle) {
                    hookClickListener(view, recycledContainerDeep, forceHook)
                    if (existAncestorRecycle) {
                        recycledContainerDeep++
                    }
                } else {
                    recycledContainerDeep = 1
                }
                //ViewGroup遍历子view
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

    /**
     * 给HookView设置Click监听
     * 设置自定义Click事件
     */
    private fun hookClickListener(view: View, recycledContainerDeep: Int, forceHook: Boolean) {
        //判断是否需要hook
        var needHook = forceHook
        if (!needHook) {
            needHook = view.isClickable
            //如果可点击且深度为0，则根据tag来判断是否已经hook过
            if (needHook && recycledContainerDeep == 0) {
                needHook = view.getTag(mPrivateTagKey) == null
            }
        }

        if (needHook) {
            try {
                sHookMethod?.let { method ->
                    val getListenerInfo: Any = method.invoke(view)
                    sHookField?.let { field ->
                        //通过field获取getListenerInfo 的 OnClickListener 事件
                        val baseClickListener = field.get(getListenerInfo) as View.OnClickListener
                        //重新设置field中getListenerInfo的IProxyClickListener
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