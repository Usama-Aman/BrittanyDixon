package com.cp.brittany.dixon.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Rect
import android.os.IBinder
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AbsListView
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.cp.brittany.dixon.R


class HideUtil private constructor(activity: AppCompatActivity, content: ViewGroup?) {
    @SuppressLint("ClickableViewAccessibility")
    private fun getScrollView(viewGroup: ViewGroup?, activity: Activity) {
        if (null == viewGroup) {
            return
        }
        val count = viewGroup.childCount
        for (i in 0 until count) {
            val view = viewGroup.getChildAt(i)
            when (view) {
                is ScrollView -> {
                    view.setOnTouchListener { _, motionEvent ->
                        dispatchTouchEvent(activity, motionEvent)
                        false
                    }
                }
                is AbsListView -> {
                    view.setOnTouchListener { _, motionEvent ->
                        dispatchTouchEvent(activity, motionEvent)
                        false
                    }
                }
                is RecyclerView -> {
                    view.setOnTouchListener { _, motionEvent ->
                        dispatchTouchEvent(activity, motionEvent)
                        false
                    }
                }
                is ViewGroup -> {
                    getScrollView(view, activity)
                }
            }
            if (view.isClickable && view is TextView && view !is EditText) {
                view.setOnTouchListener { _, motionEvent ->
                    dispatchTouchEvent(activity, motionEvent)
                    false
                }
            }
        }
    }


    private fun dispatchTouchEvent(mActivity: Activity, ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = mActivity.currentFocus
            if (null != v && isShouldHideInput(v, ev)) {
                hideSoftInput(mActivity, v.windowToken)
            }
        }
        return false
    }


    private fun isShouldHideInput(v: View, event: MotionEvent): Boolean {
        if (v is EditText) {
            val rect = Rect()
            v.getHitRect(rect)
            return !rect.contains(event.x.toInt(), event.y.toInt())
        }
        return true
    }


    private fun hideSoftInput(mActivity: Activity, token: IBinder?) {
        if (token != null) {
            val im =
                (mActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            im.hideSoftInputFromWindow(
                token,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    companion object {

        fun init(activity: AppCompatActivity) {
            HideUtil(activity, null)
        }


        fun init(activity: AppCompatActivity, content: ViewGroup?) {
            HideUtil(activity, content)
        }


        fun hideSoftKeyboard(activity: Activity?) {
            if (null == activity) {
                throw RuntimeException("Error System")
            }
            val view = activity.currentFocus
            if (null != view) {
                val inputMethodManager =
                    (activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
                inputMethodManager.hideSoftInputFromWindow(
                    view.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            }
        }


        fun hideSoftKeyboard(view: View?) {
            if (null != view) {
                val inputMethodManager =
                    view.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(
                    view.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            } else {
                throw RuntimeException("Error System")
            }
        }


        fun hideDialogSoftKeyboard(dialog: Dialog?) {
            if (null == dialog) {
                throw RuntimeException("Error System")
            }
            val view = dialog.currentFocus
            if (null != view) {
                val inputMethodManager =
                    dialog.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(
                    view.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            }
        }
    }


    init {
        var contentCheckItem = content
        if (contentCheckItem == null) {
            contentCheckItem = activity.findViewById(R.id.content) as ViewGroup
        }
        getScrollView(contentCheckItem, activity)
        contentCheckItem.setOnTouchListener { _, motionEvent ->
            dispatchTouchEvent(activity, motionEvent)
            false
        }
    }
}