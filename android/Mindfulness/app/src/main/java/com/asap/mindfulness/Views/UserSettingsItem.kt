package com.asap.mindfulness.Views

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.widget.TextView

import com.asap.mindfulness.R
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.user_settings_item.view.*


/**
 * TODO: document your custom view class.
 */
class UserSettingsItem : FrameLayout {
    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, title: String, currentValue: String) : super(context) {
        this.title = title
        this.currentValue = currentValue
        initView()
    }

    lateinit var view: View

    var title = "No Title"
        set(value) {
            view.user_settings_main.text = value
        }
    var currentValue = "No Value!"
        set(value) {
            view.user_settings_current.text = value
        }

    private fun initView() {
        view = View.inflate(context, R.layout.user_settings_item, null)
        addView(view)
    }
}
