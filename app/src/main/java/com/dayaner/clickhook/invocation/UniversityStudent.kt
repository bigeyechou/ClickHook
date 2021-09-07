package com.dayaner.clickhook.invocation

import android.util.Log
import com.dayaner.clickhook.MainActivity

/**
 * -------------------------------------
 * 作者：dayaner
 * -------------------------------------
 * 时间：9/6/21 12:43 PM
 * -------------------------------------
 * 描述：
 * -------------------------------------
 * 备注：
 * -------------------------------------
 */
class UniversityStudent : StudentInterface {

    override fun name(name: String) {
        Log.i(MainActivity.TAG, "name:大学生 " + name)
    }

    override fun age(age: Int) {

    }
}